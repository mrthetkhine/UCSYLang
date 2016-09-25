import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.tree.*;


class ParseTree extends JFrame
{
	ParseTree()
	{
		
				this.setTitle("Abstract Syntax Tree");
				this.setSize(500,500);
			//	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				
				
	}
	void display(SimpleNode root)
	{
				DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(root.getClass().toString());
				//for (int i = 0; i< root.jjtGetNumChildren(); i++)
					buildChild((SimpleNode)root,treeRoot);
				JTree tree = new JTree(treeRoot);
				this.getContentPane().add(new JScrollPane(tree));
				this.setVisible(true);
	}
	
	void buildChild(SimpleNode node,DefaultMutableTreeNode parent)
	{
		//DefaultMutableTreeNode root =null ;
		DefaultMutableTreeNode child = null;
		
		if(node.jjtGetNumChildren()!=0) //Not leaf
		{
			
			for (int i = 0; i<node.jjtGetNumChildren(); i++)
			{
				child = new DefaultMutableTreeNode(node.jjtGetChild(i).getClass().toString());
				parent.add(child);
				buildChild((SimpleNode)(node.jjtGetChild(i)),child);
			}
		}
		/*else
		{
			child =new DefaultMutableTreeNode(node.getClass().toString());
			parent.add(child);
		}*/
	}
	public static void main(String[]arhgs)
	{
		new ParseTree();
	}
}