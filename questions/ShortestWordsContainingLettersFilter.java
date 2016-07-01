package sprax.questions;

import java.util.ArrayList;
import java.util.Arrays;

import sprax.files.FileUtil;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Find the shortest word(s) in a given list of words that contain all of
 * a given list of letters.  If a letter appears more than once in the list,
 * it must occur in any found word at least as many times as it appears in the list 
 * of letters (e.g., for the input letters ‘d e e f’, the words 'freed' and 'feed' 
 * do qualify, but 'fed' and 'fee' do not.)
 */
public class ShortestWordsContainingLettersFilter implements StringFilter
{
    /**
     * count of each letter in the domain set of contiguous letters
     * (length 26 if only lower-case English alphabet letters are allowed with offset = (int)'a')
     */
    final int         letterCounts[];
    /** if letter is in letterCounts, it's count must be at index = letter - firstLetterOffset */
    final int         firstLetterOffset;
    int               totalLetterCount;
    int               shortestWordLen;
    ArrayList<String> shortestWords;
    
    ShortestWordsContainingLettersFilter(int letterCounts[], int firstLetterOffset)
    {
        if (letterCounts == null)
            throw new IllegalArgumentException("null");
        
        this.letterCounts = letterCounts;
        this.firstLetterOffset = firstLetterOffset;
        for (int count : letterCounts) {
            totalLetterCount += count;
        }
        shortestWordLen = Integer.MAX_VALUE;
        shortestWords = new ArrayList<>();
    }
    
    @Override
    public boolean filterString(String word)
    {
        int len = word.length();
        if (len > shortestWordLen)
            return true;
        if (len < totalLetterCount)
            return true;
        
        char wordLetterArray[] = word.toCharArray();
        int wordLetterCounts[] = new int[letterCounts.length];
        for (char ch : wordLetterArray) {
            wordLetterCounts[ch - firstLetterOffset]++;
        }
        
        for (int j = 0; j < letterCounts.length; j++) {
            if (wordLetterCounts[j] < letterCounts[j])
                return true;
        }
        // At this point, we know len <= shortestWordLen
        if (shortestWordLen > len) {
            shortestWordLen = len;
            shortestWords.clear();
        }
        shortestWords.add(word);
        return true;
    }
    
    static int test_letters(char letters[], String expect[])
    {
        int numWrong = 0;
        
        String fileName = "words.txt";
        String filePath = FileUtil.getTextFilePath(fileName);

        
        int letterCounts[] = new int[26];
        int offset = 'a';
        for (char ch : letters) {
            letterCounts[ch - offset]++;
        }
        
        ShortestWordsContainingLettersFilter filter = new ShortestWordsContainingLettersFilter(letterCounts, offset);      
        
        Sx.putsArray("Searching " + filePath + " for words containing:", letters);
        FileLineFilter.filterFile(filePath, filter);
        ArrayList<String> result = filter.shortestWords;

        Sx.format("Found %d words of shortest length %d:\n", result.size(), filter.shortestWordLen);
        Sx.putsList(result);
              
        if (result.size() == expect.length) {
            for (int j = 0; j < expect.length; j++) {
                numWrong += Sz.oneIfFalse(expect[j].equals(result.get(j)));
            }
        } else {
            numWrong = 100;
        }
        
        return numWrong;
    }
    
    public static int unit_test()
    {
        String testName = ShortestWordsContainingLettersFilter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        char lettersA[] = { 'a', 'd', 'e', 'i', 'r', 's', 'w' };
        String expectA[] = { "bawdries", "dishware", "rawhides", "sideward", "tawdries" };
        numWrong += test_letters(lettersA, expectA);
        
        char lettersB[] = { 'x', 's', 'r', 'q', 'e' };
        String expectB[] = { "exchequers", "quixotries" };
        numWrong += test_letters(lettersB, expectB);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
