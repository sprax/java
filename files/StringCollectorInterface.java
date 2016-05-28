package sprax.files;

public interface StringCollectorInterface<T>
{
    public boolean addString(String str); // Return true IFF the collector changes (by keeping str
                                          // as a new entry)
    
    public boolean addString(char[] chr, int beg, int end); // Return true IFF the collector
                                                            // actually changes
    
    public boolean contains(final String str); // Return true IFF the collector actually changes
    
    // TODO: include this? No, probably not...
    // public boolean contains(final char[] chr, int beg, int end);
    
    public int size();
    
    public T getCollector();
}
