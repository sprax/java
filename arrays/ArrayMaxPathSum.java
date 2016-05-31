package sprax.arrays;

import java.io.IOException;

import sprax.Sx;

class PrcNode extends Object	// weighted point object with row and col and next ref
{
	final int pay;
	final int row;
	final int col;
	PrcNode next;
	PrcNode(int p, int r, int c, PrcNode nxt) { pay = p; row = r; col = c; next = nxt; }
	@Override
	public String toString() { return String.format("Prc(%2d, %2d | %2d)", row, col, pay); };
	public static PrcNode reverse(PrcNode list)
	{
		PrcNode head = null;
		while (list != null)
		{
			PrcNode temp = list;
			list = list.next;
			temp.next = head;
			head = temp;
		}
		return head;
	}
}

public class ArrayMaxPathSum 
{
    /////////////////////////// PATH SUMS ON ARRAYS //////////////////////////////
    
    /** 
     * Verify that all rows have the same number of columns, the specified begRow and begCol >= 0,
     * and dstRow & dstCol are within the gird, i.e. < gridRows & < gridCols.
     * @param Z
     * @param gridRows
     * @param gridCols
     * @param begRow
     * @param begCol
     * @param dstRow
     * @param dstCol
     * @return error code < 0 if there is a problem
     */
    public static int checkRectGridArgs(final Object Z[][], int gridRows, int gridCols, int begRow, int begCol, int dstRow, int dstCol)
    {
        for (int row = 1; row < gridRows; row++) {
            if (Z[row].length != gridCols) {
                return -7;
            }
        }
        if (begRow < 0 || begCol < 0 || begRow >= gridRows || begCol >= gridCols)
            return -5;
        if (dstRow < 0 || dstCol < 0 || dstRow >= gridRows || dstCol >= gridCols)
            return -3;
        if (begRow > dstRow || begCol > dstCol)
            return -1;
        
        return 0;
    }
    
    /**
     *  Given a matrix you have to find the shortest path from one point
     *  to another within the matrix. The cost of path is all the matrix 
     *  entries on the way.  You can move in any direction (up, down, left,
     *  right, diagonally), e.g.

  5 9 10 1
  3 7 4 4
  8 2 1 9

  So shortest path from (0,0) to (2,2) is not (0,0)--(1,1)--(2,2) with
  cost = 5 + 7 + 1     = 13 (wrong), but is (0,0)--(1,0)--(2,1)--(2,2) with
  cost = 5 + 3 + 2 + 1 = 11 (right).
     */
    
    /**
     * findMaxNodeWeightedPathRectGridDownRightDiag finds a maximal path sum of weights,
     * assuming that the only allowed path steps
     * are right, down, or both, i.e., diagonal right and down.  Thus the search for a
     * path between two grid nodes is limited to the rectangle bounded by their coordinates.
     * 
     * Note that diagonal path steps, e.g. (0,0) to (1,1) without going through (0,1) or (1,0), allow
     * for shorter path lengths, not generally path with greater weight sums.
     * The maximal sum can only be different from that found by 
     * findMaxNodeWeightedPathRectGridDownRight if some nodes have negative weights.  In that case,
     * the sum may be greater if the negative nodes are skipped by moving diagonally.
     */
    public static int findMaxNodeWeightedPathSumRectGridDownRightDiag(Integer Z[][], int begRow, int begCol, int dstRow, int dstCol)
    {
        int gridRows = Z.length;
        int gridCols = Z[0].length;
        int status = checkRectGridArgs(Z, gridRows, gridCols, begRow, begCol, dstRow, dstCol);
        if (status < 0)
            throw new IllegalArgumentException("checkRectGridArgs: " + status);
        
        final int pathRows = dstRow - begRow + 1;
        final int pathCols = dstCol - begCol + 1;
        int pathSums[][] = new int[pathRows][];
        for (int row = 0; row < pathRows; row++)
            pathSums[row] = new int[pathCols];
        
        // first column of pathSums
        int prevSum = pathSums[0][0] = Z[begRow][begCol];
        for (int nodeRow = begRow+1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++)
            prevSum = pathSums[pathRow][0] = prevSum + Z[nodeRow][begCol];
        
        // first row of pathSums
        prevSum = pathSums[0][0];
        for (int nodeCol = begCol+1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++)
            prevSum = pathSums[0][pathCol] = prevSum + Z[begRow][nodeCol];
        
        for (int nodeRow = begRow+1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++) {
            for (int nodeCol = begCol+1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++) {
                int payRight = pathSums[pathRow][pathCol-1];
                int payDown  = pathSums[pathRow-1][pathCol];
                int payDiag  = pathSums[pathRow-1][pathCol-1];
                prevSum = Math.max(payRight, payDown);
                prevSum = Math.max(prevSum, payDiag);
                pathSums[pathRow][pathCol] = prevSum + Z[nodeRow][nodeCol];
            }
        }
        return pathSums[pathRows-1][pathCols-1];
    }
    
