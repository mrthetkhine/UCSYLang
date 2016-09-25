import java.util.*;

class TypeCheckVisitor implements UCSYVisitor
{
	UCSY parser ;
	
	TypeCheckVisitor(UCSY p)
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
  	
  	CommonInheritedAttribute.currentType = node.theClass ;
  	SymbolTable.getSymbolTable().openScope();
  	/***************************************************************************
  	 * add this and super as special variable to symbol table
  	 ***************************************************************************/
  	 
  	try
  	{ 
  		SymbolTableEntry thisEntry = new SymbolTableEntry("this", node.theClass.className);
  		SymbolTable.getSymbolTable().insert( thisEntry );
  	
  		SymbolTableEntry superEntry = new SymbolTableEntry("super",node.theClass.parentClassName);
  		SymbolTable.getSymbolTable().insert( superEntry ) ;
  	}
  	catch(Exception e)
  	{
  		
  	}
  
  	node.classHeader.jjtAccept(this, node.theClass);
  	node.classBody.jjtAccept(this, node.theClass );
  	
  	SymbolTable.getSymbolTable().closeScope();
  	
  	node.lineNo = node.classHeader.lineNo;
  	
  	//Check to see a class override all of the method it have to be override
  	// Including method of abstract class, and interface including all abstract method of
  	//its ancestors
  	if(! node.theClass.isAbstract())
  	{
  		ArrayList<MethodToOverride> toOverrideMethod = null ;
  		try
  		{
  			toOverrideMethod = node.theClass.getAllAbstractMetodOfAncestors();
  		}
  		catch(NotExceptedTypeException e)
  		{
  			reportSSError(node.lineNo, e.exceptedTypeName +" is not excepted to be of "+ e.foundTypeName);
  			return null;
  		}
  		catch(TypeNotFoundException e)
  		{
  			if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  			{
  				CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  				reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  			}
  			
  			return null;
  		}
  		catch(Exception e )
  		{
  		}
  		for (int i = 0; i< toOverrideMethod.size(); i++)
  		{
  			MethodToOverride met = toOverrideMethod.get(i);
  			
  			try
  			{
  				if(! node.theClass.isOverrideAbstractMethod( met ))
  				{
  					reportSSError(node.lineNo," class "+ node.theClass.className+" is not abstract and does not override "+ met.methodName+" of type "+ met.parentType);
  				//inform(UCSYClassAttribute.getTextualRep( met.modifier ));
  				}
  			}
  			catch(MissingOverrideModifierException e)
  			{
  				reportSSError(node.lineNo," method "+ met.methodName +" is override but missing override modifier");
  			}
  			catch(WeakerAccessSpecifierException e)
  			{
  				reportSSError(node.lineNo," method "+ met.methodName +" is override but provide a weaker access modifier");
  			}
  			catch(IncorrectMethodReturnTypeException e)
  			{
  				reportSSError(node.lineNo," method "+ met.methodName +" is override but provide wrong return type ");
  			}
  		}
  	}
  	
  	/***************************************************************************
  	 * 		Check Cyclic Inherirachy
  	 ***************************************************************************/
  	 ArrayList<UCSYClass> allParentClass = null;
  	 try
  	 {
  	 	allParentClass = node.theClass.getAllParent();
	 }
	 catch(CyclicInheritanceException e)
	 {
	 	reportSSError(node.lineNo,"Cyclic Inheritance involving "+ e.cyclicType);
	 	return null;
	 }
	 catch(Exception e)
	 {
	 	
	 	return null;
	 }
		
