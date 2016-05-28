package sprax.grids;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import sprax.Sx;
import sprax.tries.WordTrie;

/**
 * Boggle board games use a rectangular grid and neighbor-link nodes.
 * 
 * TODO: More efficient ways to prevent self-intersecting paths,
 * e.g. and iterator that begins right after the previous or
 * "from" node.
 */
/**
 * BoggleNode
 */
class BoggleNode extends GridNodeChar
{
    Set<String> mFoundWords;  // methods may implement this as a HashSet.
}

public class BoggleBoard extends RectGrid8<Character, BoggleNode>
{
    public static int      sMinWordLen     = 3;
    public static String   sDictionaryPath = "text/En/words.txt";
    public static WordTrie sWordTrie       = new WordTrie();
    
    public static void setDictionaryPath(String dictPath) {
        sDictionaryPath = dictPath;
    }
    
    protected BoggleNode       mNodes[][];
    
    int                        mMinWordLen;
    int                        mMaxWordLen;
    int                        mMaxFoundLen;
    SortedMap<String, Integer> mFoundWords;
    
    BoggleBoard(int numRows, int numCols, int minWordLen, int maxWordLen)
    {
        super(numRows, numCols);
        mMinWordLen = minWordLen;
        mMaxWordLen = maxWordLen;
        mFoundWords = new TreeMap<String, Integer>();
        initWordsFromDictionaryFile(mMinWordLen, mMaxWordLen, sDictionaryPath);
    }
    
    BoggleBoard(int numRows, int numCols)
    {
        this(numRows, numCols, sMinWordLen, numRows * numCols);
    }
    
    @Override
    BoggleNode[] getNodes(int row) {
        return mNodes[row];
    }
    
    @Override
    BoggleNode[][] getNodes()
    {
        return mNodes;
    }
    
    @Override
    void setNode(BoggleNode node, int row, int col) {
        mNodes[row][col] = node;
    }
    
    @Override
    protected void createNodes()
    {
        mNodes = new BoggleNode[mNumRows][];
        for (int row = 0; row < mNumRows; row++) {
            mNodes[row] = new BoggleNode[mNumCols];
            for (int col = 0; col < mNumCols; col++) {
                mNodes[row][col] = createNode(row, col);
            }
        }
    }
    
    public void initWordsFromDictionaryFile(int minWordLen, int maxWordLen, String dictionaryPath)
    {
        sWordTrie.initFromSortedDictionaryFile(minWordLen, maxWordLen, dictionaryPath, 1);
    }
    
    @Override
    BoggleNode createNode(int row, int col) {
        return new BoggleNode();
    }
    
    public void setLetters(String letterString)
    {
        if (letterString != null)
            setLetters(letterString.toCharArray());
    }
    
