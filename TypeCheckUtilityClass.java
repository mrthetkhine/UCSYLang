import java.util.*;

class TypeCheckUtilityClass
{
	static boolean isMethodIntheMethodList(ArrayList<Method> methodList, Method m)
	{
		for (int i = 0; i< methodList.size(); i++)
		{
			Method met = methodList.get(i);
			//Omit temporarily
			if(met.methodName.equals( m.methodName ) /*&& met.methodSignature.equals(m.methodSignature) */)
			{
				return true;
			}
		}
		return false;
	}
	static String arithmeticOperatorTypeCheck(String operator,String opOneType, String opTwoType)throws UnAppliableOperationException
	{
		String resultType = "";
		
		//System.out.println ("Arithemtic opera " + opOneType + "  "+ opTwoType);
		if( CentralTypeTable.isPrimitiveType(opOneType)  && CentralTypeTable.isPrimitiveType( opTwoType ) )
		{
			if(CentralTypeTable.isNumberType( opOneType ) && CentralTypeTable.isNumberType( opTwoType))
			{
				if(CentralTypeTable.isRealType(opOneType) || CentralTypeTable.isRealType( opTwoType))
				{
					//Do floating point operation
					if(CentralTypeTable.isDouble(opOneType) || CentralTypeTable.isDouble(opTwoType))
					{
						resultType = "d";
					}
					else
					{
						//System.out.println ("Do result "+ opOneType +" "+opTwoType);	
						resultType = "f";
					}
				}
				else 
				{
					if(CentralTypeTable.isLong(opOneType) || CentralTypeTable.isLong(opTwoType))
					{
						resultType = "l";
					}
					else
					{
						resultType = "i";
					}
				}
			}
			else if(CentralTypeTable.isString(opOneType) || CentralTypeTable.isString(opTwoType))
			{
				//Only + operator is allowed to opearate on string
				if(! operator.equals("+"))
				{
					throw new UnAppliableOperationException(operator,opOneType,opTwoType);
				}
				else
				{
					resultType = "m";
				}
			}
			else
			{	
			//Not Number and Not String
			
				throw new UnAppliableOperationException(operator,opOneType,opTwoType);
			}
			
		}
		else
		{
			//Not Number and Not String
			///System.out.println ("Throw ing "+opOneType );	
			throw new UnAppliableOperationException(operator,opOneType,opTwoType);
		}
		//System.out.println ("Going down");
		return resultType;
	}
	static String relationanlOperatorCheck(String operator,String opOneType,String opTwoType)throws UnAppliableOperationException
	{
		String resultType = "";
		
		if( isValidOperandForRelationalOperator(operator,opOneType,opTwoType))
		{
			return "t";//return boolean type
		}
		
		return resultType ;
	}
	static boolean isValidOperandForRelationalOperator(String operator,String opOneType,String opTwoType)throws UnAppliableOperationException
	{
		if( operator.equals("==") || operator.equals("!="))
		{
			if( CentralTypeTable.isNumberType(opOneType) && CentralTypeTable.isNumberType(opTwoType))
			{
				return true;
			}
			else if( CentralTypeTable.isCharacter( opOneType ) && CentralTypeTable.isCharacter(opTwoType))
			{
				return true;
			}
			else if( CentralTypeTable.isBoolean(opOneType) && CentralTypeTable.isBoolean(opTwoType))
			{
				return true;
			}
			else if( !CentralTypeTable.isPrimitiveType( opOneType) && (!CentralTypeTable.isPrimitiveType( opTwoType) || opTwoType.equals("null")) )
			{
				return true;
			}
			else if( CentralTypeTable.isString(opOneType) )
			{
				return CentralTypeTable.isString(opTwoType) || opTwoType.equals("null");	
			}
			else if (CentralTypeTable.isString(opTwoType))
			{
				return CentralTypeTable.isString(opOneType) || opOneType.equals("null");	
			}
			else
			{
				throw new UnAppliableOperationException(operator,opOneType,opTwoType);
			}
		}
		else if(operator.equals(">") || operator.equals(">=") || operator.equals("<") || operator.equals("<="))//Operator is > ,>=,<,<= 
		{
			if(CentralTypeTable.isNumberType(opOneType) && CentralTypeTable.isNumberType(opTwoType))	
			{
				return true;
			}
			else if(CentralTypeTable.isCharacter(opOneType) && CentralTypeTable.isCharacter(opTwoType))
			{
				return true;
			}
			else
			{
				throw new UnAppliableOperationException(operator,opOneType,opTwoType);
			}
		}
		throw new UnAppliableOperationException(operator,opOneType,opTwoType);
	}
	static String booleanOperatorTypeCheck(String operator,String opOneType, String opTwoType)throws UnAppliableOperationException
	{
		String resultType ="t";
		
		if( CentralTypeTable.isBoolean( opOneType ) && CentralTypeTable.isBoolean(opTwoType))
		{
			return "t";
		}
		else
		{
			throw new UnAppliableOperationException(operator,opOneType,opTwoType);
		}
		
	}
	static String getTypeDescriptionFromMemonic(String typeName)
	{
		
		if ( CentralTypeTable.isByte(typeName))
		{
			return "byte";
		}
		else if(CentralTypeTable.isCharacter(typeName))
		{
			return "char";
		}
		else if(CentralTypeTable.isBoolean(typeName))
		{
			return "boolean";
		}
		else if(CentralTypeTable.isShort(typeName))
		{
			return "short";
		}
		else if(CentralTypeTable.isInteger(typeName))
		{
			return "integer";
		
		}
		else if(CentralTypeTable.isLong(typeName))
		{
			return "long";
		}
		else if(CentralTypeTable.isFloat(typeName))
		{
			return "float";
		}
		else if(CentralTypeTable.isDouble(typeName))
		{
			return "double";
		}
		else if(CentralTypeTable.isString(typeName))
		{
			return "string";
		}
		else if(typeName.charAt(0) == '[')
		{
			int lastIndex = typeName.lastIndexOf("[") ;
			String arrayClassName = typeName.substring(lastIndex+1,typeName.length());
			
			for (int i = 0; i<= lastIndex; i++)
			{
				arrayClassName +="[]";
			}
			return arrayClassName;
		}
		else
			return typeName;
	}
	/****************************************************************************
	 *Current Name is type that access member expression
	 *ChildName is parent of member
	 * class Demo
	 *{
	 *  void method()
	 *	{
	 		Human h;
	 		h.method(); //Here currentName is Demo , childName is Human,
	 	}		
	 *} 
	 ****************************************************************************/
	public static void checkAccessValid(String currentName,String childName,int accessModifier)throws InvalidAccessToMemberException
	{
		
		try
		{
			if(UCSYClassAttribute.isPublic( accessModifier ))
			{
				return;
			}			
			else if( UCSYClassAttribute.isProtected( accessModifier ) )
			{
				Type parentType ;
				parentType = CentralTypeTable.getCentralTypeTable().getType( currentName );
			
				switch( parentType.itsType )
				{
					case CLASSTYPE:
						if( currentName.equals( childName)) //Same type ok
						{
							return;
						}
						else 
						{
							UCSYClass currentClass = CentralTypeTable.getCentralTypeTable().getAClass(currentName);
							
							if( currentClass.isChildOf( childName ))
							{
								return;
							}
							else
							{
								throw new InvalidAccessToMemberException( currentName,childName );
							}
						}
					//break;
					case INTERFACETYPE:
						throw new InvalidAccessToMemberException( currentName,childName );
					//break;
					case METATYPE:
						throw new InvalidAccessToMemberException( currentName,childName );
					
				}
			}
			else if( UCSYClassAttribute.isPrivate( accessModifier ))
			{
				if( currentName.equals( childName ))
				{
					return;
				}
				else
				{
					throw new InvalidAccessToMemberException( currentName,childName );
				}
			}
			else
			{
				System.out.println ("Invalid Access Modifier");
			}
		}
		catch(TypeNotFoundException e)
		{
			
		}
		catch(NotExceptedTypeException e)
		{
			
		}
	}
	
