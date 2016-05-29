package sprax.trees;

class DeepBinLink extends BinLink 
{
  protected int depth;  // TODO: loosen this
  
  public DeepBinLink(BinLink node, int depth) {
    super(node.mKey, node.mLeft, node.mRight);
    this.depth = depth;
  }
  
  public DeepBinLink(int data, int depth) {
    super(data);
    this.depth = depth;
  }
  
  public DeepBinLink(int data, int depth, BinLink left, BinLink right) {
    super(data, left, right);
    this.depth = depth;
  }
  
  @Override
  public int getDepth() {     // return stored depth instead of computing it.
    return depth;
  }
  public int computeDepth() { // return newly computed, not stored, depth. 
    return super.getDepth(); 
  }

  public interface DeepNodeVisitor<T extends BinLink> {
    abstract void visit(T node, int param);
  }
  
//  protected interface NodePredicate<T extends DeepBinLink> {
//    abstract boolean apply(DeepBinLink node);
//  }

  static class VerifyDepth implements NodePredicate<DeepBinLink> {
    public boolean apply(DeepBinLink node) {
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
//  public boolean verifySubTreeDepthBreadthFirst(DeepBinLink subTree) {
//    return BinTree.verifyTreeBreadthFirstQueueLevelOrder(subTree, new BinTree.TreeVerifyDepth());
//  }

  public static void main(String[] args) {
    System.out.println("BinLink test!");
    BinTree.unit_test();
  }
}
