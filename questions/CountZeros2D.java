package sprax.questions;

import java.util.HashMap;

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
    
    
    public static int unit_test()
    {
        String testName = CountZeros2D.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}
