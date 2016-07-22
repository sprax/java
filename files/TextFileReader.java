package sprax.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

/**
 * Read the lines of a text file into one container or StringBuffer.
 * 
 * Reads a text file line-by-line and generically adds each line to a 
 * container or "string collector," such as a HashSet, HashMap, Hashtable,
 * StringBuffer, Trie, or what have you, using the StringCollectorInterface 
 * interface defined below.  This simple interface has no need for
 * the more elaborate generic methods such as Collection.addAll().
 * So we just parameterize it by <T> instead of <T extends Collection>.
 * 
 * For a Collection-specific utility in this package, see TextFileToStringCollection.
 * 
 * @author Sprax Lines
 */
public class TextFileReader
{
    
    public static HashSet<String> readFileIntoHashSet(String textFilePath)
    {
        HashSetStringCollector myHSSC = new HashSetStringCollector();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(myHSSC);
        return myHSSC.getCollector();
    }
    
    public static TreeSet<String> readFileIntoTreeSet(String textFilePath)
    {
        TreeSetStringCollector mySC = new TreeSetStringCollector();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(mySC);
        return mySC.getCollector();
    }
    
    public static HashMap<String, Integer> readFileIntoHashMap(String textFilePath)
    {
        HashMapStringCollector myHMSC = new HashMapStringCollector();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(myHMSC);
        return myHMSC.getCollector();
    }
    
    public static ArrayList<String> readFileIntoArrayList(String textFilePath)
    {
        ArrayListStringCollector myALSC = new ArrayListStringCollector();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(myALSC);
        return myALSC.getCollector();
    }
    
    public static ArrayList<char[]> readFileIntoArrayListOfLowerCaseWordsChr(String textFilePath)
    {
        ArrayListLowerCaseWordCollector myWords = new ArrayListLowerCaseWordCollector();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoCharrayCollector(myWords);
        return myWords.getCollector();
    }
    
    public static ArrayList<String> readFileIntoArrayListOfWordsStr(String textFilePath)
    {
        ArrayListWordCollectorStr myWords = new ArrayListWordCollectorStr();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(myWords);
        return myWords.getCollector();
    }
    
    public static ArrayList<String> readFileIntoArrayListOfLowerCaseWordsStr(String textFilePath)
    {
        ArrayListLowerCaseWordCollectorStr myWords = new ArrayListLowerCaseWordCollectorStr();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(myWords);
        return myWords.getCollector();
    }
    
    public static StringBuffer readFileIntoStringBuffer(String textFilePath)
    {
        StringBufferStringCollector mySBSC = new StringBufferStringCollector();
        TextFileReader tfr = new TextFileReader(textFilePath);
        tfr.readIntoStringCollector(mySBSC);
        return mySBSC.getCollector();
    }
    
    private final String mTextFilePath;
    
    public TextFileReader(String textFilePath) {
        mTextFilePath = textFilePath;
    }
    
    
    // type wild-carding
    void printCollection(Collection<?> c) {
        for (Object e : c) {
            System.out.println(e);
        }
    }
    
    public int readIntoStringCollector(StringCollectorInterface<?> stringCollector)
    {
        int lineCount = 0;
        File file = new File(mTextFilePath);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
                while ((line = reader.readLine()) != null) {
                    if (stringCollector.addString(line)) {
                        lineCount++;
                    }
                }
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND: " + mTextFilePath);
            e.printStackTrace();  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lineCount;
    }
    
    static final int sCharBufSize = 8192; // 2048;
    
