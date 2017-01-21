package sprax.grids;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sprax.grids.PoppersGame.Tap;
import sprax.sprout.Sx;

/* 
 * Minimum-number-of-taps solution for a specified Poppers game.
 * So far, its stupid brute force.  Stupid because the same board
 * position may be obtained via different paths.
 * TODO: marked visited board states.
 * If no solution is found (in less than the calculated mMaxTaps), 
 * then mMinTaps is set to -1 and the result string notes the failure.
 * 
 * TODO: Find a better estimate to use as an initial lower bound
 * 			 in the adaptive search.
 * TODO: Find a strict lower bound.  Even if it is not always close
 *       to being a greatest lower bound, if could be used to terminate
 *       search paths early.  If the strict lb is fast, that could give 
 *       a big speed-up.
 * 
 * @author sprax    2012 June
 */


public class PoppersSolver 
{       
    static final SimpleDateFormat sDateFormat = new SimpleDateFormat("HH:mm:ss"); 

    final PoppersGame       mGame;
    final PoppersBoard  mBoard; // for convenience only
    final int     mNumRows;     // for convenience only
    final int     mNumCols;     // for convenience only
    int           mMinTaps;     // Actual minimum required taps, if known
    int           mMaxTaps;     // Crude upper bound on min required taps
    int           mEstMin0;     // Initial estimate of minimum required taps
    int           mEstMin1;     // Another estimate of minimum required taps
    int           mEstMin2;     // Another estimate of minimum required taps
    int           mEstMin3;     // Another estimate of minimum required taps
    int           mEstMin4;     // Another estimate of minimum required taps
    int           mDeepest;		// max depth explored so far
    int	          mVerbose;
    long          mTapCount;    // Total number of simulated taps
    long          mBegTime;
    long          mNxtTime;
    long          mBfsTime;   // Milliseconds to find first minimal-tap solution using bfbfs
    double        mDblTime;
    Tap[]         mTapSeq;      // First-found minimal winning tap sequence
    String        mResultStr;
    List<PoppersBoard> mBackupBoards;
    Map<String, Integer>    mBoardStrToTaps;
    static final long sMinute = 60000;
    static long   sShowTimeMs = sMinute * 10;
    static int    sDartNumbers[] = { 0, 0, 1, 3, 5, 7, 9 };

    PoppersSolver(PoppersGame game)
    {
        if (game == null)
            throw new IllegalArgumentException("game is null");
        mGame    = new PoppersGame(game);     // defensive copy
        mBoard   = mGame.mBoard;          // for convenience only
        mNumRows = mGame.getNumRows();
        mNumCols = mGame.getNumCols();
        mBoardStrToTaps = new HashMap<>();

        // Upper bounds on num taps: If every square held mMaxValue, tapping 
        // each square in reading order until it exploded would give:
        mMaxTaps = 1 + mNumRows + mNumCols;
        int more = mBoard.mMaxValue - 2;
        if (more > 0)
            mMaxTaps += more * mNumRows * mNumCols;
        if (mMaxTaps > mBoard.mTotValue)
            mMaxTaps = mBoard.mTotValue;

        // Get estimate(s) of the minimum number of taps
        mEstMin0 = estMin0();
        mEstMin1 = estMin1();
        mEstMin2 = estMin2();
        mEstMin3 = estMin3();
        mEstMin4 = estMin4();
        mBfsTime = -1;
    }

    void solve() 
    {
        if (mBfsTime < 0) {
            mBfsTime = 0;
            long begTime = System.currentTimeMillis();
            //mMinTaps     = bfsAdaptive();
            mMinTaps = bfsIncreasing();		
            mBfsTime   = System.currentTimeMillis() - begTime;
            mDblTime   = mBfsTime > 0 ? mBfsTime : 0.5;
        }
    }

    int minTaps() { 
        solve(); 
        return mMinTaps; 
    }

