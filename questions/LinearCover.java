/*
https://www.careercup.com/question?id=5760148355153920 * Median
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
 * You are given a range [first, last], initially white. You need to paint it black. 
 * For this purpose you have a set of triples [(f, l, cost), ...] - where each triple
 * means that you can paint range [f, l] for `cost` coins (limitations: 
 * cost is floating point >= 0, f, l, first, last are integers). 
 * Find minimum cost needed to paint the whole range [first, last] or return -1 if it's impossible 
 * Example:
 * <pre>
 * [first, last] = [0, 5] and set of triples is
 * [[0, 5, 10], [0, 4, 1], [0, 2, 5], [2, 5, 1]]
 * 
 * Clearly the answer is to take [0, 4, 1] and [2, 5, 1] - the total cost will be 2. 
 * 
 * Another example:
 * 
 * [first, last] = [0, 5]
 * triples are [[1, 4, 10], [2, 5, 6]]
 * answer is -1, because it's impossible to color whole range.
 * 
 * Possible Google interview or Code Jam question:
 * https://www.careercup.com/question?id=5760148355153920
 * </pre>
 */
public class LinearCover
{
    static class Segment 
    {
        public final int first, last;
        public final float cost;
        public Segment(int first, int last, float cost) {
            this.first = first;
            this.last = last;
            this.cost = cost;
        }
    }
    
    static class CompareLastFirstCost implements Comparator<Segment>
    {
        @Override
        public int compare(Segment segA, Segment segB) {
            return compareSegments(segA, segB);
        }
        
        public static int compareSegments(Segment segA, Segment segB) {
            int diff = segA.last - segB.last;
            if (diff != 0)
                return diff;
            diff = segA.first - segB.first;
            if (diff != 0)
                return diff;
            return Float.compare(segA.cost, segB.cost);
        }
    }
    
    
    public static float costToCoverOrMinusOne(int beg, int end, Segment segments[])
    {
        float totalCost = costToCoverOrMaxVal(beg, end, segments); 
        if (totalCost == Float.MAX_VALUE)
            totalCost = -1.0F;
        return totalCost;
    }
    
    /**
     * Algorithm: Sort the covering segments, primarily by their last points, then
     * map each segment end points to the minimum total cost to get there so far.
     * Complexity: O(NlogN) for the sorting, then O(N * M) for the mapping, where M
     * is the mean number of segments overlapping each other, for a worst-case of O(N^2).
     * 
     * TODO: Heuristically, it might seem better (if more greedy) to iterate 
     * over the main map in key-descending order, as in longest-reach-first, but
     * looking at the geometry, it's really the same in either order.  It is possible
     * to iterate the sub-map in descending order (just obtain it using 
     * minCostMap.descendingMap().subMap(...)), but that definitely does not help
     * in the general or random case.  The only ordering that would help there would 
     * be to iterate over the qualifying segments in ascending order of cost, so that
     * the first cost found is always the minimum cost, but that would require another
     * sort.  The keys have to be sorted to obtain the subMap. 
     * 
     * @param beg   Beginning of the main segment to cover
     * @param end   End (inclusive) of the segment to cover
     * @param segments  Array of segments to use for covering the main one
     * @return
     */
    public static float costToCoverOrMaxVal(int beg, int end, Segment segments[])
    {
        float totalCost = Float.MAX_VALUE;
        TreeMap<Integer, Float> minCostMap = new TreeMap<>();
        minCostMap.put(beg, 0.0F);           // initialize: getting to the beginning is free
        
        // Sort by last, then first, then cost:
        Arrays.sort(segments, CompareLastFirstCost::compareSegments);
        
        for (Segment seg : segments) {
            float minCost = minCostMap.getOrDefault(seg.last, Float.MAX_VALUE);
            SortedMap<Integer, Float> sub = minCostMap.subMap(seg.first, seg.last);
            for (Map.Entry<Integer, Float> entry : sub.entrySet()) {
                float newCost = entry.getValue() + seg.cost;
                if (minCost > newCost) {
                    minCost = newCost;
                }
            }
            if (minCost < Float.MAX_VALUE) {
                minCostMap.put(seg.last, minCost);
            }
        }
        
        NavigableMap<Integer, Float> tail = minCostMap.tailMap(end, true);
        for (float cost : tail.values())
            if (totalCost > cost)
                totalCost = cost;
        return totalCost;
    }
    
    public static int unit_test(int level)
    {
        String testName = LinearCover.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Segment segs[] = { 
                new Segment(0, 5, 10F), 
                new Segment(0, 4,  1F), 
                new Segment(0, 2,  5F), 
                new Segment(2, 5,  1F),
        };
        int beg = 0, end = 5;
        float cost = costToCoverOrMinusOne(beg, end, segs);
        numWrong += Sz.oneIfDiff(cost, 2.0F);
        
        segs[1] = new Segment(-2, 7, 7F);
        cost = costToCoverOrMinusOne(beg, end, segs);
        numWrong += Sz.oneIfDiff(cost, 6.0F);

        segs[1] = new Segment(-2,  4, 7F);
        segs[2] = new Segment( 5, 11, 3F);
        segs[3] = new Segment( 3,  5, 2F);
        beg = -1;
        end =  9;
        cost = costToCoverOrMinusOne(beg, end, segs);
        numWrong += Sz.oneIfDiff(cost, 12.0F);
         
        Segment segsNoCover[] = {
                new Segment(1, 4, 10F), 
                new Segment(2, 5,  6F),
        };
        cost = costToCoverOrMinusOne(beg, end, segsNoCover);
        numWrong += Sz.oneIfDiff(cost, -1.0F);

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) 
    {
        unit_test(2);
    }

}


