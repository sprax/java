package sprax.grids;

import java.util.Random;

import sprax.Sx;
import sprax.arrays.ArrayMaxPathSum;

// TODO: Derive from Grid

/**
 * RectGrid<T>  Grid of packed, connected nodes, generally 2-dimensional,
 * meaning that any node's neighbors can be visualized as lying next to 
 * it in a regular, packed pattern a plane.  Regular means spatially 
 * uniform.  Packed means no holes; all neighbors are present, except at
 * the boundaries, if there are any.
 */
public abstract class RectGrid<T, NodeT extends GridNode<T>> extends Grid<T, NodeT>
{
    public static final int sMaxNumNeighbors = 0;
    static int              sDbg             = 1;
    
    protected int           mNumRows;
    protected int           mNumCols;
    
    RectGrid(int numRows, int numCols)  // template constructor
    {
        super(numRows, numCols);
        if (numRows < 2 || numCols < 2)
            throw new IllegalStateException("RectGrid: dimensions < 2x2");
    }

    abstract void      setNode(NodeT node, int row, int col);
    abstract NodeT     getNode(int row, int col);    // { return mNodes[row][col]; }
    abstract NodeT[]   getNodes(int row);        // { return mNodes[row]; }
    abstract NodeT[][] getNodes();               // { return mNodes; }

    @SuppressWarnings("unchecked")
    protected GridNode<T> addNeighbors(int row, int col, int num)
    {
        GridNode<T> node = getNode(row, col);     // Getting a node by row & col always works for a
                                              // RectGrid
        node.mNumNeighbors = num;
        node.setNeighbors(new GridNode[num]);     // Safe: only creating object references
        return node;
    }
    
    @Override
    void setDimensions(int numRows, int numCols)
    {
        mNumRows = numRows;
        mNumCols = numCols;
    }
    
    // // Even base constructor could do this, but why should it?
    // // Deferring construction until more of the derived class's
    // // framework is in place seems better.
    // void createRows() {
    // mNodes = new GridNode<?>[mNumRows][];
    // for (int row = 0; row < mNumRows; row++) {
    // mNodes[row] = new GridNode<?>[mNumCols];
    // }
    // }
    
