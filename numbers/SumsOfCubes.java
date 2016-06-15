package sprax.numbers;

/**
 * SumsOfCubes, or, numbers that are sums of multiple pairs of cubes.
 * @author sprax 2016.06.14
 */

import java.util.Collections;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import sprax.sprout.Sx;
import sprax.test.Sz;


/** 
 * Let "t" be a good number if "t" can be written as sum of 2 cubes in at least 2 distinct ways.
 * Given n, write a method which prints all good numbers up to and including n.
 * @author Sprax
 */
public class SumsOfCubes
{
    /**
     * Algorithm: compute all sums of cubes in some (non-repeating) order
     * up to or somewhat past maxNum, storing these sums as we go,
     * and add any previously stored sums to the result list.
     * @param maxNum
     * @return List of Longs x s.t. x == a**3 + b**3 == c**3 + d**3 for distinct
     * natural numbers a, b, c, d.   
     */
    public static SortedSet<Long> sumsOfCubesNumbers(long maxNum)
    {
        SortedSet<Long> result = new TreeSet<>();       // empty list
        if (maxNum < 10)
            return result;
        
        // compute a bound on natural numbers to try, 
        long bound = approxCubeRoot(maxNum); 
        HashSet<Long> sumOfCubes = new HashSet<>();
        for (long j = 1; j < bound; j++) {
            long jcubed = j*j*j;
            for (long k = j + 1; k < bound; k++) {
                long kcubed = k*k*k;
                long sum = jcubed + kcubed;
                if (sum > maxNum)
                    break;
                if (sumOfCubes.contains(sum)) {
                    result.add(sum);
                } else {
                    sumOfCubes.add(sum);
                }
            }
        }        
        return result;
    }
    
    /**
     * @param num
     * @return approximate cube root from log and exp
     */
    static long approxCubeRoot(long num)
    {
        double lnNum = Math.log(num);
        double droot = Math.exp(lnNum/3.0);
        return Math.round(droot);
    }
    
    static int test_approxCubeRoot()
    {
        int numWrong = 0;
        numWrong = Sz.oneIfFalse(2 == approxCubeRoot(8));
        numWrong = Sz.oneIfFalse(3 == approxCubeRoot(27));
        numWrong = Sz.oneIfFalse(4 == approxCubeRoot(64));
        numWrong = Sz.oneIfFalse(5 == approxCubeRoot(125));
        numWrong = Sz.oneIfFalse(6 == approxCubeRoot(216));
        numWrong = Sz.oneIfFalse(7 == approxCubeRoot(343));
        numWrong = Sz.oneIfFalse(8 == approxCubeRoot(512));
        numWrong = Sz.oneIfFalse(9 == approxCubeRoot(729));
        for (long k = 10; k < 100; k++)
            numWrong += Sz.oneIfFalse(k == approxCubeRoot(k*k*k));
        return numWrong;
    }
    
    public static int unit_test(int lvl)
    {   
        String testName = SumsOfCubes.class.getName() + ".unit_test";  
        Sz.begin(testName);
        int numWrong = 0;
        
        numWrong += test_approxCubeRoot();
        
        long maxNum = Integer.MAX_VALUE;
        SortedSet<Long> cubesSums = sumsOfCubesNumbers(maxNum);
        int numFound = cubesSums.size();
        Sx.format("The %d natural numbers that are sums of cubes in at least two different ways, up to %d:\n"
                , numFound, maxNum);
        Sx.putsIterable(cubesSums, 10, cubesSums.size());
        Sx.format("Found %d numbers <= %d that are sums of cubes in at least two different ways.\n"
                , numFound, maxNum);
        
        long max = Collections.max(cubesSums);
        numWrong += Sz.oneIfFalse(max <= maxNum);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}