    public int readIntoCharrayCollector(StringCollectorInterface<?> stringCollector)
    {
        BufferedReader reader = null;
        try {
            File file = new File(mTextFilePath);
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {    
            if (reader == null)
                return 0;
        } 
        
        char line[] = new char[sCharBufSize + 128];
        String rest = null;
        int lineCount = 0, charCount = 0, rlen;
        try {
            while ((charCount = reader.read(line, 0, sCharBufSize)) > 0) {
                if (charCount == sCharBufSize) {
                    rest = reader.readLine();
                    rlen = rest.length();
                    //char chrs[] = rest.toCharArray();
                    for (int j = sCharBufSize, k = 0; k < rlen; k++, j++) 
                        line[j] = rest.charAt(k);
                    charCount += rlen;
                }
                if (stringCollector.addString(line, 0, charCount)) {
                    lineCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lineCount;
    }
    
    public static int unit_test(final String[] args)
    {
        
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
   
        String textFilePath = null;
        if (args.length > 0 && args[0] != null) {
            textFilePath = args[0];
        } else  {
            textFilePath = new String("ReadMe.txt"); 
        }
        
        for (String arg : args) {
            System.out.println(arg);     
        }
        
        TextFileReader tfr = new TextFileReader(textFilePath);
        
        System.out.println("\n\t  readFileIntoStringCollector<HashSet>:");
        HashSetStringCollector myHSSC = new HashSetStringCollector();
        int numSetLines = tfr.readIntoStringCollector(myHSSC);
        if (numSetLines == 0) {
            System.out.println("<file empty>");
        } else {
            System.out.println("<found " + numSetLines + " lines>");
            Iterator<String> its = myHSSC.getCollector().iterator();
            while ( its.hasNext() ) {
                System.out.println( its.next() );
            }
        }
        
        System.out.println("\n\t  readFileIntoStringCollector<StringBuffer>:");
        StringBufferStringCollector mySBSC = new StringBufferStringCollector();
        int numStrLines = tfr.readIntoStringCollector(mySBSC);
        if (numStrLines == 0) {
            System.out.println("<file empty>");
        } else {
            System.out.println("<found " + numStrLines + " lines>");
            System.out.println( mySBSC.mStringBuffer.toString() );
        }
        
        System.out.println("\n\t  readFileIntoStringCollector<Hashtable>:");
        HashtableStringCollector myHTSC = new HashtableStringCollector();
        int numTabLines = tfr.readIntoStringCollector(myHTSC);
        if (numTabLines == 0) {
            System.out.println("<file empty>");
        } else {
            System.out.println("<found " + numTabLines + " lines>");
            for (Entry<String, Integer> line : myHTSC.mHashtable.entrySet()) {
                System.out.println(line);
            }
        }
        
        System.out.println("\n\t  readFileIntoStringCollector<HashMap>:");
        HashMapStringCollector myHMSC = new HashMapStringCollector();
        int numMapLines = tfr.readIntoStringCollector(myHMSC);
        if (numMapLines == 0) {
            System.out.println("<file empty>");
        } else {
            System.out.println("<found " + numMapLines + " lines>");
            for (String line : myHMSC.mHashMap.keySet()) {
                System.out.println(line + " : " + myHMSC.mHashMap.get(line));
            }
        }
        
        // test static methods:
        
        System.out.println("\n\t  readFileIntoHashSet:");
        HashSet<String> lines = TextFileReader.readFileIntoHashSet(textFilePath);
        if (lines == null || lines.isEmpty()) {
            System.out.println("<file empty>");
        } else {
            for (String line : lines) {
                System.out.println(line);
            }
        }
        
        System.out.println("\n\t  readFileIntoArrayList:");
        ArrayList<String> alls = TextFileReader.readFileIntoArrayList(textFilePath);
        if (alls == null || alls.isEmpty()) {
            System.out.println("<file empty>");
        } else {
            for (String line : alls) {
                System.out.println(line);
            }
        }
        
        System.out.println("\n\t  readFileIntoArrayListOfLowerCaseWords:");
        ArrayList<char[]> words = TextFileReader.readFileIntoArrayListOfLowerCaseWordsChr(textFilePath);
        if (words == null || words.isEmpty()) {
            System.out.println("<file empty>");
        } else {
            for (char[] line : words) {
                System.out.println(line);
            }
        }
        
        System.out.println("\n\t  readFileIntoStringBuffer:");
        StringBuffer contents = TextFileReader.readFileIntoStringBuffer(textFilePath);
        if (contents == null || contents.length() == 0) {
            System.out.println("<file empty>");
        } else {
            System.out.println(contents.toString());
        }
        
        return 0;
    }
    
    public static void main(final String[] args) { unit_test(args); }
    
    
    // Overly-specified method depends on final, class-scope data of non-generic type HashSet<String>:
    /****  
    final HashSet<String> mLines = new HashSet<String>();
    System.out.println("\n\t  readFileIntoStringCollector:");

    int numLines = tfr.readFileIntoStringCollector(textFilePath, new StringCollectorInterface() {
      public boolean addString(String str) {
        return mLines.add(str);
      }
    });
    if (lines == null || lines.isEmpty()) {
      System.out.println("<file empty>");
    } else {
      System.out.println("<found " + numLines + " lines>");
      for (String line : lines) {
        System.out.println(line);
      }
    }
     ****/    
    
}

