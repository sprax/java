package sprax.strings;

import sprax.Sz;
import sprax.sprout.Sx;

public class StringCommonChars
{
    /**
     * Return buffer containing all chars in stringA that are also in stringB,
     * in the order or stringA. Common letters appear in the output as many
     * times as they occur in stringA, even if that is more times than they
     * appear in stringB.
     * 
     * @param stringA
     * @param stringB
     * @return
     */
    public static StringBuffer commonCharacters(String stringA, String stringB)
    {
        // Two way comparison.  Output the common characters in the order of sa.
        final int alphabetSize = 256;                               // 8-bit characters
        boolean isCharInStringB[] = new boolean[alphabetSize];      // array of booleans initialized to false
        for (int j = 0; j < stringB.length(); j++) {
            char index = stringB.charAt(j);                              // Java char is UTF-16, basically a C unsigned short
            isCharInStringB[index] = true;
        }
        StringBuffer out = new StringBuffer();
        for (int j = 0; j < stringA.length(); j++) {
            char index = stringA.charAt(j);
            if (isCharInStringB[index]) {
                out.append((char) index);
            }
        }
        return out;
    }
    
    /**
     * Return buffer containing all chars in stringA that are also in stringB,
     * in the order or stringA. Each common char appears only once in the output,
     * even if it occurred multiple times in both stringA and stringB.
     * 
     * @param stringA
     * @param stringB
     * @return string buffer containing each common char once.
     */
    public static StringBuffer commonCharsOnce(String stringA, String stringB)
    {
        // Two way comparison.  Output the common characters in the order of sa.
        final int alphabetSize = 256;                               // 8-bit characters
        boolean commonChar[] = new boolean[alphabetSize];      // array of booleans initialized to false
        for (int j = 0; j < stringB.length(); j++) {
            char index = stringB.charAt(j);                              // Java char is UTF-16, basically a C unsigned short
            commonChar[index] = true;
        }
        StringBuffer out = new StringBuffer();
        for (int j = 0; j < stringA.length(); j++) {
            char index = stringA.charAt(j);
            if (commonChar[index]) {
                out.append((char) index);
                commonChar[index] = false;                     // append only the first occurrence
            }
        }
        return out;
    }
    
    public static StringBuffer commonCharacters(String sa, String sb, String sc) {
        // Three way comparison.  Output the common characters in the order of sa.
        final int alphabetSize = 256;
        char alphabet[] = new char[alphabetSize];     // initially all 0
        for (int j = 0; j < sb.length(); j++) {
            int index = sb.charAt(j);
            alphabet[index] = 1;
        }
        for (int j = 0; j < sc.length(); j++) {
            char index = sc.charAt(j);
            if (alphabet[index] == 1) {
                alphabet[index] = 2;
            }
        }
        StringBuffer out = new StringBuffer();
        for (int j = 0; j < sa.length(); j++) {
            char index = sa.charAt(j);
            if (alphabet[index] == 2) {
                out.append((char) index);
            }
        }
        return out;
    }
    
    public static StringBuffer commonCharacters(String sa, String so[])
    {
        // N-way comparison.  Output the common characters in the order of sa.
        int numStrs = so.length;
        final int alphabetSize = 256;
        int alphabet[] = new int[alphabetSize];
        for (int n = 0; n < numStrs; n++) {
            for (int j = 0; j < so[n].length(); j++) {
                int index = so[n].charAt(j);
                if (alphabet[index] == n) {
                    alphabet[index] = n + 1;
                }
            }
        }
        StringBuffer out = new StringBuffer();
        for (int j = 0; j < sa.length(); j++) {
            int index = sa.charAt(j);
            if (alphabet[index] == numStrs) {
                out.append((char) index);
            }
        }
        return out;
    }
    
    /**
     * unit_test
     */
    public static int unit_test() 
    {
        String testName = StringCommonChars.class.getName();
        Sz.begin(testName);
        int numWrong = 0;
        
        String sa = new String("A walrus in Belarus?  My stars!");
        String sb = new String("Isn't that preposterous?  Blech!");
        String sc = new String("The Bosporus is more prosperous!");
        String sd = new String("One bop on the head is not more preposterous!");
        
        Sx.puts("\ncommon characters 2-way:");
        StringBuffer ss = commonCharacters(sa, sb);
        Sx.puts("    " + sa);
        Sx.puts("    " + sb);
        Sx.puts(" => " + ss);
        
        Sx.puts("\ncommon characters 2-way, first occurence only:");
        ss = commonCharsOnce(sa, sb);
        Sx.puts("    " + sa);
        Sx.puts("    " + sb);
        Sx.puts(" => " + ss);
        
        Sx.puts("\ncommon characters 3-way:");
        ss = commonCharacters(sa, sb, sc);
        Sx.puts("    " + sa);
        Sx.puts("    " + sb);
        Sx.puts("    " + sc);
        Sx.puts(" => " + ss);
        
        Sx.puts("\ncommon characters N-way:");
        String strs[] = { sb, sc, sd };
        ss = commonCharacters(sa, strs);
        for (String str : strs)
            Sx.puts("    " + str);
        Sx.puts(" => " + ss);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
