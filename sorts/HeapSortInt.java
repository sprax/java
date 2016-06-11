package sprax.sorts;

import sprax.Sz;
import sprax.sprout.Sx;

/**
 * Heap methods on array, no keeping of state.
 * Implements *procedural* heapify and heapsort methods,
 * as opposed to object-oriented methods. The methods
 * are static, so the client must track the state of the
 * supplied array.
 * 
 * @author sprax
 */
public class HeapSortInt implements SortInt
{

    @Override
    public void sort(int[] array) {
        heapSort(array, array.length);
    }
    
    public static void heapSort(int[] iA, int len)
    {
        // first place iA in max-heap order
        heapify(iA, len);
        
        int last = len - 1;
        while (last > 0) {
            // swap the root(maximum value) of the heap with the
            // last element of the heap
            int tmp = iA[last];
            iA[last] = iA[0];
            iA[0] = tmp;
            // put the heap back in max-heap order
            siftDown(iA, 0, --last);
            // decrement the size of the heap so that the previous
            // max value will stay in its proper place
            // last--; // TODO: ordering ok?
        }
    }
    
    /**
     * heapify: put array in binary heap order, which means if a node at
     * position k has left and right children, they will be at positions
     * 2*k and 2*k + 1.
     * 
     * @param iA
     */
    public static void heapify(int[] iA, int len)
    {
        // start is assigned the index of the last parent node in iA 
        int last = len - 1;
        int start = (last - 1) / 2; //binary heap
        
        while (start >= 0) {
            // sift down the node at index start to the proper place
            // such that all nodes below the start index are in heap
            // order
            siftDown(iA, start, last);
            start--;
        }
        // after sifting down the root all nodes/elements are in heap order
    }
    
    public static void siftDown(int[] iA, int start, int last) {
        // last represents the limit of how far down the heap to sift
        int root = start;
        
        while ((root * 2 + 1) <= last) {      //While the root has at least one child
            int child = root * 2 + 1;           //root*2+1 points to the left child
            //if the child has a sibling and the child's value is less than its sibling's...
            if (child + 1 <= last && iA[child] < iA[child + 1])
                child = child + 1;           //... then point to the right child instead
            if (iA[root] < iA[child]) {     //out of max-heap order
                int temp = iA[root];
                iA[root] = iA[child];
                iA[child] = temp;
                root = child;                //repeat to continue sifting down the child now
            }
            else
                return;
        }
    }
    
    /**
     * removeRoot removes the root from the heap array,
     * but does not actually reduce the size of the array,
     * so the last element gets duplicated. So it only
     * "works" the first N times, where N is the original
     * array's length. (Usually a heap DS maintains max
     * and current heap sizes, in addition to the array,
     * but one alternative is to use a sentinel value to
     * mark the end of valid data. If all valid values
     * will be >= 0, then you can insert -1 into an array
     * of size maxSize+1 and check for root value == -1
     * instead of maintaining maxSize and curSize in a DS.)
     * 
     * @param iA
     * @return
     */
    public static int removeRoot(int[] iA, int len)
    {
        int root = iA[0];
        iA[0] = iA[len - 1];
        siftDown(iA, 0, len - 1);
        return root;
    }
    
    public static class CurrentClassGetter extends SecurityManager
    {
        public String getClassName() {
            return getClassContext()[1].getName();
        }
    }
    
    public static int unit_test(int level)
    {
        //  System.getSecurityManager().getClassContext()[0].getName();
        CurrentClassGetter ccg = new CurrentClassGetter();
        String testName = ccg.getClassName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int iA[] = { 7, 5, 9, 3, 11, 1, 2, 8, 0, 10, -1, 4, 6, 12, 13, 14 };
        int origSize = iA.length;
        int root;
        Sx.putsArray("before heapify:     ", iA);
        heapify(iA, origSize);
        Sx.putsArray("after heapify:      ", iA);
        heapSort(iA, origSize);
        Sx.putsArray("after heapSort:     ", iA);
        if (!SortUtil.verifySorted(iA))
            throw new IllegalStateException("heapSort failed!");
        
        int k = 0, len = origSize, five[] = new int[5];
        heapify(iA, origSize);
        Sx.putsArray("after heapify:      ", iA);
        five[k++] = root = removeRoot(iA, len--);
        Sx.putsArray("after removeRoot 1: ", iA, "  root " + root);
        five[k++] = root = removeRoot(iA, len--);
        Sx.putsArray("after removeRoot 2: ", iA, "  root " + root);
        five[k++] = root = removeRoot(iA, len--);
        Sx.putsArray("after removeRoot 3: ", iA, "  root " + root);
        five[k++] = root = removeRoot(iA, len--);
        five[k++] = root = removeRoot(iA, len--);
        
        for (int numLeft = origSize - 5; numLeft > 5; numLeft--) {
            removeRoot(iA, len--);
            Sx.putsArray("remove " + numLeft, iA);
        }
        
        int old = Integer.MAX_VALUE;
        for (int num : five) {
            if (old < num)
                numWrong++;
            old = num;
        }
        Sx.putsArray("Largest 5 original values, sorted in descending order: ", five);
        Sx.putsSubArray("Smallest 5 original values, in no particular order: ", iA, 0, 5);
        
        if (level > 0) {
            
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(1);
    }
    
}
