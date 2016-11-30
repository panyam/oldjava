
package com.sri.games.mine;

import com.sri.games.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

/**
 * The panel that coordinates information between all the other panels.
 */
public class GamePanel extends Panel implements ActionListener, ComponentListener
{
    Panel buttonPanel = new Panel();
    OptionsPanel optionsPanel = new OptionsPanel();
    boolean isOPShowing = false;
    Button newgame = new Button("New Game");
    Button options = new Button("Options...");

    private static final int BEGINNER = 0;
    private static final int INTERMEDIATE = 1;
    private static final int EXPERT = 2;
    private static final int CUSTOM = 3;
    public int level = BEGINNER;

    private int vals[][]={{8,8,10},{16,16,40},{16,30,99}};

	Dimension prefSize = new Dimension (5, 5);
    TopPanel topPanel;
    MineCanvas minePanel;

    private int HEIGHT = 450,WIDTH = 630;

        /**
         * Constructor.
         */
    public GamePanel()
    {
        setLayout(null);
        topPanel = new TopPanel();
        minePanel = new MineCanvas();
        minePanel.addActionListener(this);
        buttonPanel.setLayout(new GridLayout(1,2,10,10));
        buttonPanel.add(newgame);
        buttonPanel.add(options);
        newgame.addActionListener(this);
        options.addActionListener(this);
        addComponentListener(this);

        add(buttonPanel);
        add(topPanel);
        add(minePanel);
        add(optionsPanel);
        newBeginnerGame();
    }

    public void componentShown(ComponentEvent e){ }
    public void componentMoved(ComponentEvent e){ }
    public void componentHidden(ComponentEvent e){ }
    public void componentResized(ComponentEvent e)
    {
        if (e.getSource() == this)
        {
            doLayout();
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == minePanel)
        {
            int id = e.getID();
            if (id == MineCanvas.GAME_RESTARTED)
            {
                topPanel.timer.restart(0);
				//topPanel.timer.start();
                topPanel.numleft.setVal((long)minePanel.nummines);
            } else if (id == MineCanvas.GAME_WON)
            {
                topPanel.timer.stop();
                topPanel.numleft.setVal(0);
                topPanel.setText("You Win... Try Again???");
                topPanel.setBlink(true);
            } else if (Math.abs(id) == MineCanvas.SQUARE_FLAGGED)
            {
                topPanel.numleft.changeBy(id < 0 ? -1 : 1);
            } else if (id == MineCanvas.START_TIMER)
            {
                System.out.println("Starting ...");
                topPanel.timer.start();
            } else if (id == MineCanvas.GAME_LOST)
            {
                topPanel.timer.stop();
                topPanel.setText("You Lose... Better Luck Next Time");
                topPanel.setBlink(true);
            }
        } else if (e.getSource() == newgame) {
            switch(optionsPanel.getLevel()) {
                case 0 : newBeginnerGame(); break;
                case 1 : newIntermediateGame(); break;
                case 2 : newExpertGame(); break;
                case 3 : newCustomGame(optionsPanel.getRows(),
                                       optionsPanel.getCols(),
                                       optionsPanel.getMines());
                    break;
            }
            topPanel.setBlink(false);
            topPanel.setText();
            int top,right = 0,bottom = 0,left;
            isOPShowing = false;
            doLayout();
            options.setLabel("Difficulty ...");
        } else if (e.getSource() == options) {
            isOPShowing = !isOPShowing;
            options.setLabel(isOPShowing ? "Difficulty" : "Back to Game");
            doLayout();
        }
    }

    public Insets getInsets() { return new Insets(0,0,0,0); }

    public void newBeginnerGame() {
        level = BEGINNER;
        minePanel.restart(vals[level][0], vals[level][1],vals[level][2]);
    }

    public void newIntermediateGame() {
        level = INTERMEDIATE;
        minePanel.restart(vals[level][0], vals[level][1],vals[level][2]);
    }

    public void newExpertGame() {
        level = EXPERT;
        minePanel.restart(vals[level][0], vals[level][1],vals[level][2]);
    }

    public void newCustomGame(int nrows,int ncols,int nummines) {
        level = CUSTOM;
        if (nummines <= (nrows * ncols)) minePanel.restart(nrows,ncols,nummines);
    }

