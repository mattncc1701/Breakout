package BOut;

/**
 * Takes care of creating a PowerUP from a String description of it
 * @author Eliot Moss
  */
public class PowerUpFactory {
  
  /**
   * make it impossible to instantiate this class, which is here
   * only for its static method(s)
   */
  private PowerUpFactory () { }
  
  /**
   * Strategy objects
   */
  private static final PowerUpStrategy plain  = new PowerUpStrategyPlain();
  private static final PowerUpStrategy incr   = new PowerUpStrategyDecoratorIncr(plain);
  private static final PowerUpStrategy decr   = new PowerUpStrategyDecoratorDecr(plain);
  private static final PowerUpStrategy incDec = new PowerUpStrategyDecoratorDecr(incr);
  private static final PowerUpStrategy mini   = new PowerUpStrategyDecoratorMiniballs(plain);
  private static final PowerUpStrategy extra   = new PowerUpStrategyDecoratorExtraBalls(plain);
  /**
   * creates a PowerUp from a description
   * @param desc a String giving the kind of PowerUp desired
   * @return a new PowerUP of that kind
   */
  public static PowerUp createPowerUp (String desc, BreakOutGame theGame, double x, double y)
  {
    String color = "Yellow";
    PowerUpStrategy strategy = plain;
    if (desc.equals("incrOnHit"))
    {
      color = "Purple";
      strategy = incr;
    }
    else if (desc.equals("decrOnMiss"))
    {
      color = "Cyan";
      strategy = decr;
    }
    else if (desc.equals("incrDecr"))
    {
      color = "Blue";
      strategy = incDec;
    }
    else if (desc.equals("miniballs"))
    {
      color = "Red";
      strategy = mini;
    }
    else if (desc.equals("extra")){
    	color = "Orange";
    	strategy = extra;
    }
    PowerUpDesc pDesc = PowerUpDesc.getPowerUpDesc(color);
    return new PowerUp(pDesc, x, y, strategy);
  }

}
