#ifndef uvm_h
#include "uvm.h"
#endif 
#ifndef nativeManager_h
#include "nativeManager.h"
#endif

#ifndef library_h
#include "library.h"
#endif

#ifndef methodCallStack_h
#include "methodCallStack.h"
#endif
#include <conio.h>
#include <windows.h>

#ifndef memoryManager_h
#include "memoryManager.h"
#endif

#include <string>
#include <fstream>
//#include "TCPServerSocket.h"
//OXCAFE is in little 
// 0 CA 1:FE
//Long and Double are store in local varible as little endian format
#define readLongFromLocalVar(localVar,index) twoWord[0] = localVar[index].otherHalf;\
                                             twoWord[1] = localVar[index+1].otherHalf;\
                                             index += 2;\
                                             longData = *(UCSYLong*)twoWord;\
                                             
#define readIntegerFromLocalVar(localVar,index) integerData = localVar[index++].intValue;
                                           
#define readDoubleFromLocalVar(localVar,index) twoWord[0] = localVar[index].otherHalf;\
                                               twoWord[1] = localVar[index+1].otherHalf;\
                                               index += 2;\
                                               doubleData=*(UCSYDouble*)twoWord;
                                               
#define readFloatFromLocalVar(localVar,index)  floatData = localVar[index++].floatValue;
UCSYLong longData;
UCSYInteger integerData;
UCSYDouble doubleData;
UCSYFloat  floatData;
u4 twoWord[2];
using namespace std;

/*__int64 getLongFromLocalVar(UCSYValue** localVar,int index)
{
         UCSYLong longData;
         u4 dataTwo [2];
         dataTwo[0] = localVar[0].otherHalf;
         dataTwo[1] = localVar[1].otherHalf;
         longData = *(UCSYLong*)dataTwo;
         return longData;
     
         
}
*/
/*********** This is library of UCSY *************************/

/*******************************************************************************
Native Method implementation of various UCSY API
native method name are given of the following form, UVM Implementor must ahere to 
these naming rule , accroding to these naming rule, native mehtod will be
loaded by the ClassLoader, actually done by ClassParser when parsing methods;

Native Method are statically bind to UVM, further improvement must be made to load
native method from DLL files 

It is the responsiblity of the native method to pop it frame
and push return value if exist to the old frame,and to set topOfOperandStack into
appropriate location
for example method name println(m,)v , class name Console
class Console
{
      public static native void println()
      {
      }
}
native mehtod name = ClassName+ MethodName + MethodSignature
for above method ,its name will be
Consoleprintln(m,)v

********************************************************************************/
//native void println(int a)
char* numberStringInt = new char[25]; //To be used by Integer.toString();

