package sprax.numbers;

import sprax.test.Sz;

/**
 * Write all jumbled numbers J s.t. 0 < J < N, where N is provided by the caller. 
 * A jumbled number is defined as a number in which neighboring digits (either left or right) 
 * differ by at most 1 value.  For example: 
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
    public static int unit_test(int lvl)
    {   
        String testName = Jumbled.class.getName() + ".unit_test";  
        Sz.begin(testName);
        int numWrong = 0;
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
