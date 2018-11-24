package sprax.counters;

import java.math.BigInteger;
import java.util.ArrayList;

import sprax.sprout.Sx;


interface ICounter<T extends Number>
{
    public T count();
}

public class Counter<T extends Number> //implements ICounter<Double>
{
    //@Override
    public T count() 
    {
        mCount = mCount + mIncrement;
        return mCount;
    }
    
    public  T mIncrement;
    private T mCount;       // initially 0
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


