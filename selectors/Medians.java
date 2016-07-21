package sprax.selectors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;

import sprax.arrays.Arrays1d;
import sprax.arrays.Arrays2d;
import sprax.containers.Merges;
import sprax.sprout.Sx;

class Wrap
{
    int val = 8;
    
    Wrap(int v)
    {
        val = v;
    }
}

// class ArrayEntry2<T extends Comparable<?>> implements Comparable<ArrayEntry2<?>>
class ArrayEntryInt2 implements Comparable<ArrayEntryInt2>
{
    int mVal;   // presumably mVal == AA[mRow][mCol] for some corresponding 2D array
    int mRow;   // 
    int mCol;   // 
    
    ArrayEntryInt2(int val, int row, int col) {
        mVal = val;
        mRow = row;
        mCol = col;
    }
    
    @Override
    public int compareTo(ArrayEntryInt2 other) {
        return mVal - other.mVal;
    }
    
}

/**
 * Finds array medians in general, and also solves this restricted problem:
 * 
 * Given a 2D array in which all rows and columns are sorted (as 1D arrays),
 * what's the most efficient way to find the median of the whole array.
 * For 3x3, the median value must lie on the off-axis, for 5x5, it may
 * lie 1-off the off axis, etc. (TODO: for 7x7, can it be 2 off?)
 * 
 * @author slines
 *
 */
public class Medians
{
    public static int sDbg = 2; // greater value mean less debugging output
                                
    public static ArrayEntryInt2 findKthSmallest(int AA[][], int k)
    {
        PriorityQueue<ArrayEntryInt2> aQ = new PriorityQueue<ArrayEntryInt2>(); // TODO: add size est?
        HashSet<Integer> aH = new HashSet<Integer>();
        ArrayEntryInt2 next = new ArrayEntryInt2(AA[0][0], 0, 0);
        aQ.add(next);   // don't need to add this to aH; it will never be duped.
        int kCount = 0;
        int nCols = AA[0].length;
        int maxRow = AA.length - 1;
        int maxCol = nCols - 1;
        while (!aQ.isEmpty())
        {
            next = aQ.remove();
            if (++kCount == k)
                break;
            
            int row = next.mRow;
            int col = next.mCol;
            // Before adding an entry to the queue, we must check that 
            // the "same" entry has never before been added to the queue.
            // We must check for sameness by content, not by address, 
            // and merely checking where the queue currently contains
            // these values is not sufficient.  We must visit each
            // array position at most once, so we must check whether
            // the queue has *ever* contained the given position.
            
            // TODO: if k < nRows and k < nCols, we don't need to 
            // check array bounds...
            
            // TODO: consider making rowColIdx an indicator of being inbounds, too?
            int rowColIdx = row * nCols + col;  // unique index for each row,col pair
            if (col < maxCol && !aH.contains(rowColIdx + 1)) {
                aH.add(rowColIdx + 1);
                aQ.add(new ArrayEntryInt2(AA[row][col + 1], row, col + 1));
            }
            if (row < maxRow && !aH.contains(rowColIdx + nCols)) {
                aH.add(rowColIdx + nCols);
                aQ.add(new ArrayEntryInt2(AA[row + 1][col], row + 1, col));
            }
        }
        
        return next;
    }
    
    public static int medianOfAA_queueWalkKth(final int AA[][])
    {
        int nCols = checkArray2(AA);
        int nRows = AA.length;
        int size = nRows * nCols;
        boolean oddSize = (size % 2 != 0);
        int ord = (size + 1) / 2;
        if (oddSize) {
            ArrayEntryInt2 ent = findKthSmallest(AA, ord);
            return ent.mVal;
        } else {
            ArrayEntryInt2 kth = findKthSmallest(AA, ord);
            ArrayEntryInt2 kp1 = findKthSmallest(AA, ord + 1);
            return (kth.mVal + kp1.mVal) / 2;
        }
    }
    
    public static int medianOfUnsortedArray(final int A[])
    {
        if (A == null || A.length == 0)
            return 0;
        
        if (A.length == 1)
            return A[0];
        if (A.length > 2)
            Arrays.sort(A);  // only need to sort if length> 2
        return medianOfNonTrivialSortedArray(A);
    }
    
    public static int medianOfSortedArray(final int A[])
    {
        if (A == null || A.length == 0)
            return 0;
        
        return medianOfNonTrivialSortedArray(A);
    }
    
    /**
     * non-trivial means A is non-null and A.length > 0
     * 
     * @param A non-trivial sorted array of int
     * @return median value
     */
    private static int medianOfNonTrivialSortedArray(final int A[])
    {
        int halfLen = A.length / 2;
        if (halfLen + halfLen == A.length)   // length is even
            return (A[halfLen - 1] + A[halfLen]) / 2;
        else
            return A[halfLen];
    }
    
    public static int medianOfAA_lame(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return 0;
        int N = 0;
        for (int j = 0; j < AA.length; j++) {
            if (AA[j] == null)
                throw new IllegalArgumentException("null array at row " + j);
            N += AA[j].length;
        }
        int io[] = new int[N];
        for (int q = 0, j = 0; j < AA.length; j++)
            for (int k = 0; k < AA[j].length; k++)
                io[q++] = AA[j][k];
        Arrays.sort(io);
        if (sDbg > 1)
            Sx.putsArray(io);
        return medianOfSortedArray(io);
    }
    
