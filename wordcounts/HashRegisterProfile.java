
package sprax.wordcounts;

import java.util.stream.Stream;

/**
 * Profile for a speaker or writer as collected from their sentences.
 * Represents word-tuples as hashes, which saves space but loses a little accuracy.
 */
public class HashRegisterProfile extends CounterProfile<Integer> {

    private SetTupleCounter<Integer> wordSet = new SetTupleCounter<>();
    private SetTupleCounter<Integer> pairSet = new SetTupleCounter<>();
    private SetTupleCounter<Integer> trebSet = new SetTupleCounter<>();

    /** Non-public default constructor to be used by a class factory */
    HashRegisterProfile() {}

    /** Initializing constructor (1 of 2): from text file. */
    public HashRegisterProfile(final String corpusFileSpec) {
        addCorpus(corpusFileSpec);
    }

    /** Initializing constructor (2 of 2): from sentence stream */
    public HashRegisterProfile(Stream<String> sentences) {
        addSentences(sentences);
    }

    @Override
    public int getWordPresence(String word) {
        int wordHash = word.hashCode();
        return wordSet.getCount(wordHash);
    }

    @Override
    public int getPairPresence(String wordA, String wordB) {
        int hashA = wordA.hashCode();
        int hashB = wordB.hashCode();
        int hashAB = combineHashes(hashA, hashB);
        return pairSet.getCount(hashAB);
    }

    @Override
    public int getTriadPresence(String wordA, String wordB, String wordC) {
        int hashA = wordA.hashCode();
        int hashB = wordB.hashCode();
        int hashC = wordC.hashCode();
        int hashAB = combineHashes(hashA, hashB);
        int hashABC = combineHashes(hashAB, hashC);
        return trebSet.getCount(hashABC);
    }

    @Override
    public TupleCounter<Integer> getWordCounts() {
        return wordSet;
    }

    @Override
    public TupleCounter<Integer> getPairCounts() {
        return pairSet;
    }

    @Override
    public TupleCounter<Integer> getTrebCounts() {
        return trebSet;
    }

    @Override
    protected void templateAddWordsAndPairs(final String sentence) 
    {
        String words[] = ProfileUtil.parseStringToWords(sentence);
        String wordOld = null;
        int hashOld = 0, hashNew = 0;
        for (String wordNew : words) {
            addWord(wordNew);
            hashNew = wordNew.hashCode();
            if (wordOld != null) {
                addHashPair(pairSet, hashOld, hashNew);
            }
            wordOld = wordNew;
            hashOld = hashNew;
        }
    }

    @Override
    protected void templateAddWordsPairsAndTriads(final String sentence) 
    {
        String words[] = ProfileUtil.parseStringToWords(sentence);
        String wordA = null, wordB = null;
        int hashB = 0, hashAB = 0;
        for (String wordC : words) {
            addWord(wordC);
            int hashC = wordC.hashCode();
            if (wordA != null) {
                addHashPair(trebSet, hashAB, hashC);
                hashAB = addHashPair(pairSet, hashB, hashC);
            } else if (wordB != null) {
                hashAB = addHashPair(pairSet, hashB, hashC);
            }
            wordA = wordB;
            wordB = wordC;
            hashB = hashC;
        }
    }

    @Override
    protected void templateAddWord(final String word) {
        wordSet.addCount(word.hashCode());
    }

    static int addHashPair(SetTupleCounter<Integer> hashCounts, int firstHash, int secondHash) {
        int combinedHash = combineHashes(firstHash, secondHash);
        hashCounts.addCount(combinedHash);
        return combinedHash;
    }

    /**
     * @param hashA
     * @param hashB
     * @return
     */
    static int combineHashes(int hashA, int hashB) {
        return hashA * HASH_COMBO_FACTOR + hashB;
    }

    public static void unit_test() 
    {
        String  testName = HashRegisterProfile.class.getName() + ".unit_test";
        System.out.println(testName + " BEGIN");    

        final String textFilePath = ProfileUtil.getTextFilePath("Melville_MobyDick.txt");

        HashRegisterProfile sp = new HashRegisterProfile(textFilePath);
        sp.showCounts(sp.getClass().getSimpleName());

        System.out.println(testName + " END");
    }

    public static void main(String[] args) {
        unit_test();
    }

}
