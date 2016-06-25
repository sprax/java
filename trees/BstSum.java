package sprax.trees;

import java.util.LinkedList;
import java.util.Queue;

import sprax.sprout.Sx;

/**
 * Supposedly a Google interview question: https://www.careercup.com/question?id=5671198293753856
 * Given a BST and a number x, find two nodes in the BST whose sum is equal to x.
 * You can not use extra memory like converting BST into one array and then solve
 *  this like 2sum.
 */

/** 
 * Search any BST for two nodes whose keys sum to N, using no additional space.
 * binary search tree: for any node N, N.left.val < N.val < N.right.val unless null.
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
 *
 * @author Sprax Lines
 */
public class BstSum
{  
    public static boolean findTwoNodesThatSumToN(int sum, BinLink twoNodes[], BinSearchTree bst) 
    {
        return findTwoNodesThatSumToN(sum, twoNodes, bst.mRoot, bst.mRoot);
    }
    
    /**
     * Search a BST for two nodes whose keys sum to N, using no additional space.
     * Strategy: to avoid duplicating searches, find smaller key first.  Perform
     * in-order traversal of BST, and for any node with key value M < N/2, search 
     * tree from the start for a node with value N - M.  (There can be at most one.)
     * 
     * Complexity: Time is O(NlogN), additional space is only the stack for recursion,
     * so O(logN) in stack space, O(1) in heap space (two array references).
     * 
     * @param sum
     * @param twoNodes  array in which to place the nodes, if found
     * @param root      root node of BST
     * @param node      current search node
     * @return true is such a pair is found, false otherwise.
     */
    static boolean findTwoNodesThatSumToN(int sum, BinLink twoNodes[], BinLink root, BinLink node)
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
