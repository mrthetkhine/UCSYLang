#ifndef uvm_h
#include "uvm.h"
#endif

#ifndef classParser_h
#include "classParser.h"
#endif

#ifndef nativeManager_h
#include "nativeManager.h"
#endif

#include <iostream>
#include <conio.h>
using namespace std;
//int NATIVE = 1<<11;
bool isNative(int attribute)
{
     if( (attribute & NATIVE ) == NATIVE )
     {
         return true;
     }
     else
     {
         return false;
     }
}

UVMClassParser* UVMClassParser::parser = NULL;
UVMClassParser* UVMClassParser::getClassParser()
{
     if(parser == NULL)           
     {
               parser = new UVMClassParser();
     }
     return parser;
}
UVMClass* UVMClassParser::parseTheClass(char* className)
{

           UVMClass *theClass;
           ///cout<<"Length of "<<strlen(className)+7<<endl;
           int len = strlen(className)+7;
           char *myClassName = new char[len];
           
           //strcpy(myClassName,"O");
           //cout<<"Aha! "<<className<<endl;
           strcpy(myClassName,className);
           strcat(myClassName,".ucode");
           
           FILE *classFile = fopen(myClassName,"rb");
           if( classFile == NULL )
           {
               cout<<"UVM Error Cannot find the class name "<<className;
               return NULL;
           }

           theClass = new UVMClass;
           theClass->sizeOfStaticVar = 0;
           theClass->sizeOfInstanceVar = 0;
          /// theClass->className.length = strlen(className);
           ///cout<<"Too cool"<<endl;
           ///theClass->className.stringData= myClassName;
           
           UCSYInteger magicCode ;
           readInteger(classFile,magicCode);
           if(magicCode != 0xBABEBABE)
           {
                 cout<<"UVM Incorrect Magic code in ucode file "<<className<<endl;
                 getch();
                 exit(0);
           }
           UCSYByte version;
           readByte(classFile,version);
           
           theClass->constantPool = parseConstantPool(classFile,theClass);
           //Parse Fileds
           
           u2 classModifier ;
           read_U2_UShort(classFile,classModifier);
           theClass->classModifier = classModifier;
           
           u2 thisClassIndex;
           u2 superClassIndex;       
           read_U2_UShort( classFile,thisClassIndex );
           
           UCSYString *strEntry = (UCSYString*)(theClass->constantPool[ thisClassIndex ]->info);
           
           theClass->className = strEntry->stringData; 
                                       
           read_U2_UShort( classFile,superClassIndex);
           
           strEntry = (UCSYString*)((theClass->constantPool[ superClassIndex ])->info);
           theClass->superClassName = strEntry->stringData; 
           
           theClass->interfaces = parseInterfaces( classFile, theClass);
           theClass->fields = parseFields(classFile,theClass);
           
           theClass->methods = parseMethods(classFile,theClass);
           
           return theClass;
}

