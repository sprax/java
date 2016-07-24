package sprax.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * StringCollector with unspecified container type.
 * 
 * @param <T> Some kind of "container" class, loosely construed. For example, a StringBuffer,
 *            CharacterBuffer, or Trie.
 */
abstract class AbstractStringCollector<T> implements StringCollectorInterface<T>
{
    protected final T mCollector; // The constructor sets the collector member (like a const ref in
                                  // C++)
    
    AbstractStringCollector(T t)
    { 
        // The collector must be passed in, since the unknown type T cannot
        mCollector = t; // be instantiated; that is, mCollector = new T() cannot compile.
    }
    
    @Override
    public T getCollector()
    { 
        // public accessor for read-only collector;
        return mCollector; // do not define setCollector.
    }
}

/**
 * StringCollector with a Collection for its container type.
 * 
 * @param <T> Some type extending Collection<String>, e.g. HashSet<String>
 */
abstract class LowerCaseWordCollector<T extends Collection<char[]>> implements
        StringCollectorInterface<T>
{
    protected final T mCollector; // The constructor sets the collector member (like a const ref in
                                  // C++)
    
    LowerCaseWordCollector(T t)
    { 
        // The collector must be passed in, since the unknown type T cannot
        mCollector = t; // be instantiated; that is, mCollector = new T() cannot compile.
    }
    
    @Override
    public T getCollector()
    { 
        // public accessor; do not define setCollector.
        return mCollector;
    }
    
    @Override
    public boolean addString(String str)
    { 
        // Return true IFF the set did not already contain this string
        return TextFilters.collectLowerCaseLetterWords(mCollector, str.toCharArray(), str.length());
    }
    
    /**
     * Extracts "words" -- that is, word-boundary delimited strings -- from a char array, converting
     * them to lower case. These "words" are not checked against any dictionary or rules, other than
     * being delimited by the beginning or end of the array or by non-word-forming characters, such
     * as whitespace or punctuation.
     * 
     * @param beg Ignored
     * @param end Words in <code>chr</code> are extracted up to this index.
     */
    @Override
    public boolean addString(char[] chr, int beg, int end)
    {
        return TextFilters.collectLowerCaseLetterWords(mCollector, chr, end);
    }
    
    @Override
    public boolean contains(final String str)
    {
        return mCollector.contains(str);
    }
    
    @Override
    public int size()
    {
        return mCollector.size();
    }
}

abstract class WordCollectorStr<T extends Collection<String>> implements
        StringCollectorInterface<T>
{
    protected final T mCollector; // The constructor sets the collector member (like a const ref in
                                  // C++)
    
    WordCollectorStr(T t)
    { 
        // The collector must be passed in, since the unknown type T cannot
        mCollector = t; // be instantiated; that is, mCollector = new T() cannot compile.
    }
    
    @Override
    public T getCollector()
    { 
        // public accessor; do not define setCollector.
        return mCollector;
    }
    
    @Override
    public boolean addString(String str)
    {
        // Return true IFF the set did not already contain this string
        int numAdded = TextFilters.collectLettersOnlyWords(mCollector, str, str.length());
        return numAdded > 0;
    }
    
    /**
     * Extracts "words" -- that is, word-boundary delimited strings -- from a char array, converting
     * them to lower case. These "words" are not checked against any dictionary or rules, other than
     * being delimited by the beginning or end of the array or by non-word-forming characters, such
     * as whitespace or punctuation.
     * 
     * @param beg Ignored
     * @param end Words in <code>chr</code> are extracted up to this index.
     * @deprecated // TODO: reorganize: distinguish addLine and addWord, no more addString?
     */
    @Override
    public boolean addString(char[] chrs, int beg, int end)
    {
        String str = new String(chrs);
        int numAdded = TextFilters.collectLettersOnlyWords(mCollector, str, end - beg);//FIXME: word vs. line
        return numAdded > 0;
    }
    
    @Override
    public boolean contains(final String str)
    {
        return mCollector.contains(str);
    }
    
    @Override
    public int size()
    {
        return mCollector.size();
    }
}

abstract class LowerCaseWordCollectorStr<T extends Collection<String>> implements
        StringCollectorInterface<T>
{
    protected final T mCollector; // The constructor sets the collector member (like a const ref in
                                  // C++)
    
    LowerCaseWordCollectorStr(T t)
    { 
        // The collector must be passed in, since the unknown type T cannot
        mCollector = t; // be instantiated; that is, mCollector = new T() cannot compile.
    }
    
    @Override
    public T getCollector()
    { 
        // public accessor; do not define setCollector.
        return mCollector;
    }
    
    @Override
    public boolean addString(String str)
    { 
        // Return true IFF the set did not already contain this string
        return TextFilters.collectLowerCaseLetterWords(mCollector, str);
    }
    
    /**
     * Extracts "words" -- that is, word-boundary delimited strings -- from a char array, converting
     * them to lower case. These "words" are not checked against any dictionary or rules, other than
     * being delimited by the beginning or end of the array or by non-word-forming characters, such
     * as whitespace or punctuation.
     * 
     * @param beg Ignored
     * @param end Words in <code>chr</code> are extracted up to this index.
     */
    @Override
    public boolean addString(char[] chr, int beg, int end)
    {
        return TextFilters.collectLowerCaseLetterWords(mCollector, new String(chr, beg, end - beg));
    }
    
    @Override
    public boolean contains(final String str)
    {
        return mCollector.contains(str);
    }
    
    @Override
    public int size()
    {
        return mCollector.size();
    }
}

