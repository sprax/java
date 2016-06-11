/***************************************************************
 *  Compilation:  javac BoyerMoore.java
 *  Execution:    java BoyerMoore pattern text
 *
 *  Reads in two strings, the pattern and the input text, and
 *  searches for the pattern in the input text using the
 *  bad-character rule part of the Boyer-Moore algorithm.
 *  (does not implement the strong good suffix rule)
 *
 *  % java BoyerMoore abracadabra abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad 
 *  pattern:               abracadabra
 *
 *  % java BoyerMoore rab abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad 
 *  pattern:         rab
 *
 *  % java BoyerMoore bcara abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad 
 *  pattern:                                   bcara
 *
 *  % java BoyerMoore rabrabracad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern:                        rabrabracad
 *
 *  % java BoyerMoore abacad abacadabrabracabracadabrabrabracad
 *  text:    abacadabrabracabracadabrabrabracad
 *  pattern: abacad
 *
 ***************************************************************/
package sprax.strings;

import sprax.Sz;
import sprax.sprout.Sx;

public class BoyerMoore implements SubStringSearch
{
    public  static final int sDefaultCharSetSize = 256;
    
    protected BoyerMooreImpl  mImpl;
    
    // pattern provided as a string
    public BoyerMoore(final String patStr, int charSetSize) 
    {
        if (charSetSize <= sDefaultCharSetSize)
            mImpl = new BoyerMooreArray(patStr, charSetSize);
        else
            mImpl = new BoyerMooreMap(patStr, charSetSize);
    }

    // pattern provided as a character array
    public BoyerMoore(final char patArr[], int charSetSize) 
    {
        if (charSetSize <= sDefaultCharSetSize)
            mImpl = new BoyerMooreArray(patArr, charSetSize);
        else
            mImpl = new BoyerMooreMap(patArr, charSetSize);
    }
    
    /** 
     * Store index of rightmost occurrence of c in the pattern
     * @param pattern
     */
    public void initializeRighmost() {
        mImpl.initializeRightmost(); 
    }
    
    @Override
    public void setPattern(String patStr) 
    {
        mImpl.mPatStr = patStr;
        mImpl.mPatArr = patStr.toCharArray();
        mImpl.initializeRightmost();
    }

    @Override
    public String getPattern() {
        return mImpl.mPatStr;
    }
    
    @Override
    public int search(String patStr, String text) {
        setPattern(patStr);
        return search(text, 0);
    }

    @Override
    public int search(char[] patArr, char[] text) {
        setPattern(new String(patArr));
        return search(text, 0);
    }

    /** return offset of first match; N if no match */
    @Override
    public int search(String text, int start) {
        return mImpl.search(text, start);
    }

    /** return offset of first match; N if no match */
    @Override
    public int search(char[] text, int start) {
        return mImpl.search(text, start);
    }

    public static int unit_test()
    {
        String  testName = UniqueStringFinders.class.getName() + ".unit_test";
        Sz.begin(testName);
        
        String patStr = "fracacafra";
        String txtStr = "fracasuzeejcafrajeesuskerfracalacafracacafraspore";
        char[] patArr = patStr.toCharArray();
        char[] txtArr   = txtStr.toCharArray();

        long begTime = System.currentTimeMillis();
        
        BoyerMoore boyermoore1 = new BoyerMoore(patStr, sDefaultCharSetSize);
        BoyerMoore boyermoore2 = new BoyerMoore(patArr, sDefaultCharSetSize+1);
        int offset1 = boyermoore1.search(txtStr, 0);
        int offset2 = boyermoore2.search(txtArr, 0);

        long endTime = System.currentTimeMillis();
        long totTime = endTime - begTime;

        // print results
        Sx.puts("text:    " + txtStr);

        Sx.print("pattern: ");
        for (int i = 0; i < offset1; i++)
            Sx.print(" ");
        Sx.puts(patStr);
        
        Sx.print("pattern: ");
        for (int i = 0; i < offset2; i++)
            Sx.print(" ");
        Sx.puts(patStr);
        
        Sx.format("%s total time: %d\n", testName, totTime);
        Sz.end(testName, 0);
        return 0;
    }
    
    public static void main(String[] args) {  
        unit_test();
    }
}
