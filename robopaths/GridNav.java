package sprax.robopaths;


import java.util.LinkedList;
import java.util.Queue;

import sprax.Sx;
import sprax.Sz;
import sprax.arrays.ArrayDiffs;

class Cell {
    final int row;
    final int col;
    Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }
    @Override 
    public String toString() {
        return String.format("<%d, %d>", row, col);
    }
}

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

    public LinkedList<Cell> pathToNearestGoal(int row, int col) 
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
        Queue<Cell> found = markGoals(input, distance, rows, cols); 
        return markDistances(found, distance, rows, cols);
    }
    
    /** 
     * Takes in raw layout and builds queue of goal cells, also starting to fill in the output 
     * which is assumed to come in pre-filled with all zeros. 
     */
    static Queue<Cell> markGoals(final char layout[][], int distance[][], int rows, int cols)
    {
        Queue<Cell> marked = new LinkedList<Cell>();
        for (int row = 0; row < rows; row++) {
            assert (layout[row] != null && layout[row].length >= cols);
            for (int col = 0; col < cols; col++) {
                char val = layout[row][col];
                if (val == X) {
                    distance[row][col] = -1;
                } else if (layout[row][col] == G) {
                    distance[row][col] = 1;
                    marked.add(new Cell(row, col));    // x = row, y = col
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
    static int markDistances(Queue<Cell> marked, int distance[][], int rows, int cols)
    {
        while (!marked.isEmpty()) {
            Cell old = marked.remove();
            int row = old.row;
            int col = old.col;
            int dst = distance[row][col] + 1;
            
            // West (Left)
            col--;
            if (col >= 0 && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Cell(row, col));
            }
            
            // East (Right)
            col += 2;
            if (col < cols && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Cell(row, col));
            }
            
            // North (Up)
            row--;
            col--;
            if (row >= 0 && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Cell(row, col));
            }
            
            // South (Down)
            row += 2;
            if (row < rows && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Cell(row, col));
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
    static LinkedList<Cell> pathToNearestGoal(int minDst[][], int rows, int cols, int row, int col) 
    {
        LinkedList<Cell> path = new LinkedList<>();     // empty list
        int dist, minDist = minimumDistanceToGoal(minDst, rows, cols, row, col);  // error checking
        while (minDist > 0) {
            path.add(new Cell(row, col));
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

    static char[][] testFloorA = {                   // 8x8 test matrix
                               { o, o, o, X, o, o, o, G },
                               { o, o, o, X, o, X, X, o },
                               { o, X, o, X, o, o, X, o },
                               { o, X, o, X, o, o, X, o },
                               { o, X, o, X, X, X, X, o },
                               { o, X, o, o, o, X, o, o },
                               { o, X, o, o, o, X, o, o },
                               { o, X, o, o, o, o, o, o },
    };
    static char[][] testFloorB = {                      // 12x12
                               { o, o, o, o, o, X, o, o, o, o, o, G },     // 0
                               { o, o, o, o, o, X, o, o, o, o, o, o },     // 1
                               { o, o, o, o, o, X, o, X, X, X, o, o },     // 2
                               { o, o, X, X, o, X, o, o, X, o, o, o },     // 3
                               { G, o, X, o, o, X, G, o, X, o, o, o },     // 4
                               { o, o, X, o, X, X, o, o, X, o, o, o },     // 5
                               { o, G, X, o, o, X, o, X, o, o, o, o },     // 6
                               { o, X, X, o, o, o, o, X, o, X, o, o },     // 7
                               { o, o, X, G, G, o, o, X, o, o, G, o },     // 8
                               { o, o, X, o, o, o, o, o, o, o, o, o },     // 9
                               { o, o, X, o, o, o, o, o, o, o, o, o },     // 10
                               { o, o, X, o, o, o, o, G, o, o, o, o },     // 11
    };
    
    
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
        LinkedList<Cell> path = mg.pathToNearestGoal(row, col);
        int pathLen = path.size();
        String label = String.format("Path of length %d from <%d, %d> to nearest goal: ", minDist, row, col);
        Sx.putsList(label, path);
        assert(minDist == pathLen);

        int rows = testFloor.length;
        int cols = testFloor[0].length;
        int gd[][] = new int[rows][cols];  // All 0s 
        int numWrong = GridNav.computeMinDist(testFloor, gd, rows, cols);        
        Sx.putsArray("static GridGoals.goalDistance output:\n", gd, GridNav::printOneDistanceCell);
        
        double err = ArrayDiffs.sumOfSquaredDifferences(gc, gd);
        Sx.format("sum of squared differences: %f\n", err);
        numWrong += Sz.wrong(err == 0.0);
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int unit_test()
    {
        String testName = GridNav.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        
        int status = 0;
        status += test_oneLayout(testFloorA);
        status += test_oneLayout(testFloorB);
        
        Sz.end(testName, 0);
        return status;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
