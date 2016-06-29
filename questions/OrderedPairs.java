package sprax.questions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sprax.graphs.Graph;
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
    /** 
     * Input: array of ordered pairs of strings; output: list of strings.
     * Lossy: Putting the input array of pairs into a map of pairs saves
     * only one second per first, so if the input contains several pairs
     * with the same first, only the last of these pairs will be stored
     * in the map.
     */
    public List<String> listFromOrderPairsArray(String[][] itinerary) 
    {
        HashMap<String, String> pairMap = new HashMap<>();
        for (String[] it : itinerary) {
            pairMap.put(it[0], it[1]);
        }
        return listFromOrderedPairsMap(pairMap);
    }
    
    /** 
     * List from pairs stored in a map.  Cycles are limited using the map's size. 
     * Maps are injective (each key has one and only one value), so representing pairs 
     * as a map eliminates "diamonds," but not cycles.
     */
    public static List<String> listFromOrderedPairsMap(Map<String, String> pairMap)
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
    
    public Graph<String> graphFromOrderedPairs()
    {
        Graph<String> graph = null;
        
        return graph;
    }
    
    
    
    public static int test_simple_chain() {
        String testName = OrderedPairs.class.getName() + ".test_simple_chain";
        Sz.begin(testName);
        Sx.puts();
        int numWrong = 0;
        
        Map<String, String> map = new HashMap<String, String>();
        
        map.put("MUC", "LHR");
        map.put("JFK", "SFO");
        map.put("CDG", "MUC");
        map.put("SFO", "SJC");
        map.put("LHR", "JFK");
        
        List<String> airportsFromMap = listFromOrderedPairsMap(map);
        Sx.putsList(airportsFromMap);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int test_single_cycle() {
        String testName = OrderedPairs.class.getName() + ".test_single_cycle";
        Sz.begin(testName);
        int numWrong = 0;
        
        Map<String, String> map = new HashMap<String, String>();
        
        map.put("LAX", "SFO");
        map.put("SJC", "LAX");
        map.put("MUC", "LHR");
        map.put("JFK", "SFO");
        map.put("CDG", "MUC");
        map.put("SFO", "SJC");
        map.put("LHR", "JFK");
        
        List<String> airportsFromMap = listFromOrderedPairsMap(map);
        Sx.putsList(airportsFromMap);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static int unit_test() {
        String testName = OrderedPairs.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_simple_chain();
        numWrong += test_single_cycle();
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
