
package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.tabs.*;
import com.sri.gui.ext.*;
import java.awt.image.*;
import com.sri.utils.*;
import com.sri.utils.adt.*;
import java.io.*;
import java.net.*;

/**
 * A popup manager object for taking care of right clicking
 * on shapes.
 */
public class PopupManager implements ActionListener, ItemListener
{
    protected Menu preferencesMenu = new Menu("Preferences");
    public CheckboxMenuItem showGridMI =
                    new CheckboxMenuItem("Show Grid", true);

    protected Menu objectMenu = new Menu("Object");
    protected MenuItem propertiesMI =
                    new MenuItem ("Properties...");
    protected MenuItem deleteObjectMI =
                    new MenuItem("Delete Object");
    protected MenuItem sendToBackMI =
                    new MenuItem("Send to Back");
    protected MenuItem bringToFrontMI =
                    new MenuItem("Bring to Front");

        /**
         * The scene element on which the popup menu was invoked.
         */
    protected SceneElement menuedElement = null;

    protected PopupMenu popupMenu = null;

        /**
         * The scene viewer object.
         */
    protected SceneViewer sceneViewer = null;

        /**
         * Constructor.
         */
    public PopupManager(SceneViewer sViewer)
    {
        this(sViewer, "");
    }

        /**
         * Constructor.
         */
    public PopupManager(SceneViewer sViewer, String title)
    {
        this.sceneViewer = sViewer;
        popupMenu = new PopupMenu(title);

            // create the menu here..
        preferencesMenu.add(showGridMI);

        objectMenu.add(deleteObjectMI);
        objectMenu.add(sendToBackMI);
        objectMenu.add(bringToFrontMI);

        popupMenu.add(objectMenu);
        popupMenu.add(preferencesMenu);

        showGridMI.addItemListener(this);

        deleteObjectMI.addActionListener(this);
        sendToBackMI.addActionListener(this);
        bringToFrontMI.addActionListener(this);
        propertiesMI.addActionListener(this);
    }

        /**
         * Handles popup showing for a scene viewer object.
         */
    public void handlePopup(MouseEvent me,
                            int mX, int mY,
                            SceneViewer sViewer)
    {
        this.sceneViewer = sViewer;

        menuedElement = sViewer.getElementAt(mX, mY);
        objectMenu.setEnabled(menuedElement != null);

        sceneViewer.remove(popupMenu);
        sceneViewer.add(popupMenu);

            /**
             * Organise the menu based on 
             * what needs to be shown
             */
        reorganiseMenu();

        popupMenu.show(sViewer, me.getX(), me.getY());
    }

        /**
         * Action Event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
        if (src == sendToBackMI)
        {
            if (menuedElement instanceof Shape)
            {
                sceneViewer.sendToBack((Shape)menuedElement);
            }
        } else if (src == bringToFrontMI)
        {
            if (menuedElement instanceof Shape)
            {
                sceneViewer.bringToFront((Shape)menuedElement);
            }
        } else if (src == deleteObjectMI)
        {
            //sceneViewer.deleteShape(menuedElement);
        }
    }

        /**
         * Item Event handler.
         */
    public void itemStateChanged(ItemEvent ie)
    {
        Object src = ie.getSource();
        if (src == showGridMI)
        {
            sceneViewer.showGrid(showGridMI.getState());
        }
    }

        /**
         * Reorganises the menu depending on the object
         * that is currently under focus.
         */
    protected void reorganiseMenu()
    {
    }
}
