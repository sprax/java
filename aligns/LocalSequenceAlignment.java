package sprax.aligns;

import java.util.ArrayList;

import sprax.Sx;
import sprax.arrays.ArrayFactory;
import sprax.sprout.Spaces;

public class LocalSequenceAlignment<T> extends GlobalSequenceAlignment<T>
{
    protected int mRow = -1;
    protected int mCol = -1;

    LocalSequenceAlignment(ArrayList<T> sA, ArrayList<T> sB) { super(sA, sB); }
    
    @Override
    protected float findSimilarityScore()
    {
        mInsDelScore = ArrayFactory.makeFloatArray(mRows, mCols);
        mBackDirects = ArrayFactory.makeCharArray(mRows, mCols);
        for (int row = 0; row < mRows; row++)
            mBackDirects[row][0] = SA_UP;
        for (int col = 1; col < mCols; col++)
            mBackDirects[0][col] = SA_LEFT;
        
        /*  For C, not Java (which initializes arrays to all 0s): */
        //  for (int row = 0; row < mRows; row++)
        //      mLcsLengths[row][0] = 0;
        //  for (int col = 1; col < mCols; col++)
        //      mLcsLengths[0][col] = 0;
        
        if (sDbg > 3) 
            Sx.putsArray("\n", mSeqB, 6);
        mSimilarityScore = 0;
        for (int row = 1; row < mRows; row++) {
            T tA = mSeqA.get(row-1);
            for (int  col = 1; col < mCols; col++) {
                float sAB = 0;
                char  dir = SA_DIAG; // FIXME SA_ZERO;
                float scr = mInsDelScore[row-1][col] + scoreMatrix(tA, SA_GAP); // gap in B (deletion from A)
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_UP;
                }
                T  tB = mSeqB.get(col-1);
                scr       = mInsDelScore[row][col-1] + scoreMatrix(SA_GAP, tB); // gap in A (insertion in B)
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_LEFT;
                }
                scr     = mInsDelScore[row-1][col-1] + scoreMatrix(tA, tB); // pos for match, neg for mismatch
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_DIAG;
                }
                if (mSimilarityScore < sAB) {
                    mSimilarityScore = sAB;
                    mRow = row;
                    mCol = col;
                    
                }
                mInsDelScore[row][col] = sAB;
                mBackDirects[row][col] = dir;                    
            }
            if (sDbg > 3) 
                Sx.putsSubArray("  " + mSeqA.get(row-1), mInsDelScore[row], 1, mInsDelScore[row].length);
        }
        return mSimilarityScore;
    }


    /************************************************************************
     * Restricted edit distance: only insertions and deletions (ID) are allowed.
     */
    @Override
    public int findEditDistanceID()
    {
        if (mEditDistance < 0) {
            mEditDistID = ArrayFactory.makeIntArray(mRows, mCols);
            for (int row = 0; row < mRows; row++)
                mEditDistID[row][0] = row;
            for (int col = 1; col < mCols; col++)
                mEditDistID[0][col] = col;
            for (int row = 1; row < mRows; row++) {
                T tA = mSeqA.get(row-1);
                for (int col = 1; col < mCols; col++) {
                    int dAB = 9990;
                    int tmp = mEditDistID[row-1][col] + ED_COST_DELETE;
                    if (dAB > tmp) {
                        dAB = tmp;
                    }
                    tmp = mEditDistID[row][col-1] + ED_COST_INSERT;
                    if (dAB > tmp) {
                        dAB = tmp;
                    }
                    T tB = mSeqB.get(col-1);
                    if (tA == tB) {
                        tmp = mEditDistID[row-1][col-1] + ED_COST_MATCH;
                        if (dAB > tmp) {
                            dAB = tmp;
                        }
                    }
                    mEditDistID[row][col] = dAB;                   
                    
                    if (sDbg > 2) 
                        Sx.putsSubArray(mSeqA.get(row-1) + " ", mEditDistID[row], 1, mEditDistID[row].length);
                }
            }
            mEditDistance = mEditDistID[mRows-1][mCols-1];
        }
        return mEditDistance;
    }
    
    
    public float findEditDistanceFromScore()
    {
        return Math.min(mRows, mCols) - 1 - getSimilarityScore();
    }  
    
    public static int test_LSA( int verbose, String strA, String strB)
    {
        ArrayList<Character> seqA = createCharacterArrayList(strA);
        ArrayList<Character> seqB = createCharacterArrayList(strB);
        LocalSequenceAlignment<Character> loc = new LocalSequenceAlignment<Character>(seqA, seqB);
        loc.test_alignment(verbose);
        Sx.format("Max similarity at row, col: %d %d; local alignment %d-%d\n"
                , loc.mRow, loc.mCol, loc.mRow-1, loc.mCol-1);
        if (loc.mRow < loc.mCol) {
            Sx.print(Spaces.get(loc.mCol - loc.mRow));
        }
        Sx.puts(strA);
        if (loc.mRow > loc.mCol) {
            Sx.print(Spaces.get(loc.mRow - loc.mCol));
        }
        Sx.puts(strB);
        Sx.puts();
        return 0;
    }


    /************************************************************************
     * unit_test
     */
    public static int unit_test(int verbose)
    {
//        test_LSA("ATCTGAT", "ATCTGAT", 0);
//        test_LSA("ATCTGAT", "TGCATA", 0);
//        
//        test_GSA("Well... Do not get mad, get even!", "Don't get mud, get cleaner?", 0);
//        test_LSA("Well... Do not get mad, get even!", "Don't get mud, get cleaner?", 0);        
        
        test_GSA(verbose, "ABCD", "DEFG");
        test_LSA(verbose, "ABCD", "DEFG");
        test_LSA(verbose, "Well... Do not get mad, get even!", "Don't get mud, get cleaner?");
        test_LSA(verbose, "abcDEFdefJKLqr", "abcJKLabcPQRdefMNOPq");
        test_LSA(verbose, "abcDEFxyzJKL", "abcJKLxyzDEF");


        
        return 0;
    }
    
    public static void main(String[] args) {  unit_test(2); }
}

