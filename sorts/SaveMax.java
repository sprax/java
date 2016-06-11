/**
 * Save the maximal N entries from a generic 
 * stream or set of objects.
 */
package sprax.sorts;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import sprax.arrays.ArrayAlgo;
import sprax.arrays.ArrayIter;
import sprax.heaps.MaxHeap;
import sprax.heaps.MinHeap;
import sprax.shuffles.Shuffler;
import sprax.sprout.Sx;

public class SaveMax
{
    /**
     * If the specified object is greater than the least entry saved
     * in the first numToSave entries the array, insert it in its
     * proper place.
     * Assumes that the first numToSave entries are already sorted in
     * ascending order. (This ensures that the least entry is the
     * one that gets replaced.)
     * 
     * @param <T> type
     * @param maxObj array of comparable objects
     * @param obj insertion candidate
     * @param numToSave number of objects to save in maximal sub-array
     */
    public static <T extends Comparable<T>> void saveMaxSort(T maxObj[], T obj, int numToSave)
    {
        if (obj.compareTo(maxObj[0]) > 0) {
            maxObj[0] = obj;
            Arrays.sort(maxObj, 0, numToSave);
        }
    }
    
    public static <T extends Comparable<T>> void saveMaxSort(T maxObj[], T obj) {
        saveMaxSort(maxObj, obj, maxObj.length);
    }
    
    /**
     * If the specified object is lesser than the greatest entry saved
     * in the first numToSave entries the array, insert it in its
     * proper place.
     * Assumes that the first numToSave entries are already sorted in
     * ascending order. (This ensures that the least entry is the
     * one that gets replaced.)
     * 
     * @param <T> type
     * @param maxObj array of comparable objects
     * @param obj insertion candidate
     * @param numToSave number of objects to save in minimal sub-array
     */
    public static <T extends Comparable<T>> void saveMinSort(T maxObj[], T obj, int numToSave)
    {
        if (obj.compareTo(maxObj[numToSave - 1]) < 0) {
            maxObj[numToSave - 1] = obj;
            Arrays.sort(maxObj, 0, numToSave);
        }
    }
    
    public static <T extends Comparable<T>> void saveMinSort(T maxObj[], T obj) {
        saveMinSort(maxObj, obj, maxObj.length);
    }
    
    /**
     * Maintains a sorted subset of maximal objects.
     * The maximal, most save-worthy element is stored
     * with index 0; The minimal, least save-worthy elements
     * stored with the greatest index.
     * If the array iterator is already full, compare the new
     * element to the last entry (the tail), and a replace the
     * tail with this new element only if it sorts prior to the tail.
     * 
     * Otherwise, just append the new
     * element, and do not sort. This means that we do NOT
     * sort the very first time the underlying array becomes
     * full. That would require an extra comparison. Instead,
     * we count on the results being sorted after the saving
     * is done, whether the array is full or not.
     * 
     * @param <T>
     * @param ai ArrayIter
     * @param obj
     */
    public static int  sCountNotFull = 0;	// FIXME
    private static int sCountIsFull  = 0;
    
    public static <T extends Comparable<T>> void saveMaxSort(ArrayIter<T> ai, T obj)
    {
        if (ai.isFull()) {
            if (obj.compareTo(ai.head()) > 0) {
                ai.head(obj);
            } else {
                return;
            }
        } else {
            ai.append(obj);
        }
        ai.sort();
    }
    
    public static <T extends Comparable<T>> void saveMinSort(ArrayIter<T> ai, T obj)
    {
        if (ai.isFull()) {
            if (obj.compareTo(ai.tail()) < 0) {
                ai.tail(obj);
            } else {
                return;
            }
        } else {
            ai.append(obj);
        }
        ai.sort();
    }
    
    ///////////////////////////////////////////////////////////////////////////////
    
    /** 
   * 
   */
    public static <T extends Comparable<T>> void saveMax(MinHeap<T> minHeap, T obj)
    {
        if (!minHeap.isHeap())
            minHeap.heapify();
        minHeap.add(obj);
    }
    
    public static <T extends Comparable<T>> void saveMin(MaxHeap<T> maxHeap, T obj)
    {
        if (!maxHeap.isHeap())
            maxHeap.heapify();
        maxHeap.add(obj);
    }
    
    ///////////////////////////////////////////////////////////////////////////////  
    
