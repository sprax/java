package sprax.fields;

import java.util.Random;

import sprax.sprout.Sx;


//////////// FIELD<T> //////////////////////////////////////////////////
/**
 * Field<T>  Field of non-packed, not necessarily connected points.
 */
public class FieldInt extends Field<Integer>
{
	static class PointInt extends Point<Integer> {
	    PointInt(double coords[], Integer val)   { super(coords, val);  }
	}

	PointInt	mPoints[]; // Declare as much as possible in terms of the generic Point, not Point<?>

	FieldInt(int numPoints)  // template constructor
	{
		super(numPoints);
	}

	@Override
	void createPoints() {
		mPoints = new PointInt[mNumPoints];
		Random rng = new Random();
		rng.nextDouble();
		for (int j = 0; j < mNumPoints; j++) {
		    double coords[] = { rng.nextDouble(), rng.nextDouble() };
			mPoints[j] = new PointInt( coords, j );
		}
	}

	@Override
	void setNeighbors() {
		// TODO Auto-generated method stub
	}

	@Override
	Field.Point<Integer> createPoint(double coords[], Integer val) {
		return new PointInt(coords, val);
	}
	
	public static int unit_test()
	{
	    Sx.puts(FieldInt.class.getName() + ".unit_test BEGIN");
	    
	    FieldInt fin = new FieldInt(5);
	    for (int j = 0; j < fin.mNumPoints; j++) {
	        PointInt pin = fin.mPoints[j];
	        Sx.puts(pin);
	    }
        Sx.puts(FieldInt.class.getName() + ".unit_test END");
	    return 0;
	}

	public static void main(String args[]) {
		unit_test();
	}
}

