using namespace std;
#ifndef uvm_h
#include "uvm.h"
#endif
#ifndef classParser_h
#include "classParser.h"
#endif
#ifndef classLoader_h
#include "classLoader.h"
#endif

#ifndef executionEngine_h
#include "executionEngine.h"
#endif

#include <iostream>
#include <conio.h>

//#define isStatic(modifier) ( modifier & STATIC ) == STATIC
//#define isNative(modifier) ( modifier & NATIVE )== NATIVE

u1 OneByte[2];
u1 TwoByte[2];
u1 FourByte[4];
u1 EightByte[8];

u1 oneByte[2];
u1 twoByte[2];
u1 fourByte[4];
u1 eightByte[8];

int STATIC = 1<<3;
int REBINDABLE = 1<<10;

bool isStatic(int attribute)
{
     if( (attribute & STATIC )== STATIC )
     {
          return true;          
     }
     else 
     {
          return false;
     }
}
bool isFreeClass( int attribute)
{
     return ((attribute & FREE )==FREE);
}
bool isRebindable(int attribute)
{
     //cout<<"Attribute is "<<attribute << "  "<<( (attribute & (1<<10) )==(1<<10) )<<endl;
     return( (attribute & (1<<10) )==(1<<10) );
     
}
bool UVMMethod::isNative()
{
//     int NATIVE = 1 << 11;
     return  (methodModifier & NATIVE) == NATIVE;
}
int getTypeOfTypeName(char *typeName)
{
    if(strcmp(typeName,"m") == 0)
          return TYPE_STRING;
    else if(strcmp(typeName,"t")==0)
         return TYPE_BOOLEAN;
    else if(strcmp(typeName,"b")==0)
         return TYPE_BYTE;
    else if(strcmp(typeName,"s")==0)
         return TYPE_SHORT;
    else if(strcmp(typeName,"i")==0)
         return TYPE_INTEGER;
    else if(strcmp(typeName,"l")==0) 
         return TYPE_LONG;
    else if(strcmp(typeName,"f")==0)
         return TYPE_FLOAT;
    else if(strcmp(typeName,"d")==0)
         return TYPE_DOUBLE;
    else if(strcmp(typeName,"c")==0)
         return TYPE_CHAR;
    else if(strcmp(typeName,"null")==0)
         return TYPE_NULL;       
    else 
    {
         char firstChar = typeName[0];
         if( isupper(firstChar)) //That is object type
               return  TYPE_OBJECT;
         else if(firstChar == '[') //That is array type
              return TYPE_ARRAY;
          
    }
}
int getSizeOfTypeInByte(int type)//retrun the type of instance or static offest in bytes
{
    switch(type)
    {
           case TYPE_STRING:
                return 4; //Pointer is 4 byte;
           case TYPE_BYTE:
                return 1;
           case TYPE_SHORT:
                return 2;
           case TYPE_INTEGER:
                return 4;
           case TYPE_LONG:
                return 8;
           case TYPE_FLOAT:
                return 4;
           case TYPE_DOUBLE:
                return 8;
           case TYPE_BOOLEAN:
                return 1;
           case TYPE_CHAR:
                return 1;
           default: //Object ,array occupies pointer type
                return 4;   
                 
    }
}
int getSizeOfTypeInWord(int type)//retrun the type of instance or static offest in bytes
{
    switch(type)
    {
           case TYPE_STRING:
           case TYPE_BYTE:
           case TYPE_SHORT:
           case TYPE_INTEGER:
           case TYPE_FLOAT:
           case TYPE_BOOLEAN:
           case TYPE_CHAR:
                return 1;
           case TYPE_LONG:
           case TYPE_DOUBLE:
                return 2;
           default: //Object ,array occupies pointer type
                return 1;   
                 
    }
}
int getSizeOfTypeInByteGivenTypeName(char *typeName)
{
    int type = getTypeOfTypeName(typeName);
    int size = getSizeOfTypeInByte(type);
    return size;
}
UCSYShort readShortFromCode(u1 *code,int index)
{
          UCSYShort data;
          char twoByte[2];
          char firstByte = code[index];
          char secondByte = code[index+1];
          twoByte[0] = secondByte;
          twoByte[1] = firstByte;
          data = *(UCSYShort *)twoByte;
          //cout<<"Read Short "<<data<<endl;
          return data;
}
int UVMClass::getSizeOfStaticData()
       {
           if( strcmp(className,"Object")==0)
           {
               return 0;
           }
           else
           {
               int size = 0;
               for(int i =0;i< noOfFields;i++)
                {
                        UVMField *field = fields[i];
                        if(isStatic(field->fieldModifier))
                        {
                             int type = getTypeOfTypeName(field->fieldType);                
                             int fSize = getSizeOfTypeInByte(type);
                             size+= fSize;
                        }
                } 
                return size;
           }    
       }
