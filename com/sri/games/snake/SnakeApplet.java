package com.sri.games.snake;
import java.awt.*;
import java.awt.event.*;
import java.applet.Applet;

public class SnakeApplet extends Applet implements ActionListener, WindowListener
{
    Button b = new Button("Show Snake Window");
	SnakeFrame nf = new SnakeFrame(this);

    public void init() {
        setLayout(new BorderLayout());
        add("Center", b);
        b.addActionListener(this);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension s = nf.getPreferredSize();
        nf.setLocation((ss.width - s.width) / 2, (ss.height - s.height) / 2);
		nf.setVisible(true);
		nf.toFront();
    }

	public void pack()
	{
		nf.pack();
	}

	public void destroy()
	{
		nf.setVisible(false);
		nf.dispose();
	}
	
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b) {
			nf.setVisible(true);
			Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		    Dimension s = nf.getPreferredSize();
	        nf.setLocation((ss.width - s.width) / 2, (ss.height - s.height) / 2);
			nf.toFront();
        }
    }
	
    public void windowClosing(WindowEvent e)
    {
        if (e.getSource() == nf)
        {
            nf.setVisible(false);
        }
    }
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
}
