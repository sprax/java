package sprax.subs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    AscendingUnMappedCharsDescendingWordCountsComp unMappedCorpusWordComp;
    AscendingUnknownCharsDescendingCipherWordCountsComp unknownCipherWordComp;
    
    // TODO: replace these with something like: boolean threeLetterWordIndexAssigned[]
    /** index of the encoded "the" in the sorted array of 3-letter ciphers */
    int threeLetterWordIndex_the = -1; 
    /** index of the encoded "and" in the sorted array of 3-letter ciphers */
    int threeLetterWordIndex_and = -1; 
    
    public SubCipher(String cipherFilePath, String corpusFilePath)
    {
        this.cipherFilePath = cipherFilePath;
        this.corpusFilePath = corpusFilePath;
        this.cipherFileLines = TextFileReader.readFileIntoArrayList(cipherFilePath);
        this.cipherCounter = new EnTextCounter(cipherFilePath);
        this.corpusCounter = new EnTextCounter(corpusFilePath);
        this.forwardTable = new char[EnTextCounter.ALPHABET_SIZE];
        this.inverseTable = new char[EnTextCounter.ALPHABET_SIZE];
        this.unMappedCorpusWordComp = new AscendingUnMappedCharsDescendingWordCountsComp();
    }
    
    void matchSingleLetterWords()
    {
        
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
    void findCiphersFromTwoLetterCorpusWords(double minWordFreqInCorpus)
    {
        // twoLetterWords will have already been sorted by descending counts
        List<String> corpusWords2 = corpusCounter.sizedWords.get(2);
        List<String> cipherWords2 = cipherCounter.sizedWords.get(2);
        
        PriorityQueue<String> wordQueue = new PriorityQueue<>(unMappedCorpusWordComp);

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
                        Sx.format("findCiphersFromTwoLetterCorpusWords trying %c -> %c from %s <> %s\n"
                                , corp1, ciph1, word, ciph);
                        if (inverseTable[ciph1 - 'a'] == 0) {
                            assignCipher(corp1, ciph1);
                            break;
                        }
                        else {
                            Sx.format("Rejecting %c -> %c because already %c -> %c\n"
                                    , corp1, ciph1, inverseTable[ciph1 - 'a'], ciph1);
                        }
                    }
                }
            } 
            else if (ciph1 != 0) {              // corp1 -> ciph1, but corp0 -> ?
                for (String ciph : cipherWords2) {
                    if (ciph1 == ciph.charAt(1)) {
                        ciph0 = ciph.charAt(0);
                        Sx.format("findCiphersFromTwoLetterCorpusWords trying %c -> %c from %s <> %s\n"
                                , corp0, ciph0, word, ciph);
                        if (inverseTable[ciph0 - 'a'] == 0) {
                            assignCipher(corp0, ciph0);
                            break;
                        }
                        else {
                            Sx.format("Rejecting %c -> %c because already %c -> %c\n"
                                    , corp0, ciph0, inverseTable[ciph0 - 'a'], ciph0);
                        }
                    }
                }
            } 
            else {
                dumpQueue(wordQueue, "findCiphersFromTwoLetterCorpusWords dumping queue:");
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
    void findCiphersFromFixedLengthCorpusWords(int wordLen, double minWordFreqInCorpus)
    {
        if (wordLen < 0 || wordLen > EnTextCounter.MAX_SIZED_LEN)
            throw new IllegalArgumentException("bad word size: " + wordLen);
        
        // These lists will have already been sorted by descending counts
        List<String> corpusWords = corpusCounter.sizedWords.get(wordLen);
        List<String> cipherWords = cipherCounter.sizedWords.get(wordLen);
        
        int initialScore = scoreInverseMap(inverseTable);
        int currentScore = initialScore;
        
        PriorityQueue<String> wordQueue = new PriorityQueue<>(unMappedCorpusWordComp);

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
                char wordChar = word.charAt(idxUnMapped);
                
                char maxCiphChar = 0;
                int maxScore = 0;
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
                        char ciphChar = ciph.charAt(idxUnMapped);
                        Sx.format("findCiphersFromFixedLengthCorpusWords trying %c -> %c from %s <> %s\n"
                                , wordChar, ciphChar, word, ciph);

                        // Check if this char in the cipher word is already mapped:
                        if (inverseTable[ciphChar - 'a'] == 0) {
                            char inverseTemp[] = cloneInverseTablePlusOne(wordChar, ciphChar);
                            guessUnknownInverseCiphersFromCharCounts(inverseTemp);
                            int score = scoreInverseMap(inverseTemp);
                            if (maxScore < score) {
                                maxScore = score;
                                maxCiphChar = ciphChar;
                            }
                        }
                        else {
                            Sx.format("Rejecting %c -> %c because already %c -> %c\n"
                                    , wordChar, ciphChar
                                    , inverseTable[ciphChar - 'a'], ciphChar);
                        }
                    }    
                }
                if (maxScore > currentScore) {
                    Sx.format("Accepting %c -> %c because it gives new score %d > %d old score\n"
                            , wordChar, maxCiphChar, maxScore, currentScore);
                    
                    // Check (again?) here whether this char in the cipher word is already mapped:
                    char oldInverse = inverseTable[maxCiphChar - 'a'];
                    if (oldInverse != 0) {
                        Sx.format("Warning: erasing the old mapping of %c to %c\n", oldInverse, maxCiphChar);
                        forwardTable[oldInverse - 'a'] = 0;
                    }

                    
                    assignCipher(wordChar, maxCiphChar);
                    currentScore = maxScore;
                }
                else if (maxCiphChar != 0) {
                    Sx.format("Rejecting %c -> %c because already %c -> %c AND/OR maxScore %d < curScore %d\n"
                            , wordChar, maxCiphChar
                            , inverseTable[maxCiphChar - 'a'], maxCiphChar
                            , maxScore, currentScore);
                }
            } 
            else if (idxUnMapped == Integer.MIN_VALUE) {
                dumpQueue(wordQueue, "findCiphersFromFixedLengthCorpusWords dumping queue: " + wordLen);
                break; // all words left in the queue have at least 2 unknown chars, so move on
            }
        }
    }
    
    char[] cloneInverseTablePlusOne(char wordChar, char ciphChar)
    {
        char table[] = inverseTable.clone();
        table[ciphChar - 'a'] = wordChar;
        return table;
    }

    
    
    void findCiphersFromCorpusWordsLenThreePlus(double minWordFreqInCorpus, int numWords) // FIXME: choose one?
    {       
        int minCount = (int)(minWordFreqInCorpus * corpusCounter.totalWordCount);

        PriorityQueue<String> wordQueue = new PriorityQueue<>(unMappedCorpusWordComp);

        // Enqueue frequent two-letter words that contain at least one unknown letter
        for (Map.Entry<String, Integer> entry : corpusCounter.wordCounts.entrySet()) {
            String word = entry.getKey();
            int count = entry.getValue();
            if (count >= minCount && word.length() > 2) 
                wordQueue.add(word);
        }
        
        PriorityQueue<String> copyQueue = new PriorityQueue<String>(wordQueue); 
        dumpQueue(copyQueue, "findCiphersFromCorpusWordsLenThreePlus dumping queue at BEGINNING");
         
        
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
                        Sx.format("findCiphersFromCorpusWordsLenThreePlus trying %c -> %c from %s <> %s\n"
                                , corpUnMappedChar, ciphCharAtIdx, word, ciph);
                        if (inverseTable[ciphCharAtIdx - 'a'] == 0) {
                            Sx.format("Accepting %c -> %c\n", corpUnMappedChar, ciphCharAtIdx);
                            assignCipher(corpUnMappedChar, ciphCharAtIdx);
                            break;
                        }
                        else {
                            Sx.format("Rejecting %c -> %c because already %c -> %c\n"
                                    , corpUnMappedChar, ciphCharAtIdx
                                    , inverseTable[ciphCharAtIdx - 'a'], ciphCharAtIdx);
                        }
                    }
                }
            }
            else if (idxUnMapped == Integer.MIN_VALUE) {
                dumpQueue(wordQueue, "findCiphersFromCorpusWordsLenThreePlus dumping queue on END");
                break; // all words left in the queue have at least 2 unknown chars, so give up
            }
        }
    }
    

    void inferCipher(int how)
    {
        findCipher_I();
        findCipher_a();
        findCipher_the();   // use wordCount("the") and letterCount('e')
        findCipher_and();   // a + and : d, n
        
        switch (how) {
            case 0: 
                findCiphersFromFixedLengthCorpusWords(2, 0.006);
                findCiphersFromFixedLengthCorpusWords(3, 0.00222);
                findCiphersFromFixedLengthCorpusWords(4, 0.005);
                findCiphersFromFixedLengthCorpusWords(5, 0.00044);
                findCiphersFromFixedLengthCorpusWords(6, 0.00033);
                findCiphersFromFixedLengthCorpusWords(7, 0.00022);
                findCiphersFromFixedLengthCorpusWords(8, 0.00015);
                findCiphersFromFixedLengthCorpusWords(9, 0.00011);
                break;
            case 1:
                findCiphersFromTwoLetterCorpusWords(0.006);
                double minWordFreqInCorpus = 0.00125;
                findCiphersFromCorpusWordsLenThreePlus(minWordFreqInCorpus, 100);
                break;
            default:
                System.exit(0);
        }
        
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
        
        showNumbersOfMissingTableValues("Number of unmapped chars after forward matching");
        
        findMissingCharsFromCipherWords();
        
        showNumbersOfMissingTableValues("Number of unmapped chars after inverse regexing");

        guessUnknownCiphersFromCharCounts();
    }
    

    
    void findMissingCharsFromCipherWords()
    {
        Sx.puts("findMissingCharsFromCipherWords");
        
        Map<String, Integer> ciphCounts = cipherCounter.wordCounts;
        String cipherWords[] = new String[ciphCounts.size()];
        cipherWords = cipherCounter.wordCounts.keySet().toArray(cipherWords);
        Arrays.sort(cipherWords, unknownCipherWordComp);
        for (String ciph : cipherWords) {
            int wordLen = ciph.length();
            if (wordLen < 2 || wordLen > EnTextCounter.MAX_SIZED_LEN)
                continue;
            
            int numUnknown = numUnknownCipherChars(ciph);
            if (numUnknown == 0)
                continue;
            if (numUnknown > 2)
                continue;           // or break, if we know the rest are as bad or worse
                
            Pattern ciphPat = cipherWordToRegexPattern(ciph, wordLen);
            for (String word : corpusCounter.sizedWords.get(wordLen)) {
                Matcher match = ciphPat.matcher(word);
                if (match.matches()) {
                    Sx.format("Matched: %s -> %s : %s\n", ciph, ciphPat.toString(), word);
                    int index = match.start(1);
                    char wordChar = word.charAt(index);
                    char ciphChar = ciph.charAt(index);
                    Sx.format("First L: %s[%d]<%c> -> %s[%d]<%c>\n", word, index, wordChar,
                            ciph, index, ciphChar);

                    // Check if this char in the cipher word is already mapped:
                    Sx.format("findMissingCharsFromCipherWords trying %c -> %c from %s <> %s\n"
                            , wordChar, ciphChar, word, ciph);
                    if (forwardTable[wordChar - 'a'] == 0) {
                        Sx.format("Accepting %c -> %c\n", wordChar, ciphChar);
                        assignCipher(wordChar, ciphChar);
                        break;
                    }
                    else {
                        Sx.format("Rejecting %c -> %c because already %c -> %c\n"
                                , wordChar, ciphChar
                                , wordChar, forwardTable[wordChar - 'a']);
                    }
                }
            }
        }
    }
    
    /**
     * Make a compiled regex pattern containing up to 2 wildcards,
     * each to match a single unknown character.  For two instances
     * of the same unknown input character, a backreference is used
     * to enforce this identity in any matched words.
     * @param ciph
     * @param wordLen
     * @return
     */
    Pattern cipherWordToRegexPattern(String ciph, int wordLen) {
        StringBuilder sb = new StringBuilder("^");
        int numWildcards = 0;
        for (int j = 0; j < wordLen; j++) {
            char ciphChar = ciph.charAt(j);
            char corpChar = inverseTable[ciphChar - 'a'];
            char firstUnknownChar = 0;
            if (corpChar == 0) {
                numWildcards++;
                if (numWildcards == 1) {
                    firstUnknownChar = ciphChar;
                    sb.append("(\\w)");         // Could use Posix: \p{Lower} (US-ASCII only) 
                } else {
                    if (ciphChar == firstUnknownChar) {
                        sb.append("(\\1)");     // back-reference to match value of first group 
                    } else {
                        sb.append("(\\w)");     // second unknown char is different from the first
                        ////sb.append("(?!\\1)");     // second unknown char is different from the first
                    }
                }
            } 
            else {
                sb.append(corpChar);
            }
        }
        sb.append("$");
        String patString = sb.toString();
        return Pattern.compile(patString);
    }

    void showNumbersOfMissingTableValues(String label)
    {
        int forward = numMissingTableValues(forwardTable);
        int inverse = numMissingTableValues(inverseTable);
        Sx.format("%s: corpus %d  cipher %d\n", label, forward, inverse);
    }
    
    int numMissingTableValues(char table[]) {
        int numMissingValues = 0;
        for (int j = 0; j < EnTextCounter.ALPHABET_SIZE; j++) 
            if (table[j] == 0)
                numMissingValues++;
        return  numMissingValues;
    }
    
    void guessUnknownCiphersFromCharCounts()
    {
        int ciphIdx = 0;
        for (int corpIdx = 0; ciphIdx < EnTextCounter.ALPHABET_SIZE; ciphIdx++) {
            char cipherChar = cipherCounter.charCounts[ciphIdx].chr;
            if (inverseTable[cipherChar - 'a'] == 0) {
                if (corpIdx >= EnTextCounter.ALPHABET_SIZE) {
                    String errorMessage = "ERROR: No unnasigned corpus chars for cipher "
                            + cipherChar;
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
    
    void guessUnknownInverseCiphersFromCharCounts(char inverseMap[])
    {
        int ciphIdx = 0;
        for (int corpIdx = 0; ciphIdx < EnTextCounter.ALPHABET_SIZE; ciphIdx++) {
            char cipherChar = cipherCounter.charCounts[ciphIdx].chr;
            if (inverseMap[cipherChar - 'a'] == 0) {
                if (corpIdx >= EnTextCounter.ALPHABET_SIZE) {
                    String errorMessage = "ERROR: No unnasigned corpus chars for cipher "
                            + cipherChar;
                    Sx.puts(errorMessage);
                    throw new IllegalStateException(errorMessage);
                }
                do {
                    char corpusChar = corpusCounter.charCounts[corpIdx++].chr;
                    if (forwardTable[corpusChar - 'a'] == 0) {          // the "real" table, read-only
                        inverseMap[cipherChar - 'a'] = corpusChar;      // the "fake" table, temporary
                        break;
                    }
                } while (corpIdx < EnTextCounter.ALPHABET_SIZE);
            }
        }
    }
    
    /**
     * This would only work if the letter frequencies in the corpus
     * are very similar to those in the cipher-encoded text.
     */
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
    

    int scoreInverseMap(char inverseMap[])
    {
        int score = 0;
        for (String ciph : cipherCounter.wordCounts.keySet()) {
            if (ciph.length() < 2)
                continue;
            String deciph = decipher(ciph, inverseMap);
            ////Sx.print(deciph + "  ");
            if (corpusCounter.wordCounts.containsKey(deciph)) {
                score += cipherCounter.wordCounts.get(ciph)*ciph.length();
            }
        }
        ////Sx.puts();
        return score;
    }
    
    String decipher(String encodedString, char inverseMap[])
    {
        char inv, decodedChars[] = encodedString.toCharArray();
        for (int j = 0; j < decodedChars.length; j++) {
            char chr = decodedChars[j];
            if (EnTextCounter.isAsciiLowerCaseLetter(chr)) {
                inv = inverseMap[chr - 'a'];
                decodedChars[j] = inv == 0 ? '_' : inv;
            }
            else if (EnTextCounter.isAsciiUpperCaseLetter(chr)) {
                chr = Character.toLowerCase(chr);
                inv = inverseMap[chr - 'a'];                    
                decodedChars[j] = inv == 0 ? '_' : Character.toUpperCase(inv);                    
            }
        }
        return new String(decodedChars);
    }
        
    void decodeCipherText()
    {
        Sx.format("Encoded/Decoded cipher text (%s)\n\n", cipherFilePath);
        for (String line : cipherFileLines) {
            String deciphered = decipher(line, inverseTable);
            Sx.puts(deciphered);
            Sx.puts(line);
        }
    }
    
   
    /**
     * Returns the total number of instances of corpus characters without
     * known cipher values.  Two instances of the same unmapped character
     * are counted as 2, not 1.  For example, if 'a' were mapped but 'l'
     * unmapped, then the number returned for the word "all" would be 2.
     */
    int numUnMappedCorpusChars(String word) {
        return (int) word.chars().filter(x -> forwardTable[x - 'a'] == 0).count();
    }
    
    /**
     * Returns the total number of instances of cipher characters with unknown
     * corresponding corpus characters.  Two instances of the same unknown
     * character are counted as 2, not 1, as in the corresponding method for
     * unmapped corpus characters. 
     * @see numUnMappedCorpusChars
     */
    int numUnknownCipherChars(String ciph) {
        return (int) ciph.chars().filter(x -> inverseTable[x - 'a'] == 0).count();
    }
    
    /**
     * Returns the total number of different cipher characters with unknown
     * corresponding corpus characters.  Two instances of the same unknown
     * character are counted as 1, not 2.  For example, if 'g' and 'o' were
     * known, but not 'n', then the number returned for the cipher word "gnno"
     * would be 1.  Finding that one missing correspondence would solve for
     * the rest of the word 
     */
    int numDistinctUnknownCipherChars(String ciph) {
        return (int) ciph.chars().distinct().filter(x -> inverseTable[x - 'a'] == 0).count();
    }
    
    ////////////// Inner Classes ///////////////
    
    class AscendingUnMappedCharsDescendingWordCountsComp implements Comparator<String>
    {       
        @Override
        public int compare(String wordA, String wordB) 
        {    
            int unmapA = numUnMappedCorpusChars(wordA);
            int unmapB = numUnMappedCorpusChars(wordB);
            if (unmapA != unmapB)
                return unmapA - unmapB;
            int countA = corpusCounter.wordCounts.getOrDefault(wordA, 0);
            int countB = corpusCounter.wordCounts.getOrDefault(wordB, 0);
            if (countA != countB)
                return Integer.compare(countB, countA); // Descending counts
            return wordA.compareTo(wordB);
        }
    }

    class AscendingUnknownCharsDescendingCipherWordCountsComp implements Comparator<String>
    {       
        @Override
        public int compare(String wordA, String wordB) 
        {    
            int unmapA = numUnknownCipherChars(wordA);
            int unmapB = numUnknownCipherChars(wordB);
            if (unmapA != unmapB)
                return unmapA - unmapB;
            int countA = cipherCounter.wordCounts.getOrDefault(wordA, 0);
            int countB = cipherCounter.wordCounts.getOrDefault(wordB, 0);
            if (countA != countB)
                return Integer.compare(countB, countA); // Descending counts
            return wordA.compareTo(wordB);
        }
    }

    ////////////// Unit Tests ///////////////

    public static int unit_test(int level) {
        String testName = SubCipher.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        SubCipher sc = new SubCipher(FileUtil.getTextFilePath("cipher.txt"),
            ////                         FileUtil.getTextFilePath("corpusEn400kWords.txt"));
            FileUtil.getTextFilePath("corpus-en.txt"));   
            ////FileUtil.getTextFilePath("corpusEn.txt"));   
            ////"src/sprax/subs/deciphered.txt");   
        
        sc.corpusCounter.showCounts("\n     CORPUS: ", 1);
        sc.cipherCounter.showCounts("\n     CIPHER: ", 3);
        sc.inferCipher(0);
        sc.showCipherRows(sc.forwardTable);
        //sc.showForwardCipher();
        sc.decodeCipherText();
        int score = sc.scoreInverseMap(sc.inverseTable);
        numWrong += 614 - score;
        
        Sx.puts();
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
}
