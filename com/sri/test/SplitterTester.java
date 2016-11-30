package com.sri.test;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.*;
import com.sri.gui.core.containers.*;

class SplitterTester extends TesterFrame {
	Button first = new Button("Item 1");
	STextField second = new STextField("Item 2");
	List third = new List();
	Component fourth = new Button("Button 4");
	Component fifth = new Button("Button 5");
	Component sixth = new Button("Button 6");
					
	//SplitContainer sc = new SplitContainer(null,null,false);
	public SplitterTester () 
	{
		super("Splitter Controls");
		pack();
		setVisible(true);
		setBounds(150,150,500,400);
		toFront();
		SplitterPanel sp = new SplitterPanel(true);
		SplitterPanel sp2 = new SplitterPanel(false);
		SplitterPanel sp1 = new SplitterPanel(true);
		setLayout(new BorderLayout());
		sp.add(first);
		sp.add(second);
		sp.add(third);
		sp1.add(fifth);
		sp1.add(sixth);
		sp2.add(sp1);
		sp2.add(sp);
		sp2.add(fourth);
		//sp.add(fourth);
		add("Center",sp2);
		sp2.autoArrange();
		sp.autoArrange();
		sp1.autoArrange();
	}
}
