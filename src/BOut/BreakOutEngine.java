package BOut;

import com.golden.gamedev.*;
import java.awt.*;
import BOut.BreakOutGame.Memento;

/**
 * The GameEngine for the UMass BreakOut game
 * @author Eliot Moss
 */
public class BreakOutEngine extends GameEngine {

  /**
   * name of the (file for the) next level to play,
   * set by GameMenu; package-level access
   */
  String nextLevel;
	
  /**
   * name of an archive file to try to load
   */
  public String restoreFilename;
  
  /**
   * the singleton engine
   */
  private static final BreakOutEngine theEngine = new BreakOutEngine();
  
  /**
   * String giving the directory in which to find graphics images
   */
  public static final String GraphicsDirectory = "graphics/";
  
  /**
   * String giving the directory in which to find audio files for sounds
   */
  public static final String SoundsDirectory = "sounds/";
  
  /**
   * this code is for quitting the game; the value is
   * determined by the GoldenT GameEngine
   */
  public final static int BreakOutQuit = -1;

  /**
   * this code is for the "game" that processes the game menu;
   * its value is determined by the fact that 0 is the default
   * value passed in by the GoldenT GaemEngine
   */
  public final static int BreakOutGameMenu = 0;

  /**
   * this code is for our game; its value need only
   * be different from the other ones, but 1 is convenient
   */
  public final static int BreakOutPlayGame = 1;
  
  /**
   * this code is for restoring a game from an archive;
   * you must also set restoreFilename for this to work
   */
  public final static int BreakOutRestore = 2;
  
  /**
   * private to make this a singleton
   */
  private BreakOutEngine ()
  {

  }
  
  /**
   * accessor to get the singleton BreakOutEngine
   * @return the one BreakOutEngine object
   */
  public static BreakOutEngine getEngine ()
  {
    return theEngine;
  }
  
  /**
   * @param GameId an int indicates which "game" to play, namely
   * -1 to quit, 0 for the menu, and 1 for UMass BreakOut
   */
  public GameObject getGame (int GameID) 
  {
    switch (GameID) 
    {
    // GameID = 0 is always the first to play
    case BreakOutGameMenu:
      return new GameMenu(this);
    case BreakOutPlayGame:
      return new BreakOutGame(this);
    case BreakOutRestore:
      Memento archivedGame = BreakOutGame.restoreSnapshot(restoreFilename);
      return new BreakOutGame(this, archivedGame);
    }
    // this will result in a run-time error message from GoldenT
    return null;
  }

  /**
   * this gives access to initialization, for use by testing classes
   */
  public void initMe ()
  {
    initEngine();
  }
  
  /**
   * Starts up the game menu; at present it does not process
   * any arguments
   * @param args a String[] of command line arguments (currently ignored)
   */
  public static void main (String[] args) 
  {
    GameLoader game = new GameLoader();
    // we presently hard code a screen size of 800x600
    game.setup(getEngine(), new Dimension(800, 600), false);
    game.start();
  }
}
