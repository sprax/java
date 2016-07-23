package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    public static final int MAX_SIZED_LEN =  9;
    public static final int MIN_WORD_COUNT = 2; // Beware of hapax legomena (very rare words)
    
    static boolean isAsciiLowerCaseLetter(char ch) {
        return ('a' <= ch && ch <= 'z');
    }
    
    static boolean isAsciiUpperCaseLetter(char ch) {
        return ('A' <= ch && ch <= 'Z');
    }

    public class WordCountComparator implements Comparator<String>
    {
        @Override
        public int compare(String sA, String sB) {
            int countA = wordCounts.getOrDefault(sA, 0);
            int countB = wordCounts.getOrDefault(sB, 0);
            if (countA != countB)
                return Integer.compare(countB, countA); // Descending counts
            return sA.compareTo(sB);
        }
    }
    
    public final String filePath;
    
    /// Letters
    int lowerLetterCounts[];
    ////int upperLetterCounts[];
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

    EnTextCounter(String filePath)
    {
        this.filePath = filePath;
        
        lowerLetterCounts = new int[ALPHABET_SIZE];
        ////upperLetterCounts = new int[ALPHABET_SIZE];
        firstLetterCounts = new int[ALPHABET_SIZE];
        finalLetterCounts = new int[ALPHABET_SIZE];
        charCounts = new CharCount[ALPHABET_SIZE];
        sizedWords = new ArrayList<ArrayList<String>>(MAX_SIZED_LEN+1);
        for (int j = 0; j < MAX_SIZED_LEN+1; j++)
            sizedWords.add(new ArrayList<String>());
                
        ArrayList<String> words = TextFileReader.readFileIntoArrayListOfWordsStr(filePath);
        for (String word : words) {
            totalWordCount++;
            
            // Ignore case for words of length > 1, and only count letters as lower case
            int length = word.length();
            if (length > 1) {
                word = word.toLowerCase();
            } else {
                lowerLetterCounts[Character.toLowerCase(word.charAt(0)) - 'a']++;
                totalLetterCount++;
            }
            
            int count = 0;
            if (wordCounts.containsKey(word)) {
                count = wordCounts.get(word);
                wordCounts.put(word, ++count);
            }
            else {
                wordCounts.put(word, 1);
                uniqWordCount++;
                if (length <= MAX_SIZED_LEN)
                    sizedWords.get(length).add(word);
            }
        }
        
        for (String word : wordCounts.keySet()) {
            int count = wordCounts.get(word);
            int length = word.length();
            if (length < 2)
                continue;
            
            totalLetterCount += count * length;
            
            ////if ((double)count/totalWordCount > 0.016)
                ////Sx.format("%15s \t %3d\n", word, count);

            char chr = word.charAt(0);
            addCharCountToLowerCount(chr, count);
            firstLetterCounts[chr - 'a'] += count;
            for (int j = 1; j < length; j++) {
                chr = word.charAt(j);
                addCharCountToLowerCount(chr, count);
            }
            finalLetterCounts[chr - 'a'] += count;
        }
  
        for (int j = 0; j < ALPHABET_SIZE; j++) {
            char chr = (char)(j + 'a');
            int count = lowerLetterCounts[j];
            ////Sx.format("%c  %4d\n", chr, count);
            charCounts[j] = new CharCount(chr, count);            
        }
        Arrays.sort(charCounts);
        
        WordCountComparator wordCountComp = new WordCountComparator();
        for (int len = 2; len <= MAX_SIZED_LEN; len++) {
            Collections.sort(sizedWords.get(len), wordCountComp);
        }
    }
    
    void addCharCountToLowerCount(char chr, int count) {
        assert(isAsciiLowerCaseLetter(chr));
        lowerLetterCounts[chr - 'a'] += count;    
    }
    
    /*
    void addCharCountToUpperOrLowerCount(char chr, int count) {
        if (isAsciiLowerCaseLetter(chr)) {
            lowerLetterCounts[chr - 'a'] += count;
        }
        else {
            upperLetterCounts[chr - 'A'] += count;
        }
    }
    */

    public void showCounts(String label, int verbose)
    {
        showTotalCounts(label, verbose);
        
        // Raw letter frequencies
        if (verbose > 1) {
            for (CharCount cc : charCounts) {
                Sx.format("%4d %c\n", cc.count, cc.chr);
            }
        }

        for (int len = 1; len <= MAX_SIZED_LEN; len++) {
            Sx.format("%d-letter words:  %d:\n", len, sizedWords.get(len).size());
            for (String word : sizedWords.get(len)) {
                int count =  wordCounts.get(word);
                if (count > MIN_WORD_COUNT + 1 - verbose)
                    Sx.format("%4s  %3d\n", word, count);
            }
        }
        showTotalCounts("Again: " + label, verbose);
    }

    public void showTotalCounts(String label, int verbose)
    {
        Sx.format("%s text file path: %s\n", label, filePath);
        Sx.format("Total words: %d   Unique words: %d  Total letters: %d\n"
                , totalWordCount, uniqWordCount, totalLetterCount);
    }
    
    public static int unit_test(int level) 
    {
        String testName = EnTextCounter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
    
        String filePath = FileUtil.getTextFilePath("corpusEn.txt");
        EnTextCounter myEtc = new EnTextCounter(filePath);
        myEtc.showCounts("showCounts for", 2);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }


     public static void main(String[] args) {
        unit_test(1);
    }

}