    String resultStr(int verbose)
    {
        if (mResultStr == null) {
            mVerbose = verbose;
            solve();
            if (mMinTaps > 0) {
                double tps = mTapCount / mDblTime;
                mResultStr = String.format("Game can be won in %d taps (%d ms, %d trials, %g Ktaps)"
                        , mMinTaps, mBfsTime, mTapCount, tps);
                if (verbose > 0) {
                    mResultStr = mResultStr.concat(":\n");
                    for (int j = 0; j < mMinTaps; j++)
                        mResultStr = mResultStr.concat("  ").concat(mTapSeq[j].toString());
                    mResultStr = mResultStr.concat("\n");
                }
            } else {
                mResultStr = String.format("No solution found in less than %d taps!", mMaxTaps);
            }
        }
        return  mResultStr;
    }

    public void showPreStats()
    {
        Sx.format("\n// %d poppers, max val: %d, max taps: %d, min ests 0&1: %d  %d\n"
                , mBoard.mNumPoppers, mBoard.mMaxValue, mMaxTaps, mEstMin0, mEstMin1);
    }
    public void showArrays()
    {
        showPreStats();
        Sx.putsArrayCode("char values[][] = {\n", mBoard.mValues, '0', "};");
    }



    /** Brute-force breadth-first search for a minimal-taps solution.
     *  Try to solve the game in 1 turn, then 2 turns, 3 ... until solved.
     *  In "reading order" (left-to-right, top-to-bottom), simulate
     *  one tap on one node, and if it results in a win, return the
     *  single-tap sequence.  If the result is not a win, restore the
     *  former board state and try tapping the next node.  If there are no
     *  single-tap wins, check for any two-tap win, then a 3-tap win, etc.
     *  
     *  If no winning sequence is found, return -1.
     */
    public int bfsIncreasing()
    {
        mTapSeq       = new Tap[mMaxTaps];
        mBackupBoards = new ArrayList<>(mEstMin0);
        if (mVerbose > 0) {
            showProgressHeader();
            mBegTime = Calendar.getInstance().getTimeInMillis();
            mNxtTime = mBegTime + sShowTimeMs;
        }
        for (int maxTapIdx = 0; maxTapIdx < mMaxTaps; maxTapIdx++) {
            mTapSeq[maxTapIdx] = mGame.new Tap(-1, -1, -1);
            int winDepth = searchMaxDepth(maxTapIdx);
            if (winDepth > 0) {
                return winDepth;
            }
        }
        return -1;
    }

    /** 
     *  Breadth-first search for a minimal-taps solution, starting an
     *  estimate of the minimum number of taps as the initial maximum
     *  depth.  If the game is not solved at that depth, try increasing
     *  depths until it is solved.  If a solution is found at the 
     *  starting depth or less, try decreasing the max depth until no 
     *  solution is found -- the min depth is then one more than that
     *  first failure depth.  
     *  
     *  If no winning sequence is found, return -1.
     */
    public int bfsAdaptive()
    {
        mTapSeq       = new Tap[mMaxTaps];
        mBackupBoards = new ArrayList<>(mEstMin0);
        if (mVerbose > 0) {
            showProgressHeader();
            mBegTime = Calendar.getInstance().getTimeInMillis();
            mNxtTime = mBegTime + sShowTimeMs;
        }


        int winDepth = -1, estMin = mBoard.estMinIdealRicochet();
        for (int j = 0; j < estMin - 1; j++)
            mTapSeq[j] = mGame.new Tap(-1, -1, -1);
        for (int maxTapIdx = estMin - 1; maxTapIdx < mMaxTaps; maxTapIdx++) {
            mTapSeq[maxTapIdx] = mGame.new Tap(-1, -1, -1);
            winDepth = searchMaxDepth(maxTapIdx);
            if (winDepth > 0)
                break;
        }
        if (winDepth > estMin)
            return winDepth;			// actual min was greater than the estimate
        else if (winDepth < 1)
            return winDepth;			// no solution was found (error condition)
        else if (winDepth < 2)
            return winDepth;			// cannot improve on a single-tap solution

        // A solution was found with 2 <= depth (i.e. num taps) <= the estimate, 
        // so try to improve it by decreasing the max search depth.
        int savedDepth = winDepth;
        for (int maxTapIdx = winDepth - 1; --maxTapIdx >= 1; ) {
            winDepth = searchMaxDepth(maxTapIdx);
            if (winDepth > 0)
                return winDepth;
        }
        return savedDepth;
    }

