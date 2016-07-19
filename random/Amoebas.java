package sprax.random;

import java.util.Random;

import sprax.test.Sz;
import sprax.arrays.Arrays1d;
import sprax.sprout.Sx;

/**
 * Example from Wikipedia article on Martingales:
 * https://en.wikipedia.org/wiki/Martingale_(probability_theory)
 * 
 * Suppose each amoeba either splits into two amoebas, with probability p, 
 * or eventually dies, with probability 1 − p. 
 * Let Xn be the number of amoebas surviving in the nth generation 
 * (in particular Xn = 0 if the population has become extinct by that time). 
 * Let r be the probability of eventual extinction. (Finding r as a function 
 * of p is an instructive exercise. Hint: The probability that the descendants
 * of an amoeba eventually die out is equal to the probability that either of 
 * its immediate offspring dies out, given that the original amoeba has split.) 
 * Then {\displaystyle \{\,r^{X_{n}}:n=1,2,3,\dots \,\}} \{\,r^{X_{n}}:n=1,2,3,\dots \,\}
 * is a martingale with respect to { Xn: n = 1, 2, 3, ... }.
 * 
 * One can also ask, What's the probability of the population lasting N generations?
 * 
 * @author sprax    2016.07.08
 */
public class Amoebas 
{
    long population;
    long generation;
    double splitProb;
    Random random;

    public Amoebas() 
    { this(1); }
    
    public Amoebas(long startNum) 
    { this(startNum, 0.5); }

    public Amoebas(long startNum, double splitProb) 
    { this(startNum, splitProb, System.currentTimeMillis()); }
    
    public Amoebas(long startNum, double splitProb, long seed) 
    { this(startNum, splitProb, new Random(seed)); }
    
    public Amoebas(long startNum, double splitProb, Random rng) 
    { 
        this.population = startNum;
        this.splitProb = splitProb;
        this.random = rng;
    }
     
    public boolean generate()
    {
        generation++;
        for (long j = 0, oldPop = population; j < oldPop; j++)
        {
            double next = random.nextDouble();
            if (next <= splitProb)
                population += 2;
            else 
                population--;
        }
        return population > 0;
    }
    
    public static int unit_test() 
    {
        String testName = Amoebas.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        double goldenRatio = 1.618033988749894848204586834365638117720;

        Amoebas am = new Amoebas(1, 1.0/goldenRatio);
        
        long oldPop = am.population;
        long targetGeneration = 32;
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < targetGeneration && oldPop > 0; j++) {
            am.generate();
            long newPop = am.population;
            double ratio = (double) newPop / oldPop;
            long millis = System.currentTimeMillis() - begTime;
            Sx.format("%3d :  %12d     %f   %8d\n",  am.generation, am.population, ratio, millis);
            oldPop = newPop;
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();        
    }	
    
}
