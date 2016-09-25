import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;



public class MainWindow extends JFrame
{
	JTabbedPane tabbedPane = new JTabbedPane();
	JEditorPane lineNo = new JEditorPane();
	JScrollPane mainScrollPane;
	JMenu fileMenu;
	JMenuBar menuBar;
	Container container;
	JLabel lineColLabel;
	JFileChooser fileChooser;// =new JFileChooser();
	JToolBar toolBar = new JToolBar();
	
	public MainWindow()
	{
		super("IDE for UCSY");
		setSize((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()-35));
		//fileChooser.setCurrentDirectory(new File("*.ucsy"));
		mainScrollPane = new JScrollPane( tabbedPane );
		
		
		toolBar.add(new NewTabAction());
		toolBar.add(new OpenAction());
		toolBar.add(new ExitAction());
		fileMenu= new JMenu("File");
		
		fileMenu.add(new NewTabAction());
		fileMenu.add(new OpenAction());
		fileMenu.addSeparator();
		
		fileMenu.add(new ExitAction());
		fileMenu.setMnemonic('F');
		
		menuBar = new JMenuBar();
		menuBar.add( fileMenu );
		setJMenuBar( menuBar );
		
		container = getContentPane();
		container.add(toolBar,BorderLayout.NORTH);
		container.add(mainScrollPane, BorderLayout.CENTER);
		lineColLabel = new JLabel("Something is in");
		
		container.add(lineColLabel,BorderLayout.SOUTH);
		setVisible( true );
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		for(int i=0;i<30;i++)
		{
			lineNo.setText(lineNo.getText() + i+"\n");
		}
	}
	
	private void createNewTab()
	{
		JPanel panel = new JPanel(new BorderLayout());
		
		panel.add(new JScrollPane(new JEditorPane()));
		
		tabbedPane.addTab("Tabbed " + tabbedPane.getTabCount() , panel);
	}
	private class NewTabAction extends AbstractAction
	{
		public NewTabAction()
		{
			putValue(Action.NAME, "New Tab");
			putValue(Action.SHORT_DESCRIPTION,"Add a new Tab");
			putValue(Action.MNEMONIC_KEY, new Integer('N'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			createNewTab();
		}
	}
	private class OpenAction extends AbstractAction
	{
		public OpenAction()
		{
			putValue(Action.NAME,"Open File");
			putValue(Action.SHORT_DESCRIPTION, "Open a source file");
			putValue(Action.MNEMONIC_KEY,new Integer('O'));
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			fileChooser.showOpenDialog(MainWindow.this);
		}
	}
	private class ExitAction extends AbstractAction
	{
		public ExitAction()
		{
			putValue( Action.NAME , "Exit");
			putValue( Action.SHORT_DESCRIPTION, "Exit Application");
			putValue( Action.MNEMONIC_KEY,new Integer('X'));
		}
		public void actionPerformed(ActionEvent ae)
		{
			if( JOptionPane.showConfirmDialog(MainWindow.this,"Are you want to save file","Confirm to save",JOptionPane.CANCEL_OPTION) == JOptionPane.OK_OPTION)
				System.exit(0);
		}
	}
	public static void main(String[]args)
	{
		
         try 
         { 
              UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
              MainWindow sp = new MainWindow();
         } catch (Exception e) 
         {
          
         }
         
		
	}
	
}