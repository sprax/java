package sprax.aligns;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;


/**
 * Provide a measure of difference between any word in a source Lexicon
 * and any word in a target Lexicon.  If the source and target Lexicons
 * are the same, the two words are assumed to be in the same language.
 * @author sprax
 *
 */
public class WordDifference implements Comparator<Word>
{
    final Lexicon mLex;
    final Map<String, Set<String>> mSrcToDstSynonyms;
    
    WordDifference(Lexicon lex)
    { 
        mLex = lex;
        mSrcToDstSynonyms = mLex.mDict_1_1;
    }
    
    
    /**
     * Compare wordB to wordA or any synonym of wordA.
     * Precedence order:
     * 1)  Literal match of wordB to synonyms of wordA, i.e., is wordB in the synSet of wordA?
     * 2)  If wordA is capitalized, and lowerCase(A) is in the lexicon, 
     *     try literal match of wordB to synSet(lower-case(wordA))
     * 3)  If wordA and/or wordB appears to be plural in a plural form, try un-pluralizing them.
     * 3a) But don't try pluralizing singular forms.
     * 4)  If wordA and wordB appear to be inflected verbs, try comparing their roots (stemming)
     * 5)  (Last resort) Literal match of word string B to word string A
     * 
     */
    @Override
    public int compare(Word wA, Word wB)
    {
        String strA = wA.getString();
        String strB = wB.getString();
        Set<String> synA = mSrcToDstSynonyms.get(strA);
        
        // Match wordB against synonyms of wordA as-is. 
        if (synA != null && synA.contains(strB))
            return 0;
            
        // Try lower-case, singular, and singular-lower-case forms if warranted.
        String uncappedB = mLex.targetUnCapitalized(strB);
        String singularB = mLex.targetSingular(strB);
        String singLowB = null;
        if (singularB != null && uncappedB != null)
            singLowB = singularB.toLowerCase();
        String presentB = mLex.targetPresent(uncappedB != null ? uncappedB : strB);
        
        if (synA != null) { 
            if (       uncappedB != null && synA.contains(uncappedB)
                    || singularB != null && synA.contains(singularB)
                    || singLowB != null && synA.contains(singLowB)
                    || presentB != null && synA.contains(presentB))
                return 20;
        }
        
        // Match against synonyms of lower-cased wordA, if it was capitalized. 
        String uncappedA = mLex.sourceUnCapitalized(strA);      
        if (uncappedA != null) {
            synA = mSrcToDstSynonyms.get(uncappedA);
            if (synA != null) {
                if (synA.contains(strB)
                        || uncappedB != null && synA.contains(uncappedB)
                        || singularB != null && synA.contains(singularB)
                        || singLowB  != null && synA.contains(singLowB)
                        || presentB  != null && synA.contains(presentB)) {
                    return 25;
                }
            }
        }
            
        // Match against synonyms of singular form of wordA, if it was plural. 
        String singularA = mLex.sourceSingular(strA);
        if (singularA != null) {
            synA = mSrcToDstSynonyms.get(singularA);
            if (synA != null) {
                if (synA.contains(strB)
                        || uncappedB != null && synA.contains(uncappedB)
                        || singularB != null && synA.contains(singularB)
                        || singLowB  != null && synA.contains(singLowB)
                        || presentB  != null && synA.contains(presentB)) {
                    return 30;
                }
            }
               
            // Match against synonyms of uncapitalized, singular form of wordA, 
            // if it was both capitalized and plural. 
            if (uncappedA != null) {
                String singLowA = singularA.toLowerCase();
                synA = mSrcToDstSynonyms.get(singLowA);
                if (synA != null) {
                    if (synA.contains(strB)
                            || uncappedB != null && synA.contains(uncappedB)
                            || singularB != null && synA.contains(singularB)
                            || singLowB  != null && synA.contains(singLowB)
                            || presentB  != null && synA.contains(presentB)) {
                        return 35;
                    }
                }
            }
        }
        
        // Try converting inflected verbs and participles to present tense "roots"
        String presentA = mLex.sourcePresent(strA);
        if (presentA != null) {
            synA = mSrcToDstSynonyms.get(presentA);
            if (synA != null) {
                if (synA.contains(strB)
                        || uncappedB != null && synA.contains(uncappedB)
                        || singularB != null && synA.contains(singularB)
                        || singLowB  != null && synA.contains(singLowB)
                        || presentB  != null && synA.contains(presentB)) {
                    return 40;
                }
            }
        }
        
        // Literal match?  This is more germane if wordA and wordB are
        // in same language, but might be OK as a last resort.
        // Pros: it will match numbers, untranslated loan words, code-switches, 
        //       and other non-sense.
        // Cons: False matches of cross-lingual homonyms, such as:
        //       "a" (article in English, preposition in Spanish)
        //       "manger" (barn in English vs. to eat in French)
        if (strA.compareToIgnoreCase(strB) == 0)
            return 0;
        
        // No match.
        return 100;      
    }
    

        
        /************************************************************************
         * unit_test
         */
        public static int unit_test(int verbose)
        {
            SentencesAlignment.unit_test(2);
            return 0;
        }    
        
        public static void main(String[] args) { unit_test(2); }
    }
    
    /** =========================================================================
     * Word differences for English-Spanish translation
     */
    class WordDifferenceEnEs extends WordDifference
    {
        static Lexicon mLexEnEs = new LexiconEnEs(); 
        WordDifferenceEnEs()
        {
            super(mLexEnEs);
        }
    }
    
   
