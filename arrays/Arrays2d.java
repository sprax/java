// ArrayAlgo.java       Author: Sprax LInes   2011.11
package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class Arrays2d
{

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

	public static int unit_test(int level) 
	{
		String   testName = Arrays2d.class.getName() + ".unit_test";  
		Sz.begin(testName);;  

		int numWrong = 0;

		Integer[] foo = { 1, 2, 3};
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.addAll(Arrays.asList(foo));        
		HashSet<Integer> set = new HashSet<Integer>();
		set.add(1);
		HashSet<Integer> one = new HashSet<Integer>(set); 
		numWrong += Sz.showWrong(one.size(), set.size());


		Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args)
	{
		unit_test(2);
	}

}

