class ArrayType extends UCSYClass
{
	int dimension;
	String memberType;
	String arrayName;
	ArrayType(String memberType, int dimension)
	{
		this.dimension = dimension;
		this.memberType = memberType;
		this.parentClassName ="Object";
		this.itsType = TypeOfType.ARRAY;
		
		String name ="";
		for (int i = 0; i< dimension; i++)
		{
			name +="[";
			
		}
		this.arrayName = name+ memberType;
	}
}