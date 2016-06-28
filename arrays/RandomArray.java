package sprax.arrays;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class RandomArray
{
     
    public static int[] makeRandomIntArray(int size, int minVal, int maxVal, Random rng)
    {
        if (size < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        int A[] = new int[size];
        Arrays.fill(A, minVal);
        int maxInc = maxVal - minVal + 1;
        for (int j = 0; j < size; j++) {
            A[j] += rng.nextInt(maxInc);
        }
        return A;
    }
    
    public static int[][] makeRandomIntArray2d(int rows, int cols, int minVal, int maxVal, Random rng)
    {
        if (rows < 1 || cols < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        int array[][] = new int[rows][cols];
        int maxInc = maxVal - minVal + 1;
        for (int j = 0; j < rows; j++) {
            for (int k = 0; k < cols; k++) {
                array[j][k] = minVal + rng.nextInt(maxInc);
            }
        }
        return array;
    }
    
    public static int[][] makeSortedRandomIntArray2d(int rows, int cols, int minVal, int maxVal, Random rng)
    {
        if (rows < 1 || cols < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        int array1[] = makeRandomIntArray(rows*cols, minVal, maxVal, rng);
        Arrays.sort(array1);
        int sorted[][] = new int[rows][cols];
        for (int j = 0; j < rows; j++) {
            for (int k = 0; k < cols; k++) {
                sorted[j][k] = array1[j*rows + k];
            }
        }
        return sorted;
    }
    
    public static int[][] makeBiSortedRandomIntArray2d(int rows, int cols, int minVal, int maxVal, Random rng)
    {
        if (rows < 1 || cols < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
         
        int array[][] = new int[rows][];
        for (int j = 0; j < rows; j++) {
            array[j] = makeRandomIntArray(cols, minVal, maxVal, rng);
            Arrays.sort(array[j]);
        }
        ZeroArrayComp comp = new ZeroArrayComp();
        Arrays.sort(array, comp);
        return array;
    }
    
    public static Integer[] makeRandomIntegerArray(int size, int minVal, int maxVal, long seed)
    {
        if (size < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        Integer A[] = new Integer[size];
        Arrays.fill(A, minVal);
        int maxInc = maxVal - minVal + 1;
        Random rng = new Random(seed);
        for (int j = 0; j < size; j++) {
            A[j] += rng.nextInt(maxInc);
        }
        return A;
    }
    
    /**** FIXME: generic factory pattern, infer type from an argument
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
    ****/
    
      
    public static int unit_test(int lvl) 
    {
        String  testName = RandomArray.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        long seed = System.currentTimeMillis();
        Random rng = new Random(seed);
        int[] array = makeRandomIntArray(200, -99, 99, rng);
        double mean = ArrayAlgo.mean(array);
        double stdd = ArrayAlgo.stddev(array);
        double svar = ArrayAlgo.variance(array);
        double dvar = stdd * stdd;
        
        Sx.printArrayFolded(array, 40);
        Sx.puts();
        Sx.format("mean %f,  stddev %f,  variance %f\n", mean, stdd, svar);
        Sx.format("variance %f - stddev*2 %f = %f\n", svar, dvar, svar - dvar);
        
        Sz.end(testName, numWrong);   
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}

class ZeroArrayComp implements Comparator<int[]>
{
    @Override
    public int compare(int[] A, int[] B) {
        int firstNonZeroA = indexOfFirstNonZero(A);
        int firstNonZeroB = indexOfFirstNonZero(B);
        return firstNonZeroB - firstNonZeroA;
    }
    
    int indexOfFirstNonZero(int[] array) {
        int index;
        for (index = 0; index < array.length; index++)
            if (array[index] != 0)
                break;
        return index;
    }
    
}
