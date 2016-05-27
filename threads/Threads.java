package sprax.threads;

public class Threads 
{
    public static void tryToSleep(long millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            return;
        }
    }        

}
