package com.sri.test;

import java.awt.*;
import java.awt.event.*;

class WindowCloser extends WindowAdapter {
    Frame f;
    public WindowCloser(Frame f) {
        this.f = f;
        f.addWindowListener(this);
    }

    public void windowClosing(WindowEvent e) {
        if (e.getSource() instanceof Frame) {
            f.setVisible(false);
            f.dispose();
            System.exit(0);
        }
    }
}
