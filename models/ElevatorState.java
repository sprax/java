package sprax.models;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sprax.Sx;
import sprax.threads.Threads;

/*
 *  TODO:
 *  Remove potential race conditions on all the request arrays?
 *  Replace arrays with heaps or sets, and synchronize on them.
 *  Fast partition & rank on request sets 
 *  
 *  Single elevator model & controller, no real view.
 *  
 *  Simple Model -- 3 System States:
 *    WAIT: If E is stopped with no current direction, wait for requests, 
 *      then go in whichever direction has more call or stop requests.
 *    UP:   If E is going up, keep going up until all up requests are filled,
 *      then stop with no current direction. 
 *    DOWN: If E is going down, keep going down until all down requests are filled,
 *      then stop with no current direction.
 *    
 *  Fourth State:
 *    STOP: As in stopped for an emergency or maintenance.  Don't go anywhere
 *      until unstopped, which puts the elevator into the WAIT state.
 *      
 *  More Complex: Use the elevator's capacity and current passenger count.
 *  
 *  Inputs:  time, elevator buttons, wall buttons, emergency intervention signals
 *      Input Commands to elevator controller:
 *          EnterStop(fn), CallForUp(fn), CallForDown(fn);
 *          CloseDoors, OpenDoors, EmergencyStop
 *      Other inputs: sensors and emergency signals
 *      
 *  Outputs: 
 *      Output Commands to elevator device (fn: floor number; cn: current floor):
 *          Command:                        |  Precondition:
 *          EmergencyStop(minTime, maxTime) |  None.
 *          CloseDoors                      |  None.
 *          OpenDoorsAndWait(minTime)       |  Stopped on a floor.
 *          MoveToFloor(fn)                 |  Doors closed 
 *          ChangeNextStopFloor(fn)         |  fn > cn if dir is up, fn < cn if dir is down
 *          OpenDoorsAndWait(minTime)       |  Stopped on a floor
 *          EmergencyStop(minTime, maxTime) |  None.
 *          
 *  Elevator States: Stopped(fn):    on floor fn, doors closed 
 *      Waiting(fn):    on floor fn, doors open
 *      Moving(dir, next fn): up or down, next floor that *could* be stopped at 
 *      EmergencyStopped(nfloor, nceiling, expireTime): 
 *          If nfloor == nceiling, it's stopped at a floor, and door-open/close commands
 *              should still work.  Other commands should not.
 *          If nfloor <  nceiling, it's stopped between those floors.
 *              Door-open/close commands should be disabled.
 *          Expiration: absolute time when to resume normal ops; 0 (default) means never.
 *  
 *  System States:
 *      The current Elevator State
 *          The basic on/off/off-line state of the system is not modeled, i.e.,
 *          in this model, the system is always "on".  
 *      All floor-change requests (door-opens/close requests don't count here) 
 *              
 *  State Transition rules:
 *      If the elevator is going up, stop at all e-requested floors > current
 *          and stop at all wall-up-requests on floors >= next possible stop.
 *      If it's going up and there are no more requests for floors > current,
 *          then, if there are requests for floors < current, go down;
 *          else, wait.
 *     If the elevator is going down, stop at all e-requested floors < current
 *          and stop at all wall-up-requests on floors <= next possible stop.
 *          
 *          
 *  @author sprax
 *
 */
public class ElevatorState
{
    public  enum Request { STOP_AT, CALL_UP, CALL_DN, HALT, UNHALT };
    protected static final Request sRequestByOrdinal[] = Request.values(); // for testing
    
    private      State    mState;
    private      State    mSaved;
    private      State    mHaltedState;
    private      State    mWaitingState;
    private      State    mRisingState;
    private      State    mSinkingState;
    
    public final int      mMinFloor;
    public final int      mMaxFloor;
    public final int      mNumFloors;   
    private      boolean  mElevStops[];
    private      boolean  mWallUpReqs[];
    private      boolean  mWallDownReqs[];
    //private      int      mNumUpReqs;
    //private      int      mNumDownReqs;
    
    protected    int      mCurrentFloor;
    protected    int      mVerbose;
    private      long     mWaitTimeMs     = 201; // polling interval
    private      long     mMoveUpTimeMs   = 800; // time to move up a floor
    private      long     mMoveDownTimeMs = 399; // time to move down a floor
    
    volatile boolean mvbRunInThread;
    
    /**  
     * The update period defaults to 500 milliseconds 
     */
    private long    mPeriodMs    =  333;
    static  long    sMinPeriodMs =  200;
    static  long    sMaxPeriodMs = 1200;
    
