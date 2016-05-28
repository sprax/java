package sprax.maths;

import java.util.ArrayList;

import sprax.Sx;


/** 
 * Compute and print binomial coefficients, a.k.a. entries in Pascal's Triangle
 * @author Sprax Lines
 */
public class PascalsTriangle 
{
	static ArrayList<long[]> sCachedRows = new ArrayList<long[]>();
	static int sRowsCached = 0;
	static boolean sInitialized;

	public static long binomialCoefficient(int row, int col)
	{
		if (row < 0 || row < col)
			throw new IllegalArgumentException("row < 0 or col > row: " + col + " > " + row);

		if (sRowsCached <= row)      
			fillCache(row + 1);
		return sCachedRows.get(row)[col];
	}

	private static void fillCache(int numRows)
	{
		long oldRow[] = sRowsCached > 0 ? sCachedRows.get(sRowsCached - 1) : null;
		for (int rr = sRowsCached; rr < numRows; rr++)
		{
			long newRow[] = new long[rr+1];
			int beg = 0, end = rr;
			newRow[beg++] = newRow[end--] = 1;
			for (; beg <= end; beg++, end--)
			{
				newRow[beg] = newRow[end] = oldRow[beg - 1] + oldRow[beg];
			}
			sCachedRows.add(newRow);
			oldRow = newRow;
		}
		sRowsCached = numRows;
	}

	public static void printRows(int numRows)
	{
		fillCache(numRows);
		for (int row = 0; row < numRows; row++)
		{
			long cc[] = sCachedRows.get(row);
			Sx.putsArray(cc);
		}
	}

	static long unit_test(int level)
	{
		long cf = binomialCoefficient(0,  0);
		printRows(level/2);
		printRows(level);
		return cf - 1;
	}

	public static void main(String args[]) { unit_test(20); }
}
