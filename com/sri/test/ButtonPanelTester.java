package com.sri.test;

import java.awt.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.*;

public class ButtonPanelTester extends TesterFrame
{
	SActionButton btns[] = new SActionButton[4];
	Component comps[] = {
		new Label("Item One"),
		new TextArea(10,5),
		new Button("Item Three"),
		new List(5),
	};
	
	public ButtonPanelTester()
	{
		super("Button Panel");
		setBackground(Color.lightGray);
		addWindowListener(new WindowCloser(this));
		setLayout(new BorderLayout());
		ButtonPanel bp = new ButtonPanel();
		
		Button b = (Button)comps[2];
		b.setBackground(Color.white);
		for (int i = 0;i < btns.length;i++)
		{
			btns[i] = new SActionButton("Button   " + (i + 1));
			btns[i].showText(true);
			btns[i].setRollOver(true);
			btns[i].setFont(new Font("Serif",Font.ITALIC + Font.BOLD, 15));
			bp.addItem(btns[i],comps[i]);
		}
		bp.selectItem(2);
		add("Center",bp);
		pack();
		setVisible(true);
		setBounds(150,150,500,400);
		toFront();
		bp.validate();
	}
}
