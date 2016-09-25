#include <cstdlib>
#include <iostream>
#ifndef uvm_h
#include "uvm.h"
#endif
#ifndef classParser_h
#include "classParser.h"
#endif
#ifndef classManager_h
#include "classManager.h"
#endif
#ifndef nativeManager_h
#include "nativeManager.h"
#endif
///#include "myHeader.h"
#ifndef executionEngine_h
#include "executionEngine.h"
#endif

#ifndef library_h
#include "library.h"
#endif
using namespace std;
#include <conio.h>

int main(int argc, char *argv[])
{
    initializeNativeMethod();
    
    UVMClassManager *classManager = UVMClassManager::getClassManager();
    UVMClass *theClass ;
    char *className = new char[80];
    
    if(argc > 1)
    {
       strcpy(className,argv[1]);     
    }
    else
    {
     strcpy(className,"ExceptionDemo");
    }
    theClass =  classManager->getAClass(className);
    UVMMethod *theMethod = theClass->findStaticMethod("main","([m,)v");
    ExecutionEngine engine ;
    engine.execute(theClass,theMethod);
    ///UVMClass *superClass = 
   
   //classManager->getAClass("[[Human");
   getch();
    
    return 0;
}


