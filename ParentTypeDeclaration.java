class ParentTypeDeclaration extends SimpleNode
{
	ParentTypeDeclaration(int i)
	{
		super(i);
	}
	ParentTypeDeclaration(UCSY p, int i)
	{
		super(p,i);
	}
	public Object jjtAccept(UCSYVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}