    /**
     * findMaxNodeWeightedPathRectGridDownRightDiag finds a maximal weight-sum path,
     * assuming that the only allowed path steps
     * are right, down, or both, i.e., diagonal right and down.  Thus the search for a
     * path between two grid nodes is limited to the rectangle bounded by their coordinates.
     * 
     * Note that diagonal path steps, e.g. (0,0) to (1,1) without going through (0,1) or (1,0), allow
     * for shorter path lengths, not generally path with greater weight sums.
     * The maximal sum can only be different from that found by 
     * findMaxNodeWeightedPathRectGridDownRight if some nodes have negative weights.  In that case,
     * the sum may be greater if the negative nodes are skipped by moving diagonally.
     */
    public static PrcNode findMaxNodeWeightedPathRectGridDownRightDiag(Integer Z[][], int begRow, int begCol, int dstRow, int dstCol)
    {
        int gridRows = Z.length;
        int gridCols = Z[0].length;
        int status = checkRectGridArgs(Z, gridRows, gridCols, begRow, begCol, dstRow, dstCol);
        if (status < 0)
            throw new IllegalArgumentException("checkRectGridArgs: " + status);
        
        final int pathRows = dstRow - begRow + 1;
        final int pathCols = dstCol - begCol + 1;
        PrcNode pathSums[][] = new PrcNode[pathRows][pathCols];
       
        // first column of pathSums
        int prevSum = Z[begRow][begCol];
        PrcNode node = pathSums[0][0] = new PrcNode(prevSum, begRow, begCol, null);
        for (int nodeRow = begRow+1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++)
        {
            prevSum = prevSum + Z[nodeRow][begCol];
            node = pathSums[pathRow][0] = new PrcNode(prevSum, nodeRow, begCol, node);
        }
        
        // first row of pathSums
        prevSum = Z[begRow][begCol];
        node = pathSums[0][0];
        for (int nodeCol = begCol+1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++)
        {
            prevSum = prevSum + Z[begRow][nodeCol];
            node = pathSums[0][pathCol] = new PrcNode(prevSum, begRow, nodeCol, node);
        }
        
        for (int nodeRow = begRow+1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++) 
        {
            for (int nodeCol = begCol+1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++) 
            {
                int payHere = Z[nodeRow][nodeCol];
            	int payRight = pathSums[pathRow][pathCol-1].pay;
                int payDown  = pathSums[pathRow-1][pathCol].pay;
                int payDiag  = pathSums[pathRow-1][pathCol-1].pay;
                // Use payRight if all are equal
                if (payRight < payDown)
                {
                	// Use payDiag unless it's less than payDown
                	if (payDiag < payDown)
                	{
                		// Use payDown
                		prevSum = payDown + payHere;
                		pathSums[pathRow][pathCol] = new PrcNode(prevSum, nodeRow, nodeCol, pathSums[pathRow-1][pathCol]);
                	}
                	else
                	{
                		// Use payDiag
                		prevSum = payDiag + payHere;
                		pathSums[pathRow][pathCol] = new PrcNode(prevSum, nodeRow, nodeCol, pathSums[pathRow-1][pathCol-1]);
                	}
                }
                else
                {
                	//  Use payRight unless it's less than payDiag
                	if (payRight < payDiag)
                	{
                		// Use payDiag
                		prevSum = payDiag + payHere;
                		pathSums[pathRow][pathCol] = new PrcNode(prevSum, nodeRow, nodeCol, pathSums[pathRow-1][pathCol-1]);                		
                	}
                	else
                	{
                		// Use payRight
                		prevSum = payRight + payHere;
                		pathSums[pathRow][pathCol] = new PrcNode(prevSum, nodeRow, nodeCol, pathSums[pathRow][pathCol-1]);
                	}
                }
            }
        }        
        return pathSums[pathRows - 1][pathCols - 1];
    }
    
