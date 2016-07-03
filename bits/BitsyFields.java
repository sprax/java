package sprax.bits;

import java.util.BitSet;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class BitsyFields 
{
    /**
     * Sprax sez.........................................................
     * First non-repeating/unique char in string:
     * 1) O(N) time, O(1-bit per alphabet char) space.
     * 2) Now how about O(1-bit per string char) space?
     Answer to 1) 2-passes using 2 half-alphabet bitmasks, 
     one for 1st occurrence, 2nd for repeat.
     Each pass gives a min index for a char pos that is ON in the
     first mask and not ON in the second.  If there is no answer
     in the first half of the alphabet, try the second half.
     Or, if the partition was not into first and last halves, 
     then use the smaller of the two answers.
     Answer to 2) Use answer to 1 if the string is longer than
     the alphabet.  (That's < 1 bit per string char).  Else, if
     the string is shorter than the alphabet, then create a bit mask 
     just large enough to store 1 bit per string char.

     What about a bitmask and an index.for the first char not (yet) found
     to be repeated?  

     No storage beyond temp loop vars:
     Can get worst case O(c*N), where c = size of alphabet, N = length
     of string, by simply trying each char in order.  Is 'A' repeated?
     Yes, then try 'B', and so on, until either some letter is not repeated
     or all letters have been tried and found to be repeated.

     One pass: use 2 strings, one empty, the other containing all the chars
     in order, whenever a character is found ...

     * ==================================================================
     */
    
    public Character firstUniqueCharBruteForce(String str, char[] allChars)
    {
        int j, count, length = str.length();
        for (char ch : allChars) {
            for (count = 0, j = 0; j < length; j++) {
                if (str.charAt(j) == ch && ++count > 1) {
                    break;
                }
            }
            if (count == 1)
            {
                return ch;
            }
        }
        return null;
    }
    
    
    /** 
     * Easy-reading version
     */
    public static Character firstUniqueLetter(String str)
    {   
        int posUpper = indexOfFirstUniqueLetterInRange(str, 'A', 'Z');
        int posLower = indexOfFirstUniqueLetterInRange(str, 'a', 'z');
        int posFirst = Math.min(posUpper, posLower);
        if (0 <= posFirst && posFirst < Integer.MAX_VALUE)
            return str.charAt(posFirst);
        return null;
    }
    
    /** 
     * Easy-reading version
     */
    public static Character firstUniqueCharBit(String str)
    {   
        int posUpper = indexOfFirstUniqueCharInRange(str, 'A', 'Z');
        int posLower = indexOfFirstUniqueCharInRange(str, 'a', 'z');
        int posFirst = Math.min(posUpper, posLower);
        if (0 <= posFirst && posFirst < Integer.MAX_VALUE)
            return str.charAt(posFirst);
        return null;
    }
    
    /**
     * Overly optimized version?
     * @param str
     * @return
     */
    public static Character firstUniqueLetter_overlyOptimized(String str)
    {   
        int posUpper = indexOfFirstUniqueCharInRange(str, 'A', 'Z');
        if (posUpper == Integer.MAX_VALUE)
            return firstUniqueLowerCaseLetter(str); 
        int posLower;
        if ((posUpper == 0) || 
                (posLower = indexOfFirstUniqueCharInRange(str, 'a', 'z')) == Integer.MAX_VALUE)
            return str.charAt(posUpper);
        int pos = Math.min(posUpper, posLower);
        return str.charAt(pos);  
    }
    
    public static Character firstUniqueLowerCaseLetter(String str)
    {   
        int pos = indexOfFirstUniqueCharInRange(str, 'a', 'z');
        if (pos < 0)
            return null;
        return str.charAt(pos);
    }
    
    public static Character firstUniqueUpperCaseLetter(String str)
    {   
        int pos = indexOfFirstUniqueCharInRange(str, 'A', 'Z');
        if (pos < 0)
            return null;
        return str.charAt(pos);
    }  
    
    
    /**
     * Returns the index of the first unique character in the input string that is
     * also in the input range, or Integer.MAX_VALUE if there is no such character.
     * @param str String of length < Integer.MAX_VALUE.  The input range is limited
     * by the size of type long (64 bits), so rangeMax - rangeMin < 64.
     * @param rangeMin    first character in the range, as an int value
     * @param rangeMax    last character in the range, as an int value
     * @return
     */
    public static int indexOfFirstUniqueLetterInRange(String str, int rangeMin, int rangeMax)
    {   
        if (str == null)
            throw new IllegalArgumentException("invalid string arg: " + str);
        
        int rangeSize = rangeMax - rangeMin + 1;
        if (rangeSize < 1 || rangeSize > 64)
            throw new IllegalArgumentException("invalid range size: " + rangeSize);
        
        int length = str.length();
        long maskOnce = 0;  // indicates char appeared at least once
        long maskMore = 0;  // indicates char appeared at least twice
        for (int j = 0; j < length; j++) {
            int idx = str.charAt(j) - rangeMin;
            if (0 <= idx && idx < rangeSize) {
                long bit = 1 << idx;
                if ((maskOnce & bit) != 0)
                    maskMore |= bit;
                else
                    maskOnce |= bit;
            }
        }
        if (maskOnce == maskMore)
            return Integer.MAX_VALUE;
        for (int j = 0; j < length; j++) {
            char ch = str.charAt(j);
            int idx = ch - rangeMin;
            if (0 <= idx && idx < rangeSize) {
                long bit = 1 << idx;
                if ((maskMore & bit) == 0)
                    return j;
            }
        }
        return Integer.MAX_VALUE;
    }
    /**
     * Returns the index of the first unique character in the input string that is
     * also in the input range, or Integer.MAX_VALUE if there is no such character.
     * @param str String of length < Integer.MAX_VALUE
     * @param rangeMin    first character in the range, as an int value
     * @param rangeMax    last character in the range, as an int value
     * @return
     */
    public static int indexOfFirstUniqueCharInRange(String str, int rangeMin, int rangeMax)
    {   
        if (str == null)
            throw new IllegalArgumentException("invalid string arg: " + str);
        
        int rangeSize = rangeMax - rangeMin + 1;
        if (rangeSize < 1 || rangeSize > 64)
            throw new IllegalArgumentException("invalid range size: " + rangeSize);
        
        int length = str.length();
        BitSet maskOnce = new BitSet(rangeSize);  // indicates char appeared at least once
        BitSet maskMore = new BitSet(rangeSize);  // indicates char appeared at least twice
        for (int j = 0; j < length; j++) {
            int idx = str.charAt(j) - rangeMin;
            if (0 <= idx && idx < rangeSize) {
                if (maskOnce.get(idx))
                    maskMore.set(idx);
                else
                    maskOnce.set(idx);
            }
        }
        if (maskOnce == maskMore)
            return Integer.MAX_VALUE;
        for (int j = 0; j < length; j++) {
            char ch = str.charAt(j);
            int idx = ch - rangeMin;
            if (0 <= idx && idx < rangeSize) {
                if ((maskMore.get(idx)) == false)
                    return j;
            }
        }
        return Integer.MAX_VALUE;
    }
    
    public static int test_firstUniqueLetter(String str) 
    {
        int wrong = 0;
        Character chrReg = firstUniqueLetter(str);
        Character chrOpt = firstUniqueLetter_overlyOptimized(str);
        Character chrBit = firstUniqueCharBit(str);
        if (chrReg != chrOpt || chrReg != chrBit) {
            wrong = 1;
            Sx.print("Error: ");
        }
        Sx.format("firstUniqueLetter(%s) -> %s | %s | \n", str, chrReg, chrOpt, chrBit);
        return wrong;
    }
    
    public static int unit_test() 
    {
        String testName =  BitsyFields.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        
        numWrong += test_firstUniqueLetter("aababcabcdabcdeabcdefabcdefzabcdefga");
        numWrong += test_firstUniqueLetter("ZYXWV !@#$%^&*() UTU VWXYZ");
        numWrong += test_firstUniqueLetter("aababcabcdabcdeabcdefabcdefzZYXWVUTUVWXYZabcydefga");
        numWrong += test_firstUniqueLetter("abcdefABCDEF-1234567890_FEDCBAfedcbax");
        numWrong += test_firstUniqueLetter("b");
        
        Sx.puts(" 63 >>  3 == " + ( 63 >>  3));
        Sx.puts("-63 >>  3 == " + (-63 >>  3));
        Sx.puts(" 63 >>> 3 == " + ( 63 >>> 3));
        Sx.puts("-63 >>> 3 == " + (-63 >>> 3));
        Sx.puts("~63 >>> 3 == " + (~63 >>> 3));
        Sx.puts("(INT_MAX - 63) >>> 2 == " + ((Integer.MAX_VALUE - 63) >>> 2));
        
        char hi4 = 8 + 4 + 2 + 1;
        char lo4 =     4 +     1;
        char ch8 = (char)((hi4 << 4) + lo4);
        Sx.format("hi4  lo4: (hi4 << 4) + lo4 = ch8 (c): %c %c: (%c) + %c = %c\n"
                , hi4, lo4, (hi4 << 4), lo4, ch8);
        Sx.format("hi4  lo4: (hi4 << 4) + lo4 = ch8 (d): %d %d: (%d) + %d = %d\n"
                , (int)hi4, (int)lo4, (int)(hi4 << 4), (int)lo4, (int)ch8);
        
        char maxChar = Character.MAX_VALUE;
        char minChar = Character.MIN_VALUE;
        Sx.format("Character.MAX_VALUE: %c  %d\n", maxChar, (int)maxChar);
        Sx.format("Character.MIN_VALUE: %c  %d\n", minChar, (int)minChar);
        
        Short maxShort = Short.MAX_VALUE;
        Short minShort = Short.MIN_VALUE;
        Sx.format("Short.MAX_VALUE: %d  %d\n", maxShort, (int)maxShort);
        Sx.format("Short.MIN_VALUE: %d  %d\n", minShort, (int)minShort);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
