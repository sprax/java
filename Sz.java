package sprax;

/**
 * static utility methods for testing, debugging, etc.
 *
 */
public class Sz 
{
    public static int oneWrong(boolean result, boolean expected) { return result == expected ? 0 : 1; }
    public static int oneWrong(int result, int expected) { return result == expected ? 0 : 1; }

    public static int showWrong(int result, int expected)
    {
        if (result == expected)
            return 0;
        System.out.format("Wrong result %d, expected %d\n", result, expected);
        return 1;
    }

    public static String passFail(int numWrong) {
        return numWrong == 0 ? "PASS" : "FAIL"; 
    }

    public static int unit_test()
    {
        String testName = Sz.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);

        int numWrong = 0;
        numWrong += oneWrong(true, true);
        numWrong += oneWrong(false, false);
        numWrong += oneWrong(0, 0);
        numWrong += oneWrong(-1, 1 - 2);

        Sx.format("END   %s,  wrong: %d, %s\n", testName, numWrong, passFail(numWrong));
        return 0;
    }

    public static void begin(String testName) {
        Sx.format("BEGIN %s\n", testName);  
    }

    public static void end(String testName, int numWrong) {
        Sx.format("END   %s,  wrong %d, %s\n", testName, numWrong, Sz.passFail(numWrong));  
    }

    public static void ender(String testName, int numCases, int numWrong) {
        Sx.format("END   %s,  right %d, wrong %d, %s\n"
                , testName, numCases - numWrong, numWrong, Sz.passFail(numWrong));  
    }

    public static void main(String args[]) { unit_test(); }
}
