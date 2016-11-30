package com.sri.games.snake;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


public class SnakeCanvas extends Canvas implements Runnable, 
                                              KeyListener {
    public static final int GRID_SIZE = 10;
    private static final String list[] = {
        "Snake V1.0",
        "by",
        "   Sriram Panyam",
        "   Copyright 1999",
        " ",
        "   KEYS",
		"   S to start new Game",
		"   P to pause current game",
        "   Arrow keys to navigate",
    };

    public static final int SNAKE_DIED_ACTION = 1;
    public static final int SNAKE_ATE_FOOD_ACTION = 2;
    public static final int GAME_STARTED = 3;
    public static final int GAME_PAUSED = 4;
    public static final int GAME_RESUMED = 5;

    protected         ActionListener      actionListener = null;

    Thread ourThread = null;
	protected boolean threadRunning = false;
	protected boolean threadSuspended = false;

    int numMice = 10;
    int numLevels = 10;

    short board[][];

    static final short EMPTY = 1;
    static final short WALL = 2;
    static final short FOOD = 3;
    static final short SNAKE = 4;

    Color fgColor = Color.black;
    Color bgColor = Color.lightGray;
    Color wallColor = Color.blue;
    Color snakeColor = Color.red;
    Color foodColor = Color.magenta;

    Dimension offDimension;
    Image     offImage;
    Graphics offGraphics;

    int speed = 1;
    int interval;

    Vector walls = new Vector();
    Point currFood = null;

    int numFood = 10;
    int foodPrice = 5;

    int cols,rows;

    Snake ourSnake;

    public Dimension getPreferredSize() {
        return new Dimension((cols + 1) * GRID_SIZE, (rows + 1) * GRID_SIZE);
    }

    public SnakeCanvas(int rows, int cols,int speed,int snL) {
        board = new short[rows][cols];

        setSpeed(speed);

        ourSnake = new Snake(new Point(cols/2,rows/2),Snake.UP,snL);

        setSize(rows,cols);

        makeAnotherFood();

        addKeyListener(this);
    }

    public void removeWall(int x,int y) {
        if (x <= 0 || x >= (cols - 1) || y <= 0 || y >= (rows - 1)) return ;
        board[x][y] = EMPTY;
    }

    public void addWall(int x,int y) {
        if (x <= 0 || x >= (cols - 1) || y <= 0 || y >= (rows - 1)) return ;
        if (board[x][y] == EMPTY) board[x][y] = WALL;
    }

    public int getSpeed() { return speed; }

    public void setSpeed(int speed) {
        if (speed < 1 || speed > 10) return ;
        this.speed = speed;
        interval = (11 - speed) * 8;
    }

    public void setWallAt(int r,int c) {
        board[r][c] = WALL;
        walls.addElement(new Point(r,c));
    }

    public void initialiseBoard() {

        walls.removeAllElements();

                    // initialise all board positions to empty...

        for (int i = 0;i < rows;i++) {
            for (int j = 0;j < cols;j++) {
                if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) {
                    board[i][j] = WALL;
                    walls.addElement(new Point(i,j));
                }
                else board[i][j] = EMPTY;
            }
        }
    }

    public void setSize(int r,int c) {
        this.rows = r;
        this.cols = c;

        initialiseBoard();
    }
    
    public void setSnakeColor(Color c) {
        snakeColor = c;
    }
    
    public void setWallColor(Color c) {
        wallColor = c;
    }
    
    public void setFoodColor(Color c) {
        foodColor = c;
    }
    
    public void setFGColor(Color c) {
        fgColor = c;
    }

    public void setBGColor(Color c) {
        bgColor = c;
    }
    
    private void makeAnotherFood() {
        boolean foundFood = false;
        Point p = new Point();
        while (!foundFood) {
            int r = ((int)(Math.random() * 1000)) % rows;
            int c = ((int)(Math.random() * 1000)) % cols;

            p.x = r;
            p.y = c;

            if (r > 0 && r < rows - 1 && 
                c > 0 && c < cols - 1 &&
                board[r][c] == EMPTY &&
                !ourSnake.intersectsItself(p,false)) {

                if (currFood != null) {
                    board[currFood.x][currFood.y] = EMPTY;
                }
                board[r][c] = FOOD;
                currFood = new Point(r,c);
                foundFood = true;
            }
        }
    }

    private int randomDir() {
        return ((int)(Math.random() * 10)) % 4;
    }

    private void checkSnakePos() {
        Point p = ourSnake.head;
        if (ourSnake.intersectsItself(ourSnake.head,true) || 
                                        board[p.y][p.x] == WALL) {
            board[currFood.x][currFood.y] = EMPTY;
            currFood = null;

            initialiseBoard();

            ourSnake = new Snake(new Point(rows/2,cols/2),randomDir(),5);

            if (ourThread != null) 
			{
				threadRunning = false;
				notify();
			}
            ourThread = null;
            paint(getGraphics());
            processActionEvent(SNAKE_DIED_ACTION);
            return ;
        }
        else if (board[p.y][p.x] == FOOD) {
            board[p.y][p.x] = EMPTY;
            processActionEvent(SNAKE_ATE_FOOD_ACTION);
            ourSnake.increaseBy(foodPrice);
            makeAnotherFood();
            return ;
        }
    }

    int counter = 1;

    public synchronized void run() {
        while (threadRunning) {
			while (threadSuspended && threadRunning)
			{
				try
				{
					wait();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			if (!threadRunning) return ;
            try {
                Thread.sleep(interval);
            }
            catch (InterruptedException e) { }
            if (currFood != null)
                if (board[currFood.x][currFood.y] == WALL) makeAnotherFood();
			if (threadRunning) ourSnake.advance();
            if (threadRunning) paint(getGraphics());
            if (threadRunning) checkSnakePos();
        }
    }

	public synchronized void startGame()
	{
		ourThread = new Thread(this);
		threadSuspended = false;
		threadRunning = true;
		ourThread.start();
	}
	
    public synchronized void resumeGame() {
		if (threadRunning)
		{
			if (threadSuspended) 
			{
				threadSuspended = false;
				notify();
			}
		} else
		{
				// should we start a new game???
			startGame();
		}
    }
    
    public void pauseGame() {
		if (threadRunning)
		{
			if (!threadSuspended)
			{
				threadSuspended = true;
				notify();
			}
		}
    }

    public void keyReleased(KeyEvent e) { keyTyped(e); }
    public void keyPressed(KeyEvent e) { }
    public void keyTyped(KeyEvent e) {
        int c = e.getKeyCode();
        char k = e.getKeyChar();
        if (!threadRunning)
		{
            if (k == 's' || k == 'S') {
				startGame();
                processActionEvent(GAME_STARTED);
            }
            return ;
        }
        if (k == 'p' || k == 'P') {
            pauseGame();
            processActionEvent(GAME_PAUSED);
            return ;
        }
		if (!threadRunning) return ;
		else if (threadSuspended)
		{
			if (c == KeyEvent.VK_UP ||  c == KeyEvent.VK_DOWN || 
                c == KeyEvent.VK_NUMPAD5 ||c == KeyEvent.VK_LEFT ||
				c == KeyEvent.VK_RIGHT)
			{
				resumeGame();
				processActionEvent(GAME_RESUMED);
			} else 
			{
				e.consume();
				return;
			}
		}
        if (c == KeyEvent.VK_UP ||  c == KeyEvent.VK_DOWN || 
                                    c == KeyEvent.VK_NUMPAD5) {
            if (ourSnake.direction == Snake.LEFT ||
                            ourSnake.direction == Snake.RIGHT) {
                ourSnake.changeDir(c == KeyEvent.VK_UP ? Snake.UP :
                                                         Snake.DOWN);
                checkSnakePos();
            }
        }
        else if (c == KeyEvent.VK_LEFT || c == KeyEvent.VK_RIGHT) {
            if (ourSnake.direction == Snake.UP ||
                            ourSnake.direction == Snake.DOWN) {
                ourSnake.changeDir(c == KeyEvent.VK_LEFT ?  Snake.LEFT:
                                                            Snake.RIGHT);
                checkSnakePos();
            }
        }
        else e.consume();
    }

    private void drawFood(Graphics g,int r,int c) {
        Color s = g.getColor();
        g.setColor(foodColor);
        g.fillRect(r * GRID_SIZE,c * GRID_SIZE,GRID_SIZE,GRID_SIZE);
        g.setColor(Color.black);
        g.drawRect(r * GRID_SIZE,c * GRID_SIZE,GRID_SIZE,GRID_SIZE);
        g.setColor(s);
    }

    private void drawWall(Graphics g,int r,int c) {
        Color s = g.getColor();
        g.setColor(wallColor);
        g.fillRect(r * GRID_SIZE,c * GRID_SIZE,GRID_SIZE,GRID_SIZE);
        g.setColor(Color.black);
        g.drawRect(r * GRID_SIZE,c * GRID_SIZE,GRID_SIZE,GRID_SIZE);
        g.setColor(s);
    }

    public void update(Graphics g) {
    }

    public void paint(Graphics g) {
        Dimension d = getSize();

        if (offGraphics == null || 
            d.width != offDimension.width || d.height != offDimension.height) {
            offDimension = d;
            offImage = createImage(d.width, d.height);
            offGraphics = offImage.getGraphics();
        }

        // Fill in applet background.

        offGraphics.setColor(bgColor);
        offGraphics.fillRect(0, 0, d.width, d.height);

        int w = cols * GRID_SIZE;
        int h = rows * GRID_SIZE;
        int xOff = (d.width - w) / 2;
        int yOff = (d.height - h)/2;//(h + 2 * fontHeight)) / 2;

        //offGraphics.translate(xOff, yOff);

        for (int i = 0;i < walls.size();i++) {
            Point p = (Point)walls.elementAt(i);
            drawWall(offGraphics,p.y,p.x);
        }

        if (threadRunning || threadSuspended) 
		{
            if (currFood == null) makeAnotherFood();
            drawFood(offGraphics,currFood.y,currFood.x);
            offGraphics.setColor(snakeColor);
            ourSnake.drawSnake(offGraphics);
        }
        else if (!threadRunning) {

            FontMetrics fm = offGraphics.getFontMetrics();
            int fontHeight = fm.getHeight();

            int x = (d.width - fm.stringWidth(list[list.length - 1]))/2,
                y = (d.height - (fontHeight * list.length))/2;

            offGraphics.setFont(new Font("Serif",Font.BOLD,20));
            offGraphics.setColor(fgColor);
            for (int i = 0;i < list.length;i++) {
                offGraphics.drawString(list[i],x,(y += fontHeight + 5));
            }
        }
        g.drawImage(offImage,0,0,this);
    }

    public void addActionListener(ActionListener l) {
        actionListener = AWTEventMulticaster.add(actionListener, l);
    }

    public void removeActionListener(ActionListener l) {
        actionListener = AWTEventMulticaster.remove(actionListener,l);
    }

    public void processActionEvent(int action) {
        if (actionListener != null) {
            actionListener.actionPerformed(
                    new ActionEvent(this,action,"Snake"));
        }
    }

}
