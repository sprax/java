package sprax.numbers;

import java.util.ArrayList;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class Primes 
{
	public static boolean isPrime(int num)
	{
		if (num < 2)
			return false;		// because 2 is the smallest prime number
		if (num < 4)
			return true;
		int root = (int)Math.sqrt(num);
		for (int k = 2; k <= root; k++)
		{
			if (num % k == 0)
				return false;
		}
		return true;
	}
	
	static int test_isPrime(int lo, int hi)
	{
		for (int j = lo; j <= hi; j++)
			Sx.format("IsPrime(%2d) = %s\n", j, isPrime(j));
		return 0;
	}
	
	public static ArrayList<Integer> primesInRangeNaive(int lo, int hi)
	{
		if (lo < 2)
			lo = 2;			// because 2 is the smallest prime number
		if (lo > hi)
			return null;
		ArrayList<Integer> primes = new ArrayList<Integer>();
		for (int j = lo; j <= hi; j++)
		{
			if (isPrime(j)) 
			{
				primes.add(j);
			}
		}
		return primes;
	}

	public static ArrayList<Integer> primesInRangeSieve(int lo, int hi)
	{
		if (lo < 2)
			lo = 2;			// because 2 is the smallest prime number
		if (lo > hi)
			return null;

		ArrayList<Integer> primes = new ArrayList<Integer>();
		boolean isLoOdd = (lo % 2) == 1;
		int rsize = hi - lo + 1;
		if (rsize < 2)
		{
			if (isLoOdd && isPrime(lo))
			{
				primes.add(lo);
				return primes;
			}
			else
			{
				return null;
			}	
		}
		int range[] = new int[rsize];	// initially all 0's
		// Set only the odd numbers in the range:
		for (int j = (isLoOdd ? 0 : 1); j < rsize; j += 2)
			range[j] = j + lo;
		
		
		// Remove multiples of primes n*p, where n > 1.
		// Start with p = 3, skip even numbers, and end at floor(sqrd(hi)).
		int ssize = 1 + (int)Math.sqrt(hi);
		int sieve[] = new int[ssize];

		int rval;
		for (int p = 3; p < ssize; p += 2)
		{
			if (sieve[p] == 0)
			{
				// p is prime, so mark all multiples of p in the sieve as non-prime
				for (int sidx = p * 2; sidx < ssize; sidx += p)
				{
					sieve[sidx] = p;
				}

				// and zero all odd multiples of of p in the range (the even ones are
				// already zero).
				if (lo <= p)
				{
					rval = p * 3;
				}
				else
				{
					int quot = lo / p;
					if (quot % 2 == 1)
						rval = p * quot;
					else
						rval = p * (quot + 1);
					if (rval < lo)
						rval += p * 2;
					//rval = (lo % p == 0) ? lo : (lo / p + 1) * p;
				}
				for (int ridx = rval - lo; ridx < rsize; ridx += p*2)
				{
					range[ridx] = 0;
				}
			}
		}
		
		if (lo == 2)
			primes.add(2);
		for (int j = (isLoOdd ? 0 : 1); j < rsize; j += 2)
		{
			if (range[j] > 0)
				primes.add(range[j]);
		}
		return primes;
	}

	
	static int test_primesInRange(int lo, int hi)
	{
		ArrayList<Integer> primesA = primesInRangeNaive(lo, hi);
		int countA = primesA.size();
		Sx.format("primesInRangeNaive(%d, %d) got %d primes:\n",  lo, hi, countA);
		Sx.putsArray(primesA);

		ArrayList<Integer> primesB = primesInRangeSieve(lo, hi);
		int countB = primesB.size();
		Sx.format("primesInRangeSeive(%d, %d) got %d primes:\n",  lo, hi, countB);
		Sx.putsArray(primesB);
		return 0;
	}
	
	public static int[] digitCounts(ArrayList<Integer> nums)
	{
		if (nums == null || nums.size() < 1)
			return null;
		
		int counts[] = new int[10];
		for (int num : nums)
		{
			while (num > 0)
			{
				counts[num % 10]++;
				num /= 10;
			}
		}
		return counts;
	}
	
	/**
	 * Given two numbers L and R, find the most frequent digit appearing in the prime numbers 
	 * within the range [L, R] inclusive.  If multiple digits have the same maximal frequency, 
	 * return the largest of them. If there are no prime numbers between L and R, return -1.
	 * @param lo
	 * @param hi
	 * @return
	 */
	public static int mostFrequentDigitInPrimesInRange(int lo, int hi)
	{
		ArrayList<Integer> primes = primesInRangeNaive(lo, hi);
		int counts[] = digitCounts(primes);
		if (counts == null)
			return -1;
		int maxCount = -1;
		int maxIndex = -1;
		// start with largest digit, so it wins in case of a tie
		for (int j = counts.length; --j >= 0; )
		{
			if (maxCount < counts[j])
			{
				maxCount = counts[j];
				maxIndex = j;
			}
		}
		return maxIndex;
	}
	
	public static int test_mostFrequentDigitInPrimesInRange()
	{
		int errors = 0;
		int lo, hi, mfd;
		
		lo = -5; hi = 29;
		mfd = mostFrequentDigitInPrimesInRange(lo, hi);
		if (mfd != 1)
			errors++;
		Sx.format("mostFrequentDigitInPrimesInRange(%d, %d) = %d\n", lo, hi, mfd);

		lo = 50; hi = 69;
		mfd = mostFrequentDigitInPrimesInRange(lo, hi);
		if (mfd != 6)
			errors++;
		Sx.format("mostFrequentDigitInPrimesInRange(%d, %d) = %d\n", lo, hi, mfd);
		return errors;
	}
	
	public static int unit_test(int level)
	{
		String testName = Primes.class.getName() + ".unit_test";
		Sz.begin(testName);

		if (level > 0)
		{
			int lo = -1;
			int hi = 97;
			if (level > 1)
				test_isPrime(lo, hi);
			test_primesInRange(lo, hi);
			int offset = 2345;
			test_primesInRange(lo + offset, hi + offset);
		}
		test_mostFrequentDigitInPrimesInRange();
		
		Sz.end(testName, 0);
		return 0;
	}

	public static void main(String args[]) { unit_test(1); }
}