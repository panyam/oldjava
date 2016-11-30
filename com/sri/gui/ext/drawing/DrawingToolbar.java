package com.sri.gui.ext.drawing;

import java.awt.*;
import java.awt.event.*;
import com.sri.gui.core.button.*;
import com.sri.gui.core.containers.*;
import com.sri.gui.core.containers.tabs.*;
import com.sri.gui.ext.*;
import com.sri.gui.ext.dialogs.*;
import java.awt.image.*;

    /**
     * This is the drawing toolbar.
     */
public class DrawingToolbar extends Container implements MouseListener, InfoListener
{
    Label info = new Label("");
    protected TabPanel tabPanel = new TabPanel(false);
    protected ButtonPanel buttonPanel = new ButtonPanel();
    protected SceneViewer sceneViewer = null;

        /**
         * The buttons and stencil for identifitying each stencil.
         */
    protected SActionButton stencilButtons[] = null;
    protected int nStencils = 0;
    protected Stencil stencils[] = null;

        /**
         * Constructor.
         */
    public DrawingToolbar(SceneViewer scene) {
        this.sceneViewer = scene;

        setBackground(Color.white.darker());
        setLayout(new BorderLayout());

        tabPanel.setLayout(new VerticalTabLayout(VerticalTabLayout.RIGHT));

        add("Center",tabPanel);
        add("South",info);

        validate();
    }

        /**
         * Add a new stencil to the list.
         */
    public void addStencil(SActionButton button, Stencil stencil)
    {
        if (stencilButtons == null)
        {
            stencilButtons = new SActionButton[3];
            stencils = new Stencil[3];
        }
        if (stencilButtons.length <= nStencils)
        {
            SActionButton sb2[] = new SActionButton[nStencils + 3];
            Stencil sp2[] = new Stencil[nStencils + 3];
            System.arraycopy(stencilButtons, 0, sb2, 0, nStencils);
            System.arraycopy(stencils, 0, sp2, 0, nStencils);
            stencils = sp2;
            stencilButtons = sb2;
        }
        stencils[nStencils] = stencil;
        stencilButtons[nStencils++] = button;
        tabPanel.add(stencil, button);
        validate();
        invalidate();
    }

    public void mouseClicked(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) {
        setInfo("");
    }

    public void setInfo(String t)
    {
        info.setText(t);
    }
}
