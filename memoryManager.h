#define memoryManager_h
#ifndef uvm_h
#include "uvm.h"
#endif

class MemoryManager
{
      static MemoryManager* memoryManager;
      MemoryManager();
public:
      static MemoryManager* getMemoryManager();
      MethodFrame* allocateMethodFrameForMethod(UVMMethod *method,UVMClass *theClass);
      UVMObject * allocateObject(UVMClass *theClass);
      UVMObject * allocateArray(UVMClass *theClass,UCSYValue *operandStack,int &topOfOperandStack);
      UVMObject* allocateRefArray(int size);
      UVMObject* allocatePrimitiveArray(int primitiveType, int size); 
      UVMObject* allocateArray(UVMClass *theClass,UCSYValue *operandStack,int dimension,char *arrayType,int topOfStack);
      void*        allocateStaticData(UVMClass *theClass);
};

