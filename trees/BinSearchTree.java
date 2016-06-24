package sprax.trees;

import java.util.LinkedList;
import java.util.Queue;

import sprax.sprout.Sx;

/** 
 * binary search tree: for any node N, N.left.val < N.val < N.right.val unless null.
 * @author sprax
 */
public class BinSearchTree extends BinTree<BinLink>
{
  BinSearchTree() { super(null); }
  
  BinSearchTree(BinLink root) { 
      super(root); 
  }
  
  public void insert(int data) {
    if (mRoot == null)
      mRoot = new BinLink(data);
    else 
      mRoot.insertBST(data);
  }
  
  /** 
   * Search for two nodes whose keys sum to N, using no additional space.
   * 
   * If found, call them node0 and node1, and return them in an array A of length 2, where A[0].key <= A[1].key
   * If not found, return empty array of nodes.
   *   
   * Strategy A (non-negative keys only?): Traverse the BST as if it were any BT and for any node with key <= N, 
   * say key == M, do a BST search for another node with key == N - M. 
   * 
   * Strategy B: Find the smaller node first, then the larger one: Find a node with key < N/2, 
   * say key == M; then search (only to the right) for a node with key == N - M. 
   * 
   * Strategy C (any integers): 
   */
  public BinLink[] findTwoNodesThatSumToN(int sum)
  {
      BinLink twoNodes[] = new BinLink[2];
      //twoNodes[0] = null;
      //twoNodes[1] = null;
      boolean found = findTwoNodesThatSumToN(sum, twoNodes, mRoot, mRoot);
      if (found)
          return twoNodes;
      return new BinLink[0];
  } 
  
  public static boolean findTwoNodesThatSumToN(int sum, BinLink twoNodes[], BinLink root, BinLink node)
  {
      if (node == null)
          return false;
      
      if (node.mKey < sum/2) {
          BinLink other = findNode(sum - node.mKey, root);
          if (other != null) {
              twoNodes[0] = node;
              twoNodes[1] = other;
              return true;
          }
      }
      if (findTwoNodesThatSumToN(sum, twoNodes, root, node.mLeft))
          return true;
      if (findTwoNodesThatSumToN(sum, twoNodes, root, node.mRight))
          return true;
      return false;
  }
  
  public BinLink findNode(int key) {
      return findNode(key, mRoot);
  }
  
  /** binary search (post-order) for exact match of key value */
  public static BinLink findNode(int key, BinLink node) {
      if (node == null)
          return null;
      if (key < node.mKey)
          return findNode(key, node.mLeft);
      if (key > node.mKey)
          return findNode(key, node.mRight);
      return node;
  }
  
  public void balance() {
    // TODO
  }
  
  /**
   * makes the complete binary tree with 2**expOf2 leaf nodes containing 
   * Integer data.
   * For example, makeCompleteTree(3) makes a tree with 8 leaf
   * nodes, and 15 = 2**4 - 1 nodes in total, with min data value 0
   * and max data value 14.
   * @param expOf2
   * @return
   */
  public static BinSearchTree makeCompleteTree(int expOf2)
  {    
    if (expOf2 > 0) {
      int powerOf2 = 2;
      for (int j = 1;  j < expOf2; j++) {
        powerOf2 *= 2;
      }
      BinSearchTree binSearchTree = new BinSearchTree();
      binSearchTree.insert2(powerOf2, powerOf2);
      return binSearchTree; 
    }
    if (expOf2 == 0) {
      return new BinSearchTree(new BinLink(0));
    }
    return null;
  }

  protected void insert2(int off, int dif) 
  {
    insert(off - 1);
    dif = dif/2;
    if (dif == 0)
      return;
    insert2(off - dif, dif);
    insert2(off + dif, dif);
  }
  

  public int minKey() {
      return minNode(mRoot).mKey;
  }
  
  protected BinLink minNode(BinLink link)
  {
      if (link.mLeft == null)
          return link;
      return minNode(link.mLeft);           // Tail recursive?
  }
  
  public Integer floorKey(int key) {
      BinLink node = floorNode(mRoot, key);
      if (node == null)
          return  null;
      return node.mKey;
  }
  
  protected BinLink floorNode(BinLink link, int key) 
  {
      if (link == null)
          return  null;
      if (key < link.mKey)
          return floorNode(link.mLeft, key);
      if (key == link.mKey)
          return link;
      // Now we know link.mData < key, so link is a lower bound.
      // Can we find a greater lower bound?
      BinLink node = floorNode(link.mRight, key);
      if (node != null)
          return  node;
      return link;
  }
  
