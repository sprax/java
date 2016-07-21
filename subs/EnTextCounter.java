package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.sprout.Sx;
import sprax.test.Sz;

/** Count of a single character (such a lower-case a letter) in a corpus */
class CharCount implements Comparable<CharCount>
{
    final char chr;
    final int count;
    CharCount(char chr, int count) {
        this.chr = chr;
        this.count = count;
    }
    
    @Override   // Most frequent first means: descending by count.
    public int compareTo(CharCount that) {
        return Integer.compare(that.count, this.count);
    }
}


/** Counts letters and words in an English language text */
public class EnTextCounter
{
    public static final int ALPHABET_SIZE = 26;
    public static final int MAX_SIZED_LEN = 3;
    
    public final String fileName;
    public final String filePath;
    
    /// Letters
    int lowerLetterCounts[];
    int firstLetterCounts[];
    int finalLetterCounts[];
    int totalLetterCount;
    CharCount charCounts[];
    
    /// Words
    Map<String, Integer> wordCounts = new HashMap<>();
    ArrayList<ArrayList<String>> sizedWords;    
    int totalWordCount;
    int uniqWordCount;
    int minWordLen;
    int maxWordLen;
    int maxFoundLen;

    EnTextCounter(String fileName)
    {
        this.fileName = fileName;
        
        lowerLetterCounts = new int[ALPHABET_SIZE];
        firstLetterCounts = new int[ALPHABET_SIZE];
        finalLetterCounts = new int[ALPHABET_SIZE];
        charCounts = new CharCount[ALPHABET_SIZE];
        sizedWords = new ArrayList<ArrayList<String>>(MAX_SIZED_LEN+1);
        for (int j = 0; j < MAX_SIZED_LEN+1; j++)
            sizedWords.add(new ArrayList<String>());
        
        
        filePath = FileUtil.getTextFilePath(fileName);
        ArrayList<String> lowerWords = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(filePath);
                for (String word : lowerWords) {
            totalWordCount++;
            int count = 0;
            if (wordCounts.containsKey(word)) {
                count = wordCounts.get(word);
                wordCounts.put(word, ++count);
            }
            else {
                wordCounts.put(word, 1);
                uniqWordCount++;
                int len = word.length();
                if (len <= MAX_SIZED_LEN)
                    sizedWords.get(len).add(word);
            }
        }
        
        for (String word : wordCounts.keySet()) {
            int count = wordCounts.get(word);
            int len = word.length();
            totalLetterCount += count * len;
            
            ////if ((double)count/totalWordCount > 0.016)
                ////Sx.format("%15s \t %3d\n", word, count);

            int idx = word.charAt(0) - 'a';
            lowerLetterCounts[idx] += count;
            for (int j = 1; j < len; j++) {
                idx = word.charAt(j) - 'a';
                lowerLetterCounts[idx] += count;
            }
            finalLetterCounts[idx] += count;
        }
  
        for (int j = 0; j < ALPHABET_SIZE; j++) {
            char chr = (char)(j + 'a');
            int count = lowerLetterCounts[j];
            ////Sx.format("%c  %4d\n", chr, count);
            charCounts[j] = new CharCount(chr, count);            
        }
        Arrays.sort(charCounts);
    }

    public void showCounts(int verbose)
    {
        
        Sx.format("Text file path: %s\n", filePath);
        Sx.format("Total words: %d   Unique words: %d  Total letters: %d\n"
                , totalWordCount, uniqWordCount, totalLetterCount);

        for (CharCount cc : charCounts) {
            Sx.format("%4d %c\n", cc.count, cc.chr);
        }

        for (int len = 1; len < 3; len++) {
            Sx.format("%d-letter words:  %d:\n", len, sizedWords.get(len).size());
            for (String word : sizedWords.get(len)) {
                Sx.format("%4s  %3d\n", word, wordCounts.get(word));
            }
        }
    }


    public static int unit_test(int level) 
    {
        String testName = EnTextCounter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
    
        EnTextCounter myEtc = new EnTextCounter("corpus.txt");
        myEtc.showCounts(1);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }


     public static void main(String[] args) {
        unit_test(1);
    }

}
