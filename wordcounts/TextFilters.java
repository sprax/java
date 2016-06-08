
package sprax.wordcounts;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class TextFilters 
{

    static Pattern sWordSeparatorPattern = Pattern.compile("([,;\".-]*\\s+|[,;\".-]+\\s*)");

    public static final int sLowerCaseOffset = 'a' - 'A';

    static boolean isAsciiLowerCaseLetter(char ch) {
        return ('a' <= ch && ch <= 'z');
    }  
    static boolean isAsciiUpperCaseLetter(char ch) {
        return ('A' <= ch && ch <= 'Z');
    }
    static boolean isAsciiLetter(char ch) {
        return (('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z'));
    }
    static boolean isNotAsciiLetter(char ch) {
        if (ch < 'A' || 'z' < ch || ('Z' < ch && ch < 'a'))
            return true;
        return false;
    }

    public static String toLowerCaseLetters(final String str)
    {
        int len;
        if (str == null || (len = str.length()) == 0)
            return str;

        char chr[] = new char[len];
        int kEnd = 0;
        for (int j = 0; j < len; j++) {
            char chr_j = str.charAt(j);
            if (isAsciiLowerCaseLetter(chr_j))
                chr[kEnd++] = chr_j;
            else if (isAsciiUpperCaseLetter(chr_j))
                chr[kEnd++] = (char)(chr_j + sLowerCaseOffset);
        }
        return new String(chr, 0, kEnd);
    }

    public static String toLowerCaseLettersAndSingleSpaces(final String str)
    {
        if (str == null || str.isEmpty()) {
            return "";
        }
        char chr[] = toLowerCaseLettersAndSingleSpaces_twoState(str.toCharArray(), str.length());
        return new String(chr);
    }

    /**
     * Parses input char array using two states for greater efficiency.
     * The two states are: whiteSpace or not, or, in-between-words vs. inside-a-word.
     * Note that any hyphen and apostrophe characters - and ' are discarded whether 
     * they appear inside or outside a word.  Thus "forty-one" becomes "fortyone", 
     * "don't" becomes "dont", and "God's will" becomes "gods will".
     * 
     * @param chr     input char array
     * @param length  number of chars in chr to process
     * @return        processed char array (single-spaced lower-case-latter words)
     */
    private static char[] toLowerCaseLettersAndSingleSpaces_twoState(char[] chr, int length)
    {
        char chrPrv = ' ';  // SPACE char represents the state whiteSpace
        int kEnd = 0;
        for (int j = 0; j < length; j++) {
            char chr_j = chr[j];

            // Replace the following characters with nothing, regardless of state.
            // Swallowing these characters by not incrementing k works only because
            // we're copying every character we keep from the input to the output.
            if (chr_j == '-' || chr_j == '\'' || chr_j == '_' || Character.isDigit(chr_j))
                continue;

            if (chrPrv == ' ') {       // current state is whiteSpace; find a letter to change it.
                if (isAsciiLowerCaseLetter(chr_j)) {
                    chr[kEnd++] = chrPrv = chr_j;
                }
                else if (isAsciiUpperCaseLetter(chr_j)) {
                    chr[kEnd++] = chrPrv = (char)(chr_j + sLowerCaseOffset);
                }
            }
            else {      // current state is NOT whiteSpace; find a word-terminator to change it.
                if (isAsciiLowerCaseLetter(chr_j)) {
                    chr[kEnd++] = chr_j;
                }
                else if (isAsciiUpperCaseLetter(chr_j)) {
                    chr[kEnd++] = (char)(chr_j + sLowerCaseOffset);
                }
                else {                           // Found a word-terminator.
                    chr[kEnd++] = chrPrv = ' ';    // Replace substring of non-letters with a single SPACE char.
                }
            }
        }
        return Arrays.copyOf(chr, kEnd);
    }

    protected static int test_toLowerCaseLettersAndSingleSpaces(String lines[], int level) 
    {
        for (String line : lines) {
            String cvrt = toLowerCaseLettersAndSingleSpaces(line);
            if ( ! cvrt.isEmpty()) {
                cvrt.trim();
                if (cvrt.length() > 0) {
                    String strs[] = cvrt.split(" ");
                    putsArray(strs);
                    System.out.println(" ==> " + strs.length + " words");
                    for (String ss : strs) {
                        ss.trim();
                        if (ss.length() > 0) {
                            System.out.println("[" + ss + "]");
                        }
                    }
                }
            }
        }
        return 0;
    }






    public static boolean collectLowerCaseLetterWords(Collection<char[]> words, char[] chr, int length)
    {
        boolean anyWordsAdded = false;
        char chrPrv = ' ';  // space
        int kBeg = 0, kEnd = 0;
        for (int j = 0; j < length; j++) {
            char chr_j = chr[j];

            // NB: Contrary to toLowerCaseLettersAndSingleSpaces_twoState, which 
            // copies all kept characters from input to output, here the hyphen,
            // apostrophe, and underscore characters terminate a word.

            // If current state is whiteSpace, find a letter to change it.
            if (chrPrv == ' ') {
                if (isAsciiLowerCaseLetter(chr_j)) {
                    kBeg = j;
                    kEnd = j + 1;
                    chrPrv = chr_j;
                }
                else if (isAsciiUpperCaseLetter(chr_j)) {
                    kEnd = kBeg = j;
                    chr[kEnd++] = chrPrv = (char)(chr_j + sLowerCaseOffset);
                }
            }
            else {
                if (isAsciiLowerCaseLetter(chr_j)) {
                    kEnd++;
                }
                else if (isAsciiUpperCaseLetter(chr_j)) {
                    chr[kEnd++] = (char)(chr_j + sLowerCaseOffset);
                }
                else {
                    if (words.add(Arrays.copyOfRange(chr, kBeg, kEnd)))
                        anyWordsAdded = true;
                    chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
                }
            }
        }
        // If the input ended in non-whiteSpace, add this last word.
        if (chrPrv != ' ' && words.add(Arrays.copyOfRange(chr, kBeg, kEnd)))
            anyWordsAdded = true;
        return anyWordsAdded;
    }

    /**
     * @param words
     * @param str
     * @return
     */
    public static boolean collectLowerCaseLetterWords(Collection<String> words, String str)
    {
        boolean anyWordsAdded = false;
        char chrPrv = ' ';  // space
        int kBeg = 0, kEnd = 0, strLen = str.length();
        for (int j = 0; j < strLen; j++) {
            char chr_j = str.charAt(j);

            // NB: Contrary to toLowerCaseLettersAndSingleSpaces_twoState, which 
            // copies all kept characters from input to output, here the hyphen,
            // apostrophe, and underscore characters terminate a word.

            // If current state is whiteSpace, find a letter to change it.
            if (chrPrv == ' ') {
                if ((isAsciiLowerCaseLetter(chr_j)) || (isAsciiUpperCaseLetter(chr_j))) {
                    kBeg = j;
                    kEnd = j + 1;
                    chrPrv = chr_j;
                }
            }
            else {
                if (isAsciiLowerCaseLetter(chr_j) || isAsciiUpperCaseLetter(chr_j)) {
                    kEnd++;
                }
                else {
                    if (words.add(str.substring(kBeg, kEnd).toLowerCase()))
                        anyWordsAdded = true;
                    chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
                }
            }
        }
        // If the input ended in non-whiteSpace, add this last word.
        if (chrPrv != ' ' && words.add(str.substring(kBeg, kEnd).toLowerCase()))
            anyWordsAdded = true;
        return anyWordsAdded;
    }

    public static String[] toWordArray(String text) {
        String cleaned = toLowerCaseLettersAndSingleSpaces(text);
        if (cleaned.isEmpty()) {
            return new String[0];
        }
        return cleaned.split(" ");
    }

    public static int test_toWordArray(String text) {
        System.out.println("primitive test on this text: (" + text + ")");
        String conv = toLowerCaseLettersAndSingleSpaces(text);
        if (conv.length() > 0) {
            String words[] = conv.split(" ");
            putsArray(words);
            return words.length;
        }
        return 0;
    }

    public static void putsArray(Object A[]) {
        for (int j = 0; j < A.length; j++) {
            System.out.print(" " + A[j]);
        }
        System.out.println();
    }

    /** Amount of output increases with level */
    public static int unit_test(int level) 
    {
        System.out.println(TextFilters.class.getName() + ".unit_test");  

        int stat = 0;
        char allChars[] = new char[256];
        for (char c = '\0'; c < 256; c++) 
            allChars[c] = c;
        String allCharString = new String(allChars);
        String lines[] = { " Spaces trimmed BEFORE,  MIDDLE,    and   AFTER?    "
                , "UPPER  to lower?  ABCDEFGHIJKLMNOPQRSTUVWXYZ -> abcdefghijklmnopqrstuvwxyz"
                , "  Digits: [0123456789] That was zero through nine..."
                , "Now for thirty-two printable non-letters: " 
                , " AAA`~!@#$%^&*()_-=+{}[]\\|;:\"',./<>?zzz "
                , allCharString
        };
        if (level > 0) {
            stat += test_toLowerCaseLettersAndSingleSpaces(lines, 1);
        }
        String text = "  What in \"dog's\" name is all this--? --well, it's @#^&$* tom=foolery!  ";
        int numWords = test_toWordArray(text);
        System.out.println("test_toWordArray returned " + numWords);

        text = " [ ] ";
        numWords = test_toWordArray(text);
        System.out.println("test_toWordArray returned " + numWords);

        text = " ";
        numWords = test_toWordArray(text);
        System.out.println("test_toWordArray returned " + numWords);

        text = "";
        numWords = test_toWordArray(text);
        System.out.println("test_toWordArray returned " + numWords);

        String[] arg = text.split(" ");
        putsArray(arg);

        String arr[] = new String[0];
        putsArray(arr);

        return stat;
    } 

    public static void main(String[] args) {
        unit_test(1);
    }

}
