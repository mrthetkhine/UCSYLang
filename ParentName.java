import java.util.*;

class ParentName extends ParentExpression
{
	ArrayList<String> nameList = new ArrayList<String>();	
	ArrayList<String> codeGenNameList = new ArrayList<String>();
	ParentName(int i)
	{
		super(i);
	}
	
	ParentName(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}