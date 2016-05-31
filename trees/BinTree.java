package sprax.trees;

import java.util.ArrayList;

/**
 * TODO:
 * Distinguish depth from height and level:
 *    Tree depth = height = distance+1 from (sub)tree root to farthest leaf node = max number of levels spanning root and leaves, so level of leaf == 1
 *    Node depth = level = distance+1 from root to this node = number of levels spanning root and this node, so level of root == 1
 * printBreadthFirst for incomplete trees
 * Make BinLink.SpacedHeightPrinter
 * rotateRootToNode
 * DONE:
 * 
 */

/**
 * 

 Given a binary tree and a node in the binary tree, find the next node in inorder traversal
 3
 [Full Interview Report]
 Country: India
 Interview Type: Written Test
 Tags: Amazon » Coding, Data Structures, Algorithms  » Software Engineer / Developer
 Question #11224909 (Report Dup) | Edit | History


 0
 of 0 vote
 Anonymous on October 18, 2011 |Edit | Edit

 Do the nodes have references to their parent nodes? If so, the next node is either the leftmost child of the right subtree, or the first ancestor node of which the node is in the left subtree of if the node does not have a right child. If not, and the node has no right child, just do an in-order traversal of the tree until you find the node, and output the next node.

 Assuming you do have a reference to the parent:


 public Node findNext(Node s) {
 Node result;
 if (s.rightChild != null) {
 result = s.rightChild;
 while (result.leftChild != null) result = result.leftChild;
 return result;
 } else {
 result = s.parent;
 Node next = s;
 while (result != null && result.rightChild != null && result.rightChild == next) {
 next = result;
 result = result.parent;
 }
 return result;
 }

 asm on October 18, 2011 |Edit | Edit

 and if we do not have a parent node, then:


 node *wanted; // the node whose successor we wish to find
 node *find_rec(node *t, bool& found) {
 if(t == 0) {
 found = false;
 return 0;
 }
 if(t == wanted) {   // found the 'wanted' node
 found = true;   // in the traversal
 return 0;
 }
 node *x = find_recurs(t->left, found);
 if(found) {
 if(x != 0)
 return x; // the successor already found: just pass it by
 return t; // this is a successor
 }
 return find_rec(t->right, found);
 }



 node *inorder_succ() {
 if(wanted->right) {
 ... // proceed as in the above soln
 return t;
 }
 bool found = false; // otherwise start the search from the root
 return find_rec(root, found);
 }
 */

/**
 * TODO
 * 

 Visualize holding an N-ary tree by its root in your left hand. Now hold any node in the tree with your right hand and release the left hand. How the tree would transform and give an algorithm to do it programatically.
 3
 [Full Interview Report]
 Country: India
 Interview Type: In-Person
 Tags: Amazon » Algorithm  » Software Engineer / Developer
 Question #11272961 (Report Dup) | Edit | History


 0
 of 0 vote
 Anonymous on October 21, 2011 |Edit | Edit

 two steps
 firstly, find right node's parent
 secondly, change parent to the right node's child
 msramachandran on October 21, 2011 |Edit | Edit

 That won't work. The answer is

 * Find the node using DFS. This is to get the path to the node from root.
 * Reverse all the links from the node to the root. like make the parent node as the child node.

 That's all. Very simple.
 Reply to Comment
 0
 of 0 vote
 Tiscaao! on October 21, 2011 |Edit | Edit

 Well, I don't think the problem is that simple. An N-ary tree means any node of the tree will have at max N children. Suppose the intermediate node which we are making the new root already has N children. Then if we make its earlier parent also its children, then the new root will have N+1 children, which violates the N-ary tree property.
 Besides to reverse the pointers we need to have parent pointers, which means we have to change tree structure as well.

 Reply to Comment
 Add a Text Comment Add a Text Comment | Add R
 */

