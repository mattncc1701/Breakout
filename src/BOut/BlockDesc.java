package BOut;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * @author Eliot Moss
 * 
 * Describes the appearance of a rectangular block, namely
 * width, height, and RGB color. Caches the actual image.
 *
 */
public class BlockDesc implements Serializable
{

  /**
   * the width of the block image, in pixels
   */
  private final int width;
  
  /**
   * the height of the block image, in pixels
   */
  private final int height;
  
  /**
   * the redness of the block image
   */
  private final int r;
  
  /**
   * the greenness of the block image
   */
  private final int g;
  
  /**
   * the blueness of the block image
   */
  private final int b;
  
  /**
   * the constructed image; may be null!
   */
  private transient BufferedImage image;
  
  /**
   * Construct a new BlockDesc object given values describing a block's appearance
   * @param width an int giving the width in pixels
   * @param height an int giving the height in pixels
   * @param r an int giving the redness of the color
   * @param g an int giving the greenness of the color
   * @param b an int giving the blueness of the color
   */
  public BlockDesc(int width, int height, int r, int g, int b)
  {
    this.width  = width;
    this.height = height;
    this.r      = r;
    this.g      = g;
    this.b      = b;
  }

  /**
   * @return the width
   */
  public int getWidth ()
  {
    return this.width;
  }

  /**
   * @return the height
   */
  public int getHeight ()
  {
    return this.height;
  }

  /**
   * @return the r
   */
  public int getR ()
  {
    return this.r;
  }

  /**
   * @return the g
   */
  public int getG ()
  {
    return this.g;
  }

  /**
   * @return the b
   */
  public int getB ()
  {
    return this.b;
  }

  /**
   * @return the image, creating it if necessary
   */
  public BufferedImage getImage ()
  {
    if (image == null)
    {
      image = makeImage(width, height, r, g, b);
    }
    return this.image;
  }

  /**
   * Creates an actual image as described
   * @param width an int giving the width in pixels
   * @param height an int giving the height in pixels
   * @param r an int giving the redness
   * @param g an int giving the greenness
   * @param b an int giving the blueness
   * @return a BufferedImage rectangle as described
   */
  private static BufferedImage makeImage (int width, int height, int r, int g, int b)
  {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(new Color(r, g, b));
    graphics.fill3DRect(0, 0, width, height, true);
    return image;
  }

}
