package sprax.sorts;

import java.util.*;

import sprax.Sz;

public class QuickSelect
{
    
    public static <T extends Comparable<T>> void quickselect(T[] a, int k) {
        quickselect(a, 0, a.length - 1, k);
    }
    
    private static final int CUTOFF = 10;
    
    private static <T extends Comparable<T>> void quickselect(T[] a, int low, int high,
            int k)
    {
        if (low + CUTOFF > high)
            insertionSort(a, low, high);
        else {
            // Sort low, middle, high
            int middle = (low + high) / 2;
            if (a[middle].compareTo(a[low]) < 0)
                swapReferences(a, low, middle);
            if (a[high].compareTo(a[low]) < 0)
                swapReferences(a, low, high);
            if (a[high].compareTo(a[middle]) < 0)
                swapReferences(a, middle, high);
            
            // Place pivot at position high - 1
            swapReferences(a, middle, high - 1);
            T pivot = a[high - 1];
            
            // Begin partitioning
            int i, j;
            for (i = low, j = high - 1;;)
            {
                while (a[++i].compareTo(pivot) < 0)
                    ;
                while (pivot.compareTo(a[--j]) < 0)
                    ;
                if (i >= j)
                    break;
                swapReferences(a, i, j);
            }
            
            // Restore pivot
            swapReferences(a, i, high - 1);
            
            // Recurse on the relevant sub-array
            int pos = k - 1;
            if (pos < i)
                quickselect(a, low, i - 1, k);
            else if (pos > i)
                quickselect(a, i + 1, high, k);
        }
    }
    
    private static void swapReferences(Object[] a, int index1, int index2) {
        Object tmp = a[index1];
        a[index1] = a[index2];
        a[index2] = tmp;
    }
    
    private static <T extends Comparable<T>> void insertionSort(T[] a, int low, int high) {
        for (int p = low + 1; p <= high; p++) {
            T tmp = a[p];
            int j;
            
            for (j = p; j > low && tmp.compareTo(a[j - 1]) < 0; j--)
                a[j] = a[j - 1];
            a[j] = tmp;
        }
    }
    
    public static int unit_test()
    {
        String testName = QuickSelect.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        for (int SIZE = 100; SIZE < 1000000; SIZE *= 2) {
            long start, end;
            long elapsed1 = 0, elapsed2 = 0, elapsed3 = 0;
            Integer[] a = new Integer[SIZE];
            
            // sorted input
            for (int i = 0; i < SIZE; i++) {
                a[i] = new Integer(i);
            }
            start = System.currentTimeMillis();
            quickselect(a, SIZE / 2);
            end = System.currentTimeMillis();
            elapsed1 = end - start;
            
            // reverse sorted input
            for (int i = 0; i < SIZE; i++) {
                a[i] = new Integer(SIZE - i);
            }
            start = System.currentTimeMillis();
            quickselect(a, SIZE / 2);
            end = System.currentTimeMillis();
            elapsed2 = end - start;
            
            // random input
            Random r = new Random();
            for (int i = 0; i < SIZE; i++) {
                a[i] = new Integer(r.nextInt(SIZE));
            }
            start = System.currentTimeMillis();
            quickselect(a, SIZE / 2);
            end = System.currentTimeMillis();
            elapsed3 = end - start;
            
            System.out.println("size: " + SIZE +
                    "\tsorted: " + elapsed1 +
                    "\treverse: " + elapsed2 +
                    "\trandom: " + elapsed3);
        }
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
