package sprax.grids;

/**
 * RectGrid<T> Grid of packed, connected nodes, generally 2-dimensional,
 * meaning that any node's neighbors can be visualized as lying next to
 * it in a regular, packed pattern a plane. Regular means spatially
 * uniform. Packed means no holes; all neighbors are present, except at
 * the boundaries, if there are any.
 */
public abstract class RectGrid8<T, NodeT extends GridNode<T>> extends RectGrid<T, NodeT>
{
    public static final int sMaxNumNeighbors = 8;
    
    // GridNode<?>[][] mNodes; // TODO: Really? Look at concrete derived class
    
    RectGrid8(int numRows, int numCols)         // single constructor
    {
        super(numRows, numCols);
    }                // call base template constructor
    
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
     * setNeighborsFromRowCol adds up to 8 neighbors to the grid node at
     * the position specified by row and col. The four corner nodes each
     * get 3 neighbors, the edge nodes get 6, and any interior nodes get 8.
     * 
     * @param row
     * @param col
     */
    @SuppressWarnings("unchecked")
    protected void setNeighborsFromRowCol(int row, int col)
    {
        GridNode<T> node = getNode(row, col);
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
                GridNode<T> neighbor = getNode(row, col);
                if (neighbor != node) {
                    node.mNeighbors[numSet] = (GridNode<T>) neighbor; // Safe: only assigning refs
                    numSet++;
                }
            }
        }
    }
    
}
