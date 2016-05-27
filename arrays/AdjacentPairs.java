package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeMap;

import sprax.Sx;


/**
 * Integer V lies strictly between integers U and W if U < V < W or if U > V > W. 
A non-empty zero-indexed array A consisting of N integers is given. 
A pair of indices (P, Q), where 0 <= P < Q < N, is said to have adjacent values 
if no value in the array lies strictly between values A[P] and A[Q]. 
For example, in array A such that: 
A[0] = 0 A[1] = 3 A[2] = 3 
A[3] = 7 A[4] = 5 A[5] = 3 
A[6] = 11 A[7] = 1 
the following pairs of indices have adjacent values: 
(0, 7), (1, 2), (1, 4), 
(1, 5), (1, 7), (2, 4), 
(2, 5), (2, 7), (3, 4), 
(3, 6), (4, 5), (5, 7). 
For example, indices 4 and 5 have adjacent values because there is no value 
in array A that lies strictly between A[4] = 5 and A[5] = 3; the only such 
value could be the number 4, and it is not present in the array. 
Write a function: 
class Solution { public int adjacent_point_pairs_count(int[] A); } 
that, given a non-empty zero-indexed array A consisting of N integers, returns the number 
of pairs of indices of this array that have adjacent values. The function should return -1 if this number exceeds 100,000,000. 
For example, given array A such that: 
A[0] = 0 A[1] = 3 A[2] = 3 
A[3] = 7 A[4] = 5 A[5] = 3 
A[6] = 11 A[7] = 1 
the function should return 12, as explained in the example above. 
Assume that: 
N is an integer within the range [1..100,000]; 
each element of array A is an integer within the range [-2,147,483,648..2,147,483,647]. 
Complexity: 
expected worst-case time complexity is O(N*log(N)); 
expected worst-case space complexity is O(N), beyond input storage (not counting the storage required for input arguments). 
Elements of input arrays can be modified.
 * @author sprax
 *
 */

/** Two ways of finding all adjacent value pairs in an array: tree-map and sorted count */
public class AdjacentPairs 
{
    /** Uses sorted map of values to indices to find distinct adjacent value pairs */
    public static int countAndPrintPairsOfIndicesWithAdjacentDifferentValues(int A[])
    {
        int pairCount = 0;		// return value
        TreeMap<Integer, ArrayList<Integer>> sortedValuesToIndices = makeMapOfValuesToIndices(A);

        Iterator<Integer> it = sortedValuesToIndices.keySet().iterator();
        int oldVal = it.next();
        while (it.hasNext())
        {
            int newVal = it.next();
            for (int one : sortedValuesToIndices.get(oldVal)) {
                Sx.puts();
                for (int two : sortedValuesToIndices.get(newVal))
                {
                    printPairSmallestFirst(one, two);
                    pairCount++;
                }
            }
            oldVal = newVal;
        }
        if (pairCount > 0) {
            Sx.puts();
        }
        return pairCount;
    }	

    /** Uses sorted map of values to indices to find not necessarily distinct adjacent value pairs */
    public static int countAndPrintPairsOfIndicesWithAdjacentPossiblyRepeatedValues(int A[])
    {
        int pairCount = 0;		// return value
        TreeMap<Integer, ArrayList<Integer>> sortedValuesToIndices = makeMapOfValuesToIndices(A);

        Iterator<Integer> it = sortedValuesToIndices.keySet().iterator();
        int oldVal = it.next();
        while (it.hasNext())
        {
            int newVal = it.next();
            ArrayList<Integer> ones = sortedValuesToIndices.get(oldVal);
            Sx.puts();
            if (ones.size() > 1)
            {
                for (int j = 0; j < ones.size(); j++) {
                    for (int k = j + 1; k < ones.size(); k++) {
                        printPairSmallestFirst(ones.get(j), ones.get(k));
                        pairCount++;
                    }
                }
            }
            for (int one : ones) {
                for (int two : sortedValuesToIndices.get(newVal))
                {
                    printPairSmallestFirst(one, two);
                    pairCount++;
                }
            }
            oldVal = newVal;
        }
        Sx.puts();
        return pairCount;
    }

    public static TreeMap<Integer, ArrayList<Integer>> makeMapOfValuesToIndices(int A[])
    {
        TreeMap<Integer, ArrayList<Integer>> sortedValuesToIndices = new TreeMap<>();
        for (int j = 0; j < A.length; j++)
        {
            int val = A[j];
            if (sortedValuesToIndices.containsKey(val)) {
                sortedValuesToIndices.get(val).add(j);
            } else {
                ArrayList<Integer> ids = new ArrayList<>();
                ids.add(j);
                sortedValuesToIndices.put(val, ids);
            }
        }
        return sortedValuesToIndices;
    }

    static void printPairSmallestFirst(int x, int y)
    {
        if (x > y) {
            Sx.format("(%d, %d) ", y, x);
        } else {
            Sx.format("(%d, %d) ", x, y);
        }
    }


    public static int countPairsOfIndicesWithAdjacentValuesAfterSorting(int A[])
    {
        if (A == null || A.length < 2) {
            return 0;
        }

        int pairCount = 0;
        Arrays.sort(A);
        Sx.putsArray("Sorted: ", A);

        int j, newValue = 0, newCount = 0, oldValue = A[0], oldCount = 1;
        for (j = 1; j < A.length; j++) {
            newValue = A[j];
            if (newValue == oldValue) {
                oldCount++;
            } else {
                newCount = 1;
                break;
            }
        }
        pairCount += nPick2(oldCount);

        for (j = j + 1; j < A.length; j++) {
            if (A[j] == newValue) {
                newCount++;
            } else {
                pairCount += oldCount * newCount + nPick2(newCount);
                oldCount = newCount;
                oldValue = newValue;
                newCount = 1;
                newValue = A[j];
            }
        }
        pairCount += nPick2(newCount);
        return pairCount;
    }

    private static int nPick2(int num) {
        return num * (num - 1) / 2;
    }

    public static int testOneArray(int A[], int level)
    {
        Sx.putsArray("Input array: ", A);

        Sx.print("numPairsOfIndicesWithAdjacentDifferentValues:");
        int pairCount = countAndPrintPairsOfIndicesWithAdjacentDifferentValues(A);
        Sx.format("Pair count: %d\n\n", pairCount);

        Sx.puts("numPairsOfIndicesWithAdjacentPossiblyRepeatedValues:");
        pairCount = countAndPrintPairsOfIndicesWithAdjacentPossiblyRepeatedValues(A);
        Sx.format("Pair count: %d\n\n", pairCount);

        int sortCount = countPairsOfIndicesWithAdjacentValuesAfterSorting(A);
        Sx.format("Sorted pairs: %d\n\n", sortCount);

        return pairCount;
    }

    public static int unit_test(int level)
    {
        String testName = AdjacentPairs.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN\n");

        int A[] = { 1, 1, 1 }; 
        int npA = testOneArray(A, level);
        int B[] = { 0, 3, 3, 7, 5, 3, 11, 1 }; 
        int npB = testOneArray(B, level);

        Sx.puts(testName + " END,  status: PASSED");
        return 0;
    }

    public static void main(String args[]) { unit_test(1); }
}
