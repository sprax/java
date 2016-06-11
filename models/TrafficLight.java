package sprax.models;

//import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import sprax.sprout.Sx;

public class TrafficLight
{
    /*
     * Enumerated states, constructed as a cyclic list with 
     * multiple entries (or, directed cyclic graph with one loop).
     * All "internal" States.  The "display" state (actual lights) 
     * could be regarded as different (broken, disconnected, etc.)  
     * Colors: Green, Yellow, Red.  Directions: east-west, north-south.
     * The normal cyclic ordering of states is to start with RedBlinkAll,
     * then this loop: <RedEastWest, GrnNorthSouth, YelNorthSouth,
     *                  RedNorthSouth, GrnEastWest, YelEastWest>, repeat.
     * Two yellow-blinking states go to the one red-blinking state, which
     * of course then enters the loop as above.
     * For safety considerations, any state transition into or out of the 
     * cycle always starts from an all red state. 
     * 
     * TODO: delay between requests: 2 cases:
     *      1.  if the priority of the next req (pek) is > than current, make it wait.
     *      2.  if a ped request is being executed, the min time for favorable light
     *          should be longer than a ped minimum, and commands may also go to a
     *          ped signal (white man/orange man).
     *  TODO: Remove out-dated or already filled requests.
     */
    enum State
    {
        // The normal cycle:
        RedEastWest(true, null),          // Red for all directions, after Yellow-east-west
        YelEastWest(true, RedEastWest),   // implies north-south lights are still red
        GrnEastWest(true, YelEastWest),   // implies north-south lights are already Red
        RedNorthSouth(true, GrnEastWest),   // Red for all, after Yellow-north-south
        YelNorthSouth(true, RedNorthSouth), // implies NOT Yns, AND east-west lights still Red
        GrnNorthSouth(true, YelNorthSouth), // implies east-west lights already Red
        // Extra entry points:
        RedBlinkAll(false, RedEastWest),        // Blinking red in all directions (stop, then go)
        YelBlinkEastWest(false, RedBlinkAll),  // Blinking yellow east-west, blinking red north-south
        YelBlinkNorthSouth(false, RedBlinkAll); // Blinking yellow north-south, blinking red east-west
        
        static {
            RedEastWest.mNext = GrnNorthSouth;   // Close the circle
        }
        final boolean mInCircle;
        State         mNext;
        long          mTTL    = 5000L;    // Default time-to-live for safety
        long          mMinTTL = 1000L;    // Minimum time-to-live for safety
                                       
        State(boolean circular, State nextState) {
            mInCircle = circular;
            mNext = nextState;
        }
    };
    
    static boolean isAllRed(State state) {
        return (state == State.RedEastWest ||
                state == State.RedNorthSouth || state == State.RedBlinkAll);
    }
    
    /**
     * Participants: cars, pedestrians, emergency vehicles.
     * Consider adding trolleys, bicycles, and buses.
     */
    public enum User
    {
        Emv(0),         // Emergency vehicles get first priority.
        Ped(2),         // Pedestrian request beats the rest
        Sys(5),         // Requests for blinking states
        Car(9);
        
        public final int mPriority;
        
        User(int priority) {
            mPriority = priority;
        }
    }
    
    /**
     * Possible Requests (events that can alter timing):
     * A pedestrian, car, or emergency vehicle/system can
     * ask for all red, or green east-west or green north-south.
     */
    class Request implements Comparable<Request>
    {
        User  mUser;
        State mRequestedState;
        Long  mRequestTime;
        
        Request(User user, State requestedState, long reqTime) {
            mUser = user;
            mRequestedState = requestedState;
            mRequestTime = reqTime;
        }
        
        @Override
        public int compareTo(Request o)
        {
            if (mUser.mPriority < o.mUser.mPriority)
                return -1;
            if (mUser.mPriority > o.mUser.mPriority)
                return 1;
            return 0;
        }
    }
    
    PriorityBlockingQueue<Request> mRequestQ;
    
    class UpdateTask extends TimerTask
    {
        private TrafficLight mTrafficLight;
        private long         mTimeLastChange;
        
        UpdateTask(TrafficLight trafficLight) {
            mTrafficLight = trafficLight;
        }
        
        @Override
        public void run()
        {
            long timeNow = System.currentTimeMillis();
            
            Request req;
            if ((req = mRequestQ.poll()) != null) {
                
                Sx.puts("\nHandling Request: " + req.mUser + "-" + req.mRequestedState);
                if (timeNow - req.mRequestTime > mRequestExpiration) {
                    Sx.puts("That request is expired!");
                } else if (req.mRequestedState.mInCircle) {
                    for (State state = mState; state != req.mRequestedState; state = state.mNext) {
                        if (state.mTTL > state.mMinTTL)
                            state.mTTL = state.mMinTTL;
                    }
                } else {
                    if (isAllRed(mState)) {
                        mTrafficLight.changeState();
                    } else {
                        // Keep sending this request back to the end of the 
                        // queue to wait for an all-red state.  Do not
                        // busy-wait or block the queue.
                        mRequestQ.add(req);
                    }
                }
            }
            
            long timeSinceChange = timeNow - mTimeLastChange;
            if (timeSinceChange >= mState.mTTL) {
                mTimeLastChange = timeNow;
                changeState();
            }
            
            // Debugging output
            // Sx.puts(mTrafficLight.mName + " ctms: " + timeNow);
        }
    }
    
