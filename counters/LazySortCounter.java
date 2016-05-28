package sprax.counters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sprax.Sx;

/**
 * Amortize the cost of tracking the top N most frequent strings by sorting a copy of some entries
 * in a string-to-count map only when it is both needed and not done already. Constructor takes
 * topNum, the maximum number of maximal string counts to keep for fast access.
 * 
 * Expected complexity for N adds, maximal count set size M, and Q queries (calls to get the max
 * list): O(1) to add a string and count not in the top M (1 HashMap put and one comparison). O(M)
 * to add a string with arbitrary count in the top M (same as above plus finding new minimum) O(M)
 * to add a string and count if every addition only increments the count by 1, but this could be
 * made O(1) if we do not use the strings as secondary sort keys, because then the new minimum
 * maximal count would just be the old one plus 1. O(M lg M) to get the current top M list once,
 * because the get will trigger a sort, but the sorted result is cached, so Q queries will be closer
 * to O(Q M lg M) * topCounts/totalCounts with topConts << totalCounts. TODO: To further reduce this
 * cost, consider storing the top M list in a TreeHashMap or TreeHashSet.
 * 
 * @author Sprax Lines
 */

public class LazySortCounter implements StringCounter
{
    final int                mMaximalCountSetSize;         // size of running maximal subset (use
                                                            // 10 for top ten)
    HashMap<String, Integer> mAllStringCounts;
    HashMap<String, Integer> mTopStringCounts;
    List<StringCount>        mCachedMaxStringCounts;
    boolean                  mIsCacheReadyToReturn;        // initially false
    int                      mTotalOfAllCounts;
    int                      mNumCached;
    int                      mMinCountInFullTopN;          // initially 0
    String                   mStringWithMinCountInFullTopN;
    
    LazySortCounter(int topNum)
    {
        assert (topNum > 0);
        mMaximalCountSetSize = topNum;
        mAllStringCounts = new HashMap<String, Integer>();
        mTopStringCounts = new HashMap<String, Integer>(topNum);
        mCachedMaxStringCounts = new ArrayList<StringCount>(topNum);
        for (int j = 0; j < topNum; j++)
        {
            mCachedMaxStringCounts.add(new StringCount("", 0));
        }
    }
    
    @Override
    public int uniqueStringCount()
    {
        return mAllStringCounts.size();
    }
    
    @Override
    public int totalOfAllCounts()
    {
        return mTotalOfAllCounts;
    }
    
    @Override
    public int maximalCountSetSize()
    {
        return mMaximalCountSetSize;
    }
    
    @Override
    public boolean add(String string)
    {
        return add(string, 1);
    }
    
    @Override
    public boolean add(String string, int increment)
    {
        assert (increment > 0);
        boolean isStringNew = false; // return value
        mTotalOfAllCounts += increment;
        Integer count = mAllStringCounts.get(string);
        if (count == null)
        {
            count = new Integer(increment);
            mAllStringCounts.put(string, count);
            isStringNew = true;
        }
        else
        {
            mAllStringCounts.put(string, count += increment);
        }
        
        if (count > mMinCountInFullTopN)
        {
            addToTheTopN(string, count);
        }
        return isStringNew;
    }
    
    private void addToTheTopN(String string, Integer count)
    {
        // Always add; hash map ensures uniqueness
        mTopStringCounts.put(string, count);
        mIsCacheReadyToReturn = false;
        
        // Only if the topN set is full might we need to discard
        // the least counted string and find the new min, etc.
        if (mTopStringCounts.size() >= mMaximalCountSetSize)
        {
            if (mTopStringCounts.size() > mMaximalCountSetSize)
            {
                // The string we just added was not already in the top N,
                // so remove the previous min-counted and find the new one.
                mTopStringCounts.remove(mStringWithMinCountInFullTopN);
                assert (mTopStringCounts.size() == mMaximalCountSetSize);
            }
            
            // Find new min-counted; initialize to the one just added; it is likely
            // to be near the smallest.
            mStringWithMinCountInFullTopN = string;
            mMinCountInFullTopN = count;
            for (Map.Entry<String, Integer> entry : mTopStringCounts.entrySet())
            {
                if (mMinCountInFullTopN > entry.getValue())
                {
                    mMinCountInFullTopN = entry.getValue();
                    mStringWithMinCountInFullTopN = entry.getKey();
                }
            }
        }
    }
    
    @Override
    public final List<StringCount> descendingMaximalStringCounts()
    {
        if (!mIsCacheReadyToReturn)
        {
            int idx = 0;
            for (Map.Entry<String, Integer> entry : mTopStringCounts.entrySet())
            {
                StringCount sc = mCachedMaxStringCounts.get(idx++);
                sc.mString = entry.getKey();
                sc.mCount = entry.getValue();
            }
            mCachedMaxStringCounts.sort(StringCount.sDescendingStringCountsComp);
            mIsCacheReadyToReturn = true;
        }
        // return a read-only view of the cached list:
        return Collections.unmodifiableList(mCachedMaxStringCounts);
    }
    
    public static void unit_test()
    {
        Sx.puts(LazySortCounter.class.getName() + ".unit_test");
        StringCounter counter = new LazySortCounter(7);
        StringCounterTest.test_oneCounter(counter);
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
