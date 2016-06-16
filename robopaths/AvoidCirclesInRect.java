package sprax.robopaths;

import java.awt.geom.Rectangle2D;

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
    
    Point2d rect0, rect1, sensors[];
    Rectangle2D.Double rect;
    /** Ctor */
    public AvoidCirclesInRect(Point2d r0, Point2d r1, Point2d sensorPoints[]) {
        
        rect = new Rectangle2D.Double(r0.x, r0.y, r1.x - r0.x, r1.y - r0.y);
        
        
    }
    


    public static int unit_test()
    {
        String testName = AvoidCirclesInRect.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        int numWrong = 0;
        
        
        //Rectangle2d rect = new Rectangle2d(0.0,  2.0);

        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }

    //// OTHER CLASSES ////

    //// TEST DATA ////

    
}
