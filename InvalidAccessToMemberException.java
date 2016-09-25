class InvalidAccessToMemberException extends Exception
{
	String parentName;
	String childName;
	
	InvalidAccessToMemberException (String pName, String cName)
	{
		this.parentName = pName;
		this.childName  = cName;
	}
	
}