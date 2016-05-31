/*
 *  Copyright 2006-2007 Columbia University.
 *
 *  This file is part of MEAPsoft.
 *
 *  MEAPsoft is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  MEAPsoft is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MEAPsoft; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA
 *
 *  See the file "COPYING" for the text of the license.
 */
package sprax.heaps;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;


public abstract class HeapCu<T> extends Vector<T>
{
    // Comparator to use to compare two elements in this Heap (if this
    // is null, assume that all elements are Comparable)
    private Comparator<T> comp = null;

    // Does the current instance obey the heap property?
    // (all operations aside from sort() are guaranteed to maintain
    // the heap property, this is just to keep track of whether or not
    // sort() has screwed stuff up).
    protected boolean isHeap = true;

    public HeapCu()
    {
        super();
    }

    public HeapCu(Comparator<T> c)
    {
        super();
        comp = c;
    }

    public HeapCu(int capacity)
    {
        super(capacity);
    }

    public HeapCu(Collection<T> c)
    {
        super();
        addAll(c);
    }
    
    public Iterator<T> iterator() {
      return super.iterator();  // TODO: does not work.
    }

    public T remove(int index)
    {
        if(!isHeap)
            rebuildHeap();

        T o = get(index);

        set(index, get(size()-1));
        removeElementAt(size()-1);

        heapify(index);

        return o;
    }

    public boolean remove(Object o)
    {
        boolean found = false;
        for(int i = 0; i < size(); i++)
        {
            if(o == null ? get(i) == null : o.equals(get(i)))
            {
                found = true;
                remove(i);

                break;
            }
        }

        return found;
    }

    public boolean add(T o)
    {
        if(!isHeap)
            rebuildHeap();

        boolean b = super.add(o);

        for(int node = size()-1; node > 0;)
        {
            int parent = (int)((node-1)/2);

            if(cmp(node, parent) < 0)
            {
                // swap them and reheapify
                T tmp = get(node);
                set(node, get(parent));
                set(parent, tmp);
            }

            node = parent;
        }

        //System.out.print("\nContents: ");
        //for(int x = 0; x < size(); x++)
        //    System.out.print(get(x) + " ");
        //System.out.println();

        return b;
    }


    /*
    public boolean addAll(Collection<T> c)
    {
        boolean b = super.addAll(c);

        rebuildHeap();

        return(b);
    }
    */

    public void rebuildHeap()
    {
        // do the whole linear time build-heap thing
        for(int i = (int)(size()/2); i >= 0; i--)
            heapify(i);

        isHeap = true; 
    }

    public void sort()
    {
        T[] a = (T[]) toArray();
        if(comp == null)
            Arrays.sort(a);
        else
            Arrays.sort(a, comp);

        elementData = a;

        // there is some wierdo off by one error here that I cannot find...
        //for(int x = size()-1; x > 0; x--)
        //{
        //    // swap end of heap with the root, then heapify whats
        //    // left.
        //    Object tmp = get(x);
        //    set(x, get(0));
        //    set(0, tmp);
        //
        //    heapify(0, x);
        //}           

        // the above code destroys the heap property - the array is
        // essentially in reverse sorted order (with respect to the
        // first element in the heap (min if MinHeap, max if MaxHeap))
        //
        // The next call to one of the Heap methods will rebuild the
        // heap.
        isHeap = false; 
    }

    protected int cmp(int node1, int node2)
    {
        int c = 0; 
        if(comp != null)
            c = comp.compare(get(node1), get(node2));
        else
            c = ((Comparable<T>)get(node1)).compareTo(get(node2));

        return c;
    }

    private void heapify(int node, int size)
    {
        if(node > size)
            return;

        int left = (node+1)*2-1;
        int right = (node+1)*2;

        int minidx = node;
        
        if(left < size && cmp(left, node) <= 0) 
            minidx = left;
        if(right < size && cmp(right, node) <= 0 && cmp(right, left) <= 0)
            minidx = right;

        if(minidx != node)
        {
            // swap them and recurse on the subtree rooted at minidx
            T tmp = get(node);
            set(node, get(minidx));
            set(minidx, tmp);
            
            heapify(minidx, size);
        }
    }

    private void heapify(int node)
    {
        heapify(node, size());
    }

    public boolean isHeap()
    {
        return isHeap;
    }
}

