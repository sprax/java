package sprax.strings;

import java.util.HashMap;
import java.util.Map;

import sprax.Sx;
import sprax.Sz;

public class FindCharsInString {
    
    public static boolean areCharsInString(String searchCharStr, final String bigStr)
    {
        char seachChars[] = searchCharStr.toCharArray();
        return areCharsInString(seachChars, bigStr);
    }    
    
    public static boolean areCharsInString(char searchChars[], final String bigStr)
    {
        // Map all search chars to their counts
        Map<Character, Integer> charToCount = new HashMap<Character, Integer>();
        for (char ch : searchChars) {
            Integer count = charToCount.get(ch);
            if (count == null)
                charToCount.put(ch, 1);
            else
                charToCount.put(ch, 1+count);
        }
        int mapSize = charToCount.size();
        
        // Try to find the entire count of each search char.
        // Decrement mapSize each time we succeed
        // If mapSize becomes 0, we've found them all, so return true.
        for (int j = 0; j < bigStr.length(); j++) {
            char ch = bigStr.charAt(j);
            if (charToCount.containsKey(ch)) {
                Integer count = charToCount.get(ch);
                if (--count == 0) {
                    if (--mapSize == 0)
                        return true;
                    charToCount.remove(ch);
                } else {
                    charToCount.put(ch, count);
                }
                
            }
        }
        return false;
    }
    
    public static int test_areCharsInString(String chars, String bigStr) 
    {
  
        return 0;
    }      
    public static int unit_test(int lvl) 
    {
        String  testName = FindCharsInString.class.getName() + ".unit_test";
        Sz.begin(testName);  
        
        String searchStr = "ABCDefgh";
        String sourceStr = "abcdEFGH";
        boolean found = areCharsInString(searchStr, sourceStr);
        Sx.format("%5s [%s] in <%s>\n", found, searchStr, sourceStr);  

        searchStr = "ABCDefgh not";
        sourceStr = "ABCD not efgh";
        found = areCharsInString(searchStr, sourceStr);
        Sx.format("%5s [%s] in <%s>\n", found, searchStr, sourceStr);  

        searchStr = "absolutely not";
        sourceStr = "I am obviously going to be there.";
        found = areCharsInString(searchStr, sourceStr);
        Sx.format("%5s [%s] in <%s>\n", found, searchStr, sourceStr);  

        sourceStr = "I am obviously always going to be there.";
        found = areCharsInString(searchStr, sourceStr);
        Sx.format("%5s [%s] in <%s>\n", found, searchStr, sourceStr);  
        
        Sz.end(testName, 0);    
        return 0;
    }  
    
    public static void main(String[] args) 
    {
        unit_test(1);
    }  
    
}