int UVMClass::getSizeOfInstanceVar()
{
           
     if(strcmp(className,"Object")==0)
     {
          return 0;                        
     }
     else
     {
          int sizeOfData = superClass->sizeOfInstanceVar;
          for(int i =0;i< noOfFields;i++)
          {
               UVMField *field = fields[i];
               if(!isStatic(field->fieldModifier))
               {
                     int type = getTypeOfTypeName(field->fieldType);                
                     int fSize = getSizeOfTypeInByte(type);
                     sizeOfData+= fSize;               
                }
          } 
       return sizeOfData + superClass->getSizeOfInstanceVar();
     }
}
       
void UVMClass::calculateStaticFieldOffest()
{
     int offest = 0;
     for(int i=0;i< noOfFields;i++)
     {
             UVMField *field = fields[i];
             if( isStatic( field->fieldModifier))
             {
                /// cout<<"Static Field "<<field->fieldName<<" occup at "<<offest<<endl;
                 field->offest.staticOffest = offest;
                 int fieldSize = getSizeOfTypeInByteGivenTypeName(field->fieldType);
                 offest+= fieldSize;
             }
     }
     //Should allocate for static fields;
}
void UVMClass::calculateIntanceFieldOffest()
{
     if( strcmp(className,"Object")==0)
     {
         return;
     }
     int offest = superClass->getSizeOfInstanceVar();
     for(int i=0;i< noOfFields;i++)
     {
             UVMField *field = fields[i];
             if(! isStatic( field->fieldModifier))
             {
                 ///cout<<"Instance Field "<<field->fieldName<<" occup at "<<offest<<endl;
                 field->offest.instanceOffest = offest;
                 int fieldSize = getSizeOfTypeInByteGivenTypeName(field->fieldType);
                 offest+= fieldSize;
             }
     }
}

void UVMClass::initializeClass()
{
     //Allocate static data;
     staticData = new u1[ sizeOfStaticVar ];
     for(int i=0;i<noOfMethods;i++)
     {
             UVMMethod *method = methods[i];
             if( strcmp(method->methodName,"<cinit>")==0)
             {
                 ExecutionEngine engine ;
                 engine.execute(this,method);
             }
     }
     //Run class initialization methods static constructor
}

