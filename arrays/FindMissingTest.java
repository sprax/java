package sprax.arrays;

import java.util.Arrays;
import java.util.Random;

import sprax.sprout.Sx;

// package-only interface
interface Finder { int find(int array[]); }

class FindMissingOrdinalFromSorted implements Finder
{
	static int findMissingFromSortedOrdinals(int array[])
	{
		if (array != null)
		{
			if (array.length == 0)
				return 1;
			for (int j = 0; j < array.length; j++) {
				if (array[j] != j+1)				// if this entry is not the expected ordinal,
					return j+1;						// then that ordinal is missing.
			}
		}
		return array.length + 1;	
	}

	@Override
	public int find(int[] array) {
		return findMissingFromSortedOrdinals(array);
	}
}

class FindMissingOrdinalUsingSeriesSum implements Finder
{
	static int findMissingFromShuffledOrdinalsSum(int array[])
	{
		if (array == null)
			return -1;
		if (array.length < 1)
			return  1;

		int M = array.length + 1;	// Because one is missing, the size of the array is maxOrd - 1
		int ordinalSum = M * (M + 1) / 2;		// sum for the ordinal series with none missing
		int foundSum = 0;

		for (int j = 0; j < array.length; j++)
		{
			foundSum += array[j];				// compute actual sum, which excludes the missing
		}
		return ordinalSum - foundSum;
	}

	@Override
	public int find(int[] array) {
		return findMissingFromShuffledOrdinalsSum(array);
	}
}

class FindMissingOrdinalUsingXor implements Finder
{
	static int findMissingFromShuffledOrdinalsXor(int array[])
	{
		if (array == null)
			return -1;
		if (array.length < 1)
			return  1;

		int missingXorSum = 0;					// will hold the xor sum of vals in array
		int ordinalXorSum = 1;					// will hold the xor sum of all ordinals
		for (int j = 0; j < array.length; j++)	// iterate over the actual array, of course
		{
			missingXorSum ^= array[j];
			ordinalXorSum ^= j + 2;				// goes up to maxOrd == length + 1
		}
		return missingXorSum ^ ordinalXorSum;	// only the missing number remains
	}

	@Override
	public int find(int[] array) {
		return findMissingFromShuffledOrdinalsXor(array);
	}
}


class FindMissingOrdinalsNaive implements Finder
{
	static int findMissingFromShuffledOrdinalsNaive(int array[])
	{
		if (array != null)
		{
			Arrays.sort(array);
			return FindMissingOrdinalFromSorted.findMissingFromSortedOrdinals(array);
		}
		return -1;
	}

	@Override
	public int find(int[] array) {
		return findMissingFromShuffledOrdinalsNaive(array);
	}
}


public class FindMissingTest
{
	/** shared static Random number generator */
	static Random sRandom = new Random();  // i.e., java.util.Random.

	int mArray[];
	int mMissing;
	FindMissingTest(int maxOrd)
	{
		mMissing = 1 + sRandom.nextInt(maxOrd);			// random number in range [1, maxOrd]
		mArray = makeOrdinalArrayWithOneMissing(maxOrd, mMissing);
		assert(mArray.length == maxOrd - 1);
	}

	/**
	 * Make an array of ordinal numbers in the range [1, N-1] with a specified
	 * one of them missing.
	 * The returned array will be of length = maxOrd - 1.
	 * @param maxOrd
	 * @param missing
	 * @return
	 */
	static int[] makeOrdinalArrayWithOneMissing(int maxOrd, int missing)
	{
		assert(0 < missing && missing <= maxOrd);
		int size = maxOrd - 1;
		int array[] = new int[size];
		int ord;
		for (ord = 1; ord < missing; ord++)
			array[ord-1] = ord;
		while (++ord <= maxOrd)
			array[ord-2] = ord;
		return array;
	}

	static int test_finder(Finder finder, int array[], int truth, int maxOrd)
	{
		int found = finder.find(array);
		int error = truth - found;
		Sx.format("%s(%d) = %d and real missing = %d  (error %d)\n"
				, finder.getClass().getSimpleName(), maxOrd, found, truth, error);
		return error;
	}

	static int test_findMissingOrdinal(int maxOrd)
	{
		int error, numErrors = 0;
		FindMissingTest test = new FindMissingTest(maxOrd);
		int missing = test.mMissing;
		int array[] = test.mArray;

		error = test_finder(new FindMissingOrdinalFromSorted(), array, missing, maxOrd);;
		if (error != 0)
			numErrors++;

		error = test_finder(new FindMissingOrdinalUsingSeriesSum(), array, missing, maxOrd);;
		if (error != 0)
			numErrors++;

		error = test_finder(new FindMissingOrdinalUsingXor(), array, missing, maxOrd);;
		if (error != 0)
			numErrors++;

		error = test_finder(new FindMissingOrdinalsNaive(), array, missing, maxOrd);;
		if (error != 0)
			numErrors++;

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
		String  testName = FindMissingTest.class.getName() + ".unit_test";  
		Sx.puts(testName + " BEGIN\n");  

		int maxOrdinal = 10;
		int errors = test_findMissingOrdinal(maxOrdinal);

		Sx.puts(testName + " ENDED with " + errors + " errors.");  
		return errors;
	}

	public static void main(String[] args) { unit_test(0); }    
}
