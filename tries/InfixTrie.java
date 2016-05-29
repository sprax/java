package sprax.tries;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Design alternatives:
 *		1.	Redundant tree
 *		2.	DAG 
 *		3.  prefixTrie AND suffixTrie (starting from shorter side)
 *		4.	Trie with wildcard character.  
 *					A letter node has 0 to 26 childredn 
 *					A wildcard node also has 0 to 26 children: the union of all letter nodes' children
 *
 * @author sprax
 *
 */
public class InfixTrie 
{
    public static int sDbg = 1; // TODO remove sDbg
    int zoid, woid; // FIXME: remove
    
    public static final int sFirstLetter  = 'a';
    public static final int sLastLetter   = 'z';
    public static final int sNumLetters   =  1 + sLastLetter - sFirstLetter;
    
    protected final InfixNode mRoot = new InfixNode('\0', null, 0); // mRoot cannot be null, so don't check for it.
    protected int   mNumWords       =  0;   // total number of words stored in the trie
    protected int   mNumNodes       =  1;   // total number of nodes stored in the trie
    protected int   mActMinWordLen  = 99;   // Actual minimum word length (or default value), reset during loading
    protected int   mActMaxWordLen  =  0;   // Actual max word length (or default value), reset during loading
    protected int   mReqMinWordLen  =  1;   // Required minimum word length; smaller words not kept; change this before loading
    protected int   mReqMaxWordLen  = 28;   // Required maximum word length, or max allowed len; change this before loading
    private String  mTextFilePath   = null; // Un-set at beginning of loading; set at end.
    /** Auxiliary map for testing auto-complete, etc. TODO: keep this? */
    protected Map<String, InfixNode>   mNewInfixNodes = new TreeMap<String, InfixNode>();
    //protected Map<InfixNode, Integer>  mPathCounts   = new HashMap<InfixNode, Integer>();
    
    protected int mNumPutChars;           // Total number of chars input when adding words
    protected int mNumNewWords;           // Reset every time we add words from a file
    protected boolean mTotalTextCountsUpToDate = false;
    
    public class InfixNode implements Comparable<InfixNode>
    {                                   // TODO: implements Iterator?
        /** All possible continuations, as an ordered set indexed by letter (an array, in fact). */
        private final InfixNode mBranches[];
        
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
        private InfixNode mFirstChild;

        /** The next (non-null) continuation when mBranches is used as a linked list */
        private InfixNode mNextSibling;
        // private InfixNode mFirstInfixNode;
        /** Assumes fixed length; otherwise, the next branch depends on the desired word length */
        // private InfixNode mNextJumpBranch;   // Useful for crossword puzzles, word rectangles, etc.  Not currently in use.
        
        /** Constructs non-root word node; parent cannot be null */
        private InfixNode(char letter, final String stem, int depth, final InfixNode parent) 
        {
            if (stem == null || depth < 1 || parent == null)
                throw new IllegalArgumentException();
            mBranches    = new InfixNode[sNumLetters];
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
                InfixNode prevBranch, nextBranch; 
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
        InfixNode(char letter, final String stem, int depth) 
        {
            mBranches = new InfixNode[sNumLetters];
            mStem     = stem;
            mDepth    = depth;
            mChar     = letter;
            //mIsWord   = false;  // already false by Java default
            
            mFirstChild = null;
            mNextSibling  = null;
        }
        
        public final InfixNode getFirstBranch()  { return mFirstChild; }
        public final InfixNode getNextBranch()   { return mNextSibling; } // TODO: template-ize ??
        //public final InfixNode getFirstInfixNode(){ return mFirstInfixNode; }
        //public final String   getStem()       { return mStem; }     // First word containing this stem
        public final boolean  isWord()          { return mWordCount > 0; }   // true IFF this is a word-node
        public final String   getWord()         { return isWord() ? mStem : null; }     // null, if this is not a word-node
        public final String   getStem()         { return isWord() ? mStem : mStem.substring(0, mDepth); }
        //public final int    getDepth()        { return mDepth; }
        //public final int    getChar()         { return mChar;  }    // == mStem.charAt(mDepth-1)
        public final int      getLetterIndex()  { return mChar - sFirstLetter; }
        //public final String   getLetter()       { return mStem.substring(mDepth-1, mDepth); }
        //public final String   getLetterAt(int x){ assert(0 <= x && x < sNumLetters); return mStem.substring(x); }
        //  public InfixNode       getBranchAtIndex(int index) {
        //    assert(0 <= index && index <= sNumLetters);
        //    return mBranches[index];                                     // may return null 
        //  }
        
        public String toString() { 
            return String.format("[%s %d %c %s w:%d t:%d]", mStem, mDepth, mChar, (isWord() ? "T":"F"), mWordCount, mTextCount);
        }
        
        public final InfixNode getBranchAtLetter(int letter) {
            assert(sFirstLetter <= letter && letter <= sLastLetter);
            return mBranches[letter - sFirstLetter];                     // may return null 
        }
        //    public final InfixNode getNextBranchFromLetter(int letter)  {
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
            for (InfixNode node = mFirstChild; node != null; node = node.mNextSibling ) {
                node.printWords();
            }
        }


        /**
         * Sorts nodes first by word count, then by text count 
         * if the word counts are the same.  Thus nodeA > nodeB
         * if nodeA.mWordCount > nodeB.mWordCount, of if 
         * nodeA.mWordCount == nodeB.mWordCount but 
         * nodeA.mTextCount >  nodeB.mTextCount
         * Contrast this with the more "natural" ordering, which 
         * is alphabetical by stem.
         * @see InfixNode.CompareAlpha and InfixNode.CompareCounts
         */
        @Override
        public int compareTo(InfixNode other) {
            if (mWordCount == other.mWordCount)
                return mTextCount - other.mTextCount;
            return mWordCount - other.mWordCount;
        }
        
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
    public InfixNode putWord(final String word)
    {
        int wordLen = word.length();
        if (badWordLen(wordLen))
            return null;
        
        mNumPutChars += wordLen;
        
        // Traverse down the trie until we reach the end of the word, creating nodes as necessary.
        InfixNode node = mRoot;
        for (int j = 0; j < wordLen; j++) {
            char cLetter = word.charAt(j);
            int  iLetter = cLetter - sFirstLetter;
            //assert(0 <= iLetter && iLetter < sNumLetters);
            if (node.mBranches[iLetter] == null) { 
                // create new branch node as a child with a back pointer to its parent.
                node.mBranches[iLetter] =  new InfixNode(cLetter, word, j+1, node);
                mNumNodes++;
            }
            node = node.mBranches[iLetter];
//            
//            if (sDbg > 0) {
//                int sum = 0, dif = node.mTextCount - node.getWordCount();
//                for (InfixNode br = node.mFirstChild; br != null; br = br.mNextSibling)
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
        //            for (InfixNode br = node.mFirstChild; br != null; br = br.mNextSibling)
        //                sum += br.mTextCount;
        //            if (sum != dif)
        //                zoid++;
        //        }
        
        return node;
    }
    
    ArrayList<String> getWords(char wordKey[], int wordLen) 
    {
    	ArrayList<String> words = new ArrayList<String>();
    	for (int j = 0; j < wordLen; j++) {
    		;
    	}
    	return words;
    }
    
}
