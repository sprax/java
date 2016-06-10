package sprax;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import sprax.sprout.*;

/**
 * static output methods for testing, debugging, etc.
 *
 * rare at start: Sd Sg Sj Ss Sv Sx Sz rare anywhere: Sj Sv Sx Sz Sd: System debug Sj: ? Sv: System
 * verbosity Sx: System exchange external interact Sx Sx Sz Sz Sz. So output Si input Sf file manip
 */
public class Sx
{
    private static int      sDbg       = 1;
    public static final int SPACE_CHAR = 32;
    
    int getDbg()
    {
        return sDbg;
    }
    
    void setDbg(int nDbg)
    {
        sDbg = nDbg;
    }
    
    // Methods for unconditional output (printing regardless of debug level).
    public static void puts()
    {
        System.out.println();
    }
    
    public static void puts(String string)
    {
        System.out.println(string);
    }
    
    public static void putss(String string)
    {
        System.out.println(string);
        System.out.println();
    }
    
    public static int puts(char[] chr)
    {
        int n = printTrimmed(chr);
        puts();
        return n;
    }
    
    public static void puts(Object object)
    {
        System.out.println(object);
    }
    
    public static void puts(int n, String s)
    {
        puts(Spaces.get(n) + s);
    }
    
    public static void print(String string)
    {
        System.out.print(string);
    }
    
    public static void print(Object object)
    {
        System.out.print(object);
    }
    
    public static void printListInt(int k)
    {
        System.out.format(" %2d", k);
    }
    
    public static void printListInt(long k)
    {
        System.out.format(" %2d", k);
    }
    
    public static void printCommaInt(int k)
    {
        System.out.format(", %2d", k);
    }

    public static void printOne(int k)
    {
        System.out.format(" %d", k);
    }
    
    public static void printOne(char ch)
    {
        System.out.format(" %c", ch);
    }
    
    public static void printOne(Character ch)
    {
        System.out.format(" %c", ch);
    }
    
    public static void printOne(String str)
    {
        System.out.format(" %s", str);
    }
    
    public static <T> void printOne(T t)
    {
        System.out.format(" " + t);
    }
    
    public static void space(int numSpaces)
    {
        Spaces.put(numSpaces);
    }
    
    /** prints trimmed sub-array of the input as a string, no spaces */
    public static int printTrimmed(char[] chr)
    {
        if (chr == null) {
            print("");
            return 0;
        }
        else {
            int beg = 0;
            while (beg < chr.length && chr[beg] < SPACE_CHAR)
                beg++;
            int end = beg;
            while (beg < chr.length && SPACE_CHAR <= chr[end] && chr[end] < 128)
                end++;
            int count = end - beg;
            if (count > 0)
                System.out.print(new String(chr, beg, count));
            return count;
        }
    }
    
    /** prints array with a space character before each entry */
    public static void printArray(char A[])
    {
        for (int j = 0; j < A.length; j++)
            print(" " + A[j]);
    }
    
    public static void printOffsetArray(char offset, char A[])
    {
        for (int j = 0; j < A.length; j++)
            format("  %c", offset + A[j]);
    }
    
    public static void printArray(boolean B[])
    {
        for (int j = 0; j < B.length; j++)
            print(" " + (B[j] ? 1 : 0));
    }
    
    public static <T> void printArray(ArrayList<T> L)
    {
        int sz = L.size();
        if (sz > 0) {
            print(L.get(0).toString());
            for (int j = 1; j < L.size(); j++)
                print(" " + L.get(j));
        }
    }
    
    public static <T> void printList(List<T> L)
    {
        int sz = L.size();
        if (sz > 0) {
            print(L.get(0).toString());
            for (int j = 1; j < L.size(); j++)
                print(" " + L.get(j));
        }
    }
    
    /** prints array with a space character before each entry */
    public static void printArray(char A[], int numSpaces)
    {
        printArray(A, numSpaces, A.length);
    }
    
    public static void printArray(Object A[], int numSpaces)
    {
        printArray(A, numSpaces, A.length);
    }
    
    public static void printArray(String preLabel, Object A[])
    {
        print(preLabel);
        printArray(A, 1, A.length);
    }
    
    public static <T> void printArray(ArrayList<T> A, int numSpaces)
    {
        printArray(A, numSpaces, A.size());
    }
    
