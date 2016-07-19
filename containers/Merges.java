package sprax.containers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import sprax.arrays.Arrays1d;
import sprax.arrays.ArrayIterInt;
import sprax.sprout.Sx;
import sprax.test.Sz;

class CompareByMapVal<M extends Map<K, V>, K, V extends Comparable<V>> implements Comparator<K>
{
    Map<K, V> mMap;
    
    CompareByMapVal(M map)
    {
        mMap = map;
    }
    
    @Override
    public int compare(K key0, K key1)
    {
        V val0 = mMap.get(key0);
        V val1 = mMap.get(key1);
        if (val0 != null && val1 != null)
            return val0.compareTo(val1);
        return 0;
    }
}

public class Merges
{
    static int sDbg = 2; // TODO: remove?
                         
    static int test_mergeMapsKeysSortedByValue()
    {
        HashMap<Long, Long> hmA = new HashMap<Long, Long>(5);
        hmA.put(12L, 21L);
        hmA.put(13L, 31L);
        hmA.put(14L, 41L);
        hmA.put(18L, 81L);
        hmA.put(17L, 71L);
        HashMap<Long, Long> hmB = new HashMap<Long, Long>(7);
        hmB.put(27L, 72L);
        hmB.put(28L, 82L);
        hmB.put(29L, 92L);
        hmB.put(26L, 62L);
        hmB.put(25L, 52L);
        hmB.put(24L, 41L);
        hmB.put(12L, 32L);
        Long ans[] = mergeMapsKeysSortedByValue(hmA, hmB);
        Sx.putsArray("Keys: ", ans);
        return 0;
    }
    
    /**
     * You have two hashmaps HM1 and HM2 where key = Id(long) value = timestamp. You need to give a
     * program to return a list of Ids combined from both the hashmaps such that they are sorted as
     * per their timestamps
     * 
     * sprax sez: sort the key sets of each HM separately, using timestamp comparator. Then merge
     * sort these two keys sets in one pass. There will be no question of which HM to use for each
     * key, since they are only merged after the lookups, with a running min value kept in a temp
     * var.
     * 
     * @param <T>
     * @param <U>
     */
    
    static Long[] mergeMapsKeysSortedByValue(HashMap<Long, Long> hmA, HashMap<Long, Long> hmB)
    {
        if (hmA == null || hmB == null)
            return null;
        
        CompareByMapVal<HashMap<Long, Long>, Long, Long> cmvA = new
                CompareByMapVal<HashMap<Long, Long>, Long, Long>(hmA);
        CompareByMapVal<HashMap<Long, Long>, Long, Long> cmvB = new
                CompareByMapVal<HashMap<Long, Long>, Long, Long>(hmB);
        
        Set<Long> keysA = hmA.keySet();
        Long longKeysA[] = new Long[keysA.size()];
        keysA.toArray(longKeysA);
        Arrays.sort(longKeysA, cmvA);
        
        Set<Long> keysB = hmB.keySet();
        Long longKeysB[] = new Long[keysB.size()];
        keysB.toArray(longKeysB);
        Arrays.sort(longKeysB, cmvB);
        
        Long mergedKeys[] = new Long[longKeysA.length + longKeysB.length];
        
        int iM = 0, iA = 0, iB = 0;
        Sx.dbg(sDbg, "Vals: "); // TODO: remove debugging
        while (iA < longKeysA.length && iB < longKeysB.length) {
            if (hmA.get(longKeysA[iA]) <= hmB.get(longKeysB[iB])) {
                Sx.dbg(sDbg, " " + hmA.get(longKeysA[iA]));
                mergedKeys[iM++] = longKeysA[iA++];
            } else {
                Sx.dbg(sDbg, " " + hmB.get(longKeysB[iB]));
                mergedKeys[iM++] = longKeysB[iB++];
            }
        }
        while (iA < longKeysA.length) {
            Sx.dbg(sDbg, " " + hmA.get(longKeysA[iA]));
            mergedKeys[iM++] = longKeysA[iA++];
        }
        while (iB < longKeysB.length) {
            Sx.dbg(sDbg, " " + hmB.get(longKeysB[iB]));
            mergedKeys[iM++] = longKeysB[iB++];
        }
        Sx.debug(sDbg);
        return mergedKeys;
    }
    
