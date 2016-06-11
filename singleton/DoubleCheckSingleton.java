package sprax.singleton;

import sprax.sprout.Sx;

public class DoubleCheckSingleton
{
    private static DoubleCheckSingleton instance;
    
    private DoubleCheckSingleton()
    {
    }
    
    @Override
    protected Object clone()
    {
        throw new IllegalStateException("No clones");
    }
    
    public static DoubleCheckSingleton getInstance()
    {
        if (instance == null)
        {
            synchronized (DoubleCheckSingleton.class)
            {
                if (instance == null)
                {
                    instance = new DoubleCheckSingleton();
                }
            }
        }
        return instance;
    }
    
    public void putsName()
    {
        Sx.puts(this.getClass().getName() + " is the name of this class.");
    }
    
    public static void unit_test()
    {
        String testName = BillPughSingleton.class.getName() + ".unit_test";
        Sx.puts(testName + ": BEGIN");
        getInstance().putsName();
        Object cloneA = null;
        try {
            cloneA = getInstance().clone();
        } catch (Exception ex) {
            Sx.puts("Caught exception: " + ex);
        }
        Sx.puts("The clone is: " + cloneA);
        
        Sx.puts(testName + ": END");
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}