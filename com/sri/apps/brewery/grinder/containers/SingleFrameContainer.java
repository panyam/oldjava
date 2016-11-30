
package com.sri.apps.brewery.grinder.containers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.sri.apps.brewery.core.*;
import com.sri.apps.brewery.grinder.*;
import com.sri.apps.brewery.grinder.screens.*;

/**
 * The container where everything is in the one frame centered on the
 * screen.  Very similar to most windows installers.
 *
 * @author Sri Panyam
 */
public class SingleFrameContainer extends GUIGrinderContainer
                                  implements ActionListener
{
        /**
         * The initial dimensions of the frame.
         */
    protected int initialWidth = 600;
    protected int initialHeight = 400;

        /**
         * The window that shows the initial tasks and their progress.
         */
    protected JWindow startupProgressWindow = new JWindow();
    protected JProgressBar startupProgBar = new JProgressBar(0, 1000);
    protected JProgressBar startupWholeProgBar = new JProgressBar(0, 1000);
    protected JLabel startupTaskNameLabel = new JLabel("Task Name");

        /**
         * The main frame that will show all screens and buttons and so on.
         */
    protected JFrame mainFrame = null;

        /**
         * The panel to hold the buttons.
         */
    protected JPanel buttonPanel = new JPanel();

        /**
         * Panel to show the name of the current screen.
         */
    protected JPanel headerPanel = new JPanel(new GridLayout(1, 1));

        /**
         * Panel for the header.
         */
    protected JLabel headerLabel = new JLabel("Current Screen Header");

        /**
         * The panel that holds items in the middle.
         */
    protected JPanel centerPanel = new JPanel();

        /**
         * Update the task display of the given task.
         */
    public void showTaskStatus(GrinderTask currTask,
                               int taskIndex,
                               int taskCount,
                               boolean isStartupTask)
    {
        startupProgBar.setIndeterminate(false);
        startupWholeProgBar.setIndeterminate(false);
        if (currTask == null || taskIndex < 0 || taskCount <= 0)
        {
            startupProgressWindow.setVisible(false);
            return ;
        }

        startupWholeProgBar.setMinimum(0);
        startupWholeProgBar.setMaximum(taskCount);
        startupWholeProgBar.setValue(taskIndex);
        startupWholeProgBar.setString(
                "Total: " + ((taskIndex * 100) / taskCount) + "% complete.");

        int min = currTask.getMinimumValue();
        int max = currTask.getMaximumValue();
        int curr = currTask.getCurrentValue();
        double pcent = currTask.getPercentComplete();
        String msg = currTask.getMessage();
        int status = currTask.getTaskStatus();

        startupProgBar.setIndeterminate(pcent < 0);

        startupProgBar.setMinimum(min);
        startupProgBar.setMaximum(max);
        startupProgBar.setValue(curr);
        startupProgBar.setString(msg);

        startupTaskNameLabel.setText(currTask.getName());

        startupProgressWindow.setVisible(true);
    }

        /**
         * Sets the current screen.
         */
    public void showScreen(GrinderScreen screen)
    {
        if ( ! (screen instanceof GUIScreen))
            throw new IllegalArgumentException("This container can only accept GUIScreen instances.");

            // initialise display if not...
        if (mainFrame == null) 
        {
            initialiseComponents();
        }

        GUIScreen guiScreen = (GUIScreen)screen;

        Dimension prefSize = guiScreen.getPreferredSize();
        Dimension centerSize = centerPanel.getPreferredSize();

        centerPanel.add("Center", guiScreen);

        headerLabel.setText(guiScreen.getTitle());

            // resize frame if necessary...
        if (prefSize.height > centerSize.height ||
            prefSize.width > centerSize.width) 
        {
            mainFrame.pack();
        }

            // set the initial size...
        mainFrame.setVisible(true);
        mainFrame.toFront();
    }

        /**
         * Initialise the components.
         */
    protected void initialiseComponents()
    {
        buttonPanel.add(nextButton = new JButton("Next"));
        buttonPanel.add(previousButton = new JButton("Previous"));
        buttonPanel.add(cancelButton = new JButton("Cancel"));
        buttonPanel.add(finishButton = new JButton("Finish"));

            // set out the header label info
        Font hdrFont = headerLabel.getFont();
        int hdrFontSize = hdrFont.getSize();
        headerLabel.setFont(new Font(hdrFont.getFontName(),
                            Font.BOLD, hdrFontSize + 10));
        headerPanel.add(headerLabel);

        String frameTitle = gData.projectName + " Installer...";
        mainFrame = new JFrame(frameTitle);
        mainFrame.setLayout(new BorderLayout());

        mainFrame.add("North", headerPanel);
        mainFrame.add("Center", centerPanel);
        mainFrame.add("South", buttonPanel);
        mainFrame.setSize(initialWidth, initialHeight);
            // center the window.
        BreweryUtils.alignComponent(mainFrame, 0, 0);

            // configure the progress window items
        Container contPanel = startupProgressWindow.getContentPane();
        contPanel.setLayout(new BorderLayout());
        contPanel.add(startupTaskNameLabel);
        contPanel.add(startupProgBar);
        contPanel.add(startupWholeProgBar);
        startupProgressWindow.setVisible(false);
        startupProgressWindow.setSize(600, 60);
        BreweryUtils.alignComponent(startupProgressWindow, 0, 0);

            // also set the look and feel to the system one...
        try
        {
            UIManager.setLookAndFeel(
                (LookAndFeel)
                Class.forName(
                    UIManager.getSystemLookAndFeelClassName()).
                        newInstance());
        } catch (Exception insExc)
        {
            insExc.printStackTrace();
        }
        SwingUtilities.updateComponentTreeUI(mainFrame);

            // now set the listeners...
        ((JButton)nextButton).addActionListener(this);
        ((JButton)previousButton).addActionListener(this);
        ((JButton)cancelButton).addActionListener(this);
        ((JButton)finishButton).addActionListener(this);
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent e)
    {
        Object src = e.getSource();

        if (src == nextButton)
        {
            gParent.goForward();
        } else if (src == previousButton)
        {
            gParent.goBackward();
        } else if (src == cancelButton)
        {
            gParent.exitInstallation();
        } else if (src == finishButton)
        {
            gParent.finishInstallation();
        }
    }
}
