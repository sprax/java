package sprax.robopaths;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import sprax.Sx;
import sprax.arrays.ArrayDiffs;

public class GridNav
{
    static final char o = '.', G = 'G', X = 'X';  // Floor, Goal, Wall or Obstacle
            
    final int         cols, rows, status;
    final char        layout[][];
    final int         distance[][];     // grid distance to nearest goal
    
    public GridNav(final char layout[][])
    {
        // TODO: Check for null and length < 1; throw exceptions? For now:
        assert (layout != null && layout.length > 0 && layout[0].length > 0);
        this.layout = layout;
        this.rows = layout.length;
        this.cols = layout[0].length;
        this.distance = new int[rows][cols];  // All 0s        
        this.status = goalDistance(layout, distance, rows, cols);
    }
    
    // Fills distance array with distances from goals, using specified number of rows and columns.
    // Obviously the pre-allocated input and output arrays must be at least that large. 
    static int goalDistance(final char input[][], int distance[][], int rows, int cols)
    {
        assert (input != null && input.length > 0 && input[0] != null);     
        Queue<Point> found = markGoals(input, distance, rows, cols); 
        return markDistances(found, distance, rows, cols);
    }
    
    // Translate distance back into readable characters
    public static void printOneDistanceCell(int val)
    {
        switch(val) {
            case -1: Sx.format("X%cX", X); break;
            case  0: Sx.format(" %c ", o); break;     // cell was never reached
            case  1: Sx.format("*%c*", G); break;
            default: Sx.format("%2d ", val); break;
        }
    }

    // Takes in raw layout and builds queue of goal cells, also starting to fill in the output 
    // which is assumed to come in pre-filled with all zeros.
    static Queue<Point> markGoals(final char layout[][], int distance[][], int rows, int cols)
    {
        Queue<Point> marked = new LinkedList<Point>();
        for (int row = 0; row < rows; row++) {
            assert (layout[row] != null && layout[row].length == cols);
            for (int col = 0; col < cols; col++) {
                char val = layout[row][col];
                if (val == X) {
                    distance[row][col] = -1;
                } else if (layout[row][col] == G) {
                    distance[row][col] = 1;
                    marked.add(new Point(row, col));    // x = row, y = col
                }
            }
        }
        return marked;
    }

    // Takes in the queue of goal cells, the initialized distance array,
    // and fills in the distance to any nearest goal.
    static int markDistances(Queue<Point> marked, int distance[][], int rows, int cols)
    {
        while (!marked.isEmpty()) {
            Point old = marked.remove();
            int row = old.x;
            int col = old.y;
            int dst = distance[row][col] + 1;
            
            // West (Left)
            col--;
            if (col >= 0 && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Point(row, col));
            }
            
            // East (Right)
            col += 2;
            if (col < cols && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Point(row, col));
            }
            
            // North (Up)
            row--;
            col--;
            if (row >= 0 && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Point(row, col));
            }
            
            // South (Down)
            row += 2;
            if (row < rows && distance[row][col] == 0) {
                distance[row][col] = dst;
                marked.add(new Point(row, col));
            }
        }
        return 0;
    }
        
    static char[][] testFloorA = {                   // 8x8 test matrix
                               { o, o, o, X, o, o, o, o },
                               { o, o, o, X, o, X, X, o },
                               { o, X, o, X, o, o, X, o },
                               { G, X, o, X, G, o, X, o },
                               { o, X, o, X, X, X, X, o },
                               { o, X, o, o, o, X, o, o },
                               { o, X, G, o, o, X, o, G },
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
        Sx.format("BEGIN %s\n", testName);
        
        Sx.putsArray(testFloor);
        GridNav mg = new GridNav(testFloor);
        int gc[][] = mg.distance;
        Sx.putsArray("GridGoals constructor made\n", gc, GridNav::printOneDistanceCell);

        int rows = testFloor.length;
        int cols = testFloor[0].length;
        int gd[][] = new int[rows][cols];  // All 0s 
        int status = GridNav.goalDistance(testFloor, gd, rows, cols);        
        Sx.putsArray("static GridGoals.goalDistance output:\n", gd, GridNav::printOneDistanceCell);
        
        double err = ArrayDiffs.sumOfSquaredDifferences(gc, gd);
        Sx.format("sum of squared differences: %f\n", err);
        status += (err == 0.0 ? 0 : 1);
        Sx.format("END %s: %s\n", testName, (status == 0 ? "PASS" : "FAIL"));
        return status;
    }
    
    public static int unit_test()
    {
        String testName = GridNav.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        
        int status = 0;
        status += test_oneLayout(testFloorA);
        status += test_oneLayout(testFloorB);
        
        Sx.format("END %s: %s\n", testName, (status == 0 ? "PASS" : "FAIL"));
        return status;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
