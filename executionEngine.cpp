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
#include <conio.h>
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
                        
#define popLong()    twoWordL[1] = operandStack[ topOfOperandStack--].otherHalf;\
                        twoWordL[0] = operandStack[ topOfOperandStack--].otherHalf;\
                        longOpOne = *(UCSYLong*)twoWordL; 

u4 twoWordL[2];                    
/* The following marco are used for native method call and return to simplify 
   execution engine code
   nativeCallAndReturn() call a native method and return from native method
   any return value are puhsed by native method itself
*/
#define nativeCallAndReturn() ExecutionEnviroment *env = new ExecutionEnviroment();\
                              env->constantPool = constantPool;\
                              env->currentClass = classToInvoke;\
                              env->currentMethod = methodToInvoke;\
                              env->localVar = localVar;\
                              env->operandStack  = operandStack;\
                              env->topOfStack    = &topOfOperandStack;\
                              env->previousFrame = oldFrame;\
                              methodToInvoke->nativeMethodCode(env);\
                              oldFrame = methodCallStack->getCurrentFrame();\
                              constantPool = oldFrame->constantPool;\
                              localVar     = oldFrame->localVar;\
                              operandStack = oldFrame->operandStack;\
                              currentClass = oldFrame->theClass;\
                              currentMethod= oldFrame->method;\
                              code         = currentMethod->methodCode->code;\
                              pc           = oldFrame->pc;\
                              topOfOperandStack = oldFrame->topOfOperandStack;\
  
/********************************************************************************
 This marco change to execution enviroment to invoke a method
 Execution enviroment consists of 
 ConstantPool
 LocalVar
 topOfOperandStack
 PC
 CurrentClass
 CurrentMethod
 
 It is used to transefer control to the invoking method
*********************************************************************************/
 #define changeExecutionEnviroment()  constantPool  = newFrame->constantPool;\
                          localVar      = newFrame->localVar;\
                          operandStack  = newFrame->operandStack;\
                          topOfOperandStack = -1;\
                          currentClass  = classToInvoke;\
                          currentMethod = methodToInvoke;\
                          code          = currentMethod->methodCode->code;\
                          pc            = 0;
                          
/* This macro pass parameter between two method */
#define passParameter() tStack = (topOfOperandStack - methodToInvoke->argSize)+1;\
                          for(int i=0;i< methodToInvoke->argSize;i++)\
                          {newFrame->localVar[ i ] = operandStack[ tStack++ ];\
                            topOfOperandStack--;}                        
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

