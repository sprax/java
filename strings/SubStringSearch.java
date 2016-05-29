package sprax.strings;

import sprax.Sx;

/**
 * Search functions return the index of the first occurrence of 
 * the search pattern in the text, or -N, where N = length of text.
 * 
 * @author sprax
 *
 */
public interface SubStringSearch 
{
    public void     setPattern(String patStr);
    public String   getPattern();
    
    /** 
     * Search text for the first occurrence of the specified pattern. 
     * This is a convenience method, equivalent to:
     * setPattern(pat);
     * search(text, 0);
     */
    public int      search(String patStr, String text);
    public int      search(char[] patArr, char[] text);
    
    /** 
     * Search text for the first occurrence of the saved pattern,
     * starting from the specified index, as in "Find next".
     * */
    public int search(String text, int start);
    
    
    /** 
     * Search text for the first occurrence of the saved pattern,
     * starting from the specified index, as in "Find next".
     * */
    public int search(char[] text, int start);   
}


class TestSubStringSearch
{
    static int testFindPattern(SubStringSearch sss, String pat, String txt)
    {
        return sss.search(pat, txt);
    }
    static int testFindNext(SubStringSearch sss, String txt, int idx)
    {
        return sss.search(txt, idx);
    }
    static void printResults(String pat, int idx) 
    {
        Sx.format("idx %4d: ", idx);
        if (idx < 0) {
            Sx.space(-idx);
        } else {
            Sx.space( idx);
        }
        Sx.puts(pat);    
    }
    static int  testFindPatternPrint(SubStringSearch sss, String pat, String txt)
    {
        Sx.print("searched: ");
        Sx.puts(txt);
        int idx = sss.search(pat, txt);
        printResults(pat, idx);
        return idx;
    }
    
    static int testFindNextPrint(SubStringSearch sss, String txt, int start)
    {
        int idx = sss.search(txt, start);
        printResults(sss.getPattern(), idx);
        return idx;
    }
    
}