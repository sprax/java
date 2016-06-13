/*************************************************************************
 *  Compilation:  javac Insertion.java
 *  Execution:    java Insertion < input.txt
 *  Dependencies: Sx, Sz
 *  Data files:   http://algs4.cs.princeton.edu/21sort/tiny.txt
 *                http://algs4.cs.princeton.edu/21sort/words3.txt
 *  
 *  Sorts a sequence of strings from standard input using insertion sort.
 *
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java Insertion < tiny.txt
 *  S O R T E X A M P L E A               [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *
 *  % java Insertion < words3.txt
 *  all bad bed bug dad ... yes yet zoo   [ one string per line ]
 *
 *************************************************************************/
package sprax.sorts;

import java.util.Comparator;

import sprax.test.Sz;

public class Insertion<T extends Comparable<T>> implements SortT<T>
{
    @Override
    public void sort(T[] iA) {
        insertionSort(iA);
    }
    
    // use natural order and Comparable interface    
    public static <T extends Comparable<T>> void insertionSort(T[] iA)
    {
        if (iA == null || iA.length < 2)
            return;                     // GIGO
        
        int N = iA.length;
        for (int i = 0; i < N; i++) {
            for (int j = i; j > 0 && SortUtil.less(iA[j], iA[j-1]); j--) {
                exch(iA, j, j-1);
            }
            assert SortUtil.isSorted(iA, 0, i);
        }
        assert SortUtil.isSorted(iA);
    }

    // use a custom order and Comparator interface - see Section 3.5
    public static <T extends Comparable<T>> void sort(T[] a, Comparator<T> c) 
    {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            for (int j = i; j > 0 && SortUtil.less(c, a[j], a[j-1]); j--) {
                exch(a, j, j-1);
            }
            assert SortUtil.isSorted(a, c, 0, i);
        }
        assert SortUtil.isSorted(a, c);
    }

    // return a permutation that gives the elements in a[] in ascending order
    // do not change the original array a[]
    public static <T extends Comparable<T>> int[] indexSort(T[] a) 
    {
        int N = a.length;
        int[] index = new int[N];
        for (int i = 0; i < N; i++)
            index[i] = i;

        for (int i = 0; i < N; i++)
            for (int j = i; j > 0 && SortUtil.less(a[index[j]], a[index[j-1]]); j--)
                exch(index, j, j-1);

        return index;
    }


        
    // exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // exchange a[i] and a[j]  (for indirect sort)
    private static void exch(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    public static int unit_test(int level) 
    {
        String testName = Insertion.class.getName() + ".unit_test";
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
        unit_test(1);
    }
}