ConstantPoolEntry** UVMClassParser::parseConstantPool(FILE *classFile,UVMClass *theClass)
{
    u2 noOfEntry ;
    read_U2_UShort(classFile,noOfEntry);
    ///cout<<"NO of Enrty in constant Pool "<<noOfEntry<<endl;
    ///cout<<"OK in this place";
    //Error in there
    ConstantPoolEntry  **constantPool= new (ConstantPoolEntry*[noOfEntry]);
    if(constantPool == NULL)
                    cout<<"OK allocated";
    theClass->constantPoolEntryCount = noOfEntry;
    
    int stringIndex;
    ///cout<<"No of entry "<< noOfEntry;
    for(int i=0;i<noOfEntry;i++)
    {
       ///     cout<<"Entry "<<i;
            char *temp;
            ConstantPoolEntry *cpEntry;
            u1 entryTag ;
            char arr[2];
            
            READ_U1(classFile,entryTag);
            
            
            cpEntry = new ConstantPoolEntry;
            cpEntry->tag = entryTag;
            
            MethodRefEntry *methodInfoEntry;
            FieldRefEntry *fieldInfoEntry;
            InterfaceMethodRefEntry *interfaceMethodInfoEntry;
            ClassRefEntry      *classRef;
            UCSYInteger *integerEntry;
            UCSYLong    *longEntry;
            UCSYFloat   *floatEntry;
            UCSYDouble  *doubleEntry;
            
            switch(entryTag)
            {
               case CPOOL_STRING_REF:
                    /// cout<<"string\n ";
                    cpEntry->tag = entryTag;
                    cpEntry->info =(void *) new UCSYString;
                     u2 stringLength ;
                     read_U2_UShort(classFile,stringLength);
                    ((UCSYString*)(cpEntry->info))->length = stringLength;
                                        
                    temp = new char[stringLength +1];
                    temp[stringLength] ='\0';
                    fread(temp,sizeof(char),stringLength,classFile);
                    ((UCSYString*)(cpEntry->info))->stringData = temp;
                                        
                                        //cout<<((UCSYString*)(cpEntry->info))->stringData<<endl;
               break;
              

             case CPOOL_INTEGER_REF:
                 /// cout<<"Integer \n";
                  UCSYInteger integerData ;
                  readInteger(classFile,integerData);
                  integerEntry = new UCSYInteger;
                  *integerEntry = integerData;
                  cpEntry->info = integerEntry;
                  ///cout<<integerData<<endl;
             break;
             case CPOOL_LONG_REF:
                  ///cout<<"Long \n";
                  UCSYLong longData;
                  readLong(classFile,longData);
                  longEntry = new UCSYLong;
                  *longEntry = longData;
                  cpEntry->info = longEntry;
                  ///cout<<longData<<endl;
             break;
             case  CPOOL_FLOAT_REF:
                  ///cout<<"Float \n";
                  UCSYFloat floatData;
                  readFloat(classFile,floatData);
                  floatEntry = new UCSYFloat;
                  *floatEntry = floatData;
                  cpEntry->info =  floatEntry;
                  ///cout<<floatData<<endl;
             break;                                                                              
             case CPOOL_DOUBLE_REF:
                   ///cout<<"Double \n";
                   UCSYDouble doubleData;
                   readDouble(classFile,doubleData);
                   doubleEntry = new UCSYDouble;
                   *doubleEntry = doubleData;
                   cpEntry->info = doubleEntry;
                   ///cout<<"Parse Double Data "<<doubleData<<endl;
             break;
             case CPOOL_METHOD_REF:
                    ///cout<<"MethodRef"<<endl;
                    u2 classIndexOfMethod;
                    u2 nameIndexOfMethod;
                    u2 signatureIndexOfMethod;
                    read_U2_UShort(classFile,classIndexOfMethod);
                    read_U2_UShort(classFile,nameIndexOfMethod);
                    read_U2_UShort(classFile,signatureIndexOfMethod);
                            
                    methodInfoEntry = new MethodRefEntry;
                    methodInfoEntry->classIndex = classIndexOfMethod;
                    methodInfoEntry->nameIndex  = nameIndexOfMethod;
                    methodInfoEntry->signatureIndex = signatureIndexOfMethod;
                            
                    //methodInfoEntry->className =  ((UCSYString*)constantPool[classIndexOfMethod]->info)->stringData;
                    methodInfoEntry->methodName= ((UCSYString*)constantPool[nameIndexOfMethod]->info)->stringData;
                    methodInfoEntry->signature = ((UCSYString*)constantPool[signatureIndexOfMethod]->info)->stringData;
                    stringIndex = ((ClassRefEntry*)constantPool[ classIndexOfMethod]->info)->classNameIndex;
                    
                    methodInfoEntry->className = ((UCSYString*)constantPool[ stringIndex]->info)->stringData;       
                    ///cout<<"Parsing method ref "<<methodInfoEntry->className<<endl;
                    cpEntry->info = methodInfoEntry;
             break;
             case CPOOL_INTERFACE_METHOD_REF:
                  ///cout<<"interface Method Ref\n";
                    //UCSYShort classIndexOfInterfaceMethod;
                    //UCSYShort nameIndexOfInterfaceMethod;
                    //UCSYShort signatureIndexOfMethod;
                    read_U2_UShort(classFile,classIndexOfMethod);
                    read_U2_UShort(classFile,nameIndexOfMethod);
                    read_U2_UShort(classFile,signatureIndexOfMethod);
                            
                    interfaceMethodInfoEntry = new InterfaceMethodRefEntry;
                    interfaceMethodInfoEntry->classIndex = classIndexOfMethod;
                    interfaceMethodInfoEntry->nameIndex  = nameIndexOfMethod;
                    interfaceMethodInfoEntry->signatureIndex = signatureIndexOfMethod;
                            
                    //interfaceMethodInfoEntry->className =  ((UCSYString*)constantPool[classIndexOfMethod]->info)->stringData;
                    interfaceMethodInfoEntry->methodName= ((UCSYString*)constantPool[nameIndexOfMethod]->info)->stringData;
                    interfaceMethodInfoEntry->signature = ((UCSYString*)constantPool[signatureIndexOfMethod]->info)->stringData;
                    
                    stringIndex = ((ClassRefEntry*)constantPool[ classIndexOfMethod]->info)->classNameIndex;
                    
                    interfaceMethodInfoEntry->className = ((UCSYString*)constantPool[ stringIndex]->info)->stringData;               
                    cpEntry->info = interfaceMethodInfoEntry;                          
             break;
                       
             case CPOOL_FIELD_REF:
                   ///cout<<"FieldRef"<<endl;
                   u2 classIndexOfField;
                   u2 nameIndexOfField;
                   u2 typeIndexOfField;
                   read_U2_UShort(classFile,classIndexOfField);
                   read_U2_UShort(classFile,nameIndexOfField);
                   read_U2_UShort(classFile,typeIndexOfField);
                            
                   fieldInfoEntry = new FieldRefEntry;
                   fieldInfoEntry->classIndex = classIndexOfField;
                   fieldInfoEntry->nameIndex  = nameIndexOfField;
                   fieldInfoEntry->typeIndex  = typeIndexOfField;
                            
                  // fieldInfoEntry->className = ((UCSYString*)constantPool[classIndexOfField]->info)->stringData;
                   fieldInfoEntry->fieldName = ((UCSYString*)constantPool[nameIndexOfField]->info)->stringData;
                   fieldInfoEntry->fieldType = ((UCSYString*)constantPool[typeIndexOfField]->info)->stringData;
                   classRef = (ClassRefEntry*)( constantPool[ classIndexOfField ]->info);         
                   fieldInfoEntry->className = classRef->className;
                   cpEntry->info = fieldInfoEntry;
             break;
             case CPOOL_CLASS_REF:
                  //cout<<"class\n";
                  u2 classNameIndex;
                  read_U2_UShort(classFile,classNameIndex);
                  
                  classRef = new ClassRefEntry;
                  classRef->classNameIndex = classNameIndex;
                  
                  classRef->className = ((UCSYString*)constantPool[ classNameIndex ]->info)->stringData;
                  cpEntry->info = classRef;
                  //cout<<"ClassRef Entry "<<classRef->className<<endl;
                  
             break;
             default:
                               cout<<"Error in reading constantpool tag ";
            }      
            constantPool[i] = cpEntry; 
    }//end of for  
    return constantPool;        
}

