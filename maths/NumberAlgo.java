package sprax.maths;

import java.util.HashMap;
import java.util.Map;

import sprax.Sx;

public class NumberAlgo 
{
    
    public static int[] firstPairSumToN(int arr[], int sum)
    {
        int range[] = {-1,-1};  // return values: invalid indices indicate failure
        Map<Integer, Integer> val2idx = new HashMap<Integer, Integer>();
        for (int j = 0; j < arr.length; j++) {
            // If there is a previously stored index i s.t. A[i] + A[j] == sum, return <i,j>
            if (val2idx.containsKey(sum - arr[j])) {
                range[0] = val2idx.get(sum - arr[j]);
                range[1] = j;
                break;
            }
            // Store only the first occurrence of each array value
            if ( ! val2idx.containsKey(arr[j]))
                val2idx.put(arr[j], j);
        }
        return range;
    }
    
    public static int[] firstContiguousSumToN(int arr[], int sum)
    {
        int range[] = {-1,-1};  // return values
        Map<Integer, Integer> sums = new HashMap<Integer, Integer>();
        for (int q = 0, j = 0; j < arr.length; j++) {
            // Special case: if A[j] == sum, return <j,j>
            int Aj = arr[j];
            if (Aj == sum) {
                range[0] = range[1] = j;
                break;
            }
            if (sums.containsKey(sum - Aj)) {
                range[0] = sums.get( sum - Aj);
                range[1] = j;
                break;
            }
            q += arr[j];         // partial sum
            sums.put(q, j);
        }
        return range;
    }
    
    
    
    public static int unit_test()
    {
        String testName = NumberAlgo.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");
        Sx.puts(NumberAlgo.class.getName() + ".unit_test");
        
        //    int stat = test_contiguous();
        
        //    long begTime, endTime, difTime;
        //    begTime = System.currentTimeMillis();
        //    for (int j = 0; j < Integer.MAX_VALUE; j++)
        //      ;
        //    endTime = System.currentTimeMillis();
        //    difTime = endTime - begTime;
        //    S.puts("MAX INT (" + Integer.MAX_VALUE + ") loop time: " + difTime);
        
        Sx.puts(testName + " END");
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test();
        DoubleToFraction.unit_test();
        for (int j = 0; j < 48; j++) {
            Sx.format("%3d  %5d  %.9f\n", j, j*j, Math.exp(-j*j/(2F*55)));
        }
    }
}


/*
 * 

given a number say 312. find the next number using the same digits. 321.
2
[Full Interview Report]
Country: India
Interview Type: In-Person
Tags: Amazon » Algorithm  » Software Engineer / Developer
Question #11274979 (Report Dup) | Edit | History


0
of 0 vote
Anonymous on October 20, 2011 |Edit | Edit

this is about generating permutations in lexicographical order:

use std::next_permutation() for that ))
msramachandran on October 20, 2011 |Edit | Edit

I solved it in a different way. If I remember correctly the space complexity blows
up if we try to compute the permutations.

 * The number is actually provided as an array of chars.
 * Take an array of length 10. to hash the rightmost occurrence of each digit (0-9).
 * Process the array from right to left.
 * If you encounter a digit for which there is a higher digit to its right, 
 * Find the rightmost occurrence of it (from the hash table) and swap the elements.
 * Sort the numbers to the right of the element you just swapped.
 */
