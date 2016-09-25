#ifndef uvm_h
#include "uvm.h"
#endif 
#ifndef nativeManager_h
#include "nativeManager.h"
#endif
#define library_h
#include <iostream>
using namespace std;
/*********** This is library of UCSY *************************/

/*******************************************************************************
Native Method implementation of various UCSY API
native method name are given of the following form, UVM Implementor must ahere to 
these naming rule , accroding to these naming rule, native mehtod will be
loaded by the ClassLoader

for example method name println(m,)v , class name Console
class Console
{
      public static native void println()
      {
      }
}
native mehtod name = ClassName_MethodName_MethodSignature
for above method ,its name will be
Console_println_m1_v

replace , with  _
replace ( or ) with _ [underscore]
********************************************************************************/
//native void println(int a)
void Console_println_integer(ExecutionEnviroment* env);


//void println(boolean b)
void Console_println_boolean(ExecutionEnviroment* env);

//void prinltn (char c)
void Console_println_char(ExecutionEnviroment* env);
//void prinltn(string s)
void Console_println_string(ExecutionEnviroment* env);
//void println(long l)
void Console_println_long(ExecutionEnviroment* env);
//void println(float f)
void Console_println_float(ExecutionEnviroment* env);
//void prinltn(double d)
void Console_println_double(ExecutionEnviroment* env);
//void println()
void Console_println(ExecutionEnviroment* env);

//Print method
//native void println(int a)
void Console_print_i__v(ExecutionEnviroment* env);

//void println(boolean b)
void Console_print_t__v(ExecutionEnviroment* env);
//void prinltn (char c)
void Console_print_c__v(ExecutionEnviroment* env);
//void prinltn(string s)
void Console_print_m__v(ExecutionEnviroment* env);
//void print(long l)
void Console_print_l__v(ExecutionEnviroment* env);
//void print(float f)
void Console_print_f__v(ExecutionEnviroment* env);
//void print(double d)
void Console_print_d__v(ExecutionEnviroment* env);
//void print()
void Console_print__v(ExecutionEnviroment* env);

void Console_read_integer(ExecutionEnviroment * env);
void Console_read_float(ExecutionEnviroment* env);
void Console_read_char(ExecutionEnviroment* env);

void Integer_toString_m(ExecutionEnviroment* env);
void NativeTest_printMethod_v(ExecutionEnviroment *env);
void MessageBox_print_v(ExecutionEnviroment *env);
void initializeNativeMethod();

//TCP facilites 
void TCPServerSocket_initialize_v(ExecutionEnviroment *env);
void TCPServerSocket_getConnection_TCPClientSocket(ExecutionEnviroment *env);
void TCPClientSocket_sendData_v(ExecutionEnviroment *env);

//String class
void String_getLength_i(ExecutionEnviroment *env);
void String_charAt_c(ExecutionEnviroment *env);
void String_concat_m(ExecutionEnviroment *env);
