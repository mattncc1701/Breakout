package BOut;

import java.util.Random;

import com.golden.gamedev.engine.*;
import com.golden.gamedev.object.*;

import static BOut.BreakOutEngine.*;

public class Ball extends Sprite implements Cloneable
{
  /**
   * the rotational speed of the ball, in radians per millisecond;
   * positive is counter-clockwise
   */
  private double spin;

  /**
   * audio channel for the ball's sounds to play
   */
  private transient BaseAudio audio;

  /**
   * a source of randomness (used to avoid getting "stuck"
   */
  private Random random = new Random();
	
  /**
   * the sound to make when bouncing off the wall
   */
  private static String BoundaryBounceSound = SoundsDirectory + "wall-bounce.wav";

  /**
   * the sound to make when bouncing off the paddle
   */
  private static String PaddleBounceSound = SoundsDirectory + "paddle-bounce.wav";

  /**
   * this flag disable the effect of spin on Ball bounces,
   * possibly useful for testing, but not available to the user
   * as something to change
   */
  private static boolean disableSpin = false;

  /**
   * report spin enabled/disabled for displaying
   * @return a String describing the current state of spin (enabled/disabled)
   */
  public static String reportSpin () {
    return (disableSpin ? "disabled" : "enabled");
  }

  /**
   * flips enable of ball spin
   */
  public static void toggleSpin () {
    disableSpin = !disableSpin;
  } 
 
  /**
   * event source for hearing about changes in status of Balls
   */
  private static BreakoutEventSource<ActiveChangedEvent<Ball>> activitySource =
    new BreakoutEventSource<ActiveChangedEvent<Ball>>();

  /**
   * getter of event source for changes in activity status of Ball
   * @return the current BreakoutEventSource<ActiveChangedEvent<Ball>>
   */
  public static BreakoutEventSource<ActiveChangedEvent<Ball>> getActiveChangedSource ()
  {
    return activitySource;
  }

  /**
   * should be called as we start a new game, so we can get ready
   */
  public static void newGame () {
    activitySource.reset();
  }

  /**
   * Takes in the position of the Ball, without an image provided.
   * @param x
   * @param y
   */
  public Ball (double x, double y)
  {
    super(x, y);
    notifyActivityChanged();
  }

  /**
   * Set the velocity (speed and direction) of the Ball
   * @param magnitude a double giving the magnitude of the speed;
   * units are pixels per millisecond
   * @param angle a double giving the angle in radians; take note
   * that the convention is the 0 is straight up, and positive
   * is counter-clockwise
   */
  public void setVelocityPolar (double magnitude, double angle) {
    this.setHorizontalSpeed(-(Math.sin(angle)*magnitude));
    this.setVerticalSpeed  (-(Math.cos(angle)*magnitude));
  }

  /**
   * setter for ball spin
   * @param spin a double giving the new spin to use (radians per millisecond)
   */
  public void setSpin (double spin) {
    this.spin = spin;
  }

  /**
   * Must be set for any sounds to play.  If null, no sounds will be made when the ball
   * collides with something.
   * @param ba
   */
  public void setAudio (BaseAudio ba)
  {
    this.audio = ba;
  }

  /**
   * Calculates and applies the velocity and spin change from a bounce
   * of the Ball.  The "factors" indicate how to translate speeds in the
   * x and y directions into the speed parallel to the surface.  If we
   * orient sour view so that the Ball is moving down toward the surface,
   * the speed parallel to the surface is positive if movement is to the
   * right.
   * <br>
   * The equations are derived from the physical analysis presented in:
   * Richard L. Garwin, "Kinematics of an Ultraelastic Rough Ball",
   * American Journal of Physics, 37, 88-92, Jan. 1969.  This determines
   * behavior for a perfectly bouncy ball (elastic collision) with spin
   * that does not slip/slide when bouncing.  A real-world analog is the
   * Superball product by Wham-O.
   * <br>
   * We make two modifications / specializations:<br>
   * 1) Since we operate in two dimensions, our "ball" is a disc.  This
   * makes the moment of inertia 1/2 * M * R^2.  In the conventions of
   * Garwin's paper, this means that alpha = 1/2.<br>
   * 2) Garwin does not consider collision with a moving surface.  However,
   * it is easy to adjust things by considering movement relative to the
   * frame of the moving surface.  We simply subtract the speed of the
   * surface from the Ball's parallel movement before computing what
   * happens in the collision, and then add the speed back afterwards.
   * <br>
   * In Garwin's notation, we end up with:<br>
   * Ca = -1/3 Cb - 4/3 Vb + 4/3 Vs<br>
   * Va = -2/3 Cb + 1/3 Vb + 2/3 Vs<br>
   *
   * Here Va and Vb are the speed of the Ball parallel to the surface
   * after and before the collision, respectively.  Vs is the speed of
   * the surface.  Ca and Cb are R * spin of the Ball, after and before
   * the collision, respectively.  (In Garwin's notation, C = R * omega,
   * where omega is the spin.)
   *
   * @param xFactor an int, -1, 0, or 1, giving the factor by which to
   * multiply the horizontal speed to get its contribution to the speed
   * parallel to the surface
   * @param yFactor an int, -1, 0, or 1, giving the factor by which to
   * multiple the vertical speed to get its contribution to the speed
   * parallel to the surface
   * @param speed a double, giving the speed of the surface; positive
   * is movement to right if we consider the surface as being horizontal
   * with the Ball coming down onto it
   * @param soundFile the name of a file with a sound to play; null means no sound
   */
  private void bounce (int xFactor, int yFactor, double speed, String soundFile) {
    // can get stuck, so add a random component to the speed
    speed += 0.03 * (random.nextFloat() - 0.5);
    // obtain parallel / normal speeds for horizontal and vertical
    double vParallel = xFactor * this.getHorizontalSpeed() +
                       yFactor * this.getVerticalSpeed();
    double vNormal = xFactor * this.getVerticalSpeed() -
                     yFactor * this.getHorizontalSpeed();
    double radius = this.getImage().getHeight() / 2.0D;
    // determine Garwin's Cb and Vb
    double cBefore = radius * spin;
    double vBefore = vParallel;
    // allow for disabling of spin effects
    if (disableSpin)
    {
      speed = 0.0D;
      cBefore = -vBefore;
    }
    // determine Garwin's Ca and Va
    double cAfter = (-1.0D/3.0D) * cBefore + (-4.0D/3.0D) * vBefore + (4.0D/3.0D) * speed;
    double vAfter = (-2.0D/3.0D) * cBefore + ( 1.0D/3.0D) * vBefore + (2.0D/3.0D) * speed;
    // apply direction reversal to normal component
    double vNAfter = -vNormal;
    // determine horizontal and vertical speeds from parallel and normal components
    double vHAfter = xFactor * vAfter  - yFactor * vNAfter;
    double vVAfter = xFactor * vNAfter + yFactor * vAfter;
    // update the Ball's state
    this.setHorizontalSpeed(vHAfter);
    this.setVerticalSpeed(vVAfter);
    this.spin = cAfter / radius;

    if (soundFile != null && audio != null)
    {
      audio.play(soundFile);
    }
  }

