package sprax.aligns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import sprax.Sx;
import sprax.files.FileUtil;
import sprax.files.StringCollection;
import sprax.files.TextFileReader;


class WordSet extends StringCollection<TreeSet<String>> 
{
    public  WordSet() {
        super(new TreeSet<String>());
    }
    
    @Override  // Return true IFF the set did not already contain this string
    public boolean addString(String str) {
        String string = str.trim();
        if (string.length() != 0)
            return mCollector.add(string);           
        return false;
    }
}

public 
/**
 * TODO: Separate classes for dictionary parsing and storage?
 * TODO: abstract and wrap the gets and puts, and guard against 
 * multiple entries for same key.  Merge the value sets.  Add 
 * Spanish word list to check for loan words triggering the
 * "no Spanish definition" check.
 * 
 * @author sprax
 */
abstract class DictionaryBiLingual 
{
    String mTitle;

    final String    mSrcLanguageCode;
    final String    mDstLanguageCode;
    final String    mSourceLanguage; 
    final String    mTargetLanguage;
    
    TreeMap<String, Set<String>> mDict_1_1;
    TreeMap<String, Set<String>> mDict_2_1;
    TreeMap<String, Set<String>> mDict_3_1;
//    HashMap<String, ArrayList<String>> mDict_1_2;
//    HashMap<String, ArrayList<String>> mDict_1_3;
//    HashMap<String, ArrayList<String>> mDict_2_2;
//    HashMap<String, ArrayList<String>> mDict_3_3;
//    HashMap<String, ArrayList<String>> mDict_N_N;
    
    TreeMap<String, String> mPastTenseToRoot;
    
    String getSrcCode() { return mSrcLanguageCode; }
    String getDstCode() { return mDstLanguageCode; }
    String getTitle()   { return mTitle; }
    
    static int      sDbg = 2;
    static char     sSepTermsDefns      = ':';
    static char     sSepDefinitions     = ';';
    final  String   mFsSourceWordList;
    final  String   mFsTargetWordList;
    static WordSet  sSourceWordSet;
    static WordSet  sTargetWordSet;
    
    DictionaryBiLingual(final String srcLangCode, final String dstLangCode)
    {
        mSrcLanguageCode = srcLangCode;
        mDstLanguageCode = dstLangCode;
        mSourceLanguage  = languageNameFromCode(srcLangCode);
        mTargetLanguage  = languageNameFromCode(dstLangCode);
        
        mFsSourceWordList = FileUtil.getTextFilePath(mSrcLanguageCode + "/" + mSrcLanguageCode + "NamesWords.txt");
        mFsTargetWordList = FileUtil.getTextFilePath(mDstLanguageCode + "/" + mDstLanguageCode + "NamesWords.txt");
        
        mPastTenseToRoot = new TreeMap<String, String>();
    }
    
    /**
     * Get language name from 2-letter code
     * TODO: Use enum const strings
     */
    static String languageNameFromCode(final String code)
    {
        if (code.equals("En"))
            return "English";
        if (code.equals("Es"))
            return "Spanish";
        return "unsupported language";
    }

    boolean isSourceWord(String word) { return getSourceWordSet().contains(word); }
    boolean isTargetWord(String word) { return getTargetWordSet().contains(word); }
    
    WordSet getSourceWordSet()
    {
        if (sSourceWordSet == null)
            sSourceWordSet = readWordList(mFsSourceWordList, mSourceLanguage);
        return sSourceWordSet;
    }
    WordSet getTargetWordSet()
    {
        if (sTargetWordSet == null)
            sTargetWordSet = readWordList(mFsTargetWordList, mTargetLanguage);
        return sTargetWordSet;
    }    
    static WordSet readWordList(String textFileSpec, String languageName)
    {
        WordSet wordSet = new WordSet();
        TextFileReader tfr = new TextFileReader(textFileSpec);
        Sx.debug(2, "Reading %s words from file: %s, ", languageName, textFileSpec);
        int numWords = tfr.readIntoStringCollector(wordSet);
        if (numWords == 0) {
            throw new IllegalStateException("Invalid "+languageName+" word list file: "+textFileSpec);
        } else {
            Sx.debug(2, "found %6d entries.\n", numWords);
            if (sDbg > 8) {
                for (String word : wordSet.getCollector() )
                    Sx.puts(word);
            }
        }
        return wordSet;
    }

    static void compareWordListFiles(String wordListFileA, String wordListFileB)
    {
        HashSet<String> wordsA = TextFileReader.readFileIntoHashSet(wordListFileA);
        HashSet<String> wordsB = TextFileReader.readFileIntoHashSet(wordListFileB);
        compareWordLists(wordsA, wordsB);
    }
    
    // TODO: make symmetric
    static void compareWordLists(Collection<String> wordsA, Collection<String> wordsB)
    {
        for (String wordB : wordsB) {
            if ( ! wordsA.contains(wordB))
                Sx.puts(wordB);
        }
    }
        
    void compareWordListFilesWithSource(String wordListFileA, String wordListFileB)
    {
        HashSet<String> wordsA = TextFileReader.readFileIntoHashSet(wordListFileA);
        HashSet<String> wordsB = TextFileReader.readFileIntoHashSet(wordListFileB);
        compareWordLists(wordsA, wordsB, getSourceWordSet().getCollector());
    }
    
    // TODO: make symmetric
    static void compareWordLists(Collection<String> wordsA, Collection<String> wordsB, Collection<String> wordsC)
    {
        for (String wordB : wordsB) {
            if ( ! wordsA.contains(wordB) &&  ! wordsC.contains(wordB))
                Sx.puts(wordB);
        }
    }    
    
    public static void main(String[] args) { DictionaryRawFileParserEnEs.unit_test(2); }
    
}
