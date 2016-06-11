package sprax.trees;

import java.util.LinkedList;
import java.util.Queue;

import sprax.sprout.Sx;

/*************************************************************************
 *
 *  Symbol table implemented by a binary search tree.
 * 
 *
 *************************************************************************/


///////////// Visitors //////////////////////////////////////////////////////

interface BinLinkStVisitor<Key extends Comparable<Key>, Val> {
	public void visit(BstInterface<Key, Val> node);
}

interface BinLinkStVisitor1<Key extends Comparable<Key>, Val> {
	public void visit(BstInterface<Key, Val> node, int param1);
}

class BinLinkStPrinter<Key extends Comparable<Key>, Val> implements BinLinkStVisitor<Key, Val>
{

	@Override
	public void visit(BstInterface<Key, Val> node) {
		// TODO Auto-generated method stub

	}

}

@SuppressWarnings("synthetic-access")   // TODO -- use accessors for left & right or not?

public class BinST<Key extends Comparable<Key>, Val>
{
	class Node implements BstInterface<Key, Val>
	{
		private Key key;                // sorted by key
		private Val val;                // associated data
		private Node left;    // left subtrees
		private Node right;   // right subtrees
		private int N;                  // number of nodes in subtree

		public Node(Key key, Val val, int N) {
			this.key = key;
			this.val = val;
			this.N = N;
		}

		@Override
		public Node left()   { return left; }
		@Override
		public Node right()  { return right; }
		@Override
		public Key key()     { return key; }
		@Override
		public Val val()     { return val; }

		public String toString()    { return "[" + key + "  " + val + "]"; }

		public int height() {
			return 1 + Math.max((left==null ? 0 : left.height()), (right==null ? 0 : right.height()));
		}

		public void visitLevelOrderWithNulls(NodeVisitor visitor) 
		{
			Queue<Node> queue = new LinkedList<Node>();
			queue.add(this);
			int numNonNull = 1;
			do {
				Node node = queue.remove();
				if (node != null)
					numNonNull--;
				visitor.visit(node);
				if (node != null) {
					if (node.left != null) {
						numNonNull++;
						queue.add(node.left);
					} else {
					    queue.add(null);
					}
					if (node.right != null) {
						numNonNull++;
						queue.add(node.right);
                    } else {
                        queue.add(null);
                    }
				} else {
					queue.add(null);        
					queue.add(null);        
				}
			} while ( ! queue.isEmpty() && numNonNull > 0);
		}

	}


	abstract class NodeVisitor {
		public abstract void visit(Node node);
	}


	protected class LevelOrderSpacedPrinter extends NodeVisitor 
	{
	    int mNodeLength;
	    int mLineLength;
	    int mSpacing;
		int mMaxIndent;            // default value
		int mPrevDepth =  0;            // depth increases as we go down
		int mPowerOf2  =  1;
		int mNumVisited;
		int mNumBeforeBreak = 1, mNumNextLine = 1;
		LevelOrderSpacedPrinter(int height, int nodeLength)  
		{
			if (nodeLength < 1)
				nodeLength = 1;
            mNodeLength = nodeLength;
			mLineLength = (1 << height) * (1 + mNodeLength);
			mSpacing    = mLineLength / 2;
		}
		private void printNonNullNode(Node node) 
		{
			// Sx.print(node.toString());
			Sx.print(node.key);
		}

		@Override
		public void visit(Node node) 
		{
            Sx.space(mSpacing - mNodeLength);
            if (node == null) {
                Sx.space(mNodeLength);
            } else {
                printNonNullNode(node);
            }
            
            if (++mNumVisited == mNumBeforeBreak) {
		        Sx.puts();
		        mSpacing = mSpacing / 2;
		        if (mSpacing < 1)
		            mSpacing = 1;
                mNumNextLine = mNumNextLine << 1;
                mNumBeforeBreak += mNumNextLine;
		    } else {
		        Sx.space(mSpacing);
		    }
		}
	}


	public void printLevelOrderSpaced(Node node, int nodeHeight) 
	{
		NodeVisitor printer = new LevelOrderSpacedPrinter(nodeHeight, 0);
		node.visitLevelOrderWithNulls(printer);
		System.out.println();    
	}


	class NodePrinter extends NodeVisitor
	{
		@Override
		public void visit(Node node) {
			if (node != null)
				Sx.print(" " + node);        
			else
				Sx.print("     ");
		}
	}

	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	private Node root;             // root of BST


	// is the symbol table empty?
	public boolean isEmpty() {
		return size() == 0;
	}

	// return number of key-value pairs in BST
	public int size() {
		return size(root);
	}

	// return number of key-value pairs in BST rooted at x
	private int size(Node x) {
		if (x == null) 
			return 0;
		else 
			return x.N;
	}

