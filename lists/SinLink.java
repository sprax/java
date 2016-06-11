package sprax.lists;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import sprax.sprout.Sx;

public class SinLink extends Link
{
    SinLink mNext;
    int     mData;
    
    @Override
    public Link getNext() {
        return mNext;
    }
    
    @Override
    public void setNext(Link link) {
        mNext = (SinLink) link;           // TODO: This seems wrong, even though it gets no warning (at least
                                // in Eclipse).
    }
    
    @Override
    public int getData() {
        return mData;
    }
    
    public SinLink(int data, SinLink link) {
        mData = data;
        mNext = link;
    }
    
    public SinLink(int data) {
        mData = data;
        mNext = null;
    }
    
    public SinLink clone() {
        return new SinLink(mData, mNext);
    }
    
    static int computeLength(SinLink list)
    {
        int length = 0;
        while (list != null) {
            list = list.mNext;
            length++;
        }
        return length;
    }
    
    public static void printAsChar(SinLink link) {
        if (link != null)
            System.out.print(" " + (char) (link.mData % (255 - 'A')));
    }
    
    public void printAsChar() {
        System.out.print(" " + (char) (mData % (255 - 'A')));
    }
    
    public static void printList(SinLink link) {
        while (link != null) {
            link.printAsChar();
            link = link.mNext;
        }
        System.out.println();
    }
    
    public void append(SinLink node) {
        mNext = node;
    }
    
    public void prepend(SinLink node) {
        if (node != null)
            ;
        node.mNext = this;
    }
    
    public SinLink findTail() {
        SinLink tail = this;
        while (tail.mNext != null) {
            tail = tail.mNext;
        }
        return tail;
    }
    
    public SinLink predecessor(SinLink list)
    {
        while (list != null) {
            if (list.mNext == this)
                return list;
            list = list.mNext;
        }
        return null;
    }
    
    public void removeFromList(SinLink list) {
        SinLink pred = predecessor(list);
        if (pred != null) {
            pred.mNext = this.mNext;
            this.mNext = null;
        }
    }
    
    // Compute the length from this node to the end.
    // If this is the head, it returns the length of the entire list.
    public int length() {
        int length = 1;					// length is at least 1 (this node is not null)
        SinLink iter = this.mNext;
        while (iter != null) {
            iter = iter.mNext;
            length++;
        }
        return length;
    }
    
    public void lowerCaseEvenNodes() {
        for (SinLink iter = this; iter != null; iter = iter.mNext) {
            if ('A' <= iter.mData && iter.mData <= 'Z')
                iter.mData += ('a' - 'A');
            iter = iter.mNext;
            if (iter == null) {
                break;
            }
        }
    }
    
    public static SinLink initAlphabetAppend(int length) {
        assert (length > 0);
        SinLink tail = null;
        while (--length >= 0) {
            SinLink temp = new SinLink('A' + length % 26, null);
            temp.append(tail);
            tail = temp;
        }
        return tail;
    }
    
    public static SinLink initAlphabetAppend_LAME(final int length) {
        // This is lame because the head is a special case: it must be created before the
        // loop and stored off as the return value,
        // and inside the loop we must create nodes with next set to null, then re-assign
        // next to the correct value,
        // and the length is limited to 26
        assert (0 <= length && length <= 26);
        SinLink head = new SinLink('A', null);
        SinLink temp = head;
        for (int j = 'A' + 1; j < 'A' + length; j++) {
            temp.mNext = new SinLink(j, null);
            temp = temp.mNext;
        }
        return head;
    }
    
    public static SinLink initAlphabetPrepend_LAME(int length) {
        assert (length > 0);
        SinLink last = new SinLink(--length + 'A', null);
        while (--length >= 0) {
            SinLink temp = new SinLink(length + 'A', null);
            last.prepend(temp);
            last = temp;
        }
        return last;
    }
    
    public static SinLink initAlphabetIterative(int length) {
        SinLink head = null;
        while (--length >= 0) {
            SinLink temp = new SinLink(length + 'A', head);
            head = temp;
        }
        return head;
    }
    
    private static SinLink initAlphabetRecursive(int length, SinLink list) {
        if (--length >= 0) {
            list = initAlphabetRecursive(length, new SinLink(length + 'A', list));
        }
        return list;
        
    }
    
    public static SinLink initAlphabetRecursive(int length) {
        return initAlphabetRecursive(length, null);
    }
    
    private static SinLink initAlphabetRecursiveBackwards(int length) {
        if (--length >= 0) {
            return new SinLink('Z' - length, initAlphabetRecursiveBackwards(length));
        }
        return null;
    }
    
    public static SinLink reverseIterative(SinLink list)
    {
        SinLink head = null;		// return this
        while (list != null) {
            SinLink temp = list;	// save link to bing the swap
            list = list.mNext;	// advance the list
            temp.mNext = head;	// reverse the link
            head = temp;			// finish the swap
        }
        return head;
    }
    
    private static SinLink reverseRecursive(SinLink list, SinLink revl)
    {
        if (list != null) {
            SinLink temp = list;
            list = list.mNext;
            temp.mNext = revl;
            revl = reverseRecursive(list, temp);
        }
        return revl;
    }
    
    public static SinLink reverseRecursive(SinLink list) {
        return reverseRecursive(list, null);
    }
    
