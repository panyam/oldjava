package com.sri.games.snake;

import com.sri.games.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends Panel implements KeyListener,ActionListener {
    SnakeCanvas nib;
    Timer timer = new Timer(Color.red,"0",5,1000,-1,1,1,false);
    LED led = new LED(Color.green,"0",5,1,false);
    Panel topPanel = new Panel(new BorderLayout());

    int numFoodAte = 0;
    int levelValue = 5;

    int currentLevel = 1;

    Label levelLabel = new Label("Level - " + currentLevel,Label.CENTER);

    public GamePanel(int w,int h,int speed,int len) {
        setLayout(new BorderLayout());
        nib = new SnakeCanvas(w,h,speed,len);
        add("Center",nib);

        levelLabel.setFont(new Font("Serif",Font.BOLD | Font.ITALIC,20));
        topPanel.add("East",led);
        topPanel.add("Center",levelLabel);
        topPanel.add("West",timer);
        add("North",topPanel);
        led.addKeyListener(this);
        timer.addKeyListener(this);
        addKeyListener(this);
        nib.addActionListener(this);
        setLevel(1);
    }

    public void setLevelValue(int l) {
        levelValue = l;
    }

    public void setLevel(int lev) {
        currentLevel = lev;
        levelLabel.setText("Level - " + currentLevel);
        numFoodAte = 0;
        if (currentLevel == 2) {
            int r = (nib.cols - 20) / 2;
            int c = nib.rows /2;
            for (int i = 0;i < 20;i++) nib.setWallAt(r,c+i);
        }
        else if (currentLevel == 3) {
            int r = (nib.cols - 20) / 3;
            int c = nib.rows/2;
            int r2 = r * 2;
            for (int i = 0;i < 20;i++) {
                nib.setWallAt(r,c+i);
                nib.setWallAt(r2,c+i);
            }
        }
        else if (currentLevel == 4) {
            for (int r = nib.rows/2,c = 10;c < 50;c++) {
                nib.setWallAt(r,c);
            }
            for (int r = 10,c = nib.cols / 2;r < 30;r++) {
                nib.setWallAt(r,c);
            }
        }
        else if (currentLevel == 5) {
            for (int r = 9,c = 9;r < 30;r++) {
                nib.setWallAt(r,c);
                nib.setWallAt(r,c + 40);
            }
            for (int r = 9,c = 9;c < 23;c++) {
                nib.setWallAt(r,c);
                nib.setWallAt(r + 20,c);
                nib.setWallAt(r,c + 26);
                nib.setWallAt(r + 20,c + 26);
            }
        }
        /*
        else if (currentLevel == 6) {
        }
        else if (currentLevel == 7) {
        }
        else if (currentLevel == 8) {
        }
        else if (currentLevel == 9) {
        }
        else if (currentLevel == 10) {
        }*/
    }
    
	public boolean isFocusTraversable()
	{
		return true;
	}
	
    public void setSnakeColor(Color c) {
        nib.setSnakeColor(c);
        repaint();
    }
    
    public void setWallColor(Color c) {
        nib.setWallColor(c);
        repaint();
    }
    
    public void setFoodColor(Color c) {
        nib.setFoodColor(c);
        repaint();
    }
    
    public void setFGColor(Color c) {
        nib.setFGColor(c);
        repaint();
    }

    public void setBGColor(Color c) {
        nib.setBGColor(c);
        repaint();
    }
    
	public boolean isGameRunning()
	{
		return nib.threadRunning;
	}
	
	public boolean isGameSuspended()
	{
		return nib.threadSuspended;
	}
	
    public synchronized void startGame()
	{
		startGame(true);
	}
	
    public synchronized void startGame(boolean notifyMainPanel)
	{
        nib.setSpeed(1);
        if (notifyMainPanel) nib.startGame();
        timer.restart(0);
		timer.start();
        led.setVal(0);
        setLevel(1);
    }
	
    public synchronized void resumeGame()
	{
		resumeGame(true);
	}

    public synchronized void resumeGame(boolean notifyMainPanel)
	{
        if (notifyMainPanel) nib.resumeGame();
        timer.resume();
    }

	public void pauseGame()
	{
		pauseGame(true);
	}
	
    public synchronized void pauseGame(boolean notifyMainPanel) {
        if (notifyMainPanel) nib.pauseGame();
        timer.pause();
    }

    public void actionPerformed (ActionEvent e) {
        if (e.getID() == SnakeCanvas.SNAKE_DIED_ACTION) {
            timer.stop();
        }
        else if (e.getID() == SnakeCanvas.SNAKE_ATE_FOOD_ACTION) {
            led.changeBy(1);
            numFoodAte ++;
            if (numFoodAte == levelValue) {
                nib.initialiseBoard();
                setLevel(currentLevel + 1);
                nib.setSpeed(nib.getSpeed() + 1);
            }
        }
        else if (e.getID() == SnakeCanvas.GAME_PAUSED) {
            pauseGame(false);
        }
        else if (e.getID() == SnakeCanvas.GAME_RESUMED) {
			resumeGame(false);
        }
        else if (e.getID() == SnakeCanvas.GAME_STARTED) {
			startGame(false);
        }
    }
    public void keyReleased(KeyEvent e) { nib.dispatchEvent(e); }
    public void keyPressed(KeyEvent e) { nib.dispatchEvent(e); }
    public void keyTyped(KeyEvent e) { nib.dispatchEvent(e); }
}
