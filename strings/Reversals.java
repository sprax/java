package sprax.strings;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * String reversals: words, sentence, or any string.
 */
public class Reversals 
{
    /** returns new string from input reversed, or null for null input. */
    public static String reverseString(String str)
    {
        char arr[] = reverseStringToChars(str);
        if (arr == null)
            return null;            // GIGO
        return new String(arr);
    }
    
    /** reverse each word separately; leaving white space in place. */
    public static String reverseEachWord(String str)
    {
        char arr[] = reverseEachWordToChars(str);
        if (arr == null)
            return null;        // GIGO
        return new String(arr);
    }
    
    /** returns new string with words and spacing in reverse order, or null for null input. */
    public static String reverseWordOrder(String str)
    {
        char arr[] = reverseStringToChars(str);
        if (arr == null)
            return null;            // GIGO
        reverseEachWord(arr);
        return new String(arr);
    }
    
    
    /** returns new string with words and spacing in reverse order, or null for null input. */
    public static String reverseWordOrderCopy(String str)
    {
        if (str == null)
            return null;            // GIGO
        char arr[] = str.toCharArray();
        char out[] = reverseWordOrderCopyIndexed(arr);
        return new String(out);
    }
    
    
    /** returns new char array from input reversed, or null from null input. */
    public static char[] reverseStringToChars(String str)
    {
        if (str == null)
            return null;            // GIGO
        char arr[] = str.toCharArray();
        reverseArray(arr);
        return arr;
    }
    
    /** reverse char array in place */
    public static void reverseArray(char[] arr)
    {
        if (arr != null && arr.length > 1) {
            reverseArrayNICE(arr, 0, arr.length);
        }
    }
    
    /** reverse char array in place, NICE = No Input Checking or Error handling */
    protected static void reverseArrayNICE(char[] arr, int beg, int end)
    {
        for (; --end > beg; ++beg) {
            char tmp = arr[beg];
            arr[beg] = arr[end];
            arr[end] = tmp;
        }
    }


    public static char[] reverseEachWordToChars(String str)
    {
        if (str == null)
            return null;        // GIGO
        char arr[] = str.toCharArray();
        reverseEachWord(arr);
        return arr;
    }
    
    public static void reverseEachWord(char arr[])
    {
        if (arr != null && arr.length > 1) { 
            char prevChar = ' ';
            int beg = 0, end;
            for (int j = 0; j < arr.length; j++) {
                char thisChar = arr[j];
                if (Character.isSpaceChar(prevChar) && ! Character.isSpaceChar(thisChar) ) {
                    beg = j;
                } else if (! Character.isSpaceChar(prevChar) && Character.isSpaceChar(thisChar) ) {
                    end = j;
                    reverseArrayNICE(arr, beg, end);
                }
                prevChar = thisChar;
            }
            if (! Character.isSpaceChar(prevChar)) {
                reverseArrayNICE(arr, beg, arr.length);
            }
        }
    }
    
    public static void reverseWordOrder(char arr[])
    {
        reverseEachWord(arr);
        reverseArray(arr);
    }
    
    /** bad solution; don't use this! 
     * @deprecated
     */
    public static char[] reverseWordOrderCopyIndexed(char arr[])
    {
        if (arr != null && arr.length > 1) { 
            int wordBegEnd[] = new int[arr.length + 1]; // word count <= (string length + 1)/2
            int begEndIndex = wordBegEndIndex(arr, wordBegEnd);
            char rev[] = new char[arr.length];
            int outIdx = 0;
            for (int w = wordBegEnd[begEndIndex-1]; w < arr.length; w++) {
                rev[outIdx++] = arr[w];
            }
            for (int j = begEndIndex; ; ) {
                int end = wordBegEnd[--j];
                int beg = wordBegEnd[--j];
                for (int k = beg; k < end; k++) {
                    rev[outIdx++] = arr[k];
                }
                if (j == 0)
                    break;
                for (int k = beg; --k >= wordBegEnd[j-1]; ) {
                    rev[outIdx++] = arr[k];
                }
            }
            return rev;
        }
        return null;
    }
    
    static int wordBegEndIndex(char arr[], int wordBegEnd[])
    {
        char prevChar = ' ';
        int begEndIndex = 0;
        for (int j = 0; j < arr.length; j++) {
            char thisChar = arr[j];
            if (Character.isSpaceChar(prevChar) && ! Character.isSpaceChar(thisChar) ) {
                wordBegEnd[begEndIndex++] = j;    // begin word
            } else if (! Character.isSpaceChar(prevChar) && Character.isSpaceChar(thisChar) ) {
                wordBegEnd[begEndIndex++] = j;    // end word
            }
            prevChar = thisChar;
        }
        if (! Character.isSpaceChar(prevChar)) {
            wordBegEnd[begEndIndex++] = arr.length;
        }
        assert(begEndIndex % 2 == 0);
        return begEndIndex;
    }
    
    public static int wrong(boolean result, boolean expected) 
    {
        return result == expected ? 0 : 1;
    }

    public static int test_reverseString(String str, String exp)
    {
        String out = reverseString(str);
        return verify_reversal(str, out, exp);
    }

    public static int verify_reversal(String str, String out, String exp)
    {
        if (out == null && out == null)
            return 0;
        boolean eqs = out.equals(exp); 
        Sx.format("%s  reversal (%s|%s=%s)\n", (eqs ? "PASS" : "FAIL"), str, out, exp);
        return wrong(eqs, true);
    }
    
    public static int unit_test(int lvl) 
    {
        String  testName = Reversals.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_reverseString(null, null);
        String str = "A";
        String rev = "A";
        numWrong += test_reverseString(str, rev);
        numWrong += test_reverseString("BC", "CB");
        numWrong += test_reverseString("DEF", "FED");
        numWrong += test_reverseString("live", "evil");
        numWrong += test_reverseString(" bad\ttab?", "?bat\tdab ");
        
        str = "a no was never ... live";
        rev = "a on saw reven ... evil";
        numWrong += verify_reversal(str, reverseEachWord(str), rev);

        str = "eno was live on TV345";
        rev = "one saw evil no 543VT";
        numWrong += verify_reversal(str, reverseEachWord(str), rev);

        str = "one 2 three  four FIVE";
        rev = "FIVE four  three 2 one";
        numWrong += verify_reversal(str, reverseWordOrder(str), rev);
        numWrong += verify_reversal(str, reverseWordOrderCopy(str), rev);

        str = "six \t SEVEN \n eight 9 ";
        rev = " 9 eight \n SEVEN \t six";
        numWrong += verify_reversal(str, reverseWordOrder(str), rev);
        numWrong += verify_reversal(str, reverseWordOrderCopy(str), rev);

        /*
        String stringA = "A collection of Sherlock Holmes detective stories.";
        String stringB = "Coveted crime classics tell of hooknose title-hero.";
        String stringC = "Heroic slack scion tells of vetoed chrome title: CEO!"; // extra C instead of S
        String stringD = "I am not worthy!";
        String stringE = "What moronity!";
        
        String stringF = "aaaa";
        String stringG = "aaa";
        System.out.format("%s: Is \"%s\" a strict anagram for \"%s\"?\n", shortName, stringF, stringG);
        boolean isAnagram = anagramsTwoMaps(stringF, stringG);
        numWrong += wrong(isAnagram, false);
        System.out.println( isAnagram ? "Yes." : "No." );
        if (isAnagram != anagramsOneMap(stringF, stringG))
            System.out.println("anagramsOneMap disagrees with anagramsTwoMaps!");
        */
          
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
