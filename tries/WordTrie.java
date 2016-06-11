//  WordTrie.java : trie representing a dictionary for word games, spell checkers, etc.
//  Sprax Lines,  July 2010

package sprax.tries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import sprax.arrays.ArrayIter;
import sprax.files.FileUtil;
import sprax.files.StringCollectorInterface;
import sprax.files.TextFilters;
import sprax.heaps.MinHeap;
import sprax.sorts.SaveMax;
import sprax.sprout.Sx;

/**
 * Trie Terminology:
 * prefix:  any search string, which might or might not match the beginning 
 *          of any words
 * key:     string that is paired with a trie value.  For a WordTrie, 
 *          the key is a word.  It's value may also be the same word, 
 *          or it may contain more or less than that word.
 * stem:    a partial key, that is, the beginning or all of a key.  
 *          The point is, the trie does contain keys that begin with 
 *          this stem -- the trie does contain non-null next branches 
 *          that continue this stem, so if it is part of a search, do
 *          not terminate the search on this stem.
 *          
 * Typical API: put, get, delete, size, contains, keys;
 *              longestPrefixOf, keysWithPrefix, keysThatMatch (Sedgewick)
 * "Extra" API: values: could be the same set as returned by keys 
 *              isEmpty: same as size == 0, but possibly cheaper, if size() is "lazy"
 * Actual API implemented here, with missing/omitted methods in brackets: 
 *              putWord, [getWord], [deleteWord], getSize, contains 
 *              [i.e. containsWord], getAllWords, longestPrefix
 *              DONE: longestSubPrefix? -- longest proper prefix...
 *              
 * This implementation mostly favors convenience and speed over compactness and 
 * miserly memory usage.  Every node maintains a linked list of its children for
 * quick iteration, and the memory used for storing words as strings holds some 
 * redundancies.  Only whole words are stored (not every prefix), but every prefix 
 * that is also a word is held separately (unless the compiler is very clever).  
 * For example, we would not store the prefix "hatc" as a string separate from 
 * word "hatch", but we would store "hatch," "hatcher," and "hatchers"
 * as separate strings, because they are all words.  A more efficient storage 
 * policy might be to keep only the longest word containing "hatch" as a prefix
 * ("hatchabilities"), or the last one alphabetically ("hatchways").  More
 * cleverly, we could store "hatch" as a linguistic stem (not merely as an
 * orthographical stem), and keep derived words such as "hatchable", "hatched",
 * "hatcher," "hatching", and "hatches" (and even "hatchery"?) as stem + suffix.
 * (Keeping the alphabetically last instance corresponds to initializing from 
 * a dictionary in backwards alphabetical order.)  In any of these schemes, 
 * getWord would entail getting a substring.
 *              
 * @author sprax
 *
 */
public class WordTrie implements StringCollectorInterface<WordTrie>
{
	public static int sDbg = 1; // TODO remove sDbg    
	int zoid, woid; // FIXME: remove

	public static final int sFirstLetter  = 'a';
	public static final int sLastLetter   = 'z';
	public static final int sNumLetters   =  1 + sLastLetter - sFirstLetter;
	public static final int sWildCardChar =  '_';		// Underscore == decimal 95, hex 5F, octal 137
	public static final int sWildCardIdx  =  sWildCardChar - sFirstLetter;	// Expect -2


	public static WordNode.CompareCounts sWordCountComparator = null;

	protected final WordNode mRoot = new WordNode('\0', null, 0); // mRoot cannot be null, so don't check for it.
	protected int mNumWords       =  0;   // total number of words stored in the trie
	protected int mNumNodes       =  1;   // total number of nodes stored in the trie
	protected int mActMinWordLen  = 99;   // Actual minimum word length (or default value), reset during loading
	protected int mActMaxWordLen  =  0;   // Actual max word length (or default value), reset during loading
	protected int mReqMinWordLen  =  1;   // Required minimum word length; smaller words not kept; change this before loading
	protected int mReqMaxWordLen  = 28;   // Required maximum word length, or max allowed len; change this before loading
	private String  mTextFilePath  = null;  // Un-set at beginning of loading; set at end.
	/** Auxiliary map for testing auto-complete, etc. TODO: keep this? */
	protected Map<String, WordNode>   mNewWordNodes = new TreeMap<String, WordNode>();
	//protected Map<WordNode, Integer>  mPathCounts   = new HashMap<WordNode, Integer>();

	protected int mNumPutChars;           // Total number of chars input when adding words
	protected int mNumNewWords;           // Reset every time we add words from a file
	protected boolean mTotalTextCountsUpToDate = false;

	public class WordNode implements Comparable<WordNode>
	{                                   // TODO: implements Iterator?
		/** All possible continuations, as an ordered set indexed by letter (an array, in fact). */
		private final WordNode mBranches[];

		/** Some word that completes this stem; only the first N chars matter, where N == depth. */
		private String         mStem;  // not final, so we can re-stem and/or "compress" post init.

		/** Level in the tree.  Since depth == prefix length, stem[depth-1] is this node's letter. */
		private final int      mDepth;

		/** The char representation of the letter at this node == mStem.charAt(mDepth-1) */
		private final char     mChar;      

		/** True IFF this node represents a word.  Regard this as a primary, 
		 *  not a derived field.  We can arrange that it be true IFF 
		 *  mDepth == mStem.length(), but that is not necessary for most uses,
		 *  and should not be assumed.  Generally it also means that this node 
		 *  is the last in a chain of nodes representing the prefixes of a word,
		 *  up to and including the word itself, but its better to regard that as
		 *  an emergent property, not part of the definition of a data structure.
		 *  This field should not be final.  If words are input not in sorted order,
		 *  a node that already exists as part of a prefix of a longer word may become
		 *  a word node itself.  The constructor can ignore it, letting it be false by 
		 *  default. */
		//        private boolean       mIsWord;

		/** Number of times this node is used in the dictionary  == times traversed when loading dictionary. */
		private int           mDictCount;
		/** Number of times this node is used in all "read" text == times traversed by WordTrie.putWord. */
		private int           mTextCount;
		private int           mTotalCount;
		private int           mWordCount; // NB: SUM{all branches' text counts} == mTextCount - mWordCount

		/** First non-NULL branch in mBranches; first as in smallest index.  
		 *  This is also the minimal child in the default sort order (alphabetical),
		 *  and the head of the singly-linked list, when mBranches is used that way.
		 *  The default order of this linked list is also alphabetical, same as the 
		 *  array order.  In this case, the linked list just provides a way of 
		 *  skipping any null-nodes when iterating.  Other orders are possible, 
		 *  such as most frequently used, most "valuable", etc. */
		private WordNode mFirstChild;

		/** The next (non-null) continuation when mBranches is used as a linked list */
		private WordNode mNextSibling;
		// private WordNode mFirstWordNode;
		/** Assumes fixed length; otherwise, the next branch depends on the desired word length */
		// private WordNode mNextJumpBranch;   // Useful for crossword puzzles, word rectangles, etc.  Not currently in use.

		/** Constructs non-root word node; parent cannot be null */
		private WordNode(char letter, final String stem, int depth, final WordNode parent) 
		{
			if (stem == null || depth < 1 || parent == null)
				throw new IllegalArgumentException();
			mBranches    = new WordNode[sNumLetters];
			mStem        = stem;
			mDepth       = depth;
			mChar        = letter;
			//mIsWord    = false;  // already false by Java default
			mFirstChild  = null;
			mNextSibling = null;

			// Add or insert new node to linked list using iteration.
			// If all additions are guaranteed to be in alphabetical order,
			// we can just append the new node to the end of the list.
			// If not, then we must insert it wherever it goes in the list,
			// preserving the previously existing next node link. 
			// Either way, we first must find the end or insertion point, 
			// which we do by iterating back from the letter key.  (Storing
			// an mLastBranch would allow us to skip the iteration when only
			// appending, but not when inserting, and it would always take up
			// the extra memory.)
			if (parent.mFirstChild == null) {    // Parent had no child until now, so
				parent.mFirstChild =  this;      // set this new node as its parent's first child.
			} else if (parent.mFirstChild.mChar < letter) {
				WordNode prevBranch, nextBranch; 
				for (int ib = letter - sFirstLetter; --ib >= 0; ) {
					if ((prevBranch = parent.mBranches[ib]) != null) {
						nextBranch = prevBranch.mNextSibling;
						prevBranch.mNextSibling = this;
						this.mNextSibling = nextBranch;
						break;
					}
				}
			} else {  // parent had at least one child, but this new one precedes them alphabetically
				this.mNextSibling = parent.mFirstChild;
				parent.mFirstChild = this;
			}
		}

		/** Constructs parent-less node, to be used only for making the root node.
		 * We could expect all-empty arguments (zero or null), but let's not make
		 * that assumption.  One can imagine partitioning a large trie into a
		 * forest of smaller ones rooted in words instead of nulls.
		 */
		private WordNode(char letter, final String stem, int depth) 
		{
			mBranches = new WordNode[sNumLetters];
			mStem     = stem;
			mDepth    = depth;
			mChar     = letter;
			//mIsWord   = false;  // already false by Java default

			mFirstChild = null;
			mNextSibling  = null;
		}

		public final WordNode getFirstBranch()  { return mFirstChild; }
		public final WordNode getNextBranch()   { return mNextSibling; } // TODO: template-ize ??
		//public final WordNode getFirstWordNode(){ return mFirstWordNode; }
		//public final String   getStem()       { return mStem; }     // First word containing this stem
		public final boolean  isWord()          { return mWordCount > 0; }   // true IFF this is a word-node
		public final String   getWord()         { return isWord() ? mStem : null; }     // null, if this is not a word-node
		public final String   getStem()         { return isWord() ? mStem : mStem.substring(0, mDepth); }
		//public final int    getDepth()        { return mDepth; }
		//public final int    getChar()         { return mChar;  }    // == mStem.charAt(mDepth-1)
		public final int      getLetterIndex()  { return mChar - sFirstLetter; }
		//public final String   getLetter()       { return mStem.substring(mDepth-1, mDepth); }
		//public final String   getLetterAt(int x){ assert(0 <= x && x < sNumLetters); return mStem.substring(x); }
		//  public WordNode       getBranchAtIndex(int index) {
		//    assert(0 <= index && index <= sNumLetters);
		//    return mBranches[index];                                     // may return null 
		//  }

		public String toString() { 
			return String.format("[%s %d %c %s w:%d t:%d]", mStem, mDepth, mChar, (isWord() ? "T":"F"), mWordCount, mTextCount);
		}

		public final WordNode getBranchAtLetter(int letter) {
			assert(sFirstLetter <= letter && letter <= sLastLetter);
			return mBranches[letter - sFirstLetter];                     // may return null 
		}
		//    public final WordNode getNextBranchFromLetter(int letter)  {
		//      assert(mBranches[letter - sFirstLetter] != null);
		//      return mBranches[letter - sFirstLetter].mNextBranch; 
		//    }

		/**
		 * Print all words that contain this node, i.e., that start with this
		 * prefix.  The sub-trie under this node is traversed recursively in
		 * depth-first pre-order, which corresponds to alphabetical order 
		 * for the stored words.
		 */
		public void printWords()
		{
			if (isWord()) {
				System.out.println(mStem);
			}
			for (WordNode node = mFirstChild; node != null; node = node.mNextSibling ) {
				node.printWords();
			}
		}

		/** 
		 * Get a list of all words under this node, i.e., that contain 
		 * this node's stem.
		 * The list will naturally be in alphabetically order, just because
		 * a trie naturally stores its keys in alphabetical order.
		 */ 
		public List<String> getWords()
		{
			List<String> words = new ArrayList<String>();
			getWordsRecurse(words);
			return words;
		}

		/**
		 * Returns a sorted set of all words under this node (as strings).
		 */
		public SortedSet<String> getWordSet()
		{
			SortedSet<String> words = new TreeSet<String>();
			getWordsRecurse(words);
			return words; 
		}

		/** 
		 * Add all words that contain this node, i.e., this stem, to the specified
		 * collection <code>words</code>.  If this container keeps them in the order
		 * in which they are inserted, the words will be in alphabetical order. 
		 */
		public void getWordsRecurse(Collection<String> words)  
		{
			if (isWord())
				words.add(mStem);
			for (WordNode node = getFirstBranch(); node != null; node = node.getNextBranch() ) {
				node.getWordsRecurse(words);
			}
		}

		/**
		 * Find all sub-nodes matching the specified pattern as a substring.
		 * The matched pattern can appear anywhere in the word, not necessarily
		 * at the beginning of the word.
		 * 
		 * Start with this node's branches (not with this node itself), 
		 * and add them to the specified collection.
		 * Call this on the a trie's root node, or on the last-node of a word
		 * to find its possible continuations.
		 * 
		 * @see WordTrie.getWordsMatchingPattern.  Unlike getWordsRecurse, which
		 * is called by getWords, this method has no non-recursive calling method
		 * in WordNode.  It is better to call this functionality from the trie
		 * class than from the node class.
		 * 
		 * @param nodeList	Hold all matching nodes.  All words under a matching node are matches.
		 * @param pattern Non-empty pattern to match against any part of a word (prefix, middle, end).
		 * @param index of first letter in the pattern.  Passed in as a convenience, it never changes.
		 * @param maxSerchDepth is also the max length of the words to search.
		 */   
		public void findNodesMatchingPatternRecurse(Collection<WordNode> nodeList
				, final String pattern, int maxSearchDepth)
		{
			if (mDepth > maxSearchDepth)
				return;

			WordNode next = this;
			for (int k = 0; k < pattern.length(); k++) {
				next = next.mBranches[pattern.charAt(k) - sFirstLetter];
				if (next == null) {
					break;    // path from node dead-ended before matching the whole pattern
				}
			}
			if (next != null)
				nodeList.add(next);

			for (WordNode node = getFirstBranch(); node != null; node = node.getNextBranch() ) {
				node.findNodesMatchingPatternRecurse(nodeList, pattern, maxSearchDepth-1);
			}
		}

