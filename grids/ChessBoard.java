package sprax.grids;

import sprax.Sx;

/**
 * Square
 */
class ChessSquare extends GridNodeChar
{
}

/*
 *   ...   ...
 *  ___________
 * |     |/////|
 * | 1,0 |/1,1/| ...
 * |_____|/////|
 * |/////|     |
 * |/0,0/| 0,1 | ...
 * |/////|_____|
 * 
 * 
 * @author sprax
 *
 */
public class ChessBoard extends RectGrid4<Character, ChessSquare>
{
    protected ChessSquare mNodes[][];
    
    ChessBoard(int numRows, int numCols)
    {
        super(numRows, numCols);
    }
    
    @Override
    ChessSquare createNode(int row, int col) {
        return new ChessSquare();
    }
    
    @Override
    ChessSquare[] getNodes(int row) {
        return mNodes[row];
    }
    
    @Override
    ChessSquare[][] getNodes() {
        return mNodes;
    }
    
    @Override
    ChessSquare getNode(int row, int col) {
        return mNodes[row][col];
    }
    
    @Override
    protected void createNodes()
    {
        mNodes = new ChessSquare[mNumRows][];
        for (int row = 0; row < mNumRows; row++) {
            mNodes[row] = new ChessSquare[mNumCols];
            for (int col = 0; col < mNumCols; col++) {
                mNodes[row][col] = createNode(row, col);
            }
        }
    }
    
    /**
     * setNeighborsFromRowCol adds up to 4 neighbors to the grid node at
     * the position specified by row and col. A corner square has 1 neighbor
     * of the same color and 2 neighbors of the opposite color.
     * An (non-corner) edge node has 2 same-color neighbors and 3 opposite
     * color neighbors. An interior node has 4 of each.
     * 
     * Assume that the first node, at row, col == 0,0, is dark.
     * For correspondence with actual games, visualize this square
     * at the bottom left corner. See the class definition comment.
     * 
     * @param row
     * @param col
     */
    protected void setNeighborsFromRowCol(int row, int col)
    {
        // setNeighborsFromRowColChecker(row, col);
    }
    
    // If we modeled the board with each square having 8 neighbors,
    // we'd need to distinguish light from dark squares.
    public boolean isDarkSquare(int row, int col)
    {
        boolean evenRow = row % 2 == 0;
        boolean evenCol = col % 2 == 0;
        if ((evenRow && evenCol) || (!evenRow) && (!evenCol))
            return true;
        return false;
    }
    
    public void setLetters(String letterString)
    {
        if (letterString != null)
            setLetters(letterString.toCharArray());
    }
    
    public void setLetters(char letters[])
    {
        if (letters == null || letters.length == 0)
            return;
        
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                mNodes[row][col].setIntVal(letters[(mNumCols * row + col) % letters.length]);
            }
        }
    }
    
    public void setLetters(char letterGrid[][])
    {
        // top-level null check
        if (letterGrid == null || letterGrid.length < mNumRows)
            return;
        // no checking of rows...
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                ChessSquare node = mNodes[row][col];
                node.setData(letterGrid[row][col]);
            }
        }
    }
    
    public void printBoard(String label)
    {
        Sx.print(getClass().getName() + mNumRows + "x" + mNumCols + ": ");
        Sx.puts(label);
        for (int row = 0; row < mNumRows; row++) {
            Sx.putsArray("     ", mNodes[row]);
        }
        Sx.puts();
    }
    
    public void showAllMoves(int nShowCols)
    {
        int q = 0;
        
        Sx.puts("showAllMoves");
    }
    
    public static int unit_test()
    {
        int nRows = 8, nCols = 8;
        ChessBoard bb = new ChessBoard(nRows, nCols);
        
        bb.showAllMoves(nCols);
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
    @Override
    void setNeighbors() {
        setNeighborsChecker();
    }
    
    @Override
    void setNode(ChessSquare node, int row, int col) {
        mNodes[row][col] = node;
    }
    
}
