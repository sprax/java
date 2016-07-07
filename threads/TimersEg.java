package sprax.threads;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * An example on multiple timers.
 *
 * @author http://www.gammelsaeter.com/
 */
public class TimersEg {
    
    public static void main(String[] args) 
    {
        Timer timer1 = new Timer();             // Get timer 1
        Timer timer2 = new Timer();             // get timer 2
        
        long delay1 = 5*1000;                   // 5 seconds delay
        long delay2 = 3*1000;                   // 3 seconds delay
        
        // Schedule the two timers to run with different delays.
        timer1.schedule(new Task("timer1"), 0, delay1);
        timer2.schedule(new Task("timer2"), 0, delay2);
    }
    
}
    
/**
 * This is a timertask because it extends the class java.util.TimerTask. This class
 * will be given to the timer (java.util.Timer) as the code to be executed.
 *
 * @see java.util.Timer
 * @see java.util.TimerTask
 * @author http://www.gammelsaeter.com/
 */
class Task extends TimerTask {
    
    private String mName;                 // A string to output
    
    /**
     * Constructs the object, sets the string to be output in function run()
     * @param str
     */
    Task(String name) {
        mName = name;
    }
    
    /**
     * When the timer executes, this code is run.
     */
    public void run() {
        // Get current date/time and format it for output
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        String current_time = format.format(date);
        
        // Output to user the name of the object and the current time
        System.out.println(mName + " - Current time: " + current_time);
    }
}
