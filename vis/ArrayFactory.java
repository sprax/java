package sprax.vis;

import java.util.Random;

import sprax.sprout.Sx;

public class ArrayFactory 
{
    Random mRng = null;

    ArrayFactory() {
        mRng = new Random();
    }

    /** 
     * creates random vector in positive unit hyper-cube
     * @param numDims
     * @return
     */
    public float[] randomVectorInUnitCube(int numDims)
    {
        float[] vec = new float[numDims];
        for (int j = 0; j < numDims; j++) {
            vec[j] = mRng.nextFloat();
        }
        return  vec;
    }

    /** 
     * creates random vector in hyper-cube with
     * coordinates in interval (-1, 1)
     * @param numDims
     * @return
     */
    public float[] randomVectorInOriginCube(int numDims)
    {
        float[] vec = new float[numDims];
        for (int j = 0; j < numDims; j++) {
            vec[j] = mRng.nextFloat() * 2f - 1f;
        }
        return  vec;
    }

    public float[] randomVectorInUnitSpherePart(int numDims)
    {
        float[] vec = new float[numDims];
        double crd, sum;
        do {
            sum = 0.0;
            for (int j = 0; j < numDims; j++) {
                crd = vec[j] = mRng.nextFloat();
                sum += crd*crd; 
            }
        } while (sum >= 1.0);
        return vec;
    }

    public float[] randomVectorInOriginSphere(int numDims, float scale)
    {
        float[] vec = new float[numDims];
        float  num;
        double sum;
        do {
            sum = 0.0;
            for (int j = 0; j < numDims; j++) {
                num = vec[j] = mRng.nextFloat();
                sum += num*num; 
            }
        } while (sum >= 1.0);
        for (int j = 0; j < numDims; j++) {
            vec[j] *= scale;
            if (mRng.nextBoolean()) {
                vec[j] = -vec[j];
            }
        }
        return vec;
    }

    public float[] randomVectorInCube(int numDims, float min, float max)
    {
        float[] vec = new float[numDims];
        for (int j = 0; j < numDims; j++) {
            vec[j] = min + max*mRng.nextFloat();
        }
        return  vec;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ArrayFactory af = new ArrayFactory();
        float[] vec =  af.randomVectorInOriginCube(5);
        Sx.putsArray(vec);

    }


}
