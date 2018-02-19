package sprax.counters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class TreeMapCounter
{
    
    public static void main(String a[]) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("java", 20);
        map.put("C++", 45);
        map.put("Java2Novice", 2);
        map.put("Unix", 67);
        map.put("MAC", 26);
        map.put("Why this kolavari", 93);
        
        map.put("java", map.get("java") + 80);
        
        Set<Entry<String, Integer>> set = map.entrySet();
        List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        int ntop = 4;
        System.out.format("Showing only the top %d highest counts:\n", ntop);
        for (Map.Entry<String, Integer> entry : list) {
            if (--ntop < 0)
                break;
            System.out.println(entry.getKey() + " ==== " + entry.getValue());
        }
    }
}
