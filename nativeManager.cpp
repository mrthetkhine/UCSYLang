#ifndef uvm_h
#include "uvm.h"
#endif
#include<iostream>
#ifndef nativeManager_h
#include "nativeManager.h"
#endif

using namespace std;
NativeManager* NativeManager::nativeManager = NULL;

int NativeManager::hash(char *methodName)
{
    int h = 0;
    for(int i=0;i<strlen(methodName);i++)
            h = h * 1979 + methodName[i];
    return abs(h);
}
char * NativeManager::getInternalName(char *className,char *methodName,char *methodSignature)
{
     char *internalName = new char[strlen(className) + strlen(methodName)+strlen(methodSignature)+1];
     strcpy(internalName,className);
     strcat(internalName,methodName);
     strcat(internalName,methodSignature);
     //cout<<"Internal Name is "<<internalName<<endl;
     return internalName;
}
void NativeManager::insert(NativeMethodEntry *item)
{
       int index = hash(item->nativeMethodName) % hashSize;
       //cout<<"Inserting "<<item->nativeMethodName<<" "<<index<<endl;
     
     if(nativeMethodTable[index] == 0)//First entry
     {
         //cout<<"Insert at first \n";                       
         nativeMethodTable[index] = item;
     }
     else
     {
         NativeMethodEntry *last = nativeMethodTable[ index ];
         
         if(strcmp(last->nativeMethodName,item-> nativeMethodName)==0)
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
NativeMethod NativeManager::getNativeMethod(char *className,char *methodName,char *methodSignature)
{
     char *internalName = getInternalName(className,methodName,methodSignature);
     int index = hash(internalName) % nativeHashSize;    
     ///cout<<"Finding for "<<className<< " "<<index<<endl;
     NativeMethodEntry *current = nativeMethodTable[ index ];        
    /// cout<<loadedClassTable[ index ];
     ///LoadedClassItem *current = NULL;
     
     while(current != NULL )
     {
           ////cout<<"Comparing "<< current->nativeMethodName <<" "<<internalName<<endl;       
           if( strcmp(current->nativeMethodName,internalName)==0)    
           {
               ///cout<<"Ok";
               return current->nativeMethod;
           }    
           current = current->next;
           
     }
     ////cout<<"Returning "<<current<<endl;
     if(current==NULL)
     {
            cout<<"CANNOT FIND NAITVE METHOD "<<internalName<<endl;          
            return NULL;
     }
     return current->nativeMethod;
}
void NativeManager::addNativeMethod(char *className,char *methodName,char *methodSignature,NativeMethod nativeMethod)
{
     char * internalName = getInternalName(className,methodName,methodSignature);
     NativeMethodEntry *item = new NativeMethodEntry();
     item->nativeMethodName = internalName;
     item->nativeMethod = nativeMethod;
     item->next = NULL;
     
     insert(item);
}
