package sprax.arrays;

import java.util.ArrayList;

import sprax.Sx;

class Prc extends Object
{
	final int pay;
	final int row;
	final int col;
	Prc(int p, int r, int c) { pay = p; row = r; col = c; }
	@Override
	public String toString() { return String.format("prc(%2d, %2d, %2d)", pay, row, col); };
}
/**
 * Given a matrix of n*n. Each cell contain 0, k, -1. 
 *    0 means there is no diamond but there is a path. 
 *    n > 0 denotes there are n diamonds at that location with a path 
 *   -1 (or any number < 0 denotes that the path is blocked. 
 * Now you have start from 0,0 and reach to last cell & then return back to 0,0 collecting maximum no of diamonds. 
 * While going to last cell you can move only right and down. 
 * While returning back you can move only left and up.
 * @author sprax
 *
 */
public class ArrayMaxBlockedPathSum 
{
	/**
	 * Find maximally-paying path forward through array, adding all points and pay-offs to the supplied path.
	 * Forward means going only right or down.
	 * The path is now cleared at the beginning; only added-to.
	 * @param AA
	 * @param path
	 * @return the payoff of the path found
	 */
	public static int forwardPayingPath(int AA[][], ArrayList<Prc> path)
	{
		int pay = 0;

		// recursive algo:
		pay = forwardPayingPathDFS(AA, path, 0, 0, 0);
		return pay;
	}

	static int forwardPayingPathDFS(int AA[][], ArrayList<Prc> path, int totalPay, int row, int col)
	{
		// if pay < 0, that path cannot continue here
		int pay = AA[row][col];
		if (pay < 0)
			return totalPay;

		totalPay += pay;
		AA[row][col] = -1;

		int nextColPay = 0;
		int nextCol = col + 1;
		if (nextCol < AA[0].length)
		{
			nextColPay = forwardPayingPathDFS(AA, path, totalPay, row, nextCol);
		}
		int nextRowPay = 0;
		int nextRow = row + 1;
		if (nextRow < AA.length)
		{
			nextRowPay = forwardPayingPathDFS(AA, path, totalPay, nextRow, col);
		}
		
		if (nextColPay < nextRowPay)
		{
			path.add(new Prc(AA[nextRow][col], nextRow, col));
			totalPay = nextRowPay;
		}
		else if (nextColPay > 0)
		{
			path.add(new Prc(AA[row][nextCol], row, nextCol));
			totalPay = nextColPay;
		}
		else 
		{
			// path.add(new Prc(pay, row, col));
			// return totalPay;
		}
		AA[row][col] = -1;
		return totalPay;
	}


	public static int backwardPayingPath(int AA[][], ArrayList<Prc> path)
	{
		int pay = 0;
		return pay;    	
	}

	public static int payingPathsForwardBack(int AA[][], ArrayList<Prc> forwardPath, ArrayList<Prc> backwardPath)
	{
		forwardPath.clear();
		backwardPath.clear();

		int forwardPay = forwardPayingPath(AA, forwardPath);
		if (forwardPay < 0)
			return forwardPay;

		int backwardPay = backwardPayingPath(AA, backwardPath);

		return forwardPay + backwardPay;
	}

	public static int unit_test(int lvl) 
	{
		String  testName = ArrayMaxBlockedPathSum.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN");    

		int AA[][] = {
				{   1,  2,  0,  1,   0, -1 },
				{   0,  0,  0,  1,   0, -1 },
				{   1,  1, -1,  1,   0,  0 },
				{   0,  0,  0,  1,   0,  0 },
				{  -1,  0,  0,  1,   0,  1 },
				{   0,  0,  0,  1,  -1,  1 },
		};

		Sx.putsArray(AA);
		ArrayList<Prc> forwardPath = new ArrayList<Prc>();
		ArrayList<Prc> backwardPath = new ArrayList<Prc>();
		int totalPay = payingPathsForwardBack(AA, forwardPath, backwardPath);
		Sx.puts("payingPathsForwardBack: " + totalPay);
		for (Prc prc : forwardPath)
		{
			Sx.puts(prc);
		}


		Sx.puts(testName + " END");    
		return 0;
	}

	public static void main(String[] args) { unit_test(1); }  
}
