package sprax.geom;

import java.util.ArrayList;

public abstract class UnitCircle
{
    protected int                  mSize;
    final protected ArrayList<Pxy> mPoints;
    
    UnitCircle(Pxy points[])
    {
        if (points == null || points.length < 1)
            throw new IllegalArgumentException("no points");
        
        mSize = points.length;
        // mPoints = Arrays.copyOf(points, points.length);		// defensive copy?  No.  Normalize.
        mPoints = new ArrayList<Pxy>(points.length);
        for (int j = 0; j < mSize; j++)
        {
            Pxy point = points[j];
            double mag = point.magnitude();
            mPoints.add(new Pxy(point.x / mag, point.y / mag));
        }
    }
    
    // Given a non-zero point, return the nearest point on (a subset of) the unit circle
    public abstract Pxy pointOfNearestProjection(Pxy point);
    
    public abstract int indexOfNearestCirclePoint(Pxy point);
    
    public Pxy getPointAt(int index)
    {
        assert (0 <= index && index < mSize);
        return mPoints.get(index);
    }
}
