package com.sri.test;

import java.awt.*;
import java.awt.event.*;
import com.sri.apps.mml.*;

class EquationTester extends TesterFrame {
	MMLEditor ef = new MMLEditor();

    public EquationTester () {
        super("Equation Field");
        setBackground(Color.black);
        setForeground(Color.white);
        ef.setBackground(Color.black);
        ef.setForeground(Color.white);
		addWindowListener(new WindowCloser(this));
        setLayout(new BorderLayout());
        add("Center",ef);
        setBounds(200,200,300,300);
        setVisible(true);
        toFront();
    }
}