    protected int searchMaxDepth(int maxTapIdx) 
    {
        mBoardStrToTaps.clear();
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                int val = mBoard.mValues[row][col];
                if (val == 1 || (val > 1 && maxTapIdx > 0)) {
                    int winDepth = bfbfsRecurse(0, maxTapIdx, row, col, val);
                    if (winDepth > 0) {
                        return winDepth;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * The recursive part of bfbf search.  Tap the specified square, 
     * and if the result is a win, return the number of taps it took
     * (which is the same as the search depth).  If it is not a win,
     * recurse deeper.  That is, this function calls itself to try
     * out each possible next move.  This is a "brute-force" search 
     * in that there is no pruning or priority-ordering of the next
     * possible moves.  It simply tries all of them in a pre-defined 
     * order.
     * @return The search depth on success, -1 on failure
     */
    int bfbfsRecurse(int idx, int maxIdx, int pRow, int pCol, int pVal)
    {
        int ret = -99;
        backupBoard(idx);
        mTapSeq[idx].mRow = pRow;
        mTapSeq[idx].mCol = pCol;
        mTapSeq[idx].mVal = pVal;
        int idxPlus1 = idx + 1;
        mGame.tap(pRow, pCol);
        mTapCount++;
        if (mGame.mPlayState == PoppersGame.PlayState.Won) {
            // Don't need to restore, just return the win.
            // But if we were collecting all winning sequences,
            // then we'd want to add a copy of the current sequence,
            // restore, and then return.
            Sx.puts();
            return idxPlus1;
        } 

        if (idx == maxIdx) {
            ret = -1;      
        } else {
            String boardVals = mBoard.toString();
            Integer prevTaps = mBoardStrToTaps.get(boardVals);
            if (prevTaps != null && prevTaps <= idxPlus1) {
                // This same board state has been seen before, after the same
                // or a smaller number of taps, and continuing from it failed 
                // to produce a minimal solution.  So quit this path now.
                ret = 0;
            } else {
                // Either this board state has never been seen before, or it
                // was obtained after a greater number of taps.  So continue
                // down this path, it may yet provide a minimal solution.
                mBoardStrToTaps.put(boardVals, idxPlus1);
                if (mDeepest < idxPlus1) {
                    mDeepest = idxPlus1;
                    if (mVerbose == 1) {
                        Sx.print(" " + mDeepest);
                    } else if (mVerbose > 2) {
                        showProgressData();
                    }
                } else if (idx == 1 && mVerbose > 2) {
                    long nowTime = System.currentTimeMillis();
                    if (mNxtTime < nowTime) {
                        mNxtTime = nowTime + sShowTimeMs;
                        showProgressData();
                    }
                }
                char val;
                for (int row = 0; row < mNumRows; row++) {
                    for (int col = 0; col < mNumCols; col++) {
                        // If this is the last tap, then only tap Poppers that are
                        // ready to pop.  In other words, if depth+1 == maxDepth, 
                        // then only try cells that have value==1.  Otherwise, try
                        // any cell with a value > 0.
                        val = mBoard.mValues[row][col];
                        if (val == 1 || (val > 1 && idxPlus1 < maxIdx)) {
                            int winDepth = bfbfsRecurse(idxPlus1, maxIdx, row, col, val);
                            if (winDepth > 0) {
                                return winDepth;
                            }
                        }
                    }
                }
            }
        }
        restoreBoard(idx);
        return ret;
    }

    void showProgressHeader()
    {
        if (mVerbose > 1)
            Sx.print("Depth  WallTime         Taps  Mega-Taps/Sec     Seconds:");
    }

    void showProgressData()
    {
        Calendar cal = Calendar.getInstance();
        Date calTime = cal.getTime();
        long nowTime = cal.getTimeInMillis();
        String times = sDateFormat.format(calTime);
        long totMils = nowTime - mBegTime;
        double totSecs = totMils/1000.0;   // elapsed seconds
        double totMics;
        if (totMils > 0)
            totMics = totMils*1000.0;   // elapsed microseconds
        else
            totMics = mTapCount*2;      // "estimate" to avoid division by 0
        double megaTPS = mTapCount / totMics;
        Sx.format("\n%5d  %s %12d  %g Ktaps\t%7.3f", mDeepest, times, mTapCount, megaTPS, totSecs);       
    }

    void backupBoard(int idx)
    {
        if (mBackupBoards.size() <= idx) {
            PoppersBoard backupBoard = new PoppersBoard(mNumRows, mNumCols);
            mBackupBoards.add(backupBoard);
        }
        PoppersBoard backupBoard = mBackupBoards.get(idx);
        backupBoard.copyBoardStateNiece(mBoard);
    } 

    void restoreBoard(int idx)
    {
        PoppersBoard backupBoard = mBackupBoards.get(idx);
        mBoard.copyBoardStateNiece(backupBoard);
    }


    // Estimate a lower bound on min number of taps to win */
    public int estMin0()
    {
        mEstMin0     = mBoard.mTotValue;
        int maxLen, minLen;
        if (mNumRows < mNumCols) {
            minLen = mNumRows; maxLen = mNumCols;
        } else {
            minLen = mNumCols; maxLen = mNumRows;
        }
        int excessA  = mBoard.mNumPoppers - minLen;
        if (excessA  > 0)
            mEstMin0 -= excessA*2;
        int excessB  = excessA - maxLen;
        if (excessB  > 0)
            mEstMin0 -= excessB*3;
        if (mEstMin0 < 1)
            mEstMin0 = 1;
        return mEstMin0;
    }


    // Estimate a lower bound on min number of taps to win */
    public int estMin1()
    {
        int  rowMaxIdx[] = new int[mNumRows], rowCounts[] = new int[mNumRows];
        int  colMaxIdx[] = new int[mNumCols], colCounts[] = new int[mNumCols];
        char colMaxVal[] = new char[mNumCols];
        char rowMaxVal[] = new char[mNumRows];
        char val, values[][] = mBoard.mValues;

        // First pass: find the counts and max val for each row and col.
        // The counts for each row and col give a crude estimate of 
        // neighbor counts.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col <mNumCols; col++) {
                val = values[row][col];
                if (val > 0) {
                    rowCounts[row]++;
                    if (rowMaxVal[row] < val) {
                        rowMaxVal[row] = val;
                        rowMaxIdx[row] = col;
                    }
                    colCounts[col]++;
                    if (colMaxVal[col] < val) {
                        colMaxVal[col] = val;
                        colMaxIdx[col] = val;
                    }
                }
            }
        }

        int nonEmptyRows = 0, nonEmptyCols = 0;
        for (int row = 0; row < mNumRows; row++)
            if (rowMaxVal[row] > 0)
                nonEmptyRows++;
        for (int col = 0; col < mNumCols; col++)
            if (colMaxVal[col] > 0)
                nonEmptyCols++;
        mEstMin1 = Math.min(nonEmptyRows, nonEmptyCols);

        for(int row = 0; row < mNumRows; row++) {
            int col = rowMaxIdx[row];
            int num = values[row][col];
            int exs = num + 2 - rowCounts[row] - colCounts[col];
            if (exs > 0) {
                mEstMin1 += exs;
            }
        }

        for(int row = 0; row < mNumRows; row++) {
            int col = rowMaxIdx[row];
            int num = values[row][col];
            int exs = num + 2 - rowCounts[row] - colCounts[col];
            if (exs > 0) {
                mEstMin1 += exs;
            }
        }
        return mEstMin1;
    }


    // Estimate a lower bound on min number of taps to win */
    public int estMin2()
    {
        mEstMin2 = 0;
        int  rowMaxIdx[] = new int[mNumRows], rowCounts[] = new int[mNumRows];
        int  colMaxIdx[] = new int[mNumCols], colCounts[] = new int[mNumCols];
        char rowMaxVal[] = new char[mNumRows], rowSums[] = new char[mNumRows];
        char colMaxVal[] = new char[mNumCols], colSums[] = new char[mNumCols];
        char val, values[][] = mBoard.mValues;

        // First pass: find the counts and max val for each row and col.
        // The counts for each row and col give a crude estimate of 
        // neighbor counts.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col <mNumCols; col++) {
                val = values[row][col];
                if (val > 0) {
                    rowCounts[row]++;
                    rowSums[row] += val;
                    if (rowMaxVal[row] < val) {
                        rowMaxVal[row] = val;
                        rowMaxIdx[row] = col;
                    }
                    colCounts[col]++;
                    colSums[col] += val;
                    if (colMaxVal[col] < val) {
                        colMaxVal[col] = val;
                        colMaxIdx[col] = val;
                    }
                }
            }
        }

