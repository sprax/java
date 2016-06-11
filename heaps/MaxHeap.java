package sprax.heaps;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import sprax.sorts.SortUtil;
import sprax.sprout.Sx;

/**
 * MaxHeap
 * TODO: array-based min-heap methods using comparator:
 * add            conditional add: add O if size < N or O.sortKey > heap.minKey
 * remove         remove node with minKey
 * heapify        call the first time size == capacity?
 * sift{up|down}  call from heapify or add (if size == capacity)
 * sort           call at the end before using results
 * 
 * @author sprax
 *
 */
public class MaxHeap<T extends Comparable<T>> extends Heap<T>
{
    //private static int sDbg = 2;
    
    public MaxHeap(T[] array, int sizeNow, boolean bHeap, Comparator<T> comp)
    {
      super(array, sizeNow, bHeap, comp != null ? comp : new Compare<T>());
    }

    public MaxHeap(T[] array, int sizeNow, boolean isHeapified)
    {
      super(array, sizeNow, isHeapified, new Compare<T>());
    }

    public MaxHeap(T[] array, int sizeNow)
    {
      super(array, sizeNow, false, new Compare<T>());
    }

    public MaxHeap(T[] array)
    {
      super(array, array.length, false, new Compare<T>());
    }


    //-------------------------------------------------------------
    @SuppressWarnings("deprecation")
    public static int test_time_sort(int sorter, int numTrials, int size)
    {
        String testName = MaxHeap.class.getName() + ".test_time_sort";
        int stat = 0;      
        
        int maxVal = size*4;
        Integer iA[] = new Integer[size];
        Random rng = new Random();              // java.util.Random.    
        for (int k, j = 0; j < size; j++) {
            k = rng.nextInt(maxVal);      
            iA[j] = k;
        }
        Integer iB[] = iA.clone();
        MaxHeap<Integer> mH = new MaxHeap<Integer>(iB, size, false);
        mH.heapify();        
        
        long begTime, endTime, runTime;
        begTime = System.currentTimeMillis();
        for (int q = 0; q < numTrials; q++) { 
            switch (sorter) {
                case  0: 
                    mH = new MaxHeap<Integer>(iA.clone(), size, false);
                    Arrays.sort(mH.mArray);
                    break;
                case  1: 
                    mH = new MaxHeap<Integer>(iB.clone(), size, true);
                    Arrays.sort(mH.mArray);
                    break;
                case  2: 
                    mH = new MaxHeap<Integer>(iA.clone(), size, false);
                    mH.sortAscend();
                    break;
                case  3: 
                    mH = new MaxHeap<Integer>(iB.clone(), size, true);
                    mH.sortAscend();
                    break;
                case  4: 
                    mH = new MaxHeap<Integer>(iA.clone(), size, false);
                    mH.sortDescend(); 
                    break;
                case  5: 
                    mH = new MaxHeap<Integer>(iB.clone(), size, true);
                    mH.sortDescend(); 
                    break;
                case  6: 
                    mH = new MaxHeap<Integer>(iA.clone(), size, false);
                    mH.sortDescendOld(); 
                    break;
                case  7: 
                    mH = new MaxHeap<Integer>(iA.clone(), size, false);
                    mH.sortDescendOff(); 
                    break;
            }
        }      
        endTime = System.currentTimeMillis();
        runTime = endTime - begTime;
        
        String sortStr[] = { "Sys", "SyH", "Asc", "AsH", "Rev", "ReH", "Old", "Off" };
        if (sorter < 4 && ! SortUtil.verifySorted(mH.mArray, mH.getSizeNow())) {
            stat -= 1;
            Sx.puts("FAILED to sort Ascending!");
        }
        if (sorter > 3 && ! SortUtil.verifySortedDescending(mH.mArray, mH.getSizeNow())) {
            stat -= 1;
            Sx.puts("FAILED to sort Descending!");
        }
        
        Sx.format(testName + " %d trials of size %d, sort %s, time(ms): %d\n"
                , numTrials, size, sortStr[sorter], runTime);
        return stat;
    }