    /**
     * prints array with the specified number of space characters before each entry
     */
    public static int printArray(char A[], int numSpaces, int numChars)
    {
        if (numChars < 0 || numChars > A.length)
            numChars = A.length;
        
        if (numSpaces < 1) {
            for (int j = 0; j < numChars; j++)
                print(A[j]);
        } else if (numSpaces == 1) {
            for (int j = 0; j < numChars; j++) {
                print(" " + A[j]);
            }
        } else {
            for (int j = 0; j < numChars; j++) {
                for (int k = numSpaces; --k >= 0;)
                    print(" ");
                print(A[j]);
            }
        }
        return numChars;
    }
    
    /**
     * prints array with the specified number of space characters before each entry
     */
    public static int printArray(Object A[], int numSpaces, int numChars)
    {
        if (numChars < 0 || numChars > A.length)
            numChars = A.length;
        
        if (numSpaces < 1) {
            for (int j = 0; j < numChars; j++)
                print(A[j]);
        } else if (numSpaces == 1) {
            for (int j = 0; j < numChars; j++) {
                print(" " + A[j]);
            }
        } else {
            for (int j = 0; j < numChars; j++) {
                for (int k = numSpaces; --k >= 0;)
                    print(" ");
                print(A[j]);
            }
        }
        return numChars;
    }
    
    /**
     * prints array with the specified number of space characters before each entry
     */
    public static <T> int printArray(ArrayList<T> A, int numSpaces, int numChars)
    {
        if (numChars < 0 || numChars > A.size())
            numChars = A.size();
        
        if (numSpaces < 1) {
            for (int j = 0; j < numChars; j++)
                print(A.get(j));
        } else if (numSpaces == 1) {
            for (int j = 0; j < numChars; j++) {
                print(" " + A.get(j));
            }
        } else {
            for (int j = 0; j < numChars; j++) {
                for (int k = numSpaces; --k >= 0;)
                    print(" ");
                print(A.get(j));
            }
        }
        return numChars;
    }
    
    public static void putsArray(char A[], int numSpaces, int numChars)
    {
        printArray(A, numSpaces, numChars);
        puts();
    }
    
    public static void printSubArray(char A[], int start, int end)
    {
        if (end > A.length)
            end = A.length;
        for (int j = start; j < end; j++)
            print(" " + A[j]);
    }
    
    public static void printSubArray(float A[], int start, int end)
    {
        if (end > A.length)
            end = A.length;
        for (int j = start; j < end; j++)
            format(" % 6.2f", A[j]);
    }
    
    public static void putsArray(char A[])
    {
        printArray(A);
        puts();
    }
    
    public static void putsArray(long A[])
    {
        printArray(A);
        puts();
    }
    
    public static void putsOffsetArray(char offset, char A[])
    {
        printOffsetArray(offset, A);
        puts();
    }
    
    public static <T> void putsArray(ArrayList<T> A)
    {
        printArray(A);
        puts();
    }
    
    public static <T> void putsList(List<T> L)
    {
        printList(L);
        puts();
    }
    
    public static <T> void putsList(String preLabel, List<T> L)
    {
        print(preLabel);
        printList(L);
        puts();
    }
    
    public static void putsArray(char A[], int numSpaces)
    {
        printArray(A, numSpaces);
        puts();
    }
    
    public static void putsArray(Object A[], int numSpaces)
    {
        printArray(A, numSpaces);
        puts();
    }
    
    public static <T> void putsArray(ArrayList<T> A, int numSpaces)
    {
        printArray(A, numSpaces);
        puts();
    }
    
    public static void putsSubArray(char A[], int start, int end)
    {
        printSubArray(A, start, end);
        puts();
    }
    
    public static void putsSubArray(float A[], int start, int end)
    {
        printSubArray(A, start, end);
        puts();
    }
    
    public static void putsSubArray(Object A[], int start, int end)
    {
        printSubArray(A, start, end);
        puts();
    }
    
    public static void putsArray(char A[], String postLabel)
    {
        printArray(A);
        puts(postLabel);
    }
    
    public static void putsArray(boolean B[], String postLabel)
    {
        printArray(B);
        puts(postLabel);
    }
    
    public static void putsArray(String preLabel, char A[])
    {
        print(preLabel);
        putsArray(A);
    }
    
    public static void putsArray(String preLabel, char A[], int numSpaces)
    {
        print(preLabel);
        putsArray(A, numSpaces);
    }
    
    public static void putsArray(String preLabel, Object A[], int numSpaces)
    {
        print(preLabel);
        putsArray(A, numSpaces);
    }
    
