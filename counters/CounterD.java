package sprax.counters;

import sprax.sprout.Sx;


interface ICounter<T extends Number>
{
    public T count();
}

public class CounterD implements ICounter<Double>
{
    // Member Data:
    public  double mIncrement;
    private double mCount;       // initially 0

    @Override
    public Double count() 
    {
        mCount = mCount + mIncrement;
        return mCount;
    }
    // Constructors:
    CounterD(double inc, double init_count) {
        mIncrement = inc;
        mCount = init_count;
    }
    CounterD(double inc) {
        this(inc, 0.0);
    }
    CounterD() {
        this(1.0, 0.0);
    }

    public static int unit_test(int lvl)
    {   
        String testName = CounterD.class.getName() + ".unit_test";  
        Sx.puts(testName + " BEGIN");
        
        Sx.puts(testName + " END");
        return 0;
    }
    
    public static void main(String[] args) { unit_test(1); }
}


