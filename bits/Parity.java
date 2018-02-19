package sprax.bits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import sprax.arrays.ArrayDiffs;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Programming Pearls opening example by Jon Bentley:
 * Sort up to 27,000 unique numbers between 0 and 27000 exclusive.
 * Hint: don't sort anything, just use an auxiliary bitfield of size
 * sufficient to cover the range of your values.
 * For example, to sort a collection of int values in the range [4000, 6000),
 * use a BitSet of size 2000 and for each value to sort, set the bit at (value - 4000).
 * To read out the sorted values, traverse the BitSet.
 * Depending on your usage, you may be able to lose the original and just keep the BitSet.
 */
public class Parity 
{
    /** O(n) parity checker, where n == bits in a long (e.g. 64) */
    public static int parityN(long num)
    {
        int result = 0;             // start with 0 for even number (zero) of on-bits
        while (num != 0) {
            result ^= (num & 1);    // flip every time an on-bit is found
            num >>= 1;              // shift right
        }
        return result;
    }
     
    /** O(n) parity checker, where n == bits in a long (e.g. 64) */
    public static int parityK(long num)
    {
        int result = 0;             // start with 0 for even number (zero) of on-bits
        while (num != 0) {
            result ^= 1;            // flip every time an on-bit is found
            num &= (num - 1);       // erase the lowest set bit of num
        }
        return result;
    }
    
    public static int testPairs(int pairs[][])
    {
        int result, numWrong = 0;
        for (int pair[] : pairs) {
            result = Parity.parityN(pair[1]);
            if (pair[0] != result) {
                numWrong++;
                Sx.format("WRONG N: %d for %d\n", result, pair[1]);
            }
            result = Parity.parityK(pair[1]);
            if (pair[0] != result) {
                numWrong++;
                Sx.format("WRONG K: %d for %d\n", result, pair[1]);
            }
        }
        return numWrong;
    }
     
    public static int unit_test() 
    {
        String testName =  Parity.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int pairs[][] = {
                { 0, 0 },
                { 1, 1 },
                { 1, 2 },
                { 0, 3 },
                { 1, 4 },
                { 0, 5 },
                { 0, 6 },
                { 1, 7 },
                { 1, 8 },
                { 0, 9 },
                { 0, 10 },
                { 1, 1024 },
                { 0, 3072 },
                { 1, 3073 },
                { 0, 4097 },
                
        };
        numWrong += testPairs(pairs);

            
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
