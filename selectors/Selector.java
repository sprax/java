package sprax.selectors;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Models selection as operations on subsets of a range of
 * indices.
 * 
 * May be used to represent an ordered set of binary switches,
 * such as an array of floor-stop buttons in an elevator,
 * or the check-boxes next to a list of songs, or the
 * selection within a list of files. 
 * 
 * The core methods are select and deselect.  
 * The concept of a current or "active" selection item may useful
 * in some models and not in others.  For instance, if all 
 * "switches" can be processed in parallel, the concept of
 * one switch being active at a time may not make any sense.
 * 
 * TODO: Add selectLessThan(int key), selectNotLessThan(int key)
 * TODO: Add select(Iterator<Integer> iter)
 * TODO: Add deselect(Iterator<Integer> iter)
 * @author sprax
 *
 */
public interface Selector 
{
    //////// setters ////////
    
    /** 
     * Select the specified index, if it is in range.
     * Return true for success; that is, if this index
     * was not already selected, but is selected now, 
     * return true.  If it was already selected, or 
     * cannot be selected, return false. 
     */
    public boolean  select(int index);
    
    /** 
     * De-select the specified index, if it was selected.
     * Return true for success; that is, if this index
     * was selected, make it not selected and return true.  
     * If it was now already selected, return false. 
     */
    public boolean  deselect(int index);
    
    /** 
     * Selects all.
     * Returns true if some indices were not already selected.
     * Otherwise, returns false.
     */
    public boolean  selectAll();
    
    /** 
     * Deselects all.  
     * Returns true if some indices had been previously selected.
     * Otherwise, returns false.
     */
    public boolean  deselectAll();
    
    /** 
     * Sets the current or "active" index, not necessarily the only
     * selected index.  Returns true IFF this index was not already
     * selected; otherwise false.  Thus includes false for the case
     * when this index was selected already, but was not the current
     * selection.
     * 
     * May throw IllegalArgumentException if index is out of range.
     */
    public boolean	setCurrent(int index);
    
    /** 
     * Un-sets the current or "active" index, leaving none.
     * Following this with an immediate call to getCurrent will get null.
     * This does not imply a delectAll operation.
     */
    public boolean	setNoCurrent();
    
    ////////  getters ////////
    /** 
     * Index of current or "active" item.  This is a value in the underlying
     * or implied selection range.  It is not an index into the container of 
     * all currently selected indices -- it is one of those contained
     * indices, if there are any, or it is mMin if no indices are selected.
     * It cannot be null, and there is no invalid index, so it must be a
     * value in the range.  
     * @return
     */
    public Integer  getCurrent();
    
    /** Gets a copy of all selected indices as a compact array of int. */
    public int[]    allSelected();
    
    /** Gets a copy of all non-selected indices as a compact array of int. */
    public int[]    nonSelected();
    
    /** smallest selected index (or index of least selected value) */
    public Integer  smallest();
    
    /** Greatest selected index, if any.  Otherwise, null. */
    public Integer  greatest();
    
    /** 
     * Returns the next selected index greater than the current or "active"
     * index.  Or, if there is no current index, it returns the smallest
     * selected index, if any.  Otherwise, it returns null. 
     */
    public Integer  nextGreater();
    
    /** Returns the next selected index greater than the specified index */
    public Integer  nextGreater(int from);
    
    /** 
     * Returns the next selected index smaller than the current or "active"
     *  index.  Or, if there is not current index, it returns the greatest
     *  selected index, if any.  Otherwise, it returns null. 
     */
    public Integer  nextSmaller();
    
    /** Returns the next selected index smaller than the specified index */
    public Integer  nextSmaller(int from);
    
    /** Returns the number of selected indices */
    public int			numSelected();
    
    /** Returns the number of selected indices less than key (its rank). */
    public int      numSmaller(int key);
    
    /** Returns the number of selected indices not less than key. */
    public int 			numNotSmaller(int key);
    
    /** Returns the number of selected indices less than key (its rank). */
    public int      numGreater(int key);
    
    
    //////// order of operations methods: leave these to a decorator ////////
    
    /** index of item selected first in time: may be non-trivial to maintain */
    //  public int  firstSelected();
    
    /** index of item selected last in time: may be non-trivial to maintain */
    //  public int  lastSelected();
}
