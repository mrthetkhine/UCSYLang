/* Generated By:JJTree: Do not edit this line. ASTAllocationPrimaryPrefix.java */

public class ASTAllocationPrimaryPrefix extends ParentName {
	ASTAllocationExpression allocationExpression;
  public ASTAllocationPrimaryPrefix(int id) {
    super(id);
  }

  public ASTAllocationPrimaryPrefix(UCSY p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
