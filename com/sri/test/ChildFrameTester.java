package com.sri.test;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.*;

class ChildFrameTester extends TesterFrame implements ActionListener, ItemListener {
	Toolkit tk = Toolkit.getDefaultToolkit();
										   
	public ChildFrameTester(){
		super("Child Frames");
		setBackground(Color.lightGray);
		addWindowListener(new WindowCloser(this));
		pack();
		setVisible(true);
		setBounds(150,150,100,400);
		toFront();
	}
	
	public void itemStateChanged(ItemEvent e) {
	}
	
	public void actionPerformed(ActionEvent e) {
	}
}
