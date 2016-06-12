package sprax.questions;

import sprax.sprout.Sx;

/** function(s) to return longest substring lexicographically greater than the whole string. */
public class LongestGreaterSubString
{

    /**
     * EPavlova on CareerCup: 
     * Let's use Failure function of KMP algorithm. 
     * First calculate it. Then iterate the array and each time get Failure[i] 
     * which is the end of the longest prefix that is suffix to the substring[0...i] 
     * We check if next character a[i +1] is greater than arr[Failure[i]+1].
     * That's is the place where we have first mismatch.
     * In case it is greater, we have found the longest substring that is 
     * lexicographic greater than our string S.
     * Otherwise we continues to iterate the array with incremented i. 
     * Time complexity - O(n), space complexity O(n)
     * @param str
     * @return
     */
    
    static String longestGreaterSubstring(String str) {
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
        String res = null;
        for (i = 1; i < str.length() - 1; i++) {
            if (failure[i] == -1) {
                if (str.charAt(0) < str.charAt(i)) {
                    res = str.substring(i);
                    break;
                }
            }
            else {
                if (str.charAt(failure[i] + 1) < str.charAt(i + 1)) {
                    res = str.substring(i - failure[i]);
                    break;
                }
            }
        }
        if (str.charAt(0) < str.charAt(i))
            return str.substring(i);
        return res;
    }
    
    public static void main(String[] args) {
        String str, out;
        str = "aardvark";
        out = longestGreaterSubstring(str);
        Sx.format("%s ==> %s\n", str, out);
        
    }
    
}
