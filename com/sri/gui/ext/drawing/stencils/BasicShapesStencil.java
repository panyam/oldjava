
package com.sri.gui.ext.drawing.stencils;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.tabs.*;
import com.sri.gui.ext.*;
import com.sri.gui.ext.drawing.*;
import com.sri.gui.ext.drawing.selectors.*;
import com.sri.gui.ext.dialogs.*;
import java.awt.image.*;

/**
 * Panel used to show basic shapes buttons.
 */
public class BasicShapesStencil extends Stencil
{
    ColorDialog cdialog = null;

    LineStyleSelector lineStyle = null;
    LineWidthSelector lineWidth = null;
    ArrowStyleSelector arrowStyle = null;

    CardLayout cardManager = new CardLayout();
    Panel southPanel = new Panel(cardManager);

        /**
         * Constructor.
         */
    public BasicShapesStencil(SceneViewer scene, InfoListener infoListener)
    {
        super(scene, infoListener);

        setLayout(new BorderLayout());

        arrowStyle = new ArrowStyleSelector(infoListener);
        lineWidth = new LineWidthSelector(infoListener);
        lineStyle = new LineStyleSelector(infoListener);

        cardManager.addLayoutComponent("LineStyle", lineStyle);
        cardManager.addLayoutComponent("LineWidth", lineWidth);
        cardManager.addLayoutComponent("ArrowStyle", arrowStyle);

        add("Center",new BasicShapeButtons());
        add("South", southPanel);

        cardManager.show(southPanel, "LineWidth");
    }

    class BasicShapeButtons extends SelectorPanel implements ItemListener
    {
        public final String basicShapeButtonNames[] =
        {
            "Normal Mouse", "Rotate Object", "Text",
            "Lines", "Rectangles", "Circles",
            "Line Color", "Fill Color", "Text Color",
            "Line Style", "Line Width", "Arrow Style",
        };

        public BasicShapeButtons()
        {
            super(BasicShapesStencil.this.infoListener);
            numStyles = 12;
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
            this.addItemListener(this);
        }

        public void itemStateChanged(ItemEvent e)
        {
            if (e.getSource() == this)
            {
                System.out.println("Curr Item: " + currItem);
                if (currItem < 3) return ;
                switch (currItem)
                {
                case 3:
                    System.out.println("Setting pre add shape mode");
                    sceneViewer.addElement(new Rect(), true);
                break;
                case 4:
                    sceneViewer.addElement(new Rect(), true);
                break;
                case 5:
                    sceneViewer.addElement(new Triangle(), true);
                break;
                case 6:
                case 7:
                case 8:
                    cdialog.setVisible(true);
                break;
                case 9:
                    cardManager.show(southPanel, "LineStyle");
                    return ;
                case 10:
                    cardManager.show(southPanel, "LineWidth");
                    return ;
                case 11:
                    cardManager.show(southPanel, "ArrowStyle");
                    return ;
                }
            }
        }

        public void generateEvent (int which)
        {
            processItemEvent(new ItemEvent(this,
                                           ItemEvent.ITEM_STATE_CHANGED,
                                           this,
                                           ItemEvent.ITEM_STATE_CHANGED));
        }

        public int pointToIndex(int x,int y)
        {
            Dimension d = this.getSize();
            if (x >= 0 && y >= 0 && x < d.width && y < d.height)
            {
                return (3 * (y * 4 / d.height)) + (x * 3 / d.width);
            }
            return -1;
        }

        public void mouseExited(MouseEvent e)
        {
            super.mouseExited(e);
            this.infoListener.setInfo("");
        }

        public void mouseMoved(MouseEvent e)
        {
            super.mouseMoved(e);
            this.infoListener.setInfo(basicShapeButtonNames[currMovingItem]);
        }

        public void mouseDragged(MouseEvent e)
        {
            super.mouseDragged(e);
            this.infoListener.setInfo(basicShapeButtonNames[currPressedItem]);
        }

        protected void prepareBuffer(Dimension d)
        {
            super.prepareBuffer(d);
            bg.setColor(Color.lightGray);
            bg.fillRect(0,0,d.width,d.height);
            int w = d.width / 3, rw = d.width % 3;
            int h = d.height / 4, rh = d.width % 4;
            int x;
            int y;
            int offX = (w - 20) / 2;
            int offY = (h - 20) / 2;

            for (int i = 0;i < 12;i++)
            {
                x = (i % 3) * w;
                y = (i / 3) * h;
                bg.setColor(Color.lightGray);
                if (i == currItem) bg.fill3DRect(x++,y++,w,h,false);
                else if (i == currMovingItem) bg.draw3DRect(x,y,w,h,true);
                else if (i == currPressedItem) bg.draw3DRect(x++,y++,w,h,false);
                drawIcon(bg,offX + x,offY + y,i);
            }
        }

