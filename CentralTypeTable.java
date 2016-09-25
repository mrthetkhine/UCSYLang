import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

class TypeNotFoundException extends Exception
{
	String typeName;
	TypeNotFoundException(String typeName)
	{
		this.typeName = typeName;
		
	}
}
class NotExceptedTypeException extends Exception
{
	String exceptedTypeName;
	String foundTypeName;
	
	NotExceptedTypeException(String exceptedTypeName, String foundTypeName)
	{
		this.exceptedTypeName = exceptedTypeName;
		this.foundTypeName    = foundTypeName;
	}
}

class CentralTypeTableEntry
{
	String typeName;
	Type type;
	boolean hasLoaded = false; // Specify whether or not the type information is loaded form the ucode file
	CentralTypeTableEntry nextElement; //To use in hash chain
	
	CentralTypeTableEntry(String typeName, Type type, boolean hasLoaded)
	{
		this.typeName = typeName;
		this.type = type;
		this.hasLoaded = hasLoaded ;
	}
}
class CentralTypeTable
{
	final int SIZE = 256;
	
	CentralTypeTableEntry[] table = new CentralTypeTableEntry[SIZE];
	
	ArrayList<String> notFoundTypes = new ArrayList<String>();
	
	//Fill the primitive and essential type in the central type table
	private static CentralTypeTable _instance ;
	
	private CentralTypeTable()
	{
		initialize();
	}
	
	static CentralTypeTable getCentralTypeTable()
	{
		if(_instance == null)
			_instance = new CentralTypeTable();
		return	_instance;
	}
	static void reset()
	{
		_instance = null;
	}
	void initialize()
	{
		CentralTypeTableEntry booleanEntry = new CentralTypeTableEntry("t",new PrimitiveType(TypeOfType.BOOLEAN),true);
		insert( booleanEntry );
		
		
		CentralTypeTableEntry byteEntry = new CentralTypeTableEntry("b",new PrimitiveType(TypeOfType.BYTE),true);
		insert( byteEntry );
		
		CentralTypeTableEntry shortEntry = new CentralTypeTableEntry("s",new PrimitiveType(TypeOfType.SHORT),true);
		insert( shortEntry );
		
		CentralTypeTableEntry intEntry = new CentralTypeTableEntry("i",new PrimitiveType(TypeOfType.INTEGER),true);
		insert( intEntry );
		
		CentralTypeTableEntry longEntry = new CentralTypeTableEntry("l",new PrimitiveType(TypeOfType.LONG),true);
		insert( longEntry );
		
		CentralTypeTableEntry floatEntry = new CentralTypeTableEntry("f",new PrimitiveType(TypeOfType.FLOAT),true);
		insert( floatEntry );
		
		CentralTypeTableEntry doubleEntry = new CentralTypeTableEntry("d",new PrimitiveType(TypeOfType.DOUBLE),true);
		insert( doubleEntry );
		
		CentralTypeTableEntry stringEntry = new CentralTypeTableEntry("m",new PrimitiveType(TypeOfType.STRING),true);
		insert( stringEntry );
		
		CentralTypeTableEntry charEntry = new CentralTypeTableEntry("c",new PrimitiveType(TypeOfType.CHAR),true);
		insert( charEntry );
		
		insertObjectClass();
		
	}
	private void insertObjectClass()
	{
		UCSYClass classType = new UCSYClass();
		classType.className = "Object";
		UCSYMethod met = new UCSYMethod();
		met.methodName ="toString";
		met.methodReturnType = "v";
		met.modifier = UCSYClassAttribute.PUBLIC;
		classType.addMethod( met);
		CentralTypeTableEntry objectEntry = new CentralTypeTableEntry("Object",classType,true);
		insert( objectEntry );
		
		UCSYClass exceptionRoot = new UCSYClass();
		exceptionRoot.className = "Exception";
		exceptionRoot.parentClassName = "Object";
		
		CentralTypeTableEntry exceptionClass = new CentralTypeTableEntry("Exception",exceptionRoot,true);
		exceptionClass.hasLoaded = false;
		insert(exceptionClass);
	}
	private int hash(String s)
	{
		int h = 0 ;
		
		for (int i = 0; i<s.length(); i++)
		{
			h = h * 1979 + s.charAt(i);
		}
		return Math.abs( h );
	}
	
