package sprax.sorts;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class QuickSortLomuto // implements SortInt
{
    
    public static void sort(int[] array, boolean verbose)
    {
        sort(array, 0, array.length - 1, verbose);
    }

    private static void sort(int[] array, int lo, int hi, boolean verbose)
    {
        if (lo >= hi)
            return;
        
        int part = partition(array, lo, hi, verbose);
        sort(array, lo, part - 1, verbose);              // sort left partition
        sort(array, part + 1, hi, verbose);              // sort right partition
    }

    /** 
     * Lomuto partition algorithm.  Less efficient, but easy to understand.  Maybe.  
     * TODO: Does not preserve order within partitions, so it does not produce a stable sort.
     * @param array
     * @param lo
     * @param hi    highest index (inclusive)
     * @return      index where partition value was placed
     */
    private static int partition(int A[], int lo, int hi, boolean verbose)
    {
        int pivotVal = A[hi];
        int pivotIdx = lo;     // place for swapping
        for (int j = lo; j <  hi; j++) {
            if (A[j] <= pivotVal) {
                swap(A, pivotIdx, j);
                pivotIdx = pivotIdx + 1;
            }
        }
        swap(A, pivotIdx, hi);
        if (verbose) {
            printArray(A);
        }
        return pivotIdx;
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


    public static void unit_test()
    {
        String testName = QuickSortLomuto.class.getName() + ".unit_test";
        int numWrong = 0;
        
        Sz.begin(testName);
        int[] array = { 0, -1, 2, -3, 4, -5, 6, -7, 8, -9 };
        QuickSortLomuto.sort(array, false);
        
        numWrong += SortUtil.countDecreasing(array);
        Sx.putsArray("QuickSorted: ", array);
        
        int az[] = { 5, 8, 1, 3, 7, 9, 2 };
        sort(az, true);
        numWrong += SortUtil.countDecreasing(az);
        //printArray(az);
        
        System.out.println("HackerRank test case:");
        int ay[] = { 1, 3, 9, 8, 2, 7, 5 };
        sort(ay, true);
        numWrong += SortUtil.countDecreasing(ay);
        //printArray(ay);
        
        Sz.end(testName, numWrong);
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
