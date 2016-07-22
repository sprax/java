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
    
    
    /** 
     * Use knowledge of English first person pronoun I to find letter i.
     * That is,
     * look for the encoded single-letter word most likely to encode "I".
     */
    boolean findForwardCipher_I()
    {
        char maxRatioChar = 0;
        double ratio, maxRatio = 0.0;
        for (String wd : cipherTextCounter.sizedWords.get(1)) {
            char chr = wd.charAt(0);
            if (EnTextCounter.isAsciiUpperCaseLetter(chr)) {
                char lwr = Character.toLowerCase(chr);
                int upperCount = cipherTextCounter.wordCounts.get(wd);
                int lowerCount = cipherTextCounter.wordCounts.getOrDefault(wd, 1);
                ratio = (float)upperCount / lowerCount;
                if (maxRatio < ratio) {
                    maxRatio = ratio;
                    maxRatioChar = lwr;
                }
            }
        }
        if (maxRatioChar != 0) {
            forwardCipher['i' - 'a'] = maxRatioChar;
            inverseCipher[maxRatioChar - 'a'] = 'i';
            return true;
        }
        return false;
    }

    void inferCipher()
    {
        findForwardCipher_I();
        
        //// FIXME: cheating to test...
        assignCipher('a', 'c');
        
        guessUnknownInverseCiphersFromCharCounts();
    }
    
    void guessUnknownInverseCiphersFromCharCounts()
    {
        for (int j = 0, k = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            char cipherChr = cipherTextCounter.charCounts[j].chr;
            if (inverseCipher[cipherChr - 'a'] == 0) {
                do {
                    char corpusChr = corpusTextCounter.charCounts[k].chr;
                    if (forwardCipher[corpusChr - 'a'] == 0) {
                        forwardCipher[corpusChr - 'a'] = cipherChr;
                        inverseCipher[cipherChr - 'a'] = corpusChr;
                        break;
                    }
                } while (++k < EnTextCounter.ALPHABET_SIZE);
                if (k >= EnTextCounter.ALPHABET_SIZE) {
                    Sx.format("WARNING: ran out of unnasigned corpus chars at %c\n", cipherChr);
                }
            }            
        }
    }
    
    void matchSingleLetterWords()
    {}
    
    void makeCharCountOnlyCiphers()
    {
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            char corpusChar = corpusTextCounter.charCounts[j].chr;
            char cipherChar = cipherTextCounter.charCounts[j].chr;
            forwardCipher[corpusChar - 'a'] = cipherChar;
            inverseCipher[cipherChar - 'a'] = corpusChar;
        }
    }
    
    void assignCipher(char corpusChar, char cipherChar)
    {
        forwardCipher[corpusChar - 'a'] = cipherChar;
        inverseCipher[cipherChar - 'a'] = corpusChar;        
    }

    void decipherText()
    {
        Sx.format("Deciphered cipher text (%s)\n", cipherFilePath);
        for (String line : cipherFileLines) {
            char chrs[] = line.toCharArray();
            for (int j = 0; j < chrs.length; j++) {
                char chr = chrs[j];
                if (EnTextCounter.isAsciiLowerCaseLetter(chr)) {
                    chrs[j] = inverseCipher[chr - 'a'];
                }
                else if (EnTextCounter.isAsciiUpperCaseLetter(chr)) {
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
        sc.cipherTextCounter.showCounts(1);
        sc.inferCipher();
        sc.showForwardCipher();
        sc.decipherText();
        
        
        Sx.puts();
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
}
