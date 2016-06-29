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
            idxFirstNonZero = BinarySearch.indexOfFirstNonZeroValue(array[j], idxFirstNonZero);
            if (idxFirstNonZero == 0) {
                return count;           // no more zeros in matrix
            }
            count += idxFirstNonZero;
        }
        return count;
    }
    
    /** TODO: Even faster: possible O(logM * logN) using binary search on diagonal values 
     * @see that doc string in the recent Python script
     */
    
    
    public static int unit_test()
    {
        String testName = CountZeros2D.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;        
        
        int rows = 77, cols = 83, minVal = 0, maxVal = 1;
        long seed = System.currentTimeMillis();
        Random rng = new Random(seed);       
        int arrayA[][] = RandomArray.makeBiSortedRandomIntArray2d(rows, cols, minVal, maxVal, rng);
        Sx.format("Input array, %d rows X %d columns:\n", rows, cols);
        Sx.putsArray(arrayA);
        int countNaive = countZerosNonSortedNaive(arrayA);
        int countSortR = countZerosRowsSorted(arrayA);
        int countSortB = countZerosBothSorted(arrayA);
        int countSortS = countZerosBothSortedBinSearch(arrayA);
        Sx.format("Count of zeros: naive %d, row-sorted %d, bi-sorted %d, bin-search %d\n"
                , countNaive, countSortR, countSortB, countSortS);
        numWrong += Sz.oneIfDiff(countNaive, countSortR);
        numWrong += Sz.oneIfDiff(countNaive, countSortB);
        numWrong += Sz.oneIfDiff(countNaive, countSortS);
        
        // Restart the RNG
        rng = new Random(seed);       
        int arrayB[][] = RandomArray.makeZeroFillBiSortedRandomIntArray2d(rows, cols, maxVal, rng);
        Sx.format("Input array, %d rows X %d columns:\n", rows, cols);
        Sx.putsArray(arrayB);
        countNaive = countZerosNonSortedNaive(arrayB);
        countSortR = countZerosRowsSorted(arrayB);
        countSortB = countZerosBothSorted(arrayB);
        countSortS = countZerosBothSortedBinSearch(arrayB);
        Sx.format("Count of zeros: naive %d, row-sorted %d, bi-sorted %d, bin-search %d\n"
                , countNaive, countSortR, countSortB, countSortS);
        numWrong += Sz.oneIfDiff(countNaive, countSortR);
        numWrong += Sz.oneIfDiff(countNaive, countSortB);
        numWrong += Sz.oneIfDiff(countNaive, countSortS);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}
