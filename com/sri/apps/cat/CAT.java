package com.sri.apps.cat;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.applet.*;
import java.io.*;
import java.awt.datatransfer.*;
import com.sri.gui.core.containers.*;

public class CAT extends Frame implements
                ActionListener, WindowListener, ItemListener, 
                Runnable, ClipboardOwner {
    protected Clipboard clipBoard = null;
    protected Applet parentApplet = null;
    protected AboutDialog ad = new AboutDialog(this);
    protected boolean doAnimation = true;
    protected boolean invertColors = true;
    protected boolean scanStarted = false;
    protected int newIW, newIH;
    protected double spacing;
    protected double lowerBound;
    protected int numAngles, numP;
    protected String currFile = null; 
    protected Projection known = new KnownProjection(1.0);
    protected Projection unknown = null;

    protected Projection currProj = null;

    protected Thread ourThread = null;

        // some double bufferin stuff....


    protected ScrollPane scroller
                        = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    protected ImagePanel imageViewer = new ImagePanel();

    protected MenuBar mbar = new MenuBar();
    protected Menu fileMenu = new Menu("File");
    protected MenuItem clearImage = new MenuItem("Clear Image...");
    protected MenuItem saveAsPGM = new MenuItem("Save Image As PGM...");
    protected MenuItem saveAsBMP = new MenuItem("Save Image As BMP...");
    protected MenuItem printMenuItem = new MenuItem("Print Image...");
    protected MenuItem exitMenuItem = new MenuItem("Exit CAT Simulator");

    protected Menu editMenu = new Menu("Edit");
    protected MenuItem copyMenuItem = new MenuItem("Copy");

    protected Menu projMenu = new Menu("Projections");
    protected CheckboxMenuItem knownProj = new CheckboxMenuItem("Known",true);
    protected CheckboxMenuItem unknownProj
                    = new CheckboxMenuItem("Unknown ...",false);

    protected Checkbox toggleInversion = new Checkbox("Invert Colors",invertColors);
    protected Checkbox toggleAnimation = new Checkbox("Animate",doAnimation);
    protected Label lbLabel = new Label("LB = ",Label.LEFT);
    protected Label mesgLabel = new Label("",Label.LEFT);
    protected TextField numAnglesText = new TextField("24",10);
    protected TextField numProjsText = new TextField("32",10);
    protected TextField spacingText = new TextField("0.1",10);
    protected TextField imageWidthText = new TextField("200",10);
    protected TextField imageHeightText = new TextField("200",10);
    protected Button startScanButton = new Button("Start Scan");

    protected Menu helpMenu = new Menu("Help");
    protected MenuItem aboutCAT = new MenuItem("About CAT Simulator...");

    public CAT() {
       super ("CAT Scan Simulator");

       clipBoard = getToolkit().getSystemClipboard();
       setMenus();
       setListeners();
       layComponents();
    }

    protected void setMenus() {
        System.out.println("Setting Menus...");
        fileMenu.add(clearImage);
        fileMenu.addSeparator();
        fileMenu.add(saveAsPGM);
        fileMenu.add(saveAsBMP);
        fileMenu.addSeparator();
        fileMenu.add(printMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        mbar.add(fileMenu);
        editMenu.add(copyMenuItem);
        editMenu.addSeparator();
        mbar.add(editMenu);

        currProj = known;
        projMenu.add(knownProj);
        projMenu.add(unknownProj);
        mbar.add(projMenu);

        helpMenu.addSeparator();
        helpMenu.add(aboutCAT);
        mbar.add(helpMenu);
        setMenuBar(mbar);
    }

    protected void setListeners() {
        System.out.println("Adding Event Listeners...");
        clearImage.addActionListener(this);
        saveAsPGM.addActionListener(this);
        saveAsBMP.addActionListener(this);
        exitMenuItem.addActionListener(this);

        copyMenuItem.addActionListener(this);

        printMenuItem.addActionListener(this);;

        knownProj.addItemListener(this);
        unknownProj.addItemListener(this);

        aboutCAT.addActionListener(this);

        toggleAnimation.addItemListener(this);
        toggleInversion.addItemListener(this);
        startScanButton.addActionListener(this);
    }

    protected void layComponents() {
        System.out.println("Laying out Components...");

        Panel comps = new Panel(new BorderLayout());
        Panel p1 = new EtchedPanel("# Angles");
        p1.add(numAnglesText);
        Panel p2 = new EtchedPanel("# Projections");
        p2.add(numProjsText);
        Panel p3 = new EtchedPanel("Spacing");
        p3.add(spacingText);
        Panel p4 = new EtchedPanel("Image Width");
        p4.add(imageWidthText);
        Panel p5 = new EtchedPanel("Image Height");
        p5.add(imageHeightText);

        Panel topPanel = new Panel(new GridLayout(2,1,1,1));
        Panel centerPanel = new Panel(new GridLayout(5,1,5,5));
        topPanel.add(toggleAnimation);
        topPanel.add(toggleInversion);
        centerPanel.add(p1);
        centerPanel.add(p2);
        centerPanel.add(p3);
        centerPanel.add(p4);
        centerPanel.add(p5);
        comps.add("South",startScanButton);
        comps.add("Center",centerPanel);
        comps.add("North",topPanel);

        setLayout(new BorderLayout());
        scroller.add(imageViewer);

        Panel midPanel = new Panel(new BorderLayout());
        Panel statusBar = new Panel(new GridLayout(1,2,5,5));

        statusBar.add(lbLabel);
        statusBar.add(mesgLabel);

        midPanel.add("Center",scroller);
        midPanel.add("South",statusBar);
        add("Center",midPanel);
        add("East",comps);
        pack();
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == knownProj) {
            if (knownProj.getState()) {
                unknownProj.setState(false);
                currProj = known;
            } else {
                currProj = null;
            }
        } else if (e.getSource() == unknownProj) {
            if (!unknownProj.getState()) {
                knownProj.setState(false);
                currProj = null;
            } else {
                knownProj.setState(false);
                currProj = null;
                FileDialog fd =
                    new FileDialog(this,"Select Data File",FileDialog.LOAD);

                fd.setVisible(true);

                currFile = fd.getDirectory();

                if (currFile != null &&
                    currFile.charAt(currFile.length() - 1) != '/') {
                    //currFile += '/';
                }

                System.out.println("Directory = " + currFile);

                currFile += fd.getFile();

                if (currFile != null) {
                    System.out.println("File selected = " + currFile);
                    try {
                        mesgLabel.setText("Loading Data File...");
                        startScanButton.setEnabled(false);
                        unknown = new UnknownProjection(1.0,
                            new DataInputStream(new FileInputStream(currFile)));
                        mesgLabel.setText("");
                        startScanButton.setEnabled(true);
                    } catch (IOException ex) {
                        System.err.println("Cannot open \"" + currFile + "\"");
                        startScanButton.setEnabled(true);
                        currProj = null;
                        return ;
                    }
                }
                currProj = unknown;
            }
        } else if (e.getSource() == toggleAnimation) {
            doAnimation = toggleAnimation.getState();
        } else if (e.getSource() == toggleInversion) {
            invertColors = toggleInversion.getState();
        }
    }

    protected boolean validateInputs() {
        String curr = "";
        try {
            numAngles = Integer.parseInt(numAnglesText.getText());
        } catch (NumberFormatException e) {
            System.err.println("Number Format error in # angles");
            numAnglesText.transferFocus();
            return false;
        }
        try {
            numP = Integer.parseInt(numProjsText.getText());
        } catch (NumberFormatException e) {
            System.err.println("Number Format error in # Projections");
            numProjsText.transferFocus();
            return false;
        }
        try {
            newIW = Integer.parseInt(imageWidthText.getText());
        } catch (NumberFormatException e) {
            System.err.println("Number Format error in Image Width");
            imageWidthText.transferFocus();
            return false;
        }
        try {
            newIH = Integer.parseInt(imageHeightText.getText());
        } catch (NumberFormatException e) {
            System.err.println("Number Format error in Image Height");
            imageHeightText.transferFocus();
            return false;
        }
        try {
            spacing = (Double.valueOf(spacingText.getText())).doubleValue();
        } catch (NumberFormatException e) {
            System.err.println("Number Format error in spacing");
            spacingText.transferFocus();
            return false;
        }
        lowerBound = -numP * spacing / 2.0;
        lbLabel.setText("LB = " + lowerBound);
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startScanButton) {
            if (scanStarted) {
                            // means the label is "Stop Scan"
                startScanButton.setLabel("Start Scan");
                stopScan();
            } else {
                            // means the label is "Start Scan"
                startScanButton.setLabel("Stop Scan");
                if (validateInputs()) startScan();
            }
        } else if (e.getSource() == printMenuItem) {
            Image im = imageViewer.getImage();
            if (im != null) {
                PrintJob pj = Toolkit.getDefaultToolkit().
                                        getPrintJob(this,"Print Image",null);
                if (pj != null) {
                    Graphics pg = pj.getGraphics() ;
                    if (pg != null) {
                        Dimension d = pj.getPageDimension();
                        pg.drawImage(im,
                                    (d.width - imageViewer.getImageWidth())/2,
                                    (d.height-imageViewer.getImageHeight())/2,
                                    this);
                        pg.dispose();
                    }
                }
            }
        } else if (e.getSource() == aboutCAT) {
            ad.setVisible(true);
        } else if (e.getSource() == copyMenuItem) {
            System.out.println("Copying to Clip Board...");
            //StringSelection con = new StringSelection("Testing copy");
            clipBoard.setContents(imageViewer,this);
        } else if (e.getSource() == clearImage) {
            //currFile = null;
            imageViewer.setImage(null);
        } else if (e.getSource() == saveAsPGM || e.getSource() == saveAsBMP) {
            Image im = imageViewer.getImage();
            if (!scanStarted && im != null) {
                int w = imageViewer.getImageWidth();
                int h = imageViewer.getImageHeight();
                int list[] = new int[w * h + 1];
                PixelGrabber pg = new PixelGrabber(im,0,0,w,h,list,0,w);
                try {
                    pg.grabPixels();
                }
                catch (InterruptedException ex) {

                }
                String fname;
                FileDialog fd =
                    new FileDialog(this,"Select output file",FileDialog.SAVE);

                fd.setVisible(true);

                fname = fd.getDirectory();

                if (fname != null &&
                    fname.charAt(fname.length() - 1) != '/') {
                    //currFile += '/';
                }

                System.out.println("Directory = " + fname);

                fname += fd.getFile();

                if (fname != null) {
                    System.out.println("File selected = " + fname);
                    try {
                        mesgLabel.setText("Saving Data File...");
                        startScanButton.setEnabled(false);
                        DataOutputStream dout = new DataOutputStream(
                                                   new FileOutputStream(fname));
                        if (e.getSource() == saveAsPGM) {
                            PPMReader.toOutputStream(dout,list,h,w,255,
                                                    "Creator: Sri Panyam", 5);
                        } else if (e.getSource() == saveAsBMP) {
                            //BMPReader.toOutputStream(dout,list,h,w,255,
                                                    //"Creator: Sri Panyam",5);
                        }
                        mesgLabel.setText("");
                        startScanButton.setEnabled(true);
                    } catch (IOException ex) {
                        System.err.println("Cannot open \"" + currFile + "\"");
                        startScanButton.setEnabled(true);
                        return ;
                    }
                }
            }
        } else if (e.getSource() == exitMenuItem) {
            dispose();
            System.exit(0);
        }
    }

    public void windowOpened(WindowEvent e) { }
    public void windowClosing(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }

    protected static DirectColorModel ourCM 
                    = new DirectColorModel(32,  0xff0000,0x00ff00, 
                                                0x0000ff,0xff000000);

    public void lostOwnership(Clipboard cl, Transferable contents) {

    }

    public void run() {
                /* calculate the high angle... */
        double hiAngle = (180 - (180.0 / numAngles)) * Math.PI / 180.0;
        double xIncr = 2.0 / newIW , yIncr = 2.0 / newIH;
        double currY, currX, xPrime;
        double projs[] = new double[1 + numP];
        double xks[] = new double[1 + numP];

                // the list of gradients and intercepts between two
                // adjacanet projections... used for interpolation...
        double ms[] = new double[1 + numP], cs[] = new double[1 + numP];
        double currAngle, angleIncr = hiAngle / numAngles;
        double startP = -spacing * numP / 2.0,currP, cp;
        double holder[] = new double[3 + 2 * numP];
        double currP1, currP2;
        int k, half = numP >> 1, doub = numP << 1;

            // some parameters for the Kx list...
        double deltaK = 2 * Math.PI / (numP * spacing);
        double kx[] = new double[1 + doub];

            // some image stuff...
        double imageArray[] = new double[newIW * newIH + 1];
        double imgmin = 0, imgmax = 0;

        mesgLabel.setText("Scan Started");

        for (int i = 1;i <= doub;i += 2) {
            kx[i] = kx[i - 1] = Math.abs(((i >> 1) - (i > numP ? numP : 0)) / 
                                        (numP * deltaK));
        }

        if (currProj == null) {
            System.err.println("No projection object is set.");
            scanStarted = false;
            ourThread = null;
            startScanButton.setLabel("Start Scan");
            return ;
        }

                // returns the x value of the ith point...
        for (int i = 0;i < numP;i++) xks[i] = (i * spacing) + lowerBound;

        for (int i = 0;i < numAngles;i++) {
            currAngle = i * angleIncr;
            mesgLabel.setText("Angle # " + (currAngle * 180.0 / Math.PI));
            double costheta = Math.cos(currAngle);
            double sintheta = Math.sin(currAngle);

            //System.out.println("Angle = " + currAngle);
                    // Now we actually calculate the projections
                    // due to the object..
                    // to make it more efficient calculate from 0 on wards
                    // on to the first half of the array and then from
                    // lowerbound to 0 onto the secon dhalf..
            for (int j = 0;j < half;j++) {
                currP1 = currProj.projection(currAngle,startP + spacing * j);
                currP2=currProj.projection(currAngle,startP+ spacing*(j+half));

                if (invertColors) {
                    currP1 = 1 - currP1;
                    currP2 = 1 - currP2;
                }

                holder[1 + numP + (j << 1)] = currP1;
                holder[2 + numP + (j << 1)] = 0;

                holder[1 + (j << 1)] = currP2;
                holder[2 + (j << 1)] = 0;
            }

            for (int j = 0; j < numP;j++) {
                double cup = startP + spacing * j;
                double pr = (j >= half ? holder[1 + ((j - half) << 1)] :
                                         holder[1 + numP + (j << 1)]);
                //System.out.println("CurrP = " + cup + " Proj = " + pr);
            }

                        // now that we have the list of projections
                        // apply a fft onto it and then multiply it
                        // with the Kx list...
            four1(holder,numP,-1);

                /*
                 * Next multiply by a factor of Kx'
                 */
            for (int j = 1;j <= doub; j += 2) {
                holder[j] *= kx[j - 1];
                holder[j + 1] *= kx[j - 1];
            }

            four1(holder,numP,1);

            for (int j = 0;j < half;j++) {
                projs[j] = holder[1 + numP + (j << 1)];
                projs[j + half] = holder[1 + (j << 1)];
            }

            for (int j = 1;j < numP;j++) {
                int j1 = j - 1;
                ms[j1] = ((projs[j] - projs[j1])/(xks[j] - xks[j1]));
                cs[j1] = projs[j1] - (ms[j1] * xks[j1]);
            }
                        /**
                         * Now go through every pixel 
                         * and find the attenuation function.
                         */
            int cnt = -1;
            for (int y = 0; y < newIH;y++) {
                currY = (y * yIncr) - 1;
                for (int x = 0;x < newIW;x++) {
                    cnt++;
                    if (i == 0) imageArray[cnt] = 0.0;

                    currX = (x * xIncr) - 1;
                    xPrime = (currX * costheta) + (currY * sintheta);

                    k = (int)((xPrime - lowerBound) / spacing);
                    //imageArray[cnt] += ((projs[k] + projs[k + 1]) / 2);
                    imageArray[cnt] += ((xPrime * ms[k]) + cs[k]);
                }
            }
            if (doAnimation) {
                double pv;
                int count = 0;
                for (int row = 0;row < newIH; row++) {
                    for (int col = 0;col < newIW; col++) {
                        pv = imageArray[count++];
                        imgmin = (imgmin < pv ? imgmin : pv);
                        imgmax = (imgmax > pv ? imgmax : pv);
                    }
                }
                double scale=255.0/(imgmax-imgmin);
                int pixList[] = new int[newIW * newIH];
                int c = 0,v;
                for (int y = 0; y < newIH ; y++) {
                    for (int x = 0; x < newIW ; x++) {
                        v = (int)((imageArray[c] - imgmin) * scale);
                        pixList[c++] =  (0xff << 24) | 
                                        (((v & 0xff)) << 16) | 
                                        (((v & 0xff)) << 8) | 
                                        (((v & 0xff)));
                    }
                }

                imageViewer.setImage(createImage(
                   new MemoryImageSource(newIW,newIH,ourCM,pixList,0,newIW)));
            }
        }
        double pv;
        int count = 0;
        for (int y = 0;y < newIH;y++) {
            for (int x = 0;x < newIW;x++) {
                pv = imageArray[count++];
                imgmin = (imgmin < pv ? imgmin : pv);
                imgmax = (imgmax > pv ? imgmax : pv);
            }
        }
        double scale=255.0/(imgmax-imgmin);
        int pixList[] = new int[newIW * newIH];
        int c = 0,v;
        for (int y = 0; y < newIH ; y++) {
            for (int x = 0; x < newIW ; x++) {
                v = (int)((imageArray[c] - imgmin) * scale);
                pixList[c++] =  (0xff << 24) | 
                                (((v & 0xff)) << 16) | 
                                (((v & 0xff)) << 8) | 
                                (((v & 0xff)));
            }
        }

        imageViewer.setImage(createImage(
                    new MemoryImageSource(newIW,newIH,ourCM,pixList,0,newIW)));
        System.err.println("CAT Scan Complete");
        scanStarted = false;
        ourThread = null;
        startScanButton.setLabel("Start Scan");
        mesgLabel.setText("Scan Complete!");
    }

    public void startScan() {
        if (ourThread == null) ourThread = new Thread(this);
        if (ourThread.isAlive()) ourThread.stop();
        imageViewer.setImage(null);
        scanStarted = true;
        ourThread.start();
    }

    public void stopScan() {
        if (ourThread != null) {
            if (ourThread.isAlive()) ourThread.stop();
            ourThread = null;
        }
        scanStarted = false;
    }

    public static void main(String args[]) {
        CAT cat = new CAT();
        cat.setVisible(true);
        cat.setBounds(50,50,300,300);
        cat.toFront();
        cat.pack();
    }

    protected void four1(double data[],int nn,int isign) {
	    int n,mmax,m,j,istep,i;
	    double wtemp,wr,wpr,wpi,wi,theta;
	    double tempr,tempi;
    
	    n=nn << 1;
	    j=1;
	    for (i=1;i<n;i+=2) {
		    if (j > i) {
                double ttmp = data[j]; data[j] = data[i]; data[i] = ttmp;
                ttmp = data[j + 1]; data[j + 1] = data[i + 1]; data[i + 1]=ttmp;
		    }
		    m=n >> 1;
		    while (m >= 2 && j > m) {
			    j -= m;
			    m >>= 1;
		    }
		    j += m;
	    }
	    mmax=2;
	    while (n > mmax) {
		    istep=2*mmax;
		    theta=6.28318530717959/(isign*mmax);
		    wtemp=Math.sin(0.5*theta);
		    wpr = -2.0*wtemp*wtemp;
		    wpi=Math.sin(theta);
		    wr=1.0;
		    wi=0.0;
		    for (m=1;m<mmax;m+=2) {
			    for (i=m;i<=n;i+=istep) {
				    j=i+mmax;
				    tempr=wr*data[j]-wi*data[j+1];
				    tempi=wr*data[j+1]+wi*data[j];
				    data[j]=data[i]-tempr;
				    data[j+1]=data[i+1]-tempi;
				    data[i] += tempr;
				    data[i+1] += tempi;
			    }
			    wr=(wtemp=wr)*wpr-wi*wpi+wr;
			    wi=wi*wpr+wtemp*wpi+wi;
		    }
		    mmax=istep;
	    }
    }
}
