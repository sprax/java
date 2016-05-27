package sprax.arrays;

import java.util.Random;
import sprax.Sx;
import sprax.shuffles.Shuffler;

public class FindZeroed 
{
	
	/** shared static Random number generator */
	static Random sRandom = new Random();  // i.e., java.util.Random.

	int mArray[];
	int mZeroed;
	FindZeroed(int array[])
	{
		mArray = array;
		mZeroed = findZeroedFromShuffledOrdinalsSum(mArray);
	}

	static int[] makeOrdinalArray(int size)
	{
		int array[] = new int[size];
		for (int j = 0; j < size; j++)
			array[j] = j+1;
		return array;
	}
	
	/**
	 * Replace a randomly chosen array element with newVal;
	 * return the random index and old value found there.
	 * @param array
	 * @param newVal
	 * @return
	 */
	static int[] replaceRandomElement(int array[], int newVal)
	{
		if (array == null || array.length < 1)
			return new int[]{-1, -1};
		int index = sRandom.nextInt(array.length);
		int oldVal = array[index];
		array[index] = newVal;
		return new int[]{index, oldVal};
	}

	static int[] zeroRandomElement(int array[])
	{
		return replaceRandomElement(array, 0);
	}
	
	static int findZeroedFromShuffledOrdinalsSum(int array[])
	{
		int foundSum = 0, N = array.length;
		int formulaSum = N * (N + 1) / 2;
		for (int j = 0; j < N; j++)
		{
			foundSum += array[j];
		}
		return formulaSum - foundSum;
	}
	
	static int test_findZeroedOrdinal(int size)
	{
		int array[] = makeOrdinalArray(size);
		int iandv[] = zeroRandomElement(array);
		int index = iandv[0];
		int oldVal = iandv[1];
		assert(oldVal == index + 1);
		Shuffler.shuffle(array);
		int zeroed = findZeroedFromShuffledOrdinalsSum(array);
		int error = zeroed - oldVal;
		Sx.format("findZeroedFromShuffledOrdinalsSum(%d) = %d and zeroed val = %d  (error %d)\n"
				, size, zeroed, oldVal, error);
		
		if (error == 0)
		{
			// restore zeroed value
			for (int j = 0; j < size; j++) {
				if (array[j] == 0) {
					array[j] = zeroed;
					break;
				}
			}
			Shuffler.shuffle(array);
			iandv = zeroRandomElement(array);
			oldVal = iandv[1];
			zeroed = findZeroedFromShuffledOrdinalsSum(array);
			error = zeroed - oldVal;
			Sx.format("findZeroedFromShuffledOrdinalsSum(%d) = %d and zeroed val = %d  (error %d)\n"
					, size, zeroed, oldVal, error);
		}
		return error;
	}
	
	public static int unit_test(int level) 
	{
		String  testName = FindZeroed.class.getName() + ".unit_test";  
		Sx.puts(testName + " BEGIN\n");  

		int size = 77;
		int errors = test_findZeroedOrdinal(size);

		Sx.puts(testName + " ENDED with " + errors + " errors.");  
		return errors;
	}

	public static void main(String[] args) { unit_test(0); }    

}
