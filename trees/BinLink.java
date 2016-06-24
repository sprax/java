package sprax.trees;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import sprax.sprout.Spaces;
import sprax.sprout.Sx;
import sprax.shuffles.Shuffler;

/** 
 * TODO:
 * Make SpacedHeightPrinter
 * @author sprax
 *
 */

public class BinLink 
{
    static int sAlphabet[] = { 
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };
    BinLink mLeft   = null;
    BinLink mRight  = null;
    int     mKey    = 0;
    
    public BinLink left()  { return mLeft;  } 
    public BinLink right() { return mRight; }
    
    //public BinLink() { }                                      // default constructor
    BinLink(int d) { mKey = d; }                                // key-only constructor
    public BinLink(int key, BinLink left, BinLink right) {      // complete constructor
        mKey  = key;
        mLeft  = left;
        mRight = right;
    }
 
    @Override
    public
    String toString() {
        return String.format("[%d  L:%s  R:%s]", mKey, (mLeft == null ? "#" : mLeft.mKey), (mRight == null ? "nil" : mRight.mKey));
    }
    
    public char toLetter() {
        return (char)(mKey % 26 + 'A');
    }
    
    public char toChar() {
        return (char)(mKey % (255 - 'A') + 'A');
    }
    
    public char toHexDigit() {
        int dig = mKey % 16;
        if (dig < 0)
            dig += 16;
        if (dig < 10)
            return (char)(dig + '0');
        else 
            return (char)(dig + ('A' - 10));
    }
    
    void printSpacedChar() {
        System.out.print(" " + toChar());
    }   
    
    
    void printSpacedHex() {
        System.out.print(" " + toHexDigit());
    }   
    
  public int getNumNodes() {
    return 1 + getNumDescendents();
  }
    
    public int getNumDescendents()
    {
        return (mLeft == null ? 0 : 1 + mLeft.getNumDescendents()) +
                (mRight == null ? 0 : 1 + mRight.getNumDescendents());
    }
  
  public static int getNumDescendents(BinLink b) {
    if (b == null) 
      return 0;
    return 1 + getNumDescendents(b.mLeft) + getNumDescendents(b.mRight);
  }
  
  
  public int getDepth() {
      return 1 + Math.max( (mLeft  == null ? 0 : mLeft.getDepth())
              ,            (mRight == null ? 0 : mRight.getDepth()));
  }
  
  public static int getDepth(BinLink b) {
      if (b == null)
          return 0;
      return 1 + Math.max( getDepth(b.mLeft), getDepth(b.mRight) );
  }
  
  public void insertBST(int key)
  {
    if (key < mKey) {
      if (mLeft == null)
        mLeft = new BinLink(key);
      else
        mLeft.insertBST(key);
    } 
    else if (key > mKey) {
      if (mRight == null)
        mRight = new BinLink(key);
      else
        mRight.insertBST(key);
    }
    // else if (key == this.key), just ignore it.  No duplicate key nodes.
  }
  

  public static BinLink growBreadthFirstLevelOrder(int rootData, int count) {
    if (count < 0)
      return null;
    BinLink   root = new BinLink(rootData);
    LinkedList<BinLink> queue = new LinkedList<BinLink>();
    queue.add(root);
    // add every new node to queue, but break out of loop as soon as 
    // count nodes have been added to the tree.  So the queue gets bigger
    // than it needs to be, and is left for garbage collection.
    for (int j = 1; j < count; j++) {
      BinLink node = queue.remove();
      node.mLeft  = new BinLink(rootData + j);
      if (++j == count)
        break;
      queue.add(node.mLeft);
      node.mRight = new BinLink(rootData + j);
      queue.add(node.mRight);
    }
    return root;
  }   
  
