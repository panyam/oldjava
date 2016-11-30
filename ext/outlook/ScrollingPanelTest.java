package ext.outlook;
/*
=====================================================================

	ScrollingPanelTest.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.BorderLayout;

import com.sun.java.swing.JFrame;
import com.sun.java.swing.JPanel;

public class ScrollingPanelTest
{
	public static void main(String[] args)
	{
		PLAF.setNativeLookAndFeel(true);
		
		JPanel scroll = new JPanel();
		scroll.setLayout(new ListLayout());
		scroll.add(new RolloverButton("Alpha"));
		scroll.add(new RolloverButton("Beta"));
		scroll.add(new RolloverButton("Gamma"));
		scroll.add(new RolloverButton("Delta"));
		scroll.add(new RolloverButton("Epsilon"));
		
		JFrame frame = new JFrame("ScrollingPanel Test");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add("Center",
			new ScrollingPanel(scroll));
		frame.setBounds(0, 0, 100, 200);
		frame.setVisible(true);

	}

}
