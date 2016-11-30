
package svtool.gui;

import svtool.*;
import svtool.data.*;
import svtool.data.models.*;
import svtool.data.populators.*;
import svtool.core.*;
import svtool.core.adt.*;
import svtool.gui.dialogs.*;
import svtool.gui.views.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import java.sql.*; 
import java.io.*; 
import java.util.*; 
//import oracle.jdbc.driver.*;
import java.math.*; 

/**
 * Mediator intarface for all the GUI components in the App Frame.
 *
 * @author Sri Panyam
 */
public interface AppMediator
{
        /**
         * Indicates a change the view to be displayed.
         */
    public void selectView(DBView view);

        /**
         * Tells that a view has changed to it needs to be handled
         * accordingly.
         */
    public void viewChanged(DBView view);
}
