package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.engine.*;
import static BOut.BreakOutEngine.*;

/**
 * A PowerUp is a Sprite that drops when a Block is destroyed.
 * It causes some special action in the game if hit with the
 * paddle (or if you fail to). At present, PowerUp objects
 * do not interact with Ball objects.
 * 
 * @author Eliot Moss
 */
public final class PowerUp extends Sprite implements Cloneable
{	

  /**
   * audio object for playing sounds
   */
  private transient BaseAudio audio;

  /**
   * file with sound for when a PowerUp falls off the bottom of the screen
   */
  private static final String PowerUpLoseSound = SoundsDirectory + "bang_1.wav";

  /**
   * file with sound for when a PowerUp hits the paddle
   */
  private static final String PowerUpHitSound = SoundsDirectory + "cling_1.wav";

  /**
   * strategy object for PowerUp's varying behavior 
   */
  private final PowerUpStrategy strategy;

  /**
   * describes the appearance of the PowerUp
   */
  private PowerUpDesc desc;

  /**
   * event source for hearing about changes in status of PowerUps 
   */
  private static BreakoutEventSource<ActiveChangedEvent<PowerUp>> activitySource =
    new BreakoutEventSource<ActiveChangedEvent<PowerUp>>();

  /**
   * getter of event source for changes in activity status of PowerUps
   * @return the current BreakoutEventSource<ActiveChangedEvent<PowerUp>>
   */
  public static BreakoutEventSource<ActiveChangedEvent<PowerUp>> getActiveChangedSource ()
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
   * no-argument constructor for deserialization
   */
  public PowerUp ()
  {
    super();
    this.strategy = null;  // deserialization can still set it (it's magic)
    notifyActivityChanged();
  }

  /**
   * Constructor that requires a String to describe what kind of block it is, with a BufferedImage
   * and a specified location. 
   * @param image
   * @param x
   * @param y
   */
  protected PowerUp (PowerUpDesc desc, double x, double y, PowerUpStrategy strategy)
  {
    super(desc.getImage(), x, y);
    this.strategy = strategy;
    this.desc     = desc;
    notifyActivityChanged();
  }

  /**
   * Activates the PowerUp (is special action) and makes its Sprite inactive.
   */
  public final void collisionWithPaddle ()
  {
    strategy.activate();
    playHitSound();
    this.setActive(false);
  }

  /**
   * Handles the collision with the bottom of the screen;
   * performs the losePowerUp special action and makes its
   * Sprite inactive
   */
  public final void collisionWithBounds ()
  {		
    strategy.lose();
    playLoseSound();
    this.setActive(false); // fell off the bottom
  }

  /**
   * set the object for playing sounds
   * @param audio the BaseAudio for playing sounds
   */
  public void setAudio (BaseAudio audio)
  {
    this.audio = audio;
  }

  /**
   * plays the sound that indicates the PowerUp was lost
   */
  protected void playLoseSound ()
  {
    if (audio != null)
    {
      audio.play(PowerUpLoseSound);
    }
  }

  /**
   * plays the sound that indicates the PowerUp was hit (won)
   */
  protected void playHitSound ()
  {
    if (audio != null)
    {
      audio.play(PowerUpHitSound);
    }
  }

  /**
   * wraps activity changes so that we can notify listeners
   * @param newValue a boolean giving the new value for whether the PowerUp is active
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
      activitySource.notify(new ActiveChangedEvent<PowerUp>(this));
    }
  }

  /**
   * @return a memento (copy) of this PowerUp
   */
  public PowerUp memento ()
  {
    try
    {
      return (PowerUp)this.clone();
    }
    catch (CloneNotSupportedException exc)
    {
      System.err.printf("Problem cloning a PowerUp:%n%s", exc);
      return null;
    }
  }

  /**
   *used to refresh the image after deserialization 
   */
  public void refreshImage ()
  {
    this.setImage(desc.getImage());
  }
}
