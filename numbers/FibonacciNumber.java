package sprax.numbers;

import java.math.BigInteger;
import java.util.ArrayList;

import sprax.sprout.Sx;


public interface FibonacciNumber<T extends Number>
{
    public T fib(int n);
}

class FibonacciLongRecurse implements FibonacciNumber<Long>
{
    @Override
    public Long fib(int n) 
    {
        if (n < 2) {
            return (long)n;
        } else {
            return fib(n-1) + fib(n-2);
        }
    }
}

class FibonacciLongIterate implements FibonacciNumber<Long>
{
    @Override    
    public Long fib(int n) 
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

class FibonacciLongMemoized implements FibonacciNumber<Long>
{
    private static ArrayList<Long> sFibCache = new ArrayList<Long>();
    static {
        sFibCache.add(0L);
        sFibCache.add(1L);
    }
    
    @Override
    public Long fib(int n) {
        return fibonacci(n);
    }
    
    public static long fibonacci(int n) {
        if (n >= sFibCache.size()) {
            sFibCache.add(n, fibonacci(n-1) + fibonacci(n-2));
        }
        return sFibCache.get(n);
    }    
}

class FibonacciBigIntMemo implements FibonacciNumber<BigInteger>
{
    private static ArrayList<BigInteger> sFibCache = new ArrayList<BigInteger>();
    static {
        sFibCache.add(BigInteger.ZERO);
        sFibCache.add(BigInteger.ONE);
        sFibCache.add(BigInteger.ONE);
    }
    
    @Override
    public BigInteger fib(int n) {
        return fibonacci(n);
    }
    
    public static BigInteger fibonacci(int n) {
        if (n >= sFibCache.size()) {
            sFibCache.add(n, fibonacci(n-1).add(fibonacci(n-2)));
        }
        return sFibCache.get(n);
    }
}

class FibonacciDoubleMemo implements FibonacciNumber<Double>
{
    private static ArrayList<Double> sFibCache = new ArrayList<Double>();
    static {
        sFibCache.add(0.0);
        sFibCache.add(1.0);
        sFibCache.add(1.0);
        sFibCache.add(2.0);
        sFibCache.add(3.0);
        sFibCache.add(5.0);
    }
    
    @Override
    public Double fib(int n)    { return fibonacci(n); }
    
    public static Double fibonacci(int n) 
    {
        if (n >= sFibCache.size()) {
            sFibCache.add(n, Math.rint(fibonacci(n-1) + (fibonacci(n-2))));
        }
        return sFibCache.get(n);
    }
}

class FibonacciDoubleClosed implements FibonacciNumber<Double>
{
    private static ArrayList<Double> sFibCache = new ArrayList<Double>();
    static {
        sFibCache.add(0.0);
        sFibCache.add(1.0);
        sFibCache.add(1.0);
        sFibCache.add(2.0);
        sFibCache.add(3.0);
        sFibCache.add(5.0);
    }
    private static int sCacheSize   = sFibCache.size();
    
    public static final double mPhi = (1.0 + Math.sqrt(5.0))/2.0;
    public static final double mR5r = (1.0 / Math.sqrt(5.0));
    
    @Override
    public Double fib(int n)    { return fibonacci(n); }
    
    public static Double fibonacci(int n) 
    {
        if (n >= sCacheSize) {
            for (int j = sCacheSize; j <= n; j++) {
                double fj = Math.floor(Math.pow(mPhi, j)*mR5r + 0.5);
                sFibCache.add(fj);
            }
            sCacheSize = n + 1;
            //assert(sCacheSize == sFibCache.size());
        }
        return sFibCache.get(n);
    }
}

class TestFibonacciNumber<T extends Number>
{   
    static <T extends Number> long test_time_firstN(FibonacciNumber<T> fibonacci, int size, T fibs[])
    {
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < size; j++) {
            fibs[j] = (T) fibonacci.fib(j);
        }
        long endTime = System.currentTimeMillis();
        long totTime = endTime - begTime;
        
        Sx.format("%d Ms, first %d, %s:\t"
                , totTime, size, fibonacci.getClass().getSimpleName());
        
        if (fibs[0] instanceof Double) {
            for (int j = size; --j >= 0; ) {
                Number fj = fibs[j];
                double dv = fj.doubleValue();
                if (dv <= Long.MAX_VALUE)
                    Sx.print(" " + fj.longValue());
                else
                    Sx.print(" " + fj);
            }            
        } else {
            for (int j = size; --j >= 0; ) {
                Sx.print(" " + fibs[j]);
            }
        }
        Sx.puts();
        return totTime;
    }
    
    public static int unit_test(int lvl)
    {   
        String testName = TestFibonacciNumber.class.getName() + ".unit_test";  
        Sx.puts(testName + " BEGIN");
        
        int size = 93;  // Limit for long
        int bigN = 512;
        Long fibs[]         = new Long[size];
        Double dbls[]       = new Double[size];
        BigInteger bigs[]   = new BigInteger[bigN];

        // static access
        int    n    = 6;
        double fibD = FibonacciDoubleClosed.fibonacci(n);
        long   fibL = (long) fibD;
        Sx.format("FibonacciDoubleClosed(%d): %f  %d\n", n, fibD, fibL);
        
        
        test_time_firstN(new FibonacciLongIterate() , size, fibs);
        test_time_firstN(new FibonacciLongMemoized(), size, fibs);
        test_time_firstN(new FibonacciBigIntMemo()  , size, bigs);
        test_time_firstN(new FibonacciDoubleMemo()  , size, dbls);
        test_time_firstN(new FibonacciDoubleClosed(), size, dbls);
        test_time_firstN(new FibonacciBigIntMemo()  , bigN, bigs);

        if (lvl > 1) {
            size = 42;
            test_time_firstN(new FibonacciLongRecurse(),    size, fibs);
            test_time_firstN(new FibonacciLongIterate(),    size, fibs);
            test_time_firstN(new FibonacciLongMemoized(),   size, fibs);
            test_time_firstN(new FibonacciBigIntMemo(), size, bigs);
        }        
        
        Sx.puts(testName + " END");
        return 0;
    }
    
    public static void main(String[] args) { unit_test(1); }
}


