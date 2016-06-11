package sprax.trees;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import sprax.sprout.Spaces;

public class BinLinkString 
{    
    String  data    = null;
    BinLinkString left    = null;
    BinLinkString right   = null;
    boolean visited = false;
    
    BinLinkString left()  { return left;  } 
    BinLinkString right() { return right; }
    
    //public BinLinkString() {}
    
    public BinLinkString(String str, BinLinkString nxt, BinLinkString prv) {
        data  = str;
        right = nxt;
        left  = prv;
    }
    
    public BinLinkString(int c, BinLinkString nxt, BinLinkString prv) {
        left  = prv;
        right = nxt;
        if (c < 'A' || c > 'Z') {
            c = 'A' + c % 26;
        }
        data = Character.toString((char) c);
    }
    
    public int getNumNodes() {
        return 1 + getNumDescendents();
    }
    
    public int getNumDescendents() {
        return (left  == null ? 0 : 1 +  left.getNumDescendents())
        + (right == null ? 0 : 1 + right.getNumDescendents());
    }
    
    public static int getNumDescendentsStatic(BinLinkString b) {
        if (b == null) 
            return 0;
        return 1 + getNumDescendentsStatic(b.left) + getNumDescendentsStatic(b.right);
    }
    
    protected static int max(int a, int b) {
        return a > b ? a : b;
    }
    
    public int getDepth() {
        return max( (left  == null ? 0 : 1 +  left.getDepth())
                , (right == null ? 0 : 1 + right.getDepth()));
    }
    
    public static int getDepthStatic(BinLinkString b) {
        if (b == null || (b.left == null && b.right == null)) {
            return 0;
        }
        if (b.left == null) {
            return 1 + getDepthStatic(b.right);
        } else if (b.right == null) {
            return 1 + getDepthStatic(b.left);
        } else {
            return 1 + max( getDepthStatic(b.left), getDepthStatic(b.right) );
        }    
    }
    
    static int mCount = 0;
    static int setCount(int num) { return mCount = num; }
    static int getCount()        { return mCount; }
    static int addCount(int num) { return mCount += num; }
    
    
    
    public static BinLinkString growBreadthFirstQueueLevelOrder(int depth, int count) {
        LinkedList<BinLinkString> queue = new LinkedList<BinLinkString>();
        BinLinkString   root = new BinLinkString(getCount(), null, null);
        queue.add(root);
        int last = count - 1;
        if (last < 0)
            last = 0;
        while ( ! queue.isEmpty() && getCount() < count) {
            BinLinkString node = queue.remove();
            node.left  = new BinLinkString(addCount(1), null, null);
            if (getCount() >= last)
                break;
            node.right = new BinLinkString(addCount(1), null, null);
            if (getCount() >= last)
                break;
            queue.add(node.left);
            queue.add(node.right);
        }
        return root;
    }   
    
    public static BinLinkString growGlobalCountingBinTreeRecurse(int depth, int n) {
        BinLinkString root = new BinLinkString(getCount(), null, null);
        if (depth-- > 0) {
            root.left  = growGlobalCountingBinTreeRecurse(depth, addCount(1));
            root.right = growGlobalCountingBinTreeRecurse(depth, addCount(1));
        }   // otherwise, b.left = b.right = null (by construction)
        return root;
    }
    
    public static BinLinkString growRandomBinTreeRecurse(int depth, Random rng, int n) {
        BinLinkString b = new BinLinkString(rng.nextInt(n), null, null);
        if (depth-- > 0) {
            b.left  = growRandomBinTreeRecurse(depth, rng, n);
            b.right = growRandomBinTreeRecurse(depth, rng, n);
        }   // otherwise, b.left = b.right = null (by construction)
        return b;
    }    
    
    
    public static BinLinkString initAlphabet(int length) {
        assert(length > 0);
        length--;
        BinLinkString head = new BinLinkString('A' + length, null, null);
        while(--length >= 0) {
            BinLinkString temp = new BinLinkString('A' + length, head, null);
            head.left = temp;
            head = temp;
        }
        head.left = null;
        return head;
    }
    
    public static void printList(BinLinkString head) {
        while (head != null) {
            System.out.print(head.data + " ");
            head = head.right;
        }
        System.out.println();
    }
    
