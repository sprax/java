package sprax.tries;

import java.util.ArrayList;
import java.util.List;

import sprax.files.FileUtil;
import sprax.files.TextFileToStringCollection;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class AutoCompleter implements AutoCompleteInterface
{
    final protected String   mDictionaryPath;
    protected WordTrie sWordTrie = new WordTrie();

    int mMinWordLen;
    int mMaxWordLen;
    int mMaxFoundLen;

    AutoCompleter(String dictionaryFile, int minWordLen, int maxWordLen) 
    {
        mDictionaryPath = FileUtil.getTextFilePath(dictionaryFile);
        mMinWordLen = minWordLen;
        mMaxWordLen = maxWordLen;
        initWordsFromDictionaryFile(mDictionaryPath, mMinWordLen, mMaxWordLen);
    }

    public WordTrie getTrie()  { return sWordTrie; }

    public void initWordsFromDictionaryFile(String dictionaryPath, int minWordLen, int maxWordLen)
    {
        sWordTrie.initFromSortedDictionaryFile(dictionaryPath, minWordLen, maxWordLen, 1);
    }

    @Override
    public List<String> getPossible(String prefix) {
        return  sWordTrie.getWordsPartiallyMatchingPrefix(prefix);
    }

    @Override
    public List<String> getPossible(String prefix, int limit) {
        List<String> words = sWordTrie.getWordsPartiallyMatchingPrefix(prefix);
        int count =  words.size();
        if (count > limit)
            return words.subList(0, limit);
        return words;
    }

    protected static int test_getPossible(AutoCompleter auto, String prefix, int limit) 
    {
        String stem = auto.getTrie().longestPrefix(prefix);
        List<String> words = auto.getPossible(prefix, limit);
        System.out.format("AutoComplete possible completions for prefix \"%s\"[%s] (%d):\t", prefix, stem, words.size());
        Sx.putsIterable(words, 8);
        return 0;
    }


    @Override
    public List<String> getProbable(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getProbable(String prefix, int limit) {
        // TODO Auto-generated method stub
        return null;
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

    public static int unit_test(int level) 
    {
        String testName = AutoCompleter.class.getName() + ".unit_test";
        Sz.begin(testName);

        int numWrong = 0;
        AutoCompleter auto = new AutoCompleter("words.txt", 3, 11);
        WordTrie trie = auto.sWordTrie;  

        String str = "expiration";
        Sx.puts(str + ": " + trie.contains(str));

        str = "filter";
        Sx.puts(str + ": " + trie.contains(str));

        str = "";
        Sx.puts("<empty> : " + trie.contains(str));

        str = "drin";
        WordTrie.test_getWordsPartiallyMatchingPrefix(trie, str);
        test_getPossible(auto, str, 5);

        String filePath = FileUtil.getTextFilePath("mobydick00.txt");
        List<String> moby = TextFileToStringCollection.load(new ArrayList<String>(), filePath);
        Sx.putsIterable(moby, 8);

        if (level > 0) {
        }

        Sz.end(testName, numWrong);
        return numWrong;
    }    
    public static void main(String[] args) {
        unit_test(1);
    }

}
