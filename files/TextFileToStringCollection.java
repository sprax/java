package sprax.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import sprax.Sx;

/**
 * Reads a text file into a Collections object.
 * For example, to add every line of the file "MobyDick.txt" into an ArrayList of Strings, use:<code><br>
 *   List<String> textLines = TextFileToStringCollection.load(new ArrayList<String>(), "MobyDick.txt");</code><br>
 * To then append the text of "middlemarch.txt", use:<code><br>
 *   TextFileToStringCollection.load(textLines, "middlemarch.txt");</code> 
 */
public class TextFileToStringCollection
{
  /**
   * Reads the specified text file into the Collections object <code>collection</code>.
   */
  static public <T extends Collection<String>> int readFile(T collection, String textFilePath)
  {
    BufferedReader reader = null;
    try {
      File file = new File(textFilePath);
      reader = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return 0;
    }
    
    int lineCount = 0;
    if (reader != null) {
      String line = null;
      try {
        while ((line = reader.readLine()) != null) {
          if (collection.add(line)) {
            lineCount++;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          if (reader != null) {
            reader.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return lineCount;
  }    
  
  static public <T extends Collection<String>> T load(T collection, String filePath) {
    readFile(collection, filePath);
    return collection; 
  }
  
  public static int test_load(Collection<String> collection, String filePath, int nLinesToPrint)
  {
    System.out.println("test_load: Reading " + filePath + " into a " + collection.getClass().getName());
    TextFileToStringCollection.load(collection, filePath);
    if (collection.size() > 0) {
      for (String line : collection) {
        if (--nLinesToPrint == 0)
          break;
        Sx.puts(line);
      }
      Sx.puts();
      return  0;
    } else {
      System.out.println("No lines read from " + filePath);
      return -1;
    }
  }
  
  public static int unit_test()
  {
    int stat = 0;
    Sx.puts(TextFileToStringCollection.class.getName() + ".unit_test");  
    String filePath = "ReadMe.txt";
    

    stat += test_load(new ArrayList<String>(), filePath, 4);
    stat += test_load(new LinkedList<String>(), filePath, 4);
    stat += test_load(new LinkedHashSet<String>(), filePath, 4);
    stat += test_load(new HashSet<String>(), filePath, 4);
    stat += test_load(new TreeSet<String>(), filePath, 4);     
    return stat;
  }
  
  public static void main(final String[] args) { unit_test(); }
}
