package sprax.hash;

import java.util.Arrays;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class RollingHash
{
    
    /**
    */
    public static int test_arr(int[] arr) {
        arr[1] *= 2;
        arr[0] *= 2;
        Arrays.sort(arr);
        return 0;
    }
    
    public static int unit_test(int level) {
        String testName = RollingHash.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
/*        StringBuilder sb = new StringBuilder("a");
        for (int j = 0; j < 16; j++) {
            String ss = sb.toString();
            int hash = ss.hashCode();
            Sx.format("%d <- %s\n", hash, ss);
            sb.append('a');
        }
*/        
        int arr[] = {5,4,3,2};
        Sx.putsArray(arr);
        test_arr(arr);
        Sx.putsArray(arr);
        
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test(2);
    }
    
}
