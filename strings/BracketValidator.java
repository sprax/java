package sprax.strings;

import java.util.Stack;

import sprax.sprout.Sx;

public class BracketValidator {


	static boolean validateBrackets(final String ss)
	{
		Stack<Character> brackets = new Stack<Character>();  // track nested brackets

		for (int j = 0; j < ss.length(); j++)
		{
			char c = ss.charAt(j);
			if (c == ')')
			{
				if (brackets.peek() == '(')
					brackets.pop();
				else
					return false;
			}
			else if (c == ']')
			{
				if (brackets.peek() == '[')
					brackets.pop();
				else
					return false;
			}
			else if (c == '}')
			{
				if (brackets.peek() == '{')
					brackets.pop();
				else
					return false;
			}
			else if (c == '(' || c == '[' || c == '{')
				brackets.push(c);
		}

		if (brackets.isEmpty())  
			return true;
		else
			return false;
	}

	public static int unit_test(int level)
	{
		String testName = BracketValidator.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN\n");

		String testStrings[] = {
				"abc[def(ghi{jkl}mno)pqr]stu",	// OK
				"sgafsaf{safafsa[fsaf}fasfs]",	// overlap
				"{{abc}",						// extra opener
				"[abc((def)ghi))jkl]",			// extra closer
				"(abc][def)",					// opener & closer transposed (counting is not enough)
				"",								// empty is OK
				"abc",							// no brackets is OK
		};

		int validCount = 0;
		int errorCount = 0;
		for (String ts : testStrings)
		{
			if (validateBrackets(ts))
			{
				validCount++;
				Sx.puts("VALID: " + ts);
			}
			else
			{
				errorCount++;			
				Sx.puts("ERROR: " + ts);
			}
		}
		int stat = Math.abs(validCount - 3) + Math.abs(errorCount - 4);

		Sx.puts(testName + " END,  status: " + (stat == 0 ? "PASSED" : "FAILED"));
		return stat;
	}

	public static void main(String args[]) { unit_test(1); }}