    //-------------------------------------------------------------
    public static int test_time_add(int adder, int numTrials, int size)
    {
      String testName = MaxHeap.class.getName() + ".test_time_sort";
      int stat = 0;      

      int maxVal = size*8;
      Integer iA[] = new Integer[size];
      Integer iC[] = new Integer[size];
      Random rng = new Random();              // java.util.Random.    
      for (int j = 0; j < size; j++) {
        iA[j] = rng.nextInt(maxVal);;
        iC[j] = rng.nextInt(maxVal);;
      }
      MaxHeap<Integer> mA = new MaxHeap<Integer>(iA, size, false);
      mA.heapify();
      Integer iB[] = mA.mArray.clone();  // pre-heapified random int array

      long begTime, endTime, runTime;
      begTime = System.currentTimeMillis();
      for (int q = 0; q < numTrials; q++) { 
        MaxHeap<Integer> mH = new MaxHeap<Integer>(iB.clone(), size, false);  // pre-heapified
        if (adder > 0)
          for (int j = 0; j < size; j++)
            mH.add(iC[j]);        
        else
          for (int j = 0; j < size; j++)
            mH.addTo(iC[j]);
      }
      endTime = System.currentTimeMillis();
      runTime = endTime - begTime;

      String addStr[] = { "addCmp", "addTo" };
      Sx.format(testName + " %d trials of size %d, sort %s, time(ms): %d\n"
          , numTrials, size, addStr[adder], runTime);
      return stat;
    }


    public static int test_time_adds(int numTrials, int size) 
    {
      String testName = MaxHeap.class.getName() + ".test_time_adds";
      Sx.puts(testName + " begin ...");
      test_time_add(0, numTrials, size);
      test_time_add(1, numTrials, size);
      test_time_add(1, numTrials, size);
      test_time_add(0, numTrials, size);
      Sx.puts(testName + " .... done");
      return 0;
    }
    
    public static int test_time_sorts(int numTrials, int size) 
    {
      String testName = MaxHeap.class.getName() + ".test_time_sorts";
      Sx.puts(testName + " begin ...");
      test_time_sort(0, numTrials, size);
      test_time_sort(1, numTrials, size);
      test_time_sort(2, numTrials, size);
      test_time_sort(3, numTrials, size);
      test_time_sort(4, numTrials, size);
      test_time_sort(5, numTrials, size);
      if (size < 20001) {
        test_time_sort(6, numTrials, size);
        test_time_sort(7, numTrials, size);
      }
      Sx.puts(testName + " .... done");
      return 0;
    }