  public static BinLink growBreadthFirstLevelOrderMini(int rootData, int count) {
    if (count < 1)
      return null;  // A tree with less than 1 node is null
    BinLink root = new BinLink(rootData);
    LinkedList<BinLink> queue = new LinkedList<BinLink>();
    queue.add(root);
    int j = 0, last = count - 1;
    while ( ! queue.isEmpty()) {
      if (++j > last)
        break;
      BinLink node = queue.remove();
      node.mLeft  = new BinLink(rootData + j);
      if (j*2 < last) 
        queue.add(node.mLeft);   // If this no will have at least a left child, enqueue it.
      if (++j > last)
        break;
      node.mRight = new BinLink(rootData + j);
      if (j*2 < last)
        queue.add(node.mRight);  // If this no will have at least a left child, enqueue it.
    }
    return root;
  }   
  
  
  
  public static BinLink growRandomBinTreeRecurse(int depth, Random rng, int n) {
    BinLink b = new BinLink(rng.nextInt(n), null, null);
    if (depth-- > 0) {
      b.mLeft  = growRandomBinTreeRecurse(depth, rng, n);
      b.mRight = growRandomBinTreeRecurse(depth, rng, n);
    }   // otherwise, b.left = b.right = null (by construction)
    return b;
  }
  
  public static BinLink initAlphabet(int length) {
    assert(length > 0);
    length--;
    BinLink head = new BinLink('A' + length, null, null);
    while(--length >= 0) {
      BinLink temp = new BinLink('A' + length, head, null);
      head.mLeft = temp;
      head = temp;
    }
    head.mLeft = null;
    return head;
  }
  
  public static void printList(BinLink link) {
    while (link != null) {
      link.printSpacedChar();
      link = link.mRight;
    }
    System.out.println();
  }
  
  /**
   * static method operates recursively on a binary tree
   */
  public static void printTreeDepthFirstRecursivePreOrder(BinLink head)
  {
	  if (head != null) {
		  head.printSpacedChar();
		  printTreeDepthFirstRecursivePreOrder(head.mLeft);
		  printTreeDepthFirstRecursivePreOrder(head.mRight);
	  }
  }
  
  /**
   * non-static member method operates on self and children recursively
   */
  public void printDepthFirstRecursivePreOrder() {
    this.printSpacedChar();
    if (mLeft != null) {
      mLeft.printDepthFirstRecursivePreOrder();
    }
    if (mRight != null) {
      mRight.printDepthFirstRecursivePreOrder();
    }
  }
  
  public void printDepthFirstRecursiveInOrder() {
    if (mLeft != null) {
      mLeft.printDepthFirstRecursiveInOrder();
    }
    this.printSpacedChar();
    if (mRight != null) {
      mRight.printDepthFirstRecursiveInOrder();
    }
  }
  
  public void printDepthFirstRecursivePostOrder() {
    if (mLeft != null) {
      mLeft.printDepthFirstRecursivePostOrder();
    }
    if (mRight != null) {
      mRight.printDepthFirstRecursivePostOrder();
    }
    this.printSpacedChar();
  }
  
  
  public boolean verifyDepthFirstRecursiveInOrder(NodePredicate<BinLink> predicate) 
  {
    if (mLeft != null && ! mLeft.verifyDepthFirstRecursiveInOrder(predicate))
      return false;
    if ( ! this.testPredicate(predicate))
      return false;
    if (mRight != null && ! mRight.verifyDepthFirstRecursiveInOrder(predicate))
      return false;
    return true;
  }
  
  boolean verifyBstInOrderRecursive(boolean faster) 
  {
    NodePredicate<BinLink> predicate = null;
    if (faster)
      predicate = new VerifyBstInOrder();    // Not redundant: linear traversal, comparing each node key with predecessor's.
    else
      predicate = new VerifyBst();           // This would be redundant: twice as many comparisons as in-order verification
    return verifyDepthFirstRecursiveInOrder(predicate);
  }
  
  public void printDepthFirstIterativePreOrder() 
  {
	  Stack<BinLink> nodeStack = new Stack<BinLink>();
	  nodeStack.push(this);
	  while (! nodeStack.isEmpty()){
		  BinLink node = nodeStack.pop();
		  node.printSpacedChar();
		  if (node.right() != null) {
			  nodeStack.push(node.right());
		  }
		  if (node.left() != null) {
			  nodeStack.push(node.left());
		  }
	  }
  } 
  
