import java.util.*;


enum UCSYOperator
{
	ASSIGNMENT,
	PLUS_ASSIGN,
	MINUS_ASSIGN,
	MULT_ASSIGN,
	DIV_ASSIGN,
	MOD_ASSIGN,
	REL_GT,
	REL_GTE,
	REL_LT,
	REL_LTE,
	REL_EQU,
	REL_NOT_EQU,
	BOL_AND,
	BOL_OR,
	BOL_NOT,
}
class UCSYClass extends Type implements MemberAccess
{
	int modifier;
	String className;
	String parentClassName = "Object";
	String adaptsClass ;
	String adaptInterface;
	ArrayList<String> interfaceList = new ArrayList<String>();
	ArrayList<String> conformList = new ArrayList<String>(); 
	ArrayList<UCSYMethod> methods = new ArrayList<UCSYMethod>();
	ArrayList<UCSYField> fields = new ArrayList<UCSYField>();
	
	//************** For only static smenatic use by free class and adapter class declaration ***********
	boolean isFreeClass;
	boolean isAdapterClass;
	boolean isSingleton;
	//***************************************************************************************************
	
	int memberModifier ; //TO pass informatio down to member 
	UCSYClass()
	{
		this.itsType = TypeOfType.CLASSTYPE;
		this.typeNameDescription = "class";
	}
	ArrayList<ASTSignatureToCall> signatureToCall = new ArrayList<ASTSignatureToCall>();
	
	void addMethod(UCSYMethod m)
	{
		//Debug.inform("Class  "+this.className +" add method "+m.methodName);
		methods.add(m);
	}
	
	void addField(UCSYField f)
	{
		fields.add(f);
	}
	public TypeOfType getType()
	{
		return TypeOfType.CLASSTYPE;
	}
	boolean isFreeClass()
	{
		return (this.modifier & UCSYClassAttribute.FREE ) == UCSYClassAttribute.FREE;
	}
	//Get a method with name and signature by searching through whole class ,meta class interface hirerachy
	Method getMethodOfNameAndSignature(String methodName,String methodSignature)throws TypeNotFoundException, NotExceptedTypeException,CyclicInheritanceException
	{
		UCSYMethod m = null;
		ArrayList<Method> allMethod = this.getAllMethodsAceessibleByClass();
		for (int i = 0; i< allMethod.size() ; i++)
		{
			Method met = allMethod.get(i);
			
			//Ommiting access specifier constraint , must be ruled 
			if( met.methodName.equals( methodName) && met.methodSignature.equals( methodSignature) )
			{
				return met;
			}
		}
		return null;
	}
	ArrayList<String> getSignaturesOfTheMethod(String methodName)
	{
		ArrayList<String> l = new ArrayList<String>();
		
		for (int i = 0; i< methods.size(); i++)
		{
			UCSYMethod met = methods.get(i);
			if(met.methodName.equals(methodName))
			{
				l.add(met.methodSignature);
			}
		}
		return l;
	}
	//ArrayList<AbstractMethod>
	boolean isAField(String fieldName)
	{
		for (int i = 0; i< fields.size(); i++)
		{
			UCSYField fi = fields.get(i);
			if(fi.fieldName.equals(fieldName))
				return true;
		}
		return false;
	}
	boolean isAMethod(String methodName)
	{
		for (int i = 0; i< methods.size(); i++)
		{
			UCSYMethod met = methods.get(i);
			if(met.methodName.equals( methodName ))
				return true;
		}
		return false;
	}
	UCSYMethod getAMethod(String methodName)
	{
		for (int i = 0; i< methods.size(); i++)
		{
			UCSYMethod met = methods.get(i);
			if( met.methodName.equals( methodName ))
			{
				met.ownerName = this.className;
				return met;
			}
		}
		return null;
	}
	boolean isFinal()
	{
		return (this.modifier & UCSYClassAttribute.FINAL) == UCSYClassAttribute.FINAL;
	}
	boolean isAbstract()
	{
		return (this.modifier & UCSYClassAttribute.ABSTRACT) == UCSYClassAttribute.ABSTRACT;
	}
	boolean implementsMethod(Method theMethod)
	{
		boolean retValue = false;
		
		for (int i = 0; i< methods.size(); i++)	
		{
			
			UCSYMethod m = methods.get(i);
			
			if( m.isPublic() && (m.methodName.equals( theMethod.methodName )) && (m.methodSignature.equals( theMethod.methodSignature)) && ( m.methodReturnType.equals( theMethod.methodReturnType)))
			{
				return true;
			}
		}
		return false;
		
	}
	ArrayList<Method> getInheritsMethod()throws NotExceptedTypeException, TypeNotFoundException
	{
		ArrayList<Method> inheritsMethod = new ArrayList<Method>();
		
		
		UCSYClass parentClass = CentralTypeTable.getCentralTypeTable().getAClass( parentClassName );
		for (int i = 0; i< methods.size(); i++)
		{
		}	
		return inheritsMethod;
	
		
		
		
	}
	ArrayList<UCSYClass> getAllParent()throws TypeNotFoundException , NotExceptedTypeException,CyclicInheritanceException
	{
		ArrayList<UCSYClass> allParent = new ArrayList<UCSYClass>();
		
		String parentName = this.parentClassName;
		while(! parentName.equals("Object"))
		{
			UCSYClass parentClass =  CentralTypeTable.getCentralTypeTable().getAClass( parentName);
			
			if( parentClass.className.equals( this.className ))
			{
				throw new CyclicInheritanceException(this.className);
			}
			allParent.add(parentClass);
			parentName = parentClass.parentClassName;
					
		}
		return allParent;
	}
	
