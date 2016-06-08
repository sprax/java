
package sprax.wordcounts;

import java.util.Set;

/**
 * Base profile for a speaker or writer based on word-tuples collected from their sentences.
 */
public abstract class CounterProfile<K> extends WordTupleProfile {

    static int HASH_COMBO_FACTOR = 31;

    public int getNumWordsCounted()   { return getWordCounts().getSize(); }
    public int getNumPairsCounted()   { return getPairCounts().getSize(); }
    public int getNumTrebsCounted()   { return getTrebCounts().getSize(); }
    
    Set<K> getWordKeys()            { return getWordCounts().getKeys(); }

    // Would prefer these to be a "read-only" references, or eliminated entirely.
    public abstract TupleCounter<K> getWordCounts();
    public abstract TupleCounter<K> getPairCounts();
    public abstract TupleCounter<K> getTrebCounts();
    
    @Override
    protected void templateShowCounts() {
        int inputWords = getNumWordsInput();
        int inputSentences = getNumSentencesInput();
        System.out.format("    input words: %d,  unique words: %d,  pair keys: %d,  triad keys: %d\n", 
                inputWords, getNumWordsCounted(), getPairCounts().getSize(), getTrebCounts().getSize());
        System.out.format("    thus word repeats = %d,  pair repeats ~ %d,  triad repeats ~ %d\n", 
                inputWords - getNumWordsCounted(), 
                inputWords - getNumPairsCounted() - inputSentences, 
                inputWords - getNumTrebsCounted() - inputSentences * 2);
    }

    public static void unit_test() 
    {
        String  testName = CounterProfile.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    

        final String textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");

        CounterProfile<String> sp = new StringCounterProfile(textFilePath);
        sp.showCounts(sp.getClass().getSimpleName());

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }

    /**
     *  Test results:
     *  Moby Dick:
     *    Storing strings: all words: 218244,  unique words: 21347,  pair keys: 115800,  triad keys: 177149
     *    Storing hashes:  all words: 218244,  unique words: 21347,  pair keys: 115749,  triad keys: 177138
     *    So just a few hash collisions:                         0,                 51,                  11
     */
}
