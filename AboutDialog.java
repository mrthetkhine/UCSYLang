import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class AboutDialog extends JDialog
{
	AboutDialog(JFrame f)
	{
		
		super(f,"About UCSY");
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JTextArea description = new JTextArea("Modern Object Oriented Programming Language designed to facilitate the construction\n"+
		" of well desinged OO Program by providing language features and mechanism for constuction of OO Design Pattern");
		description.setWrapStyleWord(true);
		panel.add(new JLabel("Unified Computing Secret for You"),BorderLayout.NORTH);
		description.setFont(new Font("Times New Roman",Font.PLAIN,18));
		panel.add(description, BorderLayout.CENTER);
		JPanel buttonOKPanel = new JPanel();
		buttonOKPanel.add( new JButton("OK") );
		
		panel.add( buttonOKPanel ,BorderLayout.SOUTH);
		add(panel);
		setSize(300,300);
		setVisible(true);
	}
	public static void main(String[]args)
	{
		 try 
         { 
         	  //System.setProperty("Quaqua.tabLayoutPolicy","wrap");
         	  //System.setProperty("Quaqua.design","jaguar");

         	 //UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
             
             UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
             //JFrame.setDefaultLookAndFeelDecorated(true);
             //JDialog.setDefaultLookAndFeelDecorated(true);
             //com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true, "panther");
			 JFrame f = new JFrame();
			 f.setSize(300,300);
			 f.setVisible(true); 	
             //UIManager.setLookAndFeel("com.sun.java.swing.plaf.macos.MacOSLookAndFeel");
             AboutDialog about = new AboutDialog(f);
             about.show();

         } catch (Exception e) 
         {
          
         }
         
		
	}
}