/*******************************************************************************
          Interpreter or ExecutionEngine                  
********************************************************************************/
#define executionEngine_h
#ifndef uvm_h
#include "uvm.h"
#endif
class ExecutionEngine
{
      char *startingClass;
      public:
             
      
      void execute(UVMClass *currentClass, UVMMethod *currentMethod);
};
