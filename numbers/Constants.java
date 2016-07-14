package sprax.numbers;

/**
 * FibonacciInt32 -- just the first 42 Fibonacci numbers, which fit into a 32-bit int.
 * @author sprax
 */

import sprax.test.Sz;

public class Constants 
{
    //// constants of type int
    
    public static final int FIBONACCIS[] = {
        1, 1, 2, 3, 5, 8, 13,
        21, 34, 55, 89, 144, 233, 377,
        610, 987, 1597, 2584, 4181, 6765, 10946,
        17711, 28657, 46368, 75025, 121393, 196418, 317811,
        514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465,
        14930352, 24157817, 39088169, 63245986, 102334155, 165580141
    }; // compare to int java.lang.Integer.MAX_VALUE = 2147483647 [0x7fffffff]
    
    /** A highly composite number (HCN) is a positive integer with more divisors
     *  than any smaller positive integer. 
     */
    public static final long HIGHLY_COMPOSITES[] = {
        1, 2, 4, 6, 12, 24, 36, 48, 60, 120, 180, 240, 360, 720, 840, 1260, 1680, 2520, 5040, 7560
    };
    
    /**
     * A perfect number is an integer that is the sum of its positive proper divisors 
     * (all divisors except itself).
     */
    public static final long PERFECT_NUMBERS[] = {
        6, 28, 496, 8128, 33550336, 
        // 8589869056, 
        // 137438691328,
        // 2305843008139952128,
        // 2658455991569831744654692615953842176,
        // 191561942608236107294793378084303638130997321548169216
    };
    
    
    // public static final long MAX_64BIT_SIGNED = 9223372036854775807;    // 2**63 − 1
    public static final long MAX_LONG = Long.MAX_VALUE;
    
    //// constants of type double
    static public final double EULERS = Math.E; // 2.71828182845904523536028747135266249775724709369995
    static public final double GOLDEN_RATIO = 1.618033988749894848204586834365638117720;
    static public final double GOLDEN_RECIP = 1.0 / GOLDEN_RATIO;
    static public final double PI = Math.PI;



    public static int unit_test(int lvl)
    {
        String testName = Constants.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
                
        double diff = EULERS - Math.exp(1.0);
        numWrong += Sz.oneIfDiff(diff, 0.0);

        double goldenRatio = (1.0 + Math.sqrt(5.0)) / 2.0;
        diff = goldenRatio - GOLDEN_RATIO;
        numWrong += Sz.oneIfDiff(diff, 0.0);

        double goldenRecip = (1.0 - Math.sqrt(5.0)) / 2.0;
        diff = goldenRecip - GOLDEN_RECIP;
        numWrong += Sz.oneIfDiff(diff, 0.0);

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(2);
    }
}
