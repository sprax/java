package sprax.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import sprax.Sx;

public class SubArrays 
{    
    // Kadane
    public static int maxContiguousSum(int iA[]) 
    {
        int maxSum = 0; 
        int maxNow = 0;
        for ( int ia : iA ) {
            maxNow = Math.max(0, maxNow+ia);
            maxSum = Math.max(maxNow, maxSum);
        }
        return maxSum;
    }
    
    
    public static int maxPositiveContiguousProduct(int iA[]) 
    {
        int maxPro = 0; 
        int maxNow = 1;
        for ( int ia : iA ) {
            maxNow = Math.max(1, maxNow * ia);
            maxPro = Math.max(maxNow, maxPro);
        }
        return maxPro;
    }  
    

    /////////////////////////////////// CONTIGUOUS SUBARRAYS ///////////////////////////////////////////
    /**
     * Naive algorithm: time = O(N^2), additional space = O(1)
     * @param arr Input array of pos and neg int's.
     * @param sum The number to which which the sub-array is to add up to. 
     * @return range = first and last index of the first sub-array adding
     * up to sum, where the first sub-array is the one that begins first,
     * not necessarily the one that ends first.  Thus in [2, 1, 0, -1, 2],
     * the first sub-array summing to 0 is [1, 0, -1] with first and last
     * indices <1,3>, and not [0] with indices <2, 2>.
     */
    public static int[] firstContiguousSumToN_naiveN2(final int arr[], int sum)
    {
        int range[] = {-1,-1};  // return values: invalid indices indicate failure
        
        for (int j = 0; j < arr.length; j++) {
            int partial = 0;
            for (int k = j; k < arr.length; k++) {
                partial += arr[k];
                if (partial == sum) {
                    range[0] = j;
                    range[1] = k;
                    return range;
                }
            }
        }
        return range;
    }
    
    /**
     * 2-pass algorithm: time = O(N), additional space = O(N)
     * @param arr Input array of pos and neg int's.
     * @param sum The number to which which the subarray is to add up to. 
     * @return range = first and last index of the first sub-array adding
     * up to sum, where the first sub-array is the one that begins first,
     * not necessarily the one that ends first.  Thus in [2, 1, 0, -1, 2],
     * the first sub-array summing to 0 is [1, 0, -1] with first and last
     * indices <1,3>, and not [0] with indices <2, 2>.
     */
    public static int[] firstContiguousSumToN(final int arr[], int sum)
    {
        int range[] = {-1,-1};  // return values: invalid indices indicate failure
        Map<Integer, Integer> sum2idx = new HashMap<Integer, Integer>();
        sum2idx.put(0, -1);
        for (int partSum = 0, j = 0; j < arr.length; j++) {
            // compute the next partial sum up to and including A[j]
            partSum += arr[j];
            // If there is a previously stored index i s.t. i < j and pS[j] - pS[i] == sum, 
            // return <i+1,j>
            if ( sum2idx.containsKey(partSum - sum) ) {
                // The starting index of the sub-array is 1 after the end 
                // of the partial array whose partial sum we subtracted.
                range[0] = sum2idx.get(partSum - sum) + 1;
                range[1] = j;
                break;
            }
            // Store only the first occurrence of each partial sum
            if ( ! sum2idx.containsKey(partSum))
                sum2idx.put(partSum, j);    // j == index of last array entry in this partial sum
        }
        return range;
    }
    
    public static int test_maxContiguousSubArrays() 
    {
        int iA[] = { 1, -2, 3, -5, 0, -4, 5, -4, 7, -9, 2, 1, 0 };
        int maxSum = maxContiguousSum(iA);
        Sx.putsArray(iA, " => max contiguous sum " + maxSum);
        int maxPro = maxPositiveContiguousProduct(iA);
        Sx.putsArray(iA, " => max positive contiguous entries product " + maxPro);
        int maxMin[] = maxMinContiguousProduct(iA);
        Sx.putsArray(iA, " => max & min contiguous product " + maxMin[0] + " and " + maxMin[1]);
        return 0;
    }
    
