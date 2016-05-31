
package sprax.eval;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Evaluator<K,V> 
{
    public static <K,V> Map<K,V> makeMap(Set<K> set, EvaluateInterface<V,K> evaluator)
    {
        Map<K,V> map = new HashMap<K,V>(set.size());
        for (K key : set)
        {
            map.put(key, evaluator.evaluate(key));
        }
        return map;
    }
}