    /** merge two arrays of type int[] and return the result */
    public static int[] mergeArrays(final int A[], final int B[])
    {
        if (A == null && B == null)
            return null;
        if (A == null || A.length == 0)
            return B;
        if (B == null || B.length == 0)
            return A;
        
        int Z[] = new int[A.length + B.length];
        return mergeArraysNiece(Z, A, B, 0, 0, 0);
    }
    
    /**
     * merge arrays A and B, starting from indices jA and jB, into the destination array Z starting
     * at jZ. There is no error checking on the inputs; they should have already been checked for
     * null, emptiness, etc. This is a helper method, related to a public mergeArrays method as its
     * "niece", where NIECE = No Input Error Checking or Emptiness allowed.
     * 
     * @param Z Destination array, already allocated
     * @param A First source array
     * @param B Second source array
     * @param jZ Starting index. Assume Z[k] is already initialized for all k < jZ.
     * @param jA Starting index of first source. Assume A[k] was already merged into Z for k < jA.
     * @param jB Starting index of second source.
     * @return Z merger of A and B, sorted if A and B were already sorted.
     */
    protected static int[] mergeArraysNiece(int Z[], final int A[], final int B[], int jZ, int jA,
            int jB)
    {
        while (true) {
            while (A[jA] <= B[jB]) {
                Z[jZ++] = A[jA++];
                if (jA == A.length) {
                    while (jB < B.length)
                        Z[jZ++] = B[jB++];
                    return Z;
                }
            }
            while (A[jA] > B[jB]) {
                Z[jZ++] = B[jB++];
                if (jB == B.length) {
                    while (jA < A.length)
                        Z[jZ++] = A[jA++];
                    return Z;
                }
            }
        }
    }
    
    /** merge three arrays of type int[] and return the result */
    public static int[] mergeArrays(final int A[], final int B[], final int C[])
    {
        if (A == null && B == null && C == null)
            return null;
        if (A == null || A.length == 0)
            return mergeArrays(B, C);
        if (B == null || B.length == 0)
            return mergeArrays(A, C);
        if (C == null || C.length == 0)
            return mergeArrays(A, B);
        
        int Z[] = new int[A.length + B.length + C.length];
        return mergeArraysNiece(Z, A, B, C, 0, 0, 0, 0);
    }
    
    /**
     * merge three arrays A, B, and C, starting from indices jA, jB, and jC, into the destination
     * array Z starting at jZ. There is no error checking on the inputs; they should have already
     * been checked for null, emptiness, etc. NIECE = No Input Error Checking or Emptiness allowed.
     */
    public static int[] mergeArraysNiece(int Z[], final int A[], final int B[], final int C[],
            int jZ, int jA, int jB, int jC)
    {
        while (true) {
            while (A[jA] <= B[jB] && A[jA] <= C[jC]) {
                Z[jZ++] = A[jA++];
                if (jA == A.length) {
                    return mergeArraysNiece(Z, B, C, jZ, jB, jC);
                }
            }
            while (A[jA] > B[jB] && B[jB] <= C[jC]) {
                Z[jZ++] = B[jB++];
                if (jB == B.length) {
                    return mergeArraysNiece(Z, A, C, jZ, jA, jC);
                }
            }
            while (C[jC] <= B[jB] && C[jC] <= A[jA]) {
                Z[jZ++] = C[jC++];
                if (jC == C.length) {
                    return mergeArraysNiece(Z, A, B, jZ, jA, jB);
                }
            }
        }
    }
    
    /** merge four arrays of type int[] and return the result */
    public static int[] mergeArrays(final int A[], final int B[], final int C[], final int D[])
    {
        if (A == null && B == null && C == null && D == null)
            return null;
        if (A == null || A.length == 0)
            return mergeArrays(B, C, D);
        if (B == null || B.length == 0)
            return mergeArrays(A, C, D);
        if (C == null || C.length == 0)
            return mergeArrays(A, B, D);
        if (D == null || D.length == 0)
            return mergeArrays(A, B, C);
        
        int X[] = new int[A.length + B.length];
        int Y[] = new int[C.length + D.length];
        int Z[] = new int[X.length + Y.length];
        mergeArraysNiece(X, A, B, 0, 0, 0);
        mergeArraysNiece(Y, C, D, 0, 0, 0);
        return mergeArraysNiece(Z, X, Y, 0, 0, 0);
    }
    
    /** merge five arrays of type int[] and return the result */
    public static int[] mergeArrays(final int A[], final int B[], final int C[], final int D[],
            final int E[])
    {
        if (A == null && B == null && C == null && D == null && E == null)
            return null;
        
        // next 2 lines could run in parallel, 3rd would have to wait
        int X[] = mergeArrays(A, B, C);
        int Y[] = mergeArrays(D, E);
        return mergeArrays(X, Y);
    }
    
