package sprax.models;

import sprax.sprout.Sx;

public class FizzBuzz {
    
    static void printA(int beg, int end)
    {
        Sx.puts("printA=====================================================");
        for (int j = beg; j <= end; j++)
            if (j % 3 == 0)
                if (j % 5 == 0)
                    Sx.puts("FizzBuzz");
                else
                    Sx.puts("Fizz");
            else if (j % 5 == 0)
                Sx.puts("Buzz");
            else
                Sx.puts(j);
    }
    
    static void printB(int beg, int end)
    {
        Sx.puts("printB=====================================================");
        for (int j = beg; j <= end; j++) {
            int mod3 = j % 3;
            int mod5 = j % 5;
            if (mod3 == 0)
                Sx.print("Fizz");
            if (mod5 == 0)
                Sx.print("Buzz");
            if (mod3 != 0 && mod5 != 0)
                Sx.print(j);
            Sx.puts();
        }
        
    }
    
    public static void main(String[] args)
    {
        Sx.puts(FizzBuzz.class.getName() + ".main");
        printA(1, 100);
        printB(1, 100);
        
    }
    
}
