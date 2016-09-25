#define uvm_h
#include <windows.h>
#include <iostream>
#ifndef classManager_h
#include "classManager.h"
#endif
//#include "classParser.h"
//For Built in primitive type of UCSY
//**************************************************
typedef __int8 UCSYByte;
typedef short UCSYShort;
typedef int UCSYInteger;
typedef __int64 UCSYLong;
typedef float UCSYFloat;
typedef double UCSYDouble;
typedef char UCSYChar;
typedef int UCSYBoolean;
typedef char UCSYChar;
typedef void* UCSYWord;
typedef unsigned short uint16;
//***************************************************
//Routnie for converting Big -Edian to Littlte Edian
typedef unsigned char u1;
typedef unsigned short u2;
typedef unsigned int u4;
typedef unsigned __int64 u8;


#define READ_U1(classFile,value)  fread(OneByte,sizeof(char),1,classFile);\
        value = OneByte[0];
#define READ_U2(classFile,value)  fread(TwoByte,sizeof(char),2,classFile);\
        twoByte[0]=TwoByte[1];\
        twoByte[1]=TwoByte[0];\
        value= *(short *)twoByte;
        
#define read_U2_UShort(classFile,value)\
        fread(TwoByte,sizeof(char),2,classFile);\
        twoByte[0]=TwoByte[1];\
        twoByte[1]=TwoByte[0];\
        value= *(unsigned short *)twoByte;
        
#define READ_U4_FLOAT(classFile,value) fread(FourByte,sizeof(char),4,classFile);\
        fourByte[0]=FourByte[3]; \
        fourByte[1]=FourByte[2];\
        fourByte[2] = FourByte[1];\
        fourByte[3] = FourByte[0];\
        value = *(float *)fourByte;       
         
#define READ_U4(classFile,value) fread(FourByte,sizeof(char),4,classFile);\
        fourByte[0]=FourByte[3];\
        fourByte[1]=FourByte[2];\
        fourByte[2] = FourByte[1];\
        fourByte[3] = FourByte[0];\
        value = *(int *)fourByte;
#define READ_U8(classFile,value) fread(EightByte,sizeof(char),8,classFile); \
        eightByte[0] = EightByte[7];\
        eightByte[1] = EightByte[6];\
        eightByte[2] = EightByte[5];\
        eightByte[3] = EightByte[4];\
        eightByte[4] = EightByte[3];\
        eightByte[5] = EightByte[2];\
        eightByte[6] = EightByte[1];\
        eightByte[7] = EightByte[0];\
        value = *(long long*)eightByte;
        
#define READ_U8_DOUBLE(classFile,value)fread(EightByte,sizeof(char),8,classFile); \
        eightByte[0] = EightByte[7];\
        eightByte[1] = EightByte[6];\
        eightByte[2] = EightByte[5];\
        eightByte[3] = EightByte[4];\
        eightByte[4] = EightByte[3];\
        eightByte[5] = EightByte[2];\
        eightByte[6] = EightByte[1];\
        eightByte[7] = EightByte[0]; \
        value = *(double*)eightByte;
        
#define readByte(classFile,value)\
        READ_U1(classFile,value);        
        
#define readShort(classFile,value)\
        READ_U2(classFile,value);
        
#define readInteger(classFile,value)\
        READ_U4(classFile,value);
        
#define readLong(classFile,value)\
        READ_U8(classFile,value);         
        
#define readFloat(classFile,value)\
        READ_U4_FLOAT(classFile,value);       
        
#define readDouble(classFile,value)\
        READ_U8_DOUBLE(classFile,value);
        
#define readUShortInt(classFile,value)\
        READ_U2_UShort(classFile,value)
//****************for ContantPool    ***********************
//From UVMConstants.java
#define CPOOL_STRING_REF            1
#define CPOOL_INTEGER_REF           2
#define CPOOL_LONG_REF              3
#define CPOOL_FLOAT_REF             4
#define CPOOL_DOUBLE_REF            5
#define CPOOL_CLASS_REF             6
#define CPOOL_METHOD_REF            7
#define CPOOL_INTERFACE_METHOD_REF  8
#define CPOOL_FIELD_REF             9 