    public static int medianOfAA_merge_bust(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return 0;
        int N = 0;
        for (int j = 0; j < AA.length; j++) {
            if (AA[j] == null)
                throw new IllegalArgumentException("null array at row " + j);
            N += AA[j].length;
        }
        int Z[] = new int[N];
        int I[] = new int[AA.length];   // indices
        
        for (int q = 0, j = 0; j < AA.length - 1; j++) {
            for (; I[j] < AA[j].length && AA[j][I[j]] <= AA[j + 1][I[j + 1]]; I[j]++) {
                Z[q++] = AA[j][I[j]];
            }
            
        }
        
        if (sDbg > 1)
            Sx.putsArray(Z);
        return medianOfSortedArray(Z);
    }
    
    public static int medianOfAA_mergeArraysPairs(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return 0;
        int N = 0;
        for (int j = 0; j < AA.length; j++) {
            if (AA[j] == null)
                throw new IllegalArgumentException("null array at row " + j);
            N += AA[j].length;
        }
        int Z[] = AA[0];
        for (int j = 1; j < AA.length; j++) {
            Z = Merges.mergeArrays(Z, AA[j]);
        }
        Sx.debugArray(1, Z);
        return medianOfSortedArray(Z);
    }
    
    public static int medianOfAA_mergeArrays(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return 0;
        int Z[] = Merges.mergeArrays(AA);
        Sx.debugArray(1, Z);
        return medianOfSortedArray(Z);
    }
    
    public static int medianOfAA_mergeArraysPq(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return 0;
        int Z[] = Merges.mergeArraysPq(AA);
        Sx.debugArray(1, Z);
        return medianOfSortedArray(Z);
    }
    
    public static int medianOfAA_mergeArraysPqSl(final int AA[][], int sizeLimit)
    {
        if (AA == null || AA.length == 0)
            return 0;
        int Z[] = Merges.mergeArraysPq(AA, sizeLimit);
        Sx.debugArray(1, Z);
        return Z[Z.length - 1];
    }
    
    public static int medianOfMediansAA(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return 0;
        int medians[] = new int[AA.length];
        for (int j = 0; j < AA.length; j++) {
            if (AA[j] == null)
                throw new IllegalArgumentException("null array at row " + j);
            medians[j] = medianOfSortedArray(AA[j]);
        }
        if (sDbg > 1)
            Sx.putsArray("medians: ", medians);
        return medianOfSortedArray(medians);
    }
    
    /**
     * Given a 2D array in which all rows and columns are sorted (as 1D arrays),
     * what's the most efficient way to find the median of the whole array.
     * For 3x3, the median value must lie on the off-axis, for 5x5, it may
     * lie 1-off the off axis, etc. (TODO: for 7x7, can it be 2 off?)
     * 
     * What constraints are actually imposed by sorted rows and columns?
     * For any indices j, k, all the elements in the (j+1)x(k+1) upper left
     * subarray must satisfy A[p][q] <= A[j][k] (for 0 <= p <= j and 0 <= q <= k).
     * Likewise, all elements in the (M-j)x(N-k) lower right subbarray must
     * satisfy A[p][q] >= A[j][k] (for j <= p < M and k <= q < N).
     * By definition, the median must not be greater or less than any half of
     * the set of values, so for for an MxN array, we must have:
     * (j+1)*(k+1) <= M*N/2, and
     * (M-j)*(N-k) <= M*N/2.
     * But (j+1)*(k+1) can be << M*N/2, as in A[0][N-1] or any other
     * entry on the secondary diagonal. That is because while all the
     * array entries in the upper left rectangular subarray bounded by
     * j,k must be <= A[j][k], the entries in the upper right subarray
     * {A[p][q]} where 0 <= p < j and k < q < N may or not be <= A[j][k].
     * 
     * Consider a 5x5 row-col-sorted array that contains only distinct
     * positive integers. The smallest possible median value is 13,
     * and the first place it can appear (in dictionary order) is at
     * the ordinal coordinates(3,3) [here ordinal basically means indexed
     * from 1], and this is, of course, the 13th place. But it could
     * in fact appear at any of these ordinally indexed spots:
     * 14 15
     * 23 24 25
     * 32 33 34
     * 41 42 43
     * 51 52
     * 
     * 
     * @param AA
     * @return
     */
    public static int medianFromSortedRowsAndColumns(final int AA[][])
    {
        if (AA == null || AA.length == 0 || AA[0] == null || AA[0].length == 0)
            return 0;
        
        int len = AA.length;
        if (len == 1) {
            return medianOfNonTrivialSortedArray(AA[0]);
        }
        
        // FIXME: this is wrong!
        //    int halfLen = len/2;
        //    Arrays.sort(AA);                 // need to sort if len > 2
        //    if (halfLen + halfLen == len)   // len is even and >= 2
        //      return (AA[halfLen - 1] + AA[halfLen])/2;
        //    else
        //      return AA[halfLen];
        return 0;
    }
    
