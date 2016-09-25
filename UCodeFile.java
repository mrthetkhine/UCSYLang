import java.util.*;
import java.io.*;

class UCodeFile
{
	static int magicCode = 0xBABEBABE;
	ConstantPool constantPool = new ConstantPool();
	
	int classModifier ;
	String className;
	String superClassName = "Object";
	
	ArrayList<String> interfaces = new ArrayList<String>();
	ArrayList<UCSYField> fields  = new ArrayList<UCSYField>();
	ArrayList<Method>    methods = new ArrayList<Method>();
	
	void setConstantPool(ConstantPool pool)
	{
		this.constantPool = pool;
	}
	void setClassName(String className)
	{
		this.className = className;
	}
	void setSuperClassName(String superClass)
	{
		this.superClassName = superClass;
	}
	void addInterface(String interfaceName)
	{
		this.interfaces.add( interfaceName );
	}
	void addField(UCSYField field)
	{
		this.fields.add(field);
	}
	
	void addMethod(Method method)
	{
		this.methods.add( method );
	}
	/***************************************************************************
	u4 MAGIC_CODE
	u1 Version
	u2 ConstantPool_Count
	ConstantPoolEntry[ ConstantPool_Count]
	u2 classModifier
	u2 thisClassIndex
	u2 superClassIndex
	u2 interface_count
	u2 interfaces[interface_count]
	u2 field_count
	Field [field_count]
	u2 method_count
	Method [method_count]
	
	Field 
	u2 modifier
	u2 nameIndex
	u2 typeIndex
 	****************************************************************************/
	
	void prepareUCodeFile()
	{
		///Debug.inform("Prodcuing ucode for "+this.className);
		this.constantPool.addStringRef(this.className);
		this.constantPool.addStringRef(this.superClassName);
		for (int i = 0; i<this.interfaces.size(); i++)
		{
			String interfaceName = this.interfaces.get(i);
			
			this.constantPool.addStringRef(interfaceName);
		}
		
		//***************** For Field ******************************************
		for (int i = 0; i< this.fields.size(); i++)
		{
			UCSYField field = this.fields.get(i);
			
			String fieldName = field.fieldName;
			String fieldType = field.fieldType;
			this.constantPool.addStringRef( fieldName );
			this.constantPool.addStringRef( fieldType );
		}
		
		//*************************** For Method  ******************************
		for (int i = 0; i< this.methods.size(); i++)
		{
			
			Method method = this.methods.get(i);
			String methodName = method.methodName;
			String methodSignature = method.methodSignature;
			String methodReturnType = method.methodReturnType;
			method.methodProtocol = methodSignature + methodReturnType;
			///Debug.inform("Method "+ methodName +" mes "+ methodSignature+" ret "+ methodReturnType);
			this.constantPool.addStringRef( methodName );
			this.constantPool.addStringRef( method.methodProtocol );
		}
	}
	void writeMagicCodeAndVersion(DataOutputStream ucode)throws IOException
	{
		ucode.writeInt( UCodeFile.magicCode );
		ucode.writeByte(1);
		
	}
	void writeConstantPool(DataOutputStream ucode)throws IOException
	{
		ucode.writeShort( this.constantPool.constantPool.size());
		
		for (int i = 0; i< this.constantPool.constantPool.size(); i++)
		{
			ConstantPoolEntry entry = this.constantPool.constantPool.get(i);
			
			entry.write(ucode);
		}
	}
	void writeClassAndSuperClass(DataOutputStream ucode)throws IOException
	{
		ucode.writeShort(this.classModifier);
		
		
		int classIndex = this.constantPool.getStringIndex(this.className);
		//Debug.inform( "Writing class Index "+ classIndex);
		ucode.writeShort(classIndex);
		
		
		int superIndex = this.constantPool.getStringIndex(this.superClassName);
		ucode.writeShort(superIndex);
		
		
	}
	void writeInterfaces(DataOutputStream ucode)throws IOException
	{
		ucode.writeShort(this.interfaces.size());
		for (int i = 0; i< this.interfaces.size(); i++)
		{
			int index = this.constantPool.getStringIndex(this.interfaces.get(i));
			ucode.writeShort( index );
		}
	}
	void writeFields(DataOutputStream ucode)throws IOException
	{
		ucode.writeShort( this.fields.size());
		for (int i = 0; i< this.fields.size(); i++)
		{
			UCSYField field = this.fields.get(i);
			
			
			int nameIndex = this.constantPool.getStringIndex(field.fieldName );
			int typeIndex  = this.constantPool.getStringIndex( field.fieldType);
			ucode.writeShort(field.modifier);
			ucode.writeShort( nameIndex );
			ucode.writeShort( typeIndex );
			
		}
	}
	void writeMethods(DataOutputStream ucode)throws IOException
	{
		ucode.writeShort(this.methods.size());
		for (int i = 0; i< this.methods.size(); i++)	
		{
			Method method = this.methods.get(i);
			int nameIndex = this.constantPool.getStringIndex( method.methodName);
			int signatureIndex = this.constantPool.getStringIndex(  method.methodSignature +method.methodReturnType);
		    ///Debug.inform("Producing code for method "+method.methodName +" of "+ method.ownerName+" size of arg "+method.sizeOfArgument);
			ucode.writeShort( method.modifier);
			ucode.writeShort( nameIndex );
			ucode.writeShort( signatureIndex );
			///Debug.inform("Size Of Argument "+ method.sizeOfArgument +" Code Size "+ method.methodCode.cp);
			ucode.writeShort( method.sizeOfArgument );
			ucode.writeShort( method.sizeOfLocalVar );
			ucode.writeShort( method.methodCode.maxStack );
			
			ucode.writeShort(method.exceptionTable.size());
			ucode.writeShort( method.methodCode.cp);
			ucode.write( method.methodCode.code,0,method.methodCode.cp);
			
			for (int j = 0; j<method.exceptionTable.size(); j++)
			{
				///Debug.inform("Writin exception table");
				ExceptionTable tab = method.exceptionTable.get(j);
				ucode.writeShort(tab.from);
				ucode.writeShort(tab.to);
				ucode.writeShort(tab.target);
				ucode.writeShort(tab.exceptionClassIndex);
			}
		}
	}
	void produceUCodeFile()
	{
		prepareUCodeFile();
		try
		{
			DataOutputStream  ucodeFile = new DataOutputStream(new FileOutputStream( this.className+".ucode"));
			this.writeMagicCodeAndVersion(ucodeFile);
			this.writeConstantPool(ucodeFile);
			this.writeClassAndSuperClass(ucodeFile);
			this.writeInterfaces(ucodeFile);
			this.writeFields(ucodeFile);
			this.writeMethods(ucodeFile);
			ucodeFile.flush();
			ucodeFile.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}