	/***********************************************************************
	 *  Search BST for given key, and return associated value if found,
	 *  return null if not found
	 ***********************************************************************/
	// does there exist a key-value pair with given key?
	public boolean contains(Key key) {
		return get(key) != null;
	}

	// return value associated with the given key, or null if no such key exists
	public Val get(Key key) {
		return get(root, key);
	}

	private Val get(Node x, Key key) {
		if (x == null) return null;
		int cmp = key.compareTo(x.key);
		if      (cmp < 0) return get(x.left, key);
		else if (cmp > 0) return get(x.right, key);
		else              return x.val;
	}

	/***********************************************************************
	 *  Insert key-value pair into BST
	 *  If key already exists, update with new value
	 ***********************************************************************/
	public void put(Key key, Val val) {
		if (val == null) { delete(key); return; }
		root = put(root, key, val);
		assert check();
	}

	private Node put(Node x, Key key, Val val) {
		if (x == null) return new Node(key, val, 1);
		int cmp = key.compareTo(x.key);
		if      (cmp < 0) x.left  = put(x.left,  key, val);
		else if (cmp > 0) x.right = put(x.right, key, val);
		else              x.val   = val;
		x.N = 1 + size(x.left) + size(x.right);
		return x;
	}

	/***********************************************************************
	 *  Delete
	 ***********************************************************************/

	public void deleteMin() {
		if (isEmpty()) throw new RuntimeException("Symbol table underflow");
		root = deleteMin(root);
		assert check();
	}

	private Node deleteMin(Node x) {
		if (x.left == null) return x.right;
		x.left = deleteMin(x.left);
		x.N = size(x.left) + size(x.right) + 1;
		return x;
	}

	public void deleteMax() {
		if (isEmpty()) throw new RuntimeException("Symbol table underflow");
		root = deleteMax(root);
		assert check();
	}

	private Node deleteMax(Node x) {
		if (x.right == null) return x.left;
		x.right = deleteMax(x.right);
		x.N = size(x.left) + size(x.right) + 1;
		return x;
	}

	public void delete(Key key) {
		root = delete(root, key);
		assert check();
	}

	private Node delete(Node x, Key key) {
		if (x == null) return null;
		int cmp = key.compareTo(x.key);
		if      (cmp < 0) x.left  = delete(x.left,  key);
		else if (cmp > 0) x.right = delete(x.right, key);
		else { 
			if (x.right == null) return x.left;
			if (x.left  == null) return x.right;
			Node t = x;
			x = min(t.right);
			x.right = deleteMin(t.right);
			x.left = t.left;
		} 
		x.N = size(x.left) + size(x.right) + 1;
		return x;
	} 


	/***********************************************************************
	 *  Min, max, floor, and ceiling
	 ***********************************************************************/
	public Key min() {
		if (isEmpty()) return null;
		return min(root).key;
	} 

	private Node min(Node x) { 
		if (x.left == null) return x; 
		else                return min(x.left); 
	} 

	public Key max() {
		if (isEmpty()) return null;
		return max(root).key;
	} 

	private Node max(Node x) { 
		if (x.right == null) return x; 
		else                 return max(x.right); 
	} 

	public Key floor(Key key) {
		Node x = floor(root, key);
		if (x == null) return null;
		else return x.key;
	} 

	private Node floor(Node x, Key key) {
		if (x == null) return null;
		int cmp = key.compareTo(x.key);
		if (cmp == 0) return x;
		if (cmp <  0) return floor(x.left, key);
		Node t = floor(x.right, key); 
		if (t != null) return t;
		else return x; 
	} 

	public Key ceiling(Key key) {
		Node x = ceiling(root, key);
		if (x == null) return null;
		else return x.key;
	}

	private Node ceiling(Node x, Key key) {
		if (x == null) return null;
		int cmp = key.compareTo(x.key);
		if (cmp == 0) return x;
		if (cmp < 0) { 
			Node t = ceiling(x.left, key); 
			if (t != null) return t;
			else return x; 
		} 
		return ceiling(x.right, key); 
	} 

	/***********************************************************************
	 *  Rank and selection
	 ***********************************************************************/
	public Key select(int k) {
		if (k < 0 || k >= size()) 
		    return null;
		Node x = select(root, k);
		return x.key;
	}

	// Return key of rank k. 
	private Node select(Node x, int k) {
		if (x == null) 
		    return null; 
		int t = size(x.left); 
		if      (t > k) return select(x.left,  k); 
		else if (t < k) return select(x.right, k-t-1); 
		else            return x; 
	} 

	public int rank(Key key) {
		return rank(key, root);
	} 

