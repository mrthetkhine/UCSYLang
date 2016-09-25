import java.util.*;

class FunctorType extends Type
{
	int modifier ;
	String functorName;
	String functorSignature;
	String functorReturnType;
	FunctorType()
	{
		itsType = TypeOfType.FUNCTOR;
		this.typeNameDescription = "functor";
	}
	public TypeOfType getType()
	{
		return itsType;
	}
	public ArrayList<UCSYMember> getAllMemberFromHierarchy()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException
	{
		ArrayList<UCSYMember> allMember = new ArrayList<UCSYMember>();
		return allMember;
	}
	public boolean isChildOf(String parentName)
	{
		return false;
	}
	/***************************************************************************
	 * currently ignored, to be added later
	 ***************************************************************************/
	public boolean assignmentCompatiable(String typeName)
	{
		return false;
	}
	boolean isInstanceOf(String typeName)
	{
		return false;
	}
	public Method getBestOverloadedMethod(String contextClass,String methodName,String methodSignature)
	throws UnambiguousResolutionException, CannotFindBestOverloadedMethodException
	{
		Method m = null;
		return m;
	}
	public boolean isAncestorOf(String typeName)
	{
		return true;
	}
	/***************************************************************************
	 * Code Generation
	 ***************************************************************************/
	 public void generateUCodeFile()
	 {
	 }
}
