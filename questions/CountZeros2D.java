package sprax.questions;

import java.util.Random;

import sprax.arrays.RandomArray;
import sprax.search.BinarySearch;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * <pre>
 * You are given a matrix with N rows and N columns of non-negative integers. 
 * Each row and column of matrix is sorted in ascending order. 
 * Find number of 0-s in the given matrix. 
 * Example:
 *     0 0 1
 *     0 1 1
 *     1 1 1
 * Answer: 3
 * 
 *     0 0
 *     0 9
 * Answer: 3
 * Possibly a Google interview or CodeJam question.
 * Solution by Sprax Lines  2016.06.27
 */
public class CountZeros2D
{
    /** Naive count as if the array were not sorted at all: O(MN), or for NxN matrix, Theta(N^2) */
    public static int countZerosNonSortedNaive(int array[][])
    {
        assert(array != null && array[0] != null);      // GIGO: not checking much.
        int count = 0;
        for (int j = 0; j < array.length; j++) {
            for (int k = 0; k < array[j].length; k++) {
                if (array[j][k] == 0) {
                    count++;
                }
            }
        }
        return count;
    }
    
    /** 
     * Naive count of 0s as if each row were sorted independently of the rest: O(M + Z) where Z == number of zeros.
     * Thus worst case O(MN), best case O(1). 
     */
    public static int countZerosRowsSorted(int array[][])
    {
        assert(array != null && array[0] != null);      // GIGO: not checking much.
        int count = 0;
        for (int j = 0; j < array.length; j++) {
            for (int k = 0; k < array[j].length; k++) {
                if (array[j][k] == 0) {
                    count++;
                }
                else {
                    break;      // rest of row must be non-zero
                }
            }
        }
        return count;
    }
    
    /**
     * Count of 0s assuming both rows and columns are sorted (and of course minimum allows value == 0).
     * Complexity: O(M + Z) where Z == number of zeros, so worst case O(MN), best case O(1)
     */
    public static int countZerosBothSorted(int array[][])
    {
        assert(array != null && array[0] != null);      // GIGO: not checking much.
        int count = 0, endRowZeros = array[0].length;
        for (int j = 0; j < array.length; j++) {
            for (int k = 0; k < endRowZeros; k++) {
                if (array[j][k] == 0) {
                    count++;
                }
                else {
                    if (k == 0) {
                        return count;   // no more zeros in matrix
                    }
                    endRowZeros = k;
                    break;              // no more zeros in this row
                }
            }
        }
        return count;
    }
    
    /** 
     * Uses binary-search to find the number of leading zeros in each row, which is bounded by
     * the number of zeros in the previous row.  
     * Complexity: O(MlogN) worst case, and best case O(1) when Z is close to 1. 
     */
    public static int countZerosBothSortedBinSearch(int array[][])
    {
        assert(array != null && array[0] != null);      // GIGO: not checking much.
        int count = 0, idxFirstNonZero = array[0].length;
        for (int j = 0; j < array.length; j++) {
            idxFirstNonZero = BinarySearch.indexOfFirstNonZeroValue(array[j], 0, idxFirstNonZero);
            if (idxFirstNonZero == 0) {
                return count;           // no more zeros in matrix
            }
            count += idxFirstNonZero;
        }
        return count;
    }
    
    protected static int countZerosBothSortedBinSearch(int array[][]
            , int rowBeg, int colBeg, int rows, int cols)
    {
        int count = 0, colEnd = colBeg + cols;
        for (int j = rowBeg; j < rowBeg + rows; j++) {
            colEnd = BinarySearch.indexOfFirstNonZeroValue(array[j], colBeg, colEnd);
            if (colBeg == colEnd)
                return count;           // no more 0s in matrix
            count += (colEnd - colBeg);
        }
        return count;
    }
    
    /** 
     * Even faster: Use binary search on the diagonal values of the biggest square sub-matrix,
     * then recurse on the leftovers (the sub-matrix outside the biggest square).  When the 
     * remaining rectangles are small, revert to binary searches on the rows and return early
     * on the first row starting with non-zero.
     * Complexity for MxN matrix, assuming M > N and L = M - N:  O(logN) + O(logL * logN)
     * @see that doc string in the recent Python script
     */
    public static int countZerosBothSortedBinSearchDiag(int array[][])
    {
        assert(array != null && array[0] != null);      // GIGO: not checking much.
        int rows = array.length;
        int cols = array[0].length;
        assert(rows > 0 && cols > 0);
        return countZerosBothSortedBinSearchRectDiag(array, 0, 0, rows, cols);
    }    

