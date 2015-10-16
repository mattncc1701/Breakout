package BOut;

import com.golden.gamedev.object.*;
import com.golden.gamedev.engine.*;

import static BOut.BreakOutEngine.*;

public class Miniball extends Sprite implements Cloneable
{
  /**
   * magnitude of the miniball in pixels per second;
   * this is non-negative; the direction is always up 
   */
  private double magnitude;

  /**
   * number of blocks this Miniball can still hit
   */
  private int blockHitsRemaining = 3;
  
  /**
   * audio channel for the miniball's sounds to play
   */
  private transient BaseAudio audio;
	
  /**
   * sound to make when colliding with a block
   */
  private static String BlockCollisionSound = SoundsDirectory + "ringout.wav";

  /**
   * event source for hearing about changes in status of Miniballs 
   */
  private static BreakoutEventSource<ActiveChangedEvent<Miniball>> activitySource =
    new BreakoutEventSource<ActiveChangedEvent<Miniball>>();
  
  /**
   * getter of event source for changes in activity status of Miniball
   * @return the current BreakoutEventSource<ActiveChangedEvent<Miniball>>
   */
  public static BreakoutEventSource<ActiveChangedEvent<Miniball>> getActiveChangedSource ()
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
   * put common initialization code here
   */
  private void initState ()
  {
    this.magnitude = 2.0D * GameState.getGameState().getBallSpeed();
    this.setVerticalSpeed(-magnitude);  // always up
  }
  
  /**
   * Basic Constructor, defaults to a null image and a standard position. See the Sprite Class for 
   * details on the basic constructor.
   */
  public Miniball ()
  {
    super();
    initState();
    notifyActivityChanged();
  }

  /**
   * Takes in the position of the Miniball, without an image provided.
   * @param x
   * @param y
   */
  public Miniball (double x, double y)
  {
    super(x, y);
    initState();
    notifyActivityChanged();
  }
	
  /**
   * This method is called if there is a collision with the boundary
   */
  public void collisionWithBounds ()
  {		
    this.setActive(false);  // must have hit the top
  }

  /**
   * For collisions with Blocks
   */
  public void collisionWithBlock (Block theBlock)
  {
    if (audio != null)
    {
      audio.play(BlockCollisionSound);
    }
    if (--blockHitsRemaining <= 0)
    {
      this.setActive(false);
    }
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
      activitySource.notify(new ActiveChangedEvent<Miniball>(this));
    }
  }

  /**
   * @return a memento (copy) of this Miniball
   */
  public Miniball memento ()
  {
    try
    {
      return (Miniball)this.clone();
    }
    catch (CloneNotSupportedException exc)
    {
      System.err.printf("Problem cloning a Miniball:%n%s", exc);
      return null;
    }
  }
  
}
