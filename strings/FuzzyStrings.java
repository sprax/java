package sprax.strings;

import sprax.Sx;
import sprax.Sz;

/**
 * Container for 2 strings, distinguished from each other by length.
 * @author sprax
 *
 */
class MaxMinStr 
{
    final String  maxStr;   // longer string
    final String  minStr;   // shorter (or same-length) string
    final int     maxLen;   // length to use for the (beginning of) the longer string
    final int     minLen;   // length to use for shorter string
    
    public MaxMinStr(final String strA, final String strB)
    {
        int lenA = strA.length();
        int lenB = strB.length();
        if (lenA == 0 || lenB == 0)
            throw new IllegalArgumentException(MaxMinStr.class.getName() +"("+strA+", "+strB+")");
        if (lenA < lenB) {
            minStr = strA;
            maxStr = strB;
            minLen = lenA;
            maxLen = lenB;
        } else {
            minStr = strB;
            maxStr = strA;
            minLen = lenB;
            maxLen = lenA;            
        }
    }
}

public class FuzzyStrings
{    
    /************************************************************************
     * Binary distance (a.k.a. Hamming distance) between strings, 
     * where maxStr must not be shorter than minStr.  Binary means sum 1 for 
     * each position where strA and strB differ.
     */
    public static int binaryDistance(String strA, String strB)
    {
        return binaryDistance(new MaxMinStr(strA, strB));
    }
    static int binaryDistance(MaxMinStr mms)
    {
        return binaryDistance(mms.maxStr, mms.minStr, mms.maxLen, mms.minLen);
    }
    protected static int binaryDistance(String maxStr, String minStr, int maxLen, int minLen)
    {
        int dist = maxLen - minLen;
        for (int j = 0; j < minLen; j++) {
            if (minStr.charAt(j) != maxStr.charAt(j))
                dist++;
        }
        return dist;
    }
    
    
    
    /************************************************************************
     * Binary difference between strings, 
     * where maxStr must not be shorter than minStr.
     * Binary means add 1 or -1 for each position where strA and strB differ,
     * or in other words, sub the signs of the difference at each position.
     */
    public static int binaryDifference(String strA, String strB)
    {
        return binaryDifference(new MaxMinStr(strA, strB));
    }
    static int binaryDifference(MaxMinStr mms)
    {
        return binaryDifference(mms.maxStr, mms.minStr, mms.maxLen, mms.minLen);
    }
    protected static int binaryDifference(String maxStr, String minStr, int maxLen, int minLen)
    {
        int dist = maxLen - minLen;
        for (int j = 0; j < minLen; j++) {
            if (minStr.charAt(j) != maxStr.charAt(j))
                dist++;
        }
        return dist;
    }
    
    /***********************************************************************
     * Minimum binary distance between strings, over all possible starting 
     * points, where maxStr must not be shorter than minStr.
     */
    public static int minBinaryDistance(String strA, String strB)
    {
        return minBinaryDistance(new MaxMinStr(strA, strB));
    }
    public static int minBinaryDistance(MaxMinStr mms)
    {
        return minBinaryDistance(mms.maxStr, mms.minStr, mms.maxLen, mms.minLen);
    }
    protected static int minBinaryDistance(String maxStr, String minStr, int maxLen, int minLen)
    {
        int minDist = Integer.MAX_VALUE;
        int difLen = maxLen - minLen;   
        for (int r = 0; r <= difLen; r++) {
            int dist = 0;
            for (int j = 0, k = r; j < minLen; j++, k++) {
                if (minStr.charAt(j) != maxStr.charAt(k))
                    dist++;
            }
            if (minDist > dist)
                minDist = dist;
        }
        return minDist + difLen;
    }
    
    /************************************************************************
     * Sum-of-absolute-differences distance between strings, 
     * where maxStr must not be shorter than minStr.
     */
    public static int differenceDistance(String strA, String strB)
    {
        return differenceDistance(new MaxMinStr(strA, strB));
    }
    static int differenceDistance(MaxMinStr mms)
    {
        return differenceDistance(mms.maxStr, mms.minStr, mms.maxLen, mms.minLen);
    }
    protected static int differenceDistance(String maxStr, String minStr, int maxLen, int minLen)
    {
        int dist = 0, j = 0;
        for ( ; j < minLen; j++) {
            int dif = minStr.charAt(j) - maxStr.charAt(j);
            if (dif < 0)
                dist -= dif;
            else
                dist += dif;
        }
        // Add difference in length times average pairwise difference?
        dist += (maxLen - minLen) * dist / minLen;
        return dist;
    }
    
