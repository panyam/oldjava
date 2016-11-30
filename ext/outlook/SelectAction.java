package ext.outlook;
/*
=====================================================================

	SelectAction.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.event.ActionEvent;

import com.sun.java.swing.ImageIcon;
import com.sun.java.swing.AbstractAction;

public class SelectAction extends AbstractAction
{
	public static final String LARGE_ICON = "LargeIcon";

	FolderBar folder;

	public SelectAction(String name, FolderBar folder)
	{
		super(name);
		this.folder = folder;
		putValue(LARGE_ICON, new ImageIcon("View.GIF"));
	}

	public void actionPerformed(ActionEvent event)
	{
		folder.setText((String)getValue(NAME));
	}
}