//For type to be represented internally
#define TYPE_BOOLEAN  1
#define TYPE_BYTE     2
#define TYPE_SHORT    3
#define TYPE_INTEGER  4
#define TYPE_LONG     5
#define TYPE_FLOAT    6
#define TYPE_DOUBLE   7
#define TYPE_CHAR     8
#define TYPE_STRING   9
#define TYPE_ARRAY    10
#define TYPE_OBJECT   11
#define TYPE_NULL     12


#define CLASS 1<<4
#define UCSYINTERFACE 1<<5
#define ABSTRACT 1<<1
#define NATIVE 1<<11
#define FREE   1<<12
#define isClass(modifier) (modifier& CLASS)==CLASS
#define isInterface(modifier) (modifier&UCSYINTERFACE)==UCSYINTERFACE
#define isAbstractClass(modifier) (modifier&ABSTRACT )== ABSTRACT
//#define isFreeClass(modifier) (modifier&FREE)==FREE



union UCSYValue
{
      void *reference;
      int intValue;
      float floatValue;
      unsigned long otherHalf;
};

class ConstantPoolEntry
{
public:
       UCSYByte tag;
       void* info;
};        
class IntegerEntry
{
public:
       UCSYInteger integerData;
};
class LongEntry
{
public:
       UCSYLong longData;
};
class FloatEntry
{
public:
       UCSYFloat floatData;
};
class DoubleEntry
{
public:
       UCSYDouble doubleData;
};
class StringEntry
{
public:
       UCSYShort length;
       char *stringData;     
};

/*******************************************************************************
          resolved field in MethodRefInfoEntry,InterfaceMethodRefEntry,FieldRefEntry
          ClassRefEntry is used for resolution of ConstantPool 
          ConstantPool entry are resoved at most one time, after they have been resolved
          the resovled entities is cache in their respecitive theEntryName variable                                                                                                                                       
                                                                                                                  
********************************************************************************/
class UVMMethod;
class MethodRefEntry
{
public:
       u2 classIndex;
       u2 nameIndex;
       u2 signatureIndex;
       
       char *className;
       char *methodName;
       char *signature; //Protocl
       
       bool resolved;
       UVMMethod *theMethod;
};
class InterfaceMethodRefEntry
{
public:
      u2 classIndex;
      u2 nameIndex;
      u2 signatureIndex;
      
      char *className;
      char *methodName;
      char *signature; // Protocol
      
      bool resolved;
      UVMMethod *theMethod;
      
};
class UVMField;
class FieldRefEntry
{
public:
       u2 classIndex;
       u2 nameIndex;
       u2 typeIndex;
       
       char *className;
       char *fieldName;
       char *fieldType;
       
       
       bool resolved ;
       UVMField *theField;
};
class UCSYString
{
public:
       UCSYShort length;
       char *stringData;
       
       UCSYString()
       {
             stringData = NULL;
             length = 0;      
       }
};
class UVMClass;
class ClassRefEntry
{
public:
     char *className; 
     int classNameIndex;
     UVMClass *theClass;    
     bool resolved;
     
     ClassRefEntry()
     {
           className = NULL;         
           resolved = false;         
     }
     
};
union Offest
{
       UCSYShort staticOffest;
       UCSYShort instanceOffest;
};
int getSizeOfTypeInByte(int type);
int getSizeOfTypeInWord(int type);
int getTypeOfTypeName(char *typeName);


typedef struct UCSYCode
{
       UCSYInteger lengthOfCode;
       u1* code;
};
class ExecutionEnviroment;
class UVMClass;
class UVMObject;
class CatchEntry
{
      public:
             UCSYShort from;
             UCSYShort to;
             UCSYShort target;
             UCSYShort exceptionClassIndex;
             UVMClass *exceptionClass;
};
typedef void (*NativeMethod)(ExecutionEnviroment* enviroment);

