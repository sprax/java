/**
 * 
 */
package sprax.strings;

import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;


public class RandomString 
{
    public static final String UPPER_CASE_ASCII_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final char[] UPPER_CASE_ASCII_LETTERS = UPPER_CASE_ASCII_STRING.toCharArray();

    public static final String LOWER_CASE_ASCII_STRING = "abcdefghijklmnopqrstuvwxyz";
    public static final char[] LOWER_CASE_ASCII_LETTERS = LOWER_CASE_ASCII_STRING.toCharArray();
    
    public static final String NUMERALS_STRING = "0123456789";
    public static final char[] NUMERIC_LETTERS = NUMERALS_STRING.toCharArray();

    public static final String ALPHANUMERIC_ASCII_STRING = NUMERALS_STRING + "_" + UPPER_CASE_ASCII_STRING + LOWER_CASE_ASCII_STRING;
    public static final char[] ALPHANUMERIC_ASCII_CHARRAY = ALPHANUMERIC_ASCII_STRING.toCharArray();
    
    
    public static String makeRandomLowerCaseString(int strlength, Random rng)
    { return makeRandomString(LOWER_CASE_ASCII_LETTERS, strlength, rng); }
    
    public static String makeRandomUpperCaseString(int strlength, Random rng)
    { return makeRandomString(UPPER_CASE_ASCII_LETTERS, strlength, rng); }
    
    public static String makeRandomAlphaNumericString(int strlength, Random rng)
    { return makeRandomString(ALPHANUMERIC_ASCII_CHARRAY, strlength, rng); }
    
        
    public static String makeRandomString(char charSet[], int strlength, Random rng)
    {
        if (charSet == null || strlength < 1 || rng == null)
            throw new IllegalArgumentException("null input");
        
        StringBuilder builder = new StringBuilder(strlength);
        for (int j = 0; j < strlength; j++) {
            int ix = rng.nextInt(charSet.length);
            builder.append(charSet[ix]);
        }
        return builder.toString();
    }
    
         
    public static int unit_test(int level)
    {
        String  testName = RandomString.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int maxLen = 64;
        Random rng = new Random(System.currentTimeMillis());
        int slen = 0;
        while(++slen < maxLen) {
            String rands = makeRandomLowerCaseString(slen, rng);
            Sx.puts(rands);
        }        
        while(--slen > 0) {
            String rands = makeRandomUpperCaseString(slen, rng);
            Sx.puts(rands);
        }
        while(++slen < maxLen) {
            String rands = makeRandomAlphaNumericString(slen, rng);
            Sx.puts(rands);
        }        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {  
        unit_test(2);
    }
}
