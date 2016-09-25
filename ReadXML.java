import org.xml.sax.helpers.*;
import org.xml.sax.*;
import org.xml.sax.SAXException;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;
//org.w3c.dom.Node
class ReadXML
{
	ArrayList keywordList = new ArrayList();
	ArrayList operatorList = new ArrayList();
	ArrayList puncutatorList = new ArrayList();
	String uri;
	
	ReadXML(String url)
	{
		uri = url;
		operatorList.add("&");
		operatorList.add(">");
		operatorList.add("<");
		
		init();
	}
	void init()
	{
		DocumentBuilder parser;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();                                                  
		//parser = factory.newDocumentBuilder();		
		try
		{
			parser = factory.newDocumentBuilder();
			Document doc = parser.parse(uri);
			Element root = doc.getDocumentElement();
			
			
			NodeList childern = root.getChildNodes();
			org.w3c.dom.Node current = null;
			for(int i=0;i< childern.getLength();i++)
			{
				current = childern.item(i);
				if(current.getNodeName().equals("keyword"))
				{
					NodeList keywordNode = current.getChildNodes();
					for(int j=0;j< keywordNode.getLength();j++)
					{
						org.w3c.dom.Node list = keywordNode.item(j);
						if(list instanceof Element)
							keywordList.add(list.getTextContent());
					}
				}
				if(current.getNodeName().equals("operator"))
				{
					NodeList operatorNode = current.getChildNodes();
					for(int j=0;j< operatorNode.getLength();j++)
					{
						org.w3c.dom.Node list = operatorNode.item(j);
						if(list instanceof Element)
							operatorList.add(list.getTextContent());
					}
				}
				if(current.getNodeName().equals("puncutator"))
				{
					NodeList puncutatorNode = current.getChildNodes();
					for(int j=0;j< puncutatorNode.getLength();j++)
					{
						org.w3c.dom.Node list = puncutatorNode.item(j);
						if(list instanceof Element)
							puncutatorList.add(list.getTextContent());
					}
				}
			}
			
				
		}
		catch(Exception e)
		{
			System.out.println (e);
		}
	}
	
	public ArrayList getOperatorList()
	{
		return operatorList;
	}
	public ArrayList getKeywordList()
	{
		return keywordList;
	}
	public ArrayList getPuncutatorList()
	{
		return puncutatorList;
	}
	void myPrint(org.w3c.dom.Node child)
	{
		if(child.getChildNodes().getLength() == 1)
		{
			if(child instanceof Element)
			{
				
				keywordList.add(child.getTextContent());
			}
		}
		else
		{
			NodeList childern = child.getChildNodes();
			for(int i =0;i<childern.getLength();i++)
				myPrint(childern.item(i));
		}
	}
	public static void main(String args[])
	{
		ReadXML obj = new ReadXML("Config.xml");
		ArrayList a = obj.getKeywordList();
		System.out.println ("===========Keyword  ========");	
		for(Object c:a)
			System.out.println ((String)(c));
		
		/*
		a = obj.getOperatorList();
		System.out.println ("===========Operator  ========");
		for(Object c:a)
			System.out.println ((String)(c));
		*/
		a = obj.getPuncutatorList();
		
		System.out.println ("=========== Puncutator  ========");
		for(Object c:a)
			System.out.println ((String)(c));
	}
}