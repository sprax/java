
package sprax.wordcounts;

/**
 * Interface for registering the occurrences words or word sequences, 
 * as from a written corpus.  Supports only additions, no deletions.
 * Does not necessarily store the actual words and tuples, 
 * or the number of times each has been added.
 */
public interface TupleRegister<K> 
{
    /** 
     * @return the number of unique (or apparently unique) keys added 
     */
    int getSize();

    /** @return the (estimated) count for the given key.  If the count
     * is not actually tracked, it may return only 1 or 0 to indicate
     * presence or absence. 
     */
    int getCount(K key);

    /** Add 1 to the count for the given key
     * @param key of type K
     * @return True if the key was not previously added; otherwise, false.
     */
    boolean addCount(K key);
}

