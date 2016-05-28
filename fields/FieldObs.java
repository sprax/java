package sprax.fields;

//////////// FIELD<T> //////////////////////////////////////////////////
/**
 * Field<T> Field of non-packed, not necessarily connected points.
 */
public abstract class FieldObs<T>
{
    public static abstract class Point<T>
    {
        T                    mData;
        public int           mNumNeighbors = 0;
        protected Point<T>[] mNeighbors;
        double               mCoords[];
        
        Point(double coords[], T val) // generic constructor
        {
            mCoords = coords;
            mData = val;
        };
        
        public String toString()
        {
            String str = new String("[" + mCoords[0]);
            for (int j = 1; j < mCoords.length; j++)
                str += (" " + mCoords[j]);
            str += (": " + mData + "]");
            return str;
        }
        
        public T getData()
        {
            return mData;
        }
        
        public void setData(T t)
        {
            mData = t;
        }
        
        public Point<T>[] getNeighbors()
        {
            return mNeighbors;
        }
        
        public void setNeighbors(Point<T>[] neighbors)
        {
            mNeighbors = neighbors;
        }
        
        public Point<T> getNeighbor(int idx)
        {
            return mNeighbors[idx];
        }
        
        public void setNeighbor(int idx, Point<T> fieldPoint)
        {
            mNeighbors[idx] = fieldPoint;
        }
    }
    
    int mNumPoints; // PointT[][] mPoints; // Declare as much as possible in terms of the generic
                    // Point, not Point<?>
                    
    FieldObs(int numPoints) // template constructor
    {
        mNumPoints = numPoints;
        createPoints();
        setNeighbors();
    }
    
    abstract Point<T> createPoint(double[] coords, T val); // return type must be the generic node
    
    abstract void createPoints(); // calls createPoint(row, col)
    
    abstract void setNeighbors(); // sets N <= sMaxNumNeighbors neighbors on each node
    
    public static void main(String[] args)
    {
        FieldInt.unit_test();
    }
}
