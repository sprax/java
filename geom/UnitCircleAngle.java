package sprax.geom;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import sprax.Sx;
import sprax.search.BinarySearch;

/**
 * Given an array, list, or set of non-zero points on the unit circle,
 * where the original order does not matter, 
 * create a function to find the index of the closest of their 
 * projections on the unit circle to any other non-zero point in the plane,
 * @author Sprax
 */
public class UnitCircleAngle extends UnitCircle
{
	TreeMap<Double, Pxy> mAngleToPoint;
	double[] mAngles;
	
	UnitCircleAngle(Pxy points[])
	{
		super(points);

		mAngleToPoint = new TreeMap<Double, Pxy>();
		for (int j = 0; j < mSize; j++)
		{
			Pxy point = mPoints.get(j);
			mAngleToPoint.put(Math.atan2(point.y, point.x), point);
		}
		mSize = mAngleToPoint.size();
		
		// The primitive types way:
		mPoints.clear();
		mAngles = new double[mSize];
		int j = 0;
		for (Map.Entry<Double, Pxy> elt : mAngleToPoint.entrySet())
		{
			double key = elt.getKey();
			Pxy    val = elt.getValue();
			Sx.format("UC Angle[%2d] is %+2.5f for Pxy %s\n", j, key, val);
			mAngles[j++] = key;
			mPoints.add(val);
		}
		
		// The Object way:
		// mAngles.addAll(mAngleToPoint.keySet());
		// mPoints.addAll(mAngleToPoint.values());
	}
	
	int indexOfNearestAngleArrays(Pxy point)
	{
		int index = -1;		// return value
		assert(point != null && (point.x != 0.0 || point.y != 0.0));
		double angle = Math.atan2(point.y, point.x);
		int insert = Arrays.binarySearch(mAngles, angle);
		// int infidx = BinarySearch<Double>.binarySearchLowerBoundDbl(A, angle);
		if (insert >= 0)
		{
			// found exact match: angle == mAngles[insert], so we finish early:
			index = insert;
		}
		else
		{
			// exact angle not found, but insert = -insertion_point - 1,
			// where insertion_point is where the search item would be
			// inserted, i.e. the index of the first element that has a
			// sort value greater than the search value.  So -insert - 2
			// gives a greatest lower bound.
			// But remember to treat the array as circular:
			int lo = -insert - 2;
			if (lo < 0)
				lo += mSize;
			int hi = (lo + 1) % mSize;
			double difLo = Math.abs(angle - mAngles[lo]);
			if (difLo > Math.PI)
				difLo = Math.PI * 2.0 - difLo;
			double difHi = Math.abs(angle - mAngles[hi]);
			if (difHi > Math.PI)
				difHi = Math.PI * 2.0 - difLo;
			if (difLo <= difHi)
				index = lo;
			else
				index = hi;
		}
		return index;
	}
	
	int indexOfNearestAngleSprax(Pxy point)
	{
		int index = -1;		// return value
		assert(point != null && (point.x != 0.0 || point.y != 0.0));
		double angle = Math.atan2(point.y, point.x);
		// int insert = Arrays.binarySearch(mAngles, angle);
		int infidx = BinarySearch.binarySearchLowerBoundDbl(mAngles, angle);
		if (infidx < 0)
			infidx += mSize;
		if (angle == mAngles[infidx])
		{
			// found exact match: angle == mAngles[insert], so we finish early:
			index = infidx;
		}
		else
		{
			// exact angle not found, but insert = -insertion_point - 1,
			// where insertion_point is where the search item would be
			// inserted, i.e. the index of the first element that has a
			// sort value greater than the search value.  So -insert - 2
			// gives a greatest lower bound:
			int lo = infidx;
			// Treat the array as circular:
			int hi = (lo + 1) % mSize;
			double difLo = Math.abs(angle - mAngles[lo]);
			if (difLo > Math.PI)
				difLo = Math.PI * 2.0 - difLo;
			double difHi = Math.abs(angle - mAngles[hi]);
			if (difHi > Math.PI)
				difHi = Math.PI * 2.0 - difLo;
			if (difLo <= difHi)
				index = lo;
			else
				index = hi;
		}
		return index;
	}
	