  public void printDepthFirstIterativeInOrder() 
  {
    HashSet<BinLink> visited = new HashSet<BinLink>();
    Stack<BinLink> nodeStack = new Stack<BinLink>();
    visited.add(this);
    nodeStack.push(this);
    while (! nodeStack.empty()) {
      BinLink node = nodeStack.peek();
      if ((node.mLeft != null) && ( ! visited.contains(node.mLeft))) {
        nodeStack.push(node.mLeft);
      } else {
        node.printSpacedChar();
        visited.add(node);
        nodeStack.pop();
        if ((node.mRight != null) && (! visited.contains(node.mRight))) {
          nodeStack.push(node.mRight);
        }
      }
    }
  }
  
  public void printDepthFirstIterativePostOrder() 
  {
	  HashSet<BinLink> visited = new HashSet<BinLink>();
	  Stack<BinLink> nodeStack = new Stack<BinLink>();
	  nodeStack.push(this);
	  while (! nodeStack.empty()) {
		  BinLink node = nodeStack.peek();
		  if ((node.mLeft != null) && ( ! visited.contains(node.mLeft))) {
			  nodeStack.push(node.mLeft);
		  } else {
			  if ((node.mRight != null) && (! visited.contains(node.mRight))) {
				  nodeStack.push(node.mRight);
			  } else {
				  node.printSpacedChar();
				  visited.add(node);
				  nodeStack.pop();
			  }
		  }
	  }
  }
  
  ///////////// Visitors //////////////////////////////////////////////////////
  
  interface NodeVisitor {
    public void visit(BinLink node);
  }
  
  interface NodeVisitor1 {
    public void visit(BinLink node, int param1);
  }
  
  class NodePrinter implements NodeVisitor {
    public void visit(BinLink node) {
      node.printSpacedChar();
    }
  }
  
  class DepthNodePrinter implements NodeVisitor {
    public void visit(BinLink node) {
      Sx.format("%3d   %c\n", node.getDepth(), node.toLetter());
    }        
  }    
  
  
  protected class SpacedDepthPrinter implements NodeVisitor 
  {
	  int mTreeDepth;			// depth of tree, e.g. depth of 3 means 7 nodes max (2**3 - 1)
	  int mTreeWidth;			// width is max num nodes + 1, so depth = 3 give width = 8
	  int mNumSpaces;
	  int mPrevDepth =  0;            // depth increases as we go down
	  SpacedDepthPrinter(int treeDepth)
	  { 
		  mTreeDepth = treeDepth;
		  mNumSpaces = 1 << mTreeDepth;
	  }
	  public void printNode(BinLink node)
	  {
		  node.printSpacedChar();
	  }
	  public void visit(BinLink node) 
	  {
		  int nodeDepth = node.getDepth();
		  if (nodeDepth > mPrevDepth) 
		  {
			  mNumSpaces >>= 1;
			  mPrevDepth = nodeDepth;
			  System.out.format("\n%2d ", nodeDepth);
		  }
		  String spaces = Spaces.get(mNumSpaces - 1);
		  System.out.format("%s%c%c%s", spaces, node.toChar(), ' ', spaces);
	  }    
  }

  public void printBreadthFirstLevelOrder() {
    NodePrinter printer = new NodePrinter();
    visitBreadthFirstLevelOrder(printer);
    System.out.println();    
  }


  void printLevelOrder()
  {
  	BinLink lineBreaker = new BinLink(0);
  	Queue<BinLink> queue = new LinkedList<BinLink>();
  	queue.add(this);
  	queue.add(lineBreaker);

  	while (queue.size() > 1) {
  		BinLink node = queue.remove();
  		if (node == lineBreaker) {
  			Sx.puts();
  			queue.add(lineBreaker);
  		} else {
  			node.printSpacedChar();
  			if (node.left() != null)
  				queue.add(node.left());
  			if (node.right() != null)
  				queue.add(node.right());
  		}
  	}
  }


  
  public void visitBreadthFirstLevelOrder(NodeVisitor visitor) 
  {
    LinkedList<BinLink> queue = new LinkedList<BinLink>();
    queue.add(this);	// obviously not null
    do {
      BinLink node = queue.remove();
      visitor.visit(node);
      if (node.mLeft != null) {
        queue.add(node.mLeft);
      }
      if (node.mRight != null) {
        queue.add(node.mRight);
      }
    } while ( ! queue.isEmpty());
  }
  
