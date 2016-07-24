package sprax.subs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import sprax.files.FileUtil;
import sprax.files.HashMapStringCollector;
import sprax.files.TextFileReader;
import sprax.files.TextFilters;
import sprax.sprout.Sx;
import sprax.test.Sz;

/*
 * TODO: Queue common words regardless of length
 */
public class SubCipher
{
    String        cipherFilePath;
    String        corpusFilePath;
    ArrayList<String> cipherFileLines;
    EnTextCounter cipherCounter;
    EnTextCounter corpusCounter;
    char forwardTable[];
    char inverseTable[];
    DescCountAscUnknownComp countAndUnMappedComp;
    
    // TODO: replace these with something like: boolean threeLetterWordIndexAssigned[]
    /** index of the encoded "the" in the sorted array of 3-letter ciphers */
    int threeLetterWordIndex_the = -1; 
    /** index of the encoded "and" in the sorted array of 3-letter ciphers */
    int threeLetterWordIndex_and = -1; 
    
    public SubCipher(String cipherFilePath, String corpusFilePath)
    {
        this.cipherFilePath = cipherFilePath;
        this.corpusFilePath = corpusFilePath;
        cipherFileLines = TextFileReader.readFileIntoArrayList(cipherFilePath);
        cipherCounter = new EnTextCounter(cipherFilePath);
        corpusCounter = new EnTextCounter(corpusFilePath);
        forwardTable = new char[EnTextCounter.ALPHABET_SIZE];
        inverseTable = new char[EnTextCounter.ALPHABET_SIZE];
        countAndUnMappedComp = new DescCountAscUnknownComp(corpusCounter.wordCounts, forwardTable);
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
                if (chr != forwardTable['i' - 'a']) {
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
            if (finalLetter == maxCountLetter && firstLetter != forwardTable['i' - 'a'])
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
        List<String> threeLetterWords = cipherCounter.sizedWords.get(3);
        for (int j = 0; j < threeLetterWords.size(); j++) {
            if (j != threeLetterWordIndex_the) {
                String cipher = threeLetterWords.get(j);

                char firstLetter = cipher.charAt(0);
                if (firstLetter == forwardTable['a' - 'a']) {
                    assignCipher('n', cipher.charAt(1));
                    assignCipher('d', cipher.charAt(2));
                    threeLetterWordIndex_and = j;
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * For each 2-letter corpus word with a reasonable count and
     * one of its letter's cipher known already, try to find its
     * cipher in the cipher text.  The trick is to order them
     * efficiently, versus just looping over the remaining 
     * unknowns several times (as in, until none are found
     * in a complete cycle (like the draw cards in solitaire).
     * Here we use a priority queue to order words as:
     *     higher counts and fewer unknowns first.
     * Since we're only assigning ciphers once, not voting on multiple
     * choices, a word must have at least one unknown to be considered.
     * If the next word in the queue has had its unknown letters 
     * discovered by matching a word earlier in the queue, it is discarded.
     *   
     * @param minWordFreqInCorpus Don't try to match cipher words
     *          against corpus words rarer than this threshold.
     */
    void findCiphersForTwoLetterWords(double minWordFreqInCorpus)
    {
        // twoLetterWords will have already been sorted by descending counts
        List<String> corpusWords2 = corpusCounter.sizedWords.get(2);
        List<String> cipherWords2 = cipherCounter.sizedWords.get(2);
        
        PriorityQueue<String> wordQueue = new PriorityQueue<>(countAndUnMappedComp);

        // Enqueue frequent two-letter words that contain at least one unknown letter
        int minCount = (int)(minWordFreqInCorpus * corpusCounter.totalWordCount);
        for (String word : corpusWords2) {
            int count = corpusCounter.wordCounts.get(word);
            if (count < minCount)
                break;
            char corp0 = word.charAt(0);
            char corp1 = word.charAt(1);
            char ciph0 = forwardTable[corp0 - 'a'];
            char ciph1 = forwardTable[corp1 - 'a'];
            if (ciph0 == 0 || ciph1 == 0) {
                wordQueue.add(word);
            }
        }
        
        while (!wordQueue.isEmpty()) {
            String word = wordQueue.remove();
            char corp0 = word.charAt(0);
            char corp1 = word.charAt(1);
            char ciph0 = forwardTable[corp0 - 'a'];
            char ciph1 = forwardTable[corp1 - 'a'];
            if (ciph0 != 0 && ciph1 != 0) {
                continue;                       // no more unknowns here
            }
            if (ciph0 != 0) {                   // corp0 is mapped to ciph0, but ciph1 is unknown
                for (String ciph : cipherWords2) {                    
                    if (ciph0 == ciph.charAt(0)) {
                        ciph1 = ciph.charAt(1);
                        Sx.debug(2, "findCiphersForTwoLetterWords trying %c -> %c from %s <> %s\n"
                                , corp1, ciph1, word, ciph);
                        if (inverseTable[ciph1 - 'a'] == 0) {
                            assignCipher(corp1, ciph1);
                            break;
                        }
                        else {
                            Sx.debug(2, "Rejecting %c -> %c because already %c -> %c\n"
                                    , corp1, ciph1, inverseTable[ciph1 - 'a'], ciph1);
                        }
                    }
                }
            } 
            else if (ciph1 != 0) {              // corp1 -> ciph1, but corp0 -> ?
                for (String ciph : cipherWords2) {
                    if (ciph1 == ciph.charAt(1)) {
                        ciph0 = ciph.charAt(0);
                        Sx.debug(2, "findCiphersForTwoLetterWords trying %c -> %c from %s <> %s\n"
                                , corp0, ciph0, word, ciph);
                        if (inverseTable[ciph0 - 'a'] == 0) {
                            assignCipher(corp0, ciph0);
                            break;
                        }
                        else {
                            Sx.debug(2, "Rejecting %c -> %c because already %c -> %c\n"
                                    , corp0, ciph0, inverseTable[ciph0 - 'a'], ciph0);
                        }
                    }
                }
            } 
            else {
                dumpQueue(wordQueue, "findCiphersForTwoLetterWords dumping queue:");
                break; // all words left in the queue have 2 unknown chars, so give up
            }
        }
    }
    
    /** 
     * Dumping the queue this way destroys it.  To preserve iut,
     * pass in a new copy of the queue to be dumped.
     * @param pq
     */
    void dumpQueue(PriorityQueue<String> pq, String reason)
    {
        Sx.puts(reason);
        int idx = 0;
        while (! pq.isEmpty()) {
            String word = pq.remove();
            Sx.format("Queue %2d:  %s  %d  %d\n", idx, word
                    , numUnMappedCorpusChars(word), corpusCounter.wordCounts.get(word));
            idx++;
        }
    }
    
    /**
     * For each N-letter corpus word with a reasonable count and
     * one of its letter's cipher known already, try to find its
     * cipher in the cipher text.  The trick is to order them
     * efficiently, versus just looping over the remaining 
     * unknowns several times (as in, until none are found
     * in a complete cycle (like the draw cards in solitaire).
     * Here we use a priority queue to order words as:
     *     higher counts and fewer unknowns first.
     * Since we're only assigning ciphers once, not voting on multiple
     * choices, a word must have at least one unknown to be considered.
     * If the next word in the queue has had its unknown letters 
     * discovered by matching a word earlier in the queue, it is discarded.
     *   
     * @param minWordFreqInCorpus Don't try to match cipher words
     *          against corpus words rarer than this threshold.
     */
    void findCiphersForFixedLengthWords(int wordLen, double minWordFreqInCorpus)
    {
        if (wordLen < 0 || wordLen > EnTextCounter.MAX_SIZED_LEN)
            throw new IllegalArgumentException("bad word size: " + wordLen);
        
        // These lists will have already been sorted by descending counts
        List<String> corpusWords = corpusCounter.sizedWords.get(wordLen);
        List<String> cipherWords = cipherCounter.sizedWords.get(wordLen);
        
        PriorityQueue<String> wordQueue = new PriorityQueue<>(countAndUnMappedComp);

        // Enqueue frequent two-letter words that contain at least one unknown letter
        int minCount = (int)(minWordFreqInCorpus * corpusCounter.totalWordCount);
        for (String word : corpusWords) {
            int count = corpusCounter.wordCounts.get(word);
            if (count < minCount)
                break;
            int numUnMapped = numUnMappedCorpusChars(word);
            if (numUnMapped > 0)
            {
                wordQueue.add(word);
            }
        }
        
        while (!wordQueue.isEmpty()) {
            String word = wordQueue.remove();
            int idxUnMapped = -1; 
            for (int j = 0; j < wordLen; j++) {
                char corpj = word.charAt(j);
                char ciphj = forwardTable[corpj - 'a'];
                if (ciphj == 0) {
                    if (idxUnMapped == -1) {
                        idxUnMapped = j;                 // index was not set, so set it
                    }
                    else {
                        idxUnMapped = Integer.MIN_VALUE; // index was already set: too many unknowns!
                        break;
                    }
                }
            }
            if (idxUnMapped >= 0) {                      // index was set just once
                char corpUnMappedChar = word.charAt(idxUnMapped);
                for (String ciph : cipherWords) {
                    // if this cipher word matches all the other, num-1 known letters of word,
                    // then guess that the one unmapped letter in word should map to ciph[idxUnknown]
                    int numMatchedChars = 0;
                    for (int j = 0; j < wordLen; j++) {
                        if (j == idxUnMapped)
                            continue;
                        if (ciph.charAt(j) != forwardTable[word.charAt(j) - 'a'])
                            break;
                        numMatchedChars++;
                    }
                    if (numMatchedChars == wordLen - 1) {
                        char ciphCharAtIdx = ciph.charAt(idxUnMapped);
                        // Check if this char in the cipher word is already mapped:
                        Sx.debug(2, "findCiphersForWordsOfFixedLength trying %c -> %c from %s <> %s\n"
                                , corpUnMappedChar, ciphCharAtIdx, word, ciph);
                        if (inverseTable[ciphCharAtIdx - 'a'] == 0) {
                            Sx.debug(1, "Accepting %c -> %c\n"
                                    , corpUnMappedChar, ciphCharAtIdx
                                    , inverseTable[ciphCharAtIdx - 'a'], ciphCharAtIdx);
                            assignCipher(corpUnMappedChar, ciphCharAtIdx);
                            break;
                        }
                        else {
                            Sx.debug(2, "Rejecting %c -> %c because already %c -> %c\n"
                                    , corpUnMappedChar, ciphCharAtIdx
                                    , inverseTable[ciphCharAtIdx - 'a'], ciphCharAtIdx);
                        }
                    }
                }
            } 
            else if (idxUnMapped == Integer.MIN_VALUE) {
                dumpQueue(wordQueue, "findCiphersForFixedLengthWords dumping queue: " + wordLen);
                break; // all words left in the queue have at least 2 unknown chars, so give up
            }
        }
    }
    
    void findCiphersForFrequentCorpusWords(double minWordFreqInCorpus, int numWords) // FIXME: choose one?
    {       
        int minCount = (int)(minWordFreqInCorpus * corpusCounter.totalWordCount);

        PriorityQueue<String> wordQueue = new PriorityQueue<>(countAndUnMappedComp);

        // Enqueue frequent two-letter words that contain at least one unknown letter
        for (Map.Entry<String, Integer> entry : corpusCounter.wordCounts.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            if (count >= minCount && word.length() > 2) 
                wordQueue.add(word);
        }
        
        while (!wordQueue.isEmpty()) {
            String word = wordQueue.remove();
            int wordLen = word.length();
            int idxUnMapped = -1; 
            for (int j = 0; j < wordLen; j++) {
                char corpj = word.charAt(j);
                char ciphj = forwardTable[corpj - 'a'];
                if (ciphj == 0) {
                    if (idxUnMapped == -1) {
                        idxUnMapped = j;                 // index was not set, so set it
                    }
                    else {
                        idxUnMapped = Integer.MIN_VALUE; // index was already set: too many unknowns!
                        break;
                    }
                }
            }
            if (idxUnMapped >= 0) {                      // index was set just once
                char corpUnMappedChar = word.charAt(idxUnMapped);
                for (String ciph : cipherCounter.sizedWords.get(wordLen)) {
                    // if this cipher word matches all the other, num-1 known letters of word,
                    // then guess that the one unmapped letter in word should map to ciph[idxUnknown]
                    int numMatchedChars = 0;
                    for (int j = 0; j < wordLen; j++) {
                        if (j == idxUnMapped)
                            continue;
                        if (ciph.charAt(j) != forwardTable[word.charAt(j) - 'a'])
                            break;
                        numMatchedChars++;
                    }
                    if (numMatchedChars == wordLen - 1) {
                        char ciphCharAtIdx = ciph.charAt(idxUnMapped);
                        // Check if this char in the cipher word is already mapped:
                        Sx.debug(2, "findCiphersForWordsOfFixedLength trying %c -> %c from %s <> %s\n"
                                , corpUnMappedChar, ciphCharAtIdx, word, ciph);
                        if (inverseTable[ciphCharAtIdx - 'a'] == 0) {
                            Sx.debug(1, "Accepting %c -> %c\n"
                                    , corpUnMappedChar, ciphCharAtIdx
                                    , inverseTable[ciphCharAtIdx - 'a'], ciphCharAtIdx);
                            assignCipher(corpUnMappedChar, ciphCharAtIdx);
                            break;
                        }
                        else {
                            Sx.debug(2, "Rejecting %c -> %c because already %c -> %c\n"
                                    , corpUnMappedChar, ciphCharAtIdx
                                    , inverseTable[ciphCharAtIdx - 'a'], ciphCharAtIdx);
                        }
                    }
                }
            } 
            else if (idxUnMapped == Integer.MIN_VALUE) {
                dumpQueue(wordQueue, "findCiphersForFixedLengthWords dumping queue: " + wordLen);
                break; // all words left in the queue have at least 2 unknown chars, so give up
            }
        }
    }
    

    void inferCipher()
    {
        findCipher_I();
        findCipher_a();
        findCipher_the();   // use wordCount("the") and letterCount('e')
        findCipher_and();   // a + and : d, n
        findCiphersForTwoLetterWords(0.006);
        
        double minWordFreqInCorpus = 0.005;
        findCiphersForFrequentCorpusWords(minWordFreqInCorpus, 100);
        /*
        //findCiphersForFixedLengthWords(2, 0.006);
        findCiphersForFixedLengthWords(3, 0.00222);
        findCiphersForFixedLengthWords(4, 0.005);
        
        findCiphersForFixedLengthWords(5, 0.00044);
        findCiphersForFixedLengthWords(6, 0.00033);
        findCiphersForFixedLengthWords(7, 0.00022);
        findCiphersForFixedLengthWords(8, 0.00015);
        findCiphersForFixedLengthWords(9, 0.00011);
        */
        
        
        
        //// findCipher_numLetterWords(10, 0.000001);
        
        //// FIXME: cheating to test...
        /*///////////////////////////////////////////////////
        assignCipher('c', 'a');
        assignCipher('u', 'b');
        assignCipher('x', 'e');
        assignCipher('x', 'e');
        assignCipher('m', 'f');
        assignCipher('p', 'i');
        assignCipher('l', 'k');

        assignCipher('f', 'x');
        assignCipher('k', 'o');
        assignCipher('g', 'p');

        assignCipher('v', 'v');     // vow
        assignCipher('r', 'w');     // was, who, now, how
        assignCipher('y', 'y');     // you, say, any, may
        assignCipher('w', 'z');     // was, who, now, how
        /////////////////////////////////////////////////////*/
        
        int numCorpusUnknown = numMissingTableValues(forwardTable);
        int numCipherUnknown = numMissingTableValues(inverseTable);
        
        Sx.format("Number of unmapped chars: corpus %d  cipher %d\n"
                , numCorpusUnknown
                , numCipherUnknown);
        guessUnknownInverseCiphersFromCharCounts();
    }
    
    int numMissingTableValues(char table[]) {
        int numMissingValues = 0;
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) 
            if (table[j] == 0)
                numMissingValues++;
        return  numMissingValues;
    }
    
    void guessUnknownInverseCiphersFromCharCounts()
    {
        int ciphIdx = 0;
        for (int corpIdx = 0; ciphIdx < EnTextCounter.ALPHABET_SIZE; ciphIdx++) {
            char cipherChar = cipherCounter.charCounts[ciphIdx].chr;
            if (inverseTable[cipherChar - 'a'] == 0) {
                if (corpIdx >= EnTextCounter.ALPHABET_SIZE) {
                    String errorMessage = 
                            "ERROR: No unnasigned corpus chars for cipher " + cipherChar;
                    Sx.puts(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
                do {
                    char corpusChar = corpusCounter.charCounts[corpIdx++].chr;
                    if (forwardTable[corpusChar - 'a'] == 0) {
                        assignCipher(corpusChar, cipherChar);
                        break;
                    }
                } while (corpIdx < EnTextCounter.ALPHABET_SIZE);
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
            assignCipher(corpusChar, cipherChar);
        }
    }
    
    void assignCipher(char corpusChar, char cipherChar)
    {
        assert(forwardTable[corpusChar - 'a'] == 0) : "Already assigned corpus: " + corpusChar;
        assert(inverseTable[cipherChar - 'a'] == 0) : "Already assigned cipher: " + cipherChar;
        forwardTable[corpusChar - 'a'] = cipherChar;
        inverseTable[cipherChar - 'a'] = corpusChar;        
    }

    public void showForwardCipher() { showCipherColumns(forwardTable); }
    public void showInverseCipher() { showCipherColumns(inverseTable); }
    
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
    
    void decodeCipherText()
    {
        HashMapStringCollector fixme_3 = new HashMapStringCollector();
        Sx.format("Encoded/Decoded cipher text (%s)\n\n", cipherFilePath);
        for (String line : cipherFileLines) {
            char chrs[] = line.toCharArray();
            for (int j = 0; j < chrs.length; j++) {
                char chr = chrs[j];
                if (EnTextCounter.isAsciiLowerCaseLetter(chr)) {
                    chrs[j] = inverseTable[chr - 'a'];
                }
                else if (EnTextCounter.isAsciiUpperCaseLetter(chr)) {
                    chr = Character.toLowerCase(chr);
                    chrs[j] = Character.toUpperCase(inverseTable[chr - 'a']);                    
                }
            }
            String deciphered = new String(chrs);
            TextFilters.collectLowerCaseLetterWords(fixme_3, deciphered);
            Sx.puts(deciphered);
            Sx.puts(line);
        }
        //Sx.putsIterable(fixme_3.getCollector().entrySet(), 1, 10000);
    }
    

    public static int unit_test(int level) {
        String testName = SubCipher.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        SubCipher sc = new SubCipher(FileUtil.getTextFilePath("cipher.txt"),
            ////                         FileUtil.getTextFilePath("corpusEn300kWords.txt"));
            ////FileUtil.getTextFilePath("corpus-en.txt"));   
            FileUtil.getTextFilePath("corpusEn.txt"));   
                ////"src/sprax/subs/deciphered.txt");   
        
        sc.corpusCounter.showCounts("\n     CORPUS: ", 1);
        sc.cipherCounter.showCounts("\n     CIPHER: ", 3);
        sc.inferCipher();
        sc.showCipherRows(sc.forwardTable);
        sc.showForwardCipher();
        sc.decodeCipherText();
        
        
        Sx.puts();
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
   
    /**
     * Returns the total number of instances of corpus characters without
     * known cipher values.  Two instances of the same unmapped character
     * are counted as 2, not 1.
     */
    int numUnMappedCorpusChars(String word) {
        return (int) word.chars().filter(x -> forwardTable[x - 'a'] == 0).count();
    }
    
    
    class DescCountAscUnknownComp implements Comparator<String>
    {
        Map<String, Integer> wordCounts;
        char cipher[];
        
        DescCountAscUnknownComp(Map<String, Integer> wordCounts, char cipher[]) {
            this.wordCounts = wordCounts;
            this.cipher = forwardTable;
        }
        
        @Override
        public int compare(String wordA, String wordB) {
            
            int unmapA = numUnMappedCorpusChars(wordA);
            int unmapB = numUnMappedCorpusChars(wordB);
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
