package sprax.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import sprax.Spaces;

interface ToInt
{
    public int toInt();
}

public abstract class BinLinkT<T extends ToInt>
{
    
    BinLinkT<T> left  = null;
    BinLinkT<T> right = null;
    T           data;
    
    BinLinkT<T> left() {
        return left;
    }
    
    BinLinkT<T> right() {
        return right;
    }
    
    public BinLinkT() {}                                        // default constructor
    
    public BinLinkT(T d) {
        data = d;
    }                         // data-only constructor
    
    public BinLinkT(T data, BinLinkT<T> left, BinLinkT<T> right) {     // complete constructor
        this.data = data;
        this.left = left;
        this.right = right;
    }
    
    public abstract BinLinkT<T> createNode(T data);
    
    public int toInt() {
        return data.toInt();
    }
    
    public char toLetter() {
        return (char) (data.toInt() % 26 + 'A');
    }
    
    public char toChar() {
        return (char) (data.toInt() % (254 - 'A') + 'A');
    }
    
    public char toHexDigit() {
        int dig = data.toInt() % 16;
        if (dig < 0)
            dig += 16;
        if (dig < 10)
            return (char) (dig + '0');
        else
            return (char) (dig + ('A' - 10));
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
    
    public int getNumDescendents() {
        return (left == null ? 0 : 1 + left.getNumDescendents())
                + (right == null ? 0 : 1 + right.getNumDescendents());
    }
    
    public static int getNumDescendentsStatic(BinLinkT<?> b) {
        if (b == null)
            return 0;
        return 1 + getNumDescendentsStatic(b.left) + getNumDescendentsStatic(b.right);
    }
    
    //    protected static int max(int a, int b) {
    //        return a > b ? a : b;
    //    }
    
    public int getDepth() {
        return 1 + Math.max((left == null ? 0 : left.getDepth())
                , (right == null ? 0 : right.getDepth()));
    }
    
    public static <U extends ToInt> int getDepthStatic(BinLinkT<U> b) {
        if (b != null)
            return 1 + Math.max(getDepthStatic(b.left), getDepthStatic(b.right));
        return 0;
    }
    
    /*********
     * STATIC ***********************************************************
     * public static <U extends ToInt> BinLinkT<U> growBreadthFirstQueueLevelOrder(int rootData, int
     * count) {
     * if (count < 0)
     * return null;
     * BinLinkT<U> root = new BinLinkT<U>(rootData);
     * LinkedList<BinLinkT<U>> queue = new LinkedList<BinLinkT<U>>();
     * queue.add(root);
     * // add every new node to queue, but break out of loop as soon as
     * // count nodes have been added to the tree. So the queue gets bigger
     * // than it needs to be, and is left for garbage collection.
     * for (int j = 1; j < count; j++) {
     * BinLinkT<U> node = queue.remove();
     * node.left = new BinLinkT<U>(rootData + j);
     * if (++j == count)
     * break;
     * queue.add(node.left);
     * node.right = new BinLinkT<U>(rootData + j);
     * queue.add(node.right);
     * }
     * return root;
     * }
     * 
     * public static BinLinkT growBreadthFirstQueueLevelOrderMini(int rootData, int count) {
     * if (count < 1)
     * return null; // A tree with less than 1 node is null
     * BinLinkT root = new BinLinkT(rootData);
     * LinkedList<BinLinkT<T>> queue = new LinkedList<BinLinkT<T>>();
     * queue.add(root);
     * int j = 0, last = count - 1;
     * while ( ! queue.isEmpty()) {
     * if (++j > last)
     * break;
     * BinLinkT node = queue.remove();
     * node.left = new BinLinkT(rootData + j);
     * if (j*2 < last)
     * queue.add(node.left); // If this no will have at least a left child, enqueue it.
     * if (++j > last)
     * break;
     * node.right = new BinLinkT(rootData + j);
     * if (j*2 < last)
     * queue.add(node.right); // If this no will have at least a left child, enqueue it.
     * }
     * return root;
     * }
     * 
     * 
     * 
     * public static BinLinkT growRandomBinTreeRecurse(int depth, Random rng, int n) {
     * BinLinkT b = new BinLinkT(rng.nextInt(n), null, null);
     * if (depth-- > 0) {
     * b.left = growRandomBinTreeRecurse(depth, rng, n);
     * b.right = growRandomBinTreeRecurse(depth, rng, n);
     * } // otherwise, b.left = b.right = null (by construction)
     * return b;
     * }
     * 
     * public static BinLinkT initAlphabet(int length) {
     * assert(length > 0);
     * length--;
     * BinLinkT head = new BinLinkT('A' + length, null, null);
     * while(--length >= 0) {
     * BinLinkT temp = new BinLinkT('A' + length, head, null);
     * head.left = temp;
     * head = temp;
     * }
     * head.left = null;
     * return head;
     * }
     * 
     * public static void printList(BinLinkT link) {
     * while (link != null) {
     * link.printSpacedChar();
     * link = link.right;
     * }
     * System.out.println();
     * }
     * 
     * public static void printTreeDepthFirstRecursivePreOrder(BinLinkT head) {
     * if (head != null) {
     * head.printSpacedChar();
     * printTreeDepthFirstRecursivePreOrder(head.left);
     * printTreeDepthFirstRecursivePreOrder(head.right);
     * }
     * }
     ********* STATIC
     ***********************************************************/
    
    public void printDepthFirstRecursivePreOrder() {
        this.printSpacedChar();
        if (left != null) {
            left.printDepthFirstRecursivePreOrder();
        }
        if (right != null) {
            right.printDepthFirstRecursivePreOrder();
        }
    }
    
    public void printDepthFirstRecursiveInOrder() {
        if (left != null) {
            left.printDepthFirstRecursiveInOrder();
        }
        this.printSpacedChar();
        if (right != null) {
            right.printDepthFirstRecursiveInOrder();
        }
    }
    
    public void printDepthFirstRecursivePostOrder() {
        if (left != null) {
            left.printDepthFirstRecursivePostOrder();
        }
        if (right != null) {
            right.printDepthFirstRecursivePostOrder();
        }
        this.printSpacedChar();
        
    }
    
    public void printDepthFirstIterativePreOrder() {
        Stack<BinLinkT<T>> nodeStack = new Stack<BinLinkT<T>>();
        nodeStack.push(this);
        while (!nodeStack.isEmpty()) {
            BinLinkT<T> node = nodeStack.pop();
            node.printSpacedChar();
            if (node.right() != null) {
                nodeStack.push(node.right());
            }
            if (node.left() != null) {
                nodeStack.push(node.left());
            }
        }
    }
    
    public void printDepthFirstIterativeInOrder() {
        HashSet<BinLinkT<T>> visited = new HashSet<BinLinkT<T>>();
        Stack<BinLinkT<T>> nodeStack = new Stack<BinLinkT<T>>();
        visited.add(this);
        nodeStack.push(this);
        while (!nodeStack.empty()) {
            BinLinkT<T> node = nodeStack.peek();
            if ((node.left != null) && (!visited.contains(node.left))) {
                nodeStack.push(node.left);
            } else {
                node.printSpacedChar();
                visited.add(node);
                nodeStack.pop();
                if ((node.right != null) && (!visited.contains(node.right))) {
                    nodeStack.push(node.right);
                }
            }
        }
    }
    
    public void printDepthFirstIterativePostOrder() {
        HashSet<BinLinkT<T>> visited = new HashSet<BinLinkT<T>>();
        Stack<BinLinkT<T>> nodeStack = new Stack<BinLinkT<T>>();
        nodeStack.push(this);
        while (!nodeStack.empty()) {
            BinLinkT<T> node = nodeStack.peek();
            if ((node.left != null) && (!visited.contains(node.left))) {
                nodeStack.push(node.left);
            } else {
                if ((node.right != null) && (!visited.contains(node.right))) {
                    nodeStack.push(node.right);
                } else {
                    node.printSpacedChar();
                    visited.add(node);
                    nodeStack.pop();
                }
            }
        }
    }
    
    public interface NodeVisitor
    {
        abstract <T extends ToInt> void visit(BinLinkT<T> node);
    }
    
    protected interface NodeVisitor1
    {
        abstract <U extends ToInt> void visit(BinLinkT<U> node, int param1);
    }
    
    protected class NodePrinter implements NodeVisitor
    {
        public <U extends ToInt> void visit(BinLinkT<U> node) {
            node.printSpacedChar();
        }
    }
    
    class DepthNodePrinter implements NodeVisitor
    {
        public <U extends ToInt> void visit(BinLinkT<U> node) {
            System.out.print(" " + node.toLetter() + "." + node.getDepth());
        }
    }
    
    protected class SpacedDepthPrinter implements NodeVisitor
    {
        int mMaxIndent = 32;
        int mPrevDepth = -1;
        
        SpacedDepthPrinter(int indent) {
            mMaxIndent = indent;
        }
        
        public <U extends ToInt> void printNode(BinLinkT<U> node) {
            node.printSpacedChar();
        }
        
        @Override
        public <U extends ToInt> void visit(BinLinkT<U> node) {
            int nodeDepth = node.getDepth();
            if (nodeDepth > mPrevDepth) {
                int numSpaces = Math.max(0, mMaxIndent - (int) (Math.pow(2, nodeDepth)));
                System.out.format("\n%2d%s", nodeDepth, Spaces.get(numSpaces));
                mPrevDepth = nodeDepth;
            }
            printNode(node);
        }
    }
    
    protected class SpacedDepthPrinterHex extends SpacedDepthPrinter
    {
        SpacedDepthPrinterHex(int indent) {
            super(indent);
        }
        
        @Override
        public <U extends ToInt> void printNode(BinLinkT<U> node) {
            node.printSpacedHex();
        }
    }
    
    /*************************************************
     * public class SumPathFinder implements NodeVisitor
     * {
     * int mSum;
     * HashSet<int[]> mPaths;
     * SumPathFinder(int sum) {
     * mSum = sum;
     * mPaths = new HashSet<int[]>();
     * }
     * 
     * @Override
     *           public void visit(BinLinkT<T> node) {
     *           BinTreeT.findAllPathsToSum(node, mSum, mPaths);
     *           }
     *           }
     ***********************************************/
    
    public void visitBreadthFirstQueueLevelOrder(NodeVisitor visitor) {
        LinkedList<BinLinkT<T>> queue = new LinkedList<BinLinkT<T>>();
        queue.add(this);
        do {
            BinLinkT<T> node = queue.remove();
            visitor.visit(node);
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        } while (!queue.isEmpty());
    }
    
    // ====================== PREDICATE ===========================
    
    boolean testPredicate(NodePredicate<BinLinkT<T>> predicate) {
        return predicate.apply(this);
    }
    
    interface NodePredicate<U extends BinLinkT<?>>
    {
        abstract boolean apply(U node);
    }
    
    class VerifyBST<U extends ToInt> implements NodePredicate<BinLinkT<U>>
    {
        public boolean apply(BinLinkT<U> node) {
            if (node != null) {
                if (node.left != null && node.left.data.toInt() >= node.data.toInt())
                    return false;
                if (node.right != null && node.right.data.toInt() <= node.data.toInt())
                    return false;
            }
            return true;
        }
    }
    
    // TODO: adding template argument, as in NodePredicate<BinLinkT<T>>,
    // breaks DeepBinLinkT....
    public boolean verifyBreadthFirstQueueLevelOrder(NodePredicate<BinLinkT<T>> predicate) {
        LinkedList<BinLinkT<T>> queue = new LinkedList<BinLinkT<T>>();
        queue.add(this);
        do {
            BinLinkT<T> node = queue.remove();
            //      if ( ! node.testPredicate(predicate)) {
            //        return false;
            //      }     
            if (!predicate.apply(node)) {
                return false;
            }
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        } while (!queue.isEmpty());
        return true;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean verifyBstBreadthFirst() {
        return verifyBreadthFirstQueueLevelOrder(new BinLinkT.VerifyBST());
    }
    
    public void printBreadthFirstQueueLevelOrder() {
        NodePrinter printer = new NodePrinter();
        visitBreadthFirstQueueLevelOrder(printer);
        System.out.println();
    }
    
    @SuppressWarnings("unchecked")
    public void visitBreadthFirstQueueLevelOrderDepth(NodeVisitor visitor) {
        LinkedList<DeepBinLinkT<T>> queue = new LinkedList<>();
        queue.add(new DeepBinLinkT<T>((BinLinkT<ToInt>) this, 0));
        do {
            DeepBinLinkT<T> node = queue.remove();
            visitor.visit(node);
            if (node.left != null) {
                queue.add(new DeepBinLinkT<T>(node.left, node.depth + 1));
            }
            if (node.right != null) {
                queue.add(new DeepBinLinkT<T>(node.right, node.depth + 1));
            }
        } while (!queue.isEmpty());
    }
    
    public void printBreadthFirstQueueLevelOrderDepth() {
        DepthNodePrinter printer = new DepthNodePrinter();
        visitBreadthFirstQueueLevelOrderDepth(printer);
        System.out.println();
    }
    
    public void printBreadthFirstQueueLevelSpaced(int indent) {
        SpacedDepthPrinter printer = new SpacedDepthPrinter(indent);
        visitBreadthFirstQueueLevelOrderDepth(printer);
        System.out.println();
    }
    
    public void printBreadthFirstQueueLevelSpacedHex(int indent) {
        SpacedDepthPrinterHex printer = new SpacedDepthPrinterHex(indent);
        visitBreadthFirstQueueLevelOrderDepth(printer);
        System.out.println();
    }
    
    /**
     * @deprecated Only works for full trees (No missing branches or leaves).
     */
    @SuppressWarnings("unchecked")
    public <T extends ToInt> void printBreadthFirstQueueLevelSpacedPowerOf2(int depth) {
        LinkedList<BinLinkT<T>> queue = new LinkedList<>();
        int counter = 0, nextPowerOf2 = 1;
        
        //        char spaces[] = new char[80];
        //        Arrays.fill(spaces, ' ');
        //        if (depth < 0 || depth > 80) {
        //            depth = 0;
        //        }
        //        String space = new String(spaces);
        
        System.out.format("\n%s", Spaces.get(depth));
        queue.add((BinLinkT<T>) this);
        do {
            BinLinkT<T> node = queue.remove();
            node.printSpacedChar();
            if (++counter == nextPowerOf2) {
                counter = 0;
                depth = Math.max(0, depth - nextPowerOf2);
                nextPowerOf2 *= 2;
                System.out.format("\n%s", Spaces.get(depth));
            }
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        } while (!queue.isEmpty());
    }
    
    //////////////////////////////////////////////////////////////////////
    
    public HashMap<Integer, Integer> pathSumsBreadthFirstQueueLevelOrder()
    {
        LevelSummer summer = new LevelSummer();
        visitBreadthFirstQueueLevelOrder(summer);
        return summer.sums2counts;
    }
    
    protected class LevelSummer implements NodeVisitor
    {
        @Override
        public <U extends ToInt> void visit(BinLinkT<U> node) {
            int nodeSum = node.data.toInt();
            if (node.left != null) {
                HashSet<Integer> leftSums = findLeftSums(node);
                for (int leftSum : leftSums) {
                    addToMap(nodeSum + leftSum);
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
        
        public <U extends ToInt> HashSet<Integer> findLeftSums(BinLinkT<U> node) {
            return null;
        }
        
        HashMap<Integer, Integer> sums2counts = new HashMap<Integer, Integer>();
    }
    
    public static void printArrayList(ArrayList<String> al) {
        for (int j = 0; j < al.size(); j++)
            System.out.print(al.get(j) + "|");
        System.out.println();
    }
    
    public static void printArray(String[] as) {
        for (int j = 0; j < as.length; j++)
            System.out.print(as[j] + "|");
        System.out.println();
    }
    
    public static void main(String[] args)
    {
        System.out.println("BinLinkT test NOT IMPLEMENTED!");
    }
}
