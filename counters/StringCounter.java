package sprax.counters;

import java.util.Comparator;
import java.util.List;
import java.util.Map;


class StringCount implements Comparable<StringCount>
{
	public static final AscendingStringCountsComp sAscendingStringCountsComp = new AscendingStringCountsComp();
	public static final DescendingStringCountsComp sDescendingStringCountsComp = new DescendingStringCountsComp();

	protected String mString;
	protected int	mCount;
	
	StringCount(String q, int c)
	{
		mString = q;
		mCount = c;
	}

	/**
	 * @return  negative number if (countA < countB) or (countA == countB and stringA < stringB),
	 * 			positive for the opposite, and 0 if (countA == countB and stringA equals stringB).  
	 */
	public static int compare(final String stringA, int countA, final String stringB, int countB)
	{
		int compCounts = countA - countB;
		if (compCounts != 0)
			return compCounts;
		return stringA.compareTo(stringB);
	}
	
	/** Compare counts for ascending order, that is, lesser counts first.
	 *  For descending order, just switch places.
	 */
	@Override
	public int compareTo(StringCount that) 
	{
        return compare(this.mString, this.mCount, that.mString, that.mCount);     			
	}

	@Override
	public String toString()
	{
		return "(" + mString + " : " + mCount + ")";
	}
}

class AscendingStringCountsComp implements Comparator<StringCount>
{
    @Override
    public int compare(StringCount scA, StringCount scB) 
    {
        return StringCount.compare(scA.mString, scA.mCount, scB.mString, scB.mCount);     			
    }
}

class DescendingStringCountsComp implements Comparator<StringCount>
{
    @Override
    public int compare(StringCount scA, StringCount scB) 
    {
        return StringCount.compare(scB.mString, scB.mCount, scA.mString, scA.mCount);     			
    }
}


class AscendingMappedStringCountsCompCountsOnly implements Comparator<String>
{
	Map<String, Integer> mStringToCount;
	
	AscendingMappedStringCountsCompCountsOnly(Map<String, Integer> map)
	{
		mStringToCount = map;
	}
	
    @Override
    public int compare(String sA, String sB) 
    {
        return mStringToCount.get(sA) - mStringToCount.get(sB);
    }
}


public interface StringCounter 
{
	int uniqueStringCount();
	int totalOfAllCounts();
	int maximalCountSetSize();
	
	/**
	 * Adds new string with an initial count of 1; or, if the string is already counted,
	 * adds 1 to is previous count.
	 * @param string
	 * @return True if the string was not previously added; otherwise, False.
	 */
	boolean add(String string);
	
	/**
	 * Adds new string and its initial count; or, if the string is already counted,
	 * adds incrementalCount to is previous count.
	 * @param string
	 * @param incrementalCount
	 * @return
	 */
	boolean add(String string, int incrementalCount);
	
	/** 
	 * Returns an unmodifiable List view of the top N String Counts in descending order
	 * (greater counts first), where N was previously specified, as by the constructor. 
	 */
	List<StringCount> descendingMaximalStringCounts();
}
