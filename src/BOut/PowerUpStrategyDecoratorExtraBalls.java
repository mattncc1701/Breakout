package BOut;

public class PowerUpStrategyDecoratorExtraBalls extends PowerUpStrategyDecorator {

	  /**
	   * wraps the given strategy with our behavior
	   * @param decorated
	   */
	  public PowerUpStrategyDecoratorExtraBalls (PowerUpStrategy decorated)
	  {
	    super(decorated);
	  }
	  
	  @Override
	  public void activate ()
	  {
	    super.activate();
	    GameState.getGameState().ballsGained = true;
	    GameState.getGameState().addBalls(1);
	  }

	}
