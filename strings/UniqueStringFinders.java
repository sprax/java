/**
 * 
 */
package sprax.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import sprax.files.FileUtil;
import sprax.files.TextFileReader;
import sprax.lists.StrLink;
import sprax.lists.StringList;
import sprax.sprout.Sx;
import sprax.test.Sz;
import sprax.tries.WordTrie;
import sprax.tries.WordTrie.NodeCount;
import sprax.tries.WordTrie.WordNode;

///////////////////////////////////////////////////////////////////////////
///////////////////////// StringAndNumber /////////////////////////////////
///////////////////////////////////////////////////////////////////////////
class StringAndNumber
{
    String  string;
    int     number;
    StringAndNumber(String s, int n) {
        string = s;
        number = n;
    }
    public String toString() {
        return "[" + string + " " + number + "]";
    }
} // END of non-public class StringNumber


/**
 * @author slines
 * Static methods to solve problems such as finding the first
 * unique string in a collection, maximal subarray of unique
 * strings, etc.
 * 
 * Performance test conclusion:
 * Reading a text file using a large char[] array as a buffer 
 * is faster than reading it line by line into Strings.
 * 
 * However, using Strings is faster than using char[] in *all*
 * puts, gets, and contains calls into a WordTrie, even though
 * only the putWord call ever converts char[] to String, and none
 * of these methods converts String to char[].  This indicates that
 * at least in the context of these basic operations, using String
 * and String.charAt(k) is faster than using char[] and charray[k].
 */


public class UniqueStringFinders 
{
    // This is by far the fastest
    public static StrLink uniqueStringsInIterable_hashMapLink(Iterable<String> strs)
    {
        // DIY linked list to be used as a stack. 
        // If we iterate through the strs in reverse order,
        // the first unique string will end up on the top of the stack.
        StrLink head = null;
        
        // Map each string to it's link in the stack.  
        // Even if the link is deleted from the stack, it remains in this map.
        HashMap<String, StrLink> str2strLink = new HashMap<String, StrLink>();
        
        // Map all new strings to links in a list, 
        // but remove list links for duplicate strings.
        for (String str : strs) {
            StrLink strLink = str2strLink.get(str);
            if (strLink == null) {
                head =  new StrLink(str, head);
                str2strLink.put(str, head);
            } else {
                strLink.suicide();
            }
        }
        return head;
    }
    
    public static String firstUniqueStringInIterable_hashMapLink(Iterable<String> strs)
    {
        StrLink link = uniqueStringsInIterable_hashMapLink(strs);
        if (link == null)
            return  null;
        // find the tail
        while (link.mNxt != null && link.mNxt.mStr != null)
            link = link.mNxt;
        return link.mStr;
    }
    
    
    // The stupidly wrapped version of the above
    public static boolean firstUniqueStringInIterable_hashMapList(StringAndNumber san, Iterable<String> strs)
    {
        StringList strList = new StringList();
        HashMap<String, StrLink> str2strLink = new HashMap<String, StrLink>();
        
        // 1st pass: map all strings to uniqueness
        for (String str : strs) {
            StrLink strLink = str2strLink.get(str);
            if (strLink == null) {
                strLink =  strList.addFirst(str);
                str2strLink.put(str, strLink);
            } else {
                strList.remove(strLink);
            }
        }
        
        // 2nd pass: return first unique string and index
        String str = strList.head().mStr;
        if (str == null)
            return false;
        san.string = str;
        san.number = 0;
        return true;
    }
    
    static int sNumTrials  = 8;
    
    static int sTimePutStr = 0;
    static int sTimeGetStr = 0;
    static int sTimeHasStr = 0;
    
    static int sTimePutChr = 0;
    static int sTimeGetChr = 0;
    static int sTimeHasChr = 0;
    
    static int sIterPutStr = 0;
    static int sIterGetStr = 0;
    static int sIterHasStr = 0;
    
    static int sIterGetChr = 0;
    static int sIterPutChr = 0;
    static int sIterHasChr = 0;
    
    static int sAvrgGetStr;
    static int sAvrgPutStr;
    static int sAvrgHasStr;
    
    static int sAvrgGetChr;
    static int sAvrgPutChr;
    static int sAvrgHasChr;
    