		/**
		 * Finds all nodes whose descendant words match the given pattern.
		 * The pattern must not be empty, but may contain the wild card 
		 * character, which matches any single ordinary character.
		 * Starting from *this* node, this method traverses the sub-trie
		 * recursively, but when it finds a match for the first character
		 * in the pattern, it matches the rest of the pattern recursively.
		 * 
		 * Called by: @see WordTrie.getWordsMatchingPatternWildCard.
		 * 
		 * @param results
		 * @param pattern
		 * @param patLen
		 * @param maxSearchDepth
		 */
		public void findNodesMatchingPatternWildCardRecIter(Collection<WordNode> results
				, Queue<WordNode> newNodes, Queue<WordNode> oldNodes
				, final String pattern, final int patLen, final int maxSearchDepth)
		{
			if (mDepth > maxSearchDepth)
				return;

			Queue<WordNode> tmpNodes = null;
			oldNodes.add(this);
			for (int k = 0; k < pattern.length(); ) {
				int patChrIdx = pattern.charAt(k) - sFirstLetter;
				if (patChrIdx == sWildCardIdx) {
					// If the next pattern char is wild, add all branches of each current node
					for (WordNode oldNode : oldNodes) {
						for (WordNode newNode = oldNode.getFirstBranch(); newNode != null; newNode = newNode.getNextBranch()) {
							newNodes.add(newNode);
						}
					}
				} else {
					// If the next pattern char is not wild, add only the indexed branch of each current node
					for (WordNode oldNode : oldNodes) {
						WordNode newNode = oldNode.mBranches[patChrIdx];
						if (newNode != null)
							newNodes.add(newNode);
					}
				}
				// If the path dead-ended before matching the whole pattern,
				// or if the whole pattern was just matched, stop iterating.
				if (newNodes.isEmpty() || ++k == patLen) {
					break; 
				}
				oldNodes.clear();
				tmpNodes = oldNodes;
				oldNodes = newNodes;
				newNodes = tmpNodes;
			}
			if ( ! newNodes.isEmpty()) {
				results.addAll(newNodes);
				newNodes.clear();
			}
			oldNodes.clear();
			for (WordNode node = getFirstBranch(); node != null; node = node.getNextBranch() ) {
				node.findNodesMatchingPatternWildCardRecIter(results, newNodes, oldNodes, pattern, patLen, maxSearchDepth-1);
			}
		}

		/**
		 * Find all sub-nodes matching the specified partial key as substring
		 * including single-character wild cards.  In other words, any wild
		 * card character in the search pattern matches any ordinary character
		 * in the word collection.
		 * 
		 * The matched pattern can appear anywhere in the word, not necessarily
		 * at the beginning of the word.
		 * 
		 * Starting with this node's branches (not with this node itself), 
		 * and add them to the specified collection.
		 * Call this on the a trie's root node, or on the last-node of a word
		 * to find its possible continuations.
		 * 
		 * @see WordTrie.getWordsMatchingPattern.  Unlike getWordsRecurse, which
		 * is called by getWords, this method has no non-recursive calling method
		 * in WordNode.  It is better to call this functionality from the trie
		 * class than from the node class.
		 * 
		 * There is no limit on the depth here, since every match starts from
		 * the beginning.
		 * 
		 * @param nodeList
		 * @param pattern Non-empty pattern to match against any part of a word (prefix, middle, end).
		 */   
		public void findNodesMatchingPrefixWildCardRecurse(Collection<WordNode> nodeList
				, final String pattern, final int patPos)
		{           
			if (patPos == pattern.length()) {
				nodeList.add(this);
				return;
			}

			int patChrIdx = pattern.charAt(patPos) - sFirstLetter;
			if (patChrIdx == sWildCardIdx) {
				// If the next pattern char is wild, try all branches of *this* node
				for (WordNode branch = getFirstBranch(); branch != null; branch = branch.getNextBranch()) {
					branch.findNodesMatchingPrefixWildCardRecurse(nodeList, pattern, patPos+1);
				}
			} else {            
				// If the next pattern char is not wild, add only the indexed branch of *this* node
				WordNode branch = mBranches[patChrIdx];
				if (branch != null)
					branch.findNodesMatchingPrefixWildCardRecurse(nodeList, pattern, patPos+1);
			}
		}


		public void findNodesMatchingPatternWildCardRecurse(Collection<WordNode> results
				, final String pattern, final int patLen, int maxSearchDepth)
		{
			if (mDepth > maxSearchDepth)
				return;

			// If *this* node matches the first char in the pattern, call the
			// prefix matcher to try matching the rest.
			if (mChar == pattern.charAt(0) || pattern.charAt(0) == sWildCardChar)
				findNodesMatchingPrefixWildCardRecurse(results, pattern, 1);

			for (WordNode node = getFirstBranch(); node != null; node = node.getNextBranch() ) {
				node.findNodesMatchingPatternWildCardRecurse(results, pattern, patLen, maxSearchDepth-1);
			}
		}




		public int getWordCount() {
			return mWordCount;
		}

		public int getTextCount() {
			return mTextCount;
		}

		/**
		 * Sorts nodes first by word count, then by text count 
		 * if the word counts are the same.  Thus nodeA > nodeB
		 * if nodeA.mWordCount > nodeB.mWordCount, of if 
		 * nodeA.mWordCount == nodeB.mWordCount but 
		 * nodeA.mTextCount >  nodeB.mTextCount
		 * Contrast this with the more "natural" ordering, which 
		 * is alphabetical by stem.
		 * @see WordNode.CompareAlpha and WordNode.CompareCounts
		 */
		@Override
		public int compareTo(WordNode other) {
			if (mWordCount == other.mWordCount)
				return mTextCount - other.mTextCount;
			return mWordCount - other.mWordCount;
		}

		/**
		 * Enables sorting first by word count, then by text count 
		 * if the word counts are the same.  As opposed to the more 
		 * "natural" ordering, which is alphabetical by stem.
		 * Assumes both nodes are non-null and valid.
		 * @see WordNode.CompareAlpha
		 * @author sprax
		 */
		class CompareCounts implements Comparator<WordNode>
		{
			/**
			 * Assumes both nodes are non-null and valid.
			 */
			@Override
			public int compare(WordNode node0, WordNode node1) {
				if (node1.mWordCount == node0.mWordCount)
					return node1.mTextCount - node0.mTextCount;
				return node1.mWordCount - node0.mWordCount;
			}
		}

		/**
		 * Enables sorting alphabetically by stem.
		 * Assumes both nodes are non-null and valid.
		 * @see WordNode.CompareCounts, which sorts by word count then text count.
		 * @author sprax
		 */
		class CompareAlpha implements Comparator<WordNode>
		{
			@Override
			public int compare(WordNode node0, WordNode node1) {    // TODO: shared strings?
				if (node0.mStem.equals(node1.mStem))                // if the stems are represented by the same enclosing string,
					return node0.mDepth - node1.mDepth;             // then the shorter substring sorts first.
				return node0.mStem.compareTo(node1.mStem);          // sort alphabetically by stem
			}
		}

	} // END of inner class WordNode


	///////////////////////////////////////////////////////////////////////////
	///////////////////////// NodeCount ///////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Holder class that wraps a WordNode and a count, to be used for 
	 * "pass by reference" method implementations.
	 * Use the non-void re-setter method node(WordNode) to change the
	 * held node and reset the count.
	 */
	public class NodeCount implements Comparable<NodeCount>, Cloneable
	{
		/** null means "empty," as in null search results, or "node not found". */
		public WordNode mNode;    
		/** count, rank, weight, or some number specific to an algorithm. */
		public int      mCount;
		public NodeCount(WordNode node, int count) { mNode = node; mCount = count; }
		NodeCount(WordNode node)            { mNode = node; mCount = 0; }
		NodeCount()                         { mNode = null; mCount = 0; }
		/** non-void re-setter, for 1-liners such as x = method(nodeCountA.node(wordNodeB); */
		NodeCount node(WordNode node) {
			mNode = node;
			mCount = 0;
			return this;
		}
		void reset() {
			mNode = null;
			mCount = 0;
		}
		@Override
		public int compareTo(NodeCount other) {
			return other.mCount - mCount;         // sort by descending count
		}
		@Override
		public NodeCount clone() { return new NodeCount(mNode, mCount); }

		public class WordCountComparator implements Comparator<NodeCount>
		{
			@Override
			public int compare(NodeCount arg0, NodeCount arg1) {
				return sWordCountComparator.compare(arg0.mNode, arg1.mNode);
			}
		}
	} // END of inner class NodeCount



	///////////////////////////////////////////////////////////////////////////
	///////////////////////// WordTrie ////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////


	public WordTrie() {
		sWordCountComparator = mRoot.new CompareCounts();
	}

	protected WordNode       getRoot()          { return mRoot; }
	public    final int      getSize()          { return mNumWords; }
	public    final int      getMinWordLen()    { return mActMinWordLen; }
	public    final int      getMaxWordLen()    { return mActMaxWordLen; }
	public    final boolean  getLoaded()        { return mTextFilePath != null; }
	//public    final WordNode getFirstWordNode() { return mRoot.getFirstWordNode(); }  //TODO: deprecate and remove
	public    final WordNode getRootBranch(int letter) { return mRoot.getBranchAtLetter(letter); }
	public    void           printWordsAll()    { mRoot.printWords(); } 
	public    int            chr2idx(char chr)  { return chr - sFirstLetter; }  // TODO: generalize...

	/**
	 * Returns the node representing this key's longest prefix in the trie.
	 */
	protected WordNode maxStemNode(String key)
	{
		if (key == null)
			return null;

		// Traverse down the trie until we reach the end of the key or null:
		WordNode prev = mRoot, node = prev;
		for (int end = key.length(), j = 0; j < end; j++) {
			node = prev.mBranches[chr2idx(key.charAt(j))];
			if (node == null) {
				return prev;    // Not found - reached end of branch
			}
			prev = node;
		}
		return node;
	}


	public String longestPrefix(String prefix)
	{
		WordNode maxNode = maxStemNode(prefix);
		if (maxNode != null) {
			if (maxNode.mDepth >= prefix.length())
				return prefix;
			else
				return maxNode.mStem.substring(0, maxNode.mDepth);
		}
		return null;
	}

	private static int test_longestPrefix(WordTrie trie, String str)
	{
		String prefix = trie.longestPrefix(str);
		System.out.format("longestPrefix: %20s  %s\n", prefix, str);
		return 0;
	}

	/**
	 * Returns the nodes representing this key's longest prefix in the trie.
	 */
	public List<WordNode> maxStemNodes(String pattern)
	{
		if (pattern == null)
			return null;

		ArrayList<WordNode> nodeList = new ArrayList<WordNode>();
		int patternLength = pattern.length();
		if (patternLength == 0) {
			nodeList.add(mRoot);
			return nodeList;
		}
		// Traverse down the trie, looking for the beginning of the pattern:
		WordNode root = mRoot;
		int begPattern = pattern.charAt(0) - sFirstLetter;
		START: for (int end = mActMaxWordLen + 1 - patternLength, j = 0; j < end; j++) {
			for (WordNode bran = root.mFirstChild; bran != null; bran = bran.mNextSibling ) {
				if (bran.getLetterIndex() == begPattern) {
					// This node matches the first letter in the pattern
					// Traverse down the trie until we get to the end of the pattern:
					WordNode next = bran; 
					for (int k = 1; k < patternLength; k++) { // pattern[0] is already matched
						next = next.mBranches[pattern.charAt(k) - sFirstLetter];
						if (next == null) {
							break START;    // path from node dead-ended before matching the whole pattern
						}
					}
					if (next != null)
						nodeList.add(next); // node has a continuation that matches the whole pattern
				}
				root = bran;
			}
		}
		return nodeList;
	}


	/**
	 * Returns a list of all words under the specified node (as strings).
	 */ 
	public static List<String> getWords(WordNode node)
	{
		if (node == null)
			return null;
		return node.getWords(); 
	}

	/**
	 * Returns a set of all words under the node (as strings).
	 */
	public static SortedSet<String> getWordSet(WordNode node)
	{
		if (node == null)
			return null;
		return node.getWordSet(); 
	}

	public SortedSet<String> getAllWords()
	{
		return mRoot.getWordSet();  // mRoot non-null by construction 
	}

	/**
	 * Returns a list of all words that stem from this string's maximal 
	 * prefix.  That is, returns all words that match the lost initial
	 * substring of the given prefix that is actually a stem.
	 * @return List of Strings representing words, in alphabetical order.
	 */
	public List<String> getWordsPartiallyMatchingPrefix(String prefix)
	{
		if (prefix == null)
			throw new IllegalArgumentException("null prefix");
		WordNode node = maxStemNode(prefix);
		return node.getWords(); 
	}

	/**
	 * Returns an ordered set containing all unique words matching 
	 * the input pattern.  If there are no matching words, the
	 * returned set will be empty, not null.
	 * @param pattern Search string, to match any part of the stored words.
	 * @return SortedSet of matched words (may be empty, but not null).
	 */
	public SortedSet<String> getWordsMatchingPattern(String pattern)
	{
		if (pattern == null)
			throw new IllegalArgumentException("null pattern");

		int patternLen = pattern.length();
		if (patternLen == 0) {
			return getAllWords();     // Return ALL words in this trie.
		}

		SortedSet<String> words = new TreeSet<String>();
		int maxSearchDepth = mActMaxWordLen - patternLen + 1; 
		if (maxSearchDepth < 1)
			return words;

		List<WordNode> nodeList = new ArrayList<WordNode>();
		mRoot.findNodesMatchingPatternRecurse(nodeList, pattern, maxSearchDepth);

		if ( ! nodeList.isEmpty()) {
			for (WordNode node : nodeList)
				node.getWordsRecurse(words);
		}

		return words;
	}

	public SortedSet<String> getWordsMatchingPrefixWildCardRecurse(String prefix)
	{
		if (prefix == null)
			throw new IllegalArgumentException("null prefix");

		int prefixLen = prefix.length();
		if (prefixLen == 0) {
			return getAllWords();     // Return ALL words in this trie.
		}
		SortedSet<String> words = new TreeSet<String>();
		if (prefixLen > mActMaxWordLen)
			return words;

		List<WordNode> nodeList = new ArrayList<WordNode>();
		mRoot.findNodesMatchingPrefixWildCardRecurse(nodeList, prefix, 0);

		if ( ! nodeList.isEmpty()) {
			for (WordNode node : nodeList)
				node.getWordsRecurse(words);
		}
		return words;
	}

