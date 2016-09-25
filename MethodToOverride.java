class MethodToOverride extends Method
{
	String parentType;
	MethodToOverride(String parentType, Method m)
	{
		this.methodName = m.methodName;
		this.methodSignature = m.methodSignature;
		this.methodReturnType = m.methodReturnType;
		this.modifier = m.modifier;
		this.parentType = parentType;
	}
}
