package com.sri.test;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.ext.*;

public class BufferTest extends TesterFrame
{
	Buffer buff = new Buffer(300,300);
	Image im = null;
	
	public BufferTest(){
		super("Buffer");
		setBackground(Color.lightGray);
		setVisible(true);
		setBounds(150,150,400,400);
		toFront();
		buff.drawLine(150,150,50,50);
		im = createImage(buff.getMemoryImageSource());
	}

	public void update(Graphics g)
	{
		paint(g);
	}
	
	public void paint(Graphics g)
	{
		g.drawImage(im,getInsets().left,getInsets().top,this);
	}
	
	public void itemStateChanged(ItemEvent e) {
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Yep " + Math.random());
	}
}
