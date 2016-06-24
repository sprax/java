package sprax.trees;

import java.util.LinkedList;
import java.util.Queue;

import sprax.sprout.Sx;

/** 
 * binary search tree: for any node N, N.left.val < N.val < N.right.val unless null.
 * @author sprax
 */
public class BstSum
{  
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

  public static boolean findTwoNodesThatSumToN(int sum, BinLink twoNodes[], BinSearchTree bst) 
  {
      return findTwoNodesThatSumToN(sum, twoNodes, bst.mRoot, bst.mRoot);
  }
  
  
  public static void unit_test(int lvl)
  {
    BinSearchTree bst = new BinSearchTree();
    int vals21[] = { 10, 5, 15, 2, 7, 12, 17,  1, 4, 6, 8, 11, 14, 16, 18, 0, 3, 9, 13, 19, 20 };
    for (int n : vals21) {
    	bst.insert(n);
    }
    
    BinLink[] twoNodes = new BinLink[2];
    int sum = 13;
    boolean foundTwo = findTwoNodesThatSumToN(sum, twoNodes, bst);
    if (foundTwo) {
        Sx.puts(twoNodes[0]);
        Sx.puts(twoNodes[1]);
    } else {
        Sx.puts("Not found");
    }
        

  }
    

  public static void main(String[] args) {
    unit_test(1);
  }
}
