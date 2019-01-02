package sprax.numbers;

import sprax.sprout.Sx;

public class Numeral2Counter 
{
    public static boolean endsIn2(int num)
    {
        int mod = num % 10;
        return mod == 2 || mod == -2;
    }
    
    public static boolean posEndsIn2(int nonneg)
    {
        assert(nonneg >= 0);
        int mod = nonneg % 10;
        return mod == 2;
    }
    
    public static int count2sInNumber(int num)
    {
        int count = 0;
        while (num != 0) {
            if (endsIn2(num))
                ++count;
            num /= 10;
        }
        return count;
    }
    
    /** count occurrences of the numeral 2 in all decimal numbers from 0 to num 
     * runtime: O(N log N)
     */
    public static int count2sInRangeNaive(int num) 
    {
        int totCount = 0;
        if (num < 0)
            num = -num;
        for (int k = 2; k <= num; k++) {
            totCount += count2sInNumber(k);
        }
        return totCount;
    }
    
    /** count occurrences of the numeral 2 in all decimal numbers from 0 to num 
     * runtime: O(N)
     */
    public static int count2sInRangeIncremental(int num) 
    {
        int numCount = 0, totCount = 0;
        if (num < 0)
            num = -num;
        for (int k = 2; k <= num; k++) {
            if (posEndsIn2(k))
                totCount += 1 + numCount;
            else {
                int t = 10;
                int r = k % t;
                while (r == 0) {
                    t *= 10;
                    r = k % t;
                }
                if (r == 2 * t)
                    ++numCount;
                else if (r == 3 * t)
                    --numCount;
                totCount += numCount;
            }
        }
        return totCount;
    }
    
    /**
     * count occurrences of the numeral 2 in all decimal numbers from 0 to num runtime: O(N)
     */
    public static long count2sInRangeUp(int num)
    {
        if (num < 0)
            num = -num;
        
        long total = 0;
        long count = 0;
        long pow10 = 1;
        long prevd = 0;
        if (num % 10 == 2)
            total = 1;
        for (int dim = num; dim != 0; dim /= 10) {
            int digit = dim % 10;
            if (digit > 2) {
                total += pow10;
            } else if (digit == 2) {
                total += (prevd + 1) * pow10 / 10;
            }
            total += digit * count;
            count = count * 10 + pow10;
            pow10 = pow10 * 10;
            prevd = digit;
        }
        return total;
    }
    
    public static int wrong(boolean result, boolean expected) { return result == expected ? 0 : 1; }
    public static int wrong(int result, int expected) { return result == expected ? 0 : 1; }

	
	public static int unit_test(int level)
	{
		String testName = Numeral2Counter.class.getName() + ".unit_test";
		Sx.format("BEGIN: %s\n", testName);

		int numWrong = 0;
        numWrong += wrong(endsIn2(1), false);
        numWrong += wrong(endsIn2(-22), true);
        numWrong += wrong(count2sInNumber(2324225), 4);
        numWrong += wrong(count2sInNumber(-1232422425), 5);
        numWrong += wrong(count2sInRangeNaive(-33), 14);
        numWrong += wrong(count2sInRangeNaive(452), 195);
        numWrong += wrong(count2sInRangeNaive(999), 300);
        //numWrong += wrong(count2sInRangeIncremental(99), 20);
        //numWrong += wrong(count2sInRangeIncremental(999), 300);
        
        Sx.format("Naive    1000: %d \n", count2sInRangeNaive(1000));
        Sx.format("Naive   10000: %d \n", count2sInRangeNaive(10000));
        Sx.format("Naive  100000: %d \n", count2sInRangeNaive(100000));
        Sx.format("Naive 1000000: %d \n", count2sInRangeNaive(1000000));
        
   
        int num; 
        num =  -1;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =   0;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =   1;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =   2;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =   3;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  10;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  11;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  20;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  23;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  27;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  30;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  37;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num =  77;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 177;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 277;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 400;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 402;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 421;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 502;  Sx.format("Up: %d : %d\n", num, count2sInRangeUp(num));
        num = 502;  Sx.format("Nv: %d : %d\n", num, count2sInRangeNaive(num));

		Sx.format("END %s, status %s\n", testName, (numWrong == 0 ? "PASS" : "FAIL"));
		return 0;
	}

	public static void main(String args[]) { unit_test(1); }
}