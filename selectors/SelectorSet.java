package sprax.selectors;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class SelectorSet implements Selector
{
    private final SortedSet<Integer> mSet;
    final int                        mMin;
    final int                        mMax;
    final int                        mLen;
    private Integer                  mCurrentIndex;
    private boolean                  mIsAllSelected;        // private for lazy evaluation
    private boolean                  mIsNoneSelected = true;
    
    SelectorSet(int min, int max)
    {
        if (max < min)
            throw new IllegalArgumentException("max < min: " + max + " < " + min);
        mMin = min;
        mMax = max;
        mLen = max - min + 1;
        mSet = new TreeSet<Integer>();
    }
    
    /*
     * Call this constructor with a synchronized sorted set to get
     * basic concurrent access safety in multi-threaded usage.
     * For example:
     *     SortedSet<Integer>  ssi = new TreeSet<Integer>();
     *     SortedSet<Integer>  sss = Collections.synchronizedSortedSet(ssi);
     *     SelectorSet selectorSet = new SelectorSet(0, 99, sss);
     *     
     * @param min
     * @param max
     * @param sortedSet (synchronized)
     */
    SelectorSet(int min, int max, final SortedSet<Integer> sortedSet)
    {
        if (max < min)
            throw new IllegalArgumentException("max < min: " + max + " < " + min);
        mMin = min;
        mMax = max;
        mLen = max - min + 1;
        sortedSet.clear();
        mSet = sortedSet;
    }
    
    //////// setters ////////
    
    @Override
    public boolean select(int index) {
        if (mMin <= index && index <= mMax && !mSet.contains(index)) {
            mIsNoneSelected = false;
            mSet.add(index);
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean deselect(int index) {
        if (mSet.contains(index)) {
            mSet.remove(index);
            mIsAllSelected = false;
            return true;
        }
        return false;
    }
    
    /**
     * Select every index in the range and return true,
     * or return false if all was already selected.
     */
    @Override
    public boolean selectAll()
    {
        mIsNoneSelected = false;
        if (mIsAllSelected)
            return false;
        for (int j = mMin; j <= mMax; j++)
            mSet.add(j);
        return mIsAllSelected = true;
    }
    
    /**
     * Empty the selection set and return true,
     * or return false if none was already selected.
     */
    @Override
    public boolean deselectAll()
    {
        mIsAllSelected = false;
        if (mIsNoneSelected)
            return false;
        mSet.clear();
        return mIsNoneSelected = true;
    }
    
    public boolean setCurrent(int index)
    {
        if (mMin <= index && index <= mMax) {
            mCurrentIndex = index;
            mIsNoneSelected = false;
            return mSet.add(mCurrentIndex);
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
    public boolean setNoCurrent()
    {
        if (mCurrentIndex == null)
            return false;
        mCurrentIndex = null;
        return true;
    }
    
    //////// getters ////////
    
    @Override
    public Integer getCurrent() {
        return mCurrentIndex;
    }
    
    @Override
    public int[] allSelected()
    {
        int idx = 0, selected[] = new int[mSet.size()];
        for (Integer sel : mSet)
            selected[idx++] = sel;
        return selected;
    }
    
    @Override
    public int[] nonSelected()
    {
        int idx = 0, selected[] = new int[mLen - mSet.size()];
        for (int j = mMin; j <= mMax; j++)
            if (!mSet.contains(j))
                selected[idx++] = j;
        return selected;
    }
    
    @Override
    public Integer smallest()
    {
        if (mSet.isEmpty())
            return null;
        return mSet.first();
    }
    
    @Override
    public Integer greatest()
    {
        if (mSet.isEmpty())
            return null;
        return mSet.last();
    }
    
    @Override
    public Integer nextGreater()
    {
        if (mCurrentIndex == null)
            return smallest();
        // If mCurrentIndex is not null, this tailSet must have a next;
        // in fact, it must be mCurrentIndex.  Return the one after that
        // (the next next) if it exists.  (Beware of NoSuchElementException.)
        Iterator<Integer> tail = mSet.tailSet(mCurrentIndex).iterator();
        tail.next();
        if (tail.hasNext()) {
            return tail.next();
        }
        return null;
    }
    
    @Override
    public Integer nextGreater(int from)
    {
        if (from < mMin)
            return smallest();
        Iterator<Integer> tail = mSet.tailSet(from).iterator();
        if (tail.hasNext()) {
            int next = tail.next();
            if (next > from)
                return next;
            if (tail.hasNext()) {
                return tail.next();
            }
        }
        return null;
    }
    
    @Override
    public Integer nextSmaller()
    {
        if (mCurrentIndex == null)
            return greatest();
        // If mCurrentIndex == smallest == set.first, this headSet will be empty;
        // otherwise, its last element will be strictly < mCurrentIndex.
        SortedSet<Integer> head = mSet.headSet(mCurrentIndex);
        if (!head.isEmpty())
            return head.last();
        return null;
    }
    
    @Override
    public Integer nextSmaller(int from)
    {
        if (from > mMax)
            return greatest();
        // If mCurrentIndex == smallest == set.first, this headSet will be empty;
        // otherwise, its last element will be strictly < mCurrentIndex.
        SortedSet<Integer> head = mSet.headSet(from);
        if (!head.isEmpty())
            return head.last();
        return null;
    }
    
    @Override
    public int numSelected() {
        return mSet.size();
    }
    
    @Override
    public int numSmaller(int key) {
        return mSet.headSet(key).size();
    }
    
    @Override
    public int numNotSmaller(int key) {
        return mSet.tailSet(key).size();
    }
    
    @Override
    public int numGreater(int key) {
        SortedSet<Integer> tail = mSet.tailSet(key);
        if (tail.isEmpty())
            return 0;
        int size = tail.size();
        if (key == tail.first())
            return size - 1;
        return size;
    }
    
    //////// convenience ////////
    
    public boolean isAllSelected()
    {
        if (mIsAllSelected)
            return true;
        if (mSet.size() == mLen)
            return mIsAllSelected = true;
        return mIsAllSelected = false;
    }
    
    public boolean isNoneSelected()
    {
        if (mIsNoneSelected)
            return true;
        if (mSet.size() == 0)
            return mIsNoneSelected = true;
        return mIsNoneSelected = false;
    }
    
    //////// UNIT TESTING ////////
    public static int unit_test()
    {
        String testName = SelectorSet.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        SelectorSet sss = new SelectorSet(0, 9);
        sss.mSet.add(1);
        sss.mSet.add(2);
        sss.mSet.add(4);
        sss.mSet.add(5);
        sss.mSet.add(8);
        sss.setCurrent(4);
        
        int key = 4;
        int rank = sss.numSmaller(key);
        int down = sss.numGreater(key);
        Sx.putsIterable(sss.mSet, " has numSmaller(" + key + ") == " + rank);
        Sx.putsIterable(sss.mSet, " has numGreater(" + key + ") == " + down);
        
        key = 0;
        rank = sss.numSmaller(key);
        down = sss.numGreater(key);
        Sx.putsIterable(sss.mSet, " has numSmaller(" + key + ") == " + rank);
        Sx.putsIterable(sss.mSet, " has numGreater(" + key + ") == " + down);
        
        key = 7;
        rank = sss.numSmaller(key);
        down = sss.numGreater(key);
        Sx.putsIterable(sss.mSet, " has numSmaller(" + key + ") == " + rank);
        Sx.putsIterable(sss.mSet, " has numGreater(" + key + ") == " + down);
        
        key = 10;
        rank = sss.numSmaller(key);
        down = sss.numGreater(key);
        Sx.putsIterable(sss.mSet, " has numSmaller(" + key + ") == " + rank);
        Sx.putsIterable(sss.mSet, " has numGreater(" + key + ") == " + down);
        
        Integer next = sss.nextGreater();
        Sx.putsIterable(sss.mSet, " has nextGreater from " + sss.getCurrent() + " as " + next);
        Integer prev = sss.nextSmaller();
        Sx.putsIterable(sss.mSet, " has nextSmaller from " + sss.getCurrent() + " as " + prev);
        
        int nons = 7;
        prev = sss.nextSmaller(nons);
        Sx.putsIterable(sss.mSet, " has nextSmaller(from==" + nons + ") as " + prev);
        next = sss.nextGreater(nons);
        Sx.putsIterable(sss.mSet, " has nextGreater(from==" + nons + ") as " + next);
        
        Integer last = sss.greatest();
        sss.setCurrent(last);
        next = sss.nextGreater();
        Sx.putsIterable(sss.mSet, " has nextGreater() from " + sss.getCurrent() + " as " + next);
        
        int more = last + 1;
        next = sss.nextGreater(more);
        Sx.putsIterable(sss.mSet, " has nextGreater(from==" + more + ") as " + next);
        
        int first = sss.smallest();
        prev = sss.nextSmaller(first);
        Sx.putsIterable(sss.mSet, " has nextSmaller(from==" + first + ") as " + prev);
        
        sss.setCurrent(first);
        prev = sss.nextSmaller();
        Sx.putsIterable(sss.mSet, " has nextSmaller from " + sss.getCurrent() + " as " + prev);
        
        int less = first - 1;
        prev = sss.nextSmaller(less);
        Sx.putsIterable(sss.mSet, " has nextSmaller(from==" + less + ") as " + prev);
        
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
