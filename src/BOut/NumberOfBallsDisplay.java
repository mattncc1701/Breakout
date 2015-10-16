package BOut;

import com.golden.gamedev.object.*;

public class NumberOfBallsDisplay extends Sprite implements Cloneable{
	
	  private static BreakoutEventSource<ActiveChangedEvent<NumberOfBallsDisplay>> activitySource =
			    new BreakoutEventSource<ActiveChangedEvent<NumberOfBallsDisplay>>();
	
	  public static BreakoutEventSource<ActiveChangedEvent<NumberOfBallsDisplay>> getActiveChangedSource ()
	  {
	    return activitySource;
	  }
	  
	  /**
	   * should be called as we start a new game, so we can get ready
	   */
	  public static void newGame () {
	    activitySource.reset();
	  }
	  
	  public NumberOfBallsDisplay ()
	  {
	    super();
	    notifyActivityChanged();
	  }
	  
	  public NumberOfBallsDisplay (int x, int y)
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
	      activitySource.notify(new ActiveChangedEvent<NumberOfBallsDisplay>(this));
	    }
	  }
}
