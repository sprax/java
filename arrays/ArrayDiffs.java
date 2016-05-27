// ArrayAlgo.java       Author: Sprax LInes   2011.11
package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import sprax.Sx;
import sprax.Sz;
import std.StdStats;


public class ArrayDiffs
{
    /** returns the minimal absolute difference between any two array elements,
     * or -1 if there is no such number. 
     * Expected time O(NlogN) from dual-pivot Quicksort, no extra space. 
     * This version was optimized for readability.
     */
    int minimalAbsDifference(int array[])
    {
        if (array == null || array.length < 2)
            return -1;
        Arrays.sort(array);
        int min = Integer.MAX_VALUE;
        for (int j = 1; j < array.length; j++) {
            int dif = array[j] - array[j - 1];
            if (dif < 0)
                dif = -dif;
            if (min > dif)
                min = dif;
        }
        return min;
    }
    
    /** returns the minimal absolute difference between any two array elements,
     * or -1 if there is no such number. 
     * Expected time O(NlogN) from dual-pivot Quicksort, no extra space.
     * This version was optimized for speed. 
     */
    int minimalAbsDifferenceOptimized(int array[])
    {
        if (array == null || array.length < 2)
            return -1;
        Arrays.sort(array);
        int min = Integer.MAX_VALUE;
        int j = 1, prev = array[0];
        do {
            int curr = array[j];
            int diff = Math.abs(curr - prev);
            if (min > diff)
                min = diff;
            prev = curr;
        } while (j < array.length);
        return min;
    }
    

    /**
     * Return sum of squares of element by element array differences.
     */
    public static int sumOfSquaredDifferences(int iA[], int iB[]) {
        if (iA == null || iB == null || iA.length != iB.length) {
            throw new IllegalArgumentException("null or mismatched array");
        }
        int sum = 0;
        for (int val, i = 0; i < iA.length; i++) {
            val = iA[i] - iB[i];
            sum += val*val;
        }
        return sum;
    }
    
    /**
     * Return sum of squares of element by element array differences.
     */
    public static int sumOfSquaredDifferences(int iA[][], int iB[][]) {
        if (iA == null || iB == null || iA.length != iB.length) {
            throw new IllegalArgumentException("null of mismatched array");
        }
        int sum = 0;
        for (int i = 0; i < iA.length; i++) {
            sum += sumOfSquaredDifferences(iA[i], iB[i]);
        }
        return sum;
    }


	
	public static int absDiff(int iA[], int iB[]) 
	{
	   if (iA == null || iB == null)
	       throw new IllegalArgumentException("null input(s)");
	   if (iA.length != iB.length)
	       throw new IllegalArgumentException("unequal lengths");
	   int answer = 0;
	   for (int j = 0; j < iA.length; j++)
	       answer += Math.abs(iA[j] - iB[j]);
	   return answer;
	}
	
    static int AA[] = {   1,  -2,   3,  -5,   4,   5,  -7,   9,  -8,   0 };
    static int BB[] = {  -2,   5,   1,  -5,  -7,   9,   0,  -8,   3,   4 };   // permutation of AA
    static int CC[] = {   3,  -7,   3,   0,  11,  -4,  -7,  17, -11,  -4 };   // { AA - BB }

	public static int unit_test(int level) 
	{
		String  testName = ArrayDiffs.class.getName() + ".unit_test";  
		Sx.format("BEGIN %s\n", testName);  
		int numWrong = 0;

		int absDiff = absDiff(AA, BB);
        numWrong += Sz.showWrong(absDiff, 66);

        int sumSqrs = sumOfSquaredDifferences(AA, BB);
        numWrong += Sz.showWrong(sumSqrs, 674);

		Sx.format("END %s,  wrong: %d, %s\n", testName, numWrong, Sz.passFail(numWrong));  
		return numWrong;
	}

	public static void main(String[] args)
	{
		unit_test(2);
	}

}

