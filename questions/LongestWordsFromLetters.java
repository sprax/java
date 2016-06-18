package sprax.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import sprax.sprout.Sx;

public class LongestWordsFromLetters
{
    final String wordFile;
    final int letterCounts[];
    int totalLetterCount;
    int longestWordLen;
    ArrayList<String> longestWords;
    
    LongestWordsFromLetters(String inputFile, int letterCounts[])
    {
        if (inputFile == null || letterCounts == null)
            throw new IllegalAccessError("null");
        
        this.wordFile = inputFile;
        this.letterCounts = letterCounts;
        System.out.format("Reading inputFile: %s\n", inputFile);
        filterFile(inputFile);
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
    
    void filterLine(String line) {
        
    }
    



    
    public static void main(String[] args) throws IOException
    {
        String fileSpec = "src/sprax/questions/StoreCredit.txt";
        if (args.length > 0)
            fileSpec = args[0];
        int letterCounts[] = new int[0];
        LongestWordsFromLetters filter = new LongestWordsFromLetters(fileSpec, letterCounts);
    }
    
}
