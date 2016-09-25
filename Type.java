import java.util.*;
enum TypeOfType
{
	METATYPE,
	CLASSTYPE,
	INTERFACETYPE,
	FUNCTOR,
	FREECLASSTYPE,
	ARRAY,
	BOOLEAN,
	CHAR,
	BYTE,
	SHORT,
	INTEGER,
	LONG,
	FLOAT,
	DOUBLE,
	STRING
	
}
abstract class Type implements MemberAccess
{
	String typeName;
	TypeOfType itsType;
	abstract TypeOfType getType();
	abstract boolean isAncestorOf(String typeName);
	
	UCodeFile uCodeFile = new UCodeFile();
	String typeNameDescription; //class, interface, meta class, integer, boolean etc
	
	abstract boolean assignmentCompatiable(String typeName);

	abstract boolean isInstanceOf(String typeName);	
	abstract Method getBestOverloadedMethod(String contextTypeName,String methodName,String signature)
	throws UnambiguousResolutionException, CannotFindBestOverloadedMethodException;
	
	abstract void generateUCodeFile();
	boolean isInteface()
	{
		return itsType == TypeOfType.INTERFACETYPE;
	}
	boolean isClass()
	{
		return itsType == TypeOfType.CLASSTYPE;
	}
	boolean isArray()
	{
		return itsType == TypeOfType.ARRAY;
	}
	String emitCodeForMemberField(String memberName)
	{
		String typeName="";
		return typeName;
	}
	String emitCodeForMemberMethod(Method m)
	{
		String typeName ="";
		return typeName;
	}

	ConstantPool constantPool ;
	
}
//To be used by TypeResolver class
interface MemberAccess
{
	ArrayList<UCSYMember> getAllMemberFromHierarchy()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException;
	
	boolean isChildOf(String parentName);
	
	
}
class PrimitiveType extends Type
{
	
	
	PrimitiveType(TypeOfType type)
	{
		itsType = type;
		this.typeNameDescription = type.toString();
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
	public boolean assignmentCompatiable(String typeName)
	{
		//retrun false;
		//This method will not be called 
		return false;
	}
	public Method getBestOverloadedMethod(String contextTypeName,String methodName,String methodSignature)
	{
		return null;
	}
	public void generateUCodeFile()
	{
	}
	public boolean isAncestorOf(String typeName)
	{
		Debug.inform("Ivalide for PrimitiveType isAncestorOf");
		return false;
	}
	boolean isInstanceOf(String typeName)	
	{
		Debug.inform("Ivalide for PrimitiveType isAncestorOf");
		return false;
	}
}