    public static int test_medianFromSortedRowsAndColumns(int size)
    {
        Sx.puts("test_medianFromSortedRowsAndColumns");
        int stat = 0;
        
        int AA[][] =   // median is 13 at ordinal(3,3) -- not the first possible spot.
        { { 1, 2, 3, 4, 5 }
                , { 6, 7, 8, 9, 10 }
                , { 11, 12, 13, 34, 35 }  // centroid position
                , { 41, 42, 43, 44, 45 }
                , { 51, 52, 53, 54, 55 }
        };
        
        int BB[][] =   // median is 13 at ordinal(3,4)
        { { 1, 2, 3, 13, 15 }  // 13 at the first possible spot (1,4)
                , { 4, 5, 6, 14, 25 }  // (1,3) is impossible because it must be <= 14+ other vals.
                , { 7, 8, 9, 34, 35 }  // 13 at (3,5) does not work using this pattern
                , { 10, 11, 12, 44, 45 }  // In fact, the median 1st appearing at (3,5) is impossible.
                , { 51, 52, 53, 54, 55 }
        };
        
        int CC[][] =   // median is 13 at ordinal(3,4)
        { { 1, 2, 3, 4, 5 }  // 13 at the first possible spot (1,4)
                , { 6, 7, 13, 24, 25 }  // (1,3) is impossible because it must be <= 14+ other vals.
                , { 8, 9, 33, 34, 35 }  // 13 at (3,5) does not work using this pattern
                , { 10, 11, 43, 44, 45 }  // In fact, the median 1st appearing at (3,5) is impossible.
                , { 12, 52, 53, 54, 55 }
        };
        
        int DD[][] =   // median is 13 at ordinal(2,5)
        { { 1, 2, 3, 4, 5 }  // 13 at the fifth possible spot (1,4)
                , { 6, 7, 8, 9, 13 }  // (1,3) is impossible because it must be <= 14+ other vals.
                , { 10, 11, 12, 34, 35 }  // 13 at (3,5) does not work using this pattern
                , { 10, 11, 12, 44, 45 }  // In fact, the median 1st appearing at (3,5) is impossible.
                , { 51, 52, 53, 54, 55 }
        };
        
        int EE[][] =   // median is 13 at ordinal(3,4)
        { { 1, 2, 3, 4, 5 }
                , { 6, 7, 8, 9, 15 }
                , { 10, 11, 12, 13, 25 }  // 13 at (3,5) does not work using this pattern
                , { 41, 42, 43, 44, 45 }  // In fact, the median 1st appearing at (3,5) is impossible.
                , { 51, 52, 53, 54, 55 }
        };
        
        int FF[][] =   // median is 13 at ordinal(4,1)
        { { 1, 2, 3, 4, 15 }
                , { 5, 6, 7, 8, 25 }
                , { 9, 10, 11, 12, 35 }
                , { 13, 42, 43, 44, 45 }  // obviously 13 at ordinal(4,2) is also possible
                , { 51, 52, 53, 54, 55 }
        };
        
        int GG[][] =   // median is 13 at ordinal(4,2).  
        { { 1, 2, 3, 4, 15 }
                , { 5, 6, 7, 8, 25 }
                , { 9, 10, 11, 34, 35 }
                , { 12, 13, 43, 44, 45 }
                , { 51, 52, 53, 54, 55 }
        };
        
        int HH[][] =   // median is 13 at ordinal(4,3).  
        { { 1, 2, 3, 4, 15 }
                , { 5, 6, 7, 24, 25 }
                , { 8, 9, 10, 34, 35 }
                , { 11, 12, 13, 44, 45 }  // 13 at (4,4) does not work using this pattern
                , { 51, 52, 53, 54, 55 }  // in fact, the median 1st appearing at (4,4) is impossible.
        };
        
        int II[][] =   // median is 13 at ordinal(5,1).  
        { { 1, 2, 3, 14, 15 }
                , { 4, 5, 6, 24, 25 }      // There are several other ways to pack
                , { 7, 8, 9, 34, 35 }      // 1 through 12 into the array before
                , { 10, 11, 12, 44, 45 }      // 13 at (5,1).
                , { 13, 52, 53, 54, 55 }
        };
        
        int JJ[][] =   // median is 13 at ordinal(5,2) -- the last possible possition!
        { { 1, 2, 3, 14, 15 }
                , { 4, 5, 6, 24, 25 }
                , { 7, 8, 9, 34, 35 }
                , { 10, 11, 43, 44, 45 }  // 13 at (5,3) does not work using this pattern
                , { 12, 13, 53, 54, 55 }  // in fact, the median 1st appearing at (5,3) is impossible.
        };
        
        int KK[][] =
        { { 1, 2, 3, 14, 15, 16 }
                , { 4, 5, 6, 17, 23, 26 }
                , { 7, 8, 9, 18, 35, 36 }
                , { 10, 11, 43, 44, 45, 46 }
                , { 12, 13, 53, 54, 55, 56 }
                , { 62, 63, 63, 64, 65, 66 }
        };
        
        int ZZZ[][][] = { /* AA, BB, CC, DD, EE, FF, GG, */HH, II, JJ, KK };
        
        int zoid = 2;
        if (zoid > 1) {
            for (int j = 0; j < ZZZ.length; j++) {
                //S.puts("median of ZZZ[" +j+ "] is " + medianOfAA_lame(ZZZ[j]));
                //S.puts("medMed of ZZZ[" +j+ "] is " + medianOfMediansAA(ZZZ[j]) + " median median");
                Sx.puts("median of ZZZ[" + j + "] is " + medianOfAA_mergeArrays(ZZZ[j])
                        + " merge_Nwise");
                Sx.puts("median of ZZZ[" + j + "] is " + medianOfAA_binarySearchKth(ZZZ[j])
                        + " binary search");
                Sx.puts("median of ZZZ[" + j + "] is " + medianOfAA_queueWalkKth(ZZZ[j])
                        + " queue walk");
                Sx.puts("youngf of ZZZ[" + j + "] is " + medianOfAA_youngify(ZZZ[j]) + " youngify");
            }
        } else {
            for (int j = 0, end = 25 / 2; j < end; j++) {
                JJ[0][0] = BIG_VAL;
                Arrays2d.youngify(JJ, 0, 0, 5, 5, BIG_VAL);
                Sx.putsArray("youngify " + j + ":\n", JJ);
            }
        }
        return stat;
    }
    
