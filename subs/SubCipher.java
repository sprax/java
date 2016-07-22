package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    EnTextCounter cipherCounter;
    EnTextCounter corpusCounter;
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
        cipherCounter = new EnTextCounter(cipherFilePath);
        corpusCounter = new EnTextCounter(corpusFilePath);
        forwardCipher = new char[EnTextCounter.ALPHABET_SIZE];
        inverseCipher = new char[EnTextCounter.ALPHABET_SIZE];
    }
    
    
    /** 
     * Use expected capitalization of the English first person pronoun I to find letter i.
     * That is, look for the encoded single-letter word most likely to encode "I".
     * Heuristic: Expect this pronoun to be capitalized.
     * Exceptions: Roman number 1=i, chat-speak, hyphenation across lines.
     */
    boolean findCipher_I()
    {
        char maxRatioChar = 0;
        double ratio, maxRatio = 0.0;
        for (String wd : cipherCounter.sizedWords.get(1)) {
            char chr = wd.charAt(0);
            if (EnTextCounter.isAsciiUpperCaseLetter(chr)) {
                char lwr = Character.toLowerCase(chr);
                int upperCount = cipherCounter.wordCounts.get(wd);
                int lowerCount = cipherCounter.wordCounts.getOrDefault(wd, 1);
                ratio = (float)upperCount / lowerCount;
                if (maxRatio < ratio) {
                    maxRatio = ratio;
                    maxRatioChar = lwr;
                }
            }
        }
        if (maxRatioChar != 0) {
            assignCipher('i', maxRatioChar);
            return true;
        }
        return false;
    }

    /** 
     * Use heuristics to find 'a' as the English indefinite article "a".
     * Heuristic: Expect this word to be very common and often not capitalized.
     * Confounders: Sentence beginnings, A used to label outlines or choices.
     * Tactics: Find the most common single letter word other than I (so find I first).
     */
    boolean findCipher_a()
    {
        ArrayList<String> singleLetterWords = cipherCounter.sizedWords.get(1);
        int count, maxCount = 0;
        char maxCharA = 0;
        for (String word : singleLetterWords) {
            count = cipherCounter.wordCounts.get(word);
            if (maxCount < count) {
                char chr = word.charAt(0);
                if (EnTextCounter.isAsciiUpperCaseLetter(chr))
                    chr = Character.toLowerCase(chr);
                if (chr != forwardCipher['i' - 'a']) {
                    maxCount = count;
                    maxCharA = chr;
                }
            }
        }
        if (maxCharA != 0) {
            assignCipher('a', maxCharA);
            return true;
        }
        return false;
    }

    boolean findCipher_the()
    {
        // Look for "the"
        ArrayList<String> threeLetterWords = cipherCounter.sizedWords.get(2);
        Collections.sort(threeLetterWords);

        // Confirm frequency of 'e'
        
        return false;
    }

    boolean findCipher_and()    {
        return false;
    }

    boolean findCipher_for()    // use "of" with "to", then "for"
    {
        return false;
    }

    void inferCipher()
    {
        findCipher_I();
        findCipher_a();
        findCipher_the();   // use wordCount("the") and letterCount('e')
        findCipher_and();   // a + and : d, n
        findCipher_for();   // of, for, to : o, r
        
        
        //// FIXME: cheating to test...
        assignCipher('c', 'a');
        assignCipher('u', 'b');
        assignCipher('x', 'e');
        assignCipher('b', 'g');
        assignCipher('p', 'i');
        assignCipher('n', 'j');
        assignCipher('l', 'k');
        assignCipher('e', 'l');
        assignCipher('h', 'm');
        assignCipher('o', 'n');
        assignCipher('k', 'o');
        assignCipher('g', 'p');
        assignCipher('s', 'r');
        assignCipher('d', 's');
        assignCipher('t', 'u');
        assignCipher('v', 'v');     // vow
        assignCipher('r', 'w');     // was, who, now, how
        assignCipher('f', 'x');     // six
        assignCipher('y', 'y');     // you, say, any, may
        assignCipher('w', 'z');     // was, who, now, how
        
        guessUnknownInverseCiphersFromCharCounts();
    }
    
    void guessUnknownInverseCiphersFromCharCounts()
    {
        for (int j = 0, k = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            char cipherChr = cipherCounter.charCounts[j].chr;
            if (inverseCipher[cipherChr - 'a'] == 0) {
                do {
                    char corpusChr = corpusCounter.charCounts[k].chr;
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
            char corpusChar = corpusCounter.charCounts[j].chr;
            char cipherChar = cipherCounter.charCounts[j].chr;
            forwardCipher[corpusChar - 'a'] = cipherChar;
            inverseCipher[cipherChar - 'a'] = corpusChar;
        }
    }
    
    void assignCipher(char corpusChar, char cipherChar)
    {
        assert(forwardCipher[corpusChar - 'a'] == 0) : "Already assigned: " + corpusChar;
        forwardCipher[corpusChar - 'a'] = cipherChar;
        inverseCipher[cipherChar - 'a'] = corpusChar;        
    }

    void decodeCipherText()
    {
        Sx.format("Encoded/Decoded cipher text (%s)\n\n", cipherFilePath);
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
            Sx.puts(line);
        }
    }
    
    public void showForwardCipher() { showCipherColumns(forwardCipher); }
    public void showInverseCipher() { showCipherColumns(inverseCipher); }
    
    public void showCipherColumns(char cipher[])
    {
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            Sx.format("%c -> %c\n", (char)(j + 'a'), cipher[j]);
        }
    }
    
    public void showCipherRows(char cipher[]) {
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            Sx.format("%c ", (char)(j + 'a'));
        }
        Sx.puts();
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) {
            Sx.format("%c ", cipher[j]);
        }
        Sx.puts();
    }
    

    public static int unit_test(int level) {
        String testName = SubCipher.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        SubCipher sc = new SubCipher();
        sc.corpusCounter.showCounts("\n     CORPUS: ", 1);
        sc.cipherCounter.showCounts("\n     CIPHER: ", 2);
        sc.inferCipher();
        sc.showCipherRows(sc.forwardCipher);
        //sc.showForwardCipher();
        sc.decodeCipherText();
        
        
        Sx.puts();
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
}
