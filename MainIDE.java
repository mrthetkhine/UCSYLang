import java.awt.*;
import javax.swing.*;
import java.awt.event.*; 
import javax.swing.text.*;
import java.util.*;
import java.io.*;


public class MainIDE extends JFrame
{
	JTabbedPane tabbedPane = new JTabbedPane();
	JPanel lowerPane = new JPanel();
	JEditorPane compileOutput = new JEditorPane();
	JLabel lineNo = new JLabel("This is line No");
	JMenu fileMenu;
	JMenu editMenu;
	JMenu buildMenu;
	JMenu settingMenu;
	JMenuBar menuBar ;
	FontDialog fontDialog;
	Font selectedFont = new Font("Times New Roman",Font.PLAIN,19);
	
	JCheckBoxMenuItem showParseTree;
	JFileChooser fileChooser;
	
	//Hashtable<String,File> listOfFile = new Hashtable<String,File>();
	//Hashtable<String,CodeEditor> listOfCodeEditor = new Hashtable<String,CodeEditor>();
	ArrayList<CodeEditor> editors = new ArrayList<CodeEditor>();
	public MainIDE()
	{
		super("IDE for UCSY");
		setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()),(int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()-30));
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,tabbedPane ,lowerPane);
		lowerPane.setLayout(new BorderLayout());
		lowerPane.add(new JScrollPane(compileOutput),BorderLayout.CENTER);
		
		compileOutput.setFont(new Font("Times New Roman",Font.PLAIN,19));
		lowerPane.add(lineNo,BorderLayout.SOUTH);
		splitPane.setDividerLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/1.5));
		splitPane.setOneTouchExpandable(true);
		
		fileMenu = new JMenu("File");
		
		fileMenu.add(new NewTabAction());
		fileMenu.add(new OpenFile());
		fileMenu.add(new SaveFile());
		fileMenu.add(new SaveAs());
		fileMenu.addSeparator();
		fileMenu.add(new ExitAction());
		
		fileMenu.setMnemonic('F');
		
		
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		editMenu.add(new Cut());
		editMenu.add(new Copy());
		editMenu.add(new Paste());
		editMenu.add(new Delete());
		
		buildMenu = new JMenu("Build");
		buildMenu.setMnemonic('B');
		buildMenu.add(new Compile());
		buildMenu.add(new Run());
		
		settingMenu = new JMenu("Settings");
		settingMenu.setMnemonic('S');
		showParseTree = new JCheckBoxMenuItem("Show Abstract Parse Tree");
		settingMenu.add(showParseTree);
		settingMenu.add(new ShowFontDialog());
		fileChooser = new JFileChooser();
		javax.swing.filechooser.FileFilter filter = new SimpleFilter("ucsy","UCSY Programs");

		javax.swing.filechooser.FileFilter filterTwo = new SimpleFilter("java","Java Prorgams");
		javax.swing.filechooser.FileFilter filterThree = new SimpleFilter("cpp","C++ Prorgams");
		fileChooser.setFileFilter( filterThree );
		fileChooser.setFileFilter( filterTwo );
		fileChooser.setFileFilter( filter );
		
		
		try
		{
			File dir = (new File(".")).getCanonicalFile();
			fileChooser.setCurrentDirectory(dir);
		}
		catch(Exception e)
		{
			
			
		}
		menuBar = new JMenuBar();
		menuBar.add( fileMenu );
		menuBar.add( editMenu );
		menuBar.add( buildMenu );
		menuBar.add( settingMenu );
		setJMenuBar( menuBar );
		
		Container container = getContentPane();
		container.add(splitPane, BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new MyWindowListener());
		setVisible( true );
		fontDialog = new FontDialog(this);
		
		
	}
	class MyWindowListener extends WindowAdapter
	{
		
		public void windowClosing(WindowEvent we)
		{
			if(JOptionPane.showConfirmDialog(MainIDE.this,"Are you want to exit","Exit",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
			{			
				System.exit(0);
			}
						
		}
	}
	private void createNewTab()
	{
		JPanel panel = new JPanel(new BorderLayout());
		CodeEditor  editor=null ;
		String tabName = "Untitled" + tabbedPane.getTabCount();
		try
		{
			editor= new CodeEditor(this);
			File currentFile = new File(tabName+".ucsy");
			FileOutputStream fout = new FileOutputStream(currentFile);
			fout.write("".getBytes());
			fout.close();
			editor.setSourceFile( currentFile );
			
			editor.setLineNoLabel(lineNo);
			panel.add(new JScrollPane(editor));
			tabbedPane.addTab( tabName, panel);
			
			int tabbedPaneIndex = tabbedPane.getTabCount()-1;
			editors.add(tabbedPaneIndex,editor);
			///System.out.println ("PUt at "+ tabbedPaneIndex+ " "+ editor);	
			//listOfCodeEditor.put(String.valueOf( tabbedPaneIndex ),editor);
			//listOfFile.put( String.valueOf( tabbedPaneIndex ),currentFile);
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
		
		
		
		int tabbedPaneIndex = tabbedPane.getTabCount()-1;
		//listOfCodeEditor.put(tabName,editor);
		
		//listOfFile.put();
	}
	private void openFile()
	{
			if(fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
				return;
			File f = fileChooser.getSelectedFile();
			
			if(f == null || !f.isFile())
				return;
			File currentFile = f;
			
			try
			{
				DataInputStream in = new DataInputStream(new FileInputStream(currentFile));
				JPanel panel = new JPanel(new BorderLayout());
		
				
				CodeEditor  editor= new CodeEditor(this);
				editor.setSourceFile(currentFile);
				String s = in.readLine();
				int offest = 0;
				while(s!= null)
				{
					editor.getDocument().insertString(offest,s+"\n",CodeEditor.PLAIN);
					s = in.readLine();
					offest = editor.getDocument().getLength();
				}
				editor.setLineNoLabel(lineNo);
				panel.add(new JScrollPane(editor));
				editor.process();
				tabbedPane.addTab(currentFile.getName() , panel);
				int tabbedPaneIndex = tabbedPane.getTabCount()-1;
				//System.out.println (tabbedPaneIndex+" "+ currentFile.getAbsolutePath());
				editors.add(tabbedPaneIndex,editor);
				
				in.close();
				//listOfCodeEditor.put(String.valueOf( tabbedPaneIndex),editor);
				//listOfFile.put(String.valueOf( tabbedPane.getSelectedIndex()),currentFile);
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
			
	}
	private void saveFile()
	{
		
		
		String tabbedPaneName = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
		
		///System.out.println ("Saving at "+ tabbedPaneName);
		if(!tabbedPaneName.startsWith("Untitled"))
		{
			try
			{
				int tabbedPaneIndex = tabbedPane.getSelectedIndex();
				CodeEditor toSave = editors.get( tabbedPaneIndex );
				///System.out.println ("Shoe "+ tabbedPaneIndex );
				//File f = new File(tabbedPaneName);
				File sourceFile = toSave.getSourceFile();	
				//System.out.println ();
				File f = new File(sourceFile.getAbsolutePath());
				
				
				
				DataOutputStream saveFile = new DataOutputStream(new FileOutputStream(f)); 
			
				saveFile.writeBytes(toSave.getDocument().getText(0,toSave.getDocument().getLength()));
				saveFile.flush();
				saveFile.close();
				toSave.setSourceFile(f);
				
			///	editors.add(tabbedPaneIndex,toSave);
			
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
	
		}
		else
		{
			if(fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				return;
		
			try
			{
				
				File f = fileChooser.getSelectedFile();
			
				String fileName = f.getAbsolutePath();
				
				if(!fileName.endsWith(".ucsy"))
				{
					fileName = fileName +".ucsy";
					f = new File(fileName);
				}
				
				int tabbedPaneIndex = tabbedPane.getSelectedIndex();
				String tabName = tabbedPane.getTitleAt( tabbedPaneIndex );
				
				
				
				CodeEditor toSave = editors.get( tabbedPaneIndex );
				
				DataOutputStream saveFile = new DataOutputStream(new FileOutputStream(new File(fileName))); 
			
				saveFile.writeBytes(toSave.getDocument().getText(0,toSave.getDocument().getLength()));
				tabbedPane.setTitleAt(tabbedPaneIndex,f.getName());
				
				editors.add(tabbedPaneIndex,toSave);	
				toSave.setSourceFile(f);
				//f = new File(file);
				//toSave.setSourceFile( f );
				
				
				saveFile.close();
			
				//System.out.println (tabbedPane.getSelectedComponent());
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
		}	
	}
	private void saveAs()
	{
		if(fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION)
				return;
		
			try
			{
				File f = fileChooser.getSelectedFile();
			
				String fileName = f.getName();
				if(!fileName.endsWith(".ucsy"))
				{
					fileName = fileName +".ucsy";
					f = new File(fileName);
				}
				int tabbedPaneIndex = tabbedPane.getSelectedIndex();
				String tabName = tabbedPane.getTitleAt( tabbedPaneIndex );
				CodeEditor toSave = editors.get( tabbedPaneIndex );
				DataOutputStream saveFile = new DataOutputStream(new FileOutputStream(f)); 
				toSave.setSourceFile(f);	
				saveFile.writeBytes(toSave.getDocument().getText(0,toSave.getDocument().getLength()));
				tabbedPane.setTitleAt(tabbedPaneIndex,fileName);
				//listOfFile.put(String.valueOf( tabbedPane.getSelectedIndex()),f);
				saveFile.close();
			
				//System.out.println (tabbedPane.getSelectedComponent());
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}
	}
	private void cut()
	{
		int tabbedPaneIndex = tabbedPane.getSelectedIndex();
		CodeEditor toCut = editors.get(tabbedPaneIndex);
		toCut.cut();
	}
	private void copy()
	{
		int tabbedPaneIndex = tabbedPane.getSelectedIndex();
		CodeEditor toCopy = editors.get(tabbedPaneIndex);
		toCopy.copy();
	}
	private void paste()
	{
		int tabbedPaneIndex = tabbedPane.getSelectedIndex();
		CodeEditor toPaste = editors.get(tabbedPaneIndex);
		toPaste.paste();
	}
	private void delete()
	{
		int tabbedPaneIndex = tabbedPane.getSelectedIndex();
		CodeEditor toDelete = editors.get(tabbedPaneIndex);
		toDelete.cut();
	}
	private void compile()
	{
		saveFile();	
		UCSY compiler = null;
		try
		{
		int tabbedPaneIndex = tabbedPane.getSelectedIndex();
		
		String tabTitle = tabbedPane.getTitleAt(tabbedPaneIndex);
		
		CodeEditor toSave = editors.get(tabbedPaneIndex);
		
		File toCompile = toSave.getSourceFile();
		
		compiler = new UCSY(new FileInputStream(toCompile));
		// compiler.ReInit(new FileInputStream(toSave.source),null);
		compileOutput.getDocument().remove(0,compileOutput.getDocument().getLength());
		compiler.setCompileOutput(this.compileOutput);
		compiler.inform("Compiling "+ toCompile.getAbsolutePath());
		compiler.compile();
		compiler = null;
		}
		catch(Exception e)
		{
			try
			{
				BufferedOutputStream bout = new BufferedOutputStream(new ByteArrayOutputStream());
				//e.printStackTrace(new PrintWriter(bout));
				
				//compiler.inform(bout.);
				//PrintWriter pw = new PrintWriter();
				
			}
			catch(Exception e2)
			{
			}
			//System.out.println ("error in compilation");
			//e.printStackTrace();
		}
		finally
		{
			CentralTypeTable.reset();
			SymbolTable.reset();
			
		}
		
	} 
	private void run()
	{
		try
		{
			int tabbedPaneIndex = tabbedPane.getSelectedIndex();
		
			String tabTitle = tabbedPane.getTitleAt(tabbedPaneIndex);
			String fileName = tabTitle.substring(0,tabTitle.length()-5);
			//JOptionPane.showMessageDialog(this,fileName,"",JOptionPane.OK_OPTION);
			FileOutputStream tempBat = new FileOutputStream("temp.bat");
			String command = "uvmproject.exe " + fileName;
			tempBat.write(command.getBytes());
			tempBat.close();
			Process p = Runtime.getRuntime().exec("temp.bat");
			//p.waitFor();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	private class NewTabAction extends AbstractAction
	{ 
		public NewTabAction()
		{
			putValue(Action.NAME, "New  Alt+N");
			putValue(Action.SHORT_DESCRIPTION,"Create a new File");
			putValue(Action.MNEMONIC_KEY, new Integer('N'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			createNewTab();
		}
	}
	private class OpenFile extends AbstractAction
	{
		public OpenFile()
		{
			putValue(Action.NAME,"Open Alt+O");
			putValue(Action.SHORT_DESCRIPTION,"Open File");
			putValue(Action.MNEMONIC_KEY,new Integer('O'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			openFile();
		}
	}
	private class SaveFile extends AbstractAction
	{
		public SaveFile()
		{
			putValue(Action.NAME,"Save Alt+S");
			putValue(Action.SHORT_DESCRIPTION,"Save File");
			putValue(Action.MNEMONIC_KEY,new Integer('s'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			saveFile();
		}
		
	}
	private class SaveAs extends AbstractAction
	{
		public SaveAs()
		{
			putValue(Action.NAME, "Save As");
			putValue(Action.SHORT_DESCRIPTION,"Save As");
			putValue(Action.MNEMONIC_KEY,new Integer('A'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			saveAs();
		}
	}
	private class ExitAction extends AbstractAction
	{
		public ExitAction()
		{
			putValue( Action.NAME , "Exit Alt+X");
			putValue( Action.SHORT_DESCRIPTION, "Exit Application");
			putValue( Action.MNEMONIC_KEY,new Integer('X'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			if(JOptionPane.showConfirmDialog(MainIDE.this,"Are sure you want to exit","Exit",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				System.exit(0);
			//else
			//	System.out.println ("Dont close");
		}
	}
	private class Cut extends AbstractAction
	{
		public Cut()
		{
			putValue( Action.NAME, "Cut Alt+U");
			putValue( Action.SHORT_DESCRIPTION, "Cut");
			putValue( Action.MNEMONIC_KEY, new Integer('U'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			cut();
		}
	}
	private class Copy extends AbstractAction
	{
		public Copy()
		{
			putValue( Action.NAME, "Copy Alt+C");
			putValue( Action.SHORT_DESCRIPTION, "Copy");
			putValue( Action.MNEMONIC_KEY, new Integer('C'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			copy();
		}
	}
	private class Paste extends AbstractAction
	{
		public Paste()
		{
			putValue( Action.NAME, "Paste Alt+V");
			putValue( Action.SHORT_DESCRIPTION, "Paste");
			putValue( Action.MNEMONIC_KEY, new Integer('V'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			paste();
		}
	}
	private class Delete extends AbstractAction
	{
		public Delete()
		{
			putValue( Action.NAME, "Delete");
			putValue( Action.SHORT_DESCRIPTION, "Delete");
			//putValue( Action.MNEMONIC_KEY, new Integer(''));
		}
		public void actionPerformed(ActionEvent ae)
		{
			delete();
		}
	}
	private class Compile extends AbstractAction
	{
		public Compile()
		{
			putValue( Action.NAME, "Compile");
			putValue( Action.SHORT_DESCRIPTION, "Compile");
			
		}
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				compile();
			}
			catch(Exception e)
			{
				
			}
		}
	}
	private class Run extends AbstractAction
	{
		public Run()
		{
			putValue( Action.NAME, "Run");
			putValue( Action.SHORT_DESCRIPTION, "Run");
			putValue( Action.MNEMONIC_KEY, new Integer('R'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			run();
		}
	}
	private class ShowFontDialog extends AbstractAction
	{
		public ShowFontDialog()
		{
			putValue( Action.NAME, "About");
			putValue( Action.SHORT_DESCRIPTION, "About Dialog");
		}
		public void actionPerformed(ActionEvent ae)
		{
			//MainIDE.this.fontDialog.show();
			String message = "Unified Computing Secret for You\r\n"+
			"Developed by Thet Khine\r\n"+
			"Supervised by Dr Khine Moe Nwe";
			JOptionPane.showMessageDialog(MainIDE.this,message);
		}
	}
	
	public static void main(String[]args)
	{
		
			String lnkArr[] = {"com.jtattoo.plaf.noire.NoireLookAndFeel",
				"com.jtattoo.plaf.mcwin.McWinLookAndFeel",
				"com.jtattoo.plaf.mint.MintLookAndFeel",
				"com.jtattoo.plaf.smart.SmartLookAndFeel",
				"com.jtattoo.plaf.hifi.HiFiLookAndFeel",
				"com.jtattoo.plaf.fast.FastLookAndFeel",
				"com.jtattoo.plaf.bernstein.BernsteinLookAndFeel",
				"com.jtattoo.plaf.aero.AeroLookAndFeel",
				"com.jtattoo.plaf.acryl.AcrylLookAndFeel",
				"ch.randelshofer.quaqua.QuaquaLookAndFeel",
				"com.birosoft.liquid.LiquidLookAndFeel",
				"net.sourceforge.napkinlaf.NapkinLookAndFeel",
				
				};		
				String uiClass = "";
		try
		{
			int index = Integer.parseInt(args[0]);
			uiClass = lnkArr[index];
		}
		catch(Exception e )
		{
			uiClass = "com.birosoft.liquid.LiquidLookAndFeel";
			com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true, "panther");
		}
         try 
         { 
         	 UIManager.setLookAndFeel(uiClass);
             //JFrame.setDefaultLookAndFeelDecorated(true);
             //JDialog.setDefaultLookAndFeelDecorated(true);
             //com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true, "panther");
			 	
             //UIManager.setLookAndFeel("com.sun.java.swing.plaf.macos.MacOSLookAndFeel");
             

         } catch (Exception e) 
         {
          	//e.printStackTrace();
         }
         MainIDE sp = new MainIDE();
		
		
	}
	
}

class SimpleFilter extends javax.swing.filechooser.FileFilter
{
	private String m_description = null;
	private String m_extension = null;
	
	public SimpleFilter(String extension, String description) 
	{
		m_description = description;
		m_extension = "." + extension.toLowerCase();
	}
	public String getDescription() 
	{
		return m_description;
	}
	public boolean accept(File f) 
	{
			if (f == null)
				return false;
			if (f.isDirectory())
				return true;
			return f.getName().toLowerCase().endsWith(m_extension);
}
}
