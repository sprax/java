/*
 * Sprax Lines  2016.05.04
 */

package sprax.maths;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class MinimalCoinsTest
{
    static final int US_TO_1DOLLAR[] = { 1, 5, 10, 25, 50, 100 };
    
    // Could get away with a LinkedHashMap, but TreeMap guarantees sorted keys
    private static final TreeMap<Integer, Integer> US_TEST_MAP;
    static {
        US_TEST_MAP = new TreeMap<>();
        US_TEST_MAP.put( 0, 0);
        US_TEST_MAP.put( 1, 1);
        US_TEST_MAP.put( 2, 2);
        US_TEST_MAP.put( 5, 1);
        US_TEST_MAP.put(10, 1);
        US_TEST_MAP.put(13, 4);
        US_TEST_MAP.put(17, 4);
        US_TEST_MAP.put(25, 1);
        US_TEST_MAP.put(29, 5);
        US_TEST_MAP.put(39, 6);
        US_TEST_MAP.put(47, 5);
        US_TEST_MAP.put(66, 4); // 50 + 10 + 5 + 1 (naive recursion may take about 1.5 seconds)
        US_TEST_MAP.put(76, 3); // 50 + 25 + 1 (naive recursion may take a while... 15 seconds on my PC)
    }
    
    private static final TreeMap<Integer, Integer> US_SLOW_MAP;
    static {
        US_SLOW_MAP = new TreeMap<>();
        US_SLOW_MAP.put(76, 3); // 50 + 25 + 1 (naive recursion may take a while...)
        US_SLOW_MAP.put(87, 5); // 50 + 25 + 10 + 1 + 1 (naive recursion may take a long while...)
        US_SLOW_MAP.put(92, 6); // 50 + 25 + 10 + 5 + 1 + 1
        US_SLOW_MAP.put(99, 8); // 50 + 25 + 2*10 + 4*1
        US_SLOW_MAP.put(123, 6); // 100 + 2*10 + 3*1
        US_SLOW_MAP.put(148, 7); // 100 + 25 + 2*10 + 3*1
    }
    
    static final int ODD_PRIMES_13[] = { 3, 5, 7, 11, 13 };
    private static final TreeMap<Integer, Integer> PRIME_TEST_MAP;
    static {
        PRIME_TEST_MAP = new TreeMap<>();
        PRIME_TEST_MAP.put( 0,  0);
        PRIME_TEST_MAP.put( 1, -1);
        PRIME_TEST_MAP.put( 2, -1);
        PRIME_TEST_MAP.put( 3,  1);
        PRIME_TEST_MAP.put( 4, -1);
        PRIME_TEST_MAP.put( 5,  1);
        PRIME_TEST_MAP.put( 6,  2);
        PRIME_TEST_MAP.put( 8,  2);
        PRIME_TEST_MAP.put( 9,  3); // 13 + 2 + 2 or 7 + 5 + 5
        PRIME_TEST_MAP.put(12,  2); // 7 + 5
        PRIME_TEST_MAP.put(17,  3); // 7 + 5 + 5
        PRIME_TEST_MAP.put(27,  3); // 13 + 11 + 3
        PRIME_TEST_MAP.put(31,  3);
        PRIME_TEST_MAP.put(47,  5);
        PRIME_TEST_MAP.put(77,  7); // 5*13 + 7 + 5 or 7*11
    }
    
    static final int MCNUGGET_SIZES[] = { 6, 9, 19 };
    private static final TreeMap<Integer, Integer> NUGGET_TEST_MAP;
    static {
        NUGGET_TEST_MAP = new TreeMap<>();
        NUGGET_TEST_MAP.put( 0,  0);
        NUGGET_TEST_MAP.put( 1, -1);
        NUGGET_TEST_MAP.put( 2, -1);
        NUGGET_TEST_MAP.put( 3, -1);
        NUGGET_TEST_MAP.put( 5, -1);
        NUGGET_TEST_MAP.put( 6,  1);
        NUGGET_TEST_MAP.put( 8, -1);
        NUGGET_TEST_MAP.put( 9,  1);
        NUGGET_TEST_MAP.put(12,  2);
        NUGGET_TEST_MAP.put(21,  3); // 7 + 5 + 5
        NUGGET_TEST_MAP.put(25,  2); // 13 + 11 + 3
        NUGGET_TEST_MAP.put(31,  3);
        NUGGET_TEST_MAP.put(47,  3); // 19 + 19 + 9
        NUGGET_TEST_MAP.put(77,  7); // 2*19 + 3*9 + 2*6
    }
    
    public static int test_minCoinsMap(MinimalCoins testInstance, Map<Integer, Integer> testMap,
            String label, int verbose)
    {
        int numWrong = 0;
        if (verbose > 0) {
            System.out.format("%s: testing %s.minCoins\n", label, testInstance.getClass().getName());
        }
        
        long startTime = System.currentTimeMillis();
        for (int sum : testMap.keySet()) {
            if (!test_minCoins(testInstance, sum, testMap.get(sum), label, verbose)) {
                ++numWrong;
            }
        }
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        if (verbose > 0) {
            System.out.format("%s: tested %s.minCoins in %d milliseconds, got %d wrong answers: %s\n\n", label, testInstance
                    .getClass().getName(), elapsedTime, numWrong, (numWrong == 0 ? "PASS" : "FAIL"));
        }
        return numWrong;
    }
    
    public static boolean test_minCoins(MinimalCoins testInstance, int sum, int expectedMinCoins,
            String label, int verbose)
    {
        if (verbose > 1) {
            System.out.format("%s: minCoins(%2d) = ", label, sum);
        }
        int numCoins = testInstance.minCoins(sum);
        boolean pass = (numCoins == expectedMinCoins);
        if (verbose > 1) {
            System.out.format("%2d %s %2d \t %s\n", numCoins, (pass ? "==" : "!="),
                    expectedMinCoins, (pass ? "PASS" : "FAIL"));
        }
        return pass;
    }
    
    public static int test_minCoinClasses(int coins[], TreeMap<Integer, Integer> testMap, 
            String label, int verbose)
    {
        int status = 0;
        
        // Don't count numWrong > 0 from the greedy algorithm as test failure; it's expected.
        test_minCoinsMap(new GreedyMinimalCoins(coins), testMap, label, verbose);
        
        // Do track numWrong from recursive algorithms.
        status += test_minCoinsMap(new DepthLimitedRecursiveMinimalCoins(coins), testMap, label, verbose);
        status += test_minCoinsMap(new RecursiveMinimalCoins(coins), testMap, label, verbose);
        
        // Also expect the dynamic algorithm to be correct.
        int maxTestSum = testMap.lastKey();
        MinimalCoins dynamicMinCoins = new CachedDynamicMinimalCoins(coins, maxTestSum);
        status += test_minCoinsMap(dynamicMinCoins, testMap, label, verbose);
        
        System.out.format("Expecting IllegalArgumentException...\n\t");
        try {
            test_minCoins(dynamicMinCoins, maxTestSum + 1,
                    -1, "Dynamic primes with sum > max allowable:", 2);
        } catch (Exception ex) {
            System.out.format("\n...got [" + ex + "]\n\n");
            if (ex.getClass() != IllegalArgumentException.class) {
                status++;
            }
        }
        return status;
    }
    
    public static int test_recursiveMinCoinClasses(int coins[], TreeMap<Integer, Integer> testMap, 
            String label, int verbose)
    {
        int status = 0;
        
        // Do track numWrong from recursive algorithms.
        status += test_minCoinsMap(new DepthLimitedRecursiveMinimalCoins(coins), testMap, label, verbose);
        status += test_minCoinsMap(new RecursiveMinimalCoins(coins), testMap, label, verbose);
        
        return status;
    }
    
    public static int unit_test(int level)
    {
        String testName = MinimalCoinsTest.class.getName() + ".unit_test";
        System.out.format("BEGIN %s\n\n", testName);
        
        int status = 0, verbose = 2;

        status += test_minCoinClasses(US_TO_1DOLLAR, US_TEST_MAP, "US Coins", verbose);
        status += test_minCoinClasses(ODD_PRIMES_13, PRIME_TEST_MAP, "Odd Primes", verbose);
        status += test_minCoinClasses(MCNUGGET_SIZES, NUGGET_TEST_MAP, "McNuggets", verbose);

        if (level > 1) {
            status += test_recursiveMinCoinClasses(US_TO_1DOLLAR, US_SLOW_MAP, "US Coins Recurse", 2);
        }
        
        System.out.format("\nEND %s: %s\n", testName, (status == 0 ? "PASS" : "FAIL"));
        return status;
    }
    
    @Test
    public void junit_test() { 
        unit_test(1); 
    }
    
    public static void main(String[] args)
    {
        unit_test(2);
    }
}
