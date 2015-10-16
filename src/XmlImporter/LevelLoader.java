package XmlImporter;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;
import java.io.*;
import BOut.*;
import XmlImporter.pattern.*;
import breakout.*;
import breakout.BlockTemplateDocument.*;
import breakout.PowerupDocument.Powerup;

/**
 * LevelLoader takes in an xml file and fills the screen with the objects for that level.
 * @author Paul Barba
 *
 */
public class LevelLoader {

  /**
   * the XML file can describe templates and give them names;
   * this table maps those names to the templates 
   */
  private Map<String,BlockTemplateDocument.BlockTemplate> table =
    new HashMap<String,BlockTemplateDocument.BlockTemplate>();
	
  /**
   * the BreakOutGame into which we will load things, e.g., create blocks
   */
  private BreakOutGame ourGame;

  /**
   * default grid spacing for blocks in the horiz dimension, in pixels
   */
  private int xGrid = 64;
  
  /**
   * default grid spacing for blocks in the vert dimension, in pixels
   */
  private int yGrid = 32;
	
  /**
   * One LevelLoader is needed per Game Object. This means that
   * if we use one Game for all levels, we use one LevelLoader.
   * If we instantiate a new one every level, we need many.
   * @param game The Game object that the level should be drawing.
   */
  public LevelLoader (BreakOutGame game)
  {
    this.ourGame = game;
  }
	
  /**
   * Takes in an xml file, and prepares the level described.
   * @param filename An xml file that matches the levelScheme.xsd schema.
   */
  public void loadGame (String filename)
  {
    table.clear(); // Don't want the templates from last level persisting.
    try {
      //GameDocument is the root object of a Level instance.
      GameDocument loader = GameDocument.Factory.parse(new File(filename));
      GameDocument.Game game = loader.getGame();
      this.xGrid = game.getGridX(); //Grids are used for lengths not ending with px.
      this.yGrid = game.getGridY();
      for (BlockTemplate template : game.getBlockTemplateArray())
      {
        table.put(template.getId(), template);
      }
      for (BlockInstanceDocument.BlockInstance blockData : game.getLevelArray(0).getBlockInstanceArray())
      {
        BlockTemplate template = table.get(blockData.getTemplate());
        if (template == null)
        {
          continue;  // skip it (TODO: should give some kind of error message)
        }
        createBlock(
            template,
            parseLength(blockData.getX(), xGrid),
            parseLength(blockData.getY(), yGrid));
      }
      for (BlockPatternDocument.BlockPattern pattern : game.getLevelArray(0).getBlockPatternArray())
      {
        BlockPatternInstance patternClass = PatternFactory.getInstance().getBlockPattern(pattern);
        String[] blocks = pattern.getBlockArray();
        int currentBlock = 0;
        Point location = new Point(pattern.getStartx(), pattern.getStarty());
        int lastBlock = pattern.getCount();
        if (lastBlock < 1) lastBlock = patternClass.preferredSize();
        for (int i = 0; i < lastBlock; i++)
        {
          createBlock(
              table.get(blocks[currentBlock]),
              gridToPixels(location.x, xGrid),
              gridToPixels(location.y, yGrid));
          if (patternClass.changeBlock())
          {
            currentBlock++;
            if (currentBlock >= blocks.length) currentBlock = 0;
          }
          location = patternClass.nextBlock(location);
        }
      }
    }
    catch(Exception e)
    {
      System.out.println(e.getMessage());
    }
  }
		
  /**
   * creates a block given the template to use and its x and y location in pixels 
   * @param template a BlockTemplate describing the desired block
   * @param x a double giving the x position in pixels
   * @param y a double giving the y position in pixels
   */
  private void createBlock (BlockTemplate template, double x, double y)
  {
    try {
      Block block = new Block(BlockDescFactory.getBlockDesc(template), x, y);
      block.setValue(template.getScore());
      block.setGame(ourGame);
      for (Powerup p : template.getPowerupArray())
      {
        //powerups.Powerup instantiation = (powerups.Powerup)(Class.forName("powerups."+p.getClass1()).newInstance());
        block.addPowerup(p.getClass1(), p.getProbability()/100.0f);
      }
      ourGame.addBlock(block);
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage()); //We don't need to die: We'll just skip this block.
    }
  }
	
  /**
   * parses a length (coordinate x or y position); this may be a plain
   * floating point number, indicating a grid position, which is multipled
   * by the grid distance, or a double followed by "px" to indicate an
   * exact pixel position (not multiplied by the grid distance)
   * @param length a String to parse: a floating point number optionally follow by "px"
   * @param grid an int giving the grid distance in pixels
   * @return a double giving the converted distance in pixels
   */
  private double parseLength (String length, int grid)
  {
    if (length.endsWith("px"))
    {
      return Double.parseDouble(length.substring(0,length.length() - 2));
    }
    else
    {
      return gridToPixels(Double.parseDouble(length), grid);
    }
  }
	
  /**
   * Converts a grid coordinate to pixels
   * @param gridCoord a double giving the grid coordinate
   * @param grid an int giving the grid distance
   * @return a double, the product of the coordinate and the grid distance
   */
  private double gridToPixels(double gridCoord, int grid)
  {
    return gridCoord * grid;
  }
  
}