    public static <T> void putsArray(String preLabel, ArrayList<T> A, int numSpaces)
    {
        print(preLabel);
        printArray(A, numSpaces);
        puts();
    }
    
    public static <T> void putsArray(String preLabel, ArrayList<T> A)
    {
        print(preLabel);
        printArray(A);
        puts();
    }
    
    public static void putsArray(String preLabel, char A[], String postLabel)
    {
        print(preLabel);
        putsArray(A, postLabel);
    }
    
    public static void printArray(int A[])
    {
        for (int j = 0; j < A.length; j++)
            printListInt(A[j]);
    }
    
    public static void printArray(int A[], PrintOneInt printOne)
    {
        for (int j = 0; j < A.length; j++)
            printOne.printOne(A[j]);
    }
    
    public static void printArray(long A[])
    {
        for (int j = 0; j < A.length; j++)
            printListInt(A[j]);
    }
    
    public static void printSubArray(int A[], int start, int end)
    {
        if (end > A.length)
            end = A.length;
        for (int j = start; j < end; j++)
            printListInt(A[j]);
    }
    
    public static void printSubArray(Object A[], int start, int end)
    {
        if (end > A.length)
            end = A.length;
        for (int j = start; j < end; j++)
            print(" " + A[j]);
    }
    
    public static void putsArray(int A[])
    {
        printArray(A);
        puts();
    }
    
    public static void putsArray(int A[], PrintOneInt printOne)
    {
        printArray(A, printOne);
        puts();
    }
    
    public static void putsArray(String preLabel, int A[])
    {
        print(preLabel);
        putsArray(A);
    }
    
    public static void putsArray(int A[], String postLabel)
    {
        printArray(A);
        puts(postLabel);
    }
    
    // 2-Dimensional Arrays, as it were
    public static void putsArray(char AA[][])
    {
        for (int j = 0; j < AA.length; j++)
            putsArray(AA[j]);
    }
    
    public static void putsOffsetArray(char offset, char AA[][])
    {
        for (int j = 0; j < AA.length; j++)
            putsOffsetArray(offset, AA[j]);
    }
    
    public static void putsOffsetArray(char offset, char AA[][], String postLabel)
    {
        for (int j = 0; j < AA.length; j++)
            putsOffsetArray(offset, AA[j]);
        puts(postLabel);
    }
    
    public static void putsArray(int AA[][])
    {
        for (int j = 0; j < AA.length; j++)
            putsArray(AA[j]);
    }
    
    
    public static void putsArray(int AA[][], PrintOneInt printOne)
    {
        for (int j = 0; j < AA.length; j++)
            putsArray(AA[j], printOne);
    }
    
    public static void putsArray(Integer AA[][])
    {
        for (int j = 0; j < AA.length; j++)
            putsArray(AA[j]);
    }
    
    public static void putsArray(String preLabel, int AA[][])
    {
        print(preLabel);
        putsArray(AA);
    }
    
    public static void putsArray(String preLabel, int AA[][], PrintOneInt printOne)
    {
        print(preLabel);
        putsArray(AA, printOne);
    }
    
    public static void putsArray(int AA[][], String postLabel)
    {
        printArray(AA);
        puts(postLabel);
    }
    
    public static void putsArrayCode(String declareStr, int AA[][])
    {
        print(declareStr);
        putsArrayCode(AA);
    }
    
    public static void putsArrayCode(String declareBeg, char AA[][], char offset, String declareEnd)
    {
        print(declareBeg);
        putsArrayCode(AA, offset);
        puts(declareEnd);
    }
    
    public static void putsArrayCode(char AA[][], char offset)
    {
        for (int j = 0; j < AA.length; j++)
            putsArrayCode(AA[j], offset);
    }
    
    public static void putsArrayCode(int AA[][])
    {
        for (int j = 0; j < AA.length; j++)
            putsArrayCode(AA[j]);
    }
    
    public static void putsArrayCode(char A[], char offset)
    {
        printArrayCode(A, offset);
        puts(",");
    }
    
    public static void putsArrayCode(int A[])
    {
        printArrayCode(A);
        puts(",");
    }
    
    public static void printArrayCode(char A[], char offset)
    {
        System.out.format("{ %c", A[0] + offset);
        for (int end = A.length, j = 1; j < end; j++)
            System.out.format(", %c", A[j] + offset);
        print(" }");
    }
    
    public static void printArrayCode(int A[])
    {
        System.out.format("{ %2d", A[0]);
        for (int end = A.length, j = 1; j < end; j++)
            System.out.format(", %2d", A[j]);
        print(" }");
    }
    
