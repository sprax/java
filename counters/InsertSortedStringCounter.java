package sprax.counters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sprax.Sx;
import sprax.Sz;

/**
 * Keeps all string counts in a hash map for quick access, and keeps a short list of the maximal
 * ones sorted using insertion (that is, comparisons and shifting of indices when adding). Expected
 * cost of adding a string or incrementing its count is asymptotically constant, or O(shortListSize)
 * in worst case scenarios.
 * 
 * Expected complexity for N adds, maximal count set size M, and Q queries (calls to get the max
 * list): O(1) to add a string and count not in the top M (1 HashMap put and one comparison). O(M)
 * to add a string with arbitrary count in the top M (same as above plus an insertion) O(1) to add a
 * string and count if every addition only increments the count by 1, because the insertion will
 * always take place at the tail of the list, not somewhere in the middle. O(1) to get the current
 * top M list, so Q queries is O(Q), no amortization necessary.
 * 
 * @author Sprax
 */
public class InsertSortedStringCounter implements StringCounter
{
    final int                                 mMaximalCountSetSize;  // size of running maximal
                                                                      // subset (use 10 for top ten)
    Map<String, Integer>                      mAllStringCounts;
    AscendingMappedStringCountsCompCountsOnly mAscendingCountsComp;
    DescendingStringCountsComp                mDescendingCountsComp;
    ArrayList<StringCount>                    mCachedMaxStringCounts;
    int                                       mTotalOfAllCounts;
    int                                       mLeastCountInFullCache;
    int                                       mNumCached;            // initially 0
                                                                      
    InsertSortedStringCounter(int topNum)
    {
        mMaximalCountSetSize = topNum;
        mAllStringCounts = new HashMap<String, Integer>();
        mAscendingCountsComp = new AscendingMappedStringCountsCompCountsOnly(mAllStringCounts);
        mDescendingCountsComp = new DescendingStringCountsComp();
        mCachedMaxStringCounts = new ArrayList<StringCount>(mMaximalCountSetSize);
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
            mAllStringCounts.put(string, increment);
            isStringNew = true;
        }
        else
        {
            mAllStringCounts.put(string, count += increment);
        }
        
        // Sx.puts("Map after adding " + string + " ++" + increment + ":");
        // for (Map.Entry<String, Integer> elt : mStringCounts.entrySet())
        // {
        // Sx.puts("	" + elt.getKey() + "|" + elt.getValue());
        // }
        
        if (count > mLeastCountInFullCache)
        {
            addToSortedMaxList(string, count);
        }
        return isStringNew;
    }
    
    private void addToSortedMaxList(String string, Integer count)
    {
        // Algo: if string is already in the cache:
        // increment its count, update its position if needed, and return;
        // otherwise, find the right position, add it there, and update threshold.
        int j = mNumCached;
        while (--j >= 0)
        {
            StringCount scj = mCachedMaxStringCounts.get(j);
            if (scj.mString.equals(string))
            {
                // This string is already in the cache; update its count and position.
                assert (scj.mCount < count);
                scj.mCount = count;
                while (--j >= 0)
                {
                    StringCount tmp = mCachedMaxStringCounts.get(j);
                    // if (tmp.mCount < count || (tmp.mCount == count &&
                    // tmp.mString.compareTo(string) < 0)) {
                    if (StringCount.compare(tmp.mString, tmp.mCount, string, count) < 0) {
                        mCachedMaxStringCounts.set(j + 1, tmp);
                    }
                    else
                    {
                        break;
                    }
                }
                mCachedMaxStringCounts.set(j + 1, scj); // This could be where it already was.
                return;
            }
            if (StringCount.compare(scj.mString, scj.mCount, string, count) < 0)
            {
                continue;
            }
            else
            {
                break;
            }
        }
        // Now we know we need to put a new StringCount at position j+1
        // and if the cache was already full, we need to discard the least entry.
        mCachedMaxStringCounts.add(j + 1, new StringCount(string, count));
        if (mNumCached < mMaximalCountSetSize)
        {
            // Increment cache size, and if this is the first time it is full,
            // update the maximal count threshold.
            if (++mNumCached == mMaximalCountSetSize)
            {
                mLeastCountInFullCache = mCachedMaxStringCounts.get(mNumCached - 1).mCount;
            }
        }
        else
        {
            // Final cache size will still be the max size; we added one, and now remove one.
            mCachedMaxStringCounts.remove(mMaximalCountSetSize);
            mLeastCountInFullCache = mCachedMaxStringCounts.get(mMaximalCountSetSize - 1).mCount;
        }
    }
    
    @Override
    public ArrayList<StringCount> descendingMaximalStringCounts()
    {
        // return a copy of the cached list:
        return new ArrayList<StringCount>(mCachedMaxStringCounts);
    }
    
    public static int unit_test()
    {
        String testName = InsertSortedStringCounter.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        StringCounter counter = new InsertSortedStringCounter(7);
        StringCounterTest.test_oneCounter(counter);
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