    /** merge an array of arrays of type int[] and return the result */
    public static int[] mergeArrays(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return null;
        
        switch (AA.length) {
        case 1:
            return AA[0];
        case 2:
            return mergeArrays(AA[0], AA[1]);
        case 3:
            return mergeArrays(AA[0], AA[1], AA[2]);
        case 4:
            return mergeArrays(AA[0], AA[1], AA[2], AA[3]);
        case 5:
            return mergeArrays(AA[0], AA[1], AA[2], AA[3], AA[4]);
        default:
            int len = AA.length,
            half = len / 2,
            j,
            k;
            int A1[][] = new int[half][];
            int A2[][] = new int[len - half][];
            for (j = 0; j < half; j++)
                A1[j] = AA[j];
            for (k = 0; j < len; j++, k++)
                A2[k] = AA[j];
            // The next 2 lines could run in parallel, 3rd would have to wait.
            int M1[] = mergeArrays(A1);
            int M2[] = mergeArrays(A2);
            return mergeArrays(M1, M2);
        }
    }
    
    /**
     * Uses a PriorityQueue (like a min heap) to merge an array of arrays of type int[] and returns
     * the result in a new array If the rows are pre-sorted, the result will be entirely sorted. If
     * there are M rows and N total elements, the complexity is bounded by O(N * log M) time, O(log
     * M) temporary space. TODO: implement a stream or iterator class that does not store the result
     * in a new array, but just returns next(), etc.
     */
    public static int[] mergeArraysPq(final int AA[][])
    {
        if (AA == null || AA.length == 0)
            return null;
        
        int size = 0;
        for (int row = 0; row < AA.length; row++) {
            if (AA[row] == null)
                throw new IllegalArgumentException("mergeArraysPQ: row " + row + " is null");
            size += AA[row].length;
        }
        // TODO: just call mergeArraysPqNiece!
        ArrayIterInt merged = new ArrayIterInt(size);
        PriorityQueue<ArrayIterInt> its = new PriorityQueue<ArrayIterInt>();
        for (int[] A : AA) {
            its.add(new ArrayIterInt(A));
        }
        while (!its.isEmpty()) {
            ArrayIterInt ai = its.remove();
            if (its.isEmpty()) {
                // S.debugSubArray(sDbg, "queue empty -- last i/o\n Adding:", ai.mArray, ai.mIndex,
                // ai.mSize);
                merged.appendRemoveAll(ai);
                break;// TODO just return finished sort ?
            } else {
                // S.dbg(sDbg, " Adding: " + ai.head());
                merged.appendRemove(ai);
            }
            int bound = its.peek().head();
            while (!ai.isEmpty()) {
                if (ai.head() <= bound) {
                    // S.dbg(sDbg, " " + ai.head());
                    merged.appendRemove(ai);
                } else {
                    its.add(ai);
                    break;
                }
            }
            // S.debug(sDbg);
        }
        // S.debug(sDbg);
        
        return merged.getArray();
    }
    
    /**
     * Uses a PriorityQueue (like a min heap) to merge an array of arrays of type int[] and returns
     * the result in a new array If the rows are pre-sorted, the result will be entirely sorted. If
     * there are M rows and N total elements, the complexity is bounded by O(N * log M) time, O(log
     * M) temporary space. TODO: implement a stream or iterator class that does not store the result
     * in a new array, but just returns next(), etc.
     */
    public static int[] mergeArraysPqNiece(final int AA[][], int size)
    {
        ArrayIterInt merged = new ArrayIterInt(size);
        PriorityQueue<ArrayIterInt> its = new PriorityQueue<ArrayIterInt>();
        for (int[] A : AA) {
            its.add(new ArrayIterInt(A));
        }
        while (!its.isEmpty()) {
            ArrayIterInt ai = its.remove();
            if (its.isEmpty()) {
                Sx.debugSubArray(sDbg, "queue empty -- last i/o\n Adding:", ai.getArray(),
                        ai.getIndex(), ai.getSize());
                merged.appendRemoveAll(ai);
                return merged.getArray();
            }
            int bound = its.peek().head();
            do {
                Sx.dbg(sDbg, " " + ai.head());
                merged.appendRemove(ai);
                if (ai.isEmpty()) // check for empty...
                    break;
                if (ai.head() > bound) { // ...before checking value
                    its.add(ai);
                    break;
                }
            } while (true);
            Sx.debug(sDbg);
        }
        Sx.debug(sDbg);
        
        return merged.getArray();
    }
    
