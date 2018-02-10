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
 * Can the space requirements given by items be packed into the specified bins?
 * Implementations: Naive exhaustive recursion with supplementary boolean array.
 * Complexity: Time O(N!), additional space O(N).
 * 
 * Short-circuited sorted exhaustive recursion, no supplementary array:
 * Expected complexity: Time ~O(N^2 log N), additional space O(1).
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
     * Expected complexity: Time ~O(N^2 log N), additional space O(1).
     */
    public static boolean canPackSort(int[] bins, int[] items)
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

        int minUsableIndex = 0;
        while (bins[minUsableIndex] < items[0]) {
            excess -= bins[minUsableIndex];
            minUsableIndex++;
        }
        if (excess < 0)
            return false;                           // return early: insufficient usable space
        usableSpace = neededSpace + excess;

        if (canPackSortRec(binsCopy, minUsableIndex, items, items.length, usableSpace, neededSpace)) {
            // Change the original array.  (Pass by value means bins = binsCopy would not.)
            for (int j = bins.length; --j >= 0; ) {
                bins[j] = binsCopy[j];
            }
            return true;
        }
        Sx.putsArray("failed binCopy:   ", binsCopy);
        return false;
    }

    static void shiftUp(int[] arr, int beg, int end)
    {
        for (int k = end; --k > beg; ) {
            arr[k] = arr[k-1];
        }
    }

    /**
     * Sorted recursion.  Early return if largest item cannot fit in largest remaining bin.
     * @param bins
     * @param numUsable
     * @param items
     * @param numUnpacked
     * @return
     */
    static boolean canPackSortRec(int[] bins, int minUsableIndex, int[] items, int numUnpacked, int usableSpace, int neededSpace)
    {
        if (numUnpacked < 1) {
            return true;
        }
        if (minUsableIndex >= bins.length) {
            return false;
        }

        int j = numUnpacked - 1;
        int k = bins.length - 1;

        // return false if the largest remaining bin cannot fit the largest unpacked item.
        if (bins[k] < items[j]) {
            return false;
        }

        // Use reverse order, assuming the inputs were sorted in ascending order.
        for (; k >= 0; k--) {
            int diff_k_j = bins[k] - items[j];
            if (diff_k_j < 0) {                         // expected to be false at beginning of loop
                break;                                  // assumes that bins is sorted ascending
            }
            //  Sx.format("Try %2d(%2d) in %2d(%2d), leaving bins: ", j, items[j], k, bins[k]);
            if (diff_k_j < items[0]) {              // If the space left in this bin would be less than the
                int reducedSpace = usableSpace - diff_k_j;            // smallest item, then this bin would become unusable.
                if (reducedSpace < neededSpace) {    // If the remaining usable space would not suffice,
                    continue;                       // move on immediately, without decrementing, etc.
                }
                neededSpace -= items[j];
                usableSpace = reducedSpace - items[j];
                // Need to swap the diminished bins[k] off the active list.
                shiftUp(bins, minUsableIndex, k);
                bins[minUsableIndex] = diff_k_j;
                minUsableIndex++;
                // Exhaustive recursion: check all remaining solutions that start with item[j] packed in bin[q]
                //  Sx.printArray(bins);
                //  Sx.format("  total space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                if (canPackSortRec(bins, minUsableIndex, items, j, usableSpace, neededSpace)) {
                    return true;
                }
                // failed, so swap back and increment.
                bins[numUsable++] = bins[k];
                bins[k] = diff_k_j + items[j];
                usableSpace += items[j] + diff_k_j;
                neededSpace += items[j];
            } else {
                neededSpace -= items[j];
                usableSpace -= items[j];
                bins[k] = diff_k_j;                
                
                // Sort the list by re-inserting diminished bin[k] value where it now belongs.
                int q = k;
                for (; --q >= 0; ) {
                    if (diff_k_j < bins[q]) {
                        bins[q + 1] = bins[q];
                    }
                    else {
                        break;
                    }
                }
                int ins_k = q + 1;
                bins[ins_k] = diff_k_j;

                // Exhaustive recursion: check all remaining solutions that start with item[j] packed in bin[q]
                //  Sx.printArray(bins);
                //  Sx.format("  total space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                if (canPackSortRec(bins, minUsableIndex, items, j, usableSpace, neededSpace)) {
                    return true;
                }
                // Failed, so re-sort/restore.
                int z = ins_k;
                for (; z < k; z++) {
                    bins[z] = bins[z + 1];
                }
                bins[z] = diff_k_j + items[j];
                //  Sx.printArray(bins);
                //  Sx.format("  rests space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                
                usableSpace += items[j];
                neededSpace += items[j];
            }
        }
        return false;
    }
    
    
    /**
     * Can the space requirements specified by items be packed into the specified bins?
     * If the packing algorithm succeeds, the values in bins will be decreased by the amounts in items.
     * The items array will be sorted.
     * Expected complexity: Time ~O(N^2 log N), additional space O(1).
     */
    public static boolean canPackTrack(int[] bins, int[] items)
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

        if (canPackTrackRec(binsCopy, bins.length, items, items.length, usableSpace, neededSpace)) {
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
    static boolean canPackTrackRec(int[] bins, int numUsable, int[] items, int numUnpacked, int usableSpace, int neededSpace)
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
            if (diff_k_j < 0) {                         // expected to be false at beginning of loop
                break;                                  // assumes that bins is sorted ascending
            }
            //  Sx.format("Try %2d(%2d) in %2d(%2d), leaving bins: ", j, items[j], k, bins[k]);
            if (diff_k_j < items[0]) {              // If the space left in this bin would be less than the
                int reducedSpace = usableSpace - diff_k_j;            // smallest item, then this bin would become unusable.
                if (reducedSpace < neededSpace) {    // If the remaining usable space would not suffice,
                    continue;                       // move on immediately, without decrementing, etc.
                }
                neededSpace -= items[j];
                usableSpace = reducedSpace - items[j];
                // Need to swap the diminished bins[k] off the active list.
                bins[k] = bins[--numUsable];
                bins[numUsable] = diff_k_j;      
                // Exhaustive recursion: check all remaining solutions that start with item[j] packed in bin[q]
                //  Sx.printArray(bins);
                //  Sx.format("  total space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                if (canPackTrackRec(bins, numUsable, items, j, usableSpace, neededSpace)) {
                    return true;
                }
                // failed, so swap back and increment.
                bins[numUsable++] = bins[k];
                bins[k] = diff_k_j + items[j];
                usableSpace += items[j] + diff_k_j;
                neededSpace += items[j];
            } else {
                neededSpace -= items[j];
                usableSpace -= items[j];
                bins[k] = diff_k_j;
                // Sort the list by re-inserting diminished bin[k] value where it now belongs.
                int q = k;
                for (; --q >= 0; ) {
                    if (diff_k_j < bins[q]) {
                        bins[q + 1] = bins[q];
                    }
                    else {
                        break;
                    }
                }
                int ins_k = q + 1;
                bins[ins_k] = diff_k_j;

                // Exhaustive recursion: check all remaining solutions that start with item[j] packed in bin[q]
                //  Sx.printArray(bins);
                //  Sx.format("  total space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                if (canPackTrackRec(bins, numUsable, items, j, usableSpace, neededSpace)) {
                    return true;
                }
                // Failed, so re-sort/restore.
                int z = ins_k;
                for (; z < k; z++) {
                    bins[z] = bins[z + 1];
                }
                bins[z] = diff_k_j + items[j];
                //  Sx.printArray(bins);
                //  Sx.format("  rests space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                
                usableSpace += items[j];
                neededSpace += items[j];
            }
        }
        return false;
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
        return canPackNaiveRec(bins, items, packed);
    }
    
    /**
     * Naive exhaustive recursion, no early failure (as when sum(bins) < sum(items)), no sorting.
     * Tries to fit items into bins in the original order given.
     * @param bins
     * @param items
     * @param packed
     * @return
     */
    static boolean canPackNaiveRec(int[] bins, int[] items, boolean[] packed)
    {
        if (allTrue(packed)) {
            return true;
        }

        for (int i = 0; i < items.length; i++) {
            if (!packed[i]) {
                // Exhaustive: check all remaining solutions that start with item[i] packed in some bin[j]
                packed[i] = true;
                for (int j = 0; j < bins.length; j++) {
                    if (bins[j] >= items[i]) {
                        bins[j] -= items[i];            // deduct item amount from bin and try to pack the rest
                        if(canPackNaiveRec(bins, items, packed)){
                            return true;                // success: return
                        }
                        bins[j] += items[i];   // failure: restore item amount to bin
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
    public static int test_canPack(BinPackIntArrays packer, int bins[], int items[], int verbose, String name, int number, boolean expected)
    {
        boolean result = false;
        int excess = BinPack.excessBinSpace(bins, items);
        if (verbose > 0) {
            Sx.format("\n\t  Test canPack:  %s: %d\n", name, number);
            Sx.putsArray("bin space before: ", bins);
            Sx.print("items to pack:    ");
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
            result = packer.canPack(bins, items);
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
    
    public static int test_packer(BinPackIntArrays packer, String packer_name, int level)
    {
        String testName = BinPack.class.getName() + ".test_packer(" + packer_name + ")";
        Sz.begin(testName);
        int numWrong = 0, testNum = 0;
        //IBinPack binPacker = new BinPackRec();

        if (level < 1) {
            int seas[] = {2, 2, 37};
            int holes[] = {4, 37};
            numWrong += test_canPack(packer, seas, holes, 1, testName, ++testNum, false);
            
            int servers[] = {8, 16, 8, 32};
            int tasks[] = {18, 4, 8, 4, 6, 6, 8, 8};
            Sx.format("%s:\t%d\n", testName, ++testNum);
            numWrong += test_canPack(packer, servers, tasks, 1, testName, ++testNum, true);
    
            int limits[] = {1, 3};
            int needs[] = { 4 };
            Sx.format("%s:\t%d\n", testName, ++testNum);
            numWrong += test_canPack(packer, limits, needs, 1, testName, ++testNum, false);
    
            int duffels[] = { 2, 5, 2, 2, 6  };
            int bags[] = { 3, 3, 5};
            Sx.format("%s:\t%d\n", testName, ++testNum);
            numWrong += test_canPack(packer, duffels, bags, 1, testName, ++testNum, true);
    
            int sashes[] = { 1, 2, 3, 4, 5, 6, 8, 9  };
            int badges[] = { 1, 4, 6, 6, 8, 8, };
            Sx.format("%s:\t%d\n", testName, ++testNum);
            numWrong += test_canPack(packer, sashes, badges, 1, testName, ++testNum, false);
        }
        
        int crates[] = FibonacciInt32.fib32Range(0, 11);
        int boxes[] = Primes.primesInRangeIntArray(2, 42);
        boxes[boxes.length-1] = 27;
        numWrong += test_canPack(packer, crates, boxes, 1, testName, ++testNum, true);

        
        if (level > 1) {
            int fibs[] = FibonacciInt32.fib32Range(0, 12);
            int mems[] = Primes.primesInRangeIntArray(2, 47);
            numWrong += test_canPack(packer, fibs, mems, 1, testName, ++testNum, true);
            
            int bins[] = FibonacciInt32.fib32Range(0, 9);
            int bits[] = Primes.primesInRangeIntArray(2, 25);
            numWrong += test_canPack(packer, bins, bits, 1, testName, ++testNum, false);

            if (level > 2) {    // A naive algorithm may take a very long time...
                int frames[] = FibonacciInt32.fib32Range(0, 13);
                int photos[] = Primes.primesInRangeIntArray(2, 70);
                numWrong += test_canPack(packer, frames, photos, 1, testName, ++testNum, false);
                            int blocks[] = FibonacciInt32.fib32Range(0, 14);
                int allocs[] = Primes.primesInRangeIntArray(2, 90);
                numWrong += test_canPack(packer, blocks, allocs, 1, testName, ++testNum, false);

                frames = FibonacciInt32.fib32Range(0, 15);
                photos = Primes.primesInRangeIntArray(2, 125);
                numWrong += test_canPack(packer, frames, photos, 1, testName, ++testNum, false);

                frames = FibonacciInt32.fib32Range(0, 15);
                photos[0] = 4;
                numWrong += test_canPack(packer, frames, photos, 1, testName, ++testNum, false);

                frames = FibonacciInt32.fib32Range(0, 36);
                photos = Primes.primesInRangeIntArray(2, 27650);
                for (int j = 1; j < 1500 && j < photos.length; j++) {
                    photos[j] += 1;
                }
                numWrong += test_canPack(packer, frames, photos, 1, testName, ++testNum, false);
            }
        }

        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static int unit_test(int level)
    {
        String testName = BinPack.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_packer(BinPack::canPackTrack, "canPackTrack", level);
        numWrong += test_packer(BinPack::canPackNaive, "canPackNaive", level);

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(2);
    }

}


