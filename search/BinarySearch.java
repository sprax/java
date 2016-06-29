package sprax.search;

import sprax.arrays.ArrayAlgo;
import sprax.selectors.Medians;
import sprax.shuffles.Shuffler;
import sprax.sorts.SortUtil;
import sprax.sprout.Sx;
import sprax.test.Sz;

public class BinarySearch
{  
    
    public static int indexOfFirstNonZeroValue(int[] sorted) 
    {
        return indexOfFirstNonZeroValue(sorted, sorted.length - 1);
    }
    
    public static int indexOfFirstNonZeroValue(int[] sorted, int size) 
    {
        int lo = 0, hi = size - 1;
        // special case:
        if (sorted[lo] > 0)
            return lo;
        else if (sorted[hi] == 0)
            return hi + 1;
        
        for (int md; lo < hi; ) {
            md = (hi + lo) >> 1;        // same as lo + (hi - lo)/2
            if (sorted[md] > 0)
                hi = md - 1;
            else
                lo = md + 1;
        }
        if (sorted[lo] > 0)
            return lo;
        else
            return hi + 1;
    }
    
    /** 
     * binary search for an index for the value v in a sorted array A,
     * that is, find k s.t. v == A[k].  Obviously, v must be the value
     * of an actual element in A.
     * 
     * @param A     sorted array of int
     * @param val   value to be search for in A
     * @return      an index k s.t. v == A[k], or -1 (invalid index)
     */
    public static int binarySearchEquals(int A[], int val)
    {
        for (int md, lo = 0, hi = A.length - 1; lo <= hi;)
        {
            md = (hi + lo) >> 1;  // average of first and last indices
            if (A[md] < val)
                lo = md + 1;        // raise the floor
            else if (A[md] > val)
                hi = md - 1;        // lower the ceiling
            else
                return md;          // found it 
        }
        return -1;
    }

    /**
     * return index of largest element v in A s.t v <= specified value
     * @param A   sorted array of int
     * @param val search value
     * @return    last index m s.t. A[m] <= val
     */
    public static int binarySearchLowerBound(int A[], int val)
    {
        int md = 0;
        for (int lo = 0, hi = A.length - 1; lo <= hi;)
        {
            md = (hi + lo) >> 1;
            if (A[md] == val)
                return md;
            if (A[md] > val)
                hi = md - 1;
            else
                lo = md + 1;
        }
        // we may have overshot by one, so 
        // check whether A[md] or A[md-1] is the actual 
        // infimum (greatest lower bound).
        // To avoid this overshoot, we'd have to check intervals
        // by doing two comparisons inside the loop, which is generally worse.
        if (A[md] <= val)
            return md;
        if (--md >= 0 && A[md] <= val)
            return md;
        return -1;
    }

    /**
     * return index of largest element v in A s.t v <= specified value
     * @param A   sorted array of int
     * @param val search value
     * @return    last index m s.t. A[m] <= val
     */
    public static int binarySearchLowerBoundDbl(double A[], double val)
    {
        int md = 0;
        for (int lo = 0, hi = A.length - 1; lo <= hi;)
        {
            md = (hi + lo) >> 1;
            if (A[md] == val)
                return md;
            if (A[md] > val)
                hi = md - 1;
            else
                lo = md + 1;
        }
        // we may have overshot by one, so 
        // check whether A[md] or A[md-1] is the actual 
        // infimum (greatest lower bound).
        // To avoid this overshoot, we'd have to check intervals
        // by doing two comparisons inside the loop, which is generally worse.
        if (A[md] <= val)
            return md;
        if (--md >= 0 && A[md] <= val)
            return md;
        return -1;
    }

    /**
     * return index of largest element v in A s.t v < specified value
     * @param A   sorted array of int
     * @param val search value
     * @return    last index m s.t. A[m] <= val
     */
    public static int binarySearchStrictLowerBound(int A[], int val)
    {
        int lo, hi, md = 0;
        for (lo = 0, hi = A.length - 1; lo <= hi;)
        {
            md = (hi + lo) >> 1;
            if (A[md] >= val)
                hi = md - 1;
            else
                lo = md + 1;
        }
        // we may have overshot by one, so 
        // check whether A[md] or A[md-1] is the actual 
        // infimum (greatest lower bound).
        // To avoid this overshoot, we'd have to check intervals
        // by doing two comparisons inside the loop, which is generally worse.
        if (A[md] < val)
            return md;
        if (--md >= 0 && A[md] < val)
            return md;
        return -1;
    }