/** TODO reverse in-order traversal
 * 

 Given a BST find the kth largest element in the BST with single traversal and without using any extra space.
 3
 Country: India
 Interview Type: Phone Interview
 Tags: Amazon » Trees and Graphs  » Intern
 Question #11298700 (Report Dup) | Edit | History


 0
 of 0 vote
 sg on October 20, 2011 |Edit | Edit

 We can do reverse inorder traversal of a BST in order to obtain kth largest element, by keeping global variable count for number of elements seen so far.

 Other method would be to use order statistics on BST, i.e. to store count of nodes in left subtree and right subtree in a node.

 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.LinkedList;

import sprax.Sx;
import sprax.shuffles.Shuffler;

public class BinTree<T extends BinLink>
{
    // Useful hack for generating trees with ordered data
    static int mCount = 0;
    
    static int setCount(int num)
    {
        return mCount = num;
    }
    
    static int getCount()
    {
        return mCount;
    }
    
    static int addCount(int num)
    {
        return mCount += num;
    }
    
    protected T mRoot = null;
    
    public BinLink getRoot()
    {
        return mRoot;
    }
    
    BinTree(T root)
    {
        this.mRoot = root; // can be null
    }
    
    public int getNumNodes()
    {
        return T.getNumDescendents(mRoot);
    }
    
    public int getDepth()
    {
        return mRoot == null ? 0 : mRoot.getDepth();
    }
    
    public static void printPathData(int sum, int path[], int beg, int end)
    {
        System.out.format("%2d:", sum);
        for (int j = beg; j <= end; j++) {
            Sx.print(" " + path[j]);
        }
        Sx.puts();
    }
    
    /**
     * growGlobalCountingBinTreeRecurse creates a binary tree in pre-order: 0 A 1 B I 2 C F J M 3 D
     * E G H K L N O
     * 
     * @param depth
     * @param n
     * @return
     */
    public static BinTree<BinLink> growGlobalCountingBinTree(int depth, int count,
            boolean leftToRight)
    {
        setCount(0);
        BinLink root = growGlobalCountingBinTreeRecurse(depth, 0, leftToRight);
        return new BinTree<BinLink>(root);
    }
    
    public static BinLink growGlobalCountingBinTreeRecurse(int depth, int n, boolean ltor)
    {
        BinLink root = new BinLink(getCount());
        if (--depth >= 0) {
            if (ltor) {
                root.mLeft = growGlobalCountingBinTreeRecurse(depth, addCount(1), ltor);
                root.mRight = growGlobalCountingBinTreeRecurse(depth, addCount(1), ltor);
            } else {
                root.mRight = growGlobalCountingBinTreeRecurse(depth, addCount(1), ltor);
                root.mLeft = growGlobalCountingBinTreeRecurse(depth, addCount(1), ltor);
            }
        } // otherwise, b.left = b.right = null (by construction)
        return root;
    }
    
    /**
     * The pair growLocalCountingBinTree and growLocalCountingBinTreeRecurse create a binary tree in
     * level order: 0 A 1 B C 2 D E F G 3 H I J K L M N O
     * 
     * @param depth
     * @param n
     * @return
     */
    public static BinTree<BinLink> growLocalCountingBinTree(int depth, int count)
    {
        BinLink root = growLocalCountingBinTreeRecurse(depth, 0);
        return new BinTree<BinLink>(root);
    }
    
    public static BinTree<BinLink> growLocalCountingBinTreeReverse(int depth, int count)
    {
        BinLink root = growLocalCountingBinTreeRecurseReverse(depth, 0);
        return new BinTree<BinLink>(root);
    }
    
    public static BinLink growLocalCountingBinTreeRecurse(int depth, int n)
    {
        BinLink b = new BinLink(n);
        if (--depth >= 0) {
            b.mLeft = growLocalCountingBinTreeRecurse(depth, n * 2 + 1);
            b.mRight = growLocalCountingBinTreeRecurse(depth, n * 2 + 2);
        } // otherwise, b.left = b.right = null (by construction)
        return b;
    }
    
    public static BinLink growLocalCountingBinTreeRecurseReverse(int depth, int n)
    {
        BinLink b = new BinLink(n);
        if (--depth >= 0) {
            b.mLeft = growLocalCountingBinTreeRecurse(depth, n * 2 + 2);
            b.mRight = growLocalCountingBinTreeRecurse(depth, n * 2 + 1);
        } // otherwise, b.left = b.right = null (by construction)
        return b;
    }
    
    public void printDepthFirstRecursivePreOrder()
    {
        if (mRoot != null)
            mRoot.printDepthFirstRecursivePreOrder();
        Sx.puts();
    }
    
    public void printDepthFirstIterativePreOrder()
    {
        if (mRoot != null)
            mRoot.printDepthFirstIterativePreOrder();
        Sx.puts();
    }
    
    public void printDepthFirstRecursiveInOrder()
    {
        if (mRoot != null)
            mRoot.printDepthFirstRecursiveInOrder();
        Sx.puts();
    }
    
    public void printDepthFirstIterativeInOrder()
    {
        if (mRoot != null)
            mRoot.printDepthFirstIterativeInOrder();
        Sx.puts();
    }
    
    public void printDepthFirstRecursivePostOrder()
    {
        if (mRoot != null)
            mRoot.printDepthFirstRecursivePostOrder();
        Sx.puts();
    }
    
    public void printDepthFirstIterativePostOrder()
    {
        if (mRoot != null)
            mRoot.printDepthFirstIterativePostOrder();
        Sx.puts();
    }
    
    public void printBreadthFirstQueueLevelOrder()
    {
        if (mRoot != null)
            mRoot.printBreadthFirstLevelOrder();
        Sx.puts();
    }
    
    public void printBreadthFirstQueueLevelSpaced()
    {
        if (mRoot != null)
            mRoot.printBreadthFirstQueueLevelSpaced();
        Sx.puts();
    }
    
    /**
     * This only works for complete trees, that is, the tree is perfectly balanced and every node
     * has both left and right children except for last-level leaves. If the root has tree depth k,
     * numNodes = 2**k - 1.
     * 
     * @param space
     */
    public void printBreadthFirstQueueLevelSpacedHexComplete(int space)
    {
        if (mRoot != null)
            mRoot.printBreadthFirstQueueLevelSpacedHex(space);
        Sx.puts();
    }
    
    public static void printBreadthFirstQueueLevelOrderSpacedHex(BinLink root)
    {
        LinkedList<BinLink> queue = new LinkedList<BinLink>();
        queue.add(root);
        do {
            BinLink node = queue.remove();
            
            // visitor.visit(node);
            
            if (node.mLeft != null) {
                queue.add(node.mLeft);
            }
            if (node.mRight != null) {
                queue.add(node.mRight);
            }
        } while (!queue.isEmpty());
    }
    
    @SuppressWarnings("deprecation")
    public static int test_print(BinTree<?> bintree)
    {
        Sx.print("printDepthFirstRecursivePreOrder  0:");
        bintree.printDepthFirstRecursivePreOrder();
        Sx.print("printDepthFirstIterativePreOrder  1:");
        bintree.printDepthFirstIterativePreOrder();
        Sx.print("printDepthFirstIterativePreOrder  2:");
        bintree.printDepthFirstIterativePreOrder();
        
        Sx.puts("\n    growLocalCountingBinTreeRecurse:");
        bintree = growLocalCountingBinTree(3, 15);
        bintree.printBreadthFirstQueueLevelSpaced();
        Sx.print("printDepthFirstRecursivePreOrder  0:");
        bintree.printDepthFirstRecursivePreOrder();
        Sx.print("printDepthFirstIterativePreOrder  1:");
        bintree.printDepthFirstIterativePreOrder();
        Sx.print("printDepthFirstIterativePreOrder  2:");
        bintree.printDepthFirstIterativePreOrder();
        
        Sx.print("printDepthFirstRecursiveInOrder   0:");
        bintree.printDepthFirstRecursiveInOrder();
        Sx.print("printDepthFirstIterativeInOrder   1:");
        bintree.printDepthFirstIterativeInOrder();
        Sx.print("printDepthFirstIterativeInOrder   2:");
        bintree.printDepthFirstIterativeInOrder();
        
        Sx.print("printDepthFirstRecursivePostOrder 0:");
        bintree.printDepthFirstRecursivePostOrder();
        Sx.print("printDepthFirstIterativePostOrder 1:");
        bintree.printDepthFirstIterativePostOrder();
        Sx.print("printDepthFirstIterativePostOrder 2:");
        bintree.printDepthFirstIterativePostOrder();
        
        Sx.print("\n    growBreadthFirstQueueLevelOrder:\n");
        bintree = TreeFactory.growBreadthFirstQueueLevelOrder(1, 15);
        
        Sx.print("printBreadthFirstQueueLevelOrder:  ");
        bintree.printBreadthFirstQueueLevelOrder();
        Sx.print("printBreadthFirstQueueLevelSpaced: ");
        bintree.printBreadthFirstQueueLevelSpaced();
        
        Sx.print("printDepthFirstRecursivePreOrder:  ");
        bintree.printDepthFirstRecursivePreOrder();
        Sx.print("printDepthFirstIterativePreOrder:  ");
        bintree.printDepthFirstIterativePreOrder();
        
        Sx.print("printDepthFirstRecursiveInOrder:   ");
        bintree.printDepthFirstRecursiveInOrder();
        Sx.print("printDepthFirstIterativeInOrder:   ");
        bintree.printDepthFirstIterativeInOrder();
        
        Sx.print("printDepthFirstRecursivePostOrder: ");
        bintree.printDepthFirstRecursivePostOrder();
        Sx.print("printDepthFirstIterativePostOrder: ");
        bintree.printDepthFirstIterativePostOrder();
        
        Sx.print("printBreadthFirstQueueLevelOrder:  ");
        bintree.printBreadthFirstQueueLevelOrder();
        
        Sx.print("printBreadthFirstQueueLevelSpacedPowerOf2: ");
        bintree.mRoot.printBreadthFirstQueueLevelSpacedPowerOf2(16);
        
        Sx.print("printBreadthFirstQueueLevelSpaced: ");
        bintree.printBreadthFirstQueueLevelSpaced();
        
        return 0;
    }
    
    public void printBreadthFirstQueueLevelOrderDepth()
    {
        if (mRoot != null)
            mRoot.printBreadthFirstQueueLevelSpaced();
        Sx.puts();
    }
    
    // //////////////////////////////// HEAPIFY ///////////////////////////////////////
    
    public void heapify()
    {
        if (mRoot != null)
            heapify(mRoot);
    }
    
    public static void heapify(BinLink root)
    {
        BinLink node = root;
        if (root.mLeft != null && root.mLeft.mKey > root.mKey) {
            node = root.mLeft;
        }
        if (root.mRight != null && root.mRight.mKey > node.mKey) {
            node = root.mRight;
        }
        if (node != root) {
            int temp = root.mKey;
            root.mKey = node.mKey;
            node.mKey = temp;
            heapify(node);
        }
    }
    
    public static void test_heapify()
    {
        BinTree<BinLink> tree = TreeFactory.growBreadthFirstQueueLevelOrder(1, 15);
        Sx.print("printBreadthFirstQueueLevelOrderDepth: ");
        tree.printBreadthFirstQueueLevelOrderDepth();
        Sx.puts("After heapify -- levels and preorder:");
        tree.heapify();
        tree.printBreadthFirstQueueLevelOrderDepth();
        tree.printDepthFirstRecursivePreOrder();
    }
    
    /**
     * findPathToNode finds a path from the tree's root to this node. On success, it returns a
     * simple array starting with the tree's root node and ending with the node's parent. That is,
     * it returns T path[M] == [mRoot, ..., node.getParent().getParent(), node.getParent()] where M
     * = tree.depth - node.depth. The smallest possible path is thus T path[1] = {mRoot}, where node
     * is a child of the root. On failure, it returns null. This includes the case node == root, as
     * there is no path from any node to itself (unless you are willing to call null a valid path,
     * in which case null is the correct answer).
     * 
     * This method uses a depth-limited search for efficiency. It computes the depths of the tree
     * and the search node, and limits the search depth to the difference between them. Even if the
     * node is not in this tree, the depth comparison is likely to save time.
     * 
     * @param node
     * @return array of nodes starting with the root and ending with the node's parent
     */
    public BinLink[] findPathToNode(T node)
    {
        if (node == null || node == mRoot)
            return null;
        int treeDepth = getDepth();
        int nodeDepth = node.getDepth();
        if (treeDepth <= nodeDepth)
            return null; // node cannot be in this tree
        int searchDepth = treeDepth - nodeDepth; // must be > 0
        BinLink path[] = new BinLink[searchDepth];
        if (findDepthLimitedPathToNode(mRoot, node, path, searchDepth))
            return path;
        return null;
    }
    
    public boolean findDepthLimitedPathToNode(BinLink ancestor, T node, BinLink path[],
            int searchDepth)
    {
        path[path.length - searchDepth] = ancestor; // Keep track of the path we're trying.
        if (node == ancestor.mLeft || node == ancestor.mRight) { // Success! This ancestor is the
                                                                 // node's parent.
            return true;
        }
        if (--searchDepth > 0) {
            if (findDepthLimitedPathToNode(ancestor.mLeft, node, path, searchDepth)) // Try left.
                return true;
            if (findDepthLimitedPathToNode(ancestor.mRight, node, path, searchDepth)) // Try right.
                return true;
        }
        return false;
    }
    
    HashMap<Integer, ArrayList<int[]>> paths = new HashMap<Integer, ArrayList<int[]>>();
    
    public void storePathData(int sum, int path[], int beg, int end)
    {
        int subPath[] = new int[1 + end - beg];
        for (int k = 0, j = beg; j <= end; k++, j++)
            subPath[k] = path[j];
        if (!paths.containsKey(sum)) {
            paths.put(sum, new ArrayList<int[]>());
        }
        paths.get(sum).add(subPath);
    }
    
    static int[] copySubPath(int path[], int subLength)
    {
        int subPath[] = new int[subLength];
        for (int j = 0; j < subLength; j++) {
            subPath[j] = path[j];
        }
        return subPath;
    }
    
    static int[] copyReversedSubPath(int path[], int subLength)
    {
        int subPath[] = new int[subLength];
        for (int k = 0, j = subLength; --j >= 0; k++) {
            subPath[j] = path[k];
        }
        return subPath;
    }
    
    public void handlePathData(int sum, int path[], int beg, int end)
    {
        printPathData(sum, path, beg, end);
        storePathData(sum, path, beg, end);
    }
    
    public void findDescendingPathsToSum(BinLink node, int sum, int path[], int level)
    {
        if (node == null)
            return;
        
        if (level > path.length) {
            System.out
                    .format("findDescendingPathsToSum: level > path.length (%d > %d)\n",
                            level, path.length);
            return;
        }
        path[level] = node.mKey;
        int tmp = 0;
        for (int j = level; j >= 0; j--) {
            tmp += path[j];
            if (tmp == sum) {
                handlePathData(sum, path, j, level);
            }
        }
        findDescendingPathsToSum(node.mLeft, sum, path, level + 1);
        findDescendingPathsToSum(node.mRight, sum, path, level + 1);
        // path[level] = Integer.MIN_VALUE; // Don't need to zap it; it won't be
        // read again
    }
    
    public static void findAllDescendingPaths(BinLink node, int level,
            int path[], HashMap<Integer, ArrayList<int[]>> pathsFound, boolean verbose)
    {
        if (node == null)
            return;
        
        path[level] = node.mKey;
        int sum = 0;
        for (int j = level; j >= 0; j--) {
            sum += path[j];
        }
        if (!pathsFound.containsKey(sum)) {
            pathsFound.put(sum, new ArrayList<int[]>());
        }
        int subPath[] = copySubPath(path, level + 1);
        if (verbose)
            printPathData(sum, subPath, 0, level);
        pathsFound.get(sum).add(subPath);
        
        findAllDescendingPaths(node.mLeft, level + 1, path, pathsFound, verbose);
        findAllDescendingPaths(node.mRight, level + 1, path, pathsFound, verbose);
    }
    
    public void findAllDascendingPathsToSum(BinLink node, int sum)
    {
        int depth = node.getDepth();
        int path[] = new int[depth];
        findDescendingPathsToSum(node, sum, path, 0);
    }
    
    public void findAllDescendingPathsToSum(int sum)
    {
        findAllDascendingPathsToSum(mRoot, sum);
    }
    
    protected interface TreeNodeVisitor
    {
        abstract void visit(BinLink node);
    }
    
    public class TreeSumPathFinder implements TreeNodeVisitor
    {
        int            mSum;  // The path sum to be found.
        HashSet<int[]> mPaths; // The collection of all paths for this sum.
        
        TreeSumPathFinder(int sum)
        {
            mSum = sum;
            mPaths = new HashSet<int[]>();
        }
        
        @Override
        public void visit(BinLink node)
        {
            findAllPathsToSum(node, mSum, mPaths, false);
        }
    }
    
    public static void saveLeftRightPathData(int sum, int nodeData, int sumLeft, int[] pathLeft,
            int sumRight, int[] pathRight, HashSet<int[]> savePaths, boolean verbose)
    {
        int path[] = new int[1 + pathLeft.length + pathRight.length];
        int q = 0;
        for (int j = pathLeft.length; --j >= 0;) {
            path[q++] = pathLeft[j];
        }
        path[q++] = nodeData;
        for (int k = 0; k < pathRight.length; k++) {
            path[q++] = pathRight[k];
        }
        savePaths.add(path);
        if (verbose) {
            System.out.format("sum %3d == %2d + %2d + %2d:", sum, sumLeft, nodeData, sumRight);
            assert (q == path.length);
            for (int j = 0; j < q; j++) {
                Sx.print(" " + path[j]);
            }
            Sx.puts();
        }
    }
    
    public static void findAllPathsToSum(BinLink node, int sum, int path[],
            HashSet<int[]> savePaths, int level, boolean verbose)
    {
        if (node == null)
            return;
        
        // for each path LP starting at node.left, sum it and add the value
        // at this node to get sumLeft. Then find all paths starting at
        // node.right that sum to sumRight == sum - sumLeft. Append each such
        // path to LP + this node to get all the paths rooted at this node
        // whose data add up to sum.
        
        // int thisNodeData = node.data;
        HashMap<Integer, ArrayList<int[]>> pathsLeft = new HashMap<Integer, ArrayList<int[]>>();
        HashMap<Integer, ArrayList<int[]>> pathsRight = new HashMap<Integer, ArrayList<int[]>>();
        
        // Add the empty path for both sides
        pathsLeft.put(0, new ArrayList<int[]>());
        pathsLeft.get(0).add(new int[0]);
        pathsRight.put(0, new ArrayList<int[]>());
        pathsRight.get(0).add(new int[0]);
        
        findAllDescendingPaths(node.mLeft, level, path, pathsLeft, verbose);
        findAllDescendingPaths(node.mRight, level, path, pathsRight, verbose);
        for (int sumLeft : pathsLeft.keySet()) {
            int sumRight = sum - node.mKey - sumLeft;
            if (pathsRight.containsKey(sumRight)) {
                for (int pathLeft[] : pathsLeft.get(sumLeft)) {
                    for (int pathRight[] : pathsRight.get(sumRight)) {
                        saveLeftRightPathData(sum, node.mKey, sumLeft, pathLeft, sumRight,
                                pathRight, savePaths, verbose);
                    }
                }
            }
        }
    }
    
    public static void findAllPathsToSum(BinLink node, int sum, HashSet<int[]> savePaths,
            boolean verbose)
    {
        int depth = node.getDepth();
        int path[] = new int[depth];
        findAllPathsToSum(node, sum, path, savePaths, 0, verbose);
    }
    
    public void findAllPathsToSumBreadthFirstQueueLevelOrder(int sum)
    {
        // Make a new path finder for each desired path sum.
        // The collection of paths found for this sum is for the whole
        // tree, not any one node.
        BinLink.SumPathFinder pathFinder = mRoot.new SumPathFinder(sum);
        mRoot.visitBreadthFirstLevelOrder(pathFinder);
        int numPaths = pathFinder.mPaths.size();
        if (numPaths > 0) {
            int maxLen = 0;
            for (int path[] : pathFinder.mPaths) {
                if (maxLen < path.length) {
                    maxLen = path.length;
                }
            }
            System.out.format("Found %d paths summing to %d   longest: %d\n"
                    , numPaths, sum, maxLen);
        }
    }
    
    public void findAllPathsToSum(int sum)
    {
        findAllPathsToSumBreadthFirstQueueLevelOrder(sum);
    }
    
    public void findAllPathsToSums(int minSum, int maxSum, boolean verbose)
    {
        for (int sum = minSum; sum < maxSum; sum++) {
            if (verbose)
                System.out.format("findAllPathsToSums(%2d):\n", sum);
            findAllPathsToSum(sum);
        }
    }
    
    public void test_findAllPathsToSum_old(int sum, boolean verbose)
    {
        HashSet<int[]> foundPaths = new HashSet<int[]>();
        findAllPathsToSum(mRoot, sum, foundPaths, verbose);
        findAllPathsToSum(mRoot.mLeft, sum, foundPaths, verbose);
        findAllPathsToSum(mRoot.mRight, sum, foundPaths, verbose);
        findAllPathsToSum(mRoot.mRight.mLeft, sum, foundPaths, verbose);
        findAllPathsToSum(mRoot.mRight.mRight, sum, foundPaths, verbose);
        findAllPathsToSum(mRoot.mRight.mLeft.mRight, sum, foundPaths, verbose);
    }
    
    public static int test_pathSums(BinTree<?> tree7, int sum, boolean verbose)
    {
        tree7.printBreadthFirstQueueLevelSpacedHexComplete(16);
        tree7.findAllDescendingPathsToSum(sum);
        Sx.puts("paths.size is " + tree7.paths.size());
        System.out.format("paths.get(%d).size() is %d\n", sum, tree7.paths.get(sum).size());
        
        // Path sums
        Sx.puts("findAllPathsToSum:");
        tree7.findAllPathsToSum(sum);
        
        Sx.puts("findAllPathsToSums:");
        tree7.findAllPathsToSums(0, 40, verbose);
        return 0;
    }
    
    protected interface TreeNodePredicate<T extends BinLink>
    { // TODO: ??
        abstract boolean apply(T node);
    }
    
    class TreeVerifyDepth implements TreeNodePredicate<DeepBinLink>
    {
        @Override
        public boolean apply(DeepBinLink node)
        {
            if (node != null) {
                // If this class were not declared static, and the node were not
                // specified as in node.depth and node.computeDepth(), these
                // fields belong to the root node, and this apply method would
                // always return true.
                if (node.depth >= 3) {
                    // polymorphic dispatch
                    Sx.puts("TreeVerifyDepth is applied to: "
                            + node.getClass().getCanonicalName());
                }
                if (node.depth != node.computeDepth()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public static int test_bst(BinLink root)
    {
        boolean bBst = false;
        bBst = root.verifyBstBreadthFirst();
        Sx.puts("root7.verifyBstBreadthFirst() is " + bBst);
        bBst = root.verifyBstInOrderRecursive(true);
        Sx.puts("root7.verifyBstInOrderRecursive() is " + bBst);
        return 0;
    }
    
    public static int test_deepBinLink(BinTree<DeepBinLink> deepBinTree)
    {
        // Verify each node's sub-tree depth, or "height of the sub tree"
        boolean bDepth = false;
        bDepth = deepBinTree.verifyTreeBreadthFirstQueueLevelOrder(deepBinTree.mRoot,
                deepBinTree.new TreeVerifyDepth());
        Sx.puts("deepBinTree : verifyTreeBreadthFirstQueueLevelOrder() is " + bDepth);
        return 0;
    }
    
    // TODO: Remove. It seems pointless to define testPredicate for
    // the tree instead of the nodes. Even though the template parameter
    // type T allows for double dispatch via polymorphism -- that is,
    // if the predicate's apply method calls methods on node -- that
    // much is already accomplished by simply calling predicate.apply(node)
    // directly.
    boolean testPredicate(T node, TreeNodePredicate<T> predicate)
    {
        return predicate.apply(node);
    }
    
    public boolean verifyTreeBreadthFirstQueueLevelOrder(T root, TreeNodePredicate<T> predicate)
    {
        if (root != null) {
            LinkedList<T> queue = new LinkedList<T>();
            queue.add(root);
            do {
                T node = queue.remove();
                if (!predicate.apply(node)) {
                    return false;
                }
                // Accomplishes nothing more than calling predicate.apply(node) directly.
                // if ( ! testPredicate(node, predicate)) {
                // return false;
                // }
                if (node.mLeft != null) {
                    queue.add((T) node.mLeft);
                }
                if (node.mRight != null) {
                    queue.add((T) node.mRight);
                }
            } while (!queue.isEmpty());
        }
        return true;
    }
    
    public boolean containsNode(T node)
    {
        if (mRoot == null)
            return false;
        return mRoot.isAncestorOf(node);
    }
    
    public T getParentOf(T node)
    {
        if (mRoot != null) {
            return (T) mRoot.getParentOfDescendantNode(node);
        }
        return null;
    }
    
    class TreeContains implements TreeNodePredicate<T>
    {
        int mSearchData;
        
        TreeContains(int data)
        {
            mSearchData = data;
        }
        
        public boolean apply(BinLink node)
        {
            if (node == null)
                return false;
            if (node.mKey == mSearchData)
                return true;
            return (apply(node.mLeft) || apply(node.mRight));
        }
    }
    
    BinLink findCommonAncestorRecursiveHelper(BinLink node, TreeNodePredicate containsP,
            TreeNodePredicate containsQ)
    {
        // assert(node != null); // TODO: remove this, it's redundant.
        boolean bLeftContainsP = containsP.apply(node.mLeft);
        boolean bLeftContainsQ = containsQ.apply(node.mLeft);
        if (bLeftContainsP != bLeftContainsQ)
            return node;
        
        BinLink sameSideNode = bLeftContainsP ? node.mLeft : node.mRight;
        return findCommonAncestorRecursiveHelper(sameSideNode, containsP, containsQ);
    }
    
    BinLink findCommonAncestorFromData(int dataP, int dataQ)
    {
        if (mRoot == null) // not necessary, but efficient
            return null;
        TreeContains containsP = new TreeContains(dataP);
        TreeContains containsQ = new TreeContains(dataQ);
        if (!containsP.apply(mRoot) || !containsQ.apply(mRoot))
            return null;
        
        return findCommonAncestorRecursiveHelper(mRoot, containsP, containsQ);
    }
    
    BinLink findCommonAncestorA(BinLink linkP, BinLink linkQ)
    {
        return findCommonAncestorFromData(linkP.mKey, linkQ.mKey);
    }
    
    class BstContains implements TreeNodePredicate<T>
    {
        int mSearchData;
        
        BstContains(int data)
        {
            mSearchData = data;
        }
        
        public boolean apply(BinLink node)
        {
            if (node == null)
                return false;
            if (node.mKey > mSearchData)
                return apply(node.mLeft);
            if (node.mKey < mSearchData)
                return apply(node.mRight);
            return true;
        }
    }
    
    BinLink findCommonAncestorBst(BinLink linkP, BinLink linkQ)
    {
        return findCommonAncestorBstFromData(linkP.mKey, linkQ.mKey);
    }
    
    BinLink findCommonAncestorBstFromData(int dataP, int dataQ)
    {
        if (mRoot == null) // not necessary, but efficient
            return null;
        BstContains containsP = new BstContains(dataP);
        BstContains containsQ = new BstContains(dataQ);
        if (!containsP.apply(mRoot) || !containsQ.apply(mRoot))
            return null;
        
        return findCommonAncestorRecursiveHelper(mRoot, containsP, containsQ);
    }
    
    static BinLink makeArbitList(int len)
    {
        // next==right, arbitrary==left
        assert (1 < len && len < 27);
        BinLink array[] = new BinLink[len];
        BinLink tail = null;
        while (--len >= 0) {
            tail = new BinLink(len, tail, tail); // initially, arbit == next
            array[len] = tail;
        }
        len = array.length; // reset len to original value
        array[len - 1].mLeft = tail; // replace null with pointer to head
        
        // TODO: just shuffle the array, then assign (1 extra loop, but code re-use)
        Random rng = new Random(); // java.util.Random.
        for (int j = len; --j >= 0;) {
            int k = rng.nextInt(len); // 0 <= k < length
            BinLink temp = array[j].mLeft;
            array[j].mLeft = array[k].mLeft;
            array[k].mLeft = temp;
        }
        return tail;
    }
    
    static BinLink makeArbitListShuffle(int len)
    {
        // next==right, arbitrary==left
        assert (1 < len && len < 27);
        BinLink array[] = new BinLink[len];
        BinLink tail = null;
        while (--len >= 0) {
            tail = new BinLink(len, tail, tail); // initially, arbit == next
            array[len] = tail;
        }
        len = array.length; // reset len to original value
        BinLink head = tail;
        array[len - 1].mLeft = head; // replace null with pointer to head
        Shuffler.shuffle(array);
        while (tail != null) {
            tail.mLeft = array[--len];
            tail = tail.mRight;
        }
        return head;
    }
    
    static final int sLc = 'a' - 'A';
    
    static BinLink cloneArbitList(BinLink head, boolean verbose)
    {
        // Dispose of special cases
        if (head == null)
            return null;
        
        // 1st pass: create new list, but with each new.arbt->old, and old.next->new
        BinLink newHead = new BinLink(head.mKey + sLc, head, null); // arbit is old head
        BinLink oldNext = head.mRight; // make copy for loop var
        head.mRight = newHead; // temporarily point old list node to its clone; we'll restore it
                               // later.
        BinLink newLink = newHead, tmpLink; // make loop control var and temp
        while (oldNext != null) {
            newLink.mRight = new BinLink(oldNext.mKey + sLc, oldNext, null); // make copy node's
                                                                             // arbit -> original
                                                                             // link, for now
            newLink = newLink.mRight; // advance the copy list, this new link is all set.
            tmpLink = oldNext; // save original node before advancing
            oldNext = oldNext.mRight; // advance the original list
            tmpLink.mRight = newLink; // use original node's next pointer to point to its clone,
                                      // temporarily
        }
        if (verbose)
            printArbitList(newHead);
        
        // 2nd pass
        for (newLink = newHead; newLink != null; newLink = newLink.mRight) {
            tmpLink = newLink.mLeft.mLeft.mRight; // get the link to assign as newLink's arbit
            newLink.mLeft.mLeft.mRight = tmpLink.mLeft; // don't need this old link to point into
                                                        // the new list anymore, so fix its next
                                                        // pointer
            newLink.mLeft = tmpLink;
        }
        if (verbose)
            printArbitList(newHead);
        
        return newHead;
    }
    
    static void printArbitList(BinLink head)
    {
        Sx.puts("ArbitList:");
        for (BinLink link = head; link != null; link = link.mRight) {
            link.printSpacedChar();
        }
        Sx.puts();
        for (BinLink link = head; link != null; link = link.mRight) {
            link.mLeft.printSpacedChar();
        }
        Sx.puts();
    }
    
    static void printArray(int arr[], String label)
    {
        Sx.print(label);
        for (int k = 0; k < arr.length; k++)
            Sx.print(" " + arr[k]);
        Sx.puts();
    }
    
    public static boolean mirrorTrees(BinLink r1, BinLink r2)
    {
        if (r1 == null && r2 == null)
            return true;
        if (r1 == null || r2 == null)
            return false;
        if (r1.mKey != r2.mKey)
            return false;
        return mirrorTrees(r1.mLeft, r2.mRight) && mirrorTrees(r1.mRight, r2.mLeft);
    }
    
    public static int test_symmetric()
    {
        BinTree<BinLink> treeLeftToRight = null, treeRightToLeft = null;
        Sx.puts("\n    Symmetric Tree Test:");
        treeLeftToRight = growGlobalCountingBinTree(3, 15, true);
        treeLeftToRight.printBreadthFirstQueueLevelSpaced();
        treeRightToLeft = growGlobalCountingBinTree(3, 15, false);
        treeRightToLeft.printBreadthFirstQueueLevelSpaced();
        boolean bSymmetricA = mirrorTrees(treeLeftToRight.mRoot, treeRightToLeft.mRoot);
        Sx.puts("TF: The 2nd tree above the mirror imagse of the 1st?  " + bSymmetricA);
        
        int temp = treeRightToLeft.mRoot.mRight.mLeft.mRight.mKey;
        treeRightToLeft.mRoot.mRight.mLeft.mRight.mKey = treeRightToLeft.mRoot.mRight.mLeft.mLeft.mKey;
        treeRightToLeft.mRoot.mRight.mLeft.mLeft.mKey = temp;
        treeRightToLeft.printBreadthFirstQueueLevelSpaced();
        boolean bSymmetricB = mirrorTrees(treeLeftToRight.mRoot, treeRightToLeft.mRoot);
        Sx.puts("TF: The 3nd tree above the mirror imagse of the 1st?  " + bSymmetricB);
        
        if (bSymmetricA == true && bSymmetricB == false)
            return 0;
        return -1;
    }
    
    public void rotateToRoot(T node, boolean verbose)
    {
        // If node is null, or is this tree's root, or is not in this tree,
        // getParentOf(node) will return null
        T parent = getParentOf(node);
        if (parent != null) {
            T newRoot = node;
            do {
                rotateNodeAndParentWithoutNullCheck(node, parent);
                if (verbose) {
                    printBreadthFirstQueueLevelSpaced();
                }
                node = (T) parent;
                parent = getParentOf(node);
            } while (parent != null);
            mRoot = newRoot;
        }
    }
    
    public void rotateToRoot_old(T node, boolean verbose)
    {
        if (node == null || mRoot == null)
            return;
        // If node is null, or is this tree's root, or is not in this tree,
        // getParentOf(node) will return null
        T newRoot = node;
        do {
            T parent = getParentOf(node);
            if (parent != null) {
                rotateNodeAndParentWithoutNullCheck(node, parent);
                if (verbose)
                    printBreadthFirstQueueLevelSpaced();
                node = parent;
            }
        } while (node != null);
        
        mRoot = newRoot;
    }
    
    public void rotateNodeAndParent(T node, T parent)
    {
        if (node == null || parent == null) {
            return;
        }
        rotateNodeAndParentWithoutNullCheck(node, parent);
    }
    
    protected void rotateNodeAndParentWithoutNullCheck(T node, T parent)
    {
        if (node == parent.mLeft) {
            BinLink temp = node.mRight;
            node.mRight = parent;
            parent.mLeft = temp;
        } else {
            BinLink temp = node.mLeft;
            node.mLeft = parent;
            parent.mRight = temp;
        }
    }
    
    /*
     * public void rotateLeft(BinTree<T> tree, T xx) { BinLink parent = getParentOf(xx);
     * 
     * BinLink yy = xx.right; xx.right = yy.left; if (yy.left != null) { yy.left.parent = xx; }
     * yy.parent = xx.parent; if (xx.parent == null) { tree.mRoot = yy; } else { if (xx ==
     * xx.parent.left) xx.parent.left = yy; else xx.parent.right = yy; } yy.left = xx; xx.parent =
     * yy; }
     */
    
    /**
     * unit_test
     */
    public static void unit_test()
    {
        
        boolean verbose = true;
        
        Sx.puts("\n    growLocalCountingBinTreeRecurse for rotation:");
        BinTree<BinLink> tree5 = growLocalCountingBinTree(3, 15);
        tree5.mRoot.mLeft.mRight = null;
        tree5.mRoot.mRight.mLeft.mRight = null;
        BinTree<BinLink> tree6 = growLocalCountingBinTreeReverse(3, 15); // TODO: bogus
        tree6.printBreadthFirstQueueLevelSpaced();
        tree5.printBreadthFirstQueueLevelSpaced();
        tree5.rotateToRoot(tree5.mRoot.mLeft, verbose);
        Sx.puts("After rotating, new root = old root.left");
        tree5.printBreadthFirstQueueLevelSpaced();
        
        int depth = 3;
        if (depth > 2) {
            
            BinLink arbitOrig = makeArbitListShuffle(26);
            printArbitList(arbitOrig);
            BinLink arbitCopy = cloneArbitList(arbitOrig, verbose);
            printArbitList(arbitCopy);
            
            BinTree<BinLink> tree3 = null, tree9 = null;
            
            test_symmetric();
            BinLink ll7 = new DeepBinLink(3, 1);
            BinLink lr7 = new DeepBinLink(5, 1);
            BinLink left7 = new DeepBinLink(4, 2, ll7, lr7);
            BinLink rl7 = new DeepBinLink(9, 1);
            BinLink rr7 = new DeepBinLink(12, 2); // TODO: intentional error here?
            BinLink right7 = new DeepBinLink(10, 2, rl7, rr7);
            DeepBinLink root7 = new DeepBinLink(7, 3, left7, right7);
            BinTree<DeepBinLink> tree7 = new BinTree<DeepBinLink>(root7);
            
            BinLink commonAncestor = null;
            commonAncestor = tree7.findCommonAncestorA(left7, rr7);
            Sx.puts("tree7.findCommonAncestorA(left7, rr7) gives " + commonAncestor.mKey);
            commonAncestor = tree7.findCommonAncestorA(rl7, rr7);
            Sx.puts("tree7.findCommonAncestorA(rl7, rr7) gives " + commonAncestor.mKey);
            
            boolean doAll = true;
            if (doAll) {
                test_print(tree7);
                test_pathSums(tree7, 12, verbose);
                test_deepBinLink(tree7);
                test_bst(tree3.mRoot);
                test_bst(root7);
                test_heapify(); // TODO: only tests on application
            }
            
            // HashMap<Integer, Integer> sums = tree3.mRoot.pathSumsBreadthFirstQueueLevelOrder();
            
            // Make a random binary tree with N = 2^M nodes: int N = 2*2*2;
            
            depth = 4;
            BinLink root4 = growGlobalCountingBinTreeRecurse(depth, 0, true);
            BinTree<BinLink> tree4 = new BinTree<BinLink>(root4);
            Sx.print("printBreadthFirstQueueLevelOrder: ");
            tree4.printBreadthFirstQueueLevelOrder();
            Sx.print("printBreadthFirstQueueLevelSpaced: ");
            tree4.printBreadthFirstQueueLevelSpaced();
            
            /*
             * root4 = growGlobalCountingBinTree(4, rng, 32); tree4 = new BinTree(root4);
             * S.print("printTreeDepthFirstRecursivePreOrder: ");
             * BinLink.printTreeDepthFirstRecursivePreOrder(root4); S.puts();
             * 
             * tree4.heapify(); tree4.printDepthFirstRecursivePreOrder();
             * 
             * S.puts(); int d = tree4.getDepth(); int n = tree4.getNumNodes();
             * S.puts("getDepth(root) got " + d + " and getNumNodes() got " + n);
             */
            
            root4.printBreadthFirstLevelOrderDepth();
            
            root4.printLevelOrder();
            
            root4 = new BinLink(2);
            root4.mLeft = new BinLink(0);
            root4.mLeft.mRight = new BinLink(1);
            root4.mRight = new BinLink(3);
            root4.printDepthFirstRecursiveInOrder();
            Sx.puts();
            root4.printDepthFirstIterativeInOrder();
        }
    }
    
    public static void main(String[] args)
    {
        unit_test();
    }
}
