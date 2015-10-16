package BOut;

import com.golden.gamedev.object.*;

/**
 * An event that indicates that a given Sprite became active
 * or inactive
 * 
 * @author Eliot Moss
 */
public class ActiveChangedEvent<S extends Sprite> implements BreakoutEvent {

  /**
   * the S whose status changed
   */
  private final S sprite;
  
  /**
   * amount by which the number active changed
   */
  private final int delta;
  
  /**
   * Constructor; records the changed S 
   * @param sprite an S that went (in)active
   */
  public ActiveChangedEvent (S sprite)
  {
    this.sprite = sprite;
    this.delta = (sprite.isActive() ? +1 : -1);
  }
  
  /**
   * getter for obtaining the changed S
   * @return the S whose status changed
   */
  public S getSprite ()
  {
    return sprite;
  }
  
  /**
   * getter for obtaining the change in number of active sprites
   * @return an int, +1 or -1, giving the net change in active sprites
   */
  public int getDelta ()
  {
    return delta;
  }
}
