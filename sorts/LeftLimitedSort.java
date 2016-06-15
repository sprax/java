package sprax.sorts;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Queue;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
* Given an array of length N and an integer K, sort the array as much as possible
 * such that no element travels more than k positions to its left - an element 
 * however can travel as much as it likes to its right.
 * @author sprax
 */
public class LeftLimitedSort<T extends Comparable<T>>
{
    /**
     * Algorithm:
     * Sort only within a leftLimit-sized moving window, basically
     * a leftLimit-size min-heap moving left to right.
     * Implementation: Uses PriorityQueue as the implementation of a min-heap.
     * @param iA
     * @param leftLimit
     */
    public static <T> void leftLimitedSort(T[] iA, int leftLimit)
    {
        if (iA == null || iA.length < 2 || leftLimit < 1)
            return;
        
        if (iA.length <= leftLimit) {
            Arrays.sort(iA);
            return;
        }
            
        Queue<T> limq = new PriorityQueue<>(leftLimit);
        for (int k = 0; k < leftLimit; k++)
            limq.add(iA[k]);
        for (int k = 0; k < iA.length; k++) {
            iA[k] = limq.remove();
            if (k + leftLimit < iA.length) {
                limq.add(iA[k + leftLimit]);
            }
        }
        assert(limq.isEmpty());
    }
    
     
    public static class CurrentClassGetter extends SecurityManager
    {
        public String getClassName() {
            return getClassContext()[1].getName();
        }
    }
    
    public static int unit_test(int level)
    {
        //  System.getSecurityManager().getClassContext()[0].getName();
        CurrentClassGetter ccg = new CurrentClassGetter();
        String testName = ccg.getClassName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Integer iA[] = { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, -5, -4, -3, -1, -2, -6, -7, -8 };
        int len = iA.length;
        Sx.format("Input Integer array of size %d:\n", len);
        Sx.putsArray(iA);
        Sx.puts();
        
        int limit = 5;
        Integer cA[] = Arrays.copyOf(iA, iA.length);
        leftLimitedSort(cA, limit);
        Sx.format("Output Integer array after sorting with leftward movement limited to %d positions:\n", limit);
        Sx.putsArray(cA);
         
        limit = 10;
        cA = Arrays.copyOf(iA, iA.length);
        leftLimitedSort(cA, limit);
        Sx.format("Output Integer array after sorting with leftward movement limited to %d positions:\n", limit);
        Sx.putsArray(cA);

        limit = 15;
        cA = Arrays.copyOf(iA, iA.length);
        leftLimitedSort(cA, limit);
        Sx.format("Output Integer array after sorting with leftward movement limited to %d positions:\n", limit);
        Sx.putsArray(cA);

        limit = 20;
        cA = Arrays.copyOf(iA, iA.length);
        leftLimitedSort(cA, limit);
        Sx.format("Output Integer array after sorting with leftward movement limited to %d positions:\n", limit);
        Sx.putsArray(cA);

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
}
