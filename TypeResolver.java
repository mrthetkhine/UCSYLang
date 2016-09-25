import java.util.*;

class TypeResolver
{
	
	/****************************************************************************
	 * typeName is current resolving type 's name
	 ****************************************************************************/
	static String resolveTypeForName(String typeName,Type theClass,ArrayList<String> nameList)throws CannotResolveSymbolException,InstanceFieldReferenceByClassReferenceException,InvalidAccessToMemberException,
		StaticMethodCannotAccessInstanceFieldException
	{
		String type="";
		MemberAccess memberAccess = null;
		String contextParentName  = typeName; //To be used in AccessCheck
		
		try
		{
		
		if(nameList.size() > 1)
		{
			String firstName = nameList.get(0);
			
			//If start with an uppercase it is type name
			if( firstName.charAt(0) == Character.toUpperCase( firstName.charAt(0) ))
			{
				
				//It is typeName ge. ClassName.staticData
				Type firstNameClass = CentralTypeTable.getCentralTypeTable().getType(firstName);
				
				String staticMemberName = nameList.get(1);
				
				ArrayList<UCSYMember> memberOfTheClass = firstNameClass.getAllMemberFromHierarchy();
				
				if( isMemberContainsInTheList(memberOfTheClass, staticMemberName))
				{
					
					
					UCSYMember staticMember = getTheMemberFromTheList(memberOfTheClass,staticMemberName);
					
					if(! UCSYClassAttribute.isStatic(staticMember.modifier))
					{
						if(staticMember.typeOfMember != MemberType.FIELD)
						{
						}
						else
						{
							String childNameToCheck ;
							//if()
							if(firstNameClass instanceof UCSYClass)
							{
								childNameToCheck = ((UCSYClass)firstNameClass).className;
							}
							else 
							{
								childNameToCheck = ((UCSYInterface)firstNameClass).interfaceName;
							}
							
							TypeCheckUtilityClass.checkAccessValid(contextParentName,childNameToCheck,staticMember.modifier);
						}
						
						///System.out.println ("Foeoe "+UCSYClassAttribute.getTextualRep(staticField.modifier)+" " + staticField.modifier);
						throw new InstanceFieldReferenceByClassReferenceException( firstName, staticMemberName);
							
					}
					else
					{
						String childNameToCheck ;
						//if()
						if(firstNameClass instanceof UCSYClass)
						{
							childNameToCheck = ((UCSYClass)firstNameClass).className;
						}
						else 
						{
							childNameToCheck = ((UCSYInterface)firstNameClass).interfaceName;
						}
						TypeCheckUtilityClass.checkAccessValid(contextParentName,childNameToCheck,staticMember.modifier);
						
						int index = 2;
							
						String childTypeName = staticMember.memberType;
							
						String motherTypeName;
						String childName;
						String motherName;
						Type motherType ;
							
						type = childTypeName;
						motherTypeName = childTypeName;
						while( index < nameList.size()  )	
						{
							motherType = CentralTypeTable.getCentralTypeTable().getType( motherTypeName );
							childName  = nameList.get( index );
								
								
							memberAccess = motherType;
								
							memberOfTheClass = memberAccess.getAllMemberFromHierarchy();
										
							if(isMemberContainsInTheList(memberOfTheClass, childName))
							{
								UCSYMember member = getTheMemberFromTheList(memberOfTheClass,childName);
								
								if(member.typeOfMember != MemberType.FIELD)
								{
								}
								else
								{
									TypeCheckUtilityClass.checkAccessValid(contextParentName,motherTypeName,member.modifier );									
								}
								
								motherTypeName = member.memberType;
									
								
							}
							else
							{
								throw new CannotResolveSymbolException(motherType.typeName +"." + childName);
							}
									
							index ++;
						}
						return motherTypeName;
					}
				}
				else
				{
					throw new CannotResolveSymbolException(firstName+"." + staticMemberName);
				}
				
				
			}
			else
			{
				String motherTypeName;
				String childName;
				String motherName;
				Type motherType ;
				
				firstName = nameList.get(0);
				SymbolTableEntry entry = null;
				entry = SymbolTable.getSymbolTable().getEntry(firstName);
			
				if(entry == null)//Try on current class 
				{
					
					ArrayList<UCSYMember> members = theClass.getAllMemberFromHierarchy();
					boolean ok = false;
					for (int i = 0; i< members.size(); i++)
					{
						UCSYMember theMember = members.get(i);
						if(theMember.memberName.equals(firstName))
						{
							ok = true;
							type = theMember.memberType;
							
							
							//Check if a method is static ,it cannot access instance memeber
							if( CommonInheritedAttribute.currentMethod.isStatic() && ! UCSYClassAttribute.isStatic( theMember.modifier))
							{
								throw new StaticMethodCannotAccessInstanceFieldException(theMember.memberName);
							}
							if(theMember.typeOfMember != MemberType.FIELD)
							{
							}
							else
							{
								TypeCheckUtilityClass.checkAccessValid(contextParentName,theMember.ownerName,theMember.modifier );	
							}
							
						}
					}
					if (!ok)
						throw new CannotResolveSymbolException(firstName);
				}
				else
				{
					type = entry.type;
					
				}
				int index = 1;
							
				String childTypeName = type;
							
				ArrayList<UCSYMember> memberOfTheClass;
							
				//type = childTypeName;
				motherTypeName = childTypeName;
				while( index < nameList.size()  )	
				{
					
					motherType = CentralTypeTable.getCentralTypeTable().getType( motherTypeName );
					childName  = nameList.get( index );
					
					memberAccess = motherType;
					
					memberOfTheClass = memberAccess.getAllMemberFromHierarchy();
										
					if(isMemberContainsInTheList(memberOfTheClass, childName))
					{
						UCSYMember member = getTheMemberFromTheList(memberOfTheClass,childName);
						if(member.typeOfMember != MemberType.FIELD)
						{
						}
						else
						{
							TypeCheckUtilityClass.checkAccessValid(contextParentName,motherTypeName,member.modifier );
						}
						
						motherTypeName = member.memberType;
					}
					else
					{
						throw new CannotResolveSymbolException(motherType.typeName +"." + childName);
					}
						
					index ++;
				}
			//System.out.println ("Returning "+ motherTypeName);
			return motherTypeName;
			
			}
		}
		else //It is simple name
		{
			
			//Search first in symbol table currnet scope
			String simpleName = nameList.get(0);
			
			//Check to see if it is type name
			if( Character.isUpperCase( simpleName.charAt(0)))
			{
				//Debug.inform("This is ok ");
				return simpleName;
			}
			
			SymbolTableEntry entry = null;
			entry = SymbolTable.getSymbolTable().getEntry(simpleName);
			
			if(entry == null)//Try on current class 
			{
			
				ArrayList<UCSYMember> members = theClass.getAllMemberFromHierarchy();
				
				for (int i = 0; i< members.size(); i++)
				{
					UCSYMember theMember = members.get(i);
					if(theMember.memberName.equals(simpleName))
					{
						//System.out.println ("Ok Find "+ theMember.memberType +" owner "+ theMember.ownerName);
						if( CommonInheritedAttribute.currentMethod.isStatic() && ! UCSYClassAttribute.isStatic( theMember.modifier))
						{
							throw new StaticMethodCannotAccessInstanceFieldException(theMember.memberName);
						}
						if(theMember.typeOfMember != MemberType.FIELD)
						{
						}
						else
						{
							TypeCheckUtilityClass.checkAccessValid(contextParentName,theMember.ownerName,theMember.modifier );
						}
						
						return theMember.memberType;
					}
				}
				throw new CannotResolveSymbolException(simpleName);
			}
			else
			{
				
				type = entry.type;
				return type;	
			}
			
		}	
		}
		catch(InstanceFieldReferenceByClassReferenceException e)
		{
			throw e;
		}
		catch(InvalidAccessToMemberException e)
		{
			throw e;
		}
		catch(StaticMethodCannotAccessInstanceFieldException e )
		{
			throw e;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new CannotResolveSymbolException(nameList.toString());
		}
		//return type;
	}
	static String resolveTypeOnSimpleNameForSuffix(String currentContextParentName, String parentTypeName,String childName)throws InvalidAccessToMemberException,CannotResolveSymbolException
	{
		//parentType is type name of parent type 
		//To be used in Suffix Node
		String type = "";
		try
		{
			Type parentType = CentralTypeTable.getCentralTypeTable().getType( parentTypeName );
			MemberAccess memberAccess = parentType;
		
			ArrayList<UCSYMember> members = memberAccess.getAllMemberFromHierarchy();
		
			for (int i = 0; i<members.size() ; i++)
			{
				UCSYMember theMember = members.get(i);
			
				if( theMember.memberName.equals(childName))
				{
					TypeCheckUtilityClass.checkAccessValid(currentContextParentName,theMember.ownerName,theMember.modifier );
					return theMember.memberType;
				}
			
			}	
			throw new CannotResolveSymbolException(parentTypeName+"," + childName);
		}
		catch(InvalidAccessToMemberException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new CannotResolveSymbolException(parentTypeName+"," + childName);
		}
		
		}
	static boolean isMemberContainsInTheList(ArrayList<UCSYMember> memberList,String memberName)
	{
		for (int i = 0; i< memberList.size(); i++)
		{
			UCSYMember member = memberList.get(i);
			if(member.memberName.equals( memberName ))
				return true;
		}
		return false;
	}
	static UCSYMember getTheMemberFromTheList(ArrayList<UCSYMember> memberList, String memberName)throws NoSuchMemberInTheList
	{
		for (int i = 0; i< memberList.size(); i++)
		{
			UCSYMember member = memberList.get(i);
			if(member.memberName.equals( memberName ))
				return member;
		}
		throw new NoSuchMemberInTheList(); //However it is impossible to reach here
	}
}

class CannotResolveSymbolException extends Exception
{
	String symbolName;
	
	CannotResolveSymbolException(String name)
	{
		this.symbolName = name;
	}
}
class NoSuchMemberInTheList extends Exception
{
	NoSuchMemberInTheList()
	{
	}
}
class InstanceFieldReferenceByClassReferenceException extends Exception
{
	String fieldName;
	String className;
	
	InstanceFieldReferenceByClassReferenceException(String className, String filedName)
	{
		this.fieldName = fieldName;
		this.className = className;
	}
}
class StaticMethodCannotAccessInstanceFieldException extends Exception
{
	String fieldName;
	
	StaticMethodCannotAccessInstanceFieldException(String fieldName)
	{
		this.fieldName = fieldName;
	}
	
}