/*******************************************************************************
          Calculation of vatables
vTableMethod is used as temporary variable in calculation of vtables       
********************************************************************************/
UVMMethod *vTableMethod[2048]; //Assme maximum depth of any class vtable will not grow more than 26
UCSYInterface *interfaceTableTemp[2048]; 
void UVMClass::constructVtable()
{
     if(strcmp(className,"Object")==0)
     {
             sizeOfVtable = 0;
             sizeOfAllInterfaces = 0;
             vtable =NULL;                         
             return;
     }
     else
     {
           if( isClass( classModifier))
           {
                  ///cout<<"Construct Vtable for "<<className<<endl;
                  int sizeOfSuperClassVtable = superClass->sizeOfVtable;
                  int index = 0;
           
                 //Copy vtable of super class into temp
                 for(int i=0;i< sizeOfSuperClassVtable;i++)
                 {
                      vTableMethod[index++] = superClass->vtable[i];
                 }
                 ////cout<<"No of Methods "<<noOfMethods<<endl;
                 for(int i=0;i< noOfMethods;i++)
                 {
                       UVMMethod *instanceMethod = methods[i];
                   
                       if(! isStatic(instanceMethod->methodModifier) && (strcmp(instanceMethod->methodName,"<init>")!=0) && (strcmp(instanceMethod->methodName,"<cinit>")!=0))
                       {

                           bool found = false;
                           for(int j=0;j<sizeOfSuperClassVtable;j++)
                           {
                                UVMMethod *superClassMethod = vTableMethod[j];
                                if( (strcmp(instanceMethod->methodName,superClassMethod->methodName)==0 )&& (strcmp(instanceMethod->methodSignature,superClassMethod->methodSignature)==0) )
                                {
                                    ////cout<<"OK replaciog with index "<<j<<" for "<<instanceMethod->methodName<<" "<<instanceMethod->methodSignature<<endl;
                                    vTableMethod[j] = instanceMethod;
                                    found = true;
                                    break;
                                }
                            }
                            if(!found)
                            {
                             ///cout<<"New entry "<< instanceMethod->methodName<<"  "<<index<<endl;     
                                   vTableMethod[index++] = instanceMethod;     
                            }
                        
                         }//if !isStatic
                    }//for
            
                    //Size of current vtable is in the index;
                   sizeOfVtable = index;
                   //Allocate and copy vtables
                   ///cout<<"Size of virtual table is "<<sizeOfVtable<<endl;
                    vtable = new UVMMethod*[ sizeOfVtable ];
            
                    for(int i=0;i<sizeOfVtable;i++)
                    {             
                        vtable[i] = vTableMethod[i];
                    }

                    
                    //****************** Code Adding new Code ******************
                    
                    index = 0;
                    int sizeOfSuperAllInterfaces = superClass->sizeOfAllInterfaces;
                    for(int i=0;i< sizeOfSuperAllInterfaces;i++)
                    {
                            interfaceTableTemp [index++] = superClass->allInterfaces[i];
                    }
                    //Check Duplicate
                    
                    for(int i=0;i< noOfInterfaces;i++)
                    {
                            UCSYInterface *aInterface = interfaces[i];
                            bool existed = false;
                            for(int j=0;j<index;j++)
                            {
                             if( strcmp(aInterface->interfaceName,interfaceTableTemp[j]->interfaceName)==0)
                             {
                                 existed =true;
                             }
                            }//for j
                            if(!existed)
                            {
                                 interfaceTableTemp[index++] = aInterface;       
                            }
                    }
                    
                    //Ok size is at index
                    sizeOfAllInterfaces = index;
                    allInterfaces = new UCSYInterface*[sizeOfAllInterfaces];
                    //Copy temp value to actual allInterfaces
                    for(int i=0;i<sizeOfAllInterfaces;i++)
                    {
                            allInterfaces[i] = interfaceTableTemp[i];
                    }
                
                    //if Class is not abstract construct itable mapping
                    if( !isAbstractClass(classModifier))
                    {
                     for(int no=0;no< sizeOfAllInterfaces;no++)
                        {
                             UCSYInterface* aInterface = allInterfaces[no];   
                             UVMClass* interfaceClass = aInterface->theInterface;
                             aInterface->itable = new UCSYInteger[ interfaceClass->sizeOfVtable ];
                             
                             for(int i=0; i<interfaceClass->sizeOfVtable; i++)
                             {
                                   bool found = false;  
                                   for(int j=0;j < sizeOfVtable;j++)  
                                   {
                                          if(strcmp( interfaceClass->vtable[i]->methodName,vtable[j]->methodName ) ==0 && strcmp(interfaceClass->vtable[i]->methodSignature,vtable[j]->methodSignature)==0) 
                                          {
                                               found = true;
                                               aInterface->itable[i] = j;     
                                          }
                                   }
                                   if(!found)
                                   {
                                             cout<<"Error in Mapping vtable to itable";
                                   }
                             }    
                        }
                     }//is !AbstractClass    
                    ///cout<<"Construct rebindbale table for "<<className<<endl;
                      //Construct rebindable table
                    index = 0;  
                    for(int i=0;i< noOfMethods;i++)
                    {
                            UVMMethod *met = methods[i];
                            //cout<<"Playing something "<<endl;
                            if( isRebindable(met->methodModifier) )
                            {
                                vTableMethod[index++] = met;           
                                //cout<<"Rebindable "<<met->methodName<<endl;
                            }
                    }
                    sizeOfRebindableTable = index;
                    rebindableTable = new UVMMethod*[ sizeOfRebindableTable ];
                    for(int i=0;i<sizeOfRebindableTable;i++)
                    {
                           rebindableTable[i] = vTableMethod[i];        
                    }
               }
			   else //This is interface
			   {
                    //cout<<"Trying interface "<<className<<endl;
					//int sizeOfSuperClassVtable = superClass->sizeOfVtable;
                    int index = 0;
                    //For Interface its parents are in the interface list
                    for(int no = 0; no< noOfInterfaces;no++)
                    {
                            UCSYInterface *superInterface = interfaces[no] ;
                            UVMClass* interfaceClass = superInterface->theInterface;
                            //interfaceClass->constructVtable();
                            int sizeOfSuperVtable = interfaceClass->sizeOfVtable;
                            for(int i =0;i<sizeOfSuperVtable;i++)
                            {
                                   bool methodExisted = false; 
                                   //Check there already existed a method
                                   for(int j=0;j< index;j++)
                                   {
                                        if( (strcmp(vTableMethod[j]->methodName,interfaceClass->vtable[i]->methodName)==0) && strcmp(vTableMethod[j]->methodSignature, interfaceClass->vtable[i]->methodSignature)==0)   
                                        {
                                            methodExisted = true;
                                            break;
                                        }
                                   } 
                                   if(! methodExisted )
                                   {
                                        vTableMethod[index++] = interfaceClass->vtable[i];
                                   }
                                   
                            }//for i

                    }//for no
                    
                    //Place its own method 
                    
                    for(int i=0;i< noOfMethods ;i++) 
                    {
                            bool methodExisted = false;
                            UVMMethod *met = methods[i];
                            
                            for(int j=0;j< index ;j++)
                            {
                                  if(strcmp(vTableMethod[j]->methodName,met->methodName)==0 && strcmp( vTableMethod[j]->methodSignature, met->methodSignature) ==0)  
                                  {
                                        methodExisted = true;  
                                        break;                                                      
                                  }
                            }  
                            if(!methodExisted)
                            {
                                  vTableMethod[index++] = met;            
                            }     
                    }
                    
                    //Ok the size of vtable of interface is in the index
                    sizeOfVtable = index;
                    vtable = new UVMMethod*[ sizeOfVtable ];
                    for(int i=0;i<index;i++)
                    {
                            vtable[i]= vTableMethod[i];
                    }
                    
                   
                    
			   }//if interface
         }
   
}
UVMMethod* UVMClass::findStaticMethod(char *methodName,char *methodSignature)
{
       for(int i=0;i<noOfMethods;i++)
       {
              UVMMethod *method = methods[i];
              if(isStatic(method->methodModifier))
              {
                      if((strcmp(method->methodName,methodName)==0) && (strcmp(method->methodSignature,methodSignature)==0))                           
                      {
                            ///cout<<"OK method code"<<method->methodCode->lengthOfCode<<endl;                                            
                            return method;                                       
                      }
              }
               
       }     
       cout<<"CANNOT FIND MEHTOD "<<methodName<<endl;
       return NULL;
}
UVMMethod* UVMClass::findConstructorMethod(char *methodSignature)
{
            for(int i=0;i< noOfMethods;i++)
            {
                    UVMMethod *method = methods[i];
                    if( (strcmp( method->methodName,"<init>" )==0) && strcmp(method->methodSignature,methodSignature)==0 )
                    {
                        return method;
                    }
            }
            cout<<"NO CONSTRUCTOR METHOD FOUND "<<methodSignature<<endl;
            return NULL;
}
UVMMethod* UVMClass::findVirtualMethod(char *methodName,char *methodSignature)
{
            for(int i=0;i <sizeOfVtable;i++ )
            {
                    UVMMethod *method = vtable[i];
                    if( (strcmp( method->methodName,methodName )==0) && strcmp(method->methodSignature,methodSignature)==0 )
                    {
                        return method;
                    }
            }
            cout<<"NO Virtual METHOD FOUND "<<methodName<<" with signature "<<methodSignature<<" in class"<<className<<endl;
            return NULL;
}
UVMMethod* UVMClass::findInterfaceMethod(char *methodName,char *methodSignature)
{
            for(int i=0;i <sizeOfVtable;i++ )
            {
                    UVMMethod *method = vtable[i];
                    if( (strcmp( method->methodName,methodName )==0) && strcmp(method->methodSignature,methodSignature)==0 )
                    {
                        method->vTableIndex = i;
                        //cout<<"found at "<<i<<endl;
                        return method;
                    }
            }
            cout<<"NO Interface METHOD FOUND "<<methodName<<" with signature "<<methodSignature<<" in class"<<className<<endl;
            return NULL;
}
UVMMethod* UVMClass::findRebindableMethod(char *methodName,char *methodSignature)
{
            ///cout<<"Size of rebindable table "<<sizeOfRebindableTable<<endl;
            for(int i=0;i< sizeOfRebindableTable; i++)
            {
                    
                    UVMMethod *method = rebindableTable[i];
                    ////cout<<"Comparing "<<method->methodName<< " "<<method->methodSignature<< endl;
                    if( (strcmp( method->methodName,methodName )==0) && strcmp(method->methodSignature,methodSignature)==0 )
                    {
                        method->vTableIndex = i;
                        //cout<<"found at "<<i<<endl;
                        return method;
                    }
            }
            cout<<"NO rebindable METHOD FOUND "<<methodName<<" with signature "<<methodSignature<<" in class"<<className<<endl;
            return NULL;
}
UVMMethod* UVMClass::getMethod(char *methodName,char *methodSignature)
{
           /// cout<<"No of Methods "<<noOfMethods<<endl;
            for(int i=0;i <noOfMethods; i++)
            {
                    UVMMethod *method = methods[i];
                   /// cout<<"Comparing getMethod "<<method->methodName<< " "<<method->methodSignature<< endl;
                    if( (strcmp( method->methodName,methodName )==0) && strcmp(method->methodSignature,methodSignature)==0 )
                    {
                        method->vTableIndex = i;
                        //cout<<"found at "<<i<<endl;
                        return method;
                    }
            }
            cout<<"NO METHOD FOUND "<<methodName<<" with signature "<<methodSignature<<" in class"<<className<<endl;
            return NULL;
}
UCSYInterface* UVMClass::getInterface(char *interfaceName)
{
     UCSYInterface *aInterface;          
     UVMClass *currentClass = this;
     
            
     for(int i=0;i<currentClass->sizeOfAllInterfaces;i++)
     {
           aInterface = currentClass->allInterfaces[i];
           if( strcmp( aInterface->interfaceName, interfaceName )==0 )
           {
                return aInterface;
           }
     }
      
                             
     cout<<"No Interface Found "<<interfaceName<< " in class "<< className<<endl; 
     return NULL;
}
UVMField* UVMClass::findStaticField(char *fieldName,char *fieldType)
{
           UVMField *field = NULL;
           for(int i=0;i<noOfFields;i++)
           {
                   field = fields[i];
                   if( isStatic ( field->fieldModifier) && (strcmp(field->fieldName,fieldName)==0) && ( strcmp( field->fieldType,fieldType) ==0))
                   {
                       return field;
                   }
           }
           cout<<"NO SUCH STATIC FIELD "<< fieldName<<endl;
}

