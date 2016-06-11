package sprax.aligns;

import java.util.ArrayList;
import java.util.Arrays;

import sprax.sprout.Spaces;
import sprax.sprout.Sx;
import sprax.arrays.ArrayFactory;

class IndexPair
{
    // Map.Entry<Integer, Integer> mEntry;
    int mIdxA;
    int mIdxB;
    IndexPair(int a, int b) {
        mIdxA = a;
        mIdxB = b;
    }
    public String toString() {
        return String.format("< %2d | %2d >", mIdxA, mIdxB);
    }
}

public class SequenceAlignment<T>
{
	public static int sDbg = 1;
//    public enum Dir { UP, LEFT, DIAG };
//    static ArrayFactory<Dir> mArrayFactory = new ArrayFactory<Dir>();
    public static final char SA_ZERO = 'Z';
    public static final char SA_UP   = 'U';
    public static final char SA_LEFT = 'L';
    public static final char SA_DIAG = 'D';
    public static final char SA_GAP  = '_';
    public static final char SA_RUN  = '*';
    
    /** Constants for computing edit distance: */
    public static final int ED_COST_DELETE = 1;
    public static final int ED_COST_INSERT = 1;
    public static final int ED_COST_MISMAT = 1;
    public static final int ED_COST_MATCH  = 0;
    
    /** Non-static constants for computing string similarity: */
    public static float     SA_COST_DELETE =  -0.5F;
    public static float     SA_COST_INSERT =  -0.5F;
    public static float     SA_COST_MISMAT =  -1.0F;
    public static float     SA_COST_MATCH  =   1.0F;

    final ArrayList<T>      mSeqA;
    final ArrayList<T>      mSeqB;
    ArrayList<T>            mMaxSeq;    // lazy: found on demand
    final int   mRows;				  // mStrA.length() + 1
    final int   mCols;				  // mStrB.length() + 1
    int         mMaxLength;
    int         mMinLength;
    int         mNumDeletes;
    int         mNumInserts;
    float mInsDelScore[][]  = null;   // lazy, but kept even after similarity score is found
    char  mBackDirects[][]  = null;   // lazy
    int   mEditDistID[][]   = null;    // lazy
    int   mEditDistance     = -1;      // lazy: computed on demand (DP array)
    float mSimilarityScore  = -1;      // lazy: computed on demand (DP array)
    float mSimScoreDistance = -1;
    boolean     mIsMaximal;
    
    public ArrayList<IndexPair>  mIndexPairs = null;    // lazy: found on demand
       

    SequenceAlignment(final ArrayList<T> seqA, final ArrayList<T> seqB)
    {
        if (seqA == null || (mRows = 1+seqA.size()) == 1 || seqB == null || (mCols = 1+seqB.size()) == 1)
            throw new IllegalArgumentException(SequenceAlignment.class.getName() +"("+seqA+", "+seqB+")");
        mSeqA = seqA;  // TODO ??
        mSeqB = seqB;
        if (seqA.size() > seqB.size()) {
            mMaxLength = seqA.size();
            mMinLength = seqB.size();
        } else {
            mMaxLength = seqB.size();
            mMinLength = seqA.size();            
        }
    }
    @Deprecated
    SequenceAlignment(final T[] arrA, final T[] arrB)
    {
        if (arrA == null || (mRows = 1+arrA.length) == 1 || arrB == null || (mCols = 1+arrB.length) == 1)
            throw new IllegalArgumentException(SequenceAlignment.class.getName() +"("+arrA+", "+arrB+")");
        mSeqA = new ArrayList<T>(Arrays.asList(arrA));
        mSeqB = new ArrayList<T>(Arrays.asList(arrB));
        mMaxLength = Math.max(arrA.length, arrB.length);
        mMinLength = Math.min(arrA.length, arrB.length);
    }
    
    public boolean isMaximal()  { return mIsMaximal; }

