package com.sri.gui.ext.dialogs;

import java.awt.*;
import java.awt.event.*;

import com.sri.gui.ext.*;

public class ColorDialog extends Dialog implements 
								ActionListener, 
								MouseListener, 
								MouseMotionListener,
								ItemListener,
								KeyListener
{
	protected int currCustomIndex = 0;
	protected final static int predefinedColors[][][] = {
		{{255,128,128}, {255,255,128},{128,255,128},{0,255,128},
						{128,255,255},{0,128,255},{255,128,192},{255,128,255}},
		{{255,0,0}, {255,255,0},{128,255,0},{0,255,64},
						{0,255,255},{0,128,192},{128,128,192},{255,0,255}},
		{{128,64,64}, {255,128,64},{0,255,0},{0,128,128},
						{0,64,128},{128,128,255},{128,0,64},{255,0,128}},
		{{128,0,0}, {255,128,0},{0,128,0},{0,128,64},
						{0,0,255},{0,0,160},{128,0,128},{128,0,255}},
		{{64,0,0}, {128,64,0},{0,64,0},{0,64,64},
						{0,0,128},{0,0,64},{64,0,64},{64,0,128}},
		{{0,0,0}, {128,128,0},{128,128,64},{128,128,128},
						{64,128,128},{192,192,192},{64,0,64},{255,255,255}},
	};
	
	protected boolean selected = false;
	protected Button ok = new Button("Ok");
	protected Button cancel = new Button("Cancel");
	protected Button transparent = new Button("Transparent");
	protected Button moreColors = new Button(">>");
	protected Button addToMainButton = new Button("Add to Custom colors");
	protected final static int axesTypes[][] = 
	{
		{ ColorSelector.HUE, ColorSelector.SATURATION, ColorSelector.LUMINANCE },	// HSB
		{ ColorSelector.RED, ColorSelector.GREEN, ColorSelector.BLUE },				// RGB
		{ ColorSelector.RED, ColorSelector.BLUE, ColorSelector.GREEN },				// RBB
		{ ColorSelector.GREEN, ColorSelector.RED, ColorSelector.BLUE },				// GRB
		{ ColorSelector.GREEN, ColorSelector.BLUE, ColorSelector.RED },				// GBR
		{ ColorSelector.BLUE, ColorSelector.GREEN, ColorSelector.RED },				// BGR
		{ ColorSelector.BLUE, ColorSelector.RED, ColorSelector.GREEN },				// BRG
	};
	
	protected Label axesTypeLabel = new Label("Axes Type:");
	protected Choice axesChoice = new Choice();
											 
	protected ColorSelector selector = new ColorSelector(axesTypes[0]);
	protected Label colorDisplayer = new Label("");
	
	protected TextField red = new TextField("128");
	protected TextField green = new TextField("128");
	protected TextField blue = new TextField("128");
	
	protected TextField hue = new TextField("128");
	protected TextField sat = new TextField("128");
	protected TextField lum = new TextField("128");
	
	protected ColorPallette predefined = new ColorPallette(6,8,5,5);
	protected ColorPallette newOnes = new ColorPallette(2,8,5,5);
	
	protected Color currColor = predefined.getColor(0,0);
	
		// right panel showing all colors.
	protected Panel right = new Panel(new BorderLayout());
	
		/**
		 * Constructor.
		 */
	public ColorDialog(Frame parent,String title)
	{
		super(parent,title,true);
		
			// Create the color values panel.
		Panel valuesPanelMid = new Panel(new GridLayout(3,4,2,2));

		axesChoice.addItem("HSB");
		axesChoice.addItem("RGB");
		axesChoice.addItem("RBG");
		axesChoice.addItem("GRB");
		axesChoice.addItem("GBR");
		axesChoice.addItem("BGR");
		axesChoice.addItem("BRG");
		axesChoice.addItemListener(this);
		
		Panel rgbVals = new Panel(new GridLayout(3,2));
		Panel hsbVals = new Panel(new GridLayout(3,2));
		
		hsbVals.add(new Label("Hue: ")); hsbVals.add(hue);
		hsbVals.add(new Label("Sat: ")); hsbVals.add(sat);
		hsbVals.add(new Label("Lum: ")); hsbVals.add(lum);
		rgbVals.add(new Label("Red: ")); rgbVals.add(red);
		rgbVals.add(new Label("Green: ")); rgbVals.add(green);
		rgbVals.add(new Label("Blue: ")); rgbVals.add(blue);

		Panel valHolder = new Panel(new BorderLayout());
		valHolder.add("West",hsbVals);
		valHolder.add("East",rgbVals);
		Panel colorHolder = new Panel(new BorderLayout());
		colorHolder.add("Center",colorDisplayer);
		colorHolder.add("South",new Label("Color/Solid"));
		
		Panel valuesPanel = new Panel(new BorderLayout());
		valuesPanel.add("West",colorHolder);
		valuesPanel.add("East",valHolder);
		
			// set the predefined colors.
		for (int i = 0;i < predefinedColors.length;i++)
		{
			for (int j = 0;j < predefinedColors[i].length;j++)
			{
				predefined.setColorAt(new Color(predefinedColors[i][j][0],
												predefinedColors[i][j][1],
												predefinedColors[i][j][2]),
									  i,j);
			}
		}
		for (int i = 0;i < 6;i ++)
		{
			for (int j = 0;j < 8;j++)
			{
				
			}
		}
		selector.addMouseListener(this);
		selector.addMouseMotionListener(this);
		predefined.addMouseListener(this);
		newOnes.addMouseListener(this);
		red.addKeyListener(this);
		green.addKeyListener(this);
		blue.addKeyListener(this);
		hue.addKeyListener(this);
		sat.addKeyListener(this);
		lum.addKeyListener(this);
		
		Panel south = new Panel();
		south.add(ok);
		south.add(cancel);
		south.add(transparent);
		south.add(moreColors);
		ok.addActionListener(this);
		cancel.addActionListener(this);
		moreColors.addActionListener(this);
		addToMainButton.addActionListener(this);
		
		Panel colorsPanel = new Panel(new BorderLayout());
		Panel predefPanel = new Panel(new BorderLayout());
		Panel newonesPanel =new Panel(new BorderLayout());
		
		colorsPanel.add("North",predefPanel);
		colorsPanel.add("South",newonesPanel);
		predefPanel.add("North",new Label("Basic Colors:"));
		predefPanel.add("Center",predefined);
		newonesPanel.add("North",new Label("Custom Colors:"));
		newonesPanel.add("Center",newOnes);
		
		Panel rightmid = new Panel(new BorderLayout(5,5));
		Panel selectorPanel = new Panel(new BorderLayout());
		Panel axtype = new Panel();
		selectorPanel.add("Center",selector);
		axtype.add(axesTypeLabel);
		axtype.add(axesChoice);		
		selectorPanel.add("South",axtype);
		rightmid.add("Center",selectorPanel);
		rightmid.add("South",valuesPanel);
		
		right.add("Center",rightmid);
		right.add("South",addToMainButton);
		
		Panel left = new Panel(new BorderLayout(10,10));
		left.add("Center",colorsPanel);
		left.add("South",south);
		
		//Panel mid = new Panel(new GridLayout(1,2,15,15));
		setLayout(new BorderLayout(10,10));
		add("West",left);
		//add("East",right);
		
		right.setVisible(false);
		//add("East",right);
		//add("West",colorsPanel);
		//add("Center",mid);
		//add("South",south);
		
		this.setColorTexts(predefined.getColor(0,0));
		setResizable(false);
		pack();
	}
	
		/**
		 * Returns the current selected color.
		 */
	public Color getColor()
	{
		return selector.getColor();
	}
	
		/**
		 * True if ok was clicked false otherwise
		 */
	public boolean colorSelected()
	{
		return selected;
	}

		/**
		 * Action event handler.
		 */
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		if (src == ok)
		{
			selected = true;
			setVisible(false);
		} else if (src == cancel)
		{
			selected = false;
			setVisible(false);
            hide();

            System.out.println("Here... visible: " + isVisible());
		} else if (src == addToMainButton)
		{
			newOnes.setColorAt(currColor,
							   currCustomIndex / newOnes.getColumns(), 
							   currCustomIndex % newOnes.getColumns());
			currCustomIndex = (currCustomIndex + 1) % (newOnes.getRows() * newOnes.getColumns());
		} else if (src == moreColors)
		{
			Button b = (Button)src;
			if (b.getLabel().indexOf(">>") >= 0)
			{
				b.setLabel("<<");
				right.setVisible(true);
				add("East",right);
			} else
			{
				b.setLabel(">>");
				right.setVisible(false);
				remove(right);
			}
			pack();
		}
	}
	
	private void setColorTexts(Color c)
	{
		currColor = c;
		red.setText("" + c.getRed());
		blue.setText("" + c.getBlue());
		green.setText("" + c.getGreen());
		
		float out[] = new float[3];
		Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),out);
		hue.setText("" + (int)(out[0] * 255));
		sat.setText("" + (int)(out[1] * 255));
		lum.setText("" + (int)(out[2] * 255));
		this.colorDisplayer.setBackground(currColor);
	}
	
	public void mousePressed(MouseEvent e) {
		Object src = e.getSource();
		if (src == this.predefined || src == newOnes)
		{
			ColorPallette bl = ((ColorPallette)src);
			int c = bl.getCurrent();
			
			if (src == newOnes) currCustomIndex = c;

			this.currColor = bl.getColor(c / bl.getColumns(),c % bl.getColumns());
			selector.setColor(currColor.getRed(),
							  currColor.getGreen(),
							  currColor.getBlue());
			setColorTexts(currColor);
		} else if (src == selector)
		{
			currColor = selector.getColor();
			setColorTexts(currColor);
		}
	}
	
	public void mouseReleased(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	
	public void mouseDragged(MouseEvent e)
	{
		if (e.getSource() == selector)
		{
			currColor = selector.getColor();
			setColorTexts(currColor);
		}
	}
	
	public void mouseMoved(MouseEvent e){}
	
	public void itemStateChanged(ItemEvent e)
	{
		if (e.getSource() == axesChoice)
		{
			this.selector.setAxesTypes(axesTypes[axesChoice.getSelectedIndex()]);
		}
	}
	
	public void keyTyped(KeyEvent e)
	{
		Object src = e.getSource();
		
		if (src == red || src == blue || src == green)
		{
			int r,g,b;
			try {r = Integer.parseInt(red.getText());} catch (Exception ex) { return ;}
			try {g = Integer.parseInt(green.getText());} catch (Exception ex) { return ;}
			try {b = Integer.parseInt(blue.getText());} catch (Exception ex) { return ;}
			currColor = new Color(r,g,b);
			selector.setColor(currColor.getRed(), currColor.getGreen(),currColor.getBlue());
			setColorTexts(currColor);
		}
		if (src == hue || src == sat || src == lum)
		{
			int h = 0, s = 0, l = 0;
			try {h = Integer.parseInt(hue.getText());} catch (Exception ex) { return ;}
			try {s = Integer.parseInt(sat.getText());} catch (Exception ex) { return ;}
			try {l = Integer.parseInt(lum.getText());} catch (Exception ex) { return ;}
			currColor = Color.getHSBColor((float)(h * 1.0 / 0xff),
										  (float)(s * 1.0 / 0xff), 
										  (float)(l * 1.0/0xff));
			selector.setColor(currColor.getRed(), currColor.getGreen(),currColor.getBlue());
			setColorTexts(currColor);
		}
	}
	
	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
}
