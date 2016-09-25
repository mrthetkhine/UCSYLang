import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.SimpleAttributeSet;
enum State
{
	START,
	DIGIT,
	LITERAL,
	OPERATOR,
	STRINGLITERAL,
	CHARLITERAL,
	COMMENT
}

class UCSYIDE extends JFrame
{
	
	JTextPane editor = new JTextPane();
	
	State currentState = State.START;
	
	JMenu mnuFile = new JMenu("File");
	JMenuItem mnuOpen = new JMenuItem("Open");
	JMenuItem mnuNew = new JMenuItem("New");
	JMenuItem mnuExit = new JMenuItem("Exit");
	
	int tokenStart;
	int tokenEnd;
	String token;
	
	static MutableAttributeSet PLAIN = new SimpleAttributeSet();
	static MutableAttributeSet COMMENT = new SimpleAttributeSet();
	static MutableAttributeSet KEYWORD = new SimpleAttributeSet();
	static MutableAttributeSet OPERATOR = new SimpleAttributeSet();
	static MutableAttributeSet NUMBER = new SimpleAttributeSet();
	static 
	{
		StyleConstants.setForeground(PLAIN, Color.BLACK);
		StyleConstants.setForeground(COMMENT , Color.PINK );
		StyleConstants.setForeground(KEYWORD, Color.BLUE);
		StyleConstants.setForeground(OPERATOR, Color.RED);
		StyleConstants.setForeground(NUMBER, Color.GREEN );
		
	}
	