	ArrayList<Method> getAllAbstractMethod()
	{
		ArrayList<Method> abstractMethods = new ArrayList<Method>();
		for (int i = 0; i< methods.size(); i++)
		{
			UCSYMethod method = methods.get(i);
			
			if( method.isAbstract())
			{
				abstractMethods.add( method );
			}
		}
		return abstractMethods;
	}
	
	ArrayList<MethodToOverride> getAllUnimplementedInterfaceMethod()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException
	{
		ArrayList<MethodToOverride> allUnimplementedInterfaceMethod = new ArrayList<MethodToOverride>();
		
		//Collected all ancestor interfaces of the class
		ArrayList<UCSYInterface> allAncestorInterface = new ArrayList<UCSYInterface>();
		for (int i = 0; i< this.interfaceList.size(); i++)
		{
			String interfaceName       = this.interfaceList.get(i); 
			UCSYInterface theInterface = CentralTypeTable.getCentralTypeTable().getAInterface( interfaceName );
		
			
			allAncestorInterface = theInterface.getAllParentInterface(theInterface.interfaceName );
			allAncestorInterface.add( theInterface );
		}
		
		for (int i = 0; i< allAncestorInterface.size(); i++)
		{
			UCSYInterface theInterface = allAncestorInterface.get(i);
		
			for (int j = 0; j< theInterface.methodList.size(); j++)
			{
				InterfaceMethod interfaceMethod = theInterface.methodList.get(j);
			
				if(! this.implementsMethod( interfaceMethod ))
				{
					allUnimplementedInterfaceMethod.add( new MethodToOverride(theInterface.interfaceName, interfaceMethod ));
				}
			}
		}
			return allUnimplementedInterfaceMethod;
				
	}
	/***************************************************************************
	 Return all abstract method of ancestor
	 this include abstrat method of parent class
	 method of interface
	 it include all abstract method of the inheritance hierarchy 
	 and interface hierarchy
	*************************************************************************************/
	ArrayList<MethodToOverride> getAllAbstractMetodOfAncestors()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException
	{
		ArrayList<MethodToOverride> allAbstractMethod = new ArrayList<MethodToOverride>();
		try
		{
			
		
			String parentClassName = this.parentClassName ;
			UCSYClass parentClass = CentralTypeTable.getCentralTypeTable().getAClass( parentClassName );
		
			ArrayList<MethodToOverride> unimplementedInterfaceMethod = this.getAllUnimplementedInterfaceMethod();
			for (int i = 0; i< unimplementedInterfaceMethod.size(); i++)
			{
				allAbstractMethod.add( unimplementedInterfaceMethod.get(i));
			}
		
			while( parentClass.isAbstract() )
			{
				///System.out.println (parentClass.className);
				//Get all abstract method of parent
				ArrayList<Method> parentAbstractMethod = parentClass.getAllAbstractMethod();
				for (int i = 0; i< parentAbstractMethod.size(); i++)
				{
					allAbstractMethod.add(new MethodToOverride( parentClass.className, parentAbstractMethod.get(i)));
				}
			
				unimplementedInterfaceMethod = parentClass.getAllUnimplementedInterfaceMethod();
			
				for (int i = 0; i< unimplementedInterfaceMethod.size(); i++)
				{
					allAbstractMethod.add( unimplementedInterfaceMethod.get(i));
				}
				parentClassName = parentClass.parentClassName;
				parentClass = CentralTypeTable.getCentralTypeTable().getAClass( parentClassName );
			}
		}
		catch(TypeNotFoundException e)
		{
			throw e;
		}
		return allAbstractMethod;
	}
	/**************************************************************************
	 *Return all public method of the current class
	 **************************************************************************/
	 ArrayList<Method> getAllPublicMethod()
	 {
	 	ArrayList<Method> allPublicMethod = new ArrayList<Method>();
	 	
	 	for (int i = 0; i< methods.size(); i++)
	 	{
	 		Method m = methods.get(i);
	 		if( m.isPublic() && !m.methodName.equalsIgnoreCase("<init>") && !m.methodName.equalsIgnoreCase("<cinit>") )
	 		{
	 			allPublicMethod.add(m);
	 			m.ownerName = this.className;
	 		}
	 	}
	 	return allPublicMethod;
	 }
	 
