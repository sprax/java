package sprax.numbers;

/**
 * Fibonacci interface and test class
 * @author sprax
 * Elapsed time: 662396 to compute first 50 recursively (ending with 50  12586269025)
 */

import java.util.ArrayList;

import sprax.sprout.Sx;
import sprax.test.Sz;

public interface Fibonacci
{
    public long fib(int n);
}

class FibonacciRecurse implements Fibonacci
{
    @Override
    public long fib(int n) 
    {
        if (n < 2) {
            return n;
        }
        else {
            return fib(n-1) + fib(n-2);
        }
    }
}

class FibonacciIterate implements Fibonacci
{
    @Override    
    public long fib(int n) 
    {
        long prev1 = 0L, prev2 = 1L;
        for(int i = 0; i < n; i++) {
            long savePrev1 = prev1;
            prev1 = prev2;
            prev2 = savePrev1 + prev2;
        }
        return prev1;
    }
}

class FibonacciMemoized implements Fibonacci
{
    private static ArrayList<Long> fibCache = new ArrayList<Long>();
    static {
        fibCache.add(0L);
        fibCache.add(1L);
    }
    
    @Override
    public long fib(int n) {
        return fibonacci(n);
    }
    
    public static long fibonacci(int n) {
        if (n >= fibCache.size()) {
            fibCache.add(n, fibonacci(n-1) + fibonacci(n-2));
        }
        return fibCache.get(n);
    }    
}

class TestFibonacci
{
    static long test_time_firstN(Fibonacci fibonacci, int size)
    {
        long fibs[] = new long[size];
        
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < size; j++) {
            fibs[j] = fibonacci.fib(j);
        }
        long endTime = System.currentTimeMillis();
        long totTime = endTime - begTime;
        
        Sx.format("%5d Ms for first %d from %s:\t"
                , totTime, size, fibonacci.getClass().getSimpleName());
        for (int j = size; --j >= 0; ) {
            Sx.format(" %d", fibs[j]);
        }
        for (int j = 0; j < size; j++) {
            Sx.format("%d, ", fibs[j]);
            if (j % 7 == 0)
                Sx.puts();
        }
        
        Sx.puts();
        return totTime;
    }
    
    public static int unit_test(int lvl)
    {   
        String testName = TestFibonacci.class.getName() + ".unit_test";  
        Sz.begin(testName);
        
        int size = 93;  // Limit for 64-bit long
        test_time_firstN(new FibonacciIterate(),  size);
        test_time_firstN(new FibonacciMemoized(), size);

        if (lvl > 1) {
            size = 42;  // Limit for 32-bit int
            test_time_firstN(new FibonacciRecurse(),  size);
            test_time_firstN(new FibonacciIterate(),  size);
            test_time_firstN(new FibonacciMemoized(), size);
        }        
        
        
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args) { unit_test(2); }
}
