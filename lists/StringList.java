package sprax.lists;

import sprax.sprout.Sx;
import sprax.strings.UniqueStringFinders;

/**
 * Uses an empty link as a tail sentinel.
 * 
 * @author Sprax Lines
 *
 */
public class StringList
{
    private StrLink mHead = new StrLink(null, null);
    private StrLink mTail = mHead;
    
    // StringList() { }
    
    public boolean isEmpty()
    {
        return mHead == mTail;
    }
    
    public StrLink head()
    {
        return mHead;
    }
    
    public StrLink tail()
    {
        return mTail;
    }
    
    public StrLink addFirst(String str)
    {
        StrLink strLink = new StrLink(str, mHead);
        mHead = strLink;
        return mHead;
    }
    
    public StrLink addLast(String str)
    {
        StrLink link = mTail;
        mTail = new StrLink(null, null);
        link.mStr = str;
        link.mNxt = mTail;
        return link;
    }
    
    public StrLink remove(StrLink link)
    {
        if (link == mHead) {            // If this link was the head, remove it
            mHead = mHead.mNxt;           // by moving the head to its own next.
            return mHead;
        } else if (link.mNxt != null) { // Else, if it is not the tail,
            link.mStr = link.mNxt.mStr;   // remove it by replacing it with its own next.
            link.mNxt = link.mNxt.mNxt;
            return link;
        }
        return null;
    }
    
    public void prepend(StrLink link) {
        link.mNxt = mHead;
        mHead = link;
    }
    
    /**
     * Adds the link to the end of list. This means that the supplied
     * link's data (its string) becomes the data in the last actual
     * list node, and the link itself, emptied of all data, becomes
     * the list's new empty sentinel tail node.
     * 
     * @param link
     */
    public void append(StrLink link)
    {
        mTail.mStr = link.mStr;
        mTail.mNxt = link;
        link.mStr = null;
        link.mNxt = null;
        mTail = link;
    }
    
    public static int unit_test(int level)
    {
        String testName = StringList.class.getName() + ".unit_test";
        Sx.puts(testName + " beg . . .");
        
        StringList list = new StringList();
        StrLink head = list.addFirst("first");
        list.remove(head);
        StrLink last = list.addLast("last");
        list.remove(head);
        list.remove(last);
        
        Sx.puts(testName + " . . . end");
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test(1);
        UniqueStringFinders.unit_test(1);
    }
    
}
