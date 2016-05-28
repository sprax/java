package sprax.lists;

import java.util.Arrays;

import sprax.Sx;
import sprax.arrays.ArrayAlgo;

/**
 * TODO: Consider deriving SortableSinList, which maintains state flags
 * and keeps a small array of ints to use just for the insertion sort
 * or even the bucket sort, if the range is small. Make it's allocation
 * lazy but permanent.
 * 
 * @author sprax
 *
 */
public class SinList
{
    static int sSmallSize = SinLinkSort.sSmallSize;
    
    SinLink    mHead;
    SinLink    mTail;
    int        mSize;
    
    public SinList(SinLink link)
    {
        if (link == null) {
            mHead = mTail = null;
            mSize = 0;
            return;
        }
        mHead = link;
        if (mHead.mNext == null) {
            mTail = mHead;
            mSize = 1;
            return;
        }
        mTail = mHead.mNext;
        mSize = 2;
        while (mTail.mNext != null) {
            mSize++;
            mTail = mTail.mNext;
        }
    }
    
    protected SinList(SinLink head, SinLink tail, int length)
    {
        mHead = head;
        mTail = tail;
        mSize = length;
    }
    
    public SinLink getTail() {
        return mTail;
    }
    
    /**
     * re-find the tail, but don't re-set mTail or mSize.
     * This method exists only for testing. It should remain private.
     * 
     * @see reset()
     */
    private SinLink findTail() {
        if (mTail == null)
            mTail = mHead;
        if (mHead == null)
            return null;
        return mTail.findTail();
    }
    
    protected int findSize()
    {
        int size = 0;
        for (SinLink link = mHead; link != null; link = link.mNext)
            size++;
        return size;
    }
    
    /**
     * Start from the head mHead and traverse to the tail,
     * resetting mTail and mSize. A null head is handled,
     * but cycles are not.
     */
    void reset()
    {
        if (mHead == null) {
            mSize = 0;
            mTail = null;
            return;
        }
        mSize = 1;
        mTail = mHead;
        resetTail();
    }
    
    static void nullify(SinList list)
    {
        list.mHead = list.mTail = null;
        list.mSize = 0;
    }
    
    /**
     * Start from the nominal tail, traverse to actual tail,
     * resetting mTail and mSize. This method provides no error checking;
     * it should remain protected.
     */
    protected void resetTail()
    {
        while (mTail.mNext != null) {
            mSize++;
            mTail = mTail.mNext;
        }
    }
    
    public static SinList fromArray(final int[] array)
    {
        if (array == null || array.length == 0)
            return new SinList(null);
        
        int index = array.length - 1;
        SinLink tail = new SinLink(array[index], null);
        if (index == 0)
            return new SinList(tail, tail, 1);
        
        SinLink head = tail;
        for (; --index >= 0;) {
            SinLink temp = new SinLink(array[index], head);
            head = temp;
        }
        return new SinList(head, tail, array.length);
    }
    
    public static SinList fromArray(final char[] array)
    {
        if (array == null || array.length == 0)
            return new SinList(null);
        
        int index = array.length - 1;
        SinLink tail = new SinLink(array[index], null);
        if (index == 0)
            return new SinList(tail, tail, 1);
        
        SinLink head = tail;
        for (; --index >= 0;) {
            SinLink temp = new SinLink(array[index], head);
            head = temp;
        }
        return new SinList(head, tail, array.length);
    }
    
    public static SinList fromArray(final Character[] array)
    {
        if (array == null || array.length == 0)
            return new SinList(null);
        
        int index = array.length - 1;
        SinLink tail = new SinLink(array[index], null);
        if (index == 0)
            return new SinList(tail, tail, 1);
        
        SinLink head = tail;
        for (; --index >= 0;) {
            SinLink temp = new SinLink(array[index], head);
            head = temp;
        }
        return new SinList(head, tail, array.length);
    }
    
    public static SinList fromString(final String string) {
        if (string == null || string.length() == 0)
            return new SinList(null);
        char array[] = string.toCharArray();
        return fromArray(array);
    }
    
    // TODO: test these for mTail and mSize correctness!
    public SinList clone() {
        SinList newList = new SinList(null);
        if (mHead != null) {
            newList.mHead = mHead.clone();          // this list's head is not null, so clone it.
            SinLink oldNext = mHead.mNext;            // make copy for loop var
            SinLink newLink = newList.mHead;          // make loop control var
            while (oldNext != null) {
                newLink.mNext = oldNext.clone();        // newLink.mNext.mNext is now oldNext.mNext, but
                                                 // will be reset to the next cloned link or to null
                oldNext = oldNext.mNext;                // advance the original
                newLink = newLink.mNext;                // advance the copy
            }
            newList.mTail = newLink;
            newList.mSize = mSize;
        }
        return newList;
    }
    