    /**
     * return index of smallest element v in A s.t v >= specified value
     * @param A   sorted array of int
     * @param val search value
     * @return    first index m s.t. A[m] >= val
     */
    public static int binarySearchUpperBound(int A[], int val)
    {
        int md = 0;
        for (int lo = 0, hi = A.length - 1; lo <= hi;)
        {
            md = (hi + lo) >> 1;
            if (A[md] == val)
                return md;
            if (A[md] > val)
                hi = md - 1;
            else
                lo = md + 1;
        }
        if (/* md < A.length && */A[md] >= val)  // TODO: exit conditions seem wrong
            return md;
        if (++md < A.length && A[md] >= val)
            return md;
        return -1;
    }

    /** 
     * binary search for an index for the value v in a sorted array A,
     * that is, find k s.t. v == A[k].  Obviously, v must be the value
     * of an actual element in A.
     * 
     * @param A     sorted array of int
     * @param val   value to be search for in A
     * @return      an index k s.t. v == A[k], or -1 (invalid index)
     */
    public static int interpolationSearchEquals(int A[], int val)
    {
        // Must do error checking before allowing interpolation
        if (A == null || A.length < 1)
            return -1;
        int lo = 0, hi = A.length - 1;
        if (val < A[lo] || val > A[hi])
            return -1;
        
        for (int md = 0; lo <= hi;)
        {
            if (A[hi] == A[lo]) {     // value of A is const in [lo .. hi];
                if (A[lo] == val)     // either this value == v, or v is not in A.
                    return lo;        // So return the smallest index found,
                break;                // or return NotFound.
            } else {
                double delta = (hi - lo) * (val - A[lo]) / (double) (A[hi] - A[lo]);
                if (delta > 1.0 || delta < -1.0) {
                    md = lo + (int) delta;
                } else {
                    md = (lo + hi) >> 1;
                }
                
                //        if (0.0 <= delta && delta <= 1.0)
                //          md = lo + 1;
                //        else if (-1.0 <= delta && delta < 0.0)
                //          md = lo - 1;
                //        else
                //          md = lo + (int)delta;
                
            }
            if (A[md] == val)
                return md;
            
            if (A[md] > val)
                hi = md - 1;
            else
                lo = md + 1;
        }
        return -1;
    }
    
    /**********************************
#include <cstdio>
#include <algorithm>

int keys[] = {1,2,2,2,2,2,3,4,5,6,7,8,9,10};

int main () {
    unsigned length = sizeof(keys)/sizeof(keys[0]);
    auto lower = std::lower_bound(&keys[0], &keys[length], 2);
    auto upper = std::upper_bound(&keys[0], &keys[length], 2);
    unsigned index = lower - &keys[0];
    while (lower++ < upper)
        printf("%u ", index++);
    printf("\n");
    return 0;
}

     */
    
    static int sCalls = 0;
    
    public static int getFirstIndexInRange(int value, int[] iA, int leftIndex, int rightIndex)
    {
        sCalls++;
        
        if (leftIndex > rightIndex)
        {
            return Integer.MAX_VALUE;    // Not found
        }
        
        int mid = (rightIndex - leftIndex) / 2 + leftIndex;
        if (iA[mid] > value)
        {
            return getFirstIndexInRange(value, iA, leftIndex, mid - 1);
        }
        if (iA[mid] < value)
        {
            return getFirstIndexInRange(value, iA, mid + 1, rightIndex);
        }
        
        // So iA[mid] == value
        int bet = getFirstIndexInRange(value, iA, leftIndex, mid - 1);
        if (mid < bet)
            return mid;
        else
            return bet;
    }
    
    public static int getFirstIndex(int value, int[] iA)
    {
        if (iA == null)
            return -1;
        return getFirstIndexInRange(value, iA, 0, iA.length - 1);
    }
    
    public static void test_getFirstIndex()
    {
        Sx.puts(BinarySearch.class.getName() + ".test_getFirstIndex");
        /*
        Expected output:
        First index of 8 is : 20
        First index of 2 is : 1
        */
        int[] input = { 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 8, 8, 8, 8, 9,
                9 };
        
        Sx.putsArray("Array size " + input.length + ": ", input);
        
        int value = 9;
        Sx.puts("First index of " + value + " is : " + getFirstIndex(value, input));
        
        value = 8;
        Sx.puts("First index of " + value + " is : " + getFirstIndex(value, input));
        
        value = 2;
        Sx.puts("First index of " + value + " is : " + getFirstIndex(value, input));
        
        value = 1;
        Sx.puts("First index of " + value + " is : " + getFirstIndex(value, input));
        
        Sx.puts("getFirstIndex... calls: " + sCalls);
        
    }
    
