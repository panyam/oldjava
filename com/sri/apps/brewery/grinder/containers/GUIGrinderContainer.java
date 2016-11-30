

package com.sri.apps.brewery.grinder.containers;

import javax.swing.*;
import java.util.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.grinder.*;

/**
 * Super class of all GUI based grinder containers.  At the very least,
 * they have a component that will show the next, previous, cancel and
 * finish buttons.
 *
 * @author Sri Panyam
 */
public abstract class GUIGrinderContainer implements GrinderContainer
{
        /**
         * The parent grinder object.
         */
    protected Grinder gParent;

        /**
         * The actualy model or the grinder data.
         */
    protected Blend gData;

        /**
         * The next button.
         */
    protected JComponent nextButton;

        /**
         * The previous button.
         */
    protected JComponent previousButton;

        /**
         * The cancel button.
         */
    protected JComponent cancelButton;

        /**
         * The finish button.
         */
    protected JComponent finishButton;

        /**
         * Sets the grinder parent and the data model.
         *
         * All actions, that require the grinder parent, before this
         * function is called may have undefined results.
         */
    public void setEnvironment(Grinder parent, Blend gData)
    {
        this.gParent = parent;
        this.gData = gData;
    }

        /**
         * Tells if the "next" option should be enabled.
         */
    public void setNextEnabled(boolean en)
    {
        nextButton.setEnabled(en);
    }

        /**
         * Tells if the "previous" option should be enabled.
         */
    public void setPreviousEnabled(boolean en)
    {
        previousButton.setEnabled(en);
    }

        /**
         * Tells if the "cancel/quit" option should be enabled.
         */
    public void setCancelEnabled(boolean en)
    {
        cancelButton.setEnabled(en);
    }

        /**
         * Tells if the "finish" option should be enabled.
         */
    public void setFinishEnabled(boolean en)
    {
        finishButton.setEnabled(en);
    }

        /**
         * Tells if the "next" option should be visible.
         */
    public void setNextVisible(boolean vis)
    {
        nextButton.setVisible(vis);
    }

        /**
         * Tells if the "previous" option should be visible.
         */
    public void setPreviousVisible(boolean vis)
    {
        previousButton.setVisible(vis);
    }

        /**
         * Tells if the "cancel/quit" option should be visible.
         */
    public void setCancelVisible(boolean vis)
    {
        cancelButton.setVisible(vis);
    }

        /**
         * Tells if the "finish" option should be visible.
         */
    public void setFinishVisible(boolean vis)
    {
        finishButton.setVisible(vis);
    }
}
