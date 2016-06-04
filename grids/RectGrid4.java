package sprax.grids;

import sprax.Sx;

/**
 * RectGrid<T> Grid of packed, connected nodes, generally 2-dimensional,
 * meaning that any node's neighbors can be visualized as lying next to
 * it in a regular, packed pattern a plane. Regular means spatially
 * uniform. Packed means no holes; all neighbors are present, except at
 * the boundaries, if there are any.
 */
public abstract class RectGrid4<T, NodeT extends GridNode<T>> extends RectGrid<T, NodeT>
{
    public static final int sMaxNumNeighbors = 4;
    
    RectGrid4(int numRows, int numCols)         // single constructor
    {
        super(numRows, numCols);
    }                // call base template constructor
    
    /**
     * Set the neighbors to be only the nodes up, right, down, and left (N, E, S, W),
     * not up-left, up-right, down-right, or down-left (NW, NE, SE, SW).
     * Neighbors are ordered clockwise, starting with the up neighbor, if it
     * exists, then right, down, and left.
     */
    protected void setNeighborsCross()
    {
        // Fill in all the nodes' neighbor arrays with pointers to the created nodes.
        int row = 0, lastRow = mNumRows - 1;
        int col = 0, lastCol = mNumCols - 1;
        
        // First corner: The node at (0, 0) gets 2 neighbors
        GridNode<T> node = addNeighbors(row, col, 2);
        node.setNeighbor(0, getNode(1, 0));  // up // Safe: only assigning refs
        node.setNeighbor(1, getNode(0, 1));  // right // Safe: only assigning refs
        
        // First row: Each node gets 3 neighbors
        for (row = 0, col = 1; col < lastCol; col++) {
            node = addNeighbors(row, col, 3);
            node.setNeighbor(0, getNode(1, col));        // up
            node.setNeighbor(1, getNode(0, col + 1));      // right
            node.setNeighbor(2, getNode(0, col - 1));      // left
        }
        
        // Second corner: The node at (0, lastCol) gets 2 neighbors
        node = addNeighbors(row, col, 2);
        node.setNeighbor(0, getNode(1, lastCol));    // up
        node.setNeighbor(1, getNode(0, lastCol - 1));    // left
        
        for (row = 1; row < lastRow; row++) {
            
            // First column: Each node gets 3 neighbors
            node = addNeighbors(row, col = 0, 3);
            node.setNeighbor(0, getNode(row + 1, col));  // up
            node.setNeighbor(1, getNode(row, col + 1));  // right
                                                                   // ////////////////////////////
            node.setNeighbor(2, getNode(row - 1, col));  // down
            
            // Interior: Each node gets 4 neighbors
            for (col = 1; col < lastCol; col++) {
                node = addNeighbors(row, col, 4);
                node.setNeighbor(0, getNode(row + 1, col));  // up
                node.setNeighbor(1, getNode(row, col + 1));  // right
                node.setNeighbor(2, getNode(row - 1, col));  // down
                node.setNeighbor(3, getNode(row, col - 1));  // left
            }
            
            // Last column: Each node gets 3 neighbors
            node = addNeighbors(row, col, 3);
            node.setNeighbor(0, getNode(row + 1, col));    // up
            node.setNeighbor(1, getNode(row - 1, col));    // down
            node.setNeighbor(2, getNode(row, col - 1));    // left
        }
        
        // Third corner: The node at (lastRow, 0) gets 2 neighbors
        node = addNeighbors(row, col = 0, 2);
        node.setNeighbor(0, getNode(row, 1));          // right
        node.setNeighbor(1, getNode(row - 1, 0));          // down
        
        // Last row: Each node gets 3 neighbors
        for (col = 1; col < lastCol; col++) {
            node = addNeighbors(row, col, 3);
            node.setNeighbor(0, getNode(row, col + 1));      // right
            node.setNeighbor(1, getNode(row - 1, col));      // down
            node.setNeighbor(2, getNode(row, col - 1));      // left
        }
        
        // Fourth corner: The node at (lastRow, lastCol) gets 2 neighbors
        node = addNeighbors(row, col, 2);
        node.setNeighbor(0, getNode(row - 1, lastCol));    // down
        node.setNeighbor(1, getNode(row, lastCol - 1));    // left
    }
    