UVMField* UVMClass::findInstanceField(char *fieldName,char *fieldType)
{
           UVMField *field = NULL;
           for(int i=0;i<noOfFields;i++)
           {
                   field = fields[i];
                   if( !isStatic ( field->fieldModifier) && (strcmp(field->fieldName,fieldName)==0) && ( strcmp( field->fieldType,fieldType) ==0))
                   {
                       return field;
                   }
           }
           cout<<"NO SUCH INSTANCE FIELD "<< fieldName<<endl;
}

void UVMClass::getField(u1 *fieldOffest,UVMField *field,UCSYValue *operandStack,int &topOfOperandStack)
{
     u1 *offest = fieldOffest;
     ///cout<<"Get Object "<<(int)fieldOffest<<" topOf stack "<<topOfOperandStack<<endl;
     if(isStatic(field->fieldModifier))
     {
        offest    += (field->offest).staticOffest;
        //cout<<"Fuckin here"<<endl;
     }
     else
     {
         //cout<<"This case "<<endl;
         offest    += (field->offest).instanceOffest;
         
     }
     
     UCSYLong longData;
     UCSYDouble doubleData;
     u4 dataTwo[2];
     switch( field->internalType )
     {
             case TYPE_BOOLEAN:
                  operandStack[++topOfOperandStack].intValue = *(bool*)offest;
             break;
 
             case TYPE_BYTE:
                  operandStack[++topOfOperandStack].intValue = *(__int8*) offest;
             break;
             case TYPE_SHORT:
                  operandStack[++topOfOperandStack].intValue = *(__int16*)offest;
             break;
             
             case TYPE_INTEGER:
                  operandStack[++topOfOperandStack].intValue = *(__int32*)offest;
             break;
             
             case TYPE_LONG:
              longData = *(UCSYLong*)offest;
              *(UCSYLong*)dataTwo = *(UCSYLong*)offest;
              ///cout<<"get long "<<longData<<" at offest "<<(int)offest<<endl;
              operandStack[++topOfOperandStack].otherHalf = dataTwo[0];
              operandStack[++topOfOperandStack].otherHalf = dataTwo[1];
              ///cout<<"after get "<<topOfOperandStack<<endl;
             break;
             
             case TYPE_CHAR:
                  operandStack[++topOfOperandStack].intValue = *(char*) offest;
             break;
             
             case TYPE_FLOAT:
                  operandStack[++topOfOperandStack].floatValue = *(float*) offest;
             break;
             
             case TYPE_DOUBLE:
              doubleData = *(UCSYDouble*) offest;
              ///cout<<"get double "<<doubleData<<" at "<<(int)offest<<endl;
              *(UCSYDouble*)dataTwo = doubleData;
              operandStack[++topOfOperandStack].otherHalf = dataTwo[0];
              operandStack[++topOfOperandStack].otherHalf = dataTwo[1];
              
             break;
             
             case TYPE_STRING:
             case TYPE_OBJECT:
             case TYPE_ARRAY:
                  operandStack[++topOfOperandStack].reference =(void*) (*(UCSYInteger*) offest);
             break;
     }

}
void UVMClass::putField(u1 *fieldOffest,UVMField *field,UCSYValue *operandStack,int &topOfStack)
{
     UCSYLong longData;
     UCSYDouble myDouble;
     u4 twoTemp[2];
     ///cout<<"Object put  "<<(int)fieldOffest<<" top "<<topOfStack<<endl;
     u1 *offest = fieldOffest;
     if( isStatic( field->fieldModifier ))
     {
         offest += (field->offest).staticOffest;
     }
     else
     {
      ///cout<<"Put "<<field->fieldName<< " at "<<(field->offest).instanceOffest<<endl;   
      offest    += (field->offest).instanceOffest;
     }
     
     switch( field->internalType )
     {
             case TYPE_BOOLEAN:
                  *(bool*)offest=  operandStack[topOfStack--].intValue;
             break;
             
             case TYPE_BYTE:
                  *(__int8*) offest=(__int8) operandStack[topOfStack--].intValue;
             break;
             
             case TYPE_SHORT:
                  *(__int16*)offest = operandStack[topOfStack--].intValue;
             break;
             
             case TYPE_INTEGER:
//                  cout<<"STORING "<<operandStack[topOfStack].intValue<<endl;
                  *(__int32*)offest = operandStack[topOfStack--].intValue;
             break;
             
             case TYPE_LONG:
              twoTemp[1] =  operandStack[ topOfStack -- ].otherHalf;
              twoTemp[0] =  operandStack[ topOfStack -- ].otherHalf;
              longData = *(UCSYLong*) twoTemp;
              
              *(UCSYLong*) offest= longData;
             break;
             
             case TYPE_CHAR:
                  *(char*) offest= (char) operandStack[topOfStack--].intValue;
             break;
             
             case TYPE_FLOAT:
                  *(float*) offest= operandStack[topOfStack--].floatValue;
             break;
             
             case TYPE_DOUBLE:
              
              twoTemp[1] =  operandStack[ topOfStack -- ].otherHalf;
              twoTemp[0] =  operandStack[ topOfStack -- ].otherHalf;
              myDouble =*(UCSYDouble*) twoTemp;
              
              *(UCSYDouble*) offest = myDouble; 
             break;
             
             case TYPE_STRING:
             case TYPE_OBJECT:
             case TYPE_ARRAY:
                  
                  *(UCSYInteger*) offest=(UCSYInteger) operandStack[topOfStack--].reference;
             break;
     }
}

