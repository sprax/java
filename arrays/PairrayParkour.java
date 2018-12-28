package sprax.arrays;

import java.util.ArrayList;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Context: Parkour over an arbitrary line-up of obstacles.  Confronted
 * by a series of walls, you try to compute how to run, climb, or jump
 * over all of them, in the order they appear, in the most efficient
 * series of moves possible.
 *
 * Question: Find the minimum number of parkour moves needed to traverse
 * to the end of an array of pairs (which may also be represented as a
 * pair of arrays).  You can picture this array as representing a series
 * of wall-like obstacles, one at each index.  So at each array index,
 * you get a pair of values.  Let's call them "height" and "boost."
 * It takes a certain amount of energy to jump or climb to the top of
 * a wall, which is proportional to its height.  But once on top, you
 * find a spring-board, zip-line, catapult, jet pack, or whatever, and
 * it gives you some additional amount of energy -- that's the boost.
 * You can use this boost to go forward and upward to get to the top of
 * the next wall -- or, if you have enough energy, to jump over it and
 * get to the top or the near side of a later wall.
 * 
 * So height and boost are both measured in units of energy.
 * Horizontal distance can also be converted to energy as one unit per
 * index.  It takes one unit of energy to go forward or backward one
 * array index, and one unit to go up one height unit,
 * but zero units to jump downward.  So it takes 2 units to go up one
 * step (1 forward, 1 upward), but only one unit to go down a step 
 * (1 forward, 0 downward).
 *
 * You can keep or use any excess energy from previous moves ("momentum")
 * until it is used up.  The boost that you get at the top of each
 * wall can be used repeatedly until you have moved to the top of a later
 * wall.  In short, "boost" is re-usable; "momentum" is not.
 *
 * If your current energy is less than the relative height of the
 * next wall, and your current location's boost is positive, you will
 * need to re-use that boost in more than one move to surmount that
 * next wall.  But if the local boost is zero or less, then you are
 * stuck and cannot progress.  That's game over -- you lose.
 *
 * For example, let's say you bring excess energy 3 to the top of some
 * wall at index K, which then gives you boost energy 4.  Your current
 * energy becomes 3 + 4 = 7.  If wall height is 20 and the next wall's
 * is 30, you are 3 units short of 10 = 30 - 20, so it will take you two
 * boosted moves to surmount wall K+1.  You *could* stop there with an
 * excess of 1 unit (3 + 2*4 - 10), and add to that 1 to whatever boost
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
 * Furthermore, you can stick to walls like Spider-Man, which means
 * that if you have current energy M, you can jump across a trough
 * of any width < M and begin climbing the wall you at the same
 * height from which you jumped.  But you only pick up boost
 * energy from the top of a wall, not from the sides.  Even though
 * you can re-use it for climbing the side of a wall, you must
 * have landed on the flat just before the wall to have picked
 * it up in the first place.
 *
 * You start at index 0, and the "end" of array A is at A.length (that is,
 * one address *after* the last value in the array (so "end" is like the
 * end() method in STL or C++'s standard collections).
 *
 * Numerical examples, where the obstacle course is represented by
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
 * H/Boost  3
 * 8                                                                  _____
 * 7                                                              ____|   |
 * 6                                      _____                   |       |
 * 5                                      |   |       _____       |       |____
 * 4                      _____           |   |   ____|   |   ____|           |
 * 3                  ____|   |       ____|   |___|       |   |               |
 * 2                  |       |___    |                   |   |               |
 * 1          _____   |           |___|                   |   |               |____
 * 0       ___|   |___|                                   |___|                   |
 * Height   0   1   0   3   4   2   1   3   6   3   4   5   0   4   7   8   5   1
 *   Index  0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20
 */
public abstract class PairrayParkour
{
	/** The "interface" method: */
	public abstract int countHops();

	/** Show iterations or other measures of complexity */
	protected abstract void showCounts();
	
    // base class Constructor
    protected PairrayParkour(int[] heights, int[] boosts)
    {
        if (heights == null || heights.length < 1 || boosts == null || boosts.length != heights.length)
            throw new IllegalArgumentException(this.getClass().getSimpleName()
            		+ " needs non-null, non-empty, same-size input arrays");
        mBoosts = boosts;
        mHeights = heights;
        mLength = heights.length;
    }
    
    protected int mLength, mMinMoves;
    protected int mHeights[], mBoosts[];
    protected ArrayList<Integer> mMinPath;
}


abstract class PairrayParkourRecursive extends PairrayParkour
{
    protected PairrayParkourRecursive(int[] heights, int[] boosts) {
		super(heights, boosts);
	}

	protected int mCalls;  // times recursive method is called
    protected int mLoops;  // times recursive method begins a loop of calling itself (loops <= calls)

    @Override
    protected void showCounts()
    {
        Sx.puts("calls: " + mCalls + "  loops: " + mLoops);
    }
}


abstract class PairrayParkourWithAuxArrays extends PairrayParkour
{
    protected int mMinHops[];

    // base class Constructor
    protected PairrayParkourWithAuxArrays(int[] heights, int[] boosts)
    {
		super(heights, boosts);
        mMinHops = new int[mLength];
    }
}


/**
 * Naive: Re-tries steps and may have to back-track out from greed.
 */
class PairrayParkourGreedyRecurseForward extends PairrayParkourRecursive
{
    protected PairrayParkourGreedyRecurseForward(int[] heights, int[] boosts) {
		super(heights, boosts);
	}

	@Override
	public int countHops()
	{
		// reset counts
		mCalls = 0;
		mLoops = 0;
		return countHopsGreedyRecurse(0, 0, 0, Integer.MAX_VALUE - 1);
	}

	int countHopsGreedyRecurse(int pos, int xse, int numHopsNow, int minNumHops)
	{
	    mCalls++;

		if (numHopsNow > minNumHops)
			return numHopsNow;           // return failure ASAP

		if (pos >= mLength)
		{
			return numHopsNow;           // return success
		}
		mLoops++;
//		for (int energy = xse + mBoosts[pos]; energy > 0; energy--)
//		{
//			if (energy < mHeights)
//			
//			int numHops = countHopsGreedyRecurse(pos + hopSize, numHopsNow + 1, minNumHops);
//			if (minNumHops > numHops)
//				minNumHops = numHops;
//		}
		return minNumHops;
	}
}

/**
 * Naive: Repeats many steps
 */
class PairrayParkourRecurseBreadthFirst extends PairrayParkourRecursive
{
    protected PairrayParkourRecurseBreadthFirst(int[] heights, int[] boosts) {
		super(heights, boosts);
	}

	@Override
    public int countHops()
    {
		mCalls = 0;
		mLoops = 0;
		ArrayList<Integer> path = new ArrayList<Integer>();
	    int minHops = countHopsRBF(0, 0, 0, Integer.MAX_VALUE, path);
	    Sx.putsArray("mMinPath: ", mMinPath);
	    return minHops;
    }
	
 
    int countHopsRBF(int idx, int xse, int hops, int minSoFar, ArrayList<Integer> path)
    {
        assert(idx < mLength);
        mCalls++;
    	////Sx.format("CALLED M=%d,  idx %d,  xse %d,  hops %d,  msf %d\n", mCalls, idx, xse, hops, minSoFar);
        

        int hopsNow = hops + 1;
        if (hopsNow > minSoFar)	{		// A shorter path was already found.
        	Sx.format("RETURN BEG FUNC, M=%d, idx=%d, xse=%d, MAX=%d\n", mCalls, idx, xse, Integer.MAX_VALUE);
        	return Integer.MAX_VALUE;	// So return "infinite" signal.
        }
        if (hops != path.size()) {
        	Sx.format("!!!!!!!!!!!!!!!!!!!!!!!!!! hops %d != %d path.size\n", hops, path.size());
        }
        //assert(hops == path.size());
		path.add(idx);
		int begSize = path.size();
        
       	mLoops++;
        int heightNow = mHeights[idx];
        int j = 0;
        for (int energy = xse + mBoosts[idx], pos = idx + 1; --energy >= 0; pos++)
        {
            ////Sx.format("LOOP_J M=%2d, %2d, hops=%d, idx=%d, xse=%d, pos=%d  energy=%d\n", mCalls, j++, hopsNow, idx, xse, pos, energy);
        	if (pos >= mLength) {
               	Sx.format("RETURN BEG LOOP, M=%d, hops=%d, idx=%d, xse=%d, energy=%d. ", mCalls, hopsNow, idx, xse, energy);
               	Sx.putsArray("PATH: ", path);
            	//mMinPath = new ArrayList<Integer>(path);		// copy the new minimal path
            	mMinPath = new ArrayList<Integer>();		// copy the new minimal path
            	for (Integer elt : path)
            		mMinPath.add(elt);		// copy the new minimal path
               	
                return hopsNow;			// arrived at the end!   Return how many moves it took.
        	}
        	int heightInc = mHeights[pos] - heightNow;
        	if (heightInc < 0)
        		heightInc = 0;
        	if (heightInc > energy) {
        		if (mBoosts[idx] <= 0) {
                	Sx.format("RETURN MAX, energy %d at idx %d\n", energy, idx);
                   	path.remove(path.size() - 1);
         			return Integer.MAX_VALUE;	// dead end: cannot jump or climb the top
        		}
        		heightNow += energy;	// use up all energy before re-using boost to climb
        		heightInc -= energy;	// remaining vertical distance to the top
        		hopsNow += heightInc / mBoosts[idx];	// how many more boosted climbing moves to the top
        		xse = heightInc % mBoosts[idx];			// excess energy upon arrival at the top
        		energy = 0;
        	} else if (heightInc == energy) {
        		xse = energy = 0;
        		// Sx.format("energy %d => 0 xse\n", energy);
        	} else {
        		xse = energy - heightInc;
        	}
            int numHops = countHopsRBF(pos, xse, hopsNow, minSoFar, path);
        	////Sx.format("result M=%d, numHops=%d  hopsNow=%d at idx=%d,  energy=%d\n", mCalls, numHops, hopsNow, idx, energy);
            if (minSoFar > numHops) {
            	minSoFar = numHops;								// save the new minimum
               	//// Sx.putsArray("BEST PATH SO FAR: ", path);
            	////	return minSoFar;	// too greedy!
            }
           	////Sx.format(">>>>>>>>>>>>>>>: PRET: ");
           	////Sx.putsArray(path);
        	path.subList(begSize, path.size()).clear();
           	////Sx.format("<<<<<<<<<<<<<<<: POST: ");
           	////Sx.putsArray(path);
        }
    	////Sx.format("RETURN M=%d, END msf=%d, energy %d at idx %d\n", mCalls, minSoFar, xse, idx);
        return minSoFar;
    }
}

class PairrayParkourDynamicProgrammingFwd extends PairrayParkourWithAuxArrays
{
    protected long mAssigns;  // upper bound on the number of assignments to aux array

    PairrayParkourDynamicProgrammingFwd(int heights[], int boosts[]) {
		super(heights, boosts);
	}

	@Override
	public int countHops()
	{
	    mAssigns = 0;

	    // init aux array
	    for (int j = 1; j < mMinHops.length; j++) {     // mMinHops[0] remains 0
	        mMinHops[j] = Integer.MAX_VALUE - 1;
	    }

	    // init conditions: first hop is special
	    mAssigns = mMinHops.length + mHeights[0];			   	// worst case is "expected" usual case
        for (int pos = mHeights[0]; pos > 0; pos--) {        	// mMinHops[0] remains 0
            if (pos >= mHeights.length)
                return 1;                              	// reached the goal in one hop
            mMinHops[pos] = 1;
        }

	    for (int j = 1; j < mHeights.length; j++) {
	        int maxPos = j + mHeights[j];
	        int hopNum = 1 + mMinHops[j];              	// 1 more than min num hops it took to get here.
	        for (int pos = maxPos; pos > j; pos--) {   	// mMinHops[0] remains 0
	            if (pos >= mHeights.length) {
	            	mAssigns += maxPos - pos;
	                return hopNum;                     	// off the end in one more hop
	            }
	            if (mMinHops[pos] > hopNum)
	                mMinHops[pos] = hopNum;
	        }
	        mAssigns += mHeights[j];						    // still here
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
	public static int test_PairrayParkour(PairrayParkour arrayParkour, int heights[], int boosts[], int expectedMinNumHops)
	{
		String className = arrayParkour.getClass().getSimpleName();
		int minNumHops = arrayParkour.countHops();
		Sx.format("%s.countHops(...)\t hops: %d\t", className, minNumHops);
		arrayParkour.showCounts();
		return Sz.showWrong(minNumHops, expectedMinNumHops);
	}

	public static int testParkours( PairrayParkour[] parkours, int heights[], int boosts[]
								  , int expMinMoves, int expMinJumps)
	{
	    int numWrong = 0;
		for (PairrayParkour parkour : parkours)
		{
	        numWrong += test_PairrayParkour(parkour, heights, boosts, expMinMoves);
		}
		return numWrong;
	}

	public static int unit_test(int lvl)
	{
		String  testName = PairrayParkour.class.getName() + ".unit_test";
		Sz.begin(testName);
		int numWrong = 0;

        int hA[] = { 1, 2, 3 }; // expected Parkour answer: 3, only one way
        int bA[] = { 2, 2, 2 }; // expected Ahopper answer: 2 (first move 1, not 2)

        int hB[] = { 1, 2, 2, 1 }; // expected Parkour answer: 3 (2 ways, via index 2 or 3)
        int bB[] = { 2, 2, 1, 1 }; // expected Ahopper answer: 3 (2 ways, via index 1 or 2)

        int hC[] = { 0, 2, 1, 2, 1, 3, 2, 4 }; // expected answer: 4
        int bC[] = { 4, 1, 1, 4, 0, 2, 1, 1 }; // expected answer: 3
        int pC[] = { 0, 1, 3, 6 };

        int hD[] = { 9, 9, 7, 6, 5, 4, 3, 2, 1, 0, 9, 9, 7, 6, 5, 4, 3, 2, 1, 0 }; // expected answer: ?
        int bD[] = { 9, 9, 7, 6, 5, 4, 3, 2, 1, 0, 9, 9, 7, 6, 5, 4, 3, 2, 1, 0 }; // expected answer: ?

        int pairs[][] = { hA, bA, hB, bB, hC, bC, hD, bD};
        int expectP[] = { 3, 3, 5, 0 };
        int expectH[] = { 2, 3, 3, 0 };

        int begTrial = 0, endTrial = begTrial + 2; // expectP.length;
		for (int j = begTrial; j < endTrial; j++) {
            int heights[] = pairs[2*j];
            int boosts[]  = pairs[2*j + 1];
            Sx.putsArray("heights: ", heights);
            Sx.putsArray("boosts:  ", boosts);
            PairrayParkour ParkourGRF = new PairrayParkourGreedyRecurseForward(heights, boosts);
            PairrayParkour ParkourRBF = new PairrayParkourRecurseBreadthFirst(heights, boosts);
            PairrayParkour ParkourNDP = new PairrayParkourDynamicProgrammingFwd(heights, boosts);
            PairrayParkour parkours[] = {
        	//	ParkourGRF,
        	    ParkourRBF,
        	//	ParkourNDP,
        	};
            numWrong += testParkours(parkours, heights, boosts, expectP[j], expectH[j]);
        }

        Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args) { unit_test(1); }
}