    public void setData(int data[][])
    {
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                getNode(row, col).setIntVal(data[row][col]);
            }
        }
    }
    
    /**
     * Set data to integer values from lower up to but not including upper.
     * 
     * @param lower
     * @param upper
     */
    public void setRandomData(int lower, int upper) // TODO: test exception
    {
        if (!(lower < upper))
            throw new IllegalArgumentException("Need lower(" + lower + ") < upper(" + upper + ")");
        int range = upper - lower;
        Random rng = new Random();  // i.e., java.util.Random.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                getNode(row, col).setIntVal(lower + rng.nextInt(range));
            }
        }
    }
    
    public Integer[][] toIntArrays()
    {
        NodeT nodes[][] = getNodes();
        if (nodes == null)
            return null;
        int length = nodes.length;
        Integer A[][] = new Integer[length][];
        for (int row = 0; row < length; row++) {
            if (getNodes(row) == null)
                return null;
            A[row] = new Integer[getNodes(row).length];
            for (int col = 0; col < A[row].length; col++) {
                A[row][col] = getNode(row, col).getIntVal();
            }
        }
        return A;
    }
    
    public void printData(String label)
    {
        Sx.print("RectGrid " + mNumRows + "x" + mNumCols + ": ");
        Sx.puts(label);
        for (int row = 0; row < mNumRows; row++) {
            Sx.putsArray("      ", getNodes(row));
        }
        Sx.puts();
    }
    
    /**
     * TODO: Make static version in ArrayAlgo using interface getIntVal(row, col)
     * 
     * @param begRow
     * @param begCol
     * @param endRow
     * @param endCol
     * @return
     * @return sum of weights along maximal path, including those of the
     *         starting and ending nodes.
     */
    int findMaxNodeWeightedPathSumDownRight(int begRow, int begCol, int dstRow, int dstCol)
    {
        int status = ArrayMaxPathSum.checkRectGridArgs(getNodes(), mNumRows, mNumCols, begRow,
                begCol, dstRow, dstCol);
        if (status < 0)
            throw new IllegalArgumentException("checkRectGridArgs: " + status);
        
        final int pathRows = dstRow - begRow + 1;
        final int pathCols = dstCol - begCol + 1;
        int pathSums[][] = new int[pathRows][];
        for (int row = 0; row < pathRows; row++)
            pathSums[row] = new int[pathCols];
        
        int prevSum = pathSums[0][0] = getNode(begRow, begCol).getIntVal();
        for (int nodeRow = begRow + 1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++)
            prevSum = pathSums[pathRow][0] = prevSum + getNode(nodeRow, begCol).getIntVal();
        
        prevSum = pathSums[0][0];
        for (int nodeCol = begCol + 1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++)
            prevSum = pathSums[0][pathCol] = prevSum + getNode(begRow, nodeCol).getIntVal();
        
        for (int nodeRow = begRow + 1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++) {
            for (int nodeCol = begCol + 1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++) {
                int pathRight = pathSums[pathRow][pathCol - 1];
                int pathDown = pathSums[pathRow - 1][pathCol];
                prevSum = Math.max(pathRight, pathDown);
                pathSums[pathRow][pathCol] = prevSum + getNode(nodeRow, nodeCol).getIntVal();
            }
        }
        return pathSums[pathRows - 1][pathCols - 1];
    }
    
    /**
     * 
     * @param begRow
     * @param begCol
     * @param dstRow
     * @param dstCol
     * @return raw array of linear or "absolute" indices of nodes in the maximal path.
     *         The correspondences between linear indices and row & column indices is given
     *         by this formulae: lindex == row*mNumCols + col
     *         and (row, col) == (lindex/mNumCols, lindex%mNumCols)
     */
    int[] findMaxNodeWeightedPathDownRight(final int begRow, final int begCol
            , final int dstRow, final int dstCol)
    {
        int status = ArrayMaxPathSum.checkRectGridArgs(getNodes(), mNumRows, mNumCols, begRow,
                begCol, dstRow, dstCol);
        if (status < 0)
            throw new IllegalArgumentException("checkRectGridArgs: " + status);
        
        final int pathRows = dstRow - begRow + 1;
        final int pathCols = dstCol - begCol + 1;
        int pathSums[][] = new int[pathRows][];
        for (int row = 0; row < pathRows; row++)
            pathSums[row] = new int[pathCols];
        
        int prevSum = pathSums[0][0] = getNode(begRow, begCol).getIntVal();
        for (int nodeRow = begRow + 1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++)
            prevSum = pathSums[pathRow][0] = prevSum + getNode(nodeRow, begCol).getIntVal();
        
        prevSum = pathSums[0][0];
        for (int nodeCol = begCol + 1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++)
            prevSum = pathSums[0][pathCol] = prevSum + getNode(begRow, nodeCol).getIntVal();
        
        for (int nodeRow = begRow + 1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++) {
            for (int nodeCol = begCol + 1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++) {
                int pathRight = pathSums[pathRow][pathCol - 1];
                int pathDown = pathSums[pathRow - 1][pathCol];
                prevSum = Math.max(pathRight, pathDown);
                pathSums[pathRow][pathCol] = prevSum + getNode(nodeRow, nodeCol).getIntVal();
            }
        }
        int path[] = maxPathFromPathSumsDownRight(pathSums, pathRows, pathCols, begRow, begCol,
                dstRow, dstCol);
        if (sDbg > 0)
            Sx.puts("findMaxNodeWeightedPathDownRight: maxPathSum  "
                    + pathSums[pathRows - 1][pathCols - 1]);
        return path;
    }
    
    // Create return value: path from beg to dst in grid coords, saved
    // as alternating row & col vals: [r0, c0, r1, c1, ... rN, cN],
    // where the path length N = (dstRow - begRow + 1) + (dstCol - begCol + 1) - 1.
    int[] maxPathFromPathSumsDownRight(int pathSums[][], final int pathRows, final int pathCols
            , final int begRow, final int begCol
            , final int dstRow, final int dstCol)
    {
        int pathLength = pathRows + pathCols - 1;
        int path[] = new int[pathLength * 2]; // 2 int vals for each node: row & col
        int step = path.length;
        path[0] = begRow;    // absolute grid coords, not relative path coords
        path[1] = begCol;
        path[--step] = dstCol;
        path[--step] = dstRow;
        while (step > 2) {
            int pathRow = path[step] - begRow;
            int pathCol = path[step + 1] - begCol;
            if (sDbg > 0) {
                try {
                    if (pathRow >= pathRows || pathCol >= pathCols)
                        sDbg++;
                    System.out.format("path[%d, %d]  pathRow %d  pathCol %d  pathSums  %d\n"
                            , step, step + 1, pathRow, pathCol, pathSums[pathRow][pathCol]);
                } catch (NullPointerException ex) {
                    throw new IllegalArgumentException("pathRow & pathCol: " + pathRow + " "
                            + pathCol);
                }
            }
            if (pathRow == 0) {
                path[--step] = pathCol + begCol - 1;
                path[--step] = pathRow + begRow;
            } else if (pathCol == 0) {
                path[--step] = pathCol + begCol;
                path[--step] = pathRow + begRow - 1;
            } else {
                if (sDbg > 2)
                    System.out.format(
                            "        path[%d, %d]  pathRow %d  pathCol %d  pathSums  %d\n"
                            , step, step + 1, pathRow - 1, pathCol - 1, pathSums[pathRow][pathCol]);
                if (pathSums[pathRow][pathCol - 1] > pathSums[pathRow - 1][pathCol]) {
                    path[--step] = pathCol + begCol - 1;
                    path[--step] = pathRow + begRow;
                } else {
                    path[--step] = pathCol + begCol;
                    path[--step] = pathRow + begRow - 1;
                }
            }
        }
        return path;
    }
    
    int findMaxNodeWeightedPathSumDownRightDiag(int begRow, int begCol, int dstRow, int dstCol)
    {
        int status = ArrayMaxPathSum.checkRectGridArgs(getNodes(), mNumRows, mNumCols, begRow,
                begCol, dstRow, dstCol);
        if (status < 0)
            throw new IllegalArgumentException("checkRectGridArgs: " + status);
        
        final int pathRows = dstRow - begRow + 1;
        final int pathCols = dstCol - begCol + 1;
        int pathSums[][] = new int[pathRows][];
        for (int row = 0; row < pathRows; row++)
            pathSums[row] = new int[pathCols];
        
        int prevSum = pathSums[0][0] = getNode(begRow, begCol).getIntVal();
        for (int nodeRow = begRow + 1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++)
            prevSum = pathSums[pathRow][0] = prevSum + getNode(nodeRow, begCol).getIntVal();
        
        prevSum = pathSums[0][0];
        for (int nodeCol = begCol + 1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++)
            prevSum = pathSums[0][pathCol] = prevSum + getNode(begRow, nodeCol).getIntVal();
        
        for (int nodeRow = begRow + 1, pathRow = 1; pathRow < pathRows; pathRow++, nodeRow++) {
            for (int nodeCol = begCol + 1, pathCol = 1; pathCol < pathCols; pathCol++, nodeCol++) {
                int pathRight = pathSums[pathRow][pathCol - 1];
                int pathDown = pathSums[pathRow - 1][pathCol];
                int pathDiag = pathSums[pathRow - 1][pathCol - 1];
                prevSum = Math.max(pathRight, pathDown);
                prevSum = Math.max(prevSum, pathDiag);
                pathSums[pathRow][pathCol] = prevSum + getNode(nodeRow, nodeCol).getIntVal();
            }
        }
        return pathSums[pathRows - 1][pathCols - 1];
    }
    
    public static int unit_test()
    {
        Sx.puts(RectGrid.class.getName() + ".unit_test");
        // RectGrid8Int zoid = new RectGrid8Int(2,2);
        // zoid.printData("zoid");
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
        RectGrid8Int.unit_test();
    }
}
