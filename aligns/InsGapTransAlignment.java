package sprax.aligns;

import java.util.ArrayList;

import sprax.Sx;
import sprax.arrays.ArrayFactory;

/** 
 * Sequence alignment with gaps, inserts, and transpositions.
 * @author sprax
 */
public class InsGapTransAlignment<T> extends InsGapSequenceAlignment<T>
{
    
    /** Additional constants for computing string similarity: */

    public static final char SA_SWAP  = 'x';

    public static float      SA_COST_SWAP =  -1.5F;
    
    /**
     * Alignment matrix for multiple inserts and gaps.  Used in conjunction with point
     * insertions and deletions. 
     */
    float mSwapScore[][]  = null;    // lazy, but kept even after similarity score is found
    
    InsGapTransAlignment(ArrayList<T> sA, ArrayList<T> sB)  { super(sA, sB); }
    

    @Override
    protected float findSimilarityScore()
    {
        mInsDelScore = ArrayFactory.makeFloatArray(mRows, mCols);
        mBackDirects = ArrayFactory.makeCharArray( mRows, mCols);
        for (int row = 0; row < mRows; row++)
            mBackDirects[row][0] = SA_UP;
        for (int col = 1; col < mCols; col++)
            mBackDirects[0][col] = SA_LEFT;        
        
        mInsRunScore = ArrayFactory.makeFloatArray(mRows, mCols);
        mDelRunScore = ArrayFactory.makeFloatArray(mRows, mCols);
        mSwapScore   = ArrayFactory.makeFloatArray(mRows, mCols);

                
        if (sDbg > 3) 
            Sx.putsArray("\n", mSeqB, 6);
        //int prevMatchRow = -1;
        int lastMatchRow = -1;
        
        for (int row = 1; row < mRows; row++) {
            char dir; 
            T tB, tA = mSeqA.get(row-1);
            int begSkip = -1, endSkip = -1;
            float scr, sAB;
            //int prevMatchCol = -1;
            int lastMatchCol = -1;
            for (int  col = 1; col < mCols; col++) {
                
                // Max between [the score from alignment of row-length prefix of seq A with the col-length 
                // prefix of seqB ending with a 1-place deletion from seq A (gap in seq B); i.e. extending the gap]
                // and [the score from initiating a gap
                sAB = Math.max( mDelRunScore[row-1][col] + scoreMatrix(tA, SA_GAP)    // extending gap in B
                        , mInsDelScore[row-1][col] + scoreMatrix(tA, SA_RUN) ); // starting a gap in B
                dir = SA_UP;
                
                scr = Math.max( mDelRunScore[row][col-1] + scoreMatrix(tA, SA_GAP)    // extending gap in A
                        , mInsDelScore[row][col-1] + scoreMatrix(tA, SA_RUN) ); // starting a gap in A
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_LEFT;
                }
                
                tB = mSeqB.get(col-1);
                scr = mInsDelScore[row-1][col-1] + scoreMatrix(tA, tB); // pos for match, neg for mismatch
                if (sAB < scr) {
                    sAB = scr;
                    dir = SA_DIAG;
                    if (row == lastMatchRow && col > lastMatchCol + 1) {
                        // we skipped ahead...
                        Sx.format("Match: %2d %2d   %c %c  beg %d\n",   row, col, tA, tB, begSkip);
                        if (begSkip == -1) {
                            begSkip =  row;
                            endSkip =  col;
                        }
                    }
                    //prevMatchRow = lastMatchRow;
                    //prevMatchCol = lastMatchCol;
                    lastMatchRow = row;
                    lastMatchCol = col;
                }
                if (row > col) {

                  T xA = mSeqA.get(col-1);
                  T xB = mSeqB.get(row-1);
                  String strA = tA.toString();                  
                  String strB = tB.toString();
                  if (strA.equals("o") && strB.equals("n")) {
                      sDbg += 1;
                      sDbg -= 1;
                  }
                    
                //scr = mSwapScore[row-1][col-1] + scoreMatrix(xA, xB);
                  float dif = (scoreMatrix(tA, xB) + scoreMatrix(xA, tB))/2 - mInsDelScore[col-1][row-1];
                  scr = mInsDelScore[row-1][col-1] + dif;
                  if (sAB < scr) {
                    sAB = scr;
                    dir = SA_SWAP;
                  }
                }

                mInsDelScore[row][col] = sAB;
                mBackDirects[row][col] = dir;
            }
            if (begSkip >= 0) {
                float transScore = 0;

                for (int q = begSkip, jA = endSkip-1, jB = begSkip-1; q < endSkip; q++, jA++, jB++) {
                    if (jA >= mSeqA.size())
                        break;
                    T cA = mSeqA.get(jA);
                    if (jB >= mSeqB.size())
                        break;
                    T cB = mSeqB.get(jB);
                    transScore += scoreMatrix(cA, cB);
                }
                if (transScore > 0) {
                    mInsDelScore[begSkip][endSkip] += 0;;
                }
            }
            if (sDbg > 3) 
                Sx.putsSubArray("  " + mSeqA.get(row-1), mInsDelScore[row], 1, mInsDelScore[row].length);
        }
        mSimilarityScore = mInsDelScore[mRows-1][mCols-1];
        return mSimilarityScore;
    }



    /************************************************************************
     * test_IGTSA
     */
    public static int test_IGTSA(int verbose, String strA, String strB)
    {
      if (verbose > 0) {
        Sx.puts("strA: " + strA);
        Sx.puts("strB: " + strB);
      }
        
        ArrayList<Character> seqA = createCharacterArrayList(strA);
        ArrayList<Character> seqB = createCharacterArrayList(strB);
        InsGapTransAlignment<Character> sa = new 
        InsGapTransAlignment<Character>(seqA, seqB); 
        sa.test_alignment(verbose);
        
        Sx.puts();
        return 0;
    }

