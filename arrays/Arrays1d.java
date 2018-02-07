// ArrayAlgo.java       Author: Sprax LInes   2011.11
package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IntSummaryStatistics;

import sprax.numbers.Primes;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class Arrays1d
{
    public static IntSummaryStatistics summaryStats(int[] iA)
    {
        return Arrays.stream(iA).summaryStatistics();
    }

    public static void test_summaryStats() {
        int[] primes = Primes.primesInRangeIntArray(2, 47);
        IntSummaryStatistics stats = summaryStats(primes);
        System.out.print("IntSummaryStats for Primes.primesInRangeIntArray(2, 47):\n\t");
        System.out.println(stats);
    }

    /**
     * Return sum of all elements.
     */
    public static int sum(int iA[])
    {    
        if (iA == null || iA.length == 0) {
            throw new IllegalArgumentException("null or empty array");
        }
        return sumNiece(iA, iA.length);
    }

    /**
     * Return sum of up to N elements.
     */
    public static int sum(int iA[], int num)
    {
        if (iA == null || iA.length == 0) {
            throw new IllegalArgumentException("null or empty array");
        }
        if (num < 0 || num > iA.length) {
            throw new IllegalArgumentException("num < 0 or num > array length");
        }
        return sumNiece(iA, num);
    }

    /**
     * Return sum of up to N elements, NIECE = No Input Error Checking 
     */
    public static int sumNiece(int iA[], int num)
    {
        int sum = 0;
        for (int i = 0; i < num; i++) {
            sum += iA[i];
        }
        return sum;
    }

    /**
     * Return sum of squares of elements.
     */
    public static int sumOfSquares(int iA[])
    {
        if (iA == null || iA.length == 0) {
            throw new IllegalArgumentException("null or empty array");
        }
        int sum = 0;
        for (int val, i = 0; i < iA.length; i++) {
            val = iA[i];
            sum += val*val;
        }
        return sum;
    }
    
    /**
     * Return the array's average value, or NaN if there is no such number.
     */
    public static <N extends Number> double mean(N[] array) {
        if (array == null || array.length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i].doubleValue();
        }
        return sum / array.length;
    }

    public static double mean(int[] array) {
        if (array == null || array.length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = 0; i < array.length; i++) {
            sum = sum + array[i];
        }
        return sum / array.length;
    }
    
   /**
     * Return sample standard deviation of array, NaN if no such value.
     */
    public static double stddev(int[] array) {
        return Math.sqrt(variance(array));
    }
    

    /**
     * Return sample variance of array, NaN if no such value.
     */
    public static double variance(int[] array) {
        if (array == null || array.length == 0) {
            return Double.NaN;
        }
        double avg = mean(array);
        double sum = 0.0;
        for (int i = 0; i < array.length; i++) {
            sum += (array[i] - avg) * (array[i] - avg);
        }
        return sum / (array.length - 1);
    }

	public static void reverseArray(int[] arr)
	{
		if (arr == null || arr.length < 2)
			return;
		for (int beg = 0, end = arr.length; --end > beg; ++beg)
		{
			arraySwap(arr,  beg, end);
		}	
	}

	public static void arraySwap(int[] arr, int beg, int end) 
	{
		int temp = arr[beg];
		arr[beg] = arr[end];
		arr[end] = temp;
	}
	
	public static void reverseArray(char[] arr)
	{
		if (arr == null || arr.length < 2)
			return;
		for (int beg = 0, end = arr.length; --end > beg; ++beg)
		{
			arraySwap(arr,  beg, end);
		}	
	}

	private static void arraySwap(char[] arr, int beg, int end) 
	{
		char tmp = arr[beg];
		arr[beg] = arr[end];
		arr[end] = tmp;
	}
	

	public static <T> void arrayListSwap(ArrayList<T> list, int j, int k) 
	{
		T jVal = list.get(j);
		T kVal = list.get(k);
		list.set(j, kVal);
		list.set(k, jVal);
	}	

	public static <T> void reverseListInPlace(ArrayList<T> list)
	{
		if (list == null || list.size() < 1)
			return;
		for (int beg = 0, end = list.size(); --end > beg; beg++) {
			arrayListSwap(list, beg, end);
		}
	}

	public static <T> void arraySwap(T[] array, int j, int k) 
	{
		T tmp = array[j];
		array[j] = array[k];
		array[k] = tmp;
	}
	

	public static <T> void reverseArray(T array[]) 
	{
		for (int beg = 0, end = array.length; --end > beg; beg++) {
			T temp     = array[end];
			array[end] = array[beg];
			array[beg] = temp;
		}
	}     
 

	/**
	 * Reverses the sub-array array[beg, end), that is, the part of array
	 * from indices beg, inclusive, to end, exclusive.  No input correction
	 * or error checking is done.
	 * 
	 * @param <T>     Type of array values
	 * @param array   The array
	 * @param beg     first index of sub-array
	 * @param end     last index + 1 of sub-array
	 */
	public static <T> void reverseSubArray(T array[], int beg, int end) 
	{
		for ( ; --end > beg; beg++) {
			T temp     = array[end];
			array[end] = array[beg];
			array[beg] = temp;
		}
	}      


	////////////////////////////////////// PERMUTATIONS & INDEX-MAPPINGS /////////////////////

	/**
	 * Given an input array iA, create an array iP 
	 * with iP[k] = product of all elements in iA except for iA[k].
	 * Must run as O(N) without using division.
	 */
	static int[] productArrayTwoPass(int[] iA)
	{
		if (iA == null || iA.length == 0)    // GIGO
			return iA;                       // Garbage In, Garbage Out

		int iP[] = new int[iA.length];
		int left = 1, right = 1;
		for (int j = 0; j < iA.length; j++)
		{
			iP[j] = left;
			left *= iA[j];
		}
		for (int k = iA.length; --k >= 0; )
		{
			iP[k] *= right;
			right *= iA[k];
		}
		return iP;
	}

	/**
	 * Given an input array iA, create an array iP 
	 * with iP[k] = product of all elements in iA except for iA[k].
	 * Must run as O(N), division is OK 
	 */
	static int[] productArrayDivided(int[] iA)
	{
		if (iA == null || iA.length == 0) // GIGO
			return iA;

		int iP[] = new int[iA.length];    // initialized to all 0s
		int product = 1;
		int numZeros = 0;
		for (int k : iA)
		{
		    if (k == 0)
		        numZeros++;
		    else
		        product *= k;
		}
		if (numZeros > 1) {
		    return iP;
		}
		for (int j = 0; j < iA.length; j++)
		{
		    if (iA[j] == 0)
		        iP[j] = product;
		    else
		        iP[j] = product / iA[j];   
		}
		return iP;
	}
	

	public static int test_productArray() 
	{
		int iA[] = { 2, -3, 4, -5, 6 };
		Sx.putsArray("productArrayTwoPass:  input", iA, "");
		int iP[] = productArrayTwoPass(iA);
		int iQ[] = productArrayDivided(iA);
		Sx.putsArray("productArrayTwoPass: output", iP, "");        
		Sx.putsArray("productArrayDivided: output", iQ, "");      
		return 0;
	}

	public static int unit_test(int level) 
	{
		String   testName = Arrays1d.class.getName() + ".unit_test";  
		Sz.begin(testName);;  

		int numWrong = 0;

		Integer[] foo = { 1, 2, 3};
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.addAll(Arrays.asList(foo));        
		HashSet<Integer> set = new HashSet<Integer>();
		set.add(1);
		HashSet<Integer> one = new HashSet<Integer>(set); 
		numWrong += Sz.showWrong(one.size(), set.size());


		ArrayList<Integer> two = new ArrayList<Integer>(5);
		two.add(15);
		two.add(32);
		Integer d1 = two.get(1);
		d1 = 2;
		Integer d0 = two.get(0);
		two.set(0,  -64);
		Sx.puts("duh.get(1) and d1: " + two.get(1) + "  " + d1);
		Sx.puts("duh.get(0) before and after: " + d0 + "  " + two.get(0));

		numWrong += test_productArray();
		test_summaryStats();

		Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args)
	{
		unit_test(2);
	}

}

