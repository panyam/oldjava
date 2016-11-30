package svtool.gui.dialogs;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;


/**
 * A dialog box that acts as an ok cancel box.
 */
public class OkDialog extends JDialog implements ActionListener
{
    JButton okButton = new JButton("OK");
    Panel buttonPanel = new Panel();

        /**
         * Constructor.
         */
    public OkDialog(JFrame parent, String title, boolean modal)
    {
        super(parent, title, modal);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("South", buttonPanel);

        addButton(okButton);
    }

        /**
         * Add a new component in the button panel.
         */
    public void addButton(JButton comp)
    {
        buttonPanel.add(comp);
        comp.addActionListener(this);
    }

        /**
         * Action Event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == okButton)
        {
            setVisible(false);
        }
    }

        /**
         * Hides or shows the dialog box.
         */
    public void setVisible(boolean vis)
    {
        if (vis)
        {
            Dimension ts = getSize();
            Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

            setLocation((ss.width - ts.width) / 2,
                        (ss.height - ts.height) / 2);
        }

        //setLocationRelativeTo(getParent());
        super.setVisible(vis);
    }
}
