package sprax.trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
//import java.util.HashSet;
import java.util.LinkedList;




class Int implements ToInt
{
    int myInt;
    Int(int mi)  { myInt = mi; }
    @Override
    public int toInt() {
        return myInt;
    }    
}

class BinLinkTI extends BinLinkT<Int>
{
    public BinLinkTI(Int d) {
        data = d;
    }

    public BinLinkTI(int count) {
        this(new Int(count));
    }

    @Override
    public BinLinkT<Int> createNode(Int data) {
        return new BinLinkTI(data);
    }
    
}

public abstract class BinTreeT<T extends BinLinkT> 
{
  static int sDbg = 1;

  // Useful hack for generating trees with ordered data
  static int mCount = 0;
  static int setCount(int num) { return mCount = num; }
  static int getCount()        { return mCount; }
  static int addCount(int num) { return mCount += num; }

  private T root = null;

  BinTreeT(T root) {
    this.root = root; // can be null
  }

  public int getNumNodes() {
    return T.getNumDescendentsStatic(root);
  }

  public int getDepth() {
    return root == null ? 0 : root.getDepth();
  }

  public static void printPathData(int sum, int path[], int beg, int end) {
    System.out.format("%2d:", sum);
    for (int j = beg; j <= end; j++) {
      System.out.print(" " + path[j]);
    }
    System.out.println();
  }

  /** 
   * growGlobalCountingBinTreeRecurse creates a binary tree in pre-order:
   * 0               A 
   * 1              B I 
   * 2            C F J M 
   * 3        D E G H K L N O 
   * @param depth
   * @param n
   * @return
   */
  /***** STATIC ***************************************************************************
  public static BinTreeT<BinLinkT<Int>> growGlobalCountingBinTree(int depth, int count)
  {
    setCount(0);
    BinLinkT root = growGlobalCountingBinTreeRecurse(depth, 0);
    return new BinTreeT<BinLinkT<Int>>(root);
  }

  public static BinLinkT growGlobalCountingBinTreeRecurse(int depth, int n) {
    BinLinkTI root = new BinLinkTI(getCount());
    if (--depth >= 0) {
      root.left  = growGlobalCountingBinTreeRecurse(depth, addCount(1));
      root.right = growGlobalCountingBinTreeRecurse(depth, addCount(1));
    }   // otherwise, b.left = b.right = null (by construction)
    return root;
  }

  /** 
   * The pair growLocalCountingBinTree and growLocalCountingBinTreeRecurse 
   * create a binary tree in level order:
   * 0               A 
   * 1              B C 
   * 2            D E F G 
   * 3        H I J K L M N O 
   * @param depth
   * @param n
   * @return
   *
  public static BinTreeT<BinLinkT> growLocalCountingBinTree(int depth, int count) {
    setCount(0);
    BinLinkT root = growLocalCountingBinTreeRecurse(depth, 0);
    return new BinTreeT<BinLinkT>(root);
  }

  public static BinLinkT growLocalCountingBinTreeRecurse(int depth, int n) {
    BinLinkT b = new BinLinkT(n);
    if (--depth >= 0) {
      b.left  = growLocalCountingBinTreeRecurse(depth, n*2 + 1);
      b.right = growLocalCountingBinTreeRecurse(depth, n*2 + 2);
    }   // otherwise, b.left = b.right = null (by construction)
    return b;
  }
*************************************************************************************************/

  public void printDepthFirstRecursivePreOrder() {
    if (root != null)
      root.printDepthFirstRecursivePreOrder();
    System.out.println();
  }

  public void printDepthFirstIterativePreOrder() {
    if (root != null)
      root.printDepthFirstIterativePreOrder();
    System.out.println();
  }

  public void printDepthFirstRecursiveInOrder() {
    if (root != null)
      root.printDepthFirstRecursiveInOrder();
    System.out.println();
  }

  public void printDepthFirstIterativeInOrder() {
    if (root != null)
      root.printDepthFirstIterativeInOrder();
    System.out.println();
  }

  public void printDepthFirstRecursivePostOrder() {
    if (root != null)
      root.printDepthFirstRecursivePostOrder();
    System.out.println();
  }

  public void printDepthFirstIterativePostOrder() {
    if (root != null)
      root.printDepthFirstIterativePostOrder();
    System.out.println();
  }

  public void printBreadthFirstQueueLevelOrder() {
    if (root != null)
      root.printBreadthFirstQueueLevelOrder();
    System.out.println();
  }

