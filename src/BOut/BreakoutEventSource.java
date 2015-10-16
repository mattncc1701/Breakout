package BOut;

import java.util.Set;
import java.util.HashSet;
/**
 * Class that manages listener registrations and notifications
 * for a source of events
 * 
 * @author Eliot Moss
 *
 * @param <E> a subtype of BreakoutEvent indicating the type of
 * event for which this is a source
 */
public class BreakoutEventSource<E extends BreakoutEvent> {
  
  /**
   * current listeners
   */
  private Set<BreakoutListener<E>> listeners = new HashSet<BreakoutListener<E>>();

  /**
   * register the listener to be notified of events from this source
   * until/unless explicitly deregistered
   * @param listener the BreakoutListener<E> to be notified
   */
  public void register (BreakoutListener<E> listener)
  {
    listeners.add(listener);
  }
  
  /**
   * deregister the given listener
   * @param listener the BreakoutListener<E> to unregister
   */
  public void deregister (BreakoutListener<E> listener)
  {
    listeners.remove(listener);
  }
  
  /**
   * call the happened method of each registered listener
   * @param event the BreakoutEvent (E) to indicate has happened
   */
  public void notify (E event)
  {
    for (BreakoutListener<E> listener : listeners)
    {
      listener.happened(event);
    }
  }

  /**
   * checks if there are any listeners (caller can avoid creating an event object)
   * @return whether there are currently any listeners on this source
   */
  public boolean anyListeners ()
  {
    return !listeners.isEmpty();
  }

  /**
   * prepare for a new game: clear out all current listeners
   */
  public void reset ()
  {
    listeners.clear();
  }

}
