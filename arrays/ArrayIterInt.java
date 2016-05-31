package sprax.arrays;

import java.util.Arrays;

import sprax.Sx;

/**
 * class ArrayIter -- easy operations on arrays, such as append,
 *  push, pop, remove, etc.  (Think Perl and other scripting languages.)
 *  Although an ArrayIter is backed by an array, it's generally better 
 *  to use ArrayIter methods and let them delegate as needed, rather
 *  than obtaining a reference to the underlying array and operating on
 *  it.  For example, use <code>arrayIter.isEmpty()</code> instead of 
 *  <code>arrayIter.getArray().length==0</code>.
 *  In fact, the underlying array is not allowed to be null or empty,
 *  so checking its length is not useful.
 *  
 * Underlying array cannot be zero-length.
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
public class ArrayIterInt implements Comparable<ArrayIterInt>
{
    private final int mArray[]; // underlying array, not necessarily "constant", but final.
    private int mIndex;         // current position
    int mSize;          // number of elements to use (often the same as mArray.length)
    public ArrayIterInt(final int A[]) {
        if (A == null)
            throw new IllegalArgumentException("ArrayIter(null)");
        mArray = A;
        mSize  = A.length;
    }
    public ArrayIterInt(int length) {
        if (length < 1)       // Underlying array cannot be zero-length.
            throw new IllegalArgumentException("ArrayIter("+length+" < 1)");
        mArray = new int[length];
    }  
    ArrayIterInt(final int A[], int beg, int end) {
        if (beg < 0)
            throw new IllegalArgumentException("ArrayIter("+beg+" < 0)");
        mArray = A;
        mIndex = beg;   // beg < 0 is illegal
        mSize  = end;   // end <= beg is legal (an "empty" iterator is OK)
    }  
    public int head()         { return   mArray[mIndex]; }
    public int tail()         { return   mArray[mSize-1]; }
    public int next()         { return   mArray[++mIndex]; }
    public int remove()       { return   mArray[mIndex++]; }
    public int[] removeAll()  { mIndex = mSize; return mArray; }
    public int  append(int n) { mArray[  mSize ] = n; return ++mSize; } // may throw ArrayOutOfBounds
    public int  appendArray(int A[])  {  return appendSubArray(A, 0, A.length); } // may throw
    public void appendRemove(ArrayIterInt   other) {  append(other.remove()); }
    public boolean isEmpty()  { return ( mIndex >= mSize ); }
    public boolean isFull()   { return ( mSize  >= mArray.length ); }
    public boolean hasNext()  { return ( mIndex + 1 < mSize ); }
    @Override
    public int compareTo(ArrayIterInt other) {
        return head() - other.head();
    }
    
    public int appendSubArray(int A[], int beg, int end) // may throw ArrayOutOfBounds, etc
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
    public int append(ArrayIterInt other)
    {
        return appendSubArray(other.mArray, other.mIndex, other.mSize);
    }
    
    /** 
     * Append up to sizeLimit remaining elements from the specified other
     * ArrayIter to this one.  No elements are removed.
     * @param  other
     * @return the resulting size of this ArrayIter
     */  
    public void appendSafe(ArrayIterInt other, int sizeLimit)
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
    public int appendRemoveAll(ArrayIterInt other)
    {
        while (other.mIndex < other.mSize)
            append(other.remove());
        return mSize;
    }
    
    /**
     * Removes the specified number of elements from the beginning.
     * That is, advances the index by size, and returns a copy of 
     * the removed elements.
     * @param   size
     * @return  mArray, the underlying array
     */
    // To be @Deprecated ?
    public int[] remove(int size) // may throw ArrayOutOfBounds, etc
    {
        int idx = mIndex + size;
        if (idx > mSize)
            throw new IllegalArgumentException("remove(size) OOB error: "+idx+" > "+mSize);
        mIndex = idx;
        return Arrays.copyOfRange(mArray, mIndex-size, mIndex); 
    }
    
    public static int unit_test(int dbg) 
    {
        ArrayIterInt   ai = null;
        try {
            ArrayIterInt a1 = new ArrayIterInt(1);  ai = a1; // ok
            ai = new ArrayIterInt(0);   // wrong, but should it be?
            ai = new ArrayIterInt(-1);  // illegal
        } catch (IllegalArgumentException ex) {
            Sx.debug(dbg, "Caught expected error: " + ex.getMessage());
            //S.debug(sDbg, "cause: "  + ex.getCause());
            //return 0;
        }
        try {
            ai.append(-1);
            ai.appendArray(ai.mArray);
        } catch (IllegalArgumentException ex) {
            Sx.debug(dbg, "Caught expected error: " + ex.getMessage());
        }
        try {
            ai.appendRemoveAll(ai);
        }  catch (ArrayIndexOutOfBoundsException ex) {
            Sx.debug(dbg, "Caught expected error: " + ex.getMessage());
        }
        
        return 0;
    }
    
    public static void main(String[] args) { unit_test(0); }
    public int[] getArray() {
        return mArray;
    }
    public int getIndex() {
        return mIndex;
    }
    
    public int getSize() {
        return mSize;
    }
    
}
