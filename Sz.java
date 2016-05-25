package sprax;

/**
 * static utility methods for testing, debugging, etc.
 *
 */
public class Sz 
{
    public static int wrong(boolean result, boolean expected) { return result == expected ? 0 : 1; }
    public static int wrong(int result, int expected) { return result == expected ? 0 : 1; }

    public static int unit_test()
    {
        String testName = Sz.class.getName() + ".unit_test";
        Sx.format("BEGIN: %s\n", testName);

        int numWrong = 0;
        numWrong += wrong(true, true);
        numWrong += wrong(false, false);
        numWrong += wrong(0, 0);
        numWrong += wrong(-1, 1 - 2);

        Sx.format("END %s, status %s\n", testName, (numWrong == 0 ? "PASS" : "FAIL"));
        return 0;
    }

    public static void main(String args[]) { unit_test(); }
}
