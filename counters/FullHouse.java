package sprax.counters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import sprax.Sz;
import sprax.sprout.Sx;

public class FullHouse
{
    /**
     * isFullHousePSX uses XOR, SUM, and PRODUCT plus some arithmetic to determine if the input is 3
     * of one int and 2 of another (like a full house). Pros: One pass, minimal extra storage. Cons:
     * Fails to distinguish 5 of a kind from 3 and 2 of a kind. Can give false positive if one of
     * the inputs is 0 (because then product==0, so if x or y == 0, x*x*x*y*y is also 0). It seems
     * like only 3 equations are being used to determine 5 quantities.
     * 
     * Counter examples: [2,2,3,3,6] [3,3,10,10,15], etc. Your methods also fails if 0 is allowed,
     * as in [0,1,1,1,1]. Your method does not distinguish between 5 of a kind and a full house,
     * though to be fair, neither did the question. It's probably best to have asked the
     * interviewer. To minimize complexity (not maximize cleverness), you might as well just count
     * how many distinct values there are, as well as how many of each value. You can return false
     * in less than one complete pass, depending on the input.
     * 
     * @return true if input is 2 of one int and 3 of another.
     */
    public static boolean isFullHousePSX(int n1, int n2, int n3, int n4, int n5, boolean bPrint)
    {
        int Z[] = { n1, n2, n3, n4, n5 };
        int x = 0, y;
        int sum = 0, product = 1;
        
        for (int i = 0; i < 5; i++)
        {
            x ^= Z[i];
            sum += Z[i];
            product *= Z[i];
        }
        y = (sum - 3 * x) / 2;
        
        boolean bFullHosue = (x * x * x * y * y == product);
        if (bPrint)
            Sx.format("isFullHousePSX: %d %d %d %d %d ==> %s with X = %d and Y = %d\n"
                    , n1, n2, n3, n4, n5, bFullHosue, x, y);
        return bFullHosue;
    }
    
    /**
     * isFullHouseMap uses a map to count the occurrences each value in the input. The input is
     * deemed a "full house" if the map ends up with exactly 2 keys and the values 2 and 3. Pros:
     * distinguishes full house from five of a kind. Cons: Overhead of managing a small map. But if
     * the app needs to check many inputs, of course the map can be made persistent and just cleared
     * before each usage. (Not static, but a different map owned by each instance.)
     */
    public static boolean isFullHouseMap(int n1, int n2, int n3, int n4, int n5, boolean bPrint)
    {
        int Z[] = { n1, n2, n3, n4, n5 };
        Map<Integer, Integer> counts = new HashMap<>();
        for (int z : Z) {
            Integer count = counts.get(z);
            if (count == null)
                count = 1;
            else
                count++;
            counts.put(z, count);
        }
        boolean bFullHouse = (counts.keySet().size() == 2 && counts.containsValue(2) && counts
                .containsValue(3));
        if (bPrint)
        {
            if (bFullHouse)
            {
                Iterator<Integer> keys = counts.keySet().iterator();
                int pairVal, threeVal, firstKey = keys.next();
                if (counts.get(firstKey) == 2)
                {
                    pairVal = firstKey;
                    threeVal = keys.next();
                }
                else
                {
                    threeVal = firstKey;
                    pairVal = keys.next();
                }
                Sx.format(
                        "isFullHouseMap: %d %d %d %d %d ==> %s with a pair of %ds and three %ds\n"
                        , n1, n2, n3, n4, n5, bFullHouse, pairVal, threeVal);
            }
            else
            {
                Sx.format("isFullHouseMap: %d %d %d %d %d ==> %s\n"
                        , n1, n2, n3, n4, n5, bFullHouse);
            }
        }
        return bFullHouse;
    }
    
    /**
     * isFullHouseCount simply counts how many of the numbers are the same as the first one, how are
     * the same as the next different number, and returns false early if a third value is found. If
     * there are exactly two different values and their counts are 2 and 3
     */
    public static boolean isFullHouseCount(int n1, int n2, int n3, int n4, int n5, boolean bPrint)
    {
        int Z[] = { n1, n2, n3, n4, n5 };
        int countFirst = 1, countOther = 0, valueOther = 0;
        for (int j = 1; j < 5; j++) {
            if (Z[j] == Z[0]) {
                ++countFirst;
            } else {
                ++countOther;
                if (countOther == 1)
                    valueOther = Z[j];
                else if (valueOther != Z[j]) {
                    return false; // Found a 3rd value
                }
            }
        }
        // Still here? Then there are at most two values.
        // If we letting 5-of-a-kind count as a fullhouse, then also check for countFirst == 5
        boolean bFullHouse = (countFirst == 2 && countOther == 3)
                || (countFirst == 3 && countOther == 2);
        if (bPrint)
        {
            if (bFullHouse)
            {
                int pairVal, threeVal;
                if (countFirst == 2)
                {
                    pairVal = Z[0];
                    threeVal = valueOther;
                }
                else
                {
                    pairVal = valueOther;
                    threeVal = Z[0];
                }
                Sx.format(
                        "isFullHouseCount: %d %d %d %d %d ==> %s with a pair of %ds and three %ds\n"
                        , n1, n2, n3, n4, n5, bFullHouse, pairVal, threeVal);
            }
            else
            {
                Sx.format("isFullHouseCount: %d %d %d %d %d ==> %s\n"
                        , n1, n2, n3, n4, n5, bFullHouse);
            }
        }
        return bFullHouse;
    }
    
    static void compareThreeMethods(int j, int k, int l, int m, int n)
    {
        boolean bPrint = false;
        boolean bMap = isFullHouseMap(j, k, l, m, n, bPrint);
        boolean bCnt = isFullHouseCount(j, k, l, m, n, bPrint);
        boolean bPsx = isFullHousePSX(j, k, l, m, n, bPrint);
        if (bMap != bCnt || bMap != bPsx)
        {
            Sx.format("\nFailure at %2d %2d %2d %2d %2d\n", j, k, l, m, n);
            bPrint = true;
            bMap = isFullHouseMap(j, k, l, m, n, bPrint);
            bCnt = isFullHouseCount(j, k, l, m, n, bPrint);
            bPsx = isFullHousePSX(j, k, l, m, n, bPrint);
        }
    }
    
    /**
     * @return
     */
    public static int test_fullHouse()
    {
        isFullHousePSX(3, 2, 3, 2, 3, true);
        isFullHousePSX(-3, 2, 3, 2, -3, true);
        isFullHousePSX(-3, 2, -3, 2, -3, true);
        isFullHousePSX(5, 5, 5, 5, 5, true);
        isFullHousePSX(2, 2, 2, 4, 4, true);
        isFullHousePSX(4, 4, 4, 4, 4, true);
        isFullHousePSX(2, 2, 3, 3, 6, true);
        
        int max = 24;
        for (int j = 0; ++j < max;) {
            for (int k = 0; ++k < max;) {
                for (int l = 0; ++l < max;) {
                    for (int m = 0; ++m < max;) {
                        for (int n = 0; ++n < max;) {
                            compareThreeMethods(j, k, l, m, n);
                        }
                    }
                }
            }
        }
        return 0;
    }
    
    public static int unit_test()
    {
        String testName = FullHouse.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = test_fullHouse();
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
    
}
