/* Generated By:JJTree: Do not edit this line. ASTAllocationExpression.java */

public class ASTAllocationExpression extends ParentExpression {
	ParentExpression allocationNode;
  public ASTAllocationExpression(int id) {
    super(id);
  }

  public ASTAllocationExpression(UCSY p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}