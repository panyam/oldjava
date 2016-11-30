package svtool.gui.anim;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

/**
 * A dialog box for showing the "about" information.
 */

public class AnimationPanel extends Container implements Runnable
{
        /**
         * When waiting for thread state change, wait this long - in ms.
         */
    protected final static long THREAD_WAIT_TIME = 100;

        /**
         * How fast is the animation?
         */
    protected long sleepTime;

        /**
         * Thread that does animations.
         */
    protected Thread ourThread;

        /**
         * When a thread is running.
         */
    protected boolean threadRunning = false;

        /**
         * When a thread is requested to stop.
         */
    protected boolean threadStopped = false;

        /**
         * Offscreen image buffImage.
         */
    protected Image buffImage = null;

        /**
         * Graphics object of the buffImage.
         */
    protected Graphics buffGraphics = null;

        /**
         * Buffer's size.
         */
    protected Dimension buffSize = new Dimension();

    protected Vector layers = new Vector(2);

        /**
         * Constructor.
         */
    public AnimationPanel(long sleepTime)
    {
        super();
        this.sleepTime = sleepTime;
        setBackground(Color.white);
        setForeground(Color.black);
        setLayout(null);
    }

        /**
         * Add a new layer here.
         */
    public void addLayer(AnimationLayer layer)
    {
        layers.addElement(layer);
    }

        /**
         * Paint method.
         */
    public synchronized void paint(Graphics g)
    {
        Dimension d = getSize();

        if (buffImage == null ||
                buffSize.width < d.width ||
                    buffSize.height < d.height)
        {
            if (buffImage != null) buffImage.flush();
            if (buffGraphics != null) buffGraphics.dispose();
            buffSize.width = Math.max(1, d.width);
            buffSize.height = Math.max(1, d.height);
            buffImage = createImage(buffSize.width, buffSize.height);
            if (buffImage == null) return ;
            buffGraphics = buffImage.getGraphics();
            System.gc();
        }

        if (buffImage == null || buffGraphics == null) return ;

        int nLayers = layers.size();

            // forward each layer by one unit...
        for (int i = 0;i < nLayers;i++)
        {
            ((AnimationLayer)layers.elementAt(i)).draw(buffGraphics, buffSize);
        }

        buffGraphics.setColor(Color.black);
        buffGraphics.drawRect(0,0,buffSize.width - 1,buffSize.height - 1);
        if (buffImage != null) g.drawImage(buffImage,0,0,this);
    }

        /**
         * Starts the animation.
         */
    public synchronized void startAnimation()
    {
        if (threadRunning && !threadStopped) return ;

            // if the thread has been stopped then
            // wait for it to stop before creating
            // a new thread
        if (threadStopped)
        {
            while (threadRunning)
            {
                try
                {
                    wait(THREAD_WAIT_TIME);
                } catch (Exception exc)
                {
                }
            }
        }

        threadStopped = false;
        threadRunning = true;
        ourThread = new Thread(this);
        ourThread.start();
    }

        /**
         * Stops the animation.
         */
    public synchronized void stopAnimation()
    {
        if (!threadRunning) return ;

        threadStopped = true;
        notifyAll();

            // while thread is still running, wait
        while (threadRunning)
        {
            try
            {
                wait(THREAD_WAIT_TIME);
            } catch (Exception exc)
            {
            }
        }

        threadRunning = false;
        threadStopped = false;
    }

        /**
         * Main run method.
         */
    public void run()
    {
        int nLayers;
        threadRunning = true;
        threadStopped = false;
        while (threadRunning && !threadStopped)
        {
            nLayers = layers.size();

                // forward each layer by one unit...
            for (int i = 0;i < nLayers;i++)
            {
                ((AnimationLayer)layers.elementAt(i)).forward(1);
            }

            try
            {
                Thread.sleep(sleepTime);
            } catch (Exception exc)
            {
            }

            paint(getGraphics());
        }
        threadRunning = threadStopped = false;
    }

    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d.height = 240;
        d.width = 320;
        return d;
    }
}
