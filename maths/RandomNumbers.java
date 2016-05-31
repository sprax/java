package sprax.maths;

import java.util.Random;

import sprax.Sx;
import sprax.Sz;
import sprax.arrays.ArrayAlgo;

public class RandomNumbers 
{
	/** shared static Random number generator */
	static Random sRandom = new Random(5);  // i.e., java.util.Random.

	/// Problem: given rand5, implement rand7
	
	static public int rand5()
	{
		return sRandom.nextInt(5);
	}
	
	/*
	 * Exactly one way to generate each number in [0, 24], so each one is equally likely
	 *  0 = 5 * 0 + 0
	 *  1 = 5 * 0 + 1
	 *  2 = 5 * 0 + 2
	 *  3 = 5 * 0 + 3
	 *  4 = 5 * 0 + 4
	 *  5 = 5 * 1 + 0
	 *  6 = 5 * 1 + 1
	 *  7 = 5 * 1 + 2
	 * ...
	 * 23 = 5 * 4 + 3
	 * 24 = 5 * 4 + 4
	 * Modulo 7, there are 3 ways to get each number in [0, 6] from [0, 20]
	 * Using [21, 24] would give extra chances for [0, 3], so reject it. 
	 * @return pseudo-random number in [0, 6]
	 */
	static public int rand7fromRand5() 
	{
		do {
			int ans = 5 * rand5() + rand5();
			if (ans < 21)
				return ans % 7;
		} while (true);
	}
	

	static public int test_rand7fromRand5(int size)
	{
        Sx.puts(RandomNumbers.class.getName() + ".test_rand7fromRand5");
		int histogram[] = new int[7];
		for (int j = 0; j < size; j++)
		{
			histogram[rand7fromRand5()]++;
		}
		Sx.putsArray("Historgram: ", histogram);
		double mean = ArrayAlgo.mean(histogram);
		double sdev = ArrayAlgo.stddev(histogram);
		Sx.format("Mean and StdDev:  %f  %f\n", mean, sdev);
		return 0;
	}
	
    public static int unit_test() 
    {
        String testName = RandomNumbers.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        numWrong += test_rand7fromRand5(10000);
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }	

}