  	 for (int i = 0; i< allParentClass.size(); i++)
  	 {
  	 	UCSYClass parentClass = allParentClass.get(i);
  	 	if( node.theClass.className.equals( parentClass.className ))
  	 	{
  	 		reportSSError(node.lineNo," Cyclic inheritance involving " + node.theClass.className);
  	 		return null;
  	 	}
  	 }
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTClassHeader node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	
  	node.lineNo = node.t.beginLine;
  	if(node.inheritsClause != null)
  	{
  		//theClass.parentClassName = node.inheritNode.p
  		node.inheritsClause.jjtAccept( this, theClass );
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
  	for (int i = 0; i< node.interfaceList.size(); i++)
  	{
  		String interfaceName = node.interfaceList.get( i );
  		if(! CentralTypeTable.isInterface( interfaceName ) )
  		{
  			reportSSError(node.lineNo, " interface required in implemented clause "+ interfaceName +" is not an interface in "+theClass.className + " class declaration");
  			return null;
  		}
  	}
  
	//Non abstract class must implements all method of their interface and parent of their interface
	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTConformsClause node, Object data)
  {
  	//Check to see class confroms method of all of its meta class
  	UCSYClass theClass = (UCSYClass) data;
  	
  	for (int i = 0; i< theClass.conformList.size(); i++)
  	{
  		String metaClassName = theClass.conformList.get(i);
  		
  		if( !CentralTypeTable.isMetaClass( metaClassName ))
  		{
  			reportSSError(node.lineNo, " meta type required in conforms clause "+ metaClassName +" is not a meta type");
  			
  		}
  		else
  		{
  			UCSYMetaClass theMetaClass;
  			ArrayList<UCSYMetaClass> allParent = null;
  			try
  			{
  				theMetaClass = CentralTypeTable.getCentralTypeTable().getAMetaClass( metaClassName );
  			
  			//Get meta class and all of its parent
  				allParent = theMetaClass.getAllParent();
  				allParent.add( theMetaClass );
  			}
  			catch(NotExceptedTypeException e)
  			{
  				reportSSError(node.lineNo," required meta type but found "+ e.foundTypeName);
  				return null;
  			}
  			catch(TypeNotFoundException e)
  			{
  				if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  				{
  					CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  					reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  				}
  				return null;
  			}
  			
  			for (int k = 0; k < allParent.size() ; k++)
  			{
  				theMetaClass = allParent.get(k);
  				
  				for (int j = 0; j< theMetaClass.methodList.size(); j++)
  				{
  					Method metaClassMethod = theMetaClass.methodList.get( j );
  				
  					///inform("Meta method "+metaClassMethod.methodName+ " "+ metaClassMethod.methodSignature + " "+ metaClassMethod.methodReturnType );
  					if(! theClass.isConformsMetaClassMethod( metaClassMethod ))
  					{
  						reportSSError(node.lineNo," class "+ theClass.className +"does not conforms method "+ metaClassMethod.methodName +" of meta class "+ theMetaClass.metaClassName );
  					}
  				}
  			}
  		}
  	
  	
  	}
  return null;
}
  //****************************************************************************
  public Object visit(ASTInheritsClause node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	if( theClass.parentClassName.equals("Object"))
  	{
  	}
  	else
  	{
  		if( !CentralTypeTable.isClass( theClass.parentClassName ))
  		{
  			reportSSError( node.lineNo, " class name expected in inherits clause of class "+theClass.className+" declaration");
  		}
  		else
  		{
  			UCSYClass parentClass = null;
  			try
  			{
  				parentClass =  CentralTypeTable.getCentralTypeTable().getAClass( theClass.parentClassName );
  			}
  			catch(NotExceptedTypeException e)
  			{
  				reportSSError(node.lineNo, e.exceptedTypeName+" is excepted to be class but found" + e.foundTypeName);
  				return null;
  			}
  			catch(TypeNotFoundException e)
  			{
  				if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  				{	
  					CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  					reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  				}
  				return null;
  			}
  			//inform(" parent class modifier "+( parentClass.modifier & UCSYClassAttribute.FINAL ));
  			if( parentClass.isFreeClass )
  			{
  				reportSSError(node.lineNo," you cannot inherits from free classs");
  			}
  			else if( parentClass.isFinal())
  			{
  				reportSSError(node.lineNo, " you cannot inherits from final class");
  			}
  			
  		}
  		
  	}
  	
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAdaptsClause node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  		
  	for (int i = 0; i< node.signatureToCall.size(); i++)
  	{
  		ASTSignatureToCall sToCallNode = node.signatureToCall.get(i);
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
  	node.methodCall.methodSignature = node.methodCall.arguments.typeName;
  	
  	/***********************************************************************
  	* Notes return type of the method must be supplied with those of the calling methods 
  	* currently assumed to be void V
  	* later , must consult class file to find out the return type of the calling method
  		
  	************************************************************************/
  	try
  	{
  		if( theClass.isAdapterClass )//It is used in adaptsClasuse, otherwise it is used in delegate forwards and decorates clause
  		{
  			String parentClassName = theClass.adaptInterface;
  			UCSYInterface parentClass = CentralTypeTable.getCentralTypeTable().getAInterface( parentClassName );
  			Method m = parentClass.getMethodOfNameAndSignature( node.signature.methodName,node.signature.methodSignature);
  			node.signature.methodReturnType = m.methodReturnType;
  			if( m != null)
  			{
  				node.method.methodReturnType = m.methodReturnType;
  			///inform("Yeah we do it "+ m.methodReturnType +" parent Class Name "+ parentClassName +" "+node.signature.methodName+" "+node.signature.methodSignature);
  			}
  			else
  			{
  				//There is no method with signature and method name  in interface 
  				reportSSError(node.lineNo, " Trying to adapts method " + node.signature.methodName + " with signature "+ node.method.methodSignature+" but not found corresponding method in interface " + parentClass.interfaceName);
  			}
  			
  			UCSYClass adaptClass = CentralTypeTable.getCentralTypeTable().getAClass( node.adaptClassName );
  			///Debug.inform( "Return type "+ node.signature.methodReturnType );
  			//Debug.inform("MethodCall Argument is " + node.methodCall.arguments.typeName );
  			node.methodToCall = (UCSYMethod)adaptClass.getBestOverloadedMethod(theClass.className, node.methodCall.methodName,node.methodCall.methodSignature);
  			
  			//Two method must be of same return type
  			///inform("Processing adapter");
  		}
  		else
  		{
  			//Delegate type can be class , meta class, interface ; ok fine
  			
  			try
  			{
  				String delegateTypeName = node.delegateClause.delegateTypeName;
  				Type theType = CentralTypeTable.getCentralTypeTable().getType( delegateTypeName );
  				
  				UCSYInterface interfaceDelegate = null;
  				UCSYClass     classDelegate     = null;
  				UCSYMetaClass metaClassDelegate = null;
  				
  				switch( theType.getType())
  				{
  					case CLASSTYPE:
  						classDelegate     = (UCSYClass)     theType;
  					break;
  					
  					case INTERFACETYPE:
  						interfaceDelegate = (UCSYInterface) theType;
  					break;
  					
  					case METATYPE:
  						metaClassDelegate = (UCSYMetaClass) theType;
  					break;
  					
  					default:
  						reportSSError(node.lineNo," only class, meta class and interface type are allowed but found "+theType.typeNameDescription);
  				}
  			}
  			catch(TypeNotFoundException e)
  			{
  				if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  				{	
  					CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  					reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  				}
  			}
  			///inform("processing delegate ");
  		}
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError( node.lineNo ,"required interface type but found "+ e.foundTypeName);
  	}
  	catch(CannotFindBestOverloadedMethodException e)
    {
        reportSSError(node.lineNo," Cannot find best overload method for "+ node.methodCall.methodName +" with signature "+ TypeCheckUtilityClass.getTypeDescriptionOfSignature(node.methodCall.methodSignature)+" in adapt clause method call part");
        return null;
    }
  	catch(TypeNotFoundException e)
  	{
  		if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  	}
  	//catch(CannnotFindBestOverload)
  	catch(Exception e)
  	{
  		parser.staticSemanticError = true;
  		//e.printStackTrace();
  	}
  	if( node.isUsedByAdaptClause )
  	{
  		
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTClassModifier node, Object data)
  { 
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
  	//Debug.inform("Free Class Declaration ");
  	//node.theFreeClass.isFreeClass = true;
  	node.freeClassHeader.jjtAccept( this,node.theFreeClass );
  	node.freeClassBody.jjtAccept( this, node.theFreeClass );
  	
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTFreeClassHeader node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFreeClassBody node, Object data)
  { 
  	for (int i = 0; i< node.methodDeclarationList.size(); i++)
  	{
  		ASTMethodDeclaration methodDecl = node.methodDeclarationList.get(i);
  		///Debug.inform("Method of free class "+methodDecl.methodName);
  		methodDecl.jjtAccept( this, data );
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMemberDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass) data;
  	
  	node.memberDeclaration.jjtAccept(this, theClass);
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTFunctorDeclaration node, Object data)
  { 
  	
  	//System.out.println ("Functor Type Prepare");
  	/*
  	node.returnType.jjtAccept( this, data );
  	node.formalParameters.jjtAccept( this, data);
  	
  	node.functorName = node.t.image;
  	node.functorSignature = node.formalParameters.formalParameterListName + node.returnType.typeName;
  	node.functorReturnType = node.returnType.typeName;
  	
  	
  	
  	node.functor                    = new FunctorType();
  	node.functor.typeName           = "#"+node.functorName ;
  	node.functor.functorName        = node.functorName;
  	node.functor.modifier           = node.modifier;
  	node.functor.functorSignature   = node.functorSignature;
  	node.functor.functorReturnType  = node.functorReturnType;
	//inform ("Functor "+ node.functorName + " "+ node.functorSignature);
	
	CentralTypeTableEntry entry = new CentralTypeTableEntry(node.functor.typeName,node.functor,true);
	CentralTypeTable.getCentralTypeTable().insert( entry );
	*/
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceDeclaration node, Object data)
  {
  	 
  	node.interfaceHeader.jjtAccept( this, node.theInterface);
  	node.interfaceBody.jjtAccept( this, node.theInterface);
  	node.lineNo = node.interfaceHeader.lineNo;
  	try
  	{
  		
  		node.theInterface.getAllParentInterface( node.theInterface.interfaceName);
  	}
  	catch(CyclicInheritanceException e)
  	{
  		reportSSError(node.lineNo,"Cyclic Inheritance found in Interface declaration involving "+e.cyclicType);
  	}
  	catch(Exception e)
  	{
  		
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAbstractMethodDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  
  	node.returnType.jjtAccept( this, data);
  	node.formalParameters.jjtAccept( this, data );
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceHeader node, Object data)
  { 
  	UCSYInterface theInterface = (UCSYInterface)data;
  	node.lineNo = node.t.beginLine;
  	
  	for (int i = 0; i< node.interfaceParentName.size(); i++)
  	{
  		String parentInterfaceName = node.interfaceParentName.get(i);
  		try
  		{
  			UCSYInterface parentInterface = CentralTypeTable.getCentralTypeTable().getAInterface( parentInterfaceName );
  		}
  		catch(TypeNotFoundException e)
  		{
  			if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  			{
  				CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  				reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  			}
  		}
  		catch(NotExceptedTypeException e)
  		{
  			reportSSError(node.lineNo ," Only interface type are allowed as parent in interface declaration but found "+ e.foundTypeName);
  		}
  		
  	}
  	
  	//Check all parent must be of interface type
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceBody node, Object data)
  { 
  	SymbolTable.getSymbolTable().openScope();
  	UCSYInterface theInterface = (UCSYInterface)data;
  	for (int i = 0; i<node.interfaceMethodDeclarationList.size(); i++)
  	{
  		ASTInterfaceMethodDeclaration dec = node.interfaceMethodDeclarationList.get(i);
  		dec.jjtAccept(this,theInterface);
  	}
  	SymbolTable.getSymbolTable().closeScope();
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
  	
  	
  	node.metaClassHeader.jjtAccept( this,node.theMetaClass );
  	node.metaClassBody.jjtAccept( this, node.theMetaClass );
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInterfaceMethodHeader node, Object data)
  { 
  	UCSYInterface theInterface = null;
  	UCSYMetaClass theMetaClass = null ;
  	
  	if( data instanceof UCSYInterface)
  	{
  		theInterface = (UCSYInterface)data;
  		
  	}
  	else if( data instanceof UCSYMetaClass)
  	{
  		theMetaClass = (UCSYMetaClass) data;
  		
  	}
  	
  	
  	
  	node.returnType.jjtAccept(this, data);
   	
  	node.formalParameters.jjtAccept( this, data);
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMetaClassHeader node, Object data)
  {
  	UCSYMetaClass theMetaClass = (UCSYMetaClass)data;
  	
  	boolean defaultClassConforms = true;
  	UCSYClass defaultClass = null ;
  	try
  	{
  		defaultClass = CentralTypeTable.getCentralTypeTable().getAClass( node.defaultClassName );
  		
  		if(defaultClass.isAbstract())
  		{
  			reportSSError( node.lineNo ," default class of meta class cannot be abstract class: only concreted type are allowed");
  		}
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError(node.lineNo, " default class must be a class type but found "+ e.foundTypeName);
  		return null;
  	}
  	catch(TypeNotFoundException e)
  	{
  		if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  		return null;
  	}
  	//Error Cannot cope with inheritance hierarchy , find all ancestor of the meta class hierarychy

  	ArrayList<UCSYMetaClass> allParent = null;
  	try
  	{
  		 			
  		//Get meta class and all of its parent
  		allParent = theMetaClass.getAllParent();
  		allParent.add( theMetaClass );
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError(node.lineNo," required meta type but found "+ e.foundTypeName);
  		return null;
  	}
  	catch(TypeNotFoundException e)
  	{
  		if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  		return null;
  	}
  			
  	for (int k = 0; k < allParent.size() ; k++)
  	{
  		theMetaClass = allParent.get(k);
  			
  		for (int j = 0; j< theMetaClass.methodList.size(); j++)
  		{
  			Method metaClassMethod = theMetaClass.methodList.get( j );
  				
  			if(! defaultClass.isConformsMetaClassMethod( metaClassMethod ))
  			{
  				reportSSError(node.lineNo," class "+ defaultClass.className +" does not conforms method "+ metaClassMethod.methodName +" of meta class "+ theMetaClass.metaClassName );
  			}
  		}
  	}

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
  	
  	CommonInheritedAttribute.currentMethod = node.constructorMethod;
  	SymbolTable.getSymbolTable().openScope();
  	if( node.methodModifier != null)
  	{
  		node.methodModifier.jjtAccept( this, data);
  		
  		///inform("Yes do it "+node.isStatic);
  	}
  	
  	
  	node.formalParameters.jjtAccept(this , data);
  	if( node.explicitConstructorCall != null)
  	{
  		node.explicitConstructorCall.jjtAccept( this, data);
  	}
  	for (int i = 0; i< node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept( this, data);
  	}
  	SymbolTable.getSymbolTable().closeScope();
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
  	//System.out.println ("Field Delcaration pre pare");
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
  		
  		
  	}
  	node.type.jjtAccept( this, theClass );
  	
  	VariableType.typeName = node.filedTypeName;
  	
  	for (int i = 0; i< node.variableDeclarator.size(); i++)
  	{
  		ASTVariableDeclarator varDecl = node.variableDeclarator.get(i);
  		varDecl.jjtAccept( this, data);
  		
  		
   	}
  	
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDelegateDeclaration node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	
  	
  	node.type.jjtAccept(this, data);
  	
  	
	node.delegateClause.jjtAccept( this, data);
  	
  		
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTHandleAllClause node, Object data)
  {
  		UCSYClass theClass = (UCSYClass)data;
  		String delegateTypeName = node.delegateTypeName;//.substring(1,node.delegateTypeName.length());
  		try
  		{
  			//Delegate type can be class, interface or meta class
  			Type type = CentralTypeTable.getCentralTypeTable().getType( delegateTypeName );
  			UCSYClass classType ;
  			UCSYMetaClass metaClassType;
  			UCSYInterface interfaceType;
  			
  			ArrayList<Method> allPublicMethod = new ArrayList<Method>();
  			switch( type.getType() )
  			{
  				case CLASSTYPE:
  					classType = (UCSYClass) type;
  					allPublicMethod = classType.getAllPublicMethod();
  					
  				break;
  				
  				case INTERFACETYPE:
  					interfaceType = (UCSYInterface) type;
  					allPublicMethod = interfaceType.getAllPublicMethod();
  				break;
  				
  				case METATYPE:
  					metaClassType = (UCSYMetaClass) type;
  					allPublicMethod = metaClassType.getAllPublicMethod();
  				break;
  			}
  			node.allPublicMethod = allPublicMethod;
  			
  			//Check to see if there is no public method ,then report error
  			if( allPublicMethod.size() == 0)
  			{
  				reportSSError(node.lineNo,"There are no public method of the  type "+ delegateTypeName +" in delegate handles all clause");
  				return null;
  			}
  			else
  			{
  				//Insert every method of public method into class definition
  				for (int i = 0; i< allPublicMethod.size(); i++)
  				{
  					Method m = allPublicMethod.get(i);
  					
  					//Check to see a method already contain in the class as member
  					if( theClass.isAField(m.methodName ))
  					{
  						reportSSError(node.lineNo," in delegate handles all clause cannot insert method "+ m.methodName +" beacause it is already defined as filed name in class" + theClass.className);
  						//return null;
  					}
  					else 
  					{
  						try
  						{
  							Method mClassMethod = theClass.getMethodOfNameAndSignature( m.methodName,m.methodSignature);
  							//Yes a method with same name and signature is already defined in the class
  							if( mClassMethod != null)
  							{
  								reportSSError(node.lineNo,"in delegate handles all clause cannot insert method "+m.methodName+" beacause it is already defined as method in class "+theClass.className);
  							}
  							else //We can now insert
  							{
  								UCSYMethod method = new UCSYMethod(m.modifier,m.methodName,m.methodSignature,m.methodReturnType) ;
  								method.ownerName       = m.ownerName;
  								///Debug.inform("Owername "+ method.ownerName);
  								theClass.addMethod( method );
  								node.allMethod.add( method );
  							}
  						}
  						catch(NotExceptedTypeException e)
  						{
  							reportSSError( node.lineNo," Type "+ e.exceptedTypeName +" is not excepted to be "+ e.foundTypeName);
  							return null;
  						}
  						catch(Exception e)
  						{
  							//CyclicInheritanceException ignore don't report it twice
  							return null;
  						}
  					}
  				}
  			}
  		}
  		catch(TypeNotFoundException e)
  		{
  			if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  			{
  				CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  				reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  			}
  		}
			

  		return null;
  }
  //****************************************************************************
  public Object visit(ASTForwardsClause node, Object data)
  {
  		//
  		for (int i = 0; i< node.signatureToCallList.size(); i++)
  		{
  			ASTSignatureToCall sToCall = node.signatureToCallList.get(i);
  			
  			//Pass information to be consumed by ASTSignatureToCall 
  			sToCall.delegateClause = new DelegateClause();
  			sToCall.delegateClause.delegateField = node.delegateField;
  			sToCall.delegateClause.delegateName  = node.delegateName;
  			sToCall.delegateClause.delegateTypeName = node.delegateTypeName; 
  			
  			 			
  			sToCall.jjtAccept( this, data );
  			
  			try
  			{
  				Type delegateType = CentralTypeTable.getCentralTypeTable().getType( node.delegateTypeName );
  				
  				UCSYClass delegateClass = null;
  				UCSYInterface delegateInterface = null;
  				UCSYMetaClass delegateMetaClass = null;
  				
  				/***********************************************************************
  				 *  must check calling method exist delegate types and callable
  				************************************************************************/
  				ArrayList<Method> methodsOfDelegateType = null;
  				switch( delegateType.getType())
  				{
  					case CLASSTYPE:
  						
  						delegateClass = (UCSYClass) delegateType;
  						
  						methodsOfDelegateType = delegateClass.getAllMethodsAceessibleByClass();
  						
  						
  					break;
  					case INTERFACETYPE:
  						delegateInterface = (UCSYInterface) delegateType;
  						methodsOfDelegateType = delegateInterface.getAllMethodsAccessibleByInterface();
  					break;
  					case METATYPE:
  						delegateMetaClass = (UCSYMetaClass) delegateType;
  						methodsOfDelegateType = delegateMetaClass.getAllMethodsAccessibleByMetaClass();
  					break;
  					
  					default:
  						reportSSError( node.lineNo, " delegate type may be class, interface or meta class type");
  						return null;
  				}
  				//We mus check which version of method must be called to parent delegate type
  				//after processing ASTMethodCall
  				UCSYMethod temp = new UCSYMethod();
  				temp.methodName = sToCall.methodCall.methodName;
  				if( ! TypeCheckUtilityClass.isMethodIntheMethodList( methodsOfDelegateType, temp ))
  				{
  					reportSSError(node.lineNo,"There are no method with the name "+ temp.methodName+" in delegate type "+ node.delegateTypeName);
  				}
  			}
  			catch(TypeNotFoundException e)
  			{
  				if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  				{	
  					CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  					reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  				}
  				return null;
  			}
  			catch(NotExceptedTypeException e)
  			{
  				reportSSError( node.lineNo,e.exceptedTypeName+" is not to be excepted as type "+ e.foundTypeName);
  				return null;
  			}
  			catch(Exception e)
  			{
  				//CyclicInheritanceException
  				//Don't report it twice
  				return null;
  			}
  		}
  		return null;
  }
  //****************************************************************************
  public Object visit(ASTDecoratesClause node, Object data)
  {
  		for (int i = 0; i< node.beforeCallOrAfterCallList.size(); i++)
  		{
  			ASTBeforeCallOrAfterCall beforeCallOrAfterCall = node.beforeCallOrAfterCallList.get(i);
  			
  			beforeCallOrAfterCall.delegateClause = new DelegateClause();
  			beforeCallOrAfterCall.delegateClause.delegateName = node.delegateName;
  			beforeCallOrAfterCall.delegateClause.delegateField = node.delegateField;
  			beforeCallOrAfterCall.delegateClause.delegateTypeName = node.delegateTypeName;
  			
  			inform("Delegate type in decorate clause"+ node.delegateTypeName );
  			beforeCallOrAfterCall.jjtAccept(this,data);
  		}
  		return null;
  }

  //****************************************************************************
  public Object visit(ASTBeforeCallOrAfterCall node, Object data)
  {
  	node.beforeOrAfter.delegateClause = node.delegateClause;
  	node.beforeOrAfter.jjtAccept(this, data);
  	///inform("Process before or after call");
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAfterCall node, Object data)
  {
  		//inform("Process after call");
  		UCSYClass theClass = (UCSYClass) data;
  		
  		node.methodSignature.jjtAccept( this, data);
  		node.methodCall.jjtAccept( this, data);
  		
  		/***********************************************************************
  		 
  		 Check to see method specified by method signature existed in the delegate type
  		 Check method specified by method call is existed in current class definition
  		 
  		 ***********************************************************************/
  		 try
  		 {
  		 	String delegateTypeName = node.delegateClause.delegateTypeName;
  		 	
  		 	Type delegateType = CentralTypeTable.getCentralTypeTable().getType( delegateTypeName );
  		 	
  		 	UCSYClass delegateClass = null;
  		 	UCSYInterface delegateInterface = null;
  		 	UCSYMetaClass delegateMetaClass = null;
  		 	
  		 	Method methodOfDelegateType = null;
  		 	Method callMethod = null;
  		 	
  		 	switch( delegateType.getType())
  		 	{
  		 		case CLASSTYPE:
  						delegateClass = (UCSYClass) delegateType;
  						// To check method of method call exist in delgate type and appliable
  						//**********************************************************************
  						methodOfDelegateType = delegateClass.getMethodOfNameAndSignature( node.methodSignature.methodName,node.methodSignature.methodSignature);
  						
  						//Cannot check here now because we have no symbol table
  						//callMethod = delegateClass.getMethodOfNameAndSignature();
  						if( methodOfDelegateType == null)
  						{
  							reportSSError(node.lineNo, " no method with name and signature "+ node.methodSignature.methodName+" existed in class "+ node.delegateClause.delegateTypeName+" to decorate ");
  						}
  						else
  						{
  							node.method.methodReturnType = methodOfDelegateType.methodReturnType;
  						}
  					break;
  					case INTERFACETYPE:
  						delegateInterface = (UCSYInterface) delegateType;
  						methodOfDelegateType = delegateInterface.getMethodOfNameAndSignature( node.methodSignature.methodName,node.methodSignature.methodSignature);
  						if( methodOfDelegateType == null)
  						{
  							reportSSError(node.lineNo, " no method with name and signature "+ node.methodSignature.methodName+" existed in interface "+ node.delegateClause.delegateTypeName+" to decorate ");
  						}
  						else
  						{
  							node.method.methodReturnType = methodOfDelegateType.methodReturnType;
  						}
  					break;
  					case METATYPE:
  						delegateMetaClass = (UCSYMetaClass) delegateType;
  						
  						methodOfDelegateType = delegateMetaClass.getMethodOfNameAndSignature( node.methodSignature.methodName,node.methodSignature.methodSignature);
  						if( methodOfDelegateType == null)
  						{
  							reportSSError(node.lineNo, " no method with name and signature "+ node.methodSignature.methodName+" existed in meta class "+ node.delegateClause.delegateTypeName+" to decorate ");
  						}
  						else
  						{
  							node.method.methodReturnType = methodOfDelegateType.methodReturnType;
  						}
  					break;
  					
  					default:
  						reportSSError( node.lineNo, " delegate type may be class, interface or meta class type");
  						return null;
  		 	}
  		 	
  		 	//Check methodCall in after clause is exist in the currentClass and appliable
  		 	
  		 	
  		 }
  		 catch(Exception e)
  		 {
  		 	return null;
  		 }	
  		
  		return null;
  }
  //****************************************************************************
  public Object visit(ASTBeforeCall node, Object data)
  {
  		UCSYClass theClass = (UCSYClass) data;
  		
  		node.methodSignature.jjtAccept( this, data);
  		node.methodCall.jjtAccept( this, data);
  		
  		 try
  		 {
  		 	String delegateTypeName = node.delegateClause.delegateTypeName;
  		 	
  		 	Type delegateType = CentralTypeTable.getCentralTypeTable().getType( delegateTypeName );
  		 	
  		 	UCSYClass delegateClass = null;
  		 	UCSYInterface delegateInterface = null;
  		 	UCSYMetaClass delegateMetaClass = null;
  		 	
  		 	Method methodOfDelegateType = null;
  		 	Method callMethod = null;
  		 	
  		 	switch( delegateType.getType())
  		 	{
  		 		case CLASSTYPE:
  						delegateClass = (UCSYClass) delegateType;
  						// To check method of method call exist in delgate type and appliable
  						//**********************************************************************
  						methodOfDelegateType = delegateClass.getMethodOfNameAndSignature( node.methodSignature.methodName,node.methodSignature.methodSignature);
  						
  						//Cannot check here now because we have no symbol table
  						//callMethod = delegateClass.getMethodOfNameAndSignature();
  						if( methodOfDelegateType == null)
  						{
  							reportSSError(node.lineNo, " no method with name and signature "+ node.methodSignature.methodName+" existed in class "+ node.delegateClause.delegateTypeName+" to decorate ");
  						}
  						else
  						{
  							node.method.methodReturnType = methodOfDelegateType.methodReturnType;
  						}
  					break;
  					case INTERFACETYPE:
  						delegateInterface = (UCSYInterface) delegateType;
  						//methodOfDelegateType = delegateInterface.getMethodOfNameAndSignature( node.methodSignature.methodName,node.methodSignature.methodSignature);
  						if( methodOfDelegateType == null)
  						{
  							reportSSError(node.lineNo, " no method with name and signature "+ node.methodSignature.methodName+" existed in interface "+ node.delegateClause.delegateTypeName+" to decorate ");
  						}
  						else
  						{
  							node.method.methodReturnType = methodOfDelegateType.methodReturnType;
  						}
  					break;
  					case METATYPE:
  						delegateMetaClass = (UCSYMetaClass) delegateType;
  						
  						methodOfDelegateType = delegateMetaClass.getMethodOfNameAndSignature( node.methodSignature.methodName,node.methodSignature.methodSignature);
  						if( methodOfDelegateType == null)
  						{
  							reportSSError(node.lineNo, " no method with name and signature "+ node.methodSignature.methodName+" existed in meta class "+ node.delegateClause.delegateTypeName+" to decorate ");
  						}
  						else
  						{
  							node.method.methodReturnType = methodOfDelegateType.methodReturnType;
  						}
  					break;
  					
  					default:
  						reportSSError( node.lineNo, " delegate type may be class, interface or meta class type");
  						return null;
  		 	}
  		 	
  		 	//Check methodCall in after clause is exist in the currentClass and appliable
  		 	
  		 	
  		 }
  		 catch(Exception e)
  		 {
  		 	return null;
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
  	
  	node.arguments.jjtAccept( this, data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTVariableDeclarator node, Object data)
  { 
  	
  	String variableType = VariableType.typeName;
  	//System.out.println ("At variable Declarator "+ vType.typeName);
  	node.variableDeclaratorId.jjtAccept( this, data);
  	node.lineNo = node.variableDeclaratorId.lineNo;
  	if(node.variableInitializer != null)
  	{
  		node.variableInitializer.jjtAccept( this, data);
  		///node.typeName = node.variableInitializer.typeName;
  		///Debug.inform("Check "+ variableType +" "+ node.variableInitializer.typeName);
  		if(!TypeCheckUtilityClass.isAssignmentCompatible(node.variableInitializer.typeName,variableType))
  		{
  			reportSSError(node.lineNo, " incompatiable type in local variable intialization, required "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(variableType)+" but found "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.variableInitializer.typeName));
  		}
  	}
  	
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTVariableDeclaratorId node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTVariableInitializer node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	node.lineNo = node.expression.lineNo;
  	node.typeName = node.expression.typeName;
  	
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
  	 	
  	SymbolTable.getSymbolTable().openScope();
  	
  	try
  	{
  		SymbolTableEntry thisEntry = new SymbolTableEntry("this",theClass.className);
  		SymbolTable.getSymbolTable().insert( thisEntry );
  	
  		SymbolTableEntry superEntry = new SymbolTableEntry("super",theClass.parentClassName);
  		SymbolTable.getSymbolTable().insert( superEntry );
  	}
  	catch(Exception e)
  	{
  		//I believe no error occur
  	}
  	CommonInheritedAttribute.currentMethod = node.method;
  	node.methodHeader.jjtAccept( this, data);
  	  	
  	node.block.jjtAccept( this, data);
  	
  	SymbolTable.getSymbolTable().closeScope();
  	ArrayList<UCSYClass> allParent = null;
  	try
  	{
  		allParent = theClass.getAllParent();
  	}
  	catch(TypeNotFoundException e)
  	{
  		if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  		return null;
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError( node.lineNo ,"Type "+ e.exceptedTypeName +" is not excepted to be of type "+e.foundTypeName);
  		return null;
  	}
  	catch(Exception e)
  	{
  		//Cyclic Inheritance Exception
  		// Don't report it twice
  		return null;
  	}	
  	//Type Check Overriding rule check to see if does not provide override modifier but override a parent method
  	// And overriding rule are as specified , all method protocol of parent and child must be same
  	// Access must be widen or = the acess of parent
  	
  	if(! theClass.isFreeClass )
  	{
  		boolean found = false;
  		int counter = 0;
  		int parentSize = allParent.size();
  	
  		while( !found && (counter < parentSize) )
  		{
  			UCSYClass parentClass =	allParent.get( counter ++ );	
  		
  			for (int i = 0; i< parentClass.methods.size(); i++)
  			{
  				Method m = parentClass.methods.get(i);
  			
  				//Find method with same signature;
  			
  				if( node.method.isSameOfNameAndSignature( m ))
  				{
  				//Ok found method with same name and signature 
  				//Check for correct return type
  					if( ! ( node.method.methodReturnType.equals( m.methodReturnType ) ))
  					{
  						reportSSError(node.lineNo,node.method.methodName +" method cannot override "+ m.methodName+" of parent "+parentClass.className+ " ,imcompatiable return type");
  					}
  					else if( m.isFinal())
  					{
  						reportSSError(node.lineNo,node.method.methodName +" trying to override final method of parent class "+parentClass.className+" :not allowed");
  					}
  					if( m.isStatic())
  					{
  						reportSSError(node.lineNo, "trying to override static method "+ m.methodName + " of class " + parentClass.className);
  					}
  					//To mask only access modifier
  					//Decimal 240 = 11110000
  					int methodAccessModifier = ( node.method.modifier & 240 );
  					int parentMethodAccessModifier = m.modifier & 240 ;
  					///inform("Method "+ UCSYClassAttribute.getTextualRep(node.method.modifier)+" parent "+ UCSYClassAttribute.getTextualRep(m.modifier));
  					if( UCSYClassAttribute.weakerAccess( methodAccessModifier , parentMethodAccessModifier ))
  					{
  						reportSSError(node.lineNo,node.methodName+" method cannot override method of "+parentClass.className+" trying to assign weaker access privilige");
  					}
  					if( !UCSYClassAttribute.isOverride(node.method.modifier ))
  					{
  						reportSSError(node.lineNo," method "+ node.methodName+ " overrides method of "+ parentClass.className+" but missing override modifier");
  					}
  					if( UCSYClassAttribute.isStatic( node.method.modifier ) )
  					{
  						reportSSError(node.lineNo,"illegal modifier static and  overrides in method "+ node.method.methodName);
  					}
  					
  					found = true;
  				}
  			}
  		
  		}
  		
  	}//if !freeClass
  	if( node.method.isNative())
  	{
  		
  		if( node.block.jjtGetNumChildren() > 0)
  		{
  			reportSSError(node.lineNo," Method with native modifier cannot contain statements ");
  		}
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodHeader node, Object data)
  { 
  	
  	//Open a scope
  	
  	
  	node.returnType.jjtAccept( this,data);
  	node.formalParameters.jjtAccept(this,data);
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFormalParameters node, Object data)
  { 
  	
  	for (int i = 0; i< node.formalParameterList.size(); i++)
  	{
  		ASTFormalParameter formalParameter = node.formalParameterList.get(i);
  		
  		formalParameter.jjtAccept( this, data);
  		node.lineNo = formalParameter.lineNo;
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFormalParameter node, Object data)
  { 
  	node.mainType.jjtAccept(this,data);
  	
  	
  	CentralTypeTableEntry entry = new CentralTypeTableEntry(node.typeName,null,false);
  	CentralTypeTable.getCentralTypeTable().insertTypeIfNotExist(entry);
  	
  	node.variableDeclaratorId.jjtAccept( this, data);
  	node.lineNo = node.variableDeclaratorId.lineNo;
  	//Insert each formal parameter variable into symbol table
  	String variableName = node.variableDeclaratorId.variableName;
  	if( variableName.charAt(0) == Character.toUpperCase( variableName.charAt(0)))
  	{
  		reportSSError(node.lineNo, "variable name must begin with a lowercase letter in UCSY you porvide as "+ variableName);
  	}
  	SymbolTableEntry variableEntry = new SymbolTableEntry( variableName,node.typeName);
  	
  	try
  	{
  		SymbolTable.getSymbolTable().insert( variableEntry );
  	}
  	catch(DuplicateNameException e)
  	{
  		reportSSError(node.lineNo," variable "+ e.varName +" in formal parameter is already defined");
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTReturnType node, Object data)
  { 
  	
  	if(node.voidType != null)
  	{
  		//System.out.println ("Why not");
  		node.voidType.jjtAccept(this,data);
  		
  	}
  	if(node.type != null)
  	{
  		node.type.jjtAccept( this, data);
  		
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
  		//node.typeName = node.primitiveType.typeName;	
  		
  	}
  	if( node.referenceType != null)
  	{
  		node.referenceType.jjtAccept(this,data);
  		///node.typeName =node.referenceType.typeName;
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimitiveType node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTReferenceType node, Object data)
  { 
  	if(node.metaInstanceType != null)
  	{
  		node.metaInstanceType.jjtAccept( this, data );
  		
  	}
  	if(node.arrayType != null)
  	{
  		node.arrayType.jjtAccept( this, data);
  		
  	}
  	if(node.classType != null)
  	{	
  		node.classType.jjtAccept( this, data);
  		
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMetaInstanceType node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTClassType node, Object data)
  { 
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArrayType node, Object data)
  { 
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
  	
  	node.exp.jjtAccept(this,data);
  	node.typeName = node.exp.typeName;
  	node.lineNo = node.exp.lineNo; 
  	///Debug.inform("Expression "+ node.typeName);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTOrNode node, Object data)
  { 
  	//inform("or ");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
  	try
 	{
 		node.typeName = TypeCheckUtilityClass.booleanOperatorTypeCheck("or",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+ e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAndNode node, Object data)
  { 
  	///inform("and");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
  	try
 	{
 		node.typeName = TypeCheckUtilityClass.booleanOperatorTypeCheck("and",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTEqualNode node, Object data)
  { 
  	///inform("==");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
 	
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.relationanlOperatorCheck("==",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNotEqualNode node, Object data)
  { 
  	///inform("!=");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
	node.lineNo = node.opOne.lineNo;
 	
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.relationanlOperatorCheck("!=",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceOfExpression node, Object data)
  {
  	node.opOne.jjtAccept(this,data);
  	node.type.jjtAccept(this,data);
  	String operandType = node.opOne.typeName;
  	String rightSideType = node.type.typeName;
  	node.lineNo = node.opOne.lineNo;
  	//Check rightHandSide must be ancestor of operandType
  	
  	if(operandType.equals(rightSideType))
  	{
  		//Ok same type
  		node.typeName = "t";	
  	}
  	else
  	{
  		try
  		{	Type t = CentralTypeTable.getCentralTypeTable().getType(operandType);
  			if(!t.isInstanceOf( rightSideType) )
  			{
  				reportSSError(node.lineNo," Type of "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(rightSideType )+" cannot be test instanceof  with "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(operandType));
  			}		 
  		}
  		catch(Exception e)
  		{
  		}
  		
  		
  		node.typeName ="t";
  	}
  	
  	///Debug.inform("Operand of type Name "+operandType+" right side type "+rightSideType);
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
  	//inform(">");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
 	node.lineNo = node.opOne.lineNo;
 	
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.relationanlOperatorCheck(">",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTGTEqualNode node, Object data)
  {
  	///inform(">=");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
 	node.lineNo = node.opOne.lineNo;
 	
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.relationanlOperatorCheck(">=",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLTNode node, Object data)
  {
  	///inform("<");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
 	node.lineNo = node.opOne.lineNo;
 	
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.relationanlOperatorCheck("<",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLTEqualNode node, Object data)
  {
  	///inform("<=");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
 	node.lineNo = node.opOne.lineNo;
 	
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.relationanlOperatorCheck("<=",node.opOne.typeName,node.opTwo.typeName);
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "t";
 	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPlusNode node, Object data)
  {
  	//inform("+");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
 	//inform("Plus type "+ node.opOne.typeName +" "+ node.opTwo.typeName);
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.arithmeticOperatorTypeCheck("+",node.opOne.typeName,node.opTwo.typeName);	
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "i";
 	}
 	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSubtractNode node, Object data)
  {
  	
	 //inform("+");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
 	//inform("Plus type "+ node.opOne.typeName +" "+ node.opTwo.typeName);
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.arithmeticOperatorTypeCheck("-",node.opOne.typeName,node.opTwo.typeName);	
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "i";
 	}

  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMultiplyNode node, Object data)
  {
  	
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
 	//inform("Plus type "+ node.opOne.typeName +" "+ node.opTwo.typeName);
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.arithmeticOperatorTypeCheck("*",node.opOne.typeName,node.opTwo.typeName);	
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "i";
 	}

 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDivsionNode node, Object data)
  {
  	//inform("+");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
 	//inform("Plus type "+ node.opOne.typeName +" "+ node.opTwo.typeName);
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.arithmeticOperatorTypeCheck("/",node.opOne.typeName,node.opTwo.typeName);	
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "i";
 	}

  	return null;
  }
  //****************************************************************************
  public Object visit(ASTModulusNode node, Object data)
  { 
  	//inform("+");
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	node.lineNo = node.opOne.lineNo;
 	//inform("Plus type "+ node.opOne.typeName +" "+ node.opTwo.typeName);
 	try
 	{
 		node.typeName = TypeCheckUtilityClass.arithmeticOperatorTypeCheck("%",node.opOne.typeName,node.opTwo.typeName);	
 	}
 	catch(UnAppliableOperationException e)
 	{
 		reportSSError(node.lineNo,"operator "+e.operator +" cannot be applied to type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opOneType) +" and " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.opTwoType));
 		node.typeName = "i";
 	}

  	return null;
  }
  //****************************************************************************
  public Object visit(ASTUnaryPlusNode node, Object data)
  { 
  	///inform("U +");
  	node.opOne.jjtAccept(this,data);
	node.lineNo = node.opOne.lineNo;
	if(! CentralTypeTable.isNumberType(node.opOne.typeName))
	{
		reportSSError(node.lineNo," unary operaor + is allowed to operated only on number type ");
	}
	node.typeName = node.opOne.typeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTUnaryMinusNode node, Object data)
  { 
  	node.opOne.jjtAccept(this,data);
 	node.lineNo = node.opOne.lineNo;
	if(! CentralTypeTable.isNumberType(node.opOne.typeName))
	{
		reportSSError(node.lineNo," unary operaor - is allowed to operated only on number type ");
	}
	node.typeName = node.opOne.typeName;

  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNotNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data); 
  	 node.typeName = node.opOne.typeName;
  	 node.lineNo = node.opOne.lineNo;
  	 if(!node.typeName.equals("t"))
  	 {
  	 	reportSSError(node.lineNo," operator not can only be applied to boolean type, but found "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.typeName));
  	 }
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCastNode node, Object data)
  { 
  	
  	node.opOne.jjtAccept(this,data);
  	
  	node.lineNo = node.opOne.lineNo;
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
  	node.castDestination = node.type.typeName;
  	
  	node.expression.jjtAccept(this,data);
  	 	
  	node.castSource = node.expression.typeName;
  	node.lineNo  = node.type.lineNo;
  	
  	//Debug.inform("FROOM "+ node.castSource +" TO "+ node.castDestination);
  	if(!TypeCheckUtilityClass.canCastable( node.castSource, node.castDestination))
  	{
  		reportSSError(node.lineNo," incovertible type from "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic( node.castSource) +" to "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.castDestination));
  	}
  	node.typeName = node.castDestination;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimaryExpression node, Object data)
  { 
  	//node.primaryExpression = (SimpleNode)node.jjtGetChild(0);
  	
  	//node.primaryExpression.jjtAccept(this,data);
  	
  	//node.lineNo = node.primaryExpression.lineNo;
  	//node.typeName = node.primaryExpression.typeName;
  	node.primaryExpression = (ParentName)node.jjtGetChild(0);
  	node.primaryExpression.jjtAccept(this,data);
  	node.lineNo   = node.primaryExpression.lineNo;
  	node.typeName = node.primaryExpression.typeName;
  	///Debug.inform("Primary Expression "+ node.typeName);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimaryPrefix node, Object data)
  { 
  	node.primaryPrefix = (ParentName)node.jjtGetChild(0);
  	node.primaryPrefix.jjtAccept(this,data);
  	node.lineNo = node.primaryPrefix.lineNo;
  	node.typeName = node.primaryPrefix.typeName;
  	
  	
  	for (int i = 0; i< node.primaryPrefix.nameList.size(); i++)
  	{
  		node.nameList.add(node.primaryPrefix.nameList.get(i));
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimarySuffix node, Object data)
  { 
  	//Suffix have two node 
  	UCSYClass theClass = (UCSYClass)data;
  	node.firstPart = (ParentName)node.jjtGetChild(0);
  	node.secondPart = (ParentName)node.jjtGetChild(1);
  	
  	node.firstPart.jjtAccept( this,data );
  	node.secondPart.jjtAccept( this,data );
  	node.lineNo = node.firstPart.lineNo;
  	node.frontName = node.firstPart.typeName;
  	node.typeName = node.frontName;
  	
  	if( node.firstPart instanceof ASTLiteralPrimaryPrefix)
  	{
  		reportSSError(node.lineNo," invalid use of literal ");
  		return null;
  	}
  	ParentName firstName = (ParentName)node.firstPart;
  	ParentName secondName = (ParentName)node.secondPart;
  	if(node.secondPart instanceof ASTArrayExpressionPrimarySuffix)
  	{
  		ASTArrayExpressionPrimarySuffix arrayExp = (ASTArrayExpressionPrimarySuffix)node.secondPart;
  		//String indexType = arrayExp.typeName;
  		//String arrayType = 
  		
  		node.typeName = node.typeName.substring(1,node.typeName.length());
  		
  		node.firstPart.nameList.remove( node.firstPart.nameList.size()-1);
  		//node.secondPartnameList.add(node.typeName);
  		
  		node.firstPart.nameList.add( node.typeName);
  		//inform("Array Type Name "+ node.firstPart.nameList);
  	}
  	for (int i = 0; i< firstName.nameList.size(); i++)
  	{
  		node.nameList.add( firstName.nameList.get(i));
  	}
  	for (int i = 0; i< secondName.nameList.size(); i++)
  	{
  		node.nameList.add(secondName.nameList.get(i));
  	}
  	
  	
  	if( node.secondPart instanceof ASTSuffixDotIdentifierNode)
  	{
  		ASTSuffixDotIdentifierNode suffixId = (ASTSuffixDotIdentifierNode)node.secondPart;
  		//suffixId.jjtAccept(this,data);
  		//inform("SUffix name "+suffixId.name + " front name "+ node.frontName);
  		node.endName = suffixId.name;
  		
  		ArrayList<String> nameList = new ArrayList<String>();
  		
  		nameList.add(node.frontName);
  		nameList.add(node.endName);
  		//inform("Resolving "+ node.nameList);
  		try
  		{
  			node.typeName = TypeResolver.resolveTypeOnSimpleNameForSuffix(theClass.className,node.frontName,node.endName);
  		}
  		catch(CannotResolveSymbolException e)
  		{
  			reportSSError(node.lineNo," cannot resolve symbol "+e.symbolName);
  			node.typeName = "null";
  		}
  		catch(InvalidAccessToMemberException e)
  		{
  			reportSSError(node.lineNo," cannot access member  of "+ e.childName + " from class "+ e.parentName + " due to protection level");
  			node.typeName = "null";
  		}
  		
  		//node.endName = TypeResolver.resolveTypeForName();
  	}
  	else if( node.secondPart instanceof ASTArgumentsSuffix)
  	{
  		ASTArgumentsSuffix argumentsSuffix = (ASTArgumentsSuffix)node.secondPart;
  		String methodSignature = argumentsSuffix.typeName;
  		String parentTypeNameOfMethod ="";
  		argumentsSuffix.methodName = firstName.nameList.get(firstName.nameList.size()-1);
  		
  		///inform("Method name is "+ argumentsSuffix.methodName);
  		 		
  		ArrayList<String> parentClassOfMethod = new ArrayList<String>();
  		try
  		{
  			if( firstName.nameList.size() > 1)
  			{
  				for (int i = 0; i< firstName.nameList.size()-1; i++)
  				{
  					parentClassOfMethod.add(firstName.nameList.get(i));
  				}
  				parentTypeNameOfMethod = TypeResolver.resolveTypeForName(theClass.className,(Type)theClass,parentClassOfMethod);
  			}
  			else
  			{
  				parentTypeNameOfMethod = theClass.className;
  				
  			}
  			String contextTypeName ="";
  			if(data instanceof UCSYClass)
  			{
  				contextTypeName = ((UCSYClass)data).className;	
  			}
  			else if(data instanceof UCSYInterface)
  			{
  				contextTypeName  = ((UCSYInterface)data).interfaceName;
  			}
  			argumentsSuffix.parentClassName = parentTypeNameOfMethod;
  			argumentsSuffix.methodSignature = methodSignature;
  			//inform("To Overload method name  "+ argumentsSuffix.method);
  			///Debug.inform("Parent type  is "+ parentTypeNameOfMethod);
  			Type theType = CentralTypeTable.getCentralTypeTable().getType(parentTypeNameOfMethod);
  			//inform("Is type is null:; "+theType);
  			//Debug.inform("Find Mehthod to overload "+ argumentsSuffix.methodName);
  			argumentsSuffix.method = (Method)theType.getBestOverloadedMethod( contextTypeName,argumentsSuffix.methodName,methodSignature);
  			argumentsSuffix.arguments.method = argumentsSuffix.method;
  			///argumentsSuffix.method.methodReturnType = 
  			//inform(parentTypeNameOfMethod + " "+ methodSignature);
  			
  		}
  		catch(CannotFindBestOverloadedMethodException e)
  		{
  			reportSSError(node.lineNo," Cannot find best overload method for "+ argumentsSuffix.methodName +" with signature "+ TypeCheckUtilityClass.getTypeDescriptionOfSignature(argumentsSuffix.methodSignature));
  			return null;
  		}
  		catch(UnambiguousResolutionException e)
  		{
  			String dataString = "";
  			for (int i = 0; i< e.met.size(); i++)
  			{
  				dataString =dataString + " and "+ e.met.get(i).methodName+e.met.get(i).methodSignature ;
  			}
  			reportSSError(node.lineNo," Unambiguous resolution found in "+dataString);
  		}
  		catch(CannotResolveSymbolException e)
  		{
  			reportSSError(node.lineNo," cannot resolve symbol "+e.symbolName);
  		}
  		catch(Exception e)
  		{
  			//e.printStackTrace();	
  			reportSSError(node.lineNo," Cannot find suitable method for "+ argumentsSuffix.methodName);
  		}
  	}
  	
  	else if( node.secondPart instanceof ASTArrayExpressionPrimarySuffix)
  	{
  		
  		
  		
  		///ASTArrayExpressionPrimarySuffix arrayExp = (ASTArrayExpressionPrimarySuffix)node.secondPart;
  		//String indexType = arrayExp.typeName;
  		//String arrayType = 
  		///node.typeName = node.firstPart.typeName.substring(1,node.firstPart.typeName.length());
  		///inform("Before "+ node.firstPart.nameList + " "+ node.firstPart.nameList.size());
  		//inform(" add "+node.typeName);
  		///node.firstPart.nameList.remove( node.firstPart.nameList.size()-1);
  		//node.secondPartnameList.add(node.typeName);
  		
  		///node.firstPart.nameList.add( node.typeName);
  		///inform("Array Type Name "+ node.firstPart.nameList);
  	}
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTName node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTIntegerLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	if(node.t.image.indexOf("L") != -1)
  	{
  		node.value = Long.parseLong(node.t.image.substring(0,node.t.image.length()-1),10);
  		node.typeName = "l";
  	}
  	else
  	{
  		node.value = Long.parseLong(node.t.image.substring(0,node.t.image.length()),10);
  		node.typeName = "i";
  	}
  	
  	///inform(node.value+"");
  	//************************ For Code Generation ******************************
  	if( node.typeName.equals("l") )
  	 CommonInheritedAttribute.currentType.uCodeFile.constantPool.addLongRef(node.value);
  	else
  		CommonInheritedAttribute.currentType.uCodeFile.constantPool.addIntegerRef((int)node.value);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTFloatLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	if(node.t.image.indexOf("D") != -1)
  	{
  		node.value = Double.parseDouble(node.t.image.substring(0,node.t.image.length()-1));
  		node.typeName = "d";
  	}
  	else
  	{
  		node.value = Double.parseDouble(node.t.image.substring(0,node.t.image.length()));
  		///Debug.inform("Float Value "+ node.value);
  		node.typeName = "f";
  	}
  	
  	///inform(node.value+"");
  	//************************ For Code Generation ******************************
  	if( node.typeName.equals("d") )
  	{
  		CommonInheritedAttribute.currentType.uCodeFile.constantPool.addDoubleRef(node.value);
  	}
  	else
  	{
  		///Debug.inform("Adding Flaot Reference to ConstantPool "+ (float)node.value );
  		CommonInheritedAttribute.currentType.uCodeFile.constantPool.addFloatRef((float)node.value);return null;
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCharLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	node.value  = node.t.image.charAt(1);
  	node.typeName = "c";
  	//inform(node.value+"");
  	
  	//************************ For Code Generation ******************************
  	CommonInheritedAttribute.currentType.uCodeFile.constantPool.addIntegerRef(node.value);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTStringLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	node.value  = node.t.image.substring(1,node.t.image.length() -1);
  	//char ch =13;
  	//String someValue =ch+"";
  	node.value  = EscapeProcess.processString(node.value);
  	
  	node.typeName = "m";
  	///inform(node.value+"");
  	
  	//************************ For Code Generation ******************************
  	CommonInheritedAttribute.currentType.uCodeFile.constantPool.addStringRef( node.value );
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDoubleLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	node.value = Double.parseDouble(node.t.image);
  	node.typeName = "d";
  	//inform(node.value+"");
  	
  	//************************ For Code Generation ******************************
  	CommonInheritedAttribute.currentType.uCodeFile.constantPool.addDoubleRef( node.value );
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLongLiteral node, Object data)
  { 
  	//Debug.inform("Long literal "+ node.t.image);
  	node.lineNo = node.t.beginLine;
  	String longData = node.t.image;
  	if(longData.charAt( longData.length()-1) =='L')
  		longData = longData.substring(0, longData.length()-1);
  	node.value  = Long.parseLong(longData);
  	node.typeName = "l";
  	///inform(node.value+"");
  	//************************ For Code Generation ******************************
  	CommonInheritedAttribute.currentType.uCodeFile.constantPool.addLongRef( node.value );
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTBooleanLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	//inform(node.value+"");
  	//************************ For Code Generation ******************************
  	
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTNullLiteral node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	node.typeName = "null";
  	
  	CommonInheritedAttribute.currentType.uCodeFile.constantPool.addIntegerRef( 0 );
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAllocationExpression node, Object data)
  { 
  	node.allocationNode = (ParentExpression)node.jjtGetChild(0);
  	node.allocationNode.jjtAccept(this,data);
  	node.typeName = node.allocationNode.typeName;
  	///Debug.inform("ASTAllocation "+node.typeName);
  	node.lineNo = node.allocationNode.lineNo;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArrayAllocation node, Object data)
  { 
  	//node.arrayAllocationNode = (ParentExpression)node.jjtGetChild(0);
  	node.arrayAllocationNode.jjtAccept(this,data);
  	node.typeName = node.arrayAllocationNode.typeName;
  	node.lineNo   = node.arrayAllocationNode.lineNo;
  	///Debug.inform("Array Allocation "+node.typeName);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceArrayAllocation node, Object data)
  { 
  	node.name.jjtAccept(this,data);
  	node.dimensionParameter.jjtAccept(this,data);
  	node.lineNo = node.name.lineNo;
  	
  	node.arrayClassName = node.name.nameList.get(0);
  	node.noOfDimension  = node.dimensionParameter.noOfDimension;
  	
  	String typeName ="";
  	
  	for (int i = 0; i< node.noOfDimension; i++)
  	{
  		typeName += "[";
  	}
  	typeName += node.arrayClassName;
  	node.typeName = typeName;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDimensionParameter node, Object data)
  { 
  	node.noOfDimension = node.expressionList.size();
  	
  	for (int i = 0; i< node.expressionList.size(); i++)
  	{
  		ASTExpression expression = node.expressionList.get(i);
  		expression.jjtAccept( this,data);
  		node.lineNo = node.expressionList.get(i).lineNo;
  		if(! (CentralTypeTable.isInteger( expression.typeName ) || CentralTypeTable.isInteger(expression.typeName)))
  		{
  			reportSSError(node.lineNo,"Only integer index are allowed in array dimension");
  		}
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTPrimitiveArrayAllocation node, Object data)
  { 
  	node.primitiveType.jjtAccept(this,data);
  	node.dimensionParameter.jjtAccept(this,data);
  	node.noOfDimensions = node.dimensionParameter.expressionList.size();
  	
  	String type="";
  	for (int i = 0; i< node.noOfDimensions; i++)
  	{
  		type += "[";
  	}
  	node.typeName = type + node.primitiveType.typeName;
  	node.lineNo = node.primitiveType.lineNo;
  	///Debug.inform("Primitive Array Type Name "+ node.typeName);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTInstanceAllocation node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	node.name.jjtAccept(this,data);
  	node.arguments.jjtAccept(this,data);
  	node.constructorClassName = node.name.nameList.get(0);
  	node.constructorSignature = node.arguments.typeName;
  	node.typeName = node.constructorClassName;
  	node.lineNo = node.name.lineNo;
  	try
  	{
  		UCSYClass newClass = CentralTypeTable.getCentralTypeTable().getAClass( node.constructorClassName );
  		if(newClass.isAbstract())
  		{
  			reportSSError(node.lineNo," Cannot instantiate abstract class "+ node.constructorClassName);
  			return null;
  		}
  		///inform("Find to overload "+ node.constructorClassName +" "+ node.constructorSignature);
  		node.constructorMethod = (UCSYMethod)newClass.getBestOverloadedConstructor( theClass.className, node.constructorSignature);
  		node.arguments.method  = node.constructorMethod;
  		//inform(node.constructorMethod.methodSignature);
  	}
  	catch(TypeNotFoundException e)
  	{
  		if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError(node.lineNo,"Only class and array type are allowed to instantiate with new but found "+ e.foundTypeName);
  	}
  	catch(UnambiguousResolutionException e)
  	{
  		reportSSError(node.lineNo," Unambiguous method resolution for constructor "+ node.constructorClassName);
  	}
  	catch(CannotFindBestOverloadedMethodException e)
  	{
  		reportSSError(node.lineNo,"Cannot find method or inaccessible "+ node.constructorClassName +" with signature " + TypeCheckUtilityClass.getTypeDescriptionOfSignature(node.constructorSignature));
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArguments node, Object data)
  { 
  	String argType="" ;
  	if(node.argumentsList != null)
  	{
  		node.argumentsList.jjtAccept(this,data);
  		node.argumentsList.method = node.method;
  		argType = node.argumentsList.typeName;
  	}
  	node.typeName = "("+ argType + ")";
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTArgumentsList node, Object data)
  { 
  	String argType ="";
  	
  	for (int i = 0; i< node.expressionList.size(); i++)
  	{
  		ASTExpression exp = node.expressionList.get(i);
  		exp.jjtAccept(this,data);
  		
  		argType += exp.typeName+",";
  	}
  	node.typeName = argType;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTStatement node, Object data)
  { 
  	node.pStatement.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTAssignmentStatement node, Object data)
  { 
  	///inform("Process Assignments");
  	node.primaryExpression.jjtAccept(this,data);
  	node.destinationType = node.primaryExpression.typeName;
  	
  	node.assignmentOperator.jjtAccept(this,data);
  	
  	node.expression.jjtAccept(this,data);
  	node.sourceType = node.expression.typeName;
  	
  	node.lineNo = node.primaryExpression.lineNo;
  	///inform(node.sourceType +" "+ node.destinationType);
  	if(! TypeCheckUtilityClass.isAssignmentCompatible( node.sourceType,node.destinationType) )
  	{
  		
  		reportSSError(node.lineNo," Type of "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.sourceType )+" cannot be assinged to "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.destinationType));
  	}
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
  	///inform("Processing block");
  	SymbolTable.getSymbolTable().openScope();
  	for (int i = 0; i< node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept( this, data);
  	}
  	SymbolTable.getSymbolTable().closeScope();
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTMethodCallStatement node, Object data)
  { 
  	node.primaryExpression.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTEmptyStatement node, Object data)
  { 
  	return null;
  }
  public Object visit(ASTLabelStatement node, Object data)
  { 
  	node.block.jjtAccept(this,data);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSwitchStatement node, Object data)
  { 
  	SymbolTable.getSymbolTable().openScope();
  	node.expression.jjtAccept(this,data);
  	
  	String expressionType = node.expression.typeName;
  	node.lineNo = node.expression.lineNo;
  	
  	CommonInheritedAttribute.withinLoop ++;
  	if(!(CentralTypeTable.isLowerIntegralType(expressionType) || CentralTypeTable.isInteger( expressionType ) || CentralTypeTable.isCharacter(expressionType)))
  	{
  		reportSSError(node.lineNo,"Only integral type and character are allowed in expression part of switch but found "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(expressionType));
  	}
  	for (int i = 0; i< node.caseStatementList.size(); i++)
  	{
  		ASTCaseStatement caseStatement = node.caseStatementList.get(i);
  		
  		caseStatement.jjtAccept( this,data);
  		node.lineNo = caseStatement.lineNo;
  		if(! TypeCheckUtilityClass.isAssignmentCompatible( caseStatement.typeName,expressionType))
  		{
  			reportSSError(node.lineNo," imcompatiable type in case label required "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(expressionType)+" but found " + TypeCheckUtilityClass.getTypeDescriptionFromMemonic(caseStatement.typeName));
  		}
  		if( node.caseValue.contains( caseStatement.caseExpression.value ))
  		{
  			reportSSError(node.lineNo, " duplicate label are not allowed in switch statement, duplicate label is "+ caseStatement.caseExpression.value);
  		}
  		else
  		{
  			node.caseValue.add(caseStatement.caseExpression.value);
  		}
  	}
  	if(node.defaultStatement != null)
  	{
  		node.defaultStatement.jjtAccept(this,data);
  	}
  	CommonInheritedAttribute.withinLoop --;
  	SymbolTable.getSymbolTable().closeScope();
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCaseStatement node, Object data)
  { 
  	node.caseExpression.jjtAccept(this,data);
  	node.lineNo = node.t.beginLine;
  	String caseType = node.caseExpression.typeName;
  	if(!(CentralTypeTable.isLowerIntegralType(caseType) || CentralTypeTable.isInteger( caseType ) || CentralTypeTable.isCharacter(caseType)))
  	{
  		reportSSError(node.lineNo,"Only integral type and character are allowed in case label of switch but found "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(caseType));
  	}
  	for (int i = 0; i< node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept(this,data);
  	}
  	node.typeName = caseType;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCaseExpression node, Object data)
  { 
  	node.jjtGetChild(0).jjtAccept(this,data);
  	node.typeName = ((ParentExpression)node.jjtGetChild(0)).typeName;
  	
  	if(node.jjtGetChild(0) instanceof ASTIntegerLiteral)
  	{
  		node.value = new Integer((int)((ASTIntegerLiteral)node.jjtGetChild(0)).value);
  	}
  	else if(node.jjtGetChild(0) instanceof ASTCharLiteral)
  	{
  		node.value = new Character( ((ASTCharLiteral)node.jjtGetChild(0)).value);
  	}
  	else if(node.jjtGetChild(0) instanceof ASTCaseIdentifier)
  	{
  		
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCaseIdentifier node, Object data)
  { 
  	//node.typeName = TypeResolver.
  	UCSYClass theClass = (UCSYClass)data;
  	ArrayList<String> nameList = new ArrayList<String>();
  	node.idName = node.t.image;
  	node.lineNo = node.t.beginLine;
  	nameList.add(node.idName);
  	
  	try
  	{
  		node.typeName = TypeResolver.resolveTypeForName(theClass.className,(Type)theClass,nameList);
  	}
  	catch(StaticMethodCannotAccessInstanceFieldException e)
  	{
  		reportSSError(node.lineNo," cannot access instance filed "+ e.fieldName +" in static method");
  		node.typeName = "null";
  	}
  	catch(CannotResolveSymbolException e)
  	{
  		reportSSError(node.lineNo," cannot resolve symbol "+e.symbolName);
  		node.typeName = "null";
  	}
  	catch(InstanceFieldReferenceByClassReferenceException e)
  	{
  		reportSSError(node.lineNo," cannot refernec instance field through a class reference ");
  		node.typeName = "null";
  	}
  	catch(InvalidAccessToMemberException e)
  	{
  		reportSSError(node.lineNo," cannot access member  "+ node.idName);
  		node.typeName = "null";
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDefaultStatement node, Object data)
  { 
  	for (int i = 0; i<node.statementList.size(); i++)
  	{
  		node.statementList.get(i).jjtAccept(this,data);
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTIfStatement node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	node.lineNo = node.expression.lineNo;
  	
  	
  	if(!node.expression.typeName.equals("t"))
  	{
  		reportSSError(node.lineNo, " only boolean expression are allowed in expression part of if statement but found "+TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.expression.typeName));
  	}
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
  	node.lineNo = node.expression.lineNo;
  	if(!node.expression.typeName.equals("t"))
  	{
  		reportSSError(node.lineNo, " only boolean expression are allowed in expression part of while statement but found "+TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.expression.typeName));
  	}
  	CommonInheritedAttribute.withinLoop ++;
  	node.statement.jjtAccept(this,data);
  	CommonInheritedAttribute.withinLoop --;
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTDoStatement node, Object data)
  { 
  	CommonInheritedAttribute.withinLoop ++;
  	node.statement.jjtAccept(this,data);
  	CommonInheritedAttribute.withinLoop --;
  	node.expression.jjtAccept(this,data);
  	node.lineNo = node.expression.lineNo;
  	if(!node.expression.typeName.equals("t"))
  	{
  		reportSSError(node.lineNo, " only boolean expression are allowed in expression part of do while statement but found "+TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.expression.typeName));
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTForStatement node, Object data)
  { 
  	SymbolTable.getSymbolTable().openScope();
  	
  	node.forInitializer.jjtAccept(this,data);
  	node.forExpression.jjtAccept(this,data);
  	node.forUpdator.jjtAccept(this,data);
  	node.lineNo = node.t.beginLine;
  	
  	CommonInheritedAttribute.withinLoop ++;
  	node.statement.jjtAccept(this,data);
  	CommonInheritedAttribute.withinLoop --;
  	
  	SymbolTable.getSymbolTable().closeScope();
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
  		node.lineNo = node.expression.lineNo;
  		if(!node.expression.typeName.equals("t"))
  		{
  			reportSSError(node.lineNo, " only boolean expression are allowed in expression part of for statement but found "+TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.expression.typeName));
  		}
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
  	String localVariableType = node.type.typeName;
  	VariableType.typeName = localVariableType;
  	//inform("Process loca var "+ node.type.typeName);
  	CentralTypeTableEntry entry = new CentralTypeTableEntry( localVariableType , null, false);
  	CentralTypeTable.getCentralTypeTable().insertTypeIfNotExist( entry );
  	
  	for (int i = 0; i< node.variableDeclaratorList.size(); i++)
  	{
  		ASTVariableDeclarator variableDeclarator = node.variableDeclaratorList.get(i);
  		
  		variableDeclarator.typeName = localVariableType;
  		variableDeclarator.jjtAccept(this, data);
  		
  		String variableName = variableDeclarator.variableDeclaratorId.variableName;
  		///	inform("Insert "+variableName);
  		if( variableName.charAt(0) == Character.toUpperCase( variableName.charAt(0)))
  		{
  			reportSSError(node.lineNo, " local variable must start with a lowercase letter, you provide as "+ variableName);
  		}
  		SymbolTableEntry variableEntry = new SymbolTableEntry( variableName, localVariableType);
  		
  		try
  		{
  			SymbolTable.getSymbolTable().insert(variableEntry);
  		}
  		catch(DuplicateNameException e)
  		{
  			reportSSError(node.lineNo, "variable "+ e.varName +" is already defined with type "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(e.typeName));
  		}
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
  	node.lineNo = node.t.beginLine;
  	if( node.expression != null)
  	{
  		node.expression.jjtAccept(this,data);
  		node.returnType = node.expression.typeName;
  		//inform("Check "+ node.returnType);
  		if( CommonInheritedAttribute.currentMethod.methodReturnType.equals("v"))
  		{
  			reportSSError(node.lineNo," You cannot return value from method whose return type is void");
  			return null;
  		}
  		if( !TypeCheckUtilityClass.isAssignmentCompatible(node.returnType, CommonInheritedAttribute.currentMethod.methodReturnType))
  		{
  			reportSSError(node.lineNo," Imcompatiable return type supplied, required "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(CommonInheritedAttribute.currentMethod.methodReturnType) +" but found "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.returnType));
  		}
  		
  	}
  	else
  	{
  		if(! CommonInheritedAttribute.currentMethod.methodReturnType.equals("v"))
  		{
  			reportSSError(node.lineNo,"You must provide an expression in return statement ,method return "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic( CommonInheritedAttribute.currentMethod.methodReturnType));
  		}
  		node.returnType = "v";
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
  	SymbolTable.getSymbolTable().openScope();
  	node.block.jjtAccept(this,data);
  	SymbolTable.getSymbolTable().closeScope();
  	
  	for (int i = 0; i< node.catchStatementList.size(); i++)
  	{
  		ASTCatchStatement catchStatement = node.catchStatementList.get(i);
  		catchStatement.jjtAccept(this,data);
  	}
  	if(node.finallyBlock != null)
  	{
  		SymbolTable.getSymbolTable().openScope();
  		node.finallyBlock.jjtAccept(this,data);
  		SymbolTable.getSymbolTable().closeScope();
  	}
  	
  	/***************************************************************************
  	 * to check following situation
  	 *	
		catch(Exception e)
		catch(ArrayIndexOutOfBoundsException e)
		
  	 ***************************************************************************/
  	try
  	{
  		for (int i = 0; i< node.catchStatementList.size(); i++)
  		{
  			String currentExceptionName = node.catchStatementList.get(i).catchExceptionType;
  			node.lineNo = node.catchStatementList.get(i).lineNo;
  			
  			UCSYClass currentExceptionClass = CentralTypeTable.getCentralTypeTable().getAClass( currentExceptionName );
  			for (int j = 0; j< i; j++)
  			{
  				String previousExceptionName = node.catchStatementList.get(j).catchExceptionType;
  				UCSYClass previousExceptionClass = CentralTypeTable.getCentralTypeTable().getAClass( previousExceptionName );
  				
  				if( currentExceptionClass.isChildOf( previousExceptionName ) )
  				{
  					reportSSError(node.lineNo, "Exception "+ previousExceptionName +" will already be caught than "+ currentExceptionName );
  				}
  			}
  		}
  	}
  	catch(Exception e)
  	{
  		//Because all error I assume are hadled above code
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTCatchStatement node, Object data)
  { 
  	SymbolTable.getSymbolTable().openScope();
  	
  	node.formalParameters.jjtAccept(this,data);
  	node.lineNo = node.formalParameters.lineNo;
  	
  	node.block.jjtAccept(this,data);
  	
  	SymbolTable.getSymbolTable().closeScope();
  	
  	//Only one Exception can be catch at a catch statement
  	if(node.formalParameters.formalParameterList.size() > 1)
  	{
  		reportSSError(node.lineNo," You cannot catch more than one exception in a catch statement");
  		
  	}
  	/***************************************************************************
  	 * Every Exception catch in catch statement must be Exception or inherited 
  	 * from Exception class
  	 ***************************************************************************/
  	 
  	 // Only one formal parameter exists for CatchStatement
  	 node.catchExceptionType = node.formalParameters.formalParameterList.get(0).typeName;
  	 
  	 try
  	 {
  	 	UCSYClass catchExceptionClass = CentralTypeTable.getCentralTypeTable().getAClass( node.catchExceptionType );
  	 	if(! ( node.catchExceptionType.equals("Exception") || catchExceptionClass.isChildOf("Exception")))
  	 	{
  	 		//Debug.inform("Compalinging about "+node.catchExceptionType);
  	 		reportSSError(node.lineNo," Every exception catch in the catch statement must be inherited from Exception class");
  	 	}
  	 }
  	 catch(TypeNotFoundException e)
  	 {
  	 	if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  	 }
  	 catch(NotExceptedTypeException e)
  	 {
  	 	reportSSError(node.lineNo," Exception class is excepted but found "+ e.foundTypeName );
  	 }
  	 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTThrowStatement node, Object data)
  { 
  	node.expression.jjtAccept(this,data);
  	node.typeName = node.expression.typeName;
  	node.lineNo   = node.expression.lineNo;
  	
  	try
  	{
  		UCSYClass exceptionClass = CentralTypeTable.getCentralTypeTable().getAClass( node.typeName );
  		
  		if(! (exceptionClass.isChildOf("Exception") || node.typeName.equals("Exception")))
  		{
  			reportSSError(node.lineNo," only class that are Exception or inherited from Exception can be throw");
  		}
  	}
  	catch(TypeNotFoundException e)
  	{
  		if( !CentralTypeTable.getCentralTypeTable().notFoundTypes.contains(e.typeName))
  		{
  			CentralTypeTable.getCentralTypeTable().notFoundTypes.add(e.typeName);	
  			reportSSError(node.lineNo,"Type "+ e.typeName +" not found ");
  		}
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError(node.lineNo," Excepting Exception or decendents of its class but found " + e.foundTypeName);
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTBreakStatement node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	
  	if( node.label != null)
  	{
  		node.labelName = node.label.image;
  	}
  	if(CommonInheritedAttribute.withinLoop <= 0)
  	{
  		reportSSError(node.lineNo," break statement is allowed only in switch,while, do while, for statement ");
  	}
  	
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTRebindStatement node, Object data)
  { 
  	//node.sourceExpression.jjtAccept(this,data);
  	node.destinationExpression.jjtAccept(this,data);
  	
  	
  	try
  	{
  		
  		UCSYClass rebindClass = CentralTypeTable.getCentralTypeTable().getAClass( node.rebindClassName );
  	
  		if(! rebindClass.isFreeClass() )
  		{
  			reportSSError(node.lineNo,"class "+ node.rebindClassName + " is not a free class not allowed operation");
  		}
  		else
  		{
  			node.rebindMethod = rebindClass.getAMethod( node.rebindMethodName );
  			if( node.rebindMethod == null)
  			{
  				reportSSError(node.lineNo," Method "+ node.rebindMethodName +" not found in free class "+ node.rebindClassName);
  			}
  		}
  		if(! (node.destinationExpression.nameList.size()== 1 || node.destinationExpression.nameList.size()== 2))
  		{
  			reportSSError(node.lineNo,"In rebind statement destination expression must be of the form obj.rebindAbleMethod" );
  			
  		}
  		else
  		{
  			//Current Object 'rebindable method
  			UCSYClass currentClass = (UCSYClass)data;
  			if(node.destinationExpression.nameList.size() ==1)
  			{
  				if(!currentClass.isAMethod( node.destinationExpression.nameList.get(0)))
  				{
  					reportSSError(node.lineNo,"rebindable method not found "+node.destinationExpression.nameList.get(0));
  				}
  				else
  				{
  					node.targetMethod = currentClass.getAMethod(node.destinationExpression.nameList.get(0));
  					
  					
  					  				
  				}
  			}
  			else //name List.size == 2
  			{
  				String object = node.destinationExpression.nameList.get(0);
  				ArrayList<String> objectName = new ArrayList<String>();
  				objectName.add(object);
  				
  				String typeNameOfObject = TypeResolver.resolveTypeForName(currentClass.className,currentClass,objectName);
  				//Debug.inform("Type Name of Object "+ typeNameOfObject);
  				UCSYClass classOfObject = CentralTypeTable.getCentralTypeTable().getAClass(typeNameOfObject);
  				node.targetMethod = classOfObject.getAMethod(node.destinationExpression.nameList.get(1));
  			}
  		}
  		if( node.targetMethod != null )
  		{
  			if(! node.targetMethod.isRebindable() )
  			{
  				reportSSError(node.lineNo," method must be rebindable to rebind ");
  			}
  			if( node.targetMethod.isStatic())
  			{
  				reportSSError(node.lineNo,"rebindable method must not be static ");
  			}
  			if(! node.rebindMethod.methodSignature.equals(node.targetMethod.methodSignature))
  			{
  				reportSSError(node.lineNo,"Signature of target method and rebind method must be same ");
  			}
  			node.targetClassName = node.targetMethod.ownerName;
  			//UCSYClass rebindClass  = CentralTypeTable.getCentralTypeTable().getAClass( node.rebindClassName );
  			
  			String parentOfRebindClass = rebindClass.parentClassName;
  			if(! node.targetClassName.equals( parentOfRebindClass ))
  			{
  				reportSSError(node.lineNo," parent of free class must be same with target method's class ");
  			}
  			
  		}
  		else
  		{
  			reportSSError(node.lineNo,"Method not existed in target method for rebind statement");
  		}
  		
  	}
  	catch(TypeNotFoundException e)
  	{
  		reportSSError(node.lineNo,"Type "+e.typeName+" is excepted to be of free class but not found !");
  	}
  	catch(NotExceptedTypeException e)
  	{
  		reportSSError(node.lineNo,"Required free class but found "+e.foundTypeName);
  	}
  	catch(CannotResolveSymbolException e)
  	{
  		reportSSError(node.lineNo," Cannot Resolved symbol "+e);
  	}
  	catch(Exception e)
  	{
  		reportSSError(node.lineNo," Static Semantic error in rebind statement ");
  	}
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
  public Object visit(ASTSuffixDotIdentifierNode node, Object data)
  { 
  	node.lineNo = node.t.beginLine;
  	node.name   = node.t.image;
  	node.nameList.add(node.name);
  	return null;
  }
  //****************************************************************************
  
  public Object visit(ASTArrayExpressionPrimarySuffix node, Object data)
  { 
  	node.arrayIndexExpression =(ParentExpression)node.jjtGetChild(0);
  	node.arrayIndexExpression.jjtAccept(this,data);
  	node.typeName = node.arrayIndexExpression.typeName;
  	node.lineNo   = node.arrayIndexExpression.lineNo;
  	if(! (CentralTypeTable.isLowerIntegralType( node.typeName) || CentralTypeTable.isInteger( node.typeName)))
  	{
  		reportSSError(node.lineNo," Only integer expression are allowed in array index, you provides "+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(node.typeName));
  	}
  	//node.nameList.add(node.typeName);
  	
  	return null;
  }
  public Object visit(ASTArgumentsSuffix node, Object data)
  { 	
  	node.arguments = (ASTArguments)node.jjtGetChild(0);
  	node.arguments.jjtAccept(this,data);
  	node.typeName = node.arguments.typeName;
  	return null;
  }
  
  //****************************************************************************
  public Object visit(ASTNamePrimaryPrefix node, Object data)
  { 
  	///inform("Name primary Prefix "+ node.name);
  	node.name.jjtAccept(this,data);
  	node.lineNo = node.name.lineNo;
  	
  	for (int i = 0; i< node.name.nameList.size(); i++)
  	{
  		node.nameList.add(node.name.nameList.get(i));
  	}
  	
  	try
  	{
  		String currentTypeName="" ;
  		if(data instanceof UCSYClass)
  		{
  			currentTypeName = ((UCSYClass)data).className;
  		}
  		else if(data instanceof UCSYInterface)
  		{
  			currentTypeName = ((UCSYInterface)data).interfaceName;
  		}
  		else if(data instanceof UCSYMetaClass)
  		{
  			currentTypeName = ((UCSYMetaClass)data).metaClassName;
  		}
  		node.typeName = TypeResolver.resolveTypeForName(currentTypeName,(Type)data,node.name.nameList);
  	}
  	catch(InvalidAccessToMemberException e)
  	{
  		reportSSError(node.lineNo," Cannot Access Member of " +e.childName+" from context of "+e.parentName+" due to protection level " );
  		node.typeName = "i";
  	}
  	catch(CannotResolveSymbolException e)
  	{
  		reportSSError(node.lineNo," Cannot resolve symbol "+ e.symbolName +" did you forget to declare the variable ");
  		node.typeName = "i"; //Fake type
  	}
  	catch(StaticMethodCannotAccessInstanceFieldException e)
  	{
  		reportSSError(node.lineNo," Cannot access instance filed  "+ e.fieldName + " from static method");
  		node.typeName = "i"; //Fake type
  	}
  	catch(Exception e)
  	{
  		reportSSError(node.lineNo," Cannot resolve symbol "+e.getMessage());
  		node.typeName = "null";
  	}
  	
  	return null;
  }
 
  //****************************************************************************
  public Object visit(ASTAllocationPrimaryPrefix node, Object data)
  { 
  	node.allocationExpression = (ASTAllocationExpression) node.jjtGetChild(0);
    //node.jjtGetChild(0).jjtAccept(this,data);
    node.allocationExpression.jjtAccept(this,data);
    node.typeName = node.allocationExpression.typeName;
    
    node.lineNo = node.allocationExpression.lineNo;
 	node.nameList.add(node.typeName);
 	
 	///Debug.inform("ASTAllocation Primary Prefix "+ node.typeName);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTParenthesePrimaryPrefix node, Object data)
  {
  	node.parExpression = (ParentExpression)node.jjtGetChild(0);
  	node.parExpression.jjtAccept(this,data);
  	node.typeName = node.parExpression.typeName;
  	node.nameList.add(node.typeName);
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTSuperPrimaryPrefix node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	node.lineNo = node.t.beginLine;
  	ArrayList<String> nameList = new ArrayList<String>();
  	try
  	{
  		node.nameList.add("super");
  		node.typeName = TypeResolver.resolveTypeForName(theClass.className,(Type)(data),node.nameList);
  	}
  	catch(CannotResolveSymbolException e)
  	{
  		node.typeName = "null";
  		reportSSError(node.lineNo, " Cannot resolve symbol "+ e.symbolName);
  	}
  	catch(Exception e)
  	{
  		//I believe no error occurs 
  	}
 
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTThisPrimaryPrefix node, Object data)
  { 
  	UCSYClass theClass = (UCSYClass)data;
  	node.lineNo = node.t.beginLine;
  	
  	if(CommonInheritedAttribute.currentMethod.isStatic())
  	{
  		reportSSError(node.lineNo," instance reference this cannot be used in static context "+ CommonInheritedAttribute.currentMethod.methodName +" is static method");
  		
  	}
  	ArrayList<String> nameList = new ArrayList<String>();
  	try
  	{
  		node.nameList.add("this");
  		node.typeName = TypeResolver.resolveTypeForName(theClass.className,(Type)(data),node.nameList);
  	}
  	catch(CannotResolveSymbolException e)
  	{
  		reportSSError(node.lineNo, " Cannot resolve symbol "+ e.symbolName);
  		node.typeName = "null";
  	}
  	catch(Exception e)
  	{
  		//I believe no error occurs 
  	}
  	return null;
  }
  //****************************************************************************
  public Object visit(ASTLiteralPrimaryPrefix node, Object data)
  { 
  	node.literalNode =(ParentExpression) node.jjtGetChild(0);
  	 	
  	node.literalNode.jjtAccept(this,data);
  	node.typeName = node.literalNode.typeName;
  	node.lineNo    = node.literalNode.lineNo;
  	return null;
  }
  public Object visit(ASTExecuteTimesStatement node,Object data)
  {
  	node.expression.jjtAccept(this,data);
  	node.expressionType = node.expression.typeName;
  	if(! (CentralTypeTable.isLowerIntegralType( node.expressionType ) || CentralTypeTable.isInteger( node.expressionType ) ))
  	{
  		reportSSError(node.lineNo," expression in excute times statment must evaluate to integral value");
  	}
  	node.statementBody.jjtAccept(this,data);
  	return null;
  }
  
}