    public static void putsSubArray(int A[], int start, int end)
    {
        printSubArray(A, start, end);
        puts();
    }
    
    public static void putsSubArray(String preLabel, int A[], int start, int end)
    {
        print(preLabel);
        putsSubArray(A, start, end);
    }
    
    public static void putsSubArray(String preLabel, float A[], int start, int end)
    {
        print(preLabel);
        putsSubArray(A, start, end);
    }
    
    public static void printSubArray(String preLabel, Integer A[], int start, int end)
    {
        print(preLabel);
        printSubArray(A, start, end);
    }
    
    public static void putsSubArray(String preLabel, Integer A[], int start, int end)
    {
        print(preLabel);
        putsSubArray(A, start, end);
    }
    
    public static void putsSubArray(String preLabel, Object A[], int start, int end)
    {
        print(preLabel);
        putsSubArray(A, start, end);
    }
    
    public static void putsArray(String preLabel, int A[], String postLabel)
    {
        print(preLabel);
        putsArray(A, postLabel);
    }
    
    public static void printArray(float A[])
    {
        for (int j = 0; j < A.length; j++)
            System.out.format(" % -5g", A[j]);
    }
    
    public static void putsArray(float A[])
    {
        printArray(A);
        puts();
    }
    
    public static void printArray(double A[])
    {
        for (int j = 0; j < A.length; j++)
            System.out.format(" % 5g", A[j]);
    }
    
    public static void putsArray(double A[])
    {
        printArray(A);
        puts();
    }
    
    public static void putsArray(double A[], String postLabel)
    {
        printArray(A);
        puts(postLabel);
    }
    
    public static void putsArray(String preLabel, double A[])
    {
        print(preLabel);
        putsArray(A);
    }
    
    public static void putsArray(String preLabel, double A[], String postLabel)
    {
        print(preLabel);
        putsArray(A, postLabel);
    }
    
    public static void printArray(Object A[])
    {
        for (int j = 0; j < A.length; j++)
            print(" " + A[j]);
    }
    
    public static void putsArray(Object A[])
    {
        printArray(A);
        puts();
    }
    
    public static void putsArray(Object A[], String postLabel)
    {
        printArray(A);
        puts(postLabel);
    }
    
    public static void putsArray(String preLabel, Object A[])
    {
        print(preLabel);
        putsArray(A);
    }
    
    public static void putsArray(String preLabel, Object A[], String postLabel)
    {
        print(preLabel);
        putsArray(A, postLabel);
    }
    
    public static <T> void printIterable(Iterable<T> iter)
    {
        for (T x : iter)
            print(" " + x);
    }
    
    public static <T> void printIterable(Iterable<T> list, int limit)
    {
        if (list == null)
            print("[null iterable]");
        else
            for (T x : list) {
                if (--limit < 0) {
                    print("...");
                    break;
                }
                print(x + " ");
            }
    }
    
    public static <T> void putsIterable(Iterable<T> iter)
    {
        printIterable(iter);
        puts();
    }
    
    public static <T> void putsIterable(Iterable<T> iter, int limit)
    {
        printIterable(iter, limit);
        puts();
    }
    
    public static <T> void putsIterable(String preLabel, Iterable<T> iter)
    {
        puts(preLabel);
        printIterable(iter);
        puts();
    }
    
    public static <T> void putsIterable(Iterable<T> iter, String postLabel)
    {
        printIterable(iter);
        puts(postLabel);
    }
    
    public static <T> void putsIterable(Iterable<T> iter, int limit, String postLabel)
    {
        printIterable(iter, limit);
        puts(postLabel);
    }
    
    public static <T> void putsIterable(String preLabel, Iterable<T> iter, int limit)
    {
        print(preLabel);
        printIterable(iter, limit);
        puts();
    }
    
    public static void printListOfCharray(Iterable<char[]> iter)
    {
        for (char[] chr : iter)
            print(" " + new String(chr));
    }
    
    public static void putsListOfCharray(Iterable<char[]> iter, String postLabel)
    {
        printListOfCharray(iter);
        puts(postLabel);
    }
    
    public static <T> void putsReversed(ListIterator<T> lit)
    {
        while (lit.hasPrevious()) {
            printOne(lit.previous());
        }
        Sx.puts();
    }
    
    public static <T> void putsReversed(List<T> list)
    {
        ListIterator<T> lit = list.listIterator(list.size());
        putsReversed(lit);
    }
    
