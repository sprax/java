package sprax.geom;

import java.util.Arrays;
import java.util.Comparator;
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
public class UnitCircleDotProduct extends UnitCircle
{		
		UnitCircleDotProduct(Pxy points[])
		{
			super(points);
			
			Sx.putsArray(mPoints);
			mPoints.sort(Pxy.sAngleComp);
			Sx.putsArray(mPoints);
		}
		
		int indexOfNearestPointInSetNaive(Pxy point)
		{
			int index = -1;		// return value
			assert(point != null && (point.x != 0.0 || point.y != 0.0));

			double maxDotProd = Double.MIN_VALUE;
			for (int j = 0; j < mSize; j++)
			{
				Pxy pxy = mPoints.get(j);
				double dotProd = pxy.dotProduct(point);
				if (maxDotProd < dotProd) 
				{
					maxDotProd = dotProd;
					index = j;
				}
			}
			return index;
		}


		@Override
		public int indexOfNearestCirclePoint(Pxy point) 
		{
			return indexOfNearestPointInSetNaive(point);
		}

		@Override
		public Pxy pointOfNearestProjection(Pxy point)
		{
			int index = indexOfNearestPointInSetNaive(point);
			if (index < 0)
				return null;
			else
				return mPoints.get(index);
		}
		
		static int test_pointOfNearestProjection(UnitCircle uc, double x, double y)
		{
			Pxy point = new Pxy(x, y);
			Pxy close = uc.pointOfNearestProjection(point);
			Sx.format("test_pointOfNearestProjection(%f, %f) = <%f, %f>\n", x, y, close.x, close.y);
			int idxBrute = uc.indexOfNearestCirclePoint(point);
			//int idxSprax = uc.indexOfNearestAngleSprax(point);
			//if (idxArray != idxSprax)
			//	Sx.format("\t  Arrays.binarySearch disagrees with sprax.search.binarySearchLowerBoundDbl: %d != %d\n",  idxArray, idxSprax);
			return idxBrute;
		}
		
		public static int unit_test(int level)
		{
			String testName = UnitCircleDotProduct.class.getName() + ".unit_test";
			Sx.puts(testName + " BEGIN\n");

			Pxy points[] = new Pxy[]
					{ new Pxy(  1.0,  0.0 )
					, new Pxy( -1.0,  0.0 ) 
					, new Pxy(  0.0,  1.0 ) 
					, new Pxy(  0.0, -1.0 ) 
					, new Pxy(  1.0,  1.0 ) 
					, new Pxy( -2.0,  1.0 ) 
					};
			UnitCircle uc = new UnitCircleDotProduct(points);
			
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

