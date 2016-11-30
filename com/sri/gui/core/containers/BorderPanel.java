package com.sri.gui.core.containers;
import java.awt.*;
import com.sri.gui.core.*;

public class BorderPanel extends SContainer
{
    public static final int SOLID = 0;
    public static final int RAISED = 1;
    public static final int LOWERED = 2;
    public static final int IN = 3;
    public static final int OUT = 4;

    protected int m_iBorderType = RAISED;
    protected int m_iBorderThickness = 2;

    public BorderPanel(int iBorderType, int iBorderThickness)
	{
    	m_iBorderType = iBorderType;
	    m_iBorderThickness = iBorderThickness;
	}

    public BorderPanel(int iBorderType)
	{
	    m_iBorderType = iBorderType;
	}
    
    public BorderPanel()
	{
	}

    // Make room for the border
    public Insets getInsets()
    {
        return new Insets(m_iBorderThickness, m_iBorderThickness,
            m_iBorderThickness, m_iBorderThickness);
    }

    // Paint the border.
    public void paint(Graphics g)
	{
    	Dimension d = getSize();
	    g.setColor(getBackground());
    	switch (m_iBorderType)
	    {
	        case SOLID:
	            g.setColor(getForeground());
	            for (int i = 0; i < m_iBorderThickness; ++i)
		            g.drawRect(i, i, d.width - i * 2 - 1, d.height - i * 2 - 1);
	            break;

	        case RAISED:
	            for (int i = 0; i < m_iBorderThickness; ++i)
    		        g.draw3DRect(i, i, d.width - i * 2 - 1, d.height - i * 2 - 1, true);
	            break;

	        case LOWERED:
	            for (int i = 0; i < m_iBorderThickness; ++i)
    		        g.draw3DRect(i, i, d.width - i * 2 - 1, d.height - i * 2 - 1, false);
	            break;

	        case IN:
	            g.draw3DRect(0, 0, d.width - 1, d.height - 1, false);
	            g.draw3DRect(m_iBorderThickness - 1, m_iBorderThickness - 1,
		        d.width - m_iBorderThickness * 2 + 1, d.height - m_iBorderThickness * 2 + 1, true);
	            break;

	        case OUT:
	            g.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
	            g.draw3DRect(m_iBorderThickness - 1, m_iBorderThickness - 1,
		        d.width - m_iBorderThickness * 2 + 1, d.height - m_iBorderThickness * 2 + 1, false);
	            break;
	    }
		super.paint(g);
	}

}
