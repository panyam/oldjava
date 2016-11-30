package com.sri.gui.ext;

import java.awt.*;
import java.awt.event.*;

public class ColorPallette extends Component implements MouseListener
{
	int vGap = 5, hGap = 5;
	int nRows = 4, nCols = 4;
	int curr = 0;
	Color cols[][];
	protected Dimension prefSize = new Dimension();

	public ColorPallette(int nRows,int nCols, int vGap, int hGap)
	{
		this.nRows = nRows;
		this.nCols = nCols;
		this.vGap = vGap;
		this.hGap = hGap;
		
		setBackground(Color.lightGray);
		
		cols = new Color[nRows][nCols];
		for (int i = 0;i < nRows;i++)
		{
			for (int j = 0;j < nCols;j++)
			{
				cols[i][j] = Color.white;
			}
		}
		prefSize.width = 4 + (20 * nCols) + ((nCols - 1) * hGap);
		prefSize.height = 4 + (15 * nRows) + ((nRows - 1) * vGap);
		
		addMouseListener(this);
	}
	
		/**
		 * Returns the number of rows in the pallette.
		 */
	public int getRows() 
	{
		return nRows;
	}
	
		/**
		 * Returns the number of columns in the pallette.
		 */
	public int getColumns() 
	{
		return nCols;
	}
	
	public int getCurrent()
	{
		return curr;
	}
	
	public Color getColor(int i,int j)
	{
		return cols[i][j];
	}
	
	public void setColorAt(Color col,int i,int j)
	{
		cols[i][j] = col;
		paint(getGraphics());
	}
	
	public Dimension getPreferredSize()
	{
		return prefSize;
	}
	
	public void paint(Graphics g)
	{
		if (g == null) return ;
		Dimension d = getSize();
		int w = (d.width - (hGap * (nCols - 1)) - 4) / nCols;
		int h = (d.height - (vGap * (nRows - 1)) - 4) / nRows;
		
		if (w == 0 || h == 0) return ;
		
		int x = 2;
		int y = 2;

		g.clearRect(0,0,d.width,d.height);
		
		for (int i = 0;i < nRows;i++)
		{
			for (int j = 0;j < nCols;j++)
			{
				if (cols[i][j] != null) g.setColor(cols[i][j]);
				g.fillRect(x,y,w,h);
				g.setColor(Color.lightGray);
				g.draw3DRect(x,y,w,h,false);
				x += w + hGap;
			}
			x = 2;
			y += h + vGap;
		}
		
		int cr = this.curr / nCols;
		int cc = this.curr % nCols;
		g.setColor(Color.black);
		g.drawRect(2 + cc * (w + hGap),2 + cr * (h + vGap),w,h);
		g.setColor(Color.lightGray);
		g.draw3DRect(1,1,d.width - 2,d.height - 2,false);
	}
	
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == this)
		{
			Dimension d = getSize();
			if (d.width == 0 || d.height == 0) return ;
			int x = e.getX(), y = e.getY();
			int w = (d.width - 4)/ nCols;
			int h = (d.height - 4)/ nRows;
			int r = y / h, c = x / w;
			if ( r < 0 || r >= nRows || c < 0 || c >= nCols) return ;
			curr = r * nCols + c;
			paint(getGraphics());
		}
	}
	
	public void mouseReleased(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
}