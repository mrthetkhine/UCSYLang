import java.util.*;
abstract class Method
{
	static String[] emptyParameter = {};
	String ownerName;
	int modifier;
	String methodName;
	String methodSignature;
	String methodReturnType;
	String methodProtocol;
	String[] parameters;
	Code methodCode = new Code();
	
	int sizeOfLocalVar = 0;
	int sizeOfArgument = 0; 
	
	//For Exception 
	ArrayList<ExceptionTable> exceptionTable = new ArrayList<ExceptionTable>();
	Method()
	{
	}
	Method(int modifier,String methodName, String methodSignature,String methodReturnType)
	{
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.methodReturnType = methodReturnType;
		///Debug.inform("Insert method "+ methodReturnType);
		this.modifier = modifier;
		this.methodProtocol = this.methodSignature + this.methodReturnType;
		splitParameter();
		
	}
	void splitParameter()
	{
		if( parameters == null)
		{
			String par = methodSignature.substring(1,methodSignature.length()-1);
		
		
			if(par.equals(""))
			{
				this.parameters = emptyParameter;
			}
			else
			{
				this.parameters = par.split(",");	
			}
		}
		
	}
	boolean isSameOfNameAndSignature(Method m)
	{
		return this.methodName.equals(m.methodName) && this.methodSignature.equals(m.methodSignature);
	}
	boolean isAbstract()
	{
		return (modifier & UCSYClassAttribute.ABSTRACT) == UCSYClassAttribute.ABSTRACT;
	}
	boolean isOverride()
	{
		return ( modifier & UCSYClassAttribute.OVERRIDE ) == UCSYClassAttribute.OVERRIDE;
	}
	boolean isStatic()
	{
		return (modifier & UCSYClassAttribute.STATIC) == UCSYClassAttribute.STATIC;
	}
	boolean isRebindable()
	{
		return ( modifier & UCSYClassAttribute.REBINDABLE ) == UCSYClassAttribute.REBINDABLE;
	}
	boolean isPrivate()
	{
		return ( modifier & UCSYClassAttribute.PRIVATE ) == UCSYClassAttribute.PRIVATE;
	}
	boolean isProtected()
	{
		return ( modifier & UCSYClassAttribute.PROTECTED ) == UCSYClassAttribute.PROTECTED;
	}
	boolean isInternal()
	{
		return ( modifier & UCSYClassAttribute.INTERNAL ) == UCSYClassAttribute.INTERNAL;
	}
	boolean isPublic()
	{
		return ( modifier & UCSYClassAttribute.PUBLIC ) == UCSYClassAttribute.PUBLIC;
	}
	
	boolean isFinal()
	{
		return (modifier & UCSYClassAttribute.FINAL ) == UCSYClassAttribute.FINAL;
	}
	boolean isNative()
	{
		return ( modifier & UCSYClassAttribute.NATIVE ) == UCSYClassAttribute.NATIVE;
	}
	
	int getNoOfParameter()
	{
		splitParameter();	
		return this.parameters.length;
	}
}