    public static <T> void putsReversed(T[] array)
    {
        ListIterator<T> lit = Arrays.asList(array).listIterator(array.length);
        putsReversed(lit);
    }
    
    public static void putsStringIntMapInColumns(Map<String, Integer> map, int numCols, int maxLen)
    {
        int q = 0;
        String ends = null;
        String formats = String.format("%%%ds %%s%%s", maxLen);
        for (Map.Entry<String, Integer> pair : map.entrySet()) {
            String word = pair.getKey();
            int count = pair.getValue();
            if (++q == numCols) {
                q = 0;
                ends = "\n";
            } else {
                ends = "  ";
            }
            System.out.format(formats, word, (count > 1 ? count : " "), ends);
        }
        puts();
    }
    
    // Methods for conditional output (printing that depends on debug level).
    // Increasing sDbg here means decreasing debugging output for all clients.
    // Increasing sDbg filters out client requests.
    // Clients should call with dbgLevel > 1 or more if they really want
    // their debug info displayed, or decrease the package dbgLevel (held here)
    // when they want to dial the debugging output back towards minimal.
    /** basic conditional print method (print w/o newline), depends on debug level. */
    public static void dbg(int dbgLevel, String string)
    {
        if (dbgLevel > sDbg)
            print(string);
    }
    
    /** basic conditional puts method (print + newline), depends on debug level. */
    public static void debug(int dbgLevel, String string)
    {
        if (dbgLevel > sDbg)
            puts(string);
    }
    
    public static void debug(int dbgLevel)
    {
        if (dbgLevel > sDbg)
            puts();
    }
    
    public static void debugArray(int nDbg, int A[])
    {
        if (nDbg > sDbg)
            putsArray(A);
    }
    
    public static void debugArray(int nDbg, int AA[][])
    {
        if (nDbg > sDbg)
            putsArray(AA);
    }
    
    public static void debugArray(int nDbg, String preLabel, int AA[][])
    {
        if (nDbg > sDbg)
            putsArray(preLabel, AA);
    }
    
    public static void debugSubArray(int nDbg, String preLabel, int A[], int start, int end)
    {
        if (nDbg > sDbg)
            putsSubArray(preLabel, A, start, end);
    }
    
    public static void printf(String formats, Object... args)
    {
        System.out.format(formats, args);
    }
    
    public static void format(String formats, Object... args)
    {
        System.out.format(formats, args);
    }
    
    public static void debug(int nDbg, String formats, Object... args)
    {
        if (nDbg > sDbg)
            System.out.format(formats, args);
    }
    
    public static void printSpaces(int numSpaces)
    {
        print(Spaces.get(numSpaces));
    }
    
    // BUFFERED CONSOLE INPUT **************************************************/
    
    /** "Singleton" instantiation by class loader */
    private static InputStreamReader sIsr            = new InputStreamReader(System.in);
    private static BufferedReader    sBufferedReader = new BufferedReader(sIsr);
    
    public static BufferedReader getBufferedReader()
    {
        return sBufferedReader;
    }
    
    /** Uses InputStreamReader */
    public static String getString()
    {
        String str = null;
        try {
            str = getBufferedReader().readLine();
        } catch (IOException iox) {
            Sx.debug(1, "getString IOException: " + iox.getMessage());
        }
        return str;
    }
    
    // assume Unicode UTF-8 encoding
    private static String charsetName = "UTF-8";

    // the scanner object
    private static Scanner scanner = new Scanner(new BufferedInputStream(System.in), charsetName);

    // assume language = English, country = US for consistency with System.out.
    private static Locale usLocale = new Locale("en", "US");
    
    // static initializer
    static { scanner.useLocale(usLocale); }

    /**
     * Read input delimited by single or double quotation marks 
     * and split it into an array of strings.
     */
    public static String[] getQuotedStrings(String prompt) 
    {
        if (prompt != null)
            print(prompt);
        String[] fields = readAllQuoted().trim().split("\\s+");
        return fields;
    }

    /**
     * Return rest of input from standard input
     */
    public static String readAllQuoted() {
        if (!scanner.hasNextLine()) 
            return null;

        return scanner.useDelimiter("[\"\']").next();
    }

    // -------------------------------------------------------------------
    /**
     * reads chars into a pre-allocated char array, but replaces line-ending characters such as \r
     * and \n with \0's (null chars).
     * 
     * @return end index of char buffer array after reading and trimming off any line ending chars.
     *         This would also be the starting position for appending any further characters to the
     *         char array.
     */
    public static int getCharray(char[] cbuf) throws IOException
    {
        int numCharsRead = getBufferedReader().read(cbuf);
        return trimEnd(cbuf, 0, numCharsRead);
    }
    
