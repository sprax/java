package sprax.robopaths;

import java.util.LinkedList;
import java.util.Queue;

import sprax.arrays.ArrayDiffs;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Given a non-empty rectangular grid containing zero or more goal cells,
 * and other cells that can either be traversed ("empty" cells) or not
 * ("obstacle" cells, such as might represent wall, barriers, buildings,
 * guards, or whatever, find the shortest distance and minimal path(s) 
 * from any cell to a goal cell.
 * @author Sprax Lines
 */
public class GridNav
{
    static final char o = '.', G = 'G', X = 'X';  // Floor, Goal, Wall or Obstacle
    
    final int         cols, rows, status;
    final char        layout[][];
    final int         minDst[][];     // grid distance to nearest goal
    
    public GridNav(final char layout[][])
    {
        assert (layout != null && layout.length > 0 && layout[0].length > 0);
        this.layout = layout;
        this.rows   = layout.length;
        this.cols   = layout[0].length;
        this.minDst = new int[rows][cols];  // All 0s        
        this.status = computeMinDist(layout, minDst, rows, cols);
    }
    
    public int minimumDistanceToGoal(int row, int col) {
        return minimumDistanceToGoal(minDst, rows, cols, row, col);
    }

    public LinkedList<GridCell> pathToNearestGoal(int row, int col) 
    {
        return pathToNearestGoal(minDst, rows, cols, row, col);   
    }

    //// static methods, friendly access ////
    
    /** Translate distance back into readable characters */
    public static void printOneDistanceCell(int v)
    {
        switch(v) {
            case -1: Sx.format(" X%cX", X); break;
            case  0: Sx.format("  %c ", o); break;     // cell was never visited
            case  1: Sx.format(" *%c*", G); break;
            default: Sx.format("%3d ",  v); break;
        }
    }

    /**
     *  Fills distance array with distances from goals, using specified number of rows and columns.
     * Obviously the pre-allocated input and output arrays must be at least that large. 
     */
    static int computeMinDist(final char input[][], int distance[][], int rows, int cols)
    {
        assert (input != null && input.length > 0 && input[0] != null);     
        Queue<GridCell> found = markGoals(input, distance, rows, cols); 
        return markDistances(found, distance, rows, cols);
    }
    
    /** 
     * Takes in raw layout and builds queue of goal cells, also starting to fill in the output 
     * which is assumed to come in pre-filled with all zeros. 
     */
    static Queue<GridCell> markGoals(final char layout[][], int distance[][], int rows, int cols)
    {
        Queue<GridCell> marked = new LinkedList<GridCell>();
        for (int row = 0; row < rows; row++) {
            assert (layout[row] != null && layout[row].length >= cols);
            for (int col = 0; col < cols; col++) {
                char val = layout[row][col];
                if (val == X) {
                    distance[row][col] = -1;
                } else if (layout[row][col] == G) {
                    distance[row][col] = 1;
                    marked.add(new GridCell(row, col));    // x = row, y = col
                }
            }
        }
        return marked;
    }

    /** Takes in the queue of goal cells, the initialized distance array,
     *  and fills in the distance to any nearest goal.
     * @param marked    temporary queue
     * @param distance  distance array to be filled in
     * @param rows      number of rows
     * @param cols      number of cols
     * @return   0 on success
     */
    static int markDistances(Queue<GridCell> marked, int distance[][], int rows, int cols)
    {
        while (!marked.isEmpty()) {
            GridCell old = marked.remove();
            int row = old.row;
            int col = old.col;
            int dst = distance[row][col] + 1;
            
            // West (Left)
            col--;
            if (col >= 0 && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new GridCell(row, col));
            }
            
            // East (Right)
            col += 2;
            if (col < cols && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new GridCell(row, col));
            }
            
            // North (Up)
            row--;
            col--;
            if (row >= 0 && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new GridCell(row, col));
            }
            
            // South (Down)
            row += 2;
            if (row < rows && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new GridCell(row, col));
            }
        }
        return 0;
    }
    
    static int minimumDistanceToGoal(int distance[][], int rows, int cols, int startRow, int startCol) 
    {
        if (distance == null || rows < 1 || cols < 1)
            throw new IllegalArgumentException("bad distance");
        if (startRow < 0 || startRow >= rows || startCol < 0 || startCol >= cols)
            throw new IllegalArgumentException("bad start");
        return distance[startRow][startCol];
    }
        
    /** 
     * Order of directions tried (ESWN) results in counter-clockwise wall following
     * @param row
     * @param col
     * @return
     */
    static LinkedList<GridCell> pathToNearestGoal(int minDst[][], int rows, int cols, int row, int col) 
    {
        LinkedList<GridCell> path = new LinkedList<>();     // empty list
        int dist, minDist = minimumDistanceToGoal(minDst, rows, cols, row, col);  // error checking
        while (minDist > 0) {
            path.add(new GridCell(row, col));
            if (minDist == 1)                           // goal at row, col
                return path;
            
            // ESWN: Try to keep nearest wall on the left by trying directions
            // in this order: East, South, West, North
            
            // East:
            if (++col < cols) {
                dist = minDst[row][col];
                if (dist > 0 && minDist > dist) {
                    minDist = dist;
                    continue;
                }
            }
            
            // South
            --col;
            if (++row < rows) {
                dist = minDst[row][col];
                if (dist > 0 && minDist > dist) {
                    minDist = dist;
                    continue;
                }
            }

            // West
            --row;
            if (--col >= 0) {
                dist = minDst[row][col];
                if (dist > 0 && minDist > dist) {
                    minDist = dist;
                    continue;
                }
            }
            
            // North
            ++col;
            if (--row >= 0) {
                dist = minDst[row][col];
                if (dist > 0 && minDist > dist) {
                    minDist = dist;
                    continue;
                }
            }
                
            throw new IllegalStateException("no way nearer");
        }
        return path;
    }

    /** NIECE: No input error-checking or exceptions! */
    static void addObstaclesToArray(char obstacles[][], int destArray[][], int rows, int cols) 
    {
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                if (obstacles[row][col] == X)
                    destArray[row][col] = -1;
    }
    
    static int addPathToArray(LinkedList<GridCell> path, int array[][], int rows, int cols) 
    {
        return addPathToArray(path, array, rows, cols, 0);
    }
    
    static int addPathToArray(LinkedList<GridCell> path, int array[][], int rows, int cols, int markOffset) 
    {
        int mark = path.size() + markOffset;    // path length (distance) + some constant
        for (GridCell cell : path) {
            array[cell.row][cell.col] = mark--;
        }
        return 0;
    }
    
    public static int test_oneLayout(char testFloor[][])
    {
        String testName = GridNav.class.getName() + ".test_oneLayout";
        Sz.begin(testName);
        
        Sx.putsArray(testFloor);
        GridNav mg = new GridNav(testFloor);
        int gc[][] = mg.minDst;
        Sx.putsArray("GridGoals constructor made\n", gc, GridNav::printOneDistanceCell);
        int row = 0, col = 0;
        int minDist = mg.minimumDistanceToGoal(row, col);
        LinkedList<GridCell> path = mg.pathToNearestGoal(row, col);
        int pathSize = path.size();
        String label = String.format("Path of length %d from <%d, %d> to nearest goal: ", minDist, row, col);
        Sx.putsList(label, path);
        assert(minDist == pathSize);


        int rows = testFloor.length;
        int cols = testFloor[0].length;
        int gd[][] = new int[rows][cols];  // All 0s 
        int numWrong = GridNav.computeMinDist(testFloor, gd, rows, cols);        
        Sx.putsArray("static GridGoals.goalDistance output:\n", gd, GridNav::printOneDistanceCell);

        int distWithPath[][] = new int[rows][cols];  // All 0s
        addPathToArray(path, distWithPath, rows, cols);
        Sx.putsArray("Array with path only:\n", distWithPath, GridNav::printOneDistanceCell);
        
        addObstaclesToArray(testFloor, distWithPath, rows, cols);
        Sx.putsArray("Array with path and obstacles:\n", distWithPath, GridNav::printOneDistanceCell);
                     
        double err = ArrayDiffs.sumOfSquaredDifferences(gc, gd);
        Sx.format("sum of squared differences: %f\n", err);
        numWrong += Sz.oneIfFalse(err == 0.0);
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int unit_test()
    {
        String testName = GridNav.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        int numWrong = 0;
        
        numWrong += test_oneLayout(testFloorA);
        numWrong += test_oneLayout(testFloorB);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }

    //// TEST DATA ////
    
    static char[][] testFloorA = {  // 8x8 test matrix
                               { o, X, o, X, o, o, X, G },
                               { o, o, o, X, o, o, o, o },
                               { X, X, o, X, o, X, X, o },
                               { o, o, o, X, o, o, o, X },
                               { o, X, o, X, X, X, o, X },
                               { o, o, o, X, o, o, o, o },
                               { o, X, X, o, o, o, X, o },
                               { o, o, o, o, X, o, o, o },
    };
    static char[][] testFloorB = {                                     // 12x12
                               { o, X, o, o, o, o, o, X, o, o, o, G }, // 0
                               { o, X, o, o, X, o, o, X, o, X, X, X }, // 1
                               { o, o, X, o, o, X, o, X, o, o, X, o }, // 2
                               { o, o, X, o, X, X, o, o, X, o, o, o }, // 3
                               { X, o, X, o, o, X, X, o, X, o, X, X }, // 4
                               { o, o, X, X, o, X, o, o, X, o, o, o }, // 5
                               { o, X, X, o, o, X, o, X, o, X, o, o }, // 6
                               { o, o, X, o, X, X, o, X, X, o, X, o }, // 7
                               { X, o, X, o, o, X, o, o, X, o, X, o }, // 8
                               { o, o, X, X, o, X, X, o, o, X, o, o }, // 9
                               { o, X, X, o, o, X, o, X, o, X, o, o }, // 10
                               { o, o, o, o, X, X, o, X, o, o, o, o }, // 11
    };
    
}
