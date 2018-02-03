package sprax.arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sprax.sprout.Sx;

public class FindTriplets 
{
    /**
     * findDifferenceTripletsWithIndices
     * Given a const array of size N, output all triplets <a,b,c> such that a-b = c.
     * Expected time is O(N^2), additional space O(N).
     * 
     * Note: For this algorithm, a pre-sorted array with no duplicates is not 
     * expected to run significantly faster than a non-sorted one with duplicates.
     * 
     * If we know the input array is sorted, then all duplicates would be
     * contiguous, so we could just store their ranges, 
     * replacing: Map<Integer, ArrayList<Integer>> val2idxList
     * with:       Map<Integer, Integer[]>> val2idxRange 
     * (and even replace the Integer[] with Pair<Integer> if you have it).
     * But what that saves is not so significant.
     * 
     * If we know there are no duplicate values in the input array, we can 
     * replace: Map<Integer, ArrayList<Integer>> val2idxList
     * with:    Map<Integer, Integer>            val2idx
     * That's better, but of course it's still O(N).
     * 
     * If the additional space is limited to O(1), we could use binary search 
     * to look up the diff's in place, but then the expected time is O(N^2 log N).  
     * Storing anything to make the lookup faster will be at least O(N) space 
     * (or O(N^2) if you store differences), so don't do it.  
     * The hashmap solution is good enough.
     * 
     */
    static void findDifferenceTripletsWithIndices(final int arr[])
    {
        // 1st pass: Map each array value to a list of indices for that value 
        Map<Integer, ArrayList<Integer>> val2idxList = new HashMap<Integer, ArrayList<Integer>>();
        for (int j = 0; j < arr.length; j++) {
            if ( ! val2idxList.containsKey(arr[j])) {
                val2idxList.put(arr[j], new ArrayList<Integer>());
            }
            val2idxList.get(arr[j]).add(j);
        }    
        // 2nd pass: for each difference (with indices j & k), 
        // lookup set of indices {m} for array value == difference 
        // and print the triples <j, k, {m}>
        for (int q = 0, j = 0; j < arr.length; j++) {
            for (int k = 0; k < arr.length; k++) {
                int dif = arr[j] - arr[k];
                if (val2idxList.containsKey(dif)) {
                    for ( int idx : val2idxList.get(dif)) {
                        System.out.format("Index triplet %2d <%d, %d, %d> gives % 3d - % 3d = % 3d\n"
                                , ++q, j, k, idx, arr[j], arr[k], dif);
                    }
                }
            }
        }
    }
    
    /**
     * Given a const array of size N, output all triplets <a,b,c> 
     * such that a - b = c and a != b and a != c and b != c.
     * Expected time is O(N^2), additional space O(N).
     * @param arr
     */
    static void findDistinctDifferenceTriplets(int A[])
    {
        Map<Integer, ArrayList<Integer>> val2idxList = new HashMap<Integer, ArrayList<Integer>>();
        for (int j = 0; j < A.length; j++) {
            if (A[j] == 0)
                continue;                             // Because a - b == 0 implies a == b
            if ( ! val2idxList.containsKey(A[j]))
                val2idxList.put(A[j], new ArrayList<Integer>());
            val2idxList.get(A[j]).add(j);
        }
        for (int q = 0, j = 0; j < A.length; j++) {
            // Note: (A[j] == 0) is OK, because if a == 0, then a - b == c implies -b == c, not that b == c (and we know c != -c)
            for (int k = 0; k < A.length; k++) {  // Can't just use k > j, because subtraction is non-commutative.
                if (k == j || A[k] == 0)
                    continue;                           // Because if b == 0, then a - b == c implies a == c
                int dif = A[j] - A[k];
                if (val2idxList.containsKey(dif)) {
                    for ( int idx : val2idxList.get(dif)) {
                        if (A[j] != dif && A[k] != dif) {
                            System.out.format("Index triplet %2d <%d, %d, %d> gives % 3d - % 3d = % 3d\n"
                                    , ++q, j, k, idx, A[j], A[k], dif);
                        }
                    }
                }
            }
        }
    }
    
    public static int test_findDifferenceTripletsIndices() 
    {
        int A[] = { -12, -7, -4, 0, 3, 5, 9, 10, 15, 16 };
        Sx.putsArray("findDistinctDifferenceTriplets", A, "");
        findDifferenceTripletsWithIndices(A);
        Sx.putsArray("findDistinctDifferenceTriplets", A, "");
        findDistinctDifferenceTriplets(A);
        
        int B[] = { -12, -7, -4, 0, 4, 5, 5, 9, 10, 15, 16 };
        Sx.putsArray("findDistinctDifferenceTriplets", B, "");
        findDistinctDifferenceTriplets(B);    
        return 0;
    }
    
    public static int test_triplets() 
    {
        return test_findDifferenceTripletsIndices();
    }    
    
    
    /*
     * 
  Tags: Google � Algorithm(1st telephone interview)  � Application Developer
  Question #10103678 (Report Dup) | Edit
  Google Interview Question for Software Engineers about Arrays
  anonymous on August 04, 2011

  Given a sorted array, output all triplets <a,b,c> such that a-b = c. Expected time is O(n^2). 
  My approach using binary search took O(n^2 logn). 
  When you attempt an approach, test your code with this example 
  and list your outputs for verification. Thanks.
  -12, -7, -4, 0, 3, 5, 9, 10, 15, 16
  Tags: Google � Arrays  � Software Engineer
  Question #10094092 (Report Dup) | Edit
  Amazon Interview Question for Software Engineer / Developers about Algorithm
  bar raiser question on August 04, 2011  
  
     */

    
    
    public static int unit_test(int level) 
    {
    	String  testName = FindTriplets.class.getName() + ".unit_test";  
    	Sx.puts(testName + " BEGIN");  
    	int stat = 0;
    	stat += test_triplets();
    	Sx.puts(testName + " END");  
    	return stat;
    }

    public static void main(String[] args)  { unit_test(2); }
}
