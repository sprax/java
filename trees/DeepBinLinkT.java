package sprax.trees;

class DeepBinLinkT<T> extends BinLinkT
{
    protected int depth;  // TODO: loosen this
                         
    public DeepBinLinkT(BinLinkT<T> node, int depth) {
        super(node.data, node.left, node.right);
        this.depth = depth;
    }
    
    // FIXME generics
    @Override
    public BinLinkT createNode(ToInt data) {
        // TODO Auto-generated method stub
        return null;
    }
    
    /*********************************************************
     * public DeepBinLinkT(int data, BinLinkT left, BinLinkT right, int depth) {
     * super(data, left, right);
     * this.depth = depth;
     * }
     ********************************************************/
    
    @Override
    public int getDepth() {     // return stored depth instead of computing it.
        return depth;
    }
    
    public int computeDepth() { // return newly computed, not stored, depth. 
        return super.getDepth();
    }
    
    public interface DeepNodeVisitor<T extends BinLinkT<?>>
    {
        abstract void visit(T node, int param);
    }
    
    //  protected interface NodePredicate<T extends DeepBinLinkT> {
    //    abstract boolean apply(DeepBinLinkT node);
    //  }
    
    static class VerifyDepth implements NodePredicate<DeepBinLinkT<?>>
    {
        public boolean apply(DeepBinLinkT<?> node) {
            if (node != null) {
                // If this class were not declared static, and the node were not 
                // specified as in node.depth and node.computeDepth(), these 
                // fields belong to the root node, and this apply method would
                // always return true.
                if (node.depth != node.computeDepth()) {
                    return false;
                }
            }
            return true;
        }
    }
    
    // TODO: what should this be?
    //  public boolean verifySubTreeDepthBreadthFirst(DeepBinLinkT subTree) {
    //    return BinTree.verifyTreeBreadthFirstQueueLevelOrder(subTree, new BinTree.TreeVerifyDepth());
    //  }
    
    public static void main(String[] args) {
        System.out.println("BinLinkT test!");
        BinTree.unit_test();
    }
    
}
