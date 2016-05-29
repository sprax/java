package sprax.tries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sprax.Sx;
import sprax.arrays.ArrayIter;
import sprax.files.FileUtil;
import sprax.files.TextFileToStringCollection;
import sprax.files.TextFilters;
import sprax.heaps.MinHeap;
import sprax.tries.WordTrie.WordNode;

public class AutoCompleteWord implements AutoCompleteInterface
{
    protected String   mDictionaryPath = "words.txt";
    protected WordTrie mWordTrie = new WordTrie();
    public void setDictionaryPath(String dictPath) {
        mDictionaryPath = dictPath;
    }

    int mMinWordLen;
    int mMaxWordLen;
    int mMaxFoundLen;

    AutoCompleteWord(WordTrie wordTrie) 
    {   
        if (wordTrie == null) {
            initWordsFromDictionaryFile(mDictionaryPath, mMinWordLen, mMaxWordLen);
        } else {
            mWordTrie   = wordTrie;
            mMinWordLen = wordTrie.mActMinWordLen;
            mMaxWordLen = wordTrie.mActMaxWordLen;
        }
    }

    AutoCompleteWord(String dictionaryPath, int minWordLen, int maxWordLen) 
    {
        mDictionaryPath = dictionaryPath;
        mMinWordLen = minWordLen;
        mMaxWordLen = maxWordLen;
        initWordsFromDictionaryFile(mDictionaryPath, mMinWordLen, mMaxWordLen);
    }

    public WordTrie getTrie()  { return mWordTrie; }

    public void initWordsFromDictionaryFile(String dictionaryPath, int minWordLen, int maxWordLen)
    {
        mWordTrie.initFromSortedDictionaryFile(dictionaryPath, minWordLen, maxWordLen, 1);
    }

    @Override
    public List<String> getPossible(String prefix) {
        return  mWordTrie.getWordsPartiallyMatchingPrefix(prefix);
    }

    @Override
    public List<String> getPossible(String prefix, int limit) 
    {
        List<String> words = mWordTrie.getWordsPartiallyMatchingPrefix(prefix);
        int count =  words.size();
        if (count > limit)
            return words.subList(0, limit);
        return words;
    }

    protected static int test_getPossible(AutoCompleteWord auto, String prefix, int limit, int verbosity) 
    {
        List<String> words = auto.getPossible(prefix, limit);
        if (verbosity > 1) {
            WordTrie.test_getWordsPartiallyMatchingPrefix(auto.getTrie(), prefix);   
            String stem = auto.getTrie().longestPrefix(prefix);
            Sx.format("AutoComplete possible completions for prefix \"%s\"[%s] (%d):\t", prefix, stem, words.size());
        }
        Sx.putsIterable(words, 8);
        return 0;
    }

    /**
     * returns list of probable matches (or an empty list if no matches are found)
     */
    @Override
    public List<String> getProbable(String prefix, int limit)
    {
        assert(prefix != null);
        assert(limit > 0);
        List<String> words = new ArrayList<String>(limit);

        int numProbWords = 0; 
        WordNode node = mWordTrie.maxStemNode(prefix);
        WordTrie.NodeCount nodeCount = mWordTrie.new NodeCount(node);
        if (mWordTrie.minGreedyContinuation(nodeCount)) {
            if (limit == 1) {
                words.add(nodeCount.mNode.getWord());
                return words;
            }
            WordTrie.NodeCount comps[] = new WordTrie.NodeCount[limit];
            comps[numProbWords++] = nodeCount.clone();
            while (WordTrie.nextGreedyContinuation(nodeCount)) {
                comps[numProbWords] = nodeCount.clone();
                if (++numProbWords == limit) {
                    break;
                }
            }
            Arrays.sort(comps, 0, numProbWords);
            for (int j = 0; j < numProbWords; j++) {      
                words.add(comps[j].mNode.getWord());
                Sx.puts(words.get(j) + "\t" + comps[j].mCount);
            }
            return words;
        }
        return words;
    }


    static int sDbg = 2;

