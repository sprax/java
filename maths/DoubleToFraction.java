package sprax.maths;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

import sprax.sprout.Sx;

public class DoubleToFraction
{
    static int sDbg = 2;
    static final BigDecimal maxLong = new BigDecimal(Long.MAX_VALUE);
    static final int maxLongScale = maxLong.scale();
    static final int maxIntOver10 = Integer.MAX_VALUE/10;
    static String ConvertToRationalString(double n)
    {
        if (n == 0)
        {
            return "0";
        }
        int denominator = 1;    
        while ((n - (int)n) != 0)
        {
            n = n * 10;
            denominator *= 10;
            if (denominator > maxIntOver10)
                break;
        }
        // Use Euclidean theorm to find gcd: en.wikipedia.org/wiki/Euclidean_algorithm
        int numerator = (int)n;
        if (denominator % numerator == 0)
        {
            return String.format("1/%d", denominator / numerator);
        }
        int i = numerator;
        int j = denominator;
        int diff = Math.abs(i-j);
        while ((i % diff != 0) || (j % diff != 0))
        {
            if (i > j)
            {
                i = diff;
            }
            else
            {
                j = diff;
            }        
            diff = Math.abs(i-j);
        }
        return String.format("%d/%d", numerator/diff, denominator/diff);
    }
    
    
    
    public static int unit_test()
    {
        
        
        double dd, enopi = Math.E / Math.PI;
        for (int j = 1; j < 10; j++) {
            dd = enopi / j;
            BigInteger bigFrac[] = toBigIntegerFraction(dd);
            //long       intFrac[] = toLongFraction(dd);
            double numer = bigFrac[0].doubleValue() * Math.PI;
            double denom = bigFrac[1].doubleValue() * Math.E;
            Sx.format("x = %g -> (%d / %d) -> (%g / %g) recip %g =?= f/x = %g\n"
                    , dd, bigFrac[0], bigFrac[1], numer, denom, denom/numer, enopi/dd);   
            //Sx.format("x = %g -> (%d / %d), and f/x = %g\n", dd, intFrac[0], intFrac[1], enopi/dd);   
        }
        
        dd = 0.12345;
        Sx.puts("Convert " + dd + ": " + ConvertToRationalString(dd));
        dd = enopi;
        Sx.puts("Convert " + dd + ": " + ConvertToRationalString(dd));
        return 0;
    }
    
    public static long[] toLongFraction(double number)
    {
        BigInteger bigIntFrac[] = toBigIntegerFraction(number);
        long           intFrac[] = 
            { bigIntFrac[0].longValue(), bigIntFrac[1].longValue() };   
        return intFrac;
    }
    
    //  public static long[] toLongFractionNoBig(double number)
    //  {
    //    double intPart = Math.floor(number);
    //    double decPart = Math.IEEEremainder(number, 1);
    //    Integer.g
    //    double dd = Long.MAX_VALUE / number;
    //    long           intFrac[] = 
    //    { bigIntFrac[0].longValue(), bigIntFrac[1].longValue() };   
    //    return intFrac;
    //  }
    
    public static BigInteger[] toBigIntegerFraction(double number)
    {
        BigDecimal bigDecFrac[] = toBigDecimalFraction(number);
        BigInteger bigIntFrac[] = { bigDecFrac[0].toBigIntegerExact()
                , bigDecFrac[1].toBigIntegerExact() };   
        return bigIntFrac;
    }
    
    
    public static BigDecimal[] toBigDecimalFraction(double number)
    {
        BigDecimal numerator = new BigDecimal(number);
        Sx.debug(sDbg, "num     " +  numerator.toString());
        int scale = numerator.scale();
        Sx.debug(sDbg, "scale   " +  scale);
        numerator = numerator.movePointRight(scale);
        Sx.debug(sDbg, "num     " +  numerator.toString());
        
        BigDecimal denominator = new BigDecimal(10).pow(scale);
        Sx.debug(sDbg, "denom   " +  denominator.toString());
        
        BigDecimal gcd = gcd(numerator, denominator);
        Sx.debug(sDbg, "gcd     " +  gcd.toString());
        
        numerator = numerator.divide(gcd);
        Sx.debug(sDbg);
        
        denominator = denominator.divide(gcd);
        // System.out.println(numerator);
        // System.out.println(createFractionLine(numerator, denominator));
        // System.out.println(denominator);
        // System.out.println();    BigDecimal fraction[] = { numerator, denominator };
        BigDecimal fraction[] = { numerator, denominator };
        return fraction;
    }
    
    private static BigDecimal gcd(BigDecimal num1, BigDecimal num2)
    {
        return (num2.equals(BigDecimal.ZERO)) ? num1 : gcd(num2, num1.remainder(num2));
    }
    
    protected static String createFractionLine(BigDecimal numerator, BigDecimal denominator)
    {
        char[] chars = new char[Math.max(numerator.precision(), denominator.precision())];
        Arrays.fill(chars, '—');
        return new String(chars);
    }
    
    
    public static int unit_test(int level) 
    {
        Sx.puts(DoubleToFraction.class.getName() + ".unit_test");  
        int stat = 0;
        
        // TODO
        
        return stat;
    }
    
    public static void main(String[] args)
    {
        unit_test(2);
    }
}



