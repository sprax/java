package sprax.maths;

import sprax.sprout.Sx;

public class SquareOlgN {

	public static int squareIterative(int value)
	{
		if (value == 1)
			return value;
		if (value < 0)
			value = -value;
		
		int multiplier = value;
		int original = value;
		boolean odd = value % 2 != 0;

		while (multiplier > 1)
		{
			value <<= 1;
			multiplier /= 2;
		}

		return odd ? value + original : value;
	}

	public static int squareRecursive(int value) 
	{
		if (value < 0)
			value = -value;
		return multiplyPositiveRecursive(value, value);
	}

	public static int multiplyBitshift(int larger, int smaller)
	{
		if (larger > smaller)
			return multiplyPositiveRecursive(larger, smaller);
		else
			return multiplyPositiveRecursive(smaller, larger);
	}
	
	/** recursive multiplication */
	private static int multiplyPositiveRecursive(int larger, int smaller)
	{
		assert(0 <= smaller);
		assert(smaller <= larger);
		if (smaller == 1) 
		{
			return larger;
		}
		if (smaller == 0)
			return 0;

		if (smaller % 2 == 0) 
			return multiplyPositiveRecursive(larger << 1, smaller / 2);
		else
			return multiplyPositiveRecursive(larger << 1, smaller / 2) + larger;
	}

	private static void test_squareIterative(int value)
	{
		int square = squareIterative(value);
		Sx.puts("squareIterative(" + value + ") \t:\t" + square);
	}


	private static void test_squareRecursive(int value)
	{
		int square = squareRecursive(value);
		Sx.puts("squareRecursive(" + value + ") \t:\t" + square);
	}



	public static void unit_test() 
	{
		String testName =  SquareOlgN.class.getName() + ".unit_test";
		Sx.puts(testName + ": BEGIN");

		test_squareIterative(0);
		test_squareRecursive(0);
		test_squareIterative(1);
		test_squareRecursive(1);
		test_squareIterative(2);
		test_squareRecursive(2);
		test_squareIterative(-2);
		test_squareRecursive(-2);
		test_squareIterative(3);
		test_squareRecursive(3);
		test_squareIterative(-3);
		test_squareRecursive(-3);

		Sx.puts(testName + ": END");
	}

	public static void main(String[] args) { unit_test(); }
}
