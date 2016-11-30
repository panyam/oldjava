package ext.outlook;
/*
=====================================================================

	JOutlookBar.java

	Created by Claude Duguay
	Copyright (c) 1998

=====================================================================
*/

import java.awt.Color;
import java.util.Vector;

import com.sun.java.swing.Icon;
import com.sun.java.swing.Action;
import com.sun.java.swing.JPanel;

public class JOutlookBar extends ContextPanel
{
	protected Vector names = new Vector();
	protected Vector views = new Vector();
	
	public JOutlookBar() {}

	public void addIcon(String context, Action action)
	{
		int index;
		JPanel view;
		if ((index = names.indexOf(context)) > -1)
		{
			view = (JPanel)views.elementAt(index);
		}
		else
		{
			view = new JPanel();
			view.setBackground(Color.gray);
			view.setLayout(new ListLayout());
			names.addElement(context);
			views.addElement(view);
			addTab(context, new ScrollingPanel(view));
		}
		RolloverButton button = new RolloverButton(
			(String)action.getValue(Action.NAME),
			(Icon)action.getValue("LargeIcon"));
		button.addActionListener(action);
		view.add(button);
		doLayout();
	}
	
}
