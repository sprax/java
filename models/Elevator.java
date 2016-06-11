package sprax.models;

import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import sprax.sprout.Sx;
import sprax.threads.Threads;

/*
 * FIXME:
 *  Remove race conditions on all the request arrays -- why init outside constructor works?
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
 *      then stop with no current direction. j
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
public class Elevator
{
    public  enum State   { WAIT, RISE, SINK, HALT };
    public  enum Request { STOP_AT, CALL_UP, CALL_DOWN, HALT, UNHALT };
    protected static final Request sRequestByOrdinal[] = Request.values(); // for testing
    
    class UpdateTask extends TimerTask 
    {    
        private final Elevator mElevator;
        
        UpdateTask(Elevator elevator) {
            mElevator = elevator;
        }
        
        @SuppressWarnings("synthetic-access")
        @Override
        public void run()
        {
            if (mHaltRequest == true) {
                mHaltRequest = false;
                // Save state for when elevator resumes, but can't save HALT
                if (mState == State.HALT)
                    mSaved  = State.WAIT;
                else
                    mSaved = mState;
                mState = State.HALT;
                haltAt();
            } else if (mUnHaltRequest == true) {
                mUnHaltRequest = false;
                mState = mSaved;            // Restore state
                unHalt();
            }
            switch(mState) {
                case WAIT:
                    int up = numReqsUp();
                    int dn = numReqsDown();
                    if (up > dn)
                        mState = State.RISE;
                    else if (dn > 0)
                        mState = State.SINK;
                    else // up <= dn && dn == 0, so up == 0
                        Threads.tryToSleep(mWaitTimeMs);
                    break;
                    
                case RISE:
                    if (numReqsUp() > 0) {
                        moveUp();
                    } else {
                        mState = State.WAIT;
                        waitAt();
                    }
                    break;                    
                    
                    
                case SINK:
                    if (numReqsDown() > 0) {
                        moveDown();
                    } else {
                        mState = State.WAIT;
                        waitAt();
                        }
                    break;
                    
                case HALT:
                    break;
                    
            }
            // Debugging output
            // long timeNow = System.currentTimeMillis();
            // Sx.puts(mElevator.mName + " ctms: " + timeNow);
        }
    }
    
    public final int      mNumFloors;
    private      boolean  mElevStops[];
    private      boolean  mWallUpReqs[];
    private      boolean  mWallDownReqs[];
    private      boolean  mHaltRequest;     // "Emergency" Stop requested
    private      boolean  mUnHaltRequest;   // "Emergency" Stop cancelled
    //private      int      mNumUpReqs;
    //private      int      mNumDownReqs;
    
    protected    int      mCurrentFloor;
    protected    int      mVerbose;
    protected    State    mState = State.WAIT;
    protected    State    mSaved;
    private      long     mWaitTimeMs     = 201; // polling interval
    private      long     mMoveUpTimeMs   = 500; // time to move up a floor
    private      long     mMoveDownTimeMs = 399; // time to move down a floor
    
    volatile boolean mvbRunInThread;
    
    /**  
     * The update period defaults to 500 milliseconds:
     * about the usual time of a Red-all-ways 
     */
    private long    mPeriodMs    = 500L;
    static  long    sMinPeriodMs =  200;
    static  long    sMaxPeriodMs = 1200;
    
    long    mRequestExpiration;         // Twice the normal cycle time seems about right.
    
    Timer           mTimer;
    Thread          mThread;
    String          mName;
    SimpleDateFormat mDateFormat;
    
    
    public Elevator(String name, int numFloors, long periodMs) 
    {
        if (name != null)
            mName = name;
        else
            mName = getClass().getCanonicalName();
        
        if (sMinPeriodMs <= periodMs && periodMs <= sMaxPeriodMs)
            mPeriodMs = periodMs;
        
        mDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");        
        
        mNumFloors    = numFloors;
        mElevStops    = new boolean[mNumFloors];
        mWallUpReqs   = new boolean[mNumFloors];
        mWallDownReqs = new boolean[mNumFloors];
        mState        = State.WAIT;
        
    }
    
    /**
     * Use this to run the update mechanism in a thread w/o a timer.
     * Usage:
     * new Thread(new Runnable() {
            public void run() { runInThread(); }
        }).start();
     */
    public void runInThread()
    {
        Thread thread = Thread.currentThread();
        while ( ! thread.isInterrupted() && mvbRunInThread) {
            if (mHaltRequest == true) {
                mHaltRequest  = false;
                // Save state for when elevator resumes, but can't save HALT
                if (mState == State.HALT)
                    mSaved  = State.WAIT;
                else
                    mSaved = mState;
                mState = State.HALT;
                haltAt();
            } else if (mUnHaltRequest == true) {
                mUnHaltRequest = false;
                mState = mSaved;            // Restore state
                unHalt();
            }
            switch(mState) {
                case WAIT:
                    // Clear any no-op requests
                    if (mElevStops[mCurrentFloor] == true)
                        mElevStops[mCurrentFloor]  = false;			// Clear any no-op requests
                    int up = numReqsUp();
                    int dn = numReqsDown();
                    if (up > dn)
                        mState = State.RISE;
                    else if (dn > 0)
                        mState = State.SINK;
                    else // up <= dn && dn == 0, so up == 0
                        Threads.tryToSleep(mWaitTimeMs);
                    break;
                    
                case RISE:
                    if (numReqsUp() > 0) {
                        moveUp();
                        Threads.tryToSleep(mMoveUpTimeMs);
                    } else {
                        mState = State.WAIT;
                        waitAt();
                    }
                    break;
                    
                case SINK:
                    if (numReqsDown() > 0) {
                        moveDown();
                        Threads.tryToSleep(mMoveDownTimeMs);
                    } else {
                        mState = State.WAIT;
                        waitAt();
                    }
                    break;
                    
                case HALT:
                    Threads.tryToSleep(mWaitTimeMs);
                    break;
                    
            }
            // Debugging output
            // long timeNow = System.currentTimeMillis();
            // Sx.puts(mName + " time: " + timeNow);
        }
        boolean inted = thread.isInterrupted();
        Sx.puts("runInThread was interrupted? " + inted + "; ending...");
        
    }   
    
    
    void startInThread()
    {
        mvbRunInThread  = true;
        mThread = new Thread(new Runnable() {
            public void run() { runInThread(); }
        });
        mThread.start();
    }
    
    /**
     * Start normal operation: as in all blinking red, then the normal cycle.
     */
    protected void startInTimer()
    {
        if (mTimer != null)
            return;
        mTimer      = new Timer();
        mTimer.schedule(new UpdateTask(this), 0, mPeriodMs);
    }
    
    /** 
     * Cancels all update tasks and kills the timer;
     * Not safe for normal operation.  This turns the 
     * traffic light off, as if were powered off.
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
    
    
    void moveUp() 
    { 
        mCurrentFloor++;
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
            Sx.format("%s %s %2d %s\n", mState.toString(), action, mCurrentFloor, reason);
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
            Sx.format("%s %s %2d %s\n", mState.toString(), action, mCurrentFloor, reason);
        }
    }
    
    void waitAt()
    {
        if (mVerbose > 0)
            Sx.format("%s Wait at %2d\n", mState, mCurrentFloor);
    }
    
    void haltAt()
    {
        if (mVerbose > 0)
            Sx.format("%s Halt at %2d\n", mState, mCurrentFloor);
    }
    
    void unHalt()
    {
        if (mVerbose > 0)
            Sx.format("%s unHalt  %2d\n", mState, mCurrentFloor);
    }
    
    boolean addRequest(int floor, Request reqType)
    {
        if (mVerbose > 1)
            Sx.puts("     Recv Request:  " + reqType + " " + floor );
        if (floor < 0 || floor >= mNumFloors)
            return false;
        switch (reqType) {
            case STOP_AT:
                if (mElevStops[floor] == false) {
                    mElevStops[floor]  = true;
                    return true;
                }
                return false;
            case CALL_UP:
                // TODO: Special case: should be no CALL_UP request on the top floor
                if (mWallUpReqs[floor] == false) {
                    mWallUpReqs[floor]  = true;
                    return true;
                }
                return false;
            case CALL_DOWN:
                // TODO: Special case: should be no CALL_DOWN request on the bottom floor
                if (mWallDownReqs[floor] == false) {
                    mWallDownReqs[floor]  = true;
                    return true;
                }
                return false;
            case HALT:
                if (mHaltRequest == false) {
                    mHaltRequest  = true;
                    return true;
                }
                return false;
            case UNHALT:
                if (mUnHaltRequest == false) {
                    mUnHaltRequest  = true;
                    return true;
                }
                return false;
        }
        return false;
    }
    
    
    
    public int nextUpStop()
    {
        // Find the next stop or call-up request above the current floor.
        int j = mCurrentFloor;
        while (++j < mNumFloors) {
            if (mElevStops[j] || mWallUpReqs[j])
                break;
        }
        if (j == mNumFloors) {
            // If there were no more stop or call-up requests above,
            // there must be at least one call-down request up there.
            // Find the highest one.
            while (--j >= mCurrentFloor) {
                if (mWallDownReqs[j])
                    break;
            }
        }
        // Still nothing?  Maybe it's an error or cancellation.
        return j;
    }
    
    public int nextDownStop()
    {
        // Find the next stop or call-down request below the current floor.
        int j = mCurrentFloor;
        while (--j >= 0) {
            if (mElevStops[j] || mWallDownReqs[j])
                break;
        }
        if (j < 0) {
            // If there were no more stop or call-down requests below,
            // then there must be at least one call-up request down there.
            // Find the lowest one.
            while (++j < mNumFloors) {
                if (mWallUpReqs[j])
                    break;
            }
        }
        // Still nothing?  Maybe it's an error or cancellation.
        return j;
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
    
    public static int unit_test(int level) 
    {
        String testName = Elevator.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");  
        int stat = 0;
        
        Random rng = new Random();
        if (level > 0) {
            Elevator elevator = new Elevator("El Ten", 10, 101);
            elevator.mVerbose = 2;
            
            int  numReqs  =  15, floor, reqIdx;
            int  jHalt    =  rng.nextInt(numReqs/2); 
            int  jUnHalt  =  jHalt + 1 + rng.nextInt(numReqs/3);
            long baseTime = 357; 
            
            //elevator.startInThread();
            elevator.startInTimer();
            
            for (int j = 0; j < numReqs; j++) {
                long randTime = rng.nextInt(2000);
                Threads.tryToSleep(baseTime + randTime);
                floor  = rng.nextInt(elevator.mNumFloors);
                if (j == jHalt)
                    reqIdx = Request.HALT.ordinal();
                else if (j == jUnHalt)
                    reqIdx = Request.UNHALT.ordinal();
                else
                    reqIdx = rng.nextInt(sRequestByOrdinal.length - 2);		// fragile
                Request req = sRequestByOrdinal[reqIdx];
                if (elevator.mVerbose > 1)
                    Sx.puts("     Send Request:  " + req + " " + floor );
                elevator.addRequest(floor, req);
            }
            
            Threads.tryToSleep(15000);
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
