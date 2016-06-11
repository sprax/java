package sprax.heaps;

import java.util.Comparator;

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

class Compare<T extends Comparable<T>> implements Comparator<T>
{
  @Override
  public int compare(T one, T other) {
    return one.compareTo(other);
  }
}

public abstract class Heap<T extends Comparable<T>> implements Comparable<Heap<T>>, HeapInterface<T>
{
    private static int sDbg = 2;
    
    protected   T[] mArray;           // the underlying array
    private     int mSizeMax;         // size of the underlying array
    private     int mSizeNow;         // number of nodes in array
    protected   Comparator<T> mComp;  // comparator 
    protected boolean mIsHeapified;   // Set to true in constructor; invariant under add.
    protected boolean mIsSortedAsc;   // Sorted implies heapified is false.
    protected boolean mIsSortedDesc;  // Reverse sorted implies heapified is true.

    //--------------------------------------------- constructor
    public Heap(T[] array, int sizeNow, boolean isHeap, Comparator<T> comp)
    {
      if (array == null || sizeNow < 0)
        throw new IllegalArgumentException("MaxHeap " + array + " " + sizeNow);
      mSizeMax      = array.length;
      mSizeNow      = sizeNow;
      mArray        = array;
      mComp         = comp != null ? comp : new Compare<T>();
      mIsHeapified  = isHeap;
      mIsSortedAsc  = false;
      mIsSortedDesc = false;
    }
    //--------------------------------------------- constructor
    public Heap(T[] array, int sizeNow, boolean bHeap)
    {
      this(array, sizeNow, bHeap, null);
    }
    //-------------------------------------------------------------
    
    @Override
    public int size()					{ return mSizeNow; }
    @Override
    public boolean isHeap()             { return ( mIsHeapified ); }
    @Override
    public boolean isSortedAscending()  { return mIsSortedAsc; }
    @Override
    public boolean isSortedDescending() { return mIsSortedDesc; }
    public void isHeap(boolean tf)      { mIsHeapified = tf; }
    public boolean isEmpty()            { return ( mSizeNow == 0 ); }
    public boolean isFull()             { return ( mSizeNow == mSizeMax ); }
    
    //-------------------------------------------------------------
    /**
     * Get the indexed element from the underlying array.
     * No error checking, no index correction.
     * @param index
     * @return
     */
    public T get(final int index) {
      return mArray[index];
    }

    //-------------------------------------------------------------
    /**
     * An internal method that sets the array entry at this index to newNode
     * without any side effects.  No trickle up or down, no heapify, no flag-
     * setting.  In particular, calling set(...) may invalidate the heap 
     * property, reinstate it, or have no effect on it.  Regardless, mIsHeap 
     * is not changed.
     */
    protected void set(int index, T newNode) {
      mArray[index] = newNode;
    }    

    /** 
     * Gets maximal element, which is the first element in the 
     * heapified array.  Calls heapify on the underlying array
     * if it is not already marked as being heapified.
     * @return maximal element according to T.compareTo.
     */
    @Override
    public T peek() 
    { 
        if ( ! mIsHeapified )
            heapify();
        return mArray[0];
    }
    
    @Override
    public int compareTo(Heap<T> other) {

        if ( ! other.mIsHeapified)
            other.heapify();
        return peek().compareTo(other.peek());
    }
    
    //-------------------------------------------------------------
    public boolean verifyHeap() 
    {
      for (int j = 0; j < mSizeNow/2; j++) {
        int left  = j*2 + 1;
        int right = left + 1;
        if ((                    mComp.compare(mArray[j], mArray[left ]) < 0) ||
            (right < mSizeNow && mComp.compare(mArray[j], mArray[right]) < 0))
          return false;
      }
      return true;
    }

    //-------------------------------------------------------------
    /** 
     * Verifies heap property on underlying array, starting from
     * the specified offset.
     */
    public boolean verifyHeap(final int off) 
    {
      int end = off + (mSizeNow - off)/2;
      for (int j = off; j < end; j++) {
        int left  = off + (j - off)*2 + 1;
        int right = left + 1;
        if ((                    mComp.compare(mArray[j], mArray[left ]) < 0) ||
            (right < mSizeNow && mComp.compare(mArray[j], mArray[right]) < 0))
          return false;
      }
      return true;
    }
    
    
    @Override
    public boolean contains(T node)
    {
    	return containsAt(0, node);
    }