	@Override
	public int indexOfNearestCirclePoint(Pxy point) 
	{
		return indexOfNearestAngleSprax(point);
	}	
	
	@Override
	public Pxy pointOfNearestProjection(Pxy point)
	{
		int    angleIndex = indexOfNearestAngleArrays(point);
		double angleValue = mAngles[angleIndex];
		Pxy    pointValue = mAngleToPoint.get(angleValue);
		return pointValue;
	}
	
	static int test_pointOfNearestProjection(UnitCircleAngle uc, double x, double y)
	{
		Pxy point = new Pxy(x, y);
		Pxy close = uc.pointOfNearestProjection(point);
		Sx.format("test_pointOfNearestProjection(%f, %f) = <%f, %f>\n", x, y, close.x, close.y);
		int idxArray = uc.indexOfNearestAngleArrays(point);
		int idxSprax = uc.indexOfNearestAngleSprax(point);
		if (idxArray != idxSprax)
			Sx.format("\t  Arrays.binarySearch disagrees with sprax.search.binarySearchLowerBoundDbl: %d != %d\n",  idxArray, idxSprax);
		return idxArray - idxSprax;
	}
	
	public static int unit_test(int level)
	{
		String testName = UnitCircleAngle.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN\n");

		Pxy points[] = new Pxy[]
				{ new Pxy(  1.0,  0.0 )
				, new Pxy( -1.0,  0.0 ) 
				, new Pxy(  0.0,  1.0 ) 
				, new Pxy(  0.0, -1.0 ) 
				, new Pxy(  1.0,  1.0 ) 
				, new Pxy(  1.0, -1.0 ) 
				, new Pxy( -1.0,  1.0 ) 
				, new Pxy( -1.0, -1.0 ) 
				, new Pxy(  2.0, -1.0 ) 
				};
		UnitCircleAngle uc = new UnitCircleAngle(points);
		
		Sx.puts("Special case:");
		test_pointOfNearestProjection(uc, -0.300000, -0.300000);
		
		Sx.puts("First quadrant:");
		test_pointOfNearestProjection(uc, 1.0, 0.0);
		test_pointOfNearestProjection(uc, 1.0, 0.5);
		test_pointOfNearestProjection(uc, 1.0, 1.0);
		test_pointOfNearestProjection(uc, 1.0, 3.0);
		test_pointOfNearestProjection(uc, 1.0, 4.0);
		test_pointOfNearestProjection(uc, 0.1, 5.0);

		Sx.puts("Second quadrant:");
		test_pointOfNearestProjection(uc,  0.0, 1.0);
		test_pointOfNearestProjection(uc, -0.1, 1.0);
		test_pointOfNearestProjection(uc, -0.2, 0.8);
		test_pointOfNearestProjection(uc, -0.3, 0.3);
		test_pointOfNearestProjection(uc, -0.8, 0.2);
		test_pointOfNearestProjection(uc, -1.0, 0.1);

		Sx.puts("Third quadrant:");
		test_pointOfNearestProjection(uc, -1.0, -0.0);
		test_pointOfNearestProjection(uc, -1.0, -0.1);
		test_pointOfNearestProjection(uc, -0.8, -0.2);
		test_pointOfNearestProjection(uc, -0.3, -0.3);
		test_pointOfNearestProjection(uc, -0.2, -0.8);
		test_pointOfNearestProjection(uc, -0.1, -1.0);
		
		Sx.puts("Fourth quadrant:");
		test_pointOfNearestProjection(uc, 0.0, -1.0);
		test_pointOfNearestProjection(uc, 0.1, -0.9);
		test_pointOfNearestProjection(uc, 0.2, -0.8);
		test_pointOfNearestProjection(uc, 0.3, -0.3);
		test_pointOfNearestProjection(uc, 0.8, -0.2);
		test_pointOfNearestProjection(uc, 0.9, -0.1);

		Sx.puts(testName + " END,  status: PASSED");
		return 0;
	}

	public static void main(String args[]) { unit_test(1); }
}
