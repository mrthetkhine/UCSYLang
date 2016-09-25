import java.io.*;
class ProcessTest
{
	public static void main(String[]args)
	{
		try
		{
			String stdoutString="";
		String stderrString="";
		Process lastProcess;
		String commandArray [] ={"C:/Program Files/Java/Jdk1.6.0/bin/java","UCSY", "D:/Project/MyThesis/OOP5/one.ucsy"};
		String []envp = null;
		try 
		{ 
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(commandArray,envp,null); 
			lastProcess=proc;
			InputStream stderr = proc.getErrorStream(); 
			InputStream stdout = proc.getInputStream(); 
			InputStreamReader isre = new InputStreamReader(stderr); 
			BufferedReader bre = new BufferedReader(isre); 
			InputStreamReader isro = new InputStreamReader(stdout); 
			BufferedReader bro = new BufferedReader(isro); 
			String line = null;
			
			int exitVal;
			while ( (line = bre.readLine()) != null) 
			{
				stderrString+=line+"\n"; 
			}
			while ( (line = bro.readLine()) != null)
			{
				stdoutString+=line+"\n"; 
			}
				exitVal = proc.waitFor();
			
		System.out.println (stderrString);
		System.out.println (stdoutString);	
		}
		catch (Throwable t) 
		{
			t.printStackTrace();
		}

			//p.wait();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
}