    long    mRequestExpiration;         // Twice the normal cycle time seems about right.
    
    Timer            mTimer;
    Thread           mThread;
    String           mName;
    SimpleDateFormat mDateFormat;
    
    // constructor
    public ElevatorState(String name, int minFloor, int maxFloor, long periodMs) 
    {
        if (name != null)
            mName = name;
        else
            mName = getClass().getCanonicalName();
        
        if (sMinPeriodMs <= periodMs && periodMs <= sMaxPeriodMs)
            mPeriodMs = periodMs;
        
        mDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");        
        
        mMinFloor     = minFloor;
        mMaxFloor     = maxFloor;
        mNumFloors    = maxFloor - minFloor + 1;
        mElevStops    = new boolean[mNumFloors];
        mWallUpReqs   = new boolean[mNumFloors];
        mWallDownReqs = new boolean[mNumFloors];
        
        mWaitingState = new WaitingState();
        mRisingState  = new RisingState();
        mSinkingState = new SinkingState();
        mHaltedState  = new HaltedState();
        mState        = mWaitingState;    
    }
    
    private void setState(State state)
    {
        if (mVerbose > 2)
            Sx.printf("     %s => %s\n", mState, state);
        mState = state;
        if (mState == mWaitingState)
            waitAt();               // Called only here to avoid repetition.
    }
    
    // Helper method, currently used by only the WaitinState
    protected void setRiseOrSinkState(int floor)
    {
        if (floor > mCurrentFloor)
            setState(mRisingState);
        else
            setState(mSinkingState);
    }
    
    
    /************************************************************************/
    /* States for the state machine                                         */
    /************************************************************************/
    
    /** Base class for State Design Pattern states.
     *  Provides default event handlers for all user requests, but not update
     */
    private static abstract class State 
    {    	
        // methods: event handlers
        boolean handleStopAt(ElevatorState sm, int floor) {
            return sm.addStopRequest(floor);
        }
        
        boolean handleCallUpAt(ElevatorState sm, int floor) {
            return sm.addCallUpRequest(floor);
        }
        
        boolean handleCallDownAt(ElevatorState sm, int floor) {
            return sm.addCallDownRequest(floor);
        }   
        
        /** "Emergency" Stop requested */
        boolean handleHalt(ElevatorState sm) {
            sm.haltNow();
            sm.mSaved = sm.mState;
            sm.setState(sm.mHaltedState);
            return true;
        }
        
        /** "Emergency" Stop canceled */
        boolean handleUnHalt(ElevatorState sm) {
            assert(sm.mSaved != sm.mHaltedState);
            sm.setState(sm.mSaved);            // Restore state prior to halt
            sm.unHalt();
            return true;
        }
        
        abstract void update(ElevatorState sm);
    }
    
    private static class WaitingState extends State 
    {
        @Override
        boolean handleStopAt(ElevatorState sm, int floor) {
            // elevator is just waiting idle, so immediately start moving to the requested floor 
            if (sm.addStopRequest(floor)) {
                sm.setRiseOrSinkState(floor);
                return true;
            }
            return false;
        }
        
        @Override
        boolean handleCallUpAt(ElevatorState sm, int floor) {
            // elevator is just waiting idle, so immediately start moving to the requested floor 
            if (sm.addCallUpRequest(floor)) {
                sm.setRiseOrSinkState(floor);
                return true;
            }
            return false;
        }
        
        @Override
        boolean handleCallDownAt(ElevatorState sm, int floor) {
            // elevator is just waiting idle, so immediately start moving to the requested floor 
            if (sm.addCallDownRequest(floor)) {
                sm.setRiseOrSinkState(floor);
                return true;
            }
            return false;
        }
        
        @Override
        void update(ElevatorState sm) { sm.updateWaitingState(); }
        
        @Override
        public String toString() { return "WAIT"; }  
    }
    
    private static class RisingState extends State 
    {
        @Override void update(ElevatorState sm) { sm.updateRisingState(); }    
        @Override public String toString() { return "RISE"; }  
    }
    
    private static class SinkingState extends State 
    {
        @Override void update(ElevatorState sm) { sm.updateSinkingState(); }
        @Override public String toString() { return "SINK"; }  
    }
    
    private static class HaltedState extends State 
    {
        /** Already in HaltedState, so do nothing! */ 
        @Override boolean handleHalt(ElevatorState sm) { return false; }
        @Override void update(ElevatorState sm) { sm.updateHaltState(); }
        @Override public String toString() { return "HALT"; }  
    }