  public void visitBreadthFirstLevelOrderDepth(NodeVisitor visitor) {
    LinkedList<DeepBinLink> queue = new LinkedList<DeepBinLink>();
    queue.add(new DeepBinLink(this, 1));  // height of subtree
    do {
      DeepBinLink node = queue.remove();
      visitor.visit(node);
      if (node.mLeft != null) {
        queue.add(new DeepBinLink(node.mLeft, node.depth+1));
      }
      if (node.mRight != null) {
        queue.add(new DeepBinLink(node.mRight, node.depth+1));
      }
    } while ( ! queue.isEmpty());
  }
  
  public void visitBreadthFirstLevelOrderDepthQueueIncludingNulls(NodeVisitor visitor) {
    LinkedList<DeepBinLink> queue = new LinkedList<DeepBinLink>();
    int depth = getDepth();
    queue.add(new DeepBinLink(this, 1));  // Root is at level 1, not 0.
    do {
      DeepBinLink node = queue.remove();
      if (node.getDepth() > depth)
        break;
      visitor.visit(node);
      if (node.mLeft != null) {
        queue.add(new DeepBinLink(node.mLeft, node.depth+1));
      } else {
        queue.add(new DeepBinLink(-33, node.depth+1));        
      }
      if (node.mRight != null) {
        queue.add(new DeepBinLink(node.mRight, node.depth+1));
      } else {
        queue.add(new DeepBinLink(-33, node.depth+1));        
      }
    } while ( ! queue.isEmpty());
  }
  
  public void printBreadthFirstLevelOrderDepth() {
    DepthNodePrinter printer = new DepthNodePrinter();
    visitBreadthFirstLevelOrderDepth(printer);
    System.out.println();    
  }
  
  public void printBreadthFirstQueueLevelSpaced()
  {
	  int depth = this.getDepth(); 
	  SpacedDepthPrinter printer = new SpacedDepthPrinter(depth);
	  visitBreadthFirstLevelOrderDepthQueueIncludingNulls(printer);
	  System.out.println();    
  }

  public void printBreadthFirstQueueLevelSpacedHex(int indent) {
    SpacedDepthPrinterHex printer = new SpacedDepthPrinterHex(indent);
    visitBreadthFirstLevelOrderDepth(printer);
    System.out.println();    
  }
    
  protected class SpacedDepthPrinterHex extends SpacedDepthPrinter
  {
    SpacedDepthPrinterHex(int indent)  { super(indent); }
    @Override public void printNode(BinLink node) {
      node.printSpacedHex();
    }        
  }
  
  public class SumPathFinder implements NodeVisitor
  {
    int mSum;
    HashSet<int[]> mPaths;
    SumPathFinder(int sum)  { 
      mSum = sum;
      mPaths = new HashSet<int[]>();
    }
    @Override
    public void visit(BinLink node) {
      BinTree.findAllPathsToSum(node, mSum, mPaths, true);
    }
  }
  

  
  // ====================== PREDICATE ===========================
  
  boolean testPredicate(NodePredicate<BinLink> predicate) {
    return predicate.apply(this);
  }
  
  interface NodePredicate<T extends BinLink> { // TODO: remove template
    abstract boolean apply(T node);
  }
  
  class VerifyBst implements NodePredicate<BinLink>
  {
    public boolean apply(BinLink node) {
      if (node != null) {
        if (node.mLeft != null && node.mLeft.mKey >= node.mKey)
          return false;
        if (node.mRight != null && node.mRight.mKey <= node.mKey)
          return false;
      }
      return true;
    }
  }
  
