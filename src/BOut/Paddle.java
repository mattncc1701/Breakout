package BOut;

import com.golden.gamedev.object.*;

public class Paddle extends Sprite implements Cloneable
{
  // For the moment, a Paddle is just a normal Sprite.
  // Pretty much all interesting behaviors are in the Ball and Blocks
  
  /**
   * Empty constructor, sets the Paddle to a default location with a null image.
   */
  public Paddle ()
  {
    super();
  }

  /**
   * Sets the Paddle's starting location.
   * @param x
   * @param y
   */
  public Paddle (double x, double y)
  {
    super(x, y);
  }
	
  /**
   * Returns the difference between the Paddles Current position and last position.
   * @return
   */
  public double getRecentVelocity ()
  {
    return this.getX() - this.getOldX();
  }

  /**
   * @return a memento (copy) of this Paddle
   */
  public Paddle memento ()
  {
    try
    {
      return (Paddle)this.clone();
    }
    catch (CloneNotSupportedException exc)
    {
      System.err.printf("Problem cloning a Paddle:%n%s", exc);
      return null;
    }
  }

}