    public static <T extends Comparable<T>> void saveMaxBottomUp(T maxObj[], int maxNumNodes, T obj)
    {
        if (obj.compareTo(maxObj[3]) > 0) {
            if (obj.compareTo(maxObj[1]) > 0) {
                maxObj[3] = maxObj[2];
                maxObj[2] = maxObj[1];
                if (obj.compareTo(maxObj[0]) > 0) {
                    maxObj[1] = maxObj[0];
                    maxObj[0] = obj;
                } else {
                    maxObj[1] = obj;
                }
            } else {
                if (obj.compareTo(maxObj[2]) > 0) {
                    maxObj[3] = maxObj[2];
                    maxObj[2] = obj;
                } else {
                    maxObj[3] = obj;
                }
            }
        }
    }
    
    /**
     * Test obj against the 4 objects already stored maxObj[0...3] and retain
     * the maximal 4, displacing the least of the stored objects as needed.
     * This method is "bottom up" because it starts by testing obj against
     * the least of the previous maximal entries. If the dats is tested in
     * random order, this first comparison will typically fail and the
     * method will return immediately.
     * Worst case: 3 comparisons and 4 assignments.
     * Most likely case, assuming uniform distribution and large N: 1 comparison,
     * 0 assignments. As N increases, the average case approaches the most
     * likely case asymptotically.
     * All objects must be non-null and mutually comparable;
     * no error checking is done.
     * 
     * @param <T>
     * @param maxObj Array of at least 4 objects mutually comparable with obj
     * @param obj
     */
    public static <T extends Comparable<T>> void saveMax4BottomUp(T maxObj[], T obj)
    {
        if (obj.compareTo(maxObj[3]) > 0) {
            if (obj.compareTo(maxObj[1]) > 0) {
                maxObj[3] = maxObj[2];
                maxObj[2] = maxObj[1];
                if (obj.compareTo(maxObj[0]) > 0) {
                    maxObj[1] = maxObj[0];
                    maxObj[0] = obj;
                } else {
                    maxObj[1] = obj;
                }
            } else {
                if (obj.compareTo(maxObj[2]) > 0) {
                    maxObj[3] = maxObj[2];
                    maxObj[2] = obj;
                } else {
                    maxObj[3] = obj;
                }
            }
        }
    }
    
    /**
     * Test obj against the 4 objects already stored maxObj[0...3] and retain
     * the maximal 4, displacing the least of the stored objects as needed.
     * Worst case: 3 comparisons and 4 assignments.
     * Most likely case, assuming uniform distribution and large N: 1 comparison,
     * 0 assignments. As N increases, the average case approaches the most
     * likely case asymptotically.
     * All objects are assumed to be non-null or at least comparable via the
     * specified comparator; no error checking is done.
     * 
     * @see saveMax4TopDown
     * @param <T> Type sortable by cmp
     * @param maxObj Array of at leat 4 objects of type T
     * @param obj Test object of type T
     * @param cmp Comparator<T>
     */
    public static <T> void saveMax4BottomUp(T maxObj[], T obj, Comparator<T> cmp)
    {
        if (cmp.compare(maxObj[3], obj) < 0) {
            if (cmp.compare(maxObj[1], obj) < 0) {
                maxObj[3] = maxObj[2];
                maxObj[2] = maxObj[1];
                if (cmp.compare(maxObj[0], obj) < 0) {
                    maxObj[1] = maxObj[0];
                    maxObj[0] = obj;
                } else {
                    maxObj[1] = obj;
                }
            } else {
                if (cmp.compare(maxObj[2], obj) < 0) {
                    maxObj[3] = maxObj[2];
                    maxObj[2] = obj;
                } else {
                    maxObj[3] = obj;
                }
            }
        }
    }
    
