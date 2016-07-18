package sprax.questions;

import java.util.*;     // TODO: organize imports later

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Question?
 */
public class BinSearchGuess 
{
    public static void main(String[] args) { unit_test(); }

    public static int unit_test() {
        String testName = BinSearchGuess.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        
        int result = guessNumber(RANGE);
        int expected = NUMBER;
        Sx.format("NUMBER = %2d  %2d = result\n", expected, result);
        
        
        for (int j = 0; j < 12; j++)
            numWrong += test_guessNumber();

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    private static int NUMBER = 1702766719;
    private static int RANGE = Integer.MAX_VALUE - 1;

    private static Random RNG = new Random(System.currentTimeMillis());
    
    public static int test_guessNumber()
    {
        NUMBER = RNG.nextInt(100);
        int result = guessNumber(NUMBER + 1);
        int expected = NUMBER;
        Sx.format("NUMBER = %2d  %2d = result\n", NUMBER, result);
        return Sz.showWrong(result, expected);
    }

    static int guess(int x) {
        return Integer.compare(NUMBER, x);
    }
    
    public static int guessNumber(int n) 
    {
        if (n < 1)
            throw new IllegalArgumentException("n < 1");
        int lo = 0, hi = n;
        while(true) {
            int md = (lo + 1)/2 + hi/2;
            int ic = guess(md);
            if (ic > 0)
                lo = md + 1;
            else if (ic < 0)
                hi = md - 1;
            else
                return md;
        }
    }
    
}
