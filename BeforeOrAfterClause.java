class BeforeOrAfterClause extends SimpleNode
{
	DelegateClause delegateClause;
	BeforeOrAfterClause(int i)
	{
		super(i);
	}
	BeforeOrAfterClause(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}