package sprax.series;

import java.util.LinkedList;

import sprax.Sx;

/**
 * From an ongoing stream of N numbers, keep track the most recent
 * window of K numbers, and every time a new number is read from the stream,
 * return the maximum of those K numbers.  
 * Space: O(K)
 * Time:  O(N) < O(NK) 
 * @author sprax
 *
 */
public class RunningMaxLazyRescan implements RunningMax
{
    LinkedList<Integer> mWindowList;
    int  mWindowSize;
    int  mMaxInWindow;
    int  mCountMaxVal;	// count how many instances of max value are in the window

    RunningMaxLazyRescan(int windowSize)
    {
        if (windowSize < 2)
        {
            throw new IllegalArgumentException("windowSize < 2");
        }
        mWindowSize = windowSize;
        mWindowList = new LinkedList<Integer>();
        mMaxInWindow = Integer.MIN_VALUE;
        mCountMaxVal = 0;
    }

    @Override
    public int getSize() { return mWindowSize; }

    @Override
    public int getMax() { return mMaxInWindow; }

    @Override
    public Object[] getWindow() 
    {
        return mWindowList.toArray(); 
    }

    @Override
    public int addNumAndReturnMax(int num)
    {
        updateMax(num);
        mWindowList.add(num);		
        if (mWindowList.size() > mWindowSize)
        {
            int old = mWindowList.pop();
            if (old == mMaxInWindow && --mCountMaxVal == 0)
            {
                // Scan list for new max
                mMaxInWindow = Integer.MIN_VALUE;
                mCountMaxVal = 0;
                for (int val : mWindowList)
                {
                    updateMax(val);
                }
            }
        }
        return mMaxInWindow;
    }

    private void updateMax(int num)
    {
        if (mMaxInWindow < num)
        {
            mMaxInWindow = num;
            mCountMaxVal = 1;
        }
        else if (mMaxInWindow == num)
        {
            mCountMaxVal++;
        }
    }

    public static void unit_test()
    {
        RunningMaxTest.test_smallSeries(new RunningMaxLazyRescan(5));
    }

    public static void main(String[] args) { unit_test(); }
}
