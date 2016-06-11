package sprax.arrays;

import java.util.Arrays;

import sprax.sprout.Sx;

/**
 * class ArrayIter -- easy operations on arrays, as in Perl
 * @author slines
 * 
 * Synonyms:
 * add, append:  add to the end, or tail.
 * pop, remove:  remove from beginning, or head.
 * push, prepend:  add to the beginning, or head, and push everything else back
 * subIter: TODO get new iterator over an existing, contiguous (sub) array
 * slice:  TODO -- not yet supported
 *
 * It is often useful to remove items from the beginning of one ordered set
 * while at the same time appending them to the end of another.  P and Q are
 * two ArrayIters, the following append/remove operations will produce the 
 * same results in appending the remainder of Q to the end of P  -- they are
 * listed here in order of increasing error checking and other overhead:
 <code>
  P.appendRemoveAll(Q);

  while (Q.mIndex < Q.mSize)
    P.appendRemove(Q);
 
  while (Q.mIndex < Q.mSize)
    P.append(Q.remove());

  P.appendSubArray(Q.mArray, Q.mIndex, Q.mSize);
  Q.removeAll();

  ArrayIter sub = new ArrayIter(ai.mArray, ai.mIndex, ai.mSize);
  merged.append(sub);
</code>

 */
public class ArrayIter<T extends Comparable<T>> implements Comparable<ArrayIter<T>>
{
  T mArray[]; // underlying array, not necessarily "constant"
  private int mIndex;   // current position
  private int mSize;    // number of elements to use (often the same as mArray.length)
  public ArrayIter(final T A[]) {
    if (A == null || A.length < 1)
      throw new IllegalArgumentException("ArrayIter: array null or length 0");
    mArray = A;
    mSize  = A.length;
  }
//  ArrayIter(T proto, int length) {
//    if (length < 1)       // TODO: make the limit 0?  Why allow zero-length arrays?
//      throw new IllegalArgumentException("ArrayIter("+length+" < 1)");
//    mArray = (T[])proto.clone();
//  }  
  public ArrayIter(final T A[], int beg, int end) {
    if (beg < 0)       // TODO: Why not allow < 0?  KISS!
      throw new IllegalArgumentException("ArrayIter("+beg+" < 0)");
    mArray = A;
    mIndex = beg;   // beg < 0 is illegal
    mSize  = end;   // end <= beg is legal (an "empty" iterator is OK)
  }  
  public T head()         { return   mArray[mIndex]; }
  public T head(T rep)    { T  tmp = mArray[mIndex]; mArray[mIndex] = rep; return tmp; }
  public T tail()         { return   mArray[mSize-1]; }
  public T tail(T rep)    { T  tmp = mArray[mSize-1]; mArray[mSize-1] = rep; return tmp; }
  public T next()         { return   mArray[++mIndex]; }
  public T remove()       { return   mArray[mIndex++]; }
  public T[] removeAll()  { mIndex = mSize; return mArray; }
  public int  append(T t) { mArray[  mSize ] = t; return ++mSize; } // may throw ArrayOutOfBounds
  public int  appendArray(T A[])  {  return appendSubArray(A, 0, A.length); } // may throw
  public void appendRemove(ArrayIter<T>   other) {  append(other.remove()); }
  public boolean isEmpty()  { return ( mIndex >= mSize ); }
  public boolean isFull()   { return ( mSize  >= mArray.length ); }
  public boolean hasNext()  { return ( mIndex + 1 < mSize ); }
  public boolean isDone()   { return ( mIndex == mSize ); }
  @Override
  public int compareTo(ArrayIter<T> other) {
    return head().compareTo(other.head());
  }
  /** 
   * Sorts only the current sub-array, that is, the entries that
   * are in use and have not already been iterated over.
   */
  public void sort()
  {
    Arrays.sort(mArray, mIndex, mSize);
  }  
  /** 
   * Resets the current index to 0 and sorts all 
   * the entries that are in use (from 0 to the
   * current size).  Obviously this invalidates 
   * the iterator's previous state.
   * 
   * TODO: Implement sortFrom and sortArray?  Not until needed.
   */
  public void sortAll()
  {
    mIndex = 0;
    sort();
  }

