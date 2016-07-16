package sprax.questions;

import java.util.Scanner;

import sprax.test.Sz;

/**
 * https://www.hackerrank.com/challenges/find-point
 */
public class SymmetricPoint2d
{
    public static void printSymmetricPoints() {
        try (Scanner in = new Scanner(System.in)) {
            int numCases = in.nextInt();
            for (int j = 0; j < numCases; j++) {
                int px = in.nextInt();
                int py = in.nextInt();
                int qx = in.nextInt();
                int qy = in.nextInt();
                int rx = 2 * qx - px;
                int ry = 2 * qy - py;
                System.out.println(rx + " " + ry);
            }
        }
    }
    
    public static int unit_test() {
        String testName = SymmetricPoint2d.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
