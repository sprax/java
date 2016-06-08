package sprax.aligns;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import sprax.Sx;

/** =========================================================================
 * Word differences for English-English proof-of-concept testing.
 */
class WordDifferenceEnToy extends WordDifference
{
    WordDifferenceEnToy()
    {
        super(LexiconEnToy.getInstance());
    }
}

public class LexiconEnToy extends Lexicon
{    
    private static final LexiconEnToy sInstance = new LexiconEnToy();
    private static TreeMap<String, Set<String>> sWordsToSynonyms;
    public static LexiconEnToy getInstance()
    {
        return sInstance;
    }
    private LexiconEnToy()
    {
        super("En", "En");
        mTitle = "Toy English Lexicon";
        mDict_1_1 = sWordsToSynonyms = getToyMap();
    }
    
    static TreeMap<String, Set<String>> getToyMap()
    {
        if (sWordsToSynonyms != null)
            return sWordsToSynonyms;
        
        sWordsToSynonyms = new TreeMap<String, Set<String>>();
        
        String key = "intense";
        HashSet<String> set = new HashSet<String>();
        set.add("mighty");
        set.add("powerful");
        set.add("strange");
        set.add("strong");
        set.add("wicked");
        sWordsToSynonyms.put(key, set);
        
        key = "saw";
        set = new HashSet<String>();
        set.add("could see");
        set.add("did see");
        set.add("had seen");
        set.add("have seen");
        sWordsToSynonyms.put(key, set);
        
        key = "by";
        set = new HashSet<String>();
        set.add("through");
        sWordsToSynonyms.put(key, set);
        
        key = "enjoyment";
        set = new HashSet<String>();
        set.add("comfort");
        set.add("dissipation");
        set.add("ease");
        set.add("pleasure");
        set.add("gusto");
        set.add("luxuriousness");
        set.add("sensuality");
        set.add("titillation");
        sWordsToSynonyms.put(key, set);
        
        key = "found";
        set = new HashSet<String>();
        set.add("derived");
        sWordsToSynonyms.put(key, set);
        
        key = "led";
        set = new HashSet<String>();
        set.add("carried");
        sWordsToSynonyms.put(key, set);
        
        key = "pleasant";
        set = new HashSet<String>();
        set.add("delightful");
        sWordsToSynonyms.put(key, set);
        
        key = "fancies";
        set = new HashSet<String>();
        set.add("thoughts");
        set.add("ideas");
        sWordsToSynonyms.put(key, set);
        
        key = "execution";
        set = new HashSet<String>();
        set.add("action");
        set.add("performance");
        set.add("practice");
        set.add("realization");
        sWordsToSynonyms.put(key, set);
        
        key = "quick";
        set = new HashSet<String>();
        set.add("fleeting");
        sWordsToSynonyms.put(key, set);
      
        key = "brown";
        set = new HashSet<String>();
        set.add("brunette");
        set.add("russet");
        sWordsToSynonyms.put(key, set);
     
        key = "jump";
        set = new HashSet<String>();
        set.add("leap");
        sWordsToSynonyms.put(key, set);
      
        key = "over";
        set = new HashSet<String>();
        set.add("above");
        set.add("beyond");
        sWordsToSynonyms.put(key, set);
      
        key = "fox";
        set = new HashSet<String>();
        set.add("trick");
        set.add("con");
        sWordsToSynonyms.put(key, set);
      
        key = "trick";
        set = new HashSet<String>();
        set.add("fool");
        set.add("cheat");
        sWordsToSynonyms.put(key, set);
      
        key = "black";
        set = new HashSet<String>();
        set.add("dark");
        set.add("midnight");
        sWordsToSynonyms.put(key, set);
      
        key = "dog";
        set = new HashSet<String>();
        set.add("harass");
        set.add("plague");
        sWordsToSynonyms.put(key, set);

        key = "lazy";
        set = new HashSet<String>();
        set.add("indolent");
        set.add("lethargic");
        sWordsToSynonyms.put(key, set);
      
        
        return sWordsToSynonyms;
    }
    
    
    public static int unit_test(int level) 
    {
        String  testName = LexiconEnToy.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        String strA = "The quick    brown  fox  jumped over   the lazy     black    dog.";
        String strB = "The fleeting russet fool leaps  beyond the indolent midnight plague.";
        
        Sentence sntA = new Sentence(strA);
        Sentence sntB = new Sentence(strB);
        WordDifference wordDiff = new WordDifferenceEnToy();
        SentencesAlignment.test_sentence_pair(sntA, sntB, wordDiff, level);
        
        
        
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args) { unit_test(2); }
    
}
