package sprax.numbers;

/**
 * FibonacciInt32 -- just the first 42 Fibonacci numbers, which fit into a 32-bit int.
 * @author sprax
 */

import java.util.Arrays;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class FibonacciInt32 implements Fibonacci
{
    public final static int fib32[] = {
                                    1, 1, 2, 3, 5, 8, 13,
                                    21, 34, 55, 89, 144, 233, 377,
                                    610, 987, 1597, 2584, 4181, 6765, 10946,
                                    17711, 28657, 46368, 75025, 121393, 196418, 317811,
                                    514229, 832040, 1346269, 2178309, 3524578, 5702887, 9227465,
                                    14930352, 24157817, 39088169, 63245986, 102334155, 165580141
                                    }; // compare to int java.lang.Integer.MAX_VALUE = 2147483647 [0x7fffffff]
    
    public final static int COUNT   = fib32.length;   // 41
                                                    
    @Override
    public long fib(int n)
    {
        return fib32(n);        // the method below
    }
    
    public static int fib32(int n)
    {
        if (0 <= n && n < COUNT)
            return fib32[n];    // the static array above
        else
            throw new IllegalArgumentException("index OOB");
    }
    
    public static int[] fib32Range(int from, int to)
    {
        if (0 <= from || from <= to || to < COUNT)
            return Arrays.copyOfRange(fib32, from, to);
        else
            throw new IllegalArgumentException("bad from/to");
    }
    
 
    public static int unit_test(int lvl)
    {
        String testName = FibonacciInt32.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += Sz.oneIfDiff(fib32(0), 1);
        numWrong += Sz.oneIfDiff(fib32(1), 1);
        numWrong += Sz.oneIfDiff(fib32(2), 2);
        numWrong += Sz.oneIfDiff(fib32(3), 3);
        
        int fibs[] = fib32Range(0, COUNT);
        numWrong += Sz.oneIfDiff(fibs.length, COUNT);

        Sx.printArrayFoldedCsv(fibs, 7);
        Sx.putss();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(2);
    }
}