    /**
     * Uses a PriorityQueue (like a min heap) to merge an array of arrays of type int[] up to the
     * specified size, and returns the result in a new array If the rows are pre-sorted, the result
     * will be entirely sorted. If there are M rows and N total elements, the complexity is bounded
     * by O(N * log M) time, O(log M) temporary space. TODO: implement a stream or iterator class
     * that does not store the result in a new array, but just returns next(), etc.
     */
    public static int[] mergeArraysPq(final int AA[][], int outputSize)
    {
        if (AA == null || AA.length == 0)
            return null;
        if (outputSize < 1)
            throw new IllegalArgumentException("mergeArraysPq: non-positive size: " + outputSize);
        
        int inputSize = 0;
        for (int row = 0; row < AA.length; row++) {
            if (AA[row] == null)
                throw new IllegalArgumentException("mergeArraysPQ: row " + row + " is null");
            inputSize += AA[row].length;
        }
        if (outputSize >= inputSize) { // no need to check size,
            return mergeArraysPqNiece(AA, inputSize); // so used the faster version
        }
        ArrayIterInt merged = new ArrayIterInt(outputSize);
        PriorityQueue<ArrayIterInt> its = new PriorityQueue<ArrayIterInt>();
        for (int A[] : AA) {
            its.add(new ArrayIterInt(A));
        }
        while (!its.isEmpty()) {
            ArrayIterInt ai = its.remove();
            if (its.isEmpty()) {
                merged.appendSafe(ai, outputSize);
                return merged.getArray();
            }
            int bound = its.peek().head();
            do {
                if (merged.append(ai.remove()) == outputSize)
                    return merged.getArray();
                if (ai.isEmpty())
                    break;
                if (ai.head() > bound) {
                    its.add(ai);
                    break;
                }
            } while (true);
        }
        ;
        return merged.getArray();
    }
    
    /**
     * mergeArraysPr(64, 131072) minVal 1000 maxInc 19 times: 672 594 625 609 609 mergeArraysPq(64,
     * 131072) minVal 1000 maxInc 19 times: 2500 2609 2438 2438 2469
     * 
     * @param nRows
     * @param nCols
     * @return
     */
    public static int test_timeNwayMergesRowColSortedArray(int nRows, int nCols)
    {
        int minVal = 1000;
        int maxInc = 19;
        long seed = System.currentTimeMillis();
        int AA[][] = Arrays1d.makeRandomRowColSortedArray(nRows, nCols, minVal, maxInc, seed);
        
        final int times = 5;
        long pr[] = new long[times], startTime;
        long pq[] = new long[times], stopTime;
        
        int X[] = { 0 };
        for (int j = 0; j < times; j++) {
            startTime = System.currentTimeMillis();
            X = mergeArrays(AA);
            stopTime = System.currentTimeMillis();
            pr[j] = stopTime - startTime;
            
            startTime = System.currentTimeMillis();
            X = mergeArraysPq(AA);
            stopTime = System.currentTimeMillis();
            pq[j] = stopTime - startTime;
        }
        System.out.format("\n mergeArraysPr(%d, %d) length %d minVal %d  maxInc %d times:   ", nRows, nCols,
                X.length, minVal, maxInc);
        for (int j = 0; j < times; j++) {
            System.out.format("%7d ", pr[j]);
        }
        System.out.format("\n mergeArraysPq(%d, %d) minVal %d  maxInc %d times:   ", nRows, nCols,
                minVal, maxInc);
        for (int j = 0; j < times; j++) {
            System.out.format("%7d ", pq[j]);
        }
        
        // S.putsArray(AA);
        return 0;
    }
    
