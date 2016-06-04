package sprax.converts;

import sprax.Sx;
import sprax.Sz;

public class HexStringToNum 
{
	/**
	 * Java defines its char set with 7 chars between '9' and 'A',
	 * so just map those to zero, and we can use array look-up, no hashing.
	 * The size of this table is 23 ints.
	 */
	static final int[] sHexCharToDecVal = { 
		 0,  1,  2,  3,  4,  5,  6,  7,  8,  9,
		    -1, -2, -3, -4, -5, -6, -7,
		10, 11, 12, 13, 14, 15,
	};

	/**
	 * Somewhat forgiving implementation of htoi: return 0 as result from null string, 
	 * non-hex chars and any other garbage in. 
	 * @param hexStr
	 * @return
	 */
	public static int htoi(final String hexStr)
	{
		if (hexStr == null || hexStr.length() == 0)
			return 0;

		int retVal = 0;		// return value
		int decVal = 0;
		int strPos = 0;
		boolean isNegative = false;
		if (hexStr.charAt(0) == '-')
		{
			isNegative = true;
			strPos = 1;
		}
		for ( ; strPos < hexStr.length(); strPos++)
		{
			int idx = hexStr.charAt(strPos) - '0';
			try {
				decVal = sHexCharToDecVal[idx];
			} 
			catch (ArrayIndexOutOfBoundsException ex) {
				Sx.format("Error: bad char(%c) at position %d of string: %s\n", hexStr.charAt(strPos), strPos, hexStr);
				return 0;
			}
			if (decVal < 0)
			{
				Sx.format("Error: bad char(%c) at position %d of string: %s\n", hexStr.charAt(strPos), strPos, hexStr);
				return 0;
			}
			retVal = (retVal << 4) + decVal;
		}
		if (isNegative)
			return -retVal;
		else
			return  retVal;
	}
	
	private static void test_htoi(String hexStr)
	{
        Sx.format("htoi(%s) \t%d\n", hexStr, htoi(hexStr));		
	}
	
    public static int unit_test()
    {
        String testName = HexStringToNum.class.getName() + ".unit_test";
        Sz.begin(testName);

        Sx.puts("1 << 4 is " + (1 << 4));
        
        Sx.format("A - 0 is: %d\n",   (int)('A' - '0'));
        for (int j = '0'; j <= 'F'; j++)
        	Sx.format("  %c", (char)(j));
        Sx.puts();	

        test_htoi("0");
        test_htoi("-0");
        test_htoi("0-");
        test_htoi("1");
        test_htoi("-1");
        test_htoi("A");
        test_htoi("11");
        test_htoi("1B");
        test_htoi("B1");
        test_htoi("FF");
        test_htoi("-");
        test_htoi("-FF");
        test_htoi("FG");
        test_htoi("F@F");
        
        test_htoi("FEDCBA");
        test_htoi("FEDCBA9876543210");
        test_htoi("FEDCBA9876543210123456789ABCDEF");
         
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args)  { unit_test(); }
}
