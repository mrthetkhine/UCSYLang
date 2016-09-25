#ifndef uvmInstruction_h
#include "uvmInstruction.h"
#endif

#ifndef executionEngine_h
#include "executionEngine.h"
#endif

#ifndef uvm_h
#include "uvm.h"
#endif

#ifndef classManager_h
#include "classManger.h"
#endif

#ifndef methodCallStack_h
#include "methodCallStack.h"
#endif

#ifndef memoryManager_h
#include "memoryManager.h"
#endif

#ifndef classParser_h
#include "classParser.h"
#endif

#include <iostream>
using namespace std;
//Reading form OperandStack             
#define readTwoInteger()  integerOpTwo = operandStack[topOfOperandStack-- ].intValue;\
                    integerOpOne = operandStack[topOfOperandStack-- ].intValue;
                    
#define readTwoLong()   dataTwo[1] = operandStack[ topOfOperandStack -- ].otherHalf;\
              dataTwo[0] =  operandStack[ topOfOperandStack -- ].otherHalf;\
              longOpTwo = *(UCSYLong*)dataTwo;\
              dataTwo[1] = operandStack[ topOfOperandStack -- ].otherHalf;\
              dataTwo[0] =  operandStack[ topOfOperandStack -- ].otherHalf;\
              longOpOne = *(UCSYLong*)dataTwo;     
              
#define readTwoFloat() floatOpTwo = operandStack[topOfOperandStack-- ].floatValue;\
                       floatOpOne = operandStack[topOfOperandStack-- ].floatValue;

#define readTwoDouble() dataTwo[1] = operandStack[ topOfOperandStack -- ].otherHalf;\
              dataTwo[0] =  operandStack[ topOfOperandStack -- ].otherHalf;\
              doubleOpTwo = *(UCSYDouble*)dataTwo;\
              dataTwo[1] = operandStack[ topOfOperandStack -- ].otherHalf;\
              dataTwo[0] =  operandStack[ topOfOperandStack -- ].otherHalf;\
              doubleOpOne = *(UCSYDouble*)dataTwo;
              
#define readTwoString() strTwo = ((UCSYString*)(operandStack[topOfOperandStack--].reference))->stringData;\
                        strOne = ((UCSYString*)(operandStack[topOfOperandStack--].reference))->stringData;        