    public static int test_binarySearch(int SS[])
    {
        int nCols = SS.length;
        int minVal = SS[0];
        int maxVal = SS[nCols - 1];
        int mmoVal = maxVal - 1;
        int medVal = Medians.medianOfSortedArray(SS);
        int midVal = (minVal + maxVal) >> 1;
        int modVal = Integer.MIN_VALUE;
        for (int v : SS) {
            if (v > medVal) {
                modVal = v;       // mode val: first v in A s.t. v >= medVal, but we use >
                break;
            }
        }
        for (int j = 0; j < SS.length; j++)
            Sx.format(" %2d", j);
        Sx.puts();
        Sx.putsArray(SS);
        Sx.puts("mode " + modVal + "  median " + medVal + "  middle " + midVal);
        
        Sx.print("   The values:");
        Sx.format(" first mode median mid 2nd last  %2d  %2d  %2d  %2d  %2d  %2d\n"
                , minVal, modVal, medVal, midVal, mmoVal, maxVal);
        
        int iMin = binarySearchEquals(SS, minVal);
        int iMod = binarySearchEquals(SS, modVal);
        int iMed = binarySearchEquals(SS, medVal);
        int iMid = binarySearchEquals(SS, midVal);
        int iMmo = binarySearchEquals(SS, mmoVal);
        int iMax = binarySearchEquals(SS, maxVal);
        Sx.print("binary search:");
        Sx.format(" first mode median mid 2nd last  %2d  %2d  %2d  %2d  %2d  %2d\n"
                , iMin, iMod, iMed, iMid, iMmo, iMax);
        
        iMin = interpolationSearchEquals(SS, minVal);
        iMod = interpolationSearchEquals(SS, modVal);
        iMed = interpolationSearchEquals(SS, medVal);
        iMid = interpolationSearchEquals(SS, midVal);
        iMmo = interpolationSearchEquals(SS, mmoVal);
        iMax = interpolationSearchEquals(SS, maxVal);
        Sx.print("interpolation:");
        Sx.format(" first mode median mid 2nd last  %2d  %2d  %2d  %2d  %2d  %2d\n", iMin, iMod,
                iMed, iMid, iMmo, iMax);
        
        iMin = binarySearchLowerBound(SS, minVal);
        iMod = binarySearchLowerBound(SS, modVal);
        iMed = binarySearchLowerBound(SS, medVal);
        iMid = binarySearchLowerBound(SS, midVal);
        iMmo = binarySearchLowerBound(SS, mmoVal);
        iMax = binarySearchLowerBound(SS, maxVal);
        Sx.print("lower bound:  ");
        Sx.format(" first mode median mid 2nd last  %2d  %2d  %2d  %2d  %2d  %2d\n", iMin, iMod,
                iMed, iMid, iMmo, iMax);
        
        iMin = binarySearchUpperBound(SS, minVal);
        iMod = binarySearchUpperBound(SS, modVal);
        iMed = binarySearchUpperBound(SS, medVal);
        iMid = binarySearchUpperBound(SS, midVal);
        iMmo = binarySearchUpperBound(SS, mmoVal);
        iMax = binarySearchUpperBound(SS, maxVal);
        Sx.print("upper bound:  ");
        Sx.format(" first mode median mid 2nd last  %2d  %2d  %2d  %2d  %2d  %2d\n", iMin, iMod,
                iMed, iMid, iMmo, iMax);
        
        test_getFirstIndex();
        
        return 0;
    }
    
    public static int test_binarySearchDouble(double sorted[], int size, int nValues)
    {
        double minVal = sorted[0];
        double maxVal = sorted[size - 1];
        double difVal = maxVal - minVal;
        int answers[] = new int[nValues];
        for (int j = 0; j < nValues; j++)
        {
            double value = maxVal - j * (difVal / nValues);
            answers[j] = binarySearchLowerBoundDbl(sorted, value);
            Sx.format("%d   %f  ->  %d\n", j, value, answers[j]);
        }
        ArrayAlgo.reverseArray(answers);
        boolean descending = SortUtil.verifyIsNonDecreasing(answers, nValues);
        if (descending)
            return 0;
        return -1;
    }
    
    public static int unit_test(int level)
    {
        String testName = BinarySearch.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        double sorted[] = { 0.1, 0.2, 0.5, 0.7, 0.88, 0.9 };
        test_binarySearchDouble(sorted, sorted.length, 10);
        
        int numWrong = 0;
        if (level > 0) {
            int SS[] = { 10, 12, 14, 17, 19, 23, 29, 31, 37, 43, 47, 18, 36, 44, 55, 66 };
            numWrong += test_binarySearch(SS);
            int TT[] = { 10, 12, 14, 14, 14, 14, 14, 14, 15, 16, 17, 18, 36, 44, 55, 66 };
            numWrong += test_binarySearch(TT);
        }
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test(1);
    }
}