    public SinList cloneA() {
        SinList newList = new SinList(null); // Don't 'correct' mSize
        if (mHead != null) {
            newList.mHead = mHead.clone();          // this list's head is not null, so clone it.
            SinLink oldNext = mHead.mNext;            // make copy for loop var
            SinLink newLink = newList.mHead;          // make loop control var
            while (oldNext != null) {
                newLink.mNext = new SinLink(oldNext.mData); // newLink.mNext.mNext is now null, but
                                                            // will be reset to the next cloned link
                                                            // or to null
                oldNext = oldNext.mNext;                    // advance the original
                newLink = newLink.mNext;                    // advance the copy
            }
        }
        return newList;
    }
    
    public SinList cloneB() {                // probably the least readable
        if (mHead == null)
            return new SinList(null);         // This 'corrects' mSize if it is not zero
        SinLink newLink = mHead.clone();
        SinLink oldNext = mHead.mNext;
        SinList newList = new SinList(newLink);
        while (oldNext != null) {
            newLink.mNext = oldNext.clone();
            oldNext = oldNext.mNext;
            newLink = newLink.mNext;
        }
        return newList;
    }
    
    /**
     * Set link as new head, increasing size by 1
     */
    public void prepend(SinLink link) {
        if (link != null) {
            if (mHead != null) {
                mHead.prepend(link);
                mSize++;
            } else {
                mHead = link;
                mSize = 1;
            }
        }
    }
    
    public void append(SinLink link)
    {
        if (link != null) {
            if (mTail != null) {
                mTail.mNext = link;
                mTail = link;
            } else {
                mHead = mTail = link;
                mSize = 1;
            }
            resetTail();
        }
    }
    
    /**
     * Append a node to the tail of this list. If the list is empty,
     * the node becomes the new head and tail.
     * 
     * @param link the node to become the new tail of the list
     * @deprecated this method is less readable than {@link #append(SinLink)}.
     */
    private void append_less_readable(SinLink link) { // deprecated
        if (link == null)
            return;
        if (mTail == null) {
            assert (mSize == 0);
            mHead = mTail = link;
            mSize = 1;
        } else {
            mTail.mNext = link;
            mTail = link;
        }
        resetTail();
    }
    
    public void insertLinkAt(SinLink node, int index) {
        if (node != null) {
            SinLink link = linkAt(index);
            if (link != null) {
                node.mNext = link.mNext;
                link.mNext = node;
                mSize++;
            } else if (mHead == null && index == 0) {
                mTail = mHead = node;
                mSize = 1;
            }
        }
    }
    
    /**
     * replaceLinkAt
     * Replace the indexed link in a list with the supplied node
     * 
     * @param node the non-null replacement node
     * @param index the position of the node to be replaced
     */
    public void replaceLinkAt(SinLink node, int index) {
        // Replace the reference, not just the data.
        // If the replacement node is null, do nothing.
        // If the list is empty or has only one link, and index == 0,
        // the replacement node becomes the whole list.
        // If index == the list's length, the node replaces the list's
        // terminating null node, so the replacement is effectively an append.
        if (node == null) {
            return;
        }
        if (index == 0) {
            if (mHead != null) {
                node.mNext = mHead.mNext;
                mHead.mNext = null;
                mHead = node;
                // size remains the same
            } else {
                mHead = node;
                mSize = 1;
            }
        } else if (0 < index && index < mSize) {
            // find the node before the one to be replaced, if it exists (that is,
            // if the list is long enough to contain it.)
            SinLink pred = linkAt(index - 1);
            assert (pred != null);
            SinLink link = pred.mNext;   // the link to be replaced
            node.mNext = link.mNext;
            pred.mNext = node;
            link.mNext = null;
            // size remains the same
        }
    }
    
