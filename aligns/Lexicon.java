package sprax.aligns;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.sprout.Sx;

/**
 * TODO: Separate classes for dictionary parsing and storage?
 * TODO: abstract and wrap the gets and puts, and guard against 
 * multiple entries for same key.  Merge the value sets.  Add 
 * Spanish word list to check for loan words triggering the
 * "no Spanish definition" check.
 * 
 * @author sprax
 */
public abstract class Lexicon 
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
    
    /**
     * Is first char of string upper case?
     * No error checking.
     */
    static boolean isCapitalized(String str) {
        return Character.isUpperCase(str.charAt(0));
    }
    
    /**
     * If the supplied word is a capitalized form of a word
     * whose lower-case form is in the (source/target lexicon, 
     * return the lower-case form; otherwise, return null.
     */
    String sourceUnCapitalized(String wordA)
    {
        if (isCapitalized(wordA)) {
            String uncappedA = wordA.toLowerCase();
            if (isSourceWord(uncappedA))
                return uncappedA;
        }
        return null;
    }
    String targetUnCapitalized(String wordB)
    {
        if (isCapitalized(wordB)) {
            String uncappedB = wordB.toLowerCase();
            if (isTargetWord(uncappedB))
                return uncappedB;
            String lowSingB = targetSingular(uncappedB);
            if (lowSingB != null && isTargetWord(lowSingB))
                return uncappedB;
            String lowPresB = targetPresent(uncappedB);
            if (lowPresB != null && isTargetWord(lowPresB))
                return uncappedB;
        }
        return null;
    }
    
    
   
    /**
     * If the argument is a known plural word, return the singular form; 
     * otherwise, return null.
     * TODO: Use look-up table, either loaded or memoized on the fly.
     * @param  possibly plural form of a word in the source language
     * @return singular form of the same word, if known
     */
    String defaultSingular(String word) 
    {
        int  lastIdx = word.length() - 1;
        char lastChar = word.charAt(lastIdx);
        if (lastChar == 's')
            return word.substring(0, lastIdx);
        return null;
    }
    String sourceSingular(String word)  { return defaultSingular(word); }
    String targetSingular(String word)  { return defaultSingular(word); }
    
    /**
     * If the argument is a known past tense word, 
     * return the present tense form; 
     * otherwise, return null.
     */
    String defaultUnPast(String word) 
    {
        int lastIdx = word.length() - 1;
        if (lastIdx < 3)
            return null;
        char lastChar = word.charAt(lastIdx);
        if (lastChar == 'd') {
            int punultIdx = lastIdx - 1;
            char penultChar = word.charAt(punultIdx);
            if (penultChar == 'e') {
                String sansD = word.substring(0, lastIdx);
                if (isSourceWord(sansD))
                    return sansD;
                String sansED = word.substring(0, punultIdx);
                if (isSourceWord(sansED))
                    return sansED;
            }
        }
        return null;
    }
    String sourceUnPast(String word)  { return defaultUnPast(word); }
    String targetUnPast(String word)  { return defaultUnPast(word); }
    
    /**
     * If the argument is a known present participle, 
     * return the present tense form; 
     * otherwise, return null.
     */
    String defaultUnPresentParticiple(String word) 
    {
        int lastIdx = word.length() - 1;
        if (lastIdx < 5)
            return null;
        char lastChar = word.charAt(lastIdx);
        if (lastChar == 'g' && word.endsWith("ing")) {
            String sansING = word.substring(0, lastIdx-2);
            if (isSourceWord(sansING))
                return sansING;
            
            String plusE = sansING.concat("e");
            if (isSourceWord(plusE))
                return plusE;
        }
        return null;
    }
    String sourceUnPresentParticiple(String word)  { return defaultUnPresentParticiple(word); }
    String targetUnPresentParticiple(String word)  { return defaultUnPresentParticiple(word); }
    
    
    /**
     * If the argument is a known past or future tense word, 
     * return the present tense form; 
     * otherwise, return null.
     */
    String sourcePresent(String word)
    {
      String present;
      if (null != (present = sourceUnPast(word)))
        return present;
      if (null != (present = sourceUnPresentParticiple(word)))
        return present;
      return null;
    }
    
    String targetPresent(String word)
    {
      String present;
      if (null != (present = targetUnPast(word)))
        return present;
      if (null != (present = targetUnPresentParticiple(word)))
        return present;
      return null;
    }    



    
    
    
    Lexicon(final String srcLangCode, final String dstLangCode)
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
