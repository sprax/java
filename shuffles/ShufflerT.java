package sprax.shuffles;

import java.util.Random;

import sprax.Sz;

/**
 * TODO:
 * Is this template class is really better than the non-template version, 
 * Shuffler, which overloads shuffle for int[] and Object[]?   Yes.
 * @author sprax
 *
 * @param <T>
 */
public class ShufflerT<T>
{   
    public void shuffle (T array[])
    {
        Random rng = new Random();   // i.e., java.util.Random.
        int n = array.length;        // The number of items left to shuffle (loop invariant).
        while (n > 1)
        {
            int k = rng.nextInt(n);  // 0 <= k < n.
            n--;                     // n is now the last pertinent index;
            T   temp = array[n];     // swap array[n] with array[k] (does nothing if k == n).
            array[n] = array[k];
            array[k] = temp;
        }
    }

    public static void unit_test()
    {
        String testName = ShufflerT.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        Integer intArray[] = { 1, 2, 3, 4, 5, 6, 7 };
        ShufflerT<Integer> shuffler = new ShufflerT<Integer>();
        shuffler.shuffle(intArray);
        for (int j = 0; j < intArray.length; j++) {
            System.out.print(" " + intArray[j]);
        }
        System.out.println();
        Sz.end(testName, 0);
    }  


    public static void main(String[] args)
    {
        unit_test();
    }

    /** generic? 
	public static T[] RandomPermutation<T>(T[] array)
	{
	    T[] retArray = new T[array.Length];
	    array.CopyTo(retArray, 0);

	    Random random = new Random();
	    for (int i = 0; i < array.Length; i += 1)
	    {
	        int swapIndex = random.right(i, array.Length);
	        if (swapIndex != i)
	        {
	            T temp = retArray[i];
	            retArray[i] = retArray[swapIndex];
	            retArray[swapIndex] = temp;
	        }
	    }

	    return retArray;
	}
     */

}