UCSYInterface** UVMClassParser::parseInterfaces(FILE *classFile, UVMClass *theClass)
{
      u2 interfaceCount;
      read_U2_UShort(classFile,interfaceCount);
      theClass->noOfInterfaces = interfaceCount;
      ///cout<<"There are "<<interfaceCount<<" interfaces \n";
      UCSYInterface **interfaces = new (UCSYInterface*[ interfaceCount ]); 
      UCSYInterface *ucsyInterface;
      u2 interfaceNameIndex;
      UCSYString *stringEntry;
      for(int i=0;i< interfaceCount;i++)
      {
              ucsyInterface = new UCSYInterface;
              read_U2_UShort(classFile,interfaceNameIndex);
              stringEntry = (UCSYString*)(theClass->constantPool[ interfaceNameIndex ]->info);
              ucsyInterface->interfaceName = stringEntry->stringData;
              ///cout<<"Interface name "<< ucsyInterface->interfaceName<<"\n";
              interfaces[ i ] = ucsyInterface;
      }           
      return interfaces;
}

UVMField** UVMClassParser::parseFields(FILE *classFile, UVMClass *theClass)
{
      u2 noOfFields ;
      read_U2_UShort(classFile,noOfFields);    
      theClass->noOfFields = noOfFields;
      UVMField **fields = new (UVMField*[noOfFields]);
      ConstantPoolEntry *entry;
      UCSYString *stringEntry;
      UVMField *aField;
      for(int i=0;i<noOfFields;i++)
      {
            aField = new UVMField;
            u2 fieldModifier;
            u2 indexOfFieldName;
            u2 indexOfFieldType;
            
            read_U2_UShort( classFile, fieldModifier);
            read_U2_UShort( classFile, indexOfFieldName );
            read_U2_UShort( classFile, indexOfFieldType );
            
            aField->fieldModifier = fieldModifier;
            
            entry = theClass->constantPool[ indexOfFieldName ];
            stringEntry = (UCSYString*)entry->info;
            aField->fieldName = stringEntry->stringData;
            
            entry = theClass->constantPool[ indexOfFieldType ];
            stringEntry = (UCSYString*) entry->info;
            aField->fieldType = stringEntry->stringData;
            aField->internalType = getTypeOfTypeName( aField->fieldType );
            fields[i] = aField;
            ///cout<<"Filed name "<<aField->fieldName <<" type "<<aField->fieldType<<endl; 
      }                 
      return fields;                     
}

