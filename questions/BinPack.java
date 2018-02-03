/*
 */

package sprax.questions;

import java.util.Arrays;

import sprax.numbers.FibonacciInt32;
import sprax.numbers.Primes;
import sprax.sprout.Sx;
import sprax.test.Sz;

/** 
 * Can the space requirements specified by items be packed into the specified bins?
 * Implementation: Naive exhaustive recursion with supplementary boolean array.
 * Complexity: Time O(N!), additional space O(N).
 */
public class BinPack implements IBinPack
{
    @Override
    public boolean canPack(int[] bins, int[] items) {
        return canPackNaive(bins, items);
    }    

    /** 
     * Can the space requirements specified by items be packed into the specified bins?
     * Recursive without sorting, adapted from:
     * https://www.careercup.com/question?id=6282171643854848 answer by 
     *  driv3r August 05, 2014 
     * Note: The values in bins will be decreased by the amounts in items.
     * The items array will be unchanged.
     */
    public static boolean canPackNaive(int[] bins, int[] items) {
        boolean[] packed = new boolean[items.length];
        return canPackRecursive(bins, items, packed);
    }
    
    /**
     * Naive exhaustive recursion, no early failure (as when sum(bins) < sum(items)), no sorting.
     * @param bins
     * @param items
     * @param packed
     * @return
     */
    static boolean canPackRecursive(int[] bins, int[] items, boolean[] packed) {
        boolean allPacked = true;
        for (boolean b : packed) {
            allPacked &= b;
        }
        if (allPacked){
            return true;
        }
        for (int i = 0; i < items.length; i++) {
            if (!packed[i]) {
                // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]
                packed[i] = true;
                for (int j = 0; j < bins.length; j++) {
                    if (bins[j] >= items[i]) {
                        bins[j] -= items[i];            // deduct item amount from bin an try packing the rest
                        if(canPackRecursive(bins, items, packed)){
                            return true;                // success: return
                        }
                        bins[j] = bins[j] + items[i];   // failure: restore item amount to bin
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
            int binTot = Arrays.stream(bins).sum();
            int itemTot = Arrays.stream(items).sum();
            int diff = binTot - itemTot;
            Sx.format("Total bin space - items space: %d - %d = %d\n", binTot, itemTot, diff);
        }
        // Test the lower-level function:
        boolean[] packed = new boolean[items.length];
        boolean result = canPackRecursive(bins, items, packed);
        if (verbose > 0) {
            Sx.format("canPack?  %s\n", result);
            Sx.putsArray("bins leftover:  ", bins);
            if (!result) {
                Sx.print("not all packed: ");
                Sx.printFilteredArrayFalse(items, packed);
                Sx.puts();
            }
        }
        return Sz.oneIfDiff(result, expected);
    }
    
    public static int unit_test(int level)
    {
        String testName = BinPack.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int servers[] = { 8, 16, 8, 32 };
        int tasks[] = { 18, 4, 8, 4, 6, 6, 8, 8 };
        numWrong += test_canPack(servers, tasks, 1, true);
        
        int limits[] = { 1, 3 };
        int needs[] = { 4 };
        numWrong += test_canPack(limits, needs, 1, false);
        
        int fibs[] = FibonacciInt32.fib32Range(0, 12);
        int mems[] = Primes.primesInRangeIntArray(2, 47);
        numWrong += test_canPack(fibs, mems, 1, true);
        
        int crates[] = FibonacciInt32.fib32Range(0, 9);
        int boxes[] = Primes.primesInRangeIntArray(2, 25);
        numWrong += test_canPack(crates, boxes, 1, false);
        
        if (level > 1) {  // 
            int frames[] = FibonacciInt32.fib32Range(0, 13);
            int photos[] = Primes.primesInRangeIntArray(2, 70);
            numWrong += test_canPack(frames, photos, 1, true);
        }
        
        if (level > 2) {  // 11, 42
            int blocks[] = FibonacciInt32.fib32Range(0, 10);
            int allocs[] = Primes.primesInRangeIntArray(2, 31);
            numWrong += test_canPack(blocks, allocs, 1, false);
            
            int frames[] = FibonacciInt32.fib32Range(0, 15);
            int photos[] = Primes.primesInRangeIntArray(2, 125);
            numWrong += test_canPack(frames, photos, 1, false);
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(2);
    }

}


