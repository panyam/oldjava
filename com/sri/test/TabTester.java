package com.sri.test;

import com.sri.gui.core.containers.tabs.*;
import com.sri.gui.core.button.*;
import java.awt.*;

public class TabTester extends TesterFrame
{
	TabPanel tab = new TabPanel(false);

	TabLayout tl = null;
	
    public TabTester () {
        super("Tab Panel");
		tl = new VerticalTabLayout(VerticalTabLayout.LEFT);
		//tl = new  SingleLineHorizontalTabLayout(SingleLineHorizontalTabLayout.TOP);
		for (int i = 0;i < 5;i++)
		{
			tab.add(new Label("Label " + i),new SActionButton("Item " + i));
		}
		tab.setLayout(tl);
		tab.setBackground(Color.lightGray);

        setLayout(new BorderLayout());
		tab.setLayout(tl);
        add("Center",tab);
        setBounds(200,200,300,300);
        setVisible(true);
        toFront();
    }
}
