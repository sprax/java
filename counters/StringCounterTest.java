package sprax.counters;

import java.util.ArrayList;
import java.util.List;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.sprout.Sx;

public class StringCounterTest 
{

	public static void test_StringCounter_Toy(StringCounter counter, int verbose)
	{
		Sx.puts(StringCounterTest.class.getName() + " testing: " + counter.getClass().getSimpleName());
		counter.add("dog", 2);
		counter.add("cat", 4);
		counter.add("eel", 1);
		counter.add("eel", 2);
		counter.add("eel", 1);
		counter.add("bug", 5);
		counter.add("fly", 4);
		counter.add("eel", 1);
		counter.add("dog", 3);
		counter.add("cow", 6);
		counter.add("eel", 1);
		counter.add("pig", 6);
		counter.add("cat", 4);
		counter.add("eel", 1);
		counter.add("nit", 1);
		getResults(counter, verbose);
		counter.add("cat", 4);
		counter.add("fly", 8);
		counter.add("ant", 16);
		counter.add("eel", 1);
		counter.add("bug", 6);
		counter.add("eel", 1);
		counter.add("eel", 1);
		getResults(counter, verbose);
	}
	

	public static long test_StringCounter_Txt( StringCounter counter
			, ArrayList<String> stringsA
			, ArrayList<String> stringsB
			, int verbose )
	{
		Sx.puts(StringCounterTest.class.getName() + " testing: " + counter.getClass().getSimpleName());
		
        long begTime = System.currentTimeMillis();
		for (String word : stringsA)
		{
			counter.add(word);
		}
		getResults(counter, verbose);
		
		for (String word : stringsB)
		{
			counter.add(word);
		}
		getResults(counter, verbose);
        long endTime = System.currentTimeMillis();
        return endTime - begTime;
	}

	
	protected static List<StringCount> getResults(StringCounter counter, int verbose)
	{
		List<StringCount> tops = counter.descendingMaximalStringCounts();
		showResults(counter, tops, verbose);
		return tops;
	}

	protected static void showResults(StringCounter counter, List<StringCount> tops, int verbose)
	{
		if (verbose > 0)
		{
			Sx.puts("StringCounter Type: " + counter.getClass().getSimpleName());
			if (verbose > 1)
			{
				Sx.puts("maximalCountSetSize: " + counter.maximalCountSetSize());
				Sx.puts("uniqueStringCount: " + counter.uniqueStringCount());
				Sx.puts("totalOfAllCounts: " + counter.totalOfAllCounts());		
			}	
			Sx.putsIterable("Descending max string counts:\n", tops);
			Sx.puts();
		}
	}

	/**
	 * Compare results from two StringCounter instances up to the smaller of their maximalCountSetSizes.
	 * @param counterA
	 * @param counterB
	 * @param verbose
	 * @return
	 */
	public static int compareCounters(StringCounter counterA, StringCounter counterB, int verbose)
	{	
		boolean isStringOOrderDIfferentForSameCounts = false;	// initially false
		if (verbose != 0)
		{
			Sx.format("Comparing %s and %s:\n\n"
					, counterA.getClass().getSimpleName()
					, counterB.getClass().getSimpleName()
			);
		}
		int uniqueStringsDif = counterA.uniqueStringCount() - counterB.uniqueStringCount();
		if (uniqueStringsDif != 0)
			return uniqueStringsDif;
		
		int totalCountsDif = counterA.totalOfAllCounts() - counterB.totalOfAllCounts();
		if (totalCountsDif != 0)
			return totalCountsDif;

		int minMaximalSetSize = Math.min(counterA.maximalCountSetSize(), counterB.maximalCountSetSize());
		List<StringCount> resultsA = getResults(counterA, verbose);
		List<StringCount> resultsB = getResults(counterB, verbose);
		for (int j = 0; j < minMaximalSetSize; j++)
		{
			StringCount scA = resultsA.get(j);
			StringCount scB = resultsB.get(j);
			int compStringAtoB = scA.mString.compareTo(scB.mString);
			int compCountAtoB = scA.mCount - scB.mCount;
			if (compStringAtoB != 0)
			{
				if (compCountAtoB == 0)
				{
					isStringOOrderDIfferentForSameCounts = true;
					if (verbose > 0)
						Sx.format("compareCounters: different strings but same counts at %d: %s %c %s : %d\n"
								, j, scA.mString, (compStringAtoB < 0 ? '<' : '>'), scB.mString, scA.mCount);
				}
				else
				{
					if (verbose != 0)
						Sx.format("compareCounters: different strings and counts at %d: %s %c %s : %d %c %d\n"
								, j, scA.mString, (compStringAtoB < 0 ? '<' : '>'), scB.mString
								, scA.mCount, (compCountAtoB < 0 ? '<' : '>'), scB.mCount);
					return compCountAtoB;
				}
			}
			else
			{
				if (compCountAtoB != 0)
				{
					if (verbose != 0)
						Sx.format("compareCounters: same strings, different counts at %d: %s: %d %c %d\n"
								, j, scA.mString, scA.mCount, (compCountAtoB < 0 ? '<' : '>'), scB.mCount);
					return compCountAtoB;
				}
			}
		}
		if (verbose != 0)
		{
			if (isStringOOrderDIfferentForSameCounts)
				Sx.puts("compareCounters: WARNING: String order differs for the same counts!\n");
			else
				Sx.puts("compareCounters: PASS: No differences in string or count order.\n");
		}
		return 0;
	}