    protected static int test_getProbableNodes(AutoCompleteWord acw, String prefix, int maxNumWords, int maxDepth, int verbose) 
    {
        WordTrie trie = acw.getTrie();
        WordNode node = trie.maxStemNode(prefix);

        WordNode wordNodes4Array[] = new WordNode[maxNumWords];
        WordNode wordNodes4AIter[] = new WordNode[maxNumWords];
        WordNode wordNodes4LHeap[] = new WordNode[maxNumWords];
        ArrayIter<WordNode> nodeIter = new ArrayIter<WordNode>(wordNodes4AIter, 0, 0); 
        MinHeap<WordNode> minHeap = new MinHeap<WordNode>(wordNodes4LHeap, 0, true);
        int numProbA = 0, numProbI = 0, numProbH = 0;
        int eGetProb = 0;
        switch (eGetProb) {
            case  0: numProbA = trie.getProbableWordNodes(wordNodes4Array, maxNumWords, node, maxDepth);
            break;
            case  1: numProbI = trie.getProbableWordNodes(nodeIter, maxNumWords, node, maxDepth);
            break;
            default: numProbH = trie.getProbableWordNodes(minHeap, maxNumWords, node, maxDepth);
            break;
        }
        if (numProbA != numProbI || numProbA != numProbH) {
            Sx.format("numProbs don't agree: A, I, H:  %d  %d  %d\n", numProbA, numProbI, numProbH);
        }

        if (verbose > 1) {
            if (verbose > 2)
                WordTrie.test_getWordsPartiallyMatchingPrefix(trie, prefix);
            String stem = trie.longestPrefix(prefix);
            Sx.format("AutoComplete probable completions for prefix \"%s\"[%s] (%d):\t"
                    , prefix, stem, numProbA);
        }

        if (numProbA == 0 && numProbI == 0 && numProbH == 0) {
            Sx.puts("[none]");
        }
        else {
            int numProb = Math.max(numProbA, numProbI);
            numProb = Math.max(numProb, numProbH);
            WordNode wn = null;
            for (int j = numProb; --j >= 0; ) { 
                WordNode wnA = wordNodes4Array[j];
                WordNode wnI = wordNodes4AIter[j];
                WordNode wnH = wordNodes4LHeap[j];
                // Sort order may be different if nodes have the same word counts and text counts.
                // TODO: Add lexicographical sorting?  Maybe.  Only on final sort.
                // TODO: Add set-wise equality/comparison.
                if ( wnA != null && wnI != null && wnH != null ) {
                    if ( (wnA != wnI && (wnA.getWordCount() != wnI.getWordCount() || wnA.getTextCount() != wnI.getTextCount())) 
                            || (wnA != wnH && (wnA.getWordCount() != wnH.getWordCount() || wnA.getTextCount() != wnH.getTextCount()))) {
                        if (sDbg > 1)
                            Sx.puts("\n    WARNING: Unequal at " + j + ": " + wnA + wnI + wnH);
                        else
                            throw new IllegalStateException("Unequal at " + j + ": " + wnA + wnI + wnH);
                    }
                    wn = wnA;
                } else {
                    wn = wnA != null ? wnA : wnI;
                    wn = wn  != null ? wn  : wnH;
                }
                int partialTextCount = trie.partialTextCount(node, wn);
                if (verbose > 2) {
                    int totalTextCount = trie.totalTextCount(wn);
                    Sx.format("%s(%d|%d|%d|%d)  ", wn.getWord(), wn.getWordCount(), wn.getTextCount(), partialTextCount, totalTextCount);
                } else {
                    Sx.format("%s(%d|%d)  ", wn.getWord(), wn.getWordCount(), wn.getTextCount());
                }
            }
        }
        Sx.puts();
        return 0;
    }



