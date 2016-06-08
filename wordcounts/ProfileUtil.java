
package sprax.wordcounts;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Profile for a speaker or writer based on word-tuples collected from their sentences.
 */
public abstract class ProfileUtil {

    public static String TEXT_DIR = "/Users/sprax/Text";

    protected static String getTextFilePath(String fileName) {
        Path path = Paths.get(TEXT_DIR, fileName);
        return path.toString();
    }
    

    /**
     * Splits a string of white-space-separated words into an array of word strings
     * @return primitive array of Strings representing words (may be empty, that is, an array of length 0)
     */
    public static String[] parseStringToWords(final String sentence) 
    {
        assert( ! sentence.isEmpty());
        return TextFilters.toWordArray(sentence);
    }


    //////// Unit testing ////////

    public static void unit_test() 
    {
        String  testName = ProfileUtil.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    

        String subDirAndTitle = "Melville/The_Confidence-Man.txt";
        final String textFilePath = getTextFilePath(subDirAndTitle);
        
        System.out.format("From dir and file name (%s) and (%s), getTextFilePath gives: %s\n", 
                TEXT_DIR, subDirAndTitle, textFilePath);

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }

}