    static int BIG_VAL = 9999;
    
    //m and n are the dimensions of the matrix and i and j are initial positions will be send as 0,0.
    
    public static int checkArray2(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            throw new IllegalArgumentException("checkArray2: null or empty array");
        int nRows = AA.length;
        int nCols = 0;
        for (int row = 0; row < nRows; row++) {
            if (AA[row] == null)
                throw new IllegalArgumentException("checkArray2: null row " + row);
            nCols = AA[row].length;
            if (nCols < 1 || nCols != AA[0].length)
                throw new IllegalArgumentException("checkArray2: row " + row + " size " + nCols
                        + "!=" + AA[0].length);
        }
        return nCols;
    }
    
    public static int medianOfAA_youngify(final int AA[][])
    {
        int nCols = checkArray2(AA);
        int nRows = AA.length;
        int bigVal = Math.min(Integer.MAX_VALUE, 100 * AA[nRows - 1][nCols - 1]);
        int size = nRows * nCols;
        boolean oddSize = (size % 2 != 0);
        int end = (size - 1) / 2;
        for (int j = 0; j < end; j++) {
            AA[0][0] = bigVal;
            Arrays2d.youngify(AA, 0, 0, nRows, nCols, bigVal);
            //S.debugArray(1, "After youngify " + j + ":\n", AA);
        }
        // System.out.format("0RD: %d %d %d\n", AA[0][0], AA[0][1], AA[1][0]);
        if (oddSize)
            return AA[0][0];
        return (AA[0][0] + Math.min(AA[0][1], AA[1][0])) / 2;
    }
    
    public static int test_timeMedianOfRowColSortedArrays(int level)
    {
        int nRows = 100;
        int nCols = 255;
        if (level == 1) {
            nRows = 1 + (1 << 12);
            nCols = nRows;
        }
        else if (level < 4) {
            nRows = 1 + (1 << 11);
            nCols = nRows;
        }
        int minVal = 1000, maxVal = 0;
        int maxInc = 32;
        long seed = System.currentTimeMillis();
        
        long begTime, endTime;
        final int times = 4;
        long tmap[] = new long[times];
        long tmar[] = new long[times];
        long tmpq[] = new long[times];
        long tmps[] = new long[times];
        long tmpy[] = new long[times];
        long tmpk[] = new long[times];
        long tmpw[] = new long[times];
        int ms[] = new int[9], q = 0;
        for (int j = 0; j < times; j++) {
            q = 0;
            int AA[][] = Arrays2d.makeRandomRowColSortedArray(nRows, nCols, minVal, maxInc, seed);
            minVal = AA[0][0];
            maxVal = AA[nRows - 1][nCols - 1];
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_binarySearchKth(AA);
            endTime = System.currentTimeMillis();
            tmpk[j] = endTime - begTime;
            
            if (level < 2)
                continue;
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_mergeArrays(AA);
            endTime = System.currentTimeMillis();
            tmar[j] = endTime - begTime;
            
            if (level < 3)
                continue;
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_mergeArraysPq(AA);
            endTime = System.currentTimeMillis();
            tmpq[j] = endTime - begTime;
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_mergeArraysPqSl(AA, (nRows * nCols + 1) / 2);
            endTime = System.currentTimeMillis();
            tmps[j] = endTime - begTime;
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_queueWalkKth(AA);
            endTime = System.currentTimeMillis();
            tmpw[j] = endTime - begTime;
            
            if (level < 4)
                continue;
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_mergeArraysPairs(AA);
            endTime = System.currentTimeMillis();
            tmap[j] = endTime - begTime;
            
            begTime = System.currentTimeMillis();
            ms[q++] = medianOfAA_youngify(AA);
            endTime = System.currentTimeMillis();
            tmpy[j] = endTime - begTime;
            
            for (int k = 0; k < q; k++) {
                if (ms[0] != ms[k])
                    System.out.format("Broken median values:  0( %d ) !=  %d( %d )\n", ms[0], k,
                            ms[k]);
                // return -1;
            }
        }
        Sx.puts("AA minVal & maxVal = " + minVal + " & " + maxVal);
        
        for (int k = 0; k < q; k++) {
            switch (k) {
            case 0:
                System.out.format(
                        "\n medianOfAA_binSearchKth    (%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmpk[j]);
                }
                break;
            case 1:
                System.out.format(
                        "\n medianOfAA_mergeArrays     (%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmar[j]);
                }
                break;
            case 2:
                System.out.format(
                        "\n medianOfAA_mergeArraysPq   (%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmpq[j]);
                }
                break;
            case 3:
                System.out.format(
                        "\n medianOfAA_mergeArraysPqSl (%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmps[j]);
                }
                break;
            case 4:
                System.out.format(
                        "\n medianOfAA_queueWalkKth    (%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmpw[j]);
                }
                break;
            case 5:
                System.out.format(
                        "\n medianOfAA_mergeArraysPairs(%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmap[j]);
                }
                break;
            case 6:
                System.out.format(
                        "\n medianOfAA_youngify        (%d, %d) minVal %d  maxInc %d -> %d  t: ",
                        nRows, nCols, minVal, maxInc, ms[k]);
                for (int j = 0; j < times; j++) {
                    System.out.format("%7d ", tmpy[j]);
                }
                break;
            }
        }
        Sx.puts();
        return 0;
    }
    
