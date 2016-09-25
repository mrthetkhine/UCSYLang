import java.io.*;
import java.util.*;

class Disassembler
{
	
	static void produceHTMLFile(UCSYClass theClass)
	{
		try
		{
			DataOutputStream dout = new DataOutputStream(new FileOutputStream( theClass.className+".html"));
			dout.writeBytes("<html><body>");
			dout.writeBytes("<h4 align=center style=\"color:\'magenta\';align:center\">Coffee Right by Thet Khine</h4><h3 style=\"color:\'green\';align:center\">Disassembly of "+theClass.className+".ucode</h3>Magic code BABEBABE<br>");
			dout.writeBytes("No of ConstantPool Entry Count "+ theClass.constantPool.constantPool.size());
			dout.writeBytes("<table border=1>");
			for (int i = 0; i< theClass.constantPool.constantPool.size(); i++)
			{
				dout.writeBytes("<tr><td><a name="+i+">"+ i+"</a></td>");
				theClass.constantPool.constantPool.get(i).writeHTML(dout);
				dout.writeBytes("</tr>");
			}
			dout.writeBytes("</table>");
			
			dout.writeBytes("<br> class name <b>"+theClass.className+"</b>");
			dout.writeBytes("<br> super class <b>"+ theClass.parentClassName+"</b>" );
			
			dout.writeBytes("<br>Interfaces implemented by the class");
			dout.writeBytes("<table border=1>");
			for (int i = 0; i< theClass.interfaceList.size(); i++)
			{
				dout.writeBytes( theClass.interfaceList.get(i));
			}
			dout.writeBytes("</table>");
			dout.writeBytes("<br>");
			dout.writeBytes("<br>No of field "+ theClass.fields.size());
			dout.writeBytes("<table border=1>");
			dout.writeBytes("<tr><th>Field Modifier</th><th>Filed Name</th><th>Field Type </th></tr>");
			for (int i = 0; i< theClass.fields.size(); i++)
			{
				UCSYField field = theClass.fields.get(i);
				dout.writeBytes("<tr><td> "+ UCSYClassAttribute.getTextualRep(field.modifier)+"</td><td>"+ field.fieldName+"</td><td>"+ TypeCheckUtilityClass.getTypeDescriptionFromMemonic(field.fieldType)+"</td></tr>");
			}	
			dout.writeBytes("</table>");
			dout.writeBytes("<br>No of Methods "+ theClass.methods.size()+"<br>");
			for (int i = 0; i< theClass.methods.size(); i++)
			{
				
				Method m = theClass.methods.get(i);
				dout.writeBytes("<table border=1>");
				dout.writeBytes("<tr><th>Modifier</th><th>Name</th><th>Protocol</th><th>Argsize</th><th>OperandStack</th><th>LocalVar</th></tr>");
				dout.writeBytes( "<tr><td>"+ UCSYClassAttribute.getTextualRep( m.modifier)+"</td><td>"+m.methodName+"</td><td>"+m.methodProtocol+"</td><td>"+m.sizeOfArgument+"</td><td>"+m.methodCode.maxStack+"</td><td>"+m.sizeOfLocalVar+"</td></tr>");
				dout.writeBytes("</table>");
				PrintOpCode.printCode(m.methodCode.code,dout);
				dout.writeBytes("<br>");
			}
			dout.writeBytes("<br>");
			dout.flush();
			dout.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	static void produceHTMLFile(UCSYInterface theClass)
	{
		try
		{
			DataOutputStream dout = new DataOutputStream(new FileOutputStream( theClass.interfaceName+".html"));
			dout.writeBytes("<html><body>");
			dout.writeBytes("<h4 align=center style=\"color:\'magenta\';align:center\">Coffee Right by Thet Khine</h4><h3 style=\"color:\'green\';align:center\">Disassembly of "+theClass.interfaceName+".ucode</h3>Magic code BABEBABE<br>");
			dout.writeBytes("No of ConstantPool Entry Count "+ theClass.constantPool.constantPool.size());
			dout.writeBytes("<table border=1>");
			for (int i = 0; i< theClass.constantPool.constantPool.size(); i++)
			{
				dout.writeBytes("<tr><td><a name="+i+">"+ i+"</a></td>");
				theClass.constantPool.constantPool.get(i).writeHTML(dout);
				dout.writeBytes("</tr>");
			}
			dout.writeBytes("</table>");
			
			dout.writeBytes("<br> class name <b>"+theClass.interfaceName+"</b>");
			dout.writeBytes("<br> super class <b>"+ "Object"+"</b>" );
			
			dout.writeBytes("<br>Parent of interface ");
			dout.writeBytes("<table border=1>");
			for (int i = 0; i< theClass.parentList.size(); i++)
			{
				dout.writeBytes( "<td>"+theClass.parentList.get(i)+"</td>");
			}
			dout.writeBytes("</table>");
			dout.writeBytes("<br>");
			dout.writeBytes("<br>No of Methods "+ theClass.methodList.size()+"<br>");
			for (int i = 0; i< theClass.methodList.size(); i++)
			{
				
				Method m = theClass.methodList.get(i);
				dout.writeBytes("<table border=1>");
				dout.writeBytes("<tr><th>Modifier</th><th>Name</th><th>Protocol</th><th>Argsize</th><th>OperandStack</th><th>LocalVar</th></tr>");
				dout.writeBytes( "<tr><td>"+ UCSYClassAttribute.getTextualRep( m.modifier)+"</td><td>"+m.methodName+"</td><td>"+m.methodProtocol+"</td><td>"+m.sizeOfArgument+"</td><td>"+m.methodCode.maxStack+"</td><td>"+m.sizeOfLocalVar+"</td></tr>");
				dout.writeBytes("</table>");
				PrintOpCode.printCode(m.methodCode.code,dout);
				dout.writeBytes("<br>");
			}
			dout.writeBytes("<br>");
			dout.flush();
			dout.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void main(String[]args)
	{
		try
		{
			String fileName = args[0];
		
			ClassReader reader = new ClassReader();
			Type theClass = reader.readClass( fileName );
			if(theClass instanceof UCSYClass)
			{
				produceHTMLFile((UCSYClass)theClass);
			}
			else
			{
				produceHTMLFile((UCSYInterface)theClass);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		//System.out.println ("Ok generted");
		
	}	
}