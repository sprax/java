package sprax.arrays;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Problems: Parkour over arrays of pairs.
 * Question: Find the minimum number of parkour moves needed to traverse
 * an array of pairs (which may also be represented as a pair of arrays).
 * At each array index, you get two values: your maximum horizontal jump
 * distance, and your maximum vertical jump limit.  
 * Each array value gives the max size of the next step, but you may also take a
 * smaller step.  Usually the arrays are specified to be non-negative.
 */
public abstract class PairrayParkour
{
	/** The "interface" method: */
	public abstract int countHops(int[] iA);

	/** Show iterations or other measures of complexity */
	protected abstract void showCounts();
}

abstract class PairrayParkourRecursive extends PairrayParkour
{
    protected long mCalls;  // times recursive method is called
    protected long mLoops;  // times recursive method begins a loop of calling itself (loops <= calls)

    // No constructor

    @Override
    protected void showCounts()
    {
        Sx.puts("calls: " + mCalls + "  loops: " + mLoops);
    }
}

abstract class PairrayParkourWithAuxArray extends PairrayParkour
{
    protected int mMinHops[];

    // base class Constructor
    protected PairrayParkourWithAuxArray(int[] inputArray)
    {
        if (inputArray == null || inputArray.length < 1)
            throw new IllegalArgumentException(this.getClass().getSimpleName()
            		+ " needs non-null, non-empty input array");
        mMinHops = new int[inputArray.length];
    }
}


/**
 * Naive: Re-tries steps and may have to back-track out from greed.
 */
class PairrayParkourGreedyRecurseForward extends PairrayParkourRecursive
{
    @Override
	public int countHops(int[] iA)
	{
		assert(iA != null);
		int length = iA.length;
		if (length < 1)
			return 0;

		// reset counts
		mCalls = 0;
		mLoops = 0;
		int count = countHopsGreedyRecurse(iA, length, 0, 0, Integer.MAX_VALUE - 1);
	    return count;
	}

	int countHopsGreedyRecurse(int[] iA, int len, int pos, int numHopsNow, int minNumHops)
	{
	    mCalls++;

		if (numHopsNow > minNumHops)
			return numHopsNow;           // return failure ASAP

		if (pos >= len)
		{
			return numHopsNow;           // return success
		}
		mLoops++;
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
 * Naive: Repeats many steps
 */
class PairrayParkourRecurseBreadthFirst extends PairrayParkourRecursive
{
    @Override
    public int countHops(int[] iA)
    {
        assert(iA != null);
        int length = iA.length;
        if (length < 2)
            return 0;

		mCalls = 0;
		mLoops = 0;
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

        mLoops++;
        for (int hopSize = 0; ++hopSize <= maxHopSize; )
        {
            int minHops = countHopsRBF(iA, length, pos + hopSize, nowHop, maxHops);
            if (minHops < Integer.MAX_VALUE)
                return minHops;
        }
        return Integer.MAX_VALUE;
    }
}

class PairrayParkourDynamicProgramming extends PairrayParkourWithAuxArray
{
    protected long mAssigns;  // upper bound on the number of assignments to aux array

    PairrayParkourDynamicProgramming(int[] inputArray) {
		super(inputArray);
	}

	@Override
	public int countHops(int[] iA)
	{
	    mAssigns = 0;

	    // sanity check
	    if (iA == null || iA.length < 1 || iA[0] < 1)
	        return Integer.MAX_VALUE;

	    // init aux array
	    if (mMinHops.length < iA.length)
	        mMinHops = new int[iA.length];
	    for (int j = 1; j < mMinHops.length; j++) {     // mMinHops[0] remains 0
	        mMinHops[j] = Integer.MAX_VALUE;
	    }

	    // init conditions: first hop is special
	    mAssigns = mMinHops.length + iA[0];			   	// worst case is "expected" usual case
        for (int pos = iA[0]; pos > 0; pos--) {        	// mMinHops[0] remains 0
            if (pos >= iA.length)
                return 1;                              	// reached the goal in one hop
            mMinHops[pos] = 1;
        }

	    for (int j = 1; j < iA.length; j++) {
	        int maxPos = j + iA[j];
	        int hopNum = 1 + mMinHops[j];              	// 1 more than min num hops it took to get here.
	        for (int pos = maxPos; pos > j; pos--) {   	// mMinHops[0] remains 0
	            if (pos >= iA.length) {
	            	mAssigns += maxPos - pos;
	                return hopNum;                     	// off the end in one more hop
	            }
	            if (mMinHops[pos] > hopNum)
	                mMinHops[pos] = hopNum;
	        }
	        mAssigns += iA[j];						    // still here
	    }
		return 0;
	}

	@Override
	protected void showCounts() {
        Sx.puts("aux assigns: " + mAssigns);
	}

}

class PairrayParkourTest
{
	public static int test_PairrayParkour(PairrayParkour arrayParkour, int[] iA, int expectedMinNumHops)
	{
		String className = arrayParkour.getClass().getSimpleName();
		int minNumHops = arrayParkour.countHops(iA);
		Sx.format("%s.countHops(...)\t hops: %d\t", className, minNumHops);
		arrayParkour.showCounts();
		return Sz.showWrong(minNumHops, expectedMinNumHops);
	}

	public static int testParkours(PairrayParkour[] parkours, int[] iA, int expectedMinNumHops)
	{
	    int numWrong = 0;
		for (PairrayParkour parkour : parkours)
		{
	        numWrong += test_PairrayParkour(parkour, iA, expectedMinNumHops);
		}
		return numWrong;
	}

	public static int unit_test(int lvl)
	{
		String  testName = PairrayParkour.class.getName() + ".unit_test";
		Sz.begin(testName);
		int numWrong = 0;

        int iA[] = { 1, 2, 2, 0, 3, 0, 0, 2 }; // expected answer: 6
        int iB[] = { 9, 9, 7, 6, 5, 4, 3, 2, 1, 0 }; // expected answer: 3
        int iC[] = { 9, 9, 7, 6, 5, 4, 3, 2, 1, 0, 9, 9, 7, 6, 5, 4, 3, 2, 1, 0 }; // expected answer: 3
        //int aiA[][] = { iA, iB, iC };

		PairrayParkour ParkourGRF = new PairrayParkourGreedyRecurseForward();
		PairrayParkour ParkourRBF = new PairrayParkourRecurseBreadthFirst();
		PairrayParkour ParkourNDP = new PairrayParkourDynamicProgramming(iA);

		PairrayParkour parkours[] = { ParkourGRF, ParkourRBF, ParkourNDP };

        Sx.putsArray("iA: ", iA);
        numWrong += testParkours(parkours, iA, 5);

        Sx.putsArray("iB: ", iB);
        numWrong += testParkours(parkours, iB, 2);

        Sx.putsArray("iC: ", iC);
        numWrong += testParkours(parkours, iC, 4);

        Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args) { unit_test(1); }
}