        void drawIcon(Graphics g,int x,int y,int which)
        {
            switch (which)
            {
            case 0 : drawPointerIcon(g,x,y); return ;
            case 1 : drawRotatorIcon(g,x,y);return ;
            case 2 : drawTextIcon(g,x,y);return ;
            case 3 : drawLineIcon(g,x,y);return ;
            case 4 : drawRectIcon(g,x,y);return ;
            case 5 : drawCircleIcon(g,x,y);return ;
            case 6 : drawLineColorIcon(g,x,y);return ;
            case 7 : drawFillIcon(g,x,y);return ;
            case 8 : drawTextColorIcon(g,x,y);return ;
            case 9 : drawLineStyleIcon(g,x,y);return ;
            case 10 : drawLineWidthIcon(g,x,y);return ;
            case 11: drawArrowStyleIcon(g,x,y);return ;
            }
        }

        void drawLineWidthIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.black);
            g.drawLine(x + 2,y + 3,x + 15,y + 3);
            g.fillRect(x + 2,y + 7,14,3);
            g.fillRect(x + 2,y + 13,14,5);
        }

        void drawLineColorIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.green);
            g.fillRect(x + 1,y + 14,17,5);
            g.setColor(Color.black);

            int pts[] = { 6,10,8,11,9,14,10,16,10,16,11,16,12,16,14,16,16,16 };
            for (int i = 12,j = 0;i >= 4;i--,j += 2) g.drawLine(x + pts[j],y + i,x + pts[j + 1],y + i);
            g.setColor(Color.yellow);
            g.drawLine(x + 9,y + 11,x + 14,y + 6);
            g.drawLine(x + 11,y + 8,x + 12,y + 7);
            g.setColor(new Color(128,128,0));
            g.drawLine(x + 11,y + 10,x + 15,y + 6);
            g.drawLine(x + 14,y + 8,x + 15,y + 7);
        }

        void drawTextIcon(Graphics g,int x,int y)
        {
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth("T");
            int h = fm.getAscent();
            g.setColor(Color.red);
            g.drawString("T",x + 10 - (w >> 1),y + 10 + (h >> 1));
        }

        void drawTextColorIcon(Graphics g,int x,int y)
        {

            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth("T");
            int h = fm.getAscent();
            g.setColor(Color.red);
            g.drawString("T",x + 10 - (w >> 1),y + 12);
            g.fillRect(x + 1,y + 14,17,5);
        }

        void drawArrowStyleIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.black);
            for (int i = 0;i < 3;i++) g.drawLine(x + 3,y + 4 + (i * 6),x + 16,y + 4 + (i * 6));
            for (int i = 0;i < 2;i++)
            {
                g.drawLine(x + 3,y + 4 + (i * 6),x + 5,y + 2 + (i * 6));
                g.drawLine(x + 3,y + 4 + (i * 6),x + 5,y + 6 + (i * 6));
            }
            for (int i = 1;i < 3;i++)
            {
                g.drawLine(x + 16,y + 4 + (i * 6),x + 14,y + 2 + (i * 6));
                g.drawLine(x + 16,y + 4 + (i * 6),x + 14,y + 6 + (i * 6));
            }
        }

        void drawLineStyleIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.black);
            g.drawLine(x + 2,y + 4,x + 17,y + 4);
            g.drawLine(x + 2,y + 8,x + 5,y + 8);
            g.drawLine(x + 8,y + 8,x + 11,y + 8);
            g.drawLine(x + 14,y + 8,x + 14,y + 8);
            for (int i = 0;i < 16;i += 2)
                g.drawLine(x + 2 + i,y + 12,x + 2 + i,y + 12);
            g.drawLine(x + 2,y + 15,x + 5,y + 15);
            g.drawLine(x + 6,y + 15,x + 6,y + 15);
            g.drawLine(x + 8,y + 15,x + 10,y + 15);
            g.drawLine(x + 12,y + 15,x + 12,y + 15);
            g.drawLine(x + 14,y + 15,x + 16,y + 15);
        }

        void drawRotatorIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.blue.darker());
            g.drawOval(x + 8,y + 8,4, 4);
            g.setColor(Color.black);
            g.drawLine(x + 4,y + 1,x + 8,y + 1);
            g.drawLine(x + 8,y + 1,x + 8,y + 5);
            g.drawRect(x + 6,y + 2,1,1);
            g.drawRect(x + 4,y + 3,1,1);
            g.drawRect(x + 3,y + 4,1,1);
            g.drawRect(x + 2,y + 6,1,1);
            g.drawRect(x + 1,y + 8,1,4);
            g.drawLine(x + 2,y + 13,x + 2,y + 14);
            g.drawLine(x + 3,y + 13,x + 3,y + 16);
            g.drawLine(x + 4,y + 15,x + 4,y + 16);
            g.drawLine(x + 5,y + 16,x + 6,y + 16);
            g.drawLine(x + 5,y + 17,x + 14,y + 17);
            g.drawLine(x + 7,y + 18,x + 12,y + 18);
            g.drawLine(x + 13,y + 16,x + 16,y + 16);
            g.drawLine(x + 15,y + 15,x + 16,y + 15);
            g.drawRect(x + 16,y + 13,1,1);
            g.drawRect(x + 17,y + 8,1,4);
        }

        void drawPointerIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.black);
            g.fillRect(x + 1,y + 8,6,3);
            g.fillRect(x + 12,y + 8,6,3);
            g.fillRect(x + 8,y + 1,3,6);
            g.fillRect(x + 8,y + 12,3,6);
            g.drawLine(x + 7,y + 7,x + 11,y + 7);
            g.drawLine(x + 7,y + 7,x + 7,y + 11);
            g.drawLine(x + 11,y + 11,x + 11,y + 7);
            g.drawLine(x + 11,y + 11,x + 7,y + 11);
        }

        void drawCircleIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.red);
            g.drawOval(x + 2,y + 1,8,10);
            g.setColor(Color.blue.darker());
            g.drawOval(x + 2,y + 13,15,7);
            g.setColor(Color.white);
            g.fillOval(x + 12,y + 2,7,11);
            g.setColor(Color.black);
            g.drawOval(x + 12,y + 2,7,11);
        }

        void drawRectIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.red);
            g.drawRect(x + 3,y + 1,5,8);
            g.setColor(Color.black);
            g.drawRect(x + 4,y + 10,10,6);
            g.setColor(Color.blue.darker());
            g.fillRect(x + 10,y + 3,6,10);
        }

        void drawLineIcon(Graphics g,int x,int y)
        {
            g.setColor(Color.blue.darker());
            g.drawLine(x + 4,y + 5,x + 14,y + 15);
            g.drawLine(x + 5,y + 4,x + 15,y + 14);
        }

        void drawFillIcon(Graphics g,int x,int y)
        {
            g.setColor(new Color(0,0,128));
            g.drawLine(x + 3,y + 12,x + 3,y + 16);
            g.drawLine(x + 2,y + 14,x + 2,y + 14);
            g.drawLine(x + 4,y + 15,x + 4,y + 15);

            g.setColor(Color.black);
            g.drawLine(x + 4,y + 9,x + 11,y + 16);
            g.drawLine(x + 6,y + 9,x + 10,y + 5);
            g.drawLine(x + 11,y + 14,x + 15,y + 10);
            g.drawLine(x + 4,y + 9,x + 4,y + 10);
            g.drawLine(x + 10,y + 15,x + 10,y + 16);
            g.drawLine(x + 11,y + 3,x + 11,y + 4);
            g.drawLine(x + 17,y + 9,x + 17,y + 10);

            g.setColor(new Color(0,128,128));
            g.drawLine(x + 10,y + 4,x + 16,y + 10);
            g.drawLine(x + 11,y + 13,x + 15,y + 9);
            g.drawLine(x + 7,y + 10,x + 10,y + 13);
            g.drawLine(x + 8,y + 9,x + 11,y + 12);
            g.drawLine(x + 11,y + 15,x + 12,y + 14);

            g.setColor(new Color(0,255,255));
            g.drawLine(x + 11,y + 6,x + 14,y + 9);
            g.drawLine(x + 8,y + 6,x + 12,y + 10);
            g.drawLine(x + 7,y + 7,x + 11,y + 11);
            g.drawLine(x + 6,y + 8,x + 10,y + 12);
            g.drawLine(x + 15,y + 11,x + 13,y + 13);

            g.setColor(Color.darkGray);
            g.drawLine(x + 5,y + 9,x + 10,y + 14);
            g.drawLine(x + 5,y + 11,x + 9,y + 15);
        }
    }

        /**
         * Ensures that the dialogs are not null.
         */
    protected void verifyDialogs()
    {
        if (cdialog == null)
        {
            System.out.println("Here...");
            Container c = getParent();
            while (c != null && !(c instanceof Frame)) c = c.getParent();

            Frame f = null;
            if (c instanceof Frame) f = (Frame)c;
            cdialog = new ColorDialog(f,"Colors...");
        }
    }

        /**
         * Component Shown Event Handler.
         */
    public void componentShown(ComponentEvent e)
    {
        if (e.getSource() == this && cdialog == null)
        {
            verifyDialogs();
        }
    }
}
