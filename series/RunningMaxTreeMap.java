package sprax.series;

import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeMap;

import sprax.Sz;
import sprax.sprout.Sx;

/**
 * From an ongoing stream of N numbers, keep track the most recent
 * window of K numbers, and every time a new number is read from the stream,
 * return the maximum of those K numbers.  
 * Space: O(K)
 * Time:  O(N) < O(NK) 
 * @author sprax
 *
 */
public class RunningMaxTreeMap implements RunningMax
{
    int  mWindowSize;
    int  mMaxInWindow;
    private TreeMap<Integer, Integer> mTreeMap;
    private Queue<Integer> mQueue;

    RunningMaxTreeMap(int windowSize)
    {
        if (windowSize < 2)
        {
            throw new IllegalArgumentException("windowSize < 2");
        }
        mWindowSize = windowSize;
        mMaxInWindow = Integer.MIN_VALUE;
        mTreeMap = new TreeMap<Integer, Integer>();
        mQueue = new LinkedList<Integer>();
    }

    @Override
    public int getSize() { return mWindowSize; }

    @Override
    public int getMax() { return mMaxInWindow; }

    @Override
    public Object[] getWindow() 
    {
        return mQueue.toArray(); 
    }

    @Override
    public int addNumAndReturnMax(int num)
    {
        if (mQueue.size() >= mWindowSize)
        {
            Integer lastNum = mQueue.poll();
            int refCount = mTreeMap.get(lastNum);
            if (refCount <= 1) {
                mTreeMap.remove(lastNum);
            } else {
                mTreeMap.put(lastNum, refCount - 1);
            }
        }
        mQueue.add(num);

        Integer numCount = mTreeMap.get(num);
        if (numCount == null)
        {
            mTreeMap.put(num, 1);
        }
        else
        {
            mTreeMap.put(num, 1 + numCount);
        }
        mMaxInWindow = mTreeMap.lastKey();
        return mMaxInWindow;
    }

    public static void unit_test()
    {
        String testName = RunningMaxTreeMap.class.getName() + ".unit_test";
        Sz.begin(testName);
        RunningMaxTest.test_smallSeries(new RunningMaxTreeMap(5));
        Sz.end(testName, 0);
    }

    public static void main(String[] args) { unit_test(); }
}
