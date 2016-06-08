
package sprax.wordcounts;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Profile for a speaker or writer based on word-tuples collected from their sentences.
 */
public abstract class WordTupleProfile {

    static final String WORD_DELIMITER = " ";     // single space, which cannot occur inside a word

    private int numSentencesInput = 0;
    private int numWordsInput = 0;

    /**
     * Is this word present in the profile?  (Was it or some hash or other correlate of it ever added?)
     * @param word
     * @return count of this word's occurrences, if available, or just 1 = (maybe) present, 0 = not present.
     */
    public abstract int getWordPresence(String word);

    /** 
     * Is this word pair present in the profile?  (Was it or some hash or other correlate of it ever added?)
     * @return count of this pair's occurrences, if available, or just 1 = (maybe) present, 0 = not present.
     */
    public abstract int getPairPresence(String wordA, String wordB);

    /** 
     * Is this word triad present in the profile?  (Was it or some hash or other correlate of it ever added?)
     * @return count of this triad's occurrences, if available, or just 1 = (maybe) present, 0 = not present.
     */
    public abstract int getTriadPresence(String wordA, String wordB, String wordC);

    /** Sub-classes should not call this directly; they should call addWord, which calls this template method */
    protected abstract void templateAddWord(final String word);

    /** Sub-classes should not call this directly.  Base class calls this or templateAddWordsPairsAndTriads; not both. */
    protected abstract void templateAddWordsAndPairs(final String sentence);

    /** Sub-classes should not call this directly.  Base class calls this or templateAddWordsAndPairs; not both. */
    protected abstract void templateAddWordsPairsAndTriads(final String sentence);

    /** Recommended that sub-classes not call this directly, but call showCounts. */
    protected abstract void templateShowCounts();

    //////// Base method implementations ///////

    public int getNumWordsInput()        { return numWordsInput; }
    public int getNumSentencesInput()    { return numSentencesInput; }

    /**
     * Public API to augment profile by one sentence. 
     */
    public void addSentence(final String sentence) {
        ++numSentencesInput;
        templateAddWordsPairsAndTriads(sentence);
    }

    /** Always call this to add the current word, even if it is a duplicate  */
    protected void addWord(final String word) {
        assert( ! word.isEmpty());
        ++numWordsInput;
        templateAddWord(word);
    }

    /** Initialize or augment profile from a text file representing a corpus */
    public void addCorpus(final String corpusFileSpec) {
        try {
            Stream<String> sentences = SentenceStream.sentencesFromFile(corpusFileSpec);
            addSentences(sentences);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.format("WARNING: Failed to initialize %s from corpus file: %s\n", 
                    this.getClass().getSimpleName(), corpusFileSpec);
        }        
    }
    
    // Initialize or augment profile from a stream of sentences
    public void addSentences(Stream<String> sentences) {
        sentences.forEach(this::addSentence);
    }

    public void showCounts(String label) {
        System.out.println(label);
        System.out.format("    input sentences: %d,  input words: %d\n", 
                getNumSentencesInput(), getNumWordsInput());
        templateShowCounts();
        System.out.println();
    }


    //////// Unit testing ////////

    public static void unit_test() 
    {
        String  testName = WordTupleProfile.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    
        //final String textFilePath = getTextFilePath("DonQuixote_EnGutenberg.txt";
        final String textFilePath = ProfileUtil.getTextFilePath("mobydickChapter1.txt");

        WordTupleProfile profileStoringStrings = new StringCounterProfile(textFilePath);
        profileStoringStrings.showCounts(profileStoringStrings.getClass().getSimpleName());

        WordTupleProfile profileStoringHashes = new HashCounterProfile(textFilePath);
        profileStoringStrings.showCounts(profileStoringHashes.getClass().getSimpleName());

        // TODO: Find a better estimate, or rather 3: separate for words, pairs, & triples.
        int numExpectedInsertions = profileStoringHashes.getNumSentencesInput();

        WordTupleProfile profileStoringFilter = new BloomFilterProfile(textFilePath);
        profileStoringFilter.showCounts(profileStoringFilter.getClass().getSimpleName());

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }

    /**
     *  Test results:
     *  Moby Dick:
     */
}
