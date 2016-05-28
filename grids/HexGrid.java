package sprax.grids;

import sprax.Sx;

/*
 * HexGrid<T>  Hexagonal grid of packed, connected hexagonal nodes, 
 * generally as if regularly packed in a plane.  Regular means spatially 
 * uniform.  Packed means no holes; all neighbors are present, except at
 * the boundaries, if there are any.  The grid is self-similar in that both
 * the nodes and the whole can be visualized as hexagons.  Either the nodes
 * or the whole can be imagined as regular, or both can be regular iff the 
 * grid's edges all contains the same number of nodes.
 * 
 *  An odd number of rows makes for easier accounting.  The regular hex grid
 *  below has 5 row and 3 columns as mapped onto a rectangular coordinate
 *  system with its axes rotate 30 degrees, and it has uniform edge lengths 
 *  of 3 nodes.  In general, and ignoring any external orientation, a
 *  a hex grid can be specified as having M rows and N columns, or as 
 *  having 3 edge lengths S, T, and U.  We may write these dimensions as
 *  MxN or S^T^U.  A regular hex grid can be specified by a single edge
 *  length S or the radius R, where the S=3 grid below has radius R=3,
 *  diameter D=5 (which is also the maximum row length).  Similarly, the
 *  S=4 grid further below has R=4 and D=7.
 *                                  ___
 *  To see 5x3 properly,        ___/   \___                 
 *  cock your head:         ___/   \_2_/   \___             
 *                         /   \_1_/   \_6_/   \
 *  row 0 has 3 cols       \_0_/   \_5_/   \_B_/
 *                         /   \_4_/   \_A_/   \
 *  row 1 has 4 cols       \_3_/   \_9_/   \_F_/
 *                         /   \_8_/   \_E_/   \
 *  row 2 has 5 cols       \_7_/   \_D_/   \_._/
 *  row 3 has 4 cols           \_C_/   \_._/
 *  row 4 has 3 cols               \_._/
 *  
 *  An even number of rows with an odd number of columns looks wrong, until 
 *  you look at it sideways:  This one can be seen as 6x3 or, better, as 5x4:
 *                                  ___
 *   6x3:                       ___/   \___            5x4:      
 *  <0, 3>                  ___/   \_E_/   \___       <0, 4>      
 *  <1, 4>                 /   \_8_/   \___/   \      <1, 5>
 *  <2, 5>                 \_3_/   \_D_/   \___/      <2, 6>
 *  <3, 5>                 /   \_7_/   \_._/   \      <3, 5>
 *  <4, 4>                 \_2_/   \_C_/   \___/      <4, 4>
 *  <5, 3>                 /   \_6_/   \_._/   \         24 = 20 + 4
 *     24 = 18 + 6         \_1_/   \_B_/   \___/     
 *                         /   \_5_/   \_._/   \     
 * (6/2)*(5/2) = 6         \_0_/   \_A_/   \___/     (5/2)*(4/2) = 4
 *                             \_4_/   \_F_/
 *                                 \_9_/
 *                                 
 *  The 5x4 interpretation is better in terms of assigning neighbors
 *  at the expected geometric positions.  Perhaps the (rows, cols) constructor
 *  should convert (6, 3) to (5,4)?  No.  
 *  Note that 8x3 is equivalent to 3^4^5:      8         .   .   .
 *  which not regular and not even symmetric.  7       .   .   .   .
 *  It is also far different from 3x8,         6     .   .   .   .   .
 *  which is equivalent to 2^2^8:              5   .   .   .   .   .   .
 *                                             4     .   .   .   .   .   .
 *     .   .   .   .   .   .   .   .           3       .   .   .   .   .
 *   .   .   .   .   .   .   .   .   .         2         .   .   .   .
 *     .   .   .   .   .   .   .   .           1           .   .   .
 *     
 *  And here is 7x4, or 4 on each of 6 sides:
 *               ___ 
 *           ___/   \___
 *       ___/   \___/   \___             
 *   ___/   \___/   \___/   \___         
 *  /   \___/   \___/   \___/   \
 *  \___/   \___/   \___/   \___/
 *  /   \___/   \___/   \___/   \
 *  \___/   \___/   \___/   \___/
 *  /   \___/   \___/   \___/   \
 *  \___/   \___/   \___/   \___/
 *  /   \___/   \___/   \___/   \
 *  \___/   \___/   \___/   \___/
 *      \___/   \___/   \___/
 *          \___/   \___/
 *              \___/
 *  
 *  In general, M rows (where M > 2) and N cols in the first row gives
 *  M*N + g(M) nodes, where the function
 *  where g(M) = SUM((k - 1)/2) for k = 3 up to and including M,
 *  or, more simply, g(M) = (M/2)*((M-1)/2), where of course 3/2 == 1, etc.
 *  Thus g<3 ... 16> = <1, 2, 4, 6, 9, 12, 16, 20, 25, 30, 36, 42, 49, 56>.
 *  
 *  TODO: Make a constructor that takes the three edge lengths and converts
 *  them to rows and cols.
 * 
 *  TODO: Should the class itself be generic in node type?  As in:
 *  public class HexGrid<Node extends GridNode<?>> extends Grid<GridNode<?>>
 *
 */
