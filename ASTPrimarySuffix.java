/* Generated By:JJTree: Do not edit this line. ASTPrimarySuffix.java */

import java.util.*;

public class ASTPrimarySuffix extends ParentName {
	ParentName firstPart;
	ParentName secondPart;
	
	String frontName;
	String endName;
	
	//ArrayList<ParentName> allPrimaryExpression = new ArrayList<ParentName>();
  public ASTPrimarySuffix(int id) {
    super(id);
  }

  public ASTPrimarySuffix(UCSY p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