    protected static int test_getProbableNodeCounts(AutoCompleteWord auto, String prefix, int manNumStems, int maxDepth, int verbose) 
    {
        List<WordTrie.NodeCount> nodeCounts = auto.getTrie().getProbableWordNodeCounts(prefix, manNumStems, maxDepth);
        if (verbose > 1) {
            if (verbose > 2)
                WordTrie.test_getWordsPartiallyMatchingPrefix(auto.mWordTrie, prefix);
            String stem = auto.getTrie().longestPrefix(prefix);
            Sx.format("AutoComplete probable completions for prefix \"%s\"[%s] (%d):\t"
                    , prefix, stem, nodeCounts.size());
        }
        if (nodeCounts == null)
            Sx.puts("[none]");
        else for (WordTrie.NodeCount nc : nodeCounts) {      
            Sx.format("%s(%d|%d|%d)  ", nc.mNode.getStem(), nc.mNode.getWordCount(), nc.mNode.getTextCount(), nc.mCount);
        }
        Sx.puts();
        return 0;
    }

    protected static int test_getGreedyProbableNodes(AutoCompleteWord auto, String prefix, int limit, int verbose) 
    {
        List<WordTrie.NodeCount> nodeCounts = auto.getTrie().getGreedyProbableWordNodes(prefix, limit);
        if (verbose > 1) {
            if (verbose > 2)
                WordTrie.test_getWordsPartiallyMatchingPrefix(auto.mWordTrie, prefix);
            String stem = auto.getTrie().longestPrefix(prefix);
            Sx.format("AutoComplete probable completions for prefix \"%s\"[%s] (%d):\t"
                    , prefix, stem, nodeCounts.size());
        }
        if (nodeCounts == null)
            Sx.puts("[none]");
        else for (WordTrie.NodeCount nc : nodeCounts) {      
            Sx.format("%s(%d|%d)  ", nc.mNode.getWord(), nc.mNode.getWordCount(), nc.mCount);
        }
        Sx.puts();
        return 0;
    }

    protected static int test_getProbable_old(AutoCompleteWord auto, String prefix, int limit, int verbose) 
    {
        List<String> words = auto.getProbable(prefix, limit);
        if (verbose > 1) {
            if (verbose > 2)
                WordTrie.test_getWordsPartiallyMatchingPrefix(auto.mWordTrie, prefix);
            String stem = auto.getTrie().longestPrefix(prefix);
            Sx.format("AutoComplete probable completions for prefix \"%s\"[%s] (%d):\t", prefix, stem, words.size());
        }
        Sx.putsIterable(words, 10, "\n");
        return 0;
    }


    @Override
    public List<String> getProbable(String prefix) {
        return getProbable(prefix, 5);
    }



