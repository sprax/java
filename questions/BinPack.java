/*
 * Median
 * 
 * Copyright (c) 2001, 2002, 2003 Marco Schmidt.
 * All rights reserved.
 */

package sprax.questions;

import java.util.ArrayList;
import java.util.Arrays;

import sprax.numbers.Fibonacci;
import sprax.numbers.Primes;
import sprax.sprout.Sx;
import sprax.test.Sz;

/** 
 * Can the space requirements specified by items be packed into the specified bins?
 * Finding the optimal solution is combinatorial NP-hard; that is, even deciding if a
 * given solution is minimal is NP-complete. See https://en.wikipedia.org/wiki/Bin_packing_problem
 * But just deciding whether a given set of items can be packed into a given set of bins
 * is NOT NP-complete, but can be solved in Theta(NlogN), as by the First-Fit algorithm.
 */
public class BinPack
{
    
    /** 
     * Can the space requirements specified by items be packed into the specified bins?
     * Recursive without sorting, adapted from:
     * https://www.careercup.com/question?id=6282171643854848 answer by 
     *  driv3r August 05, 2014 
     * Note: The values in bins will be decreased by the amounts in items.
     * The items array will be unchanged.
     */
    public static boolean canPack(int[] bins, int[] items) {
        boolean[] packed = new boolean[items.length];
        return canPackRecursive(bins, items, packed);
    }
    
    /**
     * @param bins
     * @param items
     * @param packed
     * @return
     */
    static boolean canPackRecursive(int[] bins, int[] items, boolean[] packed) {
        boolean allUsed = true;
        for (boolean b : packed) {
            allUsed &= b;
        }
        if (allUsed){
            return true;
        }
        for (int i = 0; i < items.length; i++) {
            if (!packed[i]) {
                // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]
                packed[i] = true;
                for (int j = 0; j < bins.length; j++) {
                    if (bins[j] >= items[i]) {
                        bins[j] -= items[i];
                        if(canPackRecursive(bins, items, packed)){
                            return true;
                        }
                        bins[j] = bins[j] + items[i];
                    }
                }
                packed[i] = false;
            }
        }
        return false;
    }
    
    public static int test_canPack(int bins[], int items[], int verbose, boolean expected)
    {
        if (verbose > 0) {
            Sx.puts("\n\t  test_canAllocate:");
            Sx.putsArray("bins before:    ", bins);
            Sx.putsArray("items to pack:  ", items);
        }
        // Test the lower-level function:
        boolean[] packed = new boolean[items.length];
        boolean result = canPackRecursive(bins, items, packed);
        if (verbose > 0) {
            Sx.format("canPack?  %s\n", result);
            Sx.putsArray("bins leftover:  ", bins);
            if (! result) {
                Sx.print("not all packed: ");
                Sx.printFilteredArrayFalse(items, packed);
                Sx.puts();
            }
        }
        return Sz.oneIfDiff(result, expected);
    }
    
    public static int unit_test() 
    {
        String testName = BinPack.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int servers[] = {8, 16, 8, 32};
        
        Sx.printArray(servers);
        Sx.puts();
        
        int tasks[] = {18, 4, 8, 4, 6, 6, 8, 8};
        numWrong += test_canPack(servers, tasks, 1, true);
        
        int limits[] = {1, 3};
        int needs[] = {4};
        numWrong += test_canPack(limits, needs, 1, false);
        
        
        int blocks[] = FibonacciInt32.fib32Range(0, 20);
        int allocs[] = Primes.primesInRangeIntArray(2, 100);
        numWrong += test_canPack(blocks, allocs, 1, false);
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test();
    }
}


