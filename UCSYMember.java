class UCSYMember
{
	String ownerName;
	int modifier;
	String memberName;
	String memberType;
	
	MemberType typeOfMember;
	Object member;
	
	
	UCSYMember(String oName, int modifier, String memberName, String memberType, MemberType typeOfMember, Object member)
	{
		this.ownerName = oName;
		this.modifier = modifier;
		this.memberName = memberName;
		this.memberType = memberType;
		//Debug.inform("I got owner "+ oName+" memberName "+ memberName+this.memberType);
		this.typeOfMember = typeOfMember;
		this.member     = member;
	}
}