	boolean isOverrideAbstractMethod(Method m)throws MissingOverrideModifierException,WeakerAccessSpecifierException,IncorrectMethodReturnTypeException
	{
		for (int i = 0; i< methods.size(); i++)
		{
			Method memberMethod = methods.get(i);
			
			if( memberMethod.methodName.equals(m.methodName) && memberMethod.methodSignature.equals(m.methodSignature))
			{
				if( ! memberMethod.isOverride())
				{
					throw new MissingOverrideModifierException();
				}
				if(! memberMethod.isPublic())
				{
					throw new WeakerAccessSpecifierException();
				}
				if(! memberMethod.methodReturnType.equals( m.methodReturnType))
				{
					throw new IncorrectMethodReturnTypeException();
				}
				return true;
			}
			
			
		}
		return false;
	}
	boolean isConformsMetaClassMethod(Method m)
	{
		for (int i = 0; i< methods.size(); i++)
		{
			Method memberMethod = methods.get(i);
			
			if( memberMethod.isPublic() 
			&& memberMethod.methodName.equals( m.methodName) 
			&& memberMethod.methodSignature.equals( m.methodSignature)
			&& memberMethod.methodReturnType.equals( m.methodReturnType)
			)
				return true;
		}
		return false;
	}
	
	
	/************************************************************************************
	 Return all of ther interface implemnts by the class, and all of their parent interface
	All interface ancestor of the class
	***************************************************************************************/
	ArrayList<UCSYInterface> getAllInterfaceHierarchy()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException
	{
		if(this.className.equals("Object"))
		{
			return new ArrayList<UCSYInterface>();
		}
		ArrayList<UCSYInterface> allInterfaces = new ArrayList<UCSYInterface>();
		ArrayList<UCSYInterface> retInterfaces = new ArrayList<UCSYInterface>();
		for (int i = 0; i< this.interfaceList.size(); i++)
		{
			String interfaceName = interfaceList.get(i);
			UCSYInterface theInterface =  CentralTypeTable.getCentralTypeTable().getAInterface( interfaceName );
			
			allInterfaces.add(theInterface);
			
			retInterfaces = theInterface.getAllParentInterface( theInterface.interfaceName );
			
			for (int j = 0; j < retInterfaces.size(); j++)
			{
				allInterfaces.add(retInterfaces.get(i));
			}
		}
		UCSYClass superClass = CentralTypeTable.getCentralTypeTable().getAClass( this.parentClassName );
		
		ArrayList<UCSYInterface> superClassInterface = superClass.getAllInterfaceHierarchy();
		for (int i = 0; i< superClassInterface.size(); i++)
		{
			UCSYInterface aInterface = superClassInterface.get(i);
			allInterfaces.add(aInterface);
		}
		return allInterfaces;
	}
	//Return all meta class hierarchy
	ArrayList<UCSYMetaClass> getAllMetaClassHierarchy()throws TypeNotFoundException, NotExceptedTypeException
	{
		ArrayList<UCSYMetaClass> allMetaClass = new ArrayList<UCSYMetaClass>();
		ArrayList<UCSYMetaClass> parentMetaClass ;
		for (int i = 0; i< this.conformList.size(); i++)
		{
			String metaClassName = conformList.get(i);
			UCSYMetaClass metaClass = CentralTypeTable.getCentralTypeTable().getAMetaClass( metaClassName );
			
			allMetaClass.add( metaClass );
			parentMetaClass = metaClass.getAllParent();
			
			for (int j = 0; j< parentMetaClass.size(); j++)
			{
				allMetaClass.add( parentMetaClass.get(j));
			}
			
		}
		return allMetaClass;
	}
	/***************************************************************************
	 return current class method, method of all its parent class hierarchy plus
	 method of its interface hierarchy
	 did not check for access specifier, must also check 
	 do it later
	 ***************************************************************************/
	
