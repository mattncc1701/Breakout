package BOut;

import com.golden.gamedev.*;
import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.collision.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import static BOut.BreakOutEngine.*;
import XmlImporter.*;

/**
 * This single game file loads and plays any level of the Break Out game
 * @author Eliot Moss
 */
public class BreakOutGame extends GameObject 
{
  /**
   * the game playing field
   */
  PlayField playField;
  
  /**
   * the game background
   */
  Background background;

  /**
   * SpriteGroup for the balls (only contains one at a time)
   */
  SpriteGroup balls;

  /**
   * SpriteGroup for the active miniballs
   */
  SpriteGroup miniballs;

  SpriteGroup ballsDisplay;
  boolean firstTimeBallDisplay = false;
  NumberOfBallsDisplay ballDisplay[] = new NumberOfBallsDisplay[10]; 
  SpriteGroup miniBallsDisplay;
  boolean firstTimeMiniBallDisplay = false;
  NumberOfMiniBallsDisplay miniBallDisplay[] = new NumberOfMiniBallsDisplay[50];
  
  /**
   * SpriteGroup for the paddles (only contains one)
   */
  SpriteGroup paddles;
  
  /**
   * SpriteGroup for the blocks (generally contains many)
   */
  SpriteGroup blocks;

  /**
   * SpriteGroup for powerUps (starts empty)
   */
  SpriteGroup powerUps;
  
  /**
   * we assume a single paddle; this is it
   */
  Paddle gamePaddle;

  /**
   * constant giving the width of the paddle
   */
  static final int PADDLE_WIDTH = 104;
  
  /**
   * font to use for the score display
   */
  GameFont scoreFont;

  /**
   * font to use for indicating level is won or list
   */
  GameFont doneFont;
  
  /**
   * for starting the game over sounds just once
   */
  boolean noEndSound = true;
  
  /**
   * name of sound file for when you win
   */
  private static final String WIN_SOUND = SoundsDirectory + "you-win.wav";
  
  /**
   * name of sound file for when you lose
   */
  private static final String LOSE_SOUND = SoundsDirectory + "you-lose.wav";
  
  /**
   * The saved game snapshot, if any
   */
  private Memento snapshot = null;
  
  /**
   * Nested class for game mementos (snapshots).
   * These can be used within a game, to go back to a previous point within
   * the same session, or can be archived to a file and restored in (the same
   * or) a later session.
   */
  public static class Memento implements Serializable
  {
    /**
     * the saved Balls (one or zero, but this keeps things more uniform)
     */
    private final List<Ball> balls = new ArrayList<Ball>();
    
//    private final List<NumberOfBallsDisplay> ballsDisplay = new ArrayList<NumberOfBallsDisplay>();
//    
//    private final List<NumberOfMiniBallsDisplay> miniBallsDisplay = new ArrayList<NumberOfMiniBallsDisplay>();
    /**
     * the saved Paddle
     */
    private final Paddle thePaddle;
    
    /**
     * the saved Blocks
     */
    private final List<Block> blocks = new ArrayList<Block>();
    
    /**
     * the saved PowerUps
     */
    private final List<PowerUp> powerUps = new ArrayList<PowerUp>();
    
    /**
     * the saved Miniballs
     */
    private final List<Miniball> miniballs = new ArrayList<Miniball>();
    
    /**
     * the corresponding GameState
     */
    private final GameState state;
    
    /**
     * factory method for trying to create a Memento of a game
     * @param game the BreakOutGame to snapshot
     * @param state the GameState to snapshot with it
     * @return the new Memento
     */
    private static Memento makeMemento (BreakOutGame game, GameState state)
    {
      return new Memento(game, state);
    }
    