    public void setLetters(char letters[])
    {
        if (letters == null || letters.length == 0)
            return;
        
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                mNodes[row][col].setIntVal(letters[(mNumCols * row + col) % letters.length]);
            }
        }
    }
    
    public void setLetters(char letterGrid[][])
    {
        // top-level null check
        if (letterGrid == null || letterGrid.length < mNumRows)
            return;
        // no checking of rows...
        for (int row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                BoggleNode node = mNodes[row][col];
                node.setData(letterGrid[row][col]);
            }
        }
    }
    
    public void printBoard(String label)
    {
        Sx.print(getClass().getName() + mNumRows + "x" + mNumCols + ": ");
        Sx.puts(label);
        for (int row = 0; row < mNumRows; row++) {
            Sx.putsArray("     ", mNodes[row]);
        }
        Sx.puts();
    }
    
    @Override
    BoggleNode getNode(int row, int col)
    {
        return mNodes[row][col];
    }
    
    public void findWordsStartingFromRowCol(int row, int col)
    {
        // Error checking?
        // assert(0 <- row && row < mNumRows && 0 <= col && col < mNumCols) ...
        BoggleNode boggleNode = getNode(row, col);
        findWordsStartingFromNode(boggleNode);
    }
    
    /** no input error checking AND stores path in an ArrayList */
    protected void findWordsStartingFromNode_list(BoggleNode boggleNode)
    {
        final WordTrie.WordNode wordTrieNode = sWordTrie.getRootBranch(boggleNode.mData);
        if (wordTrieNode != null) {     // TODO: can eliminate this if all root branches exist, i.e.
                                    // every letter begins at least one word.
            ArrayList<GridNode<Character>> path = new ArrayList<GridNode<Character>>(mMaxWordLen);
            path.add(boggleNode);
            boggleNode.mFoundWords = new HashSet<String>();
            findWordsFromPath_list(boggleNode.mFoundWords, path, 0, wordTrieNode);
        }
    }
    
    /**
     * resets mFoundWords by creating a new Set.
     * Uses an ArrayList to store the path, rather than a simple array.
     * 
     * @param path
     * @param prevNode
     * @param words
     */
    public void findWordsFromPath_list(Set<String> originNodeFoundWords,
            List<GridNode<Character>> path, int pathIdx, final WordTrie.WordNode prevNode)
    {
        GridNode<Character> node = path.get(pathIdx);
        int pathIdxPlus1 = pathIdx + 1;             // don't use ++pathIdx !
        for (GridNode<Character> neighbor : node.mNeighbors) {
            int neighborIdx = path.indexOf(neighbor); // FIXME: searching entire path, tho only
                                                      // beginning is valid
            // skip this neighbor if it is already in the path
            if (0 <= neighborIdx && neighborIdx <= pathIdx)
                continue;
            final WordTrie.WordNode nextNode = prevNode.getBranchAtLetter(neighbor.mData);
            if (nextNode != null) {                   // path + neighbor is a valid stem
                final String word = nextNode.getWord();
                if (word != null) {                     // path + neighbor is already a word
                    originNodeFoundWords.add(word);
                    this.mFoundWords.put(word, 1);
                }
                if (pathIdxPlus1 < path.size())
                    path.set(pathIdxPlus1, neighbor);
                else
                    path.add(neighbor);
                findWordsFromPath_list(originNodeFoundWords, path, pathIdxPlus1, nextNode);
            }
        }
        return;
    }
    
    /** no input error checking AND stores path in an ArrayList */
    protected void findWordsStartingFromNode(BoggleNode boggleNode)
    {
        final WordTrie.WordNode wordTrieNode = sWordTrie.getRootBranch(boggleNode.mData);
        if (wordTrieNode != null) {     // TODO: can eliminate this if all root branches exist, i.e.
                                    // every letter begins at least one word.
            BoggleNode path[] = new BoggleNode[mMaxWordLen];
            path[0] = boggleNode;
            boggleNode.mFoundWords = new HashSet<String>();
            findWordsFromPath(boggleNode.mFoundWords, path, 0, wordTrieNode);
        }
    }
    
    /**
     * resets mFoundWords by creating a new Set.
     * Uses an ArrayList to store the path, rather than a simple array.
     * 
     * @param path
     * @param prevNode
     * @param words
     */
    public void findWordsFromPath(Set<String> originNodeFoundWords, GridNode<Character>[] path,
            int pathIdx, final WordTrie.WordNode prevNode)
    {
        GridNode<Character> node = path[pathIdx];
        int pathIdxPlus1 = pathIdx + 1;             // don't use ++pathIdx !
        Neighbors: for (GridNode<Character> neighbor : node.mNeighbors) {
            for (int j = pathIdx; --j >= 0;)
                if (path[j] == neighbor)
                    continue Neighbors;                             // skip this neighbor if it is already in the path
            final WordTrie.WordNode nextNode = prevNode.getBranchAtLetter(neighbor.mData);
            if (nextNode != null) {                   // path + neighbor is a valid stem
                final String word = nextNode.getWord();
                if (word != null) {                     // path + neighbor is already a word
                    originNodeFoundWords.add(word);
                    int count = mFoundWords.containsKey(word) ? mFoundWords.get(word) : 0;
                    this.mFoundWords.put(word, count + 1);
                    if (mMaxFoundLen < word.length())
                        mMaxFoundLen = word.length();
                }
                path[pathIdxPlus1] = neighbor;
                findWordsFromPath(originNodeFoundWords, path, pathIdxPlus1, nextNode);
            }
        }
        return;
    }
    
    public void findAllWords()
    {
        mFoundWords.clear();
        mMaxFoundLen = 0;
        for (int q = 0, row = 0; row < mNumRows; row++) {
            for (int col = 0; col < mNumCols; col++) {
                findWordsStartingFromRowCol(row, col);
                if (sDbg > 2) {
                    BoggleNode bn = getNode(row, col);
                    for (String word : bn.mFoundWords) {
                        Sx.puts(++q + "\t" + word);
                    }
                }
            }
        }
    }
    
    public void showAllWords(int nShowCols)
    {
        int q = 0;
        String ends = null;
        String formats = String.format("%%%ds %%s%%s", mMaxFoundLen);
        for (Map.Entry<String, Integer> pair : mFoundWords.entrySet()) {
            String word = pair.getKey();
            int count = pair.getValue();
            if (++q == nShowCols) {
                q = 0;
                ends = "\n";
            } else {
                ends = "  ";
            }
            System.out.format(formats, word, (count > 1 ? count : " "), ends);
        }
        Sx.puts();
    }
    
    public static int unit_test()
    {
        int nRows = 4, nCols = 4;
        BoggleBoard bb = new BoggleBoard(nRows, nCols);
        char dataGrid[][] =
        { { 'A', 'B', 'C', 'D', 'E', 'F' }
                , { 'G', 'H', 'I', 'J', 'K', 'L' }
                , { 'M', 'N', 'O', 'P', 'Q', 'R' }
                , { 'S', 'T', 'U', 'V', 'W', 'X' } };
        bb.setLetters(dataGrid);
        bb.printBoard("dataGrid");
        bb.setLetters("toofew");
        bb.printBoard("from literal");
        
        String tryWord = "improbable";
        Sx.puts("hasWord(" + tryWord + ") ?  " + sWordTrie.contains(tryWord));
        
        int nShowCols = 8;
        String str = "dibyuamlncpekors";
        bb.setLetters(str);
        bb.printBoard("from string: " + str);
        bb.findAllWords();
        Sx.puts("Unique words found: " + bb.mFoundWords.size());
        bb.showAllWords(nShowCols);
        
        str = "dibeiamlnbpecors";
        bb.setLetters(str);
        bb.printBoard("from string: " + str);
        bb.findAllWords();
        Sx.puts("Unique words found: " + bb.mFoundWords.size());
        bb.showAllWords(nShowCols);
        return 0;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
    
}