class UVMMethod
{
public:
       int methodModifier;
       char *methodName;
       char *methodSignature;
       UVMClass *parentClass; 
       u2 maxNoOfLocalArray; //no of local arrray in word
       u2 maxSizeOfOperandStack; // no of Operands stack size in word
       u2 argSize ;
       UCSYCode *methodCode;
       
       int noOfCatchEntries;
       CatchEntry **catchTables;
       //u2 lengthOfCode;
       //u1  *methodCode;      
       NativeMethod nativeMethodCode; 
       bool isNative();
       bool isRebindable();
       int vTableIndex; //To be used by interface call
       //TO add Native Method
       //***********************************************************************
};

class MethodFrame;
/********************************************************************************
ExecutionEnviroment is primarly used by NativeManager and native Method it is to 
communicate with UVM and C,C++ Method
*********************************************************************************/
class ExecutionEnviroment
{
public:
      UVMClass *currentClass;
      UVMMethod *currentMethod;
      ConstantPoolEntry **constantPool;
      UCSYValue *localVar;
      UCSYValue  *operandStack;
      int *topOfStack;
      MethodFrame *previousFrame;
      
      UCSYString* readStringData();
      UCSYInteger readIntegerData();
      UCSYLong    readLongData();
      UCSYDouble  readDoubleData();
      UCSYFloat   readFloatData();
      
};

class VirtualMethodTable;
class UCSYInterface;

bool isStatic(int a);
class UVMClass
{
//       friend UVMClassParser;
public:
       UCSYShort classModifier;
       char* className;
       int sizeOfInstanceVar;
       int sizeOfStaticVar;
       int constantPoolEntryCount;
       
       UVMClass *superClass;
       char *superClassName;
       
       int noOfInterfaces;
       UCSYInterface **interfaces;
       ConstantPoolEntry **constantPool;
       int noOfFields;
       UVMField **fields;
       int noOfMethods;
       UVMMethod **methods;
       
       
       
       u1 *staticData;

       
       int sizeOfVtable;
       UVMMethod **vtable;
       
       int sizeOfRebindableTable;
       UVMMethod **rebindableTable;
       
       int sizeOfAllInterfaces;
       UCSYInterface **allInterfaces;
        //Store interface table(vtable of interfaceClass)=> vtable 's index
       char *getSuperClassName()
       {
            return superClassName;
       }
       char *getClassName()
       {
            return className;
       }
       int getSizeOfStaticData();
       int getSizeOfInstanceVar();
       void calculateStaticFieldOffest();
       void calculateIntanceFieldOffest();
       void initializeClass();
       void constructVtable();
       
       UVMMethod *findStaticMethod(char *methodName,char *methodSignature);
       UVMMethod *findConstructorMethod(char *methodSignature);
       UVMMethod* findVirtualMethod(char *methodName,char *methodSignature);
       UVMMethod* findInterfaceMethod(char *methodName,char* methodSignature);
       UVMMethod* findRebindableMethod(char *methodName,char *methodSignature);//to be used by rebindable method
       UVMMethod* getMethod(char *methodName,char * methodSignature);//To be used by rebindale method 
       
       void getStaticField(UVMField *field,UCSYValue *operandStack,int &topOfOperandStack);
       void putStaticField(UVMField *field,UCSYValue *operandStack,int &topOfOperandStack);
       
       UVMField *findStaticField(char *fieldName,char *fieldType);
       UVMField *findInstanceField(char *fieldName,char *fieldType);
       UCSYInterface *getInterface(char *interfaceName);
       //Old version       to be used by UVM Interpreter
       void putField(u1 *fieldOffest,UVMField *field,UCSYValue *operandStack,int &topOfOperandStack);
       void getField(u1* data,UVMField *field,UCSYValue *operandStack,int &topOfOperandStack);
      
      //They are to be used by Native Method ;-) That night is so messy,due to a marco
       void putField(UVMObject *object,UVMField *field,UCSYValue *operandStack,int &topOfOperandStack);
       void getField(UVMObject *object,UVMField *field,UCSYValue *operandStack,int &topOfOperandStack);
       
      

