/* Generated By:JJTree: Do not edit this line. ASTPrimaryExpression.java */

import java.util.*;

public class ASTPrimaryExpression extends ParentExpression {
	ParentName primaryExpression;
	
	
	//********* TO USED in Code Generation *********************************
	ArrayList<ParentName> allPrimaryExpression = new ArrayList<ParentName>();
  public ASTPrimaryExpression(int id) {
    super(id);
  }

  public ASTPrimaryExpression(UCSY p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
