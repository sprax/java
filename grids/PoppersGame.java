package sprax.grids;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;
import sprax.Spaces;
import sprax.Sx;

class Dart
{
    int mRow;
    int mCol;
    int mVeX;
    int mVeY;
    
    Dart(int row, int col, int vex, int vey) {
        mRow = row;
        mCol = col;
        mVeX = vex;
        mVeY = vey;
    }
    
    void updatePos() {
        mRow += mVeY;
        mCol += mVeX;
    }
}

public class PoppersGame
{
    public enum PlayState
    {
        Play, Pause, Won, Lost
    }
    
    /** Stores the position (row & col) and value <I>before the tap</I>. */
    class Tap
    {
        int mRow, mCol, mVal;
        
        Tap(int row, int col, int val) {
            mRow = row;
            mCol = col;
            mVal = val;
        }
        
        public String toString() {
            return String.format("[%d  %d (%d)]", mRow, mCol, mVal);
        }
    }
    
    int                 mTurnsLeft;
    int                 mTurnCount;   // mTurnsLeft + mTurnCount == original num turns
    PlayState           mPlayState;
    PoppersBoard        mBoard;
    PoppersBoard[]      mBoardSeq;
    Tap[]               mTapSeq;   // Sequence of actual game taps (not simulated taps)
    PoppersSolver       mSolver;
    LinkedHashSet<Dart> mDarts;
    Queue<Dart>         mNewDarts;
    Queue<Dart>         mDelDarts;
    
    public PoppersGame(PoppersBoard pb, int turnsLeft)
    {
        mTurnsLeft = turnsLeft;
        mBoard = new PoppersBoard(pb);
        mDarts = new LinkedHashSet<Dart>(pb.mNumRows * pb.mNumCols);
        mNewDarts = new LinkedList<Dart>();
        mDelDarts = new LinkedList<Dart>();
        mPlayState = PlayState.Pause;
    }
    
    public PoppersGame(char values[][], int numTurns)
    {
        this(new PoppersBoard(values), numTurns);
    }
    
    // static BoardState createBoardState(char values[][]) { // TODO: remove?
    // return new BoardState(2, 2);
    // }
    
    /** Copy constructor: use this, don't implement clone */
    public PoppersGame(PoppersGame o)
    {
        this(o.mBoard, o.mTurnsLeft);
    }
    
    PoppersSolver getSolver()
    {
        if (mSolver == null) {
            mSolver = new PoppersSolver(this);
        }
        return mSolver;
    }
    
    public void solveGame()
    {
        getSolver().solve();
    }
    
    /**
     * Solves game, if necessary, and returns the minimum number of taps.
     * TODO: if the game was not already solved, this solves it in it current
     * state, not in its original state.
     */
    public int minTaps()
    {
        return getSolver().minTaps();
    }
    
    public int getNumRows() {
        return mBoard.mNumRows;
    }
    
    public int getNumCols() {
        return mBoard.mNumCols;
    }
    
    /*
     * Advance one time step.  For each Dart, update its position.  
     * If the new position is out of bounds, queue the Dart for removal.  
     * If the new position is occupied, i.e. has a positive value, process
     * the hit immediately, before handling any other Dart at that position.
     * Any hit destroys the Dart (queues it for removal) and immediately
     * decrements the value at that position.  
     * If the value goes to zero, an explosion creates 4 new Darts, which
     * are queued up for addition at the beginning of the next step. 
     * (If the value at a position is < 0, that means it is unoccupied,
     * and any Dart just passes through it with no effect, even if was
     * just exploded by another Dart in the same time step.)
     */
    void step()
    {
        // Assume that the darts moving sideways reach the next cell
        // sooner than the darts moving up or down. So update them first.
        for (Dart dart : mDarts) {
            if (dart.mVeY == 0) {
                updateDart(dart);
            }
        }
        for (Dart dart : mDarts) {
            if (dart.mVeX == 0) {
                updateDart(dart);
            }
        }
        mDarts.removeAll(mDelDarts);
        mDelDarts.clear();
        mDarts.addAll(mNewDarts);
        mNewDarts.clear();
    }
    
