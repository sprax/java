package sprax.questions;

import java.util.*;     // TODO: organize imports later

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Question?
 */
public class BinSearchGuess 
{
    static Random RNG = new Random(System.currentTimeMillis());

    int theNumber;
  
    public static void main(String[] args) { unit_test(); }

    public static int unit_test() 
    {
        String testName = BinSearchGuess.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        BinSearchGuess bng = new BinSearchGuess();
                
        numWrong += test_guessNumber(bng, 1, 1);
        numWrong += test_guessNumber(bng, 1, 2);
        numWrong += test_guessNumber(bng, 2, 2);
        numWrong += test_guessNumber(bng, 1, 3);
        numWrong += test_guessNumber(bng, 2, 3);
        numWrong += test_guessNumber(bng, 3, 3);
        numWrong += test_guessNumber(bng, Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
        numWrong += test_guessNumber(bng, Integer.MAX_VALUE, Integer.MAX_VALUE);

        for (int j = 0; j < 12; j++) {
            int number = 1 + RNG.nextInt(Integer.MAX_VALUE);
            numWrong += test_guessNumber(bng, number, number+1);
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
        
    public static int test_guessNumber(BinSearchGuess bng, int num, int maxNum)
    {
        int expect = bng.theNumber = num;
        
        int result = bng.guessNumber(maxNum);
        Sx.format("Expect %2d  %2d result\n", expect, result);
        return Sz.showWrong(result, expect);
    }

    int guess(int x) {
        return Integer.compare(theNumber, x);
    }
    
    public int guessNumber(int n) {
         if (n < 1)
            throw new IllegalArgumentException("n < 1");
        int lo = 1, hi = n;
        while(true) {
            if (lo == hi)
                return lo;
            int md = lo/2 + hi/2;
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