    /**************************************************************************/
    /* Elevator updateStates methods, may be called from within State updates */
    /**************************************************************************/
    
    void updateWaitingState()
    {
        // Clear any no-op requests
        // waitAt(); -- only called in setState
        int up = numReqsUp();
        int dn = numReqsDown();
        if (up > dn)
            setState(mRisingState);
        else if (dn > 0)
            setState(mSinkingState);
        else // up <= dn && dn == 0, so up == 0
            Threads.tryToSleep(mWaitTimeMs);
    }
    
    void updateRisingState()
    {
        if (numReqsUp() > 0) {
            moveUp();
        } else if (numReqsDown() > 0) { 
            setState(mSinkingState);
        } else {
            setState(mWaitingState);
        }
    }
    
    void updateSinkingState()
    {
        if (numReqsDown() > 0) {
            moveDown();
        } else if (numReqsUp() > 0) {
            setState(mRisingState);
        } else {
            setState(mWaitingState);
        }
    }
    
    void updateHaltState()
    {
        Threads.tryToSleep(mWaitTimeMs);
    }
    
    /************************************************************************/
    /* Elevator mechanics methods, called by States update methods          */
    /************************************************************************/
    
    void waitAt()   // TODO: refactor so this can only be called from WaitingState?  No...
    {
        if (mVerbose > 0)
            Sx.printf("%s Wait at %2d\n", mState, mCurrentFloor);
    }

    void moveUp() 
    { 
        Threads.tryToSleep(mMoveUpTimeMs);
        if (++mCurrentFloor > mMaxFloor)
            Sx.puts("                        current floor OOB: " + mCurrentFloor);
        int nextUp = nextUpStop();
        if (mVerbose > 0) {
            String action;
            String reason;
            if (mElevStops[mCurrentFloor]) {
                mElevStops[mCurrentFloor] = false;
                action = "Stop at";
                reason = "for discharge";
                if (mWallUpReqs[mCurrentFloor]) {
                    mWallUpReqs[mCurrentFloor] = false;
                    reason.concat("and pick-up");
                }
            } else if (mWallUpReqs[mCurrentFloor]) {
                mWallUpReqs[mCurrentFloor] = false;
                action = "Stop at";
                reason = "for pick-up";
            } else if (mCurrentFloor == nextUp) {
                mWallDownReqs[mCurrentFloor] = false;
                action = "Stop at";
                reason = "for pick-up";
            } else {
                action = "Pass up";
                reason = "en route to " + nextUp;
            }       
            Sx.printf("%s %s %2d %s\n", mState, action, mCurrentFloor, reason);
        } else {
            if (mElevStops[mCurrentFloor]) {
                mElevStops[mCurrentFloor] = false;
                if (mWallUpReqs[mCurrentFloor]) {
                    mWallUpReqs[mCurrentFloor] = false;
                }
            } else if (mWallUpReqs[mCurrentFloor]) {
                mWallUpReqs[mCurrentFloor] = false;
            } else if (mCurrentFloor == nextUp) {
                mWallDownReqs[mCurrentFloor] = false;
            }       
        }
    }
    
    void moveDown() // TODO: handle non-verbose 
    { 
        Threads.tryToSleep(mMoveDownTimeMs);
        mCurrentFloor--; 
        int nextDown = nextDownStop();
        if (mVerbose > 0) {
            String action;
            String reason;
            if (mElevStops[mCurrentFloor]) {
                mElevStops[mCurrentFloor] = false;
                action = "Stop at";
                reason = "for discharge";
                if (mWallDownReqs[mCurrentFloor]) {
                    mWallDownReqs[mCurrentFloor] = false;
                    reason.concat("and pick-up");
                }
            } else if (mWallDownReqs[mCurrentFloor]) {
                mWallDownReqs[mCurrentFloor] = false;
                action = "Stop at";
                reason = "for pick-up";
            } else if (mCurrentFloor == nextDown) {
                mWallUpReqs[mCurrentFloor] = false;
                action = "Stop at";
                reason = "for pick-up";
            } else {
                action = "Pass dn";
                reason = "en route to " + nextDown;
            }    	
            Sx.printf("%s %s %2d %s\n", mState, action, mCurrentFloor, reason);
        }
    }
    
    void haltNow()
    {
        if (mVerbose > 1) {
            Sx.printf("     Hndl by %s:  HALT at or near %d:  %s\n"
                    , mState, mCurrentFloor, (mState != mHaltedState ? "NEW" : "dupe"));
        }
        if (mVerbose > 0)
            Sx.printf("%s Halt at %2d\n", mState, mCurrentFloor);
    }
    
