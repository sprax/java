package sprax.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.HashSet;
import java.util.LinkedList;

import sprax.Sz;

public class TreeFactory<T extends BinLink>
{
    // static hacks for debugging, to save typing, etc.
    static int sDbg = 1;
    
    public static void puts() {
        System.out.println();
    }
    
    public static void puts(String string) {
        System.out.println(string);
    }
    
    public static void print(String string) {
        System.out.print(string);
    }
    
    public static void debug(int dbgLevel, String string) {
        if (dbgLevel > sDbg)
            System.out.print(string);
    }
    
    // Useful hack for generating trees with ordered data
    static int mCount = 0;
    
    static int setCount(int num) {
        return mCount = num;
    }
    
    static int getCount() {
        return mCount;
    }
    
    static int addCount(int num) {
        return mCount += num;
    }
    
    private T mRoot = null;
    
    /**
     * growGlobalCountingBinTreeRecurse creates a binary tree in pre-order:
     * 0 A
     * 1 B I
     * 2 C F J M
     * 3 D E G H K L N O
     * 
     * @param depth
     * @param n
     * @return
     */
    public static BinTree<BinLink> growGlobalCountingBinTree(int depth, int count)
    {
        setCount(0);
        BinLink root = growGlobalCountingBinTreeRecurse(depth, 0);
        return new BinTree<BinLink>(root);
    }
    
    public static BinLink growGlobalCountingBinTreeRecurse(int depth, int n) {
        BinLink root = new BinLink(getCount());
        if (--depth >= 0) {
            root.mLeft = growGlobalCountingBinTreeRecurse(depth, addCount(1));
            root.mRight = growGlobalCountingBinTreeRecurse(depth, addCount(1));
        }   // otherwise, b.left = b.right = null (by construction)
        return root;
    }
    
    /**
     * The pair growLocalCountingBinTree and growLocalCountingBinTreeRecurse
     * create a binary tree in level order:
     * 0 A
     * 1 B C
     * 2 D E F G
     * 3 H I J K L M N O
     * 
     * @param depth
     * @param n
     * @return
     */
    public static BinTree<BinLink> growLocalCountingBinTree(int depth, int count) {
        setCount(0);
        BinLink root = growLocalCountingBinTreeRecurse(depth, 0);
        return new BinTree<BinLink>(root);
    }
    
    public static BinLink growLocalCountingBinTreeRecurse(int depth, int n) {
        BinLink b = new BinLink(n);
        if (--depth >= 0) {
            b.mLeft = growLocalCountingBinTreeRecurse(depth, n * 2 + 1);
            b.mRight = growLocalCountingBinTreeRecurse(depth, n * 2 + 2);
        }   // otherwise, b.left = b.right = null (by construction)
        return b;
    }
    
    public static BinTree<BinLink> growBreadthFirstQueueLevelOrder(
            int rootData, int count)
    {
        BinLink root = rootGrowBreadthFirstQueueLevelOrder(rootData, count);
        return new BinTree<BinLink>(root);
    }
    
    public static BinLink rootGrowBreadthFirstQueueLevelOrder(int rootData,
            int count)
    {
        if (count < 0)
            return null;
        BinLink root = new BinLink(rootData);
        LinkedList<BinLink> queue = new LinkedList<BinLink>();
        queue.add(root);
        // add every new node to queue, but break out of loop as soon as
        // count nodes have been added to the tree. So the queue gets bigger
        // than it needs to be, and is left for garbage collection.
        for (int j = 1; j < count; j++) {
            BinLink node = queue.remove();
            node.mLeft = new BinLink(rootData + j);
            if (++j == count)
                break;
            queue.add(node.mLeft);
            node.mRight = new BinLink(rootData + j);
            queue.add(node.mRight);
        }
        return root;
    }
    
    /**
     * unit_test
     */
    public static int unit_test()
    {
        String testName = TreeFactory.class.getName() + ".unit_test";
        Sz.begin(testName);
        int numWrong = 0;
        
        BinTree<BinLink> tree3 = null;
        
        print("\n    growGlobalCountingBinTreeRecurse:\n");
        tree3 = growGlobalCountingBinTree(3, 15);
        tree3.printBreadthFirstQueueLevelSpaced();
        print("printDepthFirstRecursivePreOrder  0:");
        tree3.printDepthFirstRecursivePreOrder();
        print("printDepthFirstIterativePreOrder  1:");
        tree3.printDepthFirstIterativePreOrder();
        print("printDepthFirstIterativePreOrder  2:");
        tree3.printDepthFirstIterativePreOrder();
        
        print("\n    growLocalCountingBinTreeRecurse:\n");
        tree3 = growLocalCountingBinTree(3, 15);
        tree3.printBreadthFirstQueueLevelSpaced();
        print("printDepthFirstRecursivePreOrder  0:");
        tree3.printDepthFirstRecursivePreOrder();
        print("printDepthFirstIterativePreOrder  1:");
        tree3.printDepthFirstIterativePreOrder();
        print("printDepthFirstIterativePreOrder  2:");
        tree3.printDepthFirstIterativePreOrder();
        
        print("printDepthFirstRecursiveInOrder   0:");
        tree3.printDepthFirstRecursiveInOrder();
        print("printDepthFirstIterativeInOrder   1:");
        tree3.printDepthFirstIterativeInOrder();
        print("printDepthFirstIterativeInOrder   2:");
        tree3.printDepthFirstIterativeInOrder();
        
        print("printDepthFirstRecursivePostOrder 0:");
        tree3.printDepthFirstRecursivePostOrder();
        print("printDepthFirstIterativePostOrder 1:");
        tree3.printDepthFirstIterativePostOrder();
        print("printDepthFirstIterativePostOrder 2:");
        tree3.printDepthFirstIterativePostOrder();
        
        print("\n    growBreadthFirstQueueLevelOrder:\n");
        tree3 = growBreadthFirstQueueLevelOrder(1, 15);
        
        print("printBreadthFirstQueueLevelOrder:  ");
        tree3.printBreadthFirstQueueLevelOrder();
        print("printBreadthFirstQueueLevelSpaced: ");
        tree3.printBreadthFirstQueueLevelSpaced();
        
        print("printDepthFirstRecursivePreOrder:  ");
        tree3.printDepthFirstRecursivePreOrder();
        print("printDepthFirstIterativePreOrder:  ");
        tree3.printDepthFirstIterativePreOrder();
        
        print("printDepthFirstRecursiveInOrder:   ");
        tree3.printDepthFirstRecursiveInOrder();
        print("printDepthFirstIterativeInOrder:   ");
        tree3.printDepthFirstIterativeInOrder();
        
        print("printDepthFirstRecursivePostOrder: ");
        tree3.printDepthFirstRecursivePostOrder();
        print("printDepthFirstIterativePostOrder: ");
        tree3.printDepthFirstIterativePostOrder();
        
        print("printBreadthFirstQueueLevelOrder:  ");
        tree3.printBreadthFirstQueueLevelOrder();
        
        print("printBreadthFirstQueueLevelSpacedPowerOf2: ");
        tree3.getRoot().printBreadthFirstQueueLevelSpacedPowerOf2(8);
        
        print("printBreadthFirstQueueLevelSpaced: ");
        tree3.printBreadthFirstQueueLevelSpaced();
        
        print("printBreadthFirstQueueLevelOrderDepth: ");
        tree3.printBreadthFirstQueueLevelOrderDepth();
        
        Sz.end(testName, numWrong);
        return numWrong;
    }
    
    public static void main(String[] args) {
        unit_test();
    }
}
