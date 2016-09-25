class ExceptionTable
{
	int from;
	int to;
	int target;
	int exceptionClassIndex;
	
	ExceptionTable()
	{
	}
	ExceptionTable(int from,int to,int target,int classIndex)
	{
		this.from = from;
		this.to   = to;
		this.target = target;
		this.exceptionClassIndex = classIndex;
	}
}