    /**
     * The Scoring Matrix function measures similarity between two objects
     * of the same type, and also gives a value for the presence of one
     * object and the absence of another.  That is, it gives a value for
     * A compared to B, and also handles the cases A compared to null and
     * null compared to B.  In general, it rewards matches (positive return 
     * value) and penalizes differences (negative return value).  Insertions 
     * and deletions (a.k.a. indels) are often evaluation as neutral (return
     * value 0) or close to it.
     * 
     * Thus it may be thought of as a generalization of component-based 
     * equality, not as a generalization of an ordering comparator (as in <=).
     * 
     * This default implementation reproduces a solution to the Longest Common Subsequence problem.
     * 
     * Override this to change the alignment problem.
     */
    public float scoreMatrix(T cA, T cB) 
    {
        if (cA.equals(cB))
            return  SA_COST_MATCH;   // Point identity
        return      0;               // No penalty for non-matches
    }
    /**
     * Gap in A means insertion (new entry in B)
     * @return Cost of one point insertion
     */
    public float scoreMatrix(char cA, T tB)
    {
        // The only possibility is (cA == SA_GAP)           
        return 0;   // No penalty for insertion.
    }
    /**
     * Gap in B means deletion from A (entry present in A but missing from B)
     * @return Cost of one point deletion
     */
    public float scoreMatrix(T tA, char cB)
    {
        // The only possibility is (cB == SA_GAP)           
        return 0;   // No penalty for deletion.
    }
    
    
    public float getSimilarityScore()
    {
        if (mSimilarityScore < 0)
            return findSimilarityScore();
        return mSimilarityScore;
    }
    