  public void printBreadthFirstQueueLevelSpaced(int space) {
    if (root != null)
      root.printBreadthFirstQueueLevelSpaced(space);
    System.out.println();
  }

  public void printBreadthFirstQueueLevelSpacedHex(int space) {
    if (root != null)
      root.printBreadthFirstQueueLevelSpacedHex(space);
    System.out.println();
  }

  public void printBreadthFirstQueueLevelOrderDepth(int space) {
    if (root != null)
      root.printBreadthFirstQueueLevelSpaced(space);
    System.out.println();
  }

  /*******************************************************************
  public static void heapifyBinTree(BinLinkT root) {
    BinLinkT node = root;
    if (root.left != null && root.left.data > root.data) {
      node = root.left;
    }
    if (root.right != null && root.right.data > node.data) {
      node = root.right;
    }
    if (node != root) {
      int temp = root.data;
      root.data = node.data;
      node.data = temp;
      heapifyBinTree(node);
    }
  }

  public static BinTreeT<BinLinkT> growBreadthFirstQueueLevelOrder(
      int rootData, int count) {
    BinLinkT root = rootGrowBreadthFirstQueueLevelOrder(rootData, count);
    return new BinTreeT<BinLinkT>(root);
  }

  public static BinLinkT rootGrowBreadthFirstQueueLevelOrder(int rootData,
      int count) {
    if (count < 0)
      return null;
    BinLinkT root = new BinLinkT(rootData);
    LinkedList<BinLinkT> queue = new LinkedList<BinLinkT>();
    queue.add(root);
    // add every new node to queue, but break out of loop as soon as
    // count nodes have been added to the tree. So the queue gets bigger
    // than it needs to be, and is left for garbage collection.
    for (int j = 1; j < count; j++) {
      BinLinkT node = queue.remove();
      node.left = new BinLinkT(rootData + j);
      if (++j == count)
        break;
      queue.add(node.left);
      node.right = new BinLinkT(rootData + j);
      queue.add(node.right);
    }
    return root;
  }
***********************************************************/
  
  HashMap<Integer, ArrayList<int[]>> paths = new HashMap<Integer, ArrayList<int[]>>();

  public void storePathData(int sum, int path[], int beg, int end) {
    int subPath[] = new int[1 + end - beg];
    for (int k = 0, j = beg; j <= end; k++, j++)
      subPath[k] = path[j];
    if (!paths.containsKey(sum)) {
      paths.put(sum, new ArrayList<int[]>());
    }
    paths.get(sum).add(subPath);
  }

  static int[] copySubPath(int path[], int subLength) {
    int subPath[] = new int[subLength];
    for (int j = 0; j < subLength; j++) {
      subPath[j] = path[j];
    }
    return subPath;
  }

  static int[] copyReversedSubPath(int path[], int subLength) {
    int subPath[] = new int[subLength];
    for (int k = 0, j = subLength; --j >= 0; k++) {
      subPath[j] = path[k];
    }
    return subPath;
  }

  public void handlePathData(int sum, int path[], int beg, int end) {
    printPathData(sum, path, beg, end);
    storePathData(sum, path, beg, end);
  }

  public void findDescendingPathsToSum(BinLinkT node, int sum, int path[],
      int level) {
    if (node == null)
      return;

    if (level > path.length) {
      System.out
      .format("findDescendingPathsToSum: level > path.length (%d > %d)\n",
          level, path.length);
      return;
    }
    path[level] = node.data.toInt();
    int tmp = 0;
    for (int j = level; j >= 0; j--) {
      tmp += path[j];
      if (tmp == sum) {
        handlePathData(sum, path, j, level);
      }
    }
    findDescendingPathsToSum(node.left, sum, path, level + 1);
    findDescendingPathsToSum(node.right, sum, path, level + 1);
    // path[level] = Integer.MIN_VALUE; // Don't need to zap it; it won't be
    // read again
  }

  public static void findAllDescendingPaths(BinLinkT node, int level,
      int path[], HashMap<Integer, ArrayList<int[]>> pathsFound) {
    if (node == null)
      return;

    path[level] = node.data.toInt();
    int sum = 0;
    for (int j = level; j >= 0; j--) {
      sum += path[j];
    }
    if (!pathsFound.containsKey(sum)) {
      pathsFound.put(sum, new ArrayList<int[]>());
    }
    int subPath[] = copySubPath(path, level + 1);
    if (sDbg > 1)
      printPathData(sum, subPath, 0, level);
    pathsFound.get(sum).add(subPath);

    findAllDescendingPaths(node.left, level + 1, path, pathsFound);
    findAllDescendingPaths(node.right, level + 1, path, pathsFound);
  }

