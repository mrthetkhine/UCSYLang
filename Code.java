class Code
{
	//Current pointer of code
	int cp = 0;
	byte code[] = new byte[64];
	
	//How much stack must be allocated for this Code ,all measured in words size
	int maxStack = 0;
	int oldStackLoacation ; //TO USE BY change load to store
	int currentStackLocation =0;
	int oldMax ;
	int getMaxStack()
	{
		return maxStack;
	}
	int getCurrentPointer()
	{
		return cp ;
	}
	//Emit a byte of code
	void emit1(int inst)
	{
		if(cp == code.length ) //Expand the code array
		{
			byte [] newCode =new byte[cp * 2 ];
			System.arraycopy(code,0,newCode,0,cp);
			code = newCode;
			
		}
		code[cp++] = (byte)inst;
	}
	//Emit two byte of code
	void emit2(int inst)
	{
		if(cp + 2 >code.length)
		{
			emit1(inst>>8);
			emit1(inst);
		}
		else
		{
			code[cp++] = (byte)(inst>>8);
			code[cp++] = (byte)(inst);
		}
	}
	void fill2At(int index,int integerData)
	{
		code[index  ] = (byte)(integerData>>8);
		code[index+1] = (byte)(integerData);
	}
	void emit4(int inst)
	{
		if(cp + 4 > code.length)
		{
			emit1(inst>>24);
			emit1(inst>>16);
			emit1(inst>>8);
			emit1(inst);
		}
		else
		{
			code[cp ++] = (byte)(inst>>24);
			code[cp ++] = (byte)(inst>>16);
			code[cp ++] = (byte)(inst>>8);
			code[cp ++] = (byte)inst;
		}
	}
	
	void insertCodeAt(int opcode,int location)
	{
		emit1(-1);
		for (int i =cp-1 ; i>=location; i--)
			code[i+1] = code[i];
		code[location] = (byte)opcode;
	}
	//Size is measure in word size
	void incOperandStack(int size)
	{
		
		this.currentStackLocation += size;
		//this.oldStackValue = this.maxStack;
		if(this.currentStackLocation > this.maxStack)
		{
			
			this.maxStack = this.currentStackLocation;
		}
		
	}
	//Emit opcode
	void emitOpcode(int opcode)
	{
		emit1(opcode);
	}
	void emitLoadConstantPool(int cpoolIndex,int sizeOfCpoolEntry)
	{
		emit1( Instruction.LOAD_CPOOL);
		emit2( cpoolIndex);
		this.incOperandStack( sizeOfCpoolEntry);
	}
	
	/***************************************************************************
	 *		load from local variable array 
	 ***************************************************************************/
	void emitLoadLocalInt(int localVarIndex)
	{
		
		emit1( Instruction.LOAD_LOCAL_INT );
		emit2( localVarIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack(1);
	}
	void emitLoadLocalLong(int localVarIndex)
	{
		emit1( Instruction.LOAD_LOCAL_LONG );
		emit2( localVarIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack(2);
	}
	void emitLoadLocalFloat(int localVarIndex)
	{
		emit1( Instruction.LOAD_LOCAL_FLOAT );
		emit2( localVarIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack(1);
	}
	void emitLoadLocalDouble(int localVarIndex)
	{
		emit1( Instruction.LOAD_LOCAL_DOUBLE );
		emit2( localVarIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack(2);
	}
	void emitLoadLocalRef(int localVarIndex)
	{
		emit1( Instruction.LOAD_LOCAL_REF );
		emit2( localVarIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack(1);
	}
	void emitLoadLocalVar(int localVarIndex,String typeName)
	 {
	 	if( CentralTypeTable.isUVMIntegerType( typeName ))
	 	{
	 		this.emitLoadLocalInt(localVarIndex);	
	 	}
	 	else if( CentralTypeTable.isLong( typeName ))
	 	{
	 		this.emitLoadLocalLong( localVarIndex);
	 	}
	 	else if( CentralTypeTable.isFloat(typeName))
	 	{
	 		this.emitLoadLocalFloat( localVarIndex );
	 	} 
	 	else if( CentralTypeTable.isDouble(typeName))
	 	{
	 		this.emitLoadLocalDouble( localVarIndex );
	 	}
	 	else
	 	{
	 		this.emitLoadLocalRef( localVarIndex );
	 	}
	 	
	 }
	/***************************************************************************
	 *			Store to local variable array
	 ***************************************************************************/
	 void emitStoreLocalInt(int localVarIndex)
	 {
	 	emit1( Instruction.STORE_LOCAL_INT );
	 	emit2( localVarIndex );
	 	this.incOperandStack(-1);
	 }
	 void emitStoreLocalLong(int localVarIndex)
	 {
	 	emit1( Instruction.STORE_LOCAL_LONG );
	 	emit2( localVarIndex );
	 	this.incOperandStack(-2);
	 }
	 void emitStoreLocalFloat(int localVarIndex)
	 {
	 	emit1( Instruction.STORE_LOCAL_FLOAT );
	 	emit2( localVarIndex );
	 	this.incOperandStack(-1);
	 }
	 void emitStoreLocalDouble(int localVarIndex)
	 {
	 	emit1( Instruction.STORE_LOCAL_DOUBLE );
	 	emit2( localVarIndex );
	 	this.incOperandStack(-2);
	 }
	 void emitStoreLocalRef(int localVarIndex)
	 {
	 	emit1( Instruction.STORE_LOCAL_REF );
	 	emit2( localVarIndex );
	 	this.incOperandStack(-1);
	 }
	 void emitStoreLocalVar(int localVarIndex,String typeName)
	 {
	 	if( CentralTypeTable.isUVMIntegerType( typeName ))
	 	{
	 		this.emitStoreLocalInt(localVarIndex);	
	 	}
	 	else if( CentralTypeTable.isLong( typeName ))
	 	{
	 		this.emitStoreLocalLong( localVarIndex);
	 	}
	 	else if( CentralTypeTable.isFloat(typeName))
	 	{
	 		this.emitStoreLocalFloat( localVarIndex );
	 	} 
	 	else if( CentralTypeTable.isDouble(typeName))
	 	{
	 		this.emitStoreLocalDouble( localVarIndex );
	 	}
	 	else
	 	{
	 		this.emitStoreLocalRef( localVarIndex );
	 	}
	 	
	 }
//************************************* Field **********************************
	void emitGetStaticField(int fieldRefIndex, int fieldSize)
	{
		emit1( Instruction.GET_STATIC_FIELD );
		emit2( fieldRefIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack( fieldSize );
	}
	void emitPutStaticField(int fieldRefIndex,int fieldSize)
	{
		emit1( Instruction.PUT_STATIC_FIELD );
		emit2( fieldRefIndex );
		this.incOperandStack(-fieldSize);
	}
	void emitGetInstanceField(int fieldRefIndex,int fieldSize)
	{
		emit1( Instruction.GET_INSTANCE_FIELD );
		emit2( fieldRefIndex );
		this.oldStackLoacation = this.currentStackLocation;
		this.oldMax = this.maxStack;
		this.incOperandStack( fieldSize );
		
	}
	void emitPutInstanceField(int fieldRefIndex, int fieldSize)
	{
		emit1( Instruction.PUT_INSTANCE_FIELD );
		emit2( fieldRefIndex );
		this.incOperandStack( - fieldSize );
	}
	void emitLoadLocalThis()
	{
		emit1(Instruction.LOAD_LOCAL_REF);
		emit2(0);
		this.incOperandStack(1);
	}
	
	//*********************************** Method Call **************************
	void emitCallStatic(int methodRefIndex, int parSize,int retSize)
	{
		emit1( Instruction.CALL_STATIC );
		emit2( methodRefIndex );
		this.incOperandStack( - parSize );
		this.incOperandStack( retSize );
	}
	void emitCallVirtual(int methodRefIndex, int parSize,int retSize)
	{
		emit1( Instruction.CALL_VIRTUAL );
		emit2( methodRefIndex );
		this.incOperandStack( - (parSize+1) ); //Hidden "this" reference variable
		this.incOperandStack( retSize );
	}
	void emitCallConstructor(int methodRefIndex,int parSize,int retSize)
	{
		emit1( Instruction.CALL_CONSTRUCTOR );
		emit2( methodRefIndex );
		this.incOperandStack(-(parSize+1));//Hidden "this" reference variable
		this.incOperandStack( retSize );
	}
	void emitCallInterface(int methodRefIndex,int parSize,int retSize)
	{
		emit1( Instruction.CALL_INTERFACE );
		emit2( methodRefIndex );
		this.incOperandStack(-(parSize+1));//Hidden "this" reference variable
		this.incOperandStack( retSize );
	}
	void emitCallRebindable(int methodRefIndex,int parSize,int retSize)
	{
		emit1( Instruction.CALL_REBINDABLE );
		emit2( methodRefIndex );
		this.incOperandStack(-(parSize+1));//Hidden "this" reference variable
		this.incOperandStack( retSize );
	}
	//********************* Method Return **************************************
	void emitIntegerReturn()
	{
		emit1( Instruction.INT_RETURN );
		this.incOperandStack(-1);
	}
	void emitLongReturn()
	{
		emit1( Instruction.LONG_RETURN );
		this.incOperandStack( -2 );
	}
	void emitFloatReturn()
	{
		emit1( Instruction.FLOAT_RETURN );
		this.incOperandStack( -1 );
	}
	void emitDoubleReturn()
	{
		emit1( Instruction.DOUBLE_RETURN );
		this.incOperandStack( -2 );
	}
	void emitRefReturn()
	{
		emit1( Instruction.REF_RETURN );
		this.incOperandStack( -1 );
	}
	void emitReturn()
	{
		emit1( Instruction.RETURN );
		
	}
	void emitReturnIfNot()
	{
		///System.out.println ("Current Pointer "+cp);
		if(cp == 0 && !CodeGenerator.currentMethod.isNative())//
		{
			emitReturn();
		}
		else if(cp==0 )
		{
			emitReturn();
		}
		else if( code[cp-1] != Instruction.RETURN )
		{
			emitReturn();
		}
		else
		{
			emitReturn();
			//Debug.inform("This case occur "+ cp + " " +code[cp-1]);
		}
	}
	void emitReturnOnType(String typeName)
	{
		if( CentralTypeTable.isVoid( typeName ))
		{
			emitReturn();
		}
		else if( CentralTypeTable.isUVMIntegerType( typeName ))
		{
			emitIntegerReturn();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			emitLongReturn();
		}
		else if( CentralTypeTable.isFloat(typeName))
		{
			emitFloatReturn();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			emitDoubleReturn();
		}
		else 
		{
			emitRefReturn();
		}
		
	}
	void emitCreateObject(int classIndex)
	{
		//Debug.inform("CREATE Object "+classIndex);
		emit1( Instruction.CREATE_OBJECT );
		emit2( classIndex );
		this.incOperandStack(1);
	}
	//==================================ADD ========================================
	void emitAddInt()
	{
		emit1( Instruction.ADD_INT );
		this.incOperandStack(-1);
	}
	void emitAddLong()
	{
		emit1( Instruction.ADD_LONG );
		this.incOperandStack( -2 );
	}
	void emitAddFloat()
	{
		emit1(Instruction.ADD_FLOAT);
		this.incOperandStack( -1 );
	}
	void emitAddDouble()
	{
		emit1( Instruction.ADD_DOUBLE );
		this.incOperandStack( -2 );
	}
	
	
	void emitAddType(String typeName)
	{
		
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitAddInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitAddLong();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitAddFloat();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitAddDouble();
		}
		
		else
		{
			Debug.inform("Code Generation error in add type ");
		}
	}
	//==================================Sub ========================================
	void emitSubInt()
	{
		emit1( Instruction.SUB_INT );
		this.incOperandStack(-1);
	}
	void emitSubLong()
	{
		emit1( Instruction.SUB_LONG );
		this.incOperandStack( -2 );
	}
	void emitSubFloat()
	{
		emit1(Instruction.SUB_FLOAT);
		this.incOperandStack( -1 );
	}
	void emitSubDouble()
	{
		emit1( Instruction.SUB_DOUBLE );
		this.incOperandStack( -2 );
	}
	
	
	void emitSubType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitSubInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitSubLong();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitSubFloat();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitSubDouble();
		}
		else
		{
			Debug.inform("Code Generation error in sub type ");
		}
	}
	//================================== MUL ========================================
	void emitMultInt()
	{
		emit1( Instruction.MULT_INT );
		this.incOperandStack(-1);
	}
	void emitMultLong()
	{
		emit1( Instruction.MULT_LONG );
		this.incOperandStack( -2 );
	}
	void emitMultFloat()
	{
		emit1(Instruction.MULT_FLOAT);
		this.incOperandStack( -1 );
	}
	void emitMultDouble()
	{
		emit1( Instruction.MULT_DOUBLE );
		this.incOperandStack( -2 );
	}
	
	
	void emitMultType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitMultInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitMultLong();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitMultFloat();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitMultDouble();
		}
		else
		{
			Debug.inform("Code Generation error in mult type ");
		}
	}
	//================================== MUL ========================================
	void emitDivInt()
	{
		emit1( Instruction.DIV_INT );
		this.incOperandStack(-1);
	}
	void emitDivLong()
	{
		emit1( Instruction.DIV_LONG );
		this.incOperandStack( -2 );
	}
	void emitDivFloat()
	{
		emit1(Instruction.DIV_FLOAT);
		this.incOperandStack( -1 );
	}
	void emitDivDouble()
	{
		emit1( Instruction.DIV_DOUBLE );
		this.incOperandStack( -2 );
	}
	
	
	void emitDivType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitDivInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitDivLong();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitDivFloat();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDivDouble();
		}
		else
		{
			Debug.inform("Code Generation error in div type ");
		}
	}
	//================================== Mod ========================================
	void emitModInt()
	{
		emit1( Instruction.MOD_INT );
		this.incOperandStack(-1);
	}
	void emitModLong()
	{
		emit1( Instruction.MOD_LONG );
		this.incOperandStack( -2 );
	}
	void emitModFloat()
	{
		emit1(Instruction.MOD_FLOAT);
		this.incOperandStack( -1 );
	}
	void emitModDouble()
	{
		emit1( Instruction.MOD_DOUBLE );
		this.incOperandStack( -2 );
	}
	
	
	void emitModType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitModInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitModLong();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitModFloat();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitModDouble();
		}
		else
		{
			Debug.inform("Code Generation error in div type ");
		}
	}
	//================================== Equal ========================================
	void emitIntEqual()
	{
		emit1( Instruction.INT_EQUAL );
		this.incOperandStack(-1);
	}
	void emitLongEqual()
	{
		emit1( Instruction.LONG_EQUAL );
		this.incOperandStack( -2 );
	}
	void emitFloatEqual()
	{
		emit1(Instruction.FLOAT_EQUAL);
		this.incOperandStack( -1 );
	}
	void emitDoubleEqual()
	{
		emit1( Instruction.DOUBLE_EQUAL );
		this.incOperandStack( -2 );
	}
	
	
	void emitEqualType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitIntEqual();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitLongEqual();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitFloatEqual();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDoubleEqual();
		}
		else
		{
			//Object must perform integer comparison
			this.emitIntEqual();
			//Debug.inform("Code Generation error in div type ");
		}
	}
	//================================== Not Equal ========================================
	void emitIntNotEqual()
	{
		emit1( Instruction.INT_NOT_EQUAL );
		this.incOperandStack(-1);
	}
	void emitLongNotEqual()
	{
		emit1( Instruction.LONG_NOT_EQUAL );
		this.incOperandStack( -2 );
	}
	void emitFloatNotEqual()
	{
		emit1(Instruction.FLOAT_NOT_EQUAL);
		this.incOperandStack( -1 );
	}
	void emitDoubleNotEqual()
	{
		emit1( Instruction.DOUBLE_NOT_EQUAL );
		this.incOperandStack( -2 );
	}
	
	
	void emitNotEqualType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitIntNotEqual();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitLongNotEqual();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitFloatNotEqual();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDoubleNotEqual();
		}
		else
		{
			//Object must perform integer comparison
			this.emitIntNotEqual();
			//Debug.inform("Code Generation error in div type ");
		}
	}
	//================================== GreaterThan Equal ========================================
	void emitIntGTEqual()
	{
		emit1( Instruction.INT_GT_EQUAL );
		this.incOperandStack(-1);
	}
	void emitLongGTEqual()
	{
		emit1( Instruction.LONG_GT_EQUAL );
		this.incOperandStack( -2 );
	}
	void emitFloatGTEqual()
	{
		emit1(Instruction.FLOAT_GT_EQUAL);
		this.incOperandStack( -1 );
	}
	void emitDoubleGTEqual()
	{
		emit1( Instruction.DOUBLE_GT_EQUAL );
		this.incOperandStack( -2 );
	}
	
	
	void emitGTEqualType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitIntGTEqual();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitLongGTEqual();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitFloatGTEqual();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDoubleGTEqual();
		}
		else
		{
			//Object must perform integer comparison
			//this.emitIntNotEqual();
			Debug.inform("Code Generation error in div type " + typeName);
		}
	}
	//================================== GreaterThan Equal ========================================
	void emitIntGT()
	{
		emit1( Instruction.INT_GT );
		this.incOperandStack(-1);
	}
	void emitLongGT()
	{
		emit1( Instruction.LONG_GT );
		this.incOperandStack( -2 );
	}
	void emitFloatGT()
	{
		emit1(Instruction.FLOAT_GT);
		this.incOperandStack( -1 );
	}
	void emitDoubleGT()
	{
		emit1( Instruction.DOUBLE_GT );
		this.incOperandStack( -2 );
	}
	
	
	void emitGTType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitIntGT();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitLongGT();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitFloatGT();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDoubleGT();
		}
		else
		{
			//Object must perform integer comparison
			//this.emitIntNotEqual();
			Debug.inform("Code Generation error in div type " + typeName);
		}
	}
	//================================== Less than ========================================
	void emitIntLT()
	{
		emit1( Instruction.INT_LT );
		this.incOperandStack(-1);
	}
	void emitLongLT()
	{
		emit1( Instruction.LONG_LT );
		this.incOperandStack( -2 );
	}
	void emitFloatLT()
	{
		emit1(Instruction.FLOAT_LT);
		this.incOperandStack( -1 );
	}
	void emitDoubleLT()
	{
		emit1( Instruction.DOUBLE_LT );
		this.incOperandStack( -2 );
	}
	
	
	void emitLTType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitIntLT();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitLongLT();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitFloatLT();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDoubleLT();
		}
		else
		{
			//Object must perform integer comparison
			//this.emitIntNotEqual();
			Debug.inform("Code Generation error in div type " + typeName);
		}
	}
	//================================== LessThan Equal ========================================
	void emitIntLTEqual()
	{
		emit1( Instruction.INT_LT_EQUAL );
		this.incOperandStack(-1);
	}
	void emitLongLTEqual()
	{
		emit1( Instruction.LONG_LT_EQUAL );
		this.incOperandStack( -2 );
	}
	void emitFloatLTEqual()
	{
		emit1(Instruction.FLOAT_LT_EQUAL);
		this.incOperandStack( -1 );
	}
	void emitDoubleLTEqual()
	{
		emit1( Instruction.DOUBLE_LT_EQUAL );
		this.incOperandStack( -2 );
	}
	
	
	void emitLTEqualType(String typeName)
	{
		if( CentralTypeTable.isUVMIntegerType( typeName ) )
		{
			this.emitIntLTEqual();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitLongLTEqual();
		}
		else if( CentralTypeTable.isFloat( typeName))
		{
			this.emitFloatLTEqual();
		}
		else if( CentralTypeTable.isDouble( typeName ))
		{
			this.emitDoubleLTEqual();
		}
		else
		{
			//Object must perform integer comparison
			//this.emitIntNotEqual();
			Debug.inform("Code Generation error in div type " + typeName);
		}
	}
	void emitAnd()
	{
		emit1( Instruction.AND );
		this.incOperandStack(-1);
	}
	void emitOr()
	{
		emit1( Instruction.OR );
		this.incOperandStack(-1);
	}
	void emitNot()
	{
		emit1( Instruction.NOT );
	}
	
	//================== Convertion Function ===================================
	void emitIntegerToByte()
	{
		emit1( Instruction.INTEGER_TO_BYTE );
		this.incOperandStack( 0 );	
	}
	
	void emitIntegerToShort()
	{
		emit1(Instruction.INTEGER_TO_SHORT);
		this.incOperandStack(0);
	}
	void emitIntegerToLong()
	{
		emit1( Instruction.INTEGER_TO_LONG );
		this.incOperandStack( 1 );
	}
	void emitIntegerToFloat()
	{
		emit1( Instruction.INTEGER_TO_FLOAT );
		this.incOperandStack( 0 );
	}
	void emitIntegerToDouble()
	{
		emit1( Instruction.INTEGER_TO_DOUBLE );
		this.incOperandStack( 1 );
	}
	
	void emitLongToInteger()
	{
		emit1( Instruction.LONG_TO_INTEGER );
		this.incOperandStack( -1 );
	}
	void emitLongToFloat()
	{
		emit1( Instruction.LONG_TO_FLOAT );
		this.incOperandStack( -1 );
	}
	void emitLongToDouble()
	{
		emit1( Instruction.LONG_TO_DOUBLE );
		this.incOperandStack( 0 );
	}
	void emitFloatToInteger()
	{
		emit1( Instruction.FLOAT_TO_INTEGER );
		this.incOperandStack( 0 );
	}
	void emitFloatToLong()
	{
		emit1( Instruction.FLOAT_TO_LONG );
		this.incOperandStack( 1 );
	}
	void emitFloatToDouble()
	{
		emit1( Instruction.FLOAT_TO_DOUBLE );
		this.incOperandStack( 1 );
	}
	void emitDoubleToInteger()
	{
		emit1( Instruction.DOUBLE_TO_INTEGER );
		this.incOperandStack( - 1);
	}
	void emitDoubleToLong()
	{
		emit1( Instruction.DOUBLE_TO_LONG );
		this.incOperandStack( 0 );
	}
	void emitDoubleToFloat()
	{
		emit1( Instruction.DOUBLE_TO_FLOAT );
		this.incOperandStack( -1 );
	}
	//***************************    From ********* TO
	void emitConvertPrimitive(String source, String destination)
	{
		if( CentralTypeTable.isCharacter( source ))
		{
			if(CentralTypeTable.isCharacter( destination ))
			{
			}
			else if(CentralTypeTable.isByte( destination ) )
			{
				emitIntegerToByte();
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				emitIntegerToShort();
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				emitIntegerToLong();	
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				emitIntegerToFloat();
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
				emitIntegerToDouble();
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}
		}
		else if( CentralTypeTable.isByte( source ))
		{
			
			if(CentralTypeTable.isCharacter( destination ) )
			{
			}
			if(CentralTypeTable.isByte( destination ))
			{
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				emitIntegerToShort();
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
				
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				emitIntegerToLong();
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				emitIntegerToFloat();
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
				emitIntegerToDouble();
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}
		}
		else if( CentralTypeTable.isShort( source) )
		{
			if(CentralTypeTable.isCharacter( destination ) )
			{
			}
			else if(CentralTypeTable.isByte( destination ) )
			{
				emitIntegerToByte();
			}
			else if( CentralTypeTable.isShort( destination ))
			{
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				emitIntegerToLong();
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				emitIntegerToFloat();
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
				emitIntegerToDouble();
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}
		}
		else if( CentralTypeTable.isInteger( source))
		{
			if(CentralTypeTable.isCharacter( destination ) )
			{
				
			}
			else if(CentralTypeTable.isByte( destination ) )
			{
				emitIntegerToByte();
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				emitIntegerToShort();
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				emitIntegerToLong();
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				emitIntegerToFloat();
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
				emitIntegerToDouble();
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}	
		}
		else if( CentralTypeTable.isLong( source ))
		{
			if(CentralTypeTable.isCharacter( destination ) )
			{
				emitLongToInteger();
			}
			else if(CentralTypeTable.isByte( destination ) )
			{
				emitLongToInteger();
				emitIntegerToByte();
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				emitLongToInteger();
				emitIntegerToShort();
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
				emitLongToInteger();
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				emitLongToInteger();
				emitIntegerToFloat();
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
				emitLongToInteger();
				emitIntegerToDouble();
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}
		}
		else if( CentralTypeTable.isFloat( source) )
		{
			if(CentralTypeTable.isCharacter( destination ) )
			{
				emitFloatToInteger();
			}
			else if(CentralTypeTable.isByte( destination ) )
			{
				emitFloatToInteger();
				emitIntegerToByte();
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				emitFloatToInteger();
				emitIntegerToShort();
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
				emitFloatToInteger();
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				emitFloatToLong();
				
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
				emitFloatToDouble();
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}
		}
		else if( CentralTypeTable.isDouble( source ))
		{
			if(CentralTypeTable.isCharacter( destination ) )
			{
				emitDoubleToInteger();
			}
			else if(CentralTypeTable.isByte( destination ) )
			{
				emitDoubleToInteger();
				emitIntegerToByte();
			}
			else if( CentralTypeTable.isShort( destination ))
			{
				emitDoubleToInteger();
				emitIntegerToShort();
			}
			else if( CentralTypeTable.isInteger( destination ))
			{
				emitDoubleToInteger();
			}
			else if( CentralTypeTable.isLong( destination ))
			{
				emitDoubleToLong();
			}
			else if( CentralTypeTable.isFloat( destination))
			{
				emitDoubleToFloat();
			}
			else if( CentralTypeTable.isDouble( destination ))
			{
			}
			else
			{
				//Debug.inform(" Invalid conversion form "+ source +" to " + destination);
			}
		}
		else
		{
			//Debug.inform("Invalid conversion from "+ source +" to "+destination);
		}
	}
	
	void emitJumpFalse(int index)
	{
		emit1( Instruction.JUMP_FALSE );
		emit2( index );
		this.incOperandStack(-1);
	}
	void emitJump(int index)
	{
		emit1( Instruction.JUMP );
		emit2( index);
	}
	void emitJumpTrue(int index)
	{
		emit1( Instruction.JUMP_TRUE );
		emit2( index );
		this.incOperandStack(-1);
	}
	void emitCreateArray(int index,int dimension)
	{
		emit1( Instruction.CREATE_ARRAY);
		emit2( index );
		this.incOperandStack(- dimension ); //Pop dimension from operand stack
		this.incOperandStack(1); //push arr_ref on top of stack
	}
	
	void emitGetElementRef()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_REF);
		this.incOperandStack(-2);
		this.incOperandStack(1);
	}
	void emitGetElementByte()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_BYTE);
		this.incOperandStack(-2);
		this.incOperandStack(1);
	}
	void emitGetElementShort()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_SHORT);
		this.incOperandStack(-2);
		this.incOperandStack(1);
	}
	void emitGetElementInt()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_INT);
		this.incOperandStack(-2);
		this.incOperandStack(1);
	}
	void emitGetElementLong()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_LONG );
		this.incOperandStack(-2);
		this.incOperandStack(2);
	}
	void emitGetElementFloat()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_FLOAT);
		this.incOperandStack(-2);
		this.incOperandStack(1);
	}
	void emitGetElementDouble()
	{
		emit1( Instruction.GET_ARRAY_ELEMENT_DOUBLE);
		this.incOperandStack(-2);
		this.incOperandStack(2);
	}
	void emitGetElementType(String typeName)
	{
		if( CentralTypeTable.isByte(typeName) || CentralTypeTable.isBoolean(typeName) || CentralTypeTable.isCharacter(typeName))
		{
			this.emitGetElementByte();
		}
		else if( CentralTypeTable.isShort( typeName ))
		{
			this.emitGetElementShort();
		}
		else if( CentralTypeTable.isInteger(typeName))
		{
			this.emitGetElementInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitGetElementLong();
		}
		else if( CentralTypeTable.isFloat( typeName ))
		{
			this.emitGetElementFloat();
		}
		else if( CentralTypeTable.isDouble(typeName))
		{
			this.emitGetElementDouble();
		}
		else
		{
			this.emitGetElementRef();
		}
	}
	
	void emitStoreElementRef()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_REF);
		this.incOperandStack(-2);
		this.incOperandStack(-1);
	}
	void emitStoreElementByte()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_BYTE);
		this.incOperandStack(-2);
		this.incOperandStack(-1);
	}
	void emitStoreElementShort()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_SHORT);
		this.incOperandStack(-2);
		this.incOperandStack(-1);
	}
	void emitStoreElementInt()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_INT);
		this.incOperandStack(-2);
		this.incOperandStack(-1);
	}
	void emitStoreElementLong()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_LONG );
		this.incOperandStack(-2);
		this.incOperandStack(2);
	}
	void emitStoreElementFloat()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_FLOAT);
		this.incOperandStack(-2);
		this.incOperandStack(1);
	}
	void emitStoreElementDouble()
	{
		emit1( Instruction.STORE_ARRAY_ELEMENT_DOUBLE);
		this.incOperandStack(-2);
		this.incOperandStack(2);
	}
	void emitStoreElementType(String typeName)
	{
		if( CentralTypeTable.isByte(typeName) || CentralTypeTable.isBoolean(typeName) || CentralTypeTable.isCharacter(typeName))
		{
			this.emitStoreElementByte();
		}
		else if( CentralTypeTable.isShort( typeName ))
		{
			this.emitStoreElementShort();
		}
		else if( CentralTypeTable.isInteger(typeName))
		{
			this.emitStoreElementInt();
		}
		else if( CentralTypeTable.isLong( typeName ))
		{
			this.emitStoreElementLong();
		}
		else if( CentralTypeTable.isFloat( typeName ))
		{
			this.emitStoreElementFloat();
		}
		else if( CentralTypeTable.isDouble(typeName))
		{
			this.emitStoreElementDouble();
		}
		else
		{
			this.emitStoreElementRef();
		}
	}
	void emitThrowException()
	{
		this.emit1(Instruction.THROW_EXCEPTION);
		
		this.incOperandStack(-1);
	}
	void emitInstanceOf(int classIndex)
	{
		this.emit1(Instruction.INSTANCE_OF);
		this.emit2(classIndex);
		
	}
	void emitRebind(int targetMethodIndex,int rebindMethodIndex)
	{
		this.emit1( Instruction.REBIND );
		this.emit2( targetMethodIndex );
		this.emit2( rebindMethodIndex );
		this.incOperandStack(-1);
	}
	
	void emitPrint(int type)
	{
		//emit1(Instruction.PRINT);
		//emit1(type);
		//Pop the data freom the stack accroding to the type and then print it
		//this.currentStackLocation--;
		//is.maxStack = (this.currentStackLocation > this.maxStack)? this.currentStackLocation: this.maxStack;
	}
}