	public static void test_oneCounter(StringCounter counter)
	{
		Sx.puts(counter.getClass().getName() + ".unit_test");
		StringCounterTest.test_StringCounter_Toy(counter, 1);
		
        final String mobydFilePath = FileUtil.getTextFilePath("MobyDick.txt");
        final String iliadFilePath = FileUtil.getTextFilePath("Iliad.txt");
		
		ArrayList<String> textWordsA = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(mobydFilePath);
		ArrayList<String> textWordsB = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(iliadFilePath);

		long time = StringCounterTest.test_StringCounter_Txt(counter, textWordsA, textWordsB, 1);
		Sx.puts("Time for test_StringCounter_Txt in MS: " + time);
	}
	

	public static void unit_test(int level, int verbose)
	{
		Sx.puts(StringCounterTest.class.getName() + ".unit_test");

		StringCounter counterPriority = new PriorityQueueCounter(5);
		StringCounterTest.test_StringCounter_Toy(counterPriority, verbose);

		StringCounter counterLazySort = new LazySortCounter(5);
		StringCounterTest.test_StringCounter_Toy(counterLazySort, verbose);

		compareCounters(counterPriority, counterLazySort, verbose);

		if (level < 1)
			return;

		StringCounter counterInsertSort = new InsertSortedStringCounter(5);
		StringCounterTest.test_StringCounter_Toy(counterInsertSort, verbose);

		compareCounters(counterPriority, counterInsertSort, verbose);

		if (level < 2)
			return;
				
		//////// words counts from texts ////////
        String 
        textFilePath = FileUtil.getTextFilePath("MobyDick.txt");
        ArrayList<String> textWordsA = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(textFilePath);
        textFilePath = FileUtil.getTextFilePath("Iliad.txt");
		ArrayList<String> textWordsB = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(textFilePath);

		int size = 100;
		counterInsertSort = new InsertSortedStringCounter(size);
		long timeInsert = StringCounterTest.test_StringCounter_Txt(counterInsertSort, textWordsA, textWordsB, 0);

		counterPriority = new PriorityQueueCounter(size);
		long timePriority = StringCounterTest.test_StringCounter_Txt(counterPriority, textWordsA, textWordsB, 0);

		compareCounters(counterInsertSort, counterPriority, verbose+1);

		counterLazySort = new LazySortCounter(size);
		long timeLazySort = StringCounterTest.test_StringCounter_Txt(counterLazySort, textWordsA, textWordsB, 0);

		compareCounters(counterPriority, counterLazySort, verbose+1);

		Sx.print("Times:");
		Sx.puts("\t InsertSorted:  " + timeInsert);
		Sx.puts("\t LazySort:      " + timeLazySort);
		Sx.puts("\t PriorityQueue: " + timePriority);
	}

	public static void main(String[] args) 
	{  
		unit_test(3, 1); 
	} 
}
