package sprax.shuffles;

import java.util.Random;

import sprax.Sx;
import sprax.Sz;

public class Shuffler
{   
	/** shared static Random number generator */
	static Random sRandom = new Random();  // i.e., java.util.Random.

	/** shuffle in-place */
	public static void shuffle(int array[])
	{
		int n = array.length;       // The number of items left to shuffle (loop invariant).
		while (n > 1)
		{
			int k = sRandom.nextInt(n);   // 0 <= k < n.
			n--;                      // n is now the last pertinent index;
			int temp = array[n];      // swap array[n] with array[k] (does nothing if k == n).
			array[n] = array[k];
			array[k] = temp;
		}
	}

	/** shuffle in-place */
	public static void shuffle(Object array[])
	{
		int n = array.length;       // The number of items left to shuffle (loop invariant).
		while (n > 1)
		{
			int k = sRandom.nextInt(n);   // 0 <= k < n.
			n--;                      // n is now the last pertinent index;
			Object temp = array[n];   // swap array[n] with array[k] (does nothing if k == n).
			array[n] = array[k];
			array[k] = temp;
		}
	}

	/** shuffle with seed */
	public static void shuffle(int array[], long seed)
	{
		sRandom.setSeed(seed);
		shuffle(array);
	}

	/** shuffle with seed */
	public static void shuffle(Object array[], long seed)
	{
		sRandom.setSeed(seed);
		shuffle(array);
	}
	
	/** get shuffled copy */
	public static int[] shuffled(int[] array) 
	{
		if (array == null || array.length < 1)
			throw new IllegalArgumentException("null or empty input");
		int copy[] = array.clone();
		shuffle(copy);
		return  copy;
	}  

	public static void SetSeed(long seed)	{ sRandom.setSeed(seed); }

	public static void unit_test() 
	{
		String testName = Shuffler.class.getName() + ".unit_test";
		Sz.begin(testName);
		
		System.out.println("Shuffler test: int array and Integer array:");
		long seed = sRandom.nextLong();
		
		int intArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Shuffler.shuffle(intArray, seed);
		for (int j = 0; j < intArray.length; j++) {
			System.out.print(" " + intArray[j]);
		}
		System.out.println();
		
		Integer integerArray[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		Shuffler.shuffle(integerArray, seed);
		for (int j = 0; j < integerArray.length; j++) {
			System.out.print(" " + integerArray[j]);
		}
		System.out.println();
		Sz.end(testName, 0);
	}
	

    public static void main(String[] args)
    {
        unit_test();
    }
   
}