  public void findAllDascendingPathsToSum(BinLinkT node, int sum)
  {
    int depth = node.getDepth();
    int path[] = new int[depth];
    findDescendingPathsToSum(node, sum, path, 0);
  }

  public void findAllDescendingPathsToSum(int sum) {
    findAllDascendingPathsToSum(root, sum);
  }

  protected interface TreeNodeVisitor {
    abstract void visit(BinLinkT node);
  }

  /**************************************************************
  public class TreeSumPathFinder implements TreeNodeVisitor {
    int mSum;               // The path sum to be found.
    HashSet<int[]> mPaths;  // The collection of all paths for this sum.
    TreeSumPathFinder(int sum) {
      mSum = sum;
      mPaths = new HashSet<int[]>();
    }

    @Override
    public void visit(BinLinkT node) {
      findAllPathsToSum(node, mSum, mPaths);
    }
  }
***************************************************************/
  
  public static void saveLeftRightPathData(int sum, int nodeData, int sumLeft, int[] pathLeft, int sumRight, int[] pathRight, HashSet<int[]> savePaths)
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
    if (sDbg > 0) {
      System.out.format("sum %3d == %2d + %2d + %2d:", sum, sumLeft, nodeData, sumRight);
      assert(q == path.length);
      for (int j = 0; j < q; j++) {
        System.out.print(" " + path[j]);
      }
      System.out.println();
    }
  }


