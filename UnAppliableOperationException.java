class UnAppliableOperationException extends Exception
{
	String operator;
	String opOneType;
	String opTwoType;
	
	
	UnAppliableOperationException(String operator, String oneType, String twoType )
	{
		
		this.operator = operator;
		this.opOneType = oneType;
		this.opTwoType = twoType;
	}
}