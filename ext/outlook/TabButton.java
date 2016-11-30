package ext.outlook;
/*
=====================================================================

	TabButton.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.Insets;
import java.awt.Dimension;

import com.sun.java.swing.JButton;

public class TabButton extends JButton
{
	public TabButton(String name)
	{
		super(name);
		setOpaque(true);
		setFocusPainted(false);
		setMargin(new Insets(2, 2, 2, 2));
		setMinimumSize(new Dimension(20, 20));
		setPreferredSize(new Dimension(20, 20));
	}

	public boolean isFocusTraversable()
	{
		return false;
	}

	public boolean isDefaultButton()
	{
		return false;
	}

}