    public static void printTreeDepthFirstRecursivePreOrder(BinLinkString head) {
        if (head != null) {
            System.out.print(head.data + " ");
            printTreeDepthFirstRecursivePreOrder(head.left);
            printTreeDepthFirstRecursivePreOrder(head.right);
        }
    }
    
    public void printDepthFirstRecursivePreOrder() {
        System.out.print(data + " ");
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
        System.out.print(data + " ");
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
        System.out.print(data + " ");
    }
    
    public void printDepthFirstIterativePreOrder() {
        Stack<BinLinkString> nodeStack = new Stack<BinLinkString>();
        nodeStack.push(this);
        while (! nodeStack.isEmpty()){
            BinLinkString node = nodeStack.pop();
            System.out.print(node.data + " ");
            if (node.right() != null) {
                nodeStack.push(node.right());
            }
            if (node.left() != null) {
                nodeStack.push(node.left());
            }
        }
    } 
    
    public void printDepthFirstIterativeInOrder() {
        boolean initState = this.visited;
        Stack<BinLinkString> nodeStack = new Stack<BinLinkString>();
        nodeStack.push(this);
        while (! nodeStack.empty()) {
            BinLinkString node = nodeStack.peek();
            if ((node.left != null) && (node.left.visited == initState)) {
                nodeStack.push(node.left);
            } else {
                System.out.print(node.data + " ");
                node.visited = ! initState;
                nodeStack.pop();
                if ((node.right != null) && (node.right.visited == initState)) {
                    nodeStack.push(node.right);
                }
            }
        }
    }
    
    public void printDepthFirstIterativePostOrder() {
        boolean initState = this.visited;
        Stack<BinLinkString> nodeStack = new Stack<BinLinkString>();
        nodeStack.push(this);
        while (! nodeStack.empty()) {
            BinLinkString node = nodeStack.peek();
            if ((node.left != null) && (node.left.visited == initState)) {
                nodeStack.push(node.left);
            } else {
                if ((node.right != null) && (node.right.visited == initState)) {
                    nodeStack.push(node.right);
                } else {
                    System.out.print(node.data + " ");
                    node.visited = ! initState;
                    nodeStack.pop();
                }
            }
        }
    }
        
    public void printBreadthFirstQueueLevelOrder() {
        LinkedList<BinLinkString> queue = new LinkedList<BinLinkString>();
        queue.add(this);
        do {
            BinLinkString node = queue.remove();
            System.out.print(node.data + " ");
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        } while ( ! queue.isEmpty());
    }
    
    public void printBreadthFirstQueueLevelSpaced(int depth) {
        LinkedList<BinLinkString> queue = new LinkedList<BinLinkString>();
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
            BinLinkString node = queue.remove();
            System.out.print(node.data + " ");
            if (++counter == nextPowerOf2) {
                counter = 0;
                depth = max(0, depth - nextPowerOf2);
                nextPowerOf2 *= 2;
                System.out.format("\n%s", Spaces.get(depth));
            }
            if (node.left != null) {
                queue.add(node.left);
            }
            if (node.right != null) {
                queue.add(node.right);
            }
        } while ( ! queue.isEmpty());
    }
    
    
    public static void shuffle (int[] array)
    {
        Random rng = new Random();   // i.e., java.util.Random.
        int n = array.length;        // The number of items left to shuffle (loop invariant).
        while (n > 1)
        {
            int k = rng.nextInt(n);  // 0 <= k < n.
            n--;                     // n is now the last pertinent index;
            int temp = array[n];     // swap array[n] with array[k] (does nothing if k == n).
            array[n] = array[k];
            array[k] = temp;
        }
    }
    
    
    /** generic?
	public static T[] RandomPermutation<T>(T[] array)
	{
	    T[] retArray = new T[array.Length];
	    array.CopyTo(retArray, 0);

	    Random random = new Random();
	    for (int i = 0; i < array.Length; i += 1)
	    {
	        int swapIndex = random.right(i, array.Length);
	        if (swapIndex != i)
	        {
	            T temp = retArray[i];
	            retArray[i] = retArray[swapIndex];
	            retArray[swapIndex] = temp;
	        }
	    }

	    return retArray;
	}
     */
    
    
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
    }
}
