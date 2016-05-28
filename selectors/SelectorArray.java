package sprax.selectors;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import sprax.Sx;

public class SelectorArray implements Selector
{
    private final boolean   mArray[];
    final int               mMin;
    final int               mMax;
    final int               mLen;
    private Integer         mCurrentIndex;
    private boolean         mIsAllSelected;                 // private for lazy evaluation
    private boolean         mIsNoneSelected = true;
    
    SelectorArray(int min, int max)
    {
        if (max < min)
            throw new IllegalArgumentException("max < min: " + max + " < " + min);
        mMin = min; mMax = max; mLen = max - min + 1; 
        mArray = new boolean[mLen];
    }
    
    //////// setters ////////
    
    @Override
    public synchronized boolean select(int index)
    {
        index -= mMin;
        if (0 <= index && index < mLen && ! mArray[index]) {
            mIsNoneSelected = false;
            mArray[index] = true;
            return true;
        }
        return false;
    }
    
    @Override
    public synchronized boolean deselect(int index) 
    {
        index -= mMin;
        if (0 <= index && index < mLen && mArray[index]) {
            mArray[index]  = false;
            mIsAllSelected = false;
            return true;
        }
        return false;
    }
    
    /** Select every index in the range and return true, 
     *  or return false if all was already selected. 
     */
    @Override
    public synchronized boolean selectAll()
    {
        mIsNoneSelected = false;
        if (mIsAllSelected)
            return false;
        Arrays.fill(mArray, true);
        return mIsAllSelected = true;
    }
    
    /** Empty the selection set and return true, 
     *  or return false if none was already selected. 
     */
    @Override
    public synchronized boolean deselectAll()
    {
        mIsAllSelected = false;
        if (mIsNoneSelected)
            return false;
        Arrays.fill(mArray, false);
        return mIsNoneSelected = true;
    }
    
    public synchronized boolean setCurrent(int index)
    {
        index -= mMin;
        if (0 <= index && index < mLen) {
            mCurrentIndex   = index;
            mIsNoneSelected = false;
            return mArray[mCurrentIndex] = true;
        }
        return false;
    }
    
    /** 
     * Un-sets the current or "active" index, leaving none.
     * Following this with an immediate call to getCurrent will get null.
     * This does not imply a deselectAll operation; in face, it does
     * not change the number of selected items; it just makes none 
     * of them active.
     */
    public synchronized boolean setNoCurrent()
    {
        if (mCurrentIndex == null)
            return false;
        mCurrentIndex = null;
        return true;
    }
    
    //////// getters ////////
    
    @Override
    public synchronized Integer getCurrent() { return mCurrentIndex; }
    
    @Override
    public synchronized int[] allSelected() 
    {
        int count = numSelected();
        int idx = 0, selected[] = new int[count];
        for (int j = mMin; j <= mMax; j++)
            if (mArray[j])
                selected[idx++] = j;
        return selected;
    }
    
    @Override
    public synchronized int[] nonSelected() 
    {
        int count = mLen - numSelected();
        int idx = 0, nonselected[] = new int[count];
        for (int j = mMin; j <= mMax; j++)
            if ( ! mArray[j])
                nonselected[idx++] = j;
        return nonselected;
    }
    
    @Override
    public synchronized Integer smallest()
    {
        for (int j = mMin; j <= mMax; j++)
            if (mArray[j])
                return j;        
        return null;
    }
    
    @Override
    public synchronized Integer greatest()
    {
        for (int j = mMax; --j >= mMin; )
            if (mArray[j])
                return j;        
        return null;
    }
    
    @Override
    public synchronized Integer nextGreater() 
    {
        if (mCurrentIndex == null)
            return smallest();
        return greater(mCurrentIndex);
    }
    
    
    @Override
    public synchronized Integer nextGreater(int from) 
    {
        if (from < mMin)
            return smallest();
        return greater(from);
    }
    
    
    protected Integer greater(int start)
    {
        for (int j = start; ++j <= mMax; )
            if (mArray[j])
                return j;        
        return null;
    }
    
    @Override
    public synchronized Integer nextSmaller() 
    {
        if (mCurrentIndex == null)
            return greatest();
        return smaller(mCurrentIndex);
    }
    
    @Override
    public synchronized Integer nextSmaller(int from) 
    {
        if (from > mMax)
            return greatest();
        return smaller(from);
    }
    
    protected Integer smaller(int start)
    {
        for (int j = start; --j >= mMin; )
            if (mArray[j])
                return j;        
        return null;
    }
    
