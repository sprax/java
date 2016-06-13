package sprax.test;

/**
 * static utility methods for testing, debugging, etc.
 *
 */
public class Sv 
{

    public static int unit_test()
    {
        String testName = Sv.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
 
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String args[]) { unit_test(); }
}
