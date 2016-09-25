interface Instruction
{
	static final int LOAD_CPOOL        = 1;	
	static final int LOAD_LOCAL_INT    = 2;
	static final int LOAD_LOCAL_LONG   = 3;
	static final int LOAD_LOCAL_FLOAT  = 4;
	static final int LOAD_LOCAL_DOUBLE = 5;
	static final int LOAD_LOCAL_REF    = 6; 
	
	static final int STORE_LOCAL_INT   = 7;
	static final int STORE_LOCAL_LONG  = 8;
	static final int STORE_LOCAL_FLOAT = 9;
	static final int STORE_LOCAL_DOUBLE = 10;
	static final int STORE_LOCAL_REF   = 11;
	
	static final int GET_STATIC_FIELD  = 12;
	static final int PUT_STATIC_FIELD  = 13;
	static final int GET_INSTANCE_FIELD = 14;
	static final int PUT_INSTANCE_FIELD = 15;
	
	static final int CALL_STATIC        = 16;
	static final int CALL_VIRTUAL       = 17;
	static final int CALL_INTERFACE     = 18;
	static final int CALL_CONSTRUCTOR   = 19;
	
	//Return
	static final int INT_RETURN         = 20;
	static final int LONG_RETURN        = 21;
	static final int FLOAT_RETURN       = 22;
	static final int DOUBLE_RETURN      = 23;
	static final int REF_RETURN         = 24;
	static final int RETURN             = 25;
	
	static final int CREATE_OBJECT      = 26;
	
	//Arithmetic
	static final int ADD_INT            = 27;
	static final int ADD_LONG           = 28; 
	static final int ADD_FLOAT          = 29;
	static final int ADD_DOUBLE	        = 30;
	
	static final int SUB_INT            = 31;
	static final int SUB_LONG           = 32;
	static final int SUB_FLOAT          = 33;
	static final int SUB_DOUBLE         = 34;
	
	static final int MULT_INT           = 35;
	static final int MULT_LONG          = 36;
	static final int MULT_FLOAT         = 37;
	static final int MULT_DOUBLE        = 39;
	
	static final int DIV_INT            = 40;
	static final int DIV_LONG           = 41;
	static final int DIV_FLOAT          = 42;
	static final int DIV_DOUBLE         = 43;
	
	static final int MOD_INT            = 44;
	static final int MOD_LONG           = 45;
	static final int MOD_FLOAT          = 46;
	static final int MOD_DOUBLE         = 47;
	
	//Relational Operator
	static final int INT_EQUAL          = 48;	
	static final int FLOAT_EQUAL        = 49;
	static final int LONG_EQUAL         = 50;
	static final int DOUBLE_EQUAL       = 51;
	
	static final int INT_NOT_EQUAL      = 52;
	static final int FLOAT_NOT_EQUAL    = 53;
	static final int LONG_NOT_EQUAL     = 54;
	static final int DOUBLE_NOT_EQUAL   = 55;
	
	static final int INT_GT_EQUAL       = 56;
	static final int FLOAT_GT_EQUAL     = 57;
	static final int LONG_GT_EQUAL      = 58;
	static final int DOUBLE_GT_EQUAL    = 59;
	
	static final int INT_LT_EQUAL       = 60;
	static final int FLOAT_LT_EQUAL     = 61;
	static final int LONG_LT_EQUAL      = 62;
	static final int DOUBLE_LT_EQUAL    = 63;
	
	static final int INT_GT             = 64;
	static final int FLOAT_GT           = 65;
	static final int LONG_GT            = 66;
	static final int DOUBLE_GT          = 67;
	
	static final int INT_LT             = 68;
	static final int FLOAT_LT           = 69;
	static final int LONG_LT            = 70;
	static final int DOUBLE_LT          = 71;
	
	static final int AND                = 72;
	static final int OR                 = 73;
	static final int NOT                = 74;
	
	static final int INTEGER_TO_BYTE    = 75;
	static final int INTEGER_TO_SHORT   = 76;
	static final int INTEGER_TO_LONG    = 77;
	static final int INTEGER_TO_FLOAT   = 78;
	static final int INTEGER_TO_DOUBLE  = 79;
	
	static final int LONG_TO_INTEGER    = 80;
	static final int LONG_TO_FLOAT      = 81;
	static final int LONG_TO_DOUBLE     = 82;
	
	static final int FLOAT_TO_INTEGER   = 83;
	static final int FLOAT_TO_LONG      = 84;
	static final int FLOAT_TO_DOUBLE    = 85;
	
	static final int DOUBLE_TO_INTEGER  = 86;
	static final int DOUBLE_TO_LONG     = 87;
	static final int DOUBLE_TO_FLOAT    = 88;
	
	static final int JUMP               = 89;
	static final int JUMP_TRUE          = 90;
	static final int JUMP_FALSE         = 91;
	
	//Array 
	static final int CREATE_ARRAY       = 92;
	static final int GET_ARRAY_LENGTH   = 93;
	
	static final int GET_ARRAY_ELEMENT_REF    = 94;
	static final int GET_ARRAY_ELEMENT_BYTE   = 95; //byte ,boolean , char
	static final int GET_ARRAY_ELEMENT_SHORT  = 96;
	static final int GET_ARRAY_ELEMENT_INT    = 97;
	static final int GET_ARRAY_ELEMENT_LONG   = 98;
	static final int GET_ARRAY_ELEMENT_FLOAT  = 99;
	static final int GET_ARRAY_ELEMENT_DOUBLE = 100;
	
	static final int STORE_ARRAY_ELEMENT_REF    = 101;
	static final int STORE_ARRAY_ELEMENT_BYTE   = 102; //byte ,boolean , char
	static final int STORE_ARRAY_ELEMENT_SHORT  = 103;
	static final int STORE_ARRAY_ELEMENT_INT    = 104;
	static final int STORE_ARRAY_ELEMENT_LONG   = 105;
	static final int STORE_ARRAY_ELEMENT_FLOAT  = 106;
	static final int STORE_ARRAY_ELEMENT_DOUBLE = 107;
	
	//Exception 
	static final int THROW_EXCEPTION            = 108;
	//InstanceOf
	static final int INSTANCE_OF                = 109;
	
	static final int REBIND                     = 110;
	static final int CALL_REBINDABLE            = 111;
}