	public void init()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar( menuBar );
		mnuFile.add( mnuNew );
		mnuFile.add( mnuOpen );
		mnuFile.add( mnuExit );
		menuBar.add( mnuFile);
		mnuExit.addActionListener(new FileExit());
		editor.addKeyListener(new MyKeyListener());
	}
	class FileExit implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			if(JOptionPane.showConfirmDialog(UCSYIDE.this,"Are you sure to exit") == JOptionPane.OK_OPTION )
			{
				System.exit(0);
			}
		}
	}
	
	class MyKeyListener extends KeyAdapter
	{
		
		String toReverse(String text)
		{
			String rev="";
			for(int i =text.length()-1;i>=0;i--)
				rev+= text.charAt(i);
			
			return rev;
		}
		public void keyReleased(KeyEvent ke)
		{
				if(ke.isActionKey()|| ke.getKeyCode()== ke.VK_BACK_SPACE || ke.getKeyCode()==ke.VK_DELETE)
					return;
				
				
				process(0);
				try
				{
					if(ke.getKeyCode()==KeyEvent.VK_ENTER)
						editor.getDocument().insertString((int)editor.getCaretPosition()+1,"",PLAIN);
				}
				catch(BadLocationException be)
				{
					
				}
			//}
				
		
		}
		
		void process(int location)
		{
			boolean stop = false;
			int line = 0;
			int i = location-1;
			if(location == 0)
				i =0;
			
			int end = editor.getDocument().getLength();
			String st="";
			String myToken="";
			char myChar = '~';
			
			try
			{
				
				
				
				while( i != end )
				{
					st = editor.getText(i,1);
					myChar = st.charAt(0);
					
									
					switch( myChar )	
					{
						case ' ':
							i++;
						break;
						case '\t':
							i++;
						break;
						case 'r':
							i++;
						break;
						
						case '+':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,"+",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							
						break;
						
						case '-':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,"-",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						
						case '*':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,"*",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						case '\n':
							line ++;
							i++;
						break;
						case '/':
							st = editor.getText(i+1,1);
							if(st.charAt(0)=='/')
							{
								st="/";
								i++;
								char ch = editor.getText(i,1).charAt(0);
								while(ch!='\n'&& i!=end)
								{
									st += ch;
									i++;
									ch = editor.getText(i,1).charAt(0);
								}
								
								editor.getDocument().remove(i-st.length(),st.length());
								editor.getDocument().insertString(i-st.length(),st,COMMENT);
								//editor.getDocument().insertString(i+1,"\r",PLAIN);
								//editor.getDocument().insertString(i," ",PLAIN);
								//stop = true;
								i++;
							}
							else
							{
								editor.getDocument().remove(i,1);
								editor.getDocument().insertString(i,"/",OPERATOR);
								i++;
								//editor.getDocument().insertString(i+1,"\r",PLAIN);
								
							}
						break;
						
						case ';':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,";",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						
						case ')':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,")",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						
						case '(':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,"(",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						
						case '>':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,">",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						
						
						 						 						
						case '<':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,"<",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						case '=':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,"=",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"",PLAIN);
							//stop = true;
						break;
					
						case ':':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,">",OPERATOR);
							i++;
							//editor.getDocument().insertString(i+1,"\r",PLAIN);
							//stop = true;
						break;
						/*
						case '':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,">",OPERATOR);
							editor.getDocument().insertString(i+1,"\r",PLAIN);
						break;
						case '':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,">",OPERATOR);
							editor.getDocument().insertString(i+1,"\r",PLAIN);
						break;
						case '':
							editor.getDocument().remove(i,1);
							editor.getDocument().insertString(i,">",OPERATOR);
							editor.getDocument().insertString(i+1,"\r",PLAIN);
						break;
						*/
						default:
							//Identifier or keywords
							//myChar = st.charAt(0);
							myToken="";
							if(Character.isDigit( myChar ))
							{
								myToken += myChar;
								i++;
								myChar = editor.getText(i,1).charAt(0);
								while(Character.isDigit( myChar ))
								{
									System.out.println (myChar);
									myToken+= myChar;
									i++;
									myChar = editor.getText(i,1).charAt(0); 
								}
								if(myChar=='.')
								{
									System.out.println (myChar);
									myToken += myChar;
									i++;
									myChar = editor.getText(i,1).charAt(0); 
								}
								while(Character.isDigit( myChar ))
								{
									System.out.println (myChar);
									myToken+= myChar;
									i++;
									myChar = editor.getText(i,1).charAt(0); 
								}
								//i--;
								editor.getDocument().remove(i-myToken.length(),myToken.length());
								editor.getDocument().insertString(i-myToken.length(),myToken,NUMBER);
								//editor.getDocument().insertString(i+1,"\r",PLAIN);
								//stop = true;
								
							}
							else if(Character.isLetter( myChar ))
							{
								myToken="";
								myToken+=myChar;
								
								myChar = editor.getText(++i,1).charAt(0); 
								while( Character.isLetter( myChar ) || Character.isDigit( myChar ))
								{
									myToken+=myChar;
									myChar = editor.getText(++i,1).charAt(0); 
								}
								
								if(isKeyWord( myToken ))
								{
									editor.getDocument().remove(i-myToken.length(),myToken.length());
									editor.getDocument().insertString(i-myToken.length(),myToken,KEYWORD);
									//editor.getDocument().insertString(i+1,"\r",PLAIN);
									//stop = true;
								}
								else //identifier
								{
									
									editor.getDocument().remove(i-myToken.length(),myToken.length());
									editor.getDocument().insertString(i-myToken.length(),myToken,PLAIN);
									//editor.getDocument().insertString(i+1,"\r",PLAIN);
									//stop = true;
								}
								
							}
							else
								i++;
							
					}
					
				}
				//editor.getDocument().insertString(i,"",PLAIN);
			}
			catch(BadLocationException be)
			{
				System.out.println ("At process " + be);
			}
		}
		
	boolean isKeyWord(String identifier)
	{
		//return Token.code for keyword else return 0


		if(identifier.equals("Function"))
			return true;
		else if(identifier.equals("Main"))
			return true;
		else if(identifier.equals("EndFunc"))
			return true;
		else if(identifier.equals("Integer"))
			return true;
		else if(identifier.equals("Boolean"))
			return true;
		else if(identifier.equals("Float"))
			return true;
		else if(identifier.equals("Char"))
			return true;
		else if(identifier.equals("If"))
			return true;
		else if(identifier.equals("Then"))
			return true;
		else if(identifier.equals("ElseIf"))
			return true;
		else if(identifier.equals("Else"))
			return true;
		else if(identifier.equals("EndIf"))
			return true;
		else if(identifier.equals("Select"))
			return true;
		else if(identifier.equals("Case"))
			return true;
		else if(identifier.equals("Default"))
			return true;
		else if(identifier.equals("EndSelect"))
			return true;
		else if(identifier.equals("For"))
			return true;
		else if(identifier.equals("To"))
			return true;
		else if(identifier.equals("Step"))
			return true;
		else if(identifier.equals("Next"))
			return true;
		else if(identifier.equals("Do"))
			return true;
		else if(identifier.equals("DWhile"))
			return true;
		else if(identifier.equals("While"))
			return true;
		else if(identifier.equals("EndDo"))
			return true;
		else if(identifier.equals("EndWhile"))
			return true;
		else if(identifier.equals("True"))
			return true;
		else if(identifier.equals("False"))
			return true;
		else if(identifier.equals("Read"))
			return true;
		else if(identifier.equals("Print"))
			return true;
		else if(identifier.equals("Println"))
			return true;
        else if(identifier.equals("Mod"))
			return true;
		else
			return false;
	}

	
	}
	public UCSYIDE()throws Exception
	{
		super("IDE for UCSY");
		Font f = new Font("Courier New",Font.PLAIN,16);
		editor.setFont(f);
		
		this.getContentPane().add(new JScrollPane(editor),BorderLayout.CENTER);
		
		
		editor.getDocument().insertString(0,"",PLAIN);
		
		this.setSize(400, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		init();
		show(true);
	}
	public static void main(String[]args)throws Exception
	{
		new UCSYIDE();
	}
}
//October 5 