    /**
     * The update period defaults to 500 milliseconds:
     * about the usual time of a Red-all-ways
     */
    private long     mPeriodMs    = 500L;
    static long      sMinPeriodMs = 200;
    static long      sMaxPeriodMs = 1200;
    static long      sMinExpireMs = 1000;
    
    long             mRequestExpiration;              // Twice the normal cycle time seems about right.
                                                       
    Timer            mTimer;
    State            mState       = State.RedBlinkAll;   // initialize to default: all blinking reds.
    String           mName;
    SimpleDateFormat mDateFormat;
    
    public TrafficLight(String name, long periodMs)
    {
        if (name != null)
            mName = name;
        else
            mName = getClass().getCanonicalName();
        
        if (sMinPeriodMs <= periodMs && periodMs <= sMaxPeriodMs)
            mPeriodMs = periodMs;
        
        mDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        mRequestQ = new PriorityBlockingQueue<Request>();
        
        // Requests should expire after about maybe one normal cycle time.
        State endState = State.RedEastWest;    // Any state in the cycle will do.
        mRequestExpiration = endState.mTTL;
        for (State state = endState.mNext; state != endState; state = state.mNext)
            mRequestExpiration += state.mTTL;
    }
    
    protected void startNormal()
    {
        if (mTimer != null)
            return;
        mTimer = new Timer();
        mTimer.schedule(new UpdateTask(this), 0, mPeriodMs);
    }
    
    protected void changeState()
    {
        Calendar cal = Calendar.getInstance();
        mState = mState.mNext;
        mState.mTTL = getNormalTTL(mState, cal);
        
        int status = changeLights();
        
        // Log the name of traffic light and the date/time of this state change
        Date calTime = cal.getTime();
        String formattedDataTime = mDateFormat.format(calTime);
        Sx.format("%d: %s changed at %s to %s\n", status, mName, formattedDataTime, mState);
    }
    
    public static long getNormalTTL(State state, Calendar cal)
    {
        long ttl;
        switch (state) {
        case GrnEastWest:
        case GrnNorthSouth:
            ttl = 15500;     // Time for green, before yellow
            break;
        case YelEastWest:
        case YelNorthSouth:
            ttl = 5000;
            break;
        case RedEastWest:
        case RedNorthSouth:
            ttl = 500;     // All directions red before one turns green
            break;
        case RedBlinkAll:
            ttl = 20000;
            break;
        case YelBlinkEastWest:
        case YelBlinkNorthSouth:
            ttl = 18000;
            break;
        default:
            ttl = 10000;
        }
        
        int minutes = cal.get(Calendar.MINUTE);
        if (15 * 30 <= minutes && minutes <= 17 * 30)
            ttl = ttl * 4 / 3;
        
        return ttl;
    }
    
    /**
     * Send the command to change the lights.
     * Do not get caught waiting, but do return a status code.
     * 
     * @return 0 for OK, non-zero for error status.
     */
    private static int changeLights()
    {
        return 0;
    }
    
    public static void tryToSleep(long millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return;
        }
    }
    
    public static int unit_test(int level)
    {
        String testName = TrafficLight.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        int stat = 0;
        
        if (level > 0) {
            TrafficLight trafficLight = new TrafficLight(null, 1200);
            trafficLight.startNormal();
            
            long timeNow = System.currentTimeMillis();
            
            Request pedGew = trafficLight.new Request(User.Ped, State.GrnEastWest, timeNow);
            Request carGew = trafficLight.new Request(User.Ped, State.GrnEastWest, timeNow / 2); // expired
            Request pedRew = trafficLight.new Request(User.Ped, State.RedEastWest, timeNow);
            Request emvRns = trafficLight.new Request(User.Ped, State.GrnEastWest, timeNow);
            Request reqs[] = { pedGew, carGew, pedRew, emvRns };
            for (int j = 0; j < 12; j++) {
                tryToSleep(2345);
                Request req = reqs[j % reqs.length];
                Sx.puts("\nAdding Request:   " + req.mUser + "-" + req.mRequestedState);
                trafficLight.mRequestQ.add(req);
            }
        }
        
        Sx.puts(testName + " END");
        return stat;
    }
    
    public static void main(String[] args)
    {
        unit_test(1);
    }
}

/*******************
 * hi
 * 
 * 4-way traffic intersection
 * no turns, cars coming from 4 dirs
 * standard green, yellow, red.
 * pedestrian cross-walk w/ button.
 * car detector on road.
 * 
 * Safety: no green both ways, no green for too short, cycles in the right order.
 * Complex business logic from events.
 * 
 * class FourWayIntersection {
 * 
 * enum State{ EastGreen, EastYellow, EastRed, NorthYellow };
 * 
 * enum Event { EastPed, NorthPed, EastCar, NorthCar };
 * 
 * final State[] stateOrder;
 * private int stateIndex;
 * 
 * Queue<Event> eventQ;
 * 
 * FourWayIntersection()
 * {
 * stateOrder = new State[4];
 * stateIndex = 0;
 * 
 * time currentStateStartTime;
 * 
 * // before switching states:
 * timeNow = getCurrent(time);
 * timeInCurrentState = timeNow - currentStateStartTime;
 * if (timeInCurrentState < minTimeInState.get(currentState) {
 * delay...
 * }
 * 
 * 
 * 
 * }
 ***************/
