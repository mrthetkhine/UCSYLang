class UCSYField
{
	int modifier;
	String fieldName;
	String fieldType;
	
	UCSYField()
	{
	}
	UCSYField(int modifier, String fieldName, String fieldType)
	{
		this.modifier = modifier;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}
	boolean isPublic()
	{
		return (modifier & UCSYClassAttribute.PUBLIC) == UCSYClassAttribute.PUBLIC;
	}
	boolean isPrivate()
	{
		return (modifier & UCSYClassAttribute.PRIVATE) == UCSYClassAttribute.PRIVATE;
	}
	boolean isProtected()
	{
		return (modifier & UCSYClassAttribute.PROTECTED) == UCSYClassAttribute.PROTECTED;
	}
	boolean isStatic()
	{
		return (modifier & UCSYClassAttribute.STATIC ) == UCSYClassAttribute.STATIC;
	}
}
