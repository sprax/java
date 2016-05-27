package sprax.arrays;

import sprax.Sx;

/**
 * Question:
 * Answers:
 */
public abstract class ArrayHopper
{
	/** The "interface" method: */
	public abstract int countHops(int[] iA);
	
	/** Show iterations or other measures of complexity */
	protected abstract void showCounts();
}

abstract class ArrayHopperRecursive extends ArrayHopper
{
    protected long mCalls;
    protected long mDives;

    @Override
    protected void showCounts()
    {
    	Sx.puts("calls: " + mCalls + "  dives: " + mDives);
    }
}

abstract class ArrayHopperWithAuxArray extends ArrayHopper
{
    protected int mMinHops[];
    
    ArrayHopperWithAuxArray(int[] inputArray)
    {
        if(inputArray == null || inputArray.length < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " needs non-null, non-empty input array");

        mMinHops = new int[inputArray.length];
    }
}



/** 
 * Naive: 
 */
class ArrayHopperGreedyRecurseForward extends ArrayHopperRecursive
{
    @Override
	public int countHops(int[] iA)
	{
		assert(iA != null);
		int length = iA.length;
		if (length < 1)
			return 0;

		mCalls = 0;
		mDives = 0;
		int count = countHopsGreedyRecurse(iA, length, 0, 0, Integer.MAX_VALUE - 1);
	    return count;
	}

	int countHopsGreedyRecurse(int[] iA, int len, int pos, int numHopsNow, int minNumHops)
	{
	    mCalls++;
	    
		if (numHopsNow > minNumHops)
			return minNumHops;

		if (pos >= len)
		{
			return numHopsNow;
		}
		mDives++;
		for (int hopSize = iA[pos]; hopSize > 0; hopSize--)
		{
			int numHops = countHopsGreedyRecurse(iA, len, pos + hopSize, numHopsNow + 1, minNumHops);
			if (minNumHops > numHops)
				minNumHops = numHops;
		}
		
		return minNumHops;
	}
	
}

/** 
 * Naive: Repeats many steps repeatedly
 */
class ArrayHopperRecurseBreadthFirst extends ArrayHopperRecursive
{
    @Override
    public int countHops(int[] iA)
    {
        assert(iA != null);
        int length = iA.length;
        if (length < 2)
            return 0;
        
		mCalls = 0;
		mDives = 0;
        for (int maxHops = 1; maxHops < length; maxHops++)
        {
            int minHops = countHopsRBF(iA, length, 0, 0, maxHops);
            if (minHops < Integer.MAX_VALUE)
                return minHops;
        }
        return Integer.MAX_VALUE;
    }
    
    int countHopsRBF(int[] iA, int length, int pos, int hops, int maxHops)
    {
    	assert(pos < length);
    	mCalls++;
    	
        int nowHop = hops + 1;
        if (nowHop > maxHops)
        	return Integer.MAX_VALUE;
        
        int maxHopSize = iA[pos];
        if (pos + maxHopSize >= length)
            return nowHop;
        
        mDives++;
        for (int hopSize = 0; ++hopSize <= maxHopSize; )
        {
            int minHops = countHopsRBF(iA, length, pos + hopSize, nowHop, maxHops);
            if (minHops < Integer.MAX_VALUE)
                return minHops;
        }
        return Integer.MAX_VALUE;
    }
}

class ArrayHopperDP extends ArrayHopperWithAuxArray
{

	ArrayHopperDP(int[] inputArray) {
		super(inputArray);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int countHops(int[] iA) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void showCounts() {
		// TODO Auto-generated method stub
		
	}
	
}

class ArrayHopperTest
{   
	public static int test_ArrayHopper(ArrayHopper arrayHopper, int[] iA)
	{
		String className = arrayHopper.getClass().getSimpleName();
		int minNumHops = arrayHopper.countHops(iA);
		Sx.format("%s.countHops(...) returned %d\t", className, minNumHops);
		arrayHopper.showCounts();
		return 0;
	}

	public static void testHoppers(ArrayHopper[] hoppers, int[] iA)
	{
		for (ArrayHopper hopper : hoppers)
		{
	        test_ArrayHopper(hopper, iA);
		}
	}
	
	public static int unit_test(int lvl) 
	{
		String  testName = ArrayHopper.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN");    

        int iA[] = { 1, 2, 2, 0, 3, 0, 0, 2 }; // expected answer: 6
        int iB[] = { 9, 9, 7, 6, 5, 4, 3, 2, 1, 0 }; // expected answer: 3
        int iC[] = { 9, 9, 7, 6, 5, 4, 3, 2, 1, 0, 9, 9, 7, 6, 5, 4, 3, 2, 1, 0 }; // expected answer: 3
        int aiA[][] = { iA, iB, iC };

		ArrayHopper hopperGRF = new ArrayHopperGreedyRecurseForward();
		ArrayHopper hopperRBF = new ArrayHopperRecurseBreadthFirst();
		ArrayHopper hopperNDP = new ArrayHopperDP(iA);
		
		ArrayHopper hoppers[] = { hopperGRF, hopperRBF, hopperNDP };

        Sx.putsArray("iA: ", iA);
        testHoppers(hoppers, iA);

        Sx.putsArray("iB: ", iB);
        testHoppers(hoppers, iB);
        
        Sx.putsArray("iC: ", iC);
        testHoppers(hoppers, iC);

		Sx.puts(testName + " END");    
		return 0;
	}

	public static void main(String[] args) { unit_test(1); }    
}
