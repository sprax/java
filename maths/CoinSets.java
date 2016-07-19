/*
 * Sprax Lines  2016.05.04
 */

package sprax.maths;

import java.util.Arrays;
import java.util.LinkedHashMap;

import sprax.sprout.Sx;

/**
 * Number of ways to make a sum with specified coin denominations.
 */
public abstract class CoinSets
{
    protected final int denominations[];
    
    /**
     * Base constructor: sorts the denominations low to high (default system sort)
     */
    protected CoinSets(int denominations[])
    {
        if (denominations == null || denominations.length == 0) {
            throw new IllegalArgumentException("denominations");
        }
        this.denominations = denominations;
        Arrays.sort(this.denominations);
        if (denominations[0] < 1) {
            throw new IllegalArgumentException("denominations must be positive");
        }
    }
    
    /**
     * Number of different ways for coins of the given denominations to make a sum.
     * Order of summation does not matter.
     * 
     * @param sum (non-negative number)
     * @return The number of ways to make the sum.
     */
    public abstract int numSums(int sum);
}



/**
 * Naive: exponential runtime
 */
class RecursiveLoopCoinSets extends CoinSets
{
    public RecursiveLoopCoinSets(int denominations[])
    {
        super(denominations);
    }

    @Override
    public int numSums(int sum)
    {
        // Special case.  
        // Base constructor ensures that denominations is not empty.
        if (sum < denominations[0]) {
            return 0;
        }
        return numSetsRecLoop(sum, denominations.length);
    }
    
    private int numSetsRecLoop(int sum, int subLength)
    {
        int numSets = 0;
        for (int j = subLength; --j >= 0;) {
            int denom = denominations[j];
            int remainder = sum - denom;
            if (remainder > 0) {
                numSets += numSetsRecLoop(remainder, j + 1);
            } else if (remainder == 0) {
                return 1 + numSetsRecLoop(denom, j );
            }
        }
        return numSets;
    }
}

/**
 * Naive: exponential runtime
 */
class RecursiveSubCoinSets extends CoinSets
{
    public RecursiveSubCoinSets(int denominations[])
    {
        super(denominations);
    }

    @Override
    public int numSums(int sum)
    {
        // Special case.  
        // Base constructor ensures that denominations is not empty.
        if (sum < denominations[0]) {
            return 0;
        }
        return numSetsRec(sum, denominations.length);
    }
    
    
    // Returns the count of ways we can sum S[0...m-1] coins to get sum
    private int numSetsRec(int sum, int subLength)
    {
        // If n is 0 then there is 1 solution (do not include any coin)
        if (sum == 0)
            return 1;
        
        // If n is less than 0 then no solution exists
        if (sum < 0)
            return 0;
        
        // If there are no coins and n is greater than 0, then no solution exists
        if (subLength <= 0 && sum >= 1)
            return 0;
        
        // count is sum of solutions (i) including S[m-1] (ii) excluding S[m-1]
        return numSetsRec(sum, subLength - 1) + numSetsRec(sum - denominations[subLength - 1], subLength);
    }
}

/**
 * Dynamic programming O(MN) where M = number of denominations, N = sum
 */
class DynamicCoinSets extends CoinSets
{
    public DynamicCoinSets(int denominations[])
    {
        super(denominations);
    }

    @Override
    public int numSums(int sum)
    {
        // Special case.
        // Base constructor ensures that denominations is not empty.
        if (sum < denominations[0]) {
            return 0;
        }
        
        // table[i] will be storing the number of solutions for
        // value i. We need n+1 rows as the table is constructed
        // in bottom up manner using the base case (n = 0)
        int table[] = new int[sum + 1];   // default initialization
        
        // Base case (If given value is 0)
        table[0] = 1;
        
        // Pick all coins one by one and update the table[] values
        // after the index greater than or equal to the value of the
        // picked coin
        for (int i = 0; i < denominations.length; i++)
            for (int j = denominations[i]; j <= sum; j++)
                table[j] += table[j - denominations[i]];
        
        return table[sum];
    }
}

/**
 * Dynamic programming O(MN) where M = number of denominations, N = sum
 *
 * Uses lazily cached results of a dynamic programming algorithm. Run time:
 * O(MN) where M = sum, N = denominations (number of different coin values).
 * Memory: O(M) where M = max sum allowed. For US coins, M = 100 makes sense.
 */
class CachedDynamicCoinSets extends CoinSets
{
    public final static int MIN_CACHE_SIZE = 100;
    private int cacheSize, cache[];
    private int maxCachedSum   = 0; // largest sum handled so far
    