    private boolean containsAt(int idx, T node)
    {
    	if (idx >= mSizeNow)
    		return false;
    	int cmp = mComp.compare(mArray[idx], node);
    	if (cmp > 0)
    		return false;
    	if (mArray[idx] == node)
    		return true;
    	// check under left child
    	idx *= 2;
    	if (containsAt(idx, node))
    		return true;
    	// check under right child
    	idx += 1;
    	return containsAt(idx, node);
    }

    @Override
    public int findFirstIndexOf(T t)
    {
    	return findFirstIndexStartingAt(0, t);
    }

    private int findFirstIndexStartingAt(int idx, T node)
    {
    	if (idx >= mSizeNow)
    		return -1;
    	int cmp = mComp.compare(mArray[idx], node);
    	if (cmp > 0)
    		return -1;
    	if (mArray[idx] == node)
    		return idx;
    	// check under left child
    	idx *= 2;
    	int foundAt = findFirstIndexStartingAt(idx, node);
    	if (foundAt >= 0)
    		return foundAt;
    	// check under right child
    	idx += 1;
    	return findFirstIndexStartingAt(idx, node);
    }
    
    //-------------------------------------------------------------
    /**
     * Add this node if either the current size is less than the max size
     * or else this node sorts prior to the current min node, in which case
     * it replaces the current min node.
     * @return true if the contents changed (i.e. this node was saved here, 
     * either adding to or replacing the current nodes); false if not.
     */
    @Override
    public boolean add(T node)
    {
      if (mSizeNow < mSizeMax) {
        mArray[mSizeNow] = node;
        trickleUp(mSizeNow++);
        return true;
      } else {
        T minNode = mArray[0];              // TODO: inline
        if (mComp.compare(node, minNode) < 0) {
          mArray[0] = node;
          trickleDown(0);
          return true;
        }
      }
      return false;
    }
    
    /**
     * TODO: Why is this here?  Supposed to be the NIECE version?
     * Add this node if either the current size is less than the max size
     * or else this node sorts prior to the current min node.
     * @return true if the contents changed (i.e. this node was saved here, 
     * either adding to or replacing the current nodes); false if not.
     */
    public boolean addTo(T node)
    {
      if (mSizeNow < mSizeMax) {
        mArray[mSizeNow] = node;
        trickleUp(mSizeNow++);
        return true;
      } else {
        T minNode = mArray[0];              // TODO: inline
        if (node.compareTo(minNode) < 0) {
          mArray[0] = node;
          trickleDown(0);
          return true;
        }
      }
      return false;
    }
    
    /** 
     * This lazier version is typically less efficient than the
     * usual, more eager version, which calls trickle up or down 
     * as part of each addition.  
     * @deprecated
     * @param node
     * @return
     */
    protected boolean addLazy(T node)
    {
      if (mSizeNow < mSizeMax) {
        mArray[mSizeNow] = node;
        //trickleUp(mSizeNow);
        if (++mSizeNow == mSizeMax)
            heapify();
        return true;
      } else {
        T minNode = mArray[0];
        if (mComp.compare(node, minNode) < 0) {
          mArray[0] = node;
          trickleDown(0);
          return true;
        }
      }
      return false;
    }
    
