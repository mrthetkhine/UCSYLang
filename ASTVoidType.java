/* Generated By:JJTree: Do not edit this line. ASTVoidType.java */

public class ASTVoidType extends SimpleNode {
	String typeName;
  public ASTVoidType(int id) {
    super(id);
  }

  public ASTVoidType(UCSY p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
