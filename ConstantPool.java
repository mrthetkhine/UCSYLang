import java.util.*;


class ConstantPool
{
	ArrayList<ConstantPoolEntry> constantPool = new ArrayList<ConstantPoolEntry>();
	

	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(StringEntry strEntry)
	{
		if(strEntry.value == null)
		{
			Debug.inform("Null Value found in inserting StringEntry in constant pool");
			return;
		}
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof StringEntry)
			{
				StringEntry str = (StringEntry)entry;
				if(str.value.equals( strEntry.value ))
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( strEntry );
	}
	private int getStringIndexEntry( StringEntry strEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof StringEntry)
			{
				StringEntry str = (StringEntry)entry;
				if(str.value.equals( strEntry.value ))
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("STRING Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addStringRef(String str)
	{
		StringEntry strEntry = new StringEntry(str);
		this.insert( strEntry );
	}
	int getStringIndex(String str)
	{
		addStringRef(str);
		StringEntry strEntry = new StringEntry(str);
		return this.getStringIndexEntry( strEntry );
	}
	/*********************************************************************************************/
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(IntegerConstantEntry intEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof IntegerConstantEntry)
			{
				IntegerConstantEntry integerEntry = (IntegerConstantEntry)entry;
				if(integerEntry.intValue == intEntry.intValue )
				{
					
					//Duplicate Not Insert
					return;
				}
				
			}
		}
		
		//No entry insert before
		constantPool.add( intEntry );
	}
	private int getIntegerIndexEntry( IntegerConstantEntry intEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof IntegerConstantEntry )
			{
				IntegerConstantEntry integerEntry = (IntegerConstantEntry)entry;
				if( integerEntry.intValue == intEntry.intValue )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("INTEGER Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addIntegerRef(int intValue)
	{
		IntegerConstantEntry integerConstant = new IntegerConstantEntry( intValue );
		this.insert( integerConstant );
	}
	int getIntegerIndex(int intValue)
	{
		addIntegerRef(intValue);
		
		IntegerConstantEntry intEntry = new IntegerConstantEntry(intValue);
		return this.getIntegerIndexEntry( intEntry );
	}
	/*********************************************************************************************/
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(LongConstantEntry lEntry)
	{
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof LongConstantEntry)
			{
				LongConstantEntry longEntry = (LongConstantEntry)entry;
				if(longEntry.longValue == lEntry.longValue )
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( lEntry );
	}
	private int getLongIndexEntry( LongConstantEntry lEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof LongConstantEntry )
			{
				LongConstantEntry longEntry = (LongConstantEntry)entry;
				if( longEntry.longValue == lEntry.longValue )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("Long Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addLongRef(long longValue)
	{
		LongConstantEntry longConstant = new LongConstantEntry( longValue );
		this.insert( longConstant );
	}
	int getLongIndex(long longValue)
	{
		addLongRef( longValue);
		LongConstantEntry longEntry = new LongConstantEntry( longValue );
		return this.getLongIndexEntry( longEntry );
	}
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(FloatConstantEntry fEntry)
	{
		///Debug.inform("Inserting "+fEntry.floatValue);
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof FloatConstantEntry)
			{
				FloatConstantEntry floatEntry = (FloatConstantEntry)entry;
				if(floatEntry.floatValue == fEntry.floatValue )
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		
		constantPool.add( fEntry );
	}
	private int getFloatIndexEntry( FloatConstantEntry fEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof FloatConstantEntry )
			{
				FloatConstantEntry floatEntry = (FloatConstantEntry)entry;
				if( floatEntry.floatValue == fEntry.floatValue )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("Float Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addFloatRef(float floatValue)
	{
		///Debug.inform("ConstantPool.java Adding FloatReference "+floatValue);
		FloatConstantEntry floatConstant = new FloatConstantEntry( floatValue );
		this.insert( floatConstant );
	}
	int getFloatIndex(float floatValue)
	{
		addFloatRef( floatValue);
		FloatConstantEntry floatEntry = new FloatConstantEntry( floatValue );
		
		int index = getFloatIndexEntry( floatEntry );
		///Debug.inform("Get Float Index "+floatValue+" index is "+ index);
		return index;
	}
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(DoubleConstantEntry dEntry)
	{
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof DoubleConstantEntry)
			{
				DoubleConstantEntry doubleEntry = (DoubleConstantEntry)entry;
				if(doubleEntry.doubleValue == dEntry.doubleValue )
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( dEntry );
	}
	private int getDoubleIndexEntry( DoubleConstantEntry dEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof DoubleConstantEntry )
			{
				DoubleConstantEntry doubleEntry = (DoubleConstantEntry)entry;
				if( doubleEntry.doubleValue == dEntry.doubleValue )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("Double Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addDoubleRef(double doubleValue)
	{
		DoubleConstantEntry doubleConstant = new DoubleConstantEntry( doubleValue );
		this.insert( doubleConstant );
	}
	int getDoubleIndex(double doubleValue)
	{
		addDoubleRef( doubleValue);
		DoubleConstantEntry doubleEntry = new DoubleConstantEntry( doubleValue );
		return this.getDoubleIndexEntry( doubleEntry );
	}
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(ClassEntry cEntry)
	{
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof ClassEntry)
			{
				ClassEntry classEntry = (ClassEntry)entry;
				if(classEntry.classNameIndex == cEntry.classNameIndex )
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( cEntry );
	}
	private int getClassIndexEntry( ClassEntry cEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof ClassEntry )
			{
				ClassEntry classEntry = (ClassEntry)entry;
				if( classEntry.classNameIndex == cEntry.classNameIndex )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("Class Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addClassRef(String className)
	{
		this.addStringRef( className );
		int strIndex = this.getStringIndex( className );
		
		ClassEntry classEntry = new ClassEntry( strIndex );
		this.insert( classEntry );
	}
	int getClassIndex(String className)
	{
		//addClassRef( className );
	
		this.addStringRef( className );
		int strIndex = this.getStringIndex( className );
		
		ClassEntry classEntry = new ClassEntry( strIndex );
		return this.getClassIndexEntry( classEntry );
	}
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(FieldReferenceEntry fEntry)
	{
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof FieldReferenceEntry)
			{
				FieldReferenceEntry fieldEntry = (FieldReferenceEntry)entry;
				if( fieldEntry.classNameIndex == fEntry.classNameIndex && fieldEntry.fieldNameIndex == fEntry.fieldNameIndex && fieldEntry.fieldTypeIndex== fEntry.fieldTypeIndex)
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( fEntry );
	}
	private int getFieldRefIndexEntry( FieldReferenceEntry fEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof FieldReferenceEntry )
			{
				FieldReferenceEntry fieldEntry = (FieldReferenceEntry)entry;
				if( fieldEntry.classNameIndex == fEntry.classNameIndex && fieldEntry.fieldNameIndex == fEntry.fieldNameIndex && fieldEntry.fieldTypeIndex== fEntry.fieldTypeIndex )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("FieldRef Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addFieldRef(String className,String fieldName,String fieldType)
	{
		this.addClassRef( className );
		int cIndex = this.getClassIndex( className );
		
		this.addStringRef( fieldName );
		int fieldNameIndex = this.getStringIndex( fieldName );
		
		this.addStringRef( fieldType );
		int fieldTypeIndex = this.getStringIndex( fieldType );
		
		FieldReferenceEntry fieldRef = new FieldReferenceEntry( cIndex, fieldNameIndex, fieldTypeIndex);
		this.insert( fieldRef );
	}
	int getFieldRefIndex(String className,String fieldName,String fieldType)
	{
		//addClassRef( className );
	
		this.addClassRef( className );
		int cIndex = this.getClassIndex( className );
		
		this.addStringRef( fieldName );
		int fieldNameIndex = this.getStringIndex( fieldName );
		
		this.addStringRef( fieldType );
		int fieldTypeIndex = this.getStringIndex( fieldType );
		
		FieldReferenceEntry fieldRef = new FieldReferenceEntry( cIndex, fieldNameIndex, fieldTypeIndex);
		this.insert( fieldRef );
		
		
		
		return this.getFieldRefIndexEntry( fieldRef );
	}
	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(MethodReferenceEntry mEntry)
	{
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof MethodReferenceEntry)
			{
				MethodReferenceEntry methodEntry = (MethodReferenceEntry)entry;
				if( methodEntry.classNameIndex == mEntry.classNameIndex && methodEntry.methodNameIndex == mEntry.methodNameIndex && methodEntry.methodSignatureIndex== mEntry.methodSignatureIndex)
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( mEntry );
	}
	private int getMethodRefIndexEntry( MethodReferenceEntry mEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof MethodReferenceEntry )
			{
				MethodReferenceEntry methodEntry = (MethodReferenceEntry)entry;
				if( methodEntry.classNameIndex == mEntry.classNameIndex && methodEntry.methodNameIndex == mEntry.methodNameIndex && methodEntry.methodSignatureIndex== mEntry.methodSignatureIndex )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("MethodRef Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addMethodRef(String className,String methodName,String methodSignature)
	{
		this.addClassRef( className );
		int cIndex = this.getClassIndex( className );
		
		this.addStringRef( methodName );
		int methodNameIndex = this.getStringIndex( methodName );
		
		this.addStringRef( methodSignature );
		int methodSignatureIndex = this.getStringIndex( methodSignature );
		
		MethodReferenceEntry methodRef = new MethodReferenceEntry( cIndex, methodNameIndex, methodSignatureIndex);
		this.insert( methodRef );
	}
	int getMethodRefIndex(String className,String methodName,String methodSignature)
	{
		//addClassRef( className );
	
		this.addClassRef( className );
		int cIndex = this.getClassIndex( className );
		
		this.addStringRef( methodName );
		int methodNameIndex = this.getStringIndex( methodName );
		
		this.addStringRef( methodSignature );
		int methodSignatureIndex = this.getStringIndex( methodSignature );
		
		MethodReferenceEntry methodRef = new MethodReferenceEntry( cIndex, methodNameIndex, methodSignatureIndex);
		this.insert( methodRef );
		
		
		return this.getMethodRefIndexEntry( methodRef );
	}

	/******************************* Internal Method used by ConstantPool2 ******/
	private void insert(InterfaceMethodReferenceEntry imEntry)
	{
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof InterfaceMethodReferenceEntry)
			{
				InterfaceMethodReferenceEntry iMethodEntry = (InterfaceMethodReferenceEntry)entry;
				if( iMethodEntry.classNameIndex == imEntry.classNameIndex && iMethodEntry.methodNameIndex == imEntry.methodNameIndex && iMethodEntry.methodSignatureIndex== imEntry.methodSignatureIndex)
				{
					//Duplicate Not Insert
					return;
				}
			}
		}
		//No entry insert before
		constantPool.add( imEntry );
	}
	private int getInterfaceMethodRefIndexEntry( InterfaceMethodReferenceEntry imEntry)
	{
		
		for (int i = 0; i< constantPool.size(); i++)
		{
			ConstantPoolEntry entry = constantPool.get(i);
			if( entry instanceof InterfaceMethodReferenceEntry )
			{
				InterfaceMethodReferenceEntry iMethodEntry = (InterfaceMethodReferenceEntry)entry;
				if( iMethodEntry.classNameIndex == imEntry.classNameIndex && iMethodEntry.methodNameIndex == imEntry.methodNameIndex && iMethodEntry.methodSignatureIndex== imEntry.methodSignatureIndex )
				{
					//Found
					return i;
				}
			}
		}
		Debug.inform("MethodRef Not Exist ");
		return -1;
	}
	
	/*********** User must use the following routine for accessing constant pool ******************/
	void addInterfaceMethodRef(String className,String methodName,String methodSignature)
	{
		this.addClassRef( className );
		int cIndex = this.getClassIndex( className );
		
		this.addStringRef( methodName );
		int methodNameIndex = this.getStringIndex( methodName );
		
		this.addStringRef( methodSignature );
		int methodSignatureIndex = this.getStringIndex( methodSignature );
		
		InterfaceMethodReferenceEntry iMethodRef = new InterfaceMethodReferenceEntry( cIndex, methodNameIndex, methodSignatureIndex);
		this.insert( iMethodRef );
	}
	int getInterfaceMethodRefIndex(String className,String methodName,String methodSignature)
	{
		//addClassRef( className );
	
		this.addClassRef( className );
		int cIndex = this.getClassIndex( className );
		
		this.addStringRef( methodName );
		int methodNameIndex = this.getStringIndex( methodName );
		
		this.addStringRef( methodSignature );
		int methodSignatureIndex = this.getStringIndex( methodSignature );
		
		InterfaceMethodReferenceEntry iMethodRef = new InterfaceMethodReferenceEntry( cIndex, methodNameIndex, methodSignatureIndex);
		this.insert( iMethodRef );
		
		
		return this.getInterfaceMethodRefIndexEntry( iMethodRef );
	}


/********************* Following mehthod are to be used by ClassReader 
 * to reconstruct ConstantPool back from ucode file
 **********************************************************************/
 void insertClassEntry(ClassEntry entry)
 {
 	this.insert(entry);
 }
 void insertDoubleEntry(DoubleConstantEntry entry)
 {
 	this.insert(entry);
 }
 void insertFloatEntry(FloatConstantEntry entry)
 {
 	this.insert(entry);
 }
 void insertIntegerEntry(IntegerConstantEntry entry)
 {
 	this.insert(entry);
 }
 void insertLongEntry(LongConstantEntry entry)
 {
 	this.insert(entry);
 }
 void insertStringEntry(StringEntry entry)
 {
 	this.insert( entry );
 }
 void insertFieldReferenceEntry(FieldReferenceEntry entry)
 {
 	this.insert( entry );
 }
 void insertInterfaceMethodReferenceEntry(InterfaceMethodReferenceEntry entry)
 {
 	this.insert( entry );
 }
 void insertMethodReferenceEntry(MethodReferenceEntry entry)
 {
 	this.insert( entry );
 }
 
}