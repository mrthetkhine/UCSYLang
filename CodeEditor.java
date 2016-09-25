import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.SimpleAttributeSet;
import java.util.*;
class CodeEditor extends JTextPane 
{
	
	MainIDE fatherIDE ;
	ReadXML config = new ReadXML("Config.xml");
	ArrayList keywordList;
	ArrayList operatorList;
	ArrayList puncutatorList;
	JLabel lineNo;
	State currentState = State.START;
	MyKeyListener listener = new MyKeyListener();
	int tokenStart;
	int tokenEnd;
	String token;
	File source;
	static MutableAttributeSet PLAIN = new SimpleAttributeSet();
	static MutableAttributeSet COMMENT = new SimpleAttributeSet();
	static MutableAttributeSet KEYWORD = new SimpleAttributeSet();
	static MutableAttributeSet OPERATOR = new SimpleAttributeSet();
	static MutableAttributeSet NUMBER = new SimpleAttributeSet();
	static MutableAttributeSet PUNCUTATOR = new SimpleAttributeSet();
	static MutableAttributeSet STRING     = new SimpleAttributeSet();
	static 
	{
		StyleConstants.setForeground(PLAIN, Color.BLACK);
		StyleConstants.setForeground(COMMENT , Color.PINK );
		StyleConstants.setForeground(KEYWORD, Color.BLUE);
		StyleConstants.setForeground(OPERATOR, Color.RED);
		StyleConstants.setForeground(NUMBER, Color.GREEN );
		StyleConstants.setForeground(PUNCUTATOR,new Color(220,22,177));
		StyleConstants.setForeground(STRING,new Color(40,200,200));
		
	}
	
	void setSourceFile(File f)
	{
		source = f;
	}
	File getSourceFile()
	{
		return source;
	}
	
	public void init()
	{
		//Font f = new Font("Courier New",Font.PLAIN,15);
		//setFont(f);
		
		setFont(fatherIDE.selectedFont);
		
		keywordList = config.getKeywordList();
		operatorList = config.getOperatorList();
		puncutatorList = config.getPuncutatorList();
		
		listener.setCodeEditor(this);
		addKeyListener(listener);
		//this.setHighlighter(new Highlighter()
			
	}
	public void process()
	{
		listener.process(1);
	}
	void setLineNoLabel(JLabel l)
	{
		lineNo = l;
	}
	class MyKeyListener extends KeyAdapter
	{
		CodeEditor editor ;
		void setCodeEditor(CodeEditor ed)
		{
			editor = ed;
		}
		String toReverse(String text)
		{
			String rev="";
			for(int i =text.length()-1;i>=0;i--)
				rev+= text.charAt(i);
			
			return rev;
		}
		public void keyReleased(KeyEvent ke)
		{
			int position=getCaretPosition();
			/*	if(ke.isActionKey()/*|| ke.getKeyCode()== ke.VK_BACK_SPACE /*|| 
				ke.getKeyCode()==ke.VK_HOME || ke.getKeyCode()==ke.VK_END*/
				//)
			/*		return;
			*/	
			//if(ke.getKeyCode()== ke.VK_DELETE)
			//	return;
			Point p = getDocumentLineAndCol();
			lineNo.setText("Line " +p.y  +"   Col "+p.x );
			if(ke.isActionKey() )
				return;	
			//JEditorPane jp = editor;
			
				
			process(tokenStart);
				
			setCaretPosition(position);
				
		
		}
		Point getDocumentLineAndCol()
		{
			Point p = new Point();
			p.y = 1;
			p.x = 1;
			try
			{
				int pos = getCaretPosition();
				int line = 1;
				String s =this.editor.getDocument().getText(0,pos);
				int index = 0;
				while(index != pos)
				{
					if(s.charAt(index)=='\n')
					{
						p.y ++;
						p.x = 0;
					}
					p.x ++;
					index++;
				}
				return p;
			}
			catch (Exception e)
			{
				return p;
			}
		}
		
		
		
		void processPunc(char ch,Integer location)
		{
			try
			{
				if( isPuncutator(ch))
				{
					getDocument().remove(location,1);
					String st = ""+ ch;
					getDocument().insertString(location,st,PUNCUTATOR);
					location ++;
				}
				
			}
			catch(Exception e)
			{
			}
		}
		
