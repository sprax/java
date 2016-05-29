/***************************************************************
 *
 *  Compilation:  javac Brtue.java
 *  Execution:    java Brute pattern text
 *
 *  Reads in two strings, the pattern and the input text, and
 *  searches for the pattern in the input text using brute force.
 *
 *  % java Brute abracadabra abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad 
 *  pattern:               abracadabra          
 *
 *  % java Brute rab abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad 
 *  pattern:         rab                         
 * 
 *  % java Brute rabrabracad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                        rabrabracad

 *
 *  % java Brute bcara abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad 
 *  pattern:                                   bcara
 * 
 *  % java Brute abacad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern: abacad
 *
 ***************************************************************/

package sprax.strings;

import sprax.Sx;
import sprax.Sz;

public class NaiveSequentialSearch implements SubStringSearch
{
    String mPatStr;
    
    NaiveSequentialSearch(String patStr)    { mPatStr = patStr; }
    
    @Override
    public void setPattern(String patStr) { mPatStr = patStr; }
    
    @Override
    public String getPattern() { return mPatStr; }
    
    @Override
    public int search(String patStr, String text) {
        mPatStr = patStr;
        return search(text, 0);
    }
    
    /***************************************************************************
     *  String versions
     ***************************************************************************/
    
    @Override
    public int search(String text, int start) {
        return search2(mPatStr, text, start);
    }

    // return offset of first match or N if no match
    public static int search1(String patStr, String text, int start) 
    {
        int N = text.length();    
        if (start < 0)
            return -N;
        
        int M = patStr.length();
        for (int i = start; i <= N - M; i++) {
            int j;
            for (j = 0; j < M; j++) {
                if (text.charAt(i+j) != patStr.charAt(j))
                    break;
            }
            if (j == M) 
                return i;            // found at offset i
        }
        return -N;                            // not found
    }
    
    // return offset of first match or N if no match
    public static int search2(String patStr, String text, int start) 
    {
        int N = text.length();    
        if (start < 0)
            return -N;
        
        int M = patStr.length();
        int i, j;
        for (i = start, j = 0; i < N && j < M; i++) {
            if (text.charAt(i) == patStr.charAt(j)) {
                j++;
            } else {
                i -= j; j = 0;
            }
        }
        if (j == M) 
            return i - M;    // found
        else        
            return -N;        // not found
    }
    
    
    /***************************************************************************
     *  char[] array versions
     ***************************************************************************/
    
    @Override
    public int search(char[] patArr, char[] text) {
        mPatStr = new String(patArr);
        return search(text, 0);
    }
    
    @Override
    public int search(char[] text, int start) {
        return search2(mPatStr, text, start);
    }    
    // return offset of first match or N if no match
    public static int search1(String patStr, char[] text, int start) 
    {
        int N = text.length;    
        if (start < 0)
            return -N;
        
        int M = patStr.length();
        for (int i = start; i <= N - M; i++) {
            int j;
            for (j = 0; j < M; j++) {
                if (text[i+j] != patStr.charAt(j))
                    break;
            }
            if (j == M) 
                return i;            // found at offset i
        }
        return -N;                   // not found
    }
    
    // return offset of first match or N if no match
    public static int search2(String patStr, char[] text, int start) 
    { 
        int N = text.length;    
        if (start < 0)
            return -N;
        
        int M = patStr.length();
        int i, j;
        for (i = start, j = 0; i < N && j < M; i++) {
            if (text[i] == patStr.charAt(j)) {
                j++;
            } else { 
                i -= j; 
                j = 0; 
            }
        }
        if (j == M) 
            return i - M;    // found
        else        
            return -N;        // not found
    } 
    
    static void searchAndShow(String patStr, int offset)
    {
        if (offset < 0) {
            Sx.print("pat (F): ");
            offset = -offset;
        } else {
            Sx.print("pat (T): ");
        }
        Sx.space(offset);
        Sx.puts(patStr);
    }

    public static int unit_test(int lvl) 
    {
        String  testName = NaiveSequentialSearch.class.getName() + ".unit_test";
        Sz.begin(testName);   
        int numWrong = 0;
        
        String patStr = "fracacafra";
        String txtStr = "fracasuzeejcafrajeesuskerfracalacafracacafrasplaca";
        char[] patArr = patStr.toCharArray();
        char[] txtArr = txtStr.toCharArray();
        
        // print results
        Sx.puts("text:    " + txtStr);
        
        int offset = search1(patStr, txtStr, 0);
        searchAndShow(patStr, offset);
        
        offset = search2(patStr, txtStr, 0);
        searchAndShow(patStr, offset);
        
        offset = search1(patStr, txtArr, 0);
        searchAndShow(patStr, offset);
        
        offset = search2(patStr, txtArr, 0);
        searchAndShow(patStr, offset);

        patStr = "calaca";
        patArr = patStr.toCharArray();
        NaiveSequentialSearch nss = new NaiveSequentialSearch(patStr);
        
        offset = nss.search(patArr, txtArr);
        searchAndShow(patStr, offset);
        
        Sx.puts("Using interface test...");
        offset = TestSubStringSearch.testFindPatternPrint(nss, patStr, txtStr);
        offset = TestSubStringSearch.testFindNextPrint(nss, txtStr, 1 + Math.abs(offset));
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) { unit_test(1); }
}