  /**
   * This method is called if there is a collision with the boundary,
   * and is given 4 booleans depending on the side that
   * this ball hits.  If it hits a corner, it would be given two trues.
   * @param onTop a boolean, true if the collision is on the top of the Ball
   * @param onBottom a boolean, true if the collision is on the bottom of the Ball
   * @param onLeft a boolean, true if the collision is on the left of the Ball
   * @param onRight a boolean, true if the collision is on the right of the Ball
   */
  public void collisionWithBounds (boolean onTop , boolean onBottom,
                                   boolean onLeft, boolean onRight)
  {
    if (onBottom) {
      // first check for loss of the Ball
      this.setActive(false); // fell off the bottom
    } else {
      // the speed direction checks are to avoid double bounces
      if (onLeft && this.getHorizontalSpeed() < 0)
      {
        this.bounce(0, 1, 0.0D, BoundaryBounceSound);
      }
      if (onRight && this.getHorizontalSpeed() > 0)
      {
        this.bounce(0, -1, 0.0D, BoundaryBounceSound);
      }
      if (onTop && this.getVerticalSpeed() < 0)
      {
        this.bounce(-1, 0, 0.0D, BoundaryBounceSound);
      }
    }
  }

  /**
   * For collisions with Blocks and other objects, takes in parameters to tell if the ball
   * collides with something on its top, bottom,, left, or right with something, and
   * the speed of that something (pixels per second; walls and blocks have speed 0,
   * but the paddle may have a different speed).
   *
   * @param onTop a boolean, true if the collision is with the top of the ball
   * @param onBottom a boolean, true if the collision is with the bottom of the ball
   * @param onLeft a boolean, true if the collision is with the left of the ball
   * @param onRight a boolean, true if the collision is with the right of the ball
   */
  public void collisionWithBlock (boolean onTop, boolean onBottom,
                                  boolean onLeft, boolean onRight)
  {
    // the speed direction checks are to avoid double bounces
    if (onLeft && this.getHorizontalSpeed() < 0)
    {
      this.bounce(0, 1, 0.0D, null);
    }
    if (onRight && this.getHorizontalSpeed() > 0)
    {
      this.bounce(0, -1, 0.0D, null);
    }
    if (onTop && this.getVerticalSpeed() < 0)
    {
      this.bounce(-1, 0, 0.0D, null);
    }
    if (onBottom && this.getVerticalSpeed() > 0)
    {
      this.bounce(1, 0, 0.0D, null);
    }
  }
	
  /**
   * Takes in the paddle it collides with, and then addresses its behavior on the bounce.
   * @param paddle
   */
  public void collisionWithPaddle (Paddle paddle)
  {
    if (this.getVerticalSpeed() > 0)
    {
      this.bounce(1, 0, paddle.getRecentVelocity(), PaddleBounceSound);
    }
  }

  /**
   * wraps activity changes so that we can notify listeners
   * @param newValue a boolean giving the new value for whether the Ball is active
   */
  public void setActive (boolean newValue)
  {
    boolean changed = (this.isActive() ^ newValue);
    super.setActive(newValue);
    if (changed)
    {
      notifyActivityChanged();
    }
  }

  /**
   * use to notify activity change listeners (if any)
   */
  private void notifyActivityChanged ()
  {
    if (activitySource != null && activitySource.anyListeners())
    {
      activitySource.notify(new ActiveChangedEvent<Ball>(this));
    }
  }

  /**
   * @return a memento (copy) of this Ball
   */
  public Ball memento ()
  {
    try
    {
      return (Ball)this.clone();
    }
    catch (CloneNotSupportedException exc)
    {
      System.err.printf("Problem cloning a Ball:%n%s", exc);
      return null;
    }
  }
  
}
