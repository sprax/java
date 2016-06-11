package sprax.grids;

import java.io.Serializable;

import sprax.sprout.Sx;

public class PoppersBoard implements Serializable
{
    public static final int   sRicochet[]      = { 0, 0, 1, 3, 5, 7, 9 };
    private static final long serialVersionUID = 1L;
    final int                 mNumRows;
    final int                 mNumCols;
    final char                mValues[][];
    int                       mNumPoppers;
    int                       mMaxValue;
    int                       mTotValue;
    char                      mStringChars[];                            // used by toString
                                                                          
    /** constructor: create new empty board from the dimensions */
    public PoppersBoard(int numRows, int numCols)
    {
        mNumRows = numRows;
        mNumCols = numCols;
        mValues = new char[numRows][numCols];
        mStringChars = new char[mNumRows];
    }
    
    /** constructor: create and initiate board from values */
    public PoppersBoard(char values[][])
    {
        this(values.length, values[0].length);
        initBoardState(values);
    }
    
    /** Use this copy constructor; don't implement clone */
    public PoppersBoard(final PoppersBoard o)
    {
        // Must clone all the array's rows and each row's array.
        mValues = o.mValues.clone();
        for (int row = 0; row < o.mValues.length; row++)
            mValues[row] = o.mValues[row].clone();
        mNumRows = o.mNumRows;
        mNumCols = o.mNumCols;
        mNumPoppers = o.mNumPoppers;
        mMaxValue = o.mMaxValue;
        mTotValue = o.mTotValue;
        mStringChars = new char[mNumRows];
    }
    
    /**
     * Safe initialization from array of arrays. If some row is shorter
     * than row[0], the remainder of that board row remains zeros.
     * 
     * @param values
     */
    protected void initBoardState(char values[][])
    {
        mNumPoppers = 0;
        mMaxValue = 0;
        mTotValue = 0;
        char val;
        int endRow = Math.min(mValues.length, values.length);
        for (int row = 0; row < endRow; row++) {
            int endCol = Math.min(mValues[row].length, values[row].length);
            for (int col = 0; col < endCol; col++) {
                val = values[row][col];
                if (val > 0) {
                    mNumPoppers++;
                    mTotValue += val;
                    if (mMaxValue < val)
                        mMaxValue = val;
                }
                mValues[row][col] = val;
            }
        }
    }
    
    /**
     * Fast copy of whole other board state into this one.
     * NIECE: No input error checking or emptiness allowed.
     */
    protected void copyBoardStateNiece(PoppersBoard o)
    {
        mNumPoppers = o.mNumPoppers;
        mMaxValue = o.mMaxValue;
        mTotValue = o.mTotValue;
        for (int row = 0; row < mNumRows; row++)
            System.arraycopy(o.mValues[row], 0, mValues[row], 0, mNumCols);
    }
    
    boolean isOutOfBounds(int row, int col) {
        if (row < 0 || row >= mNumRows || col < 0 || col >= mNumCols)
            return true;
        return false;
    }
    