    void PressedRect(Graphics g,int x,int y,int w,int h) {
        Color dg = new Color(128,128,128);
        g.setColor(Color.lightGray);
        g.drawLine(x+1,y+h-1,x+1,y+h-1);
        g.setColor(dg);
        g.drawLine(x,y,x+w-1,y);
        g.drawLine(x,y,x,y+h-1);
        g.setColor(Color.black);
        g.drawLine(x+1,y+1,x+w-2,y+1);
        g.drawLine(x+1,y+1,x+1,y+h-2);
        g.setColor(Color.white);
        g.drawLine(x+w,y,x+w,y+h);
        g.drawLine(x+w,y+h,x,y+h);
    }

		/**
		 * Gets the rpeferred size.
		 */
	public Dimension getPreferredSize()
	{
        Dimension ts = topPanel.getPreferredSize();
        Dimension bs = buttonPanel.getPreferredSize();
        Dimension ms = minePanel.getPreferredSize();
		prefSize.width = Math.max(ts.width, Math.max(bs.width, ms.width));
		prefSize.height = ts.height + ms.height + bs.height + 20;
		return prefSize;
	}

        /**
         * Does the lay out of the panels.
         */
    public void doLayout()
    {
        Dimension d = getSize();
        Dimension ts = topPanel.getPreferredSize();
        Dimension bs = buttonPanel.getPreferredSize();
        Dimension ms = minePanel.getPreferredSize();
        topPanel.setVisible(false);
        minePanel.setVisible(false);
        optionsPanel.setVisible(false);
        if (isOPShowing)
        {
            optionsPanel.setVisible(true);
            optionsPanel.setBounds(0, 0, d.width, d.height - bs.height);
			optionsPanel.doLayout();
        } else
        {
            topPanel.setVisible(true);
            minePanel.setVisible(true);
            topPanel.setBounds(0, 0, d.width, ts.height);
            minePanel.setBounds((d.width - ms.width) / 2, 
                                ts.height + ((d.height - ts.height -
                                            bs.height - ms.height) / 2),
                                ms.width,
                                ms.height);
        }
        buttonPanel.setBounds(0, d.height - bs.height, d.width, bs.height);
    }
}

class TopPanel extends Panel implements Runnable{
    LED numleft;
    Timer timer;
    int nleft;
    static final int HAPPY = 1,TROUBLE = 2,DEAD = 3;
    int state = HAPPY;
    static Thread t;
    int x = 0;
    boolean blink = false;
    private Color bgcol = Color.black.brighter();
    private Color fgcol = Color.white;
    String caption = 
        "Mine Sweeper V1.0 - by Sriram Panyam Copyright 1998";
    String scrollText = new String(caption);
    Image buff = null;
    Graphics gc = null;
    Dimension bd = new Dimension();
    boolean inited = false;
    int pw,ph,w;

        /**
         * Constructor.
         */
    public TopPanel()
    {
        setLayout(new BorderLayout());
        setBackground(bgcol);
        setForeground(fgcol);
        setFont(new Font("Serif",Font.BOLD,15));
        timer = new Timer(Color.red,"000",4,1000,9999,1,1,true);
        numleft = new LED(Color.green,"99",3,1,false);
        add("West",numleft);
        add("East",timer);
        t = new Thread(this);
        t.start();
    }

    public void setText() { setText(caption); }
    public void setText(String text) { this.scrollText = text; }
    public void setBlink(boolean blink) { this.blink = blink; }

    public void initGraphics()
    {
        pw = getSize().width - numleft.getSize().width - timer.getSize().width;
        ph = getSize().height;
        if (pw <= 0 || ph == 0) return ;
        if ((buff == null) || (pw != bd.width) || (ph != bd.height))
        {
            bd.width = pw;
            bd.height = ph;
            buff = createImage(pw,ph);
            if (buff == null) return ;
            gc = buff.getGraphics();
            inited = true;
            x = bd.width;
        }
    }

    public void paint(Graphics g)
    {
		try
		{
			initGraphics();
			if (gc == null || buff == null) return;
			FontMetrics f = gc.getFontMetrics();
			int w = f.stringWidth(scrollText);
			gc.setColor(bgcol);
			gc.fillRect(0,0,pw,ph);
			//gc.setColor((blink && (x%5 == 0) ? Color.lightGray : Color.black));
			gc.setColor(fgcol);
			gc.drawString(scrollText,x,f.getAscent() + 2);
			if (--x < -w) x = bd.width;
			g.drawImage(buff,numleft.getSize().width + 1,0,null); 
		} catch (Exception e)
		{
		}
    }

    public void update(Graphics g) {
    }

    public void run() {
        //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        while (true) {
            try { Thread.sleep(20); paint(getGraphics()); }
            catch (InterruptedException e) { }
        }
    }
}