	public SortedSet<String> getWordsMatchingPatternWildCardRecurse(String pattern)
	{
		if (pattern == null)
			throw new IllegalArgumentException("null pattern");

		int patternLen = pattern.length();
		if (patternLen == 0) {
			return getAllWords();     // Return ALL words in this trie.
		}

		SortedSet<String> words = new TreeSet<String>();
		int maxSearchDepth = mActMaxWordLen - patternLen + 1; 
		if (maxSearchDepth < 1)
			return words;

		List<WordNode> results = new ArrayList<WordNode>();
		mRoot.findNodesMatchingPatternWildCardRecurse(results, pattern, 0, maxSearchDepth);

		if ( ! results.isEmpty()) {
			for (WordNode node : results)
				node.getWordsRecurse(words);
		}
		return words;
	}

	public SortedSet<String> getWordsMatchingPatternWildCardRecIter(String pattern)
	{
		if (pattern == null)
			throw new IllegalArgumentException("null pattern");

		int patternLen = pattern.length();
		if (patternLen == 0) {
			return getAllWords();     // Return ALL words in this trie.
		}

		SortedSet<String> words = new TreeSet<String>();
		int maxSearchDepth = mActMaxWordLen - patternLen + 1; 
		if (maxSearchDepth < 1)
			return words;

		List<WordNode> nodeList = null;
		nodeList = new ArrayList<WordNode>();
		Queue<WordNode> newNodes = new LinkedList<WordNode>();
		Queue<WordNode> oldNodes = new LinkedList<WordNode>();
		mRoot.findNodesMatchingPatternWildCardRecIter(nodeList, newNodes, oldNodes, pattern, pattern.length(), maxSearchDepth);

		if ( ! nodeList.isEmpty()) {
			for (WordNode node : nodeList)
				node.getWordsRecurse(words);
		}
		return words;
	}



	protected int getProbableWordNodes(MinHeap<WordNode> minHeap, int maxNumNodes, WordNode node, int maxDepth)
	{
		//      if (node == null)
		//        return 0;

		minHeap.setSizeNow(0);                                          // Reset min heap to size to zero and heap property true,
		minHeap.isHeap(true);                                          // because find calls add(), which kepps heap property true.
		findMaxWordCountRecs(minHeap, maxNumNodes, node, maxDepth);    // find the actual results
		minHeap.sortAscend();
		return minHeap.getSizeNow();
	}


	protected int getProbableWordNodes(ArrayIter<WordNode> nodeIter, int maxNumNodes, WordNode node, int maxDepth)
	{
		//      if (node == null)
		//        return 0;

		nodeIter.setIndex(nodeIter.setSize(0));   // reset array iterator
		findMaxWordCountRecs(nodeIter, maxNumNodes, node, maxDepth);    // find the actual results
		nodeIter.sort();                       // Always sort (again) after the collection phase, full or not.
		return nodeIter.getSize();
	}

	public int getProbableWordNodes(WordNode maxNodes[], int maxNumNodes, String prefix, int maxDepth)
	{
		if (prefix == null || maxNumNodes < 2 || maxDepth < 1 || maxNodes.length < maxNumNodes)
			throw new IllegalArgumentException("getProbableNodes "+prefix+" "+maxNumNodes+" "+maxDepth);

		WordNode node = maxStemNode(prefix);
		return getProbableWordNodes(maxNodes, maxNumNodes, node, maxDepth);
	}

	protected int getProbableWordNodes(WordNode maxNodes[], int maxNumNodes, WordNode node, int maxDepth)
	{
		//      if (node == null)
		//        return 0;

		for (int j = 0; j < maxNumNodes; j++)        
			maxNodes[j] = mRoot;                              // initialize all to dummy value
		findMaxWordCountRecs(maxNodes, maxNumNodes, node, maxDepth);    // find the actual results
		// Partial "binary search" for counting the number of found words.
		// Typically this number is either the max allowed or else very small (as in 0, or 1 or 2),
		// so if the middle node was changed, count down from the max; if not, count up from 0.
		int halfMax = maxNumNodes/2 - 1;
		if (maxNodes[halfMax] == mRoot) {
			maxNumNodes = 0; 
			while (maxNodes[maxNumNodes] != mRoot)
				++maxNumNodes;                      // counting up
		} else {
			while (maxNodes[maxNumNodes-1] == mRoot)
				--maxNumNodes;                      // counting down
		}
		return maxNumNodes;
	}

	public List<NodeCount> getProbableWordNodeCounts(String prefix, int maxNumStems, int maxDepth)
	{
		if (prefix == null || maxNumStems < 2)
			throw new IllegalArgumentException("getProbableNodes null or limit < 2");

		WordNode node = maxStemNode(prefix);
		List<NodeCount> nodeCounts = new ArrayList<NodeCount>(maxNumStems);
		findMaxWordAndTotalTextCountRecs4(nodeCounts, node, 0, maxDepth);

		// Collections.sort(nodeCounts);   // this would sort on mCount, not mNode.mWordCount
		Collections.sort(nodeCounts, nodeCounts.get(0).new WordCountComparator());
		return nodeCounts;
	}


	public List<NodeCount> getGreedyProbableWordNodes(String prefix, int limit)
	{
		if (prefix == null || limit < 2)
			throw new IllegalArgumentException("getProbableNodes null or limit < 2");

		WordNode node = maxStemNode(prefix);
		NodeCount nodeCount = new NodeCount(node);
		if (minGreedyContinuation(nodeCount)) {
			List<NodeCount> comps = new ArrayList<NodeCount>(limit);
			comps.add(nodeCount.clone());


			int numProbWords = 1; 
			while (nextGreedyContinuation(nodeCount)) {
				comps.add(nodeCount.clone());
				if (++numProbWords == limit)
					break;
			}
			// Collections.sort(comps);   // this would sort on mCount, not mNode.mWordCount
			Collections.sort(comps, nodeCount.new WordCountComparator());
			return comps;
		}
		return null;
	}

	/** StringCollectorInterface method: return the backing container, which could be
	 * an object of such type as Collection<String>, but which in this case
	 * is just the trie itself. */
	@Override
	public WordTrie getCollector() {
		return this;
	}

	/**
	 * Determines whether the key is a word stored in this trie.
	 * Complexity for success is O(L) where L = key.length().
	 * For failure, it's typically << O(L).
	 * 
	 * @param  key  string that may represent a word
	 * @return true IFF the trie contains key as a word.
	 */
	@Override
	public boolean contains(final String key)
	{
		// Traverse down the trie until we get to the end of the string:
		WordNode node = mRoot;
		for (int end = key.length(), j = 0; j < end; j++) {
			node = node.mBranches[key.charAt(j) - sFirstLetter];
			if (node == null) {
				return  false;    // Not found - reached end of stem
			}
		}
		return node.isWord();
	}
	@Override
	public int size() {
		return getSize();
	}

	public boolean containsWord(StringBuffer sb) {
		return contains(sb.toString());
	}

	public boolean containsWord(final char chr[]) 
	{
		// Traverse down the trie until we get to the end of the string:
		WordNode node = mRoot;
		for (int end = chr.length, j = 0; j < end; j++) {
			node = node.mBranches[chr[j] - sFirstLetter];
			if (node == null) {
				return  false;    // Not found - reached end of stem
			}
		}
		return node.isWord();
	}

	/**
	 * For large test runs, this seems to run 25 to 40% slower
	 * than hasWord(String str).  But for short runs, there 
	 * is often no apparent difference.
	 * @deprecated
	 * @param str
	 * @return
	 */
	public boolean containsWordToChr(String str) 
	{
		return containsWord(str.toCharArray());
	}

	/**
	 * Char array [] is faster than String.charAt() for look up.
	 * <code>
	 * With kMax == 0:<br>
	 * STR size 58124    count 581240    MS 2687<br>
	 * CHR size 58124    count 581240    MS 2141<br>
	 * CHR size 58124    count 581240    MS 2156<br>
	 * STR size 58124    count 581240    MS 2563<br>
	 * </code>
	 * @param trie
	 * @return
	 */
	public static int test_timeHasWordStrVsChr(WordTrie trie)
	{
		Sx.puts("test_timeHasWordStrVsChr: beg . . .");

		Set<String> words = new TreeSet<String>();
		WordNode nA = trie.getRootBranch('a');
		WordNode nM = trie.getRootBranch('m');
		WordNode nP = trie.getRootBranch('p');
		WordNode nS = trie.getRootBranch('s');
		WordNode nW = trie.getRootBranch('w');

		nW.getWordsRecurse(words);  
		nS.getWordsRecurse(words);  
		nP.getWordsRecurse(words);  
		nM.getWordsRecurse(words);  
		nA.getWordsRecurse(words);  
		int size = words.size();

		long begTime, endTime, runTime;
		int k, kMax = 100, count = 0, trials = 10;
		boolean does = false;
		char [] chrs;

		begTime = System.currentTimeMillis();
		for (int j = 0; j < trials; j++) {
			trie.setAllTotalTextCounts();
		}
		endTime = System.currentTimeMillis();
		runTime = endTime - begTime;
		Sx.puts("setAllTotalTextCounts  count " + trials + "    MS " + runTime);

		begTime = System.currentTimeMillis();
		for (int j = 0; j < trials; j++) {
			for (final String word : words) {
				chrs = word.toCharArray();
				for (k = kMax; --k >= 0; )
					does = trie.contains(word);
				if (does)
					++count;
			}
		}
		endTime = System.currentTimeMillis();
		runTime = endTime - begTime;
		Sx.puts("STR size " + size + "    count " + count + "    MS " + runTime);

		count = 0;
		begTime = System.currentTimeMillis();
		for (int j = 0; j < trials; j++) {
			for (final String word : words) {
				chrs = word.toCharArray();
				for (k = kMax; --k >= 0; )
					does = trie.containsWord(chrs);
				if (does)
					++count;
			}
		}
		endTime = System.currentTimeMillis();
		runTime = endTime - begTime;
		Sx.puts("CHR size " + size + "    count " + count + "    MS " + runTime);

		//Merges.unit_test(1);

		count = 0;
		begTime = System.currentTimeMillis();
		for (int j = 0; j < trials; j++) {
			for (final String word : words) {
				chrs = word.toCharArray();
				for (k = kMax; --k >= 0; )
					does = trie.containsWord(chrs);
				if (does)
					++count;
			}
		}
		endTime = System.currentTimeMillis();
		runTime = endTime - begTime;
		Sx.puts("CHR size " + size + "    count " + count + "    MS " + runTime);

		count = 0;
		begTime = System.currentTimeMillis();
		for (int j = 0; j < trials; j++) {
			for (final String word : words) {
				chrs = word.toCharArray();
				for (k = kMax; --k >= 0; )
					does = trie.contains(word);
				if (does)
					++count;
			}
		}
		endTime = System.currentTimeMillis();
		runTime = endTime - begTime;
		Sx.puts("STR size " + size + "    count " + count + "    MS " + runTime);

		Sx.puts("test_timeHasWordStrVsChr: . . . end");
		return 0;
	}


	private boolean badWordLen(int wordLen)
	{
		if (wordLen < mReqMinWordLen || wordLen > mReqMaxWordLen)
			return true;

		if (mActMinWordLen > wordLen) {
			mActMinWordLen = wordLen;
		} else if (mActMaxWordLen < wordLen) {
			mActMaxWordLen = wordLen;
		}
		return false;
	}

	/**
	 * Puts a word into the trie, if it is not already there, creating nodes as necessary.  
	 * If the word is new, the last node created will have this word as its value, and it
	 * will remain a leaf node unless and until another word is added that has this one
	 * as a prefix.  All nodes traversed, new or not, will have their text count incremented. 
	 * @param word
	 * @param prev
	 * @return
	 */
	public WordNode putWord(final String word)
	{
		int wordLen = word.length();
		if (badWordLen(wordLen))
			return null;

		mNumPutChars += wordLen;

		// Traverse down the trie until we reach the end of the word, creating nodes as necessary.
		WordNode node = mRoot;
		for (int j = 0; j < wordLen; j++) {
			char cLetter = word.charAt(j);
			int  iLetter = cLetter - sFirstLetter;
			//assert(0 <= iLetter && iLetter < sNumLetters);
			if (node.mBranches[iLetter] == null) { 
				// create new branch node as a child with a back pointer to its parent.
				node.mBranches[iLetter] =  new WordNode(cLetter, word, j+1, node);
				mNumNodes++;
			}
			node = node.mBranches[iLetter];
			//            
			//            if (sDbg > 0) {
			//                int sum = 0, dif = node.mTextCount - node.getWordCount();
			//                for (WordNode br = node.mFirstChild; br != null; br = br.mNextSibling)
			//                    sum += br.mTextCount;
			//                if (sum != dif)
			//                    zoid++;
			//            }

			node.mTextCount++;
			node.mTotalCount += node.mDepth;
		}
		if ( ! node.isWord()) {
			node.mStem   = word;
			mNumWords++;                // increment total word count IFF this word is new
		}
		node.mWordCount++;            // Always increment word count, so TC - WC = SUM{branch.TC}

		//        if (sDbg > 0) {
		//            int sum = 0, dif = node.mTextCount - node.getWordCount();
		//            for (WordNode br = node.mFirstChild; br != null; br = br.mNextSibling)
		//                sum += br.mTextCount;
		//            if (sum != dif)
		//                zoid++;
		//        }

		return node;
	}

	/**
	 * TODO: duplicated code from putWord(String) -- String is faster than char[] for puts and gets
	 * @param chrs
	 * @param beg
	 * @param end
	 * @return
	 */
	public WordNode putWord(final char[] chrs, int beg, int end)
	{
		int wordLen = end - beg;
		if (badWordLen(wordLen))
			return null;

		mNumPutChars += wordLen;    

		// Traverse down the trie until we reach the end of the word, creating nodes as necessary.
		WordNode node = mRoot;
		for (int j = beg; j < end; j++) {
			char cLetter = chrs[j];
			int  iLetter = cLetter - sFirstLetter;
			if (node.mBranches[iLetter] == null) { 
				// create new branch node as a child with a back pointer to its parent.

				node.mBranches[iLetter] =  new WordNode(cLetter, new String(chrs, beg, end-beg), j+1-beg, node);
				mNumNodes++;
			}
			node = node.mBranches[iLetter];

			//            if (sDbg > 0) {
			//                int sum = 0, dif = node.mTextCount - node.getWordCount();
			//                for (WordNode br = node.mFirstChild; br != null; br = br.mNextSibling)
			//                    sum += br.mTextCount;
			//                if (sum != dif)
			//                    Sx.debug(1, "sum!=txt-wrd:" + node.mStem + " " + node.mDepth);
			//            }

			node.mTextCount++;
			node.mTotalCount += node.mDepth;
		}
		if ( ! node.isWord()) {
			node.mStem   = new String(chrs, beg, end-beg);  // This is rare -- new word that is prefix of old word.
			mNumWords++;                // increment total word count IFF this word is new
		}
		node.mWordCount++;            // Always increment word count, so TC - WC = SUM{branch.TC}

		//        if (sDbg > 0) {
		//            int sum = 0, dif = node.mTextCount - node.getWordCount();
		//            for (WordNode br = node.mFirstChild; br != null; br = br.mNextSibling)
		//                sum += br.mTextCount;
		//            if (sum != dif)
		//                zoid++;
		//        }
		return node;
	}


