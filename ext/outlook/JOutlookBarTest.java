package ext.outlook;

/*
=====================================================================

	JOutlookBarTest.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.Color;
import java.awt.BorderLayout;

import com.sun.java.swing.JFrame;
import com.sun.java.swing.JPanel;
import com.sun.java.swing.border.MatteBorder;
import com.sun.java.swing.border.EtchedBorder;

public class JOutlookBarTest
{
	public static void main(String[] args)
	{
		PLAF.setNativeLookAndFeel(true);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(new MatteBorder(5, 5, 5, 5, Color.lightGray));
		FolderBar folderbar = new FolderBar("Folder Bar");
		JPanel content = new JPanel();
		content.setBorder(new EtchedBorder(EtchedBorder.RAISED));
		panel.add("North", folderbar);
		panel.add("Center", content);
		
		JOutlookBar outlook = new JOutlookBar();
		outlook.addIcon("Dev", new SelectAction("Claude", folderbar));
		outlook.addIcon("Dev", new SelectAction("Alan", folderbar));
		outlook.addIcon("Dev", new SelectAction("Susan", folderbar));
		outlook.addIcon("Dev", new SelectAction("Dennis", folderbar));
		outlook.addIcon("Dev", new SelectAction("Jar", folderbar));
		outlook.addIcon("Dev", new SelectAction("Greg", folderbar));
		outlook.addIcon("Ops", new SelectAction("Bill", folderbar));
		outlook.addIcon("Ops", new SelectAction("John", folderbar));
		outlook.addIcon("QA", new SelectAction("Allan", folderbar));
		outlook.addIcon("QA", new SelectAction("Mark", folderbar));
		outlook.addIcon("QA", new SelectAction("Salem", folderbar));
		outlook.addIcon("QA", new SelectAction("Naoko", folderbar));
		
		JFrame frame = new JFrame("Outlook Bar");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add("West", outlook);
		frame.getContentPane().add("Center", panel);
		frame.setBounds(0, 0, 500, 400);
		frame.setVisible(true);
	}

}