    public static int test_sumContiguousSubArrays() 
    {
        //int iB[] = { 1, 2, 0, -2, 3, -4, 0 };
        int iB[] = { -1, 2, 1, 2, -3, 0, 4 };
        for (int sum = 0; sum < 10; sum += 3) {
            Sx.puts();
            int naive[] = firstContiguousSumToN_naiveN2(iB, sum);
            Sx.putsArray(iB, " => 1st contiguous sum to " + sum + ": [" + naive[0] + ", " + naive[1] + "] (naive algo)");
            int range[] = firstContiguousSumToN(iB, sum);
            Sx.putsArray(iB, " => 1st contiguous sum to " + sum + ": [" + range[0] + ", " + range[1] + "]");
        }
        return 0;
    }
    
    
    static int max_subarray(int A[], int countA)
    {
        // This version of the algorithm assumes that at least one element is >= 0, OR, 
        // that we can take a zero-length sub-array with sum 0 as the max sub-array.  
        // Either way, we don't need to keep any subarray (even of length 1) whose sum is < 0.
        int max_so_far = 0;
        int max_ending_here = 0;
        for (int j = 0; j < countA; j++) {
            // If the running sum up to and including this element is < 0, drop it and take 0 instead; 
            // it could only diminish the sum we're after.
            max_ending_here = Math.max(0, max_ending_here + A[j]);
            max_so_far = Math.max(max_so_far, max_ending_here);
        }
        return max_so_far;
    }
    
    static int Kadane(int arr[], int arrLen, int firstAndLastIdx[])
    {
        firstAndLastIdx[0] = firstAndLastIdx[1] = 0;
        int maxSum = 1 << 31;    // INT_MIN
        int tmpSum = 0;
        for(int tmpFirst = 0, tmpLast = 0; tmpLast < arrLen ; tmpLast++) 
        {
            tmpSum += arr[tmpLast];
            if (maxSum < tmpSum) {
                maxSum = tmpSum;
                firstAndLastIdx[0] = tmpFirst;
                firstAndLastIdx[1] = tmpLast;
            }
            if (tmpSum < 0) {
                tmpSum = 0;
                tmpFirst = tmpLast + 1;
            }
        }
        return maxSum;
    }
    
    static int test_Kadane()
    {
        int A[] = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        int countA = A.length;
        
        int maxSum = max_subarray(A, countA);
        
        int firstAndLastIdx[] = new int[2];
        int maxContSum = Kadane(A, countA, firstAndLastIdx);
        int firstIdx = firstAndLastIdx[0];
        int lastIdx  = firstAndLastIdx[1];
        int stat =  -1;
        if (maxContSum == 6 && firstIdx == 3 && lastIdx == 6) 
            stat = 0;
        
        Sx.putsArray(A, " gives max subArray:");
        Sx.printSubArray(A, firstIdx, lastIdx);
        Sx.printf(" <start, last, sum> = <%2d  %2d  %2d>\n", firstIdx, lastIdx, maxContSum);  
        return stat;
    }

    public static int[] maxMinContiguousProduct(int iA[]) // TODO: seems to work -- test it more!
    {
        int maxProduct = 0, maxPositive = 0; 
        int minProduct = 0, minNegative = 0;
        int minTemp;
        for ( int ia : iA ) {
            if (ia > 0) {
                maxPositive = Math.max(ia, ia * maxPositive);
                if (minNegative < 0)
                    minNegative = ia * minNegative;
            } else if (ia < 0) {
                minTemp = Math.min(ia, ia * maxPositive);
                if (maxPositive > 0)
                    maxPositive = ia * minNegative;
                minNegative = minTemp;
            } else {                  // ia == 0
                maxPositive = 0;
                minNegative = 0;
                continue;
            }
            maxProduct  = Math.max(maxPositive, maxProduct);
            minProduct  = Math.min(minNegative, minProduct);
        }
        int ret[] = { maxProduct, minProduct };
        return ret;
    }  
    
