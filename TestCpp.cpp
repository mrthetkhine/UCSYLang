#include <iostream>
using namespace std;
#include <conio.h>
#include "uvm.h"
class Dynamic
{
      Dynamic()
      {
           for(int i=0;i<100;i++)
           {
               ptrtable[i]= NULL;
           }
      }
      public :
             int *ptrtable[100];
             
             static Dynamic* instance;
             static Dynamic* getInstance();
             
};
Dynamic* Dynamic::instance = NULL;
Dynamic* Dynamic::getInstance()
{
         if( instance == NULL)
         {
             instance = new Dynamic();
         }
         return instance;
}
int main()
{
    Dynamic *ptr = Dynamic::getInstance();
    //int *p = ptr->ptrtable[1];
   int *p = ptr->ptrtable[1];
   cout<<*p;
    getch();
    return 0;
}
