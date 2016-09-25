#ifndef memoryManager_h
#include "memoryManager.h"
#endif
#ifndef methodCallStack_h
#include "methodCallStack.h"
#endif

#include <iostream>
using namespace std;
char elementName[200];
MemoryManager* MemoryManager::memoryManager = NULL;
MemoryManager::MemoryManager()
{
}
MemoryManager* MemoryManager::getMemoryManager()
{
       if( memoryManager == NULL )
       {
           memoryManager = new MemoryManager();
       }       
       return memoryManager;
}
MethodFrame* MemoryManager::allocateMethodFrameForMethod(UVMMethod *aMethod,UVMClass *aClass)
{
             ///cout<<"Allocate for "<<aMethod->methodName<< " "<<aMethod->methodSignature<<" "<<aMethod->argSize<<endl;
             MethodFrame *newFrame;
             
             newFrame =new MethodFrame();
             newFrame->operandStack = new UCSYValue[ aMethod->maxSizeOfOperandStack ];
             newFrame->localVar = new UCSYValue[ aMethod->maxNoOfLocalArray ];
             newFrame->pc = 0;
             ///cout<<"ADD Class "<<aClass->className<<endl;
             newFrame->theClass = aClass;
             newFrame->method = aMethod;
             newFrame->topOfOperandStack = -1;
             newFrame->constantPool = aClass->constantPool;
             
             MethodCallStack* runtimeStack = MethodCallStack::getMethodCallStack();
             ///cout<<"PUSH "<<newFrame->theClass->constantPool<<endl;
             runtimeStack->pushAFrame(newFrame);
             
             return newFrame;
}
UVMObject* MemoryManager::allocateObject(UVMClass *theClass)
{
            UVMObject *object = new UVMObject();
            object->theClass = theClass;

            object->instanceData = new u1[theClass->sizeOfInstanceVar];
            if(theClass->sizeOfRebindableTable >0 )
            {
                 object->rebindableTable = new UVMMethod*[ theClass->sizeOfRebindableTable ];                   
                 for(int i=0;i< theClass->sizeOfRebindableTable;i++)
                 {
                        object->rebindableTable[i] = theClass->rebindableTable[i]; 
                 }
            }
            return object;
}
void* MemoryManager::allocateStaticData(UVMClass *theClass)
{
      void *staticData = new u1[ theClass->sizeOfStaticVar ];
      return staticData;
}
UVMObject* MemoryManager::allocateRefArray(int size)
{
      UVMObject* refArray = new UVMObject();
      //Ref is 4 byte so allocate with integer
      //new (ConstantPoolEntry*[noOfEntry]);
      int actualSize = 4 + size* 4; //Ref Occupies 4 byte
      UCSYInteger *data = new UCSYInteger[size+1];
      refArray->instanceData =(u1*)data ;
      data[0] =size;
      
      for(int i=1;i<size+1;i++)
      {
          data[i]= 0;    
      }
      return refArray;      
}
UVMObject* MemoryManager::allocatePrimitiveArray(int primitiveType,int size)
{
      ///cout<<"To allocate "<<primitiveType<<" of size 33 "<<size<<endl;      
      UVMObject* primitiveArray = new UVMObject();
      
      int sizeToAllocate = 4; //Tag
      switch(primitiveType)
      {
             case TYPE_BOOLEAN:
             case TYPE_BYTE:
             case TYPE_CHAR:
             break;
                  sizeToAllocate = sizeToAllocate + size;       
             case TYPE_SHORT:

                  sizeToAllocate =  sizeToAllocate + 2*size;       
             break;
             
             case TYPE_INTEGER:
             case TYPE_FLOAT:
                  sizeToAllocate = sizeToAllocate + 4*size;       
             break;
             
             case TYPE_LONG:
             case TYPE_DOUBLE:
                  sizeToAllocate = sizeToAllocate + 8*size;       
             break;
             
             default:
                  cout<<"Invalid Primitive Type in array allocation "<<endl;              
      }
      u1* ptr = new u1[sizeToAllocate];
      UCSYInteger *intPtr = (int*)ptr;
      intPtr[0] = size; //Set Length Field
      
      
      for(int i=4;i<sizeToAllocate;i++)
              ptr[i]=0;
      
      primitiveArray->instanceData = ptr;  
      return primitiveArray;      
}

UVMObject* MemoryManager::allocateArray(UVMClass *theClass,UCSYValue *operandStack,int &topOfOperandStack)
{
       int dim = theClass->noOfDimension;
       UVMObject *arrayObject ;
       int tos = topOfOperandStack - dim + 1;
       arrayObject = allocateArray(theClass,operandStack,dim,theClass->className,tos);
       topOfOperandStack = tos-1;
       return arrayObject;          
}
UVMObject * MemoryManager::allocateArray(UVMClass *theClass,UCSYValue *operandStack,int dimension,char *arrayType,int topOfStack)
{
       UCSYInteger sizeOfArray = operandStack[topOfStack].intValue;    
       UVMObject *arrayObject;
        if(dimension == 1)
        {
            
             int lengthOfArrayType = strlen(arrayType) +1;
             strcpy(elementName,arrayType);
             int i=0;
             while( arrayType[i]=='[')
             {
                 i++;   
             }
             int index=0;
             for(int j=i;j<strlen(arrayType);j++)
             {
                 elementName[index++] = arrayType[j];    
             }
             
             elementName[index]='\0';
             ///cout<<"Element Name is"<<elementName<<endl;
             if(strlen(elementName) >1) //Reference Type
             {
                    arrayObject = allocateRefArray(sizeOfArray);
             }
             else //Primitive Type
             {
                  ///cout<<"Primitive "<<endl;
                  int pType = getTypeOfTypeName(elementName);               
                  arrayObject = allocatePrimitiveArray(pType,sizeOfArray);
             }
             
        }
        else
        {
            arrayObject = allocateRefArray(sizeOfArray);
            UCSYInteger* member = (UCSYInteger*)arrayObject->instanceData;
            for(int i=1;i<sizeOfArray+1;i++)
            {
                    member[i] = (UCSYInteger)allocateArray(theClass,operandStack,dimension-1,arrayType,topOfStack++);
            }
        }
        
       return arrayObject;    
}