  public int appendSubArray(T A[], int beg, int end) // may throw ArrayOutOfBounds, etc
  {
    if (end - beg > mArray.length - mSize)
      throw new IllegalArgumentException("appendSubArray OOB error: "+(end-beg)+" > "+(mArray.length-mSize));
   
    while (beg < end)
      mArray[mSize++] = A[beg++];
    return mSize; 
  }

  /** 
   * Append all remaining elements from the specified other
   * ArrayIter to this one.  No elements are removed.
   * @param  other
   * @return the resulting size of this ArrayIter
   */  
  public int append(ArrayIter<T> other)
  {
    // TODO: assert(this != other);
    return appendSubArray(other.mArray, other.mIndex, other.mSize);
  }

  /** 
   * Append up to sizeLimit remaining elements from the specified other
   * ArrayIter to this one.  No elements are removed.
   * @param  other
   * @return the resulting size of this ArrayIter
   */  
  public void appendSafe(ArrayIter<T> other, int sizeLimit)
  {
    
    int beg  = other.mIndex;
    int size = Math.min(other.mSize - beg, mArray.length - mSize);
    int end  = size < sizeLimit  ?  beg + size : beg + sizeLimit;
    while (beg < end)
      mArray[mSize++] = other.mArray[beg++];
  }

  /** 
   * remove all remaining elements from the specified other
   * ArrayIter and append them to this one, without any error checking. 
   * @param  other
   * @return the resulting size of this ArrayIter
   */
  public int appendRemoveAll(ArrayIter<T> other)
  {
    if (this != other)
      while (other.mIndex < other.mSize)
        append(other.remove());
    return mSize;
  }
  
  /**
   * remove a number of elements, that is, advance the index by size.
   * @param   size
   * @return  mArray, the underlying array
   */
  @Deprecated  // TODO: make this make sense?
  public T[] remove(int size) // may throw ArrayOutOfBounds, etc
  {
    int idx = mIndex + size;
    if (idx > mSize)
      throw new IllegalArgumentException("remove(size) OOB error: "+idx+" > "+mSize);
    mIndex = idx;
    return mArray; 
  }


	public int getSize() {
	  return mSize;
  }
	public int setSize(int mSize) {
	  this.mSize = mSize;
	  return mSize;
  }
	public int getIndex() {
	  return mIndex;
  }
	public void setIndex(int mIndex) {
	  this.mIndex = mIndex;
  }
	
  public static int unit_test(int dbg) 
  {
    String testName =  ArrayIter.class.getName() + ".unit_test";
    Sx.puts(testName + ": BEGIN");

    ArrayIter<Integer>   ai = null;
    try {
      ArrayIter<Integer> a1 = new ArrayIter<Integer>(new Integer[ 1]);  ai = a1; // ok
      ArrayIter<Integer> a0 = new ArrayIter<Integer>(new Integer[ 0]);  // wrong, but should it be?
      assert(a1.getSize() == a0.getSize());
    } catch (IllegalArgumentException ex) {
      Sx.debug(dbg, "Error: " + ex.getMessage());
      //S.debug(sDbg, "cause: "  + ex.getCause());
      //return 0;
    }
    try {
      ArrayIter<Integer> aN = new ArrayIter<Integer>(new Integer[-1]);  // illegal
      aN.append(0);
    } catch (NegativeArraySizeException ex) {
      Sx.debug(dbg, "Error: " + ex.getMessage());
      //S.debug(sDbg, "cause: "  + ex.getCause());
      //return 0;
    }    
    
    try {
      ai.append(-1);
      ai.appendArray(ai.mArray);
    } catch (IllegalArgumentException ex) {
      Sx.debug(dbg, "Error: " + ex.getMessage());
    } catch (ArrayIndexOutOfBoundsException ex) {
      Sx.debug(dbg, "Error: " + ex.getMessage());
    }

    ai.appendRemoveAll(ai);
    
    Sx.puts(testName + ": END");
    return 0;
  }

  public static void main(String[] args) { unit_test(0); }

}