void UVMClass::getField(UVMObject *object,UVMField *field,UCSYValue *operandStack,int &topOfOperandStack)
{
     
     
     UCSYLong longData;
     UCSYDouble doubleData;
     u4 dataTwo[2];
     switch( field->internalType )
     {
             case TYPE_BOOLEAN:
                  operandStack[++topOfOperandStack].intValue = (bool)field->getFieldByte(object);
             break;
 
             case TYPE_BYTE:
                  operandStack[++topOfOperandStack].intValue = field->getFieldByte(object);
             break;
             case TYPE_SHORT:
                  operandStack[++topOfOperandStack].intValue = field->getFieldShort(object);
             break;
             
             case TYPE_INTEGER:
                  operandStack[++topOfOperandStack].intValue = field->getFieldInteger(object);
             break;
             
             case TYPE_LONG:
              //longData = *(UCSYLong*)offest;
              *(UCSYLong*)dataTwo = field->getFieldLong(object);
              ///cout<<"get long "<<longData<<" at offest "<<(int)offest<<endl;
              operandStack[++topOfOperandStack].otherHalf = dataTwo[0];
              operandStack[++topOfOperandStack].otherHalf = dataTwo[1];
              ///cout<<"after get "<<topOfOperandStack<<endl;
             break;
             
             case TYPE_CHAR:
                  operandStack[++topOfOperandStack].intValue = (char) field->getFieldByte(object);
             break;
             
             case TYPE_FLOAT:
                  operandStack[++topOfOperandStack].floatValue = field->getFieldFloat(object);
             break;
             
             case TYPE_DOUBLE:
              doubleData = field->getFieldDouble(object);
              ///cout<<"get double "<<doubleData<<" at "<<(int)offest<<endl;
              *(UCSYDouble*)dataTwo = doubleData;
              operandStack[++topOfOperandStack].otherHalf = dataTwo[0];
              operandStack[++topOfOperandStack].otherHalf = dataTwo[1];
              
             break;
             
             case TYPE_STRING:
             case TYPE_OBJECT:
             case TYPE_ARRAY:
                  //operandStack[++topOfOperandStack].reference =(void*) (*(UCSYInteger*) offest);
                  operandStack[++topOfOperandStack].reference = field->getFieldRef(object);
             break;
     }

}

