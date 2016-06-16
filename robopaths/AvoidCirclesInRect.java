package sprax.robopaths;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Given a rectangle with lower-left coordinates (x0, y0) and upper-right 
 * coordinates (x1, y1) and N sensors at coordinates {(m,n)} inside the 
 * rectangle, find a path from the left side to the ride side that avoids
 * all sensors.   Each sensor can sense in a circular region of radius r 
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
 * <br>                                           (x1, y1)
 * |----------------------------------------------|     <br> 
 * |..............................................|end  <br>
 * |..............................................|     <br>
 * |start.........................................|     <br>
 * |..............................................|     <br>
 * |----------------------------------------------|<br> 
 * (x0, y0)
 * </code>
 * @author Sprax Lines
 */
public class AvoidCirclesInRect
{
    
    public static int unit_test()
    {
        String testName = AvoidCirclesInRect.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        int numWrong = 0;
        
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }

    //// TEST DATA ////

    
}
