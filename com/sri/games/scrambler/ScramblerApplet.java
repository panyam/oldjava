package com.sri.games.scrambler;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.applet.*;

public class ScramblerApplet extends Applet implements ActionListener
{
    Button b = null;
	ScramblerGameFrame sgf = null;

	public ScramblerApplet()
	{
	}
	
    public void init() {
        setLayout(new BorderLayout());
		if (b == null) b = new Button("Show Puzzle Window");
        add("Center", b);
        b.addActionListener(this);
		if (sgf == null) sgf = new ScramblerGameFrame(this);
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension s = sgf.getPreferredSize();
        sgf.setLocation((ss.width - s.width) / 2, (ss.height - s.height) / 2);
		sgf.setVisible(true);
		sgf.toFront();
    }

	public void pack()
	{
		sgf.pack();
	}

	public void destroy()
	{
		sgf.setVisible(false);
		sgf.dispose();
	}
	
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == b)
		{
			sgf.setVisible(true);
			Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		    Dimension s = sgf.getPreferredSize();
	        sgf.setLocation((ss.width - s.width) / 2, (ss.height - s.height) / 2);
			sgf.toFront();
        }
    }
}
