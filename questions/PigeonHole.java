package sprax.questions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
     * 
     * @param array
     * @return array of indices of the subset of values whose sum is a multiple of N,
     * where N is the length of the input array.
     */
    public static int[] subsetWhoseSumIsMultipleOfArrayLen(int array[])
    {
        int N = array.length;
        int sumsOfFirstN[] = new int[N];
        int mod, sum = 0;
        for (int j = 0; j < N; j++) {
            mod = array[j] % N;
            if (mod == 0) {
                return new int[] {j};
            }
            sumsOfFirstN[j] = sum + mod;
        }
        
        
        return null;
    }
    
    
    public static int unit_test()
    {
        String testName = PigeonHole.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        

        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        //useArgsOrDefaults(args);
        unit_test();
    }
    
}
