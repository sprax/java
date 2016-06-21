/*
 * Median
 * 
 * Copyright (c) 2001, 2002, 2003 Marco Schmidt.
 * All rights reserved.
 */

package sprax.questions;

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
        boolean[] used = new boolean[items.length];
        return canPackRecursive(bins, items, used);
    }
    
    /**
     * @param bins
     * @param items
     * @param used
     * @return
     */
    static boolean canPackRecursive(int[] bins, int[] items, boolean[] used) {
        boolean allUsed = true;
        for (boolean b : used) {
            allUsed &= b;
        }
        if (allUsed){
            return true;
        }
        for (int i = 0; i < items.length; i++) {
            if (!used[i]) {
                // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]
                used[i] = true;
                for (int j = 0; j < bins.length; j++) {
                    if (bins[j] >= items[i]) {
                        bins[j] = bins[j] - items[i];
                        if(canPackRecursive(bins, items, used)){
                            return true;
                        }
                        bins[j] = bins[j] + items[i];
                    }
                }
                used[i] = false;
            }
        }
        return false;
    }
    
    public static int test_canAllocate(int bins[], int items[], int verbose, boolean expected)
    {
        if (verbose > 0) {
            Sx.puts("\n\t  test_canAllocate:");
            Sx.putsArray("bins before:    ", bins);
            Sx.putsArray("items to pack:  ", items);
        }
        boolean result = canPack(bins, items);
        if (verbose > 0) {
            Sx.format("canPack?  %s\n", result);
            Sx.putsArray("bins leftover:  ", bins);
            if (! result)                               // TODO: Actual difference?
                Sx.putsArray("not all packed: ", items);
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
        numWrong += test_canAllocate(servers, tasks, 1, true);
        
        int limits[] = {1, 3};
        int needs[] = {4};
        numWrong += test_canAllocate(limits, needs, 1, false);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test();
    }
    
    
    /**
     * Ex: 
Servers capacity limits: 8, 16, 8, 32 
Tasks capacity needs: 18, 4, 8, 4, 6, 6, 8, 8 
For this example, the program should say 'true'. 

Ex2: 
Server capacity limits: 1, 3 
Task capacity needs: 4 
For this example, program should return false. 
     */
    
}
