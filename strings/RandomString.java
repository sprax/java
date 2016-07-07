/**
 * 
 */
package sprax.strings;

import java.util.Random;

import sprax.sprout.Sx;
import sprax.test.Sz;


public class RandomString 
{

    public static String makeRandomString(char charSet[], int strlength, Random rng)
    {
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
        
        String chars = "abcdefghijklmnopqrstuvwxyz";
        char charSet[] = chars.toCharArray();
        Random rng = new Random(System.currentTimeMillis());
        for (int j = 1; j < 100; j++) {
            String rands = makeRandomString(charSet, j, rng);
            Sx.puts(rands);
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {  
        unit_test(2);
    }
}
