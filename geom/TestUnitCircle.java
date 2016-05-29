package sprax.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;

import sprax.Sx;
import sprax.search.BinarySearch;

/**
 * Given an array, list, or set of non-zero points on the unit circle,
 * where the original order does not matter, 
 * create a function to find the index of the closest of their 
 * projections on the unit circle to any other non-zero point in the plane.
 * 
 * TODO: Points whose projects are exactly halfway between two unit circle
 * set point may go either way, depending on the implementation (e.g. angle
 * vs. dot-product measures).  Can we "correct" for that in the presence of
 * round-off errors?
 * @author Sprax
 */
public class TestUnitCircle
{		
	static Pxy sTestCirclePoints[] = new Pxy[]
			{ new Pxy(  1.0,  0.0 )
			, new Pxy( -1.0,  0.0 ) 
			, new Pxy(  0.0,  1.0 ) 
			, new Pxy(  0.0, -1.0 ) 
			, new Pxy(  1.0,  1.0 ) 
			, new Pxy( -2.0,  1.0 ) 
			, new Pxy( -1.0, -1.0 ) 
			};


	static int test_pointOfNearestProjection(UnitCircle uc, double x, double y)
	{
		Pxy point = new Pxy(x, y);
		Pxy close = uc.pointOfNearestProjection(point);
		Sx.format("test_pointOfNearestProjection(%f, %f) = <%f, %f>\n", x, y, close.x, close.y);
		int index = uc.indexOfNearestCirclePoint(point);
		Pxy found = uc.getPointAt(index);
		if (close != found)
			Sx.puts("close != found: " + close + " != " + found);
		return index;
	}

	public static ArrayList<Integer> testQuadrants(UnitCircle uc)
	{
		Sx.puts("testQuadrants(" + uc.getClass().getSimpleName() + "):");
		ArrayList<Integer> ans = new ArrayList<Integer>();
		Sx.puts("First quadrant:");
		ans.add(test_pointOfNearestProjection(uc, 1.0, 0.0));
		ans.add(test_pointOfNearestProjection(uc, 1.0, 0.5));
		ans.add(test_pointOfNearestProjection(uc, 1.0, 1.0));
		ans.add(test_pointOfNearestProjection(uc, 1.0, 3.0));
		ans.add(test_pointOfNearestProjection(uc, 1.0, 4.0));
		ans.add(test_pointOfNearestProjection(uc, 0.1, 5.0));

		Sx.puts("Second quadrant:");
		ans.add(test_pointOfNearestProjection(uc,  0.0, 1.0));
		ans.add(test_pointOfNearestProjection(uc, -0.1, 1.0));
		ans.add(test_pointOfNearestProjection(uc, -0.2, 0.8));
		ans.add(test_pointOfNearestProjection(uc, -0.3, 0.3));
		ans.add(test_pointOfNearestProjection(uc, -0.8, 0.2));
		ans.add(test_pointOfNearestProjection(uc, -1.0, 0.1));

		Sx.puts("Third quadrant:");
		ans.add(test_pointOfNearestProjection(uc, -1.0, -0.0));
		ans.add(test_pointOfNearestProjection(uc, -1.0, -0.1));
		ans.add(test_pointOfNearestProjection(uc, -0.8, -0.2));
		ans.add(test_pointOfNearestProjection(uc, -0.3, -0.3));
		ans.add(test_pointOfNearestProjection(uc, -0.2, -0.8));
		ans.add(test_pointOfNearestProjection(uc, -0.1, -1.0));

		Sx.puts("Fourth quadrant:");
		ans.add(test_pointOfNearestProjection(uc, 0.0, -1.0));
		ans.add(test_pointOfNearestProjection(uc, 0.1, -0.9));
		ans.add(test_pointOfNearestProjection(uc, 0.2, -0.8));
		ans.add(test_pointOfNearestProjection(uc, 0.3, -0.3));
		ans.add(test_pointOfNearestProjection(uc, 0.8, -0.2));
		ans.add(test_pointOfNearestProjection(uc, 0.9, -0.1));

		return ans;
	}

	public static int unit_test(int level)
	{
		String testName = UnitCircleAngle.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN\n");

		UnitCircle uca = new UnitCircleAngle(sTestCirclePoints);
		ArrayList<Integer> ucai = testQuadrants(uca);
		Sx.putsArray(ucai);
		Sx.puts();
		
		UnitCircle ucd = new UnitCircleDotProduct(sTestCirclePoints);
		ArrayList<Integer> ucdi = testQuadrants(ucd);
		Sx.putsArray(ucdi);
		Sx.puts();
		
		boolean eqs = ucai.equals(ucdi);
		Sx.puts(testName + " END,  status: " + (eqs ? "PASSED" : "FAILED"));
		return 0;
	}

	public static void main(String args[]) { unit_test(1); }
}

