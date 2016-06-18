package sprax.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import sprax.files.FileUtil;
import sprax.sprout.Sx;

/**
 * Find the longest words in a given list of words that can be constructed 
 * from a given list of letters.  Your solution should take as its first 
 * argument the name of a plain text file that contains one word per line. 
 * The remaining arguments define the list of legal letters. A letter may not
 * appear in any single word more times than it appears in the list of letters
 * (e.g., the input letters ‘a a b c k’ can make ‘back’ and ‘cab’ but not ‘abba’). 
 * <pre>
 * Here's an example of how it should work:
 * 
 * {@code
 * prompt> word-maker WORD.LST w g d a s x z c y t e i o b
 * ['azotised', 'bawdiest', 'dystocia', 'geotaxis', 'iceboats', 'oxidates', 'oxyacids', 'sweatbox', 'tideways'] 
 * } </pre>
 * Note: Just return the longest words which match, not all.
 */
public class LongestWordsFromLetters
{
    final String wordFile;
    /** count of each letter in the domain set of contiguous letters 
     * (length 26 if only lower-case English alphabet letters are allowed with offset = (int)'a') 
     */
    final int letterCounts[];
    /** if letter is in letterCounts, it's count must be at index = letter - firstLetterOffset */
    final int firstLetterOffset;    
    int totalLetterCount;
    int longestWordLen;
    ArrayList<String> longestWords;
    
    LongestWordsFromLetters(String inputFile, int letterCounts[], int firstLetterOffset)
    {
        if (inputFile == null || letterCounts == null)
            throw new IllegalAccessError("null");
        
        this.wordFile = inputFile;
        this.letterCounts = letterCounts;
        this.firstLetterOffset = firstLetterOffset;
        for (int count : letterCounts) {
            totalLetterCount += count;
        }
        longestWords = new ArrayList<>();
        System.out.format("Reading inputFile: %s\n", inputFile);
        filterFile(inputFile);
    }
    
    void filterLine(String line)
    {
        int len = line.length();
        if (len < longestWordLen)
            return;
        if (len > totalLetterCount)
            return;
        int tempLetterCounts[] = Arrays.copyOf(letterCounts, letterCounts.length);
        for (int j = 0; j < len; j++) {
            int letterIndex = line.charAt(j) - firstLetterOffset;
            if (--tempLetterCounts[letterIndex] < 0)
                return;
        }
        // At this point, we know len >= longestWordLen
        if (longestWordLen < len) {
            longestWordLen = len;
            longestWords.clear();
        }
        longestWords.add(line);
    }
    
    int filterFile(String path)
    {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            while((line = br.readLine()) != null) {
                filterLine(line);
            }
        } catch (IOException iox) {
            Sx.puts("Exception reading " + path + ":\n" + iox);
            return -1;
        } catch (Exception ex) {
            Sx.puts("Exception filtering " + path + ":\n" + ex);
            return -2;
        }
        return 0;
    }
    
    public static void main(String[] args)
    {
        char letters[];
        String fileName, filePath;
        if (args.length > 0) {
            filePath = args[0];
        } else {
            fileName = "words.txt";
            filePath = FileUtil.getTextFilePath(fileName);            
        }
        if (args.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int j = 1; j < args.length; j++)
                sb.append(args[j]);
            letters = sb.toString().toCharArray();
        } else {
            char defaultLets[] = { 'w', 'g', 'd', 'a', 's', 'x', 'z', 'c', 'y', 't', 'e', 'i', 'o', 'b' };
            letters = defaultLets;
        }
        
        int letterCounts[] = new int[26];
        int offset = 'a';
        for (char ch : letters) {
            letterCounts[ch - offset]++;
        }
        
        LongestWordsFromLetters filter = new LongestWordsFromLetters(filePath, letterCounts, offset);
        Sx.putsList(filter.longestWords);
    }
    
}
