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
public class BitSort 
{
    public final int rangeSize;
    public final BitSet sortedNumbers;
    
    BitSort(int[] numbers, int rangeSize)
    {
        assert(numbers != null);        // GIGO: checking not much.
        this.rangeSize = rangeSize;
        sortedNumbers = new BitSet(rangeSize);
        sortInput(numbers);
    }
    
    public void sortInput(int[] numbers)
    {
        for (int nn : numbers)
            sortedNumbers.set(nn);
    }
    
    public boolean contains(int nn)
    {
        return sortedNumbers.get(nn);
    }
    
    public List<Integer> toList(int beg, int end)
    {
        List<Integer> list = new ArrayList<>();
        int idx = sortedNumbers.nextSetBit(beg);
        while (0 <= idx && idx < end) {
            list.add(idx);
            idx = sortedNumbers.nextSetBit(idx + 1);
        }
        return list;
    }
    
    public int[] toArray(int beg, int end)
    {
        BitSet range = sortedNumbers.get(beg, end);
        int size = range.cardinality();
        int array[] = new int[size];
        for (int idx = 0, j = 0; j < size; j++, idx++) {
            idx = sortedNumbers.nextSetBit(idx);
            if (idx == -1)
                break;
            array[j] = beg + idx;
        }
        return array;
    }
    
     
    public static int unit_test() 
    {
        String testName =  BitSort.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int origArray[] = { 1, 4, 7, 11, 17, 18, 20, 29, 32 };
        Sx.putsArray("origArray:", origArray);
        List<Integer> testList = Arrays.stream(origArray).boxed().collect(Collectors.toList());
        Collections.shuffle(testList);
        int testArray[] = testList.stream().mapToInt(i->i).toArray();

        int rangeSize = 40;
        Sx.putsArray("testArray:", testArray);
        int beg = 0, end = rangeSize;
        BitSort bs = new BitSort(testArray, rangeSize);
        List<Integer> list = bs.toList(beg, end);
        int array[] = bs.toArray(beg, end);
        Sx.putsList("toList:     ", list, 0);
        Sx.putsArray("toArray:  ", array);
        
        numWrong += Sz.compareListAndArray(list, array);
        numWrong += ArrayDiffs.absDiff(origArray, array);
            
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
