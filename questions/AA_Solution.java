package sprax.questions;

import java.util.*;     // TODO: organize imports later

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Question?
 */
public class AA_Solution 
{
    public static void main(String[] args) { unit_test(); }

    public static int unit_test() {
        String testName = AA_Solution.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;

        numWrong += test_blank();

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int test_blank() {
        int result = blank();
        int expected = 0;
        return Sz.oneIfDiff(result, expected);
    }

    public static int blank() {
        Sx.puts("blank");
        return 0;
    }
    
}
