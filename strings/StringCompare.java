package sprax.strings;

import sprax.test.Sz;

public class StringCompare {
	
	public static StringBuffer commonCharacters(StringBuffer sa, StringBuffer sb) {
		// Two way comparison.  Output the common characters in the order of sa.
		final int alphabetSize = 256;
		int alphabet[] = new int[alphabetSize];
		for (int j = 0; j < sb.length(); j++) {
			int index = sb.charAt(j);
			if (alphabet[index] == 0) {
				alphabet[index] =  1;
			}
		}
		StringBuffer out = new StringBuffer();
		for (int j = 0; j < sa.length(); j++) {
			int index = sa.charAt(j);
			if (alphabet[index] == 1) {
				out.append((char)index);
			}
		}
		return out;
	}
	
	public static StringBuffer commonCharacters(StringBuffer sa, StringBuffer sb, StringBuffer sc) {
		// Three way comparison.  Output the common characters in the order of sa.
		final int alphabetSize = 256;
		int alphabet[] = new int[alphabetSize];
		for (int j = 0; j < sb.length(); j++) {
			int index = sb.charAt(j);
			if (alphabet[index] == 0) {
				alphabet[index] =  1;
			}
		}
		for (int j = 0; j < sc.length(); j++) {
			int index = sc.charAt(j);
			if (alphabet[index] == 1) {
				alphabet[index] =  2;
			}
		}
		StringBuffer out = new StringBuffer();
		for (int j = 0; j < sa.length(); j++) {
			int index = sa.charAt(j);
			if (alphabet[index] == 2) {
				out.append((char)index);
			}
		}
		return out;
	}	
	
	public static StringBuffer commonCharacters(StringBuffer sa, StringBuffer so[], int numStrs) {
		// Three way comparison.  Output the common characters in the order of sa.
		final int alphabetSize = 256;
		int alphabet[] = new int[alphabetSize];
		for (int n = 0; n < numStrs; n++) {
			for (int j = 0; j < so[n].length(); j++) {
				int index = so[n].charAt(j);
				if (alphabet[index] == n) {
					alphabet[index] =  n + 1;
				}
			}
		}
		StringBuffer out = new StringBuffer();
		for (int j = 0; j < sa.length(); j++) {
			int index = sa.charAt(j);
			if (alphabet[index] == numStrs) {
				out.append((char)index);
			}
		}
		return out;
	}	
    
    public static int unit_test()
    {
        String testName = StringCompare.class.getName();
        Sz.begin(testName);
        int numWrong = 0;
        
        StringBuffer sa = new StringBuffer("A walrus in Belarus?  My stars!");
        StringBuffer sb = new StringBuffer("Isn't that preposterous?  Blech!");
        StringBuffer sc = new StringBuffer("The Bosporus is more prosperous!");
        StringBuffer ss = commonCharacters(sa, sb);
        System.out.println("common characters(" + sa + ", " + sb + ") == (" + ss + ")");
        StringBuffer st = commonCharacters(sa, sb, sc);
        System.out.println("common characters(" + sa + ", " + sb + ", " + sc + ") == (" + st + ")");
        StringBuffer bufs[] = { sb, sc };
        StringBuffer su = commonCharacters(sa, bufs, 2);
        System.out.println("common characters(" + sa + ", " + sb + ", " + sc + ") == (" + su + ")");
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }

}
