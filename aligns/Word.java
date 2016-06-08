package sprax.aligns;

import java.util.ArrayList;
import java.util.Comparator;


import sprax.Sx;

/**
 * Representation of a single word.
 * TODO:    Word-tuples: digram, trigram, tetragram, pentagram, n-gram, 
 *      OR, better: words2, words3, words4, words5.
 *  
 * @author sprax
 *
 */
public class Word extends TextPart
{
    public Word(final String str) {
        mString = str;
    }
    @Override
    public String toString() {
        return mString;
    }
    @Override
    public int getNumWords()    { return 1; }
    @Override
    public int getNumParts()    { return 1; }
    @Override
    public void putsParts() {
        Sx.puts(mString);
    }
}

class WordStringDifference implements Comparator<String>
{
    @Override
    public int compare(String sA, String sB) {
        return sA.compareToIgnoreCase(sB);
    }
}

//class WordsAlignment extends FuzzyGSA<Word, WordDifference>
//class WordsAlignment extends GlobalSequenceAlignment<Word>
//class WordsAlignment extends FuzzyGSA<Word, WordDifference>
class WordsAlignment extends FuzzyInsGapGSA<Word, WordDifference>
{
    WordsAlignment(ArrayList<Word> sA, ArrayList<Word> sB, WordDifference wD)  { super(sA, sB, wD); }
    //WordsAlignment(ArrayList<Word> sA, ArrayList<Word> sB, WordDifference wD)  { super(sA, sB); }
    
    @Override
    public float scoreMatrix(Word wA, Word wB) 
    {
        int df = mDifferencer.compare(wA, wB) / 100;
        if (df == 0) {
            String sA = wA.getString();
            String sB = wB.getString();
            char   cA = sA.charAt(0);
            char   cB = sB.charAt(0);
            if (Character.isUpperCase(cA) && Character.isUpperCase(cB))
                return SA_COST_MATCH * 2.5F;
            return SA_COST_MATCH;
        } else {
            return SA_COST_MISMAT;
        }
    }
    
    @Override
    public char getComparisonChar(IndexPair ip, String strA, String strB)
    {
        if (ip.mIdxA < 0 || ip.mIdxB < 0)
            return ' ';
        return '=';
    }
    
    /************************************************************************
     * unit_test
     * 
     */
    public static int unit_test(int verbose)
    {
//        test_GSA(verbose, "ATCTGAT", "ATCTGAT");
//        test_GSA(verbose, "ATCTGAT", "TGCATA");
//        test_GSA(verbose, "Well... Do not get mad, get even!", "Don't get mud, get cleaner?");
//        test_GSA(verbose, "abcDEFdefJKLqr", "abcJKLabcPQRdefMNOPq");
//        test_GSA(verbose, "abcDEFxyz0JKL", "abcJKLxyz0DEF");
//        test_GSA(verbose, "ABcDEfGH", "ABfDEcGH");
        test_GSA(verbose, "ABnDpEoGH", "ABoDEqGHn");
        SentencesAlignment.unit_test(2);

        return 0;
    }    
    
    public static void main(String[] args) { unit_test(2); }
}