    /**
     * Test obj against the 4 objects already stored maxObj[0...3] and retain
     * the maximal 4, displacing the least of the stored objects as needed.
     * Worst case: 2 comparisons and 4 assignments.
     * Most likely case, assuming uniform distribution and large N: 2 comparison,
     * 0 assignments. As N increases, the average case approaches the most
     * likely case asymptotically.
     * All objects are assumed to be non-null or at least comparable via the
     * specified comparator; no error checking is done.\
     * 
     * @see saveMax4BottomUp
     * @param <T> Type sortable by cmp
     * @param maxObj Array of at leat 4 objects of type T
     * @param obj Test object of type T
     * @param cmp Comparator<T>
     */
    public static <T> void saveMax4TopDown(T maxObj[], T obj, Comparator<T> cmp)
    {
        if (cmp.compare(maxObj[1], obj) < 0) {
            maxObj[3] = maxObj[2];
            maxObj[2] = maxObj[1];
            if (cmp.compare(maxObj[0], obj) < 0) {
                maxObj[1] = maxObj[0];
                maxObj[0] = obj;
            } else {
                maxObj[1] = obj;
            }
        } else {
            if (cmp.compare(maxObj[3], obj) < 0) {
                if (cmp.compare(maxObj[2], obj) < 0) {
                    maxObj[3] = maxObj[2];
                    maxObj[2] = obj;
                } else {
                    maxObj[3] = obj;
                }
            }
        }
    }
    
    /**
     * Save maximal 3. starting close to the top.
     * Worst case: 2 comparisons, 3 assignments.
     * Asymptotic: 2 comparisons, 0 assignments.
     * 
     * @param <T>
     * @param maxObj
     * @param obj
     * @param cmp
     */
    public static <T> void saveMax3TopDown(T maxObj[], T obj, Comparator<T> cmp)
    {
        if (cmp.compare(maxObj[1], obj) < 0) {
            maxObj[2] = maxObj[1];
            if (cmp.compare(maxObj[0], obj) < 0) {
                maxObj[1] = maxObj[0];
                maxObj[0] = obj;
            } else {
                maxObj[1] = obj;
            }
        } else if (cmp.compare(maxObj[2], obj) < 0) {
            maxObj[2] = obj;
        }
    }
    
    /**
     * Save maximal 3. starting close to the top.
     * Worst case: 3 comparisons, 3 assignments.
     * Asymptotic: 1 comparisons, 0 assignments.
     * 
     * @param <T>
     * @param maxObj
     * @param obj
     * @param cmp
     */
    public static <T> void saveMax3BottomUp(T maxObj[], T obj, Comparator<T> cmp)
    {
        if (cmp.compare(maxObj[2], obj) < 0) {
            if (cmp.compare(maxObj[0], obj) < 0) {
                maxObj[2] = maxObj[1];
                maxObj[1] = maxObj[0];
                maxObj[0] = obj;
            } else if (cmp.compare(maxObj[1], obj) < 0) {
                maxObj[2] = maxObj[1];
                maxObj[1] = obj;
            } else {
                maxObj[2] = obj;
            }
        }
    }
    
    /**
     * Save maximal 3. starting close to the top.
     * Worst case: 3 comparisons, 3 assignments.
     * Asymptotic: 1 comparisons, 0 assignments.
     * 
     * @param <T>
     * @param maxObj
     * @param obj
     * @param cmp
     */
    public static <T> void saveMax3BottomUpA(T maxObj[], T obj, Comparator<T> cmp)
    {
        if (cmp.compare(maxObj[2], obj) < 0) {
            if (cmp.compare(maxObj[1], obj) < 0) {
                maxObj[2] = maxObj[1];
                if (cmp.compare(maxObj[0], obj) < 0) {
                    maxObj[1] = maxObj[0];
                    maxObj[0] = obj;
                } else {
                    maxObj[1] = obj;
                }
            } else {
                maxObj[2] = obj;
            }
        }
    }
    
    static int testFindDupe()
    {
        int size = 2134;
        int nums[] = new int[size + 1];
        for (int j = 0; j < size; j++) {
            nums[j] = j;
            
        }
        Random rng = new Random();
        int dupe = rng.nextInt(size);
        nums[size] = dupe;
        
        Shuffler.shuffle(nums);
        
        int xor = 0, sum = 0, ans = 0;
        for (int j = 0; j < size; j++) {
            xor = (xor ^ j) ^ nums[j];
            sum += nums[j];
        }
        sum += nums[size];
        ans = xor ^ nums[size];
        int formula = size * (size - 1) / 2;
        Sx.format("sum:  %d - %d == %d [correct: %d]\n", sum, formula, sum - formula, dupe);
        Sx.format("xor:  %d ^ %d == %d [correct: %d]\n", xor, nums[size], ans, dupe);
        return xor;
    }
    
