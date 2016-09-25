#include "classLoader.h"
#ifndef uvm_h
#include "uvm.h"
#endif
#ifndef classParser_h
#include "classParser.h"
#endif
#include <iostream>
using namespace std;
UVMClassLoader* UVMClassLoader::loader = NULL;
UVMClass* UVMClassLoader::loadTheClass(char *className)
{
      if(UVMClassManager::getClassManager()->isAlreadyLoaded(className))
      {
           return UVMClassManager::getClassManager()->getAClass(className);                                                           
      }     
      UVMClassParser *classParser = UVMClassParser::getClassParser();    
      UVMClass *theClass = classParser ->parseTheClass(className);
      UVMClass *superClass;
      char *currentClassName = theClass->getClassName();
      //cout<<"OK at this stage  "<<currentClassName<<" \n";
      if(strcmp(currentClassName,"Object")==0)
      {            
            superClass = NULL;
      }
      else
      {
          //Load parent super class and parent interface
          UVMClassManager* classManager= UVMClassManager::getClassManager();
          
          
          if( !(UVMClassManager::getClassManager()->isAlreadyLoaded(theClass->superClassName)))
          {
               superClass = loadTheClass( theClass->superClassName );
                      ///UVMClassManager::getClassManager()->insertAClass( superClass );
          }
          superClass = UVMClassManager::getClassManager()->getAClass(theClass->superClassName);
          theClass->superClass = superClass;
                 //theClass->interfaces = new UCSYInterface*[ theClass->noOfInterfaces ];
          for(int i=0;i< theClass->noOfInterfaces;i++)
          {
               UVMClass *interfaceClass ;
               if( !UVMClassManager::getClassManager()->isAlreadyLoaded(theClass->interfaces[i]->interfaceName))
               {
                    interfaceClass = loadTheClass( theClass->interfaces[i]->interfaceName);
                      ///UVMClassManager::getClassManager()->insertAClass( interfaceClass );
               }
               interfaceClass = UVMClassManager::getClassManager()->getAClass(theClass->interfaces[i]->interfaceName);
               theClass->interfaces[i]->theInterface = interfaceClass;
          }
       }
          //Calculate instance field offest and static field offest
          ///cout<<"Loading "<<theClass->className<<" complete\n";
          
          theClass->calculateStaticFieldOffest();
          theClass->calculateIntanceFieldOffest();
          theClass->constructVtable();
          
      
          UVMClassManager::getClassManager()->insertAClass(theClass);
          theClass->initializeClass();

      return theClass;
}
