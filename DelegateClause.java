class DelegateClause extends SimpleNode
{
	String delegateName;
	String delegateTypeName;
	UCSYField delegateField;
	
	DelegateClause()
	{
	}
	DelegateClause(int i)
	{
		super(i);
	}
	DelegateClause(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}