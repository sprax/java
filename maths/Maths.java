/*
 * Sprax Lines  2016.05.04
 */

package sprax.maths;

import sprax.Sx;
import sprax.Sz;
import org.apache.commons.lang3.math.NumberUtils;

/** random collection of basic math operations */
public abstract class Maths
{
    //////// MAXIMUM of 3 ////////
    
    // Java's built-in Math
    public static int max3Math(int a, int b, int c) {
        return Math.max(Math.max(a, b), c);
    }
    
    // Apache Commons
    public static int max3Apache(int a, int b, int c) {
        return NumberUtils.max(a, b, c);
    }
    
    // Roll your own (optimized by best guess)
    public static int max3Compare(int a, int b, int c)
    {
        int max;
        if (a > b && a > c) {
            max = a;
        } else if (b > c) {
            max = b;
        } else {
            max = c;
        }
        return max;
    }
    
    //////// MINIMUM of 3 ////////
    
    // Java's built-in Math
    public static int min3Math(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
    
    // Apache Commons
    public static int min3Apache(int a, int b, int c) {
        return NumberUtils.min(a, b, c);
    }
    
    // Roll your own (optimized by best guess)
    public static int min3Compare(int a, int b, int c)
    {
        int min;
        if (a < b && a < c) {
            min = a;
        } else if (b < c) {
            min = b;
        } else {
            min = c;
        }
        return min;
    }
    
    public static int unit_test() {
        String testName = Maths.class.getName() + ".unit_test";
        Sx.format("BEGIN %s\n", testName);
        int numWrong = 0;
        
        int ans = max3Math(1, -2, 3);
        numWrong += Sz.oneWrong(ans, 3);
        
        ans = max3Apache(1, -2, 3);
        numWrong += Sz.oneWrong(ans, 3);
        
        ans = max3Compare(1, -2, 3);
        numWrong += Sz.oneWrong(ans, 3);
        
        ans = min3Math(1, -2, 3);
        numWrong += Sz.oneWrong(ans, -2);
        
        ans = min3Apache(1, -2, 3);
        numWrong += Sz.oneWrong(ans, -2);
        
        ans = min3Compare(1, -2, 3);
        numWrong += Sz.oneWrong(ans, -2);
        Sx.format("\nEND %s:  %d wrong, %s\n", testName, numWrong,
                (numWrong == 0 ? "PASS" : "FAIL"));
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
