package sprax.sorts;

import sprax.sprout.Sx;
import sprax.test.Sz;

interface IndexComparable<T>
{
    public T get(int index);
    
    public void assign(int index, T value);
    
    public void exchange(int j, int k);
    
    public int compare(int j, int k);
    
    public int size();
}

class IntArray implements IndexComparable<Integer>
{
    int[] mArray;
    
    IntArray(int[] array)
    {
        mArray = array;
    }
    
    @Override
    public Integer get(int index) {
        return mArray[index];
    }
    
    @Override
    public void assign(int index, Integer value) {
        mArray[index] = value;
    }
    
    @Override
    public void exchange(int j, int k) {
        int value = mArray[j];
        mArray[j] = mArray[k];
        mArray[k] = value;
    }
    
    @Override
    public int compare(int j, int k) {
        return mArray[j] - mArray[k];
    }
    
    @Override
    public int size() {
        return mArray.length;
    }
}

class InPlaceStableSort<T> implements SortInt
{
    IndexComparable<T> mA;
    
    InPlaceStableSort(IndexComparable<T> toBeSorted) {
        mA = toBeSorted;
    }
    
    @Override
    public void sort(int[] iA) {
        inPlaceStableSort(iA);
    }    
    
    /** Not public, but friendly.  An instance of InPlaceStableSort is created and discarded. */
    static void inPlaceStableSort(int[] iA) {
        IntArray intArray = new IntArray(iA);
        InPlaceStableSort<Integer> ipss = new InPlaceStableSort<Integer>(intArray);
        ipss.sort();
    }    
    
    /** sorts its own member array */
    public void sort() {
        sort(0, mA.size());
    }    
    
    public static InPlaceStableSort<Integer> createFromIntArray(int[] toBeSorted)
    {
        IntArray ia = new IntArray(toBeSorted);
        return new InPlaceStableSort<Integer>(ia);
    }
    
    int lower(int from, int to, int val) {
        int len = to - from, half;
        while (len > 0) {
            half = len / 2;
            int mid = from + half;
            if (mA.compare(mid, val) < 0) {
                from = mid + 1;
                len = len - half - 1;
            } else
                len = half;
        }
        return from;
    }
    
    int upper(int from, int to, int val) {
        int len = to - from, half;
        while (len > 0) {
            half = len / 2;
            int mid = from + half;
            if (mA.compare(val, mid) < 0)
                len = half;
            else {
                from = mid + 1;
                len = len - half - 1;
            }
        }
        return from;
    }
    
    void insert_sort(int from, int to) {
        if (to > from + 1) {
            for (int i = from + 1; i < to; i++) {
                for (int j = i; j > from; j--) {
                    if (mA.compare(j, j - 1) < 0)
                        mA.exchange(j, j - 1);
                    else
                        break;
                }
            }
        }
    }
    
    int gcd(int m, int n)
    {
        while (n != 0)
        {
            int t = m % n;
            m = n;
            n = t;
        }
        return m;
    }
    
    void reverse(int from, int to) {
        while (from < to) {
            mA.exchange(from++, to--);
        }
    }
    
    void rotate(int from, int mid, int to)
    {
        /*  a less sophisticated but costlier version: 
        reverse(from, mid-1); 
        reverse(mid, to-1); 
        reverse(from, to-1); 
         */
        if (from == mid || mid == to)
            return;
        int n = gcd(to - from, mid - from);
        while (n-- != 0) {
            T val = mA.get(from + n);
            int shift = mid - from;
            int p1 = from + n, p2 = from + n + shift;
            while (p2 != from + n) {
                mA.assign(p1, mA.get(p2));
                p1 = p2;
                if (to - p2 > shift)
                    p2 += shift;
                else
                    p2 = from + (shift - (to - p2));
            }
            mA.assign(p1, val);
        }
    }
    
    void merge(int from, int pivot, int to, int len1, int len2)
    {
        if (len1 == 0 || len2 == 0)
            return;
        if (len1 + len2 == 2) {
            if (mA.compare(pivot, from) < 0)
                mA.exchange(pivot, from);
            return;
        }
        int first_cut, second_cut;
        int len11, len22;
        if (len1 > len2) {
            len11 = len1 / 2;
            first_cut = from + len11;
            second_cut = lower(pivot, to, first_cut);
            len22 = second_cut - pivot;
        } else {
            len22 = len2 / 2;
            second_cut = pivot + len22;
            first_cut = upper(from, pivot, second_cut);
            len11 = first_cut - from;
        }
        rotate(first_cut, pivot, second_cut);
        int new_mid = first_cut + len22;
        merge(from, first_cut, new_mid, len11, len22);
        merge(new_mid, second_cut, to, len1 - len11, len2 - len22);
    }
    
    void sort(int from, int to) {
        if (to - from < 12) {
            insert_sort(from, to);
            return;
        }
        int middle = (from + to) / 2;
        sort(from, middle);
        sort(middle, to);
        merge(from, middle, to, middle - from, to - middle);
    }
    

    static int unit_test(int level)
    {
        String testName = InPlaceStableSort.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int[] iA = { 0, -1, 2, -3, 4, -5, 6, -7, 8, -9 };
        InPlaceStableSort<Integer> ipss = InPlaceStableSort.createFromIntArray(iA);
        ipss.sort();
        Sx.putsArray("intArray sorted: ", iA);
        boolean sorted = SortUtil.verifySorted(iA);
        numWrong += Sz.wrong(sorted);
        
        int iB[] = { 5, -4, 8, -1, 0, 3, -7, 9, -2, 6 };
        ipss.sort(iB);
        Sx.putsArray("intArray sorted: ", iB);
        sorted = SortUtil.verifySorted(iB);
        numWrong += Sz.wrong(sorted);
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test(1);
    }
    

};
