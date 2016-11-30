package com.sri.gui.ext.graph;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


import com.sri.gui.ext.backgrounds.*;

/**
 * This class is an object that represents a title for 
 * a graph.  The text even be shown in an angle.
 * 
 * However the text needs to be only a single lined text.
 */
public class Title extends Component implements MouseListener, FocusListener
{
        /**
         * The background that needs to be drawn.
         */
    protected Background background = null;
    
        /**
         * Are we in edit mode?
         */
    protected boolean inEditMode = false;
    
        /**
         * Tells if we have focus or not.
         */
    protected boolean hasFocus = false;
    
        /**
         * Predefined values of sin of any angle between 0 and 360
         */
    public final static double sinTheta[] = new double[360];
    
        /**
         * Predefined values of cos of any angle between 0 and 360
         */
    public final static double cosTheta[] = new double[360];
    
        /**
         * Initialising sin and cos tables.
         */
    static 
    {
        for (int i = 0;i < 360;i++)
        {
            sinTheta[i] = Math.sin(i * Math.PI / 180.0);
            cosTheta[i] = Math.cos(i * Math.PI / 180.0);
        }
    }
    
        // angle is between 0 and 360 inclusive
    protected int angle = 0;
    protected String text = "";
                      
    protected Image buffer = null;
    protected Graphics bg = null;
    protected Dimension bs = new Dimension();

        /**
         * Default Constructor.
         * 
         * Specifies the text and an angle of
         * 0 degrees is used.
         */
    public Title(String t)
    {
        this(t,0);    
    }
    
        /**
         * Constuctor.
         * 
         * Specifies the text and the angle in degrees.
         */
    public Title(String t,int angle)
    {
        this.text = t;
        this.angle = angle;
        addFocusListener(this);
        addMouseListener(this);
    }
    
        /**
         * Tells if we can recieve focus.
         */
    public boolean isFocusTraversable()
    {
        return true;
    }
    
    public Dimension getPreferredSize()
    {
        Dimension out = new Dimension();
        Graphics g = getGraphics();
        
        if (g == null || text == null || text.length() == 0) return out;
        
        FontMetrics fm = g.getFontMetrics();
        
        int maxW = 0;
        int totalH = 0;
        
        int h = fm.getMaxAscent() + 2;
        StringTokenizer tokens = new StringTokenizer(text,"\n");
        
        while (tokens.hasMoreTokens())
        {
            String curr = tokens.nextToken();
            int w = fm.stringWidth(curr);
            if (w > maxW) maxW = w;
            totalH += h;
        }
        
            // so now we have the best width and heights...
            // all we do now is rotate these values by the appropriate 
            // angles and we have the width and height of the
            // rotated text...
        out.width = maxW + 5;
        out.height = totalH + 10;
        return out;
    }

        /**
         * TElls if we are in edit mode.
         */
    public boolean inEditMode()
    {
        return inEditMode;
    }
    
        /**
         * Prepares and draws on the offscreen buffer.
         */
    private void prepareBuffer(Dimension d, boolean hasFocus)
    {
        if (buffer == null)
        {
            if (buffer != null) buffer.flush();
            bs.width = Math.max(1,d.width);
            bs.height = Math.max(1,d.height);
            buffer = createImage(bs.width,bs.height);
            if (buffer == null) return ;
            if (bg != null) bg.dispose();
            bg = buffer.getGraphics();
        }
        if (bg == null) return ;
        
        bg.clearRect(0,0,bs.width,bs.height);
        bg.setColor(Color.yellow);
        if (this.background != null)
        {
            background.paint(bg,this,0,0,bs.width,bs.height);
        }
        
        bg.setColor(Color.black);
        
        FontMetrics fm = bg.getFontMetrics();
        int h = fm.getAscent();
        bg.drawString(this.text,(bs.width - fm.stringWidth(text)) / 2,(bs.height + h) / 2);
        if (hasFocus)
        {
            bg.drawRect(0,0,bs.width - 1,bs.height - 1);
        
            bg.fillRect(-2,-2,4,4);
            bg.fillRect(bs.width / 2 - 2,-2,4,4);
            bg.fillRect(bs.width -2,-2,4,4);
        
            bg.fillRect(-2,bs.height / 2 -2,4,4);
            bg.fillRect(bs.width -2,bs.height / 2 -2,4,4);
        
            bg.fillRect(-2,bs.height -2,4,4);
            bg.fillRect(bs.width / 2 - 2,bs.height -2,4,4);
            bg.fillRect(bs.width -2,bs.height -2,4,4);
        }
    }
    
        /**
         * Paint method.
         */
    public void paint(Graphics g)
    {
        Dimension d = getSize();
        if (g == null) return ;
        prepareBuffer(d,hasFocus);
        
        if (buffer != null) g.drawImage(buffer,0,0,this);
    }    
    
    public void focusLost(FocusEvent e)
    {
        if (e.getSource() == this)
        {
            hasFocus = false;
            paint(getGraphics());
        }
    }
    
    public void focusGained(FocusEvent e)
    {
        if (e.getSource() == this)
        {
            hasFocus = true;
            paint(getGraphics());
        }
    }
    
    public void mouseMoved(MouseEvent e)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }
    public void mouseDragged(MouseEvent e){ }
    public void mouseClicked(MouseEvent e){ }
    public void mouseReleased(MouseEvent e){ }
    public void mousePressed(MouseEvent e){ requestFocus();}
    public void mouseEntered(MouseEvent e){ }
    public void mouseExited(MouseEvent e){ }
}