    void updateDart(Dart dart)
    {
        dart.updatePos();
        if (mBoard.isOutOfBounds(dart.mRow, dart.mCol)) {
            mDelDarts.add(dart);
        } else {
            int val = mBoard.mValues[dart.mRow][dart.mCol];
            if (val > 0) {
                mBoard.mValues[dart.mRow][dart.mCol]--; // update board immediately
                mDelDarts.add(dart); // delete dart after loop
                if (val == 1) {
                    explode(dart.mRow, dart.mCol);
                }
            }
        }
    }
    
    void explode(int row, int col)
    {
        mBoard.mNumPoppers--;
        Dart left = new Dart(row, col, -1, 0);
        Dart right = new Dart(row, col, 1, 0);
        Dart up = new Dart(row, col, 0, -1);
        Dart down = new Dart(row, col, 0, 1);
        mNewDarts.add(left);
        mNewDarts.add(right);
        mNewDarts.add(up);
        mNewDarts.add(down);
    }
    
    /**
     * Tap a board square, either in an actual game or simulated game.
     * Roughly speaking, everything below this is in the game model;
     * everything above this is part of the game view.
     */
    void tap(int row, int col)
    {
        if (--mBoard.mValues[row][col] == 0) {
            explode(row, col);
            do {
                step();
            } while (!mDarts.isEmpty() && mBoard.mNumPoppers != 0);
            if (mBoard.mNumPoppers < 1) {
                mPlayState = PlayState.Won;
            }
        }
    }
    
    void doTurn()
    {
        getTap();
        if (mPlayState == PlayState.Won) {
            Sx.puts("You WIN!");
        } else if (--mTurnsLeft < 1) {
            mPlayState = PlayState.Lost;
            Sx.puts("You lose.");
        }
    }
    
    // FIXME: make getTap return a Tap, move game play into doTurn
    void getTap() {
        String tapPrompt = "Enter tap row & col: ";
        int promptLen = tapPrompt.length();
        String spaces = Spaces.get(promptLen);
        while (true) {
            Sx.print(tapPrompt);
            String tapStr = Sx.getString();
            if (tapStr.length() > 2) {
                try (Scanner scanner = new Scanner(tapStr).useDelimiter("\\s+")) {
                    if (scanner.hasNextInt()) {
                        int tapRow = scanner.nextInt();
                        if (scanner.hasNextInt()) {
                            int tapCol = scanner.nextInt();
                            Sx.format("%s%d %d ", spaces, tapRow, tapCol);
                            if (mBoard.isOutOfBounds(tapRow, tapCol)) {
                                Sx.format("is out of bounds; try row < %d, col < %d\n",
                                        getNumRows(), getNumCols());
                            } else {
                                char val = mBoard.mValues[tapRow][tapCol];
                                if (val < 1) {
                                    Sx.puts("has no Popper; try again...");
                                } else {
                                    Sx.puts("is OK; tapping...");
                                    mTapSeq[mTurnCount] = new Tap(tapRow, tapCol, val);
                                    mBoardSeq[mTurnCount] = new PoppersBoard(mBoard);
                                    mTurnCount++;
                                    tap(tapRow, tapCol);
                                    break;
                                }
                            }
                        }
                    } else if (scanner.hasNext("....")) {		// accept only four-letter commands
                        String str = scanner.next();
                        if (str.equalsIgnoreCase("help"))
                            showHelp();
                        else if (str.equalsIgnoreCase("hint"))
                            showHint();
                        else if (str.equalsIgnoreCase("show"))
                            showBoard();
                        else if (str.equalsIgnoreCase("undo"))
                            undoLastTurn();
                        else
                            Sx.puts("Unrecognized command: " + str);
                    }
                }
            }
        }
    }   // end of getTap()
    
