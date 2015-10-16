package XmlImporter.pattern;
import org.apache.xmlbeans.XmlException;

import breakout.BlockPatternDocument;

/**
 * Singleton Factory that turns descriptions into Pattern objects. Makes use of reflection.
 * @author Paul Barba
 *
 */
public class PatternFactory 
{
	  private static PatternFactory instance = new PatternFactory();

	  private PatternFactory()
	  {
	  }

	  public static PatternFactory getInstance()
	  {
		  return instance;
	  }
	  
	  /**
	   * Turns an xml description of a pattern into an actual object using reflection.
	   * @param description the xml object describing the pattern to use.
	   * @return a BlockPattern that can now be used to place blocks
	   * @throws XmlException if the patternClass isn't described properly. Most likely cause: incorrect Class Name.
	   */
	  public BlockPatternInstance getBlockPattern(BlockPatternDocument.BlockPattern description) throws XmlException
	  {
		  try{
			  /* In xml you'd write class="SpiralPattern(3,2,false,etc)" to pass arguments into the pattern.
			   * An alternative would be to do class="SpiralPattern" args="3,2,false,etc". I just liked this syntax.
			   */
			  String[] tokens = description.getClass1().split("[(,)]");
			  //Instantiates a class based on the name of the pattern. Prevents us from having to keep a list of valid patterns.
			  BlockPatternInstance patternClass = (BlockPatternInstance)(Class.forName("XmlImporter.pattern."+tokens[0]).newInstance());
			  patternClass.parseArgs(tokens);
			  return patternClass;
		  }
		  catch(IllegalAccessException e)
		  {
			  throw new XmlException(e.getMessage());
		  }
		  catch(InstantiationException e)
		  {
			  throw new XmlException(e.getMessage());
		  }
		  catch(ClassNotFoundException e)
		  {
			  throw new XmlException(e.getMessage());
		  }
	  }
}
