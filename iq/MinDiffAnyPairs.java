/*
 */

package sprax.questions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import sprax.test.Sz;

/**
 * 
 * <pre>

 * </pre>
 */
public class MinDiffAnyPairs
{
    static class DiffPair implements Comparable<DiffPair>
    {
        public final int xx, yy, absDiff;
        
        public DiffPair(int xx, int yy) {
            this.xx = xx;
            this.yy = yy;
            this.absDiff = Math.abs(xx - yy);
        }

		@Override
		public int compareTo(DiffPair o) {
			if (absDiff == o.absDiff)
				return Integer.compare(xx,  o.xx);
			else
				return Integer.compare(absDiff, o.absDiff);
		}
    }
    
    static class CompDiffPair implements Comparator<DiffPair>
    {
		@Override
		public int compare(DiffPair arg0, DiffPair arg1) {
			return arg0.compareTo(arg1);
		}	
    }
    
    public static int[] scanIntArray() 
    {    
        try (Scanner in = new Scanner(System.in)) {
            
            int numEntries = in.nextInt();
            int arr[] = new int[numEntries];
            for (int j = 0; j < numEntries; j++) {
            	arr[j] = in.nextInt();
            }
            return arr;
        } catch(Throwable ex)
        {
        	System.out.println("scanIntArray returning empty array; caught:\n" + ex);
        	return new int[0];
        }
    }


    public static int showMinDiffAnyPairs(final int array[])
    {
    	Arrays.sort(array);
    	ArrayList<DiffPair> dpList = new ArrayList<>();
    	int minDiff = Integer.MAX_VALUE;
    	for (int j = 1; j < array.length; j++) {
    		int diff = array[j] - array[j-1];
    		if (minDiff < diff)
    			continue;
    		if (minDiff > diff) {
    			minDiff = diff;
    			dpList.clear();
    		}
    		DiffPair dp = new DiffPair(array[j-1], array[j]);
    		dpList.add(dp);
    	}

    	for (DiffPair dp : dpList)
    		System.out.format("%d %d ", dp.xx, dp.yy);
    	System.out.println();
    	return dpList.size();
    }

    
    public static int unit_test(int level)
    {
        String testName = MinDiffAnyPairs.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int array[] = scanIntArray();
        showMinDiffAnyPairs(array);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(2);
    }

}


