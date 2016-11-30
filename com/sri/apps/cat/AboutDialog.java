package com.sri.apps.cat;

/*
 * Author...........: Sriram Panyam
 * File.............: AboutDialog.java
 * Date.............: 16 July 99
 * Email............: spany@students.cs.mu.oz.au
 * Version..........: 1.0
 * Purpose..........: Displays Credits for the CAT simulator
 */

import java.awt.*;
import java.awt.event.*;
import java.lang.*;

/**
 * This class shows the information regarding the project..
 */

public class AboutDialog extends Dialog implements ActionListener, Runnable{

                    // Button used to close the Dialog window..
    Button okButton = new Button("Ok");

    protected static String titles[] = {
        "Developed for ",
        "   The University of Melbourne",
        "   Department of Physics",
        "   Computational Physics",
        "   ",
        "Designed and Developed by",
        "-------------------------",
        "   ",
        "           Sriram Panyam",
        "   ",
        "   No animals were harmed in ",
        "   the making of this legendary ",
        "   applet.",
        "   A chicken sandwich was consumed",
        "   and that is about it.  I think.",
    };

    int width = 380,height = 350;
    int awidth,aheight;
    int maxStringSize = 0;
    int x = 0,y;


    Thread ourThread = null;

        /**
         * The constructor.  Does all the initialization.  The dialog box
         * is initially invisible.
         */
    public AboutDialog (Frame parent) {
        super(parent,"About CAT Simulator V1.0",true);
        setResizable(true);

        add("South",okButton);

        setFont(new Font("Serif",Font.BOLD,15));
        setBackground(bgColor);
        setForeground(fgColor);

        for (int i = 0;i < titles.length;i++) {
            if (titles[i].length() > titles[maxStringSize].length()) 
                maxStringSize = i;
        }

        okButton.addActionListener(this);
        okButton.setBackground(Color.lightGray);
        okButton.setForeground(Color.black);

        setBounds(100,100,width,height);

        setVisible(false);
    }


            /**
             * What happens when the Ok button is pressed????
             */
    public void actionPerformed(ActionEvent e) {

                    // if the ok button was clicked
        if (e.getSource() == okButton) {
            setVisible(false);
        }
    }

        /**
         * This function is called to show and hide this dialog.
         * When the dialog is being shown, the animation starts running.
         * When the dialog is being hidden, the animation stops.
         */
    public void setVisible(boolean show) {
        if (show) {
            startThread();
            super.setVisible(true);
        }
        else {
            stopThread();
            super.setVisible(false);
        }
    }

        // The following variables and functions, relate to the
        // animation in the dialog box.
    private Image buffer = null;
    private Graphics bufferGraphics = null;
    private Dimension buffSize = null;

    private long ourInterval = 20;
    private static Color bgColor = Color.black;
    private static Color fgColor = Color.white;
    private static Color starColor = Color.white;

    boolean inited = false;

    public void paint(Graphics g) {
        Insets insets = getInsets();
        if (buffer == null) update(g);
        g.drawImage(buffer,insets.left,insets.top,this);
    }

            /*
             * Standard stuff for double buffering.
             */
    public void update(Graphics g) {
        Dimension d = getSize();
        Insets insets = getInsets();

                // size of the ok button
        Dimension okd = okButton.getSize();

        awidth = d.width - insets.left - insets.right;
        aheight = d.height - insets.top - insets.bottom - okd.height;

                // if our buffer isnot created or it
                // is not the correct size then re create the buffer
                // image..
        if (buffer == null || (buffSize.width != awidth) ||
                                   (buffSize.height != aheight)) {

            if (bufferGraphics != null) bufferGraphics.dispose();

            buffSize = new Dimension(awidth,aheight);

            buffer = createImage(awidth,aheight);

            if (buffer == null) return;

            bufferGraphics = buffer.getGraphics();
        }

        bufferGraphics.setColor(bgColor);
        bufferGraphics.fillRect(0,0,d.width,d.height);
        bufferGraphics.setColor(fgColor);

		int i, t;
        Color store = bufferGraphics.getColor();
        int ypos = y;
        FontMetrics f = bufferGraphics.getFontMetrics();
        x = (width - f.stringWidth(titles[maxStringSize]))/2;
        int Asc = f.getAscent();
        bufferGraphics.setColor(Color.white);
        for (int in = 0;in < titles.length;in++) { 
            bufferGraphics.drawString(titles[in],x + 5,ypos);
            ypos += Asc;
        }
        y -= 1;
        if (y <= (-1 * ((titles.length - 1) * Asc))) y = height + f.getAscent();
        bufferGraphics.setColor(store);
        paint(g);
    }

        /*
         * This method is the main body of our continuously running thread.
         * It sleeps for a while and repaints.
         */
    public void run() {
        while (true) {
            try {
                Thread.sleep(ourInterval);
                repaint();
            }
            catch (InterruptedException e) { }
        }
    }

        /**
         *  Starts the animation in the dialog box.
         */
    public void startThread() {
        if (ourThread == null) {
            ourThread = new Thread(this);
            ourThread.start();
        }
    }

        /**
         * Stops the animation in the dialog box.
         */
    public void stopThread() {
        if (ourThread != null) {
            ourThread.stop();
            ourThread = null; 
        }
    }
}



