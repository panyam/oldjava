package svtool.gui.dialogs;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;
import svtool.gui.anim.*;

import java.awt.*;
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
public class AboutDialog extends OkDialog
{
    String lines[] = new String[]
    {
        " Developed for Telstra by:",
        "   Sriram Panyam", 
        "   Ph: 0409 019 095",
        "   Email: sriram.panyam@team.telstra.com",
        "   ",
        " This program can be used in anyway as ",
        " may be seemed fit by Telstra.  The author",
        " however takes no responsibility for any ",
        " outcomes that may arise as a result of",
        " using this application.  Nor does the ",
        " author guarantee the validity of this",
        " program.",
        "   ",
        " Special thanks to: ",
        "   Andrew Schwarz, ",
        "   Tony Lees and ",
        "   Rocky Pereira ",
        "   Steve Cherry ",
        "   ",
        " for helping with the testing of ",
        " this tool."
    };

    AnimationPanel animPanel = null;
    AnimationLayer background = new HMStars(200);
    AnimationLayer foreground = new VTextForeground(lines);

        /**
         * Connect to a database.
         */
    public AboutDialog(JFrame parent, String title, long animDelay)
    {
        super(parent, title, true);
        animPanel = new AnimationPanel(animDelay);
        getContentPane().add("Center", animPanel);

        animPanel.addLayer(background);
        animPanel.addLayer(foreground);
        pack();
    }

        /**
         * Override setVisible function to start and stop animation.
         */
    public void setVisible(boolean vis)
    {
        if (vis)
        {
            animPanel.startAnimation();
        } else
        {
            animPanel.stopAnimation();
        }
        super.setVisible(vis);
    }
}
