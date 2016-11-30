
import java.awt.*;
import java.applet.*;
import java.awt.event.*;

/**
 * A wrapper panel for the chess game!
 */
public class ChessFrame extends Frame implements 
									 ActionListener, ItemListener,
									WindowListener
{
    Board board = null;
	
	public ChessFrame()
	{
        board = new Board();
        setLayout(new BorderLayout());
        add("Center", board);
        addWindowListener(this);
        //addItemListener(this);
        //addActionListener(this);
	}
	
		/**
		 * Action event handler
		 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
	}
	
		/**
		 * Item Event handler.
		 */
	public void itemStateChanged(ItemEvent e)
	{
		Object src = e.getSource();
	}
	
	public void windowClosing(WindowEvent e)
	{ 
		setVisible(false);
		if (getParent() == null) 
		{
			dispose();
			System.exit(0);
		}
	}
	public void windowClosed(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }

	public static void main(String args[])
	{
		ChessFrame cp = new ChessFrame();
		cp.setBounds(50, 50, 500, 400);
		cp.setVisible(true);
		cp.toFront();
	}
}
