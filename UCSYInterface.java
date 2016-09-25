import java.util.*;

class UCSYInterface extends Type implements MemberAccess
{
	int modifier;
	String interfaceName;
	ArrayList<String> parentList = new ArrayList<String>();
	ArrayList<InterfaceMethod> methodList = new ArrayList<InterfaceMethod>();
	
	UCSYInterface()
	{
		this.itsType = TypeOfType.INTERFACETYPE;
		this.typeNameDescription = "interface";
	}
	public TypeOfType getType()
	{
		return itsType;
	}
	
	void addInterfaceMethod(InterfaceMethod m)
	{
		methodList.add( m );
	}
	void addParent(String p)
	{
		parentList.add( p );
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
	//Return all ancestors of the interface
	
	ArrayList<UCSYInterface> getAllParentInterface(String currentInterfaceName)throws TypeNotFoundException, NotExceptedTypeException,CyclicInheritanceException
	{
		ArrayList<UCSYInterface> allParent = new ArrayList<UCSYInterface>();
		ArrayList<UCSYInterface> retParent = new ArrayList<UCSYInterface>();
		for (int i = 0; i< this.parentList.size(); i++)
		{
			String parentInterfaceName = this.parentList.get(i);
			
			if(parentInterfaceName.equals( currentInterfaceName ))
			{
				throw new CyclicInheritanceException(currentInterfaceName);
			}
			UCSYInterface parentInterface = CentralTypeTable.getCentralTypeTable().getAInterface(parentInterfaceName);
			
			
			if(! isAlreadyInParentList( allParent, parentInterface))
			{
				allParent.add( parentInterface );
			}
			
			retParent = parentInterface.getAllParentInterface( currentInterfaceName );
			
			for (int j = 0; j< retParent.size(); j++)
			{
				allParent.add(retParent.get(j));
			}
					
		}
		return allParent;
	}
	
	/***************************************************************************
	 * return all parent hierary of interface, current interface is not included
	 ****************************************************************************/
	ArrayList<UCSYInterface> getAllAncestorInterfaces()throws TypeNotFoundException, NotExceptedTypeException,Exception
	{
		ArrayList<UCSYInterface> allInterface  ;
		
		allInterface = this.getAllParentInterface( this.interfaceName);
		
		return allInterface;
	}
	
	boolean isAlreadyInParentList(ArrayList<UCSYInterface> allParent,UCSYInterface myInterface)
	{
		for (int i = 0; i< allParent.size(); i++)	
		{
				UCSYInterface parent = allParent.get(i);
				if(parent.interfaceName.equals( myInterface.interfaceName ))
				{
					return true;
				}
		}
		return false;
	}
	
	//Return a method matching methodname and signature
	//search whole interface hierarchy
	
	Method getMethodOfNameAndSignature(String methodName,String methodSignature)throws NotExceptedTypeException, TypeNotFoundException,CyclicInheritanceException
	{
		Method m = null;
		
		ArrayList<Method> allMethod = getAllMethodsAccessibleByInterface();
		
		for (int i = 0; i<allMethod.size(); i++)
		{
			Method met = allMethod.get(i);
			
			if( met.methodName.equals( methodName )
				&& met.methodSignature.equals( methodSignature ) )
			{
				return met;
			}
		}
		return null;
	}
	
	ArrayList<Method> getAllPublicMethod()
	{
		ArrayList<Method> allPublicMethod = new ArrayList<Method>();
		
		for (int i = 0; i< methodList.size(); i++)
		{
			Method m = methodList.get(i);
			m.ownerName = this.interfaceName;
			allPublicMethod.add( m );
		}
		return allPublicMethod;
	}
	/***************************************************************************
	 Return methods of current interface +
	 all methods of all of its ancestor interface 
	 **************************************************************************/
	
	ArrayList<Method> getAllMethodsAccessibleByInterface()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException
	{
		
		ArrayList<Method> allMethod = new ArrayList<Method>();
		
		//Add all methods of current interface
		for (int i = 0; i< methodList.size(); i++)
		{
			allMethod.add(methodList.get(i));
		}
		
		//Find all methods of its interface
		ArrayList<UCSYInterface> allParentInterface = this.getAllParentInterface( this.interfaceName );
		
		
		for (int i = 0; i< allParentInterface.size(); i++)	
		{
			UCSYInterface parentInterface = allParentInterface.get(i);
			
			for (int j = 0; j< parentInterface.methodList.size(); j++)
			{
				InterfaceMethod parentMethod = parentInterface.methodList.get(j);
				
				if( !isAlreadyInMethodList( allMethod, parentMethod ) )
				{
					allMethod.add( parentMethod );
				}
			}
		}
		return allMethod;
	}
	
	static boolean isAlreadyInMethodList(ArrayList<Method> list, Method m)
	{
		for (int i = 0; i<list.size() ; i++)
		{
			Method met = list.get(i);
			
			if( met.methodName.equals( m.methodName) && met.methodSignature.equals( m.methodSignature) )
				return true;
		}
		return false;
	}
	
	public ArrayList<UCSYMember> getAllMemberFromHierarchy()throws NotExceptedTypeException,TypeNotFoundException,CyclicInheritanceException
	{
		ArrayList<UCSYMember> allMember = new ArrayList<UCSYMember>();
		ArrayList<Method>     allAccessibleMethods = this.getAllMethodsAccessibleByInterface();
		
		for (int i = 0; i< allAccessibleMethods.size(); i++)
		{
			Method m = allAccessibleMethods.get(i);
			allMember.add(new UCSYMember(this.interfaceName,m.modifier,m.methodName,m.methodReturnType,MemberType.METHOD_OF_INTERFACE,m));
		}
		return allMember;
	}
	
	public boolean isChildOf(String parentName)
	{
		return false;
	}
	/***************************************************************************
	 *Given a typename check to see that current class is ancestor of the typeName
	 ***************************************************************************/
	public boolean isAncestorOf(String typeName)
	{
		
		try
		{
			UCSYInterface typeNameInterface = CentralTypeTable.getCentralTypeTable().getAInterface(typeName);
			
			ArrayList<UCSYInterface> parentsOfTypeName = typeNameInterface.getAllAncestorInterfaces();
			
			for (int i = 0; i< typeNameInterface.parentList.size(); i++)
			{
				String parent = typeNameInterface.parentList.get(i);
				
				if( parent.equals( this.interfaceName ))
				{
					return true;
				}
			}
			//System.out.println ("Size "+ parentsOfTypeName.size());
			for (int i = 0; i< parentsOfTypeName.size(); i++)
			{
				ArrayList<String> parents = parentsOfTypeName.get(i).parentList;
				//System.out.println ("Size 2 "+ parents.size());
				for (int j = 0; j< parents.size(); j++)			
				{
					
					String parentName = parents.get(j);
					
					if(parentName.equals( this.interfaceName ))
					{
						return true;
					}
				}
			}
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	boolean isInstanceOf(String typeName)
	{
		if(typeName.equals("Object"))
		{
			return true;
		}
		if(this.interfaceName.equals(typeName))
		{
			return true;
		}
		try
		{
			Type aType = CentralTypeTable.getCentralTypeTable().getType(typeName);
			
			if(aType instanceof UCSYClass)
			{
				UCSYClass aClass = (UCSYClass)aType;
				ArrayList<String> interfaces;
				
				
				while(!aClass.className.equals("Object"))
				{
					
					interfaces = aClass.interfaceList;
					for (int i = 0; i< interfaces.size(); i++)
					{
						String aInterface = interfaces.get(i);
						if(this.interfaceName.equals(aInterface))
						{
							return true;
						}	
					}
					aClass = CentralTypeTable.getCentralTypeTable().getAClass(aClass.parentClassName);
				}
				return false;
				
			}
			else if(aType instanceof UCSYInterface)
			{
				//Debug.inform("This interface Name "+this.interfaceName);
				UCSYInterface typeInterface = (UCSYInterface)aType;
				ArrayList<UCSYInterface> allSuperInterfaces = this.getAllAncestorInterfaces();
				
				for (int i = 0; i<allSuperInterfaces.size(); i++)
				{
					UCSYInterface aInterface = allSuperInterfaces.get(i);
					if(typeInterface.interfaceName.equals(aInterface.interfaceName))	
					{
						return true;
					}
				}
				return false;
				
			}
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
			
			
		
	}
	public boolean assignmentCompatiable(String typeName)
	{
		//System.out.println ("Assigne Interface "+ typeName);
		if(this.interfaceName.equals( typeName ))
		{
			return true;
		}
		else if( this.isAncestorOf( typeName ))
		{
			return true;
		}
		else
		{
			//If typeName is a Class, then if that class implements this interface return true;
			
			if(CentralTypeTable.getCentralTypeTable().isClass( typeName ))
			{
				
				try
				{
					UCSYClass typeNameClass = CentralTypeTable.getCentralTypeTable().getAClass( typeName );	
					ArrayList<UCSYInterface> allInterface = typeNameClass.getAllInterfaceHierarchy();
					
					for (int i = 0; i< allInterface.size(); i++)
					{
						String intefaceName = allInterface.get(i).interfaceName;
						
						if(this.interfaceName.equals( interfaceName ))
						{
							return true;
						}
					}
					return false;
					
				}
				catch(Exception e)
				{
					return false;
				}
			}
		}
		return false;
	}
	public Method getBestOverloadedMethod(String contextName ,String methodName,String methodSignature)throws 
	UnambiguousResolutionException,CannotFindBestOverloadedMethodException
	{
		//Debug.inform("Interface OverloadResolved "+methodName);
		Method bestMethod = null;
		int noOfParameter = MethodOverloadResolution.getNoOfParameter(methodSignature);
		
		try
		{
			ArrayList<Method> allMethods = this.getAllMethodsAccessibleByInterface();
			ArrayList<Method> allMethodToOverload = new ArrayList<Method>(); 
			
			for (int i = 0; i< allMethods.size(); i++)
			{
				Method met = allMethods.get(i);
				int parSize = MethodOverloadResolution.getNoOfParameter(met.methodSignature);
				if( (met.methodName.equals(methodName)) &&(parSize == noOfParameter))
				{
					allMethodToOverload.add(met);
				}
			}
			
			/*
			int exactMatch = MethodOverloadResolution.findExactMatch(allMethodToOverload,methodSignature);
			
			if( exactMatch != -1)
			{
				bestMethod = allMethodToOverload.get(exactMatch);
				Debug.inform("Found Exact match for "+methodName+" is "+bestMethod.methodName );
				return bestMethod;
				//System.out.println ("Found eact match "+ allMethodToOverload.get(exactMatch).methodName +" "+ allMethodToOverload.get(exactMatch).methodSignature);
			}
			else
			{
				ArrayList<Method> ruleOneMethod = MethodOverloadResolution.findBestOverloadUsingRuleOne(allMethodToOverload,methodSignature);
				
				if(ruleOneMethod.size()== 0 )
				{
					//Find using rule Two
					ArrayList<Method> ruleTwoMethod = MethodOverloadResolution.findBestOverloadUsingRuleTwo(allMethodToOverload,methodSignature);;
					if( ruleTwoMethod.size() > 1 )
					{
						throw new UnambiguousResolutionException(ruleTwoMethod);
					}
					else if(ruleTwoMethod.size() == 1)
					{
						bestMethod = ruleTwoMethod.get(0);
						return bestMethod;
					}
					else
					{
						throw new CannotFindBestOverloadedMethodException();
					}
				}
				else if( ruleOneMethod.size() > 1 )
				{
					throw new UnambiguousResolutionException(ruleOneMethod);
				}
				else
				{
					bestMethod = ruleOneMethod.get(0);
					return bestMethod;
				}
				
			}
			*/
			bestMethod = MethodOverloadResolution.findBestOverloadedMethod(allMethodToOverload,methodSignature);
			//System.out.println (exactMatch);
		}
		catch(TypeNotFoundException e)
		{
			//It will be already catched
		}	
		catch(NotExceptedTypeException e)
		{
			//It will be already catched
		}
		catch(CyclicInheritanceException e)
		{
			//It will be already catched
		}
		return bestMethod;
		
	}
	
	/***************************************************************************
	 * Code Generation
	 ***************************************************************************/
	 String emitCodeForMemberMethod(Method m)
	{
		///Debug.inform("EMITTING "+ this.interfaceName+" method "+m.methodName);
		String typeName =m.methodReturnType;
		CodeGenerator.currentClass.uCodeFile.constantPool.addInterfaceMethodRef( this.interfaceName,m.methodName,m.methodProtocol);
		int methodRefIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getInterfaceMethodRefIndex(this.interfaceName,m.methodName,m.methodProtocol);
		int parSize = CentralTypeTable.getSizeForSignature( m.methodSignature );
		int retSize = CentralTypeTable.getSizeOfType( m.methodReturnType );
		//Debug.inform("Pop "+ parSize+" push "+ retSize+" return type"+m.methodReturnType);
		
		CodeGenerator.currentMethod.methodCode.emitCallInterface( methodRefIndex,parSize,1);
		
		return typeName;
	}
	 public void generateUCodeFile()
	 {
	 }
}

