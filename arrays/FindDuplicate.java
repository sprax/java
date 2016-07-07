
package sprax.arrays;

import java.util.Arrays;
import java.util.Random;

import sprax.shuffles.Shuffler;
import sprax.sprout.Sx;

/**
 * Find the single duplicate in an array of size N which contains
 * all ordinal numbers from 1 to N-1, including exactly two copies
 * of the duplicate value.
 * @author sprax
 */
public class FindDuplicate 
{
	/** shared static Random number generator */
	static Random sRandom = new Random();  // i.e., java.util.Random.

	int mArray[];
	int mDuplicate;
	FindDuplicate(int array[])
	{
		mArray = array;
		mDuplicate = findDuplicateFromShuffledOrdinalsXor(mArray);
	}


	/** Make sorted array of ordinal numbers up to size-1 and including one specified duplicate */
	static int[] makeOrdinalArrayWithOneDuplicate(int size, int dupe)
	{
		assert(0 < dupe && dupe < size);
		int array[] = new int[size];
		int j = 0;
		for (; j < dupe; j++) {			
			array[j] = j + 1;
		}
		array[j++] = dupe;
		for (; j < size; j++) {			
			array[j] = j;
		}
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
		int index = sRandom.nextInt(array.length - 1);
		int oldVal = array[index];
		array[index] = newVal;
		return new int[]{index, oldVal};
	}

	static int[] zeroRandomElement(int array[])
	{
		return replaceRandomElement(array, 0);
	}

	static int[] replaceRandomElementWithDuplicate(int array[])
	{
		if (array == null || array.length < 2)
			return null;

		int size = array.length;
		int duplicateValueIndex = sRandom.nextInt(size);
		int replacedValueIndex = sRandom.nextInt(size);
		// Ensure that we get two different indices
		while (replacedValueIndex == duplicateValueIndex)
			replacedValueIndex = sRandom.nextInt(array.length);
		
		int replacedVal = array[replacedValueIndex];
		int duplicateVal = array[duplicateValueIndex];
		array[replacedValueIndex] = duplicateVal;
		return new int[]{ replacedVal, duplicateVal };
	}
	
	/**
	 * Given a sorted array containing all the ordinal numbers from 1 
	 * to the length of the array with the exception that 
	 * one of these numbers is entered twice, replace another which
	 * is now missing, find the duplicated number.
	 * 
	 * @param array
	 * @return duplicated ordinal value
	 */
	static int findDuplicateFromSortedOrdinals(int array[])
	{
		if (array == null || array.length < 2)
			return -1;

		int size = array.length;
		for (int j = 0; j < size; j++)
		{
			if (array[j] != j+1)
				return array[j];
		}
		return -1;
	}
	
	/** 
	 * Naive way of finding the duplicate in an array of randomly ordered ordinal numbers.
	 * Complexity dominated by the sort, expected to be O(N lg N) in place, or O(N) in time
	 * with O(N) additional space -- but possibly better with in-place modified merge sort?
	 */
	static int findDuplicateFromShuffledOrdinalsNaive(int array[])
	{
		if (array == null || array.length < 2)
			return -1;
		
		Arrays.sort(array);
		return findDuplicateFromSortedOrdinals(array);
	}
	
	static int findDuplicateFromShuffledOrdinalsSum(int array[])
	{
		if (array == null || array.length < 2)
			return -1;
		
		int N = array.length;
		int formulaSum = (N - 1) * N / 2;	// sum of the N-1 ordinals in [1...N-1]
		int foundSum = 0;					// actual sum the values including duplicate
		for (int j = 0; j < N; j++)
		{
			foundSum += array[j];
		}
		return foundSum - formulaSum;
	}
	
	static int findDuplicateFromShuffledOrdinalsXor(int array[])
	{
		if (array == null || array.length < 2)
			return -1;

		int N = array.length;
		int xorSum = array[N - 1];			// Don't forget to include last value
		for (int j = 0; j < N - 1; j++)		// iterate only from 1 to N-1
		{
			xorSum ^= (array[j] ^ (j+1));	// "cancel out" all non-repeated numbers
		}
		return xorSum;						// only the repeated number remains
	}
	
	/**
	 * Given a sorted array containing all the ordinal numbers from 1 
	 * to array.length in ascending order, with the exception that 
	 * one of these numbers is entered twice, replace another which
	 * is now missing, find the duplicated number.
	 * 
	 * @param array
	 * @return duplicated ordinal value
	 */
	static int findDuplicateAndMissingFromShuffledOrdinals(int array[])
	{
		return findDuplicateFromShuffledOrdinalsXor(array);
	}
	
	static int test_findDuplicatedOrdinal(int size)
	{
		int error, numErrors = 0;
		int duplicate = 1 + sRandom.nextInt(size - 2);		// random number in the range [1, size-1]
		int array[] = makeOrdinalArrayWithOneDuplicate(size, duplicate);

		int cheatDupe = findDuplicateFromSortedOrdinals(array);
		error = duplicate - cheatDupe;
		if (error != 0)
			numErrors++;
		Sx.format("findDuplicateFromSortedOrdinals(%d) = %d and real dupe = %d  (error %d)\n"
				, size, cheatDupe, duplicate, error);
		
		Shuffler.shuffle(array);
		int sumDupe = findDuplicateFromShuffledOrdinalsSum(array);
		error = duplicate - sumDupe;
		if (error != 0)
			numErrors++;
		Sx.format("findDuplicateFromShuffledOrdinalsSum(%d) = %d and real dupe = %d  (error %d)\n"
				, size, sumDupe, duplicate, error);
		
		int xorDupe = findDuplicateFromShuffledOrdinalsXor(array);
		error = duplicate - xorDupe;
		if (error != 0)
			numErrors++;
		Sx.format("findDuplicateFromShuffledOrdinalsXor(%d) = %d and real dupe = %d  (error %d)\n"
				, size, xorDupe, duplicate, error);
		
		int naiveDupe = findDuplicateFromShuffledOrdinalsNaive(array);
		error = duplicate - naiveDupe;
		if (error != 0)
			numErrors++;
		Sx.format("findDuplicateFromShuffledOrdinalsNaive(%d) = %d and real dupe = %d  (error %d)\n"
				, size, naiveDupe, duplicate, error);

		/*******************************
		if (error == 0)
		{
			// restore missing value
			for (int j = 0; j < size; j++) {
				if (array[j] == 0) {
					array[j] = replaced;
					break;
				}
			}
			Shuffler.shuffle(array);
			int repAndDupe[] = replaceRandomElementWithDuplicate(array);
			int replaced = repAndDupe[0];
		}
		***********************/
		return numErrors;
	}
	
	public static int unit_test(int level) 
	{
		String  testName = FindZeroed.class.getName() + ".unit_test";  
		Sx.puts(testName + " BEGIN\n");  

		int size = 10;
		int errors = test_findDuplicatedOrdinal(size);

		Sx.puts(testName + " ENDED with " + errors + " errors.");  
		return errors;
	}

	public static void main(String[] args) { unit_test(0); }    

}
