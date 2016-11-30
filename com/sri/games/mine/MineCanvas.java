
package com.sri.games.mine;

import com.sri.games.*;
import java.awt.*;
import java.awt.event.*;
import java.applet.*;

public class MineCanvas extends Canvas implements
                                    MouseListener, MouseMotionListener
{
    public final static int GAME_RESTARTED = 0;
    public final static int GAME_WON = 1;
    public final static int MINE_REVEALED = 2;
    public final static int SQUARE_FLAGGED = 3;
    public final static int START_TIMER = 4;
    public final static int GAME_LOST = 5;

		/**
		 * The action listener.
		 */
    protected transient ActionListener actionListener = null;

    private int numleft;
    private boolean inited = false;
    private Image buff;
    private Dimension buffSize = new Dimension(0, 0);
    private Graphics buffG;
    private int cr = 0,cc = 0,pr = 0,pc = 0;
    private static int ht = 16,wt = 16;
    private boolean started = false;
    private boolean hidden[][],flagged[][],plained[][],camefrom[][];
    int rows,cols,nummines;
    private int field[][];
    private boolean blown = false;
    private boolean gameover = false;
    private Color[] list = new Color[8];
    private int minerowlist[],minecollist[];
    private boolean right_clicked = false;
    Dimension prefSize = new Dimension(10, 10);

        /**
         * Constructor.
         */
    public MineCanvas()
    {
        setBackground(Color.lightGray);
        setForeground(Color.black);
        list[0] = Color.black;
        list[1] = Color.green.darker();
        list[2] = Color.blue;
        list[3] = Color.pink;
        list[4] = Color.red;
        list[5] = Color.orange.darker();
        list[6] = Color.cyan;
        list[7] = Color.magenta;
		addMouseListener(this);
		addMouseMotionListener(this);
    }

    public void restart(int rows,int cols,int nummines) {
        this.rows = rows;
        this.cols = cols;
        this.nummines = nummines;
        prefSize.width = cols * wt;
        prefSize.height = rows * ht;
        minerowlist = new int[nummines];
        minecollist = new int[nummines];
        restart();
    }

    public Dimension getPreferredSize()
    {
        prefSize.width = 4 + cols * ht;
        prefSize.height = 4 + rows * ht;
        return prefSize;
    }

    public void restart() {
        makeField();
        gameover = started = blown = false;
        hidden = new boolean[rows][cols];
        plained = new boolean[rows][cols];
        flagged = new boolean[rows][cols];
        camefrom = new boolean[rows][cols];
        for (int i = 0;i < rows;i++) 
        {
            for (int j = 0;j < cols; j++)
            {
                hidden[i][j] = true; 
                camefrom[i][j] = plained[i][j] = flagged[i][j] = false;
            }
        }
        inited = false;
        processActionEvent(new ActionEvent(this, GAME_RESTARTED, "MineSweeper"));
    }

    private void Sort(int a[],int l,int r) { 
        int i,j,t;
        for (i = a.length - 1;i >= 0;i--) {
            for (j = 1;j <= i;j++) {
                if (a[j-1] > a[j]) {
                    t = a[j-1]; a[j-1] = a[j]; a[j] = t;
                }
            }
        }
    }

    private void printField() {
        for (int i = 0;i < rows;i++) {
            for (int j = 0;j < cols;j++)
                System.out.print(field[i][j] + " ");
            System.out.println("");
        }
    }

    public void makeField() {
        int vector[] = new int[rows*cols];
        int temp = 0;
        int limit,m,n;
        field = new int[rows][cols];
        for (m = 0;m < rows;m++)
            for (n = 0; n < cols; n++)
                vector[(n*rows) + m] = field[m][n] = (int)(Math.random()*10000);
        Sort(vector,0,vector.length - 1);
        limit = vector[nummines - 1];
	    for (m = 0;m < rows;m++)
		    for (n=0;n < cols;n++) {
			    field[m][n] = (field[m][n] <= limit ? (-1) : (0));
                if (field[m][n] == -1) {
                    minerowlist[temp] = m; minecollist[temp++] = n;
                }
            }

        for (m = 0; m < rows;m++)
            for (n = 0; n < cols; n++)
                if (field[m][n] == 0) { 
                    int v = countmines(m,n); field[m][n] = v;
                }
        //printField();
    }

    private int rc[][]={{-1,-1},{0,-1},{1,-1},{-1,0},{1,0},{-1,1},{0,1},{1,1}};

    private void showNeighbours(int r,int c,boolean plain) {
        for (int i = 0;i < 8;i++) {
            int x = r + rc[i][0],y = c + rc[i][1];
            if (countFlags(cr,cc) >= field[cr][cc]){
                if (x >= 0 && x < rows && y >= 0 && y < cols) {
                    if (!flagged[x][y]) {
                        hidden[x][y] = plained[x][y] = plain;
                        if (!plain) {
                            camefrom[r][c] = true;
                            if (field[x][y] == 0 && camefrom[x][y] == false) 
                                    showNeighbours(x,y,plain);
                        }
                        drawBlock(buffG,x,y,status(x,y));
                    }
                }
            }
        }
    }

    private int countFlags(int r,int c) {
        int num = 0;
        for (int i = 0;i < 8;i++) {
            int x = r + rc[i][0],y = c + rc[i][1];
            if (x >= 0 && x < rows && y >= 0 && y < cols)
                if (flagged[x][y]) num++;
        }
        return num;
    }

    private int countmines(int r,int c) {
        int num = 0;
        for (int i = 0;i < 8;i++) {
            int x = r + rc[i][0],y = c + rc[i][1];
            if (x >= 0 && x < rows && y >= 0 && y < cols)
                if (field[x][y] == -1) num++;
        }
        return num;
    }

    private boolean xy2index(int x,int y) { 
        pr = cr;pc = cc;cc = x/wt; cr = y/ht; 
        return (inRange(cr,cc) && inRange(pr,pc));
    }

    private boolean inRange(int r,int c) { 
        return ((r >= 0) && (c >= 0) && (r < rows) && (c < cols));
    }

    public void drawBlock(Graphics g,int r,int c,int mode) {
        Color curr = g.getColor();
        g.setColor(Color.black);
        int inc = 2;
        int xpos = inc + (c*wt),ypos = inc + (r * ht);
        switch (mode) {
            case 1 : 
			{		// drawn on a square with an unclicked
					// bomb
				drawBomb(g,xpos,ypos);break;
			}
            case 2 : 
			{		// draws a cell with a number on it
            g.setColor(Color.lightGray);
            g.fillRect(xpos,ypos,wt,ht);
            if (field[r][c] > 0) {
				g.setFont(new Font("Serif",Font.BOLD,12));
                g.setColor(list[field[r][c] - 1]);
                g.drawString(field[r][c] + "",xpos + 5,ypos + 12);
                }
            }break;
            case 3 : {	// drawn on the square where the bomb was clicked
                        g.setColor(Color.red);
                        g.fillRect(xpos,ypos,wt,ht);
                        g.setColor(Color.black);
                        drawBomb(g,xpos,ypos);
                     }break;
            case 4 : {	// draws a plain unrevealed cell
                        g.setColor(Color.lightGray);
                        g.fill3DRect(xpos,ypos,wt,ht,true); 
                     }break;
            case 5 : {      // a Flag.
                        g.setColor(Color.lightGray);
                        g.fill3DRect(xpos,ypos,wt,ht,true);
                        g.setColor(Color.red);
                        g.fillRect(xpos + 4,ypos + 3,4,4);
                        g.setColor(Color.red.darker());
                        g.drawLine(xpos + 8,ypos + 3,xpos + 8,ypos + 6);
                        g.drawLine(xpos + 8,ypos + 6,xpos + 5,ypos + 6);
                        g.drawLine(xpos + 6,ypos + 5,xpos + 7,ypos + 5);
                        g.drawLine(xpos + 7,ypos + 5,xpos + 7,ypos + 4);
                        g.setColor(new Color(128,128,128));
                        g.drawLine(xpos+5,ypos + 12,xpos+11,ypos + 12);
                        g.drawLine(xpos+7,ypos+11, xpos+9,ypos+11);
                        g.setColor(Color.black);
                        g.drawLine(xpos+8,ypos+7,xpos+8,ypos+11);
                        g.drawLine(xpos+6,ypos+12, xpos+10,ypos+12);
                    }break;
            case 6 : {      // A red cross - flag over non mine block
                        g.setColor(Color.red);
                        g.drawLine(xpos+3,ypos+3, xpos+11,ypos+11);
                        g.drawLine(xpos+4,ypos+3, xpos+12,ypos+11);
                        g.drawLine(xpos+12,ypos+3, xpos+4,ypos+11);
                        g.drawLine(xpos+3,ypos+11, xpos+11,ypos+3);
                        g.setColor(Color.red.darker());
                        g.drawLine(xpos+3,ypos+12, xpos+4,ypos+12);
                        g.drawLine(xpos+4,ypos+12, xpos+7,ypos+9);
                        g.drawLine(xpos+8,ypos+9, xpos+11,ypos+12);
                        g.drawLine(xpos+11,ypos+12, xpos+12,ypos+12);
                        g.drawLine(xpos+3,ypos+4, xpos+6,ypos+7);
                        g.drawLine(xpos+12,ypos+4, xpos+9,ypos+7);
                     }break;
            case 7 : 
			{
                g.setColor(Color.lightGray);
                g.fillRect(xpos,ypos,wt,ht);
            }break;
        }
        g.setColor(curr);
    }
                     
    public void drawBomb(Graphics g,int x,int y) {
        Color c = g.getColor();
        g.setColor(new Color(128,128,128));
        g.drawLine(x+2,y+7,x+12,y+7);
        g.drawLine(x+7,y+2,x+7,y+12);
        g.fillRect(x+6,y+4,3,7);
        g.fillRect(x+4,y+6,7,3);
        g.setColor(Color.black);
        g.fillRect(x+5,y+5,5,5);
        g.drawLine(x+3,y+7,x+11,y+7);
        g.drawLine(x+7,y+3,x+7,y+11);
        g.drawLine(x+4,y+4,x+4,y+4);
        g.drawLine(x+4,y+10,x+4,y+10);
        g.drawLine(x+10,y+4,x+10,y+4);
        g.drawLine(x+10,y+10,x+10,y+10);
        g.setColor(Color.white);
        g.drawLine(x+6,y+6,x+6,y+6);
        g.setColor(c);
    }

    private boolean is_game_over() {
        for (int i = 0;i < rows;i++)
        {
            for (int j = 0;j < cols;j++) 
            {
                if (field[i][j] != -1 && hidden[i][j]) return false;
            }
        }
        return true;
    }

        /**
         * Paint it.
         */
    public void paint(Graphics g)
    {
        Dimension d = getSize();
        if (!inited || buff == null ||
            d.width != buffSize.width || d.height != buffSize.height)
        {
            buffSize.width = d.width;
            buffSize.height = d.height;
            buff = createImage(d.width + 4,d.height + 4);
            if (buff == null) return ;
            buffG = buff.getGraphics();
            inited = true;
        }

        buffG.setColor(Color.lightGray);
        buffG.fillRect(0,0,prefSize.width,prefSize.height);
        PressedRect(buffG,0,0,prefSize.width - 2,prefSize.height - 2);

        buffG.setColor(Color.lightGray);
        buffG.fillRect(0,0,prefSize.width + 2,prefSize.height + 2);
        if (!gameover) if ((gameover = is_game_over())) end_game();
        for (int i = 0;i < rows;i++)
        {
            for (int j = 0;j < cols;j++)
            {
                drawBlock(buffG,i,j,status(i,j));
            }
        }
        //PressedRect(buffG,0,0,prefSize.width + 2,prefSize.height + 2);
        if (buff != null) g.drawImage(buff,0,0,null);
    }

    public int status(int i,int j) {
        if (plained[i][j]) return 7;
        if (blown) {
            if (hidden[i][j]) {
                if (flagged[i][j]) return (field[i][j] == -1 ? 5 : 6);
                else return (field[i][j] == -1 ? 1 : 4);
            }
            else {
                if (field[i][j] == -1) return (i == cr && j == cc ? 3 : 1);
                else return 2;
            }
        }
        else {
            if (hidden[i][j]) return (flagged[i][j] ? 5 : 4);
            else return 2;
        }
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

    public void update(Graphics g) {
    }

    public void mousePressed(MouseEvent e)
    {
        if (gameover) return ;
        if (!blown) {
            right_clicked = (e.getModifiers() == Event.META_MASK);
            if (!xy2index(e.getX(),e.getY())) return ;
            drawBlock(buffG,pr,pc,status(pr,pc));
            /*if (e.modifiers == Event.ALT_MASK) {
                showNeighbours(cr,cc,true);
                return true;
            }*/
            int s = (!hidden[cr][cc] || flagged[cr][cc] ? status(cr,cc) : 7);
            if (hidden[cr][cc] && e.getModifiers() == Event.META_MASK) s = 5;

            drawBlock(buffG,cr,cc,s);
            //paint(getGraphics());
			if (getGraphics() != null) getGraphics().drawImage(buff,0,0,null);
            if ((gameover = is_game_over())) 
            {
                end_game();
            }
        }
    }

	public void mouseClicked(MouseEvent e){ }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseMoved(MouseEvent e){ }
	public void mouseDragged(MouseEvent e)
	{
		mousePressed(e);
	}
	
    public void mouseReleased(MouseEvent e) {
        if (gameover) return ;
        if (!blown)
        {
            if (started == false)
            {
                started = true;
                processActionEvent(new ActionEvent(this,
                                                   START_TIMER,
                                                   "MineSweeper"));
            }
            if (!xy2index(e.getX(),e.getY())) return ;
            if (right_clicked)
            {
                if (hidden[cr][cc])
                {
                    flagged[cr][cc] = !flagged[cr][cc];
                    processActionEvent(new ActionEvent(this,
                                SQUARE_FLAGGED * (!flagged[cr][cc] ? 1 : -1),
                                "MineSweeper"));
                }
            }
            else {
                if (hidden[cr][cc] && !flagged[cr][cc]) 
                    hidden[cr][cc] = false;
                if (!flagged[cr][cc] && field[cr][cc] == -1) {
                    blown = true;
                    started = false;
                    processActionEvent(new ActionEvent(this,
                                                       GAME_LOST,
                                                       "MineSweeper"));
                    paint(getGraphics()); 
                    return ;
                }
                if (field[cr][cc] == 0) showNeighbours(cr,cc,false);
            }
            drawBlock(buffG,cr,cc,status(cr,cc));
            paint(getGraphics());
            if ((gameover = is_game_over())) end_game();
        }
    }

        /**
         * Finish the games.
         */
    private void end_game() {
        processActionEvent(new ActionEvent(this,
                                           GAME_WON, 
                                           "MineSweeper"));
        for (int i = 0;i < nummines;i++) 
            flagged[minerowlist[i]][minecollist[i]] = true;
        paint(getGraphics());
    }

		/**
		 * Adds an action listener.
		 */
    public synchronized void addActionListener(ActionListener l)
    {
        if (l != null) {
	        actionListener = AWTEventMulticaster.add(actionListener, l);
        }
    }

		/**
		 * Removes action listener
		 */
    public synchronized void removeActionListener(ActionListener l)
    {
        if (l != null) {
	        actionListener = AWTEventMulticaster.remove(actionListener, l);
        }
    }
	
		/**
		 * Mouse action event handler.
		 */
	protected void processActionEvent (ActionEvent e)
    {
		if (isEnabled() && actionListener != null && e != null) {
			actionListener.actionPerformed(e);
		}
	}
}
