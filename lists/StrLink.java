package sprax.lists;

/**
 * Very simple link class containing only a string and a next link ref.
 * 
 * @author sprax
 */
public class StrLink
{
    public String  mStr;
    public StrLink mNxt;
    
    public StrLink(String str, StrLink nxt)
    {
        mStr = str;
        mNxt = nxt;
    }
    
    /**
     * removes itself from its list, if is part of one, and dereferences its own data. This method
     * performs the well-known "self-deletion" trick of replacing its own data with that of the next
     * link in the list, if there is one, then deleting/dereferencing any left over cruft. In C/C++,
     * that would mean calling delete on the original "next" pointer, or nullifying its own data if
     * it were the tail of the list. In Java and other languages with garbage collection, replacing
     * the internal references with those of the next node is enough to cause the cruft to be GC'd,
     * but this link is the last one, its data must be nullified, and clients may need to check for
     * that null condition as indicating that the list is effectively empty.
     */
    public void suicide()
    {
        if (mNxt != null) {
            mStr = mNxt.mStr;
            mNxt = mNxt.mNxt;
        } else {
            mStr = null;
        }
    }
}