        // Second pass: Collect the solitaries, etc.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col <mNumCols; col++) {
                val = values[row][col];
                if (val > 0) {
                    if (rowCounts[row] == 1 && colCounts[col] == 1)
                        mEstMin2 += val;
                    else if (rowCounts[row] == 1) {
                        switch (colCounts[col]) {
                            case 2:
                                mEstMin2 += colSums[col] - 1;
                                break;
                            case 3:
                                mEstMin2 += colSums[col] - 3;
                                break;
                            case 4:
                                mEstMin2 += colSums[col] - 5;
                                break;
                            case 5:
                                mEstMin2 += colSums[col] - 7;
                                break;
                        }
                    }
                }
            }
        }    
        return mEstMin2;
    }




    /**
     *  Estimate a lower bound on min number of taps to win.
     *  So far, this one seems the most reliable.  That is,
     *  it consistently underestimate the actual number of 
     *  taps to win, which indicates that it actually a
     *  lower bound.  It's based on each row and column
     *  benefiting from the maximal possible ricochet pops,
     *  based just on the number and values in each row
     *  and column.  Since actual configurations are likely
     *  to be less than ideal for ricochets, the actual
     *  number of required taps will typically be more.
     *  The lower bound property is based on this idealized
     *  account capturing the actual maximal effect of
     *  ricochets.
     */
    public int estMin3()
    {
        mEstMin3 = 0;
        int  rowMaxIdx[] = new int[mNumRows], rowCounts[] = new int[mNumRows];
        int  colMaxIdx[] = new int[mNumCols], colCounts[] = new int[mNumCols];
        char colMaxVal[] = new char[mNumCols];
        char rowMaxVal[] = new char[mNumRows];
        char val;

        char valcopy[][] = mBoard.mValues.clone();
        for (int row = 0; row < mBoard.mValues.length; row++)
            valcopy[row] = mBoard.mValues[row].clone();

        // First pass
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col <mNumCols; col++) {
                val = valcopy[row][col];
                if (val > 0) {
                    rowCounts[row]++;
                    if (rowMaxVal[row] < val) {
                        rowMaxVal[row] = val;
                        rowMaxIdx[row] = col;
                    }
                }
            }
        }


        // Second pass: Distribute the darts' max popping power within each row,
        // then recalculate column counts and distribute the max popper power
        // among what remains in each column.
        for(int row = 0; row < mNumRows; row++) {
            int rdn = sDartNumbers[rowCounts[row]];
            for (int col = 0; col < mNumCols; col++) {
                val = valcopy[row][col];
                if (val > 1 && rdn > 0) {
                    int mto = val - 1;
                    if (mto > 0) {
                        valcopy[row][col] = 1;
                        rdn -= mto;
                    }
                }
            }
        } 

        // Third pass: count column values from the copy
        for (int col = 0; col <mNumCols; col++) {
            for (int row = 0; row < mNumRows; row++) {
                val = valcopy[row][col];
                if (val > 0) {
                    colCounts[col]++;
                    if (colMaxVal[col] < val) {
                        colMaxVal[col] = val;
                        colMaxIdx[col] = val;
                    }
                }
            }
        }

        // Fourth pass: distribute popping power within each column.
        for (int col = mNumCols; --col >= 0;  ) {
            int min, rdn = sDartNumbers[colCounts[col]];
            for(int row = mNumRows; --row >= 0;  ) {
                val = valcopy[row][col];
                if (val > 0 && rdn > 0) {
                    min = Math.min(val,  rdn);
                    valcopy[row][col] -= min;
                    rdn -= min;
                }
            }
        }

        // Fifth pass: just count up whatever remains.
        for (int col = mNumCols; --col >= 0;  )
            for(int row = mNumRows; --row >= 0;  )
                mEstMin3 += valcopy[row][col];

        return mEstMin1;
    }


    /**
     *  Estimate a lower bound on min number of taps to win.
     *  An ad hoc approach.
     */
    public int estMin4()
    {
        mEstMin4 = 0;

        int  rowMaxIdx[] = new int[mNumRows], rowCounts[] = new int[mNumRows];
        int  colMaxIdx[] = new int[mNumCols], colCounts[] = new int[mNumCols];
        char rowMaxVal[] = new char[mNumRows], rowSums[] = new char[mNumRows];
        char colMaxVal[] = new char[mNumCols], colSums[] = new char[mNumCols];
        int  rowTaps[] = new int[mNumRows];
        char val, values[][] = mBoard.mValues;

        // First pass: find the counts and max val for each row and col.
        // The counts for each row and col give a crude estimate of 
        // neighbor counts.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col <mNumCols; col++) {
                val = values[row][col];
                if (val > 0) {
                    rowCounts[row]++;
                    rowSums[row] += val;
                    if (rowMaxVal[row] < val) {
                        rowMaxVal[row] = val;
                        rowMaxIdx[row] = col;
                    }
                    colCounts[col]++;
                    colSums[col] += val;
                    if (colMaxVal[col] < val) {
                        colMaxVal[col] = val;
                        colMaxIdx[col] = val;
                    }
                }
            }
        }
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col <mNumCols; col++) {
                val = values[row][col];
                if (val > 0) {
                    if (rowTaps[row] < 1)
                        rowTaps[row] = 1;
                }
            }
        }
        return mEstMin4;
    }


    public static int testSolveRandom()
    {
        PoppersGame game;
        PoppersSolver solver;
        int    numGames = 0;
        int    numDupes = 0;
        String estLess  = "---- Less ----";
        String estSame  = "==== Same ====";
        String estMore  = "++++ MORE ++++";
        int      scores[] = new int[12];
        double percents[] = new double[12];
        double dblGames, gamesAveKtaps = 0, timesAveKtaps = 0, totalSolverTime = 0;
        Map<String, Integer>    boardStrToNumGen = new HashMap<>();

        for (int end = 7*60*60*24, j = 0; j < end; j++) {
            while(true) {
                game = PoppersGame.makeRandomGame(6, 5, 4, 0.615);
                String  str = game.mBoard.toString();
                Integer gen = boardStrToNumGen.get(str);
                if (gen != null) {
                    boardStrToNumGen.put(str, 1+gen);
                    numDupes++;
                } else {
                    boardStrToNumGen.put(str, 1);
                    solver = game.getSolver();
                    if (4 < solver.mEstMin0 && solver.mEstMin0 < 14 && solver.mMaxTaps < 40) {
                        break; 
                    }
                }
            }
            solver.mVerbose = 1;
            solver.showArrays();

            int estA = game.mBoard.estMinIdealRicochet();

            // Solve the game as a side-effect of getting the solver results.
            String results = solver.resultStr(0);
            Sx.print(results);
            gamesAveKtaps = gamesAveKtaps*numGames;
            numGames++;
            dblGames = numGames;
            gamesAveKtaps    = (gamesAveKtaps + solver.mTapCount/solver.mDblTime)/dblGames;
            timesAveKtaps    =  timesAveKtaps*totalSolverTime;
            totalSolverTime += solver.mDblTime;
            timesAveKtaps    = (timesAveKtaps + solver.mTapCount)/totalSolverTime;
            Sx.format("  gamesAveKtaps: %g  timesAveKtaps: %g\n", gamesAveKtaps, timesAveKtaps);

            int bestEst = estA;
            String best = "estA", est;
            if (bestEst < solver.mMinTaps)
                est = estLess;
            else if (bestEst == solver.mMinTaps)
                est = estSame;
            else 
                est = estMore;

            int dif0 = solver.mEstMin0 - solver.mMinTaps;
            if (dif0 < 0) {
                scores[ 0]++;
                scores[ 1] += dif0;
                scores[ 5] -= dif0;
            } else if (dif0 > 0) {
                scores[ 2]++;
                scores[ 3] += dif0;
                scores[ 5] += dif0;
            } else {
                scores[ 4]++;
            }
            int difA = estA - solver.mMinTaps;
            if (difA < 0) {
                scores[ 6]++;
                scores[ 7] += difA;
                scores[11] -= difA;
            } else if (difA > 0) {
                scores[ 8]++;
                scores[ 9] += difA;
                scores[11] += difA;
            } else {
                scores[10]++;
            }

            Sx.format("MinTaps: estimates:  %2d  %2d  %2d  %2d  %2d  act: %d  %s (%s)\n"
                    , solver.mEstMin0, solver.mEstMin1, solver.mEstMin2
                    , solver.mEstMin3, estA, solver.mMinTaps, est, best);
            for (int k = 0; k < 12; k += 2) {
                percents[k] = scores[k] / dblGames;
            }
            if (scores[0] > 0)
                percents[1] = (double) scores[1] /  scores[0];
            if (scores[2] > 0)
                percents[3] = (double) scores[2] /  scores[2];
            if (scores[0] + scores[2] > 0)
                percents[5] = (double) scores[5] / (scores[0] + scores[2]);
            if (scores[6] > 0)
                percents[7] = (double) scores[7] /  scores[6];
            if (scores[8] > 0)
                percents[9] = (double) scores[9] /  scores[8];
            if (scores[6] + scores[8] > 0)
                percents[11] = (double)scores[11]/ (scores[6] + scores[8]);

            Sx.putsArray("Scores est0, estA <>= ", scores, " [" + numGames + " | " + numDupes + "]");
            Sx.putsArray("Fractions 0 and A <>= ", percents);
        }
        Sx.puts();
        return 0;
    }

    public static int unit_test(int level) 
    {
        String  testName = PoppersSolver.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");    

        PoppersGame game = null;
        PoppersSolver solver = null;
        String results = null;

        game = PoppersTestGames.makeTestGame();
        game.showBoard();
        //game.showArrays();
        solver = new PoppersSolver(game);
        results = solver.resultStr(1);
        Sx.puts(results);

        game = PoppersTestGames.makeTestGame_3_18();
        game.showBoard();
        //game.showArrays();
        solver = new PoppersSolver(game);
        results = solver.resultStr(1);
        Sx.puts(results);


        game = PoppersTestGames.makeTestGame_3_18();
        game.showBoard();
        game.tap(3, 3);
        game.tap(3, 3);
        game.tap(1, 2);
        game.showBoard();

        if (level > 3) {
            game = PoppersTestGames.makeTestGame6();
            solver = game.getSolver();
            solver.showPreStats();
            game.showBoard(0);
            results = solver.resultStr(3);
            Sx.puts(results);
        }

        if (level > 2) {
            game = PoppersTestGames.makeTestGameSlow12();
            solver = game.getSolver();
            solver.showPreStats();
            game.showBoard(0);
            results = solver.resultStr(3);
            Sx.puts(results);
        }

        if (level > 2)
            testSolveRandom();

        Sx.puts(testName + " END");    
        return 0;
    }


    public static void main(String[] args) { unit_test(3); }   
}   // end of class PoppersSolver

