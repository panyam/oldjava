package com.sri.test;

import java.awt.*;
import java.awt.event.*;

import com.sri.gui.core.button.*;

class ButtonTester extends TesterFrame implements ActionListener, ItemListener {
	Toolkit tk = Toolkit.getDefaultToolkit();
	SCheckGroup gr = new SCheckGroup();
	//SButton one = new SRollOverButton(tk.getImage("images/open.gif"),"One");
	//SButton two = new SRollOverButton(tk.getImage("images/save.gif"),"two");
	SButton three = new SActionButton(tk.getImage("images/saveall.gif"),"three");
	SButton four = new  SCheckButton(tk.getImage("images/cut.gif"),"four",gr);
	SButton five = new  SCheckButton(tk.getImage("images/copy.gif"),"five",gr);
	SButton six = new  SCheckButton(tk.getImage("images/paste.gif"),"five",gr);
								
	String names[] = {
		"NW", "N", "NE", "W", "MID", "E", "SW", "S", "SE"
	};
	
	int dirs[] = 				
	{
		SButton.NORTH_WEST , SButton.NORTH, SButton.NORTH_EAST,
		SButton.WEST , SButton.CENTER, SButton.EAST,
		SButton.SOUTH_WEST , SButton.SOUTH, SButton.SOUTH_EAST
	};
	Button bs0[] = new Button[9];
	Button bs1[] = new Button[9];
	
	public ButtonTester(){
		super("Button");
		setBackground(Color.lightGray);
		Panel left = new Panel(new BorderLayout());//new GridLayout(6,1,1,1));
		setLayout(new BorderLayout());
		Panel right = new Panel(new GridLayout(2,1,5,5));
		Panel top = new Panel(new BorderLayout());
		Panel bottom = new Panel(new BorderLayout());
		Panel dirP[] = { new Panel(new GridLayout(3,3,2,2)),
						 new Panel(new GridLayout(3,3,2,2))};
		
		top.add("North",new Label("Title Position"));
		bottom.add("North",new Label("Content Alignment"));
		top.add("Center",dirP[0]);
		bottom.add("Center",dirP[1]);
		right.add(top);
		right.add(bottom);
		
		for (int i = 0;i < 9;i++)
		{
			Button b1 = new Button(names[i]);
			Button b2 = new Button(names[i]);
			b1.setActionCommand(i + "");
			b2.setActionCommand(i + "");
			b1.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					three.setTextPosition(dirs[Integer.parseInt(e.getActionCommand())]);
				}
			});
			b2.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					three.setContentAllignment(dirs[Integer.parseInt(e.getActionCommand())]);
				}
			});
			dirP[0].add(b1);
			dirP[1].add(b2);
		}
		
		add("Center",left);
		add("East",right);
		//add(one);
		//add(two);
		left.add(three);
		
		three.setRollOver(true);
		four.setRollOver(true);
		five.setRollOver(true);
		six.setRollOver(true);
		//left.add(four);
		//left.add(five);
		//left.add(six);
		pack();
		setVisible(true);
		//setBounds(0,150,100,400);
		toFront();
	}
	
	public void itemStateChanged(ItemEvent e) {
		System.out.println("Yep item event " + Math.random());
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Yep " + Math.random());
	}
}
