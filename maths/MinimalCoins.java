/*
 * Sprax Lines  2016.05.04
 */

package sprax.maths;

import java.util.Arrays;

/**
 * Minimal numbers of coins to make a sum, making change with odd denominations, dynamic programming.
 */
public abstract class MinimalCoins
{
    protected int denominations[];
    
    /**
     * Base constructor: sorts the denominations low to high (default system sort)
     */
    protected MinimalCoins(int denominations[])
    {
        if (denominations == null || denominations.length == 0) {
            throw new IllegalArgumentException("denominations");
        }
        this.denominations = denominations;
        Arrays.sort(this.denominations);
    }
    
    /**
     * Make change with the minimum number of coins. Given a non-negative number
     * and an array of positive denominations (coin values) sorted from smallest
     * to largest, return the smallest non-negative number of coins that add up
     * to the specified sum, or -1 if no such number exists.
     * 
     * @param sum (non-negative number)
     * @return The minimal number of coins comprising the sum, or -1 if no such
     *         number exists.
     */
    public abstract int minCoins(int sum);
    
    
    public static void main(String[] args)
    {
        MinimalCoinsTest.unit_test(1);
    }
}

/**
 * Count as many of the biggest coins as fit, then the next biggest on the
 * remainder, until the remainder is either 0 or indivisible.
 */
class GreedyMinimalCoins extends MinimalCoins
{
    public GreedyMinimalCoins(int denominations[])
    {
        super(denominations);
    }
    
    @Override
    public int minCoins(int sum)
    {
        if (sum < 0) {
            throw new IllegalArgumentException("sum < 0");
        }
        int minCoins = 0, remainder = sum;
        for (int j = denominations.length; --j >= 0;) {
            int denom = denominations[j];
            if (denom <= remainder) {
                int quotient = remainder / denom;
                remainder -= quotient * denom;
                minCoins += quotient;
            }
        }
        if (remainder != 0) {
            return -1;
        }
        return minCoins;
    }
}

/**
 * Naive recursion: exponential runtime
 */
class RecursiveMinimalCoins extends MinimalCoins
{
    public RecursiveMinimalCoins(int denominations[])
    {
        super(denominations);
    }
    
    @Override
    public int minCoins(int sum)
    {
        if (sum < 0) {
            throw new IllegalArgumentException("sum < 0");
        }
        if (sum == 0) {
            return 0;
        }
        int minCoins = minCoinsRec(sum);
        if (minCoins == Integer.MAX_VALUE) {
            return -1;
        }
        return minCoins;
    }
    
    protected int minCoinsRec(int sum)
    {
        int minCoins = Integer.MAX_VALUE;
        for (int j = denominations.length; --j >= 0;) {
            int denom = denominations[j];
            int remainder = sum - denom;
            if (remainder > 0) {
                int numCoins = minCoinsRec(remainder);
                if (numCoins == Integer.MAX_VALUE) {        // dead end
                    continue;
                }
                ++numCoins;                                 // increment by 1 coin
                if (minCoins > numCoins) {
                    minCoins = numCoins;
                }
            } else if (remainder == 0) {
                // No need to test smaller denominations, so break out.
                minCoins = 1;
                break;
            }
        }
        return minCoins;
    }
}


/**
 * Recursion with cut-off
 */
class DepthLimitedRecursiveMinimalCoins extends RecursiveMinimalCoins
{
    public DepthLimitedRecursiveMinimalCoins(int denominations[])
    {
        super(denominations);
    }
    
    @Override
    protected int minCoinsRec(int sum) 
    {
        return minCoinsRec(sum, 0, Integer.MAX_VALUE / 2);
    }
    
    protected int minCoinsRec(int sum, int depth, int minSoFar)
    {
        if (depth + 5 >= minSoFar) {
            return Integer.MAX_VALUE;           // dead end
        }
        int minCoins = Integer.MAX_VALUE;
        for (int j = denominations.length; --j >= 0;) {
            int denom = denominations[j];
            int remainder = sum - denom;
            if (remainder > 0) {
                int numCoins = minCoinsRec(remainder, depth + 1, minCoins);
                if (numCoins == Integer.MAX_VALUE) {
                    continue;
                }
                ++numCoins;
                if (minCoins > numCoins) {
                    minCoins = numCoins;
                }
            } else if (remainder == 0) {
                // No need to test smaller denominations, so break out.
                minCoins = 1;
                break;
            }
        }
        return minCoins;
    }
}

/**
 * Uses lazily cached results of a dynamic programming algorithm. Run time:
 * O(MN) where M = sum, N = denominations (num different coin values) Memory:
 * O(M) where M = max sum allowed. For US coins, M = 100 makes sense.
 */
class CachedDynamicMinimalCoins extends MinimalCoins
{
    public final static int MIN_CACHE_SIZE = 32;
    private final int       maxAllowedSum;      // limited by cache size
    private int             maxCachedSum   = 0; // largest sum handled so far
    private int             minCoinCache[];     // primitive cache, never resized
                                                 
    public CachedDynamicMinimalCoins(int denominations[], int maxAllowedSum)
    {
        super(denominations);
        this.maxAllowedSum = maxAllowedSum > MIN_CACHE_SIZE 
                           ? maxAllowedSum : MIN_CACHE_SIZE;
        minCoinCache = new int[maxAllowedSum + 1];
        
        // Initialize the cache: 0 coins for sum 0, and MAX_VALUE (effectively
        // infinity) for all greater sums.
        minCoinCache[0] = 0;
        for (int j = 1; j <= maxAllowedSum; j++) {
            minCoinCache[j] = Integer.MAX_VALUE;
        }
    }

    private void lazyCacheSum(int newMaxSum)
    {
        // Cache the minimal number of coins for values up to sum,
        // if not already cached.
        assert (newMaxSum > maxCachedSum);
        for (int sum = maxCachedSum + 1; sum <= newMaxSum; sum++) {
            for (int j = denominations.length; --j >= 0;) {
                int denom = denominations[j];
                if (denom <= sum) {
                    int numCoins = minCoinCache[sum - denom];
                    if (numCoins == Integer.MAX_VALUE) {
                        continue;
                    }
                    ++numCoins;
                    if (minCoinCache[sum] > numCoins) {
                        minCoinCache[sum] = numCoins;
                    }
                }
            }
        }
        maxCachedSum = newMaxSum;
    }
    
    @Override
    public int minCoins(int sum)
    {
        if (sum < 0) {
            throw new IllegalArgumentException("sum < 0");
        }
        if (sum > maxAllowedSum) {
            throw new IllegalArgumentException("sum > max allowed sum (" + sum + " > " + maxAllowedSum
                    + ")");
        }
        if (sum > maxCachedSum) {
            lazyCacheSum(sum);
        }
        int minCoins = minCoinCache[sum];
        if (minCoins == Integer.MAX_VALUE) {
            return -1;
        }
        return minCoins;
    }

}
