package sprax.sorts;

import sprax.sprout.Sx;

public class QuickSort<T extends Comparable<T>>
{
    
    public static void sort(int[] array)
    {
        if (array == null || array.length < 2)
            return;
        
        sort(array, 0, array.length - 1);
    }
    
    private static void sort(int[] array, int lo, int hi)
    {
        if (lo >= hi)
            return;
        
        int part = partition(array, lo, hi);
        sort(array, lo, part - 1);				// sort left partition
        sort(array, part + 1, hi);				// sort right partition
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
        int[] array = { 0, -1, 2, -3, 4, -5, 6, -7, 8, -9 };
        QuickSort.sort(array);
        Sx.putsArray("QuickSorted: ", array);
    }
    
    public static void main(String[] args) {
        unit_test(5);
    }
}