	ArrayList<Method> getAllMethodsAceessibleByClass()throws TypeNotFoundException,NotExceptedTypeException,CyclicInheritanceException
	{
		
		ArrayList<Method> allMethod = new ArrayList<Method>();
		
		//Add method of current classs
		for (int i = 0; i< methods.size() ; i++)
		{
			allMethod.add( methods.get(i));
		}
		
		//Get all method of parent class and add to the allMethod
		ArrayList<UCSYClass> allParentClass = this.getAllParent();
		
		for (int i = 0; i< allParentClass.size(); i++)
		{
			UCSYClass parentClass = allParentClass.get(i);
			
			for (int j = 0; j< parentClass.methods.size(); j++)	
			{
				Method parentMethod = parentClass.methods.get(j);
				
				if(! isAlreadyInMethodList( allMethod, parentMethod ))
				{
					allMethod.add(parentMethod);
				}
			}
		}
		
		//Get all methods of Interfaces 
		ArrayList<UCSYInterface> allInterface = this.getAllInterfaceHierarchy();
		
		for (int i = 0; i< allInterface.size(); i++)
		{
			UCSYInterface parentInterface = allInterface.get(i);
			
			for (int j = 0; j< parentInterface.methodList.size(); j++)
			{
				
					Method interfaceMethod = parentInterface.methodList.get(j);
					
					if(! isAlreadyInMethodList(allMethod, interfaceMethod ))
					{
						allMethod.add( interfaceMethod );
					}
				
			}
		}
		//Get all Method of the meta class
		ArrayList<UCSYMetaClass> allMetaClass = this.getAllMetaClassHierarchy();
		for (int i = 0; i<allMetaClass.size(); i++)
		{
			UCSYMetaClass parentMetaClass = allMetaClass.get(i);
			
			for (int j = 0; j< parentMetaClass.methodList.size(); j++)
			{
				Method metaClassMethod = parentMetaClass.methodList.get(j);
				if( ! isAlreadyInMethodList( allMethod, metaClassMethod ))
				{
					allMethod.add( metaClassMethod );
				}
			}
		}
		return allMethod;
	}
	
	boolean isAlreadyInMethodList(ArrayList<Method> list, Method m)
	{
		for (int i = 0; i<list.size() ; i++)
		{
			Method met = list.get(i);
			
			if( met.methodName.equals( m.methodName) && met.methodSignature.equals( m.methodSignature) )
				return true;
		}
		return false;
	}
	public ArrayList<ClassAndField> getAllFieldFromHierarchy()throws TypeNotFoundException,NotExceptedTypeException
	{
		ArrayList<ClassAndField> allField =new  ArrayList<ClassAndField>();
		
		for (int i = 0; i< fields.size(); i++)
		{
			UCSYField field = fields.get(i);
			
			allField.add(new ClassAndField(this.className,field.modifier,field.fieldName,field.fieldType));
		}
		String className = this.parentClassName;
		
		while(! className.equals("Object"))
		{
			UCSYClass theClass = CentralTypeTable.getCentralTypeTable().getAClass(className);	
			for (int i = 0; i<theClass.fields.size(); i++)
			{
				UCSYField field = theClass.fields.get(i);
			
				allField.add(new ClassAndField(this.className,field.modifier,field.fieldName,field.fieldType));
			}
			className = theClass.parentClassName;
		}
		
		return allField;
	}
	
