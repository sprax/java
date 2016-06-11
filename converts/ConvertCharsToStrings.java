package sprax.converts;

import org.junit.Assert;
import org.junit.Test;

import sprax.Sz;
import sprax.sprout.Sx;

public class ConvertCharsToStrings
{
    
    static int sNumIters = 100000;
    
    // Times:
    // 1. Initialization of "s" outside the loop
    // 2. Init of "s" inside the loop
    // 3. newFunction() actually checks the string length,
    // so the function will not be optimized away by the hotstop compiler
    
    @Test
    // Fastest: 237ms / 562ms / 2434ms
    public static void testCacheStrings()
    {
        // Cache all possible Char strings
        String[] char2string = new String[Character.MAX_VALUE];
        for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
            char2string[i] = Character.toString(i);
        }
        
        for (int x = 0; x < sNumIters; x++) {
            char[] s = "abcdefg".toCharArray();
            for (int i = 0; i < s.length; i++) {
                doSomething(char2string[s[i]]);
            }
        }
    }
    
    @Test
    // Fast: 1687ms / 1725ms / 3382ms
    public static void testCharToString() throws Exception
    {
        for (int x = 0; x < sNumIters; x++) {
            String s = "abcdefg";
            for (int i = 0; i < s.length(); i++) {
                // Fast: Creates new String objects, but does not copy an array
                doSomething(Character.toString(s.charAt(i)));
            }
        }
    }
    
    @Test
    // Very fast: 1331 ms/ 1414ms / 3190ms
    public static void testSubstring() throws Exception
    {
        for (int x = 0; x < sNumIters; x++) {
            String s = "abcdefg";
            for (int i = 0; i < s.length(); i++) {
                // The fastest! Reuses the internal char array
                doSomething(s.substring(i, i + 1));
            }
        }
    }
    
    @Test
    // Slowest: 2525ms / 2961ms / 4703ms
    public static void testNewString() throws Exception
    {
        char[] value = new char[1];
        for (int x = 0; x < sNumIters; x++) {
            char[] s = "abcdefg".toCharArray();
            for (int i = 0; i < s.length; i++) {
                value[0] = s[i];
                // Slow! Copies the array
                doSomething(new String(value));
            }
        }
    }
    
    private static void doSomething(String string)
    {
        // Do something with the one-character string
        Assert.assertEquals(1, string.length());
    }
    
    public static int unit_test()
    {
        String testName = ConvertCharsToStrings.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        long begTime, endTime, runTime;
        
        begTime = System.currentTimeMillis();
        testCacheStrings();
        endTime = System.currentTimeMillis();
        runTime = endTime - begTime;
        Sx.puts("done: " + runTime);
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}