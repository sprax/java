package sprax.graphs;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import sprax.arrays.Arrays1d;
import sprax.sprout.Sx;

/**
 * Given a matrix containing 0 and 1. Consider 1 as 'Land' and 0 as 'Water'. Find out the number of
 * 'Islands' in the matrix. That is, set of all adjacent 1 will make up for an island.
 * 
 * For example:
 * 
 * [ 0 1 1 0 1 ] [ 1 1 1 0 0 ] [ 0 0 0 1 1 ] [ 1 0 0 1 0 ]
 * 
 * This problem has 4 islands. ( consider set of 1s, vertically, horizontally and diagonally ).
 * 
 * Note: Classify this as a graph problem, not an array problem, because the initial array is used
 * only to specify a graph; the graph does not cover the whole array. In other words, the problem
 * domain is a graph constructed from only part the array.
 */

class Anode
{
    int         mValue;    // original array value
    Point       mPoint;    // node location (x = row, y = col)
    List<Point> mAdj;      // nearest neighbors
    int         mComponent;
    
    Anode(int value, Point point, List<Point> adjList, int component)
    {
        mValue = value;
        mPoint = point;
        mAdj = adjList;
        mComponent = component;
    }
}

///   Agraph: Used as a 2-D array graph, but works with any graph of 2D points
class Agraph
{
    int         mComponentCount;
    List<Anode> mNodes;
    
    Agraph(int compCount, List<Anode> nodes)
    {
        mComponentCount = compCount;
        mNodes = nodes;
    }
}

public class ArrayIslands
{
    int         count;          // initially zero
    boolean     mMarked[][];    // initially all false
    final int   mArray[][];
    final int   mRows;
    final int   mCols;
    int         mComponentCount;
    List<Anode> mNodes;
    Agraph      mGraph;
    
    /**
     * Constructor uses BFS to build a minimal graph, counting the connected components along the
     * way
     */
    public ArrayIslands(final int array[][])
    {
        // FIXME: handle case of 1x1 array?
        assert (array != null && array.length > 1);
        mRows = array.length;
        mCols = array[0].length;
        mArray = array;
        mMarked = new boolean[mRows][mCols];
        mNodes = new ArrayList<Anode>();
        
        for (int j = 0; j < mRows; j++)
        {
            int rowJ[] = mArray[j];
            assert (rowJ.length == mCols);
            for (int k = 0; k < mCols; k++)
            {
                if (!mMarked[j][k] && rowJ[k] > 0)
                {
                    mMarked[j][k] = true;
                    exploreNewIsland(j, k);
                    mComponentCount++;
                }
            }
        }
        mGraph = new Agraph(mComponentCount, mNodes);
    }
    
    protected void exploreNewIsland(int row, int col)
    {
        Point newPoint = new Point(row, col);
        Queue<Point> queue = new LinkedList<Point>();
        queue.add(newPoint);
        do {
            Point point = queue.remove();
            List<Point> adjList = getAdjListRightDownLeftUp(point);
            Anode node = new Anode(mArray[row][col], point, adjList, mComponentCount);
            mNodes.add(node);
            for (Point adj : adjList)
            {
                queue.add(adj);
            }
        } while (!queue.isEmpty());
    }
    
    private List<Point> getAdjListRightDownLeftUp(Point point)
    {
        ArrayList<Point> adjList = new ArrayList<Point>();
        int row = point.x;
        int col = point.y + 1; // right
        if (col < mCols) // in bounds right?
            addNewPointIfPosVal(adjList, row, col);
        ++row;
        --col; // down
        if (row < mRows) // in bounds bottom?
            addNewPointIfPosVal(adjList, row, col);
        --row;
        --col; // left
        if (col >= 0) // in bounds left?
            addNewPointIfPosVal(adjList, row, col);
        --row;
        ++col; // up
        if (row >= 0) // in bounds top?
            addNewPointIfPosVal(adjList, row, col);
        return adjList;
    }
    
    /** If this point position is new (unmarked) and positively values, add it. */
    private void addNewPointIfPosVal(List<Point> adjList, int x, int y)
    {
        if (mMarked[x][y] == false && mArray[x][y] > 0)
        {
            mMarked[x][y] = true;
            adjList.add(new Point(x, y));
        }
    }
    
    public static int test_ArrayIslands()
    {
        int errors = 0;
        final int array[][] = new int[][] {
                { 0, 1, 1, 0, 2, 0, 0, 0, 0, 0 },
                { 1, 1, 1, 0, 0, 0, 3, 3, 0, 3 },
                { 0, 0, 1, 0, 0, 0, 3, 0, 3, 3 },
                { 4, 0, 0, 5, 5, 0, 3, 3, 3, 0 },
                { 0, 6, 0, 5, 0, 0, 3, 0, 3, 0 }
        };
        ArrayIslands ai = new ArrayIslands(array);
        int nComp = ai.mComponentCount;
        if (nComp != 6)
            errors++;
        Sx.puts("Found " + nComp + " islands (connected components in:");
        Sx.putsArray(array);
        Sx.puts();
        
        final int matrix[][] = new int[][] {
                { 0, 1, 0, 2, 2, 0, 0, 0 },
                { 3, 0, 4, 0, 2, 0, 5, 5 },
                { 0, 0, 4, 0, 0, 0, 5, 0 },
                { 6, 6, 0, 0, 7, 0, 0, 0 },
                { 6, 0, 0, 0, 0, 8, 8, 8 },
                { 6, 6, 6, 0, 0, 0, 0, 8 },
                { 6, 0, 6, 0, 0, 8, 8, 8 },
                { 6, 0, 6, 0, 0, 8, 0, 8 },
                { 6, 6, 0, 0, 0, 8, 8, 8 }
        };
        ai = new ArrayIslands(matrix);
        nComp = ai.mComponentCount;
        if (nComp != 8)
            errors++;
        Sx.puts("Found " + nComp + " islands (connected components in:");
        Sx.putsArray(matrix);
        Sx.puts();
        
        return errors;
    }
    
    public static int unit_test(int level)
    {
        String testName = Arrays1d.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN\n");
        
        int errors = test_ArrayIslands();
        
        Sx.puts(testName + " ENDED with " + errors + " errors.");
        return errors;
    }
    
    public static void main(String[] args)
    {
        unit_test(0);
    }
}