/**
 * StringCollector with a Map of Strings to Integers for its container type. By default, the Integer
 * value counts the number of times the String key was added using addString.
 * 
 * @param <T> Some type extending Map<String, Integer>, e.g. HashMap<String, Integer>
 */
abstract class MapStringCollector<T extends Map<String, Integer>> implements
        StringCollectorInterface<T>
{
    protected final T mCollector; // The constructor sets the collector member (like a const ref in
                                  // C++)
    
    MapStringCollector(T t)
    { // The collector must be passed in, since the unknown type T cannot
        mCollector = t; // be instantiated; that is, mCollector = new T() cannot compile.
    }
    
    @Override
    public T getCollector()
    { // public accessor; do not define setCollector.
        return mCollector;
    }
    
    @Override
    public boolean addString(String str)
    { // Return true IFF the set did not already contain this string
        Integer count = mCollector.get(str);
        if (count != null) { // previously added word
            mCollector.put(str, count + 1);
            return false;
        }
        mCollector.put(str, 1); // new word
        return true;
    }
    
    @Override
    public boolean addString(char[] chr, int beg, int end)
    {
        return addString(new String(chr, beg, end - beg));
    }
    
    @Override
    public boolean contains(final String str)
    {
        return mCollector.containsKey(str);
    }
    
    @Override
    public int size()
    {
        return mCollector.size();
    }
}

// public static int unit_test(final String[] args)
// {
// return TextFileReader.unit_test(args);
// }
// public static void main(final String[] args) { unit_test(args); }

// ////////////////////////// LISTS and SETS ///////////////////////////////////

class ArrayListStringCollector extends StringCollection<ArrayList<String>>
{
    public ArrayListStringCollector()
    {
        super(new ArrayList<String>());
    }
}

class ArrayListLowerCaseWordCollector extends LowerCaseWordCollector<ArrayList<char[]>>
{
    public ArrayListLowerCaseWordCollector()
    {
        super(new ArrayList<char[]>());
    }
}

class ArrayListWordCollectorStr extends WordCollectorStr<ArrayList<String>>
{
    public ArrayListWordCollectorStr()
    {
        super(new ArrayList<String>());
    }
}

class ArrayListLowerCaseWordCollectorStr extends LowerCaseWordCollectorStr<ArrayList<String>>
{
    public ArrayListLowerCaseWordCollectorStr()
    {
        super(new ArrayList<String>());
    }
}

class HashSetStringCollector extends StringCollection<HashSet<String>>
{
    public HashSetStringCollector()
    {
        super(new HashSet<String>());
    }
}

class TreeSetStringCollector extends StringCollection<TreeSet<String>>
{
    public TreeSetStringCollector()
    {
        super(new TreeSet<String>());
    }
}

// ////////////////////////// MAPS and TABLES //////////////////////////////////

// Moved: class HashMapStringCollector extends MapStringCollector<HashMap<String, Integer>>

class TreeMapStringCollector extends MapStringCollector<TreeMap<String, Integer>>
{
    TreeMap<String, Integer> mTreeMap;
    
    TreeMapStringCollector()
    {
        super(new TreeMap<String, Integer>());
        mTreeMap = mCollector;
    }
}

class HashtableStringCollector extends MapStringCollector<Hashtable<String, Integer>>
{
    Hashtable<String, Integer> mHashtable;
    
    public HashtableStringCollector()
    {
        super(new Hashtable<String, Integer>());
        mHashtable = mCollector;
    }
}

// ////////////////////////// BUFFERS as Containers ////////////////////////////

class StringBufferStringCollector extends AbstractStringCollector<StringBuffer>
{
    StringBuffer mStringBuffer;
    
    StringBufferStringCollector()
    {
        super(new StringBuffer());
        mStringBuffer = mCollector;
    }
    
    @Override
    public boolean addString(String str)
    { // Return true IFF str is non-empty (so adding it changes the collector)
        // TODO: trim the string?
        if (str.isEmpty()) {
            return false;
        }
        mStringBuffer.append(str).append(System.getProperty("line.separator"));
        return true;
    }
    
    @Override
    public boolean addString(char[] chr, int beg, int end)
    {
        return addString(new String(chr, beg, end - beg));
    }
    
    @Override
    public boolean contains(final String str)
    {
        return mStringBuffer.indexOf(str) >= 0;
    }
    
    @Override
    public int size()
    {
        return mStringBuffer.length();
    }
}
