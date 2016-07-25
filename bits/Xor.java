package sprax.bits;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sprax.sprout.Sx;
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
        
        
        String  strSame = "a(\\d)b(\\1)c";
        Pattern patSame = Pattern.compile(strSame);
        String  strDiff = "a(\\d)b(?!\\1)c";
        Pattern patDiff = Pattern.compile(strDiff);

        String  wrdSame = "a7b7c";
        String  wrdDiff = "a2b4c";

        Pattern pat = patSame;
        Matcher match = pat.matcher(wrdSame);
        if (match.matches()) {
            Sx.format("Pos backreference: %s matches %s\n", pat.pattern(), wrdSame);
        }
        match = pat.matcher(wrdDiff);
        if (!match.matches()) {
            Sx.format("Pos backreference: %s doesn't match %s\n", pat.pattern(), wrdDiff);
        }
        
        pat = patDiff;
        match = pat.matcher(wrdSame);
        if (match.matches()) {
            Sx.format("Neg backreference: %s matches %s\n", pat.pattern(), wrdSame);
        }
        match = pat.matcher(wrdDiff);
        if (!match.matches()) {
            Sx.format("Neg backreference: %s doesn't match %s\n", pat.pattern(), wrdDiff);
        }
        
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }

    public static void main(String[] args)
    {
        unit_test();
    }
}
