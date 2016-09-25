#define methodCallStack_h
#ifndef uvm_h
#include "uvm.h"
#endif

#define maxNoOfFrame 4500

class MethodCallStack
{
      MethodCallStack();
      
      static MethodCallStack *methodCallStack;
      
      
      

public:
       int top;
       MethodFrame* frames[maxNoOfFrame];
       MethodFrame *currentFrame;
       //Max no of method call depth is 256
       static MethodCallStack* getMethodCallStack();
       MethodFrame* getCurrentFrame();
       void popAFrame();
       void pushAFrame(MethodFrame *newFrame);

};


