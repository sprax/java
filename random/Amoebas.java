package sprax.random;

import java.util.Random;

import sprax.test.Sz;
import sprax.arrays.ArrayAlgo;
import sprax.sprout.Sx;

public class Amoebas 
{
    long population;
    long generation;
    double splitProb;
    Random rng;

    Amoebas() 
    { this(1); }
    
    Amoebas(long startNum) 
    { this(startNum, 0.5); }

    Amoebas(long startNum, double splitProb) 
    { this(startNum, splitProb, System.currentTimeMillis()); }
    
    Amoebas(long startNum, double splitProb, long seed) 
    { this(startNum, splitProb, new Random(seed)); }
    
    Amoebas(long startNum, double splitProb, Random rng) 
    { 
        this.population = startNum;
        this.splitProb = splitProb;
        this.rng = rng;
    }
     
    public boolean generate()
    {
        generation++;
        for (long j = 0, oldPop = population; j < oldPop; j++)
        {
            double next = rng.nextDouble();
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
        
        Amoebas amoebas = new Amoebas(1, 0.615);
        while (amoebas.population > 0) {
            amoebas.generate();
            Sx.format("%d :  %d\n",  amoebas.generation, amoebas.population);
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args)
    {
        unit_test();        
    }	
    
}
