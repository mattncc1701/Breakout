package BOut;

/**
 * The strategy decorator that decrements the score multiplier
 * if lost.
 * 
 * @author Eliot Moss
 *
 */
public final class PowerUpStrategyDecoratorDecr
    extends PowerUpStrategyDecorator
{

  /**
   * Constructor just passes strategy we are decorating to the superclass
   * @param decorated PowerUpStrategy we are decorating
   */
  PowerUpStrategyDecoratorDecr (PowerUpStrategy decorated)
  {
    super(decorated);
  }
  
  /* (non-Javadoc)
   * @see BOut.PowerUpStrategyDecorator#lose()
   */
  @Override
  public void lose () {
    super.lose();
    GameState.getGameState().decrementMultiplier();
  }

}
