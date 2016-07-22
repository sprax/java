package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class SubCipher
{
    String        cipherFilePath;
    String        corpusFilePath;
    ArrayList<String> cipherFileLines;
    EnTextCounter cipherTextCounter;
    EnTextCounter corpusTextCounter;
    char forwardCipher[];
    char inverseCipher[];
    
    SubCipher() 
    {
        this(FileUtil.getTextFilePath("cipher.txt"),
             FileUtil.getTextFilePath("corpus.txt"));
    }
    
    public SubCipher(String cipherFilePath, String corpusFilePath)
    {
        this.cipherFilePath = cipherFilePath;
        this.corpusFilePath = corpusFilePath;
        cipherFileLines = TextFileReader.readFileIntoArrayList(cipherFilePath);
        cipherTextCounter = new EnTextCounter(cipherFilePath);
        corpusTextCounter = new EnTextCounter(corpusFilePath);
        forwardCipher = new char[EnTextCounter.ALPHABET_SIZE];
        inverseCipher = new char[EnTextCounter.ALPHABET_SIZE];
    }
    
    void makeCharCountCiphers()
    {
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            char corpusChr = corpusTextCounter.charCounts[j].chr;
            char cipherChr = cipherTextCounter.charCounts[j].chr;
            forwardCipher[corpusChr - 'a'] = cipherChr;
            inverseCipher[cipherChr - 'a'] = corpusChr;
        }
    }


    void decipherText()
    {
        Sx.format("Deciphered cipher text (%s)\n", cipherFilePath);
        for (String line : cipherFileLines) {
            char chrs[] = line.toCharArray();
            for (int j = 0; j < chrs.length; j++) {
                char chr = chrs[j];
                if (isAsciiLowerCaseLetter(chr)) {
                    chrs[j] = inverseCipher[chr - 'a'];
                }
                else if (isAsciiUpperCaseLetter(chr)) {
                    chr = Character.toLowerCase(chr);
                    chrs[j] = Character.toUpperCase(inverseCipher[chr - 'a']);                    
                }
            }
            String deciphered = new String(chrs);
            Sx.puts(deciphered);
        }
    }
    
    public void showForwardCipher() { showCipher(forwardCipher); }
    public void showInverseCipher() { showCipher(inverseCipher); }
    
    public void showCipher(char cipher[])
    {
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            Sx.format("%c -> %c\n", (char)(j + 'a'), cipher[j]);
        }
    }
    
    public static int unit_test(int level) {
        String testName = SubCipher.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        SubCipher sc = new SubCipher();
        sc.makeCharCountCiphers();
        sc.showForwardCipher();
        sc.decipherText();
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
}
