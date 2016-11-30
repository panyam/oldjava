package com.sri.test;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.*;
import com.sri.gui.ext.graph.*;

public class GraphTester extends TesterFrame
{
	public GraphTester()
	{
		super("Graph");
		GraphPanel gr = new GraphPanel();
		//gr.SetXRange(0,100);
		//gr.SetYRange(0,100);
		setLayout(new BorderLayout());
		add("Center",gr);
		pack();
		toFront();
		setVisible(true);
		setBounds(200,200,400,300);
	}
}
