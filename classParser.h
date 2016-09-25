#define classParser_h
#ifndef uvm_h
#include "uvm.h"
#endif
class UVMClassParser
{
     u1 OneByte[2];
     u1 TwoByte[2];
     u1 FourByte[4];
     u1 EightByte[8];

     u1 oneByte[2];
     u1 twoByte[2];
     u1 fourByte[4];
     u1 eightByte[8];
 
     static UVMClassParser *parser;
     UVMClassParser()
     {
     }
      public:
             static UVMClassParser* getClassParser();
             ConstantPoolEntry** parseConstantPool(FILE *classFile,UVMClass *theClass);
             
             UCSYInterface** parseInterfaces(FILE *classFile, UVMClass* theClass);
             UVMField** parseFields(FILE *classFile,UVMClass *theClass);
             UVMMethod** parseMethods(FILE *classFile,UVMClass *theClass);
             
             UVMClass* parseTheClass(char* className);
             
};
