package sprax.files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import sprax.sprout.Sx;
import sprax.test.Sz;

public class TextFilters 
{
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
        char chr[] = toLowerCaseLettersAndSingleSpaces_twoStateB(str.toCharArray());
        return new String(chr);
    }

    /**
     * Not as efficient as two-state parsing methods (states: whiteSpace or not).
     * @deprecated
     * @param chr     input char array
     * @param length  number of chars in chr to process
     * @return        processed char array (single-spaced lower-case-latter words)
     */
    protected static char[] toLowerCaseLettersAndSingleSpaces_prevChar(char[] chr, int length)
    {
        char chrPrv = ' ';  // space
        int kEnd = 0;
        for (int j = 0; j < length; j++) {
            char chr_j = chr[j];
            if (isAsciiLowerCaseLetter(chr_j))
                chr[kEnd++] = chrPrv = chr_j;
            else if (isAsciiUpperCaseLetter(chr_j))
                chr[kEnd++] = chrPrv = (char)(chr_j + sLowerCaseOffset);
            else if (chr_j == '-' || chr_j == '\'' || chr_j == '_' || Character.isDigit(chr_j))
                /* replace it with nothing, and don't update chrPrv */ ;
            else if (chrPrv != ' ')
                chr[kEnd++] = chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
        }
        return Arrays.copyOf(chr, kEnd);
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
    public static char[] toLowerCaseLettersAndSingleSpaces_twoState(char[] chr, int length)
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
    
    public static char[] toLowerCaseLettersAndSingleSpaces_twoStateA(char[] chr, int length)
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

            if (isAsciiUpperCaseLetter(chr_j)) {
                chr_j += sLowerCaseOffset;
            }

            if (chrPrv == ' ') {       // current state is whiteSpace; find a letter to change it.
                if (isAsciiLowerCaseLetter(chr_j)) {
                    chr[kEnd++] = chrPrv = chr_j;
                }
            }
            else {      // current state is NOT whiteSpace; find a word-terminator to change it.
                if (isAsciiLowerCaseLetter(chr_j)) {
                    chr[kEnd++] = chr_j;
                }
                else {                           // Found a word-terminator.
                    chr[kEnd++] = chrPrv = ' ';    // Replace substring of non-letters with a single SPACE char.
                }
            }
        }
        return Arrays.copyOf(chr, kEnd);
    }  

    public static char[] toLowerCaseLettersAndSingleSpaces_twoStateB(char[] chr)
    {
        char chrPrv = ' ';  // SPACE char represents the state whiteSpace
        int kEnd = 0, length = chr.length;
        for (int j = 0; j < length; j++) {
            char chr_j = chr[j];

            // Replace the following characters with nothing, regardless of state.
            // Swallowing these characters by not incrementing k works only because
            // we're copying every character we keep from the input to the output.
            if (chr_j == '-' || chr_j == '\'' || chr_j == '_' || Character.isDigit(chr_j))
                continue;

            if (isAsciiUpperCaseLetter(chr_j)) {
                chr_j += sLowerCaseOffset;
            }

            if (isAsciiLowerCaseLetter(chr_j)) {
                chr[kEnd++] = chrPrv = chr_j;
            } else if (chrPrv != ' ') {       // current state is whiteSpace; find a letter to change it.
                chr[kEnd++] = chrPrv = ' ';
            }
        }
        return Arrays.copyOf(chr, kEnd);
    }

    protected static int test_toLowerCaseLettersAndSingleSpaces(String lines[], int level) 
    {
        for (String line : lines) {
            String cvrt = toLowerCaseLettersAndSingleSpaces(line);
            if (cvrt != null) {
                cvrt.trim();
                if (cvrt.length() > 0) {
                    String strs[] = cvrt.split(" ");
                    Sx.putsArray(strs, " ==> " + strs.length + " words");
                    for (String ss : strs) {
                        ss.trim();
                        if (ss.length() > 0) {
                            Sx.puts("[" + ss + "]");
                        }
                    }
                }
            }
        }
        return 0;
    }

    public static List<char[]> collectLowerCaseLetterWordsIntoList(char[] chr, int length)
    {
        ArrayList<char[]> wordList = new ArrayList<char[]>();
        collectLowerCaseLetterWords(wordList, chr, length);
        return wordList;
    }

 
    /**
     * Extracts "words" -- that is, word-boundary delimited strings --
     * from a char array, converting them to lower case.  These "words"
     * are not checked against any dictionary or rules, other than being
     * delimited by the beginning or end of the array or by non-word-forming
     * characters, such as whitespace or punctuation.
     * 
     * Parses input char array using two states for greater efficiency.
     * The two states are: whiteSpace or not, or, in-between-words vs. inside-a-word.
     * Note that any hyphen, apostrophe, and underscore characters (- ' _) are discarded 
     * whether they appear inside or outside a word.  Thus "forty-one" becomes "fortyone", 
     * "don't" becomes "dont", and "God's will" becomes "Gods will".
     * 
     * TODO: Make sure that any two hyphen or punctuation characters do terminate words:
     * don't -> dont
     * Don''told''me  ->  Don told me
     * Don--tell'm "Go fish" ->  Don tellm Go fish
     *   
     * @param <T>    String collector type
     * @param words  String collector
     * @param chr    The char array that may contain words.
     * @param length The number of chars in chr to process.
     * @return       The number of words actually kept by the collector.
     *               If the collector discards duplicates, the number kept
     *               may be less than the number of words found.
     */
    public static int collectLettersOnlyWords(Collection<String> words, char[] chr, int length)
    {
        int numWordsAdded = 0;
        char chrPrv = ' ';  // space
        int kBeg = 0, kEnd = 0;
        for (int j = 0; j < length; j++) {
            char chr_j = chr[j];

            // If current state is whiteSpace, find any letter to begin a new word.
            if (chrPrv == ' ') {
                if (isAsciiLetter(chr_j)) {
                    kBeg = j;
                    kEnd = j + 1;
                    chrPrv = chr_j;
                }
            }
            // If current state is a letter, omit select interior non-letters
            else if (isAsciiLetter(chrPrv)) {
                if (isAsciiLetter(chr_j)) {
                    chr[kEnd++] = chrPrv = chr_j;
                }
                // Inside a word, replace the following characters with nothing
                else if (chr_j == '-' || chr_j == '\'' || chr_j == '_' || Character.isDigit(chr_j)) {
                    chrPrv = chr_j;
                }
                else {
                    String str = new String(chr, kBeg, kEnd - kBeg);
                    if (words.add(str)) {
                        numWordsAdded++;
                    }
                    chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
                }
            }
            else {  // previous char must have been in the ignorable set [-'_]
                if (isAsciiLetter(chr_j)) {
                    chr[kEnd++] = chrPrv = chr_j;
                }
                else {
                    String str = new String(chr, kBeg, kEnd - kBeg);
                    if (words.add(str)) {
                        numWordsAdded++;
                    }
                    chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
                }
            }
        }
        // If the input ended in non-whiteSpace, add this last word.
        if (chrPrv != ' ') {
            String str = new String(chr, kBeg, kEnd - kBeg);
            if (words.add(str)) {
                numWordsAdded++;
            }
        }
        return numWordsAdded;
    }

    
    /**
     * Extracts "words" -- that is, word-boundary delimited strings --
     * from a char array, converting them to lower case.  These "words"
     * are not checked against any dictionary or rules, other than being
     * delimited by the beginning or end of the array or by non-word-forming
     * characters, such as whitespace or punctuation.
     * 
     * NB: Contrary to toLowerCaseLettersAndSingleSpaces_twoState, which 
     * copies all kept characters from input to output, here the hyphen,
     * apostrophe, and underscore characters terminate a word.  For example, 
     * "it's re-bar" would be parsed into "it", "s", "re", and "bar".
     * If the actual collector keeps only words of length > 2, then only "bar" 
     * would survive.
     * @param <T>    String collector type
     * @param words  String collector
     * @param chr    The char array that may contain words.
     * @param length The number of chars in chr to process.
     * @return       The number of words actually kept by the collector.
     *               If the collector discards duplicates, the number kept
     *               may be less than the number of words found.
     */
    public static <T extends StringCollectorInterface<?>> int collectLowerCaseLetterWords(T words, char[] chr, int length)
    {
        int numWordsAdded = 0;
        char chrPrv = ' ';  // space
        int kBeg = 0, kEnd = 0;
        for (int j = 0; j < length; j++) {
            char chr_j = chr[j];

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
                    //if (words.addString(Arrays.copyOfRange(chr, kBeg, kEnd)))
                    if (words.addString(chr, kBeg, kEnd)) {
                        numWordsAdded++;
                    }
                    chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
                }
            }
        }
        // If the input ended in non-whiteSpace, add this last word.
        if (chrPrv != ' ' && words.addString(chr, kBeg, kEnd))
            numWordsAdded++;
        return numWordsAdded;
    }

    public static <T extends StringCollectorInterface<?>> int collectLowerCaseLetterWords(T words, String str)
    {
        int numWordsAdded = 0;
        char chrPrv = ' ';  // space
        int kBeg = 0, kEnd = 0, strLen = str.length();
        for (int j = 0; j < strLen; j++) {
            char chr_j = str.charAt(j);

            // If current state is whiteSpace, find a letter to change it.
            if (chrPrv == ' ') {
                if ((isAsciiLowerCaseLetter(chr_j)) || (isAsciiUpperCaseLetter(chr_j))) {
                    kBeg = j;
                    kEnd = j + 1;
                    chrPrv = chr_j;
                }
            }
            else {
                if ((isAsciiLowerCaseLetter(chr_j)) || (isAsciiUpperCaseLetter(chr_j))) {
                    kEnd++;
                }
                else {
                    if (words.addString(str.substring(kBeg, kEnd).toLowerCase())) {
                        numWordsAdded++;
                    }
                    chrPrv = ' ';    // Covert substring of anything else to a single SPACE char.
                }
            }
        }
        // If the input ended in non-whiteSpace, add this last word.
        if (chrPrv != ' ' && words.addString(str.substring(kBeg, kEnd).toLowerCase()))
            numWordsAdded++;
        return numWordsAdded;
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
     * TODO: return num added?
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

    public static int test_toLowerCaseLetterWords(String lines[]) 
    {
        for (String line : lines) {
            char charray[] = line.toCharArray();
            int length = charray.length;
            if (length > 0) {
                List<char[]> wordList = collectLowerCaseLetterWordsIntoList(charray, length);
                if (! wordList.isEmpty()) {    
                    Sx.putsListOfCharray(wordList, " ==> " + wordList.size() + " words");
                    for (char [] word : wordList) {
                        Sx.puts("[" + new String(word) + "]");
                    }
                }
            }
        }  
        return 0;
    }
    

    public static String[] toWordArray(String text) {
        String conv = toLowerCaseLettersAndSingleSpaces(text);
        if (conv.isEmpty()) {
            return new String[0];
        }
        return conv.split(" ");
    }

    public static int test_toWordArray(String text) {
        Sx.puts("primitive test on this text: (" + text + ")");
        String conv = toLowerCaseLettersAndSingleSpaces(text);
        if (conv.length() > 0) {
            String words[] = conv.split(" ");
            Sx.putsArray(words);
            return words.length;
        }
        return 0;
    }


    /** Amount of output increases with level */
    public static int unit_test(int level) 
    {
        String testName = TextFilters.class.getName() + ".unit_test";
        Sz.begin(testName);

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
        int numWrong = 0;
        if (level > 0) {
            numWrong += test_toLowerCaseLettersAndSingleSpaces(lines, 1);
            Sx.puts("+++++++++++++++++++++++++++++++++++++++++++++");
            numWrong += test_toLowerCaseLetterWords(lines);
        }

        String text = "  What in \"dog's\" name is all this--? --well, it's @#^&$* tom=foolery!  ";
        int numWords = test_toWordArray(text);
        Sx.puts("test_toWordArray returned " + numWords);
        
        text = " [ ] ";
        numWords = test_toWordArray(text);
        Sx.puts("test_toWordArray returned " + numWords);
        
        text = "";
        numWords = test_toWordArray(text);
        Sx.puts("test_toWordArray returned " + numWords);
        
        String[] arg = text.split(" ");
        Sx.putsArray(arg);
        
        String arr[] = new String[0];
        Sx.putsArray(arr);
        
        Sz.end(testName, numWrong);
        return numWrong;
    } 

    public static void main(String[] args) {
        unit_test(1);
    }

}
