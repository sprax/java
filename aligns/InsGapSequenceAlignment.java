package sprax.aligns;

import java.util.ArrayList;

import sprax.arrays.ArrayFactory;
import sprax.sprout.Sx;

/** 
 * Sequence alignment with gap/multiple insert penalties
 * 
 * @author sprax
 */
public class InsGapSequenceAlignment<T> extends GlobalSequenceAlignment<T>
{
    /** Additional constants for computing string similarity: */
    public static float      SA_COST_1stDEL = -0.5F;
    public static float      SA_COST_1stINS = -0.5F;
    public static float      SA_COST_2ndDEL = -0.15F;
    public static float      SA_COST_2ndINS = -0.15F;

    
    /**
     * Alignment matrix for multiple inserts and gaps.  Used in conjunction with point
     * insertions and deletions. 
     */
    float mInsRunScore[][]  = null;    // lazy, but kept even after similarity score is found
    float mDelRunScore[][]  = null;    // lazy, but kept even after similarity score is found

    InsGapSequenceAlignment(ArrayList<T> sA, ArrayList<T> sB)  { super(sA, sB); }
    
    
    /**
     * Gap in A means insertion (new entry in B)
     * @return Cost of one point insertion
     */
    @Override
    public float scoreMatrix(char cA, T tB)
    {
        if (cA == SA_RUN)           // Gap in A means insertion in B
            return (SA_COST_1stINS + SA_COST_2ndINS);
        else // if (cA == SA_GAP)           // Gap in A means insertion in B
            return SA_COST_2ndINS;
    }
    /**
     * Gap in B means deletion from A (entry present in A but missing from B)
     * @return Cost of one point deletion
     */
    @Override
    public float scoreMatrix(T tA, char cB)
    {
        if (cB == SA_RUN)           // Gap run in B means deletions from A
            return (SA_COST_1stDEL + SA_COST_2ndDEL);
        else // if (cB == SA_GAP)           // Gap in B means deletion from A
            return SA_COST_2ndDEL;
    }
    
    
    @Override
    protected float findSimilarityScore()
    {
        mInsDelScore = ArrayFactory.makeFloatArray(mRows, mCols);
        mBackDirects = ArrayFactory.makeCharArray(mRows, mCols);
        for (int row = 0; row < mRows; row++)
            mBackDirects[row][0] = SA_UP;
        for (int col = 1; col < mCols; col++)
            mBackDirects[0][col] = SA_LEFT;
        
        mInsRunScore = ArrayFactory.makeFloatArray(mRows, mCols);
        mDelRunScore = ArrayFactory.makeFloatArray(mRows, mCols);
                
        if (sDbg > 3) 
            Sx.putsArray("findSimilarityScore : mInsDelScore\n", mSeqB, 6);
        for (int row = 1; row < mRows; row++) {
            char dir;
            T chB, chA = mSeqA.get(row-1);
            float sAB, sDn, sAc, sDg;
            for (int  col = 1; col < mCols; col++) {
                
                // Max between [the score from alignment of row-length prefix of seq A with the col-length 
                // prefix of seqB ending with a 1-place deletion from seq A (gap in seq B); i.e. extending the gap]
                // and [the score from initiating a gap
                sDn = Math.max( mDelRunScore[row-1][col] + scoreMatrix(chA, SA_GAP)    // extending gap in B
                              , mInsDelScore[row-1][col] + scoreMatrix(chA, SA_RUN) ); // starting a gap in B

                sAc = Math.max( mInsRunScore[row][col-1] + scoreMatrix(chA, SA_GAP)    // extending gap in A
                              , mInsDelScore[row][col-1] + scoreMatrix(chA, SA_RUN) ); // starting a gap in A

                // Set overall score and back direction depending on which is greater: scoreDown or scoreAcross.
                if (sDn < sAc) {
                    sAB = sAc;
                    dir = SA_LEFT;
                } else {
                    sAB = sDn;
                    dir = SA_UP;
                }
                
                chB = mSeqB.get(col-1);
                sDg = mInsDelScore[row-1][col-1] + scoreMatrix(chA, chB); // pos for match, neg for mismatch
                if (sAB < sDg) {
                    sAB = sDg;
                    dir = SA_DIAG;
                }
                
                mDelRunScore[row][col] = sDn;
                mInsRunScore[row][col] = sAc;
                mInsDelScore[row][col] = sAB;
                
                mBackDirects[row][col] = dir;                    
            }
            if (sDbg > 3) 
                Sx.putsSubArray("  " + mSeqA.get(row-1), mInsDelScore[row], 1, mInsDelScore[row].length);
        }
        mSimilarityScore = mInsDelScore[mRows-1][mCols-1];
        return mSimilarityScore;
    }
    

    
    /************************************************************************
     * test_IGSA
     */
    public static int test_IGSA(int verbose, String strA, String strB)
    {
        ArrayList<Character> seqA = createCharacterArrayList(strA);
        ArrayList<Character> seqB = createCharacterArrayList(strB);
        InsGapSequenceAlignment<Character> alignment = new 
        InsGapSequenceAlignment<Character>(seqA, seqB);
        alignment.test_alignment(verbose);
        Sx.puts();
        return 0;
    }
    

    /************************************************************************
     * unit_test
     */
    public static int unit_test(int verbose)
    {
        if (verbose > 2) {
            test_IGSA(verbose, "ATCTGAT", "ATCTGAT");
            test_IGSA(verbose, "ATCTGAT", "TGCATA");
            test_IGSA(verbose, "TGCATA", "ATCTGAT");
            Sx.puts("=========================================");
            test_IGSA(verbose, "Well... Do not get mad, get even!", "Don't get mud, get cleaner?");
            test_IGSA(verbose, "AAGAAAGAAAAGAA", "AAGAAGAAAGAA");
            test_IGSA(verbose, "AAGAAAGAAAAGAA", "AAGAAGGAAAGAA");
            test_IGSA(verbose, "AAGAAAGAAAAGAA", "AAGAAGGAAAGAAA");
            test_IGSA(verbose, "abcDEFdefJKLqr", "abcJKLabcPQRdefMNOPq");
            test_IGSA(verbose, "ABcDEfGH", "ABfDEcGH");
            
            test_IGSA(verbose, "ABcDEfGH", "BfDEcGH");
        }
        
        test_IGSA(10, "AB", "B");
        return 0;
    }
    
    public static void main(String[] args) {  unit_test(2); }
}

