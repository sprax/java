package sprax.trees;

import java.util.LinkedList;
import java.util.Queue;

import sprax.Spaces;
import sprax.Sx;
import sprax.Sz;

public class BTree
{
    public static BTree NEWLINE;
    static {
        NEWLINE = new BTree(Integer.MIN_VALUE);
    }
    
    // all access is default or "friendly"
    int                 mVal;
    BTree               mL;
    BTree               mR;
    
    // Constructor, value only, null children.
    public BTree(int val) {
        mVal = val;
        mL = null;
        mR = null;
    }
    
    // Constructor, recursive.
    public BTree(int val, BTree left, BTree right) {
        mVal = val;
        mL = left;
        mR = right;
    }
    
    // Clone a tree recursively.
    static BTree copyRecurse(BTree tree)
    {
        if (tree == null)
            return null;
        return new BTree(tree.mVal, copyRecurse(tree.mL), copyRecurse(tree.mR));
    }
    
    // Make tree breadth-first from array.
    public static BTree makeTree(int vals[])
    {
        if (vals == null || vals.length == 0)
            return null;
        Queue<BTree> bq = new LinkedList<BTree>();
        BTree root = new BTree(vals[0]);
        BTree node = root;
        bq.add(node);
        for (int j = 0;;) {
            if (++j == vals.length)
                break;
            node = bq.remove();
            node.mL = new BTree(vals[j]);
            bq.add(node.mL);
            if (++j == vals.length)
                break;
            node.mR = new BTree(vals[j]);
            bq.add(node.mR);
        }
        return root;
    }
    
    // Make a new tree with left-right mirror symmetry.
    static BTree makeSymmetric(int rootVal, BTree left)
    {
        BTree right = copyRecurse(left);
        reverse(right);
        return new BTree(rootVal, left, right);
    }
    
    static public boolean isSymmetric(BTree tree)
    {
        if (tree == null)
            return true;
        return mirrorTrees(tree.mL, tree.mR);
    }
    
    static public boolean mirrorTrees(BTree tA, BTree tB)
    {
        if (tA == null && tB == null)
            return true;
        if (tA == null || tB == null)
            return false;
        if (tA.mVal != tB.mVal)
            return false;
        return mirrorTrees(tA.mL, tB.mR) && mirrorTrees(tA.mR, tB.mL);
    }
    
    static void reverse(BTree tree)
    {
        if (tree == null)
            return;
        BTree temp = tree.mL;
        tree.mL = tree.mR;
        tree.mR = temp;
        reverse(tree.mL);
        reverse(tree.mR);
    }
    
    // isMaxHeap means no child's value can exceed its parent's.
    // Top-level public function, separate from recursive worker function.
    static public boolean isMaxHeap(BTree tree)
    {
        if (tree == null)
            return true;
        return isMaxHeap(tree.mL, tree.mVal) && isMaxHeap(tree.mR, tree.mVal);
    }
    
    // Protected recursive function.  Separation makes this more readable.
    static protected boolean isMaxHeap(BTree tree, int parentValue)
    {
        if (tree == null)
            return true;
        if (tree.mVal > parentValue)
            return false;
        return isMaxHeap(tree.mL, tree.mVal) && isMaxHeap(tree.mR, tree.mVal);
    }
    
    // All-in-one recursive function, but less readable.
    static public boolean isMaxHeapRecurse(BTree tree)
    {
        if (tree == null)
            return true;
        if ((tree.mL != null && tree.mL.mVal > tree.mVal)
                || (tree.mR != null && tree.mR.mVal > tree.mVal))
            return false;
        return isMaxHeap(tree.mL) && isMaxHeap(tree.mR);
    }
    
    // isMinHeap means no child's value can be less than its parent's.
    // Top-level public function, separate from recursive worker function.
    static public boolean isMinHeap(BTree tree)
    {
        if (tree == null)
            return true;
        return isMinHeap(tree.mL, tree.mVal) && isMinHeap(tree.mR, tree.mVal);
    }
    
    // Protected recursive function.  Separation makes this more readable.
    static protected boolean isMinHeap(BTree tree, int parentValue)
    {
        if (tree == null)
            return true;
        if (tree.mVal < parentValue)
            return false;
        return isMinHeap(tree.mL, tree.mVal) && isMinHeap(tree.mR, tree.mVal);
    }
    
    // Tree depth is the maximal depth of any non-null node
    static public int depth(BTree tree)
    {
        if (tree == null)
            return 0;
        return 1 + Math.max(depth(tree.mL), depth(tree.mR));
    }
    
    static public int printBreadthFirstWithoutSpaces(BTree tree)
    {
        if (tree == null)
            return 0;
        Queue<BTree> bq = new LinkedList<BTree>();
        bq.add(tree);
        bq.add(NEWLINE);
        int lines = 0;
        while (!bq.isEmpty()) {
            BTree node = bq.remove();
            if (node == null) {
                System.out.format(" -");
            } else if (node == NEWLINE) {
                System.out.println();
                lines++;
                if (!bq.isEmpty()) {
                    bq.add(NEWLINE);
                }
            } else {
                System.out.format(" %X", node.mVal);
                bq.add(node.mL);
                bq.add(node.mR);
            }
        }
        return lines;
    }
    
    static public int printBreadthFirstWithSpaces(BTree tree)
    {
        if (tree == null)
            return 0;
        int deep = depth(tree);
        int marg = 1 << deep;
        String spaceL = Spaces.get(marg);
        String spaceR = Spaces.get(marg - 1);
        
        Queue<BTree> bq = new LinkedList<BTree>();
        bq.add(tree);
        bq.add(NEWLINE);
        int lines = 0;
        while (!bq.isEmpty()) {
            BTree node = bq.remove();
            if (node == null) {
                System.out.format(" -");
            } else if (node == NEWLINE) {
                System.out.println();
                marg >>= 1;
                spaceL = Spaces.get(marg);
                spaceR = Spaces.get(marg - 1);
                lines++;
                if (!bq.isEmpty()) {
                    bq.add(NEWLINE);
                }
            } else {
                System.out.format("%s%X%s", spaceL, node.mVal, spaceR);
                bq.add(node.mL);
                bq.add(node.mR);
            }
        }
        return lines;
    }
    
    public static int unit_test()
    {
        String testName = BTree.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
        BTree bt = makeTree(arr);
        printBreadthFirstWithSpaces(bt);
        Sx.format("Reversed:\n");
        reverse(bt);
        int height = printBreadthFirstWithSpaces(bt);
        int depth = depth(bt);
        numWrong += Sz.oneWrong(height, depth + 1);
        
        BTree st = makeSymmetric(0, bt);
        printBreadthFirstWithSpaces(st);
        
        boolean minHeap = isMinHeap(st);
        numWrong += Sz.oneWrong(minHeap, true);
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