char bufferString[80]; //To be used by Console.readString
void Console_println_integer(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData =readIntegerFromLocalVar(env->localVar, localIndex);
     cout<< integerData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
void Console_println_short(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData =readIntegerFromLocalVar(env->localVar, localIndex);
     cout<< (UCSYShort)integerData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
void Console_println_byte(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData =readIntegerFromLocalVar(env->localVar, localIndex);
     cout<< (int)integerData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void println(boolean b)
void Console_println_boolean(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData = readIntegerFromLocalVar(env->localVar,localIndex);
     cout<<((integerData==1)?"true":"false")<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void prinltn (char c)
void Console_println_char(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger intergerData = readIntegerFromLocalVar(env->localVar,localIndex);
     cout<<(char)integerData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void prinltn(string s)
void Console_println_string(ExecutionEnviroment* env)
{
     UCSYString *str = (UCSYString*)env->localVar[0].reference;
     
     cout<<str->stringData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void println(long l)
void Console_println_long(ExecutionEnviroment* env)
{
     //cout<<"To println long "<<endl;
     int localIndex = 0;
     UCSYLong lData;// = readLongFromLocalVar(env->localVar,localIndex);
     u4 dataTwo [2];
     dataTwo[0] = env->localVar[0].otherHalf;
     dataTwo[1] = env->localVar[1].otherHalf;
     lData = *(UCSYLong*)dataTwo;
     cout<<lData<<endl;
    
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void println(float f)
void Console_println_float(ExecutionEnviroment* env)
{
     int localIndex =0;
     UCSYFloat floatData = readFloatFromLocalVar(env->localVar,localIndex);
     cout<<floatData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void prinltn(double d)
void Console_println_double(ExecutionEnviroment* env)
{
     
     int localIndex = 0;
     UCSYDouble doubleData = readDoubleFromLocalVar(env->localVar,localIndex );
    
     cout<<doubleData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void println()
void Console_println(ExecutionEnviroment* env)
{
     cout<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
void Console_print_integer(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData =readIntegerFromLocalVar(env->localVar, localIndex);
     cout<< integerData;
     MethodCallStack::getMethodCallStack()->popAFrame();
}

//void println(boolean b)
void Console_print_boolean(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData = readIntegerFromLocalVar(env->localVar,localIndex);
     cout<<((integerData==1)?"true":"false");
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void prinltn (char c)
void Console_print_char(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger intergerData = readIntegerFromLocalVar(env->localVar,localIndex);
     cout<<(char)integerData;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void prinltn(string s)
void Console_print_string(ExecutionEnviroment* env)
{
     UCSYString *str = (UCSYString*)env->localVar[0].reference;
     
     cout<<str->stringData;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void println(long l)
void Console_print_long(ExecutionEnviroment* env)
{
     //cout<<"In print long "<<endl;
     int localIndex = 0;
     UCSYLong lData = readLongFromLocalVar(env->localVar,localIndex);
     cout<<lData;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void println(float f)
void Console_print_float(ExecutionEnviroment* env)
{
     int localIndex =0;
     UCSYFloat floatData = readFloatFromLocalVar(env->localVar,localIndex);
     cout<<floatData;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
//void prinltn(double d)
void Console_print_double(ExecutionEnviroment* env)
{
     
     int localIndex = 0;
     UCSYDouble doubleData = readDoubleFromLocalVar(env->localVar,localIndex );
    
     cout<<doubleData;
     MethodCallStack::getMethodCallStack()->popAFrame();
}
void Console_read_integer(ExecutionEnviroment* env)
{
     int data ;
     cin>>data;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     frame->operandStack[++frame->topOfOperandStack].intValue = data;
}
void Console_read_float(ExecutionEnviroment* env)
{
     float data ;
     cin>>data;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     frame->operandStack[++frame->topOfOperandStack].floatValue = data;
}
void Console_read_char(ExecutionEnviroment* env)
{
     char data ;
     cin>>data;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     frame->operandStack[++frame->topOfOperandStack].intValue = (UCSYByte)data;
}
void Console_read_long(ExecutionEnviroment* env)
{
     long data ;
     cin>>data;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     *(UCSYLong*)twoWord =  data;
     frame->operandStack[++frame->topOfOperandStack].intValue = twoWord[0];
     frame->operandStack[++frame->topOfOperandStack].intValue = twoWord[1];
}
void Console_read_double(ExecutionEnviroment* env)
{
     UCSYDouble data ;
     cin>>data;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     *(UCSYDouble*)twoWord =  data;
     frame->operandStack[++frame->topOfOperandStack].intValue = twoWord[0];
     frame->operandStack[++frame->topOfOperandStack].intValue = twoWord[1];
}
void Console_read_string(ExecutionEnviroment* env)
{
     UCSYString *myStr = new UCSYString();
     gets( bufferString );
     
     int length = strlen(bufferString);
     myStr-> stringData = new char[length+1];
     bufferString[length+1] ='\0';
     myStr->length = length;
     strcpy(myStr->stringData,bufferString);

     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
   

     frame->operandStack[++frame->topOfOperandStack].reference = myStr;
}
void MessageBox_show_v(ExecutionEnviroment* env)
{
     UCSYString *str = (UCSYString*)env->localVar[0].reference;
     MessageBox(NULL,str->stringData,"UCSY",NULL);
     MethodCallStack::getMethodCallStack()->popAFrame();
}
void Integer_toString_m(ExecutionEnviroment* env)
{
     int localIndex = 0;
     UCSYInteger integerData = readIntegerFromLocalVar(env->localVar,localIndex);
     UCSYString *str = new UCSYString();
     numberStringInt = itoa(integerData,numberStringInt,10);
     int stringLength = strlen(numberStringInt);
     str->stringData= new char[stringLength+1];
     strcpy(str->stringData,numberStringInt);
     str->stringData[stringLength] ='\0';
     str->length = stringLength;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     frame->operandStack[++frame->topOfOperandStack].reference = str;
}
void NativeTest_printMethod_v(ExecutionEnviroment *env)
{
     UVMObject *object =(UVMObject*) env->localVar[0].reference;//Retrieve this
     UVMClass  *myClass = object->theClass;
     UVMField *field = myClass->findInstanceField("myStr","m");
     UVMObject *str = field->getFieldRef(object);
     UCSYString* myStr = (UCSYString*)str;
     cout<<myStr->stringData<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
}
char buffer[500];
class FileReader
{
      public:
             char * fileName;
             ifstream myFile;
             
             FileReader(char *fName)
             {
                 //cout<<"In this suica"<<endl;            
                 fileName=fName;            
                 myFile.open(fileName);
                 //cout<<"Opening OK "<< (myFile==NULL) <<endl;
             }
             char* readLine()
             {
                   if (myFile.eof())
                   {
                     return NULL;
                   }
                   else
                   {
                       myFile.getline(buffer,449);
                       return buffer;
                   }   
             }
             void close()
             {
                    myFile.close();
             }
};
void FileReader_openFile(ExecutionEnviroment *env)
{
     UVMObject *object =(UVMObject*) env->localVar[0].reference;//Retrieve this
     UVMClass  *myClass = object->theClass;
     UVMField *field = myClass->findInstanceField("fileName","m");
     
     UCSYString *fileName =(UCSYString*) field->getFieldRef(object);
     //cout<<"File Name is  "<<fileName->stringData<<endl;
     field = myClass->findInstanceField("nativeFile","Object");
     FileReader *nativeFileReader = new FileReader(fileName->stringData);
     field->putFieldRef(object,(UVMObject*)nativeFileReader);
     ///cout<<"OK constructed"<<endl;
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
}
void FileReader_readLine(ExecutionEnviroment *env)
{
     UVMObject *object =(UVMObject*) env->localVar[0].reference;//Retrieve this
     UVMClass  *myClass = object->theClass;
     UVMField *field = myClass->findInstanceField("nativeFile","Object");
     FileReader* nativeFileReader = (FileReader*)field->getFieldRef(object);
     char *data = nativeFileReader->readLine();
     UCSYString *line ;
     
     if( data == NULL)
     {
         line = NULL;
     }
     else
     {
         line = new UCSYString();     
         int length = strlen(data);
         line->length = length;
         line->stringData= new char[length+1];
         strcpy(line->stringData, data);
         line->stringData[length] = '\0';
         ///cout<<"Reading "<< data<<endl;
     }
     
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     frame->operandStack[++frame->topOfOperandStack].reference = line;
}
class TCPClientSocket
{
      SOCKET clientSocket;
      SOCKADDR_IN clientAddress;
public:
       TCPClientSocket()
       {
       }
       TCPClientSocket(SOCKET clSocket,SOCKADDR_IN address)
       {
              clientSocket = clSocket;
              clientAddress = address;                
       }
       void sendData(char *str)
       {
            int len = strlen(str);
            //cout<<"Sending "<<len<<endl;
            int retValue;
            retValue = send(clientSocket,str,len,0);
       }
       void close()
       {
            closesocket(clientSocket);
       }
       
};
class TCPServerSocket
{
    static bool isInitialized ;
    WSADATA wsaData;
    SOCKET serverSocket;
    SOCKET clientSocket;
    SOCKADDR_IN serverAddr;
    SOCKADDR_IN clientAddr;
    int clientAddrLen;
    int serverPort;
    
public:
       TCPServerSocket();
       void initializeSocket();
       void setServerPort(int portNo);
       void bindAndListen();
       TCPClientSocket* acceptConnection(); 
       int sendData(char *str);
};
bool TCPServerSocket::isInitialized = false;

TCPServerSocket::TCPServerSocket()
{
       if(!isInitialized)
       {
           WSAStartup(MAKEWORD(2,2),&wsaData);              
           isInitialized = true;              
       }  
    serverSocket = socket(AF_INET,SOCK_STREAM, IPPROTO_TCP);
    serverAddr.sin_family = AF_INET;
    //serverAddr.sin_port   = htons(port);
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
                                
}

void TCPServerSocket::setServerPort(int portNo)
{
     serverAddr.sin_port   = htons(portNo);
}
void TCPServerSocket::initializeSocket()
{
}
int TCPServerSocket::sendData(char *str)
{
    
}
void TCPServerSocket::bindAndListen()
{
    bind(serverSocket,(SOCKADDR*)&serverAddr,sizeof(serverAddr));
    listen(serverSocket,100);
}
TCPClientSocket* TCPServerSocket::acceptConnection()
{
     int clientAddrLen;            
     clientSocket = accept(serverSocket,(SOCKADDR*)&clientAddr,&clientAddrLen);            
     TCPClientSocket* client = new TCPClientSocket(clientSocket,clientAddr);
     
     return client;
}
/*
int main()
{
    
    /*
    WSADATA wsaData;
    SOCKET listeningSocket;
    SOCKET newConnection;
    SOCKADDR_IN serverAddr;
    SOCKADDR_IN clientAddr;
    int port = 8080;
    char *buffer = new char[32];
    int byteReceived;
    
    char *data="HTTP/1.0 200 OK\r\nContent-Type:text/html\r\nContent-Length:27\r\n\r\n<html><h1>Fuck</h1></html>\n";
    WSAStartup(MAKEWORD(2,2),&wsaData);
    
    listeningSocket = socket(AF_INET,SOCK_STREAM, IPPROTO_TCP);
    serverAddr.sin_family = AF_INET;
    serverAddr.sin_port   = htons(port);
    serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
    
    bind(listeningSocket,(SOCKADDR*)&serverAddr,sizeof(serverAddr));
    listen(listeningSocket,100);
    
    int clientAddrLen;
    while(true)
    {
    newConnection = accept(listeningSocket,(SOCKADDR*)&clientAddr,&clientAddrLen);
    /*while(true)
    {
           byteReceived = recv(newConnection,buffer,SIZE-1,0);      
           if( byteReceived <=0)    
           {
               cout<<"Fucking";
              // goto send;
               break;
               //cout<<"";
           }
           buffer[ byteReceived ] ='\0';
           cout<<buffer;
    }
    
    cout<<"Ok sending ";
    send:
    int strLen = strlen(data);
    int s = send(newConnection,data,strLen,0);
    if(s!= strLen)
           cout<<"Send Failed";
    //closesocket(newConnection);
    }
    //getch();
    
 */
 /*
    TCPServerSocket socket ;
    socket.setServerPort(80);
    socket.bindAndListen();
    TCPClientSocket* client;
    char* str =new char[300];
    strcpy(str,"HTTP/1.0 200 OK\r\nContent-Type:text/html\r\nContent-Length:27\r\n\r\n<html><h1>Fuck</h1></html>\n");
    while(true)
    {
               cout<<"";
               client = socket.acceptConnection();
               
               client->sendData(str);
               //client->close();
    }
    
    
    WSACleanup();
    return 0;
}
*/

void TCPServerSocket_initialize_v(ExecutionEnviroment *env)
{
     //TCPServerSocket *tcpServerSocket = new TCPServerSocket();
     UVMObject *object =(UVMObject*) env->localVar[0].reference;//Retrieve this
     UVMClass  *myClass = object->theClass;
     UVMField *field = myClass->findInstanceField("portNo","i");
     
     UCSYInteger portNo= field->getFieldInteger(object);
     //cout<<"Port No is "<<portNo<<endl;
     TCPServerSocket *nativeSocket = new TCPServerSocket();
     nativeSocket->setServerPort(portNo);
     nativeSocket->bindAndListen();
     
     UVMObject *nativeSocketObject = (UVMObject*)nativeSocket;
     field = myClass->findInstanceField("nativeSocket","Object");
     field->putFieldRef(object,nativeSocketObject);
     
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();

}

void TCPServerSocket_getConnection_TCPClientSocket(ExecutionEnviroment *env)
{
     UVMObject *object =(UVMObject*) env->localVar[0].reference;//Retrieve this
     UVMClass  *myClass = object->theClass;
     UVMField *field = myClass->findInstanceField("nativeSocket","Object");
     UVMObject *nativeSocketObject = field->getFieldRef(object);
     TCPServerSocket *serverSocket = (TCPServerSocket*)nativeSocketObject;
     
     TCPClientSocket* clientSocket = serverSocket->acceptConnection();
     
     UVMClass* clientSocketClass =  UVMClassManager::getClassManager()->getAClass("TCPClientSocket");
     UVMObject *clientSocketObject = MemoryManager::getMemoryManager()->allocateObject(clientSocketClass);
     
     field = clientSocketClass->findInstanceField("nativeSocket","Object");
     field->putFieldRef(clientSocketObject,(UVMObject*)clientSocket);
     
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     ///cout<<"Top of operand Stack is "<<frame->topOfOperandStack<<endl;
     frame->operandStack[++frame->topOfOperandStack].reference = clientSocketObject;
}

void TCPClientSocket_sendData_v(ExecutionEnviroment *env)
{
     UVMObject* object =(UVMObject*) env->localVar[0].reference;
     
     UVMClass* clientSocketClass =  env->currentClass;
     UVMField *field = clientSocketClass->findInstanceField("nativeSocket","Object");
     UVMObject *nativeSocketObject = field->getFieldRef(object);
     
     char *str = ((UCSYString*)env->localVar[1].reference)->stringData;
     TCPClientSocket *cppTCPClientSocket = (TCPClientSocket*)nativeSocketObject;
     cppTCPClientSocket->sendData(str);
     //UVMObject 
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
}


void String_getLength_i(ExecutionEnviroment *env)
{
     UVMObject* object =(UVMObject*) env->localVar[0].reference;
     UVMField *field = object->theClass->findInstanceField("data","m");
     UCSYString *stringData = (UCSYString*)field->getFieldRef(object);
     int length = stringData->length;
     
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     ///cout<<"Top of operand Stack is "<<frame->topOfOperandStack<<endl;
     frame->operandStack[++frame->topOfOperandStack].intValue = length;
}

void String_charAt_c(ExecutionEnviroment *env)
{
     UVMObject* object =(UVMObject*) env->localVar[0].reference;
     UVMField *field = object->theClass->findInstanceField("data","m");
     UCSYString *stringData = (UCSYString*)field->getFieldRef(object);
     int localVarIndex = 1;
     int index = readIntegerFromLocalVar(env->localVar,localVarIndex);
     
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     ///cout<<"Top of operand Stack is "<<frame->topOfOperandStack<<endl;
     frame->operandStack[++frame->topOfOperandStack].intValue = (char)stringData->stringData[index];
}
void String_concat_m(ExecutionEnviroment *env)
{
     UVMObject* object =(UVMObject*) env->localVar[0].reference;
     UVMField *field = object->theClass->findInstanceField("data","m");
     UCSYString *stringData = (UCSYString*)field->getFieldRef(object);
     
     UCSYString *parString =(UCSYString*) env->localVar[1].reference;
     
     int newStringLength = stringData->length + parString->length +1;
     char *newCString = new char[newStringLength];
     strcpy(newCString,stringData->stringData);
     strcat(newCString,parString->stringData);
     newCString[newStringLength-1] ='\0';
     UCSYString  *newString = new UCSYString;
     newString->stringData = newCString;
     newString->length = newStringLength -1;
     
     MethodCallStack::getMethodCallStack()->popAFrame();
     MethodFrame *frame = MethodCallStack::getMethodCallStack()->getCurrentFrame();
     ///cout<<"Top of operand Stack is "<<frame->topOfOperandStack<<endl;
     frame->operandStack[++frame->topOfOperandStack].reference = newString;
}
void initializeNativeMethod()
{
      NativeManager* nativeManager = NativeManager::getNativeManager();
      nativeManager->addNativeMethod("Console","println","(m,)v",Console_println_string);
      nativeManager->addNativeMethod("Console","println","(l,)v",Console_println_long);
      nativeManager->addNativeMethod("Console","println","(i,)v",Console_println_integer);
      nativeManager->addNativeMethod("Console","println","(b,)v",Console_println_byte);
      nativeManager->addNativeMethod("Console","println","(s,)v",Console_println_short);
      nativeManager->addNativeMethod("Console","println","(d,)v",Console_println_double);
      nativeManager->addNativeMethod("Console","println","(f,)v",Console_println_float);
      nativeManager->addNativeMethod("Console","println","(t,)v",Console_println_boolean);
      nativeManager->addNativeMethod("Console","println","(c,)v",Console_println_char);
      nativeManager->addNativeMethod("Console","println","()v",Console_println);
      
      nativeManager->addNativeMethod("Console","print","(m,)v",Console_print_string);
      nativeManager->addNativeMethod("Console","print","(l,)v",Console_print_long);
      nativeManager->addNativeMethod("Console","print","(i,)v",Console_print_integer);
      nativeManager->addNativeMethod("Console","print","(d,)v",Console_print_double);
      nativeManager->addNativeMethod("Console","print","(f,)v",Console_print_float);
      nativeManager->addNativeMethod("Console","print","(t,)v",Console_print_boolean);
      nativeManager->addNativeMethod("Console","print","(c,)v",Console_print_char);
    
      nativeManager->addNativeMethod("Console","readInt","()i",Console_read_integer);
      nativeManager->addNativeMethod("Console","readFloat","()f",Console_read_float);
      nativeManager->addNativeMethod("Console","readChar","()c",Console_read_char);
      nativeManager->addNativeMethod("Console","readLong","()l",Console_read_long);      
      nativeManager->addNativeMethod("Console","readDouble","()d",Console_read_double);      
      nativeManager->addNativeMethod("Console","readString","()m",Console_read_string);      
      
      nativeManager->addNativeMethod("FileReader","openFile","()v",FileReader_openFile);     
      nativeManager->addNativeMethod("FileReader","readLine","()m",FileReader_readLine);      
       
      nativeManager->addNativeMethod("MessageBox","show","(m,)v",MessageBox_show_v);
      
      nativeManager->addNativeMethod("Integer","toString","(i,)m",Integer_toString_m);
      
      nativeManager->addNativeMethod("NativeTest","printMethod","()v",NativeTest_printMethod_v);
      
      nativeManager->addNativeMethod("TCPServerSocket","initialize","()v",TCPServerSocket_initialize_v);
      nativeManager->addNativeMethod("TCPServerSocket","getConnection","()TCPClientSocket",TCPServerSocket_getConnection_TCPClientSocket);
      nativeManager->addNativeMethod("TCPClientSocket","sendData","(m,)v",TCPClientSocket_sendData_v);
      //String Class
      nativeManager->addNativeMethod("String","getLength","()i",String_getLength_i);
      nativeManager->addNativeMethod("String","charAt","(i,)c",String_charAt_c);
      nativeManager->addNativeMethod("String","concat","(m,)m",String_concat_m);
}
