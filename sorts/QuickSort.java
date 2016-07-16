package sprax.sorts;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class QuickSort<T extends Comparable<T>>
{
    
    public static void sort(int[] array)
    {
        if (array == null || array.length < 2)
            return;
        
        sort(array, 0, array.length - 1);
    }
    
    public static void sort(int[] array, boolean verbose)
    {
        sort(array, 0, array.length - 1, verbose);
    }
    
    private static void sort(int[] array, int lo, int hi)
    {
        if (lo >= hi)
            return;
        
        int part = partition(array, lo, hi);
        sort(array, lo, part - 1);		// sort left partition
        sort(array, part + 1, hi);	    // sort right partition
    }
    
    private static void sort(int[] array, int lo, int hi, boolean verbose)
    {
        if (lo >= hi)
            return;
        
        int part = partition(array, lo, hi);
        sort(array, lo, part - 1, verbose);              // sort left partition
        sort(array, part + 1, hi, verbose);              // sort right partition
        if (verbose && hi - lo > 1)
            putsSubArray(array, lo, hi);
    }
    
    static void putsSubArray(int[] ar, int lo, int hi) {
        for (int j = lo; j <= hi; j++)
            System.out.print(ar[j] + " ");
        System.out.println();
    }
    
    private static int partition(int[] array, int lo, int hi)
    {
        int beg = lo;
        int end = hi + 1;
        int val = array[lo];
        while (true)
        {
            while (array[++beg] < val && beg < hi)
                ;
            while (array[--end] > val && end > lo)
                ;
            if (beg >= end)
                break;
            swap(array, beg, end);
        }
        swap(array, lo, end);
        return end;
    }
    
    private static void swap(int[] array, int j, int k)
    {
        int temp = array[j];
        array[j] = array[k];
        array[k] = temp;
    }
    
    public static void unit_test(int size)
    {
        String testName = QuickSort.class.getName() + ".unit_test";
        int numWrong = 0;
        
        Sz.begin(testName);
        int[] array = { 0, -1, 2, -3, 4, -5, 6, -7, 8, -9 };
        QuickSort.sort(array);
        
        numWrong += SortUtil.countDecreasing(array);
        Sx.putsArray("QuickSorted: ", array);
        
        
        int ar[] = {5, 8, 1, 3, 7, 9, 2};
        sort(ar, true);
        
        Sz.end(testName, numWrong);
    }
    
    public static void main(String[] args) {
        unit_test(5);
    }
}
