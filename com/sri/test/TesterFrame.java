package com.sri.test;

import java.awt.*;
import java.awt.event.*;

public class TesterFrame extends Frame
{
	public TesterFrame(String name)
	{
		super(name + " Tester - by Sri Panyam");
		addWindowListener(new WindowCloser(this));
	}
}