void UVMClass::putField(UVMObject *object,UVMField *field,UCSYValue *operandStack,int &topOfStack)
{
     UCSYLong longData;
     UCSYDouble myDouble;
     UCSYInteger integerData;
     UCSYShort shortData;
     UCSYFloat floatData;
     void* refData;
     u4 twoTemp[2];
    // cout<<"Object New  put  top "<<topOfStack<<endl;

     
     switch( field->internalType )
     {
             case TYPE_BOOLEAN:
                  integerData=  operandStack[topOfStack--].intValue;
                  field->putFieldByte(object,(bool)integerData);
             break;
             
             case TYPE_BYTE:
                  integerData=(__int8) operandStack[topOfStack--].intValue;
                  field->putFieldByte(object,(UCSYByte)integerData);
             break;
             
             case TYPE_SHORT:
                  shortData = operandStack[topOfStack--].intValue;
                  field->putFieldShort(object,shortData);
             break;
             
             case TYPE_INTEGER:
                  integerData = operandStack[topOfStack--].intValue;
                  field->putFieldInteger(object,integerData);
             break;
             
             case TYPE_LONG:
              twoTemp[1] =  operandStack[ topOfStack -- ].otherHalf;
              twoTemp[0] =  operandStack[ topOfStack -- ].otherHalf;
              longData = *(UCSYLong*) twoTemp;
              
              field->putFieldLong(object,longData);
             break;
             
             case TYPE_CHAR:
                  integerData= (char) operandStack[topOfStack--].intValue;
                  field->putFieldByte(object,(UCSYByte)integerData);
             break;
             
             case TYPE_FLOAT:
                  floatData= operandStack[topOfStack--].floatValue;
                  field->putFieldFloat(object,floatData);
             break;
             
             case TYPE_DOUBLE:
              
              twoTemp[1] =  operandStack[ topOfStack -- ].otherHalf;
              twoTemp[0] =  operandStack[ topOfStack -- ].otherHalf;
              myDouble =*(UCSYDouble*) twoTemp;
              
              field->putFieldDouble(object, myDouble); 
             break;
             
             case TYPE_STRING:
             case TYPE_OBJECT:
             case TYPE_ARRAY:
                  
                  refData= operandStack[topOfStack--].reference;
                  field->putFieldRef(object,(UVMObject*)refData);
             break;
     }
}

