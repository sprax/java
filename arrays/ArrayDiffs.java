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
    /**
     * returns the minimal absolute difference between any two array elements, or -1 if there is no
     * such number. Expected time O(NlogN) from dual-pivot Quicksort, no extra space. This version
     * was optimized for readability.
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
    
    /**
     * returns the minimal absolute difference between any two array elements, or -1 if there is no
     * such number. Expected time O(NlogN) from dual-pivot Quicksort, no extra space. This version
     * was optimized for speed.
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
    public static int sumOfSquaredDifferences(int iA[], int iB[])
    {
        if (iA == null || iB == null || iA.length != iB.length) {
            throw new IllegalArgumentException("null or mismatched array");
        }
        int sum = 0;
        for (int val, i = 0; i < iA.length; i++) {
            val = iA[i] - iB[i];
            sum += val * val;
        }
        return sum;
    }
    
    /**
     * Return sum of squares of element by element array differences.
     */
    public static int sumOfSquaredDifferences(int iA[][], int iB[][])
    {
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
    
    /**
     * return maximal absolute difference between elements in different 
     * partitions of A: A[0 .. k] and A[k+1 .. N-1] where 0 <= k < N-1 and N > 1
     */
    public static int maxAbsPartitionDiff(int A[])
    {
        // handle bad input
        if (A == null || A.length < 2)
            return -1;
        
        int maxAbsDiff = 0; // The result to return
        
        // First pass: store left-maxima (maximum value so far, left-to-right) in an extra array
        int B[] = new int[A.length - 1];
        int max = B[0] = A[0]; // we know A.length > 1
        for (int j = 1; j < A.length - 1; j++) {
            if (max < A[j])
                max = A[j];
            B[j] = max;
        }
        
        // Second pass in reverse order: compute difference between max up to k
        // and each element right of k, keeping the max abs diff.
        for (int k = A.length - 1; --k >= 0;) {
            int absDiff = Math.abs(B[k] - A[k + 1]);
            if (maxAbsDiff < absDiff)
                maxAbsDiff = absDiff;
        }
        return maxAbsDiff;
    }
    
    /**
     * return maximal absolute difference between maximal elements in different 
     * partitions of A: A[0 .. k] and A[k+1 .. N-1] where 0 <= k < N-1 and N > 1
     * 
     */
    public static int maxAbsDiffPartitionMaxes(int A[])
    {
        // handle bad input
        if (A == null || A.length < 2)
            return -1;
        
        int maxAbsDiff = 0; // The result to return
        
        // First pass: store left-maxima (maximum value so far, left-to-right) in an extra array
        int B[] = new int[A.length];
        int max = B[0] = A[0]; // we know A.length > 1
        for (int j = 1; j < A.length - 1; j++) {
            if (max < A[j])
                max = A[j];
            B[j] = max;
        }
        
        // Second pass in reverse order: subtract right maxima from left maxima 
        // and store the max abs value.
        max = A[A.length - 1];
        for (int k = A.length - 1; --k >= 0;) {
            if (max < A[k + 1])
                max = A[k + 1];
            int absDiff = Math.abs(B[k] - max);
            if (maxAbsDiff < absDiff)
                maxAbsDiff = absDiff;
        }
        return maxAbsDiff;
    }
    
    static int AA[] = { 1, -2, 3, -5, 4, 5, -7, 9, -8, 0 };
    static int BB[] = { -2, 5, 1, -5, -7, 9, 0, -8, 3, 4 };    // permutation of AA
    static int CC[] = { 3, -7, 3, 0, 11, -4, -7, 17, -11, -4 }; // { AA - BB }
                                                                
    public static int unit_test()
    {
        String testName = ArrayDiffs.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numCases = 0, numWrong = 0;
        
        numCases++;
        int absDiff = absDiff(AA, BB);
        numWrong += Sz.showWrong(absDiff, 66);
        
        numCases++;
        int sumSqrs = sumOfSquaredDifferences(AA, BB);
        numWrong += Sz.showWrong(sumSqrs, 674);

        numCases++;
        int maxAbs = maxAbsPartitionDiff(AA);
        numWrong += Sz.showWrong(maxAbs, 17);

        numCases++;
        int absMax = maxAbsDiffPartitionMaxes(AA);
        numWrong += Sz.showWrong(absMax, 9);
        
        Sz.ender(testName, numCases, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}
