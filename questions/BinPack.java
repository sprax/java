/*
 */

package sprax.questions;

import java.util.Arrays;

import sprax.numbers.FibonacciInt32;
import sprax.numbers.Primes;
import sprax.sprout.Sx;
import sprax.test.Sz;

/** 
 * Can the space requirements given by items be packed into the specified bins?
 * Implementations: Naive exhaustive recursion with supplementary boolean array.
 * Complexity: Time O(N!), additional space O(N).
 */
public class BinPack implements IBinPack
{
    @Override
    public boolean canPack(int[] bins, int[] items) {
        if (excessBinSpace(bins, items) < 0)
            return false;
        return canPackNaive(bins, items);
    }    

    public static int excessBinSpace(int[] bins, int[] items) {
        return Arrays.stream(bins).sum() - Arrays.stream(items).sum();
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
                        bins[j] -= items[i];            // deduct item amount from bin and try to pack the rest
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
}

@FunctionalInterface
interface BinPackIntArrays
{
    boolean canPack(int[] bins, int[] items); 
}

class BinPackTest
{
    public static int test_canPack(int bins[], int items[], int verbose, boolean expected, BinPackIntArrays packer)
    {
        int excess = BinPack.excessBinSpace(bins, items);
        if (verbose > 0) {
            Sx.puts("\n\t  Test canPack:");
            Sx.putsArray("Bins space before:", bins);
            Sx.putsArray("Items to pack:    ", items);
            int binTot = Arrays.stream(bins).sum();
            int itemTot = Arrays.stream(items).sum();
            int diff = binTot - itemTot;
            assert(diff == excess);
            Sx.format("Total bin space - items space: %d - %d = %d\n", binTot, itemTot, diff);
        }
        if (excess < 0) {
            Sx.puts("Insufficient total bin space.");
            return 0;
        }

        // Test the lower-level (static) method:
        long begTime = System.currentTimeMillis();
        boolean result = packer.canPack(bins, items);
        long runTime = System.currentTimeMillis() - begTime;

        if (verbose > 0) {
            Sx.format("Pack items in bins? %s\n", result);
            Sx.putsArray("Bin space after:  ", bins);
        }
        Sx.format("Run time millis:    %d\n", runTime);
        return Sz.oneIfDiff(result, expected);
    }
    
    public static int test_packer(BinPackIntArrays packer, int level)
    {
        String testName = BinPack.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int servers[] = { 8, 16, 8, 32 };
        int tasks[] = { 18, 4, 8, 4, 6, 6, 8, 8 };
        numWrong += test_canPack(servers, tasks, 1, true, packer);
        
        int limits[] = { 1, 3 };
        int needs[] = { 4 };
        numWrong += test_canPack(limits, needs, 1, false, packer);
        
        int fibs[] = FibonacciInt32.fib32Range(0, 12);
        int mems[] = Primes.primesInRangeIntArray(2, 47);
        numWrong += test_canPack(fibs, mems, 1, true, packer);
        
        int crates[] = FibonacciInt32.fib32Range(0, 9);
        int boxes[] = Primes.primesInRangeIntArray(2, 25);
        numWrong += test_canPack(crates, boxes, 1, false, packer);
        
        if (level > 1) {
            int frames[] = FibonacciInt32.fib32Range(0, 13);
            int photos[] = Primes.primesInRangeIntArray(2, 70);
            numWrong += test_canPack(frames, photos, 1, true, packer);
        }
        
        if (level > 2) {  // 11, 42
            int blocks[] = FibonacciInt32.fib32Range(0, 10);
            int allocs[] = Primes.primesInRangeIntArray(2, 31);
            numWrong += test_canPack(blocks, allocs, 1, false, packer);
            
            int frames[] = FibonacciInt32.fib32Range(0, 15);
            int photos[] = Primes.primesInRangeIntArray(2, 125);
            numWrong += test_canPack(frames, photos, 1, false, packer);
        }
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static int unit_test(int level)
    {
        String testName = BinPack.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_packer(BinPack::canPackNaive, level);

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(1);
    }

}


