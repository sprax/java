package sprax.files;

import java.util.Collection;

/**
 * StringCollector with a Collection for its container type.
 * 
 * @param <T> Some type extending Collection<String>, e.g. HashSet<String>
 */
public abstract class StringCollection<T extends Collection<String>> implements
        StringCollectorInterface<T>
{
    protected final T mCollector; // The constructor sets the collector member (like a const ref in
                                  // C++)
    
    /** The collector must be passed in, since the unknown type T cannot
     * be instantiated; that is, mCollector = new T() cannot compile.
     */
    protected StringCollection(T t)
    { 
        mCollector = t; 
    }
    
    /** public accessor; do not define setCollector. 
     *  TODO: Consider removing this from the interface, 
     *  or doing more to make the returned collector a read-only view. 
     */
    @Override
    public T getCollector()
    {
        return mCollector;
    }
    
    /** Return true IFF the set did not already contain this string */
    @Override
    public boolean addString(String str)
    { 
        return mCollector.add(str);
    }
    
    @Override
    public boolean addString(char[] chr, int beg, int end)
    {
        return mCollector.add(new String(chr, beg, end - beg));
    }
    
    @Override
    public boolean contains(final String str)
    {
        return mCollector.contains(str);
    }
    
    @Override
    public int size()
    {
        return mCollector.size();
    }
}
