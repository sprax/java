package sprax.arrays;

import java.util.HashMap;
import java.util.Map;

import sprax.sprout.Sx;

public class FindPairs 
{
    //////////////////////////////////////// PAIRS //////////////////////////////////////////
    public static int[] firstPairSumToN(final int arr[], final int sum)
    {
        int range[] = {-1,-1};  // return values: invalid indices indicate failure
        Map<Integer, Integer> val2idx = new HashMap<Integer, Integer>();
        for (int j = 0; j < arr.length; j++) {
            // If there is a previously stored index i s.t. A[i] + A[j] == sum, return <i,j>
            if ( val2idx.containsKey(sum - arr[j]) ) {
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
    
    public static int test_pairs() 
    {
        //int iB[] = { 1, 2, 0, -2, 3, -4, 0 };
        int iB[] = { -1, 2, 1, 2, -3, 0, 4 };
        for (int sum = 0; sum < 10; sum += 3) {
            Sx.puts();
            int pair[] = firstPairSumToN(iB, sum);
            Sx.putsArray(iB, " => 1st pair to add up to " + sum + ": [" + pair[0] + ", " + pair[1] + "]");
        }
        return 0;
    }  
      
    

    
    public static int unit_test(int level) 
    {
    	String  testName = FindPairs.class.getName() + ".unit_test";  
    	Sx.puts(testName + " BEGIN");  
    	int stat = 0;
    	stat += test_pairs();

    	Sx.puts(testName + " END");  
    	return stat;
    }

    public static void main(String[] args)  { unit_test(2); }
}
