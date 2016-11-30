package com.sri.games.mine;

import com.sri.games.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class MineApplet extends Applet implements ActionListener, WindowListener
{
    Button b = new Button("Show Window");
    Frame f= new Frame("MineSweeper V1.0 - by Sriram Panyam");
	GamePanel gp = new GamePanel();

    public MineApplet()
    {
        init();
    }

    public void init()
    {
        setLayout(new BorderLayout());
        add("Center", b);
        b.addActionListener(this);
        f.setLayout(new BorderLayout());
        f.add("Center", gp);
        f.addWindowListener(this);
        f.pack();
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension s = f.getPreferredSize();
        f.setLocation((ss.width - s.width) / 2, (ss.height - s.height) / 2);
		f.setVisible(true);
		f.toFront();
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == b)
        {
			gp.minePanel.restart();
			Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		    Dimension s = f.getPreferredSize();
	        f.setLocation((ss.width - s.width) / 2, (ss.height - s.height) / 2);
		    f.setVisible(true);
	        f.toFront();
        }
    }

	public void pack()
	{
		f.pack();
	}
	
	public void destroy()
	{
		f.setVisible(false);
		f.dispose();
	}
	
    public void windowClosing(WindowEvent e)
    {
        if (e.getSource() instanceof Frame)
        {
            ((Frame)e.getSource()).setVisible(false);
            //((Frame)e.getSource()).dispose();
        }
    }
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
	
	public static void main(String args[])
	{
        Frame f= new Frame("MineSweeper V1.0 - by Sriram Panyam");
        f.setLayout(new BorderLayout());
        f.add("Center", new GamePanel());
        f.setVisible(true);
        f.toFront();
		f.setBounds(100, 100, 300, 300);
        //f.addWindowListener(this);
        //f.pack();
	}
}

