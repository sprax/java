/*
 */

package sprax.questions;

import java.util.Arrays;
import java.util.stream.IntStream;

import sprax.arrays.Arrays1d;
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
     * If the packing algorithm succeeds, the values in bins will be decreased by the amounts in items.
     * The items array will be sorted.
     */
    public static boolean canPackRec(int[] bins, int[] items)
    {
        int usableSpace = Arrays.stream(bins).sum();
        int neededSpace = Arrays.stream(items).sum();
        int excess = usableSpace - neededSpace;
        if (excess < 0)
            return false;                           // return early: insufficient total space
        
        int[] binsCopy = Arrays.copyOf(bins, bins.length);
        Arrays.sort(binsCopy);
        Arrays.sort(items);

        if (binsCopy[bins.length - 1] < items[items.length - 1])
            return false;                           // return early: max bin < max item

        if (canPackRecursive(binsCopy, bins.length, items, items.length, usableSpace, neededSpace)) {
            // Change the original array.  (Pass by value means bins = binsCopy would not.)
            for (int j = bins.length; --j >= 0; ) {
                bins[j] = binsCopy[j];
            }
            return true;
        }
        Sx.putsArray("failed binCopy:   ", binsCopy);
        
        return false;
    }
    
    /**
     * Sorted recursion.  Early return if largest item cannot fit in largest remaining bin.
     * @param bins
     * @param numUsable
     * @param items
     * @param numUnpacked
     * @return
     */
    static boolean canPackRecursive(int[] bins, int numUsable, int[] items, int numUnpacked, int usableSpace, int neededSpace)
    {
        if (numUnpacked < 1) {
            return true;
        }
        if (numUsable < 1) {
            return false;
        }
        
        int j = numUnpacked - 1;
        int k = numUsable - 1;

        // return false if the largest remaining bin cannot fit the largest unpacked item.
        if (bins[k] < items[j]) {
            return false;
        }

        // Use reverse order, assuming the inputs were sorted in ascending order.
        for (; k >= 0; k--) {
            int diff_k_j = bins[k] - items[j];
            if (diff_k_j >= 0) {                        // expected to be true at beginning of loop
                boolean swapping = false;
                if (diff_k_j < items[0]) {              // If the space left in this bin would be less than the
                    usableSpace -= diff_k_j;            // smallest item, then this bin would become unusable.
                    if (usableSpace < neededSpace) {    // If the remaining usable space would not suffice,
                        return false;                   // return false immediately, without decrementing, etc.
                    }
                    swapping = true;                    // Need to swap the diminished bins[k] off the active list.
                }
                usableSpace -= items[j];
                neededSpace -= items[j];
                bins[k] = diff_k_j;
                
                if (swapping) {
                    bins[k] = bins[--numUsable];
                    bins[numUsable] = diff_k_j;      
                } else {
                    // Otherwise, sort the list by re-inserting diminished bin[k] value where it now belongs.
                    int q = k;
                    for (; --q >= 0; ) {
                        if (diff_k_j < bins[q]) {
                            bins[q + 1] = bins[q];
                        }
                        else {
                            break;
                        }
                    }
                    bins[q + 1] = diff_k_j;
                }

                // Exhaustive recursion: check all remaining solutions that start with item[j] packed in bin[q]
                if (canPackRecursive(bins, numUsable, items, j, usableSpace, neededSpace)) {
                    return true;
                }
                
                // failed, so swap back and increment.
                if (swapping) {
                    bins[numUsable] = bins[k];
                    bins[k] = diff_k_j;
                    usableSpace += diff_k_j;
                    numUsable++;
                }
                usableSpace += items[j];
                neededSpace += items[j];
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
            Sx.puts("items to pack:    ");
            Sx.printArrayFolded(items, 24);
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
            if (result) {
                assert Arrays.stream(bins).sum() == excess;
            }
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

        int duffels[] = { 2, 5, 2, 2, 6  };
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

            int frames[] = FibonacciInt32.fib32Range(0, 13);
            int photos[] = Primes.primesInRangeIntArray(2, 70);
            numWrong += test_canPack(binPacker, frames, photos, 1, true);
        
            int blocks[] = FibonacciInt32.fib32Range(0, 14);
            int allocs[] = Primes.primesInRangeIntArray(2, 90);
            numWrong += test_canPack(binPacker, blocks, allocs, 1, false);

            frames = FibonacciInt32.fib32Range(0, 15);
            photos = Primes.primesInRangeIntArray(2, 125);
            numWrong += test_canPack(binPacker, frames, photos, 1, false);

            if (level > 2) {    // A naive algorithm may take a very long time...
                frames = FibonacciInt32.fib32Range(0, 15);
                photos[0] = 4;
                numWrong += test_canPack(binPacker, frames, photos, 1, false);
                
                frames = FibonacciInt32.fib32Range(0, 36);
                photos = Primes.primesInRangeIntArray(2, 27650);
                for (int j = 1; j < 1500 && j < photos.length; j++) {
                    photos[j] += 1;
                }
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


