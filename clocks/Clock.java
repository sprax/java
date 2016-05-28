package sprax.clocks;

import sprax.Sx;
import sprax.Sz;

public abstract class Clock
{
    public Clock()
    {
        // TODO Auto-generated constructor stub
    }
    
    static double degreesClockwiseFromAnalogHourHandToMinuteHand(int hours, int minutes, int seconds)
    {
        double degreesMinuteHand = 6.0 * (minutes + seconds / 60.0);
        double degreesHourHand = 30.0 * (hours % 12 + minutes / 60.0 + seconds / 3600.0);
        return degreesMinuteHand - degreesHourHand;
    }
    
    public static int unit_test()
    {
        String testName = Clock.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        // test 101 uniformly incremented times from 00:00:00 to 12:00:00
        int hours = 0, minutes = 0, seconds = 0;
        double degrees;
        for (int j = 0; j <= 100; j++)
        {
            hours = 12 * j / 100;
            minutes = (12 * 60 * j / 100) % 60;
            seconds = (12 * 60 * 60 * j / 100) % 60;
            degrees = degreesClockwiseFromAnalogHourHandToMinuteHand(hours, minutes, seconds);
            Sx.format("degreesClockwiseFromAnalogHourHandToMinuteHand(%2d, %2d, %2d) : %5.2f\n",
                    hours, minutes, seconds, degrees);
        }
        
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }    
}