	// Number of keys in the subtree less than x.key. 
	private int rank(Key key, Node x) {
		if (x == null) 
		    return 0; 
		int cmp = key.compareTo(x.key); 
		if      (cmp < 0) return rank(key, x.left); 
		else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right); 
		else              return size(x.left); 
	} 

	/***********************************************************************
	 *  Range count and range search.
	 ***********************************************************************/
	public Iterable<Key> keys() {
		return keys(min(), max());
	}

	public Iterable<Key> keys(Key lo, Key hi) {
		Queue<Key> queue = new LinkedList<Key>();
		keys(root, queue, lo, hi);
		return queue;
	} 

	private void keys(Node x, Queue<Key> queue, Key lo, Key hi) { 
		if (x == null) return; 
		int cmplo = lo.compareTo(x.key); 
		int cmphi = hi.compareTo(x.key); 
		if (cmplo < 0) keys(x.left, queue, lo, hi); 
		if (cmplo <= 0 && cmphi >= 0) queue.add(x.key); 
		if (cmphi > 0) keys(x.right, queue, lo, hi); 
	} 

	public int size(Key lo, Key hi) {
		if (lo.compareTo(hi) > 0) return 0;
		if (contains(hi)) return rank(hi) - rank(lo) + 1;
		else              return rank(hi) - rank(lo);
	}


	// height of this BST (one-node tree has height 0)
	public int height() { return height(root); }

	public int height(Node x) 
	{
		if (x == null)
			return 0;
		return 1 + Math.max( height(x.left), height(x.right) );
	}       



	// level order traversal
	public Iterable<Key> levelOrder() {
		Queue<Key> keys = new LinkedList<Key>();
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(root);
		while (!queue.isEmpty()) {
			Node x = queue.remove();
			if (x == null) 
				continue;
			keys.add(x.key);
			queue.add(x.left);
			queue.add(x.right);
		}
		return keys;
	}
	
	public static void addRangeBsOrder(BinST<String, Integer> tree, int lo, int hi)
	{   
	    if (lo > hi)
	        return;
	    
	    int md = lo + hi >> 1;
        char ch[] = { (char) (md + 'A') };
        tree.put(new String(ch), md+1);
        addRangeBsOrder(tree, lo, md-1);
        addRangeBsOrder(tree, md+1, hi);
	}
	
	


	/*************************************************************************
	 *  Check integrity of BST data structure
	 *************************************************************************/
	private boolean check() {
		if (!isBST())            Sx.puts("Not in symmetric order");
		if (!isSizeConsistent()) Sx.puts("Subtree counts not consistent");
		if (!isRankConsistent()) Sx.puts("Ranks not consistent");
		return isBST() && isSizeConsistent() && isRankConsistent();
	}

	// does this binary tree satisfy symmetric order?
	// Note: this test also ensures that data structure is a binary tree since order is strict
	private boolean isBST() {
		return isBST(root, null, null);
	}

	// is the tree rooted at x a BST with all keys strictly between min and max
	// (if min or max is null, treat as empty constraint)
	// Credit: Bob Dondero's elegant solution
	private boolean isBST(Node x, Key min, Key max) {
		if (x == null) return true;
		if (min != null && x.key.compareTo(min) <= 0) return false;
		if (max != null && x.key.compareTo(max) >= 0) return false;
		return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
	} 

	// are the size fields correct?
	private boolean isSizeConsistent() { return isSizeConsistent(root); }
	private boolean isSizeConsistent(Node x) {
		if (x == null) return true;
		if (x.N != size(x.left()) + size(x.right()) + 1) 
			return false;
		return isSizeConsistent(x.left) && isSizeConsistent(x.right);
	} 

	// check that ranks are consistent
	private boolean isRankConsistent() {
		for (int i = 0; i < size(); i++)
			if (i != rank(select(i))) return false;
		for (Key key : keys())
			if (key.compareTo(select(rank(key))) != 0) return false;
		return true;
	}


	/*****************************************************************************
	 *  Test client
	 *****************************************************************************/

	public static int unit_test(int level) 
	{
		String  testName = BinLink.class.getName() + ".unit_test";
		Sx.puts(testName + " BEGIN");    

		int stat = 0, lo = 0, hi = 62;
		BinST<String, Integer> bst = new BinST<String, Integer>();
		addRangeBsOrder(bst, lo, hi);
		if ( ! bst.check())
			stat -= 1;

		Sx.puts("\nKey-sorted order:");
		for (String key : bst.keys())
			Sx.format("[%s %2d] ", key,  bst.get(key));
		Sx.puts();

		Sx.puts("\nTree-level order:");
		for (String key : bst.levelOrder())
			Sx.format("[%s %2d] ", key,  bst.get(key));
		Sx.puts();

		Sx.puts("\nTree-spaced order:");
		int height = bst.root.height();
		bst.printLevelOrderSpaced(bst.root, height);
		Sx.puts();

		Sx.puts(testName + " END");    
		return stat;
	}

	public static void main(String[] args)    { unit_test(1); }

}