	/** 
	 * returns node representing an already added word. 
	 * No error checking!
	 * @param word
	 * @return
	 */
	public WordNode getWordNode(String word)
	{
		// Traverse down the trie until we reach the end of the key or null:
		WordNode node = mRoot;
		for (int end = word.length(), j = 0; j < end; j++) {
			//node = node.mBranches[chr2idx(word.charAt(j))];
			node = node.mBranches[word.charAt(j) - sFirstLetter];
		}
		return node;
	}

	public WordNode getWordNode(char[] word)
	{
		// Traverse down the trie until we reach the end of the key or null:
		WordNode node = mRoot;
		for (int end = word.length, j = 0; j < end; j++) {
			//node = node.mBranches[chr2idx(word.charAt(j))];
			node = node.mBranches[word[j] - sFirstLetter];
		}
		return node;
	}

	/** StringCollectorInterface method to mimic part of the Collection API */
	@Override
	public boolean addString(String str) {
		int numBefore = mNumWords;
		WordNode node = putWord(str);
		if (mNumWords > numBefore) {        // This implies that node != null
			mNewWordNodes.put(node.mStem, node);
			if (sDbg > 2)
				System.out.format("NEW:  %2d \t%s\n", node.mDepth, node.mStem);
			return true;
		}
		return false;
	}

	/**
	 * StringCollectorInterface method to mimic part of the Collection API
	 * @return True IFF the supplied word is new, i.e., not already 
	 * contained in this trie.
	 */
	@Override
	public boolean addString(char[] chr, int beg, int end) {
		int numBefore = mNumWords;

		WordNode node = null;
		if (sDbg > 0)
			node = putWord(new String(chr, beg, end-beg));
		else
			node = putWord(chr, beg, end);

		if (mNumWords > numBefore) {        // This implies that node != null
			mNewWordNodes.put(node.mStem, node);
			if (sDbg > 2)
				System.out.format("NEW:  %2d \t%s\n", node.mDepth, node.mStem);
			return true;
		}
		return false;
	}