       //For ArrayClass only
       int noOfDimension;
       UVMClass *element;
       
       bool isAncestorOrSameClassOf(UVMClass *aClass);
       bool isInstanceOf(UVMClass *checkClass);
       
       
};
class UCSYInterface
{
public:
      char *interfaceName;
      UVMClass *theInterface;
      UCSYInteger *itable;
      
};
//********************************* Interpreter and Execution Engine *************
class MethodFrame
{
public:
       int pc; //Program counter
       int topOfOperandStack;
       UCSYValue *localVar;
       UCSYValue *operandStack;
       UVMMethod *method;
       MethodFrame *previousFrame;
       ConstantPoolEntry **constantPool;
       UVMClass *theClass;
       
};//Missing semincolon make me create a lot of trouble :-P

class MethodCallStack;
class UVMObject
{
public:
       UVMObject()
       {
       }
       UVMClass *theClass;
       u1 *instanceData;
       UVMMethod** rebindableTable;
};

class UVMClassManager;
class LoadedClassItem;
class LoadedClassItem
{
public:
       char  *className;
       UVMClass *theClass;
       LoadedClassItem *next;
};

class UVMField
{
public:
       //UCSYShort indexOfFieldName;
       //UCSYShort indexOfFieldType;
       //UCSYByte  attribute;
       
       //************************ UVM Implementation Specific ***********************
       char *fieldName;
       char *fieldType;
       u2 fieldModifier;
       Offest offest;      
       
       int internalType; // InternalType used by UVM , beacuse string representation is too costly
       UVMClass *parentClass;  
           
       void putFieldInteger(UVMObject *object,UCSYInteger integerData);
       void putFieldByte(UVMObject *object,UCSYByte byteData);
       void putFieldShort(UVMObject *object,UCSYShort shortData);
       void putFieldLong(UVMObject *object,UCSYLong longData);
       void putFieldFloat(UVMObject *object,UCSYFloat floatData);
       void putFieldDouble(UVMObject *object,UCSYDouble doubleData);
       void putFieldRef(UVMObject *object,UVMObject *refValue);
       
       
       UCSYByte    getFieldByte(UVMObject *object);
       UCSYShort   getFieldShort(UVMObject *object);
       UCSYInteger getFieldInteger(UVMObject* object);
       UCSYLong    getFieldLong(UVMObject *object);
       UCSYFloat   getFieldFloat(UVMObject *object);
       UCSYDouble  getFieldDouble(UVMObject *object);
       UVMObject* getFieldRef(UVMObject *object);

};

extern void initializeNativeMethod();
UCSYShort readShortFromCode(u1 *code,int index);
int getDimensionOfArray(char *arrayName);

UCSYInteger getIntegerFromArray(UVMObject* arrayObject,int index);
UCSYByte    getByteFromArray(UVMObject* arrayObject,int index);
UCSYShort   getShortFromArray(UVMObject* arrayObject,int index);
UCSYLong    getLongFromArray(UVMObject* arrayObject,int index);
UCSYFloat   getFloatFromArray(UVMObject* arrayObject,int index);
UCSYDouble  getDoubleFromArray(UVMObject* arrayObject,int index);
UVMObject*  getRefFromArray(UVMObject* arrayObject,int index);


void setByteToArray(UVMObject *arrayObject,int index,UCSYByte byteValue);
void setShortToArray(UVMObject *arrayObject,int index,UCSYShort shortValue);
void setIntegerToArray(UVMObject *arrayObject,int index,UCSYInteger integerValue);
void setLongToArray(UVMObject *arrayObject,int index,UCSYLong longValue);
void setFloatToArray(UVMObject *arrayObject,int index,UCSYFloat floatValue);
void setDoubleToArray(UVMObject *arrayObject,int index,UCSYDouble doubleValue);
void setRefToArray(UVMObject *arrayObject,int index,UVMObject *refValue);