    public CachedDynamicCoinSets(int denominations[])
    {
        super(denominations);
        makeCache(MIN_CACHE_SIZE);
    }
    
    public CachedDynamicCoinSets(int denominations[], int maxSumToCache)
    {
        super(denominations);
        makeCache(maxSumToCache);
    }
    
    private void makeCache(int maxSumToCache)
    {
        if (maxSumToCache < MIN_CACHE_SIZE)
            maxSumToCache = MIN_CACHE_SIZE;
        cacheSize = maxSumToCache + 1;
        cache = new int[cacheSize];
        
        // Base case
        cache[0] = 1;
    }
    
    @Override
    public int numSums(int sum)
    {
        // Special case.
        // Base constructor ensures that denominations is not empty.
        if (sum < denominations[0]) {
            return 0;
        }
        
        // Pick all coins one by one and update the table[] values
        // after the index greater than or equal to the value of the
        // picked coin
        if (sum > maxCachedSum) {
            for (int i = 0; i < denominations.length; i++) {
                for (int j = denominations[i]; j <= sum; j++) {
                    if (j > maxCachedSum) {
                        cache[j] += cache[j - denominations[i]];
                    }
                }
            }
            maxCachedSum = sum;
        }
        return cache[sum];
    }

  
    //////// UNIT TESTING ////////
    
    public static int US_COINS[] = { 1, 5, 10, 25, 50, 100 };
    public static LinkedHashMap<Integer, Integer> US_TEST_MAP;
    static {
        US_TEST_MAP = new LinkedHashMap<>();
        US_TEST_MAP.put(1, 1);
        US_TEST_MAP.put(3, 1);
        US_TEST_MAP.put(5, 2);
        US_TEST_MAP.put(6, 2);
        US_TEST_MAP.put(7, 2);
        US_TEST_MAP.put(10, 4);
        US_TEST_MAP.put(11, 4);
        US_TEST_MAP.put(15, 6); // <15,0,0> <10,1,0> <5,2,0> <5,0,1> <0,3,0> <0,1,1>
        US_TEST_MAP.put(17, 6);
        US_TEST_MAP.put(20, 9); // <20,0,0> <15,1,0> <10,2,0> <10,0,1> <5,3,0> <5,1,1> 
                                // <0,4,0>  <0,2,1>  <0,0,2>
        US_TEST_MAP.put(24, 9);
        US_TEST_MAP.put(25, 13);
        /**********************************************
        US_TEST_MAP.put(37, 24);
        US_TEST_MAP.put(51, 50);
        US_TEST_MAP.put(57, 62);
        US_TEST_MAP.put(63, 77);
        US_TEST_MAP.put(74, 112);
        US_TEST_MAP.put(87, 187);
        US_TEST_MAP.put(100, 293);
        US_TEST_MAP.put(200, 2728);
        US_TEST_MAP.put(300, 12318);
        US_TEST_MAP.put(400, 38835);
        US_TEST_MAP.put(500, 98411);
        ***********************************************/
    }
    int UK_COINS[] = { 1, 2, 5, 10, 20, 50, 100, 200 };
    
   public static int test_coinSetsClass(CoinSets coinSets)
   {
       int numWrong = 0;
       
       for(int sum : US_TEST_MAP.keySet()) {
           int expected = US_TEST_MAP.get(sum);
           int computed = coinSets.numSums(sum);
           if (computed != expected) {
               ++numWrong;
           }
           Sx.format("%s.numSums(%d) = %d, %d expected\n"
                   , coinSets.getClass().getSimpleName(), sum, computed, expected);
       }
       return numWrong;
   }
   
  public static int unit_test() {
      String testName = CoinSets.class.getName() + ".unit_test";
      Sx.format("BEGIN %s\n", testName);
      int numWrong = 0;
      
      CoinSets 
      coinSets = new RecursiveLoopCoinSets(US_COINS);
      numWrong += test_coinSetsClass(coinSets);
      
      coinSets = new RecursiveSubCoinSets(US_COINS);
      numWrong += test_coinSetsClass(coinSets);
      
      coinSets = new DynamicCoinSets(US_COINS);
      numWrong += test_coinSetsClass(coinSets);
      
      coinSets = new CachedDynamicCoinSets(US_COINS, US_COINS[US_COINS.length - 1]);
      numWrong += test_coinSetsClass(coinSets);
      
      Sx.format("\nEND %s:  %d wrong, %s\n", testName, numWrong, (numWrong == 0 ? "PASS" : "FAIL"));
      return numWrong;
  }
  
    public static void main(String[] args)
    {
        unit_test();
    }
}