    /**
     * Create a memento from a given BreakOutGame
     * @param game the BreakOutGame to snapshot
     * @param state the GameState at the same time
     */
    private Memento (BreakOutGame game, GameState state)
    {
      for (Sprite ball : game.balls.getSprites())
      {
        if (ball != null && ball.isActive())
        {
          balls.add(((Ball)ball).memento());
        }
      }
      thePaddle = game.gamePaddle.memento();
      for (Sprite block : game.blocks.getSprites())
      {
        if (block != null && block.isActive())
        {
          blocks.add(((Block)block).memento());
        }
      }
      for (Sprite powerUp : game.powerUps.getSprites())
      {
        if (powerUp != null && powerUp.isActive())
        {
          powerUps.add(((PowerUp)powerUp).memento());
        }
      }
      for (Sprite miniball : game.miniballs.getSprites())
      {
        if (miniball != null && miniball.isActive())
        {
          miniballs.add(((Miniball)miniball).memento());
        }
      }
      
//      for (Sprite numBalls : game.ballsDisplay.getSprites())
//      {
//        if (numBalls != null && numBalls.isActive())
//        {
//          ballsDisplay.add(((NumberOfBallsDisplay)numBalls).memento());
//        }
//      }
//      for (Sprite numMiniBalls : game.miniBallsDisplay.getSprites())
//      {
//        if (numMiniBalls != null && numMiniBalls.isActive())
//        {
//          miniBallsDisplay.add(((NumberOfMiniBallsDisplay)numMiniBalls).memento());
//        }
//      }
      this.state = state.memento();
    }
    
    /**
     * restore the GameState (globally) and restore Sprites into the given game
     * @param g a BreakOutGame in which to set the Sprites
     */
    public void restoreStateAndSprites (BreakOutGame g)
    {
      GameState.setGameState(this.state.memento());

      g.gamePaddle = this.thePaddle.memento();
      g.addPaddle(g.gamePaddle);

      for (Ball b : this.balls)
      {
        g.addBall(b.memento());
      }
      for (Miniball b : this.miniballs)
      {
        g.addMiniball(b.memento());
      }
      for (Block b : this.blocks)
      {
        g.addBlock(b.memento());
      }
      for (PowerUp p : powerUps)
      {
        g.addPowerUp(p.memento());
      }
    }
  }
  
  /**
   * create a Memento of this game's current situation
   * @return a Memento capturing the current game situation
   */
  public Memento memento ()
  {
    return Memento.makeMemento(this, GameState.getGameState());
  }
  