    public static boolean isListPalindromeLengthReversal(SinLink list, int length)
    {
        // This is pretty lame, because it reverses the first half of the list
        // in place, then re-reverses it!
        boolean retVal = true;
        assert (list != null && length >= 0);
        int halfLength = length / 2;
        SinLink temp = list, tail = null;
        for (int j = 0; j < halfLength; j++) {
            temp = list.mNext;
            list.mNext = tail;
            tail = list;
            list = temp;
        }
        SinLink next = list;
        // tail is now the head of the reversed list that points backward to the original head
        // list is now the head of the 2nd half of the original list, but if the length
        // is odd, it must be advanced one place
        if (length % 2 == 1)
            list = list.mNext;
        
        while (tail != null) {
            if (tail.mData != list.mData)
                retVal = false;
            // advance list, guarding against a bogus length.
            assert (list != null);
            list = list.mNext;
            // reverse from tail to the original head
            temp = tail.mNext;
            tail.mNext = next;
            next = tail;
            tail = temp;
        }
        return retVal;
    }
    
    public static boolean isListPalindromeLengthStack(SinLink list, int length)
    {
        assert (list != null && length >= 0);
        
        // Use a stack to store nodes from the first half of the list.
        // It saves space to store only the nodes' data.
        Stack<Integer> stack = new Stack<Integer>();
        int halfLength = length / 2;
        SinLink iter = list;
        for (int j = 0; j < halfLength; j++) {
            stack.push(iter.mData);
            iter = iter.mNext;
        }
        // If the length is odd, advance the iterator once more
        if (length % 2 == 1)
            iter = iter.mNext;
        
        for (int j = 0; j < halfLength; j++) {
            int back = stack.pop();
            if (back != iter.mData)
                return false;
            // Advance list, guarding against the parameter
            // length being greater than the actual length.
            assert (iter != null);
            iter = iter.mNext;
        }
        return true;
    }
    
    public static boolean isListPalindromeStack(SinLink list)
    {
        Stack<Integer> stack = new Stack<Integer>();
        SinLink fast = list, slow = list;
        while (fast != null && fast.mNext != null) {
            stack.push(slow.mData);
            slow = slow.mNext;
            fast = fast.mNext.mNext;
        }
        // If the list's length is odd, advance the slow iterator once more,
        // but don't add the middle node's value to the stack.
        if (fast != null)
            slow = slow.mNext;
        
        while (slow != null) {
            int back = stack.pop();
            if (back != slow.mData)
                return false;
            slow = slow.mNext;
        }
        return true;
    }
    
    public static void swapValues(SinLink linkA, SinLink linkB)
    {
        int tempVal = linkA.mData;
        linkA.mData = linkB.mData;
        linkB.mData = tempVal;
    }
    
    public static void unit_test()
    {
        System.out.println("Init iteratively by appending, the GOOD way backwards:");
        printList(initAlphabetAppend(26));
        System.out
                .println("Init iteratively by appending, the DUMB way forwards, lc at even indices:");
        SinList list = null;
        SinLink link = initAlphabetAppend_LAME(3);
        link.lowerCaseEvenNodes();
        printList(link);
        System.out.println("Init iteratively by prepending, always LAME:");
        link = initAlphabetPrepend_LAME(3);
        printList(link);
        System.out.println("isListPalindromeLengthReversal: "
                + isListPalindromeLengthReversal(link, 3) + "\n");
        
        String string = "ABCCBA";
        int length = string.length();
        list = ListFactory.fromString(string);
        link = list.mHead;
        System.out.println("isListPalindromeLengthStack(" + string + ")  "
                + isListPalindromeLengthStack(link, length));
        System.out.println("isListPalindromeLengthReversal(" + string + ")  "
                + isListPalindromeLengthReversal(link, length));
        System.out.println("isListPalindromeStack(" + string + ")  " + isListPalindromeStack(link));
        printList(link);
        
        string = "ABCcBA";
        length = string.length();
        list = ListFactory.fromString(string);
        link = list.mHead;
        System.out.println("isListPalindromeLengthStack(" + string + ")  "
                + isListPalindromeLengthStack(link, length));
        System.out.println("isListPalindromeStack(" + string + ")  " + isListPalindromeStack(link));
        System.out.println("isListPalindromeLengthReversal(" + string + ")  "
                + isListPalindromeLengthReversal(link, length));
        printList(link);
        
        string = "ABCBA";
        length = string.length();
        list = ListFactory.fromString(string);
        link = list.mHead;
        System.out.println("isListPalindromeLengthStack(" + string + ")  "
                + isListPalindromeLengthStack(link, length));
        System.out.println("isListPalindromeStack(" + string + ")  " + isListPalindromeStack(link));
        System.out.println("isListPalindromeLengthReversal(" + string + ")  "
                + isListPalindromeLengthReversal(link, length));
        printList(link);
        
        boolean doAll = true;
        if (doAll) {
            System.out.println("Init backwards from last to 'A', iterative:");
            link = initAlphabetIterative(5);
            printList(link);
            System.out.println("Reverse, iterative:");
            printList(link = reverseIterative(link));
            
            System.out.println("Init backwards from last to 'A', recursive:");
            printList(initAlphabetRecursive(4));
            System.out.println("Reverse, recursive:");
            printList(reverseRecursive(initAlphabetRecursive(4)));
            
            System.out.println("Init backwards from 'Z' to first, recursive:");
            printList(initAlphabetRecursiveBackwards(3));
            System.out.println("Reverse, recursive:");
            printList(reverseRecursive(initAlphabetRecursiveBackwards(3)));
            
            List<Character> javaList = new LinkedList<Character>();
            Character[] chrs = { 'H', 'E', 'F', 'E', 'L', 'L', 'B', 'I', 'D', 'G', 'A', 'F', 'J',
                    'A', 'C', 'K' };
            javaList.addAll(Arrays.asList(chrs));
            Collections.sort(javaList);
            Sx.puts(javaList);
            
            SinList sinList = ListFactory.fromArray(chrs);
            sinList.sort();
        }
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
