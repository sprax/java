package sprax.strings;

import sprax.sprout.Sx;
import sprax.test.Sz;

/** function(s) to return longest substring lexicographically greater than the whole string. */
public class LongestGreaterSubstring
{
    // ad hoc search for first greater character followed by
    // brute force back-tracking to grow prefix; no extra space needed.
    static String longestGreaterSubstring(String string)
    {
        String result = "";
        if (string == null || string.isEmpty())
            return result;                          // GIGO
        
        // Find the index (beg) of the first char > string[0] or > string[m]
        // where all the characters immediately preceding the found character
        // match the beginning of the string, i.e. string[0 .. m-1]
        char c0 = string.charAt(0);
        int beg = 0, same = 0;
        while (++beg < string.length()) {
            
            char chr = string.charAt(beg);
            if (chr == string.charAt(same))
                same++;
            else if (chr > string.charAt(same)) {
                beg -= same;
                break;
            } else {
                same = 0;
            }
        }
        int strlen = string.length();
        if (beg < strlen) {
            // Now that we know string[beg ..] > string[0 ..], we find the
            // longest prefix string[pre .. beg-1] matching string[0 .. k] where 0 < pre < beg and 0 <= k < beg
            int pre = beg;
            while (--pre > 0) {
                if (string.charAt(pre) == c0) {
                    int m = 0, prefSize = beg - pre;
                    while (++m < prefSize) {
                        if (string.charAt(m) != string.charAt(pre + m))
                            break;                      // inner break
                    }
                    if (m == prefSize) {
                        beg = pre;                      // prefix string[pre .. beg-1] == string[0 .. beg-1-pre]
                    } else {
                        break;                          // outer break
                    }
                }
            }
            result = string.substring(beg);
        }
        return result;
    }
    
    
    /**
     * Modified from EPavlova on CareerCup: 
     * Let's use Failure function of KMP algorithm. 
     * First calculate it. Then iterate the array and each time get Failure[i] 
     * which is the end of the longest prefix that is suffix to the substring[0...i] 
     * We check if next character a[i +1] is greater than arr[Failure[i]+1].
     * That's is the place where we have first mismatch.j
     * In case it is greater, we have found the longest substring that is 
     * lexicographic greater than our string S.
     * Otherwise we continues to iterate the array with incremented i. 
     * Time complexity - O(n), space complexity O(n)
     * @param str
     * @return
     */
    static String longestGreaterSubstringKMP(String str) 
    {
        String result = "";
        int[] failure = new int[str.length()];
        failure[0] = -1;
        int i = 1;
        //compute failure function
        while (i < str.length()) {
            int cur = failure[i - 1];
            while (str.charAt(i) != str.charAt(cur + 1)) {
                if (cur != -1)
                    cur = failure[cur];
                else
                    break;
            }
            if (str.charAt(i) == str.charAt(cur + 1)) {
                failure[i] = cur + 1;
            }
            else
                failure[i] = -1;
            i++;
        }
        for (i = 1; i < str.length() - 1; i++) {
            if (failure[i] == -1) {
                if (str.charAt(0) < str.charAt(i)) {
                    result = str.substring(i);
                    break;
                }
            }
            else {
                if (str.charAt(failure[i] + 1) < str.charAt(i + 1)) {
                    result = str.substring(i - failure[i]);
                    break;
                }
            }
        }
        if (str.charAt(0) < str.charAt(i))
            return str.substring(i);
        return result;
    }
    
    
    //// TESTING ////
    
    public static int testOne(String str, String expected) {
        //String result = longestGreaterSubstring(str);
        String result = longestGreaterSubstring(str);
        String resKMP = longestGreaterSubstringKMP(str);
        int numWrong = Sz.oneIfDiff(result, expected);
        numWrong    += Sz.oneIfDiff(result, resKMP);
        Sx.format("'%s' ==> '%s' =?= '%s'\n", str, result, resKMP);
        return numWrong;
    }
    
    public static int unit_test() {
        String testName = LongestGreaterSubstring.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += testOne("aardvark", "ardvark");
        numWrong += testOne("thethezoo", "thezoo");
        numWrong += testOne("thetheaterzoo", "zoo");
        numWrong += testOne("zzzyxwvutsrqp", "");
        numWrong += testOne("thezoothezoo", "zoothezoo");
        numWrong += testOne("thethethezoo", "thethezoo");
        numWrong += testOne("thethirdthezoo", "thirdthezoo");
        numWrong += testOne("thethethirdthezoo", "thethirdthezoo");
        numWrong += testOne("thearielthethirdthezoo", "thethirdthezoo");

        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