		void processOperator(char ch, Integer location)
		{
			try
			{
				if( isOperator(ch))
				{
					getDocument().remove(location,1);
					String st = ""+ ch;
					getDocument().insertString(location,st,OPERATOR);
					location ++;
				}
				
			}
			catch(Exception e)
			{
			}
		}
		public void process(int location)
		{
			boolean stop = false;
			int line = 0;
			int i = location-1;
			if(location == 0)
				i =0;
			
			int end = getDocument().getLength();
			String st="";
			String myToken="";
			char myChar = '~';
			String myString="";
			try
			{
				
				
				
				while( i != end )
				{
					st = getText(i,1);
					myChar = st.charAt(0);
					
					processPunc(myChar,i);
					processOperator(myChar,i );				
					switch( myChar )	
					{
						case ' ':
							i++;
						break;
						case '\t':
							i++;
						break;
						case '\r':
							i++;
						break;
						
						case '/':
							st = getText(i+1,1);
							if(st.charAt(0)=='/')
							{
								st="/";
								i++;
								char ch = getText(i,1).charAt(0);
								while(ch!='\n'&& i!=end)
								{
									st += ch;
									i++;
									ch = getText(i,1).charAt(0);
								}
								
								getDocument().remove(i-st.length(),st.length());
								getDocument().insertString(i-st.length(),st,COMMENT);
								//getDocument().insertString(i+1,"\r",PLAIN);
								//getDocument().insertString(i," ",PLAIN);
								//stop = true;
								i++;
							}
							else if(st.charAt(0)=='*')//Multiline comment
							{
								st ="*";
								i++;
								
								myLabel:
								while(true)
								{
									//st+="*";	
									char ch = getText(i,1).charAt(0);
									while(ch!='*')
									{
										st += ch;
										i++;
										ch = getText(i,1).charAt(0);	
									}
									//st += ch;
									//int oldI = i;
									if(getText(i+1,1).charAt(0) == '/')
									{
										break myLabel;
									}
									else
									{
										//st+=ch;
									}
									i++;
								}
								//st = '*'+st;
								getDocument().remove(i-st.length(),st.length());
								getDocument().insertString(i-st.length(),st,COMMENT);
								//i++;
							}
							else
							{
								getDocument().remove(i,1);
								getDocument().insertString(i,"/",OPERATOR);
								i++;
								//getDocument().insertString(i+1,"\r",PLAIN);
								
							}
							i++;
						break;
						case 34:
							
							myString="";
							i++;
							myChar = getText(i,1).charAt(0);
							while( myChar!=34)
							{
								myString+=myChar;
								i++;
								myChar = getText(i,1).charAt(0); 
								///System.out.println ("HJJJ "+myChar);
							}
							//myString+="\"";
							getDocument().remove(i-myString.length(),myString.length());
							getDocument().insertString(i-myString.length(),myString,STRING);
							i++;
							//JOptionPane.showMessageDialog(null,myString);
						break;
						default:
							//Identifier or keywords
							//myChar = st.charAt(0);
							myToken="";
							if(Character.isDigit( myChar ))
							{
								myToken += myChar;
								i++;
								myChar = getText(i,1).charAt(0);
								while(Character.isDigit( myChar ))
								{
									
									myToken+= myChar;
									i++;
									myChar = getText(i,1).charAt(0); 
								}
								if(myChar=='.')
								{
									
									myToken += myChar;
									i++;
									myChar = getText(i,1).charAt(0); 
								}
								while(Character.isDigit( myChar ))
								{
									
									myToken+= myChar;
									i++;
									myChar = getText(i,1).charAt(0); 
								}
								//i--;
								getDocument().remove(i-myToken.length(),myToken.length());
								getDocument().insertString(i-myToken.length(),myToken,NUMBER);
								//getDocument().insertString(i+1,"\r",PLAIN);
								//stop = true;
								
							}
							else if(Character.isLetter( myChar ))
							{
								myToken="";
								myToken+=myChar;
								
								myChar = getText(++i,1).charAt(0); 
								while( Character.isLetter( myChar ) || Character.isDigit( myChar ))
								{
									myToken+=myChar;
									myChar = getText(++i,1).charAt(0); 
								}
								
								if(isKeyWord( myToken ))
								{
									getDocument().remove(i-myToken.length(),myToken.length());
									getDocument().insertString(i-myToken.length(),myToken,KEYWORD);
									//getDocument().insertString(i+1,"\r",PLAIN);
									//stop = true;
								}
								else //identifier
								{
									
									getDocument().remove(i-myToken.length(),myToken.length());
									getDocument().insertString(i-myToken.length(),myToken,PLAIN);
									//getDocument().insertString(i+1,"\r",PLAIN);
									//stop = true;
								}
								
							}
							else
								i++;
						
							
					}
					
				}
				//getDocument().insertString(i,"",PLAIN);
			}
			catch(BadLocationException be)
			{
				//System.out.println ("At process " + be);
			}
		}
	
	boolean isKeyWord(String identifier)
	{
		//return Token.code for keyword else return 0

		if(keywordList.contains(identifier))
			return true;
		else
			return false;
		
	}
	boolean isPuncutator(char punc)
	{
		String s = "" + punc;
		if(puncutatorList.contains(s))
			return true;
		else
			return false;
	}
	boolean isOperator(char opr)
	{
		String s = "" + opr;
		if(operatorList.contains(s))
			return true;
		else	
			return false;
	}
}
	
	
	public CodeEditor(MainIDE father)throws Exception
	{
		
		fatherIDE = father;
		init();
		getDocument().insertString(0,"",PLAIN);
		
		
				
	}
	public static void main(String[]args)throws Exception
	{
		MainIDE m = new MainIDE();
		new CodeEditor(m);
	}
}
//October 5 