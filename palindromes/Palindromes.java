package sprax.palindromes;

import sprax.sprout.Sx;

/**
 * Basic static methods on number, string, and word palindromes, including:
 * isPalindrome, nextPalindromeNumber, with tests.
 * 
 * @author sprax
 *
 */
public class Palindromes 
{
    public static boolean isPalindrome(String str)
    {
        if (str == null)
            return false;
        return isPalindrome(str.toCharArray());
    }

    public static boolean isPalindrome(char charray[])
    {
        if (charray == null || charray.length == 0)
            return false;
        for (int iLeft = 0, iRight = charray.length - 1; iLeft < iRight; iLeft++, iRight--) {
            if (charray[iLeft] != charray[iRight])
                return false;
        }
        return true;
    }

    public static int test_isPalindrome()
    {
        int status = 0;
        // odd length
        status += isPalindrome("aBcBa") ?   0  :  -1;
        status += isPalindrome("aBcBf") ?  -2  :   0;
        // even length
        status += isPalindrome("aBccBa") ?   0  :  -4;
        status += isPalindrome("aBcCBa") ?  -8  :   0;
        return status;
    }

    /**
     * Naive Sequential Search for next palindromic whole number. 
     * @deprecated NSS
     * @param num
     * @return
     */
    private static int nextPalindromeNumberNSS(Integer num)
    {
        if (num < 0)
            return  0;
        if (num < 9)
            return ++num;
        if (num < 11)
            return  11;

        for (Integer nxt = num + 1; nxt < Integer.MAX_VALUE; nxt++) {
            if (isPalindrome(nxt.toString()))
                return nxt;
        }
        return 0;
    }



    /**
     * Given an integer value, returns the next palindromic whole number
     * as an int.  A negative number cannot be a palindrome because of
     * the minus sign, so for any positive N, -N maps to 0, and any 
     * positive single-digit number is considered a palindrome, so,
     * e.g., 0 goes to 1, 1 to 2, etc.
     * @param num Any integer value
     * @return    Next (non-negative) palindromic integer.
     */  
    public static int nextPalindromeNumber(Integer num)
    {
        if (num < 0)
            return  0;
        if (num < 9)
            return ++num;

        String str = num.toString();
        char chr[] = str.toCharArray();
        boolean increased = false;

        int len = chr.length;
        int half = len/2;
        int iLeft = half - 1;
        int iRight = len - half; // or iRight = len - iLeft + 1 and iLeft = len - iRight + 1;
        for ( ; iLeft >= 0; --iLeft, ++iRight) {
            // if (chr[iLeft] == chr[iRight])  continue; // Skip if left and right chars are the same
            if (chr[iRight] < chr[iLeft]) {
                chr[iRight++] = chr[iLeft--];
                increased = true;
                break;
            } else if (chr[iRight] > chr[iLeft]) {
                increased = true;                   // We will alter the string below...
                iLeft = half - 1;                   // Reset left and right indices to point back to the middle.
                iRight = len - half;                // We may have to copy the entire left side to the right, after breaking from this loop. 
                if (chr[iRight-1] < '9') {
                    chr[iLeft+1] = ++chr[iRight-1];   // Increment the digit in the next higher (or middle) place
                    break;
                } else {                            // We got a 9, so we must propagate the increase leftward.
                    int j = iRight - 1;               // There must be a non-9 in the left half of the number string;
                    do {                              // otherwise, we'd have already broken out of this loop.
                        chr[j--] = '0';                 // "Increment" the 9 to 0 and carry the 1
                    } while (chr[j] == '9');          // Repeat as necessary...
                    chr[j]++;                         // Add the carried 1 to the first non-9 digit and break.
                    break;
                }
            }
        }
        // If the input number was already a palindrome, return the next one:
        if ( ! increased )
            return nextPalindromeNumber(num + 1);  // This takes care of 9, 99, 999, etc.

        // Now just mirror-copy left to right:
        for ( ; iLeft >= 0; --iLeft, ++iRight)
            chr[iRight] = chr[iLeft];

        str = new String(chr);
        int ans = Integer.parseInt(str);    
        return ans;
    }

    public static int test_nextPalindromeNumberAgainstNSS(int beg, int num, int inc)
    {
        for ( ; --num >= 0; beg += inc) {
            int nxt = nextPalindromeNumber(beg);
            int nss = nextPalindromeNumberNSS(beg);
            Sx.puts(beg + " --> " + nxt + " ? " + nss);
            if (nxt != nss) {
                System.err.format("next against NSS: %d --> %d != %d\n", beg, nxt, nss);
            }
        }
        return 0;
    }

    public static int test_nextPalindromeNumberIsPalindrome(int beg, int num, int inc)
    {
        for ( ; --num >= 0; beg += inc) {
            Integer nxt = nextPalindromeNumber(beg);
            if (nxt <= beg || ! isPalindrome(nxt.toString())) {
                System.err.format("%d --> %d, not a palindrome\n", beg, nxt);
            }
        }
        return 0;
    }

    public static int unit_test()
    {
        Sx.puts(Palindromes.class.getName() + ".unit_test");

        int stat = test_isPalindrome();
        int num = 919010928;
        Sx.puts(Integer.MAX_VALUE + " > " + num);
        Sx.puts(num + " : " + nextPalindromeNumber(num)); ///////////////////////////////////////////////////////
        num = 70999908;
        Sx.puts(num + " : " + nextPalindromeNumber(num)); ///////////////////////////////////////////////////////
        num = 9;
        Sx.puts(num + " : " + nextPalindromeNumber(num)); ///////////////////////////////////////////////////////

        test_nextPalindromeNumberAgainstNSS(0, 20, 37);
        test_nextPalindromeNumberAgainstNSS(0, 11, 999);
        test_nextPalindromeNumberIsPalindrome(193, 22, 71938 - 193);
        test_nextPalindromeNumberIsPalindrome(987999, 1, 1);

        for (int next = 0, seq = 10000; --seq >= 0; ) {
            next = nextPalindromeNumber(next);
            Sx.print(" " + next);
            if (seq % 10 == 0)
                Sx.puts();
        }
        test_nextPalindromeNumberIsPalindrome(1901092, 4, 919010929 - 1901092);

        //    long begTime, endTime, difTime;
        //    begTime = System.currentTimeMillis();
        //    for (int j = 0; j < Integer.MAX_VALUE; j++)
        //      ;
        //    endTime = System.currentTimeMillis();
        //    difTime = endTime - begTime;
        //    S.puts("MAX INT (" + Integer.MAX_VALUE + ") loop time: " + difTime);
        return stat;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