public class HexGrid<T, NodeT extends GridNode<T>> extends Grid<T, NodeT>
{
    public static final int sMaxNumNeighbors = 6;
    
    final int               mNumRows;
    final int               mNumCols;
    NodeT[][]               mNodes;
    
    HexGrid(int numRows, int numCols)         // single constructor
    {
        super(numRows, numCols);
        mNumRows = numRows;
        mNumCols = numCols;
    }                // call base template constructor
    
    static int sTot = 0;
    
    @Override
    protected void createNodes()
    {
        int row = 0, numHexCols = mNumCols, halfNumRows = mNumRows / 2;
        
        sTot = 0;
        mNodes = (NodeT[][]) new GridNode<?>[mNumRows][];
        for (; row < halfNumRows; row++, numHexCols++) {
            mNodes[row] = (NodeT[]) new GridNode<?>[numHexCols];
            // System.out.format("\nHex<%d, %d> row %d has %d cols:", mNumRows, mNumCols, row,
            // numHexCols);
            for (int col = 0; col < numHexCols; col++) {
                mNodes[row][col] = createNode(row, col);
                // System.out.format(" node(%d, %d)", row, col);
                sTot++;
            }
        }
        if (row + row == mNumRows)  // mNumRows is even
            numHexCols--;
        for (; row < mNumRows; row++, numHexCols--) {
            mNodes[row] = (NodeT[]) new GridNode<?>[numHexCols];
            // System.out.format("\nHex<%d, %d> row %d has %d cols:", mNumRows, mNumCols, row,
            // numHexCols);
            for (int col = 0; col < numHexCols; col++) {
                mNodes[row][col] = createNode(row, col);
                // System.out.format(" node(%d, %d)", row, col);
                sTot++;
            }
        }
        System.out.format("\nHex<%d, %d> nodes: %3d", mNumRows, mNumCols, sTot);
    }
    
    @Override
    protected void setNeighbors()
    {
        // Fill in all the nodes' neighbor arrays with pointers to the created nodes.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                setNeighborsFromRowCol(row, col);
            }
        }
    }
    
    /**
     * setNeighborsFromRowCol adds up to 6 neighbors to the grid node at
     * the position specified by row and col. The six corner nodes each
     * have 3 neighbors, the other edge nodes have 5, and interior nodes
     * have 6 neighbors.
     * 
     * @param row
     * @param col
     */
    protected void setNeighborsFromRowCol(int row, int col)
    {
        GridNode<?> node = mNodes[row][col];
        int maxRowIndex = mNumRows - 1;
        int maxColIndex = mNumCols - 1;
        int minRow = (row == 0) ? 0 : row - 1;
        int maxRow = (row == maxRowIndex) ? maxRowIndex : row + 1;
        int minCol = (col == 0) ? 0 : col - 1;
        int maxCol = (col == maxColIndex) ? maxColIndex : col + 1;
        // Get number of neighbors: (maxRow - minRow + 1)*(maxCol - minCol + 1) - 1;
        // The neighbor array will hold only as many refs as there are neighbors.
        int dfRow = maxRow - minRow;
        int dfCol = maxCol - minCol;
        node.mNumNeighbors = dfRow * dfCol + dfRow + dfCol;
        node.mNeighbors = new GridNode[node.mNumNeighbors]; // Safe: only creating object refs
        int numSet = 0;
        for (row = minRow; row <= maxRow; row++) {
            for (col = minCol; col <= maxCol; col++) {
                GridNode<?> neighbor = mNodes[row][col];
                if (neighbor != node) {
                    node.mNeighbors[numSet++] = (GridNode) neighbor; // Safe: only assigning refs
                }
            }
        }
    }
    
    public static int unit_test()
    {
        Sx.puts("HexGrid unit_test");
        for (int j = 3; j < 20; j++)
            for (int k = 3; k < 5; k++) {
                HexGrid<Integer, GridNode<Integer>> hexGrid = new HexGrid<Integer, GridNode<Integer>>(
                        j, k);
                int dif = sTot - j * k;
                int num = (j / 2) * ((j - 1) / 2);
                Sx.print("  " + dif + "  " + num);
            }
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
    @Override
    NodeT createNode(int row, int col) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    void setDimensions(int numRows, int numCols) {
        // TODO Auto-generated method stub
        
    }
}