    @Override
    public List<String> getProbablePrefixes(String prefix, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getProbableSuffixes(String prefix, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getProbableRepaired(String prefix, int limit) {
        // TODO Auto-generated method stub
        return null;
    }

    protected static int test_textFileToWords(int level) 
    {
        List<String> text = TextFileToStringCollection.load(new ArrayList<String>(), "convertMe.txt");
        String lines[] = new String[text.size()]; // TODO: is toArray wasteful or efficient?  Gotta be a waste...?
        return TextFilters.test_toLowerCaseLetterWords(text.toArray(lines));
    }

    /** Amount of output decreases with increasing level */
    public static int unit_test(int level) 
    {
        int minWordLen = 3;
        int maxWordLen = 16;
        Sx.puts(AutoCompleteWord.class.getName() + ".unit_test");  
        int stat = 0, maxNum = 5, maxStemDepth = 8;     

        String dictPath = FileUtil.getTextFilePath("words.txt");
        AutoCompleteWord auto = new AutoCompleteWord(dictPath, minWordLen, maxWordLen);
        WordTrie trie = auto.mWordTrie;
        String str = "sper";

        if (level > 3) {
            stat += test_textFileToWords(1);
            test_getPossible(auto, str, maxNum, 2);
            test_getProbableNodes(auto, str, maxNum, 16, 2);
            test_getProbableNodeCounts(auto, str, maxNum, maxStemDepth, 2);
        }

        String filePath = FileUtil.getTextFilePath("MobyDick.txt");
        int numNewWords = WordTrie.test_addAllWordsInTextFile(trie, minWordLen, maxWordLen, filePath, 1);
        if (level > 1) {
            test_getPossible(auto, str, maxNum, 2);
            test_getProbableNodes(auto, str, maxNum, 16, 2);
            test_getProbableNodeCounts(auto, str, maxNum, maxStemDepth, 2);
        }      

        filePath = FileUtil.getTextFilePath("Iliad.txt");
        numNewWords = WordTrie.test_addAllWordsInTextFile(trie, minWordLen, maxWordLen, filePath, 1); 
        if (level > 1) {
            test_getPossible(auto, str, maxNum, 2);
            test_getProbableNodes(auto, str, maxNum, 16, 2);
            test_getProbableNodeCounts(auto, str, maxNum, maxStemDepth, 2);
        }

        test_ux_AutoCompleteWord(auto.mWordTrie);

        // TODO: automate detection of changing result sets.

        return stat;
    }


    public static int test_ux_AutoCompleteWord(WordTrie wordTrie)
    {
        AutoCompleteWord acw = new AutoCompleteWord(wordTrie);
        String strA, strB, strC = null;
        String promptA = "Enter part of a word: ";
        String promptB = "                More? ";
        String promptC = "More, or Q to Quit... ";
        int promptLen  = promptA.length();
        int maxNumProb = 10;
        int maxWordLen = 16;
        int maxStemLen =  5;
        int maxStemAdd =  3;
        int verbose    =  1;

        while (true) {
            for (strC = null; strC == null || strC.isEmpty(); ) {
                Sx.print(promptA);
                strA = Sx.getString();
                strB = TextFilters.toLowerCaseLetters(strA);
                if (strB.isEmpty()) {
                    Sx.puts(" -- oops, the input must contain letters.");
                    continue;
                }
                strC = acw.mWordTrie.longestPrefix(strB);
                if (strC.isEmpty())
                    Sx.puts(" -- no actual word prefix found.");
            }
            Sx.printSpaces(promptLen);
            test_getProbableNodes(acw, strC, maxNumProb, maxWordLen, verbose);
            Sx.printSpaces(promptLen);
            maxStemLen = strC.length() + maxStemAdd;
            test_getProbableNodeCounts(acw, strC, maxNumProb, maxStemLen, verbose);

            Sx.print(promptB + strC);
            strA = Sx.getString();
            strB = TextFilters.toLowerCaseLetters(strA);
            strC = acw.mWordTrie.longestPrefix(strC + strB);
            Sx.printSpaces(promptLen);
            test_getProbableNodes(acw, strC, maxNumProb, maxWordLen, verbose);
            Sx.printSpaces(promptLen);
            maxStemLen = strC.length() + maxStemAdd;
            test_getProbableNodeCounts(acw, strC, maxNumProb, maxStemLen, verbose);

            Sx.print(promptC + strC);
            strA = Sx.getString();
            if (strA.length() > 0 && strA.charAt(0) == 'Q')
                break;
            strB = TextFilters.toLowerCaseLetters(strA);
            strC = acw.mWordTrie.longestPrefix(strC + strB);
            Sx.printSpaces(promptLen - 4 - strC.length());
            Sx.print(strC + " -> ");
            test_getProbableNodes(acw, strC, maxNumProb, maxWordLen, verbose);
            Sx.printSpaces(promptLen - 4 - strC.length());
            Sx.print(strC + " -> ");
            maxStemLen = strC.length() + maxStemAdd;
            test_getProbableNodeCounts(acw, strC, maxNumProb, maxStemLen, verbose);
        }

        Sx.puts("Goodbye from test_ux_AutoCompleteWord!");
        return 0;
    }

    /**
     * TODO: Sort list of WordNodes by mWordCount instead of accumulated, *relative* totalCount.
     * Or even by mTotalCount, if that is made to be the same as totalCount.
     * Then move set/list methods into WordTrie....
     * Combine exhaustive with greedy, or just take exhaustive?
     * 
     */
    public static void main(String[] args) {
        unit_test(1);
    }

}
