
package com.sri.apps.netsim;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.tabs.*;
import com.sri.gui.ext.*;
import com.sri.gui.ext.drawing.*;
import com.sri.gui.ext.drawing.selectors.*;
import com.sri.gui.ext.dialogs.*;
import java.awt.image.*;
import com.sri.utils.*;
import com.sri.utils.adt.*;
import java.io.*;
import java.net.*;

/**
 * Popup menu manager for network elements.
 */
public class NetworkPopupManager extends PopupManager 
{
        /**
         * Constructor.
         */
    public NetworkPopupManager(SceneViewer sViewer)
    {
        super (sViewer, "Network Menu...");
    }

        /**
         * Action Event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
    }

        /**
         * Item Event handler.
         */
    public void itemStateChanged(ItemEvent ie)
    {
        Object src = ie.getSource();
    }
}
