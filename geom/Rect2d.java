package sprax.geom;

import java.awt.geom.Rectangle2D;

import sprax.sprout.Sx;
import sprax.test.Sz;

/** Double-precision rectangle in the plane based on Jim Harris's Rectangle2D.Double */
public class Rect2d extends Rectangle2D.Double
{
    /** Generated SVUID */
    private static final long serialVersionUID = -1312604939547098136L;
    
    public Rect2d(double x, double y, double w, double h) {
        super(x, y, w, h);
    }
    
    protected Rect2d(Rectangle2D rect) {
        super(rect.getCenterX(), rect.getCenterY(), rect.getWidth(), rect.getHeight());
    }
    
    /** hide the default constructor, at least for now */
    private Rect2d() {
        super();
    }

    @Override
    public String toString() {
        return String.format("[p<%+2.4f|%+2.4f> w%f h%f]", x, y, width, height);
    }
    
    public static int unit_test() 
    {
        String className = Rect2d.class.getName();
        String testName = className + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Rect2d rectA = new Rect2d(-1, 0, 1, 2);
        Rect2d rectB = new Rect2d(0, -1, 5, -10);
        Rect2d rectC = new Rect2d(new Rectangle2D.Double());    // protected empty
        Rect2d rectD = new Rect2d();                            // private empty
        Sx.format("Rect2d A: %s\n",  rectA);
        Sx.format("Rect2d B: %s\n",  rectB);
        Sx.format("Rect2d C: %s\n",  rectC);
        Sx.format("Rect2d D: %s\n",  rectD);
         
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String args[]) {
        unit_test();
    }
}
