package sprax.aligns;

import java.util.ArrayList;
import java.util.Comparator;

interface Differable<U> {
    public int minus(Differable<U> d);
}

class CharacterDifference implements Comparator<Character>
{
    @Override
    public int compare(Character cA, Character cB) {
        return cA - cB;
    }
}

/**
 * Fuzzy Global Sequence Alignment is fuzzy because instead of only testing
 * sequence elements for equality, the scoreMatrix function compares their
 * values and returns a score between -1 and 1.
 * 
 * @author sprax
 * @param <T>   Type T
 * @param <U>   Comparator for type T
 */
public class FuzzyGSA<T, U extends Comparator<T>> extends GlobalSequenceAlignment<T>
{
    U mDifferencer;
    FuzzyGSA(ArrayList<T> sA, ArrayList<T> sB, U differ) { 
        super(sA, sB);
        mDifferencer = differ;
    }
    
    /**
     * Scoring Matrix Function generally rewards character matches and penalizes differences as 
     * well as insertions and deletions (a.k.a. indels). 
     */
    @Override
    public float scoreMatrix(T cA, T cB) 
    {
        if (cA.equals(cB))          // Object identify as defined by type T
            return SA_COST_MATCH;   // Point identity (usual "cost" is +1)
        
        int df = mDifferencer.compare(cA, cB);
        if (df < 0)
            df = -df;                // Use absolute difference
        switch(df) {
            case 0:                 // Only possible if compare is inconsistent with equals
                return  0.80F;
            case 1: 
                return -0.25F;
            case 2: 
                return -0.50F;
            case 3: 
                return -0.75F;
            default: 
                return -1;
        }
    }
	
	@Override
    public char getComparisonChar(IndexPair ip, String strA, String strB)
    {
	    if (ip.mIdxA < 0 || ip.mIdxB < 0)
            return ' ';
	    
	    int cmp = strA.compareTo(strB);
	    if (cmp < 0)
	        return '<';
	    else if (cmp > 0)
	        return '>';
	    
        return '=';
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
        return 0;
    }
    
    public static void main(String[] args) {  unit_test(3); }    
}
