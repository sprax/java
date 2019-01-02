package sprax.questions;

import java.util.Scanner;

import sprax.test.Sz;

/**
 * https://www.hackerrank.com/challenges/find-point
 */
public class AA_LibraryClass 
{
    public static int blank() {
        return 0;
    }
    
    //////////////// TESTING ////////////////
    
    public static int test_blank() {
        int result = blank();
        int expected = 0;
        return Sz.oneIfDiff(result, expected);
    }

	public static int unit_test() {
		String testName = AA_LibraryClass.class.getName() + ".unit_test";
		Sz.begin(testName);
		int numWrong = 0;

		numWrong += test_blank();

		Sz.end(testName, numWrong);
		return numWrong;
	}

	public static void main(String[] args) {
		unit_test();
	}
}
