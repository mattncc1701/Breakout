package BOut;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import static BOut.BreakOutEngine.*;

/**
 * Describes the appearance of a PowerUp
 * It is just part of a filename used to find an image file
 * 
 * @author Eliot Moss
 */
public class PowerUpDesc implements Serializable {
  
  /**
   * the short name of the PowerUp appearance
   */
  private final String desc;
  
  /**
   * the full filename we will use
   */
  private final String filename;
  
  /**
   * Create a description given the String that names it
   * 
   * @param desc a String naming the PowerUp appearance
   */
  private PowerUpDesc (String desc)
  {
    this.desc = desc;
    this.filename = GraphicsDirectory + "PowerUp" + desc + ".png"; 
  }

  /**
   * Factory method for making a PowerUpDesc;
   * allows for future optimization :-)
   * @param desc the String naming the PowerUp appearance
   * @return a PowerUpDesc for that appearance
   */
  public static PowerUpDesc getPowerUpDesc (String desc)
  {
    return new PowerUpDesc(desc);
  }
  
  /**
   * @return the desc
   */
  public String getDesc () {
    return this.desc;
  }

  /**
   * Obtain the image corresponding to this appearance description
   * @return the BufferedImage for this PowerUp description
   */
  public BufferedImage getImage ()
  {
    return BreakOutEngine.getEngine().getImage(filename);
  }
}
