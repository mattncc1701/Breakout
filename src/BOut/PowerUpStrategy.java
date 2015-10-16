package BOut;

import java.io.Serializable;

/**
 * Interface for the varying part of PowerUp's behavior
 * 
 * @author Eliot Moss
 */
public interface PowerUpStrategy extends Serializable {

  /**
   * Handles the case of activating PowerUp
   */
  public abstract void activate ();

  /**
   * handles the case of <i>not</i> getting the PowerUp
   */
  public abstract void lose ();

}