    /**
     * @deprecated
     * @param strs
     * @return
     */
    public static LinkedHashSet<String> uniqueStringsSet(Iterable<String> strs)
    {
        LinkedHashSet<String> uniqueFirst = new LinkedHashSet<String>();
        for (String str : strs) {
            if (uniqueFirst.contains(str))
                uniqueFirst.remove(str);   // If found, remove str from its current place.
            uniqueFirst.add(str);        // But always and add it (back) on at the tail.
        }
        return uniqueFirst;
    }
    
    /**
     * Strings that occur an even number of times in the input will be discarded
     * from the output, leaving only those of odd frequency.  If any string occurs
     * only once, it will be the first in the output.
     * FIXME: This is wrong.
     * @param strs
     * @return
     */
    public static LinkedHashMap<String, Integer> uniqueFirstString2indexMap(Iterable<String> strs)
    {
        LinkedHashMap<String, Integer> string2freq = new LinkedHashMap<String, Integer>();
        
        // Map each string to its original index, removing duplicates as we go.
        
        for (String str : strs) {
            Integer freq = string2freq.get(str);
            if (freq != null) {
                //string2freq.remove(str);         // if found, remove str from its current place...
                string2freq.put(str, 1+freq);  // ...but always add str to the end of the ordered set.
            } else {
                string2freq.put(str, 1);  // ...but always add str to the end of the ordered set.
            }
        }
        return string2freq;
    }
    
    
    
    public static boolean firstUniqueStringInIterable_linkHashMap(StringAndNumber san, Iterable<String> strs)
    {
        LinkedHashMap<String, Integer> string2freq = uniqueFirstString2indexMap(strs);
        if (string2freq.isEmpty())
            return false;
        
        // 2nd pass: return first unique string and index
        int index = 0;
        for (String str : string2freq.keySet()) {
            if  (string2freq.get(str) == 1) {
                san.string = str;
                san.number = index;
                return true;
            }
            index++;
        }
        return false;
    }
    
    
    public static boolean firstUniqueStringInIterable_hashMap(StringAndNumber san, Iterable<String> strs)
    {
        Map<String, Boolean> str2unique = new HashMap<String, Boolean>();
        
        // 1st pass: map all strings to uniqueness
        for (String str : strs) {
            if (str2unique.containsKey(str))
                str2unique.put(str, false);
            else
                str2unique.put(str, true);
        }
        
        // 2nd pass: return first unique string and index
        int index = 0;
        for (String str : strs) {
            if  (str2unique.get(str)) {
                san.string = str;
                san.number = index;
                return true;
            }
            index++;
        }
        return false;
    }
    
    
    
    
    public static boolean firstUniqueStringInIterable_wordTrie(StringAndNumber san, Iterable<String> strs)
    {
        WordTrie trie = new WordTrie();
        
        // 1st pass: load trie
        for (String str : strs) {
            trie.putWord(str);
        }
        
        // 2nd pass: return  first word node w/ mWordCount == 1
        int index = 0;
        for (String str : strs) {
            WordTrie.WordNode node = trie.getWordNode(str);
            if (node.getWordCount() == 1) {
                san.string = str;
                san.number = index;
                return true;
            }
            index++;
        }
        return false;
    }
    
    public static boolean firstUniqueStringInIterable_wordTrieMap(StringAndNumber san, Iterable<String> strs)
    {
        WordTrie trie = new WordTrie();
        Map<String, WordNode> str2node = new HashMap<String, WordNode>();
        
        // 1st pass: load trie
        for (String str : strs) {
            WordNode node = trie.putWord(str);
            if ( ! str2node.containsKey(str))
                str2node.put(str, node);
        }
        
        // 2nd pass: return  first word node w/ mWordCount == 1
        int index = 0;
        for (String str : strs) {
            WordNode node = str2node.get(str);
            if (node.getWordCount() == 1) {
                san.string = str;
                san.number = index;
                return true;
            }
            index++;
        }
        return false;
    }
    
    
    public static NodeCount time_firstUniqueWordNodeInIterableStr(Iterable<String> words)
    {
        WordTrie trie = new WordTrie();
        
        // 1st pass: load trie
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < sNumTrials; j++)
            for (String word : words) {
                trie.putWord(word);
            }
        long endTime = System.currentTimeMillis();
        long putTime = endTime - begTime;
        sTimePutStr += putTime;
        sIterPutStr += sNumTrials;
        sAvrgPutStr  = sTimePutStr/sIterPutStr;
        