    protected static int countZerosBothSortedBinSearchRectDiag(int array[][]
            , int rowBeg, int colBeg, int rows, int cols)
    {
        // special case thin rectangle
        if (rows < BinarySearch.SMALL_SIZE || cols < BinarySearch.SMALL_SIZE) {
            return countZerosBothSortedBinSearch(array, rowBeg, colBeg, rows, cols);
        }

        if (rows == cols)
            return countZerosBothSortedBinSearchSquareDiag(array, rowBeg, colBeg, rows);
        
        int count = 0, sideSize = rows;
        if (rows > cols) {
            sideSize = cols;
            count += countZerosBothSortedBinSearch(array, rowBeg + cols, colBeg, rows - cols, cols);
        } 
        else if (cols > rows){
            count += countZerosBothSortedBinSearch(array, rowBeg, colBeg + rows, rows, cols - rows);
        }
        count += countZerosBothSortedBinSearchSquareDiag(array, rowBeg, colBeg, sideSize);
        return count;
    }
    
    /** 
     * Faster handling for square matrix, basically O(logN)
     */
    protected static int countZerosBothSortedBinSearchSquareDiag(int array[][], int rowBeg, int colBeg, int sideSize)
    {   
        // special case small square
        if (sideSize < BinarySearch.SMALL_SIZE) {
            return countZerosBothSortedBinSearch(array, rowBeg, colBeg, sideSize, sideSize);
        }
        
        int count = 0;
        int offset = offsetToFirstNonZeroValueSquareDiag(array, rowBeg, colBeg, sideSize);
        count += offset*offset;
        if (0 < offset && offset < sideSize) {
            count += countZerosBothSortedBinSearchRectDiag(array, rowBeg, colBeg + offset, offset, sideSize - offset);
            count += countZerosBothSortedBinSearchRectDiag(array, rowBeg + offset, colBeg, sideSize - offset, offset);
        }
        return count;
    }
  
    /**
     * returns the diagonal offset to the first non-zero array value, starting from (row, col) = (rowBeg, colBeg)
     * @param sorted    matrix of non-negative integers with all 0s at the beginnings of rows and each row having
     *                  no more leading zeros than the previous one.  If the array is sorted in both rows and 
     *                  columns, it will meet these criteria, but it does not have to be sorted.  In other words,
     *                  the zeros must all be above or left of all non-zeros, but the non-zeros can be in any
     *                  order at all among themselves.  
     * @param rowBeg
     * @param colBeg
     * @param sideSize  size of the sides of the square sub-matrix inside sorted[][]
     * @return
     */
    public static int offsetToFirstNonZeroValueSquareDiag(int[][] sorted, int rowBeg, int colBeg, int sideSize)
    {
        int lo = 0, hi = sideSize - 1;
        
        // special case:
        if (sorted[lo + rowBeg][lo + colBeg] > 0)
            return lo;
        else if (sorted[hi + rowBeg][hi + colBeg] == 0)
            return hi + 1;
        
        for (int md; lo < hi; ) {
            md = (hi + lo) >> 1;        // same as lo + (hi - lo)/2
            if (sorted[md + rowBeg][md + colBeg] > 0)
                hi = md - 1;
            else
                lo = md + 1;
        }
        if (sorted[lo + rowBeg][lo + colBeg] > 0)
            return lo;
        else
            return hi + 1;
    }
    
    
   
    public static int test_all_against_naive(int array[][], int rows, int cols)
    {
        String testName = CountZeros2D.class.getName() + ".test_all_against_naive";
        Sz.begin(testName);
        int numWrong = 0;        
        
        Sx.format("Input array, %d rows X %d columns:\n", rows, cols);
        if (rows < 100 && cols < 100)
            Sx.putsArray(array);
        else
            Sx.puts ("...too big, not printing...");
        
        int countNaive = countZerosNonSortedNaive(array);
        int countSortR = countZerosRowsSorted(array);
        int countSortB = countZerosBothSorted(array);
        int countSortS = countZerosBothSortedBinSearch(array);
        int countSortD = countZerosBothSortedBinSearchDiag(array);
        Sx.format("Count of zeros: naive %d, row-sorted %d, bi-sorted %d, bin-search %d, diag-bin-search %d\n"
                , countNaive, countSortR, countSortB, countSortS, countSortD);
        numWrong += Sz.oneIfDiff(countNaive, countSortR);
        numWrong += Sz.oneIfDiff(countNaive, countSortB);
        numWrong += Sz.oneIfDiff(countNaive, countSortS);
        numWrong += Sz.oneIfDiff(countNaive, countSortD);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int unit_test()
    {
        String testName = CountZeros2D.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;        
        
        int rows = 97, cols = 83, minVal = 0, maxVal = 1;
        long seed = System.currentTimeMillis();
        Random rng = new Random(seed);       
        int array[][] = RandomArray.makeBiSortedRandomIntArray2d(rows, cols, minVal, maxVal, rng);

        numWrong += test_all_against_naive(array, rows, cols);
                    
        array = RandomArray.makeZeroFillBiSortedRandomIntArray2d(rows, cols, maxVal, rng);
        numWrong += test_all_against_naive(array, rows, cols);
        
        rows = 11;
        cols = 47;
        array = RandomArray.makeZeroFillBiSortedRandomIntArray2d(rows, cols, maxVal, rng);
        numWrong += test_all_against_naive(array, rows, cols);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}