	/** 
	 * Initialize WordTrie from dictionary, a plain text file that contains
	 * one word per line, in alphabetical order.  (Right now, nothing here
	 * really depends on the dictionary being sorted, but enhancements to 
	 * this class, such as methods supporting word grids, may depend on the
	 * sort order. 
	 * @param minWordLen
	 * @param maxWordLen
	 * @param textFilePath
	 * @return
	 */
	public int initFromSortedDictionaryFile(final String textFilePath, int minWordLen, int maxWordLen, int verbosity)
	{   
		if (textFilePath == null)
			return 0;

		// The following initialization check is not perfect, but may be adequate.
		if (textFilePath.equals(mTextFilePath)    // if this trie was already initialized from this file,
				&& minWordLen >= mReqMinWordLen       // and it already includes all the shortest words allowed,
				&& maxWordLen <= mReqMaxWordLen) {    // and it already includes all the longest words allowed,
			return mNumWords;                       // then just return the number of words loaded.
		}

		mTextFilePath   = null;
		mActMinWordLen  = Integer.MAX_VALUE;
		mActMaxWordLen  = 0;
		mNumNewWords    = 0;
		mTotalTextCountsUpToDate = false;

		BufferedReader reader = null;
		try {
			File file = new File(textFilePath);
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			System.out.println("Error opening dictionary file: " + textFilePath);
			e.printStackTrace();
			return 0;
		}

		int numLinesRead = 0;

		if (reader != null) {
			mReqMinWordLen = minWordLen;
			mReqMaxWordLen = maxWordLen;
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					numLinesRead++;
					int wordLen = line.length();
					if (wordLen < minWordLen || wordLen > maxWordLen) {
						continue;
					}
					WordNode node = null;
					node = putWord(line);    // Don't (yet) need putWord(line, prevWordNode);
					if (node != null) {
						node.mDictCount++;
						mNumNewWords++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		mTextFilePath  = textFilePath;
		mReqMinWordLen = minWordLen;
		mReqMaxWordLen = maxWordLen;
		if (verbosity > 0) {    
			Sx.format("Read %d lines, kept %d words, %d <= lengths <= %d, from %s\n"
					, numLinesRead, mNumNewWords, mActMinWordLen, mActMaxWordLen, textFilePath);
			showStats(textFilePath);
		}
		return mNumNewWords;  // return the number of "new" words (new if this is the only init).
	}

	protected void showStats(String label) 
	{
		Sx.format("After %-20s  totals:  %d words,  %d chars,  %d nodes,  %d new words\n"
				, label, mNumWords, mNumPutChars, mNumNodes, mNumNewWords); 
	}

	/** 
	 * Supplement WordTrie with words from a text file.
	 * 
	 * @param minWordLen
	 * @param maxWordLen
	 * @param textFilePath
	 * @return number of new words added.  Words already in the Trie, or outside the
	 * specified length range don't count.
	 */
	public int addAllWordsInTextFile(int minWordLen, int maxWordLen, final String textFilePath, int verbosity)
	{   
		mNumNewWords = 0;
		BufferedReader reader = null;
		try {
			File file = new File(textFilePath);
			reader = new BufferedReader(new FileReader(file));
		} catch (Exception e) {
			System.out.println("Error opening dictionary file: " + textFilePath);
			e.printStackTrace();
			return 0;
		}

		int numLinesRead = 0;
		if (reader != null) {
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					numLinesRead++;
					line.trim();
					int lineLen = line.length();
					if (lineLen < minWordLen) {
						continue;
					}          
					mNumNewWords += addLowerCaseLetterWordsToTrie(this, line.toCharArray(), lineLen);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (numLinesRead > 0)
			mTotalTextCountsUpToDate = false;
		if (verbosity > 1)
			Sx.puts("Read " + numLinesRead + " lines, added " + mNumNewWords + " new words from " + textFilePath + ":");
		return mNumNewWords;  // return the number of words read, not necessarily kept.
	}
	
    // TODO: Move this to a better place for static methods?
    public static int addLowerCaseLetterWordsToTrie(WordTrie trie, char[] chr, int length)
    {
        return TextFilters.collectLowerCaseLetterWords(trie, chr, length);
    }

	public void showNewWords(int numCols, int maxLen)
	{
		int q = 0;
		String ends = null;
		String formats = String.format("%%%ds %%s%%s", maxLen);
		for (Map.Entry<String, WordNode>  pair : mNewWordNodes.entrySet()) {
			String word = pair.getKey();
			int   count = pair.getValue().mTextCount;
			if (++q == numCols) {
				q = 0;
				ends = "\n";
			} else {
				ends = "\t";
			}
			System.out.format(formats, word, (count > 1 ? count : "  "), ends);
		}
		Sx.puts();
	}

	protected static int test_getWordsPartiallyMatchingPrefix(WordTrie trie, String prefix) 
	{
		String stem = trie.longestPrefix(prefix);
		List<String> words = trie.getWordsPartiallyMatchingPrefix(prefix);
		Sx.format("WordTrie words partially matching prefix \"%s\"[%s] (%d):\t", prefix, stem, words.size());
		Sx.putsIterable(words, 8);
		return 0;
	}

	public static int test_getWordsMatchingPattern(WordTrie trie, String pattern) 
	{
		Set<String> words = trie.getWordsMatchingPattern(pattern);
		System.out.format("Words matching pattern \"%s\" (%d):\t", pattern, words.size());
		Sx.putsIterable(words, 8);
		return 0;
	}

	public static int test_getWordsMatchingPrefixWildCardRecurse(WordTrie trie, String pattern) 
	{
		Set<String> words = trie.getWordsMatchingPrefixWildCardRecurse(pattern);
		System.out.format("Words matching pattern \"%s\" (%d):\t", pattern, words.size());
		Sx.putsIterable(words, 8);
		return 0;
	}

	public static int test_getWordsMatchingPatternWildCardRecurse(WordTrie trie, String pattern) 
	{
		Set<String> words = trie.getWordsMatchingPatternWildCardRecurse(pattern);
		System.out.format("Words matching pattern \"%s\" (%d):\t", pattern, words.size());
		Sx.putsIterable(words, 8);
		return 0;
	}

	public static int test_getWordsMatchingPatternWildCardRecIter(WordTrie trie, String pattern) 
	{
		Set<String> words = trie.getWordsMatchingPatternWildCardRecIter(pattern);
		System.out.format("Words matching pattern \"%s\" (%d):\t", pattern, words.size());
		Sx.putsIterable(words, 8);
		return 0;
	}

	public Map.Entry<WordNode, Integer> minGreedyContinuation(String str)
	{
		if (str == null)
			return null;
		WordNode lastNode = maxStemNode(str);
		return minGreedyContinuation(lastNode);
	}
	public Map.Entry<WordNode, Integer> nextGreedyContinuation(String str)
	{
		if (str == null)
			return null;
		WordNode lastNode = maxStemNode(str);
		return nextGreedyContinuation(lastNode);
	}
	public Map.Entry<WordNode, Integer> maxGreedyContinuation(String str)
	{
		if (str == null)
			return null;
		WordNode lastNode = maxStemNode(str);
		return maxGreedyContinuation(lastNode);
	}

	/**
	 * Looks for a minimal most-frequently-stemmed word that
	 * contains this stem node, or this node itself, if it is a word node. 
.    * Here "most-frequently-stemmed" basically
	 * means most often partially entered stem.  Being greedy, the algorithm
	 * might not find the most often entered whole word.
	 *
	 * The count value totals the number of times the stem continued
	 * with each additional letter in the path to the returned node.
	 * If the input node was already a word node, this count will be 0.
	 * 
	 * @param nodeCount
	 * @return True if any such word is found, otherwise false.  */
	public boolean minGreedyContinuation(NodeCount nodeCount)
	{
		WordNode node = nodeCount.mNode;
		if (node == null)
			return false;

		int totalCount = node.mTextCount;
		WordNode child, greedy = node;
		while ( ! greedy.isWord() && (child = greedy.mFirstChild) != null) {
			int maxCount = 0;
			do {
				if (maxCount < child.mTextCount) {
					maxCount = child.mTextCount;
					greedy = child;
				}
			} while((child = child.mNextSibling) != null);
			totalCount += maxCount;
		}    
		// cache this node's total path count?
		// mPathCounts.put(greedy, totalCount);
		nodeCount.mNode = greedy;
		nodeCount.mCount = totalCount;
		return greedy.isWord();
	}

	public Map.Entry<WordNode, Integer> minGreedyContinuation(WordNode node)
	{
		if (node == null)
			return null;

		int totalCount = node.mTextCount;
		WordNode child, greedy = node;
		while ( ! greedy.isWord() && (child = greedy.mFirstChild) != null) {
			int maxCount = 0;
			do {
				if (maxCount < child.mTextCount) {
					maxCount = child.mTextCount;
					greedy = child;
				}
			} while((child = child.mNextSibling) != null);
			totalCount += maxCount;
		}    
		// cache this node's total path count
		// mPathCounts.put(greedy, totalCount);
		return new AbstractMap.SimpleEntry<WordNode, Integer>(greedy, totalCount);
	}

	/**
	 * Returns the entry for the next word that properly contains this stem node, 
	 * or null if none exists.
	 * @param node
	 * @return Entry for first word > this node stem, or null if the stem is maximal.
	 * The count value totals the number of times the stem continued
	 * with each additional letter in the path to the returned node.
	 * Since the returned node cannot be the input node, this count must be > 0.   
	 */
	public Map.Entry<WordNode, Integer> nextGreedyContinuation(WordNode node)
	{
		if (node == null)
			return null;

		int totalCount = node.mTextCount;
		WordNode child, greedy = node;
		while ((child = greedy.mFirstChild) != null) {
			int maxCount = 0;
			do {
				if (maxCount < child.mTextCount) {
					maxCount = child.mTextCount;
					greedy = child;
				}
			} while((child = child.mNextSibling) != null);
			totalCount += maxCount;
			if (greedy.isWord()) {
				// cache this node's total path count
				// mPathCounts.put(greedy, totalCount);
				return new AbstractMap.SimpleEntry<WordNode, Integer>(greedy, totalCount);
			}
		}
		return null;
	}

	public static boolean nextGreedyContinuation(NodeCount nodeCount)
	{
		WordNode node = nodeCount.mNode;
		if (node == null)
			return false;

		int totalCount = node.mTextCount;
		WordNode child, greedy = node;
		while ((child = greedy.mFirstChild) != null) {
			int maxCount = 0;
			do {
				if (maxCount < child.mTextCount) {
					maxCount = child.mTextCount;
					greedy = child;
				}
			} while((child = child.mNextSibling) != null);
			totalCount += maxCount;
			if (greedy.isWord()) {
				// cache this node's total path count
				// mPathCounts.put(greedy, totalCount);
				nodeCount.mNode = greedy;
				nodeCount.mCount = totalCount;
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the entry for the maximal most-frequently-stemmed word that
	 * contains this stem node, or this node itself, if it is a word node.
	 * Here "most-frequently-stemmed" basically
	 * means most often partially entered stem.  Being greedy and seeking
	 * the longest word, the algorithm is NOT likely to return the most 
	 * often entered whole word.
	 * @param node
	 * @return Entry for the longest maximal word >= this node stem.
	 * The count value totals the number of times the stem continued
	 * with each additional letter in the path to the returned node.
	 * If the input node was already a word node, this count will be 0.
	 */
	public Map.Entry<WordNode, Integer> maxGreedyContinuation(WordNode node)
	{
		if (node == null)
			return null;

		int totalCount = node.mTextCount;
		WordNode child, greedy = node;
		while ((child = greedy.mFirstChild) != null) {
			int maxCount = 0;
			do {
				if (maxCount < child.mTextCount) {
					maxCount = child.mTextCount;
					greedy = child;
				}
			} while((child = child.mNextSibling) != null);
			totalCount += maxCount;
		}    
		// remember this node's total path count
		// mPathCounts.put(greedy, totalCount);
		return new AbstractMap.SimpleEntry<WordNode, Integer>(greedy, totalCount);
	}


	public Map.Entry<WordNode, Integer> maxDepthLimitedContinuation(WordNode start, int tot, int limit)
	{
		if (start == null || limit <= 0)
			return null;

		int totalCount = 0;

		WordNode node = start;
		for (WordNode child = node.mFirstChild; child != null; child = child.mNextSibling) {
			int maxCount = 0;
			if (maxCount < child.mTextCount) {
				maxCount = child.mTextCount;
				node = child;
			}
			totalCount += maxCount;
		}    
		// remember this node's total path count
		// mPathCounts.put(node, totalCount);
		return new AbstractMap.SimpleEntry<WordNode, Integer>(node, totalCount);
	}  

	/**
	 * Exhaustive search to 3 levels down.
	 * // NB: SUM{all branches' text counts} == mTextCount - mWordCount
	 * @param nodeCount
	 * @return
	 */
	public boolean maxTextCount3xhs(NodeCount nodeCount)
	{
		zoid = 0;

		WordNode node = nodeCount.mNode;
		if (node == null)
			return false;

		nodeCount.mCount = 0;

		WordNode maxNode[] = new WordNode[4];
		maxNode[0] = node;
		int maxCount[] = new int[4];
		maxCount[0] = node.mTextCount;

		int tmpCount0 = node.mTextCount;
		for (WordNode b1 = node.mFirstChild; b1 != null; b1 = b1.mNextSibling) {
			int tmpCount1 = tmpCount0 + b1.mTextCount;
			if (maxCount[1] < tmpCount1) {
				maxCount[1] = tmpCount1;
				maxNode[1]  = b1;
			}
			for (WordNode b2 = b1.mFirstChild; b2 != null; b2 = b2.mNextSibling) {
				int tmpCount2 = tmpCount1 + b2.mTextCount;
				if (maxCount[2] < tmpCount2) {
					maxCount[2] = tmpCount2;
					maxNode[2] = b2;
				}
				for (WordNode b3 = b2.mFirstChild; b3 != null; b3 = b3.mNextSibling) {
					int tmpCount3 = tmpCount2 + b3.mTextCount;
					if (maxCount[3] < tmpCount3) {
						maxCount[3] = tmpCount3;
						maxNode[3] = b3;
					}
				}
			}
		}
		WordNode theNode = maxNode[0];
		int theCount = maxCount[0];
		for (int j = 1; j < 4; j++) {
			if (maxNode[j] == null)
				break;
			if (theCount < maxCount[j]) {
				theCount = maxCount[j];
				theNode  = maxNode[j];
			}
		}
		if (theNode != maxNode[3]) {
			zoid++;
		}
		// remember this node's total path count
		// mPathCounts.put(theMax, maxCount);
		nodeCount.mNode = theNode;
		nodeCount.mCount = theCount;
		return true;
	}


	static int zoidDisagree = 0;
	public static boolean test_equalNodes(String label, WordNode wnA, WordNode wnB) 
	{  
		if (wnA == wnB)
			return true;
		Sx.puts(label + wnA + " != " + wnB);
		++zoidDisagree;
		return false;
	}

	public static boolean test_equalNodesOk(String label, WordNode wnA, WordNode wnB) 
	{  
		if (wnA == wnB)
			return true;
		Sx.puts(label + wnA + " != " + wnB);
		return false;
	}


	/**
	 * Exhaustive search, N levels down.
	 * // NB: SUM{all branches' text counts} == mTextCount - mWordCount
	 * @param nodeCount
	 * @return
	 */
	public boolean maxTextCount3opt(NodeCount nodeCount)
	{
		zoid = 0;

		WordNode node = nodeCount.mNode;
		if (node == null)
			return false;

		nodeCount.mCount   = 0;
		WordNode maxNode   = node;
		int      maxCount  = node.mTextCount;

		WordNode b0 = node;
		int tmpCount0 = node.mTextCount;
		int b1SumCount = 0, b1MaxCount = 0;
		for (WordNode b1 = node.mFirstChild; b1 != null; b1 = b1.mNextSibling) {
			int tmpCount1 = tmpCount0 + b1.mTextCount;
			if (maxCount < tmpCount1) {
				maxCount   = tmpCount1;
				maxNode    = b1;
				b1MaxCount = b1.mTextCount;
			}
			int b2SumCount = 0, b2MaxCount = 0;
			for (WordNode b2 = b1.mFirstChild; b2 != null; b2 = b2.mNextSibling) {
				int tmpCount2 = tmpCount1 + b2.mTextCount;
				if (maxCount < tmpCount2) {
					maxCount   = tmpCount2;
					maxNode    = b2;
					b2MaxCount = b2.mTextCount;
				}
				WordNode b3MaxNode = null;
				int b3SumCount = 0, b3MaxCount = 0, b2DifCount = b2.mTextCount - b2.mWordCount;
				for (WordNode b3 = b2.mFirstChild; b3 != null; b3 = b3.mNextSibling) {
					if (b3MaxCount < b3.mTextCount) {
						b3MaxCount = b3.mTextCount;
						b3MaxNode  = b3;
					}
					if (b3SumCount > b2DifCount - b3MaxCount)
						break;
					else if (b3SumCount == b2DifCount - b3MaxCount && b3.mNextSibling != null)
						break;
					b3SumCount += b3.mTextCount;
				}
				if (b3MaxNode != null) {
					int tmpCount3 = tmpCount2 + b3MaxCount;
					if (maxCount < tmpCount3) {
						maxCount  = tmpCount3;
						maxNode   = b3MaxNode;
					}
				}

				// These 2 sides should be equal unless the above loop breaks early. 
				if (b3SumCount != b2DifCount && !(b3SumCount > b2DifCount - b3MaxCount))
					zoid++;

				if (b2MaxCount > b1.mTextCount - b1.mWordCount - b2SumCount)
					break;
				else if (b2MaxCount == b1.mTextCount - b1.mWordCount - b2SumCount && b2.mNextSibling != null)
					break;
				b2SumCount   += b2.mTextCount;
			}
			if (b1 != null && b1.mStem.equals("outs"))
				zoid++;

			if (b1SumCount > b0.mTextCount - b0.mWordCount - b1MaxCount)
				break;
			else if (b1MaxCount == b0.mTextCount - b0.mWordCount - b1SumCount && b1.mNextSibling != null)
				break;
			b1SumCount   += b1.mTextCount;
		}

		nodeCount.mNode  = maxNode;
		nodeCount.mCount = maxCount;
		return true;
	}

	public int maxTextCountContRec(WordNode node, int maxDepth)
	{
		WordNode child = node.mFirstChild;
		if (child == null || --maxDepth < 0)
			return node.mTextCount;

		int count, maxCount = 0;
		do {
			count = maxTextCountContRec(child, maxDepth);
			if (maxCount < count) {
				maxCount = count;
			}
		} while((child = child.mNextSibling) != null);

		return maxCount + node.mTextCount;
	}

	/**
	 * Walk the trie from the root down to the end of the string,
	 * adding up the text counts to get the total.
	 * @param stem Must be a valit stem in the trie.  No error checking.
	 * @return total text count from root to stem end
	 */
	protected int totalTextCount(final String stem)
	{
		int total = 0;
		WordNode node = mRoot;
		for (int j = 0; j < stem.length(); j++) {
			node = node.mBranches[stem.charAt(j) - sFirstLetter];
			total += node.mTextCount;
		}
		return total;
	}


	protected void setAllTotalTextCounts() 
	{
		setTotalTextCounts(mRoot, 0);
		mTotalTextCountsUpToDate = true;
	}

	protected void setTotalTextCounts(WordNode node, int count) 
	{
		count += node.mTextCount;
		node.mTotalCount = count;
		WordNode child = node.mFirstChild;
		if (child == null)
			return;

		do { setTotalTextCounts(child, count);
		} while((child = child.mNextSibling) != null);
	}

	/**
	 * Travel down the trie until we get to the end of the string,
	 * adding up the text counts to get the total.
	 * @param stem Must be a valid stem node in the trie.  No error checking.
	 * @return total text count from root to stem end
	 */
	protected int totalTextCount(final WordNode stemNode)
	{
		int total = 0;
		WordNode node = mRoot;
		for (int j = 0; j < stemNode.mDepth; j++) {
			node = node.mBranches[stemNode.mStem.charAt(j) - sFirstLetter];
			total += node.mTextCount;
		}
		return total;
	}


	/**
	 * Travel down the trie until we get to the end of the string,
	 * adding up the text counts to get the total.
	 * @param stem Must be a valid stem node in the trie.  No error checking.
	 * @return total text count from root to stem end
	 */
	protected int partialTextCount(final WordNode firstNode, final WordNode lastNode)
	{
		WordNode node = firstNode;
		int total = node.mTextCount;
		for (int j = firstNode.mDepth; j < lastNode.mDepth; j++) {
			node = node.mBranches[lastNode.mStem.charAt(j) - sFirstLetter];
			total += node.mTextCount;
		}
		return total;
	}

	public WordNode[] maxTextCountContPath(WordNode node, int maxLen)
	{
		if (node == null || maxLen < 1)
			return null;

		WordNode nodePath[] = new WordNode[maxLen];
		nodePath[0] = node;
		maxTextCountContPathRec(nodePath, 1);
		return nodePath;
	}

	public int maxTextCountContPathRec(WordNode nodePath[], int len)
	{
		WordNode node = nodePath[len-1];
		WordNode child = node.mFirstChild;
		if (child == null || len >= nodePath.length)
			return node.mTextCount;

		WordNode maxNode = child;
		int count, maxCount = 0;
		do {
			nodePath[len] = child;
			count = maxTextCountContPathRec(nodePath, len+1);
			if (maxCount < count) {
				maxCount = count;
				maxNode = child;
			}
		} while((child = child.mNextSibling) != null);
		nodePath[len] = maxNode;

		return maxCount + node.mTextCount;
	}

	class MaxTotalTextCountFinder {
		final WordNode mNode;
		final WordNode mPath[];
		final int mDepth;
		int       mPathLen;
		int       mMaxTextCount;
		WordNode  mMaxNode;

		MaxTotalTextCountFinder(WordNode node, int depth) {
			mNode  = node;
			mDepth = depth;
			mPath  = new WordNode[mDepth];
			mPath[0] = mNode;
			mPathLen = 1;
		}

		public WordNode maxNode() {
			if (mMaxNode == null)
				doFind();
			return mMaxNode;
		}
		public int maxCount() {
			if (mMaxNode == null)
				doFind();
			return mMaxTextCount;
		}
		public void doFind() {
			findMaxTotalTextCountWordNode(mNode, 0, 0);
		}

		private void findMaxTotalTextCountWordNode(WordNode node, int count, int len) 
		{
			WordNode child = node.mFirstChild;
			count += node.mTextCount;
			if (child == null) { // If node is in this trie, it must be a word node
				if ( ! node.isWord())
					throw new IllegalArgumentException("findMaxTotalTextCountWordNode not a word?");
			if (mMaxTextCount < count) {
				mMaxTextCount = count;
				mMaxNode = node;
			}
			return;
			}
			if (len >= mDepth) {
				//Map.Entry<WordNode, Integer> minCon = minGreedyContinuation(node);
				//WordNode endNode  = minCon.getKey();
				//int      endCount = minCon.getValue();  // Don't use this in total
				if (mMaxTextCount < count) {
					mMaxTextCount = count;
					mMaxNode = node;
				}
				return;
			}
			do { 
				findMaxTotalTextCountWordNode(child, count, len+1);
			} while((child = child.mNextSibling) != null);
		}

	} // End of class: MaxTotalTextCountFinder

	public void findMaxTotalTextRec(NodeCount nodeCount, WordNode node, int count, int maxDepth) 
	{
		count += node.mTextCount;
		WordNode child = node.mFirstChild;
		if (child == null || --maxDepth < 0) {
			if (nodeCount.mCount < count) {
				nodeCount.mCount = count;
				nodeCount.mNode  = node;
			}
			return;
		}
		do { findMaxTotalTextRec(nodeCount, child, count, maxDepth);
		} while((child = child.mNextSibling) != null);
	}


	public void findMaxTotalTextRecOpt(NodeCount nodeCount, WordNode node, int count, int maxDepth) 
	{
		count += node.mTextCount;
		WordNode child = node.mFirstChild;
		if (child == null || --maxDepth < 0) {
			if (nodeCount.mCount < count) {
				nodeCount.mCount = count;
				nodeCount.mNode = node;
			}
			return;
		}
		int sumCount = 0, maxCount = 0, txtCount;
		int difCount = node.mTextCount - node.mWordCount;
		do { 
			findMaxTotalTextRecOpt(nodeCount, child, count, maxDepth);
			txtCount = child.mTextCount;
			if (maxCount < txtCount) {
				maxCount = txtCount;
			}
			if (sumCount >= difCount - maxCount)
				break;
			sumCount += txtCount;
		} while((child = child.mNextSibling) != null);
	}



	/**
	 *     
	 * @param nodeCounts[] must have space for at least 3 NodeCounts
	 * @param node
	 * @param count
	 * @param maxDepth
	 */
	 public void findMaxTotalTextRecs3(NodeCount nodeCounts[], WordNode node, int count, int maxDepth) 
	 {
		 WordNode child = node.mFirstChild;
		 if (child == null || maxDepth == 0) {
			 int lastCount = count + node.mTextCount;
			 keepMax3NodeCountsBottomUp(nodeCounts, node, lastCount);
			 return;
		 }
		 count += node.mTextCount;
		 maxDepth--;
		 do { 
			 findMaxTotalTextRecs3(nodeCounts, child, count, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }

	 public void findMaxTotalTextRecs4(NodeCount nodeCounts[], WordNode node, int count, int maxDepth) 
	 {
		 count += node.mTextCount;
		 WordNode child = node.mFirstChild;
		 if (child == null || --maxDepth < 0) {
			 keepMaxNodeCountsBottomUp4(nodeCounts, node, count);
			 return;
		 }
		 do { 
			 findMaxTotalTextRecs4(nodeCounts, child, count, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }

	 /**
	  * Find 4 nodes maximizing mTextCount at end-of-word or maxDepth.
	  * @param nodeCounts
	  * @param node
	  * @param mCount
	  * @param maxDepth
	  */
	 public void findMaxTextCountRecs4(WordNode maxNodes[], WordNode node, int maxDepth) 
	 {
		 WordNode child = node.mFirstChild;
		 if (child == null || --maxDepth < 0) {
			 keepMaxTextCountNodesBottomUp4(maxNodes, node, node.mTextCount);
			 return;
		 }
		 do { findMaxTextCountRecs4(maxNodes, child, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }


	 /**
	  * FIXME: this is a test method, meant to compare word count with text count, not for "production"
	  * @param nodeCounts
	  * @param node
	  * @param count
	  * @param maxDepth
	  */
	 public void findMaxWordAndTotalTextCountRecs4(List<NodeCount> nodeCounts, WordNode node, int count, int maxDepth) 
	 {
		 count += node.mTextCount;
		 WordNode child = node.mFirstChild;
		 if (child == null || --maxDepth < 0) {
			 keepMaxNodeCountsBottomUp4(nodeCounts, node, count);
			 return;
		 }
		 if (node.isWord()) {
			 keepMaxNodeCountsBottomUp4(nodeCounts, node, count);
		 }
		 do { findMaxWordAndTotalTextCountRecs4(nodeCounts, child, count, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }


	 /**
	  * TODO: branch and bound.
	  * @param maxNodes
	  * @param node
	  * @param maxDepth
	  */
	 public void findMaxWordCountRecs(MinHeap<WordNode> minHeap, int maxNumNodes, WordNode node, int maxDepth) 
	 {
		 // Check for further processing vs. returning now, in this order:
		 // 1) Branch and bound: We have node.mWordCount = node.mTextCount - SUM({node.mBranches}.mTextCount),
		 //    which implies the following:
		 //    A) mWordCount <= mTextCount
		 //    B) the word count of a child node <= mTextCount - mWordCount - partial sum of 
		 //    earlier sibling's mTextCounts.

		 //        if (node.mStem.length() > 4 && node.mStem.substring(0, 5).equals("breas"))
		 //            zoid++;
		 if (minHeap.isFull() && node.mTextCount <= minHeap.get(0).mWordCount)    // Even if this node is a word,
			 return;                                         // its count is below threshold.
		 if (node.isWord()) {
			 SaveMax.saveMax(minHeap, node);       // It could be a keeper.
		 }
		 WordNode child = node.mFirstChild;                  // Does it have any children?
		 if (child == null || --maxDepth < 0)                // If no child, or depth limit reached,
			 return;                                         // stop searching this branch.

		 do { findMaxWordCountRecs(minHeap, maxNumNodes, child, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }


	 /**
	  * TODO: branch and bound.
	  * @param maxNodes
	  * @param node
	  * @param maxDepth
	  */
	 public void findMaxWordCountRecs(ArrayIter<WordNode> nodeArrayIter, int maxNumNodes, WordNode node, int maxDepth) 
	 {
		 // Check for further processing vs. returning now, in this order:
		 // 1) Branch and bound: We have node.mWordCount = node.mTextCount - SUM({node.mBranches}.mTextCount),
		 //    which implies the following:
		 //    A) mWordCount <= mTextCount
		 //    B) the word count of a child node <= mTextCount - mWordCount - partial sum of 
		 //    earlier sibling's mTextCounts.

		 //        if (node.mStem.length() > 3 && node.mStem.substring(0, 4).equals("thus"))
		 //            woid++;

		 if (nodeArrayIter.isFull() && node.mTextCount <= nodeArrayIter.head().mWordCount)    // Even if this node is a word,
			 return;                                         // its count is below threshold.
		 if (node.isWord()) {
			 SaveMax.saveMaxSort(nodeArrayIter, node);       // It could be a keeper.
			 //SaveMax.saveMaxBottomUp(maxNodes, maxNumNodes, node);       // It could be a keeper.
		 }
		 WordNode child = node.mFirstChild;                  // Does it have any children?
		 if (child == null || --maxDepth < 0)                // If no child, or depth limit reached,
			 return;                                         // stop searching this branch.

		 do { findMaxWordCountRecs(nodeArrayIter, maxNumNodes, child, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }


	 /**
	  * TODO: branch and bound.
	  * @param maxNodes
	  * @param node
	  * @param maxDepth
	  */
	 public void findMaxWordCountRecs(WordNode maxNodes[], int maxNumNodes, WordNode node, int maxDepth) 
	 {
		 // Check for further processing vs. returning now, in this order:
		 // 1) Branch and bound: We have node.mWordCount = node.mTextCount - SUM({node.mBranches}.mTextCount),
		 //    which implies the following:
		 //    A) mWordCount <= mTextCount
		 //    B) the word count of a child node <= mTextCount - mWordCount - partial sum of 
		 //    earlier sibling's mTextCounts.
		 if (node == null || node.mStem == null)
			 return;

		 if (node.mStem.length() > 3 && node.mStem.substring(0, 4).equals("thus"))
			 woid++;
		 if (node.mTextCount <= maxNodes[0].mWordCount)   	// Even if this node is a word,
			 return;                                         // its count is below threshold.
		 if (node.isWord()) {
			 SaveMax.saveMaxSort(maxNodes, node, maxNumNodes);       // It could be a keeper.
			 //SaveMax.saveMaxBottomUp(maxNodes, maxNumNodes, node);       // It could be a keeper.
		 }
		 WordNode child = node.mFirstChild;                  // Does it have any children?
		 if (child == null || --maxDepth < 0)                // If no child, or depth limit reached,
			 return;                                         // stop searching this branch.

		 do { findMaxWordCountRecs(maxNodes, maxNumNodes, child, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }



	 /**
	  * TODO: branch and bound.
	  * @param maxNodes
	  * @param node
	  * @param maxDepth
	  */
	 public void findMaxWordCountRecs4(WordNode maxNodes[], WordNode node, int maxDepth) 
	 {
		 // Check for further processing vs. returning now, in this order:
		 // 1) Branch and bound: We have node.mWordCount = node.mTextCount - SUM({node.mBranches}.mTextCount),
		 //    which implies the following:
		 //    A) mWordCount <= mTextCount
		 //    B) the word count of a child node <= mTextCount - mWordCount - partial sum of 
		 //    earlier sibling's mTextCounts.
		 // 2) If node is a word, call keepMax*, but keep going unless firstChild is null.
		 // 3) Return now if firstChild==null or maxDepth==0
		 if (node.mTextCount <= maxNodes[3].mWordCount)    // Even if this node is a word,
			 return;                                         // its count is below threshold.
		 if (node.isWord())
			 keepMaxWordCountNodesBottomUp4(maxNodes, node); // It could be a keeper.
		 WordNode child = node.mFirstChild;                // Does it have any children?
		 if (child == null || --maxDepth < 0)              // If no child, or depth limit reached,
			 return;                                         // stop searching this branch.

		 do { findMaxWordCountRecs4(maxNodes, child, maxDepth);
		 } while((child = child.mNextSibling) != null);
	 }


	 public static void keepMaxTextCountNodesBottomUp4(WordNode maxNodes[], WordNode node, int count)
	 {
		 if (maxNodes[3].mTextCount < count) {
			 if (maxNodes[1].mTextCount < count) {
				 maxNodes[3] = maxNodes[2];
				 maxNodes[2] = maxNodes[1];
				 if (maxNodes[0].mTextCount < count) {
					 maxNodes[1]  = maxNodes[0];               
					 maxNodes[0]  = node;
				 } else {
					 maxNodes[1]  = node;
				 }
			 } else {
				 if (maxNodes[2].mTextCount < count) {
					 maxNodes[3]  = maxNodes[2];
					 maxNodes[2]  = node;
				 } else {
					 maxNodes[3]  = node;
				 }
			 }
		 }
	 }

	 public static void keepMaxWordCountNodesBottomUp4(WordNode maxNodes[], WordNode node)
	 {
		 int count = node.mWordCount;
		 if (maxNodes[3].mWordCount < count) {
			 if (maxNodes[1].mWordCount < count) {
				 maxNodes[3] = maxNodes[2];
				 maxNodes[2] = maxNodes[1];
				 if (maxNodes[0].mWordCount < count) {
					 maxNodes[1]  = maxNodes[0];               
					 maxNodes[0]  = node;
				 } else {
					 maxNodes[1]  = node;
				 }
			 } else {
				 if (maxNodes[2].mWordCount < count) {
					 maxNodes[3]  = maxNodes[2];
					 maxNodes[2]  = node;
				 } else {
					 maxNodes[3]  = node;
				 }
			 }
		 }
	 }

	 public static void keepMax3NodeCountsTopDown(NodeCount nodeCounts[], WordNode node, int count)
	 {
		 if (nodeCounts[0].mCount < count) {
			 nodeCounts[2].mCount = nodeCounts[1].mCount;
			 nodeCounts[2].mNode  = nodeCounts[1].mNode;
			 nodeCounts[1].mCount = nodeCounts[0].mCount;
			 nodeCounts[1].mNode  = nodeCounts[0].mNode;               
			 nodeCounts[0].mCount = count;
			 nodeCounts[0].mNode  = node;
		 } else if (nodeCounts[1].mCount < count) {
			 nodeCounts[2].mCount = nodeCounts[1].mCount;
			 nodeCounts[2].mNode  = nodeCounts[1].mNode;
			 nodeCounts[1].mCount = count;
			 nodeCounts[1].mNode  = node;
		 } else if (nodeCounts[2].mCount < count) {
			 nodeCounts[2].mCount = count;
			 nodeCounts[2].mNode  = node;
		 }
	 }


	 public static void keepMax3NodeCountsBottomUp(NodeCount nodeCounts[], WordNode node, int count)
	 {

		 if (nodeCounts[2].mCount < count) {
			 if (nodeCounts[1].mCount < count) {
				 if (nodeCounts[0].mCount < count) {
					 nodeCounts[2].mCount = nodeCounts[1].mCount;
					 nodeCounts[2].mNode  = nodeCounts[1].mNode;
					 nodeCounts[1].mCount = nodeCounts[0].mCount;
					 nodeCounts[1].mNode  = nodeCounts[0].mNode;               
					 nodeCounts[0].mCount = count;
					 nodeCounts[0].mNode  = node;
				 } else {
					 nodeCounts[2].mCount = nodeCounts[1].mCount;
					 nodeCounts[2].mNode  = nodeCounts[1].mNode;
					 nodeCounts[1].mCount = count;
					 nodeCounts[1].mNode  = node;
				 }
			 } else {
				 nodeCounts[2].mCount = count;
				 nodeCounts[2].mNode  = node;
			 }
		 }
	 }



	 public static void keepMaxNodeCountsBottomUp4(NodeCount nodeCounts[], WordNode node, int count)
	 {
		 if (nodeCounts[3].mCount < count) {
			 if (nodeCounts[1].mCount < count) {
				 nodeCounts[3].mCount = nodeCounts[2].mCount;
				 nodeCounts[3].mNode  = nodeCounts[2].mNode;
				 nodeCounts[2].mCount = nodeCounts[1].mCount;
				 nodeCounts[2].mNode  = nodeCounts[1].mNode;
				 if (nodeCounts[0].mCount < count) {
					 nodeCounts[1].mCount = nodeCounts[0].mCount;
					 nodeCounts[1].mNode  = nodeCounts[0].mNode;               
					 nodeCounts[0].mCount = count;
					 nodeCounts[0].mNode  = node;
				 } else {
					 nodeCounts[1].mCount = count;
					 nodeCounts[1].mNode  = node;
				 }
			 } else {
				 if (nodeCounts[2].mCount < count) {
					 nodeCounts[3].mCount = nodeCounts[2].mCount;
					 nodeCounts[3].mNode  = nodeCounts[2].mNode;
					 nodeCounts[2].mCount = count;
					 nodeCounts[2].mNode  = node;
				 } else {
					 nodeCounts[3].mCount = count;
					 nodeCounts[3].mNode  = node;
				 }
			 }
		 }
	 }


	 public void keepMaxNodeCountsBottomUp4(List<NodeCount> nodeCounts, WordNode node, int count)
	 {
		 int size = nodeCounts.size();
		 if (size < 4) {
			 NodeCount nodeCount = new NodeCount(node, count);
			 nodeCounts.add(nodeCount);
			 if (size == 3)
				 Collections.sort(nodeCounts, nodeCount.new WordCountComparator());
			 return;
		 }
		 if (nodeCounts.get(3).mCount < count) {
			 if (nodeCounts.get(1).mCount < count) {
				 nodeCounts.get(3).mCount = nodeCounts.get(2).mCount;
				 nodeCounts.get(3).mNode  = nodeCounts.get(2).mNode;
				 nodeCounts.get(2).mCount = nodeCounts.get(1).mCount;
				 nodeCounts.get(2).mNode  = nodeCounts.get(1).mNode;
				 if (nodeCounts.get(0).mCount < count) {
					 nodeCounts.get(1).mCount = nodeCounts.get(0).mCount;
					 nodeCounts.get(1).mNode  = nodeCounts.get(0).mNode;               
					 nodeCounts.get(0).mCount = count;
					 nodeCounts.get(0).mNode  = node;
				 } else {
					 nodeCounts.get(1).mCount = count;
					 nodeCounts.get(1).mNode  = node;
				 }
			 } else {
				 if (nodeCounts.get(2).mCount < count) {
					 nodeCounts.get(3).mCount = nodeCounts.get(2).mCount;
					 nodeCounts.get(3).mNode  = nodeCounts.get(2).mNode;
					 nodeCounts.get(2).mCount = count;
					 nodeCounts.get(2).mNode  = node;
				 } else {
					 nodeCounts.get(3).mCount = count;
					 nodeCounts.get(3).mNode  = node;
				 }
			 }
		 }
	 }




	 protected static int test_addAllWordsInTextFile(WordTrie trie, int minWordLen, int maxWordLen, final String fileName, int verbosity) 
	 {
		 trie.mNewWordNodes.clear();
		 int numNewWords = trie.addAllWordsInTextFile(minWordLen, maxWordLen, fileName, verbosity);
		 if (numNewWords > 0 && verbosity > 1) {
			 trie.showNewWords(6, trie.mActMaxWordLen);
		 }
		 if (verbosity > 0) {    
			 trie.showStats(fileName);

			 if (verbosity > 1) {    
				 int      maxFreq = 0, maxLen = 0, minLen = 9999;
				 WordNode frqNode = null, lnNode = null, shNode = null;
				 for (WordNode node : trie.mNewWordNodes.values()) {
					 if (maxFreq < node.mTextCount) {
						 maxFreq = node.mTextCount;
						 frqNode = node;
					 }
					 if (maxLen < node.mDepth) {
						 maxLen = node.mDepth;
						 lnNode = node;
					 }   
					 if (minLen > node.mDepth) {
						 minLen = node.mDepth;
						 shNode = node;
					 }   
				 }
				 if (frqNode != null) {
					 Sx.puts("Most frequent new word: " + frqNode.mStem + "  " + maxFreq);
					 Sx.puts("      Longest new word: " + lnNode.mStem + "  " + maxLen);
					 Sx.puts("     Shortest new word: " + shNode.mStem + "  " + minLen);
				 }
			 }
		 }
		 return numNewWords;
	 }


	 public static int test_ux_stringEntry()
	 {
		 String prompt = "Enter a string for ... whatever: ";
		 int promptLen = prompt.length();
		 Sx.print(prompt);

		 String strA = Sx.getString();
		 Sx.printSpaces(promptLen);
		 Sx.print(strA);


		 strA += Sx.getString();
		 Sx.printSpaces(promptLen);
		 Sx.print(strA);

		 strA += Sx.getString();
		 Sx.printSpaces(promptLen);
		 Sx.puts(strA);
		 Sx.puts("Goodbye from getString!");

		 return 0;
	 }


	 protected static int switchedGetProbableWordNodes(WordTrie trie, int eGetProb, int maxNumNodes, int maxDepth
			 , WordNode node, WordNode wordNodes4Array[], ArrayIter<WordNode> nodeIter, MinHeap<WordNode> minHeap)
	 {
		 switch (eGetProb) {
		 case 0: 
			 return trie.getProbableWordNodes(wordNodes4Array, maxNumNodes, node, maxDepth);
		 case 1: 
			 return trie.getProbableWordNodes(nodeIter, maxNumNodes, node, maxDepth);
		 default: 
			 return trie.getProbableWordNodes(minHeap, maxNumNodes, node, maxDepth);
		 }

	 }

	 protected static int test_time_getProbable(WordTrie trie, int eGetProb, int numTrials, int maxNumWords
			 , int maxDepth, int maxLength, int verbose)
	 {
		 int stat = 0;

		 WordNode root = trie.getRoot();
		 WordNode wordNodes4Array[] = new WordNode[maxNumWords];
		 WordNode wordNodes4AIter[] = new WordNode[maxNumWords];
		 WordNode wordNodes4LHeap[] = new WordNode[maxNumWords];


		 long begTime, endTime, runTime;

		 ArrayIter<WordNode> nodeIter = new ArrayIter<WordNode>(wordNodes4AIter, 0, 0); 
		 MinHeap<WordNode> minHeap = new MinHeap<WordNode>(wordNodes4LHeap, 0, true);
		 int numProb = 0;
		 int nodesTested = 0, totalFound = 0;        
		 begTime = System.currentTimeMillis();
		 for (int j = 0; j < numTrials; j++) {
			 nodesTested = 0;
			 for (WordNode w1 = root.getFirstBranch(); w1 != null; w1 = w1.getNextBranch()) {       
				 //                if (w1.mChar < 'a' || w1.mChar > 't')
					 //                    continue;
				 nodesTested++;
				 numProb = switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
						 , w1, wordNodes4Array, nodeIter, minHeap);
				 if (maxLength < 2)
					 continue;
				 for (WordNode w2 = w1.getFirstBranch(); w2 != null; w2 = w2.getNextBranch()) {  
					 nodesTested++;
					 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
							 , w2, wordNodes4Array, nodeIter, minHeap);
					 if (maxLength < 3)
						 continue;
					 for (WordNode w3 = w2.getFirstBranch(); w3 != null; w3 = w3.getNextBranch()) {  
						 nodesTested++;
						 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
								 , w3, wordNodes4Array, nodeIter, minHeap);
						 if (maxLength < 4)
							 continue;
						 for (WordNode w4 = w3.getFirstBranch(); w4 != null; w4 = w4.getNextBranch()) {  
							 nodesTested++;
							 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
									 , w4, wordNodes4Array, nodeIter, minHeap);
							 if (maxLength < 5)
								 continue;
							 for (WordNode w5 = w4.getFirstBranch(); w5 != null; w5 = w5.getNextBranch()) {  
								 nodesTested++;
								 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
										 , w5, wordNodes4Array, nodeIter, minHeap);
								 if (maxLength < 6)
									 continue;
								 for (WordNode w6 = w5.getFirstBranch(); w6 != null; w6 = w6.getNextBranch()) {  
									 nodesTested++;
									 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
											 , w6, wordNodes4Array, nodeIter, minHeap);
									 if (maxLength < 7)
										 continue;
									 for (WordNode w7 = w6.getFirstBranch(); w7 != null; w7 = w7.getNextBranch()) {  
										 nodesTested++;
										 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
												 , w7, wordNodes4Array, nodeIter, minHeap);
										 if (maxLength < 8)
											 continue;
										 for (WordNode w8 = w7.getFirstBranch(); w8 != null; w8 = w8.getNextBranch()) {  
											 nodesTested++;
											 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
													 , w8, wordNodes4Array, nodeIter, minHeap);
											 if (maxLength < 9)
												 continue;
											 for (WordNode w9 = w8.getFirstBranch(); w9 != null; w9 = w9.getNextBranch()) {  
												 nodesTested++;
												 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
														 , w9, wordNodes4Array, nodeIter, minHeap);
												 if (maxLength < 10)
													 continue;
												 for (WordNode wA = w9.getFirstBranch(); wA != null; wA = wA.getNextBranch()) {  
													 nodesTested++;
													 numProb += switchedGetProbableWordNodes(trie, eGetProb, maxNumWords, maxDepth
															 , wA, wordNodes4Array, nodeIter, minHeap);
												 }
											 }
										 }
									 }
								 }
							 }
						 }
					 }
				 }
				 totalFound += numProb;
			 }
		 }
		 endTime = System.currentTimeMillis();
		 runTime = endTime - begTime;
		 Sx.format("time_test: %d trials, up to %d letters, %d nodes/trial, wordsFound %d  DS: %s  time: %d\n"
				 , numTrials, maxLength, nodesTested, totalFound
				 , (eGetProb==0 ? "Array [] " : (eGetProb==1 ? "ArrayIter" : "MaxHeap  "))
				 , runTime );

		 Sx.format("numFull v. Not: %d v %d\n"
				 , SaveMax.getCountIsFull(), SaveMax.sCountNotFull);

		 return stat;    
	 }


	 static int test_patternMatching(WordTrie trie) {

		 String str = "expiration";
		 Sx.puts(str + ": " + trie.contains(str));

		 str = "filter";
		 Sx.puts(str + ": " + trie.contains(str));

		 str = "";
		 Sx.puts("<empty> : " + trie.contains(str));

		 test_getWordsPartiallyMatchingPrefix(trie, "drin");
		 test_getWordsPartiallyMatchingPrefix(trie, "exquis");
		 test_getWordsPartiallyMatchingPrefix(trie, "site");
		 test_getWordsPartiallyMatchingPrefix(trie, "xquis");
		 test_getWordsPartiallyMatchingPrefix(trie, "bj");
		 test_getWordsPartiallyMatchingPrefix(trie, "");

		 Sx.puts("\n    test_getWordsMatchingPrefixWildCardRecurse:");
		 try {
			 test_getWordsMatchingPrefixWildCardRecurse(trie, null);
		 } catch (Exception ex) {
			 assert(ex.getMessage().equals("null pattern"));
		 }
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "a_a");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "a_q");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "baca");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "exq_is");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "acq_it");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "acq_a_");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "wo_f");
		 test_getWordsMatchingPrefixWildCardRecurse(trie, "w_t_f");

		 Sx.puts("\n    test_getWordsMatchingPatternWildCardRecurse:");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "a_a");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "a_q");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "baca");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "xq_is");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "_q_is");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "_q_i_");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "cq_a_");
		 test_getWordsMatchingPatternWildCardRecurse(trie, "w_t_f");

		 Sx.puts("\n    test_getWordsMatchingPattern:");
		 test_getWordsMatchingPattern(trie, "");
		 test_getWordsMatchingPattern(trie, "xquis");
		 test_getWordsMatchingPattern(trie, "site");
		 test_getWordsMatchingPattern(trie, "ardva");
		 test_getWordsMatchingPattern(trie, "aardv");
		 test_getWordsMatchingPattern(trie, "aa");
		 test_getWordsMatchingPattern(trie, "qqxx");
		 test_getWordsMatchingPattern(trie, "abcdefghijklmnopqrstuvwxyzabcdefghijklmnop");
		 test_getWordsMatchingPattern(trie, "etymol");  // 13 letters

		 Sx.puts("\n    test_getWordsMatchingPatternWildCardRecIter:");
		 test_getWordsMatchingPatternWildCardRecIter(trie, "xq_is");
		 test_getWordsMatchingPatternWildCardRecIter(trie, "_q_is");
		 test_getWordsMatchingPatternWildCardRecIter(trie, "_q_i_");
		 test_getWordsMatchingPatternWildCardRecIter(trie, "cq_a_");


		 test_longestPrefix(trie, "rock");
		 test_longestPrefix(trie, "rockpaper");
		 test_longestPrefix(trie, "rockabies");
		 test_longestPrefix(trie, "roczz");
		 return 0;
	 }

	 /**
	  * WordTrie unit test
	  * TODO: Next steps:
	  * *) remove all path stuff, it has no value...all it gives is random
	  * access into a short path that can be sequentially accessed very quickly anyway
	  * 0) Rename
	  * 1) switch away from Map.Entry methods....  deferred.
	  * 2) put branching bounds on text count methods
	  *    a) exhaustive...
	  *    b) even on the greedy ones!
	  * 3) pure word count methods?
	  * 4) mixed text + word count methods?
	  * 5) re-visit which fields to keep in WordNode vs. in visitors.
	  * 6) maps to maintain as fields in WordTrie?
	  * 
	  * 7) Extend letter set for words:   [0, 64) <--> ASCII['\-0-9A-Za-z]
	  * 7) Extend letter set for phrases: [0, 96) <--> ASCII 32 to 127, and 
	  *        127 maps ALL others: 0 - 31, 127 - 255.
	  * 
	  * Proposed most likely continuations algorithm to auto-complete a WORD:
	  * 1) get A = firstGreedy(stem) and B = nextGreedy(A).  
	  *    If A.wC < B.wC, get C = nextGreedy(B).
	  *    Use at least the one with highest wC.
	  * 2) get {Dj} = 4 highest scorers from exhaustive search: 
	  *    D[] = maxExhaust(stem, 4)
	  * 3) If stem is a key, get E = greedy or exhaustive result from 
	  *    maxProperSubStemNode(stem).  This is to cover "typos".
	  * Order the top 5 of these possibly 8 continuations {A,B,C,D0,D1,D2,D3,E}.
	  * 
	  * To auto-complete a PHRASE, add these steps:
	  * 4) From stem, get maxStem and also a word + prefix from splitting, 
	  *    and get maxExhaust(maxStem) and maxExhaus(word) + maxExhuast(prefix)...
	  */
	 public static int unit_test(int level)
	 {
		 Sx.puts(WordTrie.class.getName() + ".unit_test");

		 int stat = 0;
		 int minWordLen = 3;
		 int maxWordLen = 12;
		 WordTrie trie = new WordTrie();
		 WordNode root = trie.getRoot();
		 String dictFile = FileUtil.getTextFilePath("words.txt");
		 trie.initFromSortedDictionaryFile(dictFile, minWordLen, maxWordLen, 1);


		 stat += test_patternMatching(trie);

		 if (level > 1) {

		     String 
		     path = FileUtil.getTextFilePath("MobyDick.txt");
			 test_addAllWordsInTextFile(trie, minWordLen, maxWordLen, path, 2); 
			 path = FileUtil.getTextFilePath("Iliad.txt");
			 test_addAllWordsInTextFile(trie, minWordLen, maxWordLen, path, 1); 

			 if (level < 3) {
				 Map.Entry<WordNode, Integer> mg = trie.maxGreedyContinuation(root);
				 Sx.puts("Greediest word of all: " + mg.getKey().mStem + "  " + mg.getValue());

				 int minGrdTot, nxtGrdTot, maxGrdTot, maxDepth = 3;

				 Map.Entry<WordNode, Integer> maxCon = null;
				 NodeCount nodeCount = trie.new NodeCount();
				 NodeCount nodeOptim = trie.new NodeCount();
				 NodeCount nodeRecur = trie.new NodeCount();
				 NodeCount nodeRecop = trie.new NodeCount();
				 WordNode minNode, nxtNode = null, maxNode;
				 WordNode optStem3 = null, xhsStem3 = null;
				 WordNode optWord3 = null, xhsWord3 = null;
				 int maxNumProb = 4;
				 NodeCount nodeCounts[] = new NodeCount[maxNumProb];
				 WordNode  maxNodes[]   = new WordNode[maxNumProb];
				 for (int q = 0; q < maxNumProb; q++) {
					 nodeCounts[q] = trie.new NodeCount();
					 maxNodes[q]   = trie.getRoot();
				 }

				 Sx.puts();
				 for (WordNode wn = root.getFirstBranch(); wn != null; wn = wn.getNextBranch()) {

					 if (wn.mChar < 'a' || wn.mChar > 't')
						 continue;

					 nxtGrdTot = 0;      
					 int maxTextCount = trie.maxTextCountContRec(wn, trie.mActMaxWordLen);

					 //WordNode[] nodePath = trie.maxTextCountContPath(wn, 6);


					 // GREEDY TEXT COUNT ////////////////////////////////////////////////////////////////

					 Map.Entry<WordNode, Integer> minCon = trie.minGreedyContinuation(wn);
					 minNode    = minCon.getKey();
					 minGrdTot  = minCon.getValue();
					 Sx.format("min greedy word starting with %c: %16s \t%d \t%d \t%d \t%d\n"
							 , wn.mChar, minNode.mStem, minNode.mDepth, minGrdTot, minNode.getWordCount(), minNode.mTotalCount);

					 Map.Entry<WordNode, Integer> nxtCon = trie.nextGreedyContinuation(minNode);
					 nodeCount.mNode = minNode;
					 boolean bFound = WordTrie.nextGreedyContinuation(nodeCount);
					 if (nxtCon == null) {
						 trie.zoid++;
						 maxCon = trie.maxGreedyContinuation(minNode);  // min == max, no nxt in between
					 } else {
						 nxtNode    = nxtCon.getKey();
						 nxtGrdTot  = nxtCon.getValue();
						 if ( ! bFound || nodeCount.mNode != nxtNode || nodeCount.mCount != nxtGrdTot)
							 throw new IllegalStateException("nextGreedyContinuation pass by ref?");
						 Sx.format("Nxt greedy word starting with %c: %16s \t%d \t%d \t%d\n"
								 , wn.mChar, nxtNode.mStem, nxtNode.mDepth, nxtGrdTot, nxtNode.getWordCount());
						 maxCon = trie.maxGreedyContinuation(nxtNode);
					 }
					 if (maxCon != null) {
						 maxNode   = maxCon.getKey();
						 maxGrdTot = maxCon.getValue();
						 int grdTot = maxGrdTot + nxtGrdTot + minGrdTot - minNode.mTextCount - nxtNode.mTextCount;
						 int gMx    = grdTot - maxTextCount;
						 Sx.format("MAX greedy word starting with %c: %16s \t%d \t%d \t%d \t(%d %s %d)\n"
								 , wn.mChar, maxNode.mStem, maxNode.mDepth, maxGrdTot, maxNode.getWordCount(), grdTot
								 , (gMx < 0 ? "<" : gMx == 0 ? "==" : "=?>?="), maxTextCount);
						 if (grdTot > maxTextCount) {
							 throw new IllegalStateException("total Greedy > Exhaustive");
						 }      
					 }


					 // EXHAUSTIVE TEXTCOUNT /////////////////////////////////////////////////////////   

					 if (trie.maxTextCount3xhs(nodeCount.node(wn))) {
						 xhsStem3 = nodeCount.mNode;
						 int txtTot3  = nodeCount.mCount;
						 Sx.format("Exhaust 3  stem starting with %c: %16s \t%d \t%d \t%d \t%d\n"
								 , wn.mChar, xhsStem3.mStem.substring(0, xhsStem3.mDepth), xhsStem3.mDepth
								 , txtTot3, xhsStem3.getWordCount(), xhsStem3.mTextCount);

						 if (xhsStem3.isWord()) {
							 xhsWord3 = xhsStem3;
						 } else {
							 Map.Entry<WordNode, Integer> extTxt3 = trie.minGreedyContinuation(xhsStem3);
							 xhsWord3     = extTxt3.getKey();
							 int extTot3  = extTxt3.getValue() + txtTot3;
							 Sx.format("Exhaust 3  extended word from %c: %16s \t%d \t%d \t%d \t%d\n"
									 , wn.mChar, xhsWord3.mStem, xhsWord3.mDepth, extTot3, xhsWord3.getWordCount(), xhsWord3.mTextCount);
						 }
					 }
					 if (trie.maxTextCount3opt(nodeOptim.node(wn))) {
						 optStem3 = nodeOptim.mNode;
						 int txtTot3  = nodeOptim.mCount;
						 Sx.format("ExOptim 3  stem starting with %c: %16s \t%d \t%d \t%d \t%d \t%d\n"
								 , wn.mChar, optStem3.mStem.substring(0, optStem3.mDepth), optStem3.mDepth
								 , txtTot3, optStem3.getWordCount(), optStem3.mTextCount, optStem3.mTotalCount);

						 if (optStem3.isWord()) {
							 optWord3 = optStem3;
						 } else {
							 Map.Entry<WordNode, Integer> extTxt3 = trie.minGreedyContinuation(optStem3);
							 optWord3     = extTxt3.getKey();
							 int extTot3  = extTxt3.getValue() + txtTot3;
							 Sx.format("ExOptim 3  extended word from %c: %16s \t%d \t%d \t%d \t%d \t%d\n"
									 , wn.mChar, optWord3.mStem, optWord3.mDepth, extTot3, optWord3.getWordCount()
									 , optWord3.mTextCount, optWord3.mTotalCount);
						 }
					 }
					 test_equalNodes("maxTextCount3xhaust & opt3 disagree: ", xhsStem3, optStem3);


					 trie.findMaxTotalTextRec(nodeRecur.node(wn), wn, 0, maxDepth);
					 WordNode recNode = nodeRecur.mNode;
					 Sx.format("Recursive3 stem starting with %c: %16s \t%d \t%d \t%d \t%d \t%d\n"
							 , wn.mChar, recNode.mStem.substring(0, recNode.mDepth), recNode.mDepth
							 , nodeRecur.mCount, recNode.getWordCount(), recNode.mTextCount, recNode.mTotalCount);
					 test_equalNodes("maxTextCount3xhst & recur3 disagree: ", xhsStem3, recNode);


					 trie.findMaxTotalTextRecOpt(nodeRecop.node(wn), wn, 0, maxDepth);
					 WordNode recOpt3 = nodeRecop.mNode;
					 Sx.format("Recur Opt3 stem starting with %c: %16s \t%d \t%d \t%d \t%d \t%d\n"
							 , wn.mChar, recOpt3.mStem.substring(0, recOpt3.mDepth), recOpt3.mDepth
							 , nodeRecop.mCount, recOpt3.getWordCount(), recOpt3.mTextCount, recOpt3.mTotalCount);
					 test_equalNodes("maxTextCountRec3 & RecOpt3 disagree: ", recNode, recOpt3);


					 nodeCounts[0].reset();
					 nodeCounts[1].reset();
					 nodeCounts[2].reset();
					 nodeCounts[3].reset();
					 trie.findMaxTotalTextRecs4(nodeCounts, wn, 0, maxDepth); 
					 test_equalNodes("maxTotTextCountRec3 & RecOpt4 disagree: ", recNode, nodeCounts[0].mNode);
					 for (int q = 0; q < 4; q++) {            
						 WordNode nodeQ = nodeCounts[q].mNode;
						 if (nodeQ != null)
							 Sx.format("Recur Opt4 stem starting with %c [%d]: %12s \t%d \t%d \t%d \t%d \t%d\n"
									 , wn.mChar, q, nodeQ.mStem /*.substring(0, nodeQ.mDepth)*/, nodeQ.mDepth
									 , nodeCounts[q].mCount, nodeQ.getWordCount(), nodeQ.mTextCount, nodeQ.mTotalCount);
					 }

					 maxNodes[0] = trie.mRoot;
					 maxNodes[1] = trie.mRoot;
					 maxNodes[2] = trie.mRoot;
					 maxNodes[3] = trie.mRoot;
					 trie.findMaxTextCountRecs4(maxNodes, wn, maxDepth); 
					 test_equalNodesOk("maxTotTextCountRec & maxTextRecs4 disagree (OK): ", recNode, maxNodes[0]);
					 for (int q = 0; q < 4; q++) {            
						 WordNode nodeQ = maxNodes[q];
						 if (nodeQ != null)
							 Sx.format("Recur Txt4 stem starting with %c [%d]: %12s \t%d \t  \t%d \t%d \t%d\n"
									 , wn.mChar, q, nodeQ.mStem /*.substring(0, nodeQ.mDepth)*/, nodeQ.mDepth
									 , nodeQ.getWordCount(), nodeQ.mTextCount, nodeQ.mTotalCount);
					 }

					 maxNodes[0] = trie.mRoot;
					 maxNodes[1] = trie.mRoot;
					 maxNodes[2] = trie.mRoot;
					 maxNodes[3] = trie.mRoot;
					 trie.findMaxWordCountRecs4(maxNodes, wn, maxDepth); 
					 test_equalNodesOk("maxTotTextCountRec & WordOpt4 disagree (OK): ", recNode, maxNodes[0]);
					 for (int q = 0; q < 4; q++) {            
						 WordNode nodeQ = maxNodes[q];
						 if (nodeQ != null)
							 Sx.format("Recur Wrd4 stem starting with %c [%d]: %12s \t%d \t  \t%d \t%d \t%d\n"
									 , wn.mChar, q, nodeQ.mStem /*.substring(0, nodeQ.mDepth)*/, nodeQ.mDepth
									 , nodeQ.getWordCount(), nodeQ.mTextCount, nodeQ.mTotalCount);
					 }

					 // if (wn.mChar == 'e')               zoof++;

					 // FindHelper //////////////////////////////////////////////////////////

					 MaxTotalTextCountFinder sh = trie.new MaxTotalTextCountFinder(wn, maxDepth);
					 WordNode maxShNode  = sh.maxNode();
					 int      maxShCount = sh.maxCount();
					 Sx.format("FindHelper stem starting with %c: %16s \t%d \t%d \t%d \t%d \t%d\n"
							 , wn.mChar, maxShNode.mStem.substring(0, maxShNode.mDepth), maxShNode.mDepth
							 , maxShCount, maxShNode.getWordCount(), maxShNode.mTextCount, maxShNode.mTotalCount);

					 test_equalNodes("maxTextCount3opt & helper3 disagree: ", xhsStem3, maxShNode);

					 // testing side-effects and/or breakpoints
					 trie.maxTextCount3opt(nodeOptim.node(wn));
					 trie.maxTextCount3opt(nodeOptim.node(minNode));

					 nodeCount.mNode = nodeOptim.mNode = nxtNode;
					 trie.maxTextCount3xhs(nodeCount);
					 trie.maxTextCount3opt(nodeOptim);

					 WordNode txtNode3 = nodeCount.mNode;
					 int      txtTot3  = nodeCount.mCount;
					 Sx.format("Exhaust 3  stem starting with %s: %16s \t%d \t%d \t%d \t%d\n"
							 , nxtNode.mStem, txtNode3.mStem.substring(0, txtNode3.mDepth)
							 , txtNode3.mDepth, txtTot3, txtNode3.getWordCount(), txtNode3.mTextCount);
					 WordNode nopNode3 = nodeOptim.mNode;
					 int      nopTot3  = nodeOptim.mCount;
					 Sx.format("ExOptim 3  stem starting with %s: %16s \t%d \t%d \t%d \t%d\n"
							 , nxtNode.mStem, nopNode3.mStem.substring(0, nopNode3.mDepth)
							 , nopNode3.mDepth, nopTot3, nopNode3.getWordCount(), nopNode3.mTextCount);

					 if (nodeCount.mNode != nodeOptim.mNode) {
						 Sx.format("maxTextCount3xhs & 3opt disagree: %s != %s (%d & %d)\n"
								 , nodeCount.mNode, nodeOptim.mNode, nodeCount.mCount, nodeOptim.mCount);
					 }

					 nodeCounts[0].reset();
					 nodeCounts[1].reset();
					 nodeCounts[2].reset();
					 trie.findMaxTotalTextRecs3(nodeCounts, nxtNode, 0, maxDepth); 
					 test_equalNodes("maxTextCountRec3 & RecOpt3 disagree: ", txtNode3, nodeCounts[0].mNode);
					 for (int q = 0; q < 3; q++) {            
						 WordNode nodeQ = nodeCounts[q].mNode;
						 if (nodeQ != null)
							 Sx.format("Recur Opt3 stem starting with %c [%d]: %12s \t%d \t%d \t%d \t%d \t%d\n"
									 , wn.mChar, q, nodeQ.mStem /*.substring(0, nodeQ.mDepth)*/, nodeQ.mDepth
									 , nodeCounts[q].mCount, nodeQ.getWordCount(), nodeQ.mTextCount, nodeQ.mTotalCount);
					 }

					 // nodePath?????
					 //      for (int j = 0; j < nodePath.length; j++) {
					 //        WordNode node = nodePath[j];
					 //        if (node != null) {
					 //          S.format("  %s(%d)", node.mStem, node.mTextCount);
					 //        }
					 //      }
					 Sx.puts("\n");
				 }
				 Sx.puts();
			 }

			 if (level == 3) {
				 stat += AutoCompleteWord.test_ux_AutoCompleteWord(trie);
				 return stat;
			 }
			 if (level == 4) {
				 Sx.puts(WordTrie.class.getName() + ".time_test begin...");
				 int numTrials   = 20;
				 int maxNumWords = 10;
				 int maxDepth    = 16;           
				 int maxLength   =  4;
				 stat += test_time_getProbable(trie, 2, numTrials, maxNumWords, maxDepth, maxLength, 0);
				 //            stat += test_time_getProbable(trie, 1, numTrials, maxNumWords, maxDepth, maxLength, 0);
				 //            stat += test_time_getProbable(trie, 0, numTrials, maxNumWords, maxDepth, maxLength, 0);
				 //            stat += test_time_getProbable(trie, 3, numTrials, maxNumWords, maxDepth, maxLength, 0);

				 SaveMax.setCountIsFull(SaveMax.sCountNotFull = 0);
				 stat += test_time_getProbable(trie, 1, numTrials, maxNumWords, maxDepth, 2, 0);
				 SaveMax.setCountIsFull(SaveMax.sCountNotFull = 0);            
				 stat += test_time_getProbable(trie, 1, numTrials, maxNumWords, maxDepth, 3, 0);
				 SaveMax.setCountIsFull(SaveMax.sCountNotFull = 0);
				 stat += test_time_getProbable(trie, 1, numTrials, maxNumWords, maxDepth, 4, 0);
				 SaveMax.setCountIsFull(SaveMax.sCountNotFull = 0);
				 stat += test_time_getProbable(trie, 1, numTrials, maxNumWords, maxDepth, 5, 0);

				 Sx.puts(WordTrie.class.getName() + ".time_test   ...end");
			 }
			 if (level == 5) {
				 stat = test_timeHasWordStrVsChr(trie);
			 }
		 }

		 return stat;
	 }


	 /**
	  * TODO: time puts vs gets, STR vs CHR
	  * @param args
	  */
	 public static void main(String[] args) {  
		 unit_test(2);
	 }

	 public int getNumWords() {
		 return mNumWords;
	 }
}
