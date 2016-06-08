
package sprax.wordcounts;

import java.util.stream.Stream;

/**
 * Profile for a speaker or writer as collected from their sentences.
 * Represents word-tuples as strings, which requires a lot of (redundant) space.
 */
public class StringCounterProfile extends CounterProfile<String> {

    private MapTupleCounter<String> wordCounts = new MapTupleCounter<>();
    private MapTupleCounter<String> pairCounts = new MapTupleCounter<>();
    private MapTupleCounter<String> trebCounts = new MapTupleCounter<>();
    
    /** Non-public default constructor to be used by a class factory */
    StringCounterProfile() {}   
    
    /** Initializing constructor (1 of 2): from text file. */
    public StringCounterProfile(final String corpusFileSpec) {
        addCorpus(corpusFileSpec);
    }

    /** Initializing constructor (2 of 2): from sentence stream */
    public StringCounterProfile(Stream<String> sentences) {
        addSentences(sentences);
    }

    @Override
    public int getWordPresence(String word) {
        return wordCounts.getCount(word);
    }

    @Override
    public int getPairPresence(String wordA, String wordB) {
        String pairAB = String.join(WORD_DELIMITER, wordA, wordB);
        return pairCounts.getCount(pairAB);
    }

    @Override
    public int getTriadPresence(String wordA, String wordB, String wordC) {
        String triadABC = String.join(WORD_DELIMITER, wordA, wordB, wordC);
        return trebCounts.getCount(triadABC);
    }

    @Override
    protected void templateAddWordsAndPairs(final String sentence) 
    {
        String words[] = ProfileUtil.parseStringToWords(sentence);
        String wordPrev = null;
        for (String wordNow : words) {
            addWord(wordNow);
            if (wordPrev != null) {
                addWordPair(wordPrev, wordNow);
            }
            wordPrev = wordNow;
        }
    }

    @Override
    protected void templateAddWordsPairsAndTriads(final String sentence) {
        String words[] = ProfileUtil.parseStringToWords(sentence);
        String wordA = null, wordB = null, wordsAB = null;
        for (String wordC : words) {
            addWord(wordC);
            if (wordA != null) {
                // Must add the new triad before replacing the old pair in wordsAB
                addWordTriad(wordsAB, wordC);
                wordsAB = addWordPair(wordB, wordC);
            } else if (wordB != null) {
                wordsAB = addWordPair(wordB, wordC);
            }
            wordA = wordB;
            wordB = wordC;
        }
    }


    @Override
    protected void templateAddWord(final String word) {
        wordCounts.addCount(word);
    }

    String addWordPair(final String first, final String second) {
        String wordPair = String.join(WORD_DELIMITER, first, second);
        pairCounts.addCount(wordPair);
        return wordPair;
    }

    void addWordTriad(final String firstAndSecond, final String third) {
        String wordTriad = String.join(WORD_DELIMITER, firstAndSecond, third);
        trebCounts.addCount(wordTriad);
    }

    public static void unit_test() 
    {
        String  testName = StringCounterProfile.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    

        final String textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");

        StringCounterProfile sp = new StringCounterProfile(textFilePath);
        sp.showCounts(sp.getClass().getSimpleName());

        if (sp.getNumWordsInput() < 50) {
            int ord = 1;
            for (String key : sp.pairCounts.getKeys()) {
                System.out.format("pair %3d: %s   %d\n", ord++, key, sp.pairCounts.getCount(key));
            }
            ord = 1;
            for (String key : sp.trebCounts.getKeys()) {
                System.out.format("triple %3d: %s   %d\n", ord++, key, sp.trebCounts.getCount(key));
            }
        }

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }

    @Override
    public MapTupleCounter<String> getWordCounts() {
        return wordCounts;
    }

    @Override
    public MapTupleCounter<String> getPairCounts() {
        return pairCounts;
    }

    @Override
    public MapTupleCounter<String> getTrebCounts() {
        return trebCounts;
    }

}
