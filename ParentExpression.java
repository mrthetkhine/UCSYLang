class ParentExpression extends SimpleNode
{
	String typeName="fake";
	ParentExpression(int i)
	{
		super(i);
	}
	
	ParentExpression(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}