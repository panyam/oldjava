package com.sri.games.snake;
import java.awt.*;
import java.applet.Applet;
import java.awt.event.*;

public class SnakeFrame extends Frame 
                    implements WindowListener, ItemListener, 
						ActionListener, ComponentListener{
    GamePanel gp = new GamePanel(40,60,1,10);
    Applet parent = null;

    Button optionsButton = new Button("Options");
	Button startGame = new Button("Start Game");
    Panel optionsPanel = new Panel();

    Label widthLabel = new Label("Width");
    Label heightLabel = new Label("Height");
    Label foodPerLev = new Label("Food per level");

    TextField widthText = new TextField("40");
    TextField heightText = new TextField("40");
    TextField foodText = new TextField("10");

    private String colors[] = {
           "Black","Blue","Cyan","Dark Gray","Gray","Green","Light Gray",
           "Magenta","Orange","Pink","Red","White","Yellow"
    };

    private Color colorList[] = {
           Color.black,Color.blue,Color.cyan,Color.darkGray,Color.gray,
           Color.green,Color.lightGray,Color.magenta,Color.orange,Color.pink,
           Color.red,Color.white,Color.yellow
    };
            
    Choice  snakeColors = new Choice(), 
            wallColors = new Choice(),
            foodColors = new Choice(),
            fgColors = new Choice(),
            bgColors = new Choice();

    public SnakeFrame(Applet par) {
        parent = par;
        init();
    }

    public void init() {
        for (int i = 0;i < colors.length;i++) {
            snakeColors.add(colors[i]);
            foodColors.add(colors[i]);
            wallColors.add(colors[i]);
            bgColors.add(colors[i]);
            fgColors.add(colors[i]);
        }

		setResizable(false);
        layComponents();
        setListeners();
    }

    public void windowClosed(WindowEvent e) { destroy(); }
    public void windowActivated(WindowEvent e) { }
    public void windowClosing(WindowEvent e) { destroy(); }
    public void windowDeactivated(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }

    private void layComponents() {
       
                    // set up the options panel..
        optionsPanel.setLayout(new GridLayout(8,2));

        optionsPanel.add(widthLabel);   optionsPanel.add(widthText);
        optionsPanel.add(heightLabel);  optionsPanel.add(heightText);
        optionsPanel.add(foodPerLev);   optionsPanel.add(foodText);

        optionsPanel.add(new Label("Snake Color"));
        optionsPanel.add(snakeColors);

        optionsPanel.add(new Label("Wall Color"));
        optionsPanel.add(wallColors);

        optionsPanel.add(new Label("Food Color"));
        optionsPanel.add(foodColors);

        optionsPanel.add(new Label("Fore ground"));
        optionsPanel.add(fgColors);

        optionsPanel.add(new Label("Back ground"));
        optionsPanel.add(bgColors);
        
        setLayout(null);

        add(gp);
        add(optionsPanel);		
		add(optionsButton);
        addKeyListener(gp);
		gp.setVisible(true);
		optionsPanel.setVisible(false);
		optionsButton.setVisible(true);

        pack();
		doLayout();
        setLocation(50,20);
        setVisible(true);
        toFront();
    }

    public String getAppletInfo() {
        return "Snake V1.0\nDeveloped by\n  Sriram Panyam\n  Copyright 1999";
    }

    public void destroy() {
        setVisible(false);
        //dispose();
			// if not an applet then quit it...
        if (parent == null) System.exit(0);
    }

	protected Dimension prefSize = new Dimension(50, 50);
	public Dimension getPreferredSize()
	{
		Dimension d = gp.getPreferredSize();
		prefSize.width = d.width + 10;
		prefSize.height = d.height + 
						  optionsButton.getPreferredSize().height +
						  getInsets().top + getInsets().bottom + 20;
		return prefSize;
	}
	
	
	public void doLayout()
	{
		Dimension d = getSize();
		Dimension gps = gp.getPreferredSize();
		Dimension os = optionsButton.getPreferredSize();
		Insets insets = getInsets();
		int wl = d.width - (insets.left + insets.right);
		int hl = d.height - (insets.bottom + os.height);
		optionsButton.setBounds(insets.left, hl,
								wl, os.height);
		gp.setBounds(insets.left + (wl - gps.width) / 2, 
					 insets.top + ((hl - insets.top - gps.height) / 2),
					 wl, hl - insets.top - 10);
		optionsPanel.setBounds(insets.left + (wl - gps.width) / 2, 
					 insets.top + ((hl - insets.top - gps.height) / 2),
					 wl, hl - insets.top - 10);
	}
	
    private void setListeners() {
        snakeColors.addItemListener(this);
        foodColors.addItemListener(this);
        fgColors.addItemListener(this);
        bgColors.addItemListener(this);
        wallColors.addItemListener(this);

        optionsButton.addKeyListener(gp);
        optionsButton.addActionListener(this);
        startGame.addActionListener(this);
		addComponentListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        if  (e.getSource() == optionsButton) {
			if (optionsPanel.isShowing())
			{
				optionsPanel.setVisible(false);
				gp.setVisible(true);
				optionsButton.setLabel("Options");
				validate();
                if (gp.isGameRunning() && gp.isGameSuspended()) gp.resumeGame();
			} else
			{
                if (gp.isGameRunning()) gp.pauseGame();
				optionsButton.setLabel("Back to Game");
				optionsPanel.setVisible(true);
				gp.setVisible(false);
				validate();
			}
        } if (e.getSource() == startGame)
		{
			if (startGame.getLabel().indexOf("Start") >= 0)
			{
				if (gp.isGameRunning() && gp.isGameSuspended())
				{
					this.gp.resumeGame();
					startGame.setLabel("Pause Game");
				} else if (!gp.isGameRunning())
				{
					gp.startGame();
					startGame.setLabel("Pause Game");
				}
			} else
			{
				if (gp.isGameRunning())
				{
					if (!gp.isGameSuspended())
					{
						this.gp.pauseGame();
						startGame.setLabel("Start Game");
					}
				} else
				{
					startGame.setLabel("Start Game");
				}
			}
		}
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == snakeColors) {
            gp.setSnakeColor(colorList[snakeColors.getSelectedIndex()]);
        }
        else if (e.getSource() == wallColors) {
            gp.setWallColor(colorList[wallColors.getSelectedIndex()]);
        }
        else if (e.getSource() == foodColors) {
            gp.setFoodColor(colorList[foodColors.getSelectedIndex()]);
        }
        else if (e.getSource() == bgColors) {
            gp.setFGColor(colorList[fgColors.getSelectedIndex()]);
        }
        else if (e.getSource() == bgColors) {
            gp.setBGColor(colorList[bgColors.getSelectedIndex()]);
        }
    }

	public void componentHidden(ComponentEvent e) { }
	public void componentShown(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { }
	public void componentResized(ComponentEvent e)
	{
		validate();
	}
	
    public static void main(String args[]) {
        SnakeFrame na = new SnakeFrame(null);
    }
}