#ifndef uvm_h
#include "uvm.h"
#endif
#ifndef methodCallStack_h
#include "methodCallStack.h"
#endif

#include <iostream>
using namespace std;

MethodCallStack* MethodCallStack::methodCallStack = NULL;
MethodCallStack::MethodCallStack()
{
     for(int i=0; i< maxNoOfFrame;i++)
     {
       frames[i] = NULL;   
     }
     top = -1;
     currentFrame = NULL;                           
}
MethodCallStack * MethodCallStack::getMethodCallStack()
{
      if( methodCallStack == NULL)
      {
          methodCallStack = new MethodCallStack();
      }          
      return methodCallStack;
}
MethodFrame* MethodCallStack::getCurrentFrame()
{
      ///cout<<"ACCESSING "<<currentFrame->theClass->className<<" "<<currentFrame->constantPool<<endl;
      return currentFrame;       
}

void MethodCallStack::pushAFrame(MethodFrame *newFrame)
{
     frames[++top]= newFrame;
     currentFrame = newFrame;
     ///cout<<"PUSH top "<<top<<" "<<newFrame->theClass->className<<" of method "<<newFrame->method->methodName<<"  "<<newFrame->constantPool<<endl;
}
void MethodCallStack::popAFrame()
{
     ///cout<<"POP  top "<<top;
     top--;
     if(top < 0)
     {
         currentFrame = NULL;
     }
     else
     {
         ///cout<<"  "<< currentFrame->theClass->className<<" of method "<<currentFrame->method->methodName<<"  "<< currentFrame->constantPool<<endl;
         currentFrame = frames[top];
         ///cout<<"CURRENT Class "<<currentFrame->theClass->className<<"top "<<top<<endl;
     }
     
}
