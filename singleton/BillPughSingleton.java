package sprax.singleton;

import sprax.Sx;

public class BillPughSingleton {
 
    private BillPughSingleton(){}
    @Override
	protected Object clone() { throw new IllegalStateException("No clones"); }
     
    private static class SingletonHelper{
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }
     
    public static BillPughSingleton getInstance(){
        return SingletonHelper.INSTANCE;
    }
    
    public void putsName()
    {
    	Sx.puts(this.getClass().getName() + " is the name of this class.");
    }
    
    
    public static void unit_test() 
    {
        String testName =  BillPughSingleton.class.getName() + ".unit_test";
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

    public static void main(String[] args) { unit_test(); }
}