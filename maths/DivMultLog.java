package sprax.maths;

import sprax.Sx;

public class DivMultLog {
	
	public static int divide(int number, int divisor) 
	{
		if (divisor == 0) {
			throw new ArithmeticException("divide by zero");
		}
		int answer = 0;
		boolean negative = false;
		if (number < 0) {
			number = -number;
			negative = ! negative;
		}
		if (divisor < 0) {
			divisor  = -divisor;
			negative = ! negative;
		}
		for (int sum = 0; sum < number; sum += divisor) {
			answer++;
		}
		if (negative) {
			return -answer;
		} else {
			return  answer;
		}
	}
	
	public static int multiply(int number, int factor)
	{
		int product = 0;
		boolean negative = false;
		if (number < 0) {
			number = -number;
			negative = ! negative;
		}
		if (factor < 0) {
			factor  = -factor;
			negative = ! negative;
		}
		for (int j = 0; j < factor; j++) {
			product += number;
		}
		if (negative) {
			return -product;
		} else {
			return  product;
		}
	}
	
	/** Lower bound (infimum) for log2 of number */
	public static int log2inf(int positiveInt)
	{
		if (positiveInt < 1)
			return -1;	// error condition
		int inf = 0;
		while ((positiveInt >>= 1) >= 1)
			inf++;
		return inf;
	}
	
	/** Lower bound (infimum) for log2 of number */
	public static int log2inf(double positiveDouble)
	{
		// check args: 
		if (positiveDouble < 1.0)
			return -1;
		if (positiveDouble < 1.0 + Integer.MAX_VALUE)
			return log2inf((int)positiveDouble);
		
		double pow2 = 1.0 + Integer.MAX_VALUE;	// e.g. 1.0 + (2^31 - 1)
		int    log2 = Integer.SIZE - 1;
	    while ((pow2 *= 2) < positiveDouble)
	    	log2++;
		
		return log2;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void unit_test()
	{
		int x = 33, y = 3;
		double z;
		System.out.println("multiply(" + x + ", " + y + ") = " + multiply(x,y) );
		System.out.println("  divide(" + x + ", " + y + ") = " +   divide(x,y) );
		for (x = -1; x < 10; x++)
		{
			System.out.println("  log2inf(" + x + ") = " +   log2inf(x) );			
		}
		x = Integer.MAX_VALUE;
		System.out.println("  log2inf(" + x + ") = " +   log2inf(x) );			
		z = x + 1.0;
		System.out.println("  log2inf(" + z + ") = " +   log2inf(z) );			
		
		Sx.puts("Integer.SIZE is " + Integer.SIZE);
		Sx.puts("MAX_VALUE + 1 is " + (1.0 + Integer.MAX_VALUE));		z = 1.23456789;
		for (x = 0; x < 30; x++)
		{
			System.out.println("  log2inf(" + z + ") = " +   log2inf(z) );
			z = z + z * Math.log(z);
		}

	}
	
	public static void main(String[] args)
	{
		unit_test();
	}
		
}