    /**
     * interface IntFromCol abstracts and mimics a 1-d array
     * It might be more intuitive and useful if Java supported 
     * operator overloading, in particular, the ability to 
     * overload [].
     * @deprecated
     * @author sprax
     *
     */
    public interface IntFromCol {
        public int getIntVal(int col);
        public int length();
    }
    /**
     * interface IntFromRowCol abstracts and mimics a 2D array.  
     * It might be more intuitive and useful if Java supported 
     * operator overloading, in particular, the ability to 
     * overload [].
     * @deprecated
     * @author sprax
     *
     */
    public interface IntFromRowCol {
        public int getIntVal(int row, int col);
        public IntFromCol getRow(int row);
        public int length();
    }
    
    static int checkRectGridArgs(final IntFromRowCol Z, int gridRows, int gridCols, int begRow, int begCol, int dstRow, int dstCol)
    {
        for (int row = 1; row < gridRows; row++) {
            if (Z.getRow(row).length() != gridCols) {
                return -7;
            }
        }
        if (begRow < 0 || begCol < 0 || begRow >= gridRows || begCol >= gridCols)
            return -5;
        if (dstRow < 0 || dstCol < 0 || dstRow >= gridRows || dstCol >= gridCols)
            return -3;
        if (begRow > dstRow || begCol > dstCol)
            return -1;
        
        return 0;
    }
    
    /** findMaxNodeWeightedPathRectGridDownRightDiag allows diagonal path steps, e.g. (0,0) to (1,1) without
     * going through (0,1) or (1,0).  The maximal sum can only be different from that found by 
     * findMaxNodeWeightedPathRectGridDownRight is some nodes have negative weights.  In that case,
     * the sum may be greater if the negative nodes are skipped by moving diagonally.
     * @see findMaxNodeWeightedPathRectGridDownRight
     * @param Z
     * @param begRow
     * @param begCol
     * @param dstRow
     * @param dstCol
     * @return
     */
    static int findMaxNodeWeightedPathRectGridDownRightDiag(IntFromRowCol Z, int begRow, int begCol, int dstRow, int dstCol)
    {
        int gridRows = Z.length();
        int gridCols = Z.getRow(0).length();
        int status = checkRectGridArgs(Z, gridRows, gridCols, begRow, begCol, dstRow, dstCol);
        if (status < 0)
            throw new IllegalArgumentException("checkRectGridArgs: " + status);
        
        final int pathRows = dstRow - begRow + 1;
        final int pathCols = dstCol - begCol + 1;
        int pathSums[][] = new int[pathRows][];
        for (int row = 0; row < pathRows; row++)
            pathSums[row] = new int[pathCols];
        
        // first column of pathSums
        int prevSum = pathSums[0][0] = Z.getIntVal(begRow, begCol);
        for (int nodeRow = begRow+1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++)
            prevSum = pathSums[pathRow][0] = prevSum + Z.getIntVal(nodeRow, begCol);
        
        // first row of pathSums
        prevSum = pathSums[0][0];
        for (int nodeCol = begCol+1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++)
            prevSum = pathSums[0][pathCol] = prevSum + Z.getIntVal(begRow, nodeCol);
        
        for (int nodeRow = begRow+1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++) {
            for (int nodeCol = begCol+1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++) {
                int payRight = pathSums[pathRow][pathCol-1];
                int payDown  = pathSums[pathRow-1][pathCol];
                int payDiag  = pathSums[pathRow-1][pathCol-1];
                prevSum = Math.max(payRight, payDown);
                prevSum = Math.max(prevSum, payDiag);
                pathSums[pathRow][pathCol] = prevSum + Z.getIntVal(nodeRow, nodeCol);
            }
        }
        //List path = new LinkedList<Prc>();
        return pathSums[pathRows-1][pathCols-1];
    }
  