    public static double[] maxAndMinContiguousProduct(double DD[]) // TODO: does not work?
    {
        double maxProduct  = 0.0, minProduct  = 0.0; 
        double maxPositive = 0.0, minNegative = 0.0;
        double maxTemp;
        for ( double dd : DD ) {
            if (dd > 0) {
                if (maxPositive > 0)
                    maxPositive = Math.max( 1, maxPositive * dd);
                minNegative = Math.min(-1, minNegative * dd);
            } else if (dd < 0) {
                maxTemp     = Math.max( 1, minNegative * dd);
                minNegative = Math.min(-1, maxPositive * dd);
                maxPositive = maxTemp;
            } else { // dd == 0
                maxPositive =  0;
                minNegative =  0;
                continue;
            }
            maxProduct  = Math.max(maxPositive, maxProduct);
            minProduct  = Math.min(minNegative, minProduct);
        }
        double ret[] = { maxProduct, minProduct };
        return ret;
    }
    
    
    public static int test_contiguous()
    {
        int iA[] = { 1, -2, 3, -1, 0, -4, 5, -4, 2, -1, 2, 1, 0 };
        int sum = 0;
        int interval[] = firstContiguousSumToN(iA, 0);
        Sx.putsArray(iA, " => 1st contiguous sum to " + sum + ": [" + interval[0]
                + ", " + interval[1] + "]");
        return 0;
    }
    
    
    
    public static int unit_test(int level) 
    {
        Sx.puts(ArrayAlgo.class.getName() + ".unit_test");  
        int stat = 0;
        
        Integer[] duh = { 1, 2, 3};
        ArrayList<Integer> dork = new ArrayList<Integer>();
        dork.addAll(Arrays.asList(duh));        HashSet<Integer> set = new HashSet<Integer>();
        set.add(1);
        HashSet<Integer> hmm = new HashSet<Integer>(set); 
        
        stat += test_sumContiguousSubArrays();
        stat += test_maxContiguousSubArrays();
        // TODO: more?
        stat += test_Kadane();
        return stat;
    }
    
    public static void main(String[] args)
    {
        unit_test(2);
    }
    
    
    
    
    /*
     * 
you have an array of integers, find the longest
subarray which consists of numbers that can be arranged in a sequence, e.g.:

a = {4,5,1,5,7,4,3,6,3,1,9}
max subarray = {5,7,4,3,6}
10
Country: -
Tags: Microsoft » Algorithm
Question #11256218 (Report Dup) | Edit | History


0
of 0 vote
sumit.gupta23121988 on October 19, 2011 |Edit | Edit

can u give the solution of this
Reply to Comment
0
of 0 vote
Aditya H on October 19, 2011 |Edit | Edit

What do you mean by numbers that can be arranged in a sequence, all numbers can be arranged in a sequence
Anonymous on October 19, 2011 |Edit | Edit

consecutive elements sequence... this is a google interview question... this was discussed earlier.
Anonymous on October 19, 2011 |Edit | Edit

can you please provide discussion link?
Anonymous on October 19, 2011 |Edit | Edit

id=9783960
Anonymous on October 20, 2011 |Edit | Edit

I think the Google interview asked for a subset of the array, this question asks for a subarray. The obvious solution is checking all possible subarrays, but there must be a better solution...
asm on October 20, 2011 |Edit | Edit

'id=9783960' it's a different problem.
Here it is asked to find a SUBARRAY (contiguous part) that can be transformed to a sequence of consecutive integers, i.e.:

a = {4,5,1,5,7,4,3,6,3,1,9}
the subarray is: {5,7,4,3,6} because these numbers can be arranged as: {3,4,5,6,7}
Anonymous on October 20, 2011 |Edit | Edit

ok, here is an algorithm I had in mind:

The idea is if the numbers of a subarray can be arranged in consecutive way that their sum can be evaluated as follows:
max*(max+1)/2 - min*(min-1)/2
where 'max' and 'min' are the maximal and minimal numbers in a subarray.

So the algorithm is go through the array updating 'max' and 'min', as well as the current sum.
In each step we check if computed sum equals to the value returned by the formula above.
If so, we have found a subarray with the given properties.
We keep the longest one seen so far.

Since we have to consider all possible array suffixes, the complexity is O(n^2)

     */
    
    
    
    
}
