import java.util.*;

class CodeGenerator
{
	static Type currentClass;
	static Method currentMethod;
	static boolean emitStore = false;
	static int executeTimeVariableName = 0; //to be used as anonymous variable in execute times statement
	static UCSY parser;
	static void inform(String data)
	{
		try
		{
			parser.inform(data);
		}
		catch(Exception e)
		{
		}
	}
	
	
	//For resconstructed node
	//static ASTPrimaryExpression primaryExpression;
	static ArrayList<ParentName> allPrimaryExpression = new ArrayList<ParentName>();
	static int withinParenthesePrimaryPrefix = -1;
	static void createNewAllPrimaryExpression()
	{
		allPrimaryExpression = new ArrayList<ParentName>();
	}
	static ArrayList<ParentName> reStructureAndGeneratePrimaryExpression()
	{
		ArrayList<ParentName> reConstructNode = new ArrayList<ParentName>();
		ArrayList<String> idList = new ArrayList<String>();
		
		ParentName prefix = allPrimaryExpression.get(0);
		
		
		if( prefix instanceof ASTNamePrimaryPrefix)
		{
			ASTNamePrimaryPrefix namePrefix = (ASTNamePrimaryPrefix)prefix;
			
			for (int i = 0; i< namePrefix.name.nameList.size(); i++)
			{
				idList.add( namePrefix.name.nameList.get(i));
			}
		}
		else if(prefix instanceof ASTThisPrimaryPrefix)
		{
			idList.add("this");
		}
		else if(prefix instanceof ASTSuperPrimaryPrefix)
		{
			idList.add("super");
		}
		else if(prefix instanceof ASTParenthesePrimaryPrefix)//This is allocation primaryprefix
		{
			reConstructNode.add(prefix);
		}
		else if(prefix instanceof ASTAllocationPrimaryPrefix)
		{
			reConstructNode.add(prefix);
		}
		else
		{
			Debug.inform("Invalid primary prefix in code generation "+prefix);
		}
		
		for (int i = 1; i< allPrimaryExpression.size(); i++)
		{
			ParentName current = allPrimaryExpression.get(i);
			
			
			//idList = new ArrayList<String>();
			
			if( current instanceof  ASTArrayExpressionPrimarySuffix)
			{
				
				if( allPrimaryExpression.get(i-1) instanceof ASTNamePrimaryPrefix)
				{
					///Debug.inform("That case");
					ASTName name = new ASTName(0);
					name.nameList = idList;
					reConstructNode.add(name);
					idList = new ArrayList<String>();	
				}
				reConstructNode.add( current );	
			}
			else if(current instanceof ASTArgumentsSuffix)
			{
				idList.remove(idList.size()-1);
				if( idList.size() > 0)
				{
					ASTName name = new ASTName(0);
					name.nameList = idList;
					reConstructNode.add(name);
					
				}
				idList = new ArrayList<String>();
				reConstructNode.add( current );
			}
			else if(current instanceof ASTSuffixDotIdentifierNode)
			{
				ASTSuffixDotIdentifierNode suffixDot = (ASTSuffixDotIdentifierNode)current;
				idList.add( suffixDot.name );
			}
			else
			{
				Debug.inform("CODE GENERAION ERROR in CodeGenerator.java");
			}
		}
		if( idList.size()>0)
		{
			
			int lastIndex = reConstructNode.size()-1;
			///Debug.inform("Last index "+lastIndex+" "+ reConstructNode.size());
			if(lastIndex >=0 )
			{
				ParentName lastNode = reConstructNode.get(lastIndex);
				if(lastNode instanceof ASTNamePrimaryPrefix)
				{
					ASTNamePrimaryPrefix namePrimaryPrefix = (ASTNamePrimaryPrefix)lastNode;
				
					for (int i = 0; i<idList.size(); i++)
					{
						namePrimaryPrefix.name.nameList.add(idList.get(i));
					}
				}
				
				else
				{
					///Debug.inform("Thiss case ");
					ASTName name = new ASTName(0);
					name.nameList = idList;
					reConstructNode.add(name);
				}
			}
			else
			{
				
				ASTName name = new ASTName(0);
				name.nameList = idList;
				reConstructNode.add(name);
			}
			
			
		}
		return reConstructNode;
	}
	static void generateCodeForReconstructeNode(ArrayList<ParentName> allNodes)
	{
		ParentName firstNode = allNodes.get(0);
		String typeName = "";
		if( firstNode instanceof ASTName)
		{
			
		}
		else if(firstNode instanceof ASTNamePrimaryPrefix)
		{
			
		}
		else if(firstNode instanceof ASTAllocationPrimaryPrefix)
		{
		}
	}
	static String generateCodeForFirstFieldNameList(ArrayList<String> nameList)
	{
		String typeName ="";
		String parentTypeName="";
		String fieldName="";
		
		try
		{
			
			String firstName = nameList.get(0);
			
			if(! Character.isUpperCase( firstName.charAt(0)))
			{
				SymbolTableEntry entry = SymbolTable.getSymbolTable().getEntry(firstName);
			
				if(entry != null)//It is local variable
				{
					///Debug.inform("LOAD_LOCAL "+ entry.indexOffestOfLocalVar);
					CodeGenerator.currentMethod.methodCode.emitLoadLocalVar(entry.indexOffestOfLocalVar,entry.type);
					typeName = entry.type;
					
					int index = 1;
					parentTypeName = typeName;
				    while(index < nameList.size())
				    {
				    	
				    	fieldName = nameList.get(index);
				    	//Debug.inform(" resolving "+fieldName);
				    	UCSYClass theParent = CentralTypeTable.getCentralTypeTable().getAClass( parentTypeName );
				    	typeName = theParent.emitCodeForMemberField( fieldName );
				    	//Debug.inform("Emitting code for Field Name "+fieldName);
				    	parentTypeName = typeName;
				    	index++;
				    }
				}
				else
				{
					UCSYClass theClass = (UCSYClass)CodeGenerator.currentClass;
					///System.out.println ("Resolving Code Gen "+firstName);
					UCSYField field = theClass.resolveField( firstName );
					if(!field.isStatic())
					{
						///inform("LOAD_LOACAL_REF 0 ");
						CodeGenerator.currentMethod.methodCode.emitLoadLocalRef(0);
					}
					theClass.emitCodeForMemberField(firstName);
					typeName = field.fieldType;
				    //type.ge
				    int index = 1;
				    parentTypeName = typeName;
				    while(index < nameList.size())
				    {
				    	fieldName = nameList.get(index);
				    	UCSYClass theParent = CentralTypeTable.getCentralTypeTable().getAClass( parentTypeName );
				    	typeName = theParent.emitCodeForMemberField( fieldName );
				    	parentTypeName = typeName;
				    	index++;
				    }
				    
				}
			}
			else //It is Type Name
			{
				typeName = firstName;
				
				int index = 1;
			    parentTypeName = typeName;
				while(index < nameList.size())
				{
				    	fieldName = nameList.get(index);
				    	UCSYClass theParent = CentralTypeTable.getCentralTypeTable().getAClass( parentTypeName );
				    	typeName = theParent.emitCodeForMemberField( fieldName );
				    	parentTypeName = typeName;
				    	index++;
				}
				return typeName;
			}
				
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return typeName;
	}
	
	static void emitCodeForAllParameter()
	{
	}	
	static void emitCodeForHandleAll(UCSYField delegateField)
	{
		String currentType = CodeGenerator.currentClass.typeName;
		//Debug.inform("Current Type is "+ currentType);
		Method met = CodeGenerator.currentMethod;
		met.splitParameter();
		String par[] = met.parameters;
		//Debug.inform("Method namne "+ met.methodName);
		met.sizeOfArgument = CentralTypeTable.getSizeForSignature( met.methodSignature ) + 1; //Always instance method
		met.sizeOfLocalVar = met.sizeOfArgument;
		
		try 
		{
			CodeGenerator.currentMethod.methodCode.emitLoadLocalThis();
			Type parentType = CentralTypeTable.getCentralTypeTable().getType( currentType);
			parentType.emitCodeForMemberField( delegateField.fieldName );	
			
			int offest = 1;
			for (int i = 0; i< par.length ; i++)
			{
				String parType = par[i];
				CodeGenerator.currentMethod.methodCode.emitLoadLocalVar(offest,parType);
				offest += CentralTypeTable.getSizeOfType( parType);
			}
			Type delegateType = CentralTypeTable.getCentralTypeTable().getType( delegateField.fieldType );
			Debug.inform("Calling "+ delegateType.typeName +" method " + CodeGenerator.currentMethod.ownerName);
			delegateType.emitCodeForMemberMethod( CodeGenerator.currentMethod );
			String retType = CodeGenerator.currentMethod.methodReturnType;
			CodeGenerator.currentMethod.methodCode.emitReturnOnType( retType );
			//met.sizeof
		}
		catch(Exception e)
		{
			Debug.inform("Cannot produce code for delegate handles all clause");
		}
		/*for (int i = 0; i< par.length; i++)
		{
			Debug.inform("Type of par "+ par[0]);
		}
		*/
	}
	static String generateCodeForFieldNameList(ArrayList<String> nameList,String currentTypeName)
	{
		String typeName = "";
		String parentTypeName = currentTypeName;
		String childName = "";
		try
		{
			for (int i = 0; i< nameList.size(); i++)
			{
				childName = nameList.get(i);
				UCSYClass parentClass = CentralTypeTable.getCentralTypeTable().getAClass( parentTypeName );
				parentTypeName = parentClass.emitCodeForMemberField(childName);
				typeName = parentTypeName;
			}
		}
		catch(Exception e)
		{
			Debug.inform("ERROR IN CODE GENERATION");
		}
		return typeName;
	}
	static void generateCodeFor(ArrayList<String> nameList)
	{
		
		if( nameList.size() == 1) //Simple Name
		{
			String simpleName = nameList.get(0);
			//Search in SymbolTable , check it is local variable
				
			SymbolTableEntry entry = SymbolTable.getSymbolTable().getEntry(simpleName);
			
			if(entry != null)//Yes it is local variable
			{
				//CodeGenerator.currentMethod.methodCode.emi
				String typeOfLocalVar = entry.type;
				int indexOfLocalVar   = entry.indexOffestOfLocalVar;
				
				if( CodeGenerator.emitStore)
				{
					CodeGenerator.currentMethod.methodCode.emitStoreLocalVar( indexOfLocalVar, typeOfLocalVar );
				}
				else
				{
					CodeGenerator.currentMethod.methodCode.emitLoadLocalVar( indexOfLocalVar, typeOfLocalVar );
				}
			}
		
		}
		
	}
	//To be used by assignment statement
	static void changeLoadToStore(int size)
	{
		int cp = CodeGenerator.currentMethod.methodCode.getCurrentPointer();
		
		boolean shouldContinue = false;
		int loadIndex = cp-1;
		switch(CodeGenerator.currentMethod.methodCode.code[loadIndex])
		{
			case Instruction.GET_ARRAY_ELEMENT_BYTE:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_BYTE;
			break;
			
			case Instruction.GET_ARRAY_ELEMENT_SHORT:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_SHORT;
			break;
			
			case Instruction.GET_ARRAY_ELEMENT_INT:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_INT;
			break;
			
			case Instruction.GET_ARRAY_ELEMENT_LONG:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_LONG;
			break;
			
			case Instruction.GET_ARRAY_ELEMENT_FLOAT:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_FLOAT;
			break;
			
			case Instruction.GET_ARRAY_ELEMENT_DOUBLE:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_DOUBLE;
			break;
			
			case Instruction.GET_ARRAY_ELEMENT_REF:
				CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_ARRAY_ELEMENT_REF;
			break;
			default:
				shouldContinue = true;
		}
		
		//load_int_ 2byte
		if(shouldContinue)
		{
			
			loadIndex = cp-3;
			int instruction = CodeGenerator.currentMethod.methodCode.code[ loadIndex ];
		
			switch( instruction )
			{
				case Instruction.LOAD_LOCAL_DOUBLE:
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_LOCAL_DOUBLE;
				break;
				case Instruction.LOAD_LOCAL_FLOAT:
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_LOCAL_FLOAT;
				break;	
				case Instruction.LOAD_LOCAL_INT:
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_LOCAL_INT;
				break;
				case Instruction.LOAD_LOCAL_LONG:
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_LOCAL_LONG;
				break;
				case Instruction.LOAD_LOCAL_REF:
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.STORE_LOCAL_REF;
				break;
			
				case Instruction.GET_STATIC_FIELD:
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.PUT_STATIC_FIELD;
				break;
				case Instruction.GET_INSTANCE_FIELD:
				///System.out.println ("OK");
					CodeGenerator.currentMethod.methodCode.code[ loadIndex ] = Instruction.PUT_INSTANCE_FIELD;
				break;
			
			default:
				Debug.inform("Invalid instruction of load in CODE GENERATION changeLoadToStore"+ instruction);
			}
			
		}
		CodeGenerator.currentMethod.methodCode.incOperandStack(- size);
	
	}
}