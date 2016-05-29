package sprax.strings;

import java.util.ArrayList;

import sprax.Sx;

public class BoyerMooreKMP 
{
	protected final int       mCharSetSize;  // the radix
	protected char[]          mPatArr;       // store the pattern as a character array
	protected String          mPatStr;       // or as a string
	protected int[]           mRightmostArr;    // the bad-character skip array


	public BoyerMooreKMP(final String patStr, int charSetSize) 
	{
		mPatStr = patStr;
		mPatArr = patStr.toCharArray();
		mCharSetSize = charSetSize;
		initRightmost();
	}    

	protected void initRightmost()
	{
		mRightmostArr = new int[mCharSetSize];
		for (int c = 0; c < mCharSetSize; c++)
			mRightmostArr[c] = -1;
		for (int j = 0; j < mPatArr.length; j++)
			mRightmostArr[mPatArr[j]] = j;
	}


	protected void intitGoodSuffix(char normal[])
	{
		/****************
		char result[] = new char[mCharSetSize];
		char left[] = (char *)normal;
		char right[] = left + mCharSetSize;
		char reversed[mCharSetSize + 1];
		char *tmp = reversed + mCharSetSize;
		int i;

		// reverse string
		*tmp = 0;
		while (left < right)
			*(--tmp) = *(left++);

		int prefix_normal[mCharSetSize];
		int prefix_reversed[mCharSetSize];

		compute_prefix(normal, mCharSetSize, prefix_normal);
		compute_prefix(reversed, mCharSetSize, prefix_reversed);

		for (i = 0; i <= mCharSetSize; i++) {
			result[i] = mCharSetSize - prefix_normal[mCharSetSize - 1];
		}

		for (i = 0; i < mCharSetSize; i++) {
			int j = mCharSetSize - prefix_reversed[i];
			int k = i - prefix_reversed[i] + 1;

			if (result[j] > k)
				result[j] = k;
		}
		*****************/
	}

	public int searchBadCharGoodSuffixHeuristics(String txt, int start)
	{
		int M = mPatArr.length;
		int N = txt.length();
		int skip;
		for (int i = start; i < N - M; i += skip) {
			skip = 0;
			for (int j = M; --j >= 0; ) {
				if (mPatArr[j] != txt.charAt(i+j)) {
					skip = Math.max(1, j - mRightmostArr[txt.charAt(i+j)]);
					break;
				}
			}
			if (skip == 0) 
				return i;    // found
		}
		return -N;                       // not found
	}

	//////////////////////////////////////////////////////////////////////
	
	public int searchBadCharHeuristic(String txt, int start)
	{
		int M = mPatArr.length;
		int N = txt.length();
		return searchBadCharHeuristic(txt, start, N - M);
	}

	private int searchBadCharHeuristic(String txt, int start, int last)
	{
		int M = mPatArr.length;
		int skip;
		for (int i = start; i <= last; i += skip) {
			skip = 0;
			for (int j = M; --j >= 0; ) {
				if (mPatArr[j] != txt.charAt(i+j)) {
					int chrIdx = txt.charAt(i+j);
					if (chrIdx < mCharSetSize) {
						skip = j - mRightmostArr[chrIdx];
					}
					else
					{
						Sx.format("txt[%d] = %c\n", chrIdx, (char)chrIdx);
						skip = 1;
					}
					break;
				}
			}
			if (skip == 0) 
				return i;    // found
		}
		return -last;                       // not found
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int unit_test(int level)
	{
		String testName = BoyerMooreKMP.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN\n");

		//String txt = "Our house is almost at the edge of the Seam.";
		String txt = "Our house is almost at the edge of the Seam.  I only have to pass a few gates to reach the scruffy field called the Meadow.  Separating the Meadow from the woods, in fact enclosing all of District 12, is a high chain-link fence topped with barbed-wire loops.  In theory, it�s supposed to be electrified twenty-four hours a day as a deterrent to the predators that live in the woods�packs of wild dogs, lone cougars, bears�that used to threaten our streets.  But since we�re lucky to get two or three hours of electricity in the evenings, it�s usually safe to touch.  Even so, I always take a moment to listen carefully for the hum that means the fence is live.  Right now, it�s silent as a stone.  Concealed by a clump of bushes, I flatten out on my belly and slide under a two-foot stretch that�s been loose for years.  There are several other weak spots in the fence, but this one is so close to home I almost always enter the woods here.";
		String pat = "the";
		BoyerMooreKMP bmk = new BoyerMooreKMP(pat, 256);
		ArrayList<Integer> starts = new ArrayList<Integer>();
		int ss = -1;
		for (;;)
		{
			ss = bmk.searchBadCharHeuristic(txt, ss+1);
			if (ss < 0)
				break;
			starts.add(ss);
		}
		for (int ff : starts)
		{
			Sx.puts(ff);
		}
		Sx.puts(testName + " END,  status: PASSED");
		return 0;
	}

	public static void main(String args[]) { unit_test(1); }}
