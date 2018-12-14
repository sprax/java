package sprax.arrays;

import java.util.Arrays;
import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class RandomArrayT
{
    
    public static Integer[] makeRandomIntegerArray(int size, int minVal, int maxVal, Random rng)
    {
        if (size < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        Integer A[] = new Integer[size];
        Arrays.fill(A, minVal);
        int maxInc = maxVal - minVal + 1;
        for (int j = 0; j < size; j++) {
            A[j] += rng.nextInt(maxInc);
        }
        return A;
    }
    
    /**** FIXME: generic factory pattern, infer type from an argument */
    public static <T extends Number> T[] makeRandomArray(int size, int minVal, int maxVal, long seed)
    {
        if (size < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        T A[] = new T[size];
        Arrays.fill(A, minVal);
        int maxInc = maxVal - minVal + 1;
        Random rng = new Random(seed);
        for (int j = 0; j < size; j++) {
            A[j] += (T) rng.nextInt(maxInc);
        }
        return A;
    }
    /****/
    
      
    public static int unit_test(int lvl) 
    {
        String  testName = RandomArrayT.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        long seed = System.currentTimeMillis();
        Random rng = new Random(seed);
        Integer[] array = makeRandomIntegerArray(200, -99, 99, rng);
        double mean = Arrays1d.mean(array);
        Sx.format("mean %f\n", mean);
        //double stdd = ArrayAlgo.stddev(array);
        //double svar = ArrayAlgo.variance(array);
        //double dvar = stdd * stdd;
        
        //Sx.printArrayFolded(array, 40);
        Sx.puts();
        //Sx.format("mean %f,  stddev %f,  variance %f\n", mean, stdd, svar);
        //Sx.format("variance %f - stddev*2 %f = %f\n", svar, dvar, svar - dvar);
        
        Sz.end(testName, numWrong);   
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}

