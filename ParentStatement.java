class ParentStatement extends SimpleNode
{
	ParentStatement(int i)
	{
		super(i);
	}
	ParentStatement(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}