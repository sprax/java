package sprax.singleton;

import java.io.ObjectStreamException;

import sprax.Sx;

// From an idea by Joshua Bloch
public enum EnumSingleton
{
	INSTANCE;

	public static EnumSingleton getInstance() 
	{
		return INSTANCE;
	}

	public void putsName()
	{
		Sx.puts(this.getClass().getName() + " is the name of this class.");
	}

	/**
	 * If the singleton implements Serializable, then this method 
	 * must be supplied to prevent multiple instantiation via IO.
	 */
	private Object readResolve() throws ObjectStreamException
	{
		return getInstance();
	}

	public static void unit_test() 
	{
		String testName =  EnumSingleton.class.getName() + ".unit_test";
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


// subclass?  No:
// The type EnumSingleton cannot be the superclass of SubEnumSingleton; a superclass must be a class.
// class SubEnumSingleton extends EnumSingleton {}
