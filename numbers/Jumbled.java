package sprax.numbers;

import java.util.Arrays;

import sprax.sprout.Sx;
import sprax.test.Sz;

/**
 * Write all jumbled numbers J s.t. 0 < J < N, where N is provided by the caller. 
 * A jumbled number is defined as a number in which neighboring 
 * digits (either left or right) differ by at most 1 value.  For example: 
 * 8987 is a jumbled number. 
 * 13 is not a jumbled number. 
 * 123456 is a jumbled number. 
 * 287 is not jumbled number.
 * Note: from the definition above, 10 is jumbled, and 90 is not, that is,  0 is adjacent only
 * to 1, not to 9 (max value difference of 1). 
 * [[Microsoft interview question?  https://www.careercup.com/question?id=5729332770111488]]
 * @author Sprax    2016.06.15
 */
public class Jumbled
{
    public static boolean isJumbled(int num)
    {
        int prd, nxd;   // previous and next digits, counting down from the end
        int dfd;        // difference between adjacent digits
        prd = num % 10;
        num = num / 10;
        while (num != 0) {
            nxd = num % 10;
            dfd = nxd - prd;
            if (dfd < -1 || dfd > 1)
                return false;
            num = num / 10;
        }
        return true;
    }
    
    public static int[] jumbledBetween0andN(int maxNum)
    {
        int jum[] = new int[maxNum+1];
        int numJum = 0;
        for (int j = 1; j < maxNum; j++) {
            if (isJumbled(j)) {
                jum[numJum++] = j;
            }
        }
        return Arrays.copyOf(jum,  numJum);
    }
    
    
    
    public static int unit_test(int lvl)
    {   
        String testName = Jumbled.class.getName() + ".unit_test";  
        Sz.begin(testName);
        int numWrong = 0;
    
        numWrong += Sz.oneIfDiff(isJumbled(  0), true);
        numWrong += Sz.oneIfDiff(isJumbled( -1), true);
        numWrong += Sz.oneIfDiff(isJumbled(  1), true);
        numWrong += Sz.oneIfDiff(isJumbled(-98), true);
        numWrong += Sz.oneIfDiff(isJumbled( 89), true);
        numWrong += Sz.oneIfDiff(isJumbled( 90), false);
        numWrong += Sz.oneIfDiff(isJumbled(100), true);

        int maxNum = 100;
        Sx.format("All jumbled numbers J: 0 < J < %d:\n", maxNum);
        int jumbled[] = jumbledBetween0andN(maxNum);
        Sx.printArrayFolded(jumbled, 10);
        Sx.puts();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
