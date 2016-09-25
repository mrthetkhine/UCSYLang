class GeneralTest
{
	public static void main(String[]args)
	{
		
		try
		{
			ConstantPool cpoll = new ConstantPool();
			cpoll.addStringRef("<init>");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}