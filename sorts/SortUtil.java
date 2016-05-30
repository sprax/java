package sprax.sorts;

import java.util.Comparator;

import sprax.Sx;
import sprax.arrays.ArrayFactory;

interface SortT<T extends Comparable<T>> {
    void sort(T array[]);
}

public class SortUtil 
{
    public static boolean verifySorted(int A[]) 
    {
        return verifyIsNonDecreasing(A, A.length);
    }
    
    public static boolean verifySorted(int A[], int size) 
    {
        return verifyIsNonDecreasing(A, size);
    }
    
    public static boolean verifyIsNonDecreasing(int[] A, int size) 
    {
        int end = Math.min(A.length, size) - 1;
        for (int j = 0; j < end; ) {
            if (A[j] > A[++j])
                return false;
        }
        return true;
    }
    
    public static boolean verifySorted(Integer A[], int size) 
    {
        return verifyIsNonDecreasing(A, size);
    }
    
    public static boolean verifyIsNonDecreasing(Integer[] A, int size) 
    {
        int end = Math.min(A.length, size) - 1;
        for (int j = 0; j < end; ) {
            if (A[j] > A[++j])
                return false;
        }
        return true;
    }
    
    public static <T extends Comparable<T>> boolean verifySorted(T A[], int size) 
    {
        return verifyIsNonDecreasing(A, size);
    }
    
    public static <T extends Comparable<T>> boolean verifyIsNonDecreasing(T[] A, int size) 
    {
        int end = Math.min(A.length, size) - 1;
        for (int j = 0; j < end; ) {
            if (A[j].compareTo(A[++j]) > 0)
                return false;
        }
        return true;
    }
    
    public static <T extends Comparable<T>> boolean verifySortedDescending(T A[], int size) 
    {
        int end = Math.min(A.length, size) - 1;
        for (int j = 0; j < end; ) {
            if (A[j].compareTo(A[++j]) > 0)
                return false;
        }
        return true;
    }
    
    public static <T extends Comparable<T>> boolean verifySortedDescending(int A[], int size) 
    {
        int end = Math.min(A.length, size) - 1;
        for (int j = 0; j < end; ) {
            if (A[j] > A[++j])
                return false;
        }
        return true;
    }
    
    /***********************************************************************
     *  Check if array is sorted - useful for debugging
     ***********************************************************************/
     public static <T extends Comparable<T>> boolean isSorted(T[] a)
     {
         return isSorted(a, 0, a.length - 1);
     }

     // is the array sorted from a[lo] to a[hi]
     public static <T extends Comparable<T>> boolean isSorted(T[] a, int lo, int hi)
     {
         for (int i = lo + 1; i <= hi; i++) {
             if ((a[i].compareTo(a[i-1]) < 0)) {
                 return false;
             }
         }
         return true;
     }

     public static <T extends Comparable<T>> boolean isSorted(T[] a, Comparator<T> c)
     {
         return isSorted(a, c, 0, a.length - 1);
     }

     // is the array sorted from a[lo] to a[hi]
     public static <T extends Comparable<T>> boolean isSorted(T[] a, Comparator<T> c, int lo, int hi) 
     {
         for (int i = lo + 1; i <= hi; i++) {
             if (c.compare(a[i], a[i-1]) < 0) {
                 return false;
             }
         }
         return true;
     }
     
     /***********************************************************************
     * Check if array is sorted - useful for debugging
     ***********************************************************************/
    
    // is the array a[] sorted?
    public static boolean isSorted(Object[] a, Comparator<Object> c) {
        return isSorted(a, c, 0, a.length - 1);
    }
    
    // is the array sorted from a[lo] to a[hi]
    public static boolean isSorted(Object[] a, Comparator<Object> c, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++) {
            if (c.compare(a[i], a[i-1]) < 0) {
                return false;
            }
        }
        return true;
    }
    
    // print array to standard output
    public static void show(Comparable<?>[] a) {
        for (int i = 0; i < a.length; i++) {
            Sx.puts(a[i]);
        }
    }    
    
    public static int countDecreasing(int sorted[])
    {
        if (sorted == null || sorted.length < 2)
            return 0;
        int count = 0;
        for (int prv = sorted[0], j = 1; j < sorted.length; j++) {
            int val = sorted[j];
            if (prv > val)
                count++;
            prv = val;
        }
        return count;
    }
    
    public static int countDecreasing(Integer sorted[])
    {
        if (sorted == null || sorted.length < 2)
            return 0;
        int count = 0;
        for (int prv = sorted[0], j = 1; j < sorted.length; j++) {
            int val = sorted[j];
            if (prv > val)
                count++;
            prv = val;
        }
        return count;
    }
    
    public static int countIncreasing(int sorted[])
    {
        if (sorted == null || sorted.length < 2)
            return 0;
        int count = 0;
        for (int prv = sorted[0], j = 1; j < sorted.length; j++) {
            int val = sorted[j];
            if (prv < val)
                count++;
            prv = val;
        }
        return count;
    }
    
    public static int test_sort_random_int_array_default(SortInt sorter, int size, int radius)
    {
        long seed = System.currentTimeMillis();
        int verbose = 1;
        return SortUtil.test_sort_random_int_array(sorter, size, radius, seed, verbose);
    }
    
    public static int test_sort_random_int_array(SortInt sorter, int size, int radius, long seed, int verbose)
    {
        int iA[] = ArrayFactory.makeRandomIntArray(size, -radius, radius, seed);
        if (verbose > 0) {
            Sx.format("test_sort_random_int_array: %s (size %d, radius %d, seed %d)\n"
                    , sorter.getClass().getSimpleName(), size, radius, seed);
            Sx.putsArray("Random: ", iA);
        }
        sorter.sort(iA);
        if (verbose > 0)
            Sx.putsArray("Sorted: ", iA);
        return SortUtil.countDecreasing(iA);
    }
    
    public static int test_sort_randomIntegerArray(SortT<Integer> sorter, int size, int radius, long seed, int verbose)
    {
        Integer[] iA = ArrayFactory.makeRandomIntegerArray(size, -radius, radius, seed);
        if (verbose > 0) {
            Sx.format("test_sort_random_int_array: %s (size %d, radius %d, seed %d)\n"
                    , sorter.getClass().getSimpleName(), size, radius, seed);
            Sx.putsArray("Random: ", iA);
        }
        sorter.sort(iA);
        if (verbose > 0)
            Sx.putsArray("Sorted: ", iA);
        return SortUtil.countDecreasing(iA);
    }

    public static void main(String[] args) {
        BucketSort.unit_test(1);
    }
}