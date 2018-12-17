package sprax.arrays;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Context: Parkour over an arbitrary line-up of obstacles.  Confronted 
 * by a series of walls, you try to compute how to run, climb, or jump
 * over them, in th order they appear, in the most efficient series of
 * moves possible.
 * 
 * Question: Find the minimum number of parkour moves needed to traverse
 * an array of pairs (which may also be represented as a pair of arrays).
 * At each array index, you get a pair of values, which we could call
 * height and energy.  You can picture this array as representing a 
 * series of wall-like obstacles.  It takes a certain amount of energy
 * to jump or climb to the top of a wall, which is proportional to its
 * height.  But once on top, you find a spring-board or catapult or 
 * human cannon or something that gives you some other amount of energy
 * that you can use to go forward, and upward, if necessary, to get to
 * the top of the next wall -- or, if you have enough energy, to jump
 * over the next wall and get to the top or the near side of a later
 * wall.  
 * The height of the walls and the energy its takes to make any move
 * are thus measured in the same units.
 * It takes one unit of energy to go forward one horizontal distance
 * unit, and one unit to go up one height unit, but zero units to jump
 * downward.
 * 
 * You can keep or use any excess energy from previous moves ("momentum")
 * until it is used up.  The "boost energy" you get at the top of each
 * wall can be used repeatedly until you have moved to a later wall.
 * In short, "boost" is re-usable; "momentum" is not.
 * 
 * If your current energy is less than the height increase of the 
 * next wall, and your current location's boost is positive, you will
 * need to re-use that boost in more than one move to surmount that 
 * next wall.  But if the local boost is zero or less, then you are
 * stuck and cannot progress.  That's game over.
 * 
 * For example, let's say you bring excess energy 3 to the top of some
 * wall K, which then gives you boost energy 4.  Your current energy
 * becomes 3 + 4 = 7.  If that wall K's height is 20 and the next wall's
 * is 30, you are 3 units short of 10 = 30 - 20, so it will take you two
 * boosted moves to surmount wall K+1.  You could stop there with an
 * excess of 1 unit (3 + 2*4 - 10), and add to that 1 whatever boost
 * you find there.  BUT, if the height of the *next* wall after that,
 * at index K+2, is <= 30, you could choose to use your 1 unit excess
 * to go one *more* unit of distance, and land on top of wall K+2
 * with 0 excess, and pick up the boost there instead.
 * 
 * In other words, instead of just climbing to the top of the wall
 * K+1 and stopping there, you can use your last move's momentum to
 * jump over K+1 and land on wall K+2.
 * If boost(K+2) - boost(K+2) > 1, the extra energy you would get
 * by choosing to land on wall K+2 would work to your advantage.
 *
 * Further examples, where the obstacle course is represented by
 * a list of pairs of the form (height, boost):
 *
 *                         0       1       2       3       4
 * Flat land, 4 moves: [(0, 1), (0, 1), (0, 1), (0, 1), (0, 0)]
 * Up stairs, 4 moves: [(0, 2), (1, 2), (2, 2), (3, 2), (4, 0)]
 * Step down, 4 moves: [(4, 1), (3, 1), (2, 1), (1, 1), (0, 0)]
 * Jump down, 2 moves: [(4, 2), (3, 0), (2, 2), (2, 0), (0, 0)] (skip 1 & 3)
 * Leap down, 1 moves: [(4, 4), (3, 0), (2, 0), (2, 0), (1, 0)] (skip 1,2,3)
 * Up & down, 3 moves: [(0, 4), (3, 5), (6, 0), (4, 1), (0, 0)] (skip K = 2)
 * 
 * H/Index  0   1   2   3   4   5   6   7   8   9   10  11  12  13  14  15
 * 7                                                              _____
 * 6                                      _____                   |
 * 5                                      |   |       _____       |
 * 4                      _____           |   |   ____|   |   ____|
 * 3                  ____|   |           |   |___|       |   |
 * 2                  |       |___    ____|               |   |
 * 1          _____   |           |___|                   |   |
 * 0      ____|   |___|                                   |___|   
 * H/Boost  2   3   0   B   B   B   B   B   B   B   B   B   B   B   B
 * 
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
