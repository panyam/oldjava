package svtool.gui.anim;

import java.awt.*;

public class HMStars implements AnimationLayer
{
        /**
         * The starColors to use for the stars.
         */
    protected final static Color starColors[] =
    {
        new Color(128, 128, 128),
        new Color(192, 192, 192),
        new Color(255, 255, 255),
    };
    protected int x[], y[], xv[], xi[];
    protected int currWidth = -1, currHeight = -1;
    protected Color c[];

    protected int numStars;

    public HMStars(int ns)
    {
	    x = y = xv = xi = null;
	    c = null;
        setNumStars(ns);
    }

    /**
     * Sets the number of stars.
     */
    public void setNumStars(int nStars)
    {
        this.numStars = nStars;
        if (nStars < 0) nStars = 200;

        if (x == null || x.length < numStars) x  = new int[numStars];
        if (y == null || y.length < numStars) y  = new int[numStars]; 
        if (xv == null || xv.length < numStars) xv = new int[numStars];
        if (xi == null || xi.length < numStars) xi = new int[numStars];
        if (c == null || c.length < numStars) c  = new Color[numStars];

        currWidth = -1;
        currHeight = -1;
    }

    public void createStars(int width, int height)
    {
        int t;
        currWidth = width;
        currHeight = height;

        for(int i=0; i < numStars; i++)
        {
            x[i] = (int)(Math.random() * width);
            y[i] = (int)(Math.random() * height);
            xi[i] = x[i];

            t = (int)(Math.random() * 3);
            //while((t = random() % 3) < 0 || t > 2) {}
              
            xv[i] = (t + 1) * 2;
            c[i] = starColors[t];
        }
    }

        /**
         * Move the stars backward by a number of steps.
         */
    public void backward(int numSteps)
    {
        int t, i;
        for (i=0; i < numStars; i++)
        {
            x[i] -= (numSteps * xv[i]);
            while (x[i] < 0) x[i] += currWidth;
        }
    }

        /**
         * Move the stars forward number of steps.
         */
    public void forward(int numSteps)
    {
        int t, i;
        if (currWidth <= 0) return ;
        for (i=0; i < numStars; i++)
        {
            //x[i] += (numSteps * xv[i]);
            //while (x[i] >= currWidth) x[i] -= currWidth;

            if ((x[i] += xv[i]) >= currWidth) {
                x[i] = 0;
                t = (int)(Math.random() * 3);

                xv[i] = (t + 1) * 2;
                c[i] = starColors[t];
            }
        }
    }
        /**
         * Paint the stars.
         */
    public void draw(Graphics g, Dimension d)
    {
        int i, t;
        int width = d.width;
        int height = d.height;
        /*CString str;
        str.Format("%s, %s", width, height);
        AfxMessageBox(str);*/

        if (width != currWidth || currHeight != height)
        {
            createStars(width, height);
        }

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        // Unrolled loop to speed up pixel plotting
        for(i=0;i<numStars - 4;i++)
        {
            g.setColor(c[i]);
            g.drawLine(x[i], y[i], x[i], y[i]);

            g.setColor(c[++i]);
            g.drawLine(x[i], y[i], x[i], y[i]);

            g.setColor(c[++i]);
            g.drawLine(x[i], y[i], x[i], y[i]);

            g.setColor(c[++i]);
            g.drawLine(x[i], y[i], x[i], y[i]);
        }
    }
}