    /**
     * replaceLinkAtEvenWithNull
     * Replace a node in a list with the supplied node, even if that is null.
     * This method is overly complicated and modal. It performs two different
     * function, depending on the parameters passed in. If the node parameter
     * is null, this method attempts to truncate the list at the position
     * specified by the index parameter. Otherwise, it attempts to replace
     * list[index] with node and fix up the links. These two functions should
     * be implemented in two separate methods.
     * 
     * @param node the replacement node or null
     * @param index the position of the node to be replaced (or where the list
     *            will be truncated).
     * @deprecated this method modal. Use {@link #replaceLinkAt(SinLink, int)}.
     */
    public void replaceLinkAtEvenWithNull(SinLink node, int index) {
        // Replace the reference, not just the data.
        // If the replacement node is null, this operation effectively truncates the list.
        if (index == 0) {
            if (mHead != null) {
                if (node != null) {
                    node.mNext = mHead.mNext;
                    mHead = node;
                    // size remains the same
                } else {
                    mHead = null;
                    mSize = 0;
                }
            }
        } else if (0 < index && index < mSize) {
            // find the node before the one to be replaced, if it exists (that is,
            // if the list is long enough)
            SinLink pred = linkAt(index - 1);
            assert (pred != null);
            SinLink link = pred.mNext;   // the link to be replaced
            if (node != null) {
                node.mNext = link.mNext;
                pred.mNext = node;
                link.mNext = null;
                // size remains the same
            } else {
                pred.mNext = null;
                link.mNext = null;
                mSize = index;
            }
        }
    }
    
    SinLink linkAt(int index) {
        SinLink link = null;
        if (0 <= index && index < mSize) {
            link = mHead;
            for (int j = 0; j <= index; j++) {
                link = link.mNext;
            }
        }
        return link;
    }
    
    public static void print(SinList list) {
        SinLink.printList(list.mHead);
    }
    
    public int removeLink(SinLink link) {
        if (link != null && mHead != null) {
            if (link == mHead) {
                mHead = mHead.mNext;
                mSize--;
                return 1;
            }
            for (SinLink iter = mHead; iter.mNext != null; iter = iter.mNext) {
                if (iter.mNext == link) {
                    iter.mNext = link.mNext;
                    link.mNext = null;
                    mSize--;
                    return 1;
                }
            }
        }
        return 0;
    }
    
    public int insertLinkAfter(SinLink link, SinLink position) {
        // Beware: if link is already in the list, this "moves" it, and can create a cycle or
        // short-circuit.
        assert (link != null);
        if (position == null) {
            link.mNext = mHead;
            mHead = link;
            mSize++;
            return 1;
        }
        for (SinLink iter = mHead; iter != null; iter = iter.mNext) {
            if (iter == position) {
                SinLink temp = iter.mNext;
                iter.mNext = link;
                link.mNext = temp;
                mSize++;
                return 1;
            }
        }
        return 0;
    }
    
    public boolean containsLink(SinLink link) {
        for (SinLink iter = mHead; iter != null; iter = iter.mNext) {
            if (iter == link) {
                return true;
            }
        }
        return false;
    }
    
    public int insertLinkAfterSafe(SinLink link, SinLink position) {
        // Safe: if link is already in the list, remove it first to prevent cycle or short-circuit.
        assert (link != null);
        if (containsLink(link)) {
            removeLink(link);
        }
        if (position == null) {
            link.mNext = mHead;
            mHead = link;
            mSize++;
            return 1;
        }
        for (SinLink iter = mHead; iter != null; iter = iter.mNext) {
            if (iter == position) {
                SinLink temp = iter.mNext;
                iter.mNext = link;
                link.mNext = temp;
                mSize++;
                return 1;
            }
        }
        return 0;
    }
    
    /**
     ******************************************************************************
     */
    protected static int test_addRemove()
    {
        Sx.puts(SinList.class.getName() + ".main");
        System.out.println("Alphabet to 5");
        SinList sll = new SinList(SinLink.initAlphabetAppend(5));
        print(sll);
        System.out.println("Remove the head link:");
        SinLink linkA = sll.mHead;
        SinLink linkB = linkA.mNext;
        SinLink linkC = linkB.mNext;
        SinLink linkD = linkC.mNext;
        sll.removeLink(linkA);
        print(sll);
        System.out.println("Insert the A link after C link:");
        sll.insertLinkAfter(linkA, linkC);
        print(sll);
        System.out.println("Insert the B link after D link:");
        sll.insertLinkAfterSafe(linkB, linkD);
        print(sll);
        System.out.println("Clone:");
        try {
            SinList tll = (SinList) sll.clone();
            print(tll);
        } catch (Exception e) {
            System.out.println(e);
        }
        
        System.out.println("CloneA:");
        SinList ull = (SinList) sll.cloneA();
        print(ull);
        
        System.out.println("CloneB:");
        SinList vll = (SinList) sll.cloneB();
        print(vll);
        
        return 0;
    }
    
