package com.sri.apps.mml;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import com.sri.gui.core.*;
import com.sri.gui.core.containers.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.tabs.*;
import java.util.*;

/**
 * The MML editor toolbar.
 */
public class MMLToolbar extends Panel implements 
								MouseListener, 
								ActionListener, 
								WindowListener
{
		/**
		 * The button panel.
		 */
	protected ButtonPanel buttonPanel = new ButtonPanel();

		/**
		 * The status bar.
		 */
	protected SLabel mouseOverStatusBar = new SLabel("", SLabel.CENTER, SLabel.LEFT);
	
		/**
		 * When clicked on this button itl show the mml code.
		 */
	SActionButton showMML = new SActionButton("Generate MML");
	
		/**
		 * Buttons that are used ONLY for content markup
		 * elements.
		 */
	protected ImagedPanel integralPanel = null;
	protected ImagedPanel sumSetPanel = null;
	protected Panel fencePanel = new Panel(new BorderLayout())
		{
			public void paint(Graphics g)
			{
				Dimension d = getSize();
				//g.setColor(Color.red);
				//g.drawRect(0, 0, d.width - 1, d.height - 1);
				//g.drawRect(1, 1, d.width - 2, d.height - 2);
				//g.drawRect(2, 2, d.width - 4, d.height - 4);
			}
		};
	protected ImagedPanel leftOpenPanel;
	protected ImagedPanel rightOpenPanel;
	protected SActionButton createFence = new SActionButton("Create Template");
	protected TextField numFenceComps = new TextField("2", 10);
	protected TextField separatorText = new TextField("", 10);

	protected SActionButton controllers[] = {
		new SActionButton("Characters"),
		new SActionButton("Integral Templates"),
		new SActionButton("Sums / Set Templates"),
		new SActionButton("Fence Templates"),
	};
	
		/**
		 * This text area contains the MathML code for the 
		 * current document.
		 */
	TextArea mmlArea = new TextArea(7, 70);
	Dialog mmlCodeFrame = null;

		/**
		 * The editor into which the symbols are placed.
		 */
	protected MMLEditor editor = null;

		/**
		 * Constructor.
		 */
    public MMLToolbar (Hashtable imageTable) {
		ImageMap iMap = new ImageMap(
							(Image)imageTable.get(MMLApplet.ALL_SYMBOLS));

		createPanels(iMap);
		
		for (int i = 0;i < controllers.length;i++)
		{
			controllers[i].addMouseListener(this);
		}
		buttonPanel.addItem(controllers[0], new Label("Characters"));
		buttonPanel.addItem(controllers[1], integralPanel);
		buttonPanel.addItem(controllers[2], sumSetPanel);
		buttonPanel.addItem(controllers[3], fencePanel);
		
		integralPanel.addActionListener(this);
		sumSetPanel.addActionListener(this);
		showMML.addActionListener(this);
		
		buttonPanel.selectItem(3);
		setLayout(new BorderLayout());
		add("Center", buttonPanel);
		
		add("South", mouseOverStatusBar);
		add("North", showMML);
        validate();
        invalidate();
    }

	public void windowClosing(WindowEvent e)
	{
		if (e.getSource() == this.mmlCodeFrame)
		{
			this.mmlCodeFrame.setVisible(false);
		}
	}
	public void windowActivated(WindowEvent e) { }
	public void windowDeactivated(WindowEvent e) { }
	public void windowClosed(WindowEvent e) { }
	public void windowOpened(WindowEvent e) { }
	public void windowIconified(WindowEvent e) { }
	public void windowDeiconified(WindowEvent e) { }
	
		/**
		 * Create the TP Panel.
		 */
	protected void createPanels(ImageMap iMap)
	{
		integralPanel = new ImagedPanel(iMap.getImage(0, 0, 1 + 25 * 7, 1 + 25 * 6), 
									  6, 7, 25, 25, false);
		String intNames[] =
		{
			"Single Integral",
			"Double Integral",
			"Triple Integral",
			"Closed Integral",
			"Area Integral",
			"Volume Integral"
		};
		for (int i = 0;i < intNames.length;i++)
		{
			for (int j = 0;j < 7;j++)
			{
				integralPanel.setActionCommand(i, j, intNames[i]);
			}
		}
		
		sumSetPanel = new ImagedPanel(iMap.getImage(0, 25 * 6, 1 + 25 * 7, 1 + 25 * 6), 
									   6, 7, 25, 25, false);
		String ssNames[] =
		{
			"Summation Template", "Product Template",
			"Co Product  Template", "Union Template",
			"Intersection Template",
		};

		for (int i = 0;i < ssNames.length;i++)
		{
			for (int j = 0;j < 7;j++)
			{
				sumSetPanel.setActionCommand(i, j, ssNames[i]);
			}
		}
			// make the fence panel..
		makeFencePanel(iMap);
	}
	
	protected void makeFencePanel(ImageMap iMap)	
	{
		Panel fenceCenterPanel = new Panel(new GridLayout(2, 1, 5, 5));
		Panel fenceSouthPanel = new Panel(new BorderLayout());
		Panel fenceSouthNorthPanel = new Panel(new GridLayout(2, 2, 5, 5));
		Panel fencesCenterLeftPanel = new Panel(new BorderLayout());
		Panel fencesCenterRightPanel = new Panel(new BorderLayout());
		
		Image fencesImage = iMap.getImage(7 * 25, 0, 1 + 25 * 7, 1 + 25 * 2);

		leftOpenPanel = new ImagedPanel(fencesImage, 2, 7, 25, 25, true);
		rightOpenPanel = new ImagedPanel(fencesImage, 2, 7, 25, 25, true);
	
		fenceSouthNorthPanel.add(new Label("# Components: "));
		fenceSouthNorthPanel.add(numFenceComps);
		fenceSouthNorthPanel.add(new Label("Separators: "));
		fenceSouthNorthPanel.add(separatorText);

		createFence.addActionListener(this);
		fenceSouthPanel.add("North",fenceSouthNorthPanel);
		fenceSouthPanel.add("South", createFence);

			/**
			 * Add the "Left" Fence seletor panel.
			 */
		fencesCenterLeftPanel.add("North", new Label("Open"));
		fencesCenterLeftPanel.add("Center", leftOpenPanel);
			/**
			 * Add the "Right" Fence seletor panel.
			 */
		fencesCenterRightPanel.add("North", new Label("Close"));
		fencesCenterRightPanel.add("Center", rightOpenPanel);
		
			/**
			 * Add the panels to the center.
			 */
		fenceCenterPanel.add(fencesCenterLeftPanel);
		fenceCenterPanel.add(fencesCenterRightPanel);

		fencePanel.add("Center", fenceCenterPanel);
		fencePanel.add("South", fenceSouthPanel);
		
		String names[][] = 
		{
			{
				"Opening Round Bracket", "Opening Square Bracket", 
				"Opening Curly Bracket", "Opening Angle Bracket", 
				"Single Bar", "Opening Floor Bracket", 
				"Opening Ceiling Bracket"
			},
			{
				"Closing Round Bracket", "Closing Square Bracket", 
				"Closing Curly Bracket", "Closing Angle Bracket", 
				"Double Bar", "Closing Floor Bracket", 
				"Closing Ceiling Bracket"
			},
		};
		for (int i = 0;i < 2;i++)
		{
			for (int j = 0;j < 7;j++)
			{
				leftOpenPanel.setActionCommand(i, j, names[i][j]);
				rightOpenPanel.setActionCommand(i, j, names[i][j]);
			}
		}
	}
	
		/**
		 * Sets the current editor intowhich the symbols
		 * are placed.
		 */
	public void setCurrentEditor(MMLEditor editor)
	{
		this.editor = editor;
	}

		/**
		 * Action event handler.
		 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		MMLSymbol created = null;
		Constructor constructor = null;
		Class tocreate = null, argsclass[] = null;
		Object args[] = null;
		if (src == showMML)
		{
                // a one-off step.
            if (mmlCodeFrame == null)
            {
                Frame parentFrame = MMLApplet.getParentFrame(this);
                if (parentFrame == null) parentFrame = new Frame();
	            mmlCodeFrame = new Dialog(parentFrame, "MML Code", false);
		        mmlCodeFrame.setLayout(new BorderLayout());
		        mmlCodeFrame.addWindowListener(this);
		        mmlCodeFrame.add("Center", mmlArea);
		        mmlArea.setEditable(false);
		        mmlCodeFrame.setBounds(200, 200, 300, 200);
		        mmlCodeFrame.pack();
            }

			mmlCodeFrame.setVisible(true);
			mmlCodeFrame.toFront();
			return ;
		} else if (src == integralPanel)
		{
			byte tpTypes[] = 
			{
				MMLTPTemplate.S_INTEGRAL,
				MMLTPTemplate.D_INTEGRAL,
				MMLTPTemplate.T_INTEGRAL,
				MMLTPTemplate.C_INTEGRAL,
				MMLTPTemplate.A_INTEGRAL,
				MMLTPTemplate.V_INTEGRAL
			};
			
			tocreate = MMLTPTemplate.class;
			argsclass = new Class [] 
				{ byte.class, boolean.class, boolean.class, boolean.class };
			args = new Object[4];
				// first argument for the type of the symbol
			args[0] = new Byte(tpTypes[integralPanel.currRow]);
			
				// second argument for whether to show the top one...
			args[1] = new Boolean(integralPanel.currCol == 2|| 
								  integralPanel.currCol == 3|| 
								  integralPanel.currCol == 5|| 
								  integralPanel.currCol == 6);
			
				// second argument for whether to show the bottom one...
			args[2] = new Boolean(integralPanel.currCol == 1 || 
								  integralPanel.currCol == 3|| 
								  integralPanel.currCol == 4|| 
								  integralPanel.currCol == 6);
			
				// second argument for whether to show as top or side
			args[3] = new Boolean(integralPanel.currCol < 4);
		} else if (src == sumSetPanel)
		{
			if (sumSetPanel.currRow < 5)
			{
				byte tpTypes[] = 
				{
					MMLTPTemplate.SUMMATION,
					MMLTPTemplate.PRODUCT,
					MMLTPTemplate.COPRODUCT,
					MMLTPTemplate.UNION,
					MMLTPTemplate.INTERSECTION,
				};
				tocreate = MMLTPTemplate.class;
				argsclass = new Class [] 
					{ byte.class, boolean.class, boolean.class, boolean.class };
				args = new Object[4];
					// first argument for the type of the symbol
				args[0] = new Byte(tpTypes[sumSetPanel.currRow]);
				
					// second argument for whether to show the top one...
				args[1] = new Boolean(sumSetPanel.currCol == 2|| 
									  sumSetPanel.currCol == 3|| 
									  sumSetPanel.currCol == 5|| 
									  sumSetPanel.currCol == 6);
			
					// second argument for whether to show the bottom one...
				args[2] = new Boolean(sumSetPanel.currCol == 1 || 
									  sumSetPanel.currCol == 3 || 
									  sumSetPanel.currCol == 4 || 
									  sumSetPanel.currCol == 6);
			
					// second argument for whether to show as top or side
				args[3] = new Boolean(sumSetPanel.currCol < 4);
			} else
			{
				if (sumSetPanel.currCol < 2)
				{
					tocreate = MMLFractionTemplate.class;
					argsclass = null;
					args = null;
				} else if (sumSetPanel.currCol < 4)
				{
					tocreate = MMLRootTemplate.class;
					argsclass = new Class[] { boolean.class };
					args = new Object[] { new Boolean(sumSetPanel.currCol != 3) };
				} else
				{
					tocreate = MMLLDivTemplate.class;
					argsclass = new Class[] { boolean.class };
					args = new Object[] { new Boolean(sumSetPanel.currCol != 5) };
				}
			}
		} else if (src == createFence)
		{
				// then create a new button fence template...
				// based on the choices...
			tocreate = MMLFenceTemplate.class;
			argsclass = new Class[]
						{
							int.class, byte.class, byte.class, String.class
						};
			byte fenceTypes[][] = 
			{
				{
					MMLFenceTemplate.O_ROUND_BRACKET, 
					MMLFenceTemplate.O_SQUARE_BRACKET, 
					MMLFenceTemplate.O_CURLY_BRACKET, 
	    			MMLFenceTemplate.O_ANGLE_BRACKET, 
					MMLFenceTemplate.S_BAR_BRACKET, 
					MMLFenceTemplate.O_CEIL_BRACKET, 
    				MMLFenceTemplate.O_FLOOR_BRACKET, 
				},
				{
	    			MMLFenceTemplate.C_ROUND_BRACKET, 
					MMLFenceTemplate.C_SQUARE_BRACKET, 
					MMLFenceTemplate.C_CURLY_BRACKET, 
					MMLFenceTemplate.C_ANGLE_BRACKET, 
					MMLFenceTemplate.D_BAR_BRACKET, 
					MMLFenceTemplate.C_CEIL_BRACKET, 
	    			MMLFenceTemplate.C_FLOOR_BRACKET, 
				}
			};
			args = new Object[4];
			args[0] = new Integer(numFenceComps.getText().trim());
			args[1] = new Byte(fenceTypes[leftOpenPanel.currRow]
											[leftOpenPanel.currCol]);
			args[2] = new Byte(fenceTypes[
							rightOpenPanel.currRow][rightOpenPanel.currCol]);
			args[3] = separatorText.getText();
		}
		
		try
		{
			constructor = tocreate.getConstructor(argsclass);
			created = (MMLSymbol) constructor.newInstance(args);
			
			editor.insertSymbol(created);
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	public void mouseMoved(MouseEvent e) { }		
	public void mouseDragged(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
		
	public void mouseEntered(MouseEvent e)
	{
		if (e.getSource() instanceof SButton)
		{
			mouseOverStatusBar.setText(((SButton)e.getSource()).getText());
		}
	}
	
	public void mouseExited(MouseEvent e)
	{ 
		if (e.getSource() instanceof SButton)
		{
			mouseOverStatusBar.setText("");
		}
	}
		
		
		/**
		 * A Panel where images are used as icons and instead
		 * of having a button for each icon, just have one big
		 * image and draw rectangels around the icons.
		 */
	public class ImagedPanel extends Panel 
							 implements MouseListener, MouseMotionListener
	{
			/**
			 * Width and height of each icon.
			 */
		int iconW, iconH;
		Image iconImage = null;
		int nRows, nCols;
		Dimension prefSize = new Dimension();
			/**
			 * The current selected row and column.
			 * This cell is ALWAYS drawn as pressed.
			 */
		int currRow = -1, currCol = -1;
		
			/**
			 * This is the cell over which the mouse is.
			 * If these values are BOTH negative then
			 * it means the mouse is being pressed
			 * if they are BOTH positive then it means
			 * the mouse is simply over the cell.
			 * If only one of them is positive then it means
			 * the cell is over no mouse...
			 */
		int mouseRow = -1, mouseCol = 1;
		
		String actionCommands[][];
		
		boolean checkStyle = false;
		
			/**
			 * The action listener.
			 */
		protected transient ActionListener actionListener = null;

			/**
			 * Constructor
			 */
		public ImagedPanel(Image im, int nRows, int nCols, 
						   int w, int h, boolean check)
		{
			this.checkStyle = check;
			iconW = w;
			iconH = h;
			
			prefSize.width = (w * nCols) + 2;
			prefSize.height = (h * nRows) + 2;
			if (check)
			{
				currRow = currCol = 0;
			}
			this.nRows = nRows;
			this.nCols = nCols;
			iconImage = im;
			actionCommands = new String[nRows][nCols];
			for (int i = 0;i < nRows;i++)
			{
				for (int j = 0;j < nCols;j++)
				{
					actionCommands[i][j] = "";
				}
			}
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
			/**
			 * Get the preferred size of this dimension.
			 */
		public Dimension getPreferredSize()
		{
			return prefSize;
		}
		
			/**
			 * Set the action command of the given button.
			 */
		public void setActionCommand(int r, int c, String comm)
		{
			actionCommands[r][c] = comm;
		}

		public void paint(Graphics g)
		{
			Dimension d = getSize();
			int cx = 0;//(d.width - (iconW * nCols)) >> 1;
			int cy = 0;//(d.height - (iconH * nRows)) >> 1;
			g.drawImage(iconImage, cx, cy, null);
			
			if (checkStyle)
			{
				if (currRow >= 0 && currRow < nRows && 
					currCol >= 0 && currCol < nCols)
				{
					g.setColor(this.getBackground());
					g.draw3DRect(cx + currCol * iconW, cy + currRow * iconH, iconW, iconH, false);
				}
				int tr = (mouseRow > 0 ? mouseRow : -mouseRow) - 1;
				int tc = (mouseCol > 0 ? mouseCol : -mouseCol) - 1;
				if (tr >= 0 && tc >= 0 && tr < nRows && tc < nCols)
				{
					mouseOverStatusBar.setText(actionCommands[tr][tc] != null ? 
											   actionCommands[tr][tc] : "");
					if ((tr != currRow || tc != currCol))
					{
						g.setColor(this.getBackground());
						g.draw3DRect(cx + tc * iconW, cy + tr * iconH, 
									 iconW, iconH, mouseRow > 0);
					}
				}
			} else
			{
				if (currRow >= 0 && currRow < nRows && 
					currCol >= 0 && currCol < nCols)
				{
					g.setColor(this.getBackground());
					g.draw3DRect(cx + currCol * iconW, cy + currRow * iconH, iconW, iconH, mouseRow > 0);
					mouseOverStatusBar.setText(actionCommands[currRow][currCol] != null ? 
											   actionCommands[currRow][currCol] : "");
				}
			}
		}
		
		public void mouseMoved(MouseEvent e)
		{
			int tr = e.getY() / iconH;
			int tc = e.getX() / iconW;
			if (mouseRow != tr + 1 || mouseCol != tc + 1)
			{
				mouseRow = tr + 1;
				mouseCol = tc + 1;
				if (!checkStyle)
				{
					currRow = tr;
					currCol = tc;
				}
				paint(getGraphics());
			}
		}

		public void mouseDragged(MouseEvent e)
		{
			mousePressed(e);
		}

		public void mouseClicked(MouseEvent e) { }
		public void mousePressed(MouseEvent e)
		{
			int tr = e.getY() / iconH;
			int tc = e.getX() / iconW;
			/*if ((mouseRow < 0 && mouseCol < 0) || 
				(mouseRow != tr + 1 || mouseCol != tc + 1))*/
			{
				mouseRow = -(tr + 1);
				mouseCol = -(tc + 1);
				if (!checkStyle)
				{
					currRow = tr;
					currCol = tc;
				}
				paint(getGraphics());
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			if (mouseRow < 0 && mouseCol < 0)
			{
				boolean inbound = true;
				if (checkStyle)
				{
					mouseRow = -mouseRow;
					mouseCol = -mouseCol;
					inbound= mouseRow <= nRows && 
							 mouseRow > 0 &&
							 mouseCol <= nCols && 
							 mouseCol > 0;
					if (inbound)
					{
						currRow = mouseRow - 1;
						currCol = mouseCol - 1;
					}
				}
				mouseCol = mouseRow = 0;
				if (currRow >= 0 && currRow < nRows && 
					currCol >= 0 && currCol < nCols && inbound)
				{
					processActionEvent(new ActionEvent(
			                           this,ActionEvent.ACTION_PERFORMED,
		                               currRow + ", " + currCol));
				}
				paint(getGraphics());
			}
		}
		
		public void mouseEntered(MouseEvent e)
		{
			mouseMoved(e);
		}
		public void mouseExited(MouseEvent e)
		{
			mouseRow = mouseCol = 0;
			if (!checkStyle) currRow = currCol = -1;
			paint(getGraphics());
		}

			/**
			 * Adds an action listener.
			 */
		public synchronized void addActionListener(ActionListener l) {
	        if (l != null) {
			    actionListener = AWTEventMulticaster.add(actionListener, l);
		    }
	    }

			/**
			 * Removes action listener
			 */
		public synchronized void removeActionListener(ActionListener l) {
	        if (l != null) {
			    actionListener = AWTEventMulticaster.remove(actionListener, l);
		    }
	    }
	
			/**
			 * Mouse action event handler.
			 */
		protected void processActionEvent (ActionEvent e) {
			if (actionListener != null && e != null) {
				actionListener.actionPerformed(e);
			}
		}
	}
}
