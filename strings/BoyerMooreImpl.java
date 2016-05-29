package sprax.strings;

import java.util.HashMap;
import java.util.Map;

public abstract class BoyerMooreImpl
{
    protected final int       mCharSetSize;  // the radix
    protected char[]          mPatArr;       // store the pattern as a character array
    protected String          mPatStr;       // or as a string
    
    public BoyerMooreImpl(final String patStr, int charSetSize) 
    {
        mPatStr = patStr;
        mPatArr = patStr.toCharArray();
        mCharSetSize = charSetSize;
    }    
    
    // pattern provided as a character array
    public BoyerMooreImpl(final char[] patArr, int charSetSize) 
    {
        mPatArr = new char[patArr.length];
        for (int j = 0; j < patArr.length; j++)
            mPatArr[j] = patArr[j];                // defensive copy
        mPatStr = new String(mPatArr);
        mCharSetSize = charSetSize;
    }    

    /** 
     * Store index of rightmost occurrence of c in the pattern
     * @param pattern
     */
    public abstract void initializeRightmost();
    
    /** return offset of first match; N if no match */
    public abstract int search(String text, int start);

    /** return offset of first match; N if no match */
    public abstract int search(char[] text, int start);
    
}

class BoyerMooreArray extends BoyerMooreImpl
{
    private int[]                   mRightmostArr;    // the bad-character skip array

    BoyerMooreArray(String patStr, int charSetSize) 
    {
        super(patStr, charSetSize);
        initializeRightmost();
    }
    
    BoyerMooreArray(char patArr[], int charSetSize) 
    {
        super(patArr, charSetSize);
        initializeRightmost();
    }    
    
    public void initializeRightmost()
    {
        mRightmostArr = new int[mCharSetSize];
        for (int c = 0; c < mCharSetSize; c++)
            mRightmostArr[c] = -1;
        for (int j = 0; j < mPatArr.length; j++)
            mRightmostArr[mPatArr[j]] = j;
    }
    
    @Override
    public int search(String txt, int start)
    {
        int M = mPatArr.length;
        int N = txt.length();
        int skip;
        for (int i = start; i <= N - M; i += skip) {
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

    @Override
    public int search(char[] text, int start) 
    {
        int M = mPatArr.length;
        int N = text.length;
        int skip;
        for (int i = start; i <= N - M; i += skip) {
            skip = 0;
            for (int j = M; --j >= 0; ) {
                if (mPatArr[j] != text[i+j]) {
                    skip = Math.max(1, j - mRightmostArr[text[i+j]]);
                    break;
                }
            }
            if (skip == 0) 
                return i;    // found
        }
        return -N;                       // not found
    }
}


class BoyerMooreMap extends BoyerMooreImpl
{
    private Map<Character, Integer> mRightmostMap;    // the bad-character skip map

    BoyerMooreMap(char patArr[], int charSetSize) 
    {
        super(patArr, charSetSize);
        initializeRightmost();
    }
    BoyerMooreMap(String patStr, int charSetSize) 
    {
        super(patStr, charSetSize);
        initializeRightmost();
    }
    
    /** 
     * Store index of rightmost occurrence of c in the pattern in a map
     */
    public void initializeRightmost()
    {
        mRightmostMap = new HashMap<Character, Integer>(mCharSetSize);
        for (int j = 0; j < mPatArr.length; j++)
            mRightmostMap.put(mPatArr[j], j);
    }
    
    @Override
    public int search(String text, int start)
    {
        int M = mPatArr.length;
        int N = text.length();
        int skip;
        for (int i = start; i <= N - M; i += skip) {
            skip = 0;
            for (int j = M; --j >= 0; ) {
                if (mPatArr[j] != text.charAt(i+j)) {
                    Integer rightMost = mRightmostMap.get(text.charAt(i+j));
                    if (rightMost == null)
                        skip = j + 1;
                    else 
                        skip = Math.max(1, j - rightMost);
                    break;
                }
            }
            if (skip == 0) 
                return i;    // found
        }
        return -N;                       // not found
    }

    @Override
    public int search(char[] text, int start) 
    {
        int M = mPatArr.length;
        int N = text.length;
        int skip;
        for (int i = start; i <= N - M; i += skip) {
            skip = 0;
            for (int j = M; --j >= 0; ) {
                if (mPatArr[j] != text[i+j]) {
                    Integer rightMost = mRightmostMap.get(text[i+j]);
                    if (rightMost == null)
                        skip = j + 1;
                    else
                        skip = Math.max(1, j - rightMost);
                    break;
                }
            }
            if (skip == 0) 
                return i;    // found
        }
        return -N;                       // not found
    }
}
