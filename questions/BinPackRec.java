/*
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

    public static int excessBinSpace(int[] bins, int[] items) {
        return Arrays.stream(bins).sum() - Arrays.stream(items).sum();
    }

    public static boolean allTrue (boolean[] values) {
        for (boolean value : values) {
            if (!value)
                return false;
        }
        return true;
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
        
        int excess = excessBinSpace(bins, items);
        if (excess < 0)
            return false;                           // return early: insufficient total space
        
        int[] binsCopy = Arrays.copyOf(bins, bins.length);
        Arrays.sort(binsCopy);
        Arrays.sort(items);

        if (binsCopy[bins.length - 1] < items[items.length - 1])
            return false;                           // return early: max bin < max item

        if (canPackRecursive(binsCopy, bins.length, items, items.length)) {
            // TODO: this does NOT change the original array, nor does bins = binsCopy
            bins = Arrays.copyOf(binsCopy, bins.length);
            assert Arrays.stream(binsCopy).sum() == excess;
            return true;
        }
        return false;
    }
    
    /**
     * Sorted recursion.  Early return if largest item cannot fit in largest remaining bin.
     * @param bins
     * @param numUsableBins
     * @param items
     * @param numUnpacked
     * @return
     */
    static boolean canPackRecursive(int[] bins, int numUsableBins, int[] items, int numUnpacked) {
        if (numUnpacked < 1)
            return true;

        int j = numUnpacked - 1;
        int k = numUsableBins - 1;

        // And now if the largest remaining bin cannot fit the next largest item, do not try.
        if (bins[k] < items[j]) 
            return false;

        // Use reverse order, assuming the inputs were sorted in ascending order.
        // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]

        for (; k >= 0; k--) {
            if (bins[k] >= items[j]) {
                bins[k] -= items[j];
                
                boolean swapped = false;
                
                int bin_k_val = bins[k];
                if (bin_k_val < items[0]) {
                    // If bins[k] is now smaller than the smallest item, remove it from the active list.
                    swapped = true;
                    bins[k] = bins[--numUsableBins];
                    bins[numUsableBins] = bin_k_val;      
                } else {
                    // Otherwise, sort the list by re-inserting bin[k] value where it now belongs.
                    for (int q = k; --q >= 0; ) {
                        if (bin_k_val < bins[q]) {
                            bins[q + 1] = bins[q];
                        }
                        else {
                            bins[q + 1 ] = bin_k_val;
                            break;
                        }
                    }
                }
                
                if(canPackRecursive(bins, numUsableBins, items, j)){
                    return true;
                }
                
                // failed, so swap back and increment.
                if (swapped) {
                    bins[numUsableBins++] = bins[k];
                    bins[k] = bin_k_val;
                    swapped = false;
                }

                bins[k] += items[j];
            }
        }
        return false;
    }
    
    

    
    public static int test_canPack(IBinPack binPacker, int bins[], int items[], int verbose, boolean expected)
    {
        boolean result = false;
        int excess = BinPack.excessBinSpace(bins, items);
        if (verbose > 0) {
            Sx.puts("\n\t  test_canPack:");
            Sx.putsArray("bin space before: ", bins);
            Sx.putsArray("items to pack:    ", items);
            int binTot = IntStream.of(bins).sum();
            int itemTot = IntStream.of(items).sum();
            int diff = binTot - itemTot;
            assert(diff == excess);
            Sx.format("Total bin space - items space: %d - %d = %d\n", binTot, itemTot, diff);
        }
        if (excess < 0) {
            Sx.puts("Insufficient total bin space.");
        } else {
            // Test the interface function:
            long begTime = System.currentTimeMillis();
            result = binPacker.canPack(bins, items);
            long runTime = System.currentTimeMillis() - begTime;
            
            if (verbose > 0) {
                Sx.format("Pack items in bins? %s\n", result);
                Sx.putsArray("Bin space after:  ", bins);
            }
            Sx.format("Run time millis:    %d\n", runTime);
        }
        return Sz.showWrong(result, expected);
    }
    
    public static int unit_test(int level) 
    {
        String testName = BinPackRec.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        IBinPack binPacker = new BinPackRec();

        int seas[] = {2, 2, 37};
        int holes[] = {4, 37};
        numWrong += test_canPack(binPacker, seas, holes, 1, false);
        
        int servers[] = {8, 16, 8, 32};
        int tasks[] = {18, 4, 8, 4, 6, 6, 8, 8};
        numWrong += test_canPack(binPacker, servers, tasks, 1, true);
        
        int limits[] = {1, 3};
        int needs[] = { 4 };
        numWrong += test_canPack(binPacker, limits, needs, 1, false);
        
        int duffels[] = { 2, 2, 2, 5, 6  };
        int bags[] = { 3, 3, 5};
        numWrong += test_canPack(binPacker, duffels, bags, 1, true);

        int sashes[] = { 1, 2, 3, 4, 5, 6, 8, 9  };
        int badges[] = { 1, 4, 6, 6, 8, 8, };
        numWrong += test_canPack(binPacker, sashes, badges, 1, false);

        if (level > 1) {        
            int fibs[] = FibonacciInt32.fib32Range(0, 12);
            int mems[] = Primes.primesInRangeIntArray(2, 47);
            numWrong += test_canPack(binPacker, fibs, mems, 1, true);
            
            int crates[] = FibonacciInt32.fib32Range(0, 9);
            int boxes[] = Primes.primesInRangeIntArray(2, 25);
            numWrong += test_canPack(binPacker, crates, boxes, 1, false);
            
            int blocks[] = FibonacciInt32.fib32Range(0, 14);
            int allocs[] = Primes.primesInRangeIntArray(2, 90);
            numWrong += test_canPack(binPacker, blocks, allocs, 1, false);

            int frames[] = FibonacciInt32.fib32Range(0, 15);
            int photos[] = Primes.primesInRangeIntArray(2, 125);
            numWrong += test_canPack(binPacker, frames, photos, 1, false);
            
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
        unit_test(1);
    }

}