  class VerifyBstInOrder implements NodePredicate<BinLink>
  {
    BinLink prevNode = null;
    public boolean apply(BinLink node) {
      if (node != null && prevNode != null && node.mKey <= prevNode.mKey)
        return false;
      prevNode = node;
      return true;
    }
  }
  
  
  
  
  // TODO: adding template argument, as in NodePredicate<BinLink>,
  // breaks DeepBinLink....
  public boolean verifyBreadthFirstLevelOrder(NodePredicate<BinLink> predicate) {
    LinkedList<BinLink> queue = new LinkedList<BinLink>();
    queue.add(this);
    do {
      BinLink node = queue.remove();
      //      if ( ! node.testPredicate(predicate)) {
      //        return false;
      //      }     
      if ( ! predicate.apply(node)) {
        return false;
      }
      if (node.mLeft != null) {
        queue.add(node.mLeft);
      }
      if (node.mRight != null) {
        queue.add(node.mRight);
      }
    } while ( ! queue.isEmpty());
    return true;
  }
  
  public boolean verifyBstBreadthFirst() {
    return verifyBreadthFirstLevelOrder(new VerifyBst());
  }
  

  /**
   * @deprecated  Only works for full trees (No missing branches or leaves).
   */
  public void printBreadthFirstQueueLevelSpacedPowerOf2(int depth) {
    LinkedList<BinLink> queue = new LinkedList<BinLink>();
    int counter = 0, nextPowerOf2 = 1;
    
    //        char spaces[] = new char[80];
    //        Arrays.fill(spaces, ' ');
    //        if (depth < 0 || depth > 80) {
    //            depth = 0;
    //        }
    //        String space = new String(spaces);
    
    System.out.format("\n%s", Spaces.get(depth));
    queue.add(this);
    do {
      BinLink node = queue.remove();
      node.printSpacedChar();
      if (++counter == nextPowerOf2) {
        counter = 0;
        depth = Math.max(0, depth - nextPowerOf2);
        nextPowerOf2 *= 2;
        System.out.format("\n%s", Spaces.get(depth));
      }
      if (node.mLeft != null) {
        queue.add(node.mLeft);
      }
      if (node.mRight != null) {
        queue.add(node.mRight);
      }
    } while ( ! queue.isEmpty());
  }
  
  //////////////////////////////////////////////////////////////////////
  
  public HashMap<Integer, Integer> pathSumsBreadthFirstLevelOrder()
  {
    LevelSummer summer = new LevelSummer();
    visitBreadthFirstLevelOrder(summer);
    return summer.sums2counts;
  }
  
  protected class LevelSummer implements NodeVisitor {
    public void visit(BinLink node) {
      int nodeSum = node.mKey;
      if (node.mLeft != null) {
        HashSet<Integer> leftSums = findLeftSums(node);
        if (leftSums != null) {
          for (int leftSum : leftSums) {
            addToMap(nodeSum + leftSum);
          }
        }
      }
    }
    
    public void addToMap(int sum) {
      Integer count = sums2counts.get(sum);
      if (count == null)
        sums2counts.put(sum, 1);
      else 
        sums2counts.put(sum, count + 1);
    }
    
    public HashSet<Integer> findLeftSums(BinLink node) {
      return null;
    }
    
    HashMap<Integer, Integer> sums2counts = new HashMap<Integer, Integer>();
  }

  
  public boolean isAncestorOf(BinLink node) 
  {
	  if (node == this)
		  return true;
	  if (mLeft != null)
		  return mLeft.isAncestorOf(node);
	  if (mRight != null)
		  return mRight.isAncestorOf(node);
	  return false;
  }
    
  public boolean isDescendentOf(BinLink node) 
  {
	  if (node != null)
		  return node.isAncestorOf(this);
	  return false;
  }

  
  public BinLink getParentOfDescendantNode(BinLink node) {
    if (node == null || node == this) 
      return null;      // node cannot be its own parent
    
    return getParentOfDescendantNodeWithoutChecking(node);
  }

