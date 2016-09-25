#define classLoader_h
#ifndef uvm_h
#include "uvm.h"
#endif
class UVMClassLoader
{
      static UVMClassLoader *loader;
      //UVMClassManager * classManager;
      UVMClassLoader()
      {
      }
      public: 
              static UVMClassLoader* getClassLoader()
              {
                    if( loader == NULL)
                    {
                        loader = new UVMClassLoader();
                        
                    }   
                    return loader;   
              }
              UVMClass *loadTheClass(char *className);
};





