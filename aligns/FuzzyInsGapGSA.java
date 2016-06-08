package sprax.aligns;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * FIXME: Where's the multi-insert/multi-gap functionality?
 * TODO: The comparisons are not yet all that fuzzy!
 * @author sprax
 *
 * @param <T>
 * @param <U>
 */
public class FuzzyInsGapGSA<T, U extends Comparator<T>> extends InsGapSequenceAlignment<T>
{   
    U mDifferencer;
    FuzzyInsGapGSA(ArrayList<T> sA, ArrayList<T> sB, U differencer) { 
        super(sA, sB);
        mDifferencer = differencer;
    }
    
    public static int test_FGSA(int verbose, CharacterDifference charDiff, String strA, String strB)
    {
        ArrayList<Character> seqA = createCharacterArrayList(strA);
        ArrayList<Character> seqB = createCharacterArrayList(strB);
        FuzzyGSA<Character, CharacterDifference> align = new 
        FuzzyGSA<Character, CharacterDifference>(seqA, seqB, charDiff);
        align.test_alignment(verbose);
        return 0;
    }
    
    /************************************************************************
     * unit_test
     */
    public static int unit_test(int verbose)
    {
        CharacterDifference charDiff = new CharacterDifference();
        FuzzyGSA.test_FGSA(verbose, charDiff, "Well... Do not get mad, get even!", "Don't get mud, get cleaner?");
        FuzzyGSA.test_FGSA(verbose, charDiff, "ABCDABCJABCKABDM", "ABCL");
        
        // Second string has no J, but its K matches J in first string.
        // First string has no L, but its second M matches L in second string.
        FuzzyGSA.test_FGSA(verbose, charDiff, "ABCIABCJMABCMPXYZ", "ABCKABCL");
        FuzzyGSA.test_FGSA(verbose, charDiff, "abcDEFdefJKLqr", "abcJKLabcPQRdefMNOPq");

        return 0;
    }
    
    public static void main(String[] args) {  unit_test(2); }    
}
