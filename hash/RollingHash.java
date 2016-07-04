package sprax.hash;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class RollingHash
{
    
    /**
    */
    public static int test_time2vs3WayMergesRowColSortedArray(int nRowsOver3, int nCols) {
        
        return 0;
    }
    
    public static int unit_test(int level) {
        String testName = RollingHash.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        StringBuilder sb = new StringBuilder("a");
        for (int j = 0; j < 16; j++) {
            String ss = sb.toString();
            int hash = ss.hashCode();
            Sx.format("%d <- %s\n", hash, ss);
            sb.append('a');
        }
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(2);
    }
    
}
