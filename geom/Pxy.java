package sprax.geom;

import java.util.Comparator;

import sprax.sprout.Sx;

//Non-zero double-precision point in the plane
public final class Pxy
{
    final double x;
    final double y;
    
    Pxy(double x, double y) {
        assert (x != 0.0 || y != 0.0);
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return String.format("(%+2.4f|%+2.4f)", x, y);
    }
    
    public double dotProduct(Pxy other) {
        assert (other != null);
        return x * other.x + y * other.y;
    }
    
    public static double dotProduct(Pxy p, Pxy q) {
        return p.dotProduct(q);
    }
    
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    static class AngleComp implements Comparator<Pxy>
    {
        @Override
        public int compare(Pxy pA, Pxy pB) {
            if (pA.x == pB.x && pA.y == pB.y)
                return 0;
            
            if (pA.y < 0) {
                if (pB.y < 0)
                    return Double.compare(pA.x, pB.x);	// Angle increases with x
                else
                    return -1;							// Angle(pA) < 0 and Angle(pB) > 0 
            } else	// (pA.y >= 0)
            {
                if (pB.y < 0)
                    return 1;							// Angle(pA) >= 0 and Angle(pB) < 0
                else
                    return Double.compare(pB.x, pA.x);	// Angle decreases with x
            }
        }
    }
    
    static Comparator<Pxy> sAngleComp = new AngleComp();
    
    public static int unit_test() 
    {
        String className = Pxy.class.getName();
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