bool UVMClass::isAncestorOrSameClassOf(UVMClass *aClass)
{
     if( strcmp( className,"Object")==0 || strcmp(className,aClass->className)==0)
     {
         return true;
     }
     else
     {
         UVMClass *superClassOfAClass = aClass->superClass;
         while( superClassOfAClass != NULL)
         {
                if( strcmp(className,superClassOfAClass->className)==0)
                {
                    return true;
                }
                superClassOfAClass = superClassOfAClass->superClass;
         }
         return false;
     }
}
bool UVMClass::isInstanceOf(UVMClass *checkClass)
{
     if( strcmp(className,checkClass->className)==0)
     {
           return true;
     }
     if(strcmp(checkClass->className,"Object"))
     {
           return true;                          
     }
     if( isClass(checkClass->classModifier))
     {
         
         UVMClass *parentClass = superClass;
         while( parentClass != NULL)
         {
                if( (strcmp(checkClass->className,parentClass->className))==0)
                {
                    return true;
                }
                parentClass = parentClass->superClass;
         }
         return false;
     }
     else if(isInterface(checkClass->classModifier))
     {
          
          UVMClass *currentClass = this;
          UCSYInterface **currentInterfaces;
          UCSYInterface *aInterface;
          while(currentClass != NULL)
          {
                  int interfaceCount = currentClass->noOfInterfaces;                             
                  currentInterfaces   = currentClass->interfaces;
                  for(int i=0;i<interfaceCount;i++)
                  {
                         aInterface = currentInterfaces[i]; 
                         if(strcmp(aInterface->interfaceName,checkClass->className)==0)
                         {
                                return true;                                                       
                         }
                  }
                  currentClass = currentClass->superClass;           
          }
          return false;
     }
}
UCSYString* ExecutionEnviroment::readStringData()
{
      void* stringEntry = (operandStack[*(topOfStack)]).reference; 
      return (UCSYString*)stringEntry;
}
UCSYInteger ExecutionEnviroment::readIntegerData()
{
}
UCSYLong ExecutionEnviroment::readLongData()
{
}
UCSYFloat ExecutionEnviroment::readFloatData()
{
}
UCSYDouble ExecutionEnviroment::readDoubleData()
{
}

int getDimensionOfArray(char *arrayName)
{
    int dimension = 0;
    int index = 0;
    while(arrayName[index++]== '[')
    {
            dimension ++;                   
    }
    return dimension;
}
//Array Operation
UCSYInteger getIntegerFromArray(UVMObject *arrayObject,int index)
{
     UCSYInteger integerData;
     UCSYInteger* intData = (UCSYInteger*)arrayObject->instanceData;
     integerData = intData[index+1] ;//Because first element is length of array
     return integerData;       
}
UCSYByte    getByteFromArray(UVMObject *arrayObject,int index)
{
        u1* offest = arrayObject->instanceData;
        offest+= 4+ index;
        UCSYByte byte = *(UCSYByte*)offest;
        return byte;    
}

UCSYShort   getShortFromArray(UVMObject *arrayObject,int index)
{
        u1* offest = arrayObject->instanceData;
        offest+= 4+ index*2;
        return *(UCSYShort*)offest;    
      
}

UCSYLong    getLongFromArray(UVMObject *arrayObject,int index)
{
     u1* offest = arrayObject->instanceData;
     offest += 4 + index*8;       
     return *(UCSYLong*)offest;
}

