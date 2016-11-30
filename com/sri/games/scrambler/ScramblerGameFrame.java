package com.sri.games.scrambler;

import java.awt.*;
import java.applet.*;
import java.awt.event.*;

/**
 * A wrapper panel for the scrambler game!
 */
public class ScramblerGameFrame extends Frame implements 
										 ActionListener, ItemListener,
										WindowListener
{
	Applet parent = null;
	Button scramble, solve, resetPuzzle;
	Checkbox animateCheckBox;
	Checkbox gridCheckBox;
	TextField nRowsText;
	TextField nColsText;
	ScramblerPanel sp;
	boolean showControls = true;
	boolean animate = true;
	boolean showGrid = true;
	
	public ScramblerGameFrame(Image image)
	{
		sp = new ScramblerPanel(image, 5, 5);
		initComponents(showControls);
	}
	
		/**
		 * Constructor.
		 */
	public ScramblerGameFrame(Applet applet)
	{
		Image im = null;
		this.parent = applet;
		int nRows = 5, nCols = 5;
		
		if (applet != null)
		{
			String param = applet.getParameter("nrows");
			try
			{
				nRows = Integer.parseInt(applet.getParameter("nrows"));
			} catch (Exception e)
			{
				nRows = 5;
			}
			try
			{
				nCols = Integer.parseInt(applet.getParameter("ncols"));
			} catch (Exception e)
			{
				nCols = 5;
			}
			im = applet.getImage(applet.getDocumentBase(), applet.getParameter("puzzleimage"));
			try
			{
				showControls = (new Boolean(applet.getParameter("showcontrols"))).booleanValue();
			} catch (Exception e)
			{
				showControls = true;
			}
			try
			{
				animate = (new Boolean(applet.getParameter("animate"))).booleanValue();
			} catch (Exception e)
			{
				nCols = 5;
			}
			try
			{
				showGrid = (new Boolean(applet.getParameter("showgrid"))).booleanValue();
			} catch (Exception e)
			{
				showGrid = true;
			}
		}
		sp = new ScramblerPanel(im, nRows, nCols);
		initComponents(showControls);
	}
	
	protected void initComponents(boolean showControls)
	{
		sp.setAnimated(animate);
		sp.showGrid(showGrid);
		setTitle("Scrambler V1.0 by Sriram Panyam");
		setLayout(new BorderLayout());
		scramble = new Button("Scramble");
		solve = new Button("Solve");
		animateCheckBox = new Checkbox("Animate", true);
		gridCheckBox = new Checkbox("Grid", true);
		nRowsText = new TextField("5");
		nColsText = new TextField("5");
		resetPuzzle = new Button("Reset");
		Panel eastPanel = new Panel(new BorderLayout());
		
		Panel controls = new Panel(new GridLayout(9, 2, 2, 2));
		controls.add(solve);
		controls.add(scramble);
		controls.add(new Label("Rows"));
		controls.add(nRowsText);
		controls.add(new Label("Columns"));
		controls.add(nColsText);
		controls.add(new Label(""));
		controls.add(resetPuzzle);
		controls.add(new Label("-----"));
		controls.add(new Label("-----"));
		controls.add(animateCheckBox);
		controls.add(gridCheckBox);
		eastPanel.add("North", controls);
		
		animateCheckBox.addItemListener(this);
		gridCheckBox.addItemListener(this);
		solve.addActionListener(this);
		scramble.addActionListener(this);
		resetPuzzle.addActionListener(this);
		add("Center", sp);
		
		if (showControls)
		{
			add("East", eastPanel);
		}
		nRowsText.setText("" + sp.nRows);
		nColsText.setText("" + sp.nCols);
	}
	
		/**
		 * Action event handler
		 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src == scramble)
		{
			sp.scramble(100);
		} else if (src == solve)
		{
			sp.solve(0);
		} else if (src == resetPuzzle)
		{
			int nr, nc;
			try
			{
				nr = Integer.parseInt(nRowsText.getText().trim());
				nc = Integer.parseInt(nColsText.getText().trim());
			} catch (Exception ex)
			{
				nr = 5;
				nc = 5;
			}
			sp.setPuzzleSize(nr, nc);
			try
			{
				sp.makePuzzle();
			} catch (Exception ex)
			{
				if (parent != null) parent.showStatus("Block sizes are too small");
			}
		}
	}
	
		/**
		 * Item Event handler.
		 */
	public void itemStateChanged(ItemEvent e)
	{
		Object src = e.getSource();
		if (src == animateCheckBox)
		{
			sp.setAnimated(animateCheckBox.getState());
		} else if (src == gridCheckBox)
		{
			sp.showGrid(gridCheckBox.getState());
		}
	}
	
	public void windowClosing(WindowEvent e)
	{ 
		setVisible(false);
		if (parent == null) 
		{
			dispose();
			System.exit(0);
		}
	}
	public void windowClosed(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }

	public static void main(String args[])
	{
		ScramblerGameFrame sp = new ScramblerGameFrame(Toolkit.getDefaultToolkit().getImage("images/lena.jpg"));
		sp.setBounds(50, 50, 500, 400);
		sp.setVisible(true);
		sp.toFront();
	}
}
