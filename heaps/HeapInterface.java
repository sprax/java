/**
 * 
 */
package sprax.heaps;

/**
 * @author sprax
 *
 */
public interface HeapInterface<T>
{
    public boolean  add(T t);
    public boolean  contains(T t);
    public int		findFirstIndexOf(T t);		// -1 if not found
    public T        remove();
    public boolean	remove(T t);
    public T        get(int index);
    public T		peek();
    public int      size();
    public void     heapify();
    public void     sort();
    public void     sortAscend();
    public void     sortDescend();
    
    public boolean  isHeap();
    public boolean  isSortedAscending();
    public boolean  isSortedDescending();
}
