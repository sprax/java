
package sprax.wordcounts;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

/**
 * Profile for a speaker or writer as collected from their sentences.
 * Represents word-tuples as hashes, which saves space but loses a little accuracy.
 */
public class BloomFilterProfile extends WordTupleProfile {

    private final double wordMissWeight = 7.0;
    private final double pairMissWeight = 3.0;
    private final double trebMissWeight = 1.0;
    private final double missWeightTotal = wordMissWeight + pairMissWeight + trebMissWeight;

    private BloomFilter<String> wordFilter;
    private BloomFilter<String> pairFilter;
    private BloomFilter<String> trebFilter;

    int minUniqueWords;
    int minUniquePairs;
    int minUniqueTriads;

    @Override
    public int getWordPresence(String word) {
        return wordFilter.mightContain(word) ? 1 : 0;
    }

    @Override
    public int getPairPresence(String wordA, String wordB) {
        String pairAB = String.join(WORD_DELIMITER, wordA, wordB);
        return pairFilter.mightContain(pairAB) ? 1 : 0;
    }

    @Override
    public int getTriadPresence(String wordA, String wordB, String wordC) {
        String trebABC = String.join(WORD_DELIMITER, wordA, wordB, wordC);
        return trebFilter.mightContain(trebABC) ? 1 : 0;
    }

    /** Non-public default constructor (can be used by a class factory) */
    BloomFilterProfile() {}
    
    /** Initializing constructor (1 of 2) */
    BloomFilterProfile(final String corpusFileSpec) {
        addCorpus(corpusFileSpec);
    }
    
    /** Initializing constructor (2 of 2) */
    BloomFilterProfile(final String corpusFileSpec, int expectedInsertions[]) {
        addCorpus(corpusFileSpec, expectedInsertions);
    }
    
    // FIXME: temporary hack
    int[] estimateUniqueWordTuples(String fileSpec) {
    	return new int[] { 30000, 50000, 70000 };
    }
    
    /** Get word-tuple counts from which to reckon expected numbers of insertions,
     * create the filters, and initialize them.
     */
    @Override
    public void addCorpus(final String corpusFileSpec) {
    	int numUniqueWordTuples[] = estimateUniqueWordTuples(corpusFileSpec);
    	addCorpus(corpusFileSpec, numUniqueWordTuples);
    }

    
    /** Get word-tuple counts from which to reckon expected numbers of insertions,
     * create the filters, and initialize them.
     */
    private void addCorpus(final String corpusFileSpec, int expectedInsertions[]) {
     	assert(expectedInsertions.length > 2);
        wordFilter = BloomFilter.create(new StringFunnel(), expectedInsertions[0]);
        pairFilter = BloomFilter.create(new StringFunnel(), expectedInsertions[1]);
        trebFilter = BloomFilter.create(new StringFunnel(), expectedInsertions[2]);    	
    	super.addCorpus(corpusFileSpec);
    }

    private class StringFunnel implements Funnel<String>
    {
		private static final long serialVersionUID = 2809783535980039343L;
		@Override
        public void funnel(String from, PrimitiveSink into) {
            into.putBytes(from.getBytes());
        }
    }

    @Override
    protected void templateAddWord(String word) {
        if (wordFilter.put(word)) {
            ++minUniqueWords;
        }
    }

    String addWordPair(final String first, final String second) {
        String wordPair = String.join(WORD_DELIMITER, first, second);
        if (pairFilter.put(wordPair)) {
            ++minUniquePairs;
        }
        return wordPair;
    }

    void addWordTriad(final String firstAndSecond, final String third) {
        String wordTriad = String.join(WORD_DELIMITER, firstAndSecond, third);
        if (trebFilter.put(wordTriad)) {
            ++minUniqueTriads;
        }
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
    protected void templateAddWordsPairsAndTriads(final String sentence) 
    {
        String words[] = ProfileUtil.parseStringToWords(sentence);
        String wordA = null, wordB = null, pairAB = null;
        for (String wordC : words) {
            addWord(wordC);
            if (wordA != null) {
                // Must add the new triad before replacing the old pair in wordsAB
                addWordTriad(pairAB, wordC);
                pairAB = addWordPair(wordB, wordC);
            } else if (wordB != null) {
                // Save pairAB for next iteration
                pairAB = addWordPair(wordB, wordC);
            }
            wordA = wordB;
            wordB = wordC;
        }
    }

    public double distanceFromWordsPairsAndTriads(final String sentence) 
    {
        double distance = 0;

        String words[] = ProfileUtil.parseStringToWords(sentence);
        int wordCount = words.length;
        int pairCount = wordCount - 1;
        int trebCount = pairCount - 1;

        int wordMisses = 0, pairMisses = 0, trebMisses = 0;
        String wordA = null, wordB = null;
        for (String wordC : words) {
            if (!wordFilter.mightContain(wordC)) {
                wordMisses++;
            }

            if (wordB != null) {
                String pairBC = String.join(WORD_DELIMITER, wordB, wordC);
                if (!pairFilter.mightContain(pairBC)) {
                    pairMisses++;
                }

                if (wordA != null) {
                    String trebABC = String.join(WORD_DELIMITER, wordA, pairBC);
                    if (!trebFilter.mightContain(trebABC)) {
                        trebMisses++;
                    }
                } 
            }

            wordA = wordB;
            wordB = wordC;
        }

        if (wordCount > 0) {
            distance += (double) wordMisses * wordMissWeight / wordCount;
            if (pairCount > 0) {
                distance += (double) pairMisses * pairMissWeight / pairCount;
                if (trebCount > 0) {
                    distance += (double) trebMisses * trebMissWeight / trebCount;
                }
            }
        }
        return distance / missWeightTotal;
    }



    @Override
    protected void templateShowCounts() {
        System.out.format("    minimal unique word keys: %d,  pair keys: %d,  and triad keys: %d\n", 
                minUniqueWords, minUniquePairs, minUniqueTriads);

        // Storage requirement: something like 100 Kb for 100K words and 3% FPP
        // System.out.format("    Estimated memory footprint of filters:  words %d,  pairs: %d,  triads: %d\n", 
        //        minUniqueWords, minUniquePairs, minUniqueTriads);
    }


    public static void unit_test() 
    {
        String  testName = BloomFilterProfile.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    

        final String textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");
        
        HashRegisterProfile hashProfile = new HashRegisterProfile(textFilePath);
        hashProfile.showCounts(hashProfile.getClass().getSimpleName() + " counts:");
        int numWordInsertions = hashProfile.getNumWordsCounted();
        int numPairInsertions = hashProfile.getNumPairsCounted();
        int numTrebInsertions = hashProfile.getNumTrebsCounted();
        int expectedInsertions[] = { numWordInsertions, numPairInsertions, numTrebInsertions };
        
        BloomFilterProfile bloomProfile = new BloomFilterProfile(textFilePath, expectedInsertions);
        bloomProfile.showCounts(bloomProfile.getClass().getSimpleName() + " counts:");

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }

}
