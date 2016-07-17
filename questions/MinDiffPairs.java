/*
 * https://www.careercup.com/question?id=5760148355153920 * Median
 * 
 * Copyright (c) 2001, 2002, 2003 Marco Schmidt.
 * All rights reserved.
 */

package sprax.questions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import sprax.test.Sz;

/**
 * 
 * <pre>

 * </pre>
 */
public class MinDiffPairs
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
    
 
    public static int unit_test(int level)
    {
        String testName = MinDiffPairs.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(2);
    }

}


