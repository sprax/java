package sprax.questions;

import java.util.Scanner;

import sprax.test.Sz;

/**
 * https://www.hackerrank.com/challenges/possible-path
 */
public class PossiblePath2d
{
    public static void test_path() {
        try (Scanner in = new Scanner(System.in)) {
            int numCases = in.nextInt();
            for (int j = 0; j < numCases; j++) {
                int a = in.nextInt();
                int b = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                
                boolean xok = (x - a) % b == 0;
                boolean yok = (y - a) % b == 0;
                if (xok && yok)
                    System.out.println("YES");
                else
                    System.out.println("NO");
            }
        }
    }
    
    public static int unit_test() {
        String testName = PossiblePath2d.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