    /**
     * For N < 10 or so, a few in-place swaps or an insertion sort
     * and re-copy into the list beats all.
     * This method does not change the link structure of the list;
     * it only moves the values around. Thus mHead and mTail remain
     * in their same places, with invariant object IDs. Only their
     * data payload may change.
     * a sub-list.
     * 
     * @param head first link in (sub)list
     * @param size size of (size)list
     */
    protected void smallSort()
    {
        switch (mSize) {
        case 0:
        case 1:
            return;
        case 2:
            if (mHead.mData > mHead.mNext.mData)
                SinLink.swapValues(mHead, mHead.mNext);
            return;
        case 3:
            SinLink next = mHead.mNext;
            SinLink last = next.mNext;
            if (mHead.mData > next.mData)
                SinLink.swapValues(mHead, next);
            if (next.mData > last.mData) {
                SinLink.swapValues(next, last);
                if (mHead.mData > next.mData)
                    SinLink.swapValues(mHead, next);
            }
            return;
        default:
            int vals[] = new int[mSize];
            vals[0] = mHead.mData;
            SinLink link = mHead.mNext;
            for (int j = 1; j < mSize; j++) {
                int k, val = link.mData;
                link = link.mNext;
                for (k = j; k > 0 && val < vals[k - 1]; k--) {
                    vals[k] = vals[k - 1];
                }
                vals[k] = val;
            }
            link = mHead;
            for (int j = 0; j < mSize; j++) {
                link.mData = vals[j];
                link = link.mNext;
            }
            return;
        }
    }
    
    public void arraysSort()
    {
        // As a last resort, copy all values into an array and
        // use the built-in or "system" sort, which is probably
        // some optimized version of quicksort or mergesort.
        int vals[] = new int[mSize];
        SinLink link = mHead;
        for (int j = 0; j < mSize; j++) {
            vals[j] = link.mData;
            link = link.mNext;
        }
        Arrays.sort(vals);
        link = mHead;
        for (int j = 0; j < mSize; j++) {
            link.mData = vals[j];
            link = link.mNext;
        }
    }
    
    /**
     * Merge another sorted list into this one.
     * If the merge changes this list, it explicitly
     * nullifies list B, since in general it will be
     * invalidated (its mTail may no longer be the true tail,
     * and its mSize may not be the true chain length).
     * 
     * @param listB the other list
     * @return
     */
    protected void mergeOtherSortedList(SinList listB)
    {
        if (listB == null || listB.mSize < 1)
            return;
        if (mHead == null || mSize < 1) {
            mHead = listB.mHead;
            mTail = listB.mTail;
            mSize = listB.mSize;
            return;
        }
        SinLink[] headAndTail = { mHead, mTail };
        if (mHead.mData <= listB.mHead.mData) {
            mergeSortedListsNiece(headAndTail, listB.mHead, listB.mTail);
        } else {
            headAndTail[0] = listB.mHead;
            headAndTail[1] = listB.mTail;
            mergeSortedListsNiece(headAndTail, mHead, mTail);
        }
        // re-initialize this list to the merge results
        mHead = headAndTail[0];
        mTail = headAndTail[1];
        mSize = mSize + listB.mSize;
        nullify(listB);
    }
    
    protected void mergeOtherSortedListNiece(SinList listB)
    {
        SinLink[] headAndTail = { mHead, mTail };
        if (mHead.mData <= listB.mHead.mData) {
            mergeSortedListsNiece(headAndTail, listB.mHead, listB.mTail);
        } else {
            headAndTail[0] = listB.mHead;
            headAndTail[1] = listB.mTail;
            mergeSortedListsNiece(headAndTail, mHead, mTail);
        }
        // re-initialize this list to the merge results
        mHead = headAndTail[0];
        mTail = headAndTail[1];
        mSize = mSize + listB.mSize;
        // nullify(listB);
    }
    