	void insert(CentralTypeTableEntry entry)
	{
		entry.type.typeName = entry.typeName;
		int index = hash(entry.typeName) % SIZE ;
		
		if(table[index] == null)
		{
			table[index] = entry; //This is first entry
		}
		else                      //There are previous item      
		{
			//System.out.println ("Second");
			CentralTypeTableEntry last = table[index];//Point to first item
			
			if(entry.typeName.equals(last.typeName))
			{
				//System.out.println ("Type Table Error:Duplicate Type Entry");
				return;
			}
			while(last.nextElement != null) //Go to the last one
			{
				if(entry.typeName.equals(last.typeName))
				{
					//System.out.println ("Type Table Error:Duplicate Type Entry");
					return;
				}
				last = last.nextElement ;
			}
			
			last.nextElement = entry; //Insert at last node of chain in hash
		}
	}
	/*
	Type getType(String typeName)
	{
		Type returnType = getLoadedType(typeName);
		
		if(returnType == null) //Not already insert into the central type table insert into it
		{
			
			loadAndInsertType( typeName );
			returnType = getLoadedType( typeName );
		}
		return returnType;
	}
	*/
	
	//Given a typeName return an CentralTypeTableEntry if present
	//else return null
	CentralTypeTableEntry getEntry(String typeName)
	{
		//Search the type entry using its name and return it ;
		
		int index = hash(typeName) % SIZE ;
		
		CentralTypeTableEntry currentItem = table[index];
		
		while( currentItem != null)
		{
			if(currentItem.typeName.equals(typeName))
			{
				return currentItem;
			}
			else
				currentItem = currentItem.nextElement;
		}
		
		return currentItem;
		
	}
	