    @SuppressWarnings("deprecation")
    public static int unit_test(int level)
    {
      String testName = MaxHeap.class.getName() + ".unit_test";
      Sx.puts(testName);
      int stat = 0;
      int size = 12;
      Integer A[] = new Integer[size];
      MaxHeap<Integer> mH = new MaxHeap<Integer>(A, 0, false);
      mH.set(0, 3);
      mH.set(1, 4);
      mH.set(2, 2);
      mH.set(3, 9);
      mH.setSizeNow(4);
      mH.addLazy(5);
      mH.addLazy(1);
      mH.add(11);
      mH.add(10);
      mH.add(11);
      mH.add(6);
      mH.add(8);
      mH.add(7);
      Sx.printSubArray("Before heapify     " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
      Sx.puts("  verifyHeap ?  " + mH.verifyHeap());

      mH.heapify();
      Sx.printSubArray("After  heapify     " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
      Sx.puts("  verifyHeap ? " + mH.verifyHeap());

      mH.sortAscend();
      Sx.printSubArray("After sortAscend   " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
      Sx.puts("  verifyHeap ? " + mH.verifyHeap());
      if ( ! SortUtil.verifySorted(mH.mArray, mH.getSizeNow())) {
        stat -= 1;
        Sx.puts("sortAscend failed!");
      }

      mH.sortAscend();
      mH.sortDescend();
      Sx.printSubArray("After sortDesc rev " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
      Sx.puts("  verifyHeap ? " + mH.verifyHeap());
      if ( ! SortUtil.verifySortedDescending(mH.mArray, mH.getSizeNow())) {
        stat -= 1;
        Sx.puts("sortDescend failed!");
      }
      
      mH.sortAscend();
      mH.sortDescendOld();
      Sx.printSubArray("After sortDesc old " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
      Sx.puts("  verifyHeap ? " + mH.verifyHeap());
      if ( ! SortUtil.verifySortedDescending(mH.mArray, mH.getSizeNow())) {
        stat -= 1;
        Sx.puts("sortDescendOld failed!");
      }
      
      mH.sortDescendOff();
      Sx.printSubArray("After sortDesc off " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
      Sx.puts("  verifyHeap ? " + mH.verifyHeap());
      if ( ! SortUtil.verifySortedDescending(mH.mArray, mH.getSizeNow())) {
        stat -= 1;
        Sx.puts("sortDescendOff failed!");
      }

      if (level > 4) {
        // This totally fails!
        Sx.puts();
        mH.sortAscend();
        mH.sortDescendNew();
        Sx.printSubArray("After sortDesc old " + mH.getSizeNow() + ": ", mH.mArray, 0, mH.getSizeNow());
        Sx.puts("  verifyHeap ? " + mH.verifyHeap());
        if ( ! SortUtil.verifySortedDescending(mH.mArray, mH.getSizeNow())) {
          stat -= 1;
          Sx.puts("sortDescendNew failed!");
        }
      }

      if (level > 2) {
        mH.add(4);
        mH.add(6);
        mH.add(7);
        mH.add(4);
        mH.add(1);
        mH.add(2);
        mH.add(5);
        mH.add(4);
        mH.add(9);
        mH.add(4);
        mH.add(2);
        mH.add(3);
        for (int j = 0; j < mH.getSizeNow(); j++) {
          Sx.putsSubArray("From offset "+j+" a heap? " + mH.verifyHeap(j) + ": ", mH.mArray, j, mH.getSizeNow());
          mH.heapify(j);
          Sx.putsSubArray("From offset "+j+" a heap? " + mH.verifyHeap(j) + ": ", mH.mArray, j, mH.getSizeNow());
        }
      }
      
      if (level > 3) {
        Sx.putsArray("After reAdd " + mH.getSizeNow() + ": ", mH.mArray);
        Sx.puts("Removal sort:   ");
        for (int j = mH.getSizeNow(); --j >= 0; ) {
          int val = mH.remove();
          Sx.print("Removed " + val + " and that leaves ");
          Sx.putsSubArray(mH.getSizeNow() + " in a heap? " + mH.verifyHeap() + ": ", mH.mArray, 0, mH.getSizeNow());
        }
        Sx.puts();
        mH.set( 0, 4);
        mH.set( 1, 6);
        mH.set( 2, 7);
        mH.set( 3, 4);
        mH.set( 4, 1);
        mH.set( 5, 2);
        mH.set( 6, 5);
        mH.set( 7, 4);
        mH.set( 8, 9);
        mH.set( 9, 4);
        mH.set(10, 2);
        mH.set(11, 3);
        mH.setSizeNow(12);
        mH.mIsHeapified = false;
        Sx.putsArray("After reSet " + mH.getSizeNow() + ": ", mH.mArray);
        mH.heapify();
        Sx.putsArray("Aft-heapify " + mH.getSizeNow() + ": ", mH.mArray);
        mH.sortDescendOff();
        Sx.putsArray("sorted Asc  " + mH.getSizeNow() + ": ", mH.mArray);
        mH.heapify();
        Sx.putsArray("pos-heapify " + mH.getSizeNow() + ": ", mH.mArray);
      }

      if (level > 2) {
        int numTrials = 11, sortSize = 60000;
        stat += test_time_sorts(numTrials, sortSize);
      }
      
      if (level > 2) {
        int numTrials = 11, addSize = 600000;
        stat += test_time_adds(numTrials, addSize);
      }

      
      int arr[] = {888, -1, 2, -2, 3, -3, 4, 4, -7, 11, -11, 9999};
      MaxHeap<Integer> mhp = new MaxHeapInt(arr);
      mhp.heapify();
      Sx.putsArray("MaxHeapified (root (first item) is max):  ", mhp.mArray);
      mhp.sortAscend();
      Sx.putsArray("sortAscend:  (order is min to max value): ", mhp.mArray);
      mhp.sortDescend();
      Sx.putsArray("sortDescend: (order is max to min value): ", mhp.mArray);
     
      
      if (stat < 0) {
        String error = "ERROR: " + testName + " failed with status " + stat;
        Sx.puts(error);
        throw new IllegalStateException(error);
      }       
      
      return stat;
    }
 
    public static void main(String[] args) { 
      unit_test(2);
    }
    
}

class MaxHeapInt extends MaxHeap<Integer>
{
    MaxHeapInt(int[] intArray) {
        super(createIntegerArray(intArray));
    }
    static Integer[] createIntegerArray(int[] intArray) {
        if (intArray != null && intArray.length > 0) {
            int len = intArray.length;
            Integer array[] = new Integer[len];
            for (int j = 0; j < len; j++)
                array[j] = intArray[j];
            return array;
        }
        return null;
    }
}
