package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Context: Parkour over an arbitrary line-up of obstacles. Confronted by a
 * series of walls, you try to compute how to run, climb, or jump over all of
 * them, in the order they appear, in the most efficient series of moves
 * possible.
 *
 * Question: Find the minimum number of parkour moves needed to traverse to the
 * end of a series of wall-like obstacles. We can model such an obstacle course
 * using lists or arrays, one obstacle at each index. Each obstacle has two
 * properties, so we can use an array of pairs or a pair of arrays. Either way,
 * at each array index, you get a pair of values. Let's call them "height" and
 * "boost." It takes a fixed amount of energy to jump or climb to the top of a
 * wall, which is proportional to its height. If you have more energy, you can
 * jump or dynamically climb *over* a wall and keep going, but you still have to
 * expend the same amount of energy as you would to get to the top and stop
 * there, Once on top, though, you find a spring-board, zip-line, catapult, jet
 * pack, or whatever, and it gives you some additional amount of energy --
 * that's the boost. You can use this boost to go forward and upward to get to
 * the top of the next wall -- or, if you have enough energy, to jump over it
 * and get to the top or the near side of a later wall.
 * 
 * So height and boost are both measured in units of energy. Horizontal distance
 * can also be converted to energy as one unit per index. It takes one unit of
 * energy to go forward or backward one array index, and one unit to go up one
 * height unit, but zero units to jump downward. So it takes 2 units to go up
 * one step (1 forward, 1 upward), but only one unit to go down a step (1
 * forward, 0 downward).
 *
 * You can keep or use any excess energy from previous moves ("momentum") until
 * it is used up. The boost that you get at the top of each wall can be used
 * repeatedly until you have moved to the top of a later wall. In short, "boost"
 * is re-usable; "momentum" is not.
 *
 * Furthermore, you can stick to walls like Spider-Man, which means that if you
 * have current energy M, you can jump across a trough of any width < M and
 * begin climbing the wall you at the same height from which you jumped, plus
 * any excess energy you had at the start of the jump. But you only pick up
 * boost energy from the top of a wall, not from the sides. So the boost you can
 * re-use it for climbing the side of a wall comes from the top of the wall
 * where you jumped, not where you are now (i.e., not from the top of the wall
 * you are trying to surmount, nor necessarily from the flat just before the
 * wall you are climbing.
 *
 * Sometimes you will be forced to climb, not just run or jump. If your current
 * energy is less than the relative height of the next wall, and your current
 * location's boost is positive, you will need to re-use that boost in more than
 * one move to surmount that next wall. But if the local boost is zero or less,
 * then you are stuck and cannot progress. That's game over -- you lose.
 *
 * For example, let's say you bring excess energy 3 to the top of some wall at
 * index K, which then gives you boost energy 4. Your current energy becomes 3 +
 * 4 = 7. If this wall's height is 20 and the next wall's is 30, you are 3 units
 * short of 10 = 30 - 20, so it will take you two boosted moves to surmount wall
 * K+1.
 *
 * ALTERNATIVE A (implemented for reference): You *MUST* stop there with an
 * excess of 1 unit (3 + 2*4 - 10), and add to that 1 to whatever boost you find
 * there. In other words, if you are climbing a vertical wall, you cannot change
 * your direction to an angle low enough to cross a whole horizontal unit of
 * distance and get past the top of the wall to the next one. Instead, you must
 * land on the top before you can redirect your momentum forward instead of
 * downward.
 *
 * ALTERNATIVE B (not in effect, not implemented, seemingly harder to code, and
 * less physically realistic, because it implies climbing/leaping *through* walls):
 * You *could* stop there with an excess of 1 unit (3 + 2*4 - 10), and add to
 * that 1 to whatever boost you find there. BUT, if the height of the *next*
 * wall after that, at index K+2, is <= 30, you could choose to use your 1 unit
 * excess to go one *more* unit of distance, and land on top of wall K+2 with 0
 * excess. You would then start your next move from K+2 with only the boost
 * energy you find there. In other words, instead of just climbing to the top of
 * the wall K+1 and stopping there, you can use your last move's momentum to
 * jump over K+1 and land on wall K+2. If boost(K+2) - boost(K+2) > 1, the extra
 * energy you would get by choosing to land on wall K+2 would work to your
 * advantage.
 *
 * EITHER WAY: You start at index 0, and the "end" of array A is at A.length
 * (that is, one address *after* the last value in the array (so "end" is like
 * the end() method in STL or C++'s standard collections).
 *
 * Numerical examples, where the obstacle course is represented by a list of
 * pairs of the form (height, boost):
 *
 * <pre>
 * {@code
 *                         0       1       2       3       4
 * Flat land, 4 moves: [(0, 1), (0, 1), (0, 1), (0, 1), (0, 0)]
 * Up stairs, 4 moves: [(0, 2), (1, 2), (2, 2), (3, 2), (4, 0)]
 * Step down, 4 moves: [(4, 1), (3, 1), (2, 1), (1, 1), (0, 0)]
 * Jump down, 2 moves: [(4, 2), (3, 0), (2, 2), (2, 0), (0, 0)] (skip 1 & 3)
 * Leap down, 1 moves: [(4, 4), (3, 0), (2, 0), (2, 0), (1, 0)] (skip 1,2,3)
 * Up & down, 3 moves: [(0, 4), (3, 5), (6, 0), (4, 1), (0, 0)] (skip K = 2)
 * 
 * H/Boost  3   2   4   2   2   1   0   3   2   6   3   3   9   1   7   1   8   3   1   3   0
 * 10                                                                             _____
 * 9                                              _____                           |   |
 * 8                      _____                   |   |                       ____|   |   _____
 * 7                      |   |       _____       |   |           _________   |       |   |   |
 * 6                      |   |       |   |       |   |____       |       |   |       |   |   |
 * 5                      |   |       |   |       |       |       |       |   |       |___|   |
 * 4                      |   |       |   |   ____|       |   ____|       |   |               |
 * 3                  ____|   |       |   |___|           |   |           |___|               |
 * 2                  |       |___    |                   |   |                               |
 * 1          _____   |           |___|                   |   |                               |
 * 0       ___|   |___|                                   |___|                               |
 * Height   0   1   0   3   8   2   1   7   3   4   8   5   0   4   7   7   3   8  10   5   8
 *   Index  0   1   2   3   4   5   6   7   8   9  10  11  12  13  14  15  16  17  18  19  20
 * }
 * </pre>
 */
public abstract class PairrayParkour {
	/** The "interface" method: */
	public abstract int countHops();

	/** Show iterations or other measures of complexity */
	protected abstract void showCounts();

	// base class Constructor
	protected PairrayParkour(int[] heights, int[] boosts) {
		if (heights == null || heights.length < 1 || boosts == null || boosts.length != heights.length)
			throw new IllegalArgumentException(
					this.getClass().getSimpleName() + " needs non-null, non-empty, same-size input arrays");
		mBoosts = boosts;
		mHoists = heights;
		mLength = heights.length;
		mMinPath = new ArrayList<Integer>();
	}

	protected int mDebug = 1;
	protected int mLength, mMoves;
	protected int mHoists[], mBoosts[];
	protected ArrayList<Integer> mMinPath;
}

abstract class PairrayParkourRecursive extends PairrayParkour {
	protected PairrayParkourRecursive(int[] heights, int[] boosts) {
		super(heights, boosts);
	}

	protected int mCalls; // times recursive method is called
	protected int mLoops; // times recursive method begins a loop of calling itself (loops <= calls)

	@Override
	protected void showCounts() {
		Sx.puts("calls: " + mCalls + "  loops: " + mLoops);
	}

	protected void dbgs(String str) {
		Sx.debug(mDebug, "%s\n", str);
	}

	protected void dbgs() {
		Sx.debug(mDebug);
	}

	protected void dbgs(int nDbg, String formats, Object... args) {
		Sx.debug(nDbg, "%4d ", mCalls);
		Sx.debug(nDbg, formats, args);
	}

	protected void dbgs(final String formats, Object... args) {
		dbgs(mDebug, formats, args);
	}

	protected void dbgs(int nDbg, final ArrayList<Integer> path) {
		Sx.debugArray(nDbg, path);
	}

	protected void dbgs(int nDbg, final int[] path) {
		Sx.debugArray(nDbg, path);
	}

	protected void dbgs(ArrayList<Integer> path) {
		Sx.debugArray(mDebug, path);
	}

	protected void dbgs(int[] path) {
		Sx.debugArray(mDebug, path);
	}

}

abstract class PairrayParkourWithAuxArrays extends PairrayParkour {
	protected int mMinHops[];

	// base class Constructor
	protected PairrayParkourWithAuxArrays(int[] heights, int[] boosts) {
		super(heights, boosts);
		mMinHops = new int[mLength];
	}
}

///////////////////////////////////////////////////////////////////////////////

/**
 * Naive: Repeats many steps
 */
class PairrayParkourRecurseBreadthFirst extends PairrayParkourRecursive {
	protected PairrayParkourRecurseBreadthFirst(int[] heights, int[] boosts) {
		super(heights, boosts);
	}

	@Override
	public int countHops() {
		mDebug = 0;
		mCalls = 0;
		mLoops = 0;
		mMoves = Integer.MAX_VALUE;
		ArrayList<Integer> path = new ArrayList<Integer>();
		int minHops = countHopsRBF(0, 0, 0, path);
		if (minHops != mMinPath.size()) {
			// The last path tried need not be optimal, so this is OK:
			Sx.debug(mDebug-1, "NOTE: minHops %d != %d mMinPath.size\n", minHops, mMinPath.size());
		}
		assert(minHops == mMoves);
		Sx.debug(mDebug+2, "Moves %4d: ", mMoves);
		Sx.debugArray(mDebug+2, mMinPath);
		return mMoves;
	}


	int countHopsRBF(int idx, int xse, int hops, ArrayList<Integer> path)
	{
		// Count this call and check arguments:
		int myCall = mCalls++;
		assert(idx < mLength);
		assert(hops == path.size());

		dbgs("");
		dbgs(mDebug+1, "BEG: idx %d, xse %d, hgt %d, bst %d, msf %d, hops %d\t path: "
				, idx, xse, mHoists[idx], mBoosts[idx], mMoves, hops);
		dbgs(mDebug+1, path);

		// Short circuit if this path would be longer than the min already found:
		int hopsBeg = hops + 1;
		if (hopsBeg > mMoves) { // A shorter path was already found.
			dbgs("CUT: idx %d AT TOP BECAUSE hops %d > %d mMoves,  path: ", idx, hopsBeg, mMoves);
			dbgs(path);
			return Integer.MAX_VALUE;
		}

		// Look for a new path to the end:
		int hopsEnd = Integer.MAX_VALUE; // Default to dead-end or "infinite" signal.
		path.add(idx);
		int boost = mBoosts[idx];
		int hoist = mHoists[idx];
		int maxUp = hoist;
		boolean mustStopBecauseClimbed = false;

		mLoops++;
		int j = 0;
		for (int rmNrg = xse + boost, pos = idx + 1; --rmNrg >= 0; pos++)
		{
			dbgs("J=%d, idx=%d, xse=%d, hops=%d, pos=%d, rmNrg=%d\n",
				j++, idx, xse, hopsBeg, pos, rmNrg);
			if (pos >= mLength) {
				dbgs("RET: OVER END, hops=%3d, idx=%d, xse=%d, energy=%d. ", hopsBeg, idx, xse, rmNrg);
				dbgs(path);
				return hopsBeg; // arrived at the end! Return how many moves it took.
			}
			int posUp = mHoists[pos] - maxUp;
			if (posUp > 0) {
				maxUp += posUp;
				rmNrg -= posUp;		// Always subtract the energy it takes to surmount highest obstacle
				if (rmNrg < 0) {
					dbgs(mDebug+1, "climb BEG: posUp %d > %d rmNrg, boost %d, hoist %d\n", posUp, rmNrg, boost, hoist);
					if (boost <= 0) {
						dbgs(mDebug+1, "RET %3d at idx %d because energy %d & boost %d: DEAD END\n"
							, mCalls, idx, rmNrg, boost);
						path.remove(path.size() - 1);
						return Integer.MAX_VALUE; 	// dead end: cannot jump or climb the top
					}
					mustStopBecauseClimbed = true;	// NOTE: if set to false, don't reset hopsNow!!

					// -rmNrg is now the vertical distance to the top after using all remaining energy
					// Use to calculate the number of boost climbing moves and the excess at the top
					int climbMoves = (int)Math.ceil((float)-rmNrg / boost);
					int mod = -rmNrg % boost;
					rmNrg = mod > 0 ? boost - mod : 0;	// excess at the top is the new remaining energy

					// Add the boosted climbing moves to the total and to the path list
					hopsBeg = hopsBeg + climbMoves;
					path.addAll(Collections.nCopies(climbMoves, pos));
					dbgs(mDebug+1, "climb END: posUp %d, boost %d, ergs %d, idx %d, new ht %d, hops: %d + %d\n"
						, posUp, boost, rmNrg, idx, mHoists[pos], hopsBeg, climbMoves);
				}
			}
			/////////////////////////////////////////// RECURSE:
			hopsEnd = countHopsRBF(pos, rmNrg, hopsBeg, path);
			dbgs(mDebug+1, "res %4d, hopsBeg=%d hopsEnd=%d at idx=%d, xse=%d, rem=%d, path: "
					, myCall, hopsBeg, hopsEnd, idx, xse, rmNrg);
			dbgs(mDebug+1, path);
			if (mMoves > hopsEnd) {
				mMoves = hopsEnd; // save the new minimum
				dbgs(mDebug+1, "MIN FOUND: hops %d, idx %d, xse %d, rme=%d; PATH: ", hopsBeg, idx, xse, rmNrg);
				dbgs(mDebug+1, path);
				mMinPath = new ArrayList<Integer>(path); // copy the new minimal path
				//// return mMoves; // too greedy!
			}
			path.subList(hopsBeg, path.size()).clear();
			if (mustStopBecauseClimbed) {
				mustStopBecauseClimbed = false;
				break;
			}
		}
		dbgs("END, idx %d, min moves so far: %d\n", idx, mMoves);
		return hopsEnd;
	}
}

///////////////////////////////////////////////////////////////////////////////

/**
 * Naive: Re-tries steps and may have to back-track out from greed.
 */
class PairrayParkourGreedyRecurseForward extends PairrayParkourRecursive
{
	protected int[] mIntPath;	// TODO: move this to base class; final answer should be immutable
	
	
	protected PairrayParkourGreedyRecurseForward(int[] heights, int[] boosts) {
		super(heights, boosts);
	}

	@Override
	public int countHops() {
		mDebug = 0;
		mCalls = 0;
		mLoops = 0;
		mMoves = Integer.MAX_VALUE;
		int path[] = new int[mLength];
		int moves = countHopsGreedyRecurse(0, 0, 0, path);
		//assert(moves == mMoves);
		Sx.debug(mDebug+2, "Moves %4d: ", mMoves);
		Sx.debugArray(mDebug+2, mMinPath);
		return mMoves;
	}

	int countHopsGreedyRecurse(int idx, int xse, int hops, int path[])
	{
		// Count this call and check arguments:
		int myCall = mCalls++;
		assert(idx < mLength);
		assert(hops < path.length);

		dbgs("");
		dbgs(mDebug+1, "BEG: idx %d, xse %d, hgt %d, bst %d, msf %d, hops %d\t path: "
				, idx, xse, mHoists[idx], mBoosts[idx], mMoves, hops);
		dbgs(mDebug+1, path);

		// Short circuit if this path would be longer than the min already found:
		int hopsBeg = hops + 1;
		if (hopsBeg > mMoves) { // A shorter path was already found.
			dbgs("CUT: idx %d AT TOP BECAUSE hops %d > %d mMoves,  path: ", idx, hopsBeg, mMoves);
			dbgs(path);
			return Integer.MAX_VALUE;
		}

		// Look for a new path to the end:
		int hopsNow = hopsBeg;			 // FIXME: swap these?
		int hopsEnd = Integer.MAX_VALUE; // Default to dead-end or "infinite" signal.
		path[hops] = idx;
		int boost = mBoosts[idx];
		int hoist = mHoists[idx];
		int maxUp = hoist;
		boolean mustStopBecauseClimbed = false;

		mLoops++;
		int j = 0;
		for (int rmNrg = xse + boost, pos = idx + 1; --rmNrg >= 0; pos++)
		{
			dbgs("J=%d, idx=%d, xse=%d, hops=%d, pos=%d, rmNrg=%d\n",
				j++, idx, xse, hopsBeg, pos, rmNrg);
			if (pos >= mLength) {
				dbgs("RET: OVER END, hops=%3d, idx=%d, xse=%d, energy=%d. ", hopsBeg, idx, xse, rmNrg);
				dbgs(path);
				return hopsBeg; // arrived at the end! Return how many moves it took.
			}
			int posUp = mHoists[pos] - maxUp;
			if (posUp > 0) {
				maxUp += posUp;
				rmNrg -= posUp;		// Always subtract the energy it takes to surmount highest obstacle
				if (rmNrg < 0) {
					dbgs(mDebug+1, "climb BEG: posUp %d > %d rmNrg, boost %d, hoist %d\n", posUp, rmNrg, boost, hoist);
					if (boost <= 0) {
						dbgs(mDebug+1, "RET %3d at idx %d because energy %d & boost %d: DEAD END\n"
							, mCalls, idx, rmNrg, boost);
						//// path.remove(path.size() - 1);	// caller should just reduce its "working length"
						return Integer.MAX_VALUE; 	// dead end: cannot jump or climb the top
					}
					mustStopBecauseClimbed = true;	// NOTE: if set to false, don't reset hopsNow!!

					// -rmNrg is now the vertical distance to the top after using all remaining energy
					// Use to calculate the number of boost climbing moves and the excess at the top
					int climbMoves = (int)Math.ceil((float)-rmNrg / boost);
					int mod = -rmNrg % boost;
					rmNrg = mod > 0 ? boost - mod : 0;	// excess at the top is the new remaining energy

					// Add the boosted climbing moves to the total and to the path list
					hopsNow = hopsBeg + climbMoves;
					for (int k = hopsBeg; k < hopsNow; k++) {
						path[k] = pos;
					}
					// path.addAll(Collections.nCopies(climbMoves, pos));
					dbgs(mDebug+1, "climb END: posUp %d, boost %d, ergs %d, idx %d, new ht %d, hops: %d + %d\n"
						, posUp, boost, rmNrg, idx, mHoists[pos], hopsNow, climbMoves);
				}
			}
			/////////////////////////////////////////// RECURSE:
			hopsEnd = countHopsGreedyRecurse(pos, rmNrg, hopsNow, path);
			dbgs(mDebug+1, "res %4d, hopsBeg=%d hopsEnd=%d at idx=%d, xse=%d, rem=%d, path: "
					, myCall, hopsBeg, hopsEnd, idx, xse, rmNrg);
			dbgs(mDebug+1, path);
			if (mMoves > hopsEnd) {
				mMoves = hopsEnd; // save the new minimum
				dbgs(mDebug+1, "MIN FOUND: hops %d, idx %d, xse %d, rme=%d; PATH: ", hopsNow, idx, xse, rmNrg);	// FIXME: clarify
				dbgs(mDebug+1, path);
				mIntPath = Arrays.copyOf(path, hopsEnd);      // copy the new minimal path
				//// mMinPath = new ArrayList<Integer>(path); // copy the new minimal path
				//// return mMoves; // too greedy!
			}
			hopsNow = hopsBeg;	//// path.subList(hopsBeg, path.size()).clear();	// caller should just reduce its "working length"
			if (mustStopBecauseClimbed) {
				mustStopBecauseClimbed = false;
				break;
			}
		}
		dbgs("END, idx %d, min moves so far: %d\n", idx, mMoves);
		return hopsEnd;
	}
}

///////////////////////////////////////////////////////////////////////////////
class PairrayParkourDynamicProgrammingFwd extends PairrayParkourWithAuxArrays {
	protected long mAssigns; // upper bound on the number of assignments to aux array

	PairrayParkourDynamicProgrammingFwd(int heights[], int boosts[]) {
		super(heights, boosts);
	}

	@Override
	public int countHops() {
		mAssigns = 0;

		// init aux array
		for (int j = 1; j < mMinHops.length; j++) { // mMinHops[0] remains 0
			mMinHops[j] = Integer.MAX_VALUE - 1;
		}

		// init conditions: first hop is special
		mAssigns = mMinHops.length + mHoists[0]; // worst case is "expected" usual case
		for (int pos = mHoists[0]; pos > 0; pos--) { // mMinHops[0] remains 0
			if (pos >= mHoists.length)
				return 1; // reached the goal in one hop
			mMinHops[pos] = 1;
		}

		for (int j = 1; j < mHoists.length; j++) {
			int maxPos = j + mHoists[j];
			int hopNum = 1 + mMinHops[j]; // 1 more than min num hops it took to get here.
			for (int pos = maxPos; pos > j; pos--) { // mMinHops[0] remains 0
				if (pos >= mHoists.length) {
					mAssigns += maxPos - pos;
					return hopNum; // off the end in one more hop
				}
				if (mMinHops[pos] > hopNum)
					mMinHops[pos] = hopNum;
			}
			mAssigns += mHoists[j]; // still here
		}
		return 0;
	}

	@Override
	protected void showCounts() {
		Sx.puts("aux assigns: " + mAssigns);
	}

}

class PairrayParkourTest {
	public static int test_PairrayParkour(PairrayParkour arrayParkour, int heights[], int boosts[],
			int expectedMinNumHops) {
		String className = arrayParkour.getClass().getSimpleName();
		int minNumHops = arrayParkour.countHops();
		Sx.format("%s.countHops(...)\t hops: %d\t", className, minNumHops);
		arrayParkour.showCounts();
		return Sz.showWrong(minNumHops, expectedMinNumHops);
	}

	public static int testParkours(PairrayParkour[] parkours, int heights[], int boosts[], int expMinMoves,
			int expMinJumps) {
		int numWrong = 0;
		for (PairrayParkour parkour : parkours) {
			numWrong += test_PairrayParkour(parkour, heights, boosts, expMinMoves);
		}
		return numWrong;
	}

	public static int unit_test(int lvl) {
		String testName = PairrayParkour.class.getName() + ".unit_test";
		Sz.begin(testName);
		int numWrong = 0;
		
		int hobos[][] = {
			{ 1, 2, 3 }, 	// expected Parkour answer: 3, only one way
			{ 2, 2, 2 },	// expected Ahopper answer: 2 (first move 1, not 2)
			{ 1, 2, 2, 1 }, // expected Parkour answer: 3 (2 ways, via index 2 or 3)
			{ 2, 2, 1, 1 }, // expected Ahopper answer: 3 (2 ways, via index 1 or 2)
			{ 1, 7, 6 },				// P 3
			{ 3, 1, 1 },				// H 1
			{ 1, 6, 4, 5, 3 },			// P 5
			{ 2, 2, 0, 1, 2 },			// H 1
			{ 1, 7, 4, 4, 3 },			// P 4
			{ 4, 0, 1, 2, 0 },			// H 2
			{ 0, 2, 1, 2, 1, 3, 2, 5, 1, },			// expected answer: 5
			{ 4, 1, 1, 4, 0, 2, 1, 1, 1, },			// expected answer: 3
			{ 0, 1, 0, 3, 8, 2, 1, 7, 3, 4, 8, 5, 0, 4, 7, 7, 3, 8, 10, 5, 8, },
			{ 3, 2, 4, 2, 2, 1, 0, 3, 2, 6, 3, 3, 9, 1, 7, 1, 8, 3,  1, 3, 0, },
		};
		
		int expectP[] = { 3, 3, 4, 6, 5, 6, 12 };
		int expectH[] = { 2, 3, 1, 1, 2, 0,  0 };

		int begTrial = 0;					// expectP.length - 2;
		int endTrial = expectP.length; 		// begTrial + 2; //
		for (int j = begTrial; j < endTrial; j++) {
			int hoists[] = hobos[2 * j];
			int boosts[] = hobos[2 * j + 1];
			Sx.format("\n\t  Test Course %d:\n", j);
			Sx.format("Hoists %3d: ", hoists.length);
			Sx.putsArray(hoists);
			Sx.format("Boosts %3d: ", boosts.length);
			Sx.putsArray(boosts);
			PairrayParkour ParkourGRF = new PairrayParkourGreedyRecurseForward(hoists, boosts);
			PairrayParkour ParkourRBF = new PairrayParkourRecurseBreadthFirst(hoists, boosts);
			PairrayParkour ParkourNDP = new PairrayParkourDynamicProgrammingFwd(hoists, boosts);
			PairrayParkour parkours[] = {
					ParkourGRF,
					ParkourRBF,
					// ParkourNDP,
			};
			numWrong += testParkours(parkours, hoists, boosts, expectP[j], expectH[j]);
		}

		Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args) {
		unit_test(1);
	}
}
