package sprax.vis;

import sprax.sprout.Sx;

public class Transform2d 
{

	public static double[] CartesianToPolar(double x, double y)
	{
		double  r = Math.sqrt(x*x + y*y);
		double  t = Math.atan2(y, x);	// Range -pi to +pi
		double rt[] = { r, t };
		return rt;
	}

	public static double[] PolarToCartesian(double r, double t)
	{
		double  x = r * Math.cos(t);
		double  y = r * Math.sin(t);
		double xy[] = { x, y };
		return xy;
	}
	
	

	public static float[] CartesianToPolar(float x, float y)
	{
		float   r = (float)Math.sqrt(x*x + y*y);
		float   t = (float)Math.atan2(y, x);	
		float  rt[] = { r, t };
		return rt;
	}
	
    public static int unit_test(int num)
    {
    	String testLabel = Transform2d.class.getName() + ".unit_test";
    	Sx.puts(testLabel + " BEGIN");

    	for (int j = 0; j <= num; j++) {
    		double r = j;
    		double t = j * ( 2 * Math.PI / num);
    		double xy[] = Transform2d.PolarToCartesian(r, t);
    		double rt[] = Transform2d.CartesianToPolar(xy[0], xy[1]);
    		double e0 = r - rt[0];
    		double e1 = t - rt[1];
    		e0 = Math.sqrt(e0*e0);
    		e1 = Math.sqrt(e1*e1);
    		Sx.format("XY: %g %g   RT: %g %g   err: %g %g\n"
    				, xy[0], xy[1], rt[0], rt[1], e0, e1);
    	}
    	
    	Sx.puts(testLabel + " END");
        return 0;
    }

	public static void main(String[] args) 
	{
		unit_test(6);
	}

}
