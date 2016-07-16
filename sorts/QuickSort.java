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
        sort(array, lo, part - 1);      // sort left partition
        sort(array, part + 1, hi);      // sort right partition
    }

    /** 
     * Uses auxiliary array for partitioning, instead of swapping in-place.
     * Extra space O(N) for O(N) savings in time.
     */
    public static void sort_aux(int[] array, boolean verbose) {
        if (array == null || array.length < 2)
            return;

        int aux[] = new int[array.length];
        sort(array, aux, 0, array.length - 1, verbose);
    }

    private static void sort(int[] array, int[] aux, int lo, int hi, boolean verbose) {
        if (lo >= hi)
            return;

        int part = partition(array, aux, lo, hi);
        sort(array, aux, lo, part - 1, verbose); // sort left partition
        sort(array, aux, part + 1, hi, verbose); // sort right partition
        if (verbose && hi - lo > 0)
            putsSubArray(array, lo, hi);
    }

    private static void sort(int[] array, int lo, int hi, boolean verbose)
    {
        if (lo >= hi)
            return;
        
        int part = partition(array, lo, hi);
        sort(array, lo, part - 1, verbose);              // sort left partition
        sort(array, part + 1, hi, verbose);              // sort right partition
        if (verbose && hi - lo > 1) {
            putsSubArray(array, lo, hi);
        }
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
    

	/** preserves original array-order even when moving values to a new partition */
	private static int partition(int[] array, int[] aux, int lo, int hi) {
		int beg = lo;
		int end = hi;
		int val = array[lo];
		for (int j = lo; j <= hi; j++) {
			int arj = array[j];
			if (arj < val)
				aux[beg++] = arj;
			else if (arj > val)
				aux[end--] = arj;
		}
		for (int j = beg; j <= end; j++)
			array[j] = val;
		for (int j = lo; j < beg; j++)
			array[j] = aux[j];
		int hit = hi;
		for (int j = end; ++j <= hi;)
			array[j] = aux[hit--];
		return end;
	}

	static void printArray(int[] ar) {
		for (int n : ar) {
			System.out.print(n + " ");
		}
		System.out.println("");
	}

    static void putsSubArray(int[] ar, int lo, int hi) {
        for (int j = lo; j <= hi; j++)
            System.out.print(ar[j] + " ");
        System.out.println();
    }

    private static void swap(int[] array, int j, int k)
    {
        int temp = array[j];
        array[j] = array[k];
        array[k] = temp;
    }

    static void quickSort(int[] ar) {
        sort(ar);
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
        
        int az[] = { 5, 8, 1, 3, 7, 9, 2 };
        sort(az);
        numWrong += SortUtil.countDecreasing(az);
        printArray(az);
        
        int ar[] = { 5, 8, 1, 3, 7, 9, 2 };
        sort_aux(ar, true);
        numWrong += SortUtil.countDecreasing(ar);
        
        Sz.end(testName, numWrong);
    }
    
    public static void main(String[] args) {
        unit_test(5);
    }
}
