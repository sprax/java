// ArrayAlgo.java       Author: Sprax LInes   2011.11
package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import sprax.Sz;
import sprax.sprout.Sx;

public class ArrayAlgo
{
    /**
     * Return sum of squares of elements.
     */
    public static int sumOfSquares(int iA[]) {
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

	private static void arraySwap(int[] arr, int beg, int end) 
	{
		int temp = arr[beg];
		arr[beg] = arr[end];
		arr[end] = temp;
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

	public static void arraySwap(char[] A, int i, int j) {
		char temp = A[i];
		A[i] = A[j];
		A[j] = temp;
		return;
	}

	public static <T> void reverseArray(T array[]) 
	{
		for (int beg = 0, end = array.length; --end > beg; beg++) {
			T temp     = array[end];
			array[end] = array[beg];
			array[beg] = temp;
		}
	}     

	public static void reverseArray(char array[]) 
	{
		for (int beg = 0, end = array.length; --end > beg; beg++) {
			char temp  = array[end];
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

	/**
	 *  create 2D array of nRows rows and nCols columns, wherein 
	 *  each row and column are sorted separately.  That is, the
	 *  sequence of entries in each row and column are monotonically
	 *  non-decreasing.
	 * @param nRows
	 * @param nCols
	 * @param minVal
	 * @param maxInc  maximum increment from on entry to the next (next in the same row or column.)
	 * @param seed    seed for Random
	 * @return
	 */
	public static int[][] makeRandomRowColSortedArray(int nRows, int nCols, int minVal, int maxInc, long seed)
	{
		int AA[][] = ArrayFactory.makeIntArray(nRows, nCols);
		Random rng = new Random(seed);
		AA[0][0] = minVal;
		for (int row = 1; row < nRows; row++)
			AA[row][0] = AA[row-1][0] + rng.nextInt(maxInc);

		for (int A[] = AA[0], col = 1; col < nCols; col++)
			A[col] = A[col-1] + rng.nextInt(maxInc);

		for (int row = 1; row < nRows; row++)
			for (int A[] = AA[row], col = 1; col < nCols; col++)
				A[col] = Math.max(AA[row-1][col], AA[row][col-1]) + rng.nextInt(maxInc);

		return AA;
	}

	public static int[][] makeRandomRowColSortedArray(int nRows, int nCols, int minVal, int maxInc)
	{
		return makeRandomRowColSortedArray(nRows, nCols, minVal, maxInc, System.currentTimeMillis());
	}

	public static int[][] makeRandomRowColSortedArray(int nRows, int nCols) 
	{
		int maxInc = 7;
		int minVal = (int)Math.log10(nRows*nCols*maxInc);
		return makeRandomRowColSortedArray(nRows, nCols, minVal, maxInc, System.currentTimeMillis());
	}

	public static int test_makeRandomRowColSortedArray()
	{
		int AA[][] = makeRandomRowColSortedArray(20, 20, 100, 7);
		Sx.putsArray(AA);
		int BB[][] = makeRandomRowColSortedArray(40, 40, 100, 7);
		Sx.putsArray("preLabel\n", BB);
		return 0;
	}

	/**
	 * This thing seems to run very slowly!  TODO: why?
	 * @param AA
	 * @param row
	 * @param col
	 * @param nRows
	 * @param nCols
	 * @param bigVal
	 */
	public static void youngify(int [][] AA, int row, int col, int nRows, int nCols, int bigVal) 
	{
		if (row > nRows || col > nCols) 
			return;

		int x = -1, y = -1;
		// If the value directly below is greater than the one at row, col, 
		// then move it up, and place the "infinite" value in its old place.
		if (row+1 < nRows && AA[row][col] > AA[row+1][col]) {
			AA[row][col] = AA[row+1][col];
			x = row+1;
			y = col;
		}
		// Then, also, If the value directly right is greater than the one at row, col, 
		// then move it left, and place the "infinite" value in its old place.
		// Actually, it does not matter which neighbor you check and move first,
		// but both of them must be handled. (Two if-then's, not one if-else-then.)
		if (col+1 < nCols && AA[row][col] > AA[row][col+1]) {
			AA[row][col] = AA[row][col+1];
			x = row;
			y = col+1;
		}
		if (x != -1) {
			AA[x][y] = bigVal;
			youngify(AA, x, y, nRows, nCols, bigVal);
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
		String   testName = ArrayAlgo.class.getName() + ".unit_test";  
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

		Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args)
	{
		unit_test(2);
	}

}