UCSYFloat   getFloatFromArray(UVMObject *arrayObject,int index)
{
     UCSYFloat floatData;
     UCSYFloat* floatDataArr = (UCSYFloat*)arrayObject->instanceData;
     floatData = floatDataArr[index+1] ;//Because first element is length of array
     return floatData;            
}

UCSYDouble  getDoubleFromArray(UVMObject *arrayObject,int index)
{
     u1* offest = arrayObject->instanceData;
     offest += 4 + index*8;       
     return *(UCSYDouble*)offest;
}

UVMObject*  getRefFromArray(UVMObject *arrayObject,int index)
{
     UVMObject* objectData;
     UVMObject** refDataArr = (UVMObject**)arrayObject->instanceData;
     objectData = refDataArr[index+1] ;//Because first element is length of array
     return objectData;                   
}


void setByteToArray(UVMObject *arrayObject,int index,UCSYByte byteValue)
{
     u1 *offest = arrayObject->instanceData;
     offest+= 4 +index;
     *(UCSYByte*)offest = byteValue;
}

void setShortToArray(UVMObject *arrayObject,int index,UCSYShort shortValue)
{
     u1 *offest = arrayObject->instanceData;
     offest+= 4+index*2;
     
     *(UCSYShort*)offest = shortValue;
}

void setIntegerToArray(UVMObject *arrayObject,int index,UCSYInteger integerValue)
{
     ////cout<<"Array Object "<<arrayObject<<endl;
     UCSYInteger* intData = (UCSYInteger*)arrayObject->instanceData;
     ////cout<<"Store at "<<index<<endl;
     intData[index+1] = integerValue;//Because first element is length of array
}

void setLongToArray(UVMObject *arrayObject,int index,UCSYLong longValue)
{
     u1 *offest = arrayObject->instanceData;
     offest += 4+ index*8;
     *(UCSYLong*)offest = longValue;
}

void setFloatToArray(UVMObject *arrayObject,int index,UCSYFloat floatValue)
{
     UCSYFloat* floatData = (UCSYFloat*)arrayObject->instanceData;
     floatData[index+1]   =floatValue;
}

void setDoubleToArray(UVMObject *arrayObject,int index,UCSYDouble doubleValue)
{
     u1 *offest = arrayObject->instanceData;
     offest += 4+ index*8;
     *(UCSYDouble*)offest = doubleValue;
}

void setRefToArray(UVMObject *arrayObject,int index,UVMObject *refValue)
{
     UVMObject **arr = (UVMObject**)arrayObject->instanceData;
     arr[index+1] = refValue;
}


//Put Field and store field


void UVMField::putFieldInteger (UVMObject *object,UCSYInteger integerData)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         ///cout<<"Getting Field Static Integer "<<endl;
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(__int32*)offest = integerData;
     
}
void UVMField::putFieldByte    (UVMObject *object,UCSYByte byteData)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(__int8*)offest = byteData;
}
void UVMField::putFieldShort   (UVMObject *object,UCSYShort shortData)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(__int16*)offest = shortData;
}
void UVMField::putFieldLong    (UVMObject *object,UCSYLong longData)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(__int32*)offest = longData;
}
void UVMField::putFieldFloat   (UVMObject *object,UCSYFloat floatData)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(UCSYFloat*)offest = floatData;
}
void UVMField::putFieldDouble  (UVMObject *object,UCSYDouble doubleData)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(UCSYDouble*)offest = doubleData;
}
void UVMField::putFieldRef     (UVMObject *object,UVMObject *refValue)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest += (this->offest).staticOffest;
     *(UCSYInteger*)offest =(UCSYInteger) refValue;
}

UCSYByte UVMField::getFieldByte(UVMObject *object)
{
      u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return *(UCSYByte*)offest;
}

UCSYShort UVMField::getFieldShort(UVMObject *object)
{
      u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return *(UCSYShort*)offest;
}

UCSYInteger UVMField::getFieldInteger(UVMObject *object)
{
      u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return *(UCSYInteger*)offest;      
}

UCSYLong    UVMField::getFieldLong(UVMObject *object)
{
      u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return *(UCSYLong*)offest;
}

UCSYFloat  UVMField::getFieldFloat(UVMObject *object)
{
      u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return *(UCSYFloat*)offest;     
}

UCSYDouble UVMField::getFieldDouble(UVMObject *object)
{
      u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
     }
     else
     {
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return *(UCSYDouble*)offest;     
}

UVMObject* UVMField::getFieldRef(UVMObject *object)
{
     u1 *offest;
     if( isStatic( fieldModifier))
     {
         offest = object->theClass->staticData;
         
     }
     else
     {
         ///cout<<"This case"<<endl;
         offest = object->instanceData;
     }
     offest+= this->offest.instanceOffest;
      return (UVMObject*)(*(UCSYInteger*)offest);      
}