    public static int unit_test(int level)
    {
        Sx.puts(SaveMax.class.getName() + ".unit_test");
        Integer savedMax5[] = { 0, 6, 7, 8, 9, 5, 1, 2, 3, 4 };
        Integer savedMin5[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        
        Sx.puts("saveMax using array:");
        Integer saved[] = Arrays.copyOf(savedMax5, savedMax5.length);
        Sx.putsArray(saved, " add 8");
        saveMaxSort(saved, 8, 5);
        Sx.putsArray(saved, " add 9");
        saveMaxSort(saved, 9, 5);
        Sx.putsArray(saved);
        
        Sx.puts("saveMin using array:");
        saved = Arrays.copyOf(savedMin5, savedMax5.length);
        Sx.putsArray(saved, " add 8");
        saveMinSort(saved, 8, 5);
        Sx.putsArray(saved, " add 4");
        saveMinSort(saved, 4, 5);
        Sx.putsArray(saved, " add 3");
        saveMinSort(saved, 3, 5);
        Sx.putsArray(saved);
        
        Sx.puts("saveMax using ArrayIter:");
        saved = Arrays.copyOf(savedMax5, savedMax5.length);
        ArrayIter<Integer> ai = new ArrayIter<Integer>(saved);
        Sx.putsArray(saved, " add 8");
        saveMaxSort(ai, 8);
        Sx.putsArray(saved, " add 5");
        saveMaxSort(ai, 5);
        Sx.putsArray(saved);
        
        Sx.puts("saveMin using ArrayIter:");
        saved = Arrays.copyOf(savedMin5, savedMin5.length);
        ai = new ArrayIter<Integer>(saved);
        saveMinSort(ai, 8);
        Sx.putsArray(saved);
        saveMinSort(ai, 5);
        Sx.putsArray(saved);
        
        Sx.puts("saveMax using minHeap:");
        saved = Arrays.copyOf(savedMin5, savedMin5.length);
        ArrayAlgo.reverseArray(saved);
        MinHeap<Integer> minHeap = new MinHeap<Integer>(saved);
        Sx.putsArray(saved, " heapify and add 8");
        saveMax(minHeap, 8);
        Sx.putsArray(saved, " add 5");
        saveMax(minHeap, 5);
        Sx.putsArray(saved, " add 4");
        saveMax(minHeap, 4);
        Sx.putsArray(saved);
        
        Sx.puts("saveMin using maxHeap:");
        saved = Arrays.copyOf(savedMin5, savedMin5.length);
        MaxHeap<Integer> maxHeap = new MaxHeap<Integer>(saved);
        Sx.putsArray(saved, " heapify and add 8");
        saveMin(maxHeap, 8);
        Sx.putsArray(saved, " add 5");
        saveMin(maxHeap, 5);
        Sx.putsArray(saved, " add 4");
        saveMin(maxHeap, 4);
        Sx.putsArray(saved);
        
        int size = 5;
        int iA[] = new int[size];
        for (int j = 0; j < size; j++)
            iA[j] = j;
        Random rng = new Random();
        int iDupe = rng.nextInt(size);
        int vDupe = size / 2;
        if (iA[iDupe] == vDupe)
            iDupe = (iDupe + 1) % size;
        int stomped = iA[iDupe];
        iA[iDupe] = vDupe;
        int xored = 0, sum = 0, nod = 0, xform = 0, form = size * (size - 1) / 2;
        for (int j = 0; j < size; j++) {
            xored = xored + (iA[j] ^ j);
            nod += j;
            sum += iA[j];
            xform ^= j;
        }
        int dif = sum - form;
        Sx.format("iDupe %d, Xored: %d, stomped %d, nod %d, sum %d, fomula %d, xform %d, dif %d \n"
                , iDupe, xored, stomped, nod, sum, form, xform, dif);
        
        for (int j = 0; j < saved.length; j++)
            saved[j] = j / 2;
        Shuffler.shuffle(iA);
        Sx.putsArray(iA, " (shuffled)");
        
        Sx.putsArray(saved, " (newed)");
        
        for (int j = 0; j < size; j++) {
            saveMax4BottomUp(saved, iA[j]);
            Sx.putsArray(saved, " (added " + iA[j]);
        }
        
        testFindDupe();
        
        return 0;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        unit_test(1);
    }
    
    public static int getCountIsFull() {
        return sCountIsFull;
    }
    
    public static void setCountIsFull(int sCountIsFull) {
        SaveMax.sCountIsFull = sCountIsFull;
    }
    
}