/*
bool isNative(int attribute)
{
     int NATIVE = 1 << 11;
     if( (attribute & NATIVE ) == NATIVE )
     {
         return true;
     }
     else
     {
         return false;
     }
}
*/
void ExecutionEngine::execute(UCSYClass *theClass,UVMMethod *theMethod)
{
       UVMClassManager *classManager    = UVMClassManager::getClassManager();       
       MethodCallStack *methodCallStack = MethodCallStack::getMethodCallStack();
       MemoryManager *memoryManager     = MemoryManager::getMemoryManager();
       
       UCSYClass *currentClass;
       UVMMethod *currentMethod;
       UCSYClass *classToInvoke;
       UCSYClass *newClass ; //To used in create Object
       UCSYClass *classOfObject; //To used in virtual call
       MethodFrame *newFrame;
       UVMMethod *methodToInvoke;
       ConstantPoolEntry** constantPool;
       UCSYValue* localVar;
       UCSYValue* operandStack ;
       int topOfOperandStack;
       u2 indexOfCPool;
       ConstantPoolEntry *cpEntry;
       
       //**************************** For miscalleneous ************************
       u4 dataTwo[2];
       //*********************** ConstantPoolEntry *****************************
       MethodRefEntry *methodRefEntry;
       ClassRefEntry  *classRefEntry;
       
       UCSYString  *stringData ;
       UCSYInteger integerData;
       UCSYFloat   floatData;
       UCSYDouble  doubleData;
       UCSYLong    longData;
       UCSYChar    charData; 
     
       //For arithmetic
       UCSYInteger integerOpOne,integerOpTwo,integerResult;
       UCSYFloat   floatOpOne, floatOpTwo,floatResult;
       UCSYLong    longOpOne, longOpTwo, longResult;
       UCSYDouble  doubleOpOne, doubleOpTwo, doubleResult;
       currentClass = theClass;
       currentMethod = theMethod;
       
       u1 * code = currentMethod->methodCode->code;

       u1 opcode ;
       int pc = 0;
       u2 constantPoolIndex;
       ///cout<<"Constant POOL "<<theClass->constantPool<<endl;
       
       MethodFrame * currentFrame = memoryManager->allocateMethodFrameForMethod(currentMethod,currentClass);
       operandStack = currentFrame->operandStack;
       constantPool = currentFrame->constantPool;
       currentFrame->method = currentMethod;
       ///cout<<"ConstantPool is "<<constantPool<<endl;
       localVar = currentFrame->localVar;
       topOfOperandStack = -1;
       ///cout<<"Size of operand stack "<<sizeof(operandStack)<<endl;
       //For parameter passing
       int tStack ;//to copy parameter
       UCSYObject *object ;//For virtual call
       MethodFrame *oldFrame;
       while( methodCallStack->currentFrame )
       {
              
              opcode = code[pc];
              ///cout<<" instruction "<<(int)opcode<<" "<<pc<<endl;
              switch(opcode)
              {
                     case LOAD_CPOOL :

                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          
                          ///cout<<"cpIndex "<<constantPoolIndex<<endl;                                    
                          cpEntry = constantPool[ constantPoolIndex ];
                          
                          //LOAD_CPOOL may refer to UCSYString, UCSYInteger,Long,Double,Float,REFERENCE
                          switch(cpEntry->tag)
                          {
                                case CPOOL_LONG_REF:
                                     longData = *(UCSYLong*)cpEntry->info;
                                     
                                     operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                                     operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                                break;
                                case CPOOL_DOUBLE_REF:
                                     doubleData = *(UCSYDouble*)cpEntry->info;
                
                                     *((double*)dataTwo) = doubleData;
                                     operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                                     operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                                break;
                                case CPOOL_STRING_REF:
                                     stringData = (UCSYString*)cpEntry->info;
                                     operandStack[ ++topOfOperandStack ].reference = stringData;
                                break;
                                case CPOOL_INTEGER_REF:
                                     operandStack[ ++topOfOperandStack ].intValue = (*(UCSYInteger *)cpEntry->info);
                                break;
                                case CPOOL_FLOAT_REF:
                                     operandStack[ ++topOfOperandStack ].floatValue = *(UCSYFloat*)cpEntry->info;
                                break;
                                default:
                                        cout<<"INVALID LOAD_CPOOL TAG";                   
                          }
                          
                          
                          ///cout<<"Ok load cpool "<<constantPoolIndex<<" Top of stack "<<topOfOperandStack-1<<endl;
                     break;       
                     case LOAD_LOCAL_REF:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          operandStack[ ++topOfOperandStack ].reference = localVar[ constantPoolIndex ].reference;
                          object = (UCSYObject*)operandStack[ topOfOperandStack-1].reference;
                          ///cout<<"LOAD "<<object<<endl;
                          pc += 2;
                          ///cout<<"Constant POOL LOAD_LOCAL_REF "<<constantPool<<endl;
                     break;
                     
                     case STORE_LOCAL_REF:
                          ///cout<<" store local pop "<<topOfOperandStack<<endl;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          localVar[ constantPoolIndex ].reference = operandStack[ topOfOperandStack --].reference;
                          object = (UCSYObject*)localVar[ constantPoolIndex ].reference;
                          ///cout<<"Store "<<object<<endl;
                          pc += 2;
                          ///cout<<"Constant POOL STORE_LOCAL_REF "<<constantPool<<endl;
                     break;
                     
                     case CREATE_OBJECT:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc +=2 ;
                          ///cout<<"Fine "<< constantPoolIndex<<" "<<constantPool<< endl ;
                          classRefEntry = (ClassRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          newClass = classManager->getAClass( classRefEntry->className );
                          object = memoryManager->allocateObject( newClass );
                          operandStack[ ++topOfOperandStack ].reference = object;
                          object = (UCSYObject*)operandStack[ topOfOperandStack ].reference;
                          ///cout<<"CREATE "<<object<<endl;
                     break;
                     
                     case CALL_CONSTRUCTOR:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          methodRefEntry = (MethodRefEntry*)((constantPool[ constantPoolIndex ])->info);
                          //cout<<" class Name "<<methodRefEntry->className<<endl;
                          classToInvoke = classManager->getAClass(methodRefEntry->className);
                          methodToInvoke = classToInvoke->findConstructorMethod( methodRefEntry->signature);
                          
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          //cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          oldFrame->topOfOperandStack= topOfOperandStack;
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          ///cout<<"Before call construc "<<topOfOperandStack<<endl;
                          tStack = (topOfOperandStack - methodToInvoke->argSize)+1;
                          
                          for(int i=0;i< methodToInvoke->argSize;i++)
                          {
                                  ///cout<<"Cons par pass "<<tStack<<endl ;
                                  newFrame->localVar[ i ] = operandStack[ tStack++ ];
                                  topOfOperandStack--;
                          }
                          
                          //Place returned Object into operandStack
                          ///cout<<"Push ref "<<newFrame->localVar[0].reference<<" at "<<topOfOperandStack+1<<endl;
                          operandStack[ ++topOfOperandStack  ].reference = newFrame->localVar[0].reference;
                          oldFrame->topOfOperandStack = topOfOperandStack;
                          
                          //Set new frame 
                          constantPool  = newFrame->constantPool;
                          localVar      = newFrame->localVar;
                          operandStack  = newFrame->operandStack;
                          topOfOperandStack    = 0;
                          currentClass  = classToInvoke;
                          currentMethod = methodToInvoke;
                          code          = currentMethod->methodCode->code;
                          pc            = 0;
                           
                          
                          if( methodToInvoke->isNative() )
                          {
                              //Call Native Method
                              ExecutionEnviroment *env = new ExecutionEnviroment();
                              env->constantPool = constantPool;
                              env->currentClass = classToInvoke;
                              env->currentMethod = methodToInvoke;
                              env->localVar = localVar;
                              env->operandStack  = operandStack;
                              env->topOfStack    = & topOfOperandStack;
                              env->previousFrame = oldFrame;                            
                              ///cout<<"Call native "<<endl; 
                              methodToInvoke->nativeMethodCode(env);
                              
                              
                              //Clean up native execution
                               ///methodCallStack->popAFrame();
                              
                              //Set current enviroment to previous state
                              oldFrame = methodCallStack->getCurrentFrame();
                             
                              constantPool = oldFrame->constantPool;
                              localVar     = oldFrame->localVar;
                              operandStack = oldFrame->operandStack;
                              currentClass = oldFrame->theClass;
                              currentMethod= oldFrame->method;
                              code         = currentMethod->methodCode->code;
                              pc           = oldFrame->pc;
                              topOfOperandStack = oldFrame->topOfOperandStack;
                              
                              //cout<<"Restoring pc at  "<<(int)pc <<" Frie "<<endl;
                          
                          }
                          else
                          {
                          
                              //This is Method written in UCSY language
                          
                              pc = -1;
                          }

                     break;
                     
                     case CALL_VIRTUAL:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          ///cout<<"Virtual call "<<constantPool<<" topOFStack "<<topOfOperandStack<<endl;
                          methodRefEntry = (MethodRefEntry*)((constantPool[ constantPoolIndex ])->info);
                          ///cout<<" VIRTUAL CONTEXT CLASS Name "<<currentClass->className<< " "<< constantPoolIndex<<" "<< methodRefEntry<<"" <<methodRefEntry->className<<endl;
                          object = (UCSYObject*)operandStack[ topOfOperandStack ].reference;
                          ///cout<<"ref in virtual call "<<object<<" object "<<object->theClass<< endl;
                          classOfObject = object->theClass;
                          
                          
                          ///cout<<"Actual Call to  "<<classOfObject->className<<endl;
                          //classToInvoke = classManager->getAClass(classOfObject->className);
                          methodToInvoke = classOfObject->findVirtualMethod(methodRefEntry->methodName,methodRefEntry->signature);
                          classToInvoke  = methodToInvoke->parentClass;
                          ///cout<<"Acatual call to "<<classToInvoke->className<<endl;
                          
                          
                          //Save Old Frame
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          //cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          oldFrame->topOfOperandStack= topOfOperandStack;
                          oldFrame->constantPool = constantPool;
                          oldFrame->theClass = currentClass;
                          ///oldFrame->method   = currentMethod;
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          ///cout<<"Before virtual call topOfStack is "<<topOfOperandStack<<endl;
                          tStack = (topOfOperandStack - methodToInvoke->argSize)+1;

                          for(int i=0;i< methodToInvoke->argSize;i++)
                          {
                                  ///cout<<"virtual call par pass "<<operandStack[ tStack ].reference<<" "<<tStack<<endl;
                                  newFrame->localVar[ i ] = operandStack[ tStack++ ];
                                  oldFrame->topOfOperandStack--;
                          }
                          
                          //Set new frame 
                          constantPool  = newFrame->constantPool;
                          localVar      = newFrame->localVar;
                          operandStack  = newFrame->operandStack;
                          topOfOperandStack    = 0;
                          currentClass  = classToInvoke;
                          currentMethod = methodToInvoke;
                          code          = currentMethod->methodCode->code;
                          pc            = 0;
                           
                          
                          if( methodToInvoke->isNative() )
                          {
                              //Call Native Method
                              ExecutionEnviroment *env = new ExecutionEnviroment();
                              env->constantPool = constantPool;
                              env->currentClass = classToInvoke;
                              env->currentMethod = methodToInvoke;
                              env->localVar = localVar;
                              env->operandStack  = operandStack;
                              env->topOfStack    = & topOfOperandStack;
                              env->previousFrame = oldFrame;                            
                              ///cout<<"Call native "<<endl; 
                              methodToInvoke->nativeMethodCode(env);
                              
                              
                              //Clean up native execution
                              /// methodCallStack->popAFrame();
                              
                              //Set current enviroment to previous state
                              oldFrame = methodCallStack->getCurrentFrame();
                            
                              constantPool = oldFrame->constantPool;
                              localVar     = oldFrame->localVar;
                              operandStack = oldFrame->operandStack;
                              currentClass = oldFrame->theClass;
                              currentMethod= oldFrame->method;
                              code         = currentMethod->methodCode->code;
                              pc           = oldFrame->pc;
                              topOfOperandStack = oldFrame->topOfOperandStack;
                              
                              ///cout<<"Restoring pc at  "<<(int)pc <<" Frie "<<endl;
                          
                          }
                          else
                          {
                          
                              //This is Method written in UCSY language
                          
                              pc = -1;
                          }
                          
                     break;
                     
                     case CALL_STATIC :
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          methodRefEntry = (MethodRefEntry*)((constantPool[ constantPoolIndex ])->info);
                          ///cout<<" class Name "<<currentClass->className<< " "<< constantPoolIndex<<" "<< methodRefEntry<<"" <<methodRefEntry->className<<endl;
                          classToInvoke = classManager->getAClass(methodRefEntry->className);
                          ///cout<<"Fine here "<<endl;
                          methodToInvoke = classToInvoke->findStaticMethod(methodRefEntry->methodName,methodRefEntry->signature);
                          
                          
                          
                          //Save Old Frame
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          ///cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          oldFrame->topOfOperandStack= topOfOperandStack;
                          oldFrame->constantPool = constantPool;
                          oldFrame->theClass = currentClass;
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          
                          tStack = (topOfOperandStack - methodToInvoke->argSize)+1;

                          for(int i=0;i< methodToInvoke->argSize;i++)
                          {
                          
                                  newFrame->localVar[ i ] = operandStack[ tStack++ ];
                                  oldFrame->topOfOperandStack--;
                          }
                          
                          //Set new frame 
                          constantPool  = newFrame->constantPool;
                          localVar      = newFrame->localVar;
                          operandStack  = newFrame->operandStack;
                          topOfOperandStack    = 0;
                          currentClass  = classToInvoke;
                          currentMethod = methodToInvoke;
                          code          = currentMethod->methodCode->code;
                          pc            = 0;
                           
                          
                          if( methodToInvoke->isNative() )
                          {
                              //Call Native Method
                              ExecutionEnviroment *env = new ExecutionEnviroment();
                              env->constantPool = constantPool;
                              env->currentClass = classToInvoke;
                              env->currentMethod = methodToInvoke;
                              env->localVar = localVar;
                              env->operandStack  = operandStack;
                              env->topOfStack    = & topOfOperandStack;
                              env->previousFrame = oldFrame;                            
                              ///cout<<"Call native "<<endl; 
                              methodToInvoke->nativeMethodCode(env);
                              
                              
                              //Clean up native execution
                              
                              ///methodCallStack->popAFrame();
                              //Set current enviroment to previous state
                              oldFrame = methodCallStack->getCurrentFrame();
                              

                              constantPool = oldFrame->constantPool;
                              localVar     = oldFrame->localVar;
                              
                              operandStack = oldFrame->operandStack;
                              
                              currentClass = oldFrame->theClass;
                              ///cout<<"Native return "<<currentClass->className<<endl;
                              currentMethod = oldFrame->method;
                              ///cout<<"current Method name "<<oldFrame->method->methodName<<" current method sig "<<currentMethod->methodSignature<<endl;
                              code         = currentMethod->methodCode->code;
                              ///cout<<"Fine "<<endl;
                              pc           = oldFrame->pc;
                              topOfOperandStack = oldFrame->topOfOperandStack;
                              
                              
                              ///cout<<"Restoring static pc at native "<<(int)pc <<" Frie "<<endl;
                          
                          }
                          else
                          {
                          
                              //This is Method written in UCSY language
                          
                              pc = -1;
                          }
                          
                     break;
                     case RETURN:
                          ///oldFrame = methodCallStack->getCurrentFrame();
                          ///cout<<"Return F"<< oldFrame->constantPool<<endl;
                          methodCallStack->popAFrame();
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          if(oldFrame != NULL )
                          {
                                      
                                      
                                      localVar     = oldFrame->localVar;
                                      operandStack = oldFrame->operandStack;
                                      currentClass = oldFrame->theClass;
                                      constantPool = currentClass->constantPool;
                                      currentMethod= oldFrame->method;
                                      code         = currentMethod->methodCode->code;
                                      pc           = oldFrame->pc;
                                      topOfOperandStack = oldFrame->topOfOperandStack;
                                      
                                      
                          }
                          
                          ///cout<<"Resume at pc "<<pc<< "  "<<topOfOperandStack<<endl;
                     break;
                     default:
                             cout<<"Byte Code Not Implemented ";
              }
              //cout<<"That is amazing "<<pc<<endl;
              pc++;
       }
}