    @Override
    public synchronized int numSelected() 
    {
        int  count = 0;
        for (int j = 0; j < mLen; j++)
            if (mArray[j])
                count++;
        return  count;
    }
    
    @Override
    public synchronized int numSmaller(int key)  // aka rank
    {
        return countDown(key, mMin);
    }
    
    @Override
    public synchronized int numNotSmaller(int key)
    {
        return countDown(mMax + 1, key);
    }
    
    @Override
    public synchronized int numGreater(int key) 
    {
        return countDown(mMax + 1, key + 1); 
    }
    
    protected int countDown(int max, int min) 
    {
        int  count = 0;
        for (int j = max; --j >= min; )
            if (mArray[j])
                count++;        
        return  count;
    }
    
    
    //////// convenience ////////
    
    public synchronized boolean isAllSelected() 
    {
        if (mIsAllSelected)
            return true;
        if (countDown(mMax+1, mMin) == mLen)
            return mIsAllSelected = true;
        return mIsAllSelected = false;
    }
    
    public synchronized boolean isNoneSelected() 
    {
        if (mIsNoneSelected)
            return true;
        if (countDown(mMax+1, mMin) == 0)
            return mIsNoneSelected = true;
        return mIsNoneSelected = false;
    }
    
    
    //////// UNIT TESTING ////////
    public static int unit_test(int lvl) 
    {
        String  testName = SelectorArray.class.getName() + ".unit_test";
        Sx.puts(testName + " BEGIN");    
        
        SelectorArray sss = new SelectorArray(0, 9);
        sss.select(1);
        sss.select(2);
        sss.select(4);
        sss.select(5);
        sss.select(8);
        sss.setCurrent(4);
        
        int key  = 4;
        int rank = sss.numSmaller(key);
        int down = sss.numGreater(key);
        Sx.putsArray(sss.allSelected(), " has numSmaller(" + key + ") == " + rank);
        Sx.putsArray(sss.allSelected(), " has numGreater(" + key + ") == " + down);
        
        key  = 0;
        rank = sss.numSmaller(key);
        down = sss.numGreater(key);
        Sx.putsArray(sss.allSelected(), " has numSmaller(" + key + ") == " + rank);
        Sx.putsArray(sss.allSelected(), " has numGreater(" + key + ") == " + down);
        
        key  = 7;
        rank = sss.numSmaller(key);
        down = sss.numGreater(key);
        Sx.putsArray(sss.allSelected(), " has numSmaller(" + key + ") == " + rank);
        Sx.putsArray(sss.allSelected(), " has numGreater(" + key + ") == " + down);
        
        key  = 10;
        rank = sss.numSmaller(key);
        down = sss.numGreater(key);
        Sx.putsArray(sss.allSelected(), " has numSmaller(" + key + ") == " + rank);
        Sx.putsArray(sss.allSelected(), " has numGreater(" + key + ") == " + down);
        
        Integer next = sss.nextGreater();
        Sx.putsArray(sss.allSelected(), " has nextGreater from " + sss.getCurrent() + " as " + next);
        Integer prev = sss.nextSmaller();
        Sx.putsArray(sss.allSelected(), " has nextSmaller from " + sss.getCurrent() + " as " + prev);
        
        int nons = 7;
        prev = sss.nextSmaller(nons);
        Sx.putsArray(sss.allSelected(), " has nextSmaller(from==" + nons + ") as " + prev);
        next = sss.nextGreater(nons);
        Sx.putsArray(sss.allSelected(), " has nextGreater(from==" + nons + ") as " + next);
        
        Integer last = sss.greatest();
        sss.setCurrent(last);
        next = sss.nextGreater();
        Sx.putsArray(sss.allSelected(), " has nextGreater() from " + sss.getCurrent() + " as " + next);
        
        int more = last + 1;
        next = sss.nextGreater(more);
        Sx.putsArray(sss.allSelected(), " has nextGreater(from==" + more + ") as " + next);
        
        int first = sss.smallest();
        prev = sss.nextSmaller(first);
        Sx.putsArray(sss.allSelected(), " has nextSmaller(from==" + first + ") as " + prev);
        
        sss.setCurrent(first);
        prev =  sss.nextSmaller();
        Sx.putsArray(sss.allSelected(), " has nextSmaller from " + sss.getCurrent() + " as " + prev);
        
        int less = first - 1;
        prev = sss.nextSmaller(less);
        Sx.putsArray(sss.allSelected(), " has nextSmaller(from==" + less + ") as " + prev);
        
        Sx.puts(testName + " END");    
        return 0;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
