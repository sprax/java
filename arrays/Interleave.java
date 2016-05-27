package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import sprax.Sx;

public class Interleave 
{
	static int[] makeInterleaved3(final int arr[])  // shuffle into new array
	{
		assert(arr.length % 3 == 0);

		int N = arr.length/3;
		int j = 0, r = 0, g = N, b = N*2;
		int rgb[] = new int[arr.length];
		while (j < arr.length)
		{
			rgb[j++] = arr[r++];
			rgb[j++] = arr[g++];
			rgb[j++] = arr[b++];
		}
		return rgb;
	}

	// @deprecated
	// because it is incorrect
	// while replacing the first pane of red pixels, each interleaved rgb pixel displaces 3 red values,
	// but replaces only 1 red value, 
	static void interleaveInPlace3(int arr[])  // shuffle
	{
		assert(arr.length % 3 == 0);

		int N = arr.length/3;
		int j = 0, r = 0, g = N, b = N*2, k = 0, m;
		int tempVal0, tempVal1, tempVal2;
		while (j < N)
		{
			tempVal0 = arr[j];
			arr[j++] = arr[r];
			arr[r++] = tempVal0;

			tempVal1 = arr[j];
			arr[j++] = arr[g];			// g = N + j/3
			arr[g++] = tempVal1;

			tempVal2 = arr[j];
			arr[j++] = arr[b];			// b = N*2 + j/3
			arr[b++] = tempVal2;

			k = j / 3;
			m = k % 3;
			if (m == 1)
				r = N + k;
			else if (m == 2)
				r = N*2 + k;
			else
				r = N + k/2;
		}
	}

	static int test_interleave()
	{
		Sx.puts("test_interleave:");
		int N = 6;
		int arr[] = new int[3*N];
		for (int j = 0; j < N; j++) {
			arr[j      ] = 100 + j;
			arr[j + N  ] = 200 + j;
			arr[j + N*2] = 300 + j;
		}
		Sx.putsArray("Before:  ", arr);
		int rgb[] = makeInterleaved3(arr);
		Sx.putsArray("After:   ", rgb);
		interleaveInPlace3(arr);
		Sx.putsArray("InPlace: ", arr);
		Sx.puts("test_interleave END.\n");
		return 0;
	}

	public static int unit_test(int level) 
	{
		String  testName = ArrayAlgo.class.getName() + ".unit_test";  
		Sx.puts(testName + " BEGIN");  

		int stat = test_interleave();
		
		Sx.puts(testName + " END");  
		return stat;
	}

	public static void main(String[] args)
	{
		unit_test(2);
	}

}
