package BOut;

/**
 * This abstract class is handy because it gives default
 * implementations of the behaviors
 * 
 * @author Eliot Moss
 */
public abstract class PowerUpStrategyDecorator implements PowerUpStrategy {

  /**
   * the PowerUpStraegy that we decorate
   */
  private final PowerUpStrategy decorated;
  
  /**
   * Accepts a PowerUpStrategy and remembers it as the one we are decorating
   * @param decorated the PowerUpStrategy that we decorate
   */
  PowerUpStrategyDecorator (PowerUpStrategy decorated)
  {
    this.decorated = decorated;
  }

  /**
   * Subclasses should override and should call this before their own action
   * @see BOut.PowerUpStrategy#activate()
   */
  public void activate () {
    this.decorated.activate();
  }

  /**
   * Subclasses should override and should call this before their own action
   * @see BOut.PowerUpStrategy#lose()
   */
  public void lose () {
    this.decorated.lose();
  }
  
}
