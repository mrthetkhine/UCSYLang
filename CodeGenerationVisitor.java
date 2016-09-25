import java.util.*;

class CodeGenerationVisitor implements UCSYVisitor
{
	UCSY parser ;
	
	CodeGenerationVisitor(UCSY p)
	{
		parser = p;
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
  public Object visit(ASTStart node, Object data)
  {
  	for (int i = 0; i< node.typeDeclaration.size(); i++)
  	{
  		ASTTypeDeclaration typeDecl = node.typeDeclaration.get(i);
  		typeDecl.jjtAccept(this,typeDecl);
  		
  	}
	return null;
  }
  public Object visit(ASTPackageDeclaration node, Object data)
  {
		return null;
  }
  public Object visit(ASTImportDeclaration node, Object data)
  {
		return null;
  }
  public Object visit(ASTTypeDeclaration node, Object data)
  {
  		node.typeDeclaration.jjtAccept(this,data);
		return null;
  }
  public Object visit(ASTClassDeclaration node, Object data)
  {
  		UCSYClass theClass = node.theClass;
  		//inform(UCSYClassAttribute.getTextualRep(theClass.modifier)+" "+ theClass.modifier );
  		//inform(theClass.typeName);

  		
  		CodeGenerator.currentClass = theClass;
  		theClass.uCodeFile.classModifier = theClass.modifier | UCSYClassAttribute.CLASS ;
  		theClass.uCodeFile.setClassName( theClass.className );
  		theClass.uCodeFile.setSuperClassName( theClass.parentClassName);
  		
  		
  		node.classHeader.jjtAccept(this,theClass);
  		node.classBody.jjtAccept(this,theClass);
  		for (int i = 0; i< theClass.interfaceList.size(); i++)
  		{
  			theClass.uCodeFile.addInterface(theClass.interfaceList.get(i));
  		}
  		Type type = theClass;
  		theClass.uCodeFile.produceUCodeFile();
  		/*
  		for (int i = 0; i< type.uCodeFile.constantPool.constantPool.size(); i++)
  		{
  			ConstantPoolEntry entry = type.uCodeFile.constantPool.constantPool.get(i);
  			
  			if(entry instanceof StringEntry )
  			{
  				StringEntry e = (StringEntry)entry;
  				inform(i+" String "+e.value);
  			}
  			else if(entry instanceof IntegerConstantEntry)
  			{
  				IntegerConstantEntry iEntry = (IntegerConstantEntry)entry;
  				inform(i+" Integer "+ iEntry.intValue);
  			}
  			else if(entry instanceof LongConstantEntry)
  			{
  				LongConstantEntry lEntry = (LongConstantEntry)entry;
  				inform(i+" Long "+ lEntry.longValue);
  			}
  			else if(entry instanceof FloatConstantEntry)
  			{
  				FloatConstantEntry fEntry = (FloatConstantEntry)entry;
  				inform(i+" Float "+ fEntry.floatValue);
  			}
  			else if(entry instanceof DoubleConstantEntry)
  			{
  				DoubleConstantEntry dEntry = (DoubleConstantEntry)entry;
  				inform(i+" Double "+ dEntry.doubleValue);
  			}
  			else if(entry instanceof ClassEntry)
  			{
  				ClassEntry cEntry = (ClassEntry) entry;
  				inform(i+"ClassIndex "+cEntry.classNameIndex);
  			}
  			else if(entry instanceof FieldReferenceEntry)
  			{
  				FieldReferenceEntry fEntry = (FieldReferenceEntry)entry;
  				inform(i+" FieldRef class "+ fEntry.classNameIndex+ " fieldName "+ fEntry.fieldNameIndex +" filedType "+ fEntry.fieldTypeIndex);
  			}
  			else if(entry instanceof MethodReferenceEntry)
  			{
  				MethodReferenceEntry mEntry = (MethodReferenceEntry)entry;
  				inform(i+" MethodRef class "+ mEntry.classNameIndex+" methodName "+ mEntry.methodNameIndex+" methodSing "+mEntry.methodSignatureIndex);
  			}
  			else if(entry instanceof InterfaceMethodReferenceEntry)
  			{
  				InterfaceMethodReferenceEntry mEntry = (InterfaceMethodReferenceEntry)entry;
  				inform(i+" IntefaceMethodRef class "+ mEntry.classNameIndex+" methodName "+ mEntry.methodNameIndex+" methodSing "+mEntry.methodSignatureIndex);
  			}
  		}
  		
		*/
  		
		return null;
  }
  public Object visit(ASTClassHeader node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	theClass.uCodeFile.constantPool.addStringRef( theClass.className );	
  	theClass.uCodeFile.constantPool.addStringRef( theClass.parentClassName );
  	
  	for (int i = 0; i< theClass.interfaceList.size(); i++)
  	{
  		theClass.uCodeFile.constantPool.addStringRef(theClass.interfaceList.get(i));
  	}
  	if(node.adaptsClause != null)
  	{
  		node.adaptsClause.jjtAccept( this, theClass);
  	}
	return null;
  }
  public Object visit(ASTImplementsClause node, Object data)
  {
		return null;
  }
  public Object visit(ASTIdentifier node, Object data)
  {
		return null;
  }
  public Object visit(ASTConformsClause node, Object data)
  {
		return null;
  }
  public Object visit(ASTInheritsClause node, Object data)
  {
		return null;
  }
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
  public Object visit(ASTSignatureToCall node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  	node.signature.jjtAccept( this, data );
  	//node.methodCall.jjtAccept( this, data);
  	if( theClass.isAdapterClass )
  	{
  		//Generate Code For adapter
  		//Debug.inform("auto Code Genertation for " +node.method.methodName);
  		try
  		{
  			///Debug.inform("Information about auto "+node.method.methodName +"  sig"+node.method.methodSignature+" ret "+node.method.methodReturnType+" prot "+node.method.methodProtocol);
  			theClass.uCodeFile.addMethod( node.method );
  			CodeGenerator.currentMethod = node.method;
  			int sizeOfArgument = CentralTypeTable.getSizeForSignature( node.method.methodSignature);
  			CodeGenerator.currentMethod.sizeOfArgument = sizeOfArgument;
  			if(! node.method.isStatic())
  			{
  				node.method.sizeOfArgument += 1;
  			}
  			
  			
  			Type type = CentralTypeTable.getCentralTypeTable().getType( node.adaptClassName);
  			
  			if(!node.methodToCall.isStatic())
  			{
  				CodeGenerator.currentMethod.methodCode.emitLoadLocalThis();
  			}
  			node.methodCall.arguments.method = node.methodToCall;
  			node.methodCall.arguments.jjtAccept(this,data);
  			type.emitCodeForMemberMethod( node.methodToCall);
  			CodeGenerator.currentMethod.methodCode.emitReturnOnType(node.method.methodReturnType);
  		}
  		catch(Exception e)
  		{
  			Debug.inform("Code Generation Error in Adapts\' Clause signature to call at clause "+node.method.methodName +" to "+node.methodToCall.methodName );
  			e.printStackTrace();
  		}
  		
  		//CodeGenerator.currentMethod.methodCode.emitmethodc
  	}
	return null;
  }
  public Object visit(ASTClassModifier node, Object data)
  {
		return null;
  }
  public Object visit(ASTClassBody node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	
  	for (int i = 0; i<node.member.size(); i++)
  	{
  		ASTMemberDeclaration memberDecl = node.member.get(i);
  		memberDecl.jjtAccept( this, theClass);
  	}
	return null;
  }
  public Object visit(ASTFreeClassDeclaration node, Object data)
  {
  		UCSYClass theClass = node.theFreeClass;
  		//Debug.inform(UCSYClassAttribute.getTextualRep(theClass.modifier)+" "+ theClass.modifier );
  		//inform(theClass.typeName);

  		
  		CodeGenerator.currentClass = theClass;
  		theClass.uCodeFile.classModifier = theClass.modifier | UCSYClassAttribute.CLASS ;
  		theClass.uCodeFile.setClassName( theClass.className );
  		theClass.uCodeFile.setSuperClassName( theClass.parentClassName);
  		
  		
  		node.freeClassHeader.jjtAccept(this,theClass);
  		node.freeClassBody.jjtAccept(this,theClass);
  		
  		Type type = theClass;
  		theClass.uCodeFile.produceUCodeFile();	
		return null;
  }
  public Object visit(ASTFreeClassHeader node, Object data)
  {
		return null;
  }
  public Object visit(ASTFreeClassBody node, Object data)
  {
  		for (int i = 0; i< node.methodDeclarationList.size(); i++)
  		{
  			node.methodDeclarationList.get(i).jjtAccept(this,data);
  		}
		return null;
  }
  public Object visit(ASTMemberDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass) data;
  	
  	node.memberDeclaration.jjtAccept(this, theClass);
	return null;
  }
  public Object visit(ASTFunctorDeclaration node, Object data)
  {
		return null;
  }
  public Object visit(ASTInterfaceDeclaration node, Object data)
  {
  	UCSYInterface theInterface = node.theInterface;
  		//inform(UCSYClassAttribute.getTextualRep(theClass.modifier)+" "+ theClass.modifier );
  		//inform(theClass.typeName);

  	node.interfaceHeader.jjtAccept(this,node.theInterface);
  	node.interfaceBody.jjtAccept(this,node.theInterface);	
  	CodeGenerator.currentClass = theInterface;
  	theInterface.uCodeFile.classModifier = theInterface.modifier | UCSYClassAttribute.INTERFACE ;
  	theInterface.uCodeFile.setClassName( theInterface.interfaceName );
  	theInterface.uCodeFile.setSuperClassName( "Object" );
  	
  	for (int i = 0; i< theInterface.parentList.size(); i++)
  	{
  		theInterface.uCodeFile.addInterface(theInterface.parentList.get(i));
  	}
  	for (int i = 0; i< theInterface.methodList.size(); i++)
  	{
  		theInterface.uCodeFile.addMethod(theInterface.methodList.get(i));
  	}
  	theInterface.uCodeFile.produceUCodeFile();
	return null;
  }
  public Object visit(ASTInterfaceHeader node, Object data)
  {
  	    //Nothig to Generate Here
		return null;
  }
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
  public Object visit(ASTInterfaceMethodDeclaration node, Object data)
  {
  		node.interfaceMethodHeader.jjtAccept(this, data);
  		
		return null;
  }
  public Object visit(ASTMetaClassDeclaration node, Object data)
  {
		return null;
  }
  public Object visit(ASTInterfaceMethodHeader node, Object data)
  {
  	CodeGenerator.currentMethod = node.interfaceMethod;
  	int sizeOfArgument = CentralTypeTable.getSizeForSignature( node.interfaceMethod.methodSignature);
  	CodeGenerator.currentMethod.sizeOfArgument = sizeOfArgument;
  	  	
  	node.interfaceMethod.sizeOfArgument += 1;
  	
  	//Debug.inform(node.interfaceMethod.methodName+" "+ node.interfaceMethod.methodSignature+" "+ node.interfaceMethod.methodReturnType);
  	
  	CodeGenerator.currentMethod.methodCode.emitReturn();
	
  	CodeGenerator.currentMethod.sizeOfLocalVar = sizeOfArgument +1;
  	//node.interfaceMethod.methodCode.code =new byte[0];
	///inform("Total No of local var size "+ CodeGenerator.currentMethod.sizeOfLocalVar);
	
  	///SymbolTable.getSymbolTable().closeScope();
		return null;
  }
  public Object visit(ASTAbstractMethodDeclaration node, Object data)
  {
  	
  	UCSYClass theClass = (UCSYClass)data;
  	theClass.uCodeFile.addMethod( node.abstractMethod );
  	CodeGenerator.currentMethod = node.abstractMethod;
  	
  	
  	
  	//inform(" Modifier "+ UCSYClassAttribute.getTextualRep( node.method.modifier) );
  	
  	int sizeOfArgument = CentralTypeTable.getSizeForSignature( node.abstractMethod.methodSignature);
  	CodeGenerator.currentMethod.sizeOfArgument = sizeOfArgument;
  	if(! node.abstractMethod.isStatic())
  	{
  		node.abstractMethod.sizeOfArgument += 1;
  	}
  	
  	
  	if(CentralTypeTable.isVoid( node.abstractMethod.methodReturnType ))  	
	{
		///Debug.inform("Ha Ha What Happen");
		CodeGenerator.currentMethod.methodCode.emitReturnIfNot();
	}
  	CodeGenerator.currentMethod.sizeOfLocalVar = SymbolTable.getSymbolTable().maxNoOfLocalVariable;
	///inform("Total No of local var size "+ CodeGenerator.currentMethod.sizeOfLocalVar);
	
  	

	return null;
  }
  public Object visit(ASTMetaClassHeader node, Object data)
  {
		return null;
  }
  public Object visit(ASTMetaClassBody node, Object data)
  {
		return null;
  }
  public Object visit(ASTAbstractConstructor node, Object data)
  {
		return null;
  }
  public Object visit(ASTAccessModifier node, Object data)
  {
		return null;
  }
  public Object visit(ASTMethodModifier node, Object data)
  {
		return null;
  }
  public Object visit(ASTFieldModifier node, Object data)
  {
		return null;
  }
  public Object visit(ASTConstructorDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	theClass.uCodeFile.addMethod( node.constructorMethod );
  	CodeGenerator.currentMethod =node.constructorMethod;
  	
  	SymbolTable.getSymbolTable().openScope();
  	SymbolTable.getSymbolTable().startMethod();
  	try
  	{
  		if(!node.constructorMethod.isStatic())
  		{
  			SymbolTableEntry entry = new SymbolTableEntry("this",theClass.className);
  			SymbolTable.getSymbolTable().insert(entry);
  		}
  	}
  	catch(Exception e)
  	{
  	}
  	node.formalParameters.jjtAccept(this,data);
  	
  	
  	int sizeOfArgument = CentralTypeTable.getSizeForSignature( node.constructorMethod.methodSignature);
  	CodeGenerator.currentMethod.sizeOfArgument = sizeOfArgument;
  	
  	if(! node.constructorMethod.isStatic())
  	{
  		node.constructorMethod.sizeOfArgument += 1;
  		
  	}
  	try
  	{
  		if(!node.constructorMethod.isStatic())
  		{
  			SymbolTableEntry entry = new SymbolTableEntry("this",theClass.className);
  			SymbolTable.getSymbolTable().insert(entry);
  		}
  	}
  	catch(Exception e)
  	{
  	}
  	
  	if(node.explicitConstructorCall != null)
  	{
  		node.explicitConstructorCall.jjtAccept(this,data);
  	}
  	for (int i = 0; i<node.statementList.size(); i++)
  	{
  		node.statementList.get(i).jjtAccept(this,data);
  	}
  	if(CentralTypeTable.isVoid( node.constructorMethod.methodReturnType ))  	
	{
		///Debug.inform("Ha Ha What Happen");
		CodeGenerator.currentMethod.methodCode.emitReturnIfNot();
	}
  	CodeGenerator.currentMethod.sizeOfLocalVar = SymbolTable.getSymbolTable().maxNoOfLocalVariable;
  	
	///inform("Total No of local var size "+ CodeGenerator.currentMethod.sizeOfLocalVar);
	
  	SymbolTable.getSymbolTable().closeScope();
	return null;
  }
  public Object visit(ASTExplicitConstructorCall node, Object data)
  {
		return null;
  }
  public Object visit(ASTFieldDeclaration node, Object data)
  {
  	node.normalFieldDeclaration.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTNormalFieldDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	theClass.uCodeFile.addField(node.normalField);
	return null;
  }
  public Object visit(ASTDelegateDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	 	
  	node.type.jjtAccept(this, data);
  	 	
	node.delegateClause.jjtAccept( this, data);
	//Debug.inform("Generate code for delegate ");
		return null;
  }
  public Object visit(ASTHandleAllClause node, Object data)
  {
  		CodeGenerator.currentClass.uCodeFile.addField(node.delegateField);
  		
  		for (int i = 0; i< node.allMethod.size(); i++)
  		{
  			Method m = node.allMethod.get(i);
  			//Debug.inform("Producing code for name "+ m.methodName + " signature "+ m.methodSignature 
  			//+" return type "+ m.methodReturnType +" owner "+ m.ownerName );
  			///Debug.inform("Owener Name "+ node.delegateField.fieldType +"  field name "+ node.delegateField.fieldName );
  			
  			CodeGenerator.currentMethod = m;
  			m.ownerName = node.delegateField.fieldType;
  			CodeGenerator.emitCodeForHandleAll( node.delegateField );
  			CodeGenerator.currentClass.uCodeFile.addMethod( m );
  			
  			//************* Automatically Generate code for delegaate handles all clause ***********
  			//Delegate variable cannot be static variable
  			
  		}
		return null;
  }
  public Object visit(ASTForwardsClause node, Object data)
  {
		return null;
  }
  public Object visit(ASTDecoratesClause node, Object data)
  {
		return null;
  }
  public Object visit(ASTBeforeCallOrAfterCall node, Object data)
  {
		return null;
  }
  public Object visit(ASTAfterCall node, Object data)
  {
		return null;
  }
  public Object visit(ASTBeforeCall node, Object data)
  {
		return null;
  }
  public Object visit(ASTMethodSignature node, Object data)
  {
		return null;
  }
  public Object visit(ASTMethodCall node, Object data)
  {
  		node.arguments.jjtAccept(this,data);
		return null;
  }
  public Object visit(ASTVariableDeclarator node, Object data)
  {
  		if(node.variableInitializer != null)
  		{
  			node.variableInitializer.jjtAccept(this,data);
  			if( CentralTypeTable.isPrimitiveType( node.typeName))
  			{
  				///Debug.inform("Yeadh "+node.variableInitializer.typeName+" to "+node.typeName);
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.variableInitializer.typeName,node.typeName);
  			}
  			node.variableDeclaratorId.jjtAccept(this,data);
  			int size = CentralTypeTable.getSizeOfType(node.typeName);
  			CodeGenerator.changeLoadToStore(size);
  		}
		return null;
  }
  public Object visit(ASTVariableDeclaratorId node, Object data)
  {
  		ArrayList<String> nameList = new ArrayList<String>();
  		nameList.add(node.variableName);
  		CodeGenerator.generateCodeForFirstFieldNameList(nameList);
		return null;
  }
  public Object visit(ASTVariableInitializer node, Object data)
  {
  		node.expression.jjtAccept(this,data);
		return null;
  }
  public Object visit(ASTArrayInitializer node, Object data)
  {
		return null;
  }
  public Object visit(ASTMethodDeclaration node, Object data)
  {
  	UCSYClass theClass = (UCSYClass)data;
  	theClass.uCodeFile.addMethod( node.method );
  	CodeGenerator.currentMethod = node.method;
  	
  	SymbolTable.getSymbolTable().openScope();
  	SymbolTable.getSymbolTable().startMethod();
  	
  	try
  	{
  		if(!node.method.isStatic())
  		{
  			SymbolTableEntry entry = new SymbolTableEntry("this",theClass.className);
  			SymbolTable.getSymbolTable().insert(entry);
  		}
  	}
  	catch(Exception e)
  	{
  	}
  	//inform(" Modifier "+ UCSYClassAttribute.getTextualRep( node.method.modifier) );
  	node.methodHeader.jjtAccept(this,theClass);
  	int sizeOfArgument = CentralTypeTable.getSizeForSignature( node.method.methodSignature);
  	CodeGenerator.currentMethod.sizeOfArgument = sizeOfArgument;
  	if(! node.method.isStatic())
  	{
  		node.method.sizeOfArgument += 1;
  	}
  	
  	node.block.jjtAccept(this,data);
  	if(CentralTypeTable.isVoid( node.method.methodReturnType ))  	
	{
		
		CodeGenerator.currentMethod.methodCode.emitReturnIfNot();
	}
  	CodeGenerator.currentMethod.sizeOfLocalVar = SymbolTable.getSymbolTable().maxNoOfLocalVariable;
	///inform("Total No of local var size "+ CodeGenerator.currentMethod.sizeOfLocalVar);
	
  	SymbolTable.getSymbolTable().closeScope();
	return null;
  }
  public Object visit(ASTMethodHeader node, Object data)
  {
  	node.returnType.jjtAccept(this,data);
  	node.formalParameters.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTFormalParameters node, Object data)
  {
  	for (int i = 0; i< node.formalParameterList.size(); i++)
  	{
  		ASTFormalParameter formalParameter = node.formalParameterList.get(i);
  		formalParameter.jjtAccept( this, data);
  						
  	}
	return null;
  }
  public Object visit(ASTFormalParameter node, Object data)
  {
  	String variableName = node.variableDeclaratorId.variableName;
  	SymbolTableEntry variableEntry = new SymbolTableEntry( variableName,node.typeName);
  	try
  	{
  		SymbolTable.getSymbolTable().insert( variableEntry );
  	}
  	catch(Exception e)
  	{
  		
  	}
  	
	return null;
  }
  public Object visit(ASTReturnType node, Object data)
  {
		return null;
  }
  public Object visit(ASTVoidType node, Object data)
  {
		return null;
  }
  public Object visit(ASTType node, Object data)
  {
		return null;
  }
  public Object visit(ASTPrimitiveType node, Object data)
  {
		return null;
  }
  public Object visit(ASTReferenceType node, Object data)
  {
		return null;
  }
  public Object visit(ASTMetaInstanceType node, Object data)
  {
		return null;
  }
  public Object visit(ASTClassType node, Object data)
  {
		return null;
  }
  public Object visit(ASTArrayType node, Object data)
  {
		return null;
  }
  public Object visit(ASTExpression node, Object data)
  {
  	
  	node.exp.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTOrNode node, Object data)
  {
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	
  	CodeGenerator.currentMethod.methodCode.emitOr();
	return null;
  }
  public Object visit(ASTAndNode node, Object data)
  {
  	node.opOne.jjtAccept(this,data);
  	node.opTwo.jjtAccept(this,data);
  	
  	
  	CodeGenerator.currentMethod.methodCode.emitAnd();
		return null;
  }
  public Object visit(ASTEqualNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitEqualType( node.typeName );
	return null;
  }
  public Object visit(ASTNotEqualNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitNotEqualType( node.typeName );
		return null;
  }
  public Object visit(ASTInstanceOfExpression node, Object data)
  {
  		node.opOne.jjtAccept(this,data);
  		//Debug.inform(" Type of "+node.type.typeName);
  		String checkType = node.type.typeName;
  		CodeGenerator.currentClass.uCodeFile.constantPool.addClassRef(checkType);
  		int classIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getClassIndex( checkType);
  		CodeGenerator.currentMethod.methodCode.emitInstanceOf( classIndex );
		return null;
  }
  public Object visit(ASTGTNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitGTType( node.typeName );
	return null;
  }
  public Object visit(ASTGTEqualNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitGTEqualType( node.typeName );
	 return null;
  }
  public Object visit(ASTLTNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitLTType( node.typeName );
	 
		return null;
  }
  public Object visit(ASTLTEqualNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitLTEqualType( node.typeName );
	 return null;
  }
  public Object visit(ASTPlusNode node, Object data)
  {
  	 //Debug.inform("ADDDDE");
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	   	 
  	 CodeGenerator.currentMethod.methodCode.emitAddType( node.typeName );
  	   	 
	 return null;
  }
  public Object visit(ASTSubtractNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitSubType( node.typeName );
	 return null;
  }
  public Object visit(ASTMultiplyNode node, Object data)
  {
  	 node.opOne.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	 
  	 node.opTwo.jjtAccept(this,data);
  	 CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	 
  	 CodeGenerator.currentMethod.methodCode.emitMultType( node.typeName );
	return null;
  }
  public Object visit(ASTDivsionNode node, Object data)
  {
  	node.opOne.jjtAccept(this,data);
  	CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	
  	node.opTwo.jjtAccept(this,data);
  	CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	
  	CodeGenerator.currentMethod.methodCode.emitDivType( node.typeName );
	return null;
  }
  public Object visit(ASTModulusNode node, Object data)
  {
  	node.opOne.jjtAccept(this,data);
  	CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  	
  	node.opTwo.jjtAccept(this,data);
  	CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opTwo.typeName,node.typeName);
  	
  	CodeGenerator.currentMethod.methodCode.emitModType( node.typeName );
	return null;
  }
  public Object visit(ASTUnaryPlusNode node, Object data)
  {
		return null;
  }
  public Object visit(ASTUnaryMinusNode node, Object data)
  {
  		CodeGenerator.currentClass.uCodeFile.constantPool.addIntegerRef(0);
  		int index = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex(0);
  		CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(index,1);
  		CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( "i",node.typeName);
  		node.opOne.jjtAccept(this,data);
  		CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.opOne.typeName,node.typeName);
  		
  		CodeGenerator.currentMethod.methodCode.emitSubType(node.typeName);
		return null;
  }
  public Object visit(ASTNotNode node, Object data)
  {
  		//Debug.inform("ProDUCCCCCCCCCCCCCCCCCCCCCCCC");
  		node.opOne.jjtAccept(this,data);
  		
  		CodeGenerator.currentMethod.methodCode.emitNot();
		return null;
  }
  public Object visit(ASTCastNode node, Object data)
  {	
  		node.opOne.jjtAccept(this,data);
		return null;
  }
  public Object visit(ASTCastExpression node, Object data)
  {
  		//Debug.inform("CAST Expression ");
  		node.expression.jjtAccept(this,data);
  		
  		if( CentralTypeTable.isPrimitiveType(node.castDestination))
  		{
  			CodeGenerator.currentMethod.methodCode.emitConvertPrimitive(node.castSource,node.castDestination);
  		}
  		else
  		{
  			
  		}
		return null;
  }
  public Object visit(ASTPrimaryExpression node, Object data)
  {
  	
  	if(node.primaryExpression instanceof ASTPrimarySuffix)
  	{
  		//inform("Involved method call and array expression ");
  		CodeGenerator.createNewAllPrimaryExpression();
  		
  		node.primaryExpression.jjtAccept(this,data);
  		
  		///inform("HOHO"+CodeGenerator.allPrimaryExpression.toString() + CodeGenerator.allPrimaryExpression.size());
  		ArrayList<ParentName> reStructuredNode = CodeGenerator.reStructureAndGeneratePrimaryExpression();
  		
  		////inform("Reconstruct "+ reStructuredNode.toString());
  		
  		String previousTypeName = CodeGenerator.currentClass.typeName;
  		for (int i = 0; i< reStructuredNode.size(); i++)
  		{
  			ParentName exp = reStructuredNode.get(i);
  			if(exp instanceof ASTName)
  			{
  				
  				ASTName name = (ASTName)exp;
  				///Debug.inform("Generate for simple name "+ name.nameList);
  				if(i==0)
  				{
  					previousTypeName = CodeGenerator.generateCodeForFirstFieldNameList(name.nameList);
  				}
  				else
  				{
  					previousTypeName = CodeGenerator.generateCodeForFieldNameList(name.nameList,previousTypeName);
  				}
  			}
  			else if(exp instanceof ASTNamePrimaryPrefix)
  			{
  				ASTNamePrimaryPrefix name = (ASTNamePrimaryPrefix)exp;
  				///Debug.inform("Generate for name "+name.nameList);
  				if(i==0)
  				{
  					previousTypeName = CodeGenerator.generateCodeForFirstFieldNameList(name.nameList);
  				}
  				else
  				{
  					previousTypeName = CodeGenerator.generateCodeForFieldNameList(name.nameList,previousTypeName);
  				}
  			}
  			else if(exp instanceof ASTArgumentsSuffix)
  			{
  				ASTArgumentsSuffix argumentSuffix = (ASTArgumentsSuffix)exp;
  				
  				if(i==0)
  				{
  					if(!argumentSuffix.method.isStatic())
  					{
  						CodeGenerator.currentMethod.methodCode.emitLoadLocalThis();
  					}
  					
  				}
  				argumentSuffix.arguments.jjtAccept(this,data);
  				
  				try
  				{
  					///Debug.inform("Invoking method "+argumentSuffix.method.methodName);
  					//Debug.inform("Select "+ argumentSuffix.method.methodReturnType);
  					Type type = CentralTypeTable.getCentralTypeTable().getType(previousTypeName );
  					previousTypeName = type.emitCodeForMemberMethod( argumentSuffix.method);
  				}
  				catch(Exception e)
  				{
  					Debug.inform("Code Generation error ");
  					e.printStackTrace();
  				}
  				//CodeGenerator.inform("Method call "+ previousTypeName + " of "+ argumentSuffix.method.methodName);
  			}
  			else if(exp instanceof ASTParenthesePrimaryPrefix)
  			{
  				ASTParenthesePrimaryPrefix parenthese = (ASTParenthesePrimaryPrefix)exp;
  			
  				parenthese.jjtAccept(this,data);
  				
  				previousTypeName = parenthese.typeName;
  				///Debug.inform("Type Name of Parenthese "+parenthese.typeName);
  			}
  			else if(exp instanceof ASTAllocationPrimaryPrefix)
  			{
  				ASTAllocationPrimaryPrefix allocationPrimaryPrefix = (ASTAllocationPrimaryPrefix)exp;
  				allocationPrimaryPrefix.jjtAccept(this,data);
  				previousTypeName = allocationPrimaryPrefix.typeName;
  			}
  			else if(exp instanceof ASTArrayExpressionPrimarySuffix)
  			{
  				ASTArrayExpressionPrimarySuffix arraySuffix = (ASTArrayExpressionPrimarySuffix)exp;
  				arraySuffix.jjtAccept(this,data);
  				
  				previousTypeName = previousTypeName.substring(1,previousTypeName.length());
  				//Debug.inform("Previous type Name "+previousTypeName);
  				arraySuffix.arrayIndexExpression.jjtAccept(this,data);
  				CodeGenerator.currentMethod.methodCode.emitGetElementType(previousTypeName);
  				
  			}
  			else
  			{
  				Debug.inform("CODE GENERATION ERROR IN ASTPrimaryExpression ");
  			}
  		}
  	}
  	else // Only consist of PrimaryPrefix
  	{
  		///inform("Fucking og ");
  		//Avoid double generaton because of parenthesePrimaryPrefix
  		 		
  		if(node.primaryExpression instanceof ASTNamePrimaryPrefix)
  		{
  			///inform("OK Generate for Name "+node.primaryExpression.nameList.toString());	
  			node.primaryExpression.jjtAccept(this,data);
  			
  			CodeGenerator.generateCodeForFirstFieldNameList( node.primaryExpression.nameList);
  		}
  		else if(node.primaryExpression instanceof ASTThisPrimaryPrefix)
  		{
  			CodeGenerator.currentMethod.methodCode.emitLoadLocalThis();
  		}
  		else if(node.primaryExpression instanceof ASTSuperPrimaryPrefix)
  		{
  		
  		}
  		else if(node.primaryExpression instanceof ASTParenthesePrimaryPrefix)
  		{
  			
  			
  			node.primaryExpression.jjtAccept(this,data);
  			
  		}
  		else if(node.primaryExpression instanceof ASTLiteralPrimaryPrefix)
  		{
  			node.primaryExpression.jjtAccept(this,data);
  		}
  		else if(node.primaryExpression instanceof ASTAllocationPrimaryPrefix)
  		{
  			
  			node.primaryExpression.jjtAccept(this,data);
  		}
  		else
  		{
  			Debug.inform("CODE GENERATION ERROR");
  		}
  		
  		
  	}
  	
  	
	return null;
  }
  public Object visit(ASTPrimarySuffix node, Object data)
  {
  	
  	node.firstPart.jjtAccept(this,data);
  	node.secondPart.jjtAccept(this,data);	
  	
	return null;
  }
  public Object visit(ASTNamePrimaryPrefix node, Object data)
  {
  	node.name.jjtAccept(this,data);
  	for (int i = 0; i< node.name.nameList.size(); i++)
  	{
  		node.codeGenNameList.add(node.name.nameList.get(i));
  	}
  	//inform("Add name");
  	//CodeGenerator.generateCodeFor(node.name.nameList);
  	CodeGenerator.allPrimaryExpression.add(node);
	return null;
  }
  public Object visit(ASTLiteralPrimaryPrefix node, Object data)
  {
  	
  	///Debug.inform("LIteal primary prefix ");
  	node.literalNode.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTThisPrimaryPrefix node, Object data)
  {
  	//inform("Add this");
  	CodeGenerator.allPrimaryExpression.add(node);
	return null;
  }
  public Object visit(ASTSuperPrimaryPrefix node, Object data)
  {
  	CodeGenerator.allPrimaryExpression.add(node);
	return null;
  }
  public Object visit(ASTParenthesePrimaryPrefix node, Object data)
  {
  	//inform("Add paren ");
  	
  	node.parExpression.jjtAccept(this,data);
  	
  	//CodeGenerator.primaryExpression.allPrimaryExpression.add(node);
	return null;
  }
  public Object visit(ASTAllocationPrimaryPrefix node, Object data)
  {
  	//inform("add allocation ");
  	node.allocationExpression.jjtAccept(this,data);
  	CodeGenerator.allPrimaryExpression.add(node);
	return null;
  }
  public Object visit(ASTArrayExpressionPrimarySuffix node, Object data)
  {
  	//inform("add ar[]");
  	CodeGenerator.allPrimaryExpression.add(node);
	return null;
  }
  public Object visit(ASTSuffixDotIdentifierNode node, Object data)
  {
  	CodeGenerator.allPrimaryExpression.add(node);
  	//inform("add "+ node.name);
  	
	return null;
  }
  public Object visit(ASTArgumentsSuffix node, Object data)
  {
  	node.typeName = node.method.methodReturnType;
  	///node.codeGenNameList.add(node.typeName);
  	///inform("add Argument suffix "+ node.codeGenNameList.toString());
  	CodeGenerator.allPrimaryExpression.add(node);
  	
	return null;
  }
  public Object visit(ASTName node, Object data)
  {
  	//inform("add name");
  	//CodeGenerator.allPrimaryExpression.add( node );
	return null;
  }
  public Object visit(ASTIntegerLiteral node, Object data)
  {
  	int cpIndex=-1;
  	if( node.typeName.equals("l"))
  	{
  		cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getLongIndex(node.value);
  	}
  	else
  	{
  		cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex((int)node.value);
  	}
  	
  	
  	
  	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,1);
	return null;
  }
  public Object visit(ASTFloatLiteral node, Object data)
  {
  	int cpIndex=-1;
  	
  	if( node.typeName.equals("d"))
  	{
  	//	CodeGenerator.currentClass.uCodeFile.constantPool.addDoubleRef(node.value);
  		CodeGenerator.currentClass.uCodeFile.constantPool.addDoubleRef( node.value );
  		cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getDoubleIndex(node.value);
  	}
  	else
  	{
  		
  		//Debug.inform("Geeting form "+ node.value);
  		CodeGenerator.currentClass.uCodeFile.constantPool.addFloatRef((float) node.value );
  		cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getFloatIndex((float)node.value);
  	}
  	
  	
  	//inform("Load CPOOL Float "+ cpIndex);
  	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,1);
	return null;
  }
  public Object visit(ASTCharLiteral node, Object data)
  {
  	int cpIndex=-1;
  	
  	cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex( node.value );
  	//inform("Load CPOOL "+ cpIndex);
  	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,1);
	return null;
  }
  public Object visit(ASTStringLiteral node, Object data)
  {
  	int cpIndex=-1;
  	
  	
  	cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getStringIndex( node.value );
  	
  	
  	  	///inform("Load CPOOL String "+ cpIndex);
	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,1);
	return null;
  }
  public Object visit(ASTDoubleLiteral node, Object data)
  {
  	int cpIndex=-1;
  	
  	cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getDoubleIndex( node.value );
  	
  	//inform("Load CPOOL "+ cpIndex);
  	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,2);
	return null;
  }
  
  public Object visit(ASTLongLiteral node, Object data)
  {
  	int cpIndex=-1;
  	cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getLongIndex( node.value );
  	
  	//inform("Load CPOOL "+ cpIndex);
  	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,2);
	return null;
  }
  public Object visit(ASTBooleanLiteral node, Object data)
  {
  	int cpIndex=-1;
  	int value = 0;
  	if( node.value == true)
  	{
  		value = 1;
  	}
  	////Debug.inform("CODE GEN BOOL VISIT ");
  	CodeGenerator.currentClass.uCodeFile.constantPool.addIntegerRef(value);
  	cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex( value );
  	
  	///inform("Load CPOOL "+ cpIndex);
	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,1);
	return null;
  }
  public Object visit(ASTNullLiteral node, Object data)
  {
  	int cpIndex=-1;
  	int value = 0;
  	
  	
  	cpIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex(value);
  	
  	//inform("Load CPOOL "+ cpIndex);
	CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(cpIndex,1);
	return null;
  }
  public Object visit(ASTAllocationExpression node, Object data)
  {
  	node.allocationNode.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTArrayAllocation node, Object data)
  {
  		node.arrayAllocationNode.jjtAccept(this,data);
  		//Debug.inform("ArrayAllocation "+node.typeName);
		return null;
  }
  public Object visit(ASTInstanceArrayAllocation node, Object data)
  {
  		node.dimensionParameter.jjtAccept(this,data);
  		CodeGenerator.currentClass.uCodeFile.constantPool.addClassRef( node.typeName );
  		
  		int classIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getClassIndex( node.typeName );
  		///Debug.inform( "Constructor is "+ node.constructorClassName +" "+classType.className +" "+ classIndex);
  		int noOfDimension = CentralTypeTable.getNoOfDimension(node.typeName);
  		//Debug.inform("Create Array "+ noOfDimension);
  		CodeGenerator.currentMethod.methodCode.emitCreateArray(classIndex,noOfDimension);
		return null;
  }
  public Object visit(ASTDimensionParameter node, Object data)
  {
  		for (int i = 0; i< node.expressionList.size(); i++)
  		{
  			node.expressionList.get(i).jjtAccept(this,data);
  		}
		return null;
  }
  public Object visit(ASTPrimitiveArrayAllocation node, Object data)
  {
  		
  		node.dimensionParameter.jjtAccept(this,data);
  		CodeGenerator.currentClass.uCodeFile.constantPool.addClassRef( node.typeName );
  		
  		int classIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getClassIndex( node.typeName );
  		///Debug.inform( "Constructor is "+ node.constructorClassName +" "+classType.className +" "+ classIndex);
  		int noOfDimension = CentralTypeTable.getNoOfDimension(node.typeName);
  		//Debug.inform("Create Array "+ noOfDimension);
  		CodeGenerator.currentMethod.methodCode.emitCreateArray(classIndex,noOfDimension);
		return null;
  }
  public Object visit(ASTInstanceAllocation node, Object data)
  {
  	///Debug.inform("Instance Allocation ");
  	
  	///Debug.inform(node.constructorMethod.methodReturnType+" "+ node.constructorMethod.methodSignature);
  	try
  	{
  		UCSYClass classType = CentralTypeTable.getCentralTypeTable().getAClass( node.constructorClassName );
  		//Debug.inform(" current Class "+ CodeGenerator.currentClass.constantPool );
  		CodeGenerator.currentClass.uCodeFile.constantPool.addClassRef( node.constructorClassName );
  		
  		int classIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getClassIndex( node.constructorClassName );
  		///Debug.inform( "Constructor is "+ node.constructorClassName +" "+classType.className +" "+ classIndex);
  		CodeGenerator.currentMethod.methodCode.emitCreateObject( classIndex );
  		node.arguments.jjtAccept(this,data);
  		classType.emitCodeForMemberMethod( node.constructorMethod );
  	}
  	catch(Exception e)
  	{
  		e.printStackTrace();
  		Debug.inform("CODE GENERATION ERROR IN INSTANCE ALLOCATION" );
  	}
	return null;
  }
  public Object visit(ASTArguments node, Object data)
  {
  	
  	//node.argumentsList.method = node.method;
  	if( node.argumentsList!= null)
  	{
  		node.argumentsList.method = node.method;
  		node.argumentsList.jjtAccept(this,data);
  		
  	}
	return null;
  }
  public Object visit(ASTArgumentsList node, Object data)
  {
  	if( node.expressionList.size()>0)
  	{
  		///Debug.inform("ASTArguments List "+ node.method);
  		node.method.splitParameter();
  		String parameters[] = node.method.parameters;
  		for (int i = 0; i< node.expressionList.size(); i++)
  		{
  			ASTExpression exp = node.expressionList.get(i);
  			exp.jjtAccept(this,data);
  		
  			if(CentralTypeTable.isPrimitiveType( exp.typeName ))
  			{
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( exp.typeName,parameters[i]);
  			}
  			else
  			{
  			}	
  		}
  	}
	return null;
  }
  public Object visit(ASTStatement node, Object data)
  {
  	node.pStatement.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTLabelStatement node, Object data)
  {
		return null;
  }
  public Object visit(ASTBreakStatement node, Object data)
  {
  	int breakCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  	
  	CodeGenerator.currentMethod.methodCode.emitJump(-1);
  	
  	CommonInheritedAttribute.breakIndex.push(breakCodeIndex);
  	int no = CommonInheritedAttribute.noOfBreak.pop()+1;
  	CommonInheritedAttribute.noOfBreak.push( no );
  	
	return null;
  }
  public Object visit(ASTMethodCallStatement node, Object data)
  {
  	node.primaryExpression.jjtAccept(this,data);
	return null;
  }
  public Object visit(ASTAssignmentStatement node, Object data)
  {
  	if(node.assignmentOperator.operator == UCSYOperator.ASSIGNMENT )
  	{
  		node.expression.jjtAccept(this,data);
  		if( CentralTypeTable.isPrimitiveType( node.primaryExpression.typeName ))
  		{
  			CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,node.primaryExpression.typeName);
  		}
  	
  	///CodeGenerator.emitStore = true;
  	//Debug.inform("FROM "+ node.sourceType+" to "+node.destinationType );
  		node.primaryExpression.jjtAccept(this,data);
  	
  	//inform("Assignment Size "+node.primaryExpression.typeName);
  		CodeGenerator.currentMethod.methodCode.currentStackLocation = CodeGenerator.currentMethod.methodCode.oldStackLoacation;
  		CodeGenerator.currentMethod.methodCode.maxStack = CodeGenerator.currentMethod.methodCode.oldMax;
  		int size = CentralTypeTable.getSizeOfType( node.primaryExpression.typeName);
  		CodeGenerator.changeLoadToStore(size);
  	//CodeGenerator.emitStore = false;
  	
  	///Debug.inform("Generate Code For Assignment");
  }
  else
  {
  		int size = -1;
  	switch(node.assignmentOperator.operator)
  	{
  		case PLUS_ASSIGN:
  			node.primaryExpression.jjtAccept(this,data);
  			node.expression.jjtAccept(this,data);
  			if( CentralTypeTable.isPrimitiveType( node.primaryExpression.typeName ))
  			{
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,node.primaryExpression.typeName);
  			}
  			CodeGenerator.currentMethod.methodCode.emitAddType(node.primaryExpression.typeName);
  			
  			node.primaryExpression.jjtAccept(this,data);
  			CodeGenerator.currentMethod.methodCode.currentStackLocation = CodeGenerator.currentMethod.methodCode.oldStackLoacation;
  			CodeGenerator.currentMethod.methodCode.maxStack = CodeGenerator.currentMethod.methodCode.oldMax;
  			size = CentralTypeTable.getSizeOfType( node.primaryExpression.typeName);
  			CodeGenerator.changeLoadToStore(size);
  		break;
  		
  		case MINUS_ASSIGN:
  			node.primaryExpression.jjtAccept(this,data);
  			node.expression.jjtAccept(this,data);
  			if( CentralTypeTable.isPrimitiveType( node.primaryExpression.typeName ))
  			{
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,node.primaryExpression.typeName);
  			}
  			CodeGenerator.currentMethod.methodCode.emitSubType(node.primaryExpression.typeName);
  			
  			node.primaryExpression.jjtAccept(this,data);
  			CodeGenerator.currentMethod.methodCode.currentStackLocation = CodeGenerator.currentMethod.methodCode.oldStackLoacation;
  			CodeGenerator.currentMethod.methodCode.maxStack = CodeGenerator.currentMethod.methodCode.oldMax;
  			size = CentralTypeTable.getSizeOfType( node.primaryExpression.typeName);
  			CodeGenerator.changeLoadToStore(size);
  		break;
  		
  		case MULT_ASSIGN:
  			node.primaryExpression.jjtAccept(this,data);
  			node.expression.jjtAccept(this,data);
  			if( CentralTypeTable.isPrimitiveType( node.primaryExpression.typeName ))
  			{
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,node.primaryExpression.typeName);
  			}
  			CodeGenerator.currentMethod.methodCode.emitMultType(node.primaryExpression.typeName);
  			
  			node.primaryExpression.jjtAccept(this,data);
  			CodeGenerator.currentMethod.methodCode.currentStackLocation = CodeGenerator.currentMethod.methodCode.oldStackLoacation;
  			CodeGenerator.currentMethod.methodCode.maxStack = CodeGenerator.currentMethod.methodCode.oldMax;
  			size = CentralTypeTable.getSizeOfType( node.primaryExpression.typeName);
  			CodeGenerator.changeLoadToStore(size);
  		break;
  		
  		case DIV_ASSIGN:
  			node.primaryExpression.jjtAccept(this,data);
  			node.expression.jjtAccept(this,data);
  			if( CentralTypeTable.isPrimitiveType( node.primaryExpression.typeName ))
  			{
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,node.primaryExpression.typeName);
  			}
  			CodeGenerator.currentMethod.methodCode.emitDivType(node.primaryExpression.typeName);
  			
  			node.primaryExpression.jjtAccept(this,data);
  			CodeGenerator.currentMethod.methodCode.currentStackLocation = CodeGenerator.currentMethod.methodCode.oldStackLoacation;
  			CodeGenerator.currentMethod.methodCode.maxStack = CodeGenerator.currentMethod.methodCode.oldMax;
  			size = CentralTypeTable.getSizeOfType( node.primaryExpression.typeName);
  			CodeGenerator.changeLoadToStore(size);
  		break;
  		
  		case MOD_ASSIGN:
  			node.primaryExpression.jjtAccept(this,data);
  			node.expression.jjtAccept(this,data);
  			if( CentralTypeTable.isPrimitiveType( node.primaryExpression.typeName ))
  			{
  				CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,node.primaryExpression.typeName);
  			}
  			CodeGenerator.currentMethod.methodCode.emitModType(node.primaryExpression.typeName);
  			
  			node.primaryExpression.jjtAccept(this,data);
  			CodeGenerator.currentMethod.methodCode.currentStackLocation = CodeGenerator.currentMethod.methodCode.oldStackLoacation;
  			CodeGenerator.currentMethod.methodCode.maxStack = CodeGenerator.currentMethod.methodCode.oldMax;
  			size = CentralTypeTable.getSizeOfType( node.primaryExpression.typeName);
  			CodeGenerator.changeLoadToStore(size);
  		break;
  		default:
  			Debug.inform("Invalid assignment operator");
  	}
  }
  	
	return null;
  }
  public Object visit(ASTAssignmentOperator node, Object data)
  {
		return null;
  }
  public Object visit(ASTBlock node, Object data)
  {
  	SymbolTable.getSymbolTable().openScope();
  	for (int i = 0; i< node.statementList.size(); i++)
  	{
  		ASTStatement statement = node.statementList.get(i);
  		statement.jjtAccept( this, data);
  	}
  	SymbolTable.getSymbolTable().closeScope();
	return null;
  }
  public Object visit(ASTEmptyStatement node, Object data)
  {
		return null;
  }
  public Object visit(ASTSwitchStatement node, Object data)
  {
  		CommonInheritedAttribute.noOfBreak.push(0);
  		SymbolTable.getSymbolTable().openScope();
  		
  		//Debug.inform("Switch Statement");
  		for (int i = 0; i< node.caseStatementList.size(); i++)
  		{
  			ASTCaseStatement caseStatement = node.caseStatementList.get(i);
  			caseStatement.switchExpression = node.expression;
  			caseStatement.parentSwitch     = node;
  			caseStatement.jjtAccept(this,data);
  		}
  		if( node.defaultStatement != null)
  		{
  			node.defaultStatement.parentSwitch = node;
  			node.defaultStatement.jjtAccept(this,data);
  		}
  		
  		SymbolTable.getSymbolTable().closeScope();
  		
  		
  		for (int i = 1; i< node.jumpFalseCodeIndex.size(); i++)
  		{
  			ASTCaseStatement caseStatement = node.caseStatementList.get(i);
  			int index = node.jumpFalseCodeIndex.get(i-1);
  			int offest = caseStatement.startOffest;
  			CodeGenerator.currentMethod.methodCode.fill2At(index, offest);
  		}
  		//Fill the last one
  		int jumpFalseOffest = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		int index = node.jumpFalseCodeIndex.get( node.jumpFalseCodeIndex.size()-1 );
  		if( node.defaultStatement != null)
  		{
  			int defaultOffest = node.defaultStatement.startOffest;
  			CodeGenerator.currentMethod.methodCode.fill2At(index,defaultOffest);
  		}
  		else
  		{
  			CodeGenerator.currentMethod.methodCode.fill2At(index,jumpFalseOffest);
  		}
  		
  		
  		
  		
  		
  		int noOfBreak = CommonInheritedAttribute.noOfBreak.pop();
  		for (int i = 0; i< noOfBreak; i++)
  		{
  			int breakCodeIndex = CommonInheritedAttribute.breakIndex.pop();
  			CodeGenerator.currentMethod.methodCode.fill2At( breakCodeIndex, jumpFalseOffest );
  		}
		return null;
  }
  public Object visit(ASTCaseStatement node, Object data)
  {
  		node.startOffest = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		//Debug.inform("Case Statement ");
  		node.switchExpression.jjtAccept(this,data);
  		node.caseExpression.jjtAccept(this,data);
  		CodeGenerator.currentMethod.methodCode.emitIntEqual();
  		
  		int codeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  		CodeGenerator.currentMethod.methodCode.emitJumpFalse(-1);
  		
  		node.parentSwitch.jumpFalseCodeIndex.add( codeIndex );
  		
  		for (int i = 0; i< node.statementList.size(); i++)
  		{
  			node.statementList.get(i).jjtAccept(this,data);
  		}
  		
		return null;
  }
  public Object visit(ASTCaseExpression node, Object data)
  {
  	node.jjtGetChild(0).jjtAccept(this,data);
  	
	return null;
  }
  
  public Object visit(ASTCaseIdentifier node, Object data)
  {
		return null;
  }
  public Object visit(ASTDefaultStatement node, Object data)
  {
  		node.startOffest = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  	 	for (int i = 0; i< node.statementList.size(); i++)
  	 	{
  	 		node.statementList.get(i).jjtAccept(this,data);
  	 	}
		return null;
  }
  public Object visit(ASTIfStatement node, Object data)
  {
  		node.expression.jjtAccept(this,data);
  		
  		int falseIndex = -1;
  		int jumpFalseCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  		int thenJumpCodeIndex = -1, elseJumpCodeIndex =-1 ;
  		int elseJumpCodeTarget = -1,thenJumpCodeTarget=-1;
  		CodeGenerator.currentMethod.methodCode.emitJumpFalse(falseIndex);
  		
  		
  		node.thenPartStatement.jjtAccept(this,data);
  		
  		int targetOfJumpFalse ;
  		
  		if( node.elsePartStatement!=null)
  		{
  			thenJumpCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  			CodeGenerator.currentMethod.methodCode.emitJump( thenJumpCodeTarget);
  			
  			targetOfJumpFalse = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  			CodeGenerator.currentMethod.methodCode.fill2At(jumpFalseCodeIndex,targetOfJumpFalse);
  			
  			node.elsePartStatement.jjtAccept(this,data);
  			elseJumpCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  			
  			//CodeGenerator.currentMethod.methodCode.emitJump(-1);
  		}
  		else
  		{
  		//int jumpOffest = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		//CodeGenerator.currentMethod.methodCode.emitJump(jumpIndex); 
  			targetOfJumpFalse = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		//Debug.inform("Insert Code at loca "+ jumpFalseCodeIndex+" "+endOfIfStatement);
  			CodeGenerator.currentMethod.methodCode.fill2At(jumpFalseCodeIndex,targetOfJumpFalse);
  		}
  		if( node.elsePartStatement!=null)
  		{
  			elseJumpCodeTarget = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  			CodeGenerator.currentMethod.methodCode.fill2At( thenJumpCodeIndex,elseJumpCodeTarget);
  		}
		return null;
  }
  public Object visit(ASTWhileStatement node, Object data)
  {
  		CommonInheritedAttribute.noOfBreak.push(0);
  		int jumpWhileStart = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		node.expression.jjtAccept(this,data);
  		int jumpFalseTarget = -1;
  		int jumpFalseCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  		CodeGenerator.currentMethod.methodCode.emitJumpFalse( jumpFalseTarget );
  		
  		node.statement.jjtAccept(this,data);
  		CodeGenerator.currentMethod.methodCode.emitJump( jumpWhileStart );
  		jumpFalseTarget = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		CodeGenerator.currentMethod.methodCode.fill2At( jumpFalseCodeIndex, jumpFalseTarget);
  		
  		
  		int noOfBreak = CommonInheritedAttribute.noOfBreak.pop();
  		for (int i = 0; i< noOfBreak; i++)
  		{
  			int breakCodeIndex = CommonInheritedAttribute.breakIndex.pop();
  			CodeGenerator.currentMethod.methodCode.fill2At( breakCodeIndex,jumpFalseTarget);
  		}
  		//Fill Break
  		
		return null;
  }
  public Object visit(ASTDoStatement node, Object data)
  {
  		CommonInheritedAttribute.noOfBreak.push(0);
  		int jumpDoWhileStart = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		node.statement.jjtAccept(this,data);
  		node.expression.jjtAccept(this,data);
  		CodeGenerator.currentMethod.methodCode.emitJumpTrue( jumpDoWhileStart );
  		
  		
  		int endOfDo = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		int noOfBreak = CommonInheritedAttribute.noOfBreak.pop();
  		for (int i = 0; i< noOfBreak; i++)
  		{
  			int breakCodeIndex = CommonInheritedAttribute.breakIndex.pop();
  			CodeGenerator.currentMethod.methodCode.fill2At( breakCodeIndex,endOfDo );
  		}
		return null;
  }
  public Object visit(ASTForStatement node, Object data)
  {
  		CommonInheritedAttribute.noOfBreak.push(0);
  		SymbolTable.getSymbolTable().openScope();
  		node.forInitializer.jjtAccept(this,data);
  		
  		int jumpForExpression = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		
  		node.forExpression.jjtAccept(this,data);
  		
  		int jumpFalseTarget = -1;
  		int jumpFalseCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  		
  		CodeGenerator.currentMethod.methodCode.emitJumpFalse( jumpFalseTarget );
  		
  		node.statement.jjtAccept(this,data);
  		node.forUpdator.jjtAccept(this,data);
  		  		
  		CodeGenerator.currentMethod.methodCode.emitJump( jumpForExpression );
  		jumpFalseTarget = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		//Debug.inform("JUMP False Target "+ jumpFalseTarget + "jump false code index "+ jumpFalseCodeIndex);
  		CodeGenerator.currentMethod.methodCode.fill2At(jumpFalseCodeIndex,jumpFalseTarget);
  		
  		
  		int endOfFor= CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		int noOfBreak = CommonInheritedAttribute.noOfBreak.pop();
  		for (int i = 0; i< noOfBreak; i++)
  		{
  			int breakCodeIndex = CommonInheritedAttribute.breakIndex.pop();
  			CodeGenerator.currentMethod.methodCode.fill2At( breakCodeIndex,endOfFor );
  		}
  		SymbolTable.getSymbolTable().closeScope();
		return null;
  }
  public Object visit(ASTForInitializer node, Object data)
  {
  	if(node.forInit != null)
  	{
  		node.forInit.jjtAccept(this,data);
  	}
		return null;
  }
  public Object visit(ASTForExpression node, Object data)
  {
  	if(node.expression != null)
  	{
  		node.expression.jjtAccept(this,data);
  		
  	}
		return null;
  }
  public Object visit(ASTForUpdator node, Object data)
  {
  	if(node.forUpdate != null)
  	{
  		node.forUpdate.jjtAccept(this,data);
  	}
		return null;
  }
  public Object visit(ASTForInit node, Object data)
  {
  		node.localVariableDeclaration.jjtAccept(this,data);
		return null;
  }
  public Object visit(ASTLocalVariableDeclaration node, Object data)
  {
  	String localVariableType = node.type.typeName;
  	for (int i = 0; i< node.variableDeclaratorList.size(); i++)
  	{
  		ASTVariableDeclarator variableDeclarator = node.variableDeclaratorList.get(i);
  		
  		
  		
  		
  		String variableName = variableDeclarator.variableDeclaratorId.variableName;
  		///	inform("Insert "+variableName);
  		SymbolTableEntry variableEntry = new SymbolTableEntry( variableName, localVariableType);
  		
  		try
  		{
  			SymbolTable.getSymbolTable().insert(variableEntry);
  		}
  		catch(Exception e)
  		{
  			
  		}
  		variableDeclarator.jjtAccept(this, data);
  		///inform("Occupy local var "+ variableEntry.name +" "+ variableEntry.indexOffestOfLocalVar);
  	}

	return null;
  }
  public Object visit(ASTForUpdate node, Object data)
  {
  		node.assignmentStatement.jjtAccept(this,data);
		return null;
  }
  public Object visit(ASTReturnStatement node, Object data)
  {
  	if(node.expression!= null)
  	{
  		node.expression.jjtAccept(this,data);
  		if( CentralTypeTable.isPrimitiveType( CodeGenerator.currentMethod.methodReturnType ) )
  		{
  			CodeGenerator.currentMethod.methodCode.emitConvertPrimitive( node.expression.typeName,CodeGenerator.currentMethod.methodReturnType);
  		}
  	}
  	CodeGenerator.currentMethod.methodCode.emitReturnOnType( CodeGenerator.currentMethod.methodReturnType);
  	//Debug.inform("Return Statement "+node.returnType);
	return null;
  }
  public Object visit(ASTContinueStatement node, Object data)
  {
		return null;
  }
  public Object visit(ASTTryStatement node, Object data)
  {
  		node.fromPC = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		SymbolTable.getSymbolTable().openScope();
  		node.block.jjtAccept(this,data);
  		node.toPC = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		SymbolTable.getSymbolTable().closeScope();
  	
  		for (int i = 0; i< node.catchStatementList.size(); i++)
  		{
  			ASTCatchStatement catchStatement = node.catchStatementList.get(i);
  			catchStatement.parentTryStatement = node;
  			catchStatement.jjtAccept(this,data);
  		}
  		if(node.finallyBlock != null)
  		{
  			SymbolTable.getSymbolTable().openScope();
  			node.finallyBlock.jjtAccept(this,data);
  			SymbolTable.getSymbolTable().closeScope();
  		}
  		
  		int jumpPC = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		//Inset jump code
  		for (int i = 0; i< node.jumpList.size(); i++)
  		{
  			int pcToInsertJump = node.jumpList.get(i);
  			CodeGenerator.currentMethod.methodCode.fill2At(pcToInsertJump,jumpPC);	
  		}
		return null;
  }
  public Object visit(ASTCatchStatement node, Object data)
  {
  		SymbolTable.getSymbolTable().openScope();
  		int target = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		node.formalParameters.jjtAccept(this,data);
  		CodeGenerator.currentClass.uCodeFile.constantPool.addClassRef( node.catchExceptionType);
  		int exceptionClassIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getClassIndex(node.catchExceptionType);
  		node.catchExceptionVarName = node.formalParameters.formalParameterList.get(0).variableDeclaratorId.variableName ;
  		
  		///Debug.inform("Exception class Type "+ node.catchExceptionVarName);
  		//CodeGenerator.currentMethod.methodCode.emitStoreLocalRef();
  		SymbolTableEntry entry = SymbolTable.getSymbolTable().getEntry(node.catchExceptionVarName);
  		int indexOfExceptionVar =entry.indexOffestOfLocalVar;
  		CodeGenerator.currentMethod.methodCode.emitStoreLocalRef( indexOfExceptionVar );
  		
  		
  		node.block.jjtAccept(this,data);
  	
  		SymbolTable.getSymbolTable().closeScope();
  		int pc = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  		node.parentTryStatement.jumpList.add(pc);
  		CodeGenerator.currentMethod.methodCode.emitJump(-1);
  		
  		ExceptionTable expTable = new ExceptionTable(node.parentTryStatement.fromPC,node.parentTryStatement.toPC,target,exceptionClassIndex);
  		CodeGenerator.currentMethod.exceptionTable.add(expTable);
  		
  		
		return null;
  }
  public Object visit(ASTThrowStatement node, Object data)
  {
  		node.expression.jjtAccept(this,data);
  		CodeGenerator.currentMethod.methodCode.emitThrowException();
		return null;
  }
  public Object visit(ASTRebindStatement node, Object data)
  {
  		//node.destinationExpression.jjtAccept(this,data);
  		if(node.destinationExpression.nameList.size()==1)
  		{
  			CodeGenerator.currentMethod.methodCode.emitLoadLocalRef(0);
  		}
  		else
  		{
  			ArrayList<String> name= new ArrayList<String>();
  			name.add(node.destinationExpression.nameList.get(0));
  			CodeGenerator.generateCodeForFirstFieldNameList(name);
  		}
  		CodeGenerator.currentClass.uCodeFile.constantPool.addMethodRef(node.targetMethod.ownerName,node.targetMethod.methodName,node.targetMethod.methodProtocol);
  		int targetMethodIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getMethodRefIndex(node.targetMethod.ownerName,node.targetMethod.methodName,node.targetMethod.methodProtocol);
  		CodeGenerator.currentClass.uCodeFile.constantPool.addMethodRef(node.rebindMethod.ownerName,node.rebindMethod.methodName,node.rebindMethod.methodProtocol);
  		int rebindMethodIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getMethodRefIndex(node.rebindMethod.ownerName,node.rebindMethod.methodName,node.rebindMethod.methodProtocol);
  		CodeGenerator.currentMethod.methodCode.emitRebind(targetMethodIndex,rebindMethodIndex);
  		///Debug.inform("Producing code for rebind statement "+ targetMethodIndex+"  "+ rebindMethodIndex );
		return null;
  }
  public Object visit(ASTExecuteTimesStatement node,Object data)
  {
  	SymbolTable.getSymbolTable().openScope();
  	String anonymousVarName = Integer.toString(CodeGenerator.executeTimeVariableName ++);
  	
  	try
  	{
  		SymbolTableEntry entry = new SymbolTableEntry(anonymousVarName,"i");
  		SymbolTable.getSymbolTable().insert(entry);
  		entry = SymbolTable.getSymbolTable().getEntry( anonymousVarName );
  		CodeGenerator.currentClass.uCodeFile.constantPool.addIntegerRef(0);
  		int integerZeroIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex(0);
  		
  		CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(integerZeroIndex,1);
  		CodeGenerator.currentMethod.methodCode.emitStoreLocalInt( entry.indexOffestOfLocalVar );
  		
  		int jumpCodeOffest = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		CodeGenerator.currentMethod.methodCode.emitLoadLocalInt( entry.indexOffestOfLocalVar );
  		node.expression.jjtAccept(this,data);
  		
  		CodeGenerator.currentMethod.methodCode.emitLTType("i");
  		
  		int jumpFalseCodeIndex = CodeGenerator.currentMethod.methodCode.getCurrentPointer()+1;
  		CodeGenerator.currentMethod.methodCode.emitJumpFalse( - 1);
  		
  		node.statementBody.jjtAccept(this,data);
  		
  		//anonymousVar = anonymousVar - 1;
  		CodeGenerator.currentMethod.methodCode.emitLoadLocalInt( entry.indexOffestOfLocalVar );
  		int integerOneIndex = CodeGenerator.currentClass.uCodeFile.constantPool.getIntegerIndex(1);
  		CodeGenerator.currentMethod.methodCode.emitLoadConstantPool(integerOneIndex,1);
  		CodeGenerator.currentMethod.methodCode.emitAddInt();
  		CodeGenerator.currentMethod.methodCode.emitStoreLocalInt( entry.indexOffestOfLocalVar );
  		
  		CodeGenerator.currentMethod.methodCode.emitJump( jumpCodeOffest );
  		
  		int endOfExecute = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
  		CodeGenerator.currentMethod.methodCode.fill2At( jumpFalseCodeIndex, endOfExecute);
  	}
  	catch(Exception e)
  	{
  	}
  	SymbolTable.getSymbolTable().closeScope();
  	return null;
  }
}
