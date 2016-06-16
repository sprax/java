package sprax.geom;

import java.awt.geom.Point2D;
import java.util.Comparator;

import sprax.sprout.Sx;

// Double-precision point in the plane based on Jim Harris's Point2D.Double
public class Point2d extends Point2D.Double
{
    /** Generated SVUID */
    private static final long serialVersionUID = 1080527089991183869L;

    public Point2d( Point2D pt){
        super(pt.getX(), pt.getY());
    }
    
    public Point2d() {
        super();
    }

    public Point2d(double x, double y) {
        super(x, y);
    }
    
    @Override
    public String toString() {
        return String.format("(%+2.4f|%+2.4f)", x, y);
    }
    
    public double dotProduct(Point2d other) {
        assert (other != null);
        return x * other.x + y * other.y;
    }
    
    public static double dotProduct(Point2d p, Point2d q) {
        return p.dotProduct(q);
    }
    
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    static class AngleComp implements Comparator<Point2d>
    {
        @Override
        public int compare(Point2d pA, Point2d pB) {
            if (pA.x == pB.x && pA.y == pB.y)
                return 0;
            
            if (pA.y < 0) {
                if (pB.y < 0)
                    return java.lang.Double.compare(pA.x, pB.x);	// Angle increases with x
                else
                    return -1;							// Angle(pA) < 0 and Angle(pB) > 0 
            } else	// (pA.y >= 0)
            {
                if (pB.y < 0)
                    return 1;							// Angle(pA) >= 0 and Angle(pB) < 0
                else
                    return java.lang.Double.compare(pB.x, pA.x);	// Angle decreases with x
            }
        }
    }
    
    static Comparator<Point2d> sAngleComp = new AngleComp();
    
    public static int unit_test() 
    {
        String className = Point2d.class.getName();
        String testName = className + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        
        Point2d pA = new Point2d(-1, 0);
        Point2d pB = new Point2d(0, -1);
        int cmp = Point2d.sAngleComp.compare(pA, pB);
        Sx.format("%s.sAngleComp.compare(%s, %s) = %d\n", className, pA, pB, cmp);
        boolean eqs = true;
        Sx.format("END %s,  status: %s\n", testName, (eqs ? "PASS" : "FAIL"));
        return 0;
    }
    
    public static void main(String args[]) {
        unit_test();
    }
}
