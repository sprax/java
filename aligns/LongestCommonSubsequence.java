package sprax.aligns;

import sprax.arrays.ArrayFactory;
import sprax.sprout.Sx;

public class LongestCommonSubsequence 
{
//    public enum Dir { UP, LEFT, DIAG };
//    static ArrayFactory<Dir> mArrayFactory = new ArrayFactory<Dir>();
    public static final char LCS_UP   = 'U';
    public static final char LCS_LEFT = 'L';
    public static final char LCS_DIAG = 'D';
    
    final String mStrA;
    final String mStrB;
    final int    mRows;
    final int    mCols;
    int  mLcsLengths[][] = null;    // eager: kept even after LCS is found
    char mBacktracks[][] = null;    // eager
    int  mEditDistID[][] = null;    // lazy
    StringBuffer mLcsStr = null;    // lazy: found on demand
    int  mEditDistance   = -1;      // lazy: computed on demand (DP array)
    
    LongestCommonSubsequence(String sa, String sb)
    {
        if (sa == null || (mRows = 1+sa.length()) == 1 || sb == null || (mCols = 1+sb.length()) == 1)
            throw new IllegalArgumentException(LongestCommonSubsequence.class.getName() +"("+sa+", "+sb+")");
        mStrA = sa;
        mStrB = sb;
    }
    
    public void initLcsArrays()
    {
        mLcsLengths = ArrayFactory.makeIntArray(mRows, mCols);
        mBacktracks = ArrayFactory.makeCharArray(mRows, mCols);
        
        /*  For C, not Java (which initializes arrays to all 0s): */
        //  for (int row = 0; row < mRows; row++)
        //      mLcsLengths[row][0] = 0;
        //  for (int col = 1; col < mCols; col++)
        //      mLcsLengths[0][col] = 0;

        Sx.putsArray("\n* ", mStrB.toCharArray(), 2);
        for (int row = 1; row < mRows; row++) {
            char cA = mStrA.charAt(row-1);
            for (int col = 1; col < mCols; col++) {
                int sAB = mLcsLengths[row-1][col];
                char dir = LCS_UP;
                if (sAB < mLcsLengths[row][col-1]) {
                    sAB = mLcsLengths[row][col-1];
                    dir = LCS_LEFT;
                }
                char cB = mStrB.charAt(col-1);
                if (cA == cB) {
                    if (sAB < mLcsLengths[row-1][col-1] + 1) {
                        sAB = mLcsLengths[row-1][col-1] + 1;
                        dir = LCS_DIAG;
                    }
                }
                mLcsLengths[row][col] = sAB;
                mBacktracks[row][col] = dir;                    
            }
            Sx.putsSubArray(mStrA.charAt(row-1) + " ", mLcsLengths[row], 1, mLcsLengths[row].length);
        }
    }
    
    /**
     * Restricted edit distance: only insertions and deletions (ID) are allowed.
     */
    public int findEditDistanceID()
    {
        if (mEditDistance < 0) {
            mEditDistID = ArrayFactory.makeIntArray(mRows, mCols);
            for (int row = 0; row < mRows; row++)
                mEditDistID[row][0] = row;
            for (int col = 1; col < mCols; col++)
                mEditDistID[0][col] = col;
            for (int row = 1; row < mRows; row++) {
                for (int col = 1; col < mCols; col++) {
                    int dAB = mEditDistID[row-1][col] + 1;
                    if (dAB > mEditDistID[row][col-1] + 1) {
                        dAB = mEditDistID[row][col-1] + 1;
                    }
                    if (mStrA.charAt(row-1) == mStrB.charAt(col-1)) {
                        if (dAB > mEditDistID[row-1][col-1]) {
                            dAB = mEditDistID[row-1][col-1];
                        }
                    }
                    mEditDistID[row][col] = dAB;                   
                }
                Sx.putsSubArray(mStrA.charAt(row-1) + " ", mEditDistID[row], 1, mEditDistID[row].length);
            }
            mEditDistance = mEditDistID[mRows-1][mCols-1];
        }
        return mEditDistance;
    }
    
    
    protected String findLCS()
    {
        if (mLcsStr == null) {
            initLcsArrays();
            mLcsStr = new StringBuffer();        
            findLCS(mRows-1, mCols-1);
        }
        return mLcsStr.toString();
    }
    
    protected void findLCS(int row, int col)
    {
        if (row == 0 || col == 0)
            return;
        switch(mBacktracks[row][col]) {
            case LCS_DIAG:
                findLCS(row-1, col-1);
                mLcsStr.append(mStrA.charAt(row-1));
                break;
            case LCS_UP:
                findLCS(row-1, col);
                break;
            default:
                findLCS(row, col-1);
                break;
        }        
    }
    
    /************************************************************************
     * unit_test
     */
    public static int unit_test(int level)
    {
        LongestCommonSubsequence lcs = null;
        if (level < 0) {
            try {
                
                lcs = new LongestCommonSubsequence("", "empty");
                Sx.puts(lcs.toString());
            } catch (Throwable ex) {
                Sx.puts(ex.getClass().getName());
                Sx.puts(ex.getMessage());
                ex.printStackTrace();
            }
            lcs = new LongestCommonSubsequence(null, "empty");
        }
        
        lcs = new LongestCommonSubsequence("ATCTGAT", "ATCTGAT");
        Sx.puts("LCSubsequence: " + lcs.findLCS());
        Sx.puts("Edit Distance: " + lcs.findEditDistanceID());

        lcs = new LongestCommonSubsequence("ATCTGAT", "TGCATA");
        Sx.puts("LCSubsequence: " + lcs.findLCS());
        Sx.puts("Edit distance: " + lcs.findEditDistanceID());

        return 0;
    }
    
    public static void main(String[] args) {  unit_test(0); }
}
