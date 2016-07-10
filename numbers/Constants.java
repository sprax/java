package sprax.numbers;

/**
 * FibonacciInt32 -- just the first 42 Fibonacci numbers, which fit into a 32-bit int.
 * @author sprax
 */

import java.util.Arrays;

import org.apache.commons.lang3.EnumUtils;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class Constants 
{
    //// constants of type int
    public final static int Fibonaccis[] = {
                                    1, 1, 2, 3, 5, 8, 13,
                                    21, 34, 55, 89, 144, 233, 377,
                                    610, 987, 1597, 2584, 4181, 6765, 10946,
                                    17711, 28657, 46368, 75025, 121393, 196418, 317811,
                                    514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465,
                                    14930352, 24157817, 39088169, 63245986, 102334155, 165580141
                                    }; // compare to int java.lang.Integer.MAX_VALUE = 2147483647 [0x7fffffff]
    
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
                
        double E_diff = EULERS - Math.exp(1.0);
        numWrong += Sz.oneIfDiff(E_diff, 0.0);

        double goldenRatio = (1.0 + Math.sqrt(5.0)) / 2.0;
        double G_diff = GOLDEN_RATIO - goldenRatio;
        numWrong += Sz.oneIfDiff(G_diff, 0.0);

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(2);
    }
}
