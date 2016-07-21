package sprax.subs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.files.TextFileToStringCollection;
import sprax.files.TextFilters;
import sprax.test.Sz;

/** Counts letters and words in an English language text */
class EnTextCounter
{
    public static final int ALPHABET_SIZE = 26;
    public static final int MAX_SIZED_LEN = 3;
    public final String fileName;
    Map<String, Integer> wordCounts = new HashMap<>();
    ArrayList<ArrayList<String>> sizedWords;
    
    int lowerLetterCounts[];
    int upperLetterCounts[];

    int mMinWordLen;
    int mMaxWordLen;
    int mMaxFoundLen;

    EnTextCounter(String fileName) {
        this.fileName = fileName;
        String path = FileUtil.getTextFilePath(fileName);
        ArrayList<String> lowerWords = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(path);
        sizedWords = new ArrayList<ArrayList<String>>(MAX_SIZED_LEN);
        for (String word : lowerWords) {
            int count = 0;
            if (wordCounts.containsKey(word)) {
                count = wordCounts.get(word);
                wordCounts.put(word, ++count);
            }
            else {
                wordCounts.put(word, 1);
                int len = word.length();
                if (len > 3)
                    continue;
                sizedWords.get(len).add(word);
            }
        }
        
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
