package ext.outlook;
/*
=====================================================================

	FolderBar.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.sun.java.swing.Icon;
import com.sun.java.swing.JLabel;
import com.sun.java.swing.JPanel;
import com.sun.java.swing.ImageIcon;
import com.sun.java.swing.border.EmptyBorder;

public class FolderBar extends JPanel
	implements ActionListener
{
	protected JLabel text;
	protected JLabel icon;

	public FolderBar(String label)
	{
		this(label, new ImageIcon("View.GIF"));
	}

	public FolderBar(String label, Icon image)
	{
		setOpaque(true);
		setBackground(Color.gray);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 7, 2, 7));
		add("Center", text = new JLabel(label));
		add("East", icon = new JLabel(image));
		text.setFont(new Font("Helvetica", Font.PLAIN, 24));
		text.setVerticalAlignment(JLabel.BOTTOM);
		text.setVerticalTextPosition(JLabel.BOTTOM);
		text.setForeground(Color.lightGray);
		icon.setVerticalAlignment(JLabel.CENTER);
	}

	public void setText(String string)
	{
		text.setText(string);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		text.setText(event.getActionCommand());
	}
}
