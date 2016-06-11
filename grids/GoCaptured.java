package sprax.grids;

import sprax.sprout.Sx;

public class GoCaptured
{
    /**
     * Given a Go board, turn number, and stone color at postion (row, col), return true
     * if that stone is captured, false otherwise.
     * 
     * @param board
     * @param turn
     * @param color
     * @param row
     * @param col
     * @return
     */
    public static boolean isCaptured(int board[][], int row, int col)
    {
        assert (board != null && 0 <= row && row < board.length && 0 <= col && col < board[0].length);
        
        int color = board[row][col];
        if (color == 0)
            return false;
        
        int rows = board.length - 1;
        int cols = board[0].length - 1;
        boolean visited[][] = new boolean[rows][cols];		// initially all false
        return isCapturedRecurse(board, visited, rows - 1, cols - 1, color, row, col);
    }
    
    protected static boolean isCapturedRecurse(int board[][], boolean visited[][], int lastRow,
            int lastCol, int thisColor, int row, int col)
    {
        visited[row][col] = true;
        
        if (col < lastCol && !visited[row][col + 1]
                && isFreeThisWay(board, visited, lastRow, lastCol, thisColor, row, col + 1))
            return false;
        
        if (row < lastRow && !visited[row + 1][col]
                && isFreeThisWay(board, visited, lastRow, lastCol, thisColor, row + 1, col))
            return false;
        
        if (0 < col && !visited[row][col - 1]
                && isFreeThisWay(board, visited, lastRow, lastCol, thisColor, row, col - 1))
            return false;
        
        if (0 < row && !visited[row - 1][col]
                && isFreeThisWay(board, visited, lastRow, lastCol, thisColor, row - 1, col))
            return false;
        
        return true;
    }
    
    protected static boolean isFreeThisWay(int board[][], boolean visited[][], int lastRow,
            int lastCol, int thisColor, int row, int col)
    {
        int nextColor = board[row][col];
        if (nextColor == 0)
            return true;
        if (nextColor == thisColor
                && !isCapturedRecurse(board, visited, lastRow, lastCol, thisColor, row, col))
            return true;
        return false;
    }
    
    // //////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public static int unit_test(int level)
    {
        String testName = GoCaptured.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN\n");
        final int B = -1;
        final int W = 1;
        int board[][] = {
                { 0, 0, B, 0, 0 },
                { 0, B, W, W, 0 },
                { 0, W, B, B, W },
                { 0, B, B, W, 0 },
                { 0, W, W, 0, 0 },
        };
        int row = 2, col = 2;
        Sx.format("isCaptured(%d, %d) = %s\n", row, col, GoCaptured.isCaptured(board, 2, 2));
        board[3][0] = W;
        Sx.format("isCaptured(%d, %d) = %s\n", row, col, GoCaptured.isCaptured(board, 2, 2));
        
        Sx.puts(testName + " END,  status: PASSED");
        return 0;
    }
    
    public static void main(String args[]) {
        unit_test(1);
    }
}
