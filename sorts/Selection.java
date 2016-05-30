package sprax.sorts;

/*************************************************************************
 *  Compilation:  javac Selection.java
 *  Execution:    java  Selection < input.txt
 *  Dependencies: Sx, Sz
 *  Data files:   http://algs4.cs.princeton.edu/21sort/tiny.txt
 *                http://algs4.cs.princeton.edu/21sort/words3.txt
 *   
 *  Sorts a sequence of strings from standard input using selection sort.
 *   
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java Selection < tiny.txt
 *  S O R T E X A M P L E A               [ one string per line ]
 *    
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *  
 *  % java Selection < words3.txt
 *  all bad bed bug dad ... yes yet zoo    [ one string per line ]
 *
 *************************************************************************/

import java.util.Comparator;

import sprax.Sz;

public class Selection<T extends Comparable<T>> implements SortT<T>
{
    @Override
    public void sort(T[] iA) {
        selectionSort(iA);
    }

    // selection sort
    public static <T extends Comparable<T>> void selectionSort(T[] tA)
    {
        if (tA == null || tA.length < 2)
            return;

        int N = tA.length;
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i+1; j < N; j++)
            {
                if (less(tA[j], tA[min])) 
                	min = j;
            }
            exch(tA, i, min);
            assert(SortUtil.isSorted(tA, 0, i));
        }
        assert(SortUtil.isSorted(tA));
    }

    // use a custom order and Comparator interface - see Section 3.5
    public static <T extends Comparable<T>> void sort(T[] a, Comparator<T> c)
    {
        int N = a.length;
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i+1; j < N; j++) {
                if (less(c, a[j], a[min])) min = j;
            }
            exch(a, i, min);
            assert SortUtil.isSorted(a, c, 0, i);
        }
        assert SortUtil.isSorted(a, c);
    }


   /***********************************************************************
    *  Helper sorting functions
    ***********************************************************************/
    
    // is v < w ?
    private static <T extends Comparable<T>> boolean less(T v, T w) {
        return (v.compareTo(w) < 0);
    }

    // is v < w ?
    private static <T extends Comparable<T>> boolean less(Comparator<T> c, T v, T w) {
        return (c.compare(v, w) < 0);
    }
        
    // exchange a[i] and a[j]
    private static void exch(Object[] a, int i, int j) {
        Object swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    public static int unit_test(int level) 
    {
        String testName = Selection.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        int verbose = 1;
     
        Selection<Integer> intSorter = new Selection<>();
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