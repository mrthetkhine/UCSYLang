import java.util.*;

class SymbolTableEntry
{
	String name;
	String type ;
	boolean used ;
	//int index = -1;
	int indexOffestOfLocalVar = -1;
	
	SymbolTableEntry nextElement;
	
	SymbolTableEntry(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	SymbolTableEntry(String name, String type,int index)
	{
		this(name,type);
		this.indexOffestOfLocalVar = index;
	}
}

class SymbolTable
{
	final int SIZE = 256;
	private static SymbolTable _instance = null;
	
	
	//For Code Generation 
	int currentLocalVarIndex = 0;
	int maxNoOfLocalVariable = 0;
	//To store stack of ecah scope identifier count
	Stack<Integer> noOfIdentifierInScope = new Stack<Integer>();
	
	Stack<String>  nameOfVariableInScope = new Stack<String>();
	SymbolTableEntry []table = new SymbolTableEntry[SIZE];
	
	SymbolTable()
	{
		
	}
	
	static SymbolTable getSymbolTable()
	{
		if(_instance == null)
			_instance = new SymbolTable();
		return	_instance;
	}
	static void reset()
	{
		_instance = null;
	}
	private int hash(String s)
	{
		int h = 0 ;
		for (int i = 0; i<s.length(); i++)
		{
			h = h * 1979 + s.charAt(i);
		}
		return Math.abs(h);
	}
	void changeMaxLocalVariable()
	{
		if(this.maxNoOfLocalVariable < this.currentLocalVarIndex)
		{
			this.maxNoOfLocalVariable = this.currentLocalVarIndex;
		}
	}
	
	void startMethod()
	{
		this.currentLocalVarIndex = 0;
		this.maxNoOfLocalVariable = 0;
	}
	void insert(SymbolTableEntry entry)throws DuplicateNameException
	{
		
		int index = hash( entry.name ) % SIZE ;
		
		if( table[index] == null) //First entry
		{
			//Insert here
			entry.indexOffestOfLocalVar = this.currentLocalVarIndex ;
			int sizeOfVariable = CentralTypeTable.getSizeOfType(entry.type);
			this.currentLocalVarIndex += sizeOfVariable;
			changeMaxLocalVariable();
			this.addAnIdentifierInScope( entry.name );
			table[ index ] = entry;
		}
		else //Insert at the last chain in the list
		{
			SymbolTableEntry last = table[ index ] ; //Point to the first item
			
			if( entry.name.equals(last.name) )
			{
				
				throw new DuplicateNameException(entry.name,last.type);
			}
			while( last.nextElement != null )
			{
				if( entry.name.equals(last.name) )
				{
					
					throw new DuplicateNameException(entry.name,last.type);
				}
				last = last.nextElement;	
			}
			
			//Insert here
			this.addAnIdentifierInScope( entry.name );
			entry.indexOffestOfLocalVar = this.currentLocalVarIndex ;
			int sizeOfVariable = CentralTypeTable.getSizeOfType(entry.type);
			this.currentLocalVarIndex += sizeOfVariable;
			changeMaxLocalVariable();
			last.nextElement = entry;
			
		}
	}
	
	void remove(String identifierName)
	{
		int index = hash( identifierName ) % SIZE;
		SymbolTableEntry currentItem = table[index];
		SymbolTableEntry previous = null;
		//Check to see it is first 
		if( currentItem.name.equals( identifierName ))
		{
			this.currentLocalVarIndex -= CentralTypeTable.getSizeOfType(table[index].type);
			table[index] = null;
		}
		else
		{
			previous = currentItem;
			currentItem = currentItem.nextElement ;
			while( currentItem != null )
			{
				if( currentItem.name.equals( identifierName )) //Delete it
				{
					this.currentLocalVarIndex -= CentralTypeTable.getSizeOfType(table[index].type);
					//previous.nextElement = null;
					previous.nextElement = currentItem.nextElement;
					return;
				}
				else
				{
					previous = currentItem;
					currentItem = currentItem.nextElement ;
				}
			}
		}
	}
	
	SymbolTableEntry getEntry(String identifierName)
	{
		int index = hash( identifierName ) % SIZE;
		
		SymbolTableEntry currentItem = table[index];
		
		while( currentItem != null )
		{
			if( currentItem.name.equals( identifierName ))
			{
				return currentItem;
			}
			else
			{
				currentItem = currentItem.nextElement;
			}
		}
		return currentItem ;
	}
	SymbolTableEntry resolveSymbol(String identifierName)throws NoSymbolException
	{
		SymbolTableEntry entry = getEntry(identifierName);
		
		if(entry == null)
			throw new NoSymbolException(identifierName);
		else
			return entry;
	}
	public void openScope()
	{
		this.noOfIdentifierInScope.push( new Integer(0));
	}
	public void addAnIdentifierInScope(String idName)
	{
		int noOfIdentifier = noOfIdentifierInScope.pop();
		noOfIdentifierInScope.push( ++noOfIdentifier );
		nameOfVariableInScope.push( idName );
	}
	public void closeScope()
	{
		int noOfIdentifierToRemove = this.noOfIdentifierInScope.pop();
		
		for (int i = 0; i< noOfIdentifierToRemove; i++)
		{
			String idToRemove = nameOfVariableInScope.pop();
			
			this.remove( idToRemove );
		}
	}
}

class DuplicateNameException extends Exception
{
	String varName;
	String typeName; //Type Name of previously declared identifier
	DuplicateNameException(String name,String typeName)
	{
		this.varName = name;
		this.typeName = typeName;
	}
}
class NoSymbolException extends Exception
{
	String identifierName;
	
	public NoSymbolException(String name)
	{
		this.identifierName = name;
	}
}