package sprax.subs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sprax.files.FileUtil;
import sprax.files.TextFileToStringCollection;
import sprax.files.TextFilters;
import sprax.test.Sz;

/** Counts letters and words in an English language text */
class EnTextCounter
{
    public static final int ALPHABET_SIZE = 26;
    
    public final String fileName;
    Map<String, Integer> WordCounts = new HashMap<>();
    int lowerLetterCounts[];
    int upperLetterCounts[];

    int mMinWordLen;
    int mMaxWordLen;
    int mMaxFoundLen;

    EnTextCounter(String fileName) {
        this.fileName = fileName;
        test_textFileToWords(fileName);
    }

    static int test_textFileToWords(String fileName) 
    {
        String path = FileUtil.getTextFilePath(fileName);
        List<String> text = TextFileToStringCollection.load(new ArrayList<String>(), path);
        String lines[] = new String[text.size()]; 
        return TextFilters.test_toLowerCaseLetterWords(text.toArray(lines));
    }
}

public class SubCipher 
{
    String cipherFileName;
    String corpusFileName;
    EnTextCounter cipherTextCounter;
    EnTextCounter corpusTextCounter;
    
    SubCipher() {
        this("cipher.txt", "corpus.txt");
    }
    
    SubCipher(String cipherFileName, String corpusFileName) 
    {
        this.cipherFileName = cipherFileName;
        this.corpusFileName = corpusFileName;
        cipherTextCounter = new EnTextCounter(cipherFileName);
        corpusTextCounter = new EnTextCounter(corpusFileName);
    }


    public static int unit_test(int level) 
    {
        String testName = SubCipher.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
    
        SubCipher mySC = new SubCipher();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }


     public static void main(String[] args) {
        unit_test(1);
    }

}
