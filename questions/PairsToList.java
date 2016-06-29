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
public class PairsToList
{
    public List<String> reconstruct(String[][] itinerary) {
        HashMap<String, String> graph = new HashMap<>();
        HashSet<String> relation = new HashSet<>();
        for (String[] it : itinerary) {
            graph.put(it[0], it[1]);
            relation.add(it[1]);
        }
        
        String start = "";
        for (String[] it : itinerary) {
            if (!relation.contains(it[0])) {
                start = it[0];
            }
        }
        if ("".equals(start)) {
            return new ArrayList<String>();
        }
        List<String> rst = new ArrayList<>();
        rst.add(start);
        while (graph.containsKey(start)) {
            start = graph.get(start);
            rst.add(start);
        }
        return rst;
    }
    
    public static List<String> fromMap(Map<String, String> art) {
        String startpoint = "";
        for (Map.Entry<String, String> en : art.entrySet()) {
            if (!art.containsValue(en.getKey())) {
                startpoint = en.getKey();
                break;
            }
        }
        
        ArrayList<String> ord = new ArrayList<>();
        ord.add(startpoint);
        for (int i = 0; i < art.size(); i++) {
            ord.add(art.get(startpoint));
            startpoint = art.get(startpoint);
        }
        return ord;
    }
    
    public static int unit_test() {
        String testName = PairsToList.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        Map<String, String> art = new HashMap<String, String>();
     
        art.put("MUC", "LHR");
        art.put("CDG", "MUC");
        art.put("SFO", "SJC");
        art.put("LHR", "SFO");
        
        List<String> airportsFromMap = fromMap(art);
        Sx.putsList(airportsFromMap);
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
