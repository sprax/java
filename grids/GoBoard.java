package sprax.grids;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class GoBoard
{
    final static int sB = -1;
    final static int sW = 1;
    
    final int        mBoard[][];
    final int        mVisited[][];
    final int        mMaxRow;
    final int        mMaxCol;
    int              mTurn;       // Ordinal number of turn, initially 0
                                   
    public GoBoard(int rows, int cols)
    {
        if (rows < 3 || cols < 3)
            throw new IllegalArgumentException("board must >= 3x3");
        
        mMaxRow = rows - 1;
        mMaxCol = cols - 1;
        mBoard = new int[rows][cols];		// initially all 0
        mVisited = new int[rows][cols];		// initially all 0
    }
    
    public GoBoard(int board[][])
    {
        if (board == null)
            throw new IllegalArgumentException("board is null");
        
        int rows = board.length;
        int cols = board[0].length;
        if (rows < 3 || cols < 3)
            throw new IllegalArgumentException("board must >= 3x3");
        
        mMaxRow = rows - 1;
        mMaxCol = cols - 1;
        // mBoard = new int[rows][cols]; // initially all 0
        mVisited = new int[rows][cols];		// initially all 0
        mBoard = board;	// TODO: should make defensive copy
    }
    
    /**
     * Given a Go board, turn number, and stone color at position (row, col), return true
     * if that stone is captured, false otherwise.
     * 
     * @param turn
     * @param row
     * @param col
     * @return
     */
    public boolean isCaptured(int row, int col)
    {
        assert (0 <= row && row <= mMaxRow && 0 <= col && col <= mMaxCol);
        
        int color = mBoard[row][col];
        if (color == 0)
            return false;
        
        return isCapturedRecurse(color, row, col);
    }
    
    public boolean isCapturedRecurse(int thisColor, int row, int col)
    {
        mVisited[row][col] = mTurn;
        
        if (isFreeRight(thisColor, row, col))
            return false;
        if (isFreeUp(thisColor, row, col))
            return false;
        if (isFreeDown(thisColor, row, col))
            return false;
        if (isFreeLeft(thisColor, row, col))
            return false;
        
        return true;
    }
    
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** return true if the site to the right is open, otherwise false. */
    protected boolean isFreeLeft(int oldColor, int row, int col)
    {
        if (--col < 0 || mVisited[row][col] == mTurn)
            return false;
        
        mVisited[row][col] = mTurn;
        
        int newColor = mBoard[row][col];
        if (newColor == 0)
            return true;
        
        if (newColor == oldColor)
        {
            if (isFreeUp(oldColor, row, col))
                return true;
            if (isFreeDown(oldColor, row, col))
                return true;
            if (isFreeLeft(oldColor, row, col))
                return true;
        }
        return false;
    }
    
    /** return true if the site to the right is open, otherwise false. */
    protected boolean isFreeRight(int oldColor, int row, int col)
    {
        if (++col > mMaxCol || mVisited[row][col] == mTurn)
            return false;
        
        mVisited[row][col] = mTurn;
        
        int newColor = mBoard[row][col];
        if (newColor == 0)
            return true;
        
        if (newColor == oldColor)
        {
            if (isFreeUp(oldColor, row, col))
                return true;
            if (isFreeDown(oldColor, row, col))
                return true;
            if (isFreeRight(oldColor, row, col))
                return true;
        }
        return false;
    }
    
    /** return true if the site to the right is open, otherwise false. */
    protected boolean isFreeUp(int oldColor, int row, int col)
    {
        if (--row < 0 || mVisited[row][col] == mTurn)
            return false;
        
        mVisited[row][col] = mTurn;
        
        int newColor = mBoard[row][col];
        if (newColor == 0)
            return true;
        
        if (newColor == oldColor)
        {
            if (isFreeLeft(oldColor, row, col))
                return true;
            if (isFreeRight(oldColor, row, col))
                return true;
            if (isFreeUp(oldColor, row, col))
                return true;
        }
        return false;
    }
    
    /** return true if the site to the right is open, otherwise false. */
    protected boolean isFreeDown(int oldColor, int row, int col)
    {
        if (++row > mMaxCol || mVisited[row][col] == mTurn)
            return false;
        
        mVisited[row][col] = mTurn;
        
        int newColor = mBoard[row][col];
        if (newColor == 0)
            return true;
        
        if (newColor == oldColor)
        {
            if (isFreeUp(oldColor, row, col))
                return true;
            if (isFreeLeft(oldColor, row, col))
                return true;
            if (isFreeDown(oldColor, row, col))
                return true;
        }
        return false;
    }
    
    public static int unit_test()
    {
        String testName = GoBoard.class.getName() + ".unit_test";
        Sz.begin(testName);
        final int B = -1;
        final int W = 1;
        int board[][] = {
                { 0, 0, B, 0, 0 },
                { 0, B, W, W, 0 },
                { 0, W, B, B, W },
                { 0, B, B, W, 0 },
                { 0, W, W, 0, 0 },
        };
        
        GoBoard goBoard = new GoBoard(board);
        int row = 2, col = 2;
        
        goBoard.mTurn++;
        Sx.format("isCaptured(%d, %d) = %s\n", row, col, goBoard.isCaptured(2, 2));
        
        goBoard.mTurn++;
        board[3][0] = W;
        Sx.format("isCaptured(%d, %d) = %s\n", row, col, goBoard.isCaptured(2, 2));
        
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String args[]) {
        unit_test();
    }
}
