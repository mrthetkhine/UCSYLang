import java.util.*;

class CollectTypeInfoVisitor implements UCSYVisitor
{
	UCSY parser ;
	
	CollectTypeInfoVisitor(UCSY p)
	{
		parser = p;
	}
	void reportSSError(int line,String s)
	{
		try
		{
			parser.reportSSError(line, s);		
		}
		catch(Exception e)
		{
			
		}
	}
	void inform(String s)
	{
		try
		{
			parser.inform(s);
		}
		catch(Exception e)
		{
		}
	}
  public Object visit(SimpleNode node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTStart node, Object data)
  {
  	//System.out.println ("I got "+ node.typeDeclaration.size());
  	for (int i = 0; i< node.typeDeclaration.size(); i++)
  	{
  		ASTTypeDeclaration typeDecl = node.typeDeclaration.get(i);
  		typeDecl.jjtAccept(this,typeDecl);
  	}
  	 return null;
  }
  //****************************************************************************
  
  
  public Object visit(ASTPackageDeclaration node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTImportDeclaration node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTTypeDeclaration node, Object data)
  { 
  	node.typeDeclaration.jjtAccept( this,data);
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTClassDeclaration node, Object data)
  { 
  	//System.out.println ("Class type prepare");
  	node.theClass = new UCSYClass();
  	node.classHeader.jjtAccept(this, node.theClass);
  	node.classBody.jjtAccept(this, node.theClass );
  	
  	
  	///Debuging
  	///inform ("Class Name "+ node.theClass.className);
  	///inform ("Parent Name "+ node.theClass.parentClassName);
  	///inform ("Interface ");
  	
  	
  	///inform ("\n");	
  	///inform ("Confroms MetaClass ");
  	
  	CentralTypeTableEntry entry = new CentralTypeTableEntry(node.theClass.className, node.theClass,true);
  	CentralTypeTable.getCentralTypeTable().insert( entry );
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTClassHeader node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	
  	theClass.className = node.className;
  	node.lineNo        = node.t.beginLine;
  	
  	if( node.classModifier != null)
  	{
  		node.classModifier.jjtAccept( this, theClass);
  	}
  	if( theClass.className.charAt(0) == Character.toLowerCase( theClass.className.charAt(0)))
  	{
  		reportSSError( node.lineNo, " all type name including class name must begin with uppercase letter, you provided as "+theClass.className);
  	}
  	if(node.inheritsClause != null)
  	{
  		//theClass.parentClassName = node.inheritNode.p
  		node.inheritsClause.jjtAccept( this, theClass );
  	}
  	else
  	{
  		theClass.parentClassName = "Object";
  	}
  	if(node.adaptsClause != null)
  	{
  		node.adaptsClause.jjtAccept( this, theClass);
  	}
  	if(node.implementsClause != null)
  	{
  		node.implementsClause.jjtAccept(this, theClass);
  	}
  	if(node.conformsClause != null)
  	{
  		node.conformsClause.jjtAccept( this, theClass);
  	}
  	
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTImplementsClause node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	
  	for (int i = 0; i< node.identifierList.size(); i++)	
  	{
  		ASTIdentifier identifier = node.identifierList.get(i);
  		identifier.jjtAccept( this,data);
  		node.interfaceList.add( identifier.idName);
  		node.lineNo = identifier.t.beginLine;
  		theClass.interfaceList.add(identifier.idName );
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTConformsClause node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  	for (int i = 0; i< node.conformsIdentifier.size(); i++)
  	{
  		ASTIdentifier identifier = node.conformsIdentifier.get(i);
  		identifier.jjtAccept( this,theClass );
  		theClass.conformList.add( identifier.idName );
  		node.lineNo = identifier.t.beginLine;
  	}
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTInheritsClause node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	node.identifier.jjtAccept(this,theClass);
  	theClass.parentClassName = node.identifier.idName;
  	node.lineNo = node.identifier.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAdaptsClause node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	theClass.isAdapterClass = true;
  	node.identifierClass.jjtAccept( this, theClass );
  	node.identifierInterface.jjtAccept( this, theClass );
  	
  	node.adaptsClass = node.identifierClass.idName;
  	node.adaptsInterface = node.identifierInterface.idName;
  	
  	theClass.parentClassName = node.identifierClass.idName;
  	theClass.adaptInterface  = node.identifierInterface.idName;
  	theClass.interfaceList.add( node.identifierInterface.idName );
  	
  	for (int i = 0; i< node.signatureToCall.size(); i++)
  	{
  		ASTSignatureToCall sToCallNode = node.signatureToCall.get(i);
  		sToCallNode.isUsedByAdaptClause = true;
  		sToCallNode.adaptInterfaceName = node.adaptsInterface;
  		sToCallNode.adaptClassName = node.adaptsClass;
  		sToCallNode.jjtAccept( this, theClass );
  		
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSignatureToCall node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  	node.signature.jjtAccept( this, data );
  	node.methodCall.jjtAccept( this, data);
  	
  	node.lineNo = node.signature.lineNo;
  	/***********************************************************************
  		* Notes return type of the method must be supplied with those of the calling methods 
  		* currently assumed to be void V
  		* later , must consult class file to find out the return type of the calling method
  		
  	************************************************************************/
  	node.method = new UCSYMethod( UCSYClassAttribute.PUBLIC | UCSYClassAttribute.OVERRIDE, node.signature.methodName, node.signature.methodSignature,"V");
  	//theClass.addMethod(node.method);
  	//**************************** Insert method *******************************
  	if( node.method.methodName.charAt(0) != Character.toLowerCase(node.method.methodName.charAt(0)))
  	{
  		reportSSError(node.lineNo,"All member name including  method must begin with a lower case letter ");
  	}
  	if( theClass.isAField( node.method.methodName ))
  	{	
  		reportSSError( node.lineNo, node.method.methodName+" is already defined as field in class "+ theClass.className);
  	}
  	else
  	{
  		ArrayList<String> sig = theClass.getSignaturesOfTheMethod( node.method.methodName);
  		boolean duplicate = false;
  		for (int i = 0; i<sig.size(); i++)
  		{
  			String s = sig.get(i);
  			if(s.equals( node.method.methodSignature))
  			{
  				duplicate = true;
  				reportSSError(node.lineNo," duplicate methods are not allowed "+ node.method.methodName + " is already defined as a method in class "+ theClass.className);
  			}
  		
  		}
  		if(!duplicate)
  		{
  			//inform("Yes Insert");
  			theClass.addMethod( node.method );
  		}
  	}
  	//**************************************************************************
  	
  	if( node.isUsedByAdaptClause )
  	{
  		//Debug.inform("Used By Adapted Clause Interface "+node.adaptInterfaceName + " className "+ node.adaptClassName);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTClassModifier node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass) data;
  	theClass.modifier |= node.modifier;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTClassBody node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass) data;
  	
  	for (int i = 0; i<node.member.size(); i++)
  	{
  		ASTMemberDeclaration memberDecl = node.member.get(i);
  		memberDecl.jjtAccept( this, theClass);
  	}
  
  	//System.out.println ("There are "+ node.member.size() + " in class ");	
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTFreeClassDeclaration node, Object data)
  { 
  	node.theFreeClass = new UCSYClass();
  	node.theFreeClass.modifier |= UCSYClassAttribute.FREE;
  	node.theFreeClass.isFreeClass = true;
  	node.freeClassHeader.jjtAccept( this,node.theFreeClass );
  	node.freeClassBody.jjtAccept( this, node.theFreeClass );
  	
  	CentralTypeTableEntry entry = new CentralTypeTableEntry(node.theFreeClass.className,node.theFreeClass,true);
  	CentralTypeTable.getCentralTypeTable().insert( entry );
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTFreeClassHeader node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	node.freeClassName = node.freeClassToken.image;
  	node.lineNo        = node.freeClassToken.beginLine;
  	theClass.className = node.freeClassName;
  	
  	
  	node.parentClassName = node.parentClassToken.image;
  	theClass.parentClassName = node.parentClassName;
  	
  	if( theClass.className.charAt(0) == Character.toLowerCase( theClass.className.charAt(0)))
  	{
  		reportSSError(node.lineNo,"Type name including free class must begin with a upppercase letter, you provided "+theClass.className);
  	}
  	if( theClass.parentClassName.charAt(0) == Character.toLowerCase( theClass.parentClassName.charAt(0)))
  	{
  		reportSSError(node.lineNo,"Type name including parent of free class must begin with a upppercase letter, you provided "+theClass.parentClassName);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFreeClassBody node, Object data)
  { 
  	for (int i = 0; i< node.methodDeclarationList.size(); i++)
  	{
  		ASTMethodDeclaration methodDecl = node.methodDeclarationList.get(i);
  		methodDecl.jjtAccept( this, data );
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMemberDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass) data;
  	
  	if( node.accessModifier != null )
  	{
  		node.accessModifier.jjtAccept( this, theClass);
  		theClass.memberModifier = node.accessModifier.modifier;
  	}
  	else 
  	{
  		theClass.memberModifier = UCSYClassAttribute.PRIVATE;
  	}
  	
  	node.memberDeclaration.jjtAccept(this, theClass);
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTFunctorDeclaration node, Object data)
  { 
  	
  	//System.out.println ("Functor Type Prepare");
  	
  	node.returnType.jjtAccept( this, data );
  	node.formalParameters.jjtAccept( this, data);
  	
  	node.functorName = node.t.image;
  	node.functorSignature = node.formalParameters.formalParameterListName + node.returnType.typeName;
  	node.functorReturnType = node.returnType.typeName;
  	
  	
  	
  	node.functor                    = new FunctorType();
  	node.functor.typeName           = node.functorName ;
  	node.functor.functorName        = node.functorName;
  	//node.functor.modifier           = theClass.memberModifier;
  	node.functor.functorSignature   = node.functorSignature;
  	node.functor.functorReturnType  = node.functorReturnType;
	//inform ("Functor "+ node.functorName + " "+ node.functorSignature);
	
	CentralTypeTableEntry entry = new CentralTypeTableEntry(node.functor.typeName,node.functor,true);
	CentralTypeTable.getCentralTypeTable().insert( entry );

  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceDeclaration node, Object data)
  {
  	node.theInterface = new UCSYInterface();
  	 
  	node.interfaceHeader.jjtAccept( this, node.theInterface);
  	node.interfaceBody.jjtAccept( this, node.theInterface);
  	node.theInterface.typeName = node.theInterface.interfaceName;
  	
  	//*************************** Insert into Central TypeTable ****************
  	CentralTypeTableEntry entry = new CentralTypeTableEntry(node.theInterface.typeName,node.theInterface,true);
  	CentralTypeTable.getCentralTypeTable().insert( entry );
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAbstractMethodDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  
  	node.returnType.jjtAccept( this, data);
  	
  	node.formalParameters.jjtAccept( this, data );
  	
  	node.modifier          |= theClass.memberModifier; 
  	node.abstractMethodName = node.t.image;
  	node.lineNo             = node.t.beginLine;
  	node.abstractMethodReturnType = node.returnType.typeName;
  	node.abstractMethodSignature  = node.formalParameters.formalParameterListName;
  	
  	node.abstractMethod = new UCSYMethod( node.modifier,node.abstractMethodName,node.abstractMethodSignature,node.abstractMethodReturnType);
  	
  	if( !CentralTypeTable.isAbstract( theClass.modifier ))
  	{
  		reportSSError( node.lineNo, " abstract metod is declared in class "+ theClass.className+ " but the class is not declared as abstract");
  	}
  	if( UCSYClassAttribute.isPrivate( node.modifier ))
  	{
  		reportSSError( node.lineNo, " abstract method cannot be declared as private ,you provide private access modifier in method "+ node.abstractMethodName);
  	}
  	if( node.abstractMethod.methodName.charAt(0) != Character.toLowerCase(node.abstractMethod.methodName.charAt(0)))
  	{
  		reportSSError(node.lineNo,"All member name including abstract method must begin with a lower case letter ");
  	}
  	if( theClass.isAField( node.abstractMethod.methodName ))
  	{	
  		reportSSError( node.lineNo, node.abstractMethod.methodName+" is already defined as field in class "+ theClass.className);
  	}
  	else
  	{
  		ArrayList<String> sig = theClass.getSignaturesOfTheMethod( node.abstractMethod.methodName);
  		boolean duplicate = false;
  		for (int i = 0; i<sig.size(); i++)
  		{
  			String s = sig.get(i);
  			if(s.equals( node.abstractMethod.methodSignature))
  			{
  				duplicate = true;
  				reportSSError(node.lineNo," duplicate methods are not allowed "+ node.abstractMethod.methodName + " is already defined as a method in class "+ theClass.className);
  			}
  	
  		}
  		if(!duplicate)
  		{
  	
  			theClass.addMethod( node.abstractMethod );
  		}
  	}
  
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceHeader node, Object data)
  { 
  	UCSYInterface theInterface = (UCSYInterface)data;
  	
  	node.interfaceName = node.t.image;
  	node.lineNo        = node.t.beginLine;
  	
  	theInterface.interfaceName = node.interfaceName;
  	
  	if( theInterface.interfaceName.charAt(0) == Character.toLowerCase( theInterface.interfaceName.charAt(0)))
  	{
  		reportSSError( node.lineNo, " all type name including interface name must start with uppercase letter, you provided as "+node.interfaceName);
  	}
  	for (int i = 0; i< node.interfaceParentName.size(); i++)
  	{
  		theInterface.addParent(node.interfaceParentName.get(i));
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceBody node, Object data)
  { 
  	UCSYInterface theInterface = (UCSYInterface)data;
  	for (int i = 0; i<node.interfaceMethodDeclarationList.size(); i++)
  	{
  		ASTInterfaceMethodDeclaration dec = node.interfaceMethodDeclarationList.get(i);
  		dec.jjtAccept(this,theInterface);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceMethodDeclaration node, Object data)
  { 
  	node.interfaceMethodHeader.jjtAccept(this, data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMetaClassDeclaration node, Object data)
  { 
  	node.theMetaClass = new UCSYMetaClass();
  	
  	node.metaClassHeader.jjtAccept( this,node.theMetaClass );
  	node.metaClassBody.jjtAccept( this, node.theMetaClass );
  	
  	
  	CentralTypeTableEntry  metaClassEntry = new CentralTypeTableEntry( node.theMetaClass.metaClassName, node.theMetaClass,true);
  	CentralTypeTable.getCentralTypeTable().insert( metaClassEntry );
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceMethodHeader node, Object data)
  { 
  	UCSYInterface theInterface = null;
  	UCSYMetaClass theMetaClass = null ;
  	
  	
  	String parent = "";
  	if( data instanceof UCSYInterface)
  	{
  		theInterface = (UCSYInterface)data;
  		parent = theInterface.interfaceName;
  	}
  	else if( data instanceof UCSYMetaClass)
  	{
  		theMetaClass = (UCSYMetaClass) data;
  		parent = theMetaClass.metaClassName;
  	}
  	
  	node.returnType.jjtAccept(this, data);
   	
  	node.formalParameters.jjtAccept( this, data);
  	
  	node.interfaceMethodName = node.t.image;
  	node.lineNo                   = node.t.beginLine;
  	node.interfaceMethodSignature = node.formalParameters.formalParameterListName;
  	node.interfaceMethodReturnType= node.returnType.typeName;
  
  	
  	node.interfaceMethod = new InterfaceMethod(UCSYClassAttribute.PUBLIC | UCSYClassAttribute.ABSTRACT,node.interfaceMethodName,node.interfaceMethodSignature,node.interfaceMethodReturnType);
  	
  	if( node.interfaceMethod.methodName.charAt(0) != Character.toLowerCase(node.interfaceMethod.methodName.charAt(0)))
  	{
  		reportSSError(node.lineNo,"All member name including interface method must begin with a lower case letter ");
  	}
  	else
  	{
  		ArrayList<String> sig;
  		if( theInterface != null)
  		{
  			sig = theInterface.getSignaturesOfTheMethod( node.interfaceMethod.methodName);
  		}
  		else
  		{
  			sig = theMetaClass.getSignaturesOfTheMethod( node.interfaceMethod.methodName);
  		}
  		boolean duplicate = false;
  		for (int i = 0; i<sig.size(); i++)
  		{
  			String s = sig.get(i);
  			if(s.equals( node.interfaceMethod.methodSignature))
  			{
  				duplicate = true;
  				reportSSError(node.lineNo," duplicate methods are not allowed "+ node.interfaceMethod.methodName + " is already defined as a method in class "+ parent);
  			}
  	
  		}
  		if(!duplicate)
  		{
  		
  			if( theInterface != null)
  			{
  				theInterface.addInterfaceMethod( node.interfaceMethod);
  			}
  			else 
  			{
  				theMetaClass.addInterfaceMethod( node.interfaceMethod );
  			}		
  		}
  	}
  
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMetaClassHeader node, Object data)
  {
  	UCSYMetaClass theMetaClass = (UCSYMetaClass)data;
  	
  	theMetaClass.metaClassName = node.t.image;
  	node.lineNo                = node.t.beginLine;
  	if(  node.parent != null)
  	{
  		theMetaClass.parentClassName = node.parent.image;
  	}
  	else
  	{
  		theMetaClass.parentClassName = "Object";
  	}
  	//Default class Name is set by UCSY.jjt jjtree specification
  	//theMetaClass.defaultClassName 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMetaClassBody node, Object data)
  { 
  	UCSYMetaClass theMetaClass = (UCSYMetaClass) data;
  	for (int i = 0; i< node.abstractConstructorList.size(); i++)
  	{
  		ASTAbstractConstructor absConstructor = node.abstractConstructorList.get(i);
  		absConstructor.jjtAccept( this, theMetaClass );
  	}
  	for (int i = 0; i< node.interfaceMethodDeclarationList.size(); i++)
  	{
  		ASTInterfaceMethodDeclaration interfaceMethodDeclaration = node.interfaceMethodDeclarationList.get(i);
  		interfaceMethodDeclaration.jjtAccept( this, theMetaClass );
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAbstractConstructor node, Object data)
  { 
  	UCSYMetaClass theMetaClass = (UCSYMetaClass)data;
  	node.formalParameters.jjtAccept( this, data );
  	node.abstractConstructorSignature = node.formalParameters.formalParameterListName;
  	node.constructorName ="<init>";
  	node.constructorReturnType = "V";
  	
  	node.abstractConstructorMethod = new InterfaceMethod(UCSYClassAttribute.PUBLIC | UCSYClassAttribute.ABSTRACT,node.constructorName,node.abstractConstructorSignature,node.constructorReturnType);
  	node.lineNo = node.t.beginLine;
  		
  	
  	//============================Check duplicate method========================
  	ArrayList<String> sig = theMetaClass.getSignaturesOfTheMethod( node.constructorName );
  	boolean duplicate = false;
  	for (int i = 0; i<sig.size(); i++)
  	{
  		String s = sig.get(i);
  		if(s.equals( node.abstractConstructorSignature))
  		{
  			duplicate = true;
  			reportSSError(node.lineNo," duplicate constructor are not allowed in meta class");
  		}
  		
  	}
  	if(!duplicate)
  	{
  		
  		theMetaClass.addInterfaceMethod(node.abstractConstructorMethod );
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodDeclarationLookAhead node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFunctorDeclarationLookAhead node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAccessModifier node, Object data)
  { 
  	/*switch(node.modifier)
  	{
  		case UCSYConstants.PRIVATE:
  			node.accessModifier = UCSYClassAttribute.PRIVATE;
  		break;
  		
  		case UCSYConstants.PROTECTED:
  			node.accessModifier = UCSYClassAttribute.PROTECTED;
  		break;	
  		
  		case UCSYConstants.PUBLIC:
  			node.accessModifier = UCSYClassAttribute.PUBLIC;
  		break;
  	}
  	*/
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodModifier node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFieldModifier node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTConstructorDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass) data;
  	node.modifier |= theClass.memberModifier;
  	if( node.methodModifier != null)
  	{
  		node.methodModifier.jjtAccept( this, data);
  		node.modifier |= node.methodModifier.modifier;
  		node.isStatic = CentralTypeTable.isMethodStatic( node.modifier );
  		///inform("Yes do it "+node.isStatic);
  	}
  	node.constructorName = node.t.image;
  	
  	node.formalParameters.jjtAccept(this , data);
  	node.constructorSignature = node.formalParameters.formalParameterListName;
  	node.constructorReturnType = "v";
  	node.constructorProtocol = node.constructorSignature+ node.constructorReturnType;
  	
  	if( node.explicitConstructorCall != null)
  	{
  		node.explicitConstructorCall.jjtAccept( this, data);
  	}
  	for (int i = 0; i< node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept( this, data);
  	}
  	
  	//**************************************************************************
  	if( !node.constructorName.equals(theClass.className))
  	{
  		this.reportSSError(node.t.beginLine," Constructor Name must be same as Class Name , you misspelled as "+node.constructorName);
  		
  	}
  	if( node.isStatic )
  	{
  		node.constructorNameInByteCode = "<cinit>";
  	}
  	else
  	{
  		node.constructorNameInByteCode = "<init>";
  		if( UCSYClassAttribute.isSingleton(theClass.modifier))
  		{
  			reportSSError(node.lineNo," Singleton class cannot contain instance constructor");
  			return null;
  		}
  	}
  	//============================Check duplicate method========================
  	ArrayList<String> sig = theClass.getSignaturesOfTheMethod( node.constructorNameInByteCode );
  	boolean duplicate = false;
  	for (int i = 0; i<sig.size(); i++)
  	{
  		String s = sig.get(i);
  		if(s.equals( node.constructorSignature ))
  		{
  			duplicate = true;
  			reportSSError(node.t.beginLine," duplicate constructor are not allowed");
  		}
  		
  	}
  	if(!duplicate)
  	{
  		node.constructorMethod = new UCSYMethod(node.modifier, node.constructorNameInByteCode,node.constructorSignature,node.constructorReturnType);
  		theClass.addMethod( node.constructorMethod );
  	}
  	//**************************************************************************
  	return null; 
  }
  
  //****************************************************************************
   public Object visit(ASTExplicitConstructorCall node, Object data)
   {
   		node.arguments.jjtAccept(this,data);
   		
   		
   		return null;
   }
  //****************************************************************************
  public Object visit(ASTFieldDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass) data;
  	//System.out.println ("Field Delcaration pare pare");
  	if( node.functorDeclaration != null )
  	{
  		node.functorDeclaration.jjtAccept( this, theClass);
  	}
  	if( node.normalFieldDeclaration != null)
  	{
  		node.normalFieldDeclaration.jjtAccept( this, theClass);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNormalFieldDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  	for (int i = 0; i< node.fieldModifier.size(); i++)
  	{
  		ASTFieldModifier fModifier = node.fieldModifier.get(i);
  		fModifier.jjtAccept( this, theClass);
  		node.fieldAttribute |= fModifier.fieldModifier;
  		
  	}
  	node.type.jjtAccept( this, theClass );
  	node.filedTypeName = node.type.typeName;
  	node.fieldAttribute |= theClass.memberModifier;
  	
  	for (int i = 0; i< node.variableDeclarator.size(); i++)
  	{
  		ASTVariableDeclarator varDecl = node.variableDeclarator.get(i);
  		varDecl.jjtAccept( this, theClass);
  		
  		if( varDecl.variableName.charAt(0) == Character.toUpperCase(varDecl.variableName.charAt(0)))
  		{
  			reportSSError(varDecl.t.beginLine, " field name must start with lower case letter");
  		}
  		else if( theClass.isAField( varDecl.variableName))
  		{
  			reportSSError( varDecl.t.beginLine," Duplicate filed declaration, "+ varDecl.variableName+ " is already defined as field in "+theClass.className);
  		}
  		else if( theClass.isAMethod( varDecl.variableName))
  		{
  			reportSSError( varDecl.t.beginLine," you cannot declare field as same name with method, "+ varDecl.variableName+ " is already defined as method in "+ theClass.className);
  		}
  		else
  		{
  			node.normalField = new UCSYField(node.fieldAttribute,varDecl.variableName, node.filedTypeName );
  			///inform("PRocess field modifier "+ UCSYClassAttribute.getTextualRep(node.fieldAttribute));
  			theClass.addField( node.normalField );
  		}	
   	}
  	
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDelegateDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	node.delegateName = node.t.image;
  	node.lineNo = node.t.beginLine;
  	node.memberModifier |= theClass.memberModifier;
  	
  	node.type.jjtAccept(this, data);
  	
  	node.delegateTypeName = node.type.typeName;
  	
  	node.delegateClause.lineNo = node.lineNo;
  	
  	node.delegateClause.delegateName     = node.delegateName;
  	node.delegateClause.delegateTypeName = node.delegateTypeName;
	node.delegateClause.jjtAccept( this, data);
  	
  	
  	if( node.delegateName.charAt(0) == Character.toUpperCase(node.delegateName.charAt(0)))
  	{
  		reportSSError( node.lineNo, " delegate field name must start with lower case letter");
  	}
  	else if( theClass.isAField( node.delegateName ))
  	{
  		reportSSError( node.lineNo ," Duplicate filed declaration, "+ node.delegateName+ " is already defined as field in class "+theClass.className);
  	}
  	else if( theClass.isAMethod( node.delegateName))
  	{
  		reportSSError( node.lineNo," you cannot declare delegate field as same name with method, "+ node.delegateName+ " is already defined as method in"+theClass.className);
  	}
  	else
  	{
  		node.delegateField = new UCSYField(node.memberModifier,node.delegateName, node.delegateTypeName );
  		theClass.addField( node.delegateField );
  		node.delegateClause.delegateField = node.delegateField;
  	}	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTHandleAllClause node, Object data)
  {
  		
  		return null;
  }
  //****************************************************************************
  public Object visit(ASTForwardsClause node, Object data)
  {
  		for (int i = 0; i< node.signatureToCallList.size(); i++)
  		{
  			ASTSignatureToCall sToCall = node.signatureToCallList.get(i);
  			sToCall.jjtAccept( this, data );
  		}
  		return null;
  }
  //****************************************************************************
  public Object visit(ASTDecoratesClause node, Object data)
  {
  		for (int i = 0; i< node.beforeCallOrAfterCallList.size(); i++)
  		{
  			ASTBeforeCallOrAfterCall beforeCallOrAfterCall = node.beforeCallOrAfterCallList.get(i);
  			beforeCallOrAfterCall.jjtAccept(this,data);
  		}
  		return null;
  }

  //****************************************************************************
  public Object visit(ASTBeforeCallOrAfterCall node, Object data)
  {
  	node.beforeOrAfter.jjtAccept(this, data);
  	///inform("Process before or after call");
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAfterCall node, Object data)
  {
  		inform("Process after call");
  		UCSYClass theClass = (UCSYClass) data;
  		node.methodSignature.jjtAccept( this, data);
  		node.methodCall.jjtAccept( this, data);
  		
  		node.lineNo = node.methodSignature.lineNo;
  		
  		/***********************************************************************
  		* Notes return type of the method must be supplied with those of the calling methods 
  		* currently assumed to be void V
  		* later , must consult class file to find out the return type of the calling method
  		
  		************************************************************************/
  		node.method = new UCSYMethod( UCSYClassAttribute.PUBLIC, node.methodSignature.methodName, node.methodSignature.methodSignature,"V");
  		//**************************** Insert method *******************************
  		if( node.method.methodName.charAt(0) != Character.toLowerCase(node.method.methodName.charAt(0)))
  		{
  			reportSSError(node.lineNo,"All member name including abstract method must begin with a lower case letter ");
  		}
  		if( theClass.isAField( node.method.methodName ))
  		{	
  			reportSSError( node.lineNo, node.method.methodName+" is already defined as field in class "+ theClass.className);
  		}
  		else
  		{
  			ArrayList<String> sig = theClass.getSignaturesOfTheMethod( node.method.methodName);
  			boolean duplicate = false;
  			for (int i = 0; i<sig.size(); i++)
  			{
  				String s = sig.get(i);
  				if(s.equals( node.method.methodSignature))
  				{
  					duplicate = true;
  					reportSSError(node.lineNo," duplicate methods are not allowed "+ node.method.methodName + " is already defined as a method in class "+ theClass.className);
  				}
  		
  			}
  			if(!duplicate)
  			{
  			
  				theClass.addMethod( node.method );
  			}
  		}
  
  		return null;
  }
  //****************************************************************************
  public Object visit(ASTBeforeCall node, Object data)
  {
  		UCSYClass theClass = (UCSYClass) data;
  		
  		node.methodSignature.jjtAccept( this, data);
  		node.methodCall.jjtAccept( this, data);
  		
  		node.lineNo = node.methodSignature.lineNo;
  		
  		/***********************************************************************
  		* Notes return type of the method must be supplied with those of the calling methods 
  		* currently assumed to be void V
  		* later , must consult class file to find out the return type of the calling method
  		
  		************************************************************************/
  	
  		node.method = new UCSYMethod( UCSYClassAttribute.PUBLIC, node.methodSignature.methodName, node.methodSignature.methodSignature,"V");
  		inform("Before "+ node.method.methodName + " "+ node.method.methodSignature);
  		//**************************** Insert method *******************************
  		if( node.method.methodName.charAt(0) != Character.toLowerCase(node.method.methodName.charAt(0)))
  		{
  			reportSSError(node.lineNo,"All member name including abstract method must begin with a lower case letter ");
  		}
  		if( theClass.isAField( node.method.methodName ))
  		{	
  			reportSSError( node.lineNo, node.method.methodName+" is already defined as field in class "+ theClass.className);
  		}
  		else
  		{
  			ArrayList<String> sig = theClass.getSignaturesOfTheMethod( node.method.methodName);
  			boolean duplicate = false;
  			for (int i = 0; i<sig.size(); i++)
  			{
  				String s = sig.get(i);
  				if(s.equals( node.method.methodSignature))
  				{
  					duplicate = true;
  					reportSSError(node.lineNo," duplicate methods are not allowed "+ node.method.methodName + " is already defined as a method in class "+ theClass.className);
  				}
  		
  			}
  			if(!duplicate)
  			{
  			
  				theClass.addMethod( node.method );
  			}
  		}
  		return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodSignature node, Object data)
  { 
  	node.methodName = node.t.image;
  	node.lineNo = node.t.beginLine;
  	node.formalParameters.jjtAccept( this, data);
  	node.methodSignature = node.formalParameters.formalParameterListName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodCall node, Object data)
  { 
  	node.methodName = node.t.image;
  	node.lineNo = node.t.beginLine;
  	node.arguments.jjtAccept( this, data);
  	node.methodSignature = node.arguments.typeName;
  	///Debug.inform("We visited method Call(************* "+ node.methodSignature);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTVariableDeclarator node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	node.variableDeclaratorId.jjtAccept( this, theClass);
  	node.t = node.variableDeclaratorId.t;
  	node.lineNo = node.variableDeclaratorId.lineNo;
  	if(node.variableInitializer != null)
  	{
  		///inform("Process ");
  		node.variableInitializer.jjtAccept( this, theClass);
  	}
  	node.variableName = node.variableDeclaratorId.variableName;
  	
  	
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTVariableDeclaratorId node, Object data)
  { 
  	
  	node.variableName = node.t.image;
  	node.lineNo = node.t.beginLine;
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTVariableInitializer node, Object data)
  { 	
  	node.expression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArrayInitializer node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	node.methodHeader.jjtAccept( this, data);
  	
  	node.methodName = node.methodHeader.methodName;
  	node.modifier  |= theClass.memberModifier;
  	
  	node.modifier  |= node.methodHeader.methodAttribute;
  	node.methodSignature = node.methodHeader.methodSignature ;
  	node.methodReturnType = node.methodHeader.methodReturnType;
  	node.methodProtocol = node.methodSignature + node.methodReturnType;
  	node.lineNo = node.methodHeader.t.beginLine;
  	
  	
  	///inform("Method modifier "+ node.methodName + " sig "+ node.methodSignature+" "+node.methodReturnType+( (node.modifier & UCSYClassAttribute.PUBLIC) == UCSYClassAttribute.PUBLIC));
  	//**************** Insert Method *******************************************
  	node.method = new UCSYMethod(node.modifier, node.methodName,node.methodSignature,node.methodReturnType);
  	//Check dupliacate filed
  	if( node.methodName.charAt(0) == Character.toUpperCase(node.methodName.charAt(0)))
  	{
  		reportSSError(node.lineNo, " all member name including method must starts with a lower case letter,you provided as "+node.methodName);
  	}
  	else if(theClass.isAField( node.methodName))
  	{
  		reportSSError(node.lineNo, " method name cannot be same as field name, you already defined "+ node.methodName +" as field ");
  	}
  	else
  	{ 
  		ArrayList<String> sig = theClass.getSignaturesOfTheMethod( node.methodName);
  		boolean duplicate = false;
  		for (int i = 0; i<sig.size(); i++)
  		{
  			String s = sig.get(i);
  			if(s.equals( node.methodSignature))
  			{
  				duplicate = true;
  				reportSSError(node.lineNo," duplicate methods are not allowed,method "+ node.methodName+ " is already defined in class "+ theClass.className);
  			}
  		
  		}
  		if(!duplicate)
  		{
  			node.method = new UCSYMethod(node.modifier, node.methodName,node.methodSignature,node.methodReturnType);
  			theClass.addMethod( node.method );
  		}
  	}
  	node.block.jjtAccept( this, data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodHeader node, Object data)
  { 
  	for (int i = 0; i< node.modifierList.size(); i++)
  	{
  		ASTMethodModifier m = node.modifierList.get(i);
  		node.methodAttribute |= m.modifier;
  	}
  	
  	node.returnType.jjtAccept( this,data);
  	node.formalParameters.jjtAccept(this,data);
  	
  	node.methodName = node.t.image;
  	node.methodSignature = node.formalParameters.formalParameterListName ;
  	node.methodReturnType = node.returnType.typeName;
  	
  	///Debug.inform( "sig "+ node.methodSignature+" ret  "+ node.methodReturnType);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFormalParameters node, Object data)
  { 
  	node.formalParameterListName = "(";
  	for (int i = 0; i< node.formalParameterList.size(); i++)
  	{
  		ASTFormalParameter formalParameter = node.formalParameterList.get(i);
  		formalParameter.jjtAccept( this, data);
  		
  		node.formalParameterListName += formalParameter.typeName+",";
  		
  	}
  	node.formalParameterListName +=")";
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFormalParameter node, Object data)
  { 
  	node.mainType.jjtAccept(this,data);
  	
  	node.typeName = node.mainType.typeName;
  	if(node.subType != null)
  	{
  		node.subType.jjtAccept(this,data);
  		node.typeName += ":"+ node.subType.typeName;
  	}
  	node.variableDeclaratorId.jjtAccept( this, data);
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTReturnType node, Object data)
  { 
  	
  	if(node.voidType != null)
  	{
  		//System.out.println ("Why not");
  		node.voidType.jjtAccept(this,data);
  		node.typeName = node.voidType.typeName;
  	}
  	if(node.type != null)
  	{
  		node.type.jjtAccept( this, data);
  		node.typeName = node.type.typeName;
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTVoidType node, Object data)
  { 
  	node.typeName = "v";
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTType node, Object data)
  { 
  	if(node.primitiveType != null)
  	{
  		node.primitiveType.jjtAccept(this, data);
  		node.lineNo = node.primitiveType.lineNo;
  		node.typeName = node.primitiveType.typeName;
  	}
  	if( node.referenceType != null)
  	{
  		node.referenceType.jjtAccept(this,data);
  		node.lineNo = node.referenceType.lineNo;
  		node.typeName = node.referenceType.typeName;
  	}
  	//System.out.println ("Type "+ node.typeName);	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimitiveType node, Object data)
  { 
  	switch(node.primitiveType)
  	{
  		case UCSYConstants.BOOLEAN:
  			node.typeName = "t";
  		break;
  		case UCSYConstants.CHAR:
  			node.typeName = "c";
  		break;
  		case UCSYConstants.BYTE:
  			node.typeName = "b";
  		break;
  		case UCSYConstants.SHORT:
  			node.typeName = "s";
  		break;
  		case UCSYConstants.INTEGER:
  			node.typeName = "i";
  		break;
  		case UCSYConstants.LONG :
  			node.typeName = "l";
  		break;
  		case UCSYConstants.FLOAT:
  			node.typeName = "f";
  		break;
  		case UCSYConstants.DOUBLE:	
  			node.typeName = "d";
  		break;
  		case UCSYConstants.STRING:
  			node.typeName = "m";
  		break;
  	}
  	//inform("Get THere");
  	node.lineNo = node.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTReferenceType node, Object data)
  { 
  	if(node.metaInstanceType != null)
  	{
  		node.metaInstanceType.jjtAccept( this, data );
  		node.lineNo = node.metaInstanceType.lineNo;
  		node.typeName = node.metaInstanceType.metaInstanceTypeName;
  	}
  	if(node.arrayType != null)
  	{
  		node.arrayType.jjtAccept( this, data);
  		node.lineNo =node.arrayType.lineNo;
  		node.typeName = node.arrayType.typeName;
  	}
  	if(node.classType != null)
  	{	
  		node.classType.jjtAccept( this, data);
  		node.lineNo = node.classType.lineNo;
  		node.typeName = node.classType.classTypeName;
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMetaInstanceType node, Object data)
  { 
  	node.metaInstanceTypeName = node.t.image;
  	node.lineNo = node.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTClassType node, Object data)
  { 
  	node.classTypeName =  node.t.image;
  	node.lineNo = node.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArrayType node, Object data)
  { 
  	String elementTypeName;
  	if(node.primitiveType !=null)
  	{
  		node.primitiveType.jjtAccept( this, data);
  		node.elementTypeName = node.primitiveType.typeName;
  	}
  	if(node.classType != null)
  	{
  		node.classType.jjtAccept( this, data );
  		node.elementTypeName = node.classType.classTypeName;
  	}
  	for (int i = 0; i< node.dimensions; i++)	
  	{
  		node.typeName +="[";
  	}
  	node.typeName += node.elementTypeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTResultType node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTExpression node, Object data)
  { 
  	node.exp = (ParentExpression)node.jjtGetChild(0);
  	node.exp.jjtAccept(this,data);
  	node.lineNo = node.exp.lineNo;
  	//inform("Process Expression in collect type ");
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTOrNode node, Object data)
  { 
  	node.opOne =(ParentExpression) node.jjtGetChild(0);
  	node.opTwo = (ParentExpression)node.jjtGetChild(1);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAndNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTEqualNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNotEqualNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceOfExpression node, Object data)
  {
  	node.opOne.jjtAccept(this,data);
  	node.type.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceOfExpressionNode node, Object data)
  {
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTGTNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTGTEqualNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLTNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLTEqualNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPlusNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSubtractNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMultiplyNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDivsionNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTModulusNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTUnaryPlusNode node, Object data)
  {
  	node.opOne.jjtAccept(this,data); 
  	node.typeName = node.opOne.typeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTUnaryMinusNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.typeName = node.opOne.typeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNotNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.typeName = node.opOne.typeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCastNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
  	node.typeName = node.opOne.typeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCastLookAhead node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCastExpression node, Object data)
  { 
  	node.type.jjtAccept(this,data);
  	node.expression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimaryExpression node, Object data)
  {
  	node.primaryExpression = (ParentName)node.jjtGetChild(0); 
  	
  	node.primaryExpression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimaryPrefix node, Object data)
  { 
  	node.primaryPrefix = (ParentName)node.jjtGetChild(0);
  	node.primaryPrefix.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimarySuffix node, Object data)
  { 
  	node.firstPart = (ParentName)node.jjtGetChild(0);
  	node.secondPart = (ParentName)node.jjtGetChild(1);
  	
  	node.firstPart.jjtAccept(this,data);
  	node.secondPart.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTName node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTIntegerLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFloatLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCharLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTStringLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDoubleLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLongLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTBooleanLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNullLiteral node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAllocationExpression node, Object data)
  { 
  	//node.allocationNode = (ParentExpression)node.jjtGetChild(0);
  	node.allocationNode.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArrayAllocation node, Object data)
  { 
  	//node.arrayAllocationNode = (ParentExpression)node.jjtGetChild(0);
  	node.arrayAllocationNode.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceArrayAllocation node, Object data)
  { 
  	node.name.jjtAccept(this,data);
  	node.dimensionParameter.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDimensionParameter node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimitiveArrayAllocation node, Object data)
  { 
  	node.primitiveType.jjtAccept(this,data);
  	node.dimensionParameter.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceAllocation node, Object data)
  { 
  	node.name.jjtAccept(this,data);
  	node.arguments.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArguments node, Object data)
  { 
  	///Debug.inform("WaWa");
  	//if(node.argumentsList !=null)
  	
  	String argType="" ;
  	if(node.argumentsList != null)
  	{
  		node.argumentsList.jjtAccept(this,data);
  		argType = node.argumentsList.typeName;
  	}
  	node.typeName = "("+ argType + ")";
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArgumentsList node, Object data)
  { 
  	for (int i = 0; i<node.expressionList.size(); i++)
  	{
  		ASTExpression exp = node.expressionList.get(i);
  		exp.jjtAccept(this,data);
  		
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTStatement node, Object data)
  { 
  	node.pStatement.jjtAccept(this,data);
  	return null;
  }
  public Object visit(ASTLabelStatement node, Object data)
  { 
  	node.block.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAssignmentStatement node, Object data)
  { 
  	///inform("Process Assignments");
  	node.primaryExpression.jjtAccept(this,data);
  	node.assignmentOperator.jjtAccept(this,data);
  	node.expression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAssignmentStatementLookAhead node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAssignmentOperator node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTBlock node, Object data)
  { 
  	for (int i = 0; i< node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept( this, data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodCallStatement node, Object data)
  { 
  	node.primaryExpression.jjtAccept(this,data);
  	node.lineNo = node.primaryExpression.lineNo;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTEmptyStatement node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSwitchStatement node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	for (int i = 0; i< node.caseStatementList.size(); i++)
  	{
  		ASTCaseStatement caseStatement = node.caseStatementList.get(i);
  		caseStatement.jjtAccept(this,data);
  	}
  	if( node.defaultStatement != null)
  	{
  		node.defaultStatement.jjtAccept(this, data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCaseStatement node, Object data)
  { 
  	node.caseExpression.jjtAccept(this,data);
  	for (int i = 0; i<node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCaseExpression node, Object data)
  { 
  	
  	node.jjtGetChild(0).jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCaseIdentifier node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDefaultStatement node, Object data)
  { 
  	for (int i = 0; i<node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTIfStatement node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	node.thenPartStatement.jjtAccept(this,data);
  	
  	if(node.elsePartStatement != null)
  	{
  		node.elsePartStatement.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTWhileStatement node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	node.statement.jjtAccept(this,data);
  	node.lineNo = node.expression.lineNo;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDoStatement node, Object data)
  { 
  	node.statement.jjtAccept(this,data);
  	node.expression.jjtAccept(this,data);
  	node.lineNo = node.statement.lineNo;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTForStatement node, Object data)
  { 
  	node.forInitializer.jjtAccept(this,data);
  	node.forExpression.jjtAccept(this,data);
  	node.forUpdator.jjtAccept(this,data);
  	node.statement.jjtAccept(this,data);
  	
  	node.lineNo = node.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTForInitializer node, Object data)
  { 
  	if(node.forInit != null)
  	{
  		node.forInit.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTForExpression node, Object data)
  { 
  	if(node.expression != null)
  	{
  		node.expression.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTForUpdator node, Object data)
  { 
  	if(node.forUpdate != null)
  	{
  		node.forUpdate.jjtAccept(this,data);
  	}
  	return null;
  	
  }
  //****************************************************************************
  public Object visit(ASTForInit node, Object data)
  { 
  	node.localVariableDeclaration.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLocalVariableDeclaration node, Object data)
  { 
  	node.type.jjtAccept(this, data);
  	
  	
  	for (int i = 0; i< node.variableDeclaratorList.size(); i++)
  	{
  		ASTVariableDeclarator variableDeclarator = node.variableDeclaratorList.get(i);
  		variableDeclarator.jjtAccept(this,data);
  		node.lineNo = variableDeclarator.lineNo;
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTForUpdate node, Object data)
  {
  	node.assignmentStatement.jjtAccept(this,data);
  	 return null;
  }
  //****************************************************************************
  public Object visit(ASTReturnStatement node, Object data)
  { 
  	if(node.expression != null)
  	{
  		node.expression.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTContinueStatement node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTTryStatement node, Object data)
  { 
  	node.block.jjtAccept(this,data);
  	for (int i = 0; i< node.catchStatementList.size(); i++)
  	{
  		ASTCatchStatement catchStatement = node.catchStatementList.get(i);
  		catchStatement.jjtAccept(this,data);
  	}
  	
  	if(node.finallyBlock != null)
  	{
  		node.finallyBlock.jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCatchStatement node, Object data)
  { 
  	node.formalParameters.jjtAccept(this,data);
  	node.block.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTThrowStatement node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTBreakStatement node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTRebindStatement node, Object data)
  { 
  	//node.sourceExpression.jjtAccept(this,data);
  	node.rebindClassName = node.rebindClassToken.image;
  	node.rebindMethodName = node.rebindMethodNameToken.image;
  	node.destinationExpression.jjtAccept(this,data);
  	///Debug.inform("To rebind method "+node.rebindMethodName+" of "+node.rebindClassName);
  	node.lineNo = node.rebindClassToken.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSuffixDotIdentifierNode node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTIdentifier node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	node.idName = node.t.image;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArrayExpressionPrimarySuffix node, Object data)
  { 
  	return null;
  }
  
  
  //****************************************************************************
  public Object visit(ASTArgumentsSuffix node, Object data)
  { 
  	node.arguments = (ASTArguments)node.jjtGetChild(0);
  	node.arguments.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNamePrimaryPrefix node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  
  public Object visit(ASTAllocationPrimaryPrefix node, Object data)
  { 
  
  	node.allocationExpression = (ASTAllocationExpression)node.jjtGetChild(0);
  	node.allocationExpression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTParenthesePrimaryPrefix node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSuperPrimaryPrefix node, Object data)
  { 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTThisPrimaryPrefix node, Object data)
  { 
  	return null;
  } 
  //****************************************************************************
  public Object visit(ASTLiteralPrimaryPrefix node, Object data)
  { 
  	node.literalNode =(ParentExpression) node.jjtGetChild(0);
  	node.literalNode.jjtAccept(this,data);
  	
  	node.typeName = node.literalNode.typeName;
  	return null;
  }
  
  public Object visit(ASTExecuteTimesStatement node,Object data)
  {
  	///Debug.inform("I got Execute Times Statement ");
  	node.expression.jjtAccept(this,data);
  	node.statementBody.jjtAccept(this,data);
  	node.lineNo = node.t.beginLine;
  	return null;
  }
}