    /**
     * Merge two sorted linked lists and return the result,
     * which will be a list of length sizeA + sizeB
     * 
     * @param headA
     * @param headB
     * @param tailA
     * @param tailB
     * @param sizeA
     * @param sizeB
     * @return
     */
    public static void mergeSortedListsNiece(SinLink[] headAndTail, SinLink headB, SinLink tailB)
    {
        SinLink headA = headAndTail[0];
        SinLink tailA = headAndTail[1];
        
        // If the greatest entry in A <= the least entry in B, join and return.
        if (headA.mData > headB.mData)
            throw new IllegalArgumentException("list A head val > list B head val: " + headA.mData
                    + " > " + headB.mData);
        
        if (tailA.mData <= headB.mData) {
            tailA.mNext = headB;
            // headAndTail[0] = headA; // This is already true.
            headAndTail[1] = tailB;
            return;
        }
        
        // We already know that headA is the minimal entry, so initialize linkZ
        // (the "zipper" cursor) to headA and then advance headA one place.
        for (SinLink linkZ = headA, linkA = headA.mNext, linkB = headB;; linkZ = linkZ.mNext) {
            if (linkA.mData <= linkB.mData) {
                linkZ.mNext = linkA;
                if (linkA.mNext == null) {
                    linkA.mNext = linkB;
                    headAndTail[1] = tailB;
                    break;
                } else {
                    linkA = linkA.mNext;
                }
            } else {
                linkZ.mNext = linkB;
                if (linkB.mNext == null) {
                    linkB.mNext = linkA;
                    // headAndTail[1] = tailA; // This is already true.
                    break;
                } else {
                    linkB = linkB.mNext;
                }
            }
        }
    }
    
    /**
     ******************************************************************************
     */
    static void testSortAndPrintX(SinList slist)
    {
        int oldSize = slist.mSize;
        Sx.print("in:  ");
        print(slist);
        slist.sort();
        Sx.print("out: ");
        print(slist);
        boolean bSorted = verifySorted(slist);
        SinLink tailF = slist.findTail();
        int newSize = slist.findSize();
        if (tailF != slist.mTail || !bSorted)
            Sx.format("FAILURE X:   sorted: %s   size<%d  %d  %d>  tail<%d  %d> O <%s  %s>\n"
                    , bSorted, oldSize, newSize, slist.mSize, tailF.mData, slist.mTail.mData
                    , tailF, slist.mTail);
    }
    
    static void testSortAndPrintY(SinList slist)
    {
        int oldSize = slist.mSize;
        slist.sortY();
        Sx.print("YYY: ");
        print(slist);
        boolean bSorted = verifySorted(slist);
        SinLink tailF = slist.findTail();
        int newSize = slist.findSize();
        if (!bSorted || oldSize != newSize || oldSize != slist.mSize || tailF != slist.mTail)
            Sx.format("FAILURE Y:   sorted: %s   size<%d  %d  %d>  tail<%d  %d> O <%s  %s>\n"
                    , bSorted, oldSize, newSize, slist.mSize, tailF.mData, slist.mTail.mData
                    , tailF, slist.mTail);
    }
    
    static void testSortAndPrintZ(SinList slist)
    {
        int oldSize = slist.mSize;
        slist.sortZ();
        Sx.print("ZZZ: ");
        print(slist);
        boolean bSorted = verifySorted(slist);
        SinLink tailF = slist.findTail();
        int newSize = slist.findSize();
        if (!bSorted || oldSize != newSize || oldSize != slist.mSize || tailF != slist.mTail)
            Sx.format("FAILURE Z:   sorted: %s   size<%d  %d  %d>  tail<%d  %d> O <%s  %s>\n"
                    , bSorted, oldSize, newSize, slist.mSize, tailF.mData, slist.mTail.mData
                    , tailF, slist.mTail);
    }
    
    static void sortAndPrint(String str)
    {
        SinList sinListX = SinList.fromString(str);
        testSortAndPrintX(sinListX);
        SinList sinListY = SinList.fromString(str);
        testSortAndPrintY(sinListY);
        SinList sinListZ = SinList.fromString(str);
        testSortAndPrintZ(sinListZ);
    }
    
    public static boolean verifySorted(SinList sinList)
    {
        if (sinList == null)
            return false;
        return sinList.verifySorted();
    }
    
    public boolean verifySorted()
    {
        for (SinLink link = mHead, next = link.mNext; next != null; link = next, next = next.mNext) {
            if (link.mData > next.mData)
                return false;
        }
        return true;
    }
    
    public static boolean verifySorted(SinLink head, int size)
    {
        if (head == null || size < 1)
            return false;
        int j = 1;
        for (SinLink link = head, next = link.mNext; next != null; link = next, next = next.mNext) {
            if (link.mData > next.mData)
                return false;
            if (++j >= size)
                return true;
        }
        return true;
    }
    
    public void sortY()
    {
        sortY(this);
    }
    