  /**
   * Creates and returns a filename based on the current date and time
   * @return a String to use as an archive file's name
   */
  private static String makeArchiveFileName ()
  {
    Calendar now = Calendar.getInstance();
    String filename = String.format("%04d-%02d-%02d-%02d-%02d-%02d.sav",
        now.get(Calendar.YEAR), now.get(Calendar.MONTH)+1, now.get(Calendar.DAY_OF_MONTH),
        now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
    return filename;
  }
  
  /**
   * archives the current game as a snapshot in a file;
   * the file's name is based on the current date and time
   */
  private void archiveGame ()
  {
    try {
      Memento snapshot = this.memento();
      String filename = makeArchiveFileName();
      FileOutputStream fout = new FileOutputStream(filename);
      ObjectOutputStream oout = new ObjectOutputStream(fout);
      oout.writeObject(snapshot);
      oout.close();
    }
    catch (FileNotFoundException exc)
    {
      System.out.printf("Could not open output file for saving; giving up!%nInfo:%n%s", exc);
    }
    catch (IOException exc)
    {
      System.out.printf("IO problem saving; giving up!%nInfo:%n%s", exc);
    }
  }
  
  /**
   * obtains an archived snapshot previously saved to a file, given the filename
   * @param filename a String giving the name of a file containing a previously saved snapshot
   * @return the Memento, or null if there was a problem
   */
  public static Memento restoreSnapshot (String filename)
  {
    try {
      FileInputStream fin = new FileInputStream(filename);
      ObjectInputStream oin = new ObjectInputStream(fin);
      Memento snapshot = (Memento)oin.readObject();
      oin.close();
      return snapshot;
    }
    catch (IOException exc)
    {
      System.out.printf("IO exception restoring; giving up!%nInfo:%n%s", exc);
    }
    catch (ClassNotFoundException exc)
    {
      System.out.printf("Class not found while restoring; giving up!%nInfo:%n%s", exc);
    }
    return null;
  }

  /**
   * Constructor; just calls superclass constructor
   * @param engine
   */
  public BreakOutGame (GameEngine engine)
  {
    super(engine);
  }

  /**
   * Constructor for creating a new game from a Memento snaphot
   * @param engine the GameEngine
   * @param snapshot the Memento to restore from
   */
  public BreakOutGame (GameEngine engine, Memento snapshot)
  {
    super(engine);
    this.snapshot = snapshot;
  }
  
  /**
   * Does the real work of setting up the game;
   * be careful about changing the order of things because
   * some steps depend on the initializations done by earlier ones
   */
  @Override
  public void initResources () 
  {
    initEventSources();
    background = createBackground();
    playField = createPlayField(background);
    SpriteGroup[] groups = createSpriteGroups();
    addSpriteGroupsToPlayField(playField, groups);
    createAndAddCollisionGroups(playField, background);
    initializeFonts();
    setDisplayRate(60);
    if (snapshot == null)
    {
      GameState.getGameState().startLevel();
      createPaddle(background);
      createBlocks();
    }
    else
    {
      // the restore-from-snapshot case
      snapshot.restoreStateAndSprites(this);
      GameState.getGameState().restart();
    }
  }

  /**
   * initialize the event sources or active/inactive states of Sprites
   */
  private void initEventSources ()
  {
    Ball.newGame();
    Miniball.newGame();
    PowerUp.newGame();
    Block.newGame();
    NumberOfBallsDisplay.newGame();
    NumberOfMiniBallsDisplay.newGame();
  }

  /**
   * create a background image for this game;
   * at the moment this is a bland gray and we do not make
   * provision for anything other than a fixed size, 800x600
   * @return a Background for the game
   */
  private Background createBackground ()
  {
    return new ColorBackground(Color.gray, 800, 600);
  }

  /**
   * create the PlayField and set its Background as requested
   * @param background the Background to use for the new PlayField
   * @return the new PlayField 
   */
  private PlayField createPlayField (Background background)
  {
    // create the playing field
    PlayField playField = new PlayField();
    playField.setComparator(new Comparator<Sprite>(){
      public int compare (Sprite s1, Sprite s2)
      {
        return s1.getLayer() - s2.getLayer();
      }
    });
    playField.setBackground(background);
    return playField;
  }

  /**
   * creates a SpriteGroup for each kinds of Sprite in the game,
   * installs each one, and returns an array of them all 
   */
  private SpriteGroup[] createSpriteGroups ()
  {
    balls     = new SpriteGroup("balls");
    miniballs = new SpriteGroup("miniballs");
    ballsDisplay = new SpriteGroup("ballsDisplay");
    miniBallsDisplay = new SpriteGroup("miniBallsDisplay");
    paddles   = new SpriteGroup("paddles");
    blocks    = new SpriteGroup("blocks");
    powerUps  = new SpriteGroup("powerUps");
    return new SpriteGroup[]{balls, miniballs,ballsDisplay, miniBallsDisplay, paddles, blocks, powerUps};
  }

  /**
   * adds each group to the PlayField
   * @param playField the PlayField to which we will add some SpriteGroups
   * @param groups a SpriteGroup[] giving the SpriteGroups to add 
   */
  private void addSpriteGroupsToPlayField (PlayField playField, SpriteGroup[] groups)
  {
    for (SpriteGroup group : groups)
    {
      playField.addGroup(group);
    }
  }

  /**
   * creates collision groups for this game and adds them to the PlayField
   * @param playField the PlayField to which to add the collision groups 
   * @param background the Background to use for and CollisionBounds objects
   */
  private void createAndAddCollisionGroups (PlayField playField, Background background)
  {
    CollisionBounds boundBallColl = new CollisionBounds(background) {
      public void collided (Sprite ball) {
        Ball theBall = (Ball) ball;
        // tell the Ball of the collision, and which side(s) are involved
        theBall.collisionWithBounds(
            this.isCollisionSide(CollisionBounds.TOP_COLLISION   ),
            this.isCollisionSide(CollisionBounds.BOTTOM_COLLISION),
            this.isCollisionSide(CollisionBounds.LEFT_COLLISION  ),
            this.isCollisionSide(CollisionBounds.RIGHT_COLLISION ));
      }
    };
    
    CollisionBounds boundMiniballColl = new CollisionBounds(background) {
      public void collided (Sprite miniball) {
        Miniball theMiniball = (Miniball) miniball;
        theMiniball.collisionWithBounds();
      }
    };

    CollisionBounds boundPowerUpColl = new CollisionBounds(background) {
      public void collided (Sprite powerUp) {
        PowerUp thePowerUp = (PowerUp) powerUp;
        thePowerUp.collisionWithBounds();
      }
    };
    
    CollisionGroup ballPaddleColl    = new CollisionGroup() {
      public void collided (Sprite ball, Sprite paddle) {
        Paddle thePaddle = (Paddle) paddle;
        Ball theBall = (Ball) ball;
        // tell the Ball of the collision (the Paddle doesn't care)
        theBall.collisionWithPaddle(thePaddle);     
      }
    };
    
    CollisionGroup powerUpPaddleColl = new CollisionGroup() {
      public void collided (Sprite powerUp, Sprite paddle) {
        PowerUp thePowerUp = (PowerUp) powerUp;
        thePowerUp.collisionWithPaddle();
      }
    };
    
    CollisionGroup ballBlockColl     = new CollisionGroup() {
      public void collided (Sprite ball, Sprite block) {
        Block theBlock = (Block) block;
        Ball theBall = (Ball) ball;
        // tell the block it was hit by a Ball
        theBlock.collisionWithBall();
        // compute side of the Block that the Ball hit
        // (determines how Ball bounces)
        boolean onBottom = false;
        boolean onTop    = false;
        boolean onLeft   = false;
        boolean onRight  = false;
        // need to take direction into account to avoid double collisions
        // (because of how Golden T computes collisions)
        if        (((getCollisionSide() & BOTTOM_TOP_COLLISION) != 0) && (theBall.getVerticalSpeed() > 0))
        {
          onBottom = true;
        } else if (((getCollisionSide() & TOP_BOTTOM_COLLISION) != 0) && (theBall.getVerticalSpeed() < 0))
        {
          onTop = true;
        } else if (((getCollisionSide() & LEFT_RIGHT_COLLISION) != 0) && (theBall.getHorizontalSpeed() < 0))
        {
          onLeft = true;
        } else if (((getCollisionSide() & RIGHT_LEFT_COLLISION) != 0) && (theBall.getHorizontalSpeed() > 0))
        {
          onRight = true;
        }
        // tell the Ball about its collision
        theBall.collisionWithBlock(onTop, onBottom, onLeft, onRight);
      }
    };

    CollisionGroup miniballBlockColl = new CollisionGroup() {
      public void collided (Sprite miniball, Sprite block) {
        Block theBlock = (Block)block;
        Miniball theMiniball = (Miniball)miniball;
        // check only one side, and check that it has not been reported before
        if (theBlock.newerMiniball(theMiniball))
        {
          theBlock.collisionWithMiniball(theMiniball);
          theMiniball.collisionWithBlock(theBlock);
        }
      }
    };

    playField.addCollisionGroup(balls    , paddles, ballPaddleColl   );
    playField.addCollisionGroup(powerUps , paddles, powerUpPaddleColl);
    playField.addCollisionGroup(balls    , null   , boundBallColl    );
    playField.addCollisionGroup(miniballs, null   , boundMiniballColl);
    playField.addCollisionGroup(powerUps , null   , boundPowerUpColl );
    playField.addCollisionGroup(balls    , blocks , ballBlockColl    );
    playField.addCollisionGroup(miniballs, blocks , miniballBlockColl);
  }

  /**
   * creates the single Paddle of the game
   * @param Background the Background, both for using as an image Background for
   * the paddle and to determine the Paddle's starting position 
   */
  private void createPaddle (Background background)
  {
    gamePaddle = new Paddle(this.background.getWidth() / 2,     // centered horizontally
                            this.background.getHeight() - 50);  // near the bottom
    this.addPaddle(gamePaddle);
  }

  /**
   * loads information about blocks from an XML file;
   * the methods there will call the utility routine addBlock,
   * in this class, to add each block 
   */
  private void createBlocks ()
  {
    // set up and load the game:
    // this handles the blocks, etc.
    LevelLoader xmlLoader = new LevelLoader(this);
    xmlLoader.loadGame(((BreakOutEngine)parent).nextLevel);
  }

  /**
   * set up the font(s) needed by the game for displaying text
   */
  private void initializeFonts ()
  {
    GameFontManager fontMgr = new GameFontManager();
    scoreFont = fontMgr.getFont(new Font("serif", Font.BOLD, 16), Color.BLACK);
    doneFont  = fontMgr.getFont(new Font("sansserif", Font.BOLD, 16), new Color(128, 0, 0));
  }

  /**
   * @param fps an int giving the target display rate, in frames per secound
   */
  private void setDisplayRate (int fps)
  {
    this.setFPS(fps);
  }

  /**
   * holds the message to display (won, lost, etc.)
   */
  private String message;

  /**
   * this is called once per frame, to cause us to update the model and the screen
   */
  public void update (long elapsedTime) 
  {
    positionPaddleFromMouse();
    processInput();
    handleEndOfGame();
    message = state.message();
    state.performAction(this, elapsedTime);
    displayBallRegion(GameState.getGameState());
    displayMiniBallRegion(GameState.getGameState());
  }
  private void displayBallRegion (GameState gs)
  {
      int initX = 1;
      int initY = this.background.getHeight() - 23; 
	  if(firstTimeBallDisplay == false){
		  firstTimeBallDisplay = true;
	      for(int i = 0; i < ballDisplay.length; i++){
    	      if(i < GameState.getGameState().getBallsRemaining()){
			      ballDisplay[i] = new NumberOfBallsDisplay(initX, initY);
			      initY = initY - 22;
			      if(i == 4){
			    	  initY = this.background.getHeight() - 23;
			    	  initX = 24;
			      }
			      ballDisplay[i].setActive(true);
			      this.addBallDisplay(ballDisplay[i]);
			      ballDisplay[i].setImmutable(false);
		      }
    	      else{
			      ballDisplay[i] = new NumberOfBallsDisplay(initX, initY);
			      initY = initY - 22;
			      if(i == 4){
			    	  initY = this.background.getHeight() - 23;
			    	  initX = 24;
			      }
			      ballDisplay[i].setActive(false);
			      ballDisplay[i].setImmutable(false);
			      this.addBallDisplay(ballDisplay[i]);
    	      }
		  }
	  }
	  else{
		  if(GameState.getGameState().ballsGained){
			  GameState.getGameState().ballsGained = false;
			  if(GameState.getGameState().getBallsRemaining() < 11){
				  ballDisplay[GameState.getGameState().getBallsRemaining()-1].setActive(true);
			  }
		  }
		  if(GameState.getGameState().ballsLost){
			  GameState.getGameState().ballsLost = false;
			  if(GameState.getGameState().getBallsRemaining() < 10){
				  ballDisplay[GameState.getGameState().getBallsRemaining()].setActive(false);
			  }
		  }
	  }
  }
  
  public void addBallDisplay (NumberOfBallsDisplay b)
  {
    b.setImage(getImageFromFile("Ball"));
    b.setBackground(background);
    b.setLayer(1);
    ballsDisplay.add(b);
  }
  private void displayMiniBallRegion (GameState gs)
  {
	  
    int initX = 47;
    int initY = this.background.getHeight() - 18;
	  if(firstTimeMiniBallDisplay == false){
		  firstTimeMiniBallDisplay = true;
	    for(int i = 0; i < miniBallDisplay.length; i++){
	    	if(i < GameState.getGameState().getMiniballsRemaining()){
		    	miniBallDisplay[i] = new NumberOfMiniBallsDisplay(initX, initY);
		    	initY = initY - 18;
		    	if(i == 9 || i == 19 || i == 29 || i == 39){
		    		initY = this.background.getHeight() - 18;
		    		initX = initX + 18;
		    	}
		        miniBallDisplay[i].setActive(true);
		        this.addMiniBallDisplay(miniBallDisplay[i]);
			      miniBallDisplay[i].setImmutable(false);
	    	}
	    	else{
		    	miniBallDisplay[i] = new NumberOfMiniBallsDisplay(initX, initY);
		    	initY = initY - 18;
		    	if(i == 9 || i == 19 || i == 29 || i == 39){
		    		initY = this.background.getHeight() - 18;
		    		initX = initX + 18;
		    	}
		    	miniBallDisplay[i].setActive(false);
		        this.addMiniBallDisplay(miniBallDisplay[i]);
			    miniBallDisplay[i].setImmutable(false);
	    	}
	    }
	  }
	  else{
		  if(GameState.getGameState().miniBallsGained){
			  GameState.getGameState().miniBallsGained = false;
			  int i = 5;
			  while(i > 0 &&  GameState.getGameState().getMiniballsRemaining() - i < 50){
				  miniBallDisplay[GameState.getGameState().getMiniballsRemaining() - i].setActive(true);  
				  i--;
			  }
			  
		  }
		  if(GameState.getGameState().miniBallsLost){
			  GameState.getGameState().miniBallsLost = false;
			  if(GameState.getGameState().getMiniballsRemaining() < 50){
				  miniBallDisplay[GameState.getGameState().getMiniballsRemaining()].setActive(false);
			  }
		  }
	  }
  }
  
  public void addMiniBallDisplay (NumberOfMiniBallsDisplay b)
  {
    b.setImage(getImageFromFile("Miniball"));
    b.setBackground(background);
    b.setLayer(1);
    miniBallsDisplay.add(b);
  }
  
  /**
   * moves the paddle to follow the mouse (horizontally)
   */
  private void positionPaddleFromMouse ()
  {
    gamePaddle.setX(this.getMouseX() - (PADDLE_WIDTH / 2));
  }

  /**
   * The possible situations with respect to input keystrokes or mouse actions
   */
  private static enum InputState
  {
    Normal {
      public InputState gotB (BreakOutGame game)
      {
        GameState gs = GameState.getGameState();
        if (gs.canFireBall()) {
          game.startNewBall(gs);
        }
        return Normal;
      }
      public InputState gotM (BreakOutGame game) {
        GameState gs = GameState.getGameState();
        if (gs.canFireMiniball())
        {
          game.fireMiniball();
        }
        return Normal;
      }
      public InputState gotP (BreakOutGame game) { return Pausing; }
      public InputState gotT (BreakOutGame game)
      {
        Ball.toggleSpin();
        return Normal;
      }
      public void performAction (BreakOutGame game, long elapsedTime) {
        GameState gs = GameState.getGameState();
        double msPerFrame = 1000.0D / (double)game.getCurrentFPS();
        gs.decrementWaits((long)(msPerFrame + 0.5D));  // 0.5 to round
        game.playField.update(elapsedTime);
      }
    },
    
    Won
    {
      public InputState gotY (BreakOutGame game) { return menuFinish(game); }
      public String message () { return "Level Over ... CONGRATULATIONS, YOU WON!  Press Y to return to Menu"; }
    },
    
    Lost
    {
      public InputState gotY (BreakOutGame game) { return menuFinish(game); }
      public String message () { return "Level Over ... Sorry, you lost.  Press Y to return to Menu"; }
    },
    
    Archiving
    {
      public InputState gotY (BreakOutGame game)
      {
        game.archiveGame();
        return Normal;
      }
      public String message () { return "Make an archive now? (Y or N)"; }
    },
    
    Pausing
    {
      public InputState gotP (BreakOutGame game) { return Normal; }
      public String message () { return "PAUSED (press P again to unpause)"; }
    },
    
    Quitting
    {
      public InputState gotY (BreakOutGame game) { return menuFinish(game); }
      public String message () { return "QUIT? (Y or N)"; }
    },
    
    Restoring
    {
      public InputState gotY (BreakOutGame game)
      {
        game.parent.nextGame = new BreakOutGame(game.parent, game.snapshot);
        return Finish;
      }
      public String message () { return "Restore from snapshot? (Y or N)"; }
    },
    
    RestoringNoSnapshot
    {
      public String message () { return "No snapshot available; press N to continue"; }
    },
    
    Saving
    {
      public InputState gotY (BreakOutGame game)
      {
        game.snapshot = game.memento();
        return Normal;
      }
      public String message () { return "Make a snaphot now? (Y or N) (any previous one is lost)"; }
    },
    
    Finish
    {
      public void performAction (BreakOutGame game, long elapsedTime) { game.finish(); }
    };
    
    public InputState gotA (BreakOutGame game) { return Archiving; }
    public InputState gotB (BreakOutGame game) { return this; }
    public InputState gotM (BreakOutGame game) { return this; }
    public InputState gotN (BreakOutGame game) { return Normal; }
    public InputState gotP (BreakOutGame game) { return this; }
    public InputState gotQ (BreakOutGame game) { return Quitting; }
    public InputState gotR (BreakOutGame game) { return (game.snapshot == null) ? RestoringNoSnapshot : Restoring; }
    public InputState gotS (BreakOutGame game) { return Saving; }
    public InputState gotY (BreakOutGame game) { return this; }
    public InputState gotT (BreakOutGame game) { return this; }
    private static InputState menuFinish (BreakOutGame game)
    {
      game.parent.nextGameID = BreakOutEngine.BreakOutGameMenu;
      return Finish;
    }
    public String message() { return null; }
    public void performAction (BreakOutGame game, long elapsedTime) { }
  }
  
  /**
   * The current input state, initially Normal
   */
  private InputState state = InputState.Normal;
  
  /**
   * handles any button or mouse presses
   */
  private void processInput ()
  {
    switch (getBsInput().getKeyPressed()) {
  
    // "Archive"
    case KeyEvent.VK_A:  state = state.gotA(this);  break;

    // "Ball" (try to fire a ball)
    case KeyEvent.VK_B:  state = state.gotB(this);  break;

    // "Miniball" (try to fire a miniball)
    case KeyEvent.VK_M:  state = state.gotM(this);  break;
    
    // "No"
    case KeyEvent.VK_N:  state = state.gotN(this);  break;

    // "Pause" (and unpause)
    case KeyEvent.VK_P:  state = state.gotP(this);  break;
    
    // "Quit"
    case KeyEvent.VK_Q:  state = state.gotQ(this);  break;
    
    // "Restore" from the snapshot
    case KeyEvent.VK_R:  state = state.gotR(this);  break;
    
    // "Snapshot" (make one)
    case KeyEvent.VK_S:  state = state.gotS(this);  break;
    
    // "Yes"
    case KeyEvent.VK_Y:  state = state.gotY(this);  break;  
    
    // "Toggle Spin"
    case KeyEvent.VK_T:  state = state.gotT(this);  break;

    // get here on any other key, or none
    default:
      // treat mouse button1 the same as M key
      if (bsInput.isMousePressed(MouseEvent.BUTTON1))
      {
        state = state.gotM(this);
      }
      break;
    }
  }

  /**
   * starts a new ball in the game
   * @param gs the current GameState
   */
  private void startNewBall (GameState gs)
  {
    Ball ball = new Ball(400, 400);       // start in center of screen
    double speed = gs.getBallSpeed();
    double angle = 0.75D * Math.PI;  // 45 degrees down and to the left
    ball.setVelocityPolar(speed, angle);
    ball.setActive(true);
    this.addBall(ball);
  }

  /**
   * This is a method to support replacement with a different object when testing
   * @return the BaseInput to use for keyboard and mouse input
   */
  protected BaseInput getBsInput () {
    return bsInput;
  }

  /**
   * deals with playing the win/lose sound, waiting for a keypress to exit, etc.
   */
  private void handleEndOfGame ()
  {
    GameState gs = GameState.getGameState();
    if (gs.levelDone())
    {
      if (noEndSound)
      {
        noEndSound = false;
        boolean won = gs.wonLevel();
        this.bsSound.play(won ? WIN_SOUND : LOSE_SOUND);
        if (state == InputState.Normal)
        {
          state = (won ? InputState.Won : InputState.Lost);
        }
      }
      if (getBsInput().isKeyPressed(KeyEvent.VK_ESCAPE))
      {
        parent.nextGameID = BreakOutEngine.BreakOutGameMenu;
        finish();
      }
    }
  }

  /**
   * returns an image, given the key part of its file name
   * @param which a String giving the key part of the image's file name
   * @return a BufferedImage, fetched from the file of conventional type
   * (.png) in the directory set to hold images (GraphicsDirectory)
   */
  private BufferedImage getImageFromFile (String which)
  {
    return getImage(GraphicsDirectory + which + ".png");
  }
  
  /**
   * handle the rendering; just the playing field plus the score
   */
  public void render (Graphics2D g)
  {
    playField.render(g);
    updateTextualDisplay(g);
  }

  /**
   * updates the textual parts of the display from the GameState 
   * @param g the Graphcs2D to which to display things
   */
  private void updateTextualDisplay (Graphics2D g)
  {
    GameState theState = GameState.getGameState();
    scoreFont.drawString(g, "Score: " + theState.getScore(), 10, 10);
    scoreFont.drawString(g, "Multiplier: " + theState.getMultiplier(), 10, 30);
    scoreFont.drawString(g, "Balls left: " + theState.getBallsRemaining(), 10, 50);
    scoreFont.drawString(g, "Miniballs left: " + theState.getMiniballsRemaining(), 10, 70);
    scoreFont.drawString(g, "Spin: " + Ball.reportSpin(), 10, 90);
    if (message != null)
    {
      doneFont.drawString(g, message, 10, 130);
    }
  }    

  /**
   * Utility routine or adding a paddle
   * @param p a Paddle to add
   */
  public void addPaddle (Paddle p)
  {
    p.setImage(getImageFromFile("Paddle"));
    p.setBackground(background);
    paddles.add(p);
  }

  /**
   * Utility routine for adding a Block; insures it is added to the SpriteGroup
   * @param b a Block to add
   */
  public void addBlock (Block b)
  {
    b.refreshImage();
    b.setAudio(bsSound);
    b.setGame(this);
    b.setLayer(-1);
    this.blocks.add(b);
  }

  /**
   * Utility routine for adding a Ball
   * @param b a Ball to add
   */
  public void addBall (Ball b)
  {
    b.setImage(getImageFromFile("Ball"));
    b.setBackground(background);
    b.setAudio(bsSound);
    b.setLayer(1);
    balls.add(b);
  }
  
  /**
   * Utility routine for adding a Miniball
   * @param m a Miniball to add
   */
  public void addMiniball (Miniball m)
  {
    m.setImage(getImageFromFile("Miniball"));
    m.setBackground(background);
    m.setAudio(bsSound);
    m.setLayer(1);
    miniballs.add(m);
  }

  /**
   * Utility routine for adding a PowerUp
   * @param p a PowerUp to add
   */
  public void addPowerUp (PowerUp p)
  {
    p.refreshImage();
    p.setBackground(background);
    p.setAudio(bsSound);
    p.setLayer(1);
    powerUps.add(p);
  }
  
  /**
   * Utility routine to drop a powerUp
   * @param x a double giving the x position where the powerUp starts 
   * @param y a double giving the y position there the powerUp starts
   * @param type a String indicating the type of powerUp
   */
  public void dropPowerUp (double x, double y, String type)
  {
    PowerUp powerUp = PowerUpFactory.createPowerUp(type, this, x, y);
    powerUp.setSpeed(0, .1);
    powerUp.setActive(true);
    this.addPowerUp(powerUp);
  }

  /**
   * routine for handling the details of firing a Ball
   */
  protected void fireBall ()
  {
    GameState gs = GameState.getGameState();
    Ball ball = new Ball(400, 400);       // start in center of screen
    double speed = gs.getBallSpeed();
    double angle = 0.75D * Math.PI;  // 45 degrees down and to the left
    ball.setVelocityPolar(speed, angle);
    ball.setActive(true);
    this.addBall(ball);
  }

  /**
   * routine for handling the details of firing a Miniball
   */
  protected void fireMiniball ()
  {
    GameState gs = GameState.getGameState();
    Paddle p = this.gamePaddle;
    double xStart = p.getX() + p.getWidth() / 2;
    double yStart = p.getY() + p.getHeight() / 2;
    Miniball miniball = new Miniball(xStart, yStart);
    miniball.setActive(true);
    miniball.setID(gs.obtainMiniballNumber());
    gs.startMiniballWaitTime(3000);
    this.addMiniball(miniball);
  }
}
