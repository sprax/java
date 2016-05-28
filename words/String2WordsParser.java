/**
 * @class  String2WordsParser.java
 * @author sprax
 */
package sprax.words;

import java.util.ArrayList;
import java.util.HashSet;

import sprax.Sx;
import sprax.Sz;
import sprax.files.FileUtil;
import sprax.files.TextFileReader;


public class String2WordsParser {

    static       int sWordCount = 0;
    static final int sMinWordLength =  2;
    static final int sMaxWordLength = 28;

    HashSet<String>	mWords;

    public String2WordsParser() {
        mWords = new HashSet<String>();
    }

    public boolean isWord(String st) {
        return mWords.contains(st);
    }

    public String parseToString(String string) {
        return parseToString(string, "");
    }

    private String parseToString(String string, String wordsAlreadyParsed) {
        //  Recursively divide a string into array of words, else return nil 
        System.out.println("string2words: " + string + " / " + wordsAlreadyParsed);

        //  If this string is a word, just return it with any words already parsed.
        if (isWord(string)) {
            if (wordsAlreadyParsed.isEmpty()) {
                return string;
            }
            return string + " " + wordsAlreadyParsed;
        }

        //	Else divide the string into two parts, and if the 2nd part is a word, keep going.
        //  Use min and max word lengths to skip checking substrings that cannot be words.
        int maxIndex = string.length();
        int minIndex = maxIndex - sMaxWordLength;
        if (minIndex < 0) { 
            minIndex = 0;
        }
        maxIndex -= sMinWordLength;
        while (maxIndex > minIndex) {
            String substr = string.substring(maxIndex);
            if (isWord(substr)) {
                if ( ! wordsAlreadyParsed.isEmpty() ) {
                    substr += " " + wordsAlreadyParsed;
                }
                String moreWords = parseToString(string.substring(0, maxIndex), substr);
                if (moreWords.length() > 0) {
                    return moreWords;
                }
            }
            maxIndex--;
        }
        return "";		// string did not completely parse into words
    }

    //  Recursively divide a string from the end into array of words, else return nil	
    public ArrayList<String> parseToArray(String string) {
        return parseToArray(string, new ArrayList<String>());
    }

    private ArrayList<String> parseToArray(String string, ArrayList<String> wordsAlreadyParsed) {

        System.out.println("parseToArray: " + string + " / " + wordsAlreadyParsed);

        //  If this string is a word, just return it with any words already parsed.
        if (isWord(string)) {
            wordsAlreadyParsed.add(0, string);
            return wordsAlreadyParsed;
        }

        //	Else divide the string into two parts, and if the 2nd part is a word, keep going.
        //  Use min and max word lengths to skip checking substrings that cannot be words.
        int maxIndex = string.length();
        int minIndex = maxIndex - sMaxWordLength;
        if (minIndex < 0) { 
            minIndex = 0;
        }
        maxIndex -= sMinWordLength;
        while (maxIndex > minIndex) {
            String substr = string.substring(maxIndex);
            if (isWord(substr) && null != parseToArray(string.substring(0, maxIndex))) {
                wordsAlreadyParsed.add(0, substr);
                ArrayList<String> moreWords = parseToArray(string.substring(0, maxIndex), wordsAlreadyParsed);
                if (moreWords != null && moreWords.size() > 0) {
                    return moreWords;
                }
            }
            maxIndex--;
        }

        return null;		// string did not completely parse into words
    }

    public static int unit_test(String[] args)
    {
        
        String testName = String2WordsParser.class.getName() + ".unit_test";
        Sz.begin(testName);

        String2WordsParser parser = new String2WordsParser();
        if (args.length > 0) {
            final String textFilePath = FileUtil.getTextFilePath("words.txt");
            parser.mWords = TextFileReader.readFileIntoHashSet(textFilePath);
        } else {
            parser.mWords.add("one");
            parser.mWords.add("two");
            parser.mWords.add("three");
            parser.mWords.add("four");
            parser.mWords.add("five");
            parser.mWords.add("fi");
            parser.mWords.add("vet");
            parser.mWords.add("wo");
            System.out.println("one : " + (parser.isWord("one") ? "yes" : "no") );
            System.out.println("six : " + (parser.isWord("six") ? "yes" : "no") );
        }

        String inputString = "threefourfiveonetwo";

        String parsedToString = parser.parseToString(inputString);
        System.out.println("parsed to String: \"" + parsedToString + "\"");

        ArrayList<String> parsedToArrayList = parser.parseToArray(inputString);
        System.out.println("parsed to StringArray: " + parsedToArrayList);
        
        inputString = "onefivetwo";
        parsedToArrayList = parser.parseToArray(inputString);
        System.out.println("parsed to StringArray: " + parsedToArrayList);
        
        
        Sz.end(testName, 0);
        return 0;
    }

    public static void main(String[] args)
    { unit_test(args); }

}
