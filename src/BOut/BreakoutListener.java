package BOut;

/**
 * Interface to be implemented by event listeners,
 * that is, objects that wish to be notified of
 * some event
 * 
 * @author Eliot Moss
 */
public interface BreakoutListener<E extends BreakoutEvent> {

  /**
   * this method is called when an event happens for which the
   * listener is registered
   * @param event the BreakoutEvent (E) that has happened
   */
  public void happened (E event);
  
}
