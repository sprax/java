package sprax.counters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import sprax.Sx;

/**
 * Problem: Given a growing large number N of strings and counts of how many times
 * each one is seen, quickly return the M most frequently seen strings along with
 * their counts.
 *
 * Solution:
 * Use a hash map to store the growing number N of unique strings with their counts.
 * Maintain a priority queue (min heap) of the M strings with the biggest counts
 * (using a dynamic threshold for inclusion -- the minimal count among the current top M).
 * Use auxiliary data structures for caching results.
 * M is fixed: the constructor takes topNum, the maximum number of maximal string counts
 * to keep for fast access.
 * 
 * BUG: This resolves ties for last place entry on a first-come first-served basis.
 * 
 * Expected complexity for N adds, maximal count set size M, and Q queries (calls to get the max list):
 * O(1) to add a string and count not in the top M (1 HashMap put and one comparison).
 * O(lg M) to add a string with arbitrary count in the top M (same as above plus heap insertion)
 * O(lg M) to add a string and count if every addition only increments the count by 1, because the 
 * 		heap insertion will not necessarily terminate at level 2 or 3.
 * O(M lg M) to get the current top M list once, because the get will effectively trigger a heap sort.
 * 		But the sorted result is cached, so Q queries will be closer to O(Q M lg M) * topCounts/totalCounts
 * 		with topConts << totalCounts. 
 *
 * @author Sprax Lines
 */
public class PriorityQueueCounter implements StringCounter
{
	final int mMaximalCountSetSize;		// size of running maximal subset (use 10 for top ten)
	PriorityQueue<String> mQueue;
	HashMap<String, Integer> mAllStringCounts;
	AscendingMappedStringCountsCompCountsOnly mAscendingCountsComp;
	DescendingStringCountsComp mDescendingCountsComp;
	ArrayList<StringCount> mCachedMaxStringCounts;
	int mUniqueStringCount;
	int mTotalCount;
	int mLeastCountInFullCache;		// initially 0
	boolean mIsCacheValid;

	PriorityQueueCounter(int topNum)
	{
		assert(topNum > 0);
		mMaximalCountSetSize = topNum;
		mAllStringCounts = new HashMap<String, Integer>();
		mAscendingCountsComp = new AscendingMappedStringCountsCompCountsOnly(mAllStringCounts);
		mDescendingCountsComp = new DescendingStringCountsComp();
		mQueue = new PriorityQueue<String>(topNum, mAscendingCountsComp);
		mCachedMaxStringCounts = new ArrayList<StringCount>(mMaximalCountSetSize);
		for (int j = 0; j < mMaximalCountSetSize; j++)
		{
			mCachedMaxStringCounts.add(new StringCount("", 0));
		}	
	}

	@Override
	public int uniqueStringCount() {
		return mUniqueStringCount;
	}

	@Override
	public int totalOfAllCounts() {
		return mTotalCount;
	}

	@Override
	public int  maximalCountSetSize() {
		return mMaximalCountSetSize;
	}
		
	@Override
	public boolean add(String string) {
		return add(string, 1);
	}

	@Override
	public boolean add(String string, int increment)
	{
		assert(increment > 0);
		boolean isStringNew = false;		// return val
		mTotalCount += increment;
		Integer count = mAllStringCounts.get(string);
		if (count == null)
		{
			count = new Integer(increment);
			mAllStringCounts.put(string, count);
			isStringNew = true;
			mUniqueStringCount++;
		}
		else
		{
			mAllStringCounts.put(string, count += increment);
		}
		
		// If the queue already contains this string, or is not full, 
		// remove this string and add it back
		// If the queue is not full, just add this;
		// otherwise, add only if its count exceeds the minimum
		// of the maximal counts.
		
		// If this string's count is greater than the least counted string in
		// the queue, (re)add it.  This condition covers the case of  this string
		// was already being in the queue, because its new count must exceed
		// its old one.  So no need to test explicitly if the queue already 
		// holds this string; trying to remove it does the same search anyway.
		if (count > mLeastCountInFullCache)
		{
			mIsCacheValid = false;
			
			// If the queue is already full, we must remove an item before adding a new one.
			// if (mUniqueStringCount > mMaximalCountSetSize)
			if (mQueue.size() >= mMaximalCountSetSize)
			{
				// If the queue already contains this string, then this string's count is
				// already in the top N.  Re-order the queue by removing this string and 
				// re-adding it.  
				// Otherwise, remove the least counted element and add this one.
				// Either way, the queue size does not change.
				if ( ! mQueue.remove(string)) 
				{
					mQueue.remove();		// Remove the least counted string.
				}
				mQueue.add(string);			// (Re)add the string.
				mLeastCountInFullCache = mAllStringCounts.get(mQueue.peek());
			}
			else
			{
				mQueue.remove(string);		// Remove it first to prevent duplicates
				mQueue.add(string);			// Add the new string.
				if (mQueue.size() >= mMaximalCountSetSize)
				{
					mLeastCountInFullCache = mAllStringCounts.get(mQueue.peek());
				}
			}
		}
		// Sx.puts("Added " + string + ", and mQueue.size = " + mQueue.size());
		return isStringNew;
	}


	@Override
	public List<StringCount> descendingMaximalStringCounts() 
	{
		if ( ! mIsCacheValid)
		{
			// Make a copy of the queue and treat it as a min heap
			PriorityQueue<String> minHeap = new PriorityQueue<String>(mQueue);
			for (int index = minHeap.size(); --index >= 0; )
			{
				// Remove the min item, and put it in its right place in the return list
				String string = minHeap.remove();
				StringCount sc = mCachedMaxStringCounts.get(index);
				sc.mString = string;
				sc.mCount = mAllStringCounts.get(string);
				mCachedMaxStringCounts.set(index, sc);
			}
			mIsCacheValid = true;
		}
		
		// return an unmodifiable list view of the cached list:
		return Collections.unmodifiableList(mCachedMaxStringCounts);
	}

	public static void unit_test()
	{
		Sx.puts(PriorityQueueCounter.class.getName() + ".unit_test");
		StringCounter counter = new PriorityQueueCounter(7);
		StringCounterTest.test_oneCounter(counter);
	}

	public static void main(String[] args) {  unit_test();  }
}