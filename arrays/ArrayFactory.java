package sprax.arrays;

import java.util.Arrays;
import java.util.Random;

import sprax.Sx;

public class ArrayFactory<T>
{
    /*************************************************************************
     * Object array factory <br>
     * Beware ClassCastException: 
     */
    public T[][] makeObjectArray(int numRows, int numCols) 
    {
      if (numRows < 1 || numCols < 1)
        throw new IllegalArgumentException("makeObjectArray("+numRows+", "+numCols+")");
      Object AA[][] = new Object[numRows][];
      for (int j = 0; j < numRows; j++) {
        AA[j] = new Object[numCols];
      }
      return (T[][])AA;
    } 
    
    public T[] makeObjectArray(int numCols) 
    {
      if (numCols < 1)
        throw new IllegalArgumentException("makeObjectArray("+numCols+")");
      Object AA[] = new Object[numCols];
      return (T[])AA;
    }
    
    public static Integer[] makeIntegerArray(int A[])
    {
        int len = A.length;
        Integer iA[] = new Integer[len];
        for (int j = 0; j < len; j++)
            iA[j] = A[j];
        return iA;
    }        

    
    /*************************************************************************
     * char array factory <br>
     */
    public static char [][] makeCharArray(int numRows, int numCols) 
    {
      if (numRows < 1 || numCols < 1)
        throw new IllegalArgumentException("makeCharArray("+numRows+", "+numCols+")");
      char AA[][] = new char[numRows][];
      for (int j = 0; j < numRows; j++) {
        AA[j] = new char[numCols];
      }
      return AA;
    }
    public static char [] makeCharArray(int numCols) 
    {
      if (numCols < 1)
        throw new IllegalArgumentException("makeCharArray("+numCols+")");
      char AA[] = new char[numCols];
      return AA;
    }

    /*************************************************************************
     * int array factory <br>
     */
    public static int [][] makeIntArray(int numRows, int numCols) 
    {
      if (numRows < 1 || numCols < 1)
        throw new IllegalArgumentException("makeIntArray("+numRows+", "+numCols+")");
      int AA[][] = new int[numRows][];
      for (int j = 0; j < numRows; j++) {
        AA[j] = new int[numCols];
      }
      return AA;
    }
    
    public static int [] makeIntArray(int numCols) 
    {
      if (numCols < 1)
        throw new IllegalArgumentException("makeIntArray("+numCols+")");
      int AA[] = new int[numCols];
      return AA;
    }
    
    public static int[] makeRandomIntArray(int size, int minVal, int maxVal, long seed)
    {
        if (size < 1 || minVal > maxVal)
            throw new IllegalArgumentException("input");
 
        int A[] = new int[size];
        Arrays.fill(A, minVal);
        int maxInc = maxVal - minVal + 1;
        Random rng = new Random(seed);
        for (int j = 0; j < size; j++) {
            A[j] += rng.nextInt(maxInc);
        }
        return A;
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
    
    /*************************************************************************
     * float array factory <br>
     */
    public static float [][] makeFloatArray(int numRows, int numCols) 
    {
      if (numRows < 1 || numCols < 1)
        throw new IllegalArgumentException("makeFloatArray("+numRows+", "+numCols+")");
      
//      float AA[][] = new float[numRows][];
//      for (int j = 0; j < numRows; j++) {
//        AA[j] = new float[numCols];
//      }
      float  AA[][] = new float[numRows][numCols];      
      return AA;
    }
    
    public static float [] makeFloatArray(int numCols) 
    {
      if (numCols < 1)
        throw new IllegalArgumentException("makeFloatArray("+numCols+")");
      float AA[] = new float[numCols];
      return AA;
    }
      
    public static int unit_test(int lvl) 
    {
        String  testName = ArrayFactory.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");    
        
        int rows = 6, cols = 5;
        int AA[][] = new int[rows][cols];
        AA[0][1] = 1;
        AA[1][2] = 2;
        AA[2][3] = 3;
        AA[3][4] = 4;
        Sx.puts("AA:");
        Sx.putsArray(AA);
        int BB[][] = AA.clone();
        Sx.puts("BB:");
        Sx.putsArray( BB);
        int CC[][] = new int[rows][cols];
        System.arraycopy(BB, 0, CC, 0, rows);
        Sx.puts("CC:");
        Sx.putsArray(CC);
        
        int array[][] = new int[3][3];
        int newarray[][] = array.clone();
        newarray[1][1]=400;
        System.out.println(array[1][1]);
        
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
