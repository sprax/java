package sprax.builders;

import java.util.Objects;
import java.util.function.Supplier;

import sprax.Sx;
import sprax.Sz;

class BitsComp<T>
{    
    private final Supplier<? extends T> ctor;
    
    private T                           field;
    
    /** T must have a default constructor. */
    BitsComp(Supplier<? extends T> ctor)
    {
        this.ctor = Objects.requireNonNull(ctor);
    }
    
    public T myMethod()
    {
        return field = ctor.get();
    }
    
    public static Integer newIntZero()
    {
        return new Integer(0);
    }
    
    long comp(T argT) {
        return (long)field & (long)argT;
    }
}

public class Builders
{
    public static void unit_test()
    {
        String testName = Builders.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        BitsComp<Integer> ibc = new BitsComp<>(BitsComp::newIntZero);
        Integer field = ibc.myMethod();
        Sx.format("field has value: %d\n", field);
        
        Sz.end(testName, 0);
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
