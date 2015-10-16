package BOut;

/**
 * Implements the PowerUpStrategyDecorator that yields new Miniballs when hit
 * 
 * @author Eliot Moss
 */
public class PowerUpStrategyDecoratorMiniballs extends PowerUpStrategyDecorator {

  /**
   * wraps the given strategy with our behavior
   * @param decorated
   */
  public PowerUpStrategyDecoratorMiniballs (PowerUpStrategy decorated)
  {
    super(decorated);
  }
  
  /**
   * add 5 Miniballs to the GameState
   */
  @Override
  public void activate ()
  {
    super.activate();
    GameState.getGameState().miniBallsGained = true;
    GameState.getGameState().addMiniballs(5);
  }

}