    /**
     * Doing four 3-ways is faster than doing eight 2-ways to get the same result. mergeArrays3(9,
     * 1048576) minVal 1000 maxInc 19; times: 422 391 390 391 391 mergeArrays2(9, 1048576) minVal
     * 1000 maxInc 19; times: 500 453 438 437 437
     * 
     * @param nRows
     * @param nCols
     * @return
     */
    public static int test_time2vs3WayMergesRowColSortedArray(int nRowsOver3, int nCols)
    {
        int nRows = nRowsOver3 * 9;
        int minVal = 1000;
        int maxInc = 19;
        long seed = System.currentTimeMillis();
        int AA[][] = Arrays1d.makeRandomRowColSortedArray(nRows, nCols, minVal, maxInc, seed);
        
        final int times = 5;
        long pr[] = new long[times], startTime;
        long pq[] = new long[times], stopTime;
        
        int A[], B[], C[], D[], X[] = null, Y[] = null;
        for (int j = 0; j < times; j++)
        {
            // 3-way
            startTime = System.currentTimeMillis();
            A = mergeArrays(AA[0], AA[1], AA[2]);
            B = mergeArrays(AA[3], AA[4], AA[5]);
            C = mergeArrays(AA[6], AA[7], AA[8]);
            X = mergeArrays(A, B, C);
            stopTime = System.currentTimeMillis();
            pr[j] = stopTime - startTime;
            int len3 = X.length;
            
            // 2-way
            startTime = System.currentTimeMillis();
            A = mergeArrays(AA[0], AA[1]);
            B = mergeArrays(AA[2], AA[3]);
            C = mergeArrays(AA[4], AA[5]);
            D = mergeArrays(AA[6], AA[7]);
            
            A = mergeArrays(A, B);
            C = mergeArrays(C, D);
            
            A = mergeArrays(A, AA[8]);
            Y = mergeArrays(A, C);
            stopTime = System.currentTimeMillis();
            pq[j] = stopTime - startTime;
            int len2 = Y.length;
            if (len2 != len3)
                Sx.puts("bust");
        }
        for (int j = 1; j < nCols; j++) {
            if (X[j] != Y[j])
                Sx.puts("BUSTED!");
            if (X[j] < X[j - 1])
                Sx.puts("BROKEN");
        }
        System.out.format("\n mergeArrays3(%d, %d) minVal %d  maxInc %d;   times:   ", nRows,
                nCols, minVal, maxInc);
        for (int j = 0; j < times; j++) {
            System.out.format("%7d ", pr[j]);
        }
        System.out.format("\n mergeArrays2(%d, %d) minVal %d  maxInc %d;   times:   ", nRows,
                nCols, minVal, maxInc);
        for (int j = 0; j < times; j++) {
            System.out.format("%7d ", pq[j]);
        }
        
        return 0;
    }
    
    public static int unit_test(int level)
    {
        String testName = Merges.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        int numWrong = 0;
        
        int AA[][] = // median is 13 at ordinal(3,3) -- not the first possible spot.
        { { 1, 2, 3, 4, 5 }
                , { 6, 7, 8, 9, 10 }
                , { 11, 12, 13, 34, 35 } // centroid position
                , { 41, 42, 43, 44, 45 }
                , { 51, 52, 53, 54, 55 }
        };
        int XY[][] =
        { { 1, 2, 6, 7, 15 }
                , { 3, 5, 8, 14, 16 }
                , { 4, 9, 13, 17, 22 }
                , { 10, 12, 18, 21, 23 }
                , { 11, 19, 20, 24, 25 }
        };
        int YX[][] =
        { { 1, 3, 6, 10, 15 }
                , { 2, 5, 9, 14, 19 }
                , { 4, 8, 13, 18, 22 }
                , { 7, 12, 17, 21, 24 }
                , { 11, 16, 20, 23, 25 }
        }; /*
            * ArrayList<Integer> al = new ArrayList<Integer>(6); Iterator<Integer> it =
            * al.iterator(); List<int []> list = Arrays.asList(AA[0]); Iterator iter =
            * list.iterator(); while (iter.hasNext()) S.puts("iter: " +
            * iter.next().getClass().getName());
            */
        Sx.puts("length AA: " + AA.length);
        mergeArraysPq(AA);
        mergeArraysPq(XY);
        mergeArraysPq(YX);
        
        Arrays1d.test_makeRandomRowColSortedArray();
        test_timeNwayMergesRowColSortedArray(64, 1 << 12);
        
        double dd = 1000;
        System.out.format("log10(%g) == %g\n", dd, Math.log10(dd));
        dd = 9999.0;
        System.out.format("log10(%g) == %g\n", dd, Math.log10(dd));
        dd = 9999.9;
        System.out.format("log10(%g) == %g\n", dd, Math.log10(dd));
        
        numWrong += test_mergeMapsKeysSortedByValue();
        
        if (level > 1)
            numWrong += test_time2vs3WayMergesRowColSortedArray(1, 1 << 20);
        
        Sx.puts();
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test(2);
    }
    
}
