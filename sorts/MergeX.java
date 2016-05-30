/*************************************************************************
 *  Compilation:  javac MergeX.java
 *  Execution:    java MergeX < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/22mergesort/tiny.txt
 *                http://algs4.cs.princeton.edu/22mergesort/words3.txt
 *   
 *  Sorts a sequence of strings from standard input using an
 *  optimized version of mergesort.
 *   
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java MergeX < tiny.txt
 *  S O R T E X A M P L E A               [ one string per line ]
 *    
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *  
 *  % java MergeX < words3.txt
 *  all bad bed bug dad ... yes yet zoo    [ one string per line ]
 *
 *************************************************************************/

package sprax.sorts;

import sprax.Sz;
import std.StdIn;
import std.StdOut;

public class MergeX
{
    private static final int CUTOFF = 7;  // cutoff to insertion sort
                                         
    private static void merge(Comparable[] src, Comparable[] dst, int lo, int mid, int hi) {
        
        // precondition: src[lo .. mid] and src[mid+1 .. hi] are sorted subarrays
        assert SortUtil.isSorted(src, lo, mid);
        assert SortUtil.isSorted(src, mid + 1, hi);
        
        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid)
                dst[k] = src[j++];
            else if (j > hi)
                dst[k] = src[i++];
            else if (less(src[j], src[i]))
                dst[k] = src[j++];   // to ensure stability
            else
                dst[k] = src[i++];
        }
        
        // postcondition: dst[lo .. hi] is sorted subarray
        assert SortUtil.isSorted(dst, lo, hi);
    }
    
    private static void sort(Comparable[] src, Comparable[] dst, int lo, int hi)
    {
        if (hi <= lo + CUTOFF) {
            insertionSort(src, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(dst, src, lo, mid);
        sort(dst, src, mid + 1, hi);
        
        /*
                if (!less(dst[mid+1], dst[mid])) {
                    for (int i = lo; i <= hi; i++) src[i] = dst[i];
                    return;
                }
        */
        // a bit faster
        if (!less(dst[mid + 1], dst[mid])) {
            System.arraycopy(dst, lo, src, lo, hi - lo + 1);
            return;
        }
        
        merge(dst, src, lo, mid, hi);
    }
    
    public static void sort(Comparable[] a) 
    {
        /*
                Comparable[] aux = new Comparable[a.length];
                for (int i = 0; i < a.length; i++)
                    aux[i] = a[i];
        */
        // a bit faster
        Comparable[] aux = a.clone();
        sort(a, aux, 0, a.length - 1);
        
        assert SortUtil.isSorted(a);
    }
    
    // sort from a[lo] to a[hi] using insertion sort
    private static void insertionSort(Comparable[] a, int lo, int hi)
    {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j - 1]); j--)
                exch(a, j, j - 1);
    }
    
    // exchange a[i] and a[j]
    private static void exch(Comparable[] a, int i, int j)
    {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
    
    // is a[i] < a[j]?
    private static boolean less(Comparable a, Comparable b)
    {
        return (a.compareTo(b) < 0);
    }
    
    public static int unit_test(int level) 
    {
        String testName = MergeX.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        int verbose = 1;
     
        Insertion<Integer> intSorter = new Insertion<>();
        numWrong += SortUtil.test_sort_randomIntegerArray(intSorter, 32, 100, 0, verbose);
        if (level > 0) {
            SortUtil.test_input();
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(0);
    }
}
