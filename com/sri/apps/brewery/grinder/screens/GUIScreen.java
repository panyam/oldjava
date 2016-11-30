
package com.sri.apps.brewery.grinder.screens;

import java.util.*;
import javax.swing.*;
import java.io.*;
import com.sri.apps.brewery.grinder.*;
import com.sri.apps.brewery.io.*;

/**
 * Super class of all GUI based GrinderScreens
 *
 * @author Sri Panyam
 */
public abstract class GUIScreen extends JPanel implements GrinderScreen
{
        /**
         * Every screen is assigned an ID
         * which is unique to the screen.
         */
    //public String getScreenName();

        /**
         * The grinder parent.
         */
    protected Grinder gParent;

        /**
         * A reference to a screen implementation.
         */
    //protected GrinderScreenImpl gScreenImpl;

        /**
         * The engine that will read and write the screen
         * details to the input and output stream.
         */
    //protected Persistor screenPersistor;

        /**
         * Called when this state is being left.
         *
         * This will perform the actions required of this screen when the
         * "previous" button is pressed.
         *
         * The return value is whether it is possible to exit from this
         * screen or not.
         */
    public boolean onBackward(GrinderScreen to)
    {
        return true;
    }

        /**
         * Called when this state is being left.
         *
         * This will perform the actions required of this screen when the
         * "cancel" button is pressed.
         */
    public Object onCancel()
    {
        return null;
    }

        /**
         * Called when this state is being left.
         *
         * This will perform the actions required of this screen when the
         * "next" button is pressed.
         * This also includes the case if the "finish" button was pressed.
         *
         * The return value is whether it is possible to exit from this
         * screen or not.
         */
    public boolean onForward(GrinderScreen to)
    {
        return true;
    }

        /**
         * Sets the Grinder that will coordinate and control
         * the screens.
         */
    public void setEnvironment(Grinder grinder)
    {
        this.gParent = grinder;
    }
}