    public String toString8bit()
    {
        // 6 rows, 5 cols, 4 bits per cell: 120 bits or 15 chars per board.
        char chrs[] = new char[15];
        boolean even = true;
        char cd = 0;
        for (int sz = 0, row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                char val = mValues[row][col];
                int vin = val;
                if (even) {
                    cd = (char) (val << 4);
                } else {
                    cd |= val;
                    chrs[sz++] = cd;
                }
                even = !even;
            }
        }
        return new String(chrs);
    }
    
    @Override
    public String toString()
    {
        if (mNumCols > 5 || mMaxValue > 7)
            return toString8bit();              // TODO: improve this when expanding...
            
        // We can pack 5 or less 3-bit values into one 16-bit char/short/bit field.
        for (int row = 0; row < mNumRows; row++) {
            char rowChars[] = mValues[row];
            char chr = rowChars[0];
            for (int col = 1; col < mNumCols; col++)
                chr = (char) ((chr << 3) | rowChars[col]);
            mStringChars[row] = chr;
        }
        return new String(mStringChars);
    }
    
    public void showValues() {
        Sx.putsOffsetArray('0', mValues);
    }
    
    /**
     * Estimate a lower bound on minimum number of taps to win.
     * Empirically, this one has tested the best so far.
     * So far, this one seems the most reliable. That is,
     * it consistently underestimate the actual number of
     * taps to win, which indicates that it actually a
     * lower bound. It's based on each row and column
     * benefiting from the maximal possible ricochet pops,
     * based just on the number and values in each row
     * and column. Since actual configurations are likely
     * to be less than ideal for ricochets, the actual
     * number of required taps will typically be more.
     * This might give a strict lower bound if this
     * idealization captured the actual maximal effect of
     * ricochets. It appearts to miss some cross-over
     * effects, where a series of pops in one row or
     * column sets off a chain reaction in a parallel
     * row or column via some bridge. Maybe this can
     * be explained as a second order effect, though a
     * 10% rate of overestimation is still substantial.
     * 
     * Statistics (sample size 500):
     * estA est0 aveErr
     * Less .834 .392
     * Same .110 .128
     * More .056 .480
     * 
     * Scores est0, estA <>= 98 -244 125 291 27 204 -420 13 14 33
     * Scores est0, estA <>= 196 -462 240 548 64 417 -896 28 34 55
     * 
     * Scores est0, estA <>= 317 -759 347 779 108 659 -1421 37 47 76
     * Scores est0, estA <>= 1033 -2455 1033 2277 350 2006 -4316 4 34 406
     */
    public int estMinIdealRicochet()
    {
        int estMin = 0;
        
        int rowCounts[] = new int[mNumRows];
        int colCounts[] = new int[mNumCols];
        int rowSums[] = new int[mNumRows];
        int colSums[] = new int[mNumCols];
        int totalTaps = 0;
        
        // First pass: find the counts and max val for each row and col.
        // The counts for each row and col give a crude estimate of
        // neighbor counts.
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                int val = mValues[row][col];
                if (val > 0) {
                    totalTaps += val;
                    rowCounts[row]++;
                    rowSums[row] += val;
                    colCounts[col]++;
                    colSums[col] += val;
                }
            }
        }
        
        // Second pass: subtract idealized ricochet pops from sum of all values
        totalTaps = 0;
        for (int rowCount, row = 0; row < mNumRows; row++) {
            rowCount = rowCounts[row];
            int diff = rowSums[row] - rowCount - sRicochet[rowCount];
            if (diff > 0)
                totalTaps += diff;
            
        }
        for (int colCount, col = 0; col < mNumCols; col++) {
            colCount = colCounts[col];
            int diff = colCount - 1 - sRicochet[colCount];
            if (diff > 0)
                totalTaps += diff;
        }
        
        int altMin = estFromNeighborCounts(rowCounts, colCounts);
        if (totalTaps > 1)
            estMin = totalTaps;
        else
            estMin = 1;
        
        if (estMin < altMin)
            return altMin;
        return estMin;
    }
    
    public int estMinB()
    {
        int estMin = 0;
        
        int rowCounts[] = new int[mNumRows];
        int colCounts[] = new int[mNumCols];
        char rowMaxVal[] = new char[mNumRows], rowSums[] = new char[mNumRows];
        char colMaxVal[] = new char[mNumCols], colSums[] = new char[mNumCols];
        int rowTaps[] = new int[mNumRows];
        int colTaps[] = new int[mNumCols];
        int val;
        
        // First pass: find the counts and max val for each row and col.
        // The counts for each row and col give a crude estimate of
        // neighbor counts.
        int totTaps = 0;
        for (int row = 0; row < mNumRows; row++) {
            int rowHits = 0, rowPops = 0, rowTots = 0;
            char rowVals[] = mValues[row];
            int rowLess[] = new int[4];
            for (int col = 0; col < mNumCols; col++) {
                val = rowVals[col];
                if (val > 0) {
                    
                    int rls = 0;
                    for (int j = val; --j > 0;)
                        rls += rowLess[j];
                    
                    int inc = val - sRicochet[rowPops] - sRicochet[colCounts[col]];
                    rowHits += inc;
                    rowTots += val;
                    rowLess[val]++;
                    rowPops++;
                    
                    rowSums[row] += val;
                    
                    colCounts[col]++;
                    colSums[col] += val;
                }
            }
            rowCounts[row] = rowPops;
            totTaps += rowHits;
        }
        
        int altMin = estFromNeighborCounts(rowCounts, colCounts);
        if (totTaps > 1)
            estMin = totTaps;
        else
            estMin = 1;
        
        if (estMin < altMin)
            return altMin;
        return estMin;
    }
    
    /** Alternative minimum based on neighbor counts alone */
    protected int estFromNeighborCounts(int rowCounts[], int colCounts[])
    {
        int altMin = 1;
        for (int row = 0; row < mNumRows; row++) {
            char rowVals[] = mValues[row];
            for (int col = 0; col < mNumCols; col++) {
                int val = rowVals[col];
                if (val > 0) {
                    int dif = val + 2 - rowCounts[row] - colCounts[col];
                    if (dif > 0) {
                        altMin += dif;
                    }
                }
            }
        }
        return altMin;
    }
    
    static void estMinVals(char values[][]) {
        PoppersBoard board = new PoppersBoard(values);
        board.showValues();
        int estA = board.estMinIdealRicochet();
        int estB = board.estMinB();
        Sx.format("estimated min taps A & B:  %d  %d\n\n", estA, estB);
    }
    
    public static int test_estMin(int level)
    {
        if (level > 1) {
            char valuesA[][] = {
                    { 1, 2 },
                    { 1, 1 },
            };
            estMinVals(valuesA);
            
            char valuesB[][] = {
                    { 1, 0, 2 },
                    { 0, 1, 0 },
                    { 1, 0, 1 },
            };
            estMinVals(valuesB);
            
            char valuesC[][] = {
                    { 1, 1, 2 },
                    { 0, 1, 0 },
                    { 1, 0, 1 },
            };
            estMinVals(valuesC);
            char valuesD[][] = {
                    { 1, 1, 2 },
                    { 0, 1, 0 },
                    { 2, 0, 1 },
            };
            estMinVals(valuesD);
            char values1[][] = {
                    { 2, 1, 2 },
                    { 0, 1, 0 },
                    { 2, 0, 1 },
            };
            estMinVals(values1);
            char valuesF[][] = {
                    { 1, 1, 1, 3 },
                    { 1, 1, 0, 0 },
                    { 3, 0, 0, 1 },
                    { 3, 0, 1, 0 },
            };
            estMinVals(valuesF);
            char valuesFA[][] = {
                    { 1, 1, 1, 3 },
                    { 1, 1, 0, 0 },
                    { 3, 0, 0, 1 },
                    { 3, 0, 1, 0 },
                    { 3, 0, 1, 0 },
            };
            estMinVals(valuesFA);
            char valuesG[][] = {
                    { 1, 1, 1, 0, 3 },
                    { 1, 1, 0, 0, 0 },
                    { 1, 1, 0, 0, 0 },
                    { 0, 0, 0, 2, 0 },
                    { 3, 0, 1, 0, 0 },
            };
            estMinVals(valuesG);
            PoppersBoard boardG = new PoppersBoard(valuesG);
            Sx.puts("game.mBoard.toString:");
            String strA = boardG.toString();
            String strB = boardG.toString();
            if (!strA.equals(strB)) {
                throw new IllegalStateException("PoppersBoard.toString is broken");
            }
        }
        char valuesE[][] = {
                { 1, 1, 3 },
                { 0, 1, 0 },
                { 3, 0, 1 },
        };
        estMinVals(valuesE);
        
        return 0;
    }
    
    public static void main(String[] args) {
        test_estMin(2);
    }
}
