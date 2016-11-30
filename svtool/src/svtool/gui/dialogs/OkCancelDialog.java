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
public class OkCancelDialog extends OkDialog
{
    boolean wasCancelled = false;
    JButton cancelButton = new JButton("Cancel");

        /**
         * Constructor.
         */
    public OkCancelDialog(JFrame parent, String title, boolean modal)
    {
        super(parent, title, modal);
        addButton(cancelButton);
    }

        /**
         * Tells if the dialog was cancelled or not.
         */
    public boolean wasCancelled()
    {
        return wasCancelled;
    }

        /**
         * Action Event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == okButton)
        {
            wasCancelled = false;
            setVisible(false);
        } else if (ae.getSource() == cancelButton)
        {
            wasCancelled = true;
            setVisible(false);
        }
    }

        /**
         * Hides or shows the dialog box.
         */
    public void setVisible(boolean vis)
    {
        if (vis) wasCancelled = true;

        super.setVisible(vis);
    }

}
