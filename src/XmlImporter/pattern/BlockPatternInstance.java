package XmlImporter.pattern;

import java.awt.Point;

/**
 * The interface for Block Patterns. To make a new one, just create a new subclass (it MUST be inside package
 * XmlImporter.pattern (which is something that could use fixing)
 * @author Paul Barba
 *
 */
public interface BlockPatternInstance {
	
	/**
	 * nextBlock is where the pattern gets expressed, by selecting the location for each block.
	 * @param previous the Point nextBlock returned last time
	 * @return the location of the next block.
	 */
	//Is it necessary to pass in the last point? Arguably the BlockPattern should just remember it. Possible change.
	public Point nextBlock(Point previous);
	
	/**
	 * Before every block is placed, changeBlock is polled. When it returns true, the next block image specified in
	 * the xml is used.
	 * @return should a new block image be used for the next one?
	 */
	//+works with any quantity of blocks. -The changes just loop through the blocks in order. More complex patterns
	//That want to do very specific things with the order of the block images aren't expressable.
	public boolean changeBlock();
	
	/**
	 * Lets the pattern request a number of blocks to be used. Useful in cases like a rectangle, where only a specific
	 * Number of blocks is appropriate. 
	 * @return the best number of blocks to use in this pattern.
	 */
	//Another possible change would be to add an argument "request". In the rectangle example, preferred size would then
	//return the closest number that would form a complete rectangle. This gives the user a little more control in
	//designing levels.
	public int preferredSize();
	
	/**
	 * Called before any other function to allow the pattern to accept parameters.
	 * @param args The text in the xml between the parenthesises, broken up by commas.
	 */
	public void parseArgs(String[] args);
}