        // 2nd pass: return  first word node w/ mWordCount == 1
        int index = 0;
        WordNode node = null;
        begTime = System.currentTimeMillis();
        for (int j = 0; j < sNumTrials; j++)
            for (String word : words) {
                node = trie.getWordNode(word);
                if (node.getWordCount() == 1)
                    return trie.new NodeCount(node, index);
                index++;
            }
        endTime = System.currentTimeMillis();
        long getTime = endTime - begTime;
        sTimeGetStr += getTime;
        sIterGetStr += sNumTrials;
        sAvrgGetStr  = sTimeGetStr/sIterGetStr;
        
        // 3rd pass: useless test of hasWord
        boolean does = false;
        begTime = System.currentTimeMillis();
        for (int j = 0; j < sNumTrials; j++)
            for (String word : words) {
                does = trie.contains(word);
            }
        endTime = System.currentTimeMillis();
        long hasTime = endTime - begTime;
        sTimeHasStr += hasTime;
        sIterHasStr += sNumTrials;
        sAvrgHasStr  = sTimeHasStr/sIterHasStr;
        
        if (does)
            Sx.format("String %d words:  put %4d  get %4d  has %4d (avg  %3d  %3d  %3d)\n"
                    , trie.getNumWords()*sNumTrials, putTime, getTime, hasTime
                    , sAvrgPutStr, sAvrgGetStr, sAvrgHasStr);
        
