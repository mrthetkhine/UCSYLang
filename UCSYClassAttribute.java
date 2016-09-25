class UCSYClassAttribute
{
	static int FINAL = 1;
	static int ABSTRACT  = 1 << 1 ;
	static int SINGLETON = 1 << 2 ;
	static int STATIC    = 1 << 3 ;
	
	static int PRIVATE   = 1 << 4;
	static int PROTECTED = 1 << 5;
	static int INTERNAL  = 1 << 6;
	static int PUBLIC    = 1 << 7;
	
	static int OVERRIDE  = 1 << 8;
	static int MULTI     = 1 << 9;
	static int REBINDABLE= 1 << 10;
	static int NATIVE    = 1 << 11;
	
	static int CLASS     = 1 << 4;
	static int INTERFACE = 1 << 5;
	static int FREE      = 1 << 12;
	static boolean isStatic(int m)
	{
		return ( m & STATIC )== STATIC;
	}
	static boolean isPrivate(int m)
	{
		return ( m & PRIVATE ) == PRIVATE;
	}
	static boolean isProtected(int m)
	{
		return (m & PROTECTED ) == PROTECTED;
	}
	static boolean isInternal( int m )
	{
		return ( m & INTERNAL ) == INTERNAL;
	}
	static boolean isPublic(int m)
	{
		return ( m & PUBLIC ) == PUBLIC;
	}
	static boolean isFinal( int m)
	{
		return ( m & FINAL ) == FINAL;
	}
	static boolean isAbstract( int m )
	{
		return ( m & ABSTRACT ) == ABSTRACT;
	}
	static boolean isSingleton( int m)
	{
		return ( m & SINGLETON ) == SINGLETON;
	}
	static boolean isOverride( int m)
	{
		return ( m & OVERRIDE ) == OVERRIDE;
	}
	static boolean isMulti( int m )
	{
		return ( m & MULTI ) == MULTI;
	}
	static boolean isRebindable( int m )
	{
		return ( m & REBINDABLE ) == REBINDABLE;
	}
	static boolean isNative( int m )
	{
		return ( m & NATIVE )== NATIVE;
	}
	static boolean isFree(int m)
	{ 
		return ( m & FREE ) == FREE;
	}
	static boolean weakerAccess(int modifierOne, int modifierTwo)
	{
		
		return modifierOne < modifierTwo;
	}
	static String getTextualRep(int m)
	{
		String mText ="";
		
		if( isStatic(m))
		{
			mText += " static";
		}
		if( isFinal(m))
		{
			mText += " final";
		}
		if( isSingleton(m))
		{
			mText += " singleton";
		}
		if( isAbstract(m))
		{
			mText += " abstract";
		}
		if( isPrivate( m ))
		{
			mText += " private";
		}
		if( isProtected( m ))
		{
			mText += " protected";
		}
		if( isInternal( m ))
		{
			mText += " internal";
		}
		if( isPublic( m ))
		{
			mText += " public";
		}
		if( isOverride( m ))
		{
			mText += " override";
		}
		if( isMulti( m ))
		{
			mText += " multi";
		}
		if( isRebindable( m ))
		{
			mText += " rebindable";
		}
		if( isNative( m ))
		{
			mText += " native";
		}
		if( isFree( m) )
		{
			mText += " free ";
		}
		return mText;
	}
}