    protected float findSimilarityScore()
    {
        mInsDelScore = ArrayFactory.makeFloatArray(mRows, mCols);        
        /*  For C, not Java (which initializes arrays to all 0s): */
        //  for (int row = 0; row < mRows; row++)
        //      mInsDelScore[row][0] = 0;
        //  for (int col = 1; col < mCols; col++)
        //      mInsDelScore[0][col] = 0;
        
        mBackDirects = ArrayFactory.makeCharArray(mRows, mCols);
        for (int row = 0; row < mRows; row++)
            mBackDirects[row][0] = SA_UP;
        for (int col = 1; col < mCols; col++)
            mBackDirects[0][col] = SA_LEFT;
        
        if (sDbg > 3) 
            Sx.putsArray("findSimilarityScore : mInsDelScore\n", mSeqB, 6);
        for (int row = 1; row < mRows; row++) {
            T chA = mSeqA.get(row-1);
            for (int  col = 1; col < mCols; col++) {
                float sAB = mInsDelScore[row-1][col] + scoreMatrix(chA, SA_GAP); // gap in B (deletion from A)
                char  dir = SA_UP;
                T  chB = mSeqB.get(col-1);
                float scr = mInsDelScore[row][col-1] + scoreMatrix(SA_GAP, chB); // gap in A (insertion in B)
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_LEFT;
                }
                scr     = mInsDelScore[row-1][col-1] + scoreMatrix(chA, chB); // pos for match, neg for mismatch
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_DIAG;
                }
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
                T chA = mSeqA.get(row-1);
                for (int col = 1; col < mCols; col++) {
                    int dAB = mEditDistID[row-1][col] + ED_COST_DELETE;
                    int tmp = mEditDistID[row][col-1] + ED_COST_INSERT;
                    if (dAB > tmp) {
                        dAB = tmp;
                    }
                    T chB = mSeqB.get(col-1);
                    if (chA == chB) {
                        tmp = mEditDistID[row-1][col-1] + ED_COST_MATCH;
                        if (dAB > tmp) {
                            dAB = tmp;
                        }
                    }
                    mEditDistID[row][col] = dAB;                   
                    
                    if (sDbg > 4) 
                        Sx.putsSubArray(mSeqA.get(row-1) + " ", mEditDistID[row], 1, mEditDistID[row].length);
                }
            }
            mEditDistance = mEditDistID[mRows-1][mCols-1];
        }
        return mEditDistance;
    }
     
    public float findEditDistanceFromScore()
    {
        if (mInsDelScore == null)
            findSimilarityScore();
        float score = mInsDelScore[mRows-1][mCols-1];
        mSimScoreDistance = Math.max(mRows, mCols) - 1 - score;
        return mSimScoreDistance;
    }    
   
    float findAlignment()
    {
        float score = getSimilarityScore();
        if (mMaxSeq == null) {
            findAlignedSequence();
            findAlignmentPairs();
        }
        return score;
    }
    
    void findAlignedSequence() {
        mMaxSeq = new ArrayList<T>();        
        findAlignmentString(mRows-1, mCols-1);
        int len = mMaxSeq.size();
        if (len == mMinLength)
            mIsMaximal = true;
        else
            mIsMaximal = false;
    }
    protected void findAlignmentString(int row, int col)
    {
        if (row == 0 || col == 0)
            return;
        switch(mBackDirects[row][col]) {
            case SA_DIAG:
                findAlignmentString(row-1, col-1);
                mMaxSeq.add(mSeqA.get(row-1));
                break;
            case SA_UP:
                findAlignmentString(row-1, col);
                break;
            case SA_LEFT:
                findAlignmentString(row, col-1);
                break;
            default:        // SA_ZERO
                return;     // go to origin, we're done.
        }        
    }

    void findAlignmentPairs()
    {
        mIndexPairs = new ArrayList<IndexPair>(mMaxLength);
        findAlignmentPairs(mRows-1, mCols-1, 0, 0);
    }
    protected void findAlignmentPairs(int row, int col, int ins, int del)
    {
        if (row == 0 && col == 0)
            return;
        IndexPair ip;
        switch(mBackDirects[row][col]) {
            case SA_DIAG:
                findAlignmentPairs(row-1, col-1,   0,     0);
                ip = new IndexPair(row-1, col-1);
                mIndexPairs.add(ip);
                break;
            case SA_UP:
                findAlignmentPairs(row-1, col  , ins, --del);
                ip = new IndexPair(row-1, del);
                mNumDeletes++;
                mIndexPairs.add(ip);
                break;
            case SA_LEFT:
                findAlignmentPairs(row  , col-1, --ins, del);
                ip = new IndexPair(ins, col-1);
                mNumInserts++;
                mIndexPairs.add(ip);
                break;
            default:        // SA_ZERO
                return;     // go to origin, we're done.
        }        
    }
        
    protected void findAlignmentPairs_old(int row, int col)
    {
        if (row == 0 && col == 0)
            return;
        IndexPair ip;
        switch(mBackDirects[row][col]) {
            case SA_DIAG:
                findAlignmentPairs_old(row-1, col-1);
                ip = new IndexPair(row-1, col-1);
                mIndexPairs.add(ip);
                break;
            case SA_UP:
                findAlignmentPairs_old(row-1, col  );
                ip = new IndexPair(row-1,    -1);
                mIndexPairs.add(ip);
                break;
            case SA_LEFT:
                findAlignmentPairs_old(row  , col-1);
                ip = new IndexPair(   -1, col-1);
                mIndexPairs.add(ip);
                break;
            default:        // SA_ZERO
                return;     // go to origin, we're done.
        }        
    }


    public String getEntryString(ArrayList<T> seq, int idx, int maxLen, int minLen) 
    {
        if (idx < 0)
            return Spaces.get(minLen);
        
        String strA = seq.get(idx).toString();
        int strLen = strA.length();
        if (strLen > maxLen)
            return strA.substring(0, maxLen);
        else if (strLen < minLen)
            return strA + Spaces.get(minLen - strLen);
        return strA;
    }
    
    public char getComparisonChar(IndexPair ip, String strA, String strB)
    {
        if (ip.mIdxA < 0 || ip.mIdxB < 0 ||  ! strA.equals(strB))
            return ' ';
        return '=';
    }
    
    public void printPairIndicesFirst(IndexPair ip, String prefix, int maxLen, int minLen)
    {
        String strA = getEntryString(mSeqA, ip.mIdxA, maxLen, minLen);
        String strB = getEntryString(mSeqB, ip.mIdxB, maxLen, minLen);
        char   chrC = getComparisonChar(ip, strA, strB);
        Sx.format("%s<% 3d % 3d>   %s %c %s\n", prefix, ip.mIdxA, ip.mIdxB, strA, chrC, strB);
    }
    public void printPairIndicesOut(IndexPair ip, String prefix, int maxLen, int minLen)
    {
        Sx.format("%s<%2d | %s> <%s | %2d>\n"
                , prefix
                , ip.mIdxA
                , getEntryString(mSeqA, ip.mIdxA, maxLen, minLen)
                , getEntryString(mSeqB, ip.mIdxB, maxLen, minLen)
                , ip.mIdxB
        );
    }
    public void printPairs(String prefix, int maxLen, int minLen)
    {
        for (IndexPair pair : mIndexPairs) {
            printPairIndicesFirst(pair, prefix, maxLen, minLen);
        }
    }    
    
    public void printPairsIndicesOut(String prefix, int maxLen, int minLen)
    {
        if (maxLen < 2) {
            for (IndexPair pair : mIndexPairs) {
                printPairIndicesFirst(pair, prefix, maxLen, minLen);
            }
        } else { 
            for (IndexPair pair : mIndexPairs) {
                printPairIndicesOut(pair, prefix, maxLen, minLen);
            }
        }
    }    
    
    /* 
     * ABCDEFGHIJKLMNO -> ABC DEF GHI JKL MNO -> DEF GHI JKL
     * ABCGHIJKLDEFMNO -> ABC GHI JKL DEF MNO -> GHI JKL DEF
     */
    static ArrayList<Character> createCharacterArrayList(String str) {
        int len = str.length();
        ArrayList<Character> chA = new ArrayList<Character>(len);
        for (int j = 0; j < len; j++) {
            chA.add(str.charAt(j));
        }
        return chA;
    }
    
    /**
     * Computes distances and alignment.
     * TODO: Computes more than is needed for most purposes.  
     */
    public int doAlignment()
    {
        findEditDistanceID();
        findAlignment();
        findEditDistanceFromScore();
        return mEditDistance;                   // TODO: status code?
    }
    
    protected int test_alignment()             { return test_alignment(0, "", 1, 1); }
    protected int test_alignment(int verbose)  { return test_alignment(verbose, "", 1, 1); }
    protected int test_alignment(int verbose, int indent) { 
        String prefix = Spaces.get(indent);
        return test_alignment(verbose, prefix, 1, 1); 
    }
    protected int test_alignment(int verbose, int indent, int maxLen, int minLen)
    {
        String prefix = Spaces.get(indent);
        return test_alignment(verbose, prefix, maxLen, minLen);
    }
    protected int test_alignment(int verbose, String prefix, int maxLen, int minLen)
    {
        int saveDbg = sDbg;
        sDbg = verbose;
        doAlignment();
        if (verbose > 3) {
            Sx.format(prefix + "Alignment Type: %s\n", getClass().getName());
        }
        if (verbose > 2) {        
            Sx.format(prefix + "Edit Distance Ins Del: %d\n", mEditDistance);
            Sx.format(prefix + "Edit Distance (Score): %f\n", mSimScoreDistance);
            Sx.format(prefix + "Similarity Score(max): %g\n", mSimilarityScore);
        }
        if (verbose > 2) {
            Sx.putsArray(prefix + "   ", mSeqA);
            Sx.putsArray(prefix + " ~ ", mSeqB);
            Sx.putsArray(prefix + " = ", mMaxSeq);
        }
        if (verbose > 2) {
            Sx.putsArray(mIndexPairs);
        }
        if (verbose > 1)
            printPairs(prefix, maxLen, minLen);
        if (verbose > 1)
            Sx.puts();
        sDbg = saveDbg;
        return 0;
    }
    
    public static int test_SA(int verbose, String strA, String strB)
    {
        ArrayList<Character> seqA = createCharacterArrayList(strA);
        ArrayList<Character> seqB = createCharacterArrayList(strB);
        SequenceAlignment<Character> aln = new SequenceAlignment<Character>(seqA, seqB);
        aln.test_alignment(verbose, 0);
        return 0;
    }    
    
    /************************************************************************
     * unit_test
     */
    public static int unit_test(int verbose)
    {
        test_SA(4, "ABCDEF", "abCDef");
        test_SA(verbose, "ATCTGAT", "ATCTGAT");
        test_SA(verbose, "ATCTGAT", "TGCATA");
        test_SA(verbose, "Well... Do not get mad, get even!", "Don't get mud, get cleaner?");
        test_SA(verbose, "abcDEFdefJKLqr", "abcJKLabcPQRdefMNOPq");
        test_SA(verbose, "abcDEFxyzJKL", "abcJKLxyzDEF");
        return 0;
    }
    
    public static void main(String[] args) {  unit_test(2); }
}

