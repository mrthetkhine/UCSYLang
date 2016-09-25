#define classManager_h
#ifndef uvm_h
#include "uvm.h"
#endif
#define hashSize 256
class LoadedClassItem;
class UVMClass;
class UVMClassManager
{

        //UVMClassLoader *classLoader;
        //UVMClassParser *classParser;
        UVMClassManager();
        
        LoadedClassItem* loadedClassTable[hashSize ];              
        static UVMClassManager* classManager;
        
 public:
        
        
        static UVMClassManager* getClassManager()
        {
          if( classManager == NULL)          
          {
               classManager = new UVMClassManager();
                  
          }
          return classManager;
        }
        
        void insert(LoadedClassItem *item);
        int hash(char *className);
        LoadedClassItem* getEntry(char *className);
        bool isAlreadyLoaded(char *className);
        void insertAClass(UVMClass *theClass);
        UVMClass* getAClass(char *className);     
};