/**************************************************
  public static void findAllPathsToSum(BinLinkT node, int sum, int path[],	HashSet<int[]> savePaths, int level)
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

    findAllDescendingPaths(node.left, level, path, pathsLeft);
    findAllDescendingPaths(node.right, level, path, pathsRight);
    for (int sumLeft : pathsLeft.keySet()) {
      int sumRight = sum - node.data - sumLeft;
      if (pathsRight.containsKey(sumRight)) {
        for (int pathLeft[] : pathsLeft.get(sumLeft)) {
          for (int pathRight[] : pathsRight.get(sumRight)) {
            saveLeftRightPathData(sum, node.data, sumLeft, pathLeft, sumRight, pathRight, savePaths);

          }
        }
      }
    }
  }

  public static void findAllPathsToSum(BinLinkT node, int sum, HashSet<int[]> savePaths) {
    int depth = node.getDepth();
    int path[] = new int[depth];
    findAllPathsToSum(node, sum, path, savePaths, 0);
  }

  public void findAllPathsToSumBreadthFirstQueueLevelOrder(int sum) 
  {
    // Make a new path finder for each desired path sum.  
    // The collection of paths found for this sum is for the whole
    // tree, not any one node.
    BinLinkT.SumPathFinder pathFinder = root.new SumPathFinder(sum);
    root.visitBreadthFirstQueueLevelOrder(pathFinder);
    int numPaths = pathFinder.mPaths.size();
    if (numPaths > 0) {
      int maxLen = 0;
//      for (int path[] : pathFinder.mPaths) {  // TODO: Why does this get an error??????????????????????????????????????????????????????????
//        if (maxLen < path.length) {
//          maxLen = path.length;
//        }
//      }
      System.out.format("Found %d paths summing to %d   longest: %d\n"
          , numPaths, sum, maxLen);
    }
  }

  public void findAllPathsToSum(int sum) {
    findAllPathsToSumBreadthFirstQueueLevelOrder(sum);
  }

  public void findAllPathsToSums(int minSum, int maxSum) {
    for (int sum = minSum; sum < maxSum; sum++) {
      if (sDbg > 0)
        System.out.format("findAllPathsToSums(%2d):\n", sum);
      findAllPathsToSum(sum);
    }
  }

  public void test_findAllPathsToSum_old(int sum) 
  {
    HashSet<int[]> foundPaths = new HashSet<int[]>();
    findAllPathsToSum(root, sum, foundPaths);
    findAllPathsToSum(root.left, sum, foundPaths);
    findAllPathsToSum(root.right, sum, foundPaths);
    findAllPathsToSum(root.right.left, sum, foundPaths);
    findAllPathsToSum(root.right.right, sum, foundPaths);
    findAllPathsToSum(root.right.left.right, sum, foundPaths);
  }

  protected interface TreeNodePredicate<T extends BinLinkT> { // TODO: ??
    abstract boolean apply(T node);
  }

  class TreeVerifyDepth implements TreeNodePredicate<DeepBinLinkT> {
    public boolean apply(DeepBinLinkT node) {
      if (node != null) {
        // If this class were not declared static, and the node were not 
        // specified as in node.depth and node.computeDepth(), these 
        // fields belong to the root node, and this apply method would
        // always return true.
        if (sDbg > 0 && node.depth >= 3) {
          // polymorphic dispatch
          System.out.println("TreeVerifyDepth is applied to: " 
              + node.getClass().getCanonicalName());
        }
        if (node.depth != node.computeDepth()) { 
          return false;
        }
      }
      return true;
    }
  }
  
  // TODO: Remove.  It seems pointless to define testPredicate for
  // the tree instead of the nodes.  Even though the template parameter
  // type T allows for double dispatch via polymorphism -- that is,
  // if the predicate's apply method calls methods on node -- that
  // much is already accomplished by simply calling predicate.apply(node)
  // directly.
  boolean testPredicate(T node, TreeNodePredicate<T> predicate) {
    return predicate.apply(node);
  }  
     
  public boolean verifyTreeBreadthFirstQueueLevelOrder(T root, TreeNodePredicate<T> predicate) 
  {
    if (root != null) {
      LinkedList<T> queue = new LinkedList<T>();
      queue.add(root);
      do {
        T node = queue.remove();
        if ( ! predicate.apply(node)) {
          return false;
        }
        // Accomplishes nothing more than calling predicate.apply(node) directly.
        // if ( ! testPredicate(node, predicate)) {
        //   return false;
        // }     
        if (node.left != null) {
          queue.add((T) node.left);
        }
        if (node.right != null) {
          queue.add((T) node.right);
        }
      } while ( ! queue.isEmpty());
    }
    return true;
  }
  
  /**
   * unit_test
   *
  public static void unit_test() {

    int depth = 3;
    BinTreeT<BinLinkT> tree3 = null;

    System.out.print("\n    growGlobalCountingBinTreeRecurse:\n");
    tree3 = growGlobalCountingBinTree(3, 15);
    tree3.printBreadthFirstQueueLevelSpaced(16);
    System.out.print("printDepthFirstRecursivePreOrder  0:");
    tree3.printDepthFirstRecursivePreOrder();
    System.out.print("printDepthFirstIterativePreOrder  1:");
    tree3.printDepthFirstIterativePreOrder();
    System.out.print("printDepthFirstIterativePreOrder  2:");
    tree3.printDepthFirstIterativePreOrder();

    System.out.print("\n    growLocalCountingBinTreeRecurse:\n");
    tree3 = growLocalCountingBinTree(3, 15);
    tree3.printBreadthFirstQueueLevelSpaced(16);
    System.out.print("printDepthFirstRecursivePreOrder  0:");
    tree3.printDepthFirstRecursivePreOrder();
    System.out.print("printDepthFirstIterativePreOrder  1:");
    tree3.printDepthFirstIterativePreOrder();
    System.out.print("printDepthFirstIterativePreOrder  2:");
    tree3.printDepthFirstIterativePreOrder();

    int sum = 12;
    tree3.printBreadthFirstQueueLevelSpacedHex(16);
    tree3.findAllDescendingPathsToSum(sum);
    System.out.println("paths.size is " + tree3.paths.size());
    System.out.format("paths.get(%d).size() is %d\n", sum,
        tree3.paths.get(sum).size());

    //tree3.findAllPathsToSum(sum);
    tree3.findAllPathsToSums(0, 40);

    boolean bBst = tree3.root.verifyBstBreadthFirst();
    System.out.println("tree3.root.verifyBstBreadthFirst() is " + bBst);

    BinLinkT ll7  = new DeepBinLinkT(3, null, null,  1);
    BinLinkT lr7  = new DeepBinLinkT(5, null, null,  1);
    BinLinkT left7  = new DeepBinLinkT(4, ll7, lr7,  2);
    BinLinkT rl7  = new DeepBinLinkT(10, null, null, 1);
    BinLinkT rr7  = new DeepBinLinkT(12, null, null, 2);  // TODO: intentional error?
    BinLinkT right7 = new DeepBinLinkT(10, rl7, rr7, 2);
    DeepBinLinkT root7  = new DeepBinLinkT(7, left7, right7, 3);
    bBst = root7.verifyBstBreadthFirst();
    BinTreeT<DeepBinLinkT> tree7 = new BinTreeT<DeepBinLinkT>(root7); 
    System.out.println("root7.verifyBstBreadthFirst() is " + bBst);
    
    // Verify each node's sub-tree depth
    boolean bDepth = false;
    bDepth = tree7.verifyTreeBreadthFirstQueueLevelOrder(root7, tree7.new TreeVerifyDepth());
    System.out.println("root7.verifyTreeBreadthFirstQueueLevelOrder() is " + bDepth);

    if (depth > 3) {

      System.out.print("printDepthFirstRecursiveInOrder   0:");
      tree3.printDepthFirstRecursiveInOrder();
      System.out.print("printDepthFirstIterativeInOrder   1:");
      tree3.printDepthFirstIterativeInOrder();
      System.out.print("printDepthFirstIterativeInOrder   2:");
      tree3.printDepthFirstIterativeInOrder();

      System.out.print("printDepthFirstRecursivePostOrder 0:");
      tree3.printDepthFirstRecursivePostOrder();
      System.out.print("printDepthFirstIterativePostOrder 1:");
      tree3.printDepthFirstIterativePostOrder();
      System.out.print("printDepthFirstIterativePostOrder 2:");
      tree3.printDepthFirstIterativePostOrder();

      System.out.print("\n    growBreadthFirstQueueLevelOrder:\n");
      tree3 = growBreadthFirstQueueLevelOrder(1, 15);

      System.out.print("printBreadthFirstQueueLevelOrder:  ");
      tree3.printBreadthFirstQueueLevelOrder();
      System.out.print("printBreadthFirstQueueLevelSpaced: ");
      tree3.printBreadthFirstQueueLevelSpaced(16);

      System.out.print("printDepthFirstRecursivePreOrder:  ");
      tree3.printDepthFirstRecursivePreOrder();
      System.out.print("printDepthFirstIterativePreOrder:  ");
      tree3.printDepthFirstIterativePreOrder();

      System.out.print("printDepthFirstRecursiveInOrder:   ");
      tree3.printDepthFirstRecursiveInOrder();
      System.out.print("printDepthFirstIterativeInOrder:   ");
      tree3.printDepthFirstIterativeInOrder();

      System.out.print("printDepthFirstRecursivePostOrder: ");
      tree3.printDepthFirstRecursivePostOrder();
      System.out.print("printDepthFirstIterativePostOrder: ");
      tree3.printDepthFirstIterativePostOrder();

      System.out.print("printBreadthFirstQueueLevelOrder:  ");
      tree3.printBreadthFirstQueueLevelOrder();

      System.out.print("printBreadthFirstQueueLevelSpacedPowerOf2: ");
      tree3.root.printBreadthFirstQueueLevelSpacedPowerOf2(16);

      System.out.print("printBreadthFirstQueueLevelSpaced: ");
      tree3.printBreadthFirstQueueLevelSpaced(16);

      System.out.print("printBreadthFirstQueueLevelOrderDepth: ");
      tree3.printBreadthFirstQueueLevelOrderDepth(24);

    }
************************************************************************
    // HashMap<Integer, Integer> sums =
    // tree3.root.pathSumsBreadthFirstQueueLevelOrder();

    /*
     * // Make a random binary tree with N = 2^M nodes: int N = 2*2*2;
     * Random rng = new Random(N); // i.e., java.util.Random.
     * 
     * depth = 4; BinLinkT root4 = growGlobalCountingBinTreeRecurse(depth,
     * 0); BinTreeT tree4 = new BinTreeT(root4);
     * System.out.print("printBreadthFirstQueueLevelOrder: ");
     * tree4.printBreadthFirstQueueLevelOrder();
     * System.out.print("printBreadthFirstQueueLevelSpaced: ");
     * tree4.printBreadthFirstQueueLevelSpaced(16);
     * 
     * root4 = growRandomBinTreeRecurse(4, rng, 32); tree4 = new
     * BinTreeT(root4);
     * System.out.print("printTreeDepthFirstRecursivePreOrder: ");
     * BinLinkT.printTreeDepthFirstRecursivePreOrder(root4);
     * System.out.println();
     * 
     * /* heapifyBinTree(root);
     * BinLinkT.printTreeRecurseDepthFirstPreOrder(root);
     * 
     * System.out.println(); int d = tree4.getDepth(); int n =
     * tree4.getNumNodes(); System.out.println("getDepth(root) got " + d +
     * " and getNumNodes() got " + n);
     *
  }
******************************************************************************/
  
  public static void main(String[] args) {
    //unit_test();
  }
}
