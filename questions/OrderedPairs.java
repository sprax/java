package sprax.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * <pre>
 * Given an bunch of airline tickets with [from, to],
 * for example [MUC, LHR], [CDG, MUC], [SFO, SJC], [LHR, SFO], please
 * reconstruct the itinerary in order, for example: [ CDG, MUC, LHR, SFO, SJC ].
 * 
 * Possibly a Google interview or CodeJam question.
 * Solution by Sprax Lines 2016.06.28
 */
public class OrderedPairs
{
    /** input: array of ordered pairs of strings; output: list of strings */
    public List<String> arrayPairsToList(String[][] itinerary) 
    {
        HashMap<String, String> pairMap = new HashMap<>();
        for (String[] it : itinerary) {
            pairMap.put(it[0], it[1]);
        }
        return mapOfPairsToList(pairMap);
    }
    
    public static List<String> mapOfPairsToList(Map<String, String> pairMap)
    {
        String first = "";
        for (Map.Entry<String, String> entry : pairMap.entrySet()) {
            if (!pairMap.containsValue(entry.getKey())) {
                first = entry.getKey();
                break;
            }
        }
        
        // Avoid cycles by iterating up to size, not: while(pairMap.containsKey(first))
        ArrayList<String> order = new ArrayList<>();
        order.add(first);
        for (int i = 0; i < pairMap.size(); i++) {
            first = pairMap.get(first);
            order.add(first);
        }
        return order;
    }
    
    Graph<String>
    
    
    
    public static int unit_test() {
        String testName = OrderedPairs.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Map<String, String> art = new HashMap<String, String>();
        
        art.put("MUC", "LHR");
        art.put("CDG", "MUC");
        art.put("SFO", "SJC");
        art.put("LHR", "SFO");
        
        List<String> airportsFromMap = mapOfPairsToList(art);
        Sx.putsList(airportsFromMap);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
