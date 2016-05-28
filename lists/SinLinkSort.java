package sprax.lists;

import sprax.Sx;
import sprax.arrays.ArrayAlgo;

public class SinLinkSort
{
    static int     sSmallSize       = 4;
    static boolean sbUseSinLinkSort = false;
    
    public static SinLink sort(SinLink list, int size)
    {
        // Use heuristics based on size and range to decide which algorithm to use.
        
        if (size < sSmallSize) {
            // For N < 10 or so, a few swaps or an insertion sort beats all.
            smallSort(list, size);
            return list;
        }
        
        // We already have the size; let's get min and max and middle link
        int maxVal = list.mData;
        int minVal = maxVal;
        int sizeA = 1, halfSize = (size + 1) / 2; // Actually, sizeA will become halfSize.
        SinLink tailA = list.mNext;
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
        SinLink listB = tailA.mNext;
        SinLink tailB = listB;
        while (true) {
            if (maxVal < tailB.mData)
                maxVal = tailB.mData;
            else if (minVal > tailB.mData)
                minVal = tailB.mData;
            if (tailB.mNext == null)
                break;
            tailB = tailB.mNext;
        }
        int range = maxVal - minVal + 1;
        double NlogN = size * Math.log(size);
        if (range < NlogN) {
            // Use bucket sort, which is O(N)
            int bins[] = new int[range];
            SinLink link = list;
            for (link = list; link != null; link = link.mNext)
                bins[link.mData - minVal]++;
            
            link = list;
            for (int j = 0; j < range; j++) {
                while (--bins[j] >= 0) {
                    link.mData = j + minVal;
                    link = link.mNext;
                }
            }
            return list;
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
        SinLink listA = list;
        tailA.mNext = null;
        int sizeB = size - sizeA;
        listA = sort(listA, sizeA);
        listB = sort(listB, sizeB);
        if (listA.mData <= listB.mData) {
            SinLink tailO = tailA;
            tailA = listA.findTail();
            if (tailO != tailA)
                sizeA = 1 + sizeA - 1;
            return mergeSortedListsNiece(listA, tailA, listB, sizeA, sizeB);
        } else {
            SinLink tailO = tailB;
            tailB = listB.findTail();
            if (tailO != tailB)
                sizeB = 1 + sizeB - 1;
            return mergeSortedListsNiece(listB, tailB, listA, sizeB, sizeA);
        }
    }
    
    /**
     * For N < 10 or so, a few in-place swaps or an insertion sort
     * and re-copy into the list beats all.
     * This method does not change the link structure of the list;
     * it only moves the values around. Thus the head and tail
     * links do not change, and this method can be used to sort
     * a sub-list in-place.
     * 
     * @param head first link in (sub)list
     * @param size size of (size)list
     */
    protected static void smallSort(final SinLink head, int size)
    {
        switch (size) {
        case 0:
        case 1:
            return;
        case 2:
            if (head.mData > head.mNext.mData)
                SinLink.swapValues(head, head.mNext);
            return;
        case 3:
            SinLink next = head.mNext;
            SinLink last = next.mNext;
            if (head.mData > next.mData)
                SinLink.swapValues(head, next);
            if (next.mData > last.mData) {
                SinLink.swapValues(next, last);
                if (head.mData > next.mData)
                    SinLink.swapValues(head, next);
            }
            return;
        default:
            int vals[] = new int[size];
            SinLink link = head;
            for (int j = 0; j < size; j++) {
                int k, val = link.mData;
                link = link.mNext;
                for (k = j; k > 0 && val < vals[k - 1]; k--) {
                    vals[k] = vals[k - 1];
                }
                vals[k] = val;
            }
            link = head;
            for (int j = 0; j < size; j++) {
                link.mData = vals[j];
                link = link.mNext;
            }
            return;
        }
    }
    
    /**
     * **************************************************************************
     * 
     * @param headAndTail Pass in a 2-element array which can be modified in-place,
     *            so that the value can be modified for the caller as if
     *            they had been passed as C-style pointers.
     * @param sizeQ
     * @return
     */
    public static void sort(SinLink[] headAndTail, int sizeQ) // TODO: declare as null...
    {
        SinLink headQ = headAndTail[0];
        SinLink tailQ = headAndTail[1];
        // Use heuristics based on size and rang to decide on which
        // algorithm to use.
        
        if (sizeQ < sSmallSize) {
            // For N < 10 or so, a few swaps or an insertion sort beats all.
            // smallSort sorts a list in-place; so the head and tail don't change.
            smallSort(headQ, sizeQ);
            return;
        }
        
        // We already have the size; let's get min and max and middle link
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
        assert (tailQ == tailB);
        
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
        SinLink headA = headQ, tailF;
        tailA.mNext = null;
        int sizeB = sizeQ - sizeA;
        
        // FIXME: change following to (static or non-static?) method in SinList...
        if (sbUseSinLinkSort) {
            headA = sort(headA, sizeA);
            headB = sort(headB, sizeB);
        } else {
            headAndTail[0] = headB;
            headAndTail[1] = tailB;
            sort(headAndTail, sizeB);
            headB = headAndTail[0];
            tailB = headAndTail[1];
            headAndTail[0] = headA;
            headAndTail[1] = tailA;
            sort(headAndTail, sizeA);
            headA = headAndTail[0];
            tailA = headAndTail[1];
        }
        
        // Here is a little "trick" that keeps the sort stable.
        // If the head link in listA has a greater value than
        // that in listB, remove the head of list B and prepend
        // it to list A. Both listA and listB remain sorted, but
        // now listA begins with the minimal element, and can
        // proceed immediately to the merge. (Calling the merge
        // method with swapped arguments would be destabilizing.)
        
        if (headA.mData > headB.mData) {
            SinLink headX = headB;
            headB = headB.mNext;
            sizeB -= 1;
            headX.mNext = headA;
            headA = headX;
            sizeA += 1;
            headAndTail[0] = headA;
        }
        // headAndTail must contain listA and tailA
        SinList.mergeSortedListsNiece(headAndTail, headB, tailB);
        
        tailF = headQ.findTail();
        SinLink tailE = tailB;
        if (tailA.mData > tailB.mData)
            tailE = tailA;
        if (tailF != tailE)
            sizeB = 44 + sizeB - 44;          // bad
        if (tailF != tailB)
            if (tailF != tailA)
                sizeA = 1 + sizeA - 1;        // bad // TODO
                
        tailQ = (tailA.mData > tailB.mData ? tailA : tailB);
        if (tailQ != tailE)
            throw new IllegalStateException("found tail and computed tail differ");
        tailQ = tailE;
        
    }
    
    /**
     * Merge two sorted (sub)lists of specified lengths.
     * The returned list will have length sizeA + sizeB.
     * 
     * @param headA
     * @param headB
     * @param sizeA
     * @param sizeB
     * @return
     */
    public static SinLink mergeSortedLists(SinLink headA, SinLink headB, int sizeA, int sizeB)
    {
        if (headA == null)
            return headB;
        if (headB == null)
            return headA;
        return mergeSortedListsNiece(headA, headB, sizeA, sizeB);
    }
    
    /**
     * Does most of the work of mergeSortedLists, but with no error checking.
     */
    public static SinLink mergeSortedListsNiece(SinLink headA, SinLink headB, int sizeA, int sizeB)
    {
        SinLink linkA = headA, linkB = headB, linkM = null;
        if (linkA.mData <= linkB.mData) {
            linkM = linkA;
            linkA = linkA.mNext;
        } else {
            linkM = linkB;
            linkB = linkB.mNext;
        }
        for (SinLink linkZ = linkM;; linkZ = linkZ.mNext) {
            if (linkA.mData <= linkB.mData) {
                linkZ.mNext = linkA;
                if (linkA.mNext == null) {
                    linkA.mNext = linkB;
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
        return linkM;
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
    protected static SinLink mergeSortedListsNiece(SinLink headA, SinLink tailA, SinLink headB,
            int sizeA, int sizeB)
    {
        // If the greatest entry in A <= the least entry in B, join and return.
        // if (headB == null)
        // return headA; // FIXME
        // if (tailA == null)
        // return headB; // FIXME
        //
        if (headA.mData > headB.mData)
            throw new IllegalArgumentException("list A head val > list B head val: " + headA.mData
                    + " > " + headB.mData);
        if (tailA.mData <= headB.mData) {
            tailA.mNext = headB;
            return headA;
        }
        
        SinLink linkA = headA, linkB = headB;
        SinLink linkM = headA;
        linkA = linkA.mNext;
        for (SinLink linkZ = linkM;; linkZ = linkZ.mNext) {
            if (linkA.mData <= linkB.mData) {
                linkZ.mNext = linkA;
                if (linkA.mNext == null) {
                    linkA.mNext = linkB;
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
        return linkM;
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
        SinList.mergeSortedListsNiece(headAndTailA, sinListB.mHead, sinListB.mTail);
        sinListA.mHead = headAndTailA[0];
        sinListA.mTail = headAndTailA[1];
        boolean bSorted = SinList.verifySorted(sinListA.mHead, sinListA.mSize + sinListB.mSize);
        SinLink.printList(sinListA.mHead);
        Sx.puts(" Sorted == " + bSorted);
        
        // error checking
        mergeSortedLists(sinListA.mHead, null, sinListA.mSize, 0);
        mergeSortedLists(null, sinListB.mHead, 0, sinListB.mSize);
        mergeSortedLists(null, null, 0, 0);
        
        return 0;
    }
    
}
