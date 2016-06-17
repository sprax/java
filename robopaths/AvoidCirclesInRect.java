package sprax.robopaths;

import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Comparator;

import javax.vecmath.Point2d;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Problem:
 * Given a rectangle with lower-left coordinates (x0, y0) and upper-right 
 * coordinates (x1, y1) and N sensors at coordinates {(m,n)} inside the 
 * rectangle, find a path from the left side to the ride side that avoids
 * all sensors.   Each sensor can sense in a circular region of radius R 
 * about its center (m,n).  Avoiding the regions around all sensors, the
 * path must reach from left side of rectangle to its right side (i.e. it
 * can start from any point with x coordinate x0 and y coordinate 
 * satisfying y0 < y < y1.  The path may end at any point (x, y) with x = x1
 * and y0 < y < y1.
 *  
 * Write an algorithm to find path (possibly shortest but not necessary) 
 * from start to end as described above. 
 * Note: all coordinates are represented as floating point numbers 
 * (choose type float or double).<br>
 * <code>
 *    Example:                                    (x1, y1)<br>
 *    |----------------------------------------------|    <br> 
 *    |....................O.......O.................|end <br>
 *    |......O...............O.......................|    <br>
 *    |........................O........O............|    <br>
 *    |start.........O...............................|    <br>
 *    |...................O................O.........|    <br>
 *    |..........O.................O.................|    <br>
 *    |----------------------------------------------|    <br> 
 * (x0, y0)
 * </code>
 * @author Sprax Lines
 */
public class AvoidCirclesInRect
{
    final Point2d corner0, corner1, sensors[];
    final double width, height, radius;
    final Rectangle2D.Double rect;
    
    double cellSize;
    int rows, cols, grid[][];
    
    /** Strategies:
     *  1) Discretize: Get an approximate solution by dividing the rectangle
     *  into a grid with cell size <= R, the radius of the sensors.  Then use
     *  ordinary wavefront algorithm, where all cells on the far right have 
     *  distance 0 from the goal, and sensor cells are treated as obstacles.
     *  NOTE 1: Using a cell size or approximately R or greater is very optimistic.
     *  If there is any doubt whether a path exists (because the sensors may
     *  be close enough together to form a blockade), a smaller cell size will
     *  help.  
     *  NOTE 2: Since the sensors are not actually constrained to be in cell 
     *  centers, this solution remains only approximate even if the cell size
     *  is made small compared to R.
     *  Thus:
     *  1a) Same as (1) but use cell size R/3, so that each sensor becomes
     *  an obstacle spanning 5 cells (Center, East, South, West, North).
     *  1b...) Same as (1) but use cell size R/5, etc...
     *  
     *  2) Potential function:
     *  2a) V(x,y) = 1 if distance(S, x, y) <= R or 0, where S is the set of 
     *  sensor coordinates.  The distance can be made easier to compute by
     *  sorting S first (by x then y), and noting that 
     *  (x**2 + y**2)**0.5 <= |x| + |y|
     *  2b) To avoid numerical edge cases, it may be better use an interpolated
     *  potential function: V(S) = Float.MAX_VALUE, and V(x,y) = R/distance(S,x,y)
     *  for (x,y) in R - S, so that V ~= 1.0 at sensor area boundaries. 
     *  
     *  Then, discretize the space as before, but this time we have not 
     *  implicitly assumed that sensors reside in cell centers, so any 
     *  blockades will be represented more accurately.  
     *  
     *  3) Create a Voronoi diagram based on the sensor points.
     *  Add segments along the top and bottom edges of the rectangle.
     *  Remove any edges that are (partly) within the radius R from any sensor
     *  (i.e. from the nearest sensor).  
     *  Search for a path along the remaining edges connecting left and
     *  right sides of the rectangle.  
     *  To get a minimal path, add up the edge lengths of each path,
     *  and select the one with a minimal sum.
     *  
     *  4) Bug algorithms: follow implicit walls around sensors and groups
     *  of sensors (blockades).
     *  
     *  5) Elastic path repelled from sensors, computed by relaxation.
     *  
     *  6) Some kind of dual graph constructed from the graph of sensors.
     */
    

    /** Ctor */
    public AvoidCirclesInRect(Point2d r0, Point2d r1, Point2d sensorPoints[], double sensorRadius) 
    {
        corner0 = r0;
        corner1 = r1;
        width = r1.x - r0.x;
        height = r1.y - r0.y;
        radius = sensorRadius;
        if (radius <= 0.0 || width < radius || height < radius)
            throw new IllegalArgumentException("bad dimensions");
        rect = new Rectangle2D.Double(r0.x, r0.y, width, height);
        sensors = Arrays.copyOf(sensorPoints, sensorPoints.length);     // defensive copy
        Comparator<Point2d> comp = new ComparePointXY();
        Arrays.sort(sensors, comp);
        
        createGrid();
        markSensorsInGrid();
        markDistancesInGrid();
    }
    
    /** 
     * Use default grid cell size of C <= radius/3.0, which gives a minimal margin of safety.
     * If every sensor were located in the very center of a grid cell, then C = 2*R/5 would
     * be (barely) enough to contain a sensor inside a region 5 cells wide and tall.  But if
     * the sensor is off-center, it's radial reach may extend beyond those five cells.  For
     * example, if vertically centered but all the way right in the center cell, it's range
     * would extend half way into the next cell on the right.  So instead, use * C = R/3.
     * but approximate the sensor's circular domain by a region 7 cells across.
     * <p>
     * But to avoid differently sized (smaller) cells at the boundaries -- in particular, at
     * the vertical boundaries, where blockades need to be reckoned with, let's actually use
     * C = H/N where H = height and N is the maximum whole number s.t. height/N >= R/3.
     * Thus N = ifloor(3*H/R).
     * <pre> 
     *       _______                     
     *      _|_|_|_|_                     
     *    _|_|_|_|_|_|_                   
     *   |_|_|_|_|_|_|_|                  
     *   |_|_|_|S|_|_|_|   Even if sensor S is located on right boundary of the central             
     *   |_|_|_|_|_|_|_|   cell, its range reaches only to the right boundary of the
     *     |_|_|_|_|_|     right-most V-centered cell.
     *       |_|_|_|                        
     *   <---R--|--R--->                          
     *  </pre>
     */
    void createGrid() 
    {
        double approxCellSize = 3.0 * height / radius;
        double vertNumCells   = Math.floor(approxCellSize);
        double actualCellSize = height / vertNumCells;
        double horzNumCells   = Math.ceil(width/actualCellSize);
        rows = (int)Math.round(vertNumCells);
        cols = (int)Math.round(horzNumCells);
        grid = new int[rows][cols];
        cellSize = actualCellSize;
    }

    void markSensorsInGrid() 
    {
        for (Point2d ss : sensors) {
            int col = (int) Math.floor((ss.x - rect.x)/cellSize);   // remember x ~ column
            int row = (int) Math.floor((ss.y - rect.y)/cellSize);   // remember y ~ row
            markSensor(row, col);
        }
        Sx.putsArray(grid);
    }

    void markDistancesInGrid()
    {
        Sx.putsArray(sensors);
        for (Point2d ss : sensors) {
            int col = (int) Math.floor((ss.x - rect.x)/cellSize);   // remember x ~ column
            int row = (int) Math.floor((ss.y - rect.y)/cellSize);   // remember y ~ row
            markSensor(row, col);
        }
        Sx.putsArray(grid);
    }
        
    void markSensor(int row, int col) {
        if (row > rows || col > cols)
            return;
        markSensorRow(row    , Math.max(0, col - 3), Math.min(col + 3, cols), -1);
        int rr = row;
        if (++rr < rows) {
            markSensorRow(rr, Math.max(0, col - 3), Math.min(col + 3, cols), -1);
            if (++rr < rows) {
                markSensorRow(rr, Math.max(0, col - 2), Math.min(col + 2, cols), -1);
                if (++rr < rows) {
                    markSensorRow(rr, Math.max(0, col - 1), Math.min(col + 1, cols), -1);
                }
            }
        }
        rr = row;
        if (--rr >= 0) {
            markSensorRow(rr, Math.max(0, col - 3), Math.min(col + 3, cols), -1);
            if (--rr >= 0) {
                markSensorRow(rr, Math.max(0, col - 2), Math.min(col + 2, cols), -1);
                if (--rr >= 0) {
                    markSensorRow(rr, Math.max(0, col - 1), Math.min(col + 1, cols), -1);
                }
            }
        }
    }

    void markSensorRow(int row, int begCol, int endCol, int mark)
    {
        for (int col = begCol; col <= endCol; col++) {
            grid[row][col] = mark;
        }
    }
    
    
    public static int unit_test()
    {
        String testName = AvoidCirclesInRect.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        int numWrong = 0;
        
        Point2d r0 = new Point2d( 0.0,  0.0);
        Point2d r1 = new Point2d(20.0, 10.0);
        double sensorRadius = Math.E;
        Point2d[] sensorPoints = {
                new Point2d(11.5, 7.5),
                new Point2d(5.5, 4.5),
        };
        
        AvoidCirclesInRect acir = new AvoidCirclesInRect(r0, r1, sensorPoints, sensorRadius);
        

        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }

    //// OTHER CLASSES ////
    
    /** compare x coordinates, then y coordinates if same x. */
    class ComparePointXY implements Comparator<Point2d> {
        @Override
        public int compare(Point2d pA, Point2d pB) {
            int xcomp = Double.compare(pA.x, pB.x);
            if (xcomp != 0)
                return xcomp;
            return Double.compare(pA.y, pB.y);
        }
        
    }

    //// TEST DATA ////

    
}
