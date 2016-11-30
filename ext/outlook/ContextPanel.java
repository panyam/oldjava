package ext.outlook;
/*
=====================================================================
  
	ContextPanel.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import com.sun.java.swing.JPanel;
import com.sun.java.swing.JButton;
import com.sun.java.swing.border.BevelBorder;

public class ContextPanel extends JPanel
	implements ActionListener
{
	protected Vector buttons = new Vector();

	public ContextPanel()
	{ 
		setLayout(new ContextLayout());
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		setPreferredSize(new Dimension(80, 80));
	}

	public void setIndex(int index)
	{
		((ContextLayout)getLayout()).setIndex(this, index);
	}

	public void addTab(String name, Component comp)
	{
		JButton button = new TabButton(name);
		add(button, comp);
		buttons.addElement(button);
		button.addActionListener(this);
	}
	
	public void removeTab(JButton button)
	{
		button.removeActionListener(this);
		buttons.removeElement(button);
		remove(button);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		Object source = event.getSource();
		for (int i = 0; i < buttons.size(); i++)
		{
			if (source == buttons.elementAt(i))
			{
				setIndex(i + 1);
				return;
			}
		}
	}
}