    /***********************************************************************
     * Minimum sum-of-absolute-differences between strings, over all possible
     * starting points, where maxStr must not be shorter than minStr.
     */
    public static int minDifferenceDistance(String strA, String strB)
    {
        return minDifferenceDistance(new MaxMinStr(strA, strB));
    }
    public static int minDifferenceDistance(MaxMinStr mms)
    {
        return minDifferenceDistance(mms.maxStr, mms.minStr, mms.maxLen, mms.minLen);
    }
    protected static int offsetDiffDistance(String maxStr, String minStr, int maxOff, int minOff, int cmpLen)
    {
        int dist = 0;
        for (int j = maxOff, k = minOff, end = j + cmpLen; j < end; j++, k++) {
            int dif = maxStr.charAt(j) - minStr.charAt(k);
            if (dif < 0)
                dist -= dif;
            else
                dist += dif;
        }
        return dist;
    }
    protected static int minDifferenceDistance(String maxStr, String minStr, int maxLen, int minLen)
    {
        int minDist = Integer.MAX_VALUE;
        int difLen = maxLen - minLen;   
        for (int r = 0; r <= difLen; r++) {
            int dist = offsetDiffDistance(maxStr, minStr, r, 0, minLen);

            // Add difference in length times average pairwise difference?
            dist += difLen * dist / minLen;
            if (minDist > dist)
                minDist = dist;
        }
        return minDist;
    }
    
    /***********************************************************************
     * Minimum sum-of-absolute-differences between strings, over all possible
     * starting points and most divisions into 2 segmentations, 
     */
    public static int minSegDifDistance(String strA, String strB)
    {
        return minSegDifDistance(new MaxMinStr(strA, strB));
    }
    public static int minSegDifDistance(MaxMinStr mms)
    {
        return minSegDifDistance(mms.maxStr, mms.minStr, mms.maxLen, mms.minLen);
    }
    /** maxStr must not be shorter than minStr. */
    protected static int minSegDifDistance(String maxStr, String minStr, int maxLen, int minLen)
    {
        int dist,  minDist = Integer.MAX_VALUE;
        int dist0;
        int dist1;
        int difLen = maxLen - minLen;
        for (int d = 1; d < minLen-1; d++) {
            for (int r = 0; r <= difLen; r++) {
                dist0 = offsetDiffDistance(maxStr, minStr, r, 0, d);
                for (int q = 0; q <= difLen - r; q++) {
                    dist1 = offsetDiffDistance(maxStr, minStr, q+d, d, minLen-d);
                    dist = dist0 + dist1;
                    if (minDist > dist)
                        minDist = dist;
                }
                
                // Add difference in length times average pairwise difference?
                //dist0 += dist0 / minLen;
                //dist1 += dist1 / minLen;
//                dist = dist0 + dist1;
//                if (minDist > dist)
//                    minDist = dist;
            }
        }
        return minDist;
    }
    
    /************************************************************************
     * unit_test
     */
    public static int unit_test()
    {
        String testName = FuzzyStrings.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        try {
            MaxMinStr mms = new MaxMinStr("null", "empty");
            Sx.puts(mms.toString());
        } catch (Throwable ex) {
            Sx.puts(ex.getClass().getName());
            Sx.puts(ex.getMessage());
            ex.printStackTrace();
        }
        String st[] = {
                "I'm a walrus in Belarus?  Preposterous!" , 
                "Your'e a walnut in Belfast?  Preposterous!", 
                "Any old winner in Belgrade is prosperous!",
                "Any cold winter in Bulgaria isn't an onus!",
                "An old wanker in Bolivia wasn't an anus until after the war.",
                "A wind banker in Borneo wasn't on bonus time until after the wars.",
                "Any wind banker in Borneo wasn't on bonus time until after the wars.",
                "Any wind banker in Borneo wasn't  on bonus time until after the wars.",
                "Any wind bonker in Borneo wasn't down bonus time until after the cars.",
        };
        String ss[] = new String[st.length];
        for (int j = 0; j < st.length; j++)
            ss[j] = new String(st[j]);
        for (int j = 1; j < ss.length; j++) {
            Sx.format("binaryDistance %4d: (%s) (%s)\n", binaryDistance(ss[j-1], ss[j]), ss[j-1], ss[j]);
            Sx.format("minBinDistance %4d: (%s) (%s)\n", minBinaryDistance(ss[j], ss[j-1]), ss[j], ss[j-1]);
            Sx.format("differDistance %4d: (%s) (%s)\n", differenceDistance(ss[j-1], ss[j]), ss[j-1], ss[j]);
            Sx.format("minDifDistance %4d: (%s) (%s)\n", minDifferenceDistance(ss[j], ss[j-1]), ss[j], ss[j-1]);
            Sx.format("minSegDifDist  %4d: (%s) (%s)\n", minSegDifDistance(ss[j-1], ss[j]), ss[j-1], ss[j]);
            Sx.puts();
        }
        Sz.end(testName, numWrong);
        return 0;
    }
    
    public static void main(String[] args) {  unit_test(); }
}
