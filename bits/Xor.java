package sprax.bits;

import sprax.test.Sz;

public class Xor 
{   
    public static int unit_test()
    {
        String testName = Xor.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        boolean[] all = { false, true };
        for (boolean a : all) {
            for (boolean b : all) {
                boolean c = a ^ b;
                System.out.println(a + " ^ " + b + " = " + c);
            }
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