    /**
     * returns the number of elements which are smaller than or equal
     * to val in the given Young's Tableau matrix
     */
    public static int lastCoordsSmallerYT(int AA[][], int nRows, int nCols, int val)
    {
        int nSmaller = 0;
        for (int row = nRows - 1, col = 0; row >= 0 && col < nCols;) {
            if (AA[row][col] <= val) {
                nSmaller += (row + 1);
                col++;
            } else {
                row--;
            }
        }
        return nSmaller;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // SOMEWHAT LAME VERSIONS:
    // these use brute force counting, ignoring all but the most
    // basic properties of a Young Tableau.
    // Only the first is implemented.
    ////////////////////////////////////////////////////////////////////////////
    /**
     * returns the number of elements that are less than or equal to val
     * in the given Young's Tableau matrix
     */
    public static int countNotGreaterThan_startTopRight(int AA[][], int nRows, int nCols, int val)
    { // lame because brute force
        int countNGT = 0;
        for (int row = 0, col = nCols - 1; row < nRows && col >= 0;) {
            if (AA[row][col] <= val) {
                countNGT += (col + 1);
                row++;
            } else {
                col--;
            }
        }
        return countNGT;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // NAIVE BUT NOT COMPLETELY LAME VERSIONS:
    // these use the basic properties of a Young Tableau,
    // but neglect some shortcuts and always do the entire count.
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * returns the number of elements that are less than or equal to val
     * in the given Young's Tableau matrix
     */
    public static int countNotGreaterThan_startBottomLeft(int AA[][], int nRows, int nCols, int val)
    {
        int countNGT = 0;
        for (int row = nRows - 1, col = 0; row >= 0 && col < nCols;) {
            if (AA[row][col] <= val) {
                countNGT += (row + 1);
                col++;
            } else {
                row--;
            }
        }
        return countNGT;
    }
    
    /**
     * returns the number of elements that are less than val
     * in the given Young's Tableau matrix
     */
    public static int countLessThan(int AA[][], int nRows, int nCols, int val)
    {
        int countLT = 0;
        for (int row = nRows - 1, col = 0; row >= 0 && col < nCols;) {
            if (AA[row][col] < val) {
                countLT += (row + 1);
                col++;
            } else {
                row--;
            }
        }
        return countLT;
    }
    
    /**
     * returns the number of elements that are greater than val
     * in the given Young's Tableau matrix
     */
    public static int countGreaterThan(int AA[][], int nRows, int nCols, int val)
    {
        return nRows * nCols - countNotGreaterThan_startBottomLeft(AA, nRows, nCols, val);
    }
    
    /**
     * returns the number of elements that are not less than val
     * in the given Young's Tableau matrix
     */
    public static int countNotLessThan(int AA[][], int nRows, int nCols, int val)
    {
        return nRows * nCols - countLessThan(AA, nRows, nCols, val);
    }
    
    /**
     * returns the number of elements that are less than or equal to val
     * in the given Young's Tableau matrix, but the count is limited
     * to a specified maximum. If the count exceeds maxCount, the
     * method immediately returns the partial count.
     */
    public static int limitCountNotGreaterThanVal(int AA[][], int nRows, int nCols, int val,
            int maxCount)
    {
        int countNGT = 0;
        for (int row = nRows - 1, col = 0; row >= 0 && col < nCols;) {
            if (AA[row][col] <= val) {
                countNGT += (row + 1);
                if (countNGT > maxCount)
                    break;
                col++;
            } else {
                row--;
            }
        }
        return countNGT;
    }
    
    /**
     * returns the number of elements that are equal to val
     * in the given Young's Tableau matrix.
     * 
     * For efficiency, call this only if you are not also calling
     * neither countLessThan nor countGreatThan, since this calls
     * both. (If you already have one of these values, you need
     * only compute the other and subtract both from the total count.)
     */
    public static int countEqualTo(int AA[][], int nRows, int nCols, int val)
    {
        return nRows * nCols - countLessThan(AA, nRows, nCols, val)
                - countGreaterThan(AA, nRows, nCols, val);
    }
    
    public static int test_counts()
    {
        int AA[][] =   // median is 13 at ordinal(5,2) -- the last possible position!
        { { 1, 2, 3, 3 }
                , { 2, 4, 4, 5 }
                , { 3, 4, 5, 5 }
                , { 4, 5, 5, 6 }
                , { 6, 6, 8, 9 }
        };
        Sx.putsArray(AA);
        int nRows = AA.length;
        int nCols = AA[0].length;
        int nElts = nRows * nCols;
        for (int val = 0; val <= 10; val++) {
            int countNGT_TR = countNotGreaterThan_startTopRight(AA, nRows, nCols, val);
            int countNGT_BL = countNotGreaterThan_startBottomLeft(AA, nRows, nCols, val);
            int countLT = countLessThan(AA, nRows, nCols, val);
            int countEQ = countEqualTo(AA, nRows, nCols, val);
            int countGT = countGreaterThan(AA, nRows, nCols, val);
            int countNLT = countNotLessThan(AA, nRows, nCols, val);
            
            if (countNGT_BL != countNGT_TR)
                throw new IllegalStateException("countNGT and/or countNGT_L are screwed up!");
            if (countEQ != nElts - countLT - countGT)
                throw new IllegalStateException("count EQ LT GT are screwed up!");
            System.out.format("Counts relative to %2d:   %2d<   %2d<=   %2d==   %2d>=   %2d>\n"
                    , val, countLT, countNGT_BL, countEQ, countNLT, countGT);
        }
        return 0;
    }
    
    /**
     * Return the least upper bound of x in AA, or Integer.MIN_VALUE.
     * In general, the least upper bound (or LUB) of x in a set Y is
     * its smallest element y not less than x. So in this case, the
     * LUB(AA, x) is the least w in AA s.t. x <= w. Of course this
     * w == AA[j][k] for some set of pairs j,k, and at some extra
     * cost, we could find a particular such pair, such as the minimum
     * or maximum (in dictionary order), but that is not done here.
     * TODO: do it?
     * 
     */
    public static int findLeastUpperBound(int AA[][], int nRows, int nCols, int argVal)
    {
        
        int maxVal = AA[nRows - 1][nCols - 1];
        if (argVal > maxVal)               // This may be an error condition, but maybe not, so
            return Integer.MIN_VALUE;    // just return an absurd value and let the caller deal.
            
        // Still here, so the value exists, and it is <= maxVal.
        int minVal = maxVal;
        for (int row = nRows - 1, col = 0; row >= 0 && col < nCols;) {
            int curVal = AA[row][col];
            if (curVal >= argVal) {   // This entry is >= argVal, not necessarily minimal...
                if (curVal == argVal)
                    return argVal;        // Found x==UB(x) in AA; end the search.
                if (minVal > curVal)
                    minVal = curVal;
                row--;    // Skip rest or row, but look for a smaller bound in the same column.
            } else {
                col++;    // Skip rest of col, but look for a smaller bound in the same row.
            }
        }
        return minVal; // Did not find x==UB(AA, x), so minVal==LUB(AA, x) 
    }
    
    public static int test_findBoundingEntry(int AA[][], int beg, int size)
    {
        int nRows = AA.length, nCols = AA[0].length;
        int minVal = AA[0][0];
        int maxVal = AA[nRows - 1][nCols - 1];
        int median = medianOfAA_mergeArrays(AA);
        Sx.putsArray(AA);
        Sx.puts("        Actual median is " + median);
        for (int x = beg, end = beg + size; x <= end; x++) {
            int lub = findLeastUpperBound(AA, nRows, nCols, x);
            System.out.format("LUB of %2d is %2d\n", x, lub);
        }
        Sx.puts();
        return 0;
    }
    
    /**
     * The Kth largest element in a set of K or more elements is defined
     * similarly to the median:
     * It is an entry that is less than at most K-1 other entries, but is
     * not greater than at least K entries.
     * It be equal to many, but less than only K-1, no more. If the set
     * were to be sorted, and all elements were unique, it must be the Kth
     * element from the maximal element. If the set contains duplicates,
     * the Kth largest defines an equivalence class that may contain more
     * than one element.
     * 
     * If m < k elements are greater than some value v = A[i][j] in A,
     * and n > k elements are greater than the next largest value w < v,
     * then v is the kth largest value in A, and A[i][j] is a member of
     * the subset of elements that share the kth largest value.
     * In other words, v is the kth largest iff:
     * [number of elements > v] < k AND [number of elements >= v] >= k
     * which is equivalent to:
     * [number of elements <= v] > |A| - k and [number of elements < v] < |A| - k
     * and gives these special cases (where x is not necessarily a value in A):
     * If [count > x] == k-1, then the kth largest v in A is the largest v <= x.
     * If [count >= x] == k, then the kth largest v in A is the smallest v >= x.
     * (Here [count > x] means |{v : v in A and v > x}|, the number of elements in A
     * that are greater than x.)
     * 
     * Binary search strategies:
     * S1: partition the search using numbers x that are not necessarily values in A,
     * and use the special cases above to terminate the search.
     * 
     * S2: partition the search using values v that are actually in A.
     * Terminate the search when:
     * [count > v] == k-1 (return v and/or i,j s.t. v = A[i][j]), OR
     * [count >= v] == k (ditto above), OR
     * [count > v] < k and [count >= v] >= k (return v and/or unique i,j
     * s.t. v = A[i][j] and this i,j pair has not already been returned,
     * OR, return v and or an iterator or some other set that contains
     * only the valid pairs if i,j).
     */
    public static int binarySearchForKthLargest_recursive(int AA[][], int nRows, int nCols,
            int first, int last, int k)
    {
        if (first > last) {
            return 100 * AA[nRows - 1][nCols - 1];
        }
        int mid = (first + last) >> 1;       // average of first and last values
        int countNLT = countNotLessThan(AA, nRows, nCols, mid);
        if (countNLT == k) {
            // return the first value in AA that is >= mid
            return findLeastUpperBound(AA, nRows, nCols, mid);
        }
        else if (countNLT > k) {
            int countGT = countGreaterThan(AA, nRows, nCols, mid);
            if (countGT < k) {
                // return the first value in AA that is >= mid
                return findLeastUpperBound(AA, nRows, nCols, mid);
            }
            Sx.debug(2, mid + " gives count > k: search [" + (mid + 1) + " .. " + last + "]");
            return binarySearchForKthLargest_recursive(AA, nRows, nCols, mid + 1, last, k);
        }
        else {
            return binarySearchForKthLargest_recursive(AA, nRows, nCols, first, mid - 1, k);
        }
    }
    
    public static int binarySearchForKthSmallest_recursive_inet(int AA[][], int nRows, int nCols,
            int first, int last, int k)
    {
        if (first > last) {
            return 1000 * (1 + AA[nRows - 1][nCols - 1]);
        }
        int mid = (first + last) >> 1;       // average of first and last values
        
        int x = countNotGreaterThan_startBottomLeft(AA, nRows, nCols, mid);
        if (x == k)
            return Math.min(mid,
                    binarySearchForKthSmallest_recursive_inet(AA, nRows, nCols, first, mid - 1, k));
        else if (x > k)
            return binarySearchForKthSmallest_recursive_inet(AA, nRows, nCols, first, mid - 1, k);
        else
            return binarySearchForKthSmallest_recursive_inet(AA, nRows, nCols, mid + 1, last, k);
    }
    
    public static int test_binarySearchForKthLargest_recursive(int AA[][], int minOrdinal,
            int maxOrdinal)
    {
        if (AA == null) {
            int sqr = 7;
            AA = Arrays2d.makeRandomRowColSortedArray(sqr, sqr, 100, 10, 1);
        }
        int nRows = AA.length, nCols = AA[0].length, size = nRows * nCols;
        int minVal = AA[0][0];
        int maxVal = AA[nRows - 1][nCols - 1];
        int first = Math.max(minOrdinal, 1);
        int last = Math.min(maxOrdinal, nRows * nCols);
        first = size / 2 - 2;
        last = size / 2 + 2;
        int med = medianOfAA_mergeArrays(AA);
        Sx.putsArray(AA);
        Sx.puts("Actual median is " + med);
        for (int k = first; k <= last; k++) {
            int kth = binarySearchForKthSmallest_recursive_inet(AA, nRows, nCols, minVal, maxVal,
                    size + 1 - k);
            System.out.format("test_bsKth_inet[%d .. %d]    %dth / %d is %d\n", minVal, maxVal, k,
                    size, kth);
        }
        Sx.puts();
        for (int k = first; k <= last; k++) {
            int kth = binarySearchForKthLargest_recursive(AA, nRows, nCols, minVal, maxVal, k);
            System.out.format("test_bsKthLargest[%d .. %d]  %dth / %d is %d\n", minVal, maxVal, k,
                    size, kth);
        }
        return 0;
    }
    
    public static int medianOfAA_binarySearchKth(final int AA[][])
    {
        int nCols = checkArray2(AA);
        int nRows = AA.length;
        int size = nRows * nCols;
        boolean oddSize = (size % 2 != 0);
        int ord = (size + 1) / 2;
        if (oddSize) {
            return binarySearchForKthLargest_recursive(AA, nRows, nCols, AA[0][0],
                    AA[nRows - 1][nCols - 1], ord);
        } else {
            //int [2] rowCol = binarySearchForKthLargest_recursive_rc(AA, nRows, nCols, AA[0][0], AA[nRows-1][nCols-1], ord);
            //return ( AA[row][col] + Math.min(AA[row][col+1], AA[row+1][col]) ) / 2;
            int kth = binarySearchForKthLargest_recursive(AA, nRows, nCols, AA[0][0],
                    AA[nRows - 1][nCols - 1], ord);
            int kp1 = binarySearchForKthLargest_recursive(AA, nRows, nCols, AA[0][0],
                    AA[nRows - 1][nCols - 1], ord + 1);
            return (kth + kp1) / 2;
        }
    }
    
    /******************************************************
     * struct/class pt{x,y}
     * boolean x_present[m]
     * boolean y_present[n]
     * Priority queue q
     * q.enqueue(pt(0,0))//Prioritizes values based on a[i][j]...minheap
     * while(k>0){
     * pt(i,j)=q.removeMin()
     * if(!x_present[i+1] && !y_present[j]){
     * q.enqueue(pt(i+1,j))
     * x_present[i+1]=true
     * y_present[j]=true
     * }
     * if(!x_present[i] && !y_present[j+1]){
     * q.enqueue(pt(i,j+1))
     * x_present[i]=true
     * y_present[j+1]=true
     * }
     * x_present[i]=false
     * y_present[j]=false
     * k--;
     * }
     * return a[i][j]
     ********************************************************/
    
    static void lucky13(Integer k)
    {
        k = 13;
    }
    
    static void lucky13(Wrap w)
    {
        w.val = 13;
    }
    
    public static int test_wrappedInteger()
    {
        Integer four = 4;
        lucky13(4);
        lucky13(four);
        Sx.puts("four: " + four);
        Wrap five = new Wrap(5);
        lucky13(five);
        Sx.puts("five: " + five.val);
        return 0;
    }
    
    public static int test_queueWalkKth(int AA[][])
    {
        for (int k = 1; k <= AA.length * AA[0].length; k++) {
            ArrayEntryInt2 ent = findKthSmallest(AA, k);
            System.out.format("findKthSmallest %3d: vrc  %3d  %3d  %3d\n", k, ent.mVal, ent.mRow,
                    ent.mCol);
        }
        return 0;
    }
    
    public static int unit_test(int level)
    {
        String testName = Medians.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        int J55[][] =   // median is 13 at ordinal(5,2) -- the last possible possition!
        { { 1, 2, 3, 13, 15 }
                , { 4, 5, 6, 24, 25 }
                , { 7, 8, 9, 34, 35 }
                , { 10, 11, 13, 44, 45 }  // 13 at (5,3) does not work using this pattern
                , { 12, 13, 53, 54, 55 }  // in fact, the median 1st appearing at (5,3) is impossible.
        };
        int J66[][] =
        { { 1, 2, 3, 14, 15, 16 }
                , { 4, 5, 6, 17, 23, 26 }
                , { 7, 8, 9, 18, 35, 36 }
                , { 10, 11, 43, 44, 45, 46 }
                , { 12, 13, 53, 54, 55, 56 }
                , { 62, 63, 63, 64, 65, 66 }
        };
        int stat = 0;
        
        if (level > 0) {
            int nIncs = 7;
            stat += test_counts();
            stat += test_wrappedInteger();
            stat += test_findBoundingEntry(J55, 40, nIncs);
            stat += test_queueWalkKth(J66);
            stat += test_binarySearchForKthLargest_recursive(J55, 11, 15);
            stat += test_medianFromSortedRowsAndColumns(1);
            //stat += test_timeMedianOfRowColSortedArrays(nRows, nCols);
        }
        
        stat += test_timeMedianOfRowColSortedArrays(3);
        Sx.puts(testName + " END");
        return stat;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
}

/**
 * sprax.Medians.unit_test
 * 1 2 3 3
 * 2 4 4 5
 * 3 4 5 5
 * 4 5 5 6
 * 6 6 8 9
 * Counts relative to 0: 0< 0<= 0== 20>= 20>
 * Counts relative to 1: 0< 1<= 1== 20>= 19>
 * Counts relative to 2: 1< 3<= 2== 19>= 17>
 * Counts relative to 3: 3< 6<= 3== 17>= 14>
 * Counts relative to 4: 6< 10<= 4== 14>= 10>
 * Counts relative to 5: 10< 15<= 5== 10>= 5>
 * Counts relative to 6: 15< 18<= 3== 5>= 2>
 * Counts relative to 7: 18< 18<= 0== 2>= 2>
 * Counts relative to 8: 18< 19<= 1== 2>= 1>
 * Counts relative to 9: 19< 20<= 1== 1>= 0>
 * Counts relative to 10: 20< 20<= 0== 0>= 0>
 * 1 2 3 13 15
 * 4 5 6 24 25
 * 7 8 9 34 35
 * 10 11 13 44 45
 * 12 13 53 54 55
 * Actual median is 13
 * LUB of 40 is 44
 * LUB of 41 is 44
 * LUB of 42 is 44
 * LUB of 43 is 44
 * LUB of 44 is 44
 * LUB of 45 is 45
 * LUB of 46 is 53
 * LUB of 47 is 53
 * 
 * 1 2 3 13 15
 * 4 5 6 24 25
 * 7 8 9 34 35
 * 10 11 13 44 45
 * 12 13 53 54 55
 * Actual median is 13
 * test_bsKth_inet[1 .. 55] 10th / 25 is 15
 * test_bsKth_inet[1 .. 55] 11th / 25 is 13
 * test_bsKth_inet[1 .. 55] 12th / 25 is 56000
 * test_bsKth_inet[1 .. 55] 13th / 25 is 56000
 * test_bsKth_inet[1 .. 55] 14th / 25 is 12
 * 
 * test_bsKthLargest[1 .. 55] 10th / 25 is 15
 * test_bsKthLargest[1 .. 55] 11th / 25 is 13
 * test_bsKthLargest[1 .. 55] 12th / 25 is 13
 * test_bsKthLargest[1 .. 55] 13th / 25 is 13
 * test_bsKthLargest[1 .. 55] 14th / 25 is 12
 * test_medianFromSortedRowsAndColumns
 * median of ZZZ[0] is 13 merge_Nwise
 * median of ZZZ[0] is 13 binary search
 * youngf of ZZZ[0] is 13 youngify
 * median of ZZZ[1] is 20 merge_Nwise
 * median of ZZZ[1] is 20 binary search
 * youngf of ZZZ[1] is 20 youngify
 * AA minVal & maxVal = 1000 & 49087
 * 
 * WORK:
 * medianOfAA_mergeArraysPairs(1025, 1025) minVal 1000 maxInc 32 -> 23564 t: 8370 6346 6390 6288
 * medianOfAA_mergeArrays (1025, 1025) minVal 1000 maxInc 32 -> 23564 t: 119 116 117 118
 * medianOfAA_mergeArraysPq (1025, 1025) minVal 1000 maxInc 32 -> 23564 t: 432 436 408 402
 * medianOfAA_mergeArraysPqSl (1025, 1025) minVal 1000 maxInc 32 -> 23564 t: 231 228 219 210
 * medianOfAA_binSearchKth (1025, 1025) minVal 1000 maxInc 32 -> 23564 t: 1 2 0 1
 * medianOfAA_youngify (1025, 1025) minVal 1000 maxInc 32 -> 23564 t: 25454 25416 24897 25468
 * 
 * HOME:
 * medianOfAA_mergeArraysPairs(1025, 1025) minVal 1000 maxInc 32 -> 23581 t: 6422 6234 5703 6375
 * medianOfAA_mergeArrays (1025, 1025) minVal 1000 maxInc 32 -> 23581 t: 109 110 125 125
 * medianOfAA_mergeArraysPq (1025, 1025) minVal 1000 maxInc 32 -> 23581 t: 422 422 391 407
 * medianOfAA_mergeArraysPqSl (1025, 1025) minVal 1000 maxInc 32 -> 23581 t: 219 218 203 203
 * medianOfAA_binSearchKth (1025, 1025) minVal 1000 maxInc 32 -> 23581 t: 0 0 0 0
 * medianOfAA_youngify (1025, 1025) minVal 1000 maxInc 32 -> 23581 t: 19125 18875 20375 21859
 */

