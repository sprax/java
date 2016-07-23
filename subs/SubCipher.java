package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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
    
    // TODO: replace these with something like: boolean threeLetterWordIndexAssigned[]
    /** index of the encoded "the" in the sorted array of 3-letter ciphers */
    int threeLetterWordIndex_the = -1; 
    /** index of the encoded "and" in the sorted array of 3-letter ciphers */
    int threeLetterWordIndex_and = -1; 
    
    
    SubCipher() 
    {
        this(FileUtil.getTextFilePath("cipher.txt"),
             FileUtil.getTextFilePath("corpusEn.txt"));
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
        // Look for "the": expect it to be most counted 3-letter word.
        // The 3-letter word list was already sorted by descending counts.
        // To make sure, check that its last letter (expected to be C('e'))
        // is also the most frequent letter in the encoded text (also 
        // expected to be C('e')), and check that the first letter is
        // not C('a'), which we may already know from findCipher_a().
        char maxCountLetter = cipherCounter.charCounts[0].chr;
        List<String> threeLetterWords = cipherCounter.sizedWords.get(3);
        for (int j = 0; j < threeLetterWords.size(); j++) {
            String cipher = threeLetterWords.get(j);
        
            // Is this word's last letter the same as the most counted, 
            // which we expect to be 'e'?
            char firstLetter = cipher.charAt(0);
            char finalLetter = cipher.charAt(2);
            if (finalLetter == maxCountLetter && firstLetter != forwardCipher['i' - 'a'])
            {
                assignCipher('t', firstLetter);
                assignCipher('h', cipher.charAt(1));
                assignCipher('e', finalLetter);
                threeLetterWordIndex_the = j;
                return true;
            }
        }
        return false;
    }
    
    boolean findCipher_and()
    {
        // Look for "and": expect it to be 2nd most counted 3-letter word.
        // The 3-letter word list was already sorted by descending counts.
        // To make sure, check that its first letter (expected to be C('a'))
        // matches what we (may have) already found using findCipher_a().
        char maxCountLetter = cipherCounter.charCounts[0].chr;
        List<String> threeLetterWords = cipherCounter.sizedWords.get(3);
        for (int j = 0; j < threeLetterWords.size(); j++) {
            if (j != threeLetterWordIndex_the) {
                String cipher = threeLetterWords.get(j);

                char firstLetter = cipher.charAt(0);
                if (firstLetter == forwardCipher['a' - 'a']) {
                    assignCipher('n', cipher.charAt(1));
                    assignCipher('d', cipher.charAt(2));
                    threeLetterWordIndex_and = j;
                    return true;
                }
            }
        }
        return false;
    }       
    
    boolean findCipher_his()    // use "of" with "to", then "for"
    {
        return false;
    }    
    
    boolean findCipher_for()    // use "of" with "to", then "for"
    {
        return false;
    }
    
    /*
     * For each 2-letter corpus word with a reasonable count and
     * one of its letter's cipher known already, try to find its
     * cipher in the cipher text.  The trick is to order them
     * efficiently, versus just looping over the remaining 
     * unknowns several times (as in, until none are found
     * in a complete cycle (like the draw cards in solitaire).
     */
    void findCipher_twoLetterWords()
    {
        // twoLetterWords will have already been sorted by descending counts
        List<String> twoLetterWords = corpusCounter.sizedWords.get(2);
        
        DescCountUnknownComp comp = new DescCountUnknownComp(corpusCounter.wordCounts, forwardCipher);
        PriorityQueue<String> twoQueue = new PriorityQueue<>(comp);
        twoQueue.addAll(twoLetterWords);
        while (! twoQueue.isEmpty()) {
            String wordy = twoQueue.remove();
            Sx.format("TWO QUEUE twoQueue: %s  %d  %d\n"
                    , wordy, corpusCounter.wordCounts.get(wordy), numUnknownChars(wordy));
        }
        
        for (String word : twoLetterWords) {
            char corp0 = word.charAt(0);
            char corp1 = word.charAt(1);
            char ciph0 = forwardCipher[corp0 - 'a'];
            char ciph1 = forwardCipher[corp1 - 'a'];
            if (ciph0 == 0 && ciph1 != 0) {
                
            } else
            if (ciph0 != 0 && ciph1 == 0) {
                    
            }
        }
        
    }
    

    void inferCipher()
    {
        findCipher_I();
        findCipher_a();
        findCipher_the();   // use wordCount("the") and letterCount('e')
        findCipher_and();   // a + and : d, n
        findCipher_twoLetterWords();   // of, for, to : o, r
        
        
        //// FIXME: cheating to test...
        assignCipher('c', 'a');
        assignCipher('u', 'b');
        assignCipher('x', 'e');
        assignCipher('b', 'g');
        assignCipher('p', 'i');

        assignCipher('l', 'k');


        assignCipher('o', 'n');
        assignCipher('k', 'o');
        assignCipher('g', 'p');
        assignCipher('s', 'r');


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
        sc.cipherCounter.showCounts("\n     CIPHER: ", 3);
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
    
   
    int numUnknownChars(String word) {
        return (int) word.chars().filter(x -> forwardCipher[x - 'a'] == 0).count();
    }
    
    
    class DescCountUnknownComp implements Comparator<String>
    {
        Map<String, Integer> wordCounts;
        char forwardCipher[];
        
        DescCountUnknownComp(Map<String, Integer> wordCounts, char forwardCipher[]) {
            this.wordCounts = wordCounts;
            this.forwardCipher = forwardCipher;
        }
        
        @Override
        public int compare(String wordA, String wordB) {
            
            int unmapA = numUnknownChars(wordA);
            int unmapB = numUnknownChars(wordB);
            if (unmapA != unmapB)
                return unmapA - unmapB;
            int countA = wordCounts.getOrDefault(wordA, 0);
            int countB = wordCounts.getOrDefault(wordB, 0);
            if (countA != countB)
                return Integer.compare(countB, countA); // Descending counts
            return wordA.compareTo(wordB);
        }
    }


    
    
}
