package sprax.grids;

import sprax.sprout.Sx;

/**
 * Square
 */
class CheckerSquare extends GridNodeChar
{
}

/*
 *   ...   ...
 * _____________
 * |     |/////|
 * | 1,0 |/1,1/| ...
 * |_____|/////|
 * |/////|     |
 * |/0,0/| 0,1 | ...
 * |/////|_____|
 * 
 * 
 * @author sprax
 */
public class CheckerBoard extends RectGrid4<Character, CheckerSquare>
{
    protected CheckerSquare mNodes[][];
    
    CheckerBoard(int numRows, int numCols)
    {
        super(numRows, numCols);
    }
    
    @Override
    CheckerSquare createNode(int row, int col) {
        return new CheckerSquare();
    }
    
    @Override
    CheckerSquare[] getNodes(int row) {
        return mNodes[row];
    }
    
    @Override
    CheckerSquare[][] getNodes() {
        return mNodes;
    }
    
    @Override
    CheckerSquare getNode(int row, int col) {
        return mNodes[row][col];
    }
    
    @Override
    protected void createNodes()
    {
        mNodes = new CheckerSquare[mNumRows][];
        for (int row = 0; row < mNumRows; row++) {
            mNodes[row] = new CheckerSquare[mNumCols];
            for (int col = 0; col < mNumCols; col++) {
                mNodes[row][col] = createNode(row, col);
            }
        }
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
                CheckerSquare node = mNodes[row][col];
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
        Sx.puts("showAllMoves");
    }
    
    public static int unit_test()
    {
        int nRows = 8, nCols = 8;
        CheckerBoard bb = new CheckerBoard(nRows, nCols);
        
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
    void setNode(CheckerSquare node, int row, int col) {
        mNodes[row][col] = node;
    }
    
}
