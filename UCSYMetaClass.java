import java.util.*;

class UCSYMetaClass extends Type implements MemberAccess
{
	int modifier ;
	String metaClassName;
	String parentClassName;
	String defaultClassName;
	ArrayList<InterfaceMethod> methodList = new ArrayList<InterfaceMethod>();
	UCSYMetaClass()
	{
		this.itsType = TypeOfType.METATYPE;
		this.typeNameDescription = "meta class";
	}
	public TypeOfType getType()
	{
		return itsType;
	}
	void addInterfaceMethod(InterfaceMethod m)
	{
		methodList.add( m );
	}
	ArrayList<String> getSignaturesOfTheMethod(String methodName)
	{
		ArrayList<String> l = new ArrayList<String>();
		
		for (int i = 0; i< methodList.size(); i++)
		{
			InterfaceMethod met = methodList.get(i);
			if(met.methodName.equals(methodName))
			{
				l.add(met.methodSignature);
			}
		}
		return l;
	}
	ArrayList<UCSYMetaClass> getAllParent()throws TypeNotFoundException,NotExceptedTypeException
	{
		ArrayList<UCSYMetaClass> allParent = new ArrayList<UCSYMetaClass>();
		
		String parentName = this.parentClassName;
		while(! parentName.equals("Object"))
		{
			
			UCSYMetaClass parentClass =  CentralTypeTable.getCentralTypeTable().getAMetaClass( parentName);
			allParent.add(parentClass);
			parentName = parentClass.parentClassName;
			
		}
		return allParent;
	}
	
	/***************************************************************************
	 Return all method of this meta class, and all method of its parent class 
	 hierarchy
	 ***************************************************************************/
	ArrayList<Method> getAllMethodsAccessibleByMetaClass()throws TypeNotFoundException,NotExceptedTypeException
	{
		ArrayList<Method> allMethods = new ArrayList<Method>();
		
		for (int i = 0; i< methodList.size(); i++)
		{
			allMethods.add(methodList.get( i ));
		}
		ArrayList <UCSYMetaClass> allParent = getAllParent();
		for (int i = 0; i< allParent.size(); i++)
		{
			UCSYMetaClass parentClass = allParent.get(i);
			for (int j = 0; j< parentClass.methodList.size(); j++)
			{
				Method parentMethod = parentClass.methodList.get(i);
				
				if( ! TypeCheckUtilityClass.isMethodIntheMethodList( allMethods, parentMethod) )
					allMethods.add( parentMethod );
			}
		}
		return allMethods;
	}
	Method getMethodOfNameAndSignature(String methodName, String methodSignature)throws TypeNotFoundException, NotExceptedTypeException
	{	
		Method m = null;
		ArrayList<Method> allMethod = this.getAllMethodsAccessibleByMetaClass();
		for (int i = 0; i< allMethod.size(); i++)
		{
			Method met = allMethod.get(i);
			
			if( met.methodName.equals( methodName) &&
				met.methodSignature.equals( methodSignature))
				return met;
		}
		return m;
	}
	ArrayList<Method> getAllPublicMethod()
	{
		ArrayList<Method> allPublicMethod = new ArrayList<Method>();
		
		for (int i = 0; i< methodList.size(); i++)
		{
			allPublicMethod.add( methodList.get(i) );
		}
		return allPublicMethod;
	}
	
	public ArrayList<UCSYMember> getAllMemberFromHierarchy()throws NotExceptedTypeException, TypeNotFoundException,CyclicInheritanceException
	{
		ArrayList<UCSYMember> allMember = new ArrayList<UCSYMember>();
		ArrayList<Method> allAccessibleMethod = this.getAllMethodsAccessibleByMetaClass();
		
		for (int i = 0; i<allAccessibleMethod.size(); i++)
		{
			Method m = allAccessibleMethod.get(i);
			allMember.add(new UCSYMember(this.metaClassName,m.modifier,m.methodName,m.methodReturnType,MemberType.METHOD_OF_METACLASS,m));
		}
		return allMember;
	}
	public boolean isChildOf(String parentName)
	{
		return false;
	}
	/***************************************************************************
	 * current ignored , to be added later
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
	/***************************************************************************
	 * Code Generation
	 ***************************************************************************/
	 public void generateUCodeFile()
	 {
	 }
	 public boolean isAncestorOf(String typeName)
	 {
	 	return true;
	 }
}