/************************************************************************
 * unit_test
 *
 * abcdefghijklmno
 * jklmnoabcdefghi
 * 
 * ABCDEFGHIJKLMNO -> ABC DEF GHI JKL MNO -> DEF GHI JKL
 * ABCGHIJKLDEFMNO -> ABC GHI JKL DEF MNO -> GHI JKL DEF
 * 
 *      a b c d e f X Y Z
 *  a   1 0 0
 *  b   0 1 0
 *  c   0 0 1
 *  X   0 0 0 0 0 0 1 0 0
 *  Y   0 0 0 0 0 0 0 1 0
 *  Z   0 0 0 0 0 0 0 0 1
 *  d
 *  e
 *  f
 *  
 *    a b c J K L g h i D E F m n o
 *  a
 *  b
 *  c
 *  D
 *  E
 *  F
 *  g
 *  h
 *  i
 *  J
 *  K
 *  L
 *  m
 *  n
 *  o
 *  
 */
public static int unit_test(int verbose)
{
    String  testName = InsGapTransAlignment.class.getName() + ".unit_test";
    Sx.puts(testName + " BEGIN");
    
//    test_IGTSA("abcABC", "abcABC", 0);
//    test_IGTSA("abcABC", "ABCabc", 0);
//    test_IGTSA("ABCABA", "ABCABAC", 0);
//    
//    Sx.puts("=========================================");
//    test_IGTSA("abcDEFghiJKLmno", "abcJKLghiDEFmno", 2);
//    test_IGTSA("abcDEFghi", "abcJKLDEF", 2);
    //test_IGTSA(verbose, "abcDEFJKL", "abcJKLDEF");
    //test_IGTSA(verbose, "abcDEFxyzJKL", "abcJKLxyzDEF");
    test_IGTSA(verbose, "ABnDEoGH", "ABoDEnGH");
       
    Sx.puts(testName + " END");    
    return 0;
}

public static void main(String[] args) {  unit_test(4); }

}
