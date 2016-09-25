import java.util.*;
import java.util.regex.*;
class EscapeProcess
{
	static String processString(String data)
	{
		String result ="";
		int index  = 0;
		char ch;
		while(index < data.length()-1)
		{
			ch =  data.charAt(index);
			switch(ch)
			{
			
				case '\\':
					if(data.charAt(index+1) =='\\')
					{
						result +='\\';
						index++;
						
					}
					else if(data.charAt(index+1)=='n')
					{
						result+= '\n';
						index++;
						
					}
					else if(data.charAt(index+1)=='r')
					{
						result +='\r';
						index++;
						
					}
					else if(data.charAt(index+1)=='t')
					{
						result+='\t';
						index++;
						
					}
				break;
				default:
					result +=ch;
					
			}
			index++;
		}
		result += data.charAt(data.length()-1);
		return result;
	}
	
}