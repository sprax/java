package sprax.sorts;

import sprax.Sx;
import sprax.Sz;

public class BucketSort
{
    int mNumBuckets;
    int mBuckets[];
    
    public BucketSort(int numBuckets) {
        mNumBuckets = numBuckets;
        mBuckets = new int[numBuckets];		// auto-initialized to 0's
    }
    
    /**
     * Assumes 0 <= A[j] < mNumBuckets for all j.
     * No error checking.
     */
    public void sort0toN(int arr[])
    {
        for (int j = 0; j < arr.length; j++) {
            mBuckets[arr[j]]++;
        }
        for (int j = 0, k = 0; j < mNumBuckets && k < arr.length; j++) {
            while (--mBuckets[j] >= 0) {
                arr[k++] = j;
            }
        }
    }
    
    public void sort(int A[], int minVal)
    {
        for (int val : A)
            mBuckets[val - minVal]++;
        
        for (int k = 0, j = 0; j < mNumBuckets; j++) {
            while (--mBuckets[j] >= 0) {
                A[k++] = j + minVal;
            }
        }
    }
    
    /**
     * sortRange does bucket sort of int in the specified range,
     * that is, in the half-open interval [min, max).
     * 
     * @param arr
     * @param min
     */
    public static void sortRange(int arr[], int min, int max)
    {
        // Assumes that for all j, min <= arr[j] < max == numBuckets. 
        // System.out.print("min & max & range: " + min + " " + max + " " + range + "\n");
        int range = max - min + 1;
        int count[] = new int[range];
        for (int j = 0; j < arr.length; j++) {
            count[arr[j] - min]++;
        }
        
        for (int k = 0, j = 0; j < count.length; j++) {
            while (--count[j] >= 0) {
                arr[k++] = j + min;
            }
        }
    }
    
    public static int[] bucketSort(int[] arr) {
        // achieves O(n) time by using O(input range) space
        if (arr.length < 2) {
            return arr;
        }
        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        for (int j = 0; j < arr.length; j++) {
            if (min > arr[j]) {
                min = arr[j];
            } else if (max < arr[j]) {
                max = arr[j];
            }
        }
        sortRange(arr, min, max);
        return arr;
    }
    
    public static int[] bucketSort_lame(int[] arr) {
        // lame because it only works for non-negative integers < array length
        int[] count = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            count[arr[i]]++;
        }
        
        for (int i = 0, j = 0; i < count.length; i++) {
            for (; count[i] > 0; (count[i])--) {
                arr[j++] = i;
            }
        }
        return arr;
    }
    
    public static int unit_test()
    {
        String testName = BucketSort.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int[] arr = new int[] { -1, 21, -7, 1, 3, 4, 6, 4, 2, 9, 1, 99, 2, 9 };
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
        arr = bucketSort(arr);
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] > arr[i + 1])
                numWrong++;
            System.out.print(arr[i] + " ");
        }
        System.out.println(arr[arr.length - 1]);
        
        final int size = 26;
        BucketSort bs = new BucketSort(size);
        String str = "thequickbrownfoxjumpsoverthelazydog";
        char[] chr = str.toCharArray();
        int[] nnn = new int[chr.length];
        for (int j = 0; j < chr.length; j++)
            nnn[j] = chr[j];
        bs.sort(nnn, 'a');
        for (int j = 0; j < chr.length; j++)
            chr[j] = (char) nnn[j];
        String rst = new String(chr);
        Sx.puts(str + " -> " + rst);
        numWrong += SortUtil.countDecreasing(nnn);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}