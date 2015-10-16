package BOut;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Manages a set of files for a file menu
 * 
 * Supports circular navigation and refresh
 * 
 * @author Eliot Moss
 */
public class FileSet implements Iterable<File> {

  /**
   * the directory that this FileSet searches
   */
  private final String directory;
  
  /**
   * the pattern string
   */
  private final String pattern;
  
  /**
   * current list of file names
   */
  private File[] files;
  
  /**
   * position in the list
   */
  private int index;
  
  /**
   * Creates and populates a file set consisting of the files
   * in the given directory that match the pattern; the initial
   * file is the first one
   * 
   * @param directory a String giving the name of the directory; we assume it exists, etc.
   * @param pattern a String giving a regex to match against file names in that directory
   */
  public FileSet (String directory, String pattern)
  {
    this.directory = directory;
    this.pattern   = pattern;
    refresh();
  }
  
  /**
   * refresh the list (and make sure the index is in range);
   * this is helpful if external actions may have changed which
   * files exist
   */
  public void refresh ()
  {
    File theDir = new File(directory);
    files = theDir.listFiles(
        new FilenameFilter() {
          public boolean accept (File dir, String name) {
            return Pattern.matches(pattern, name);
          }
        });
    Arrays.sort(files, new Comparator<File>() {
      public int compare (File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
      }
    });
    fixIndex();
  }
  
  /**
   * guarantees that 0 <= index < length of array, unless array is
   * empty or non-existent, in which case index is set to 0
   */
  private void fixIndex ()
  {
    int n = size();
    if (n == 0)
    {
      index = 0;
    }
    else
    {
      index = (((index % n) + n) % n); // this way ot handle negative numbers too
    }
  }
  
  /**
   * Used to advance the index, circularly, in either direction by any amount
   * @param amount an int giving the number of slots to proceed circularly (negative
   * means backwards, also circularly)
   */
  public void advance (int amount)
  {
    index += amount;
    fixIndex();
  }
  
  /**
   * @return an int giving the number of files in the set
   */
  public int size ()
  {
    return (files == null) ? 0 : files.length;
  }
  
  /**
   * obtains the current File
   * @return the File at the current position in the FileSet; returns null
   * if the FileSet has no files
   */
  public File current ()
  {
    if (size() == 0)
    {
      return null;
    }
    else
    {
      return files[index];
    }
  }
  
  /**
   * get the display form of the file name;
   * this is the whole name is the pattern has no groups,
   * otherwise it is the contents of the first group
   * @return a String suitable for display
   */
  public String currentDisplay ()
  {
    File file = current();
    if (file == null)
    {
      return "";
    }
    return getDisplayFor(file.getName());
  }
  
  /**
   * finds the display form of a name
   * @param name a String giving the full name to look up
   * @return the display form String for it
   */
  public String getDisplayFor (String name)
  {
    int pos = name.lastIndexOf('.');
    return (pos > 0) ? name.substring(0, pos) : name;
  }
  
  /**
   * Provide an iterator over the FileSet
   * @return a Iterator<File> that iterates over the FileSet
   */
  public Iterator<File> iterator ()
  {
    return new Iterator<File>() {
      private int pos = 0;
      public boolean hasNext () { return files != null && pos < files.length; }
      public void    remove  () { throw new UnsupportedOperationException(); }
      public File    next    () { return files[pos++]; }
    };
  }
}
