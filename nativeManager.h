#define nativeManager_h
#define nativeHashSize 256
#ifndef uvm_h
#include "uvm.h"
#endif
class NativeMethodEntry
{
      public:
      char *nativeMethodName;
      NativeMethod nativeMethod;
      
      NativeMethodEntry* next;
      
      
};
class NativeManager
{
    
      NativeManager()
     {
          for(int i=0;i< nativeHashSize;i++)
          {
            nativeMethodTable[i] = NULL;
          }                          
     }
      NativeMethodEntry * nativeMethodTable[ nativeHashSize ];
      static NativeManager* nativeManager;
      
      public:
             static NativeManager* getNativeManager()
             {
                    if( nativeManager == NULL )
                    {
                        nativeManager = new NativeManager();
                    }     
                    return nativeManager;   
             }
             
             void insert(NativeMethodEntry *item);
             int hash(char *nativeMethodName);
             NativeMethod getNativeMethod(char *className,char *methodName,char *methodSignature);
             char* getInternalName(char *className,char *methodName,char *methodSignature);
             void addNativeMethod(char *className,char *methodName,char *methodSignature,NativeMethod nativeMethod);
};