    /**
     * Static method creates a new list to contain the supplied list's
     * second half, temporarily truncates the supplied list to just its
     * first half, sorts the two halves independently, them merges them
     * back into the supplied list.
     * 
     * @param listQ
     */
    public static void sortY(SinList listQ) // TODO: declare as null...
    {
        int sizeQ = listQ.mSize;
        if (sizeQ < sSmallSize) {
            // For N < 10 or so, a few swaps or an insertion sort beats all.
            // smallSort sorts a list in-place; so the head and tail don't change.
            listQ.smallSort();
            return;
        }
        
        // Find the max and min values and the middle link
        SinLink headQ = listQ.mHead;
        int maxVal = headQ.mData;
        int minVal = maxVal;
        int sizeA = 1, halfSize = (sizeQ + 1) / 2; // Actually, sizeA will become halfSize.
        SinLink tailA = headQ.mNext;
        while (true) {
            if (maxVal < tailA.mData)
                maxVal = tailA.mData;
            else if (minVal > tailA.mData)
                minVal = tailA.mData;
            if (++sizeA >= halfSize)
                break;
            else
                tailA = tailA.mNext;
        }
        SinLink headB = tailA.mNext;
        SinLink tailB = headB;
        assert (tailB != null);
        while (true) {
            if (maxVal < tailB.mData)
                maxVal = tailB.mData;
            else if (minVal > tailB.mData)
                minVal = tailB.mData;
            if (tailB.mNext == null)
                break;
            tailB = tailB.mNext;
        }
        assert (listQ.mTail == tailB);
        
        // If the range is not much bigger than the number of entries,
        // use bucket sort. This puts the sorted values back in-place
        // in the linked list, so head, tail, and size do not change.
        int range = maxVal - minVal + 1;
        double NlogN = sizeQ * Math.log(sizeQ);
        if (range < NlogN) {
            // Use bucket sort, which is O(N)
            int bins[] = new int[range];
            SinLink link = headQ;
            for (link = headQ; link != null; link = link.mNext)
                bins[link.mData - minVal]++;
            
            // Copy the sorted values back into the linked list.
            link = headQ;
            for (int j = 0; j < range; j++) {
                while (--bins[j] >= 0) {
                    link.mData = j + minVal;
                    link = link.mNext;
                }
            }
            return;
        }
        
        // Otherwise, use a modified merge sort: sort the first and second halves
        // of the supplied list independently, and return the merged results.
        // The modification to pre-compute which sorted half starts lower, and
        // if the tail of this lower list is not greater than the head of the
        // higher list, we simply append the higher list onto the tail of the
        // lower list and return, omitting the unneeded merge.
        //
        // First, to finish separating the first and second halves, truncate the
        // original list at tailA. Also compute the size of the second half.
        tailA.mNext = null;
        listQ.mTail = tailA;
        listQ.mSize = sizeA;
        int sizeB = sizeQ - sizeA;
        
        // Sort the first and second halves independently.
        // Note that calling sort invalidates the cached list heads headQ and headB
        SinList.sortY(listQ);
        headQ = listQ.mHead;
        
        SinList listB = new SinList(headB, tailB, sizeB);
        SinList.sortY(listB);
        headB = listB.mHead;    // Calling sortY(listB) may have changed this via merge.
        
        // Here is a little "trick" that keeps the sort stable.
        // If the head link in listA has a greater value than
        // that in listB, remove the head of list B and prepend
        // it to list A. Both listA and listB remain sorted, but
        // now listA begins with the minimal element, and can
        // proceed immediately to the merge. (Calling the merge
        // method with swapped arguments would be destabilizing.)
        
        if (headQ.mData > headB.mData) {
            SinLink headX = headB;
            listB.mHead = headB.mNext;
            listB.mSize -= 1;
            headX.mNext = headQ;
            listQ.mHead = headX;
            listQ.mSize += 1;
        }
        mergeSortedListsNiece(listQ, listB);
        // listQ.mergeOtherSortedList(listB);
    }
    
