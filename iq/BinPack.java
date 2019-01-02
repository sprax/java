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
    public static boolean canPackSort1(int[] bins, int[] items)
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


    static void shiftDown(int[] arr, int beg, int end)
    {
        while (beg < end)
            arr[beg] = arr[++beg];
    }


    static void shiftUp(int[] arr, int beg, int end)
    {
        while (end > beg)
            arr[end] = arr[--end];
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
                bins[minUsableIndex++] = diff_k_j;
                // Exhaustive recursion: check all remaining solutions that start with item[j] packed in bin[q]
                //  Sx.printArray(bins);
                //  Sx.format("  total space %3d, max to pack %2d\n", usableSpace, (j > 0 ? items[j-1] : 0));
                if (canPackSortRec(bins, minUsableIndex, items, j, usableSpace, neededSpace)) {
                    return true;
                }
                // failed, so swap back and increment.
                minUsableIndex--;
                shiftDown(bins, minUsableIndex, k);
                int bins_k_prior = diff_k_j + items[j];
                bins[k] = bins_k_prior;
                usableSpace += bins_k_prior;
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
                for (int j = 1; j < 1988 && j < photos.length; j++) {
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

        //numWrong += test_packer(BinPack::canPackNaive, "canPackNaive", level);
        numWrong += test_packer(BinPack::canPackTrack, "canPackTrack", level);
        //numWrong += test_packer(BinPack::canPackSort1, "canPackSort1", level);

        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args) 
    {
        unit_test(3);
    }

}

