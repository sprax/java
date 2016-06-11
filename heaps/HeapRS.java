package sprax.heaps;

import sprax.sprout.Sx;

/*************************************************************************
 *  Compilation:  javac Heap.java
 *  Execution:    java Heap < input.txt
 *  Dependencies: 
 *  Data files:   http://algs4.cs.princeton.edu/24pq/tiny.txt
 *                http://algs4.cs.princeton.edu/24pq/words3.txt
 *  
 *  Sorts a sequence of strings from standard input using heapsort.
 *
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java Heap < tiny.txt
 *  S O R T E X A M P L E A               [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *
 *  % java Heap < words3.txt
 *  all bad bed bug dad ... yes yet zoo   [ one string per line ]
 *
 *************************************************************************/

/**
 * WARNING: 1-based indexing!
 * Assumes that pq holds a heap as a complete binary tree string in the 
 * array slice pq[1..N] with pq[0] unused.
 * @author Sprax from Robert Sedgewick
 * @param <T>
 */
public class HeapRS<T extends Comparable<T>>
{
	public static <T extends Comparable<T>> void sort(T[] pq) 
	{
		int size = pq.length;
		for (int k = size/2; k >= 1; k--)
			sink(pq, k, size);
		while (size > 1) {
			exch(pq, 1, size--);
			sink(pq, 1, size);
		}
	}

	/***********************************************************************
	 * Helper functions to restore the heap invariant.
	 **********************************************************************/

	private static <T extends Comparable<T>> void sink(T pq[], int k, int size)
	{
		while (2*k <= size) {
			int j = 2*k;
			if (j < size && less(pq, j, j+1)) 
				j++;
			if (!less(pq, k, j)) 
				break;
			exch(pq, k, j);
			k = j;
		}
	}

	/****
	private static <T extends Comparable<T>> void swim(T pq[], int k)
	{
		while (k > 1 && less(pq, k/2, k))
		{
			exch(pq, k/2, k);
			k = k/2;
		}
	}
	****/

	/***********************************************************************
	 * Helper functions for comparisons and swaps.
	 * Indices are "off-by-one" to support 1-based indexing.
	 **********************************************************************/
	private static <T extends Comparable<T>> boolean less(T pq[], int i, int j) 
	{
		return pq[i-1].compareTo(pq[j-1]) < 0;
	}

	private static void exch(Object[] pq, int i, int j) 
	{
		Object swap = pq[i-1];
		pq[i-1] = pq[j-1];
		pq[j-1] = swap;
	}

	// print array to standard output
	private static <T extends Comparable<T>> void show(T[] a)
	{
		for (int i = 0; i < a.length; i++) {
			Sx.puts(a[i]);
		}
	}

	public static void unit_test()
	{
		String[] a = { "one", "two", "three", "four", "five", "six"};
		HeapRS.sort(a);
		show(a);
	}
	
	// Read strings from standard input, sort them, and print.
	public static void main(String[] args) { unit_test(); }
}