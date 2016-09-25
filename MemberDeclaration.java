class MemberDeclaration extends SimpleNode
{
	int memberModifier;
	MemberDeclaration(int i)
	{
		super(i);
	}
	MemberDeclaration(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