	static boolean isAssignmentCompatible(String source,String destination)
	{
		if(source == null || destination == null)
		{
			return false;
		}
		else if( destination.equals( source ))
		{
			return true;
		}
		else if( source.equals("null") && (! CentralTypeTable.isPrimitiveType( destination )))
		{
				return true;
		}
		else if( destination.charAt(0) == '[') //Destination is array type
		{
			int destinationDimension , sourceDimension;
			destinationDimension = destination.lastIndexOf("[")+1;
			sourceDimension      = source.lastIndexOf("[")+1;
			
			if( destinationDimension == sourceDimension )
			{
				String destinationTypeName = destination.substring( destinationDimension,destination.length());
				String sourceTypeName      = source.substring( sourceDimension, source.length());
				
				return isAssignmentCompatible( sourceTypeName, destinationTypeName);
			}
			else
			{
				
				return false;
			}
		}
		else if( CentralTypeTable.isPrimitiveType( source ) && CentralTypeTable.isPrimitiveType( destination ))
		{
			//System.out.println ("Getting "+ source +" "+ destination);
			
			if(CentralTypeTable.isBoolean( destination ))	
			{
			
				return CentralTypeTable.isBoolean( source );
			}
			else if(CentralTypeTable.isCharacter( destination ))
			{
				return CentralTypeTable.isCharacter( source ) ;
			}
			else if(CentralTypeTable.isString( destination ))
			{
				return CentralTypeTable.isString( source );
			}
			else if( CentralTypeTable.isByte( destination ))
			{
				return CentralTypeTable.isByte( source );
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				return CentralTypeTable.isByte( source )||
					CentralTypeTable.isShort( source );
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
				
				return CentralTypeTable.isByte( source )||
					   CentralTypeTable.isShort( source ) ||
					   CentralTypeTable.isInteger( source );
			}
			else if( CentralTypeTable.isLong( destination ) )
			{
				return CentralTypeTable.isByte( source )||
					   CentralTypeTable.isShort( source ) ||
					   CentralTypeTable.isInteger( source )||
					   CentralTypeTable.isLong( source );
			}
			else if( CentralTypeTable.isFloat( destination ))
			{
				
				return CentralTypeTable.isByte( source )||
					   CentralTypeTable.isShort( source ) ||
					   CentralTypeTable.isInteger( source )||
					   CentralTypeTable.isLong( source )||
					   CentralTypeTable.isFloat( source );
			}
			else if(CentralTypeTable.isDouble( destination ))
			{
				return CentralTypeTable.isByte( source )||
					   CentralTypeTable.isShort( source ) ||
					   CentralTypeTable.isInteger( source )||
					   CentralTypeTable.isLong( source )||
					   CentralTypeTable.isFloat( source )||
					   CentralTypeTable.isDouble( source );
			}
		}
		else
		{
			try
			{
				
				///System.out.println ("Source "+ source +" Destination "+ destination);
				Type destinationType = CentralTypeTable.getCentralTypeTable().getType( destination );
				if( destinationType.assignmentCompatiable( source ) )
				{
					//System.out.println (source + " "+ destination);	
					return true;
				}
			}
			catch(Exception e)
			{
				return false;
			}
		}
		return false;
		
	}
	static boolean canCastable(String source, String destination)
	{
		if( TypeCheckUtilityClass.isAssignmentCompatible(source,destination))
		{
			//Debug.inform("This case is handled");
			//This case also handled for Referece Casting
			return true;
		}
		else if( CentralTypeTable.isNumberType( destination ) )
		{
			///Debug.inform("OK fine des "+destination+" source "+source);
			return CentralTypeTable.isNumberType( source )||
			       CentralTypeTable.isCharacter( source );
		}
		if( CentralTypeTable.isCharacter( destination ))
		{
			return CentralTypeTable.isIntegeralType( source);
		}
		else if(  !CentralTypeTable.isPrimitiveType( destination) && !CentralTypeTable.isPrimitiveType( source ) )
		{
			Type destinationType = null;
			try
			{
				destinationType = CentralTypeTable.getCentralTypeTable().getType( destination );
			}
			catch(TypeNotFoundException e)
			{
				return false;
			}
			if(destinationType.isChildOf( source))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else //To be check for refernec type
		{
			return false;
		}
	}
	static String getTypeDescriptionOfSignature(String methodSignature)
	{
		String par[] = MethodOverloadResolution.splitParameter( methodSignature);
		String description = "( ";
		for (int i = 0; i< par.length; i++)
		{
			description += TypeCheckUtilityClass.getTypeDescriptionFromMemonic( par[i]) +",";
		}
		description +=" )";
		return description;
	}
}


//To simulate golbal variable ,used by inherited attribute passing of TypeCheckVisitor
class VariableType
{
	static String typeName;
	
}