//LONG and Double are store in Little endian order 
//Fist byte is pushed into opernadStack
//Then second byte is pushed
void ExecutionEngine::execute(UVMClass *theClass,UVMMethod *theMethod)
{
       UVMClassManager *classManager    = UVMClassManager::getClassManager();       
       MethodCallStack *methodCallStack = MethodCallStack::getMethodCallStack();
       MemoryManager *memoryManager     = MemoryManager::getMemoryManager();
       
       UVMClass *currentClass;
       UVMMethod *currentMethod;
       UVMClass *classToInvoke;
       UVMClass *newClass ; //To used in create Object
       UVMClass *classOfObject; //To used in virtual call
       UVMClass *classOfField; //To used in static instance getField PutField
       UCSYInterface *aInterface ; //To be used by interace call
       int indexOfVtable ;//To be used by interface call
       MethodFrame *newFrame;
       UVMMethod *methodToInvoke;
       int targetMethodIndex, rebindMethodIndex ; //To be used by Rebind
       UVMMethod *targetMethod,*rebindMethod;
       UVMField  *theField ;//To be used by getAndPut Field

//     Execution Enviroment
       ConstantPoolEntry** constantPool;
       UCSYValue* localVar;
       UCSYValue* operandStack ;
       int topOfOperandStack;
       u2 indexOfCPool;
       ConstantPoolEntry *cpEntry;
       
       //**************************** For miscalleneous ************************
       u4 dataTwo[2];
       //*********************** ConstantPoolEntry *****************************
       MethodRefEntry *methodRefEntry,*methodRefEntryTwo;
       InterfaceMethodRefEntry *interfaceMethodRefEntry;
       ClassRefEntry  *classRefEntry;
       FieldRefEntry  *fieldRefEntry;
       UCSYString  *stringData ;
       UCSYInteger integerData;
       UCSYFloat   floatData;
       UCSYDouble  doubleData;
       UCSYLong    longData;
       UCSYChar    charData; 
       UCSYByte    byteData;
       //For ArrayProcessing
       UCSYInteger arrayIndex;
       UVMObject *referenceData;
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
       u2 localVarIndex;
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
       UVMObject *object ;//For virtual call
       
       UVMObject *exceptionObject ;//for ThrowException
       UVMClass  *exceptionClass;
       UVMClass  *catchClass;
       CatchEntry **catchTable;
       CatchEntry *aCatchEntry;
       int locationOfObject ;//For Virtual Call to calculate object position on operandStack
       MethodFrame *oldFrame;
       
       MethodFrame *retFrame;//For return of various type INT_RETURN etc
       int retTopOfStack; 
       //For Arithmetic 
       int divisor;
       while( methodCallStack->currentFrame )
       {
              
              opcode = code[pc];
              //cout<<" instruction "<<(int)opcode<<" at pc "<<pc<<endl;
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
                                     *((UCSYLong*)dataTwo  )= longData;
                                     
                                     operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[0];
                                     operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                                     
                                break;
                                case CPOOL_DOUBLE_REF:
                                     doubleData = *(UCSYDouble*)cpEntry->info;
                
                                     *((UCSYDouble*)dataTwo) = doubleData;
                                     operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[0];
                                     operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                                     
                                break;
                                case CPOOL_STRING_REF:
                                     stringData = (UCSYString*)cpEntry->info;
                                     
                                     operandStack[ ++topOfOperandStack ].reference = stringData;
                                     //cout<<"ConstantPool Load "<<operandStack[ topOfOperandStack ].reference<<endl;
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
                          
                          
                          ///cout<<"Ok load cpool "<<constantPoolIndex<<" Top of stack "<<topOfOperandStack<<endl;
                     break;       
                     case LOAD_LOCAL_REF:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          operandStack[ ++topOfOperandStack ].reference = localVar[ constantPoolIndex ].reference;
                          ///object = (UVMObject*)operandStack[ topOfOperandStack].reference;
                          ///cout<<"LOAD object "<<object<<endl;
                          pc += 2;
                          ///cout<<"Constant POOL LOAD_LOCAL_REF "<<constantPool<<endl;
                     break;
                
                     case STORE_LOCAL_REF:
                          ///cout<<" store local pop "<<topOfOperandStack<<endl;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          localVar[ constantPoolIndex ].reference = operandStack[ topOfOperandStack --].reference;
                          ///object = (UVMObject*)localVar[ constantPoolIndex ].reference;
                          ///cout<<"Store "<<object<<endl;
                          pc += 2;
                          ///cout<<"Constant POOL STORE_LOCAL_REF "<<constantPool<<endl;
                     break;
                     case LOAD_LOCAL_INT:
                          localVarIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          ///cout<<"LOAD INT "<<&localVar[ localVarIndex ]<<"top of stack "<<topOfOperandStack<<" local var index"<<localVarIndex<<endl;
                          operandStack[ ++topOfOperandStack ].intValue = localVar[ localVarIndex ].intValue;
                          
                     break;
                     
                     case STORE_LOCAL_INT:
                          localVarIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          ///cout<<"INT VALuE "<< operandStack[topOfOperandStack].intValue<<endl;
                          localVar[ localVarIndex ].intValue = operandStack[ topOfOperandStack --].intValue ;
                          
                     break;
                     
                     case LOAD_LOCAL_FLOAT:
                          localVarIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          operandStack[ ++topOfOperandStack ].floatValue = localVar[ localVarIndex ].floatValue;
                          
                     break;
                     
                     case STORE_LOCAL_FLOAT:
                          localVarIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          ///cout<<"Store local float "<<operandStack[ topOfOperandStack ].floatValue<<endl;
                          localVar[ localVarIndex ].floatValue = operandStack[ topOfOperandStack --].floatValue ;
                          
                     break;
                     case LOAD_LOCAL_LONG:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          operandStack[ ++topOfOperandStack ].otherHalf = localVar[ constantPoolIndex ].otherHalf;
                          operandStack[ ++topOfOperandStack ].otherHalf = localVar[ constantPoolIndex+1].otherHalf;
                          dataTwo[0] = localVar[ constantPoolIndex ].otherHalf;
                          dataTwo[1] = localVar[ constantPoolIndex+1].otherHalf;
                          //cout<<"LOad loacal LOng value "<<*(UCSYLong*)dataTwo<<endl;
                     break;
                     case LOAD_LOCAL_DOUBLE:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          operandStack[ ++topOfOperandStack ].otherHalf = localVar[ constantPoolIndex ].otherHalf;
                          operandStack[ ++topOfOperandStack ].otherHalf = localVar[ constantPoolIndex+1].otherHalf;
                     break;
                     case STORE_LOCAL_LONG:
                          localVarIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          localVar[localVarIndex +1  ].otherHalf  =  operandStack[topOfOperandStack-- ].otherHalf;
                          localVar[localVarIndex     ].otherHalf  =  operandStack[topOfOperandStack-- ].otherHalf;
                     break;
                     case STORE_LOCAL_DOUBLE:
                          localVarIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          ///cout<<"ope : 0 "<<operandStack[topOfOperandStack].otherHalf<<" 1: "<<operandStack[ topOfOperandStack-1].otherHalf<<endl;
                          localVar[localVarIndex +1  ].otherHalf  =  operandStack[topOfOperandStack-- ].otherHalf;
                          localVar[localVarIndex     ].otherHalf  =  operandStack[topOfOperandStack-- ].otherHalf;
                          //cout<<"STORE LONG "<<longOpOne<<endl;
                          //getch();
                     break;
                     case CREATE_OBJECT:
                          /* Create Object must place resulting object on the stack */
                          ///cout<<"Creating Object "<<endl;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc +=2 ;
                          ///cout<<"Fine "<< constantPoolIndex<<" "<<constantPool<< endl ;
                          classRefEntry = (ClassRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          newClass = classManager->getAClass( classRefEntry->className );
                          object = memoryManager->allocateObject( newClass );
                          operandStack[ ++topOfOperandStack ].reference = object;
                          ///object = (UVMObject*)operandStack[ topOfOperandStack ].reference;
                     break;
                     
                     case CALL_CONSTRUCTOR:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          methodRefEntry = (MethodRefEntry*)((constantPool[ constantPoolIndex ])->info);
                          //cout<<" class Name "<<methodRefEntry->className<<endl;
                          classToInvoke = classManager->getAClass(methodRefEntry->className);
                          methodToInvoke = classToInvoke->findConstructorMethod( methodRefEntry->signature);

                          locationOfObject = topOfOperandStack -methodToInvoke->argSize + 1;
                          //Save Old Frame
                          oldFrame = methodCallStack->getCurrentFrame();
                          //cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          
                          passParameter();
                          
                          //Push return value of Construct(that is reference in operandStack)
                          oldFrame->topOfOperandStack = locationOfObject;
                          ///cout<<"After constructor call "<<oldFrame->topOfOperandStack<<endl;                        
                          changeExecutionEnviroment();
                          //cout<<"Parameter passing "<<endl;
                          if( methodToInvoke->isNative() )
                          {
                              nativeCallAndReturn();
                          }
                          else
                          {
                              //This is Method written in UCSY language
                              pc = -1;
                          }
                          
                     break;

/* How Virtual Call Operate
   1. Fetch theClass of reference on the Stack
   2. Fetch the Vtable of theClass
   3. Lookup the desired virtual method in vTable of theClass
   4. Tranfer Control to the Actual Class of method acquired by stage 3
*/                     
                     case CALL_VIRTUAL:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          methodRefEntry    = (MethodRefEntry*)(constantPool[ constantPoolIndex])->info;
                          //Calculate location of object in operand stack
                          classToInvoke     = classManager->getAClass(methodRefEntry->className);
                          methodToInvoke    = classToInvoke->findVirtualMethod( methodRefEntry->methodName,methodRefEntry->signature);
                          locationOfObject = topOfOperandStack -methodToInvoke->argSize + 1;
                          object = (UVMObject*)operandStack[ locationOfObject ].reference;
                          ///cout<<"Virtual object "<<object<<endl;
                          
                          classOfObject = object->theClass;
                          methodToInvoke = classOfObject->findVirtualMethod(methodRefEntry->methodName,methodRefEntry->signature);
                          classToInvoke  = methodToInvoke->parentClass;
                          
                          //Save Old Frame
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          //cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          //oldFrame->topOfOperandStack= topOfOperandStack;
                          
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          
                          passParameter();
                          //Add this line
                          oldFrame->topOfOperandStack= locationOfObject-1;                        
                          changeExecutionEnviroment();
                          
                          if( methodToInvoke->isNative() )
                          {
                              nativeCallAndReturn();
                          }
                          else
                          {
                              //This is Method written in UCSY language
                              pc = -1;
                          }
                          
                     break;
                     case CALL_REBINDABLE:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          methodRefEntry    = (MethodRefEntry*)(constantPool[ constantPoolIndex])->info;
                          //Calculate location of object in operand stack
                          classToInvoke     = classManager->getAClass(methodRefEntry->className);
                          ///cout<<"Class To call rebindable "<<classToInvoke->className<<endl;
                          methodToInvoke    = classToInvoke->findRebindableMethod( methodRefEntry->methodName,methodRefEntry->signature);
                          locationOfObject  = topOfOperandStack -methodToInvoke->argSize + 1;

                          object = (UVMObject*)operandStack[ locationOfObject ].reference;
                          ///cout<<"Virtual object "<<object<<endl;
                          
                          classOfObject = object->theClass;
                          methodToInvoke = object->rebindableTable[methodToInvoke->vTableIndex ];
                          classToInvoke  = methodToInvoke->parentClass;
                          
                          //Save Old Frame
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          //cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          //oldFrame->topOfOperandStack= topOfOperandStack;
                          
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          
                          passParameter();
                          //Add this line
                          oldFrame->topOfOperandStack= locationOfObject-1;                        
                          changeExecutionEnviroment();
                          
                          if( methodToInvoke->isNative() )
                          {
                              nativeCallAndReturn();
                          }
                          else
                          {
                              //This is Method written in UCSY language
                              pc = -1;
                          }
                          
                     break;
                     case CALL_INTERFACE:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          interfaceMethodRefEntry    = (InterfaceMethodRefEntry*)(constantPool[ constantPoolIndex])->info;
                          
                          //Calculate location of object in operand stack
                          classToInvoke     = classManager->getAClass(interfaceMethodRefEntry->className);
                          //Actually finding index of vtable of interface
                          methodToInvoke    = classToInvoke->findInterfaceMethod( interfaceMethodRefEntry->methodName,interfaceMethodRefEntry->signature);
                          locationOfObject = topOfOperandStack -methodToInvoke->argSize + 1;
                          
                          object = (UVMObject*)operandStack[ locationOfObject ].reference;
                          ///cout<<"Virtual object "<<object<<endl;
                          
                          classOfObject = object->theClass;
                          /*aInterface = classOfObject->getInterface(interfaceMethodRefEntry->className);
                          
                          indexOfVtable = aInterface->itable[ methodToInvoke->vTableIndex ];
                          cout<<"Fine at this stage "<<aInterface->interfaceName<<endl;
                          methodToInvoke = classOfObject->vtable[indexOfVtable];
                          */
                          methodToInvoke = classOfObject->findVirtualMethod(interfaceMethodRefEntry->methodName,interfaceMethodRefEntry->signature);
                          classToInvoke  = methodToInvoke->parentClass;
                          
                          //Save Old Frame
                          oldFrame = methodCallStack->getCurrentFrame();
                          
                          //cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          oldFrame->topOfOperandStack= topOfOperandStack;
                          
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          
                          passParameter();
                                                  
                          changeExecutionEnviroment();
                          
                          if( methodToInvoke->isNative() )
                          {
                              nativeCallAndReturn();
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
                          classToInvoke = classManager->getAClass(methodRefEntry->className);
                          methodToInvoke = classToInvoke->findStaticMethod(methodRefEntry->methodName,methodRefEntry->signature);
                          //Save Old Frame
                          
                          oldFrame = methodCallStack->getCurrentFrame();
                                 
                          ///cout<<"Save pc "<<(int)pc<<endl;
                          oldFrame->pc = pc;
                          
                          ///cout<<"Method to invoke "<<methodToInvoke->methodName<<" class to invoke "<<classToInvoke->className <<endl ;
                          newFrame = memoryManager->allocateMethodFrameForMethod(methodToInvoke,classToInvoke);
                          //Pass Parameter 
                          passParameter();
                          oldFrame->topOfOperandStack= topOfOperandStack;                       
                          changeExecutionEnviroment();
                          if( methodToInvoke->isNative() )
                          {
                              /*
                                It is the responisbility of the native method to 
                                place return value on old stack 
                              */
                              
                              nativeCallAndReturn();
                          }
                          else
                          {
                              //This is Method written in UCSY language
                              pc = -1;
                          }
                          
                     break;
                      
                     case GET_STATIC_FIELD:
                          ///cout<<"GET STATIC FIELD "<<endl;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;

                          fieldRefEntry = (FieldRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          classOfField  = classManager->getAClass( fieldRefEntry->className );
                          theField      = classOfField->findStaticField( fieldRefEntry->fieldName,fieldRefEntry->fieldType);
                          classOfField->getField(classOfField->staticData,theField,operandStack,topOfOperandStack); 
                     break;
                     
                     case PUT_STATIC_FIELD:
                          ////cout<<"PUT STATIC FIELD "<<endl;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          fieldRefEntry = (FieldRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          classOfField  = classManager->getAClass( fieldRefEntry->className );
                          theField      = classOfField->findStaticField( fieldRefEntry->fieldName,fieldRefEntry->fieldType);

                          classOfField->putField(classOfField->staticData,theField,operandStack,topOfOperandStack); 
                     break;
                     
                     case GET_INSTANCE_FIELD:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          object        = (UVMObject*)operandStack[ topOfOperandStack --].reference;
                         /// cout<<"Object "<<object<<endl;
                          fieldRefEntry = (FieldRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          classOfField  = classManager->getAClass( fieldRefEntry->className );
                          theField      = classOfField->findInstanceField( fieldRefEntry->fieldName,fieldRefEntry->fieldType);
                          ///cout<<"GET FIELD "<< operandStack[topOfOperandStack].reference<<endl;
                          
                          classOfField->getField(object->instanceData,theField,operandStack,topOfOperandStack); 
                          
                     break;
                     
                     case PUT_INSTANCE_FIELD:
                          ///cout<<"We are here"<<endl;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          object        = (UVMObject*)operandStack[ topOfOperandStack --].reference;
                          ///cout<<"Object PUt instance "<<object<<endl;
                          fieldRefEntry = (FieldRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          classOfField  = classManager->getAClass( fieldRefEntry->className );
                          theField      = classOfField->findInstanceField( fieldRefEntry->fieldName,fieldRefEntry->fieldType);
                          ///cout<<"PUT FIELD "<<operandStack[topOfOperandStack].reference<<endl;
                          
                          classOfField->putField( object->instanceData,theField,operandStack,topOfOperandStack);

                     break;

                     case RETURN:
                          
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
                          //PUSH reutrn value on stack
                          
                     break;
                     
                     case INT_RETURN:
                          retFrame = methodCallStack->getCurrentFrame();
                          methodCallStack->popAFrame();
                          oldFrame = methodCallStack->getCurrentFrame();
                          retTopOfStack = topOfOperandStack;
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
                          //cout<<"RetValue "<<retFrame->operandStack[retTopOfStack].intValue<<endl;
                          operandStack[++topOfOperandStack].intValue = retFrame->operandStack[retTopOfStack--].intValue;
                          //topOfOperandStack+=2;
                     break;
                      case FLOAT_RETURN:
                          retFrame = methodCallStack->getCurrentFrame();
                          methodCallStack->popAFrame();
                          oldFrame = methodCallStack->getCurrentFrame();
                          retTopOfStack = topOfOperandStack;
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
                          operandStack[++topOfOperandStack].floatValue = retFrame->operandStack[retTopOfStack--].floatValue;
                     break;
                     case REF_RETURN:
                          retFrame = methodCallStack->getCurrentFrame();
                          methodCallStack->popAFrame();
                          oldFrame = methodCallStack->getCurrentFrame();
                          retTopOfStack = topOfOperandStack;
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
                          operandStack[++topOfOperandStack].reference = retFrame->operandStack[retTopOfStack--].reference;
                     break;
                     case LONG_RETURN:
                     case DOUBLE_RETURN:     
                          retFrame = methodCallStack->getCurrentFrame();
                          methodCallStack->popAFrame();
                          oldFrame = methodCallStack->getCurrentFrame();
                          retTopOfStack = topOfOperandStack;
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
                          //cout<<"value of top S "<<retTopOfStack<<endl;
                          operandStack[++topOfOperandStack].otherHalf = retFrame->operandStack[retTopOfStack-1].otherHalf;
                          operandStack[++topOfOperandStack].otherHalf = retFrame->operandStack[retTopOfStack].otherHalf;
                     break;

                     //Arithmetic
                     case ADD_INT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue + operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case ADD_LONG:
                          readTwoLong();
                          
                          longResult = longOpOne + longOpTwo;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case ADD_FLOAT:
                          //cout<<" Adding Float "<<operandStack[topOfOperandStack-1].floatValue<<" "<<operandStack[ topOfOperandStack].floatValue<<endl;
                          operandStack[ topOfOperandStack-1].floatValue = operandStack[topOfOperandStack-1].floatValue + operandStack[topOfOperandStack].floatValue;
                          topOfOperandStack --; 
                     break;
                     
                     case ADD_DOUBLE:
                          readTwoDouble();
                          doubleResult = doubleOpOne + doubleOpTwo;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case SUB_INT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue - operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case SUB_LONG:
                          readTwoLong();
                          longResult = longOpOne - longOpTwo;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case SUB_FLOAT:
                          operandStack[ topOfOperandStack-1].floatValue = operandStack[topOfOperandStack-1].floatValue - operandStack[topOfOperandStack].floatValue;
                          topOfOperandStack --; 
                     break;
                     
                     case SUB_DOUBLE:
                          readTwoDouble();
                          doubleResult = doubleOpOne - doubleOpTwo;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case MULT_INT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue * operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case MULT_LONG:
                          readTwoLong();
                          longResult = longOpOne * longOpTwo;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];          
                     break;
                     
                     case MULT_FLOAT:
                          operandStack[ topOfOperandStack-1].floatValue = operandStack[topOfOperandStack-1].floatValue * operandStack[topOfOperandStack].floatValue;
                          topOfOperandStack --; 
                     break;
                     
                     case MULT_DOUBLE:
                          readTwoDouble();
                          doubleResult = doubleOpOne * doubleOpTwo;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];            
                     break;
                     
                     case DIV_INT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue / operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case DIV_LONG:
                          readTwoLong();
                          longResult = longOpOne / longOpTwo;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case DIV_FLOAT:
                          operandStack[ topOfOperandStack-1].floatValue = operandStack[topOfOperandStack-1].floatValue / operandStack[topOfOperandStack].floatValue;
                          topOfOperandStack --; 
                     break;
                     
                     case DIV_DOUBLE:
                          readTwoDouble();
                          doubleResult = doubleOpOne / doubleOpTwo;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case MOD_INT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue % operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case MOD_LONG:
                          readTwoLong();
                          longResult = longOpOne % longOpTwo;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case MOD_FLOAT:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          divisor =(int) (floatOpOne / floatOpTwo);
                          floatResult = floatOpOne -( divisor * floatOpTwo);
                          operandStack[ ++topOfOperandStack].floatValue = floatResult;

                     break;
                     
                     case MOD_DOUBLE:
                          readTwoDouble();
                          divisor =(int) (doubleOpOne / doubleOpTwo);
                          doubleResult = doubleOpOne -( divisor * doubleOpTwo);
                          
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     //Relational 
                     // Equal
                     case INT_EQUAL:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue == operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case LONG_EQUAL:
                          readTwoLong();
                          integerResult = longOpOne == longOpTwo;
                          
                          operandStack[ ++topOfOperandStack ].intValue = integerResult;
                     break;
                     
                     case FLOAT_EQUAL:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          integerResult = floatOpOne == floatOpTwo;
                          
                          operandStack[ ++topOfOperandStack].intValue = integerResult;

                     break;
                     
                     case DOUBLE_EQUAL:
                          readTwoDouble();
                          integerResult = doubleOpOne == doubleOpTwo;
                          operandStack[ ++topOfOperandStack].intValue = integerResult;                          
                     break;
                     
                     //NOt equal
                     case INT_NOT_EQUAL:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue != operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case LONG_NOT_EQUAL:
                          readTwoLong();
                          integerResult = longOpOne != longOpTwo;
                          operandStack[ ++topOfOperandStack ].intValue = integerResult;
                     break;
                     
                     case FLOAT_NOT_EQUAL:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          integerResult = floatOpOne != floatOpTwo;
                          
                          operandStack[ ++topOfOperandStack].intValue = integerResult;

                     break;
                     
                     case DOUBLE_NOT_EQUAL:
                          readTwoDouble();
                          integerResult = doubleOpOne != doubleOpTwo;
                          operandStack[ ++topOfOperandStack].intValue = integerResult;                          
                     break;

                     //GT Equal
                     case INT_GT_EQUAL:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue >= operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case LONG_GT_EQUAL:
                          readTwoLong();
                          integerResult = longOpOne >= longOpTwo;
                          operandStack[ ++topOfOperandStack ].intValue = integerResult;
                     break;
                     
                     case FLOAT_GT_EQUAL:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          integerResult = floatOpOne >= floatOpTwo;
                          
                          operandStack[ ++topOfOperandStack].intValue = integerResult;

                     break;
                     
                     case DOUBLE_GT_EQUAL:
                          readTwoDouble();
                          integerResult = doubleOpOne >= doubleOpTwo;
                          operandStack[ ++topOfOperandStack].intValue = integerResult;                          
                     break;
                     //LT Equal
                     case INT_LT_EQUAL:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue <= operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case LONG_LT_EQUAL:
                          readTwoLong();
                          integerResult = longOpOne <= longOpTwo;
                          operandStack[ ++topOfOperandStack ].intValue = integerResult;
                     break;
                     
                     case FLOAT_LT_EQUAL:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          integerResult = floatOpOne <= floatOpTwo;
                          
                          operandStack[ ++topOfOperandStack].intValue = integerResult;

                     break;
                     
                     case DOUBLE_LT_EQUAL:
                          readTwoDouble();
                          integerResult = doubleOpOne <= doubleOpTwo;
                          operandStack[ ++topOfOperandStack].intValue = integerResult;                          
                     break;
                     //GT 
                     case INT_GT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue > operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case LONG_GT:
                          readTwoLong();
                          integerResult = longOpOne > longOpTwo;
                          operandStack[ ++topOfOperandStack ].intValue = integerResult;
                     break;
                     
                     case FLOAT_GT:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          integerResult = floatOpOne > floatOpTwo;
                          
                          operandStack[ ++topOfOperandStack].intValue = integerResult;

                     break;
                     
                     case DOUBLE_GT:
                          readTwoDouble();
                          integerResult = doubleOpOne > doubleOpTwo;
                          operandStack[ ++topOfOperandStack].intValue = integerResult;                          
                     break;
                     //LT 
                     case INT_LT:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue < operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case LONG_LT:
                          readTwoLong();
                          integerResult = longOpOne < longOpTwo;
                          operandStack[ ++topOfOperandStack ].intValue = integerResult;
                     break;
                     
                     case FLOAT_LT:
                          floatOpTwo = operandStack[topOfOperandStack--].floatValue;
                          floatOpOne = operandStack[topOfOperandStack--].floatValue;
                          integerResult = floatOpOne < floatOpTwo;
                          
                          operandStack[ ++topOfOperandStack].intValue = integerResult;

                     break;
                     
                     case DOUBLE_LT:
                          readTwoDouble();
                          integerResult = doubleOpOne < doubleOpTwo;
                          operandStack[ ++topOfOperandStack].intValue = integerResult;                          
                     break;
                     
                     case AND:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue && operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case OR:
                          operandStack[ topOfOperandStack-1].intValue = operandStack[topOfOperandStack-1].intValue || operandStack[topOfOperandStack].intValue;
                          topOfOperandStack --; 
                     break;
                     
                     case NOT:
                          operandStack[ topOfOperandStack].intValue = !operandStack[topOfOperandStack].intValue ;

                     break;
                     
                     case INTEGER_TO_BYTE:
                          
                          operandStack[ topOfOperandStack].intValue = (UCSYByte)operandStack[topOfOperandStack].intValue;
                     break;
                     
                     case INTEGER_TO_SHORT:
                          operandStack[ topOfOperandStack].intValue = (UCSYShort)operandStack[topOfOperandStack].intValue;
                     break;
                     
                     case INTEGER_TO_LONG:
                          longResult = operandStack[ topOfOperandStack -- ].intValue;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];          
                     break;
                     
                     case INTEGER_TO_FLOAT:
                          operandStack[ topOfOperandStack ].floatValue = (UCSYFloat)operandStack[topOfOperandStack].intValue;
                     break;
                     
                     case INTEGER_TO_DOUBLE:
                          integerResult = operandStack[ topOfOperandStack --].intValue;
                          doubleResult = (UCSYDouble)integerResult;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];
                     break;
                     
                     case LONG_TO_INTEGER:
                          dataTwo[1]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          dataTwo[0]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          longResult  = *(UCSYLong*)dataTwo;
                          operandStack[++ topOfOperandStack ].intValue = (UCSYInteger)longResult;
                     break;
                     
                     case LONG_TO_FLOAT:
                          dataTwo[1]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          dataTwo[0]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          longResult  = *(UCSYLong*)dataTwo;
                          operandStack[++ topOfOperandStack ].floatValue = (UCSYFloat)longResult;
                     break;
                     
                     case LONG_TO_DOUBLE:
                          dataTwo[1]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          dataTwo[0]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          longResult  = *(UCSYLong*)dataTwo;
                          doubleResult = (UCSYDouble)longResult;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];

                     break;
                     
                     case FLOAT_TO_INTEGER:
                          operandStack[ ++ topOfOperandStack].intValue = (UCSYInteger)operandStack[topOfOperandStack--].floatValue;
                     break;
                     
                     case FLOAT_TO_LONG:
                          floatResult = operandStack[ topOfOperandStack--].floatValue;
                          longResult  = (UCSYLong)floatResult;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];          
                     break;
                     
                     case FLOAT_TO_DOUBLE:
                          floatResult = operandStack[ topOfOperandStack--].floatValue;
                          doubleResult  = (UCSYDouble)floatResult;
                          *(UCSYDouble*)dataTwo = doubleResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];          
                     break;
                     
                     case DOUBLE_TO_INTEGER:
                          dataTwo[1]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          dataTwo[0]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          doubleResult  = *(UCSYDouble*)dataTwo;
                          operandStack[++ topOfOperandStack ].intValue = (UCSYInteger)doubleResult;
                     break;
                     
                     case DOUBLE_TO_LONG:
                          dataTwo[1]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          dataTwo[0]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          doubleResult = *(UCSYDouble*)dataTwo;
                          longResult   = (UCSYLong)doubleResult;
                          *(UCSYLong*)dataTwo = longResult;
                          operandStack[ ++topOfOperandStack ].otherHalf = dataTwo[0];
                          operandStack[ ++topOfOperandStack ].otherHalf  = dataTwo[1];          
                          
                     break;
                     
                     case DOUBLE_TO_FLOAT:
                          dataTwo[1]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          dataTwo[0]  =  operandStack[topOfOperandStack-- ].otherHalf;
                          doubleResult  = *(UCSYDouble*)dataTwo;
                          operandStack[++ topOfOperandStack ].floatValue = (UCSYFloat)doubleResult;
                     break;
                     
                     case JUMP:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          //pc += 2;
                          pc = constantPoolIndex-1;     
                     break;
                     
                     case JUMP_FALSE:
                          integerResult = operandStack[topOfOperandStack--].intValue;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc +=2;
                          if( integerResult == 0)
                          {
                              pc = constantPoolIndex-1;
                          }
                     break;
                     
                     case JUMP_TRUE:
                          integerResult = operandStack[topOfOperandStack--].intValue;
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc +=2;
                          if( integerResult == 1)
                          {
                              pc = constantPoolIndex-1;
                          }
                     break;
                     
                     case CREATE_ARRAY:
                         constantPoolIndex = readShortFromCode(code,pc+1);
                         pc +=2; 
                         classRefEntry = (ClassRefEntry*)(constantPool[ constantPoolIndex ]->info);
                         newClass = classManager->getAClass( classRefEntry->className );
                         object =memoryManager->allocateArray(newClass,operandStack,topOfOperandStack);
                         operandStack[++topOfOperandStack].reference = object;
                         ///cout<<"Current value of "<<topOfOperandStack<<" "<< object<<endl;
                     break;
                     
                     case GET_ARRAY_ELEMENT_BYTE:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          operandStack[ ++topOfOperandStack].intValue = getByteFromArray(object,arrayIndex);
                     break;
                     
                     case GET_ARRAY_ELEMENT_SHORT:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          operandStack[ ++topOfOperandStack].intValue = getShortFromArray(object,arrayIndex);
                     break;
                     
                     case GET_ARRAY_ELEMENT_INT:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          operandStack[ ++topOfOperandStack].intValue = getIntegerFromArray(object,arrayIndex);
                     break;
                     
                     case GET_ARRAY_ELEMENT_LONG:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          *(UCSYLong*)dataTwo = getLongFromArray(object,arrayIndex);
                          operandStack[++topOfOperandStack].otherHalf = dataTwo[0];
                          operandStack[++topOfOperandStack].otherHalf = dataTwo[1];
                     break;
                     
                     case GET_ARRAY_ELEMENT_FLOAT:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          operandStack[ ++topOfOperandStack].floatValue = getFloatFromArray(object,arrayIndex);
                     break;
                     case GET_ARRAY_ELEMENT_DOUBLE:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          *(UCSYDouble*)dataTwo = getDoubleFromArray(object,arrayIndex);
                          operandStack[++topOfOperandStack].otherHalf = dataTwo[0];
                          operandStack[++topOfOperandStack].otherHalf = dataTwo[1];
                     break;
                     case GET_ARRAY_ELEMENT_REF:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          ///cout<<"Get array Element Ref "<<object<<endl;
                          operandStack[ ++topOfOperandStack].reference = getRefFromArray(object,arrayIndex);
                     break;
                     
                     case STORE_ARRAY_ELEMENT_BYTE:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          ///cout<<"Store array element int "<<object<<" "<<arrayIndex<<endl;
                          integerData = operandStack[topOfOperandStack--].intValue;
                          setByteToArray(object,arrayIndex,(UCSYByte)integerData);
                     break;
                     
                     case STORE_ARRAY_ELEMENT_SHORT:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          ///cout<<"Store array element int "<<object<<" "<<arrayIndex<<endl;
                          integerData = operandStack[topOfOperandStack--].intValue;
                          setShortToArray(object,arrayIndex,(UCSYShort)integerData);
                     break;
                     
                     case STORE_ARRAY_ELEMENT_INT:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          ///cout<<"Store array element int "<<object<<" "<<arrayIndex<<endl;
                          integerData = operandStack[topOfOperandStack--].intValue;
                          
                          setIntegerToArray(object,arrayIndex,integerData);
                     break;
                     
                     case STORE_ARRAY_ELEMENT_LONG:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          dataTwo[1] = operandStack[ topOfOperandStack--].otherHalf;
                          dataTwo[0] = operandStack[ topOfOperandStack--].otherHalf;
                          longData = *(UCSYLong*)dataTwo;
                          
                          setLongToArray(object,arrayIndex,longData);
                     break;
                     
                     case STORE_ARRAY_ELEMENT_FLOAT:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          floatData = operandStack[topOfOperandStack--].floatValue;
                          
                          setFloatToArray(object,arrayIndex,floatData);

                     break;
                     
                     case STORE_ARRAY_ELEMENT_DOUBLE:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          dataTwo[1] = operandStack[ topOfOperandStack--].otherHalf;
                          dataTwo[0] = operandStack[ topOfOperandStack--].otherHalf;
                          doubleData = *(UCSYDouble*)dataTwo;
                          
                          setDoubleToArray(object,arrayIndex,doubleData);
                     break;
                     
                     case STORE_ARRAY_ELEMENT_REF:
                          arrayIndex = operandStack[topOfOperandStack--].intValue;
                          object     = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          referenceData = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          //cout<<"OK "<<endl;
                          setRefToArray(object,arrayIndex,referenceData);
                     break;
                     
                     case THROW_EXCEPTION:
                          //cout<<"Top of operand stack in throw exception "<<topOfOperandStack<<endl;
                          exceptionObject = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          exceptionClass = exceptionObject->theClass; 
                          while( 1 )
                          {
                                 bool found = false;
                                 int noOfCatchEntries = currentMethod->noOfCatchEntries;
                                 catchTable  = currentMethod->catchTables;
                                 for(int j=0;j< noOfCatchEntries;j++)
                                 {
                                        aCatchEntry = catchTable[j]; 
                                        if( pc >= aCatchEntry->from && pc<= aCatchEntry->to)
                                        {
                                            classRefEntry = (ClassRefEntry*)(constantPool[ aCatchEntry->exceptionClassIndex]->info);
                                            //Catch type is parent of same class of exceptionClass
                                            catchClass = classManager->getAClass( classRefEntry->className );
                                            
                                            if( catchClass->isAncestorOrSameClassOf(exceptionClass ))
                                            {
                                                found = true;
                                                pc = aCatchEntry->target -1;
                                                operandStack[++topOfOperandStack].reference = exceptionObject;
                                                break;
                                            }
                                            
                                            //cout<<"Found "<<endl;
                                            
                                        }
                                 }
                                 if(found)
                                 {

                                          break;
                                 }
                                 else
                                 {
                                     //retFrame = methodCallStack->getCurrentFrame();
                                     methodCallStack->popAFrame();
                                     oldFrame = methodCallStack->getCurrentFrame();
                                     //retTopOfStack = topOfOperandStack;
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
                                      else
                                      {
                                          //UVM must handle exception
                                      }
                                 }
                          }
                          
                     break;
                     case INSTANCE_OF:
                          constantPoolIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          classRefEntry = (ClassRefEntry*)(constantPool[ constantPoolIndex ]->info);
                          newClass = classManager->getAClass( classRefEntry->className );
                          object   = (UVMObject*)operandStack[topOfOperandStack--].reference;
                          if(object == 0) //null
                          {
                              operandStack[++topOfOperandStack].intValue= 0; 
                          }
                          else
                          {
                              classToInvoke = object->theClass;
                              if(classToInvoke->isInstanceOf(newClass))
                              {
                                      operandStack[++topOfOperandStack].intValue= 1;                                 
                              }
                              else
                              {
                                      operandStack[++topOfOperandStack].intValue= 0;                                 
                              }
                          }
                     break;
                     case REBIND:
                          targetMethodIndex = readShortFromCode(code,pc+1);
                          pc += 2;
                          methodRefEntry    = (MethodRefEntry*)(constantPool[ targetMethodIndex])->info;
                          rebindMethodIndex = readShortFromCode(code,pc+1);
                          methodRefEntryTwo = (MethodRefEntry*)(constantPool[ rebindMethodIndex])->info;
                          pc += 2;
                          
                          object        = (UVMObject*)operandStack[ topOfOperandStack --].reference;
                          classOfObject = object->theClass;
                          ///cout<<"Before Problem "<<endl;
                          methodToInvoke = classOfObject->findRebindableMethod(methodRefEntry->methodName,methodRefEntry->signature);
                          targetMethodIndex = methodToInvoke->vTableIndex;//No Need to concern with Vatalbe ,index is only to rebinable table
                          ///cout<< "Change Slot "<<targetMethodIndex<<endl; 
                          newClass = classManager->getAClass( methodRefEntryTwo->className );
                          ///cout<<"To change with class "<<newClass->className<<endl;
                          object->rebindableTable[ targetMethodIndex ] = newClass->getMethod(methodRefEntryTwo->methodName,methodRefEntryTwo->signature);
                     break;
                     
                     default:
                             cout<<"Byte Code Not Implemented "<<(int)opcode <<" "<<pc;
              }
              pc++;
       }
}
