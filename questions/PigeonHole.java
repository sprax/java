package sprax.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import sprax.files.FileUtil;
import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Given an array (or list) of N integers, 
 * (1) find a non-empty subset whose sum is a multiple of N. 
 * (2) find a non-empty subset whose sum is a multiple of 2N. 
 * Compare the solutions of the two questions
 */
public class PigeonHole
{
    /** N natural numbers (integers > 0) */
    int numbers[];
    
    PigeonHole(int numbers[])
    {
        if (numbers == null || numbers.length < 1)
            throw new IllegalArgumentException("bad numbers");
        
        this.numbers = numbers;
    }

    /**
     * @param array
     * @return array of indices of the subset of values whose sum is a multiple of N,
     * where N is the length of the input array.
     * NIECE: No Input Error Checking/Exceptions.
     */
    public static int[] subsetWhoseSumIsMultipleOfArrayLen(int array[])
    {
        
        return subsetWhoseSumIsMultipleOfArrayLen(array, array.length);
    }

    /**
     * @param array
     * @return array of indices of the subset of values whose sum is a multiple of N,
     * where N may or may not be the length of the input array.
     * NIECE: No Input Error Checking/Exceptions.
     */
    public static int[] subsetWhoseSumIsMultipleOfArrayLen(int array[], int N)
    {
        int partialSumModN[] = new int[N];
        HashMap<Integer, Integer> diffMap = new HashMap<>();
        int mod, sum = 0;
        for (int j = 0; j < N; j++) {
            mod = array[j] % N;
            if (mod == 0) {
                return new int[] {j};
            }
            else if (diffMap.containsKey(mod)) {
                int firstSumIndex = diffMap.get(mod);
                int subset[] = new int[firstSumIndex + 2];
                for (int k = 0; k <= firstSumIndex; k++) {
                    subset[k] = k;
                }
                subset[firstSumIndex + 1] = j;
                return subset;
            }
            else {
                sum = (sum + mod) % N;            
                partialSumModN[j] = sum;
                diffMap.put(N - sum, j);
            }
        }
        return new int[] {};
    }
    
    
    public static int test_subsetWhoseSumIsMultipleOfArrayLen(int tst[], int N, boolean expected)
    {
        int sub[] = subsetWhoseSumIsMultipleOfArrayLen(tst, N);
        Sx.putsArray("Testing:  ", tst, "  to find a subset whose sum is a multiple of " + N);
        Sx.putsArray("SubIndex: ", sub);
        int sum = Sx.putsIndexedArray("SubArray: ", tst, sub);
        int mod = sum % N;
        Sx.format("Sum & Mod: %d %% %d = %d\n", sum, N, mod);
        boolean result = (sub.length > 0);
        return Sz.oneIfDiff(result, expected);
    }
    
    public static int unit_test()
    {
        String testName = PigeonHole.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int tst[] = { 2, 1, 3, 37 };
        int N = tst.length;
        numWrong += test_subsetWhoseSumIsMultipleOfArrayLen(tst, N, true);

        N = N * 5;
        numWrong += test_subsetWhoseSumIsMultipleOfArrayLen(tst, N, true);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        //useArgsOrDefaults(args);
        unit_test();
    }
    
}
