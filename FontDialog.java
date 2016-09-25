import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;


class FontDialog extends JDialog implements ActionListener
{
	JFrame parent;
	String[]  availableFontName ;
	String[]  availableFontSize;
	JComboBox fontName;
	JComboBox fontSize;
	JLabel    exampleText;
	Font      selectedFont;
	JButton   okButton = new JButton("Ok");
	JButton   cancelButton = new JButton("Cancel");
	FontDialog(JFrame par)
	{
		
		super(par,"UCSY",true);
		this.parent = par;
		this.setSize(300,200);
		
		
		this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth())/2-150,(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2)-150);		
		exampleText = new JLabel("Unified Computing Secret for You");
		selectedFont = new Font("Times New Roman",Font.PLAIN, 19);
		exampleText.setFont( selectedFont );
		
		
		
		
		
		
		add(exampleText,BorderLayout.CENTER);
		JPanel okCancelPanel = new JPanel();
		okCancelPanel.add( okButton );
		okButton.addActionListener(new OkAction());
		
		okCancelPanel.add( cancelButton );
		cancelButton.addActionListener( new CancelAction());
		add(okCancelPanel, BorderLayout.SOUTH);
	}
	public void actionPerformed(ActionEvent ae)
	{
		selectedFont = new Font((String)fontName.getSelectedItem(),Font.PLAIN, Integer.parseInt((String)fontSize.getSelectedItem()));
		exampleText.setFont( selectedFont );
	}
	public static void main(String[]args)
	{
		try
		{
		UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
		}
		catch(Exception e)
		{
			
		}
		FontDialog dialog = new FontDialog(null);
		dialog.show();
	}

	class OkAction implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			
			FontDialog.this.show(false);
		}
	}
	class CancelAction implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			FontDialog.this.show(false);
		}
	}	
}