/******************************************************************************
     * BEGIN sprax.questions.BinPack.unit_test
BEGIN sprax.questions.BinPack.test_packer(canPackTrack)

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 1
bin space before:   1  1  2  3  5  8 13 21 34 55 89
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 27
Total bin space - items space: 232 - 224 = 8
Pack items in bins? true
Bin space after:    1  1  2  2  1  0  0  1  0  0  0
Run time millis:    1

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 2
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47
Total bin space - items space: 376 - 328 = 48
Pack items in bins? true
Bin space after:    1  1  2  3  4  4  5  5  5  6  6  6
Run time millis:    0

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 3
bin space before:   1  1  2  3  5  8 13 21 34
items to pack:      2  3  5  7 11 13 17 19 23
Total bin space - items space: 88 - 100 = -12
Insufficient total bin space.

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 4
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67
Total bin space - items space: 609 - 568 = 41
Pack items in bins? true
Bin space after:    1  1  2  2  2  2  3  3  4  5  5  5  6
Run time millis:    0
Wrong result true, expected false

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 5
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89

Total bin space - items space: 986 - 963 = 23
Pack items in bins? true
Bin space after:    1  1  2  2  2  2  3  3  3  0  1  1  1  1
Run time millis:    0
Wrong result true, expected false

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 6
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89
 97 101 103 107 109 113
Total bin space - items space: 1596 - 1593 = 3
failed binCopy:     1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Pack items in bins? false
Bin space after:    1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Run time millis:    12239

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 7
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
items to pack:      4  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89
 97 101 103 107 109 113
Total bin space - items space: 1596 - 1595 = 1
failed binCopy:     1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Pack items in bins? false
Bin space after:    1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Run time millis:    6154

      Test canPack:  sprax.questions.BinPack.test_packer(canPackTrack): 8
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377 610 987 1597 2584 4181 6765 10946 17711 28657 46368 75025 121393 196418 317811 514229 832040 1346269 2178309 3524578 5702887 9227465 14930352
items to pack:      2  4  6  8 12 14 18 20 24 30 32 38 42 44 48 54 60 62 68 72 74 80 84 90
 98 102 104 108 110 114 128 132 138 140 150 152 158 164 168 174 180 182 192 194 198 200 212 224
 228 230 234 240 242 252 258 264 270 272 278 282 284 294 308 312 314 318 332 338 348 350 354 360
 368 374 380 384 390 398 402 410 420 422 432 434 440 444 450 458 462 464 468 480 488 492 500 504
 510 522 524 542 548 558 564 570 572 578 588 594 600 602 608 614 618 620 632 642 644 648 654 660
 662 674 678 684 692 702 710 720 728 734 740 744 752 758 762 770 774 788 798 810 812 822 824 828
 830 840 854 858 860 864 878 882 884 888 908 912 920 930 938 942 948 954 968 972 978 984 992 998
 1010 1014 1020 1022 1032 1034 1040 1050 1052 1062 1064 1070 1088 1092 1094 1098 1104 1110 1118 1124 1130 1152 1154 1164
 1172 1182 1188 1194 1202 1214 1218 1224 1230 1232 1238 1250 1260 1278 1280 1284 1290 1292 1298 1302 1304 1308 1320 1322
 1328 1362 1368 1374 1382 1400 1410 1424 1428 1430 1434 1440 1448 1452 1454 1460 1472 1482 1484 1488 1490 1494 1500 1512
 1524 1532 1544 1550 1554 1560 1568 1572 1580 1584 1598 1602 1608 1610 1614 1620 1622 1628 1638 1658 1664 1668 1670 1694
 1698 1700 1710 1722 1724 1734 1742 1748 1754 1760 1778 1784 1788 1790 1802 1812 1824 1832 1848 1862 1868 1872 1874 1878
 1880 1890 1902 1908 1914 1932 1934 1950 1952 1974 1980 1988 1994 1998 2000 2004 2012 2018 2028 2030 2040 2054 2064 2070
 2082 2084 2088 2090 2100 2112 2114 2130 2132 2138 2142 2144 2154 2162 2180 2204 2208 2214 2222 2238 2240 2244 2252 2268
 2270 2274 2282 2288 2294 2298 2310 2312 2334 2340 2342 2348 2352 2358 2372 2378 2382 2384 2390 2394 2400 2412 2418 2424
 2438 2442 2448 2460 2468 2474 2478 2504 2522 2532 2540 2544 2550 2552 2558 2580 2592 2594 2610 2618 2622 2634 2648 2658
 2660 2664 2672 2678 2684 2688 2690 2694 2700 2708 2712 2714 2720 2730 2732 2742 2750 2754 2768 2778 2790 2792 2798 2802
 2804 2820 2834 2838 2844 2852 2858 2862 2880 2888 2898 2904 2910 2918 2928 2940 2954 2958 2964 2970 2972 3000 3002 3012
 3020 3024 3038 3042 3050 3062 3068 3080 3084 3090 3110 3120 3122 3138 3164 3168 3170 3182 3188 3192 3204 3210 3218 3222
 3230 3252 3254 3258 3260 3272 3300 3302 3308 3314 3320 3324 3330 3332 3344 3348 3360 3362 3372 3374 3390 3392 3408 3414
 3434 3450 3458 3462 3464 3468 3470 3492 3500 3512 3518 3528 3530 3534 3540 3542 3548 3558 3560 3572 3582 3584 3594 3608
 3614 3618 3624 3632 3638 3644 3660 3672 3674 3678 3692 3698 3702 3710 3720 3728 3734 3740 3762 3768 3770 3780 3794 3798
 3804 3822 3824 3834 3848 3852 3854 3864 3878 3882 3890 3908 3912 3918 3920 3924 3930 3932 3944 3948 3968 3990 4002 4004
 4008 4014 4020 4022 4028 4050 4052 4058 4074 4080 4092 4094 4100 4112 4128 4130 4134 4140 4154 4158 4160 4178 4202 4212
 4218 4220 4230 4232 4242 4244 4254 4260 4262 4272 4274 4284 4290 4298 4328 4338 4340 4350 4358 4364 4374 4392 4398 4410
 4422 4424 4442 4448 4452 4458 4464 4482 4484 4494 4508 4514 4518 4520 4524 4548 4550 4562 4568 4584 4592 4598 4604 4622
 4638 4640 4644 4650 4652 4658 4664 4674 4680 4692 4704 4722 4724 4730 4734 4752 4760 4784 4788 4790 4794 4800 4802 4814
 4818 4832 4862 4872 4878 4890 4904 4910 4920 4932 4934 4938 4944 4952 4958 4968 4970 4974 4988 4994 5000 5004 5010 5012
 5022 5024 5040 5052 5060 5078 5082 5088 5100 5102 5108 5114 5120 5148 5154 5168 5172 5180 5190 5198 5210 5228 5232 5234
 5238 5262 5274 5280 5282 5298 5304 5310 5324 5334 5348 5352 5382 5388 5394 5400 5408 5414 5418 5420 5432 5438 5442 5444
 5450 5472 5478 5480 5484 5502 5504 5508 5520 5522 5528 5532 5558 5564 5570 5574 5582 5592 5624 5640 5642 5648 5652 5654
 5658 5660 5670 5684 5690 5694 5702 5712 5718 5738 5742 5744 5750 5780 5784 5792 5802 5808 5814 5822 5828 5840 5844 5850
 5852 5858 5862 5868 5870 5880 5882 5898 5904 5924 5928 5940 5954 5982 5988 6008 6012 6030 6038 6044 6048 6054 6068 6074
 6080 6090 6092 6102 6114 6122 6132 6134 6144 6152 6164 6174 6198 6200 6204 6212 6218 6222 6230 6248 6258 6264 6270 6272
 6278 6288 6300 6302 6312 6318 6324 6330 6338 6344 6354 6360 6362 6368 6374 6380 6390 6398 6422 6428 6450 6452 6470 6474
 6482 6492 6522 6530 6548 6552 6554 6564 6570 6572 6578 6582 6600 6608 6620 6638 6654 6660 6662 6674 6680 6690 6692 6702
 6704 6710 6720 6734 6738 6762 6764 6780 6782 6792 6794 6804 6824 6828 6830 6834 6842 6858 6864 6870 6872 6884 6900 6908
 6912 6918 6948 6950 6960 6962 6968 6972 6978 6984 6992 6998 7002 7014 7020 7028 7040 7044 7058 7070 7080 7104 7110 7122
 7128 7130 7152 7160 7178 7188 7194 7208 7212 7214 7220 7230 7238 7244 7248 7254 7284 7298 7308 7310 7322 7332 7334 7350
 7352 7370 7394 7412 7418 7434 7452 7458 7460 7478 7482 7488 7490 7500 7508 7518 7524 7530 7538 7542 7548 7550 7560 7562
 7574 7578 7584 7590 7592 7604 7608 7622 7640 7644 7650 7670 7674 7682 7688 7692 7700 7704 7718 7724 7728 7742 7754 7758
 7760 7790 7794 7818 7824 7830 7842 7854 7868 7874 7878 7880 7884 7902 7908 7920 7928 7934 7938 7950 7952 7964 7994 8010
 8012 8018 8040 8054 8060 8070 8082 8088 8090 8094 8102 8112 8118 8124 8148 8162 8168 8172 8180 8192 8210 8220 8222 8232
 8234 8238 8244 8264 8270 8274 8288 8292 8294 8298 8312 8318 8330 8354 8364 8370 8378 8388 8390 8420 8424 8430 8432 8444
 8448 8462 8468 8502 8514 8522 8528 8538 8540 8544 8564 8574 8582 8598 8600 8610 8624 8628 8630 8642 8648 8664 8670 8678
 8682 8690 8694 8700 8708 8714 8720 8732 8738 8742 8748 8754 8762 8780 8784 8804 8808 8820 8822 8832 8838 8840 8850 8862
 8864 8868 8888 8894 8924 8930 8934 8942 8952 8964 8970 8972 9000 9002 9008 9012 9014 9030 9042 9044 9050 9060 9068 9092
 9104 9110 9128 9134 9138 9152 9158 9162 9174 9182 9188 9200 9204 9210 9222 9228 9240 9242 9258 9278 9282 9284 9294 9312
 9320 9324 9338 9342 9344 9350 9372 9378 9392 9398 9404 9414 9420 9422 9432 9434 9438 9440 9462 9464 9468 9474 9480 9492
 9498 9512 9522 9534 9540 9548 9552 9588 9602 9614 9620 9624 9630 9632 9644 9650 9662 9678 9680 9690 9698 9720 9722 9734
 9740 9744 9750 9768 9770 9782 9788 9792 9804 9812 9818 9830 9834 9840 9852 9858 9860 9872 9884 9888 9902 9908 9924 9930
 9932 9942 9950 9968 9974 10008 10010 10038 10040 10062 10068 10070 10080 10092 10094 10100 10104 10112 10134 10140 10142 10152 10160 10164
 10170 10178 10182 10194 10212 10224 10244 10248 10254 10260 10268 10272 10274 10290 10302 10304 10314 10322 10332 10334 10338 10344 10358 10370
 10392 10400 10428 10430 10434 10454 10458 10460 10464 10478 10488 10500 10502 10514 10530 10532 10560 10568 10590 10598 10602 10608 10614 10628
 10632 10640 10652 10658 10664 10668 10688 10692 10710 10712 10724 10730 10734 10740 10754 10772 10782 10790 10800 10832 10838 10848 10854 10860
 10862 10868 10884 10890 10892 10904 10910 10938 10940 10950 10958 10974 10980 10988 10994 11004 11028 11048 11058 11060 11070 11072 11084 11088
 11094 11114 11118 11120 11132 11150 11160 11162 11172 11174 11178 11198 11214 11240 11244 11252 11258 11262 11274 11280 11288 11300 11312 11318
 11322 11330 11352 11354 11370 11384 11394 11400 11412 11424 11438 11444 11448 11468 11472 11484 11490 11492 11498 11504 11520 11528 11550 11552
 11580 11588 11594 11598 11618 11622 11634 11658 11678 11682 11690 11700 11702 11718 11720 11732 11744 11778 11780 11784 11790 11802 11808 11814
 11822 11828 11832 11834 11840 11864 11868 11888 11898 11904 11910 11924 11928 11934 11940 11942 11954 11960 11970 11972 11982 11988 12008 12012
 12038 12042 12044 12050 12072 12074 12098 12102 12108 12110 12114 12120 12144 12150 12158 12162 12164 12198 12204 12212 12228 12240 12242 12252
 12254 12264 12270 12278 12282 12290 12302 12324 12330 12344 12348 12374 12378 12380 12392 12402 12410 12414 12422 12434 12438 12452 12458 12474
 12480 12488 12492 12498 12504 12512 12518 12528 12540 12542 12548 12554 12570 12578 12584 12590 12602 12612 12614 12620 12638 12642 12648 12654
 12660 12672 12690 12698 12704 12714 12722 12740 12744 12758 12764 12782 12792 12800 12810 12822 12824 12830 12842 12854 12890 12894 12900 12908
 12912 12918 12920 12924 12942 12954 12960 12968 12974 12980 12984 13002 13004 13008 13010 13034 13038 13044 13050 13064 13094 13100 13104 13110
 13122 13128 13148 13152 13160 13164 13172 13178 13184 13188 13218 13220 13230 13242 13250 13260 13268 13292 13298 13310 13314 13328 13332 13338
 13340 13368 13382 13398 13400 13412 13418 13422 13442 13452 13458 13464 13470 13478 13488 13500 13514 13524 13538 13554 13568 13578 13592 13598
 13614 13620 13628 13634 13650 13670 13680 13682 13688 13692 13694 13698 13710 13712 13722 13724 13730 13752 13758 13760 13764 13782 13790 13800
 13808 13830 13832 13842 13860 13874 13878 13880 13884 13902 13904 13908 13914 13922 13932 13934 13964 13968 13998 14000 14010 14012 14030 14034
 14052 14058 14072 14082 14084 14088 14108 14144 14150 14154 14160 14174 14178 14198 14208 14222 14244 14250 14252 14282 14294 14304 14322 14324
 14328 14342 14348 14370 14388 14390 14402 14408 14412 14420 14424 14432 14438 14448 14450 14462 14480 14490 14504 14520 14534 14538 14544 14550
 14552 14558 14562 14564 14592 14594 14622 14628 14630 14634 14640 14654 14658 14670 14684 14700 14714 14718 14724 14732 14738 14742 14748 14754
 14760 14768 14772 14780 14784 14798 14814 14822 14828 14832 14844 14852 14868 14870 14880 14888 14892 14898 14924 14930 14940 14948 14952 14958
 14970 14984 15014 15018 15032 15054 15062 15074 15078 15084 15092 15102 15108 15122 15132 15138 15140 15150 15162 15174 15188 15194 15200 15218
 15228 15234 15242 15260 15264 15270 15272 15278 15288 15290 15300 15308 15314 15320 15330 15332 15350 15360 15362 15374 15378 15384 15392 15402
 15414 15428 15440 15444 15452 15462 15468 15474 15494 15498 15512 15528 15542 15552 15560 15570 15582 15584 15602 15608 15620 15630 15642 15644
 15648 15650 15662 15668 15672 15680 15684 15728 15732 15734 15738 15740 15750 15762 15768 15774 15788 15792 15798 15804 15810 15818 15824 15860
 15878 15882 15888 15890 15902 15908 15914 15920 15924 15938 15960 15972 15974 15992 16002 16008 16034 16058 16062 16064 16068 16070 16074 16088
 16092 16098 16104 16112 16128 16140 16142 16184 16188 16190 16194 16218 16224 16230 16232 16250 16253 16267 16273 16301 16319 16333 16339 16349
 16361 16363 16369 16381 16411 16417 16421 16427 16433 16447 16451 16453 16477 16481 16487 16493 16519 16529 16547 16553 16561 16567 16573 16603
 16607 16619 16631 16633 16649 16651 16657 16661 16673 16691 16693 16699 16703 16729 16741 16747 16759 16763 16787 16811 16823 16829 16831 16843
 16871 16879 16883 16889 16901 16903 16921 16927 16931 16937 16943 16963 16979 16981 16987 16993 17011 17021 17027 17029 17033 17041 17047 17053
 17077 17093 17099 17107 17117 17123 17137 17159 17167 17183 17189 17191 17203 17207 17209 17231 17239 17257 17291 17293 17299 17317 17321 17327
 17333 17341 17351 17359 17377 17383 17387 17389 17393 17401 17417 17419 17431 17443 17449 17467 17471 17477 17483 17489 17491 17497 17509 17519
 17539 17551 17569 17573 17579 17581 17597 17599 17609 17623 17627 17657 17659 17669 17681 17683 17707 17713 17729 17737 17747 17749 17761 17783
 17789 17791 17807 17827 17837 17839 17851 17863 17881 17891 17903 17909 17911 17921 17923 17929 17939 17957 17959 17971 17977 17981 17987 17989
 18013 18041 18043 18047 18049 18059 18061 18077 18089 18097 18119 18121 18127 18131 18133 18143 18149 18169 18181 18191 18199 18211 18217 18223
 18229 18233 18251 18253 18257 18269 18287 18289 18301 18307 18311 18313 18329 18341 18353 18367 18371 18379 18397 18401 18413 18427 18433 18439
 18443 18451 18457 18461 18481 18493 18503 18517 18521 18523 18539 18541 18553 18583 18587 18593 18617 18637 18661 18671 18679 18691 18701 18713
 18719 18731 18743 18749 18757 18773 18787 18793 18797 18803 18839 18859 18869 18899 18911 18913 18917 18919 18947 18959 18973 18979 19001 19009
 19013 19031 19037 19051 19069 19073 19079 19081 19087 19121 19139 19141 19157 19163 19181 19183 19207 19211 19213 19219 19231 19237 19249 19259
 19267 19273 19289 19301 19309 19319 19333 19373 19379 19381 19387 19391 19403 19417 19421 19423 19427 19429 19433 19441 19447 19457 19463 19469
 19471 19477 19483 19489 19501 19507 19531 19541 19543 19553 19559 19571 19577 19583 19597 19603 19609 19661 19681 19687 19697 19699 19709 19717
 19727 19739 19751 19753 19759 19763 19777 19793 19801 19813 19819 19841 19843 19853 19861 19867 19889 19891 19913 19919 19927 19937 19949 19961
 19963 19973 19979 19991 19993 19997 20011 20021 20023 20029 20047 20051 20063 20071 20089 20101 20107 20113 20117 20123 20129 20143 20147 20149
 20161 20173 20177 20183 20201 20219 20231 20233 20249 20261 20269 20287 20297 20323 20327 20333 20341 20347 20353 20357 20359 20369 20389 20393
 20399 20407 20411 20431 20441 20443 20477 20479 20483 20507 20509 20521 20533 20543 20549 20551 20563 20593 20599 20611 20627 20639 20641 20663
 20681 20693 20707 20717 20719 20731 20743 20747 20749 20753 20759 20771 20773 20789 20807 20809 20849 20857 20873 20879 20887 20897 20899 20903
 20921 20929 20939 20947 20959 20963 20981 20983 21001 21011 21013 21017 21019 21023 21031 21059 21061 21067 21089 21101 21107 21121 21139 21143
 21149 21157 21163 21169 21179 21187 21191 21193 21211 21221 21227 21247 21269 21277 21283 21313 21317 21319 21323 21341 21347 21377 21379 21383
 21391 21397 21401 21407 21419 21433 21467 21481 21487 21491 21493 21499 21503 21517 21521 21523 21529 21557 21559 21563 21569 21577 21587 21589
 21599 21601 21611 21613 21617 21647 21649 21661 21673 21683 21701 21713 21727 21737 21739 21751 21757 21767 21773 21787 21799 21803 21817 21821
 21839 21841 21851 21859 21863 21871 21881 21893 21911 21929 21937 21943 21961 21977 21991 21997 22003 22013 22027 22031 22037 22039 22051 22063
 22067 22073 22079 22091 22093 22109 22111 22123 22129 22133 22147 22153 22157 22159 22171 22189 22193 22229 22247 22259 22271 22273 22277 22279
 22283 22291 22303 22307 22343 22349 22367 22369 22381 22391 22397 22409 22433 22441 22447 22453 22469 22481 22483 22501 22511 22531 22541 22543
 22549 22567 22571 22573 22613 22619 22621 22637 22639 22643 22651 22669 22679 22691 22697 22699 22709 22717 22721 22727 22739 22741 22751 22769
 22777 22783 22787 22807 22811 22817 22853 22859 22861 22871 22877 22901 22907 22921 22937 22943 22961 22963 22973 22993 23003 23011 23017 23021
 23027 23029 23039 23041 23053 23057 23059 23063 23071 23081 23087 23099 23117 23131 23143 23159 23167 23173 23189 23197 23201 23203 23209 23227
 23251 23269 23279 23291 23293 23297 23311 23321 23327 23333 23339 23357 23369 23371 23399 23417 23431 23447 23459 23473 23497 23509 23531 23537
 23539 23549 23557 23561 23563 23567 23581 23593 23599 23603 23609 23623 23627 23629 23633 23663 23669 23671 23677 23687 23689 23719 23741 23743
 23747 23753 23761 23767 23773 23789 23801 23813 23819 23827 23831 23833 23857 23869 23873 23879 23887 23893 23899 23909 23911 23917 23929 23957
 23971 23977 23981 23993 24001 24007 24019 24023 24029 24043 24049 24061 24071 24077 24083 24091 24097 24103 24107 24109 24113 24121 24133 24137
 24151 24169 24179 24181 24197 24203 24223 24229 24239 24247 24251 24281 24317 24329 24337 24359 24371 24373 24379 24391 24407 24413 24419 24421
 24439 24443 24469 24473 24481 24499 24509 24517 24527 24533 24547 24551 24571 24593 24611 24623 24631 24659 24671 24677 24683 24691 24697 24709
 24733 24749 24763 24767 24781 24793 24799 24809 24821 24841 24847 24851 24859 24877 24889 24907 24917 24919 24923 24943 24953 24967 24971 24977
 24979 24989 25013 25031 25033 25037 25057 25073 25087 25097 25111 25117 25121 25127 25147 25153 25163 25169 25171 25183 25189 25219 25229 25237
 25243 25247 25253 25261 25301 25303 25307 25309 25321 25339 25343 25349 25357 25367 25373 25391 25409 25411 25423 25439 25447 25453 25457 25463
 25469 25471 25523 25537 25541 25561 25577 25579 25583 25589 25601 25603 25609 25621 25633 25639 25643 25657 25667 25673 25679 25693 25703 25717
 25733 25741 25747 25759 25763 25771 25793 25799 25801 25819 25841 25847 25849 25867 25873 25889 25903 25913 25919 25931 25933 25939 25943 25951
 25969 25981 25997 25999 26003 26017 26021 26029 26041 26053 26083 26099 26107 26111 26113 26119 26141 26153 26161 26171 26177 26183 26189 26203
 26209 26227 26237 26249 26251 26261 26263 26267 26293 26297 26309 26317 26321 26339 26347 26357 26371 26387 26393 26399 26407 26417 26423 26431
 26437 26449 26459 26479 26489 26497 26501 26513 26539 26557 26561 26573 26591 26597 26627 26633 26641 26647 26669 26681 26683 26687 26693 26699
 26701 26711 26713 26717 26723 26729 26731 26737 26759 26777 26783 26801 26813 26821 26833 26839 26849 26861 26863 26879 26881 26891 26893 26903
 26921 26927 26947 26951 26953 26959 26981 26987 26993 27011 27017 27031 27043 27059 27061 27067 27073 27077 27091 27103 27107 27109 27127 27143
 27179 27191 27197 27211 27239 27241 27253 27259 27271 27277 27281 27283 27299 27329 27337 27361 27367 27397 27407 27409 27427 27431 27437 27449
 27457 27479 27481 27487 27509 27527 27529 27539 27541 27551 27581 27583 27611 27617 27631 27647
Total bin space - items space: 39088168 - 39087868 = 300
Pack items in bins? true
Bin space after:    1  1  2  2  3  3  4  5  5  7  7  8  9  9 10 11 12 13 13 16 16 17 19 19 20 21 21 21  1  1  0  1  1  0  1  0
Run time millis:    12709
Wrong result true, expected false
END   sprax.questions.BinPack.test_packer(canPackTrack),  wrong 3,  FAIL
BEGIN sprax.questions.BinPack.test_packer(canPackSort1)

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 1
bin space before:   1  1  2  3  5  8 13 21 34 55 89
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 27
Total bin space - items space: 232 - 224 = 8
Pack items in bins? true
Bin space after:    1  1  0  0  0  1  0  0  1  2  2
Run time millis:    0

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 2
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47
Total bin space - items space: 376 - 328 = 48
Pack items in bins? true
Bin space after:    1  1  2  3  4  4  5  5  5  6  6  6
Run time millis:    0

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 3
bin space before:   1  1  2  3  5  8 13 21 34
items to pack:      2  3  5  7 11 13 17 19 23
Total bin space - items space: 88 - 100 = -12
Insufficient total bin space.

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 4
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67
Total bin space - items space: 609 - 568 = 41
Pack items in bins? true
Bin space after:    1  1  2  2  2  2  3  3  4  5  5  5  6
Run time millis:    0
Wrong result true, expected false

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 5
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89

Total bin space - items space: 986 - 963 = 23
Pack items in bins? true
Bin space after:    1  1  1  1  1  1  0  2  2  2  2  3  3  3
Run time millis:    0
Wrong result true, expected false

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 6
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
items to pack:      2  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89
 97 101 103 107 109 113
Total bin space - items space: 1596 - 1593 = 3
failed binCopy:     1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Pack items in bins? false
Bin space after:    1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Run time millis:    13066

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 7
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
items to pack:      4  3  5  7 11 13 17 19 23 29 31 37 41 43 47 53 59 61 67 71 73 79 83 89
 97 101 103 107 109 113
Total bin space - items space: 1596 - 1595 = 1
Pack items in bins? false
Bin space after:    1  1  2  3  5  8 13 21 34 55 89 144 233 377 610
Run time millis:    0

      Test canPack:  sprax.questions.BinPack.test_packer(canPackSort1): 8
bin space before:   1  1  2  3  5  8 13 21 34 55 89 144 233 377 610 987 1597 2584 4181 6765 10946 17711 28657 46368 75025 121393 196418 317811 514229 832040 1346269 2178309 3524578 5702887 9227465 14930352
items to pack:      2  4  6  8 12 14 18 20 24 30 32 38 42 44 48 54 60 62 68 72 74 80 84 90
 98 102 104 108 110 114 128 132 138 140 150 152 158 164 168 174 180 182 192 194 198 200 212 224
 228 230 234 240 242 252 258 264 270 272 278 282 284 294 308 312 314 318 332 338 348 350 354 360
 368 374 380 384 390 398 402 410 420 422 432 434 440 444 450 458 462 464 468 480 488 492 500 504
 510 522 524 542 548 558 564 570 572 578 588 594 600 602 608 614 618 620 632 642 644 648 654 660
 662 674 678 684 692 702 710 720 728 734 740 744 752 758 762 770 774 788 798 810 812 822 824 828
 830 840 854 858 860 864 878 882 884 888 908 912 920 930 938 942 948 954 968 972 978 984 992 998
 1010 1014 1020 1022 1032 1034 1040 1050 1052 1062 1064 1070 1088 1092 1094 1098 1104 1110 1118 1124 1130 1152 1154 1164
 1172 1182 1188 1194 1202 1214 1218 1224 1230 1232 1238 1250 1260 1278 1280 1284 1290 1292 1298 1302 1304 1308 1320 1322
 1328 1362 1368 1374 1382 1400 1410 1424 1428 1430 1434 1440 1448 1452 1454 1460 1472 1482 1484 1488 1490 1494 1500 1512
 1524 1532 1544 1550 1554 1560 1568 1572 1580 1584 1598 1602 1608 1610 1614 1620 1622 1628 1638 1658 1664 1668 1670 1694
 1698 1700 1710 1722 1724 1734 1742 1748 1754 1760 1778 1784 1788 1790 1802 1812 1824 1832 1848 1862 1868 1872 1874 1878
 1880 1890 1902 1908 1914 1932 1934 1950 1952 1974 1980 1988 1994 1998 2000 2004 2012 2018 2028 2030 2040 2054 2064 2070
 2082 2084 2088 2090 2100 2112 2114 2130 2132 2138 2142 2144 2154 2162 2180 2204 2208 2214 2222 2238 2240 2244 2252 2268
 2270 2274 2282 2288 2294 2298 2310 2312 2334 2340 2342 2348 2352 2358 2372 2378 2382 2384 2390 2394 2400 2412 2418 2424
 2438 2442 2448 2460 2468 2474 2478 2504 2522 2532 2540 2544 2550 2552 2558 2580 2592 2594 2610 2618 2622 2634 2648 2658
 2660 2664 2672 2678 2684 2688 2690 2694 2700 2708 2712 2714 2720 2730 2732 2742 2750 2754 2768 2778 2790 2792 2798 2802
 2804 2820 2834 2838 2844 2852 2858 2862 2880 2888 2898 2904 2910 2918 2928 2940 2954 2958 2964 2970 2972 3000 3002 3012
 3020 3024 3038 3042 3050 3062 3068 3080 3084 3090 3110 3120 3122 3138 3164 3168 3170 3182 3188 3192 3204 3210 3218 3222
 3230 3252 3254 3258 3260 3272 3300 3302 3308 3314 3320 3324 3330 3332 3344 3348 3360 3362 3372 3374 3390 3392 3408 3414
 3434 3450 3458 3462 3464 3468 3470 3492 3500 3512 3518 3528 3530 3534 3540 3542 3548 3558 3560 3572 3582 3584 3594 3608
 3614 3618 3624 3632 3638 3644 3660 3672 3674 3678 3692 3698 3702 3710 3720 3728 3734 3740 3762 3768 3770 3780 3794 3798
 3804 3822 3824 3834 3848 3852 3854 3864 3878 3882 3890 3908 3912 3918 3920 3924 3930 3932 3944 3948 3968 3990 4002 4004
 4008 4014 4020 4022 4028 4050 4052 4058 4074 4080 4092 4094 4100 4112 4128 4130 4134 4140 4154 4158 4160 4178 4202 4212
 4218 4220 4230 4232 4242 4244 4254 4260 4262 4272 4274 4284 4290 4298 4328 4338 4340 4350 4358 4364 4374 4392 4398 4410
 4422 4424 4442 4448 4452 4458 4464 4482 4484 4494 4508 4514 4518 4520 4524 4548 4550 4562 4568 4584 4592 4598 4604 4622
 4638 4640 4644 4650 4652 4658 4664 4674 4680 4692 4704 4722 4724 4730 4734 4752 4760 4784 4788 4790 4794 4800 4802 4814
 4818 4832 4862 4872 4878 4890 4904 4910 4920 4932 4934 4938 4944 4952 4958 4968 4970 4974 4988 4994 5000 5004 5010 5012
 5022 5024 5040 5052 5060 5078 5082 5088 5100 5102 5108 5114 5120 5148 5154 5168 5172 5180 5190 5198 5210 5228 5232 5234
 5238 5262 5274 5280 5282 5298 5304 5310 5324 5334 5348 5352 5382 5388 5394 5400 5408 5414 5418 5420 5432 5438 5442 5444
 5450 5472 5478 5480 5484 5502 5504 5508 5520 5522 5528 5532 5558 5564 5570 5574 5582 5592 5624 5640 5642 5648 5652 5654
 5658 5660 5670 5684 5690 5694 5702 5712 5718 5738 5742 5744 5750 5780 5784 5792 5802 5808 5814 5822 5828 5840 5844 5850
 5852 5858 5862 5868 5870 5880 5882 5898 5904 5924 5928 5940 5954 5982 5988 6008 6012 6030 6038 6044 6048 6054 6068 6074
 6080 6090 6092 6102 6114 6122 6132 6134 6144 6152 6164 6174 6198 6200 6204 6212 6218 6222 6230 6248 6258 6264 6270 6272
 6278 6288 6300 6302 6312 6318 6324 6330 6338 6344 6354 6360 6362 6368 6374 6380 6390 6398 6422 6428 6450 6452 6470 6474
 6482 6492 6522 6530 6548 6552 6554 6564 6570 6572 6578 6582 6600 6608 6620 6638 6654 6660 6662 6674 6680 6690 6692 6702
 6704 6710 6720 6734 6738 6762 6764 6780 6782 6792 6794 6804 6824 6828 6830 6834 6842 6858 6864 6870 6872 6884 6900 6908
 6912 6918 6948 6950 6960 6962 6968 6972 6978 6984 6992 6998 7002 7014 7020 7028 7040 7044 7058 7070 7080 7104 7110 7122
 7128 7130 7152 7160 7178 7188 7194 7208 7212 7214 7220 7230 7238 7244 7248 7254 7284 7298 7308 7310 7322 7332 7334 7350
 7352 7370 7394 7412 7418 7434 7452 7458 7460 7478 7482 7488 7490 7500 7508 7518 7524 7530 7538 7542 7548 7550 7560 7562
 7574 7578 7584 7590 7592 7604 7608 7622 7640 7644 7650 7670 7674 7682 7688 7692 7700 7704 7718 7724 7728 7742 7754 7758
 7760 7790 7794 7818 7824 7830 7842 7854 7868 7874 7878 7880 7884 7902 7908 7920 7928 7934 7938 7950 7952 7964 7994 8010
 8012 8018 8040 8054 8060 8070 8082 8088 8090 8094 8102 8112 8118 8124 8148 8162 8168 8172 8180 8192 8210 8220 8222 8232
 8234 8238 8244 8264 8270 8274 8288 8292 8294 8298 8312 8318 8330 8354 8364 8370 8378 8388 8390 8420 8424 8430 8432 8444
 8448 8462 8468 8502 8514 8522 8528 8538 8540 8544 8564 8574 8582 8598 8600 8610 8624 8628 8630 8642 8648 8664 8670 8678
 8682 8690 8694 8700 8708 8714 8720 8732 8738 8742 8748 8754 8762 8780 8784 8804 8808 8820 8822 8832 8838 8840 8850 8862
 8864 8868 8888 8894 8924 8930 8934 8942 8952 8964 8970 8972 9000 9002 9008 9012 9014 9030 9042 9044 9050 9060 9068 9092
 9104 9110 9128 9134 9138 9152 9158 9162 9174 9182 9188 9200 9204 9210 9222 9228 9240 9242 9258 9278 9282 9284 9294 9312
 9320 9324 9338 9342 9344 9350 9372 9378 9392 9398 9404 9414 9420 9422 9432 9434 9438 9440 9462 9464 9468 9474 9480 9492
 9498 9512 9522 9534 9540 9548 9552 9588 9602 9614 9620 9624 9630 9632 9644 9650 9662 9678 9680 9690 9698 9720 9722 9734
 9740 9744 9750 9768 9770 9782 9788 9792 9804 9812 9818 9830 9834 9840 9852 9858 9860 9872 9884 9888 9902 9908 9924 9930
 9932 9942 9950 9968 9974 10008 10010 10038 10040 10062 10068 10070 10080 10092 10094 10100 10104 10112 10134 10140 10142 10152 10160 10164
 10170 10178 10182 10194 10212 10224 10244 10248 10254 10260 10268 10272 10274 10290 10302 10304 10314 10322 10332 10334 10338 10344 10358 10370
 10392 10400 10428 10430 10434 10454 10458 10460 10464 10478 10488 10500 10502 10514 10530 10532 10560 10568 10590 10598 10602 10608 10614 10628
 10632 10640 10652 10658 10664 10668 10688 10692 10710 10712 10724 10730 10734 10740 10754 10772 10782 10790 10800 10832 10838 10848 10854 10860
 10862 10868 10884 10890 10892 10904 10910 10938 10940 10950 10958 10974 10980 10988 10994 11004 11028 11048 11058 11060 11070 11072 11084 11088
 11094 11114 11118 11120 11132 11150 11160 11162 11172 11174 11178 11198 11214 11240 11244 11252 11258 11262 11274 11280 11288 11300 11312 11318
 11322 11330 11352 11354 11370 11384 11394 11400 11412 11424 11438 11444 11448 11468 11472 11484 11490 11492 11498 11504 11520 11528 11550 11552
 11580 11588 11594 11598 11618 11622 11634 11658 11678 11682 11690 11700 11702 11718 11720 11732 11744 11778 11780 11784 11790 11802 11808 11814
 11822 11828 11832 11834 11840 11864 11868 11888 11898 11904 11910 11924 11928 11934 11940 11942 11954 11960 11970 11972 11982 11988 12008 12012
 12038 12042 12044 12050 12072 12074 12098 12102 12108 12110 12114 12120 12144 12150 12158 12162 12164 12198 12204 12212 12228 12240 12242 12252
 12254 12264 12270 12278 12282 12290 12302 12324 12330 12344 12348 12374 12378 12380 12392 12402 12410 12414 12422 12434 12438 12452 12458 12474
 12480 12488 12492 12498 12504 12512 12518 12528 12540 12542 12548 12554 12570 12578 12584 12590 12602 12612 12614 12620 12638 12642 12648 12654
 12660 12672 12690 12698 12704 12714 12722 12740 12744 12758 12764 12782 12792 12800 12810 12822 12824 12830 12842 12854 12890 12894 12900 12908
 12912 12918 12920 12924 12942 12954 12960 12968 12974 12980 12984 13002 13004 13008 13010 13034 13038 13044 13050 13064 13094 13100 13104 13110
 13122 13128 13148 13152 13160 13164 13172 13178 13184 13188 13218 13220 13230 13242 13250 13260 13268 13292 13298 13310 13314 13328 13332 13338
 13340 13368 13382 13398 13400 13412 13418 13422 13442 13452 13458 13464 13470 13478 13488 13500 13514 13524 13538 13554 13568 13578 13592 13598
 13614 13620 13628 13634 13650 13670 13680 13682 13688 13692 13694 13698 13710 13712 13722 13724 13730 13752 13758 13760 13764 13782 13790 13800
 13808 13830 13832 13842 13860 13874 13878 13880 13884 13902 13904 13908 13914 13922 13932 13934 13964 13968 13998 14000 14010 14012 14030 14034
 14052 14058 14072 14082 14084 14088 14108 14144 14150 14154 14160 14174 14178 14198 14208 14222 14244 14250 14252 14282 14294 14304 14322 14324
 14328 14342 14348 14370 14388 14390 14402 14408 14412 14420 14424 14432 14438 14448 14450 14462 14480 14490 14504 14520 14534 14538 14544 14550
 14552 14558 14562 14564 14592 14594 14622 14628 14630 14634 14640 14654 14658 14670 14684 14700 14714 14718 14724 14732 14738 14742 14748 14754
 14760 14768 14772 14780 14784 14798 14814 14822 14828 14832 14844 14852 14868 14870 14880 14888 14892 14898 14924 14930 14940 14948 14952 14958
 14970 14984 15014 15018 15032 15054 15062 15074 15078 15084 15092 15102 15108 15122 15132 15138 15140 15150 15162 15174 15188 15194 15200 15218
 15228 15234 15242 15260 15264 15270 15272 15278 15288 15290 15300 15308 15314 15320 15330 15332 15350 15360 15362 15374 15378 15384 15392 15402
 15414 15428 15440 15444 15452 15462 15468 15474 15494 15498 15512 15528 15542 15552 15560 15570 15582 15584 15602 15608 15620 15630 15642 15644
 15648 15650 15662 15668 15672 15680 15684 15728 15732 15734 15738 15740 15750 15762 15768 15774 15788 15792 15798 15804 15810 15818 15824 15860
 15878 15882 15888 15890 15902 15908 15914 15920 15924 15938 15960 15972 15974 15992 16002 16008 16034 16058 16062 16064 16068 16070 16074 16088
 16092 16098 16104 16112 16128 16140 16142 16184 16188 16190 16194 16218 16224 16230 16232 16250 16253 16267 16273 16301 16319 16333 16339 16349
 16361 16363 16369 16381 16411 16417 16421 16427 16433 16447 16451 16453 16477 16481 16487 16493 16519 16529 16547 16553 16561 16567 16573 16603
 16607 16619 16631 16633 16649 16651 16657 16661 16673 16691 16693 16699 16703 16729 16741 16747 16759 16763 16787 16811 16823 16829 16831 16843
 16871 16879 16883 16889 16901 16903 16921 16927 16931 16937 16943 16963 16979 16981 16987 16993 17011 17021 17027 17029 17033 17041 17047 17053
 17077 17093 17099 17107 17117 17123 17137 17159 17167 17183 17189 17191 17203 17207 17209 17231 17239 17257 17291 17293 17299 17317 17321 17327
 17333 17341 17351 17359 17377 17383 17387 17389 17393 17401 17417 17419 17431 17443 17449 17467 17471 17477 17483 17489 17491 17497 17509 17519
 17539 17551 17569 17573 17579 17581 17597 17599 17609 17623 17627 17657 17659 17669 17681 17683 17707 17713 17729 17737 17747 17749 17761 17783
 17789 17791 17807 17827 17837 17839 17851 17863 17881 17891 17903 17909 17911 17921 17923 17929 17939 17957 17959 17971 17977 17981 17987 17989
 18013 18041 18043 18047 18049 18059 18061 18077 18089 18097 18119 18121 18127 18131 18133 18143 18149 18169 18181 18191 18199 18211 18217 18223
 18229 18233 18251 18253 18257 18269 18287 18289 18301 18307 18311 18313 18329 18341 18353 18367 18371 18379 18397 18401 18413 18427 18433 18439
 18443 18451 18457 18461 18481 18493 18503 18517 18521 18523 18539 18541 18553 18583 18587 18593 18617 18637 18661 18671 18679 18691 18701 18713
 18719 18731 18743 18749 18757 18773 18787 18793 18797 18803 18839 18859 18869 18899 18911 18913 18917 18919 18947 18959 18973 18979 19001 19009
 19013 19031 19037 19051 19069 19073 19079 19081 19087 19121 19139 19141 19157 19163 19181 19183 19207 19211 19213 19219 19231 19237 19249 19259
 19267 19273 19289 19301 19309 19319 19333 19373 19379 19381 19387 19391 19403 19417 19421 19423 19427 19429 19433 19441 19447 19457 19463 19469
 19471 19477 19483 19489 19501 19507 19531 19541 19543 19553 19559 19571 19577 19583 19597 19603 19609 19661 19681 19687 19697 19699 19709 19717
 19727 19739 19751 19753 19759 19763 19777 19793 19801 19813 19819 19841 19843 19853 19861 19867 19889 19891 19913 19919 19927 19937 19949 19961
 19963 19973 19979 19991 19993 19997 20011 20021 20023 20029 20047 20051 20063 20071 20089 20101 20107 20113 20117 20123 20129 20143 20147 20149
 20161 20173 20177 20183 20201 20219 20231 20233 20249 20261 20269 20287 20297 20323 20327 20333 20341 20347 20353 20357 20359 20369 20389 20393
 20399 20407 20411 20431 20441 20443 20477 20479 20483 20507 20509 20521 20533 20543 20549 20551 20563 20593 20599 20611 20627 20639 20641 20663
 20681 20693 20707 20717 20719 20731 20743 20747 20749 20753 20759 20771 20773 20789 20807 20809 20849 20857 20873 20879 20887 20897 20899 20903
 20921 20929 20939 20947 20959 20963 20981 20983 21001 21011 21013 21017 21019 21023 21031 21059 21061 21067 21089 21101 21107 21121 21139 21143
 21149 21157 21163 21169 21179 21187 21191 21193 21211 21221 21227 21247 21269 21277 21283 21313 21317 21319 21323 21341 21347 21377 21379 21383
 21391 21397 21401 21407 21419 21433 21467 21481 21487 21491 21493 21499 21503 21517 21521 21523 21529 21557 21559 21563 21569 21577 21587 21589
 21599 21601 21611 21613 21617 21647 21649 21661 21673 21683 21701 21713 21727 21737 21739 21751 21757 21767 21773 21787 21799 21803 21817 21821
 21839 21841 21851 21859 21863 21871 21881 21893 21911 21929 21937 21943 21961 21977 21991 21997 22003 22013 22027 22031 22037 22039 22051 22063
 22067 22073 22079 22091 22093 22109 22111 22123 22129 22133 22147 22153 22157 22159 22171 22189 22193 22229 22247 22259 22271 22273 22277 22279
 22283 22291 22303 22307 22343 22349 22367 22369 22381 22391 22397 22409 22433 22441 22447 22453 22469 22481 22483 22501 22511 22531 22541 22543
 22549 22567 22571 22573 22613 22619 22621 22637 22639 22643 22651 22669 22679 22691 22697 22699 22709 22717 22721 22727 22739 22741 22751 22769
 22777 22783 22787 22807 22811 22817 22853 22859 22861 22871 22877 22901 22907 22921 22937 22943 22961 22963 22973 22993 23003 23011 23017 23021
 23027 23029 23039 23041 23053 23057 23059 23063 23071 23081 23087 23099 23117 23131 23143 23159 23167 23173 23189 23197 23201 23203 23209 23227
 23251 23269 23279 23291 23293 23297 23311 23321 23327 23333 23339 23357 23369 23371 23399 23417 23431 23447 23459 23473 23497 23509 23531 23537
 23539 23549 23557 23561 23563 23567 23581 23593 23599 23603 23609 23623 23627 23629 23633 23663 23669 23671 23677 23687 23689 23719 23741 23743
 23747 23753 23761 23767 23773 23789 23801 23813 23819 23827 23831 23833 23857 23869 23873 23879 23887 23893 23899 23909 23911 23917 23929 23957
 23971 23977 23981 23993 24001 24007 24019 24023 24029 24043 24049 24061 24071 24077 24083 24091 24097 24103 24107 24109 24113 24121 24133 24137
 24151 24169 24179 24181 24197 24203 24223 24229 24239 24247 24251 24281 24317 24329 24337 24359 24371 24373 24379 24391 24407 24413 24419 24421
 24439 24443 24469 24473 24481 24499 24509 24517 24527 24533 24547 24551 24571 24593 24611 24623 24631 24659 24671 24677 24683 24691 24697 24709
 24733 24749 24763 24767 24781 24793 24799 24809 24821 24841 24847 24851 24859 24877 24889 24907 24917 24919 24923 24943 24953 24967 24971 24977
 24979 24989 25013 25031 25033 25037 25057 25073 25087 25097 25111 25117 25121 25127 25147 25153 25163 25169 25171 25183 25189 25219 25229 25237
 25243 25247 25253 25261 25301 25303 25307 25309 25321 25339 25343 25349 25357 25367 25373 25391 25409 25411 25423 25439 25447 25453 25457 25463
 25469 25471 25523 25537 25541 25561 25577 25579 25583 25589 25601 25603 25609 25621 25633 25639 25643 25657 25667 25673 25679 25693 25703 25717
 25733 25741 25747 25759 25763 25771 25793 25799 25801 25819 25841 25847 25849 25867 25873 25889 25903 25913 25919 25931 25933 25939 25943 25951
 25969 25981 25997 25999 26003 26017 26021 26029 26041 26053 26083 26099 26107 26111 26113 26119 26141 26153 26161 26171 26177 26183 26189 26203
 26209 26227 26237 26249 26251 26261 26263 26267 26293 26297 26309 26317 26321 26339 26347 26357 26371 26387 26393 26399 26407 26417 26423 26431
 26437 26449 26459 26479 26489 26497 26501 26513 26539 26557 26561 26573 26591 26597 26627 26633 26641 26647 26669 26681 26683 26687 26693 26699
 26701 26711 26713 26717 26723 26729 26731 26737 26759 26777 26783 26801 26813 26821 26833 26839 26849 26861 26863 26879 26881 26891 26893 26903
 26921 26927 26947 26951 26953 26959 26981 26987 26993 27011 27017 27031 27043 27059 27061 27067 27073 27077 27091 27103 27107 27109 27127 27143
 27179 27191 27197 27211 27239 27241 27253 27259 27271 27277 27281 27283 27299 27329 27337 27361 27367 27397 27407 27409 27427 27431 27437 27449
 27457 27479 27481 27487 27509 27527 27529 27539 27541 27551 27581 27583 27611 27617 27631 27647
Total bin space - items space: 39088168 - 39087868 = 300
Pack items in bins? true
Bin space after:    1  1  0  1  0  1  1  0  1  1  2  2  3  3  4  5  5  7  7  8  9  9 10 11 12 13 13 16 16 17 19 19 20 21 21 21
Run time millis:    14584
Wrong result true, expected false
END   sprax.questions.BinPack.test_packer(canPackSort1),  wrong 3,  FAIL
END   sprax.questions.BinPack.unit_test,  wrong 6,  FAIL
     *******************************************************************************/


