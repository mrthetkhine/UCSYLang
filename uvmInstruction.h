//Same as Instruction.java

#define uvmInstruction

#define LOAD_CPOOL          1	
#define LOAD_LOCAL_INT      2
#define LOAD_LOCAL_LONG     3
#define LOAD_LOCAL_FLOAT    4
#define LOAD_LOCAL_DOUBLE   5
#define LOAD_LOCAL_REF      6 
	
#define STORE_LOCAL_INT     7
#define STORE_LOCAL_LONG    8
#define STORE_LOCAL_FLOAT   9
#define STORE_LOCAL_DOUBLE  10
#define STORE_LOCAL_REF     11
	
#define GET_STATIC_FIELD    12
#define PUT_STATIC_FIELD    13
#define GET_INSTANCE_FIELD  14
#define PUT_INSTANCE_FIELD  15
	
#define CALL_STATIC         16
#define CALL_VIRTUAL        17
#define CALL_INTERFACE      18
#define CALL_CONSTRUCTOR    19
	
//Return
#define INT_RETURN          20
#define LONG_RETURN         21
#define FLOAT_RETURN        22
#define DOUBLE_RETURN       23
#define REF_RETURN          24
#define RETURN              25

#define CREATE_OBJECT       26

#define ADD_INT             27
#define ADD_LONG            28 
#define ADD_FLOAT           29
#define ADD_DOUBLE	        30
	
#define SUB_INT             31
#define SUB_LONG            32
#define SUB_FLOAT           33
#define SUB_DOUBLE          34
	
#define MULT_INT            35
#define MULT_LONG           36
#define MULT_FLOAT          37
#define MULT_DOUBLE         39
	
#define DIV_INT             40
#define DIV_LONG            41
#define DIV_FLOAT           42
#define DIV_DOUBLE          43
	
#define MOD_INT             44
#define MOD_LONG            45
#define MOD_FLOAT           46
#define MOD_DOUBLE          47
//Relational
#define INT_EQUAL           48	
#define FLOAT_EQUAL         49
#define LONG_EQUAL          50
#define DOUBLE_EQUAL        51
	
#define INT_NOT_EQUAL       52
#define FLOAT_NOT_EQUAL     53
#define LONG_NOT_EQUAL      54
#define DOUBLE_NOT_EQUAL    55
	
#define INT_GT_EQUAL        56
#define FLOAT_GT_EQUAL      57
#define LONG_GT_EQUAL       58
#define DOUBLE_GT_EQUAL     59
	
#define INT_LT_EQUAL        60
#define FLOAT_LT_EQUAL      61
#define LONG_LT_EQUAL       62
#define DOUBLE_LT_EQUAL     63
	
#define INT_GT              64
#define FLOAT_GT            65
#define LONG_GT             66
#define DOUBLE_GT           67
	
#define INT_LT              68
#define FLOAT_LT            69
#define LONG_LT             70
#define DOUBLE_LT           71
#define AND                 72
#define OR                  73
#define NOT                 74

	
#define INTEGER_TO_BYTE     75
#define INTEGER_TO_SHORT    76
#define INTEGER_TO_LONG     77
#define INTEGER_TO_FLOAT    78
#define INTEGER_TO_DOUBLE   79
	
#define LONG_TO_INTEGER     80
#define LONG_TO_FLOAT       81
#define LONG_TO_DOUBLE      82
	
#define FLOAT_TO_INTEGER    83
#define FLOAT_TO_LONG       84
#define FLOAT_TO_DOUBLE     85
	
#define DOUBLE_TO_INTEGER   86
#define DOUBLE_TO_LONG      87
#define DOUBLE_TO_FLOAT     88

#define JUMP                89
#define JUMP_TRUE           90
#define JUMP_FALSE          91


//Array Operation

#define CREATE_ARRAY              92
#define GET_ARRAY_LENGTH          93
	
#define GET_ARRAY_ELEMENT_REF     94
#define GET_ARRAY_ELEMENT_BYTE    95 //byte ,boolean , char
#define GET_ARRAY_ELEMENT_SHORT   96
#define GET_ARRAY_ELEMENT_INT     97
#define GET_ARRAY_ELEMENT_LONG    98
#define GET_ARRAY_ELEMENT_FLOAT   99
#define GET_ARRAY_ELEMENT_DOUBLE  100
	
#define STORE_ARRAY_ELEMENT_REF   101
#define STORE_ARRAY_ELEMENT_BYTE  102 //byte ,boolean , char
#define STORE_ARRAY_ELEMENT_SHORT 103
#define STORE_ARRAY_ELEMENT_INT   104
#define STORE_ARRAY_ELEMENT_LONG  105
#define STORE_ARRAY_ELEMENT_FLOAT 106
#define STORE_ARRAY_ELEMENT_DOUBLE 107

#define THROW_EXCEPTION            108
#define INSTANCE_OF                109

#define REBIND                     110
#define CALL_REBINDABLE            111