	void insertTypeIfNotExist(CentralTypeTableEntry entry)
	{
		if(! this.isTypeExist(entry.typeName))
		{
			insert(entry);
		}
	}
	Type getType(String typeName) throws TypeNotFoundException
	{
		
		
		CentralTypeTableEntry entry = getEntry(typeName);
		if(entry != null )
		{
			if(entry.hasLoaded )
			{	
				return entry.type;
			}
			else
			{
				/**************************************************************
				 *We must load a type from Hard Disk ,but currently ignored
				 **************************************************************/
				try
				{
					CentralTypeTableEntry newEntry = null;
					if(! CentralTypeTable.isArray(typeName))
					{
						ClassReader reader = new ClassReader();
						Type theClass = reader.readClass(typeName);
						newEntry = new CentralTypeTableEntry(typeName,theClass,true);
						newEntry.type.typeName = theClass.typeName;
						CentralTypeTable.getCentralTypeTable().insert(newEntry);
					}
					else
					{
						
						int dimension = typeName.lastIndexOf("[")+1;
						String arrayTypeName = CentralTypeTable.getTypeNameFromArray( typeName );
						
						Type t = CentralTypeTable.getCentralTypeTable().getType( arrayTypeName );
					
						ArrayType arrayType = new ArrayType(arrayTypeName,dimension);
						newEntry = new CentralTypeTableEntry( typeName, arrayType,true);
						CentralTypeTable.getCentralTypeTable().insert(newEntry);
					}
					return newEntry.type;
				}		
				catch(Exception e)
				{
					
					//System.out.println ("Cannot Read from file "+typeName);	
					//e.printStackTrace();
					throw new TypeNotFoundException(typeName);
				}
			}
		}
		else
		{
			try
			{
				CentralTypeTableEntry newEntry = null;
				//System.out.println ("Loading "+typeName);
				if(! CentralTypeTable.isArray(typeName))
				{
					ClassReader reader = new ClassReader();
					Type theClass = reader.readClass(typeName);
					newEntry = new CentralTypeTableEntry(typeName,theClass,true);
					CentralTypeTable.getCentralTypeTable().insert(newEntry);
				}
				else
				{
					int dimension = typeName.lastIndexOf("[")+1;
					String arrayTypeName = CentralTypeTable.getTypeNameFromArray( typeName );
					Type t = CentralTypeTable.getCentralTypeTable().getType( arrayTypeName );
					
					ArrayType arrayType = new ArrayType(arrayTypeName,dimension);
					newEntry = new CentralTypeTableEntry( typeName, arrayType,true);
					CentralTypeTable.getCentralTypeTable().insert(newEntry);
				}
				return newEntry.type;
			}		
			catch(Exception e)
			{
				
				//System.out.println ("Cannot Read from file  "+typeName);	
				//e.printStackTrace();
				throw new TypeNotFoundException(typeName);
			}
			
			//throw new TypeNotFoundException(typeName);
		}
		
	}
	boolean isTypeExist(String typeName )
	{
		
		try
		{
			Type type = getType(typeName);
			
			
			return true;	
		}
		catch(TypeNotFoundException e)
		{
			
			return false;
		}
		catch(Throwable e)
		{	
			
			return false;
		}
				
	}
	UCSYClass getAClass(String typeName )throws TypeNotFoundException, NotExceptedTypeException
	{
		
		if( isTypeExist( typeName ))
		{
			
			Type type = getType( typeName );
			if(type.getType() == TypeOfType.CLASSTYPE )
			{
				return (UCSYClass)( type);
			}
			else
			{
				throw new NotExceptedTypeException( typeName, type.typeNameDescription );
			}
		}
		else
		{
			throw new TypeNotFoundException( typeName);	
		}
	}
	UCSYMetaClass getAMetaClass(String typeName )throws TypeNotFoundException, NotExceptedTypeException
	{
		if( isTypeExist( typeName ))
		{
			Type type = getType( typeName );
			if(type.getType() == TypeOfType.METATYPE )
			{
				return (UCSYMetaClass)( type);
			}
			else
			{
				throw new NotExceptedTypeException( typeName, type.typeNameDescription );
			}
		}
		else
		{
			throw new TypeNotFoundException( typeName);	
		}
	}
	UCSYInterface getAInterface(String typeName )throws TypeNotFoundException, NotExceptedTypeException
	{
		if( isTypeExist( typeName ))
		{
			Type type = getType( typeName );
			if(typeName.equals("Object"))
			{
				UCSYInterface theInterface = new UCSYInterface();
				theInterface.interfaceName ="Object";
				return theInterface;
			}
			if(type.getType() == TypeOfType.INTERFACETYPE )
			{
				return (UCSYInterface)( type);
			}
			else
			{
				throw new NotExceptedTypeException( typeName, type.typeNameDescription );
			}
		}
		else
		{
			throw new TypeNotFoundException( typeName);	
		}
	}
	static boolean isMethodStatic(int mo)
	{
		return ( mo & UCSYClassAttribute.STATIC ) == UCSYClassAttribute.STATIC ;
	}
	static boolean isAbstract(int mo)
	{
		return (mo & UCSYClassAttribute.ABSTRACT ) ==UCSYClassAttribute.ABSTRACT;
	
	}
	static boolean isFinal(int mo)
	{
		return (mo & UCSYClassAttribute.FINAL ) ==UCSYClassAttribute.FINAL;
	}
	static boolean isInterface(String interfaceName)//Normal Name
	{
		Type entry = null;
		try
		{
		    entry = CentralTypeTable.getCentralTypeTable().getType(interfaceName);
			return entry.getType() == TypeOfType.INTERFACETYPE;
		}
		catch(TypeNotFoundException e)
		{
			return false;
		}
	}
	static boolean isClass(String className )
	{
		Type entry = null; 
		try
		{
		
			entry= CentralTypeTable.getCentralTypeTable().getType( className );
			return entry.getType() == TypeOfType.CLASSTYPE;
		}
		catch(TypeNotFoundException e)
		{
			return false;
		}
		
		
	}
	static boolean isMetaClass(String metaClassName )
	{
		Type entry;
		try
		{
			entry = CentralTypeTable.getCentralTypeTable().getType( metaClassName );
			return entry.getType() == TypeOfType.METATYPE;
		}
		catch(TypeNotFoundException e)
		{
			return false;
		}
	}
	