	/***************************************************************************
	 * return all member fields + methods from all of the Hierarchy
	 **************************************************************************/
	public ArrayList<UCSYMember> getAllMemberFromHierarchy()throws TypeNotFoundException,NotExceptedTypeException ,CyclicInheritanceException
	{
		ArrayList<UCSYMember> allMember =new  ArrayList<UCSYMember>();
		
		
			//Add field of current class
		for (int i = 0; i< fields.size(); i++)
		{
			UCSYField field = fields.get(i);
			
			allMember.add(new UCSYMember(this.className,field.modifier,field.fieldName,field.fieldType,MemberType.FIELD,field));
		}	
		
			//Add all methods of current class
		for (int i = 0; i< methods.size(); i++)
		{
			UCSYMethod method = methods.get(i);
			if(! (method.methodName.equals("<init>") || method.methodName.equals("<cinit>")))
				allMember.add(new UCSYMember(this.className, method.modifier, method.methodName,method.methodReturnType,MemberType.METHOD_OF_CLASS,method));
		}
			//Get all methods of Interfaces 
		ArrayList<UCSYInterface> allInterface ;
		allInterface= this.getAllInterfaceHierarchy();
		
		for (int i = 0; i< allInterface.size(); i++)
		{
			UCSYInterface parentInterface = allInterface.get(i);
			
			for (int j = 0; j< parentInterface.methodList.size(); j++)
			{
				
				Method interfaceMethod = parentInterface.methodList.get(j);
					
				allMember.add(new UCSYMember(parentInterface.interfaceName, interfaceMethod.modifier,interfaceMethod.methodName,interfaceMethod.methodReturnType,MemberType.METHOD_OF_INTERFACE,interfaceMethod));
				
			}
		}
		String className = this.parentClassName;
		
		while(! className.equals("Object"))
		{
			UCSYClass theClass = CentralTypeTable.getCentralTypeTable().getAClass(className);	
			for (int i = 0; i<theClass.fields.size(); i++)
			{
				UCSYField field = theClass.fields.get(i);
		
				allMember.add(new UCSYMember(theClass.className,field.modifier,field.fieldName,field.fieldType,MemberType.FIELD,field));
			}
			for (int i = 0; i< theClass.methods.size(); i++)
			{
				UCSYMethod method = theClass.methods.get(i);
				if(! (method.methodName.equals("<init>") || method.methodName.equals("<cinit>")))
					allMember.add(new UCSYMember(theClass.className, method.modifier, method.methodName,method.methodReturnType,MemberType.METHOD_OF_CLASS,method));
			}
			ArrayList<UCSYInterface> allParentClassInterface ;
			allParentClassInterface= theClass.getAllInterfaceHierarchy();
		
			for (int i = 0; i< allParentClassInterface.size(); i++)
			{
				UCSYInterface parentInterface = allParentClassInterface.get(i);
			
				for (int j = 0; j< parentInterface.methodList.size(); j++)
				{
				
					Method interfaceMethod = parentInterface.methodList.get(j);
					
					allMember.add(new UCSYMember(parentInterface.interfaceName, interfaceMethod.modifier,interfaceMethod.methodName,interfaceMethod.methodReturnType,MemberType.METHOD_OF_INTERFACE,interfaceMethod));
					
				}	
			}
			className = theClass.parentClassName;
		}	
		return allMember;
		
		
	}
	
