/** sprax 2016.03 */

package sprax.strings;


import java.util.ArrayList;

import sprax.sprout.Sx;
import sprax.test.Sz;


/**
 Original statement: 
 Given a number print the number of combinations you can derive from the number. 1=A, 2=B, 26=Z, 0=+. 
 For example: 1123 can be parsed 5 different ways:
 1)  1,1,2,3 which corresponds to the letter sequence AABC
 3)  1,1,23 - AAW 
 4)  11,2,3 - JBC 
 4)  11,2,3 - JBC 
 5)  1,12,3 - AKC 

 But what do they mean, "0=+"?

 Instead, let's map 0:A, 1:B, 2:C, 3:D, 4:E, 5:F, 6:G, 7:H, 8:I, 9:J, 10:K . . . 25:Z.
 * 
 * 
 * @author sprax
 *
 */
public class IntToCharSeqs {

    public static char digitToLetter(char digit) {
        return (char)('A' - '0' + digit);
    }    
    
    public static char numberToLetter(int number) {
        return (char)('A' - '0' + number);
    }
    
    public static int toCharSequences(int input)
    {
        if (input < 0)
            return 0;

        String str = String.valueOf(input);
        int len = str.length();
        ArrayList<Character> list = new ArrayList<>();
        return toCharSequencesRec(str, len, 0, list);
    }
    
    private static int toCharSequencesRec(String str, int length, int index, ArrayList<Character> list)
    {
        if (index >= length) {
            Sx.putsList(list);
            return 1;
        }

        char char0 = str.charAt(index);
        if (++index == length || char0 > '2') {
            list.add(digitToLetter(char0));
            return toCharSequencesRec(str, length, index, list);
        } 

        char char1 = str.charAt(index);
        if (char0 == '1' || (char0 == '2' && char1 < '6')) {
            ArrayList<Character> newList = new ArrayList<>(list);
            list.add(digitToLetter(char0));
            int val = 10*(char0 -'0') + char1;
            char chr = numberToLetter(val);
            
            newList.add(chr);
            return toCharSequencesRec(str, length, index, list) +
                    toCharSequencesRec(str, length, index + 1, newList);
        }
        
        list.add(digitToLetter(char0));
        return toCharSequencesRec(str, length, index, list);
    }

    /** To get only the number of sequences, we can discard info. */
    public static int numCharSequencesFromInt(int input)
    {
        if (input < 0)
            return 0;

        String str = String.valueOf(input);
        int len = str.length();
        return numCharSequencesRec(str, len - 1, 0);
    }


    private static int numCharSequencesRec(String str, int length, int index)
    {
        if (length <= index) {
            return 1;
        }

        char char0 = str.charAt(index);
        if (char0 > '2') {
            return numCharSequencesRec(str, length, index + 1);
        } 

        char char1 = str.charAt(index + 1);
        if (char0 == '1' || (char0 == '2' && char1 < '6')) {
            return numCharSequencesRec(str, length, index + 1) +
                   numCharSequencesRec(str, length, index + 2);
        }
        
        return numCharSequencesRec(str, length, index + 1);
    }

    public static boolean testOneInput(int input, int expected)
    {
        int result = numCharSequencesFromInt(input);
        Sx.format("%d => %d\n", input, result);
        int numWrong = Sz.oneWrong(result, expected);
        
        result = toCharSequences(input);
        numWrong += Sz.oneWrong(result, expected);
        
        return numWrong == 0;
    }

    public static int unit_test(int level)
    {
        String testName = IntToCharSeqs.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        testOneInput(9, 1);
        testOneInput(333, 1);
        testOneInput(55555, 1);
        testOneInput(2525, 4);     // (2,5,2,5) (25,2,5) (25,25) (2,5,25)
        testOneInput(1123, 5);     // (1,1,2,3) (11,2,3) (11,23) (1,12,3) (1,1,23)
        testOneInput(18215, 6);    // (1,8,2,1,5) (18,2,1,5) (18,21,5) (18,2,15) (1,8,21,5) (1,8,2,15)
        testOneInput(12012, 6);    // (1,2,0,1,2) (12,0,1,2) (12,0,12) (1,2,0,12) (1,20,12) (1,20,1,2)

        Sx.puts(testName + " END,  status: PASSED");
        return 0;
    }

    public static void main(String args[]) { unit_test(1); }
}