  private BinLink getParentOfDescendantNodeWithoutChecking(BinLink node) 
  {
	  // Don't check if node == null or node == this;
	  // that should have already been done

	  if (node == mLeft || node == mRight)
		  return this;

	  if (mLeft != null)
		  return mLeft.getParentOfDescendantNodeWithoutChecking(node);
	  if (mRight != null)
		  return mRight.getParentOfDescendantNodeWithoutChecking(node);
	  return null;
  }
  
  public BinLink getParent(BinLink root) {
    if (root == null)
      return null;
    return root.getParentOfDescendantNode(this);
  }
  
  
  
  protected static int test_insertBST(int size)
  {
	  BinLink root = new BinLink(size/2);
	  int[] lets = new int[size];
	  for (int j = size; --j >= 0; ) {
		  lets[j] = j;
	  }
	  Sx.putsArray(lets);
	  Shuffler.shuffle(lets);
	  Sx.putsArray(lets);
	  for (int j = 0; j < size; j++) {
		  Sx.format("  %c", (char)('A' + lets[j]));
	  }
	  Sx.puts();
	  for (int j = 1; j < size; j++) {
		  root.insertBST(lets[j]);
	  }

	  int treeDepth = root.getDepth();
	  Sx.format("printBreadthFirstQueueLevelSpaced(%d):\n", treeDepth);
	  root.printBreadthFirstQueueLevelSpaced();
      Sx.puts();

	  Sx.puts("printBreadthFirstLevelOrderDepth(): ");
      root.printBreadthFirstLevelOrderDepth();
      Sx.puts();
      
	  Sx.puts("printTreeDepthFirstRecursivePreOrder: ");
      printTreeDepthFirstRecursivePreOrder(root);
      Sx.puts();
      
	  Sx.puts("root.keys() returns: ");
      Iterable<Integer> theKeys = root.keys();
      Sx.putsIterable(theKeys);
      Sx.puts();
      
	  Sx.puts("root.keysInRange(5, 15) returns: ");
      Iterable<Integer> someKeys = root.keysInRange(5, 15);
      Sx.putsIterable(someKeys);
      Sx.puts();
      
	  return 0;
  }
  
  //* Keys of this node and all descendants in-order
  public Iterable<Integer> keys()
  {
	  Queue<Integer> queue = new LinkedList<Integer>();
	  keys(this, queue);
	  return queue;
  }

  private void keys(BinLink bin, Queue<Integer> queue)
  {
	  if (bin == null)
		  return;
	  keys(bin.mLeft, queue);
	  queue.add(bin.mKey);
	  keys(bin.mRight, queue);
  }
  
  public Iterable<Integer> keysInRange(int lo, int hi)
  {
	  Queue<Integer> queue = new LinkedList<Integer>();
	  keysInRange(this,  queue, lo, hi);
	  return queue;
  }
  
  private void keysInRange(BinLink bin, Queue<Integer> queue, int lo, int hi)
  {
	  if (bin == null)
		  return;
	  int cmplo = lo - bin.mKey;
	  int cmphi = hi - bin.mKey;
	  if (cmplo < 0) 
		  keysInRange(bin.mLeft, queue, lo, hi);
	  if (cmplo <= 0 && cmphi >= 0)
		  queue.add(bin.mKey);
	  if (cmphi > 0)
		  keysInRange(bin.mRight, queue, lo, hi);
  }
  
  

  public static int unit_test(int level) 
  {
    String  testName = BinLink.class.getName() + ".unit_test";
    Sx.puts(testName + " BEGIN");    

    int stat = 0;    
    int ic = -33;
    int md = 255 - 'A';
    ic = ic % md + 'A';
    System.out.format("md %d  int(%d) char(%c)\n",  md, ic, ic);
    Sx.puts("(char)'A' + 0 is: " + (char)('A' + 0));
    
    test_insertBST(20);
     
    Sx.puts(testName + " END");    
    return stat;
  }

  public static void main(String[] args)    
  {
	  unit_test(1);
  }
}
