package sprax.bits;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Programming Pearls opening example by Jon Bentley:
 * Sort up to 27,000 unique numbers between 0 and 27000 exclusive.
 * Hint: don't sort anything, just use a bitfield.
 */
public class BitSort 
{
    public final int size;
    public final BitSet sortedNumbers;
    
    BitSort(int[] numbers)
    {
        assert(numbers != null);        // GIGO: checking not much.
        size = numbers.length;
        sortedNumbers = new BitSet(size);
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
        while (idx < end) {
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
            array[j] = beg + idx;
        }
        return array;
    }
    
     
    public static int unit_test() 
    {
        String testName =  BitSort.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int testArray[] = { 1, 4, 7, 11, 17, 18, 20, 29, 32 };
        Sx.putsArray("testArray:", testArray);
        int beg = 0, end = 30;
        BitSort bs = new BitSort(testArray);
        List<Integer> list = bs.toList(beg, end);
        int array[] = bs.toArray(beg, end);
        Sx.putsList("toList:     ", list, 0);
        Sx.putsArray("toArray:  ", array);
            
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