    // -------------------------------------------------------------------
    /**
     * reads up to maxLen chars in to char array starting at the specified offset.
     */
    public static int getCharray(char[] cbuf, int off, int maxLen) throws IOException
    {
        int numCharsRead = getBufferedReader().read(cbuf, off, maxLen);
        return trimEnd(cbuf, off, numCharsRead);
    }

    /**
     * Trims line-endings and any other chars < SPACE_CHAR from the end of a sequence of characters
     * in cbuf, and returns the resulting end index.
     * 
     * @param cbuf
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public static int trimEnd(char[] cbuf, int off, int len) throws IOException
    {
        int end = off + len;
        while (end > off && cbuf[end - 1] < SPACE_CHAR) {
            cbuf[--end] = '\0';
        }
        return end;
    }
    
    // -------------------------------------------------------------------
    public static char getChar()
    {
        String s = getString();
        return s.charAt(0);
    }
    
    // -------------------------------------------------------------------
    public static int getInt()
    {
        String s = getString();
        return Integer.parseInt(s);
    }
    
    public static int test_getString()
    {
        String prompt = "Enter a string: ";
        int promptLen = prompt.length();
        print(prompt);
        
        String strA = getString();
        printSpaces(promptLen);
        print(strA);
        
        strA += getString();
        printSpaces(promptLen);
        print(strA);
        
        strA += getString();
        printSpaces(promptLen);
        puts(strA);
        puts("Goodbye from getString!");
        
        return 0;
    }
    
    public static int test_getStrings()
    {
        String prompt = "Enter a string between quotation marks: ";
        int promptLen = prompt.length();                
        String strs[] = Sx.getQuotedStrings(prompt);
        printSpaces(promptLen);        
        Sx.putsArray(strs);

        puts("Goodbye from test_getStrings!");
        
        return 0;
    }
    
    public static int test_getCharray() throws IOException
    {
        char charray[] = new char[128];
        String prompt = "Enter some chars: ";
        int promptLen = prompt.length();
        print(prompt);
        
        // test mixes different getCharray methods, which somewhat complicates things...
        int numKept = getCharray(charray);
        printSpaces(promptLen);
        int end = printTrimmed(charray); // print(char[])
        if (end != numKept)
            throw new IllegalStateException(
                    "getCharray and print(char[]) disagree on line endings!");
        
        end = getCharray(charray, end, charray.length - end);
        printSpaces(promptLen);
        int len = printArray(charray, 0, end);
        if (len != end)
            throw new IllegalStateException(
                    "getCharray and printArray(char[], numSpaces, end) disagree on line endings!");
        
        end = getCharray(charray, end, charray.length - end);
        printSpaces(promptLen);
        printArray(charray, 0, end);
        
        end = getCharray(charray, end, charray.length - end);
        printSpaces(promptLen);
        printTrimmed(charray);
        
        end = getCharray(charray, end, charray.length - end);
        printSpaces(promptLen);
        putsArray(charray, 0, end);
        
        puts("Goodbye from getCharray!");
        return 0;
    }
    
    /**
     * unit_test
     */
    public static int unit_test(int level) throws IOException
    {
        String testName = Sx.class.getName() + ".unit_test";
        format("BEGIN %s\n", testName);

        String str = null;
        char[] chr = new char[0];
        System.out.println(str);
        System.out.println(chr);
        
        puts("puts(literal string)");
        String string = "puts(String)";
        puts(string);
        Object o = "puts(Object from literal string)";
        puts(o);
        Integer integer = 42;
        puts(integer);
        printf("one %d  two %d  three %d  ", 1, 2, 3);
        format("four %d five %d  six %d \n", 4, 5, 6);
        
        ArrayList<String> straw = new ArrayList<String>();
        straw.add("abc");
        straw.add("abc");
        straw.add("first");
        straw.add("last");
        straw.add("efg");
        
        ListIterator<String> lit = straw.listIterator(straw.size());
        Sx.putsReversed(lit);
        
        if (level > 1) {
            test_getString();
            test_getCharray();
            test_getStrings();
        }
        format("END   %s,  PASS\n", testName);
        return 0;
    }
    
    public static void main(String[] args) throws IOException
    {
        unit_test(1);
    }
}
