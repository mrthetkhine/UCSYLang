import java.io.*;

abstract class ConstantPoolEntry
{
	abstract int getTag();
	abstract void write(DataOutputStream ucode)throws IOException;		
	
	abstract void writeHTML(DataOutputStream html)throws IOException;
			
	
}

class IntegerConstantEntry extends ConstantPoolEntry
{
	int intValue;
	
	IntegerConstantEntry(int value)
	{
		this.intValue = value;
	}
	int getTag()
	{
		return UVMConstants.CPOOL_INTEGER_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeInt(intValue);
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>Integer</td><td>"+ this.intValue+"</td>");
	}
}

class LongConstantEntry extends ConstantPoolEntry
{
	long longValue;
	
	LongConstantEntry(long value)
	{
		this.longValue = value;
	}
	int getTag()
	{
		return UVMConstants.CPOOL_LONG_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeLong(longValue);
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>Long</td><td>"+ this.longValue+"</td>");
	}
}

class FloatConstantEntry extends ConstantPoolEntry
{
	float floatValue ;
	
	FloatConstantEntry ( float value)
	{
		this.floatValue = value;
	}
	int getTag()
	{
		return UVMConstants.CPOOL_FLOAT_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		//Debug.inform("Writing constantpooll entry float");
		ucode.writeByte(getTag());
		ucode.writeFloat(floatValue);
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>Float</td><td>"+ this.floatValue+"</td>");
	}
}

class DoubleConstantEntry extends ConstantPoolEntry
{
	double doubleValue ;
	
	DoubleConstantEntry( double value)
	{
		this.doubleValue = value;
	}
	int getTag()
	{
		return UVMConstants.CPOOL_DOUBLE_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeDouble( doubleValue );
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>Double</td><td>"+ this.doubleValue+"</td>");
	}
}

class FieldReferenceEntry extends ConstantPoolEntry
{
	int classNameIndex;
	int fieldNameIndex;
	int fieldTypeIndex;
	
	
	FieldReferenceEntry(int classNameIndex, int nameIndex, int typeIndex)
	{
		this.classNameIndex = classNameIndex;
		this.fieldNameIndex = nameIndex;
		this.fieldTypeIndex = typeIndex;
		
	}
	int getTag()
	{
		return UVMConstants.FIELD_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeShort( classNameIndex );
		ucode.writeShort( fieldNameIndex );
		ucode.writeShort( fieldTypeIndex );
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>FieldRef</td><td>classNameIndex= <a href=#"+ this.classNameIndex+">" +this.classNameIndex+"</a>"+" fieldNameIndex=<a href=#"+ this.fieldNameIndex+">" +this.fieldNameIndex+"</a>" +" fieldTypeIndex <a href=#"+ this.fieldTypeIndex+">"+this.fieldTypeIndex+"</a></td>");
	}
}
class MethodReferenceEntry extends ConstantPoolEntry
{
	int classNameIndex;
	int methodNameIndex;
	int methodSignatureIndex;
	
	MethodReferenceEntry(int cIndex, int mNameIndex, int mSignatureIndex)
	{
		this.classNameIndex = cIndex;
		this.methodNameIndex = mNameIndex;
		this.methodSignatureIndex = mSignatureIndex;
		
	}
	int getTag()
	{
		return UVMConstants.METHOD_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeShort( classNameIndex );
		ucode.writeShort( methodNameIndex );
		ucode.writeShort( methodSignatureIndex );
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>MethodRef</td><td>classNameIndex= <a href=#"+ this.classNameIndex+">" +this.classNameIndex+"</a>"+" methodNameIndex=<a href=#"+ this.methodNameIndex+">" +this.methodNameIndex+"</a>" +" methodSignatureIndex <a href=#"+ this.methodSignatureIndex+">"+this.methodSignatureIndex+"</a></td>");
	}
}

class InterfaceMethodReferenceEntry extends ConstantPoolEntry
{
	int classNameIndex;
	int methodNameIndex;
	int methodSignatureIndex;
	
	InterfaceMethodReferenceEntry( int cNameIndex, int mNameIndex, int mSignatureIndex)
	{
		this.classNameIndex = cNameIndex;
		this.methodNameIndex = mNameIndex;
		this.methodSignatureIndex = mSignatureIndex;
	}
	int getTag()
	{
		return UVMConstants.INTERFACE_METHOD_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeShort( classNameIndex );
		ucode.writeShort( methodNameIndex );
		ucode.writeShort( methodSignatureIndex );
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>InterfaceMethodRef</td><td>classNameIndex= <a href=#"+ this.classNameIndex+">" +this.classNameIndex+"</a>"+" methodNameIndex=<a href=#"+ this.methodNameIndex+">" +this.methodNameIndex+"</a>" +" methdoSignatureIndex <a href=#"+ this.methodSignatureIndex+">"+this.methodSignatureIndex+"</a></td>");
	}
}

class StringEntry extends ConstantPoolEntry
{
	String value;
	
	StringEntry( String va)
	{
		this.value = va;
	}
	int getTag()
	{
		return UVMConstants.CPOOL_STRING_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeShort( value.length() );
		ucode.writeBytes(value);
		
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		if(this.value.equals("<init>"))
		{
			html.writeBytes("<td>String</td><td> &lt;init&gt;</td>");
		}
		else if(this.value.equals("<cinit>"))
		{
			html.writeBytes("<td>String</td><td> &lt;cinit&gt;</td>");
		}
		else
		{
			html.writeBytes("<td>String</td><td> "+this.value+"</td>");
		}
	}
}
class ClassEntry extends ConstantPoolEntry
{
	int classNameIndex;
	
	ClassEntry(int cIndex)
	{
		this.classNameIndex = cIndex;
	}
	int getTag()
	{
		return UVMConstants.CLASS_REF;
	}
	void write(DataOutputStream ucode)throws IOException
	{
		ucode.writeByte(getTag());
		ucode.writeShort( classNameIndex );
	}
	void writeHTML(DataOutputStream html)throws IOException
	{
		html.writeBytes("<td>Class</td><td> classNameIndex= <a href=#"+this.classNameIndex+">"+this.classNameIndex+"</a></td>");
	}
}