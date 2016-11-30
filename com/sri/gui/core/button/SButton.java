package com.sri.gui.core.button;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import com.sri.gui.core.*;

    /**
     * The super class of all buttons.
     */
public abstract class SButton extends Component 
                              implements 
                                    MouseListener,
                                    MouseMotionListener,
                                    FocusListener {
    
    protected final static int MOUSE_OUT        = 0;
    protected final static int MOUSE_OVER       = 1;
    protected final static int MOUSE_PRESSED    = 2;
    
    protected final static int SHOW_BORDER_MASK = 1 << 0;               // 0th bit
    protected final static int SHOW_IMAGE_MASK =  1 << 1;               // 1st bit
    protected final static int SHOW_TEXT_MASK =   1 << 2;               // 2nd bit
    protected final static int HAS_FOCUS_MASK =   1 << 3;               // 3rd bit
    protected final static int PRESSED_INSIDE_MASK = 1 << 4;            // 4th bit
    protected final static int MOUSE_STATE_MASK = ((1 << 2) - 1) << 5;  // 5th and 6th bit
    protected final static int TEXT_POS_MASK = ((1 << 2) - 1) << 7;     // 7th-10th bits
    protected final static int CONTENT_AL_MASK = ((1 << 3) - 1) << 11;  // 11th-14th bits
    protected final static int ROLL_OVER_MASK = 1 << 15;                // 15th bit
    protected final static int SHOW_FOCUS_MASK = 1 << 16;               // 16th bit

            /**
             * The content allignment and text position constants.
             */
    public final static int NORTH = 1;
    public final static int EAST = 2;
    public final static int WEST = 4;
    public final static int SOUTH = 8;
    public final static int NORTH_EAST = NORTH | EAST;
    public final static int SOUTH_EAST = SOUTH | EAST;
    public final static int SOUTH_WEST = SOUTH | WEST;
    public final static int NORTH_WEST = NORTH | WEST;
    public final static int CENTER = 0;

        /**
         * Extra space around the content.
         */
    protected final static int EXTRA_BOUNDARY = 4;
    
        /**
         * The flags.
         */
    protected int flags = 0;
    
        /**
         * The preferred size for the button.
         */
    protected Dimension prefSize = new Dimension();
   
        /**
         * This should be like images[x] (x = 1,2,3) for enabled ones
         */
    protected Image images[] = null;
    protected Image disabledImage = null;

        /**
         * Button's text.
         */
    protected String text = "";

    public void mousePressed(MouseEvent e) { requestFocus(); }
    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { mouseMoved(e); }
    public void mouseExited(MouseEvent e) { mouseMoved(e); }

    public void mouseDragged(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }

    protected SButton()
    {
        setTextPosition(CENTER);
        setContentAllignment(CENTER);
        setRollOver(true);
        showText(true);
        showImage(true);
        showBorder(true);
        showFocus(false);
        addFocusListener(this);
    }
    
    public final boolean isFocusTraversable()
    {
        return true;
    }
    
        /**
         * Creates image holders.
         */
    protected synchronized void createImages() {
        if (images == null) {
            images = new Image[3];
        }
    }

        /**
         * Clears all images.
         */
    protected final void clearImages(int wh) {
        if (images[wh] != null) {
            images[wh].flush();
            images[wh] = null;
        }
    }
        
    public void focusLost(FocusEvent e)
    {
        if (e.getSource() == this)            
        {
            flags &= (0xffffffff ^ HAS_FOCUS_MASK);
			int ms = getMouseState();
			int ns = (isRollOver() ? MOUSE_OUT : MOUSE_OVER);
			if (ns != ms) setMouseState(ns);
            paint(getGraphics());
        }
    }
    
    public void focusGained(FocusEvent e)
    {
        if (e.getSource() == this)            
        {
            flags |= HAS_FOCUS_MASK;
			int ms = getMouseState();
			int ns = (isRollOver() ? MOUSE_OUT : MOUSE_OVER);
			if (ns != ms) setMouseState(ns);
            paint(getGraphics());
        }
    }

        /**
         * Sets the mouse out image.
         */
    public synchronized void setMouseOutImage(Image im) {
        createImages();
        clearImages(MOUSE_OUT);
        if (im != null) {
            images[MOUSE_OUT] = im;
            //images[1][MOUSE_OUT] = Utils.createDisabledImage(im,this);
        }
    }

        /**
         * Sets the mouse over image.
         */
    public synchronized void setMouseOverImage(Image im) {
        createImages();
        clearImages(MOUSE_OVER);
        if (im != null) {
            images[MOUSE_OVER] = im;
            //images[1][MOUSE_OVER] = Utils.createDisabledImage(im,this);
        }
    }

        /**
         * Sets the mouse pressed image.
         */
    public synchronized void setMousePressedImage(Image im) {
        createImages();
        clearImages(MOUSE_PRESSED);
        if (im != null) {
            images[MOUSE_PRESSED] = im;
            //images[1][MOUSE_PRESSED] = Utils.createDisabledImage(im,this);
        }
    }

        /**
         * Sets image display option.
         */
    public synchronized void showImage(boolean sh) {
        if (sh) flags |= SHOW_IMAGE_MASK;
        else flags &= (0xffffffff ^ SHOW_IMAGE_MASK);
        paint(getGraphics());
    }

        /**
         * Sets text display option.
         */
    public synchronized void showText(boolean sh) {
        if (sh) flags |= SHOW_TEXT_MASK;
        else flags &= (0xffffffff ^ SHOW_TEXT_MASK);
        paint(getGraphics());
    }

        /**
         * Sets border display option.
         */
    public synchronized void showBorder(boolean sh) {
        if (sh) flags |= SHOW_BORDER_MASK;
        else flags &= (0xffffffff ^ SHOW_BORDER_MASK);
        paint(getGraphics());
    }

        /**
         * Sets border display option.
         */
    public synchronized void showFocus(boolean sh) {
        if (sh) flags |= SHOW_FOCUS_MASK;
        else flags &= (0xffffffff ^ SHOW_FOCUS_MASK);
        paint(getGraphics());
    }
    
        /**
         * Returns the text of the button.
         */
    public String getText() {
        return text;
    }
    
        /**
         * Sets the roll over option.
         */
    public void setRollOver(boolean roll)
    {
        if (roll) flags |= ROLL_OVER_MASK;
        else flags &= (0xffffffff ^ ROLL_OVER_MASK);
        setMouseState(roll ? MOUSE_OUT : MOUSE_OVER);
        paint(getGraphics());
    }
    
        /**
         * Tells if the button is a rollover type button.
         */
    public boolean isRollOver()
    {
        return (flags & ROLL_OVER_MASK) != 0;
    }
        
        /**
         * Gets the text position type.
         */
    public int getTextPosition()    {
        return (flags & TEXT_POS_MASK) >> 7;
    }
    
        /**
         * Sets the text position.
         */
    public synchronized void setTextPosition(int loc) {
        flags = (flags & (0xffffffff ^ TEXT_POS_MASK)) | (loc << 7);
        paint(getGraphics());
    }
    
        /**
         * Gets the content alignment type.
         */
    public int getContentAllignment()    {
        return (flags & CONTENT_AL_MASK) >> 11;
    }
    
        /**
         * Sets the content alignment.
         */
    public synchronized void setContentAllignment(int loc) {
        flags = (flags & (0xffffffff ^ CONTENT_AL_MASK)) | (loc << 11);
        paint(getGraphics());
    }
    
        /**
         * Sets the text of the button.
         */
    public synchronized void setText(String t) {
        text = t;
        paint(getGraphics());
    }
        
        /**
         * Gets the mouse state.
         */
    protected int getMouseState() {
        return (flags & MOUSE_STATE_MASK) >> 5;
    }
    
        /**
         * Sets the mouse state.
         */
    protected void setMouseState(int state) {
        flags = (flags & 0xffffff9f) | (state << 5);
    }
    
    
        /**
         * Returns the preferred size of the object.
         */
    public Dimension getPreferredSize() {
        if (prefSize == null) prefSize = new Dimension(1,1);
        int iw = 0;        // max image width
        int ih = 0;        // max image height
        int sw = 0;        // text width
        int sh = 0;        // text height
        int extraBoundary = 6;
        
            /**
             * This is the text's position WRT the image. 
             * In calculating the preferred size, the content alignment
             * does not play any part.
             */
        int tp = this.getTextPosition();

                // calculate image width only if images are showing
        if ((flags & SHOW_IMAGE_MASK) != 0) {
            for (int i = 0;i < images.length;i++) {
                if (images[i] != null) {
                    int cw = images[i].getWidth(this);
                    int ch = images[i].getHeight(this);
                    if (cw > iw) iw = cw;
                    if (ch > ih) ih = ch;
                }
            }
        }
        
                // calculate Text dimensions only if Texts are showing
        if ((flags & SHOW_TEXT_MASK) != 0 && this.text != null) {
            Graphics g = getGraphics();
            if (g != null) {
                FontMetrics fm = g.getFontMetrics(getFont());
                sh = fm.getAscent() + 5;
                sw = fm.stringWidth(text) + 5;
            }
        }
        
            // now based on these two width and heigth parameters
            // we find the preferred size.  The preferred size
            // now depends on the layout of the text with respect
            // to the image. 
            // First we calculate the preferred height
        int textLocation = getTextPosition();
        prefSize.height = ((((tp & NORTH) != 0) || ((tp & SOUTH) != 0)) ? 
                           ih + sh : Math.max(sh,ih)) + EXTRA_BOUNDARY;
        
        prefSize.width = ((((tp & EAST) != 0) || ((tp & WEST) != 0)) ?
                          iw + sw : Math.max(sw,iw)) + EXTRA_BOUNDARY;
        
        return prefSize;                
    }
    
        /**
         * Paints the button.
         */
    public synchronized void paint(Graphics g) {
        if (g == null) return ;
        Dimension d = getSize();
        int dw = d.width - 4;
        int dh = d.height - 4;
        
        int tp = getTextPosition();
        int ca = getContentAllignment();

        //System.out.println("text, TP, CA = " + text + ", " + tp + ", " + ca);
        Image whichImage = null;
        int ms = getMouseState();
        int ix = 0, iy = 0, iw = 0, ih = 0;
        int sx = 0, sy = 0, sw = 0, sh = 0;
        int cx = 0, cy = 0, cw = 0, ch = 0;

        boolean showImage = (flags & SHOW_IMAGE_MASK) != 0;
        boolean showText = (flags & SHOW_TEXT_MASK) != 0;
        
        g.setColor(getBackground());
        g.fillRect(0,0,d.width,d.height);
        
        if (showImage)
        {
            if (isEnabled()) {
                whichImage = images[ms];
            } else {
                whichImage = disabledImage;
                if (whichImage == null)
                    whichImage = Utils.createDisabledImage(images[ms],this);
            }
            iw = (whichImage == null ? 0 : whichImage.getWidth(this));
            ih = (whichImage == null ? 0 : whichImage.getHeight(this));
        }
        
        if (showText)
        {
            FontMetrics fm = g.getFontMetrics(getFont());
            if ((flags & SHOW_TEXT_MASK) != 0) {
                sw = fm.stringWidth(text) + 5;
                sh = fm.getAscent();
            }
        }
        
            // now we calculate the dimension of 
            // the total content
            // including the image and string bounds...
        ch = EXTRA_BOUNDARY + (((tp & NORTH) != 0 || (tp & SOUTH) != 0) ?
                                        ih + sh : Math.max(ih,sh));
        if (ch > dh) ch = dh;

        cw = EXTRA_BOUNDARY + (((tp & EAST) != 0 || (tp & WEST) != 0) ?
                                        iw + sw : Math.max(iw,sw));
        if (cw > dw) cw = dw;
        
                // now we try and find the contents boundaries
        cx = (dw - cw) / 2;
        cy = (dh - ch) / 2;
        
        if ((ca & NORTH) != 0)
        {
            cy = 2; 
        } else if ((ca & SOUTH) != 0)
        {
            cy = d.height - ch - 2;
        }

        if ((ca & EAST) != 0)
        {
            cx = 2;
        } else if ((ca & WEST) != 0)
        {
            cx = d.width - cw - 2;
        }
        
        sx = cx + (cw - sw) / 2;
        sy = cy + (sh + ch) / 2;
        
        ix = cx + (cw - iw) / 2;
        iy = cy + (ch - ih) / 2;
        
        if (((tp & NORTH) != 0) || ((tp & SOUTH) != 0))
        {
            sy = ((tp & NORTH) != 0 ? cy + 2 + sh : cy + ch - 2);
            iy = ((tp & NORTH) != 0 ? sy + 2 : sy - sh - ih - 2);
            if (iy < cy) iy = cy;
            if (iy > cy + ch - ih && ch > ih)
            {
                iy = cy + ch - ih;
            }
        }
        if (((tp & EAST) != 0) || ((tp & WEST) != 0))
        {
            sx = ((tp & WEST) != 0 ? cx + 2 : cx + cw - sw - 2);
            ix = ((tp & WEST) != 0 ? sx + sw + 2 : 
                                             sx - iw - 2);
            if (ix < cx) ix = cx;
            if (ix > cx + cw - iw && cw > iw)
            {
                ix = cx + cw - iw;
            }
        }
        
        //cx -= 1; cy -= 1;
        //cw += 2; ch += 2;
        cy--;
        cx --;
        cw += 10;
        if ((flags & SHOW_BORDER_MASK) != 0 && ms == MOUSE_PRESSED) {
            sx++; sy++;
            ix++; iy++;
            cx++; cy++;
        }
        
            // drwa the image if we ahve to
        if (showImage && whichImage != null) 
            g.drawImage(whichImage,ix + 1,iy + 1,this);
            
            // draw the text if we have to..
        if (showText && text != null) {
            g.setColor(getForeground());
            g.setFont(getFont());
            g.drawString(text,sx + 1,sy + 1);
        }
        
            // draw the bevel
        if ((flags & SHOW_BORDER_MASK) != 0 && ms != MOUSE_OUT) {
            g.setColor(Color.lightGray);
            g.draw3DRect(0,0,d.width - 1,d.height - 1,ms == MOUSE_OVER);
        }
        
            /**
             * finally draw the focus if we have to...
             */
        if ((flags & HAS_FOCUS_MASK) != 0 && (flags & SHOW_FOCUS_MASK) != 0)
        {
            Color c = g.getColor();
            
            g.setColor(getForeground());
            for (int i = 4, y = 2;i <= d.width - 8;i += 4)
            {
                g.fillOval(i,y,2,2);
                g.fillOval(i,d.height - y - y,2,2);
            }
            for (int i = 4, x = 2;i <= d.width - 8;i += 4)
            {
                g.fillOval(x,i,2,2);
                g.fillOval(d.width - x - x,i,2,2);
            }
            g.setColor(c);
        }
    }
}