    /**
     * No calls to new
     */
    public void sortZ()
    {
        if (mSize < sSmallSize) {
            // For N < 10 or so, a few swaps or an insertion sort beats all.
            // smallSort sorts a list in-place; so the head and tail don't change.
            smallSort();
            return;
        }
        
        // Find the max and min values and the middle link
        int maxVal = mHead.mData;
        int minVal = maxVal;
        int sizeA = 1, halfSize = (mSize + 1) / 2; // Actually, sizeA will become halfSize.
        SinLink tailA = mHead.mNext;
        while (true) {
            if (maxVal < tailA.mData)
                maxVal = tailA.mData;
            else if (minVal > tailA.mData)
                minVal = tailA.mData;
            if (++sizeA >= halfSize)
                break;
            else
                tailA = tailA.mNext;
        }
        SinLink headB = tailA.mNext;
        SinLink tailB = headB;
        assert (tailB != null);
        while (true) {
            if (maxVal < tailB.mData)
                maxVal = tailB.mData;
            else if (minVal > tailB.mData)
                minVal = tailB.mData;
            if (tailB.mNext == null)
                break;
            tailB = tailB.mNext;
        }
        assert (mTail == tailB);
        
        // If the range is not much bigger than the number of entries,
        // use bucket sort. This puts the sorted values back in-place
        // in the linked list, so head, tail, and size do not change.
        int range = maxVal - minVal + 1;
        double NlogN = mSize * Math.log(mSize);
        if (range < NlogN) {
            // Use bucket sort, which is O(N)
            int bins[] = new int[range];
            SinLink link = mHead;
            for (link = mHead; link != null; link = link.mNext)
                bins[link.mData - minVal]++;
            
            // Copy the sorted values back into the linked list.
            link = mHead;
            for (int j = 0; j < range; j++) {
                while (--bins[j] >= 0) {
                    link.mData = j + minVal;
                    link = link.mNext;
                }
            }
            return;
        }
        
        // Otherwise, use a modified merge sort: sort the first and second halves
        // of the supplied list independently, and return the merged results.
        // The modification to pre-compute which sorted half starts lower, and
        // if the tail of this lower list is not greater than the head of the
        // higher list, we simply append the higher list onto the tail of the
        // lower list and return, omitting the unneeded merge.
        //
        // First, to finish separating the first and second halves, truncate the
        // original list at tailA. Also compute the size of the second half.
        
        // Save *this* list's head; we already have its original size and tail.
        SinLink headA = mHead;
        
        // Temporarily replace original list with just its second half:
        int sizeB = mSize - sizeA;
        mHead = headB;
        mTail = tailB;
        mSize = sizeB;
        sortZ();
        headB = mHead;
        tailB = mTail;
        
        tailA.mNext = null;
        
        // Temporarily replace this list with just its first half and sort it.
        mHead = headA;
        mTail = tailA;
        mSize = sizeA;
        sortZ();
        
        // Here is a little "trick" that keeps the sort stable.
        // If the head link in listA has a greater value than
        // that in listB, remove the head of list B and prepend
        // it to list A. Both listA and listB remain sorted, but
        // now listA begins with the minimal element, and can
        // proceed immediately to the merge. (Calling the merge
        // method with swapped arguments would be destabilizing.)
        if (mHead.mData > headB.mData) {
            SinLink headX = headB;
            headB = headB.mNext;
            sizeB -= 1;
            headX.mNext = mHead;
            mHead = headX;
            mSize += 1;
        }
        mergeOtherSortedList(headB, tailB, sizeB);
    }
    
    /**
     * Merge listB into listA, so that listA ends up with all the
     * links, and listB may be invalidated.
     * 
     * @param listA
     * @param listB
     */
    protected void mergeOtherSortedList(SinLink headB, SinLink tailB, int sizeB)
    {
        SinLink headA = mHead;
        SinLink tailA = mTail;
        
        // The list heads must be pre-sorted
        if (headA.mData > headB.mData)
            throw new IllegalArgumentException("list A head val > list B head val: " + headA.mData
                    + " > " + headB.mData);
        
        // If the greatest entry in A <= the least entry in B, join and return.
        if (tailA.mData <= headB.mData) {
            mTail.mNext = headB;
            mTail = tailB;
            mSize += sizeB;
            return;
        }
        
        // We already know that headA is the minimal entry, so initialize linkZ
        // (the "zipper" cursor) to headA and then advance headA one place.
        for (SinLink linkZ = headA, linkA = headA.mNext, linkB = headB;; linkZ = linkZ.mNext) {
            if (linkA.mData <= linkB.mData) {
                linkZ.mNext = linkA;
                if (linkA.mNext == null) {
                    linkA.mNext = linkB;
                    mTail = tailB;
                    break;
                } else {
                    linkA = linkA.mNext;
                }
            } else {
                linkZ.mNext = linkB;
                if (linkB.mNext == null) {
                    linkB.mNext = linkA;
                    break;
                } else {
                    linkB = linkB.mNext;
                }
            }
        }
        mSize += sizeB;
    }
    
