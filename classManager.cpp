#include "classManager.h"
#ifndef uvm_h
#include "uvm.h"
#endif
#ifndef classLoader_h
#include "classLoader.h"
#endif

#include <iostream>
#include <conio.h>
using namespace std;
class LoadedClassItem;
int UVMClassManager::hash(char *name)
{
    int h = 0;
    for(int i=0;i<strlen(name);i++)
            h = h * 1979 + name[i];
    return abs(h);
}

void UVMClassManager::insert(LoadedClassItem *item)
{
     int index = hash(item->className) % hashSize;
    /// cout<<"Inserting "<<item->className<<" "<<index<<endl;
     
     if(loadedClassTable[index] == 0)//First entry
     {
         ///cout<<"Insert at first \n";                       
         loadedClassTable[index] = item;
     }
     else
     {
         LoadedClassItem *last = loadedClassTable[ index ];
         
         if(strcmp(last->className,item-> className)==0)
         {
              ///cout<<"Duplicate entry in loadedClass "<<item->className<<endl;                            
              return;
         }
         while(last->next != NULL )
         {
              last = last->next;            
         }
         last->next = item;
     }
}
void UVMClassManager::insertAClass(UVMClass *theClass)
{
     LoadedClassItem *item = new LoadedClassItem;
     item->className = new char[strlen(theClass->className)+1];
     strcpy(item->className, theClass->className);
     item->theClass = theClass;
     //item->next = NULL;
     insert(item);
}
LoadedClassItem* UVMClassManager::getEntry(char *className)
{
     int index = hash(className) % hashSize;    
     ///cout<<"Finding for "<<className<< " "<<index<<endl;
     LoadedClassItem *current = loadedClassTable[ index ];        
    /// cout<<loadedClassTable[ index ];
     ///LoadedClassItem *current = NULL;
     
     while(current != NULL )
     {
           if( strcmp(current->className,className)==0)    
           {
               return current;
           }    
           current = current->next;
           
     }
     ////cout<<"Returning "<<current<<endl;
     return current;
}
bool UVMClassManager::isAlreadyLoaded(char *className)
{

     int index = hash(className) % hashSize;
     bool found = false;
     ////cout<<"Heere "<<index<<endl;
     LoadedClassItem *current = loadedClassTable[ index ];        
     //LoadedClassItem *current = NULL;
     //cout<<loadedClassTable[ index ]<<endl;
     while(current != NULL )
     {
           if( strcmp(current->className,className)==0)    
           {
               ///cout<<"Found class "<<endl;
               return true;
           }    
           else
           {
               current = current->next;
           }
     }
     return found ;
}
UVMClass* UVMClassManager::getAClass(char *className)
{
        if( !isAlreadyLoaded(className))
        {
            if(className[0]=='[')//That is arrayClass
            {
                   ///cout<<className<<endl;
                   UVMClass *arrayClass = new UVMClass();
                   arrayClass->className = className;
                   int dim = getDimensionOfArray(className);
                   arrayClass->noOfDimension = dim;
                   ///cout<<"Dimensiotn "<<dim<<endl;
                   char *elementName = new char[strlen(className)-dim +1 ];
                   int index = 0;
                   for(int i=dim;i<strlen(className);i++)
                   {
                           elementName[index++] = className[i];
                   }
                   elementName[index] ='\0';
                   
                   if( elementName[0] >='A' && elementName[0] <='Z' )
                   {
                       //It got refernce as element
                       UVMClassLoader::getClassLoader()->loadTheClass(elementName);
                       LoadedClassItem *elementItem = getEntry(elementName);
                       arrayClass->element = elementItem->theClass;
                   }
                   else //Prmitive Array
                   {
                        arrayClass->element = NULL;    
                   }
                   UVMClassManager::getClassManager()->insertAClass(arrayClass);
            }
            else
            {
                UVMClassLoader::getClassLoader()->loadTheClass(className);
            }
        }
        
        LoadedClassItem *item = getEntry(className);
        ///cout<<"I got "<<item->theClass->className<<"\n";

        return item->theClass;
               
}
UVMClassManager* UVMClassManager::classManager = NULL;
UVMClassManager::UVMClassManager()
{
                //loadedClassTable = new LoadedClassItem*[hashSize];         
   for(int i=0;i<hashSize;i++)
         loadedClassTable[i] = NULL;     
   
  /// cout<<"OK constructed class Manager"<<endl;
}
//*************** Loader ********************************************************
