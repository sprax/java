package sprax.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sprax.files.FileUtil;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Find the longest words in a given list of words that can be constructed
 * from a given list of letters. Your solution should take as its first
 * argument the name of a plain text file that contains one word per line.
 * The remaining arguments define the list of legal letters. A letter may not
 * appear in any single word more times than it appears in the list of letters
 * (e.g., the input letters �a a b c k� can make �back� and �cab� but not �abba�).
 * 
 * <pre>
 * Here's an example of how it should work:
 * 
 * {@code
 * prompt> word-maker WORD.LST w g d a s x z c y t e i o b
 * ['azotised', 'bawdiest', 'dystocia', 'geotaxis', 'iceboats', 'oxidates', 'oxyacids', 'sweatbox', 'tideways'] 
 * }
 * </pre>
 * 
 * Note: Just return the longest words which match, not all.
 */
public class LongestWordsFromLettersFilter implements StringFilter
{
    /**
     * count of each letter in the domain set of contiguous letters
     * (length 26 if only lower-case English alphabet letters are allowed with offset = (int)'a')
     */
    final int         letterCounts[];
    /** if letter is in letterCounts, it's count must be at index = letter - firstLetterOffset */
    final int         firstLetterOffset;
    int               totalLetterCount;
    int               longestWordLen;
    ArrayList<String> longestWords;
    
    LongestWordsFromLettersFilter(int letterCounts[], int firstLetterOffset)
    {
        if (letterCounts == null)
            throw new IllegalArgumentException("null");
        
        this.letterCounts = letterCounts;
        this.firstLetterOffset = firstLetterOffset;
        for (int count : letterCounts) {
            totalLetterCount += count;
        }
        longestWords = new ArrayList<>();
    }
    
    @Override
    public boolean filterString(String word)
    {
        int len = word.length();
        if (len < longestWordLen)
            return true;
        if (len > totalLetterCount)
            return true;
        int tempLetterCounts[] = Arrays.copyOf(letterCounts, letterCounts.length);
        for (int j = 0; j < len; j++) {
            int letterIndex = word.charAt(j) - firstLetterOffset;
            if (--tempLetterCounts[letterIndex] < 0)
                return true;
        }
        // At this point, we know len >= longestWordLen
        if (longestWordLen < len) {
            longestWordLen = len;
            longestWords.clear();
        }
        longestWords.add(word);
        return true;
    }
    
    static int test_example_letters()
    {
        int numWrong = 0;
        
        String fileName = "words.txt";
        String filePath = FileUtil.getTextFilePath(fileName);

        char letters[] = { 'w', 'g', 'd', 'a', 's', 'x', 'z', 'c', 'y', 't', 'e', 'i', 'o', 'b' };
        
        int letterCounts[] = new int[26];
        int offset = 'a';
        for (char ch : letters) {
            letterCounts[ch - offset]++;
        }
        
        LongestWordsFromLettersFilter filter = new LongestWordsFromLettersFilter(letterCounts, offset);
        FileLineFilter.filterFile(filePath, filter);
        ArrayList<String> result = filter.longestWords;
        Sx.putsList(filter.longestWords);
        
        String expect[] = { "azotised", "bawdiest", "dystocia", "geotaxis",
                "iceboats", "oxidates", "oxyacids", "sweatbox", "tideways" };
        
        numWrong += Sz.compareListAndArray(result, expect);
        
        return numWrong;
    }
    

    
    public static int unit_test()
    {
        String testName = LongestWordsFromLettersFilter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_example_letters();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