        return null;
    }
    
    public static NodeCount time_firstUniqueWordNodeInIterableChr(Iterable<char[]> words)
    {
        WordTrie trie = new WordTrie();
        
        // 1st pass: load trie
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < sNumTrials; j++)
            for (char[] word : words) {
                trie.putWord(word, 0, word.length);
            }
        long endTime = System.currentTimeMillis();
        long putTime = endTime - begTime;  
        sTimePutChr += putTime;
        sIterPutChr += sNumTrials;
        sAvrgPutChr  = sTimePutChr/sIterPutChr;
        
        // 2nd pass: return  first word node w/ mWordCount == 1
        int index = 0;
        WordNode node = null;
        begTime = System.currentTimeMillis();
        for (int j = 0; j < sNumTrials; j++)
            for (char[] word : words) {
                node = trie.getWordNode(word);
                if (node.getWordCount() == 1)
                    return trie.new NodeCount(node, index);
                index++;
            }
        endTime = System.currentTimeMillis();
        long getTime = endTime - begTime;
        sTimeGetChr += getTime;
        sIterGetChr += sNumTrials;
        sAvrgGetChr  = sTimeGetChr/sIterGetChr;
        
        // 3rd pass: useless test of hasWord
        boolean does = false;
        begTime = System.currentTimeMillis();
        for (int j = 0; j < sNumTrials; j++)
            for (char[] word : words) {
                does = trie.containsWord(word);
            }
        endTime = System.currentTimeMillis();
        long hasTime = endTime - begTime;
        sTimeHasChr += hasTime;
        sIterHasChr += sNumTrials;
        sAvrgHasChr  = sTimeHasChr/sIterHasChr;
        
        if (does)
            Sx.format("char[] %d words:  put %4d  get %4d  has %4d (avg  %3d  %3d  %3d)\n"
                    , trie.getNumWords()*sNumTrials, putTime, getTime, hasTime
                    , sAvrgPutChr, sAvrgGetChr, sAvrgHasChr);    
        
        return null;
    }
    
    public static boolean time_firstUniqueWordInIterableStr(StringAndNumber san, Iterable<String> words)
    {
        NodeCount nc = time_firstUniqueWordNodeInIterableStr(words);
        if (nc == null)
            return false;
        san.string = nc.mNode.getWord();
        san.number = nc.mCount;
        return true;
    }
    
    
    public static boolean time_firstUniqueWordInIterableChr(StringAndNumber san, Iterable<char[]> words)
    {
        NodeCount nc = time_firstUniqueWordNodeInIterableChr(words);
        if (nc == null)
            return false;
        san.string = nc.mNode.getWord();
        san.number = nc.mCount;
        return true;
    }
    
    
    public static int test_time_firstUniqueWordChr(final List<char[]> words, int numTrials)
    {
        int numWordsC = words.size();
        StringAndNumber san = new StringAndNumber(null, 0);
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < numTrials; j++)
            time_firstUniqueWordInIterableChr(san, words);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - begTime;  
        
        if (san.string == null) {
            Sx.puts("test_firstUniqueWordInIterableChr: none of the "
                    + numWordsC + " words are unique!  " + runTime);
            return -3;
        } else {
            Sx.format("firstUniqueWordInIterableChr got: %s (index %d/%d) in %d MS\n"
                    , san.string, san.number, numWordsC, runTime);
        }
        return 0;
    }
    
    
    public static List<String> test_makeWordListStr(String textFilePath, int numTrials)
    {
        ArrayList<String> words = null;
        int numWordsA = 0;
        
        // Read in all the words from the text file.
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < numTrials; j++)
            words = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(textFilePath);      
        long endTime = System.currentTimeMillis();
        long runTime = endTime - begTime;  
        numWordsA = words.size();
        if (numWordsA == 0) {
            Sx.puts(textFilePath + ": <file empty>");
            return words;
        }
        Sx.format("String: read %d String words in %d MS\n", numWordsA, runTime);
        
        // Add a would-be unique word 3 times:
        String thrice = "blrrrch";
        boolean added = words.add(thrice);
        int numWordsB = words.size();
        if ( ! added || numWordsB != numWordsA + 1) {
            Sx.puts("Unique word already added!  " + thrice);
            return words;
        }
        added = words.add(thrice);
        added = words.add(thrice);
        
        // Add a unique word
        String unique = "wysiwyg";
        added = words.add(unique);
        numWordsB = words.size();
        if ( ! added || numWordsB != numWordsA + 4) {
            Sx.puts("Unique word already added!  " + unique);
            return words;
        }
        
        // Duplicate all the other words!
        ArrayList<String> temps = TextFileReader.readFileIntoArrayListOfLowerCaseWordsStr(textFilePath);
        words.addAll(temps);
        return words;
    }
    
    public static ArrayList<char[]> test_makeWordListChr(String textFilePath, int numTrials)
    {
        ArrayList<char[]> words = null;
        int numWordsA = 0;
        
        // Read in all the words from the text file.
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < numTrials; j++)
            words = TextFileReader.readFileIntoArrayListOfLowerCaseWordsChr(textFilePath);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - begTime; 
        numWordsA = words.size();
        if (numWordsA == 0) {
            Sx.puts(textFilePath + ": <file empty>");
            return words;
        }
        Sx.format("char[]: read %d char[] words in %d MS\n", numWordsA, runTime);
        
        // Add a unique word
        String unique = "wysiwyg";
        boolean added = words.add(unique.toCharArray());
        int numWordsB = words.size();
        if ( ! added || numWordsB != numWordsA + 1) {
            Sx.puts("Unique word already read!  " + unique);
            return words;
        }
        
        // Duplicate all the other words!
        ArrayList<char[]> temps = TextFileReader.readFileIntoArrayListOfLowerCaseWordsChr(textFilePath);
        words.addAll(temps);
        return words;
    }
    
    public static int test_time_firstUniqueWordStr(final List<String> words, int numTrials)
    {
        int numWordsC = words.size();
        StringAndNumber san = new StringAndNumber(null, 0);
        long begTime = System.currentTimeMillis();
        for (int j = 0; j < numTrials; j++)
            time_firstUniqueWordInIterableStr(san, words);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - begTime;  
        
        if (san.string == null) {
            Sx.puts("test_firstUniqueWordInIterableStr: none of the "
                    + numWordsC + " words are unique!  " + runTime);
            return -3;
        } else {
            Sx.format("firstUniqueWordInIterableStr got: %s (index %d/%d) in %d MS\n"
                    , san.string, san.number, numWordsC, runTime);
        }
        return 0;
    }
    
    public static int unit_test(int level)
    {
        String  testName = UniqueStringFinders.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        ArrayList<String> straw = new ArrayList<String>();
        straw.add("abc");
        String word = firstUniqueStringInIterable_hashMapLink(straw);
        assert(word.equals("abc"));
        straw.add("abc");    
        word = firstUniqueStringInIterable_hashMapLink(straw);
        assert(word == null);
        straw.add("first");
        straw.add("last");    
        straw.add("abc");    
        word = firstUniqueStringInIterable_hashMapLink(straw);
        assert(word.equals("first"));
        
        ListIterator<String> lit = straw.listIterator(straw.size());
        Sx.putsReversed(lit);
        
        if (level > 1) {
            String textFilePath = FileUtil.getTextFilePath("ReadMe.txt");
            int  numTrials = 10;
            long begTime, endTime, mapTime, wrdTime, wrmTime, lhmTime, hmlTime, hmsTime;
            long setTime, totTime = 0;
            
            List<String> wordsStr = test_makeWordListStr(textFilePath, numTrials);
            List<char[]> wordsChr = test_makeWordListChr(textFilePath, numTrials);    
            StringAndNumber sanMap = new StringAndNumber(null, 0);
            StringAndNumber sanWrd = new StringAndNumber(null, 0);
            StringAndNumber sanWrm = new StringAndNumber(null, 0);
            StringAndNumber sanLhm = new StringAndNumber(null, 0);
            //StringAndNumber sanHmL = new StringAndNumber(null, 0);
            StringAndNumber sanHml = new StringAndNumber(null, 0);
            
            // The uselessly wrapped version of the above
            begTime = System.currentTimeMillis();
            boolean lhFound = false;
            for (int j = 0; j < numTrials; j++) {
                lhFound = firstUniqueStringInIterable_hashMapList(sanHml, wordsStr);
                numWrong += Sz.oneIfFalse(lhFound);
            }
            endTime  = System.currentTimeMillis();
            hmlTime  = endTime - begTime;
            totTime += hmlTime;
            
            // This is by far the fastest
            begTime = System.currentTimeMillis();
            String linkStr = null;
            for (int j = 0; j < numTrials; j++) {
                linkStr = firstUniqueStringInIterable_hashMapLink(wordsStr);
            }
            endTime  = System.currentTimeMillis();
            hmsTime  = endTime - begTime;
            totTime += hmsTime;
            
            begTime = System.currentTimeMillis();
            for (int j = 0; j < numTrials; j++) {
                boolean hmFound = firstUniqueStringInIterable_hashMap(sanMap, wordsStr);
                numWrong += Sz.oneIfFalse(hmFound);
            }
            endTime  = System.currentTimeMillis();
            mapTime  = endTime - begTime;
            totTime += mapTime;
            
            begTime = System.currentTimeMillis();
            for (int j = 0; j < numTrials; j++) {
                boolean wtFound = firstUniqueStringInIterable_wordTrie(sanWrd, wordsStr);
                numWrong += Sz.oneIfFalse(wtFound);
            }
            endTime  = System.currentTimeMillis();
            wrdTime  = endTime - begTime;
            totTime += wrdTime;
            
            begTime = System.currentTimeMillis();
            for (int j = 0; j < numTrials; j++) {
                boolean wmFound = firstUniqueStringInIterable_wordTrieMap(sanWrm, wordsStr);
                numWrong += Sz.oneIfFalse(wmFound);
            }
            endTime  = System.currentTimeMillis();
            wrmTime  = endTime - begTime;
            totTime += wrmTime;
            
            begTime = System.currentTimeMillis();
            for (int j = 0; j < numTrials; j++) {
                lhFound = firstUniqueStringInIterable_linkHashMap(sanLhm, wordsStr);
            }
            endTime  = System.currentTimeMillis();
            lhmTime  = endTime - begTime;
            totTime += lhmTime;
            
            begTime = System.currentTimeMillis();
            LinkedHashSet<String> linkedSet = null;
            for (int j = 0; j < numTrials; j++) {
                linkedSet = uniqueStringsSet(wordsStr);
            }
            String setStr = linkedSet.iterator().next();
            endTime  = System.currentTimeMillis();
            setTime  = endTime - begTime;
            totTime += setTime;
            
            Sx.format("firstUniqueString:  %d trials\n", numTrials);
            Sx.format("    hmsTime %d hmlTime %d  mapTime %d  wrdTime %d  wrmTime %d  lhmTime %d  setTime %d\n"
                    , hmsTime,   hmlTime,    mapTime,    wrdTime,    wrmTime,    lhmTime,    setTime);
            Sx.format("    %s %s %s %s %s %s %s\n"
                    , linkStr, sanHml, sanMap, sanWrd, sanWrm, sanLhm, setStr);
            
            test_time_firstUniqueWordStr(wordsStr, numTrials);
            test_time_firstUniqueWordChr(wordsChr, numTrials);
            test_time_firstUniqueWordStr(wordsStr, numTrials);
            test_time_firstUniqueWordChr(wordsChr, numTrials);
            
            Sx.puts(testName + " . . . end (" + totTime + ")");
        }
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {  
        unit_test(2);
    }
}
