/*
 * Median
 * 
 * Copyright (c) 2001, 2002, 2003 Marco Schmidt.
 * All rights reserved.
 */

package sprax.questions;

import java.util.Arrays;
import java.util.stream.IntStream;

import sprax.numbers.FibonacciInt32;
import sprax.numbers.Primes;
import sprax.sprout.Sx;
import sprax.test.Sz;

/** 
 * Can the space requirements specified by items be packed into the specified bins?
 * Implementation: Naive exhaustive recursion with supplementary boolean array.
 * Complexity: Time O(N!), additional space O(N).
 */
public class BinPackRec implements IBinPack
{
    @Override
    public boolean canPack(int[] bins, int[] items) {
        return canPackRec(bins, items);
    }    
    /** 
     * Can the space requirements specified by items be packed into the specified bins?
     * Recursive without sorting, adapted from:
     * https://www.careercup.com/question?id=6282171643854848 answer by 
     *  driv3r August 05, 2014 
     * Note: The values in bins will be decreased by the amounts in items.
     * The items array will be unchanged.
     */
    public static boolean canPackRec(int[] bins, int[] items) {
        
        int binTot = Arrays.stream(bins).sum();
        int itemTot = Arrays.stream(items).sum();
        int diffTot = binTot - itemTot;
        if (diffTot < 0)
            return false;                           // early return
        
        Arrays.sort(bins);
        Arrays.sort(items);
        boolean[] packed = new boolean[items.length];
        return canPackRecursive(bins, bins.length, items, packed);
    }
    
    /**
     * Naive exhaustive recursion, no early failure (as when sum(bins) < sum(items)), no sorting.
     * @param bins
     * @param items
     * @param packed
     * @return
     */
    static boolean canPackRecursive(int[] bins, int numUsableBins, int[] items, boolean[] packed) {
        boolean allUsed = true;
        for (boolean b : packed) {
            allUsed &= b;
        }
        if (allUsed){
            return true;
        }
        for (int j = items.length; --j >= 0; ) {
            if (!packed[j]) {
                // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]
                packed[j] = true;
                for (int k = numUsableBins; --k >= 0; ) {
                    if (bins[k] >= items[j]) {
                        bins[k] -= items[j];
                        
                        int tmp = bins[k];
                        boolean swapped = false;
                        if (bins[k] < items[0]) {
                            swapped = true;
                            bins[k] = bins[--numUsableBins];
                            bins[numUsableBins] = tmp;      
                        }
                        
                        if(canPackRecursive(bins, numUsableBins, items, packed)){
                            return true;
                        }
                        
                        // failed, so swap back and increment?
                        /*
                        if (swapped) {
                            bins[numUsableBins++] = bins[k];
                            bins[k] = tmp;
                        } 
                        */

                        bins[k] += items[j];
                    }
                }
                packed[j] = false;
            }
        }
        return false;
    }
    
    
    /**
     * Naive exhaustive recursion, no early failure (as when sum(bins) < sum(items)), no sorting.
     * @param bins
     * @param items
     * @param packed
     * @return
     */
    /*****************************
    static boolean canPackRecursive(int[] bins, int numUsableBins, int[] items, boolean[] packed) {
        boolean allUsed = true;
        for (boolean b : packed) {
            allUsed &= b;
        }
        if (allUsed){
            return true;
        }
        for (int j = items.length; --j >= 0; ) {
            if (!packed[j]) {
                // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]
                packed[j] = true;
                for (int k = bins.length; --k >= 0; ) {
                    if (bins[k] >= items[j]) {
                        bins[k] -= items[j];
                        
                        
                        // bins[k] was just decreased: if it's now 0, "remove" it;
                        // otherwise, re-sort bins:
                        /
                        int binskV = bins[k];
                        for (int q = k; --q >= 0; ) {
                            if (binskV < bins[q]) {
                                continue;
                            }
                            else {
                                
                                break;
                            }
                                
                        }
                        /
                        Arrays.sort(bins);
                        
                        
                        
                        
                        if(canPackRecursive(bins, numUsableBins, items, packed)){
                            return true;
                        }
                        bins[k] = bins[k] + items[j];
                    }
                }
                packed[j] = false;
            }
        }
        return false;
    }
    ******/
    
    public static int test_canPack(IBinPack binPacker, int bins[], int items[], int verbose, boolean expected)
    {
        if (verbose > 0) {
            Sx.puts("\n\t  test_canAllocate:");
            Sx.putsArray("bins before:    ", bins);
            Sx.putsArray("items to pack:  ", items);
            int binTot = IntStream.of(bins).sum();
            int itemTot = IntStream.of(items).sum();
            int diff = binTot - itemTot;
            Sx.format("Total bin space - items space: %d - %d = %d\n", binTot, itemTot, diff);
        }
        
        // Test the interface function:
        boolean result = binPacker.canPack(bins, items);
        
        if (verbose > 0) {
            Sx.format("canPack?  %s\n", result);
            Sx.putsArray("bins leftover:  ", bins);
        }
        return Sz.oneIfDiff(result, expected);
    }
    
    public static int unit_test(int level) 
    {
        String testName = BinPackRec.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        IBinPack binPacker = new BinPackRec();
        
        int seas[] = {2, 2, 37};
        int holes[] = {4, 37};
        numWrong += test_canPack(binPacker, seas, holes, 1, true);
        
        int servers[] = {8, 16, 8, 32};
        int tasks[] = {18, 4, 8, 4, 6, 6, 8, 8};
        numWrong += test_canPack(binPacker, servers, tasks, 1, true);
        
        int limits[] = {1, 3};
        int needs[] = { 4 };
        numWrong += test_canPack(binPacker, limits, needs, 1, false);
        
        int fibs[] = FibonacciInt32.fib32Range(0, 12);
        int mems[] = Primes.primesInRangeIntArray(2, 47);
        numWrong += test_canPack(binPacker, fibs, mems, 1, true);
        
        int crates[] = FibonacciInt32.fib32Range(0, 9);
        int boxes[] = Primes.primesInRangeIntArray(2, 25);
        numWrong += test_canPack(binPacker, crates, boxes, 1, false);
        
        if (level > 1) {
            int blocks[] = FibonacciInt32.fib32Range(0, 11);
            int allocs[] = Primes.primesInRangeIntArray(2, 42);
            numWrong += test_canPack(binPacker, blocks, allocs, 1, false);
            
            int frames[] = FibonacciInt32.fib32Range(0, 15);
            int photos[] = Primes.primesInRangeIntArray(2, 125);
            numWrong += test_canPack(binPacker, frames, photos, 1, true);
            
            if (level > 2) {    // takes a very long time...
                frames = FibonacciInt32.fib32Range(0, 15);
                photos[0] = 4;
                numWrong += test_canPack(binPacker, frames, photos, 1, false);
            }
        }
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(3);
    }

}