UVMMethod** UVMClassParser::parseMethods(FILE *classFile, UVMClass *theClass)
{
      u2 noOfMethods ;
      read_U2_UShort(classFile,noOfMethods);       
      UVMMethod **methods = new UVMMethod*[ noOfMethods ];
      UVMMethod *aMethod;
      theClass->noOfMethods = noOfMethods;
      ///cout<<"No of methods "<<noOfMethods<<endl;
      for(int i = 0 ; i < noOfMethods; i++)
      {
              aMethod = new UVMMethod;
              aMethod->parentClass = theClass;
              u2 methodModifier;
              u2 methodNameIndex;
              u2 methodProtocolIndex;
              u2 sizeOfArgumentsInWord;
              u2 sizeOfLocalVariablesInWord;
              u2 maxSizeOfOperandStackInWord;
              u2 noOfCatchEntries;
              u2 lengthOfMethodCode;
              
              read_U2_UShort( classFile, methodModifier );
              read_U2_UShort( classFile, methodNameIndex );
              read_U2_UShort( classFile, methodProtocolIndex );
              read_U2_UShort( classFile, sizeOfArgumentsInWord );
              read_U2_UShort( classFile, sizeOfLocalVariablesInWord );
              read_U2_UShort( classFile, maxSizeOfOperandStackInWord );
              read_U2_UShort( classFile, noOfCatchEntries);
              read_U2_UShort( classFile, lengthOfMethodCode );
              
              aMethod->methodModifier = methodModifier;
              aMethod->maxNoOfLocalArray = sizeOfLocalVariablesInWord;
              aMethod->maxSizeOfOperandStack = maxSizeOfOperandStackInWord;
              aMethod->argSize = sizeOfArgumentsInWord;
              aMethod->noOfCatchEntries = noOfCatchEntries;
              
              
              UCSYString *strEntry = (UCSYString*)theClass->constantPool[ methodNameIndex ]->info;
              aMethod->methodName = strEntry->stringData;
              strEntry = (UCSYString*)theClass->constantPool[ methodProtocolIndex ]->info;
              aMethod->methodSignature = strEntry->stringData;
              ///cout<<"Parsing class "<<theClass->className<<"  "<< aMethod->methodName<<" argSize "<<aMethod->argSize<<endl;
              UCSYCode* methodCode = new UCSYCode;
                          
              methodCode->lengthOfCode = lengthOfMethodCode;
              methodCode->code = new u1[ lengthOfMethodCode ];
              fread(methodCode->code,sizeof(u1),lengthOfMethodCode,classFile);
              ///cout<<" method code "<<(int)methodCode->code[0]<<" "<< theClass->className<<endl;
              aMethod->methodCode = methodCode;
              if( isNative(aMethod->methodModifier))
              {
                  NativeManager* nativeManager = NativeManager::getNativeManager();
                  aMethod->nativeMethodCode = nativeManager->getNativeMethod(theClass->className,aMethod->methodName,aMethod->methodSignature);
              }
              ///cout<<" Method Name "<<aMethod->methodName<<" Sig "<<aMethod->methodSignature<<endl;
              CatchEntry **catchTable = NULL;
              if(noOfCatchEntries>0)
              {
                     catchTable = new CatchEntry*[ noOfCatchEntries ];
              }
              CatchEntry *aCatchEntry;
              for(int j=0;j<noOfCatchEntries;j++)
              {
                      ///cout<<"Parsing No of Catch Entries "<<noOfCatchEntries<<endl;
                      aCatchEntry = new CatchEntry;
                      aCatchEntry->exceptionClass = NULL;
                      UCSYShort from,to,target,exceptionClassIndex;
                      read_U2_UShort( classFile, from );
                      read_U2_UShort( classFile, to );
                      read_U2_UShort( classFile, target );
                      read_U2_UShort( classFile, exceptionClassIndex );
                      
                      aCatchEntry->from = from;
                      aCatchEntry->to   = to;
                      aCatchEntry->target = target;
                      aCatchEntry->exceptionClassIndex = exceptionClassIndex;
                      catchTable[j] = aCatchEntry;
              }                       
              aMethod->catchTables = catchTable;
              methods[ i ] = aMethod;
              
      }
      return methods;
}