    void unHalt()
    {
        if (mVerbose > 0)
            Sx.printf("%s unHalt  %2d\n", mState, mCurrentFloor);
    }
        
    /************************************************************************/
    /* Elevator logic: decide the direction and floor of the next stop      */
    /************************************************************************/

    public int nextUpStop()
    {
        // Find the next stop or call-up request above the current floor.
        int floor = mCurrentFloor;
        while (++floor <= mMaxFloor)
        {
            if (mElevStops[floor] || mWallUpReqs[floor])
                return floor;
        }
        // If there were no more stop or call-up requests above,
        // there must be at least one call-down request up there.
        // Find the *highest* one.  The elevator will stop at any 
        // lower call-down floors on the way down from the topmost one.
        while (--floor >= mCurrentFloor)
        {
            if (mWallDownReqs[floor])
                return floor;
        }
        // Still nothing?  Maybe it's an error or cancellation.
        return mCurrentFloor;   // Should result in a no-op/reset.
    }

    public int nextDownStop()
    {
        // Find the next stop or call-down request below the current floor.
        int floor = mCurrentFloor;
        while (--floor >= 0)
        {
            if (mElevStops[floor] || mWallDownReqs[floor])
                return floor;
        }
        // If there were no more stop or call-down requests below,
        // then there must be at least one call-up request down there.
        // Find the lowest one.
        while (++floor < mNumFloors)
        {
            if (mWallUpReqs[floor])
                return floor;
        }
        // Still nothing?  Maybe it's an error or cancellation.
        return mCurrentFloor;   // Should result in a no-op/reset.
    }
    
    /** return the total number of requested stops above current floor */
    public int numReqsUp()
    {
        int count = 0;
        for (int j = mCurrentFloor; ++j < mNumFloors; ) {
            if (mElevStops[j])
                count++;
            if (mWallUpReqs[j])
                count += 1;     // Count it as one, even tho it will be followed by another
            if (mWallDownReqs[j])
                count += 1;
        }
        return count;
    }
    
    /** return the total number of requested stops below current floor */
    public int numReqsDown()
    {
        int count = 0;
        for (int j = mCurrentFloor; --j >= 0; ) {
            if (mElevStops[j])
                count++;
            if (mWallUpReqs[j])
                count += 1;
            if (mWallDownReqs[j])
                count += 1;     // Count it as one, even tho it will be followed by another
        }
        return count;
    }
    
    /************************************************************************/
    /* Test methods and unit tests                                          */
    /************************************************************************/

    /** Test method: translates floor and request type into a request */
    boolean addRequest(int floor, Request reqType)
    {
        if (mVerbose > 1)
            Sx.puts("     Recv Request:  " + reqType + " " + floor );
        if (floor < 0 || floor >= mNumFloors)
            return false;
        switch (reqType) {
        case STOP_AT:
            return mState.handleStopAt(this, floor);
        case CALL_UP:
            return mState.handleCallUpAt(this, floor);
        case CALL_DN:
            return mState.handleCallDownAt(this, floor);
        case HALT:
            return mState.handleHalt(this);
        case UNHALT:
            return mState.handleUnHalt(this);
        }
        return false;
    }
    
    boolean addStopRequest(int floor) 
    {
        boolean added = false;
        if (floor != mCurrentFloor) {
            if (mElevStops[floor] == false) {
                mElevStops[floor]  = true;
                added = true;                    // new stop request, added
            }
        }
        if (mVerbose > 1) {
            String compare = floor < mCurrentFloor ? "<" : (floor > mCurrentFloor ? ">" : "=");
            String novelty = added ? "NEW" : (floor == mCurrentFloor ? "No-op" : "dupe");
            Sx.printf("     Hndl by %s:  %s %d  %s  %d:  %s\n"
                    , mState, Request.STOP_AT, floor, compare, mCurrentFloor, novelty);
        }
        return added;
    }    
    
    boolean addCallUpRequest(int floor)
    {
        // TODO: Special case: should be no Call-UP request on the top floor
        boolean added = false;
        assert(mMinFloor <= floor && floor <= mMaxFloor);
        if (mWallUpReqs[floor] == false) {
            mWallUpReqs[floor]  = true;
            added = true;
        }
        if (mVerbose > 1) {
            Sx.printf("     Hndl by %s:  %s %d  %s  %d:  %s\n"
                    , mState, Request.CALL_UP, floor, (floor < mCurrentFloor ? "<" : ">")
                    , mCurrentFloor, (added ? "NEW" : "dupe"));
        }
        return added;
    }
    
