package com.sri.gui.core;

/*
 * Author...........: Sriram Panyam
 * File.............: SSlider.java
 * Date.............: 16 July 99
 * Email............: spany@students.cs.mu.oz.au
 * Version..........: 1.0
 * Purpose..........: A slider control.
 */

/**
 * This control represents the class of Slider control objects.  These
 * sliders are very similar to the Scrollbar available within the AWT.
 * However, they have much sleeker look and feel.<p>
 *
 * The Slider can be vertical or horizontal.  The orientation must be one
 * of the following: 
 * <dl>
 * <dd> VERTICAL
 * <dd> HORIZONTAL
 * </dl>
 *
 * The percentage indicator of the slider can have various fill types.
 * Currently only three are supporeted:
 * <dl>
 * <dd> PLAIN_FILL
 * <dd> BLOCK_FILL
 * <dd> LINE_FILL
 * </dl> 
 *
 * The value of the pointer can be set either as a ratio of the distance or
 * as an explicit value.
 */

import java.awt.*;
import java.awt.event.*;

public class SSlider extends Canvas implements 
                    Adjustable, Runnable, ComponentListener, 
                    MouseListener, MouseMotionListener {
        /**
         * No Fill.
         */
    public final static int NO_FILL = 0;

        /**
         * A normail fill where the area is colored in a certain color.
         */
    public final static int PLAIN_FILL = 1;

        /**
         *  A fill where instead of completely filling the area,
         *  blocks are used.  Similar to what u see in MS Word.
         */
    public final static int BLOCK_FILL = 2;

        /**
         *  A fill where instead of completely filling the area,
         *  lines are used.
         */
    public final static int LINE_FILL = 3;

    protected Color fillColor = Color.green;
    protected Color boxBorderColor = Color.black;
    protected Color boxColor = Color.white;
    protected int fillType = BLOCK_FILL;

            /**
             * Show or hide Ticks.
             */
    protected boolean showTicks = true;
            
            /**
             * Number of ticks to show.
             *
             * Negative value indicates auto ticks.
             */
    protected int nTicks = -1;
    
    protected int minVal, maxVal;
    protected double ratio;
    protected boolean isRatio;
    protected int currVal;
    protected int targetVal;

    protected boolean enabled = true;

            /**
             * Enable Selection.
             * 
             * If true, the actual slider viewing rectangle is wider.
             */
    protected boolean enableSelection = true;
    protected boolean boxRaised = false;

            /**
             * The color of the pointer.
             */
    protected Color pointerColor = Color.red;
    protected Color pointerBorderColor = pointerColor;

            /**
             * Use 3D controls or not.
             */
    protected boolean use3D = true;

            /**
             * Back ground color.
             */
    protected Color bgColor = Color.red;

    protected int gap = 5;

            /**
             * Width of the box on which the pointer moves.
             */
    protected final static int defaultBoxWidth = 15;
    protected int boxWidth = defaultBoxWidth;

            /**
             * Height of the pointer.
             */
    protected int pointerH = boxWidth + 10;

            /**
             * Width of the pointer.
             */
    protected int pointerW = 8;

        // The following variables and functions, relate to the
        // double buffering in the component
    private Image buffer = null;
    private Graphics bufferGraphics = null;
    private Dimension buffSize = new Dimension();

    protected Thread ourThread = new Thread(this);

    protected         AdjustmentListener    adjustmentListener = null;

            /**
             * Orientation of the slider.
             *
             * Vertical or Horizontal.
             */
    protected int orient;

        /**
         * Indicates that the slider is a vertical one.
         */
    public static final int VERTICAL = 0;

        /**
         * Indicates that the slider is a horizontal one.
         */
    public static final int HORIZONTAL = 1;

        /**
         * Indicates that the pointer is a simple one which points to no
         * direction.
         */
    public static final int NO_DIRECTION = 0;

        /**
         * Indicates that the pointer points downwards in a horizontal
         * slider and left in a vertical slider.
         */
    public static final int BOTTOM_LEFT = 1;

        /**
         * Indicates that the pointer points upwards in a horizontal
         * slider and right in a vertical slider.
         */
    public static final int TOP_RIGHT = 2;

        /**
         * Direction of the pointer.
         */
    protected int pointerDir = NO_DIRECTION;

        /**
         * Constructor for a slider that is vertical by default.  
         * The parameters are the minimum and the maximum values of the
         * slider.
         */
    public SSlider(int minVal, int maxVal) {
        this(minVal, maxVal,VERTICAL);
    }


        /**
         * Constructor for the Slider.
         *
         * The parameters are minimum, maximum and the orientation.
         */
    public SSlider(int minVal, int maxVal, int orient) {
        if (minVal >= maxVal) 
            throw new IllegalArgumentException 
                            ("Minval HAS to be less than Maxval");
        setBackground(Color.lightGray);
        setForeground(Color.black);
        this.minVal = minVal;
        this.maxVal = maxVal;
        this.orient = orient;
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);
    }

    public void setOrientation(int d) {
        orient = d;
        paint(getGraphics()); invalidate();
    }
            /**
             * Returns the value currently pointer by the pointer.
             */
    public int getValue() {
        return (isRatio ? (int)(minVal + ((maxVal-minVal) * ratio)) : currVal);
    }

            /**
             * Sets the pointers width.
             */
    public void setPointerWidth(int w) {
        pointerW = w;
        paint(getGraphics()); invalidate();
    }

    public void setThreeD(boolean s) {
        use3D = s;
        paint(getGraphics()); invalidate();
    }

    public void enableSelection(boolean e) {
        enableSelection = e;
        if (!e) {
            boxWidth = 1;
        } else {
            boxWidth = defaultBoxWidth;
        }
        paint(getGraphics()); invalidate();
    }

    public void setBoxWidth(int w) {
        this.boxWidth = w;
        paint(getGraphics());
        invalidate();
    }

            /**
             * Sets the minumum of the slider.
             */
    public void setMinimum(int m) {
        minVal = m;
        if (maxVal <= m) maxVal = m + 1;
        if (getValue() < m) ratio = 0;
        paint(getGraphics());
    }

            /**
             * Sets the maximum value of the slider.
             */
    public void setMaximum(int m) {
        maxVal = m;
        if (minVal >= m) maxVal = m - 1;
        if (getValue() > m) ratio = 1.0;
        paint(getGraphics());
    }

            /**
             * Changes the direction of the pointer.
             */
    public void setPointerDirection(int d) {
        pointerDir = d;
        paint(getGraphics()); invalidate();
    }

            /**
             * Enables/Disables the control.
             */
    public void setEnabled(boolean en) {
        enabled = en;
        paint(getGraphics());
    }

        /**
         *  Set the value of the current slider as a ratio 
         *  of the range.
         *
         *  d is between 0 and 1.
         */
    public void setValue(double d) {
        if (d <= 1.0 && d >= 0.0) {
            isRatio = true;
            ratio = d;
            paint(getGraphics());
        }
    }

            /**
             * Sets the value of the slider.
             *
             * If the value is outside the minimum and maximum bounds, the
             * value is not set.
             */
    public void setValue(int v) {
        if (v >= minVal || v <= maxVal) {
            isRatio = false;
            ratio = (v - minVal) * 1.0 / (maxVal - minVal);
            currVal = v;
            paint(getGraphics());
        } 
    }

            /**
             * Sets the value of the slider.
             *
             * However, the value is painted slowly, giving the visual
             * effect of a slow-motion type update.
             */
    public void setValueSlowly(int v) {
        if (v >= minVal || v < maxVal) {
            targetVal = v;

            if (ourThread != null && ourThread.isAlive()) {
                ourThread.stop();
            }
            ourThread = null;
            ourThread = new Thread(this);
            ourThread.start();
        }
    }

            /**
             * Sets the width between ticks.
             */
    public void setTicks(int t) {
        nTicks = t;
        paint(getGraphics());  invalidate();
    }

            /**
             * Sets the fill type in the slider. 
             *
             * Has to be one of the following values:
             * <dl>
             * <dd> NO_FILL
             * <dd> PLAIN_FILL
             * <dd> BLOCK_FILL
             * <dd> LINE_FILL
             * </dl> 
             */
    public void setFillType(int ft) {
        fillType = ft;
        paint(getGraphics());
    }

            /**
             * Sets the fill color of the slider.
             */
    public void setFillColor(Color c) {
        fillColor = c;
        paint(getGraphics());
    }

            /**
             * Toggles the use of "ticks" in the slider.
             */
    public void setTicks(boolean t) {
        showTicks = t;
        paint(getGraphics());
    }

    public void update(Graphics g) {
    }

        /**
         * Paints the slider.
         */
    public void paint(Graphics g) {
        Dimension d = getSize();

        if (buffer == null || 
              buffSize.width != d.width || buffSize.height != d.height) {
            buffSize.width = Math.max(1, d.width);
            buffSize.height = Math.max(1, d.height);
            buffer = createImage(buffSize.width, buffSize.height);
            if (buffer == null) return ;
            bufferGraphics = buffer.getGraphics();
        }
        if (bufferGraphics == null) return ;
        bufferGraphics.setColor(SystemColor.control);
        bufferGraphics.fillRect(0,0,d.width, d.height);

            // now to first draw the range box
            // then the blocks or the fill depending on what is set
            // then the pointer
            // and finally the ticks.
        drawRangeBox(bufferGraphics,boxWidth);
        drawPointer(bufferGraphics);

        //drawTicks(bufferGraphics);
        g.drawImage(buffer, 0,0,this);
    }


            // draws the pointer...
            //
            // Takes into effect all the pointer types.
    protected void drawPointer(Graphics g) {
        Dimension d = getSize();
        int x = gap, y = gap, width = 0, height = 0;
        int pointerRange = 0;
        int mid = 0;
        if (pointerDir == NO_DIRECTION) {
            if (orient == HORIZONTAL) {
                y = (d.height - pointerH) / 2;
                pointerRange = (d.width - pointerW - gap - gap);
                x = (int)(gap + (ratio * pointerRange));
                drawBiDiPointer(g,x,y,pointerW, pointerH);
            } else {
                x = (d.width - pointerH) / 2;
                pointerRange = (d.height - pointerW - gap - gap);
                y = (int)(d.height - gap - pointerW - (ratio * pointerRange));
                drawBiDiPointer(g,x,y,pointerH, pointerW);
            }
        } else if (pointerDir == TOP_RIGHT) {
            drawTopRightPointer(g,x,y,width, height);
        } else {
            drawBottomLeftPointer(g,x,y,width, height);
        }
    }

            // draws the box representing the range for the slider.
    protected void drawRangeBox(Graphics g, int thickness) {
        int x = gap, y = gap, w = 0, h = 0;
        int len = 0;
        Color store = g.getColor();
        Dimension d = getSize();
        if (orient == HORIZONTAL) {
            y = (d.height - thickness) / 2;
            w = d.width - 2 - 2 * gap;
            h = thickness;
        } else {
            x = (d.width - thickness) / 2;
            h = d.height - 2 - 2 * gap;
            w = thickness;
        }

                // now draw the box using 3D or no 3D depending
                // on the 3D flag.
        if (use3D) g.fill3DRect(x,y , w , h,boxRaised);
        else {
            g.setColor(boxBorderColor);
            g.drawRect(x,y,w,h);
        }

                // draw the box itself..
        if (w > 2 && h > 2) {
            g.setColor(boxColor);
            g.fillRect(x + 1, y + 1,w - 2,h - 2);
        }

                // draw the fill.
        if (enableSelection && fillType != NO_FILL) {
            g.setColor(fillColor);
            if (orient == HORIZONTAL) {
                h = thickness;
                int pointerRange = (d.width - gap - gap);
                w = (int)((ratio * pointerRange));
            } else {
                x++;
                w = thickness;
                int pointerRange = (d.height - gap - gap);
                y = (int)(d.height - gap - (ratio * pointerRange));
                h = d.height - gap - y - 1;
            }
            if (fillType == PLAIN_FILL) {
                if (w > 4 && h > 4) g.fillRect(x + 2, y + 2, w - 4, h - 4);
            }
            else if (fillType == BLOCK_FILL) {
                if (orient == HORIZONTAL) {
                    for (int i = x + gap + 1;i < x + w - 5;i += 9)
                        g.fillRect(i,y + 3,6,boxWidth - 4);
                } else {
                    for (int i = y + h - 9;i >= y;i -= 9)
                        g.fillRect(x + 2,i,boxWidth - 4,6);
                }
            }
            else if (fillType == LINE_FILL) {
                if (orient == HORIZONTAL) {
                    for (int i = x + gap + 1;i < 1 + x + w;i += 3)
                        g.drawLine(i,y + 3,i,y + boxWidth - 3);
                } else {
                    for (int i = y;i < y + h - 1;i += 3)
                        g.drawLine(x + 2,i,x + boxWidth - 3,i);
                }
            }
        }
        g.setColor(store);
    }

    public void componentHidden(ComponentEvent e) { }
    public void componentMoved(ComponentEvent e) { }
    public void componentShown(ComponentEvent e) { }
    public void componentResized(ComponentEvent e) {
        invalidate(); paint(getGraphics());
    }

    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { mouseClicked(e); }
    public void mousePressed(MouseEvent e) { mouseDragged(e); }
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == this) {
            mouseDragged(e);
            processAdjustmentEvent();
        }
    }

    public void mouseMoved(MouseEvent e) { }
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() == this) {
            if (!enabled) return ;
            if (orient == HORIZONTAL) {
                    // then only consider the x coodinate of the point...
                int x = e.getX() - gap;
                int w = getSize().width - gap - gap;
                x = Math.min(w,x);
                setValue(x * 1.0 / w);
            } else {
                    // then only consider the y coodinate of the point...
                int y = getSize().height - e.getY() - gap;
                int h = getSize().height - gap - gap;
                setValue(y * 1.0 / h);
            }
        }
    }

            /*
             * This is the backbone function of the thread that does an
             * animated update of the values.
             */
    public void run() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        int incr;
        int curr = getValue();
        incr = (targetVal > curr ? 1 : -1);
        while (curr != targetVal) {
            setValue(curr += incr);
            try { Thread.sleep(20); }
            catch (InterruptedException e) { }
            curr = getValue();
        }
    }

            /**
             * Adds an adjustment listener to this control.
             */
    public void addAdjustmentListener(AdjustmentListener l) {
        adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
    }

            /**
             * Removes an adjustment listener to this control.
             */
    public void removeAdjustmentListener(AdjustmentListener l) {
        adjustmentListener = AWTEventMulticaster.remove(adjustmentListener,l);
    }

    public void processAdjustmentEvent() {
        if (!enabled) return ;
        if (adjustmentListener != null) {
            adjustmentListener.adjustmentValueChanged(
                    new AdjustmentEvent(this,
                                    AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED,
                                    AdjustmentEvent.TRACK,
                                    getValue()));
        }
    }

            /**
             * Returns the minimum value of the slider.
             */
    public int getMinimum() { return minVal; }

            /**
             * Returns the maximum value of the slider.
             */
    public int getMaximum() { return maxVal; }

            /**
             * Returns the orientation of the slider.
             */
    public int getOrientation() { return orient; }

            /**
             * Returns the block increment value of the slider.
             */
    public int getBlockIncrement() { return 0; }

            /**
             * Returns the unit increment value of the slider.
             */
    public int getUnitIncrement() { return 0; }

            /**
             * Returns the visible amount of the slider
             */
    public int getVisibleAmount() { return 0; }

            /**
             * Sets the block increment of the slider;
             */
    public void setBlockIncrement (int b) { }

            /**
             * Sets the unit increment of the slider;
             */
    public void setUnitIncrement(int b) { }

            /**
             * Sets the visible amount of the slider;
             */
    public void setVisibleAmount(int v) { }

            /*
             * Draws a pointer that points in no direction.
             */
    protected void drawBiDiPointer(Graphics g,
                                   int x,int y,
                                   int width,int height) {
        Color store = g.getColor();
        g.setColor(pointerColor);
        if (use3D) {
            g.fill3DRect(x, y, width, height,boxRaised);
            g.setColor(Color.black);
            g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
            g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
            g.setColor(store);
            if (!enabled) {
                store = g.getColor();
                g.setColor(pointerColor.brighter());
                g.fillRect(x + 1,y + 1,width - 2,height - 2);
                g.setColor(store);
            }
        } else {
            if (!enabled) {
                g.setColor(pointerColor.brighter());
                g.fillRect(x + 1,y + 1,width - 2,height - 2);
            } else {
                g.fillRect(x, y, width, height);
            }
        }
        g.setColor(store);
    }

    protected void drawTopRightPointer(Graphics g,
                                   int x,int y,
                                   int width,int height) {
        Color curr = g.getColor();
        Color lig = curr.brighter();
        Color dark = curr.darker();
        g.fillRect(x,y,width,height);
        if (orient == HORIZONTAL) {
            g.setColor(lig);
            g.drawLine(x + width / 2, y, x, y + width / 2);
            g.drawLine(x, y + width / 2, x, y + height - 1);
            g.setColor(Color.black);
            g.drawLine(x + width / 2, y, x + width, y + width / 2);
            g.setColor(dark);
            g.drawLine(x + width / 2, y + 1, x + width - 1, y + 1 + width / 2);
            g.drawLine(x + width - 1, y + 1 + width / 2,
                       x + width - 1,y + height - 1);
            g.drawLine(x + width - 1,y + height - 1,
                       x + 1,y + height - 1);
            g.setColor(Color.black);
            g.drawLine(x + width, y + width / 2, x + width, y + height);
            g.drawLine(x + width, y + height, x, y + height);

            if (!enabled) {
                    // disabled paint not done yet....
            }
        } else {
                    // pointers for vertical sliders not drawn yet...
        }
        g.setColor(curr);
    }

    protected void drawBottomLeftPointer(Graphics g,
                                   int x,int y,
                                   int width,int height) {
        Color curr = g.getColor();
        Color lig = curr.brighter();
        Color dark = curr.darker();
        g.fillRect(x,y,width,height);
        if (orient == HORIZONTAL) {
            g.setColor(lig);
            g.drawLine(x, y, x + width - 1, y);
            g.drawLine(x, y, x, y + height - width / 2);
            g.drawLine(x, y + height - width / 2,
                       x + width / 2, y + height);
            g.setColor(Color.black);
            g.drawLine(x + width / 2, y + height,
                       x + width, y + height - width / 2);
            g.setColor(dark);
            g.drawLine(x + width / 2, y + height - 1,
                       x + width - 1, y + height - width / 2);
            g.drawLine(x + width - 1, y + height - width / 2,
                       x + width - 1, y + 1);
            if (!enabled) {
                    // disabled paint not done yet....
            }
        } else {
                    // pointers for vertical sliders not drawn yet...
        }
        g.setColor(curr);
    }

    public Dimension getPreferredSize() {
        if (orient == HORIZONTAL) {
            return new Dimension(2 * gap + pointerW + 70,pointerW + 5);
        } else {
            return new Dimension(pointerW + 5,2 * gap + pointerW + 70);
        }
    }
}