  int maxKey() {
      return maxNode(mRoot).mKey;
  }
  
  BinLink maxNode(BinLink link) 
  {
      if (link == null)
          return  null;
      return maxNode(link.mRight);      // Tail recursive?
  }
  
  /**
   * Find ceiling (least upper bound) of key in this BST
   * @param key
   * @return
   */
  public Integer ceilingKey(int key)
  {
      BinLink node = ceilingNode(mRoot, key);
      if (node == null)
          return  null;
      return node.mKey;
  }
  
  protected BinLink ceilingNode(BinLink link, int key)
  {
      if (link == null)
          return  null;
      if (key > link.mKey)
          return ceilingNode(link.mRight, key);
      if (key == link.mKey)
          return link;
      //  Now we know link.mData is an upper bound for key.
      //  Can we find a lower upper bound?
      BinLink node = ceilingNode(link.mLeft, key);
      if (node != null)
          return  node;
      return link;
  }
  
  
  /**
   * Gets all keys using BST range searching
   */
  public Iterable<Integer> keys() {
      return keys(minKey(), maxKey());
  }

  /**
   * Return Iterable containing all BST keys in the range [lo, hi]
   */
  public Iterable<Integer> keys(int lo, int hi) 
  {
      Queue<Integer> keyQ = new LinkedList<Integer>();
      keys(keyQ, mRoot, lo, hi);
      return keyQ;
  }
  
  protected void keys(Queue<Integer> keyQ, BinLink link, int lo, int hi) 
  {
      if (link == null)
          return;
      if (lo < link.mKey)
          keys(keyQ, link.mLeft, lo, hi);
      if (lo <= link.mKey && link.mKey <= hi)
          keyQ.add(link.mKey);
      if (hi > link.mKey)
          keys(keyQ, link.mRight, lo, hi);
  }
  

  
  
  /**
   * unit_test
   */
  public static void unit_test(int lvl)
  {
    BinSearchTree treeA = new BinSearchTree();
    int vals21[] = { 10, 5, 15, 2, 7, 12, 17,  1, 4, 6, 8, 11, 14, 16, 18, 0, 3, 9, 13, 19, 20 };
    for (int n : vals21) {
    	treeA.insert(n);
    }

    treeA.printBreadthFirstQueueLevelSpacedHexComplete(24);
    treeA.printDepthFirstIterativePreOrder();    
    treeA.printDepthFirstIterativeInOrder();

    boolean bBst = false;
    bBst = treeA.getRoot().verifyBstBreadthFirst();
    Sx.puts("treeA.verifyBstBreadthFirst() is " + bBst);
    bBst = treeA.getRoot().verifyBstInOrderRecursive(true);
    Sx.puts("treeA.verifyBstInOrderRecursive() is " + bBst);

    BinSearchTree treeB = new BinSearchTree(null);
    treeB.insert2(8, 8);
    bBst = treeB.getRoot().verifyBstBreadthFirst();
    Sx.puts("insert2: treeB.verifyBstBreadthFirst() is " + bBst);
    bBst = treeB.getRoot().verifyBstInOrderRecursive(false);
    Sx.puts("insert2: treeB.verifyBstInOrderRecursive() is " + bBst);
    treeB.printDepthFirstIterativePreOrder();    
    treeB.printDepthFirstIterativeInOrder();
    treeB.printBreadthFirstQueueLevelSpacedHexComplete(24);
    
    BinSearchTree treeC = makeCompleteTree(3);
    bBst = treeC.getRoot().verifyBstBreadthFirst();
    Sx.puts("insert2: treeC.verifyBstBreadthFirst() is " + bBst);
    bBst = treeC.getRoot().verifyBstInOrderRecursive(true);
    Sx.puts("insert2: treeC.verifyBstInOrderRecursive() is " + bBst);
    treeC.printDepthFirstIterativePreOrder();    
    treeC.printDepthFirstIterativeInOrder();
    treeC.printBreadthFirstQueueLevelSpacedHexComplete(24);
    
    Iterable<Integer> it = treeC.keys(1, -1);
    Sx.putsIterable("keys(1, -1): ", it, 50, 100);
    
    it = treeC.keys(1, 1);
    Sx.putsIterable("keys(1,  1): ", it, 50, 100);
    
    it = treeC.keys(2, 5);
    Sx.putsIterable("keys(2,  5): ", it, 50, 100);
  }
    

  public static void main(String[] args) {
    unit_test(1);
  }
}
