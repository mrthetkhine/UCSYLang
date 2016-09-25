import java.io.*;

class PrintOpCode
{
	public static void printCode(byte[]code, DataOutputStream dout)throws IOException
	{
		int pc =0,opcode,index=0;
		dout.writeBytes("<table border=1>");
		dout.writeBytes("<tr><th>PC</th><th>Inst</th><th>Operand</th>");
		int i1=0,i2=0;
		//System.out.println (code.length);
		for(int i=0;pc < code.length;i++)
		{
			opcode = code[pc];
			///System.out.println ("Yeah "+ pc);	
			dout.writeBytes("<tr><td>"+pc+"</td>");
			switch (opcode)
			{
				case Instruction.LOAD_CPOOL:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>load_cpool</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.LOAD_LOCAL_DOUBLE:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>load_local_double</td><td><i style=\"color:red\">" + index+"</td>");
					
				break;
				
				case Instruction.LOAD_LOCAL_FLOAT:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>load_local_float</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.LOAD_LOCAL_INT:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>load_local_int</td><td><i style=\"color:red\">"+ index+"</i></td>");
				break;
				
				case Instruction.LOAD_LOCAL_LONG:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>load_local_long</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.LOAD_LOCAL_REF:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>load_local_ref</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.STORE_LOCAL_DOUBLE:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>store_local_double</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.STORE_LOCAL_FLOAT:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>store_local_float</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.STORE_LOCAL_INT:
					
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>store_local_int</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.STORE_LOCAL_LONG:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>store_local_long</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				
				case Instruction.STORE_LOCAL_REF:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>store_local_ref</td><td><i style=\"color:red\">" + index+"</i></td>");
				break;
				case Instruction.GET_INSTANCE_FIELD:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>get_instance_field</td><td><a href=#"+index+">"  + index+"</i></td>");
				break;
				case Instruction.GET_STATIC_FIELD:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>get_static_field</td><td><a href=#"+index+">" + index+"</td>");
				break;
				
				case Instruction.PUT_INSTANCE_FIELD:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>put_instance_field</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.PUT_STATIC_FIELD:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>put_static_field</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.CALL_STATIC:
					index = code[pc+1] | code[pc+2];
					pc += 2;

					dout.writeBytes("<td>call_static</td><td><a href=#"+index+">" + index+"</td>");
				break;
				
				case Instruction.CALL_VIRTUAL:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>call_virtual</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.CALL_REBINDABLE:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>call_rebindable</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.CALL_CONSTRUCTOR:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>call_constructor</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.CALL_INTERFACE:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>call_interface</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.RETURN:
					dout.writeBytes("<td>return</td>");
				break;
				
				case Instruction.INT_RETURN:
					dout.writeBytes("<td>int_return</td>");
				break;
				
				case Instruction.FLOAT_RETURN:
					dout.writeBytes("<td>float_return</td>");
				break;
				
				case Instruction.LONG_RETURN:
					dout.writeBytes("<td>long_return</td>");
				break;
				
				case Instruction.DOUBLE_RETURN:
					dout.writeBytes("<td>double_return</td>");
				break;
				
				case Instruction.REF_RETURN:
					dout.writeBytes("<td>ref_return</td>");
				break;
				
				case Instruction.CREATE_OBJECT:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>create_object</td><td><a href=#"+index+">" + index+"</td>");
					
				break;
				
				case Instruction.ADD_INT:
					dout.writeBytes("<td>add_int</td><td></td>");
				break;
				
				case Instruction.ADD_LONG:
					dout.writeBytes("<td>add_long</td><td></td>");
				break;
				
				case Instruction.ADD_FLOAT:
					dout.writeBytes("<td>add_float</td><td></td>");
				break;
				
				case Instruction.ADD_DOUBLE:
					dout.writeBytes("<td>add_double</td><td></td>");
				break;
				
				case Instruction.SUB_INT:
					dout.writeBytes("<td>sub_int</td><td></td>");
				break;
				
				case Instruction.SUB_LONG:
					dout.writeBytes("<td>sub_long</td><td></td>");
				break;
				
				case Instruction.SUB_FLOAT:
					dout.writeBytes("<td>sub_float</td><td></td>");
				break;
				
				case Instruction.SUB_DOUBLE:
					dout.writeBytes("<td>sub_double</td><td></td>");
				break;
				
				case Instruction.MULT_INT:
					dout.writeBytes("<td>mult_int</td><td></td>");
				break;
				
				case Instruction.MULT_LONG:
					dout.writeBytes("<td>mult_long</td><td></td>");
				break;
				
				case Instruction.MULT_FLOAT:
					dout.writeBytes("<td>mult_float</td><td></td>");
				break;
				
				case Instruction.MULT_DOUBLE:
					dout.writeBytes("<td>mult_double</td><td></td>");
				break;
				
