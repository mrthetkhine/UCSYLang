import java.io.*;
import java.util.*;

class ClassReader
{
	DataInputStream din ;
	ConstantPool constantPool = new ConstantPool();
	
	UCSYClass theClass = new UCSYClass();
	ArrayList<UCSYField> fields = new ArrayList<UCSYField>();
	ArrayList<UCSYMethod> methods = new ArrayList<UCSYMethod>();
	ArrayList<String> superInterfaces = new ArrayList<String>();
	String superClass ;
	int classAttribute;
	String className;
	
	void readMagicAndVersion(DataInputStream din)throws IOException
	{
		din.readInt();
		din.readByte();
	}
	void readConstantPool(DataInputStream din)throws IOException
	{
		int cCount = din.readShort();
		
		for (int i = 0; i< cCount; i++)
		{
			int tag = din.readByte();
			
			switch(tag)
			{
				case UVMConstants.CLASS_REF:
					int classNameIndex = din.readShort();
					this.constantPool.insertClassEntry(new ClassEntry( classNameIndex ));
				break;
				case UVMConstants.CPOOL_DOUBLE_REF:
				{
					double doubleValue = din.readDouble();
					this.constantPool.insertDoubleEntry(new DoubleConstantEntry( doubleValue));
				}
				break;
				case UVMConstants.CPOOL_FLOAT_REF:
				{
					float floatValue = din.readFloat();
					this.constantPool.insertFloatEntry(new FloatConstantEntry( floatValue ));
				}	
				break;
				case UVMConstants.CPOOL_INTEGER_REF:
				{
					int intValue = din.readInt();
					this.constantPool.insertIntegerEntry(new IntegerConstantEntry( intValue ));
				}
				break;
				case UVMConstants.CPOOL_LONG_REF:
				{
					long longValue = din.readLong();
					this.constantPool.insertLongEntry(new LongConstantEntry( longValue ));
				}
				break;
				case UVMConstants.CPOOL_STRING_REF:
				{
					int strLen = din.readShort();
					byte[] data = new byte[strLen];
					din.read(data);
					String str = new String(data);
					StringEntry entry = new StringEntry(str);
					this.constantPool.insertStringEntry(entry);
				}
				break;
				case UVMConstants.FIELD_REF:
					int fieldClassIndex = din.readShort();
					int fieldNameIndex = din.readShort();
					int fieldTypeIndex = din.readShort();
					this.constantPool.insertFieldReferenceEntry(new FieldReferenceEntry( fieldClassIndex,fieldNameIndex,fieldTypeIndex));
				break;
				case UVMConstants.METHOD_REF:
					int methodClassName = din.readShort();
					int methodNameIndex = din.readShort();
					int methodSignatureIndex = din.readShort();
					this.constantPool.insertMethodReferenceEntry(new MethodReferenceEntry( methodClassName,methodNameIndex,methodSignatureIndex));
				break;
				case UVMConstants.INTERFACE_METHOD_REF:
					int iMethodClassNameIndex = din.readShort();
					int iMethodNameIndex = din.readShort();
					int iMethodSignatureIndex = din.readShort();
					this.constantPool.insertInterfaceMethodReferenceEntry(new InterfaceMethodReferenceEntry(iMethodClassNameIndex,iMethodNameIndex,iMethodSignatureIndex));
				break;
				default:
					System.out.println ("Invlaid constant pool entry ");
			}
		}
	}
	void readClassAndSuperClass(DataInputStream din)throws IOException
	{
		this.classAttribute = din.readShort();
		int classNameIndex = din.readShort();
		int superClassNameIndex = din.readShort();
		
		//Debug.inform("Reading class Name index "+ classNameIndex);
		StringEntry entry = (StringEntry)this.constantPool.constantPool.get(classNameIndex);
		this.className = entry.value;
		
		entry = (StringEntry)this.constantPool.constantPool.get(superClassNameIndex);
		this.superClass = entry.value;
	}
	void readInterfaces(DataInputStream din)throws IOException
	{
		int interfacesCount = din.readShort();
		for (int i = 0; i<interfacesCount ; i++)
		{
			int interfaceIndex = din.readShort();
			StringEntry entry = (StringEntry)this.constantPool.constantPool.get(interfaceIndex);
			this.superInterfaces.add(entry.value);
		}
	}
	void readFileds(DataInputStream din)throws IOException
	{
		int fieldCount = din.readShort();
		for (int i = 0; i< fieldCount; i++)
		{
			int fieldModifier = din.readShort();
			int fieldNameIndex = din.readShort();
			int fieldTypeIndex = din.readShort();
			
			StringEntry entry = (StringEntry)this.constantPool.constantPool.get(fieldNameIndex);
			String fieldName = entry.value;
			entry = (StringEntry)this.constantPool.constantPool.get(fieldTypeIndex);
			String fieldType = entry.value;
			UCSYField field = new UCSYField();
			field.modifier = fieldModifier;
			field.fieldName = fieldName;
			field.fieldType = fieldType;
			this.fields.add(field); 
		}
	}
	void readMethods(DataInputStream din)throws IOException
	{
		int methodCount = din.readShort();
		for (int i = 0; i< methodCount; i++)
		{
			UCSYMethod method = new UCSYMethod();
			
			int methodModifier = din.readShort();
			int methodNameIndex = din.readShort();
			int methodSignatureIndex = din.readShort();
			
			method.sizeOfArgument = din.readShort();//size of argument
			method.sizeOfLocalVar = din.readShort();//size of local var
			
			int maxOperand = din.readShort();//size of max operand stack
			
			int noOfCatchEntries = din.readShort();
			int codeLength = din.readShort();//Code Length
			byte code[] = new byte[codeLength];
			
			///Debug.inform("Size of arg "+ method.sizeOfArgument +" "+method.sizeOfLocalVar+" "+ codeLength);
			din.read(code);
			method.methodCode.code = code;
			method.methodCode.maxStack = maxOperand;
			String methodName = ((StringEntry)this.constantPool.constantPool.get(methodNameIndex)).value;
			String methodProtocol = ((StringEntry)this.constantPool.constantPool.get(methodSignatureIndex)).value;
			method.methodProtocol = methodProtocol;
			method.modifier = methodModifier;
			method.methodName = methodName;
			//
			method.methodSignature = methodProtocol.substring(0, methodProtocol.lastIndexOf(")")+1);
			String returnType = methodProtocol.substring( methodProtocol.lastIndexOf(")")+1,methodProtocol.length());
			method.methodReturnType = returnType;
			///System.out.println (method.methodReturnType);
			
			
			for (int j = 0; j<noOfCatchEntries; j++)	
			{
				ExceptionTable tab;
				int from;
				int to;
				int target;
				int classIndex ;
				from = din.readShort();
				to   = din.readShort();
				target = din.readShort();
				classIndex = din.readShort(); 
				
				tab = new ExceptionTable(from,to,target,classIndex);
				method.exceptionTable.add(tab);
			}
		
			this.methods.add(method);
		}
	}
	Type readClass(String className)throws IOException
	{
		din = new DataInputStream(new FileInputStream(className+".ucode"));
		this.readMagicAndVersion(din);
		this.readConstantPool(din);
		this.readClassAndSuperClass(din);
		this.readInterfaces(din);
		this.readFileds(din);
		this.readMethods(din);
		
		Type t = null;
		if( (this.classAttribute & UCSYClassAttribute.CLASS) == UCSYClassAttribute.CLASS)
		{
			UCSYClass theClass = new UCSYClass();
			theClass.className = this.className;
			theClass.parentClassName = this.superClass;
			theClass.constantPool = this.constantPool;
			theClass.fields = this.fields;
			theClass.methods = this.methods;
			theClass.interfaceList = this.superInterfaces;
			theClass.typeName = this.className;
			t = theClass;
			
		}
		else 
		{
			UCSYInterface theInterface = new UCSYInterface();
			theInterface.addParent("Object");
			theInterface.interfaceName = this.className;
			theInterface.constantPool = this.constantPool;
			for (int i = 0; i< this.superInterfaces.size(); i++)
			{
				theInterface.addParent(this.superInterfaces.get(i));
			}
			for (int i = 0; i< this.methods.size(); i++)
			{
				InterfaceMethod iMethod = new InterfaceMethod();
				UCSYMethod met = this.methods.get(i);
				
				iMethod.modifier = met.modifier;
				iMethod.methodName = met.methodName;
				iMethod.methodSignature = met.methodSignature;
				iMethod.methodReturnType = met.methodReturnType;
				iMethod.methodProtocol  = met.methodProtocol;
				iMethod.methodCode = met.methodCode;
				iMethod.sizeOfArgument = met.sizeOfArgument;
				iMethod.sizeOfLocalVar = met.sizeOfLocalVar;
				//iMethod.method
				theInterface.methodList.add(iMethod);
			}
			theInterface.typeName = this.className;
			t = theInterface;
		}
		
		
		return t;
	}
}