    /**
     * Sets neighbors at each squares 4 corners. Modeling a checker board
     * with a RectGrid4 (not a RectGrid9) means that all white squares are
     * ignored, so a standard 8x8 board comprised of black and white squares
     * is modeled by a 4x4 board that tracks only the black squares.
     * The first and last corner nodes each get 1 neighbor, all other edge
     * nodes each get 2, and any interior node gets 4 neighbors.
     * Neighbors are ordered clockwise, starting with the upper-left neighbor, if it
     * exists, then upper-right, lower-right, and finally lower-left.
     */
    void setNeighborsChecker()  // TODO: this is still cross
    {
        // For any node in an evenly indexed row, starting with row 0, right
        // neighbors are in the column with the same index as the node itself,
        // and left neighbors are in the column with node's col-1.
        // For any node in an oddly indexed row, left neighbros have the
        // same col index, but right neighbors have column index col+1.
        
        // Fill in all the nodes' neighbor arrays with pointers to the created nodes.
        int row = 0, lastRow = mNumRows - 1;
        int col = 0, lastCol = mNumCols - 1;
        
        // First corner: The node at (0, 0) gets 1 neighbor
        GridNode<T> node = addNeighbors(row, col, 1);
        node.setNeighbor(0, getNode(row + 1, col));        // up-right; safe: only assigning
                                                               // ref)
        
        // First row: Each node gets 2 neighbors
        for (col = 1; col <= lastCol; col++) {
            node = addNeighbors(row, col, 2);
            node.setNeighbor(0, getNode(row + 1, col - 1));      // up-left
            node.setNeighbor(1, getNode(row + 1, col));      // up-right
        }
        
        boolean oddRow = true;
        for (row = 1; row < lastRow; row++, oddRow = !oddRow) {
            
            if (oddRow) {
                
                // Odd row interior: Each node gets 4 neighbors
                for (col = 0; col < lastCol; col++) {
                    node = addNeighbors(row, col, 4);
                    node.setNeighbor(0, getNode(row + 1, col));    // up-left
                    node.setNeighbor(1, getNode(row + 1, col + 1));    // up-right
                    node.setNeighbor(2, getNode(row - 1, col + 1));    // down-right
                    node.setNeighbor(3, getNode(row - 1, col));    // down-left
                }
                
                // Odd row, last column: Each node gets 2 neighbors
                node = addNeighbors(row, col, 2);
                node.setNeighbor(0, getNode(row + 1, col));      // up-left
                node.setNeighbor(1, getNode(row - 1, col));      // down-left
                
            } else {  // even row
            
                // Even row, first column: Each node gets 2 neighbors
                node = addNeighbors(row, col = 0, 2);
                node.setNeighbor(0, getNode(row + 1, col));      // up-right
                node.setNeighbor(1, getNode(row - 1, col));      // down-right
                
                // Even row interior: Each node gets 4 neighbors, even the last node.
                for (col = 1; col <= lastCol; col++) {
                    node = addNeighbors(row, col, 4);
                    node.setNeighbor(0, getNode(row + 1, col - 1));    // up-left
                    node.setNeighbor(1, getNode(row + 1, col));    // up-right
                    node.setNeighbor(2, getNode(row - 1, col));    // down-right
                    node.setNeighbor(3, getNode(row - 1, col - 1));    // down-left
                }
            }
        }
        
        // Last row: Each node gets 2 neighbors
        if (oddRow) {
            // Odd row at top: each node gets 2 neighbors
            for (col = 0; col < lastCol; col++) {
                node = addNeighbors(row, col, 2);
                node.setNeighbor(0, getNode(row - 1, col + 1));    // down-right
                node.setNeighbor(1, getNode(row - 1, col));    // down-left
            }
            // Odd row, last node is the corner at (lastRow, lastCol): 1 neighbor
            node = addNeighbors(row, col, 1);
            node.setNeighbor(0, getNode(row - 1, col));        // down-left
        } else {
            // Even row, first node is the corner at (lastRow, 0): 1 neighbor
            node = addNeighbors(row, col = 0, 1);
            node.setNeighbor(0, getNode(row - 1, col));        // down-right
            // Even row at top: each node gtes 2 neighbors
            for (col = 1; col <= lastCol; col++) {
                node = addNeighbors(row, col, 2);
                node.setNeighbor(0, getNode(row - 1, col));    // down-right
                node.setNeighbor(1, getNode(row - 1, col - 1));    // down-left
            }
        }
    }
    
    public static int unit_test()
    {
        Sx.puts(RectGrid4.class.getName() + ".unit_test");
        RectGrid4<Character, GridNodeCharTest> rectGrid4Checked = new RectGrid4Checked(7, 7);
        rectGrid4Checked.printData("Checker neighbors");
        
        RectGrid4<Character, GridNodeCharTest> rectGrid4Crossed = new RectGrid4Crossed(7, 7);
        rectGrid4Crossed.printData("Crossed neighbors");
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
        // RectGrid8Int.unit_test();
    }
    
}

class GridNodeCharTest extends GridNodeChar
{
    @Override
    public void setNeighbor(int idx, GridNode<Character> gridNode) {
        mNeighbors[idx] = gridNode;
        char ch = getData();
        ch += 1;
        setData(ch);
    }
}

class RectGrid4Checked extends RectGrid4<Character, GridNodeCharTest>
{
    GridNodeCharTest[][] mNodes;
    
    RectGrid4Checked(int rows, int cols) {
        super(rows, cols);
    }
    
    @Override
    GridNodeCharTest getNode(int row, int col) {
        return mNodes[row][col];
    }
    
    @Override
    GridNodeCharTest[] getNodes(int row) {
        return mNodes[row];
    }
    
    @Override
    GridNodeCharTest[][] getNodes() {
        return mNodes;
    }
    
    @Override
    GridNodeCharTest createNode(int row, int col) {
        return new GridNodeCharTest();
    }
    
    @Override
    void createNodes() {
        mNodes = new GridNodeCharTest[mNumRows][];
        for (int row = 0; row < mNumRows; row++) {
            mNodes[row] = new GridNodeCharTest[mNumCols];
            for (int col = 0; col < mNumCols; col++) {
                mNodes[row][col] = createNode(row, col);
                mNodes[row][col].setData('A');
            }
        }
    }
    
    @Override
    protected void setNeighbors()
    {
        setNeighborsChecker();
    }
    
    @Override
    void setNode(GridNodeCharTest node, int row, int col) {
        mNodes[row][col] = node;
    }
    
}

class RectGrid4Crossed extends RectGrid4Checked
{
    RectGrid4Crossed(int numRows, int numCols) {
        super(numRows, numCols);
    }
    
    @Override
    protected void setNeighbors()
    {
        setNeighborsCross();
    }
}
