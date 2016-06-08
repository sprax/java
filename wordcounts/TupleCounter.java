
package sprax.wordcounts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Interface for counting up the number of occurrences words or word sequences, 
 * as from a written corpus.  Supports only increments, no decrements or deletions.
 */
public interface TupleCounter<K> extends TupleRegister<K>
{
    /** 
     * @return the set of keys, if they are in fact stored, or an empty set if they are not
     */
    Set<K> getKeys();
}


/** Accumulates keys and their integral counts */
class MapTupleCounter<K> implements TupleCounter<K> 
{
    private HashMap<K, Integer> tupleCounts = new HashMap<>();

    @Override
    public int getSize()           	{ return tupleCounts.size(); }

    @Override
    public Set<K> getKeys()        	{ return tupleCounts.keySet(); }

    @Override
    public int getCount(K key) 		{ return tupleCounts.getOrDefault(key, 0); }

    @Override
    public boolean addCount(K key) 	{ return null == tupleCounts.put(key, getCount(key) + 1); }
}

/** Accumulates counts that can only be 0 or 1 */
class SetTupleCounter<K> implements TupleCounter<K> 
{
    private HashSet<K> tupleSet = new HashSet<>();

    @Override
    public int getSize()           	{ return tupleSet.size(); }

    @Override
    public Set<K> getKeys()        	{ return tupleSet; }

    @Override
    public int getCount(K key)     	{ return tupleSet.contains(key) ? 1 : 0; }

    @Override
    public boolean addCount(K key) 	{ return tupleSet.add(key); }
}
