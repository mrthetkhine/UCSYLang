class CyclicInheritanceException extends Exception
{
	String cyclicType;
	
	CyclicInheritanceException(String typeName)
	{
		cyclicType = typeName;
	}
}