    /** If state is not already play or won or lost, play. Else, no-op. */
    void play() {
        if (mPlayState == PlayState.Pause) {
            mPlayState = PlayState.Play;
            if (mTapSeq == null) {
                // This is the first time the interactive aspect of this game
                // is starting up, so allocate these fields now (be lazy).
                mTapSeq = new Tap[mTurnsLeft];
                mBoardSeq = new PoppersBoard[mTurnsLeft];
                showHelp();
            }
            while (mPlayState == PlayState.Play) {
                doTurn();
            }
        }
    }
    
    /** If state is not already pause or won or lost, pause. Else, no-op. */
    void pause()
    {
        if (mPlayState == PlayState.Play) {
            mPlayState = PlayState.Pause;
        }
    }
    
    /**************************************************************************
     * The View, as it were.
     */
    public void showBoard() {
        showBoard(1);
    }
    
    public void showBoard(int verbose)
    {
        if (verbose > 0)
            Sx.format("%d poppers and %d turns left\n", mBoard.mNumPoppers, mTurnsLeft);
        mBoard.showValues();
    }
    
    public void showHelp()
    {
        Sx.format("When prompted, enter a row & col to tap [0 %d] & [0 %d],\n"
                , getNumRows(), getNumCols());
        Sx.puts("or one of the following commands: help hint undo show\n");
    }
    
    /**
     * Get solver for current board state. If minTaps > numTurnsLeft,
     * TODO: If the current board state is part of an already-known
     * solution, don't re-solve the game, just use the existing solution.
     * This may be non-trivial.
     */
    public void showHint()
    {
        PoppersSolver ps = new PoppersSolver(this);
        if (ps.minTaps() > mTurnsLeft) {
            Sx.format("From here you'd need %d taps, but you have only %d.  Undo?\n"
                    , ps.minTaps(), mTurnsLeft);
        } else {
            Tap hint = ps.mTapSeq[0];
            Sx.format("Hint: try %d %d\n", hint.mRow, hint.mCol);
        }
    }
    
    public void undoLastTurn()
    {
        if (mTurnCount > 0) {
            mTurnCount--;
            mTurnsLeft++;
            Sx.puts("Undoing " + mTapSeq[mTurnCount]);
            mBoard.copyBoardStateNiece(mBoardSeq[mTurnCount]);
            showBoard();
        } else {
            Sx.puts("Cannot undo a first turn not yet taken.");
        }
    }
    
    /**************************************************************************
     * static methods for generating games, etc.
     */
    
    /**
     * Generate random matrix of board values.
     * 
     * @param numRows
     * @param numCols
     * @param maxVal If non-zero, values must be in the range [1, maVal]
     * @param probZero Probability of a zero value assignment; must be in range [0.0, 1.0).
     * @return
     */
    public static char[][] randomBoardValues(int numRows, int numCols, int maxVal, double probZero)
    {
        assert (0 < maxVal && 0.0 <= probZero && probZero < 1.0);
        char values[][] = new char[numRows][numCols];
        Random rng = new Random();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                double randZero = Math.random();
                if (randZero <= probZero)
                    values[row][col] = 0;
                else
                    values[row][col] = (char) (1 + rng.nextInt(maxVal));
            }
        }
        return values;
    }
    
    public static PoppersGame makeRandomGame(int numRows, int numCols, int maxVal, double probZero)
    {
        char randValues[][] = randomBoardValues(numRows, numCols, maxVal, probZero);
        PoppersGame game = new PoppersGame(randValues, 0);
        return game;
    }
    
    public static int unit_test(int lvl)
    {
        String testName = PoppersGame.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        
        // PoppersGame game = PoppersTestGames.makeTestGame();
        PoppersGame game = PoppersTestGames.makeTestGame_3_18();
        game.showBoard();
        Sx.puts(game.mBoard);
        if (lvl > 1) {
            game.play();
        }
        
        Sx.puts(testName + " END");
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test(2);
    }
}
