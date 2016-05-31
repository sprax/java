package sprax.counters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import sprax.Sx;
import sprax.heaps.MinHeap;


/** 
 * Maintain a priority queue (min heap) of N strings with maximal counts
 * and auxiliary data structures for adding unique strings, incrementing
 * their counts, and quickly returning the top N strings with their counts.
 * Constructor takes topNum, the maximum number of maximal string counts
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
public class MinHeapCounter implements StringCounter
{
	final int mMaximalCountSetSize;		// size of running maximal subset (use 10 for top ten)
	MinHeap<String> mMinHeap;
	HashMap<String, Integer> mAllStringCounts;
	AscendingMappedStringCountsCompCountsOnly mAscendingCountsComp;
	DescendingStringCountsComp mDescendingCountsComp;
	ArrayList<StringCount> mCachedMaxStringCounts;
	int mUniqueStringCount;
	int mTotalCount;
	int mLeastCountInFullCache;		// initially 0
	boolean mIsCacheValid;

	MinHeapCounter(int topNum)
	{
		assert(topNum > 0);
		mMaximalCountSetSize = topNum;
		mAllStringCounts = new HashMap<String, Integer>();
		mAscendingCountsComp = new AscendingMappedStringCountsCompCountsOnly(mAllStringCounts);
		mDescendingCountsComp = new DescendingStringCountsComp();
		String[] stringArray = new String[topNum];
		mMinHeap = new MinHeap<String>(stringArray, 0, false, mAscendingCountsComp);
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
			if (mMinHeap.size() >= mMaximalCountSetSize)
			{
				// If the queue already contains this string, then this string's count is
				// already in the top N.  Re-order the queue by removing this string and 
				// re-adding it.  
				// Otherwise, remove the least counted element and add this one.
				// Either way, the queue size does not change.
				
				/////if ( ! mMinHeap.remove(string)) 
				////{
				////	mMinHeap.remove();		// Remove the least counted string.
				////}
				mMinHeap.add(string);			// (Re)add the string.
				mLeastCountInFullCache = mAllStringCounts.get(mMinHeap.peek());
			}
			else
			{
				mMinHeap.remove(string);		// Remove it first to prevent duplicates
				mMinHeap.add(string);			// Add the new string.
				if (mMinHeap.size() >= mMaximalCountSetSize)
				{
					mLeastCountInFullCache = mAllStringCounts.get(mMinHeap.peek());
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
			MinHeap<String> minHeap = new MinHeap<String>(mMinHeap);
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
		
		// return an unmodifiable list view of the  cached list:
		return Collections.unmodifiableList(mCachedMaxStringCounts);
	}

	public static void unit_test()
	{
		Sx.puts(MinHeapCounter.class.getName() + ".unit_test");
		StringCounter counter = new MinHeapCounter(7);
		StringCounterTest.test_oneCounter(counter);
	}


	public static void main(String[] args) {  unit_test();  }
}