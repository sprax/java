package sprax.test;

import sprax.sprout.Sx;

/**
 * static utility methods for testing, debugging, etc.
 *
 */
public class Sz 
{
    public static int oneIfFalse(boolean result) { return result ? 0 : 1; }
    public static int oneIfDiff(boolean result, boolean expected) { return result == expected ? 0 : 1; }
    public static int oneIfDiff(int result, int expected) { return result == expected ? 0 : 1; }
    public static int oneIfDiff(String result, String expected) { return result.equals(expected) ? 0 : 1; }

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

    public static void begin(String testName) {
        System.out.format("BEGIN %s\n", testName);  
    }

    public static void end(String testName, int numWrong) {
        System.out.format("END   %s,  wrong %d,  %s\n\n", testName, numWrong, Sz.passFail(numWrong));  
    }

    public static void ender(String testName, int numCases, int numWrong) {
        System.out.format("END   %s,  right %d,  wrong %d, %s\n\n"
                , testName, numCases - numWrong, numWrong, Sz.passFail(numWrong));  
    }


    public static int unit_test()
    {
        String testName = Sz.class.getName() + ".unit_test";
        begin(testName);

        int numWrong = 0;
        numWrong += oneIfFalse(true);
        numWrong += oneIfDiff(true, true);
        numWrong += oneIfDiff(false, false);
        numWrong += oneIfDiff(0, 0);
        numWrong += oneIfDiff(-1, 1 - 2);

        end(testName, numWrong);
        return numWrong;
    }

    public static void main(String args[]) { unit_test(); }
}