    boolean addCallDownRequest(int floor)
    {
        boolean added = false;
        assert(mMinFloor <= floor && floor <= mMaxFloor);
        if (mWallDownReqs[floor] == false) {
            mWallDownReqs[floor]  = true;
            added = true;
        }
        if (mVerbose > 1) {
            Sx.printf("     Hndl by %s:  %s %d  %s  %d:  %s\n"
                    , mState, Request.CALL_DN, floor, (floor < mCurrentFloor ? "<" : ">")
                    , mCurrentFloor, (added ? "NEW" : "dupe"));
        }
        return added;
    }
    
    /**************************************************************************/
    /* Update Loops: Run state-machine in a timer or other thread, or in main */
    /**************************************************************************/


    /**
     * Use this to run the update loop using a timer.
     * Usage:
     <br><code>
        mTimer = new Timer();
        <br>
        mTimer.schedule(new UpdateTask(this), 0, mPeriodMs);
     </code>
     */
    class UpdateTask extends TimerTask 
    {    
        private final ElevatorState mElevator;
        
        UpdateTask(ElevatorState elevatorState) {
            mElevator = elevatorState;
        }
        
        //@SuppressWarnings("synthetic-access")
        @Override
        public void run()
        {
            mState.update(mElevator);
        }
    }
    
    
    /**
     * Run using updates in a timer
     */
    void startUsingTimer()
    {
        if (mTimer != null)
            return;
        mTimer      = new Timer();
        mTimer.schedule(new UpdateTask(this), 0, mPeriodMs);
    }
    
    /**
     * Use this method to run the update loop in a thread w/o a timer.
     * Usage:
     * new Thread(new Runnable() {
            public void run() { runInThread(); }
        }).start();
     */
    public void runInThread()
    {
        Thread thread = Thread.currentThread();
        while ( ! thread.isInterrupted() && mvbRunInThread) {
            mState.update(this);
        }
        boolean inted = thread.isInterrupted();
        Sx.puts("runInThread was interrupted? " + inted + "; ending...");
    }   

    
    void startUsingThread()
    {
        mvbRunInThread  = true;
        mThread = new Thread(new Runnable() {
            public void run() { runInThread(); }
        });
        mThread.start();
    }
    
    /** 
     * Cancels all update tasks and kills the timer;
     * Not safe for normal operation.  This turns the 
     * state machine off, as if an actual machine were powered off.
     */
    protected void finish()
    {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer  = null;
        }
        if (mThread != null) {
            mThread.interrupt();
            mvbRunInThread = false;
            ////mThread.stop();     // TODO: How to make it die gracefully?
            mThread = null;
        }
        
    }
    
    
    public static int unit_test(int level) 
    {
        String testName = ElevatorState.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");  
        int stat = 0;
        
        Random rng = new Random();
        if (level > 0) {
            ElevatorState elevator = new ElevatorState("El Ten", 0, 9, 101);
            elevator.mVerbose = 3;
            
            int  numReqs  =  15, floor, reqIdx;
            int  jHalt    =  rng.nextInt(numReqs/2); 
            int  jUnHalt  =  jHalt + 2 + rng.nextInt(numReqs/3);
            long baseTime = 357; 
            
            //elevator.startUsingThread();
            elevator.startUsingTimer();
            
            for (int j = 0; j < numReqs; j++) {
                long randTime = rng.nextInt(2000);
                Threads.tryToSleep(baseTime + randTime);
                floor  = rng.nextInt(elevator.mNumFloors);
                if (j == jHalt) {
                    reqIdx = Request.HALT.ordinal();
                } else if (j == jUnHalt) {
                    reqIdx = Request.UNHALT.ordinal();
                } else {
                    reqIdx = rng.nextInt(sRequestByOrdinal.length - 2);		// fragile
                }
                Request request   = sRequestByOrdinal[reqIdx];
                if (elevator.mVerbose > 1) {                
                    if (reqIdx < sRequestByOrdinal.length - 2)
                        Sx.printf("     Send Request:  %s %d\n", request, floor);
                    else
                        Sx.printf("     Send Request:  %s\n", request);
                }
                elevator.addRequest(floor, request);
            }
            
            Threads.tryToSleep(800);
            elevator.finish();
        }
        
        Sx.puts(testName + " END");  
        return stat;
    }    
    
    public static void main(String[] args)
    {
        unit_test(1);
    }    
    
}