    public static int test_findMaxNodeWeightedPathSumRectGridDownRightDiag(Integer Z[][], int begRow, int begCol, int dstRow, int dstCol, int expected)
    {
    	Sx.format("Max node-weighted path from (%d, %d) to (%d, %d) through:\n", begRow, begCol, dstRow, dstCol);
    	Sx.putsArray(Z);
    	int computed = findMaxNodeWeightedPathSumRectGridDownRightDiag(Z, begRow, begCol, dstRow, dstCol);
    	Sx.format("is found by findMaxNodeWeightedPathSumRectGridDownRightDiag to sum to: %d\n", computed);
    	int delta = expected - computed;   
    	Sx.format("which differs from expected %d by %d\n", expected, delta);
    	return delta;
    }
    
    public static int test_findMaxNodeWeightedPathRectGridDownRightDiag(Integer Z[][], int begRow, int begCol, int dstRow, int dstCol, int expected)
    {
    	Sx.format("Max node-weighted path from (%d, %d) to (%d, %d) through:\n", begRow, begCol, dstRow, dstCol);
    	Sx.putsArray(Z);
    	PrcNode endNode = findMaxNodeWeightedPathRectGridDownRightDiag(Z, begRow, begCol, dstRow, dstCol);
    	int computed = endNode.pay;
        PrcNode begNode = PrcNode.reverse(endNode);
    	Sx.puts("Forward Path: ");
    	for (PrcNode node = begNode; node != null; node = node.next)
    	{
    		Sx.puts(node);
    	}

    	Sx.format("is found by findMaxNodeWeightedPathRectGridDownRightDiag to sum to: %d\n", computed);
    	int delta = expected - computed;   
    	Sx.format("which differs from expected %d by %d\n", expected, delta);
    	return delta;
    }
    
	public static int unit_testA(int level)
	{
		Integer Z[][] = new Integer[][]{
				{ 0, 1, 1, 0 }, 
				{ 0, 0, 2, 0 }, 
				{ 0, 1, 1, 1 }, 
				{ 0, 1, 0, 1 }, 
		};
		int begRow = 0, begCol = 0;
		int dstRow = Z.length - 1;
		int dstCol = Z[0].length - 1;
		int expect = 7;
		int differ = test_findMaxNodeWeightedPathSumRectGridDownRightDiag(Z, begRow, begCol, dstRow, dstCol, expect);		
		return differ;
	}
    
	public static int unit_testB(int level)
	{
		Integer Z[][] = new Integer[][]{
				{  0,  1,  1, -1,  0 }, 
				{  0,  0, -2,  0,  1 }, 
				{  0,  1,  1,  3,  0 }, 
				{  0,  2,  1,  1,  0 }, 
		};
		int begRow = 0, begCol = 0;
		int dstRow = Z.length - 1;
		int dstCol = Z[0].length - 2;
		int expect = 7;
		int differ = test_findMaxNodeWeightedPathSumRectGridDownRightDiag(Z, begRow, begCol, dstRow, dstCol, expect);		
		return differ;
	}

    
	public static int unit_testC(int level)
	{
		Integer Z[][] = new Integer[][]{
				{  0,  1,  1, -1,  0 }, 
				{  0,  0, -2,  0,  1 }, 
				{  0,  1,  1,  3,  0 }, 
				{  0,  1,  1, -2,  0 }, 
				{  0,  2, -1,  4, -1 }, 
		};
		int begRow = 0, begCol = 0;
		int dstRow = Z.length - 1;
		int dstCol = Z[0].length - 2;
		int expect = 8;
		int differ = test_findMaxNodeWeightedPathRectGridDownRightDiag(Z, begRow, begCol, dstRow, dstCol, expect);		
		return differ;
	}

	public static int unit_test(int level) throws IOException
	{
		int statA = unit_testA(level);
		int statB = unit_testB(level);
		int statC = unit_testC(level);
		return Math.abs(statA) + Math.abs(statB) + Math.abs(statC);
	}

	public static void main(String[] args) throws IOException
	{
		unit_test(1);
	}
}
