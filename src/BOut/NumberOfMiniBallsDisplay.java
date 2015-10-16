package BOut;

import com.golden.gamedev.object.*;

public class NumberOfMiniBallsDisplay extends Sprite implements Cloneable{

	  private static BreakoutEventSource<ActiveChangedEvent<NumberOfMiniBallsDisplay>> activitySource =
			    new BreakoutEventSource<ActiveChangedEvent<NumberOfMiniBallsDisplay>>();
	
	  public static BreakoutEventSource<ActiveChangedEvent<NumberOfMiniBallsDisplay>> getActiveChangedSource ()
	  {
	    return activitySource;
	  }
	  
	  /**
	   * should be called as we start a new game, so we can get ready
	   */
	  public static void newGame () {
	    activitySource.reset();
	  }
	  
	  public NumberOfMiniBallsDisplay ()
	  {
	    super();
	    notifyActivityChanged();
	  }
	  
	  public NumberOfMiniBallsDisplay (int x, int y)
	  {
	    super(x,y);
	    notifyActivityChanged();
	  }
	  
	  
	  public void setActive (boolean newValue)
	  {
	    boolean changed = (this.isActive() ^ newValue);
	    super.setActive(newValue);
	    if (changed)
	    {
	      notifyActivityChanged();
	    }
	  }
	  
	  private void notifyActivityChanged ()
	  {
	    if (activitySource != null && activitySource.anyListeners())
	    {
	      activitySource.notify(new ActiveChangedEvent<NumberOfMiniBallsDisplay>(this));
	    }
	  }

}
