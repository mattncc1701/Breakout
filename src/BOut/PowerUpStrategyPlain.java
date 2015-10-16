package BOut;

/**
 * The plain strategy: do-nothing behaviors
 * 
 * @author Eliot Moss
 */
public final class PowerUpStrategyPlain implements PowerUpStrategy {

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategy#activate()
   */
  @Override
  public void activate () {
    // do-nothing version of the method
  }

  /* (non-Javadoc)
   * @see BOut.PowerUpStrategy#lose()
   */
  @Override
  public void lose () {
    // do-nothing version of the method
  }

}
