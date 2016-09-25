class Debug
{
	static UCSY parser;
	static void inform(String data)
	{
		try
		{
			parser.inform(data);
		}
		catch(Exception e)
		{
			System.out.println (data);
			//e.printStackTrace();
		}
	}
}