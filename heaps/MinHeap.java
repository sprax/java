/**
 * 
 */
package sprax.heaps;

import java.util.Comparator;

import sprax.Sz;
import sprax.sorts.SortUtil;
import sprax.sprout.Sx;

class ReverseCompare<T extends Comparable<T>> implements Comparator<T>
{
  @Override
  public int compare(T one, T other) {
    return other.compareTo(one);
  }
}

/**
 * @author sprax
 */
public class MinHeap<T extends Comparable<T>> extends Heap<T>
{
  public MinHeap(T[] array, int sizeNow, boolean bHeap, Comparator<T> revComp)
  {
    super(array, sizeNow, bHeap, revComp != null ? revComp : new ReverseCompare<T>());
  }

  public MinHeap(T[] array, int sizeNow, boolean bHeap)
  {
    super(array, sizeNow, bHeap, new ReverseCompare<T>());
  }

  public MinHeap(T[] array, int sizeNow)
  {
    super(array, sizeNow, false, new ReverseCompare<T>());
  }

  public MinHeap(T[] array)
  {
    super(array, array.length, false, new ReverseCompare<T>());
  }

  public MinHeap(MinHeap<T> mh)
  {
	    super(mh.mArray, mh.size(), false, new ReverseCompare<T>());
  }
  
  
  //-------------------------------------------------------------
  /** 
   * Sorts the underlying array in ascending order.
   * @see Heap.sortDescend 
   */
  @Override
  public void sortAscend() 
  {
      super.sortDescend();
  }   
  
  //-------------------------------------------------------------
  /** 
   * Sorts the underlying array in descending order.
   * @see Heap.sortAscend 
   */
  @Override
  public void sortDescend() 
  {
      super.sortAscend();
  }   
  
}



class MinHeapInt extends MinHeap<Integer>
{
	MinHeapInt(int maxSize) {
		super(createIntegerArray(new int[maxSize]));
	}
	
	MinHeapInt(int[] intArray) {
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

    public static int unit_test(int level)
    {
      String testName = MinHeapInt.class.getName() + ".unit_test";
      Sz.begin(testName);
      int numWrong = 0;

      int arr[] = {888, -1, 2, -2, 3, -3, 4, 4, -7, 11, -11, 9999};
      Sx.putsArray("Making a MinHeap from non-ordered array:  ", arr);
      MinHeap<Integer> mhi = new MinHeapInt(arr);
      mhi.heapify();
      Sx.putsArray("MinHeapified (root (first item) is min):  ", mhi.mArray);
      mhi.sortDescend();
      Sx.putsArray("sortDescend: (order is max to min value): ", mhi.mArray);
      mhi.sortAscend();
      Sx.putsArray("sortAscend:  (order is min to max value): ", mhi.mArray);
      
      boolean bSorted = SortUtil.verifySorted(mhi.mArray, mhi.mArray.length);
      if (bSorted == false) {
        String error = "ERROR: " + testName + " failed.";
        Sx.puts(error);
        throw new IllegalStateException(error);
      }         
      mhi = null;
      
      int A[] = {6, 6, 6, 4, 4, 4, 4, 3, 3, 3, 2, 2, 1 };
      int len = A.length;
      int halflen = len/2;
      MinHeapInt mhGT = new MinHeapInt(halflen);
      for (int a : A) {
      	if (a > mhGT.peek())
      		mhGT.add(a);
      }
      Sx.putsArray(A, " with > gives peek(): " + mhGT.peek());
      mhGT = null;
      
      MinHeapInt mhGE = new MinHeapInt(halflen);
      for (int a : A) {
      	if (a >= mhGE.peek())
      		mhGE.add(a);
      }
      Sx.putsArray(A, " with >= gives peek(): " + mhGE.peek());
      
      int theNum = 5;
      boolean containsTheNum = mhGE.contains(theNum);
      Sx.putsArray("minHeap(>=) contains " + theNum + "?\t" + containsTheNum, mhGE.mArray);

      theNum = 4;
      containsTheNum = mhGE.contains(theNum);
      Sx.putsArray("minHeap(>=) contains " + theNum + "?\t" + containsTheNum, mhGE.mArray);
      
      theNum = 7;
      containsTheNum = mhGE.contains(theNum);
      Sx.putsArray("minHeap(>=) contains " + theNum + "?\t" + containsTheNum, mhGE.mArray);
      
      Sz.end(testName, numWrong);
      return numWrong;
    }
 
    public static void main(String[] args) { 
      unit_test(2);
    }
    
}