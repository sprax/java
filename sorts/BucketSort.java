package sprax.sorts;

import sprax.Sx;
import sprax.Sz;

public class BucketSort implements SortInt
{
    int mNumBuckets;
    int mBuckets[];
    
    public BucketSort(int numBuckets) {
        mNumBuckets = numBuckets;
        mBuckets = new int[numBuckets];		// auto-initialized to 0's
    }
    

    @Override
    public void sort(int[] array) {
        bucketSort(array);
    }
    
    /**
     * Sort int array in place using an auxiliary array of size 
     * equal to the range of values in the original array.  The
     * array is sorted in-place by counting then assigning raw values.
     * Achieves O(N) time by using O(input range) extra space.
     */
    public static void bucketSort(int[] arr) {
        if (arr == null || arr.length < 2) {
            return;                             // GIGO
        }

        // No need for MAX_VALUE, we know the array is not empty.
        int min = arr[0];
        int max = min;
        for (int j = 1; j < arr.length; j++) {
            if (min > arr[j]) {
                min = arr[j];
            } else if (max < arr[j]) {
                max = arr[j];
            }
        }
        sortRange(arr, arr, min, max);
    }

    /**
     * Achieves O(N) time by using O(input range) extra space.
     * Places sorted contents of input array into output array.
     */
    public static void bucketSort(int[] input, int output[])
    {
        if (input == null || input.length < 1)
            return;                             // GIGO
        if (output == null || output.length < 1)
            return;                             // GIGO
        if (input.length < 2) {
            output[0] = input[0];
            return;                             // already sorted
        }

        // No need for MAX_VALUE, we know the array is not empty.
        int min = input[0];
        int max = min;
        for (int j = 1; j < input.length; j++) {
            if (min > input[j]) {
                min = input[j];
            } else if (max < input[j]) {
                max = input[j];
            }
        }
        sortRange(input, output, min, max);
    }
    
    
    /**
     * sortRange does bucket sort of int in the specified range,
     * that is, in the half-open interval [min, max).
     * 
     * @param input
     * @param min
     */
    static void sortRange(int input[], int output[], int min, int max)
    {
        // Assumes that for all j, min <= input[j] < max == numBuckets. 
        // System.out.print("min & max & range: " + min + " " + max + " " + range + "\n");
        int range = max - min + 1;
        int count[] = new int[range];
        for (int j = 0; j < input.length; j++) {
            count[input[j] - min]++;
        }
        for (int k = 0, j = 0; j < count.length; j++) {
            while (--count[j] >= 0) {
                output[k++] = j + min;
            }
        }
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

    /** @deprecated */
    static int[] bucketSort_lame(int[] arr) 
    {
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
    

    public static int unit_test(int level)
    {
        String testName = BucketSort.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        // test handling of null input
        int nullA[] = null;
        bucketSort(nullA);
        
        // test handling of empty input
        int emptyA[] = new int[0];
        bucketSort(emptyA, emptyA);
        
        int[] arr = new int[] { -1, 21, -7, 1, 3, 4, 6, 4, 2, 9, 1, 99, 2, 9 };
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
        bucketSort(arr);
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
        
        if (level > 0) {
            int radius = 100;
            int numBuckets = radius*2 + 1;
            BucketSort bucketSort = new BucketSort(numBuckets);
            numWrong += SortUtil.test_sort_random_int_array_default(bucketSort, 32, radius);
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args) {
        unit_test(1);
    }

}