    //-------------------------------------------------------------
    /**
     * Removes entry 0 (a.k.a. root, head, min/max element) and restores
     * the heap property on the now smaller heap.  To re-heapify, it 
     * only needs to copy the last entry to the root and call
     * trickleDown(0).
     * @return former root
     */
    @Override
    public boolean remove(T node)           // delete item with max key
    {                           // (assumes non-empty list)
    	int foundAt = findFirstIndexOf(node);
    	if (foundAt >= 0) 
    	{
    		mArray[foundAt] = mArray[--mSizeNow];
    		trickleDown(foundAt);
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    
    @Override
    public T remove()           // delete item with max key
    {                           // (assumes non-empty list)
      T root = mArray[0];
      mArray[0] = mArray[--mSizeNow];
      trickleDown(0);
      return root;
    }
        
    
    //-------------------------------------------------------------
    public void trickleUp(int index)
    {
        T bottom = mArray[index];
        int parent = (index-1) / 2;
        while(index > 0 && mComp.compare(mArray[parent], bottom) < 0) {
            mArray[index] = mArray[parent];  // move it down
            index = parent;
            parent = (parent-1) / 2;
        }
        mArray[index] = bottom;
    }
    //-------------------------------------------------------------
    public void trickleDown(int index)
    {
        trickleDown(index, mSizeNow);
    }
    //-------------------------------------------------------------
    public void trickleDown(int index, int last)
    {
      int largerChild;
      T top = mArray[index];       // save root
      while(index < last/2)       // while node has at
      {                                  //    least one child,
        int leftChild  = 2*index+1;
        int rightChild = leftChild+1;
        // find larger child
        if(rightChild < last &&  // (rightChild exists?)
            mComp.compare(mArray[leftChild], mArray[rightChild]) < 0)
          largerChild = rightChild;
        else
          largerChild = leftChild;
        // top >= largerChild?
        if (mComp.compare(top, mArray[largerChild]) >= 0)
          break;
        // shift child up
        mArray[index] = mArray[largerChild];
        index = largerChild;            // go down
      }  // end while
      mArray[index] = top;            // root to index
    }
   

    //-------------------------------------------------------------
    /** 
     * heapify: put array in binary heap order, which means if a node at 
     * position k has left and right children, they will be at positions
     * 2*k and 2*k + 1.
     */
    @Override
    public void heapify() 
    {
      // start is assigned the index of the last parent node in mArray 
      for(int start = (mSizeNow - 2) / 2; start >= 0; start--) {
        // sift down the node at index start to the proper place
        // such that all nodes below the start index are in heap
        // order
        trickleDown(start);
      }
      // after sifting down the root all nodes/elements are in heap order
      mIsHeapified = true;
    }
    
    /**
     * Heapify the underlying array starting at the specified
     * offset.  Don't use this for sorting; there are much more
     * efficient ways.
     * @param offset
     */
    public void heapify(int offset) 
    {
      // start is assigned the index of the last parent node in mArray 
      for(int start = (mSizeNow - 2) / 2; start >= offset; start--) {
        // sift down the node at index start to the proper place
        // such that all nodes below the start index are in heap
        // order
        trickleDown(start, offset);
      }
      // after sifting down the root all nodes/elements are in heap order
      mIsHeapified = true;
    }

  
    //-------------------------------------------------------------
    /** 
     * Sorts the underlying array in the order entailed by the compareTo
     * operator (generally the default order is ascending) by repeatedly
     * popping the first node off the beginning and placing it at 
     * the end.  This is an efficient sort, but it destroys the 
     * heap property.  It sets the sorted-ascending property to true
     * and the sorted-descending property to false.  
     * 
     * Note: "ascending" order means that lesser entries precede
     * greater entries, as determined by compareTo.  For a MaxHeap,
     * sorting in ascending order is faster than sorting
     * in descending order, which is implemented by sorting then
     * reversing.
     * 
     * @see sortDescend
     */
    @Override
    public void sort() 
    {
        if (mIsSortedAsc)
            return;
        if ( ! mIsHeapified)
            heapify();
        for (int last = mSizeNow; --last >= 0; ) {
            placeFirstLast(last);
        }
        mIsHeapified = false;
        mIsSortedAsc = true;
    }    
    @Override
    public void sortAscend() 
    {
        sort();
    }  
    
    //-------------------------------------------------------------
    /**
     * Swap first and last entry with side effects used for sorting. 
     * That is, swap the first and last entries, and partially 
     * re-heapify by trickling down only from index 0 to the last-1.
     * Assuming the underlying array was not empty and was already 
     * heapified from positions [0, N), this leaves the array
     * heapified from positions [0, N-1), with the formerly 0th
     * entry placed at position N-1.  Overall, the heap property
     * will be destroyed, but mSizeNow is unchanged.
     * 
     * 
     */
    protected void placeFirstLast(int last)              
    {                           
        T root = mArray[0];               // (assumes non-empty list)
        mArray[0] = mArray[last];
        trickleDown(0, last);
        mArray[last] = root;
    }
    //-------------------------------------------------------------
    /** 
     * Sorts the underlying array in descending order by first 
     * sorting it in ascending order, then reversing it.
     * Effective complexity should be O(NlogN + N) -> O(NlogN)
     */
    @Override
    public void sortDescend() 
    {
      if (mIsSortedDesc)
        return;
      
      sort();
      reverse();
      
      mIsHeapified  = true;
      mIsSortedAsc  = false;
      mIsSortedDesc = true;
    }    

    //-------------------------------------------------------------
    /** 
     * Reverses the currently used part of the underlying array,
     * without changing any other state.  In particular, the flags
     * mIsHeap, etc., are not changed.
     */
    protected void reverse() 
    {
        for (int beg = 0, end = mSizeNow; --end > beg; beg++) {
            T temp = mArray[end];
            mArray[end] = mArray[beg];
            mArray[beg] = temp;
        }
    }    

    /**
     * @deprecated
     */
    public void sortDescendNew() 
    {
        if ( ! mIsHeapified)
            heapify();
        
        if (sDbg > 0) {
            Sx.printSubArray("After  heapify     " + mSizeNow + ": ", (Integer[])mArray, 0, mSizeNow);
            Sx.puts("  verifyHeap ? " + verifyHeap());
        }
        
        // Start the trickleOff at 1 and end at size-1; 
        // node 0 is already in its right place, and the last node will be. 
        for (int end = mSizeNow-1, j = 0; j < end; j++) {
            trickleUpChildrenOff(j);
            if (sDbg > 0) {
                Sx.printSubArray("After  trickleOff(" + j + "): ", (Integer[])mArray, 0, mSizeNow);
                Sx.puts("  verifyHeap ? " + verifyHeap());
            }     
        }
        mIsHeapified  = true;
        mIsSortedAsc  = false;
        mIsSortedDesc = true;
    }    
    //-------------------------------------------------------------
    /**
     * Sorts the heap nodes in ascending order.  If the nodes are
     * not already be in heap order, this method calls heapify before
     * sorting.  The heap property will of course apply afterward.  
     * (Sorting a min-heap in descending order would un-heapify it.)
     * @deprecated
     */
    public void sortDescendOld() 
    {
        if ( ! mIsHeapified)
            heapify();
        
//        if (sDbg > 0) {
//            Sx.printSubArray("After  heapify     " + mSizeNow + ": ", (Integer[])mArray, 0, mSizeNow);
//            Sx.puts("  verifyHeap ? " + verifyHeap());
//        }
        
        // Start the trickleOff at 1 and end at size-1; 
        // node 0 is already in its right place, and the last node will be. 
        for (int end = mSizeNow-1, j = 1; j < end; j++) {
            heapifyOffOld(j);
//            if (sDbg > 0) {
//                Sx.printSubArray("After  trickleOff(" + j + "): ", (Integer[])mArray, 0, mSizeNow);
//                Sx.puts("  verifyHeap ? " + verifyHeap());
//            }     
        }
        mIsHeapified  = true;
        mIsSortedAsc  = false;
        mIsSortedDesc = true;
    }
    public void heapifyOffOld(int off) 
    {
      // start is assigned the index of the last parent node in mArray 
      for(int start = off + (mSizeNow - off - 2) / 2; start >= off; start--) {
        // sift down the node at index start to the proper place
        // such that all nodes below the start index are in heap
        // order
        trickleDownOffOld(start, off);
      }
      // after sifting down the root all nodes/elements are in heap order
      mIsHeapified = true;
    }
    //-------------------------------------------------------------
    private void trickleDownOffOld(int start, int offset)
    {

        int root = start, leftChild, rightChild;
        
        while((leftChild = offset + 2*(root-offset)+1) < mSizeNow){      //While the root has at least one child

            //if the child has a sibling and the child's value is less than its sibling's...
            if ((rightChild = leftChild+1) < mSizeNow && mComp.compare(mArray[leftChild], mArray[rightChild]) < 0)
                leftChild = rightChild;           //... then point to the right child instead
            if (mComp.compare(mArray[root], mArray[leftChild]) < 0) {     //out of max-heap order
                T temp  = mArray[root];
                mArray[root] = mArray[leftChild];
                mArray[leftChild] = temp;
                root = leftChild;                //repeat to continue sifting down the child now
            } else { 
                return;
            }
        }
    }
    
    /**
     * @deprecated Way slow: 100s of times slower than sortDescend for size > 10,000
     */
    void sortDescendOff() 
    {
        if ( ! mIsHeapified)
            heapify();
        
//        if (sDbg > 0) {
//            Sx.printSubArray("After  heapify     " + mSizeNow + ": ", (Integer[])mArray, 0, mSizeNow);
//            Sx.puts("  verifyHeap ? " + verifyHeap());
//        }
        
        // Start the trickleOff at 1 and end at size-1; 
        // node 0 is already in its right place, and the last node will be. 
        for (int end = mSizeNow-1, j = 1; j < end; j++) {
            heapifyOff(j);
//            if (sDbg > 0) {
//                Sx.printSubArray("After  trickleOff(" + j + "): ", (Integer[])mArray, 0, mSizeNow);
//                Sx.puts("  verifyHeap ? " + verifyHeap());
//            }     
        }
        mIsHeapified  = true;
        mIsSortedAsc  = false;
        mIsSortedDesc = true;
    }  
    
    
    public void heapifyOff(int off) 
    {
      // start is assigned the index of the last parent node in mArray 
      for(int start = off + (mSizeNow - off - 2) / 2; start >= off; start--) {
        // sift down the node at index start to the proper place
        // such that all nodes below the start index are in heap
        // order
        trickleDownOff(start, off);
      }
      // after sifting down the root all nodes/elements are in heap order
      mIsHeapified = true;
    }
    
    public void trickleDownOff(int index, int offset)
    {
      int largerChild;
      T top = mArray[index];       // save root
      while(index < offset + (mSizeNow - offset)/2)       // while node has at
      {                                  //    least one child,
        int leftChild  = offset + 2*(index-offset)+1;
        int rightChild = leftChild+1;
        // find larger child
        if(rightChild < mSizeNow &&  // (rightChild exists?)
            mComp.compare(mArray[leftChild], mArray[rightChild]) < 0)
          largerChild = rightChild;
        else
          largerChild = leftChild;
        // top >= largerChild?
        if (leftChild >= mSizeNow)
            break;
        if( mComp.compare(top, mArray[largerChild]) >= 0)
          break;
        // shift child up
        mArray[index] = mArray[largerChild];
        index = largerChild;            // go down
      }
      mArray[index] = top;            // root to index
    }
    

    

    
    //-------------------------------------------------------------
    public void trickleUpChildrenOff(int index)
    {
      int offset = index + 1;


      // while(index < offset + (mSizeNow - offset)/2)       // while node has at

      int leftChild  = 2*index+1;     // offset + 2*(index-offset)+1;
      if (leftChild < mSizeNow)   
        trickleUpOff(leftChild, offset);
      else
        return;

      int rightChild = leftChild+1;
      if (rightChild < mSizeNow)   
        trickleUpOff(rightChild, offset);

    }
    
    public void trickleUpOff(int index, int off)
    {
        T bottom = mArray[index];
        int parent = off + (index-off-1) / 2;
        while(index > off && mComp.compare(mArray[parent], bottom) < 0 ) {
            mArray[index] = mArray[parent];  // move it down
            index = parent;
            parent = (parent-1) / 2;
        }
        mArray[index] = bottom;
    }
    
    
    
    //-------------------------------------------------------------
    //-------------------------------------------------------------
    
    void swap(int j, int k) 
    {
        T tmp = mArray[j];
        mArray[j] = mArray[k];
        mArray[k] = tmp;
    }
		public int getSizeNow() {
	    return mSizeNow;
    }
		public void setSizeNow(int mSizeNow) {
	    this.mSizeNow = mSizeNow;
    }


    
}


/*
sprax.Heap.test_time_sort 11 trials of size 10000, sort Sys, time(ms): 35
sprax.Heap.test_time_sort 11 trials of size 10000, sort Asc, time(ms): 58
sprax.Heap.test_time_sort 11 trials of size 10000, sort Rev, time(ms): 60
sprax.Heap.test_time_sort 11 trials of size 10000, sort Old, time(ms): 12120
sprax.Heap.test_time_sort 11 trials of size 10000, sort Off, time(ms): 13729
*/
