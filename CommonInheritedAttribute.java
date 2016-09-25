//To be used in type Checking as global variable
/*******************************************************************************
 * To be used by TypeResolver in determinig whether a static method access instance
 * variable
 *******************************************************************************/
 
import java.util.*;

class CommonInheritedAttribute
{
	static UCSYMethod currentMethod;
	static Stack<Integer> noOfBreak = new Stack<Integer>();
	static Stack<Integer> breakIndex = new Stack<Integer>();
	/*****************************************
	 * To Check break is in a loop
	 *****************************************/
	static int withinLoop;
	static Type currentType;
	static UCSY parser;
	
	static void inform(String data)
	{
		try
		{
			parser.inform(data);	
		}
		catch(Exception e)
		{
		}
	}
}