				case Instruction.DIV_INT:
					dout.writeBytes("<td>div_int</td><td></td>");
				break;
				
				case Instruction.DIV_LONG:
					dout.writeBytes("<td>div_long</td><td></td>");
				break;
				
				case Instruction.DIV_FLOAT:
					dout.writeBytes("<td>div_float</td><td></td>");
				break;
				
				case Instruction.DIV_DOUBLE:
					dout.writeBytes("<td>div_double</td><td></td>");
				break;
				
				case Instruction.MOD_INT:
					dout.writeBytes("<td>mod_int</td><td></td>");
				break;
				
				case Instruction.MOD_LONG:
					dout.writeBytes("<td>mod_long</td><td></td>");
				break;
				
				case Instruction.MOD_FLOAT:
					dout.writeBytes("<td>mod_float</td><td></td>");
				break;
				
				case Instruction.MOD_DOUBLE:	
					dout.writeBytes("<td>mod_double</td><td></td>");
				break;
				
				case Instruction.INT_EQUAL:
					dout.writeBytes("<td>int_equal</td><td></td>");
				break;
				
				case Instruction.LONG_EQUAL:
					dout.writeBytes("<td>long_equal</td><td></td>");
				break;
				
				case Instruction.FLOAT_EQUAL:
					dout.writeBytes("<td>float_equal</td><td></td>");
				break;
				
				case Instruction.DOUBLE_EQUAL:
					dout.writeBytes("<td>double_equal</td><td></td>");
				break;
				
				case Instruction.INT_NOT_EQUAL:
					dout.writeBytes("<td>int_not_equal</td><td></td>");
				break;
				
				case Instruction.LONG_NOT_EQUAL:
					dout.writeBytes("<td>long_not_equal</td><td></td>");
				break;
				
				case Instruction.FLOAT_NOT_EQUAL:
					dout.writeBytes("<td>float_not_equal</td><td></td>");
				break;
				
				case Instruction.DOUBLE_NOT_EQUAL:
					dout.writeBytes("<td>double_not_equal</td><td></td>");
				break;
				
				case Instruction.INT_GT:
					dout.writeBytes("<td>int_gt</td><td></td>");
				break;
				
				case Instruction.LONG_GT:
					dout.writeBytes("<td>long_gt</td><td></td>");
				break;
				
				case Instruction.FLOAT_GT:
					dout.writeBytes("<td>float_gt</td><td></td>");
				break;
				
				case Instruction.DOUBLE_GT:
					dout.writeBytes("<td>double_gt</td><td></td>");
				break;
				
				case Instruction.INT_LT:
					dout.writeBytes("<td>int_lt</td><td></td>");
				break;
				
				case Instruction.LONG_LT:
					dout.writeBytes("<td>long_lt</td><td></td>");
				break;
				
				case Instruction.FLOAT_LT:
					dout.writeBytes("<td>float_lt</td><td></td>");
				break;
				
				
				case Instruction.DOUBLE_LT:
					dout.writeBytes("<td>double_lt</td><td></td>");
				break;
				
				case Instruction.INT_GT_EQUAL:
					dout.writeBytes("<td>int_gt_equal</td><td></td>");
				break;
				
				case Instruction.LONG_GT_EQUAL:
					dout.writeBytes("<td>long_gt_equal</td><td></td>");
				break;
				
				case Instruction.FLOAT_GT_EQUAL:
					dout.writeBytes("<td>float_gt_equal</td><td></td>");
				break;
				
				case Instruction.DOUBLE_GT_EQUAL:
					dout.writeBytes("<td>double_gt_equal</td><td></td>");
				break;
				
				case Instruction.INT_LT_EQUAL:
					dout.writeBytes("<td>int_lt_equal</td><td></td>");
				break;
				
				case Instruction.LONG_LT_EQUAL:
					dout.writeBytes("<td>long_lt_equal</td><td></td>");
				break;
				
				case Instruction.FLOAT_LT_EQUAL:
					dout.writeBytes("<td>float_lt_equal</td><td></td>");
				break;
				
				case Instruction.DOUBLE_LT_EQUAL:
					dout.writeBytes("<td>double_lt_equal</td><td></td>");
				break;
				
				case Instruction.AND:
					dout.writeBytes("<td>and</td><td></td>");
				break;
				
				case Instruction.OR:
					dout.writeBytes("<td>or</td><td></td>");
				break;
				
				case Instruction.NOT:
					dout.writeBytes("<td>not</td><td></td>");
				break;
				
				case Instruction.INTEGER_TO_BYTE:
					dout.writeBytes("<td>integer_to_byte</td><td></td>");
				break;
				
				case Instruction.INTEGER_TO_SHORT:
					dout.writeBytes("<td>integer_to_short</td><td></td>");
				break;
				
				case Instruction.INTEGER_TO_FLOAT:
					dout.writeBytes("<td>integer_to_float</td><td></td>");
				break;
				