	//***************************************************************************
	static boolean isCharacter(String typeName)
	{
		return typeName.equals("c");
	}
	static boolean isBoolean(String typeName)
	{
		return typeName.equals("t");
	}
	static boolean isByte(String typeName)
	{
		return typeName.equals("b");
	}
	static boolean isShort(String typeName)
	{
		return typeName.equals("s");
	}
	static boolean isInteger(String typeName)
	{
		return typeName.equals("i");
	}
	static boolean isLong(String typeName)
	{
		return typeName.equals("l");
	}
	static boolean isFloat(String typeName)
	{
	
		return typeName.equals("f");
	}
	static boolean isDouble(String typeName)
	{
		return typeName.equals("d");
	}
	static boolean isString(String typeName)
	{
		return typeName.equals("m");
	}
	static boolean isArray(String typeName)
	{
		return typeName.startsWith("[");
	}
	static String getTypeNameFromArray(String typeName)
	{
		int lastIndex = typeName.lastIndexOf("[");
		String arrayTypeName = typeName.substring(lastIndex+1,typeName.length());
		
		return arrayTypeName;
	}
	static boolean isPrimitiveType(String typeName)
	{
		return ( isBoolean(typeName) ||
				 isCharacter(typeName)||
				 isByte(typeName)||
				 isShort(typeName)||
				 isInteger(typeName)||
				 isLong(typeName)||
				 isFloat(typeName)||
				 isDouble(typeName)||
				 isString(typeName)
				 	
				 );
	}
	static boolean isLowerIntegralType(String typeName)
	{
		return (isByte(typeName ) ||
				isShort(typeName));
	}
	static boolean isNumberType(String typeName)
	{
		return 
		(
			isLowerIntegralType(typeName)||
			isLong( typeName)     ||
			isInteger( typeName ) ||
			isFloat( typeName )   ||
			isDouble( typeName )
		);
	}
	static boolean isIntegeralType(String typeName)
	{
		return isPrimitiveType(typeName) && ( isLowerIntegralType(typeName) || 
									isInteger(typeName)||
									isLong(typeName));
	}
	static boolean isRealType(String typeName)
	{
		return isFloat(typeName) || isDouble(typeName);
	}
	static boolean isUVMIntegerType(String typeName)
	{
		return CentralTypeTable.isBoolean(typeName) ||	
				CentralTypeTable.isCharacter(typeName) ||
				CentralTypeTable.isLowerIntegralType(typeName) ||
				CentralTypeTable.isInteger(typeName);
	}
	static boolean isVoid(String typeName)
	{
		return typeName.equals("v");
	}
	static boolean canPromoteUsingRuleOne(String source,String destination)
	{
		if( destination.equals( source ))
		{
			return true;
		}
		else if( CentralTypeTable.isBoolean( destination ) )
		{
			return CentralTypeTable.isBoolean(source);
		}
		else if( CentralTypeTable.isCharacter( destination ))
		{
			return CentralTypeTable.isCharacter( source );
		}
		else if( CentralTypeTable.isByte( destination ) )
		{
			return CentralTypeTable.isByte( source );
		}
		else if( CentralTypeTable.isShort( destination ))
		{
			return CentralTypeTable.isByte( source ) ||
					CentralTypeTable.isShort( source );
		}
		else if( CentralTypeTable.isInteger( destination ))
		{
			return CentralTypeTable.isByte( source )||
				CentralTypeTable.isShort( source )  ||
				CentralTypeTable.isInteger( source );
		}
		else if(CentralTypeTable.isLong( destination ))
		{
			return CentralTypeTable.isByte( source )||
					CentralTypeTable.isShort( source )||
					CentralTypeTable.isInteger( source )||
					CentralTypeTable.isLong( source );
		}
		else if(CentralTypeTable.isFloat( destination ) )
		{
			return CentralTypeTable.isFloat( source );
		}
		else if(CentralTypeTable.isDouble( destination) )
		{
			return CentralTypeTable.isFloat( source )||
				   CentralTypeTable.isDouble( source );
		}
		else 
		{
			return false;
		}
	}
	
	static int getSizeOfType(String typeName)
	{
		//All type expect doulbe and long occupy 1 , long and double occupy two word
		//Size is measured in word size
		if( typeName.equals("v"))
		{
			return 0;
		}
		else if(CentralTypeTable.isLong( typeName) || CentralTypeTable.isDouble( typeName) )
		{
			return 2;
		}
		else
		{
			return 1;
		}
	}
	static int getSizeForSignature(String methodSignature)
	{
		String[] par = MethodOverloadResolution.splitParameter( methodSignature );
		int totalSize = 0;
		for (int i = 0; i< par.length; i++)
		{
			totalSize += CentralTypeTable.getSizeOfType( par[i]);
		}
		return totalSize;
	}
	static int getNoOfDimension(String arrayName)
	{
		int dim = 0;
		dim = arrayName.lastIndexOf("[")+1;
		return dim;
	}
	
}




class DuplicateTypeEntryException extends Exception
{
	String typeName;
	
	public DuplicateTypeEntryException(String typeName)
	{
		this.typeName = typeName;
	}
}