    /**
     * Merge listB into listA, so that listA ends up with all the
     * links, and listB may be invalidated.
     * 
     * @param listA
     * @param listB
     */
    protected static void mergeSortedListsNiece(SinList listA, SinList listB)
    {
        SinLink headA = listA.mHead;
        SinLink tailA = listA.mTail;
        SinLink headB = listB.mHead;
        SinLink tailB = listB.mTail;
        
        // The list heads must be pre-sorted
        if (headA.mData > headB.mData)
            throw new IllegalArgumentException("list A head val > list B head val: " + headA.mData
                    + " > " + headB.mData);
        
        // If the greatest entry in A <= the least entry in B, join and return.
        if (tailA.mData <= headB.mData) {
            listA.mTail.mNext = headB;
            listA.mTail = tailB;
            listA.mSize += listB.mSize;
            return;
        }
        
        // We already know that headA is the minimal entry, so initialize linkZ
        // (the "zipper" cursor) to headA and then advance headA one place.
        for (SinLink linkZ = headA, linkA = headA.mNext, linkB = headB;; linkZ = linkZ.mNext) {
            if (linkA.mData <= linkB.mData) {
                linkZ.mNext = linkA;
                if (linkA.mNext == null) {
                    linkA.mNext = linkB;
                    listA.mTail = tailB;
                    break;
                } else {
                    linkA = linkA.mNext;
                }
            } else {
                linkZ.mNext = linkB;
                if (linkB.mNext == null) {
                    linkB.mNext = linkA;
                    break;
                } else {
                    linkB = linkB.mNext;
                }
            }
        }
        listA.mSize += listB.mSize;
    }
    
    /**
     * From the JavaDoc on Collections.sort():
     * The sorting algorithm is a modified mergesort (in which the merge
     * is omitted if the highest element in the low sublist is less than
     * the lowest element in the high sublist). This algorithm offers
     * guaranteed n log(n) performance. This implementation dumps the
     * specified list into an array, sorts the array, and iterates over
     * the list resetting each element from the corresponding position
     * in the array. This avoids the n2 log(n) performance that would
     * result from attempting to sort a linked list in place.
     */
    public void sort()
    {
        // Use heuristics based on size and range to decide on which
        // algorithm to use.
        
        SinLink headAndTail[] = { mHead, mTail };
        SinLinkSort.sort(headAndTail, mSize);
        mHead = headAndTail[0];
        mTail = headAndTail[1];
    }
    
    protected static int test_sort()
    {
        String strA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String stra = strA.toLowerCase();
        char[] chrZ = strA.toCharArray();
        ArrayAlgo.reverseArray(chrZ);
        String strZ = new String(chrZ);
        char[] chrz = stra.toCharArray();
        ArrayAlgo.reverseArray(chrZ);
        String strz = new String(chrz);
        String strZz = strZ + "-" + strz;
        
        SinList sinListA, sinListB;
        sinListA = SinList.fromString("JLPQRSTVW");
        // sinListA = SinList.fromString("01236789ACEKMOQR");
        SinLink tailA = new SinLink('W');
        sinListA.append(tailA);
        sinListB = SinList.fromString("KMNORSUVX");
        // sinListB = SinList.fromString("45ABCDEFGHIJLNPQSU");
        SinLink tailB = new SinLink('Z');
        sinListB.append(tailB);
        SinLink headAndTailA[] = { sinListA.mHead, tailA };
        mergeSortedListsNiece(headAndTailA, sinListB.mHead, sinListB.mTail);
        sinListA.mHead = headAndTailA[0];
        sinListA.mTail = headAndTailA[1];
        boolean bSorted = SinList.verifySorted(sinListA.mHead, sinListA.mSize + sinListB.mSize);
        SinLink.printList(sinListA.mHead);
        Sx.puts(" Sorted == " + bSorted);
        
        sortAndPrint("BA");
        sortAndPrint("ZYX");
        sortAndPrint("ZCZA");
        sortAndPrint("BAXW");
        sortAndPrint("XWBA");
        sortAndPrint("XFFXXF");
        sortAndPrint("ZG4CBA0");
        sortAndPrint("XG4EDCBA0");
        sortAndPrint("9876543210");
        sortAndPrint("FEDCBAKJXYZ45");
        sortAndPrint(strZ);
        sortAndPrint(strZz);
        
        // SinLink headM = SinList.mergeSortedListsNiece(sinListA.mHead, sinListB.mHead,
        // sinListA.mSize, sinListB.mSize);
        // SinLink.printList(headM);
        
        // error checking
        SinLinkSort.mergeSortedLists(sinListA.mHead, null, sinListA.mSize, 0);
        SinLinkSort.mergeSortedLists(null, sinListB.mHead, 0, sinListB.mSize);
        SinLinkSort.mergeSortedLists(null, null, 0, 0);
        
        sortAndPrint("ZY1XV0");
        sortAndPrint("ZY1X2V0");
        return 0;
    }
    
    public static void main(String[] args)
    {
        test_addRemove();
        test_sort();
    }
}