				case Instruction.INTEGER_TO_LONG:
					dout.writeBytes("<td>integer_to_long</td><td></td>");
				break;
				
				case Instruction.INTEGER_TO_DOUBLE:
					dout.writeBytes("<td>integer_to_double</td><td></td>");
				break;
				
				case Instruction.LONG_TO_INTEGER:
					dout.writeBytes("<td>long_to_integer</td><td></td>");
				break;
				
				case Instruction.LONG_TO_FLOAT:
					dout.writeBytes("<td>long_to_float</td><td></td>");
				break;
				
				case Instruction.LONG_TO_DOUBLE:
					dout.writeBytes("<td>long_to_double</td><td></td>");
				break;
				
				case Instruction.FLOAT_TO_INTEGER:
					dout.writeBytes("<td>float_to_integer</td><td></td>");
				break;
				
				case Instruction.FLOAT_TO_LONG:
					dout.writeBytes("<td>float_to_long</td><td></td>");
				break;
				
				case Instruction.FLOAT_TO_DOUBLE:
					dout.writeBytes("<td>float_to_double</td><td></td>");
				break;
				
				case Instruction.DOUBLE_TO_INTEGER:
					dout.writeBytes("<td>double_to_integer</td><td></td>");
				break;
				
				case Instruction.DOUBLE_TO_LONG:
					dout.writeBytes("<td>double_to_long</td><td></td>");
				break;
				
				case Instruction.DOUBLE_TO_FLOAT:
					dout.writeBytes("<td>double_to_float</td><td></td>");
				break;
				
				case Instruction.JUMP:
					
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>jump</td><td>"+index+"</td>");
				break;
				
				case Instruction.JUMP_FALSE:
					
					index =(int) ( code[pc+1] | code[pc+2]);
					
					pc += 2;
					dout.writeBytes("<td>jump_false</td><td>"+index+"</td>");
				break;
				
				case Instruction.JUMP_TRUE:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>jump_true</td><td>"+index+"</td>");
				break;
				
				case Instruction.CREATE_ARRAY:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>create_array</td><td><a href=#"+index+">" + index+"</td>");
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_BYTE:
					dout.writeBytes("<td>get_array_element_byte</td><td></td>");
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_SHORT:
					dout.writeBytes("<td>get_array_element_short</td><td></td>");
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_INT:
					dout.writeBytes("<td>get_array_element_int</td><td></td>");
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_LONG:
					dout.writeBytes("<td>get_array_element_long</td><td></td>");
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_FLOAT:
					dout.writeBytes("<td>get_array_element_float</td><td></td>");
				
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_DOUBLE:
					dout.writeBytes("<td>get_array_element_double</td><td></td>");
				break;
				
				case Instruction.GET_ARRAY_ELEMENT_REF:
					dout.writeBytes("<td>get_array_element_ref</td><td></td>");
				
				break;
				
				
				case Instruction.STORE_ARRAY_ELEMENT_BYTE:
					dout.writeBytes("<td>store_array_element_byte</td><td></td>");
				break;
				
				case Instruction.STORE_ARRAY_ELEMENT_SHORT:
					dout.writeBytes("<td>store_array_element_short</td><td></td>");
				break;
				
				case Instruction.STORE_ARRAY_ELEMENT_INT:
					dout.writeBytes("<td>store_array_element_int</td><td></td>");
				break;
				
				case Instruction.STORE_ARRAY_ELEMENT_LONG:
					dout.writeBytes("<td>store_array_element_long</td><td></td>");
				break;
				
				case Instruction.STORE_ARRAY_ELEMENT_FLOAT:
					dout.writeBytes("<td>store_array_element_float</td><td></td>");
				break;
				
				case Instruction.STORE_ARRAY_ELEMENT_DOUBLE:
					dout.writeBytes("<td>store_array_element_double</td><td></td>");
				break;
				
				case Instruction.STORE_ARRAY_ELEMENT_REF:
					dout.writeBytes("<td>store_array_element_ref</td><td></td>");
				break;
				
				case Instruction.THROW_EXCEPTION:
					dout.writeBytes("<td>throw_exception</td><td></td>");
				break;
				case Instruction.INSTANCE_OF:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					dout.writeBytes("<td>instance_of</td><td><a href=#"+index+">" + index+"</td>");
				break;
				case Instruction.REBIND:
					index = code[pc+1] | code[pc+2];
					pc += 2;
					i2 =  code[pc+1] | code[pc+2];
					pc+=2;
					dout.writeBytes("<td>rebind</td><td><a href=#"+index+">" + index+"</a>&nbsp;<a href=#"+i2 +">"+i2+"</a></td>");
				break;
				default:
					System.out.println ("Undefined instruction "+ opcode +" at "+ pc);
			}
			pc++;
			dout.writeBytes("</tr>");
		}
		dout.writeBytes("</table>");
	}
}