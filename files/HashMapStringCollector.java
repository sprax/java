package sprax.files;

import java.util.HashMap;

/**
 * StringCollector with unspecified container type.
 * 
 * @param <T> Some kind of "container" class, loosely construed. For example, a StringBuffer,
 *            CharacterBuffer, or Trie.
 */
public class HashMapStringCollector extends MapStringCollector<HashMap<String, Integer>>
{
    HashMap<String, Integer> mHashMap;
    
    public HashMapStringCollector()
    {
        super(new HashMap<String, Integer>());
        mHashMap = mCollector;
    }
}
