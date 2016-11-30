package svtool.gui.anim;

import java.awt.*;

/**
 * The layer that does vertically moving text.
 */
public class VTextForeground implements AnimationLayer
{
    protected int nLines;
    protected String lines[];
    protected int maxStringSize;
    protected int maxStringIndex;
    protected int x, y;

        /**
         * Constructor.
         */
    public VTextForeground()
    {
        this(null);
    }

        /**
         * Constructor.
         */
    public VTextForeground(String lines[])
    {
        setLines(lines);
    }

        /**
         * Move the text backward by a number of steps.
         */
    public void backward(int numSteps)
    {
        y += numSteps;
    }

        /**
         * Move the text forward number of steps.
         */
    public void forward(int numSteps)
    {
        y -= numSteps;
    }

        /**
         * Paint the text.
         */
    public void draw(Graphics g, Dimension d)
    {
        if (maxStringIndex < 0) return ;

        int ypos = y;

        FontMetrics fm = g.getFontMetrics();
        int maxWidth = fm.stringWidth(lines[maxStringIndex]);
        int Asc = fm.getMaxAscent();
        g.setColor(Color.white);
        if (y <= (-1 * ((nLines - 1) * Asc))) y = d.height + Asc;

        x = (d.width - maxWidth)/2;

        for (int i = 0;i < nLines;i++)
        { 
            g.drawString(lines[i], x + 5, ypos);
            ypos += Asc;
        }
    }

        /**
         * Sets the lines that need to be scrolled.
         */
    public void setLines(String lines[])
    {
        int currLen = 0;
        this.nLines = lines == null ? 0 : lines.length;
        this.lines = new String[nLines];
        maxStringIndex = 0;
        maxStringSize = 0;
        y = (nLines * -100);
        for (int i = 0;i < nLines;i++)
        {
            this.lines[i] = lines[i];
            currLen = lines[i].length();
            if (currLen > maxStringSize)
            {
                maxStringSize = currLen;
                maxStringIndex = i;
            }
        }
    }
}
