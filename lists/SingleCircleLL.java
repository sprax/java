package sprax.lists;

public class SingleCircleLL
{
    SinLink mHead;
    int     mSize;
    
    SingleCircleLL(SinLink head, SinLink tail) {
        this.mHead = head;
        mHead.mNext = tail;
        tail.mNext = head;
        mSize = 2;
    }
    
    SingleCircleLL(SinLink link, int length) {
        mHead = link;
        mSize = length;
    }
    
    public int insertAfter(SinLink link, SinLink pos) {
        // returns the number of links added, 0 or 1
        SinLink iter = mHead;
        for (int j = mSize; --j >= 0; iter = iter.mNext) {
            if (iter == pos) {
                link.mNext = iter.mNext;
                iter.mNext = link;
                mSize++;
                return 1;
            }
        }
        return 0;
    }
    
    public SinLink findFirst() {
        // return the link with the least data value
        SinLink first = mHead;
        SinLink iter = mHead;
        for (int j = mSize; --j >= 0; iter = iter.mNext) {
            if (first.mData > iter.mData) {
                first = iter;
            }
        }
        return first;
    }
    
    public static void printList(SingleCircleLL list) {
        SinLink iter = list.mHead;
        for (int j = list.mSize; --j >= 0; iter = iter.mNext) {
            System.out.print(iter.mData + " ");
        }
        System.out.println();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        SingleCircleLL circ = new SingleCircleLL(new SinLink('A', null), new SinLink('B', null));
        SinLink last = circ.mHead.mNext;
        for (int j = 2; j < 26; j++) {
            SinLink temp = new SinLink(j + 'A', null);
            circ.insertAfter(temp, last);
            last = temp;
        }
        printList(circ);
        circ.mHead = circ.mHead.mNext.mNext.mNext.mNext.mNext;
        printList(circ);
        SinLink first = circ.findFirst();
        circ.mHead = first;
        printList(circ);
    }
    
}
