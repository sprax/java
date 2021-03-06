package sprax.grids;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Queue;

import sprax.arrays.ArrayDiffs;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class GridGuards
{
    static final char o = '.', G = 'G', X = 'X';  // Floor, Guard, Wall or Obstacle
            
    final int         wide, tall;
    final char        layout[][];
    final int         distance[][];
    
    public GridGuards(final char layout[][])
    {
        // TODO: Check for null and length < 1; throw exceptions? For now:
        assert (layout != null && layout.length > 0 && layout[0].length > 0);
        
        this.layout = layout;
        this.wide = layout[0].length;
        this.tall = layout.length;
        this.distance = new int[tall][wide];  // All 0s
        
        Queue<Point> marked = new LinkedList<Point>();
        
        markGuards(marked);
        markDistances(marked);
    }
    
    private void markGuards(Queue<Point> marked)
    {
        for (int row = 0; row < tall; row++) {
            assert (layout[row] != null && layout[row].length == wide);
            for (int col = 0; col < wide; col++) {
                char val = layout[row][col];
                if (val == X) {
                    distance[row][col] = -1;
                } else if (layout[row][col] == G) {
                    marked.add(new Point(row, col));    // x = row, y = col
                    distance[row][col] = 1;
                }
            }
        }
    }
    
    
    // Translate distance back into readable characters
    public static void printOneDistanceCell(int val)
    {
        switch(val) {
        case -1: Sx.printOne(X); break;
        case  0: Sx.printOne(o); break;     // cell was never reached
        case  1: Sx.printOne(G); break;
        default: Sx.printOne(val); break;
        }
    }

    private void markDistances(Queue<Point> marked)
    {
        while (!marked.isEmpty()) {
            Point old = marked.remove();
            int row = old.x;
            int col = old.y;
            int dst = distance[row][col] + 1;
            
            // West (Left)
            col--;
            if (col >= 0 && distance[row][col] == 0) {
                addToMarked(row, col, dst, marked);
            }
            
            // East (Right)
            col += 2;
            if (col < wide && distance[row][col] == 0) {
                addToMarked(row, col, dst, marked);
            }
            
            // North (Up)
            row--;
            col--;
            if (row >= 0 && distance[row][col] == 0) {
                addToMarked(row, col, dst, marked);
            }
            
            // South (Down)
            row += 2;
            if (row < tall && distance[row][col] == 0) {
                addToMarked(row, col, dst, marked);
            }
        }
        assert (marked.isEmpty());
    }
    
    void addToMarked(int row, int col, int dst, Queue<Point> marked) {
        distance[row][col] = dst;
        marked.add(new Point(row, col));
    }
    
    public static int[][] guardDistance(final char input[][])
    {
        assert (input != null && input.length > 0 && input[0] != null);
        int tall = input.length;
        int wide = input[0].length;
        Queue<Point> found = new LinkedList<>();
        int output[][] = new int[tall][wide];  // All 0s
        
        for (int row = 0; row < tall; row++) {
            for (int col = 0; col < wide; col++) {
                char val = input[row][col];
                if (val == X) {
                    output[row][col] = -1;
                } else if (input[row][col] == G) {
                    found.add(new Point(row, col));
                    output[row][col] = 1;
                }
            }
        }
        
        while (!found.isEmpty()) {
            Point old = found.remove();
            int row = old.x;
            int col = old.y;
            int dst = output[row][col] + 1;
            
            // West (Left)
            col--;
            if (col >= 0 && output[row][col] == 0) {
                output[row][col] = dst;
                found.add(new Point(row, col));
            }
            
            // East (Right)
            col += 2;
            if (col < wide && output[row][col] == 0) {
                output[row][col] = dst;
                found.add(new Point(row, col));
            }
            
            // North (Up)
            row--;
            col--;
            if (row >= 0 && output[row][col] == 0) {
                output[row][col] = dst;
                found.add(new Point(row, col));
            }
            
            // South (Down)
            row += 2;
            if (row < tall && output[row][col] == 0) {
                output[row][col] = dst;
                found.add(new Point(row, col));
            }
        }
        return output;
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
        String testName = GridGuards.class.getName() + ".test_oneLayout";
        Sz.begin(testName);
        
        Sx.putsArray(testFloor);
        GridGuards mg = new GridGuards(testFloor);
        int gc[][] = mg.distance;
        Sx.putsArray("GridGuards constructor made distance matrix:\n", gc);
        int gd[][] = GridGuards.guardDistance(testFloor);
        Sx.putsArray("static GridGuards.guardDistance output:\n", gd, GridGuards::printOneDistanceCell);
        double err = ArrayDiffs.sumOfSquaredDifferences(gc, gd);
        Sx.format("sum of squared differences: %f\n", err);
        int numWrong = (err == 0.0 ? 0 : 1);
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int unit_test()
    {
        String testName = GridGuards.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_oneLayout(testFloorA);
        numWrong += test_oneLayout(testFloorB);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
