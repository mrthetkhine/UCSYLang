import java.io.*;

class ReadLine
{
	static long fileCount  = 0;
	static long lineCount = 0;
	long countLine(String path,String extension)
	{
		
		try
		{
			File f = new File(path);
			
			File files[] = f.listFiles();
			
			if(files == null)
				return 0;
			for (int i = 0; i< files.length ; i++)
			{
				File aFile = files[i];
				
				if(aFile.isDirectory())
				{
					 countLine(aFile.getAbsolutePath(),extension);
				}
				if(aFile.getAbsolutePath().endsWith(extension))
				{
					//System.out.println (aFile.getAbsolutePath());
					DataInputStream din = new DataInputStream(new FileInputStream(aFile));
					
					String s="";
					
					while((s = din.readLine())!=null)
					{
						lineCount ++;
					}
					fileCount ++;
				
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println ("");
		}
		return lineCount;
	}
	public static void main(String[]args)
	{
		ReadLine readLine = new ReadLine();
		try
		{
			long lineCount = readLine.countLine(args[0],args[1]);	
			System.out.println ("No of File "+fileCount);
			System.out.println ("No of line "+ lineCount);
		}
		catch(Exception e)
		{
		}
		
	}
}