	/***************************************************************************
	 *Given a parent Name test whether is it a decendents of parent name
	 ***************************************************************************/
	public boolean isChildOf(String parentName)
	{
		boolean retValue = false;
		try
		{	
			ArrayList<UCSYClass> parentClass = this.getAllParent();
		
			for (int i = 0; i< parentClass.size(); i++)
			{
				UCSYClass parent = parentClass.get(i);
			
				if( parent.className.equals( parentName ))
				{
					return true;
				}
			}
		
			ArrayList<UCSYInterface> parentInterface = this.getAllInterfaceHierarchy();
		
			for (int i = 0; i< parentInterface.size(); i++)
			{
				UCSYInterface parent = parentInterface.get(i);
			
				if(parent.interfaceName.equals( parentName))
				{
					return true;
				}
			}
		}
		catch(Exception e)
		{
			return false;
		}
		return false;
		
	}
	/***************************************************************************
	 *Given a typename check to see that current class is ancestor of the typeName
	 ***************************************************************************/
	public boolean isAncestorOf(String typeName)
	{
		try
		{
			
			if( this.className.equals("Object") && (! CentralTypeTable.isPrimitiveType( typeName)))
			{
				return true;
			}
			
			UCSYClass typeNameClass = CentralTypeTable.getCentralTypeTable().getAClass(typeName);
			
			if( typeNameClass.isChildOf( this.className ))
				return true;
			else
				return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	/***************************************************************************
	 *Used for TypeChecking
	 ***************************************************************************/
	public boolean assignmentCompatiable(String typeName)
	{
		///System.out.println ("Checking for Compatiablity of "+ this.className+" "+ typeName);
		//Destination is class
		if( this.className.equals( typeName ))
		{
			return true;
		}
		else 
			return this.isAncestorOf( typeName );
		
	}
	public boolean isInstanceOf(String typeName)
	{
		///Debug.inform("This case");
		if(typeName.equals("Object"))
		{
			return true;
		}
		if(this.className.equals(typeName))
		{
			return true;
		}
		try
		{
			Type aType = CentralTypeTable.getCentralTypeTable().getType(typeName);
			
			if(aType instanceof UCSYClass)
			{
				UCSYClass aClass = (UCSYClass)aType;
				String superClassName = aClass.parentClassName;
				
				while(!superClassName.equals("Object"))
				{
					if(superClassName.equals(className))
					{
						return true;
					}
					superClassName = aClass.parentClassName;
					aClass         = CentralTypeTable.getCentralTypeTable().getAClass(superClassName);
				}
				return false;
				
			}
			else if(aType instanceof UCSYInterface)
			{
				UCSYClass aClass = this;
				ArrayList<String> allInterfaces ;
				
				while(!aClass.className.equals("Object"))
				{
					allInterfaces = aClass.interfaceList;
					for (int i = 0; i<allInterfaces.size() ; i++)
					{
						String aInterface = allInterfaces.get(i);
						//Debug.inform("Comparing "+);
						if( aInterface.equals(typeName) )	
						{
							return true;
						}
					}
					aClass = CentralTypeTable.getCentralTypeTable().getAClass(aClass.parentClassName);		
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
	
	public boolean isImplementedInterface(String interfaceType)
	{
		//If the current class or ancestor of this class implements this inteface 
		//return true;
		for (int i = 0; i< this.interfaceList.size(); i++)
		{
			String interfaceName = this.interfaceList.get(i);
			
			if( interfaceName.equals( interfaceType))
			{
				//System.out.println ("OK");
				return true;
			}
		}
		if(! this.className.equals("Object"))
		{
			try
			{
				UCSYClass parentClass = CentralTypeTable.getCentralTypeTable().getAClass( this.parentClassName );
				return parentClass.isImplementedInterface( interfaceType );
			}
			catch(Exception e)
			{
				return false;
			}
		}
		return false;
	}
	/*************************************************************************************************
	 *	ContextClass is class that invoke the method of this class
	 *  class MyClass
	 *	{
	 		void main()
	 		{
	 			Human h;
	 			h.display(); //Here MyClass is contextClass, methodName is display , signature is ()
	 			//Current class is Human
	 		}
	 *	}
	 *[1] Exact match; that is, match using no or only trivial conversions 
	  [2] Match using promotions; that is, integral promotions (int to long,byte to i n t , short to int, float to double , 
	  [3] Match using standard conversions (for example, int to double , double to int , Derived * to Base*
	 *************************************************************************************************/
	public Method getBestOverloadedMethod(String contextClassName,String methodName,String methodSignature)
	throws UnambiguousResolutionException, CannotFindBestOverloadedMethodException
	{
		Method bestMethod = null;
		int noOfParameter = MethodOverloadResolution.getNoOfParameter(methodSignature);
		try
		{
			ArrayList<UCSYMember> allMembers = this.getAllMemberFromHierarchy();
			ArrayList<Integer> indexToRemove = new ArrayList<Integer>();
			//Remove all, field , method that are not with same name or not same parameter length
			
			for (int i = 0; i< allMembers.size(); i++)
			{
				
				UCSYMember member = allMembers.get(i);
				
				
				if(member.typeOfMember == MemberType.FIELD)
				{
					
					indexToRemove.add(i);
				}
				else 
				{
					Method met = (Method)member.member;
					//System.out.println ("Metthos "+ met.methodName +" "+met.methodSignature + " "+ i);
					if(met.methodName.equals( methodName))
					{
						if(met.getNoOfParameter() != noOfParameter)
						{
							//System.out.println ("Remove "+ i);
							indexToRemove.add(i);
						}
						try
						{
							TypeCheckUtilityClass.checkAccessValid(contextClassName,member.ownerName, met.modifier);
						}
						catch(InvalidAccessToMemberException e)
						{
							//Remove method that are not accessible by this context
							//System.out.println ("Remove "+ i);
							if(!indexToRemove.contains(i))
							{
								indexToRemove.add(i);
							}
						}
					}
					else
					{
						//System.out.println ("Remove "+ i);						
						indexToRemove.add(i);
					} 
				}
				
			}
			for (int i = indexToRemove.size()-1; i >= 0; i--)
			{
				int index = indexToRemove.get(i);
				//System.out.println ("Removing "+ i);
				allMembers.remove(index);
			}
			
			ArrayList<Method> allMethodToOverload = new ArrayList<Method>();
			for (int i = 0; i< allMembers.size(); i++)
			{
				Method met = (Method)allMembers.get(i).member;
				///Debug.inform("Add "+allMembers.get(i).memberType);
				met.ownerName = allMembers.get(i).ownerName;
				allMethodToOverload.add( met );
				met.splitParameter();
				met.methodProtocol = met.methodSignature + met.methodReturnType;
				//System.out.println ("Method Name "+ met.methodName+ " "+met.methodSignature);
				
			}
			bestMethod = MethodOverloadResolution.findBestOverloadedMethod(allMethodToOverload, methodSignature);
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
	public Method getBestOverloadedConstructor(String contextClassName,String methodSignature)
	throws UnambiguousResolutionException, CannotFindBestOverloadedMethodException
	{
		ArrayList<Method> allMethodToOverload = new ArrayList<Method>();
		int noOfParameter = MethodOverloadResolution.getNoOfParameter(methodSignature);
		for (int i = 0; i< this.methods.size(); i++)
		{
			Method met = this.methods.get(i);
			met.splitParameter();
			if(met.methodName.equals("<init>") && noOfParameter == met.getNoOfParameter())
			{
				try
				{
					TypeCheckUtilityClass.checkAccessValid(contextClassName,this.className, met.modifier);
					allMethodToOverload.add(met);
					met.ownerName = this.className;
					met.methodReturnType ="v";
					met.methodProtocol = met.methodSignature + met.methodReturnType;
				}
				catch(Exception e)
				{
					
				}
				
			}
		}
		Method bestMethod = MethodOverloadResolution.findBestOverloadedMethod( allMethodToOverload,methodSignature );
		return bestMethod;
	}
	/***************************************************************************
	 * Code Generation 
	 ***************************************************************************/
	 public void generateUCodeFile()
	 {
	 	this.uCodeFile.produceUCodeFile();
	 }
	 
	 UCSYField resolveField(String fieldName)
	 {
	 	///Debug.inform("Resolving "+fieldName);
	 	try
	 	{
	 		ArrayList<UCSYMember> allMembers = this.getAllMemberFromHierarchy();
	 		for (int i = 0; i< allMembers.size(); i++)
	 		{
	 			UCSYMember member = allMembers.get(i);
	 		
	 			if( member.member instanceof UCSYField && member.memberName.equals(fieldName))
	 			{
	 				return (UCSYField)member.member;
	 			}
	 		}
	 	}
	 	catch(Exception e)
	 	{
	 		e.printStackTrace();
	 	}
	 	return null;
	 }
	 
	String emitCodeForMemberField(String fieldName)
	{
		String typeName="";
		
		ArrayList<UCSYMember> allMembers = null;
		try
		{
			allMembers= this.getAllMemberFromHierarchy();
		}
		catch(Exception e)
		{
		}
		for (int i = 0; i< allMembers.size(); i++)
		{
			UCSYMember member = allMembers.get(i);
			
			if( member.memberName.equals(fieldName) && (member.member instanceof UCSYField))
			{
				
				UCSYField field = (UCSYField)member.member;
			
				CodeGenerator.currentClass.uCodeFile.constantPool.addFieldRef(member.ownerName,field.fieldName,field.fieldType);
			
				int fieldRefIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getFieldRefIndex(member.ownerName,field.fieldName,field.fieldType);
				int fieldSize = CentralTypeTable.getSizeOfType( field.fieldType );
				//Debug.inform("Found ");
				if(field.isStatic())
				{
					///CodeGenerator.inform("getStatic Field "+ fieldRefIndex);
					
					CodeGenerator.currentMethod.methodCode.emitGetStaticField( fieldRefIndex,fieldSize);
				}
				else
				{
					///CodeGenerator.inform("getInstance Field "+ fieldRefIndex);
					CodeGenerator.currentMethod.methodCode.emitGetInstanceField( fieldRefIndex, fieldSize);
				}
				typeName = field.fieldType;
				return typeName;
				//CodeGenerator.currentClass.constantPool.addfield
			}
		}
		return typeName;
	}
	
	String emitCodeForMemberMethod(Method m)
	{
		String typeName =m.methodReturnType;
		m.methodProtocol = m.methodSignature + m.methodReturnType;
		CodeGenerator.currentClass.uCodeFile.constantPool.addMethodRef( m.ownerName,m.methodName,m.methodProtocol);
		int methodRefIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getMethodRefIndex(m.ownerName,m.methodName,m.methodProtocol);
		int parSize = CentralTypeTable.getSizeForSignature( m.methodSignature );
		int retSize = CentralTypeTable.getSizeOfType( m.methodReturnType );
		//Debug.inform("Pop "+ parSize+" push "+ retSize+" return type"+m.methodReturnType);
		//Debug.inform("Calling "+ m.ownerName);
		if(m.isStatic())
		{
			///Debug.inform("STATIC_CALL "+ m.ownerName +" "+ m.methodName +" "+m.methodSignature);
			
			CodeGenerator.currentMethod.methodCode.emitCallStatic( methodRefIndex,parSize,retSize);
		}
		else if(! m.methodName.equals("<init>"))
		{
			///Debug.inform("INSTANCE_CALL "+ m.ownerName +" "+ m.methodName +" "+m.methodSignature);
			if( m.isRebindable())
			{
				///Debug.inform("Call Rebindable "+ m.methodName);
				CodeGenerator.currentMethod.methodCode.emitCallRebindable( methodRefIndex,parSize,retSize);
			}
			else
			{
				CodeGenerator.currentMethod.methodCode.emitCallVirtual( methodRefIndex,parSize,retSize);
			}
			
			
		}
		else
		{
			CodeGenerator.currentMethod.methodCode.emitCallConstructor( methodRefIndex,parSize,1);
		}
		return typeName;
	}
	public static void main(String[]args)
	{
		System.out.println ("Final "+Integer.toBinaryString(UCSYClassAttribute.FINAL));
		System.out.println ("Abstract "+Integer.toBinaryString(UCSYClassAttribute.ABSTRACT));
		
		System.out.println ("Singleton "+Integer.toBinaryString(UCSYClassAttribute.SINGLETON));
		System.out.println ("Static "+Integer.toBinaryString(UCSYClassAttribute.STATIC));
		
		System.out.println ("private "+Integer.toBinaryString(UCSYClassAttribute.PRIVATE));
		System.out.println ("protected "+Integer.toBinaryString(UCSYClassAttribute.PROTECTED));
		System.out.println ("Internal "+Integer.toBinaryString(UCSYClassAttribute.INTERNAL));
		System.out.println ("Public "+Integer.toBinaryString(UCSYClassAttribute.PUBLIC));
		
		System.out.println ("override "+Integer.toBinaryString(UCSYClassAttribute.OVERRIDE));
		System.out.println ("multi "+Integer.toBinaryString(UCSYClassAttribute.MULTI));
		System.out.println ("rebindable "+Integer.toBinaryString(UCSYClassAttribute.REBINDABLE));
		System.out.println ("native "+Integer.toBinaryString(UCSYClassAttribute.NATIVE));
		
		
	}
}


//To be used by TypeResolver

enum MemberType
{
	FIELD,
	METHOD_OF_CLASS,
	METHOD_OF_INTERFACE,
	METHOD_OF_METACLASS
}


