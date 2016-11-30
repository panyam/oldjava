

package com.sri.apps.vocab;

import com.sri.apps.vocab.filters.*;
import com.sri.apps.vocab.orderers.*;
import com.sri.gui.core.*;
import com.sri.gui.core.containers.*;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;
import java.applet.*;

/**
 * The main applet that runs vocab builder.
 */
public class VocabApplet extends Applet
                         implements
                            ActionListener,
                            //MouseListener,
                            ItemListener
{
        /**
         * The master word list.
         */
    protected WordList masterList = new WordList(3000);

        /**
         * Temporary word list.
         */
    protected WordList tempList = (WordList)masterList.clone();

        /**
         * The list where masterList is cloned and used by filtering out
         * words and so on.
         */
    protected WordList filteredList = (WordList)masterList.clone();

        /**
         * The final list to be used after all the filters, arrangements
         * and word counts have been set.
         */
    protected WordList finalList = (WordList)masterList.clone();

        /**
         * Number of options to display in the multiple choice.
         */
    protected final static int NUM_OPTIONS = 5;

        /**
         * Given a filter class name, returns the appropriate class and
         * editor information.
         */
    protected Hashtable filterTable = new Hashtable();

        /**
         * Table of word orderer/arranger objects.
         */
    protected Hashtable ordererTable = new Hashtable();

        /**
         * A popup menu that shows which filters to add.
         */
    protected PopupMenu addFiltersPopup = new PopupMenu("Add Filter");

        /**
         * Panel to edit current filters in used.
         */
    protected FilterPanel filterInfoPanel = null;

        // indicates change in filter info options
    protected boolean filterOptionsChanged = true;

        /**
         * Panel for doing the search.
         */
    protected SearchPanel searchPanel = null;

        /**
         * Panel taht allows selection of words
         */
    protected QuizPanel quizPanel = null;

        /**
         * Panel for selecting word count and order options.
         */
    protected WordOptionsPanel wordOptionsPanel = null;

        // indicates change in word options...
    protected boolean wordOptionsChanged = true;

        /**
         * List of available filters in a combo box.
         */
    protected Choice addFilterChoice = new Choice();

        /**
         * Cardlayout for choosing the right editor for filters.
         */
    protected CardLayout editorChooser = new CardLayout();
    protected Panel filterEditorCards = new Panel(editorChooser);

        /**
         * Initialises the applet.
         */
    public void init()
    {
        init(false);
    }

        /**
         * Initialise the components.
         */
    protected void initComponents()
    {
        filterInfoPanel = new FilterPanel();
        searchPanel = new SearchPanel();
        quizPanel = new QuizPanel();
        wordOptionsPanel = new WordOptionsPanel();
    }

        /**
         * Initialise the applet.
         */
    protected void init(boolean asApplication)
    {
        if (asApplication)
        {

        } else
        {
            readAppletParameters();
        }

        initComponents();
        layComponents();
    }

        /**
         * Updates the word order and the count.
         */
    public void updateWordOrderAndCount()
    {
        if (wordOptionsChanged)
        {
            Orderer ord = wordOptionsPanel.getOrderer();
            ord.arrangeList(filteredList, tempList);
            int wordCount = wordOptionsPanel.getWordCount();
            if (wordCount > 0)
            {
                tempList.getSubset(0, wordCount - 1, finalList);
            } else
            {
                tempList.copyTo(finalList);
            }
        }
        wordOptionsChanged = false;
    }

        /**
         * Recalculates the wordlist with the new set of filters and word
         * counts and ordering info.
         */
    public void updateFilteredList()
    {
        if (filterOptionsChanged)
        {
            Vector fList = filterInfoPanel.getFilterList();
            masterList.getFilteredWords(fList, filteredList);
            filteredList.copyTo(finalList);
            wordOptionsChanged = true;
        }
        filterOptionsChanged = false;
    }

        /**
         * Read parameters from the applets param tags.
         */
    protected void readAppletParameters()
    {
        int numFilters = 0;
        int numOrderers = 0;
        
        try
        {
            numFilters = Integer.parseInt(getParameter("numfilters"));
            numOrderers = Integer.parseInt(getParameter("numorderers"));
        } catch (Exception exc)
        {
            exc.printStackTrace();
        }

        addFilterChoice.add("Add New filter");
        addFilterChoice.add("-----------");

            // read the word lists...
        String wordListFile = getParameter("wordlist");
        readWordList(wordListFile);

            // read info about the word list orderers...
        for (int i = 0;i < numOrderers;i++)
        {
            String ordName = getParameter("orderer" + i + "_name");
            String ordClassName 
                    = getParameter("orderer" + i + "_class");
            try
            {
                Class ordClass = Class.forName(ordClassName);
                Orderer orderer = (Orderer)ordClass.newInstance();
                ordererTable.put(ordName, orderer);
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }

            // read info about the word filters...
        for (int i = 0;i < numFilters;i++)
        {
            String filterName = getParameter("filter" + i + "_name");
            String filterClassName 
                    = getParameter("filter" + i + "_class");
            String filterEditorClassName 
                    = getParameter("filter" + i + "_editor");

            try
            {
                Class filterClass = 
                    Class.forName(filterClassName);
                Class filterEditorClass = 
                    Class.forName(filterEditorClassName);
                FilterEditor fEditor = 
                    (FilterEditor)filterEditorClass.newInstance();

                FilterInfo filterInfo = new FilterInfo();
                filterInfo.filterName = filterName;
                filterInfo.filterClass = filterClass;
                filterInfo.filterEditor = fEditor;
                //availableFilterList.add(filterName);
                addFilterChoice.add(filterName);
                editorChooser.addLayoutComponent(filterName, fEditor);

                MenuItem filterMI = new MenuItem(filterName);
                filterMI.addActionListener(new AddFilterAction(filterInfo));

                filterTable.put(filterName, filterInfo);
            } catch (Exception exc)
            {
                exc.printStackTrace();
            }
        }
    }

        /**
         * Read the word list..
         *
         * At the moment the word list consists a bunch of 
         * <entry=xxx><meaning=xxxxx>entries...
         */
    protected void readWordList(String listFile)
    {
        StringBuffer tagBuffer = new StringBuffer("");

        try
        {
            URL url = new URL(getDocumentBase(), listFile);

            DataInputStream din = new DataInputStream(url.openStream());

            String line = din.readLine();

            while (line != null)
            {
                int sIndex = 0;
                int nChars = line.length();
                char ch;
spaceFindLoop:
                while (sIndex < nChars)
                {
                    ch = line.charAt(sIndex);
                    if (ch == ' ' || ch == '\t') break spaceFindLoop;
                    sIndex++;
                }

                String word = line.substring(0, sIndex).trim();
                String meaning = line.substring(sIndex).trim();

                if (word.length() == 0 || meaning.length() == 0)
                {
                    System.out.println("Problem: " + word + ", " + meaning);
                }
                masterList.addWord(new Word(word, meaning));
                //System.out.println("Read: " + word  + "|||" + meaning);
                line = din.readLine();
            }
        } catch (Exception exc)
        {
        }
    }

        /**
         * Lays out the components.
         */
    protected void layComponents()
    {
        setLayout(new BorderLayout());
        Panel southMainPanel = new Panel(new BorderLayout());
        Panel southPanel = new Panel(new GridLayout(1, 2));

        southPanel.add(searchPanel);
        southPanel.add(filterInfoPanel);

        southMainPanel.add("Center", southPanel);
        southMainPanel.add("South", wordOptionsPanel);

        add("South", southMainPanel);
        add("Center", quizPanel);
    }

        /**
         * Action event handler.
         */
    public void actionPerformed(ActionEvent ae)
    {
        Object src = ae.getSource();
    }

        /**
         * Item event handler.
         */
    public void itemStateChanged(ItemEvent ie)
    {
        Object src = ie.getSource();
    }

        /**
         * A class to close a frame.
         */
    public class WindowCloser extends WindowAdapter
    {
            /**
             * Close the window.
             */
        public void windowClosing(WindowEvent we)
        {
            Object src = we.getSource();
            if (src instanceof Window)
            {
                ((Window)src).setVisible(false);
            }
        }
    }

        /**
         * A filter for adding a new action to the list.
         */
    protected class AddFilterAction implements ActionListener
    {
        protected FilterInfo filterInfo;

            /**
             * Constructor.
             */
        public AddFilterAction(FilterInfo fInfo)
        {
            filterInfo = fInfo;
        }

            /**
             * Action event handler.
             */
        public void actionPerformed(ActionEvent ae)
        {
        }
    }

        /**
         * Panel for selection options on ordering and count of words to be
         * extracted.
         */
    class WordOptionsPanel extends EtchedPanel
                           implements ItemListener, KeyListener
    {
        protected Panel wordOrderPanel = new Panel();
        protected Panel wordCountPanel = new Panel();

            /**
             * To choose how the words are to be presented.
             */
        protected Choice wordOrderChoice = new Choice();

            /**
             * Tells how many items are required.
             */
        protected Choice wordCountChoice = new Choice();

            /**
             * Fields to enter the number of words to select.
             */
        protected TextField numWordsField= new TextField("50", 5);

            /**
             * Constructor.
             */
        public WordOptionsPanel()
        {
            super("Other Options: ", new GridLayout(1, 2));

            add(wordOrderPanel);
            add(wordCountPanel);

            wordOrderPanel.add(new Label("Word Order: "));
            wordOrderPanel.add(wordOrderChoice);

            wordCountPanel.add(new Label("Word Count: "));
            wordCountPanel.add(wordCountChoice);
            wordCountPanel.add(numWordsField);

            for (Enumeration keys = ordererTable.keys();
                    keys.hasMoreElements();)
            {
                String name = (String)keys.nextElement();
                wordOrderChoice.addItem(name);
            }

            wordCountChoice.addItem("All");
            wordCountChoice.addItem("Custom");

            numWordsField.setEnabled(false);

            wordOrderChoice.addItemListener(this);
            wordCountChoice.addItemListener(this);
            numWordsField.addKeyListener(this);
        }

            /**
             * Get the select wordlist orderer/arranger.
             */
        public Orderer getOrderer()
        {
            return (Orderer)ordererTable.get(
                        wordOrderChoice.getSelectedItem());
        }

            /**
             * Gets the list of filters to be applied.
             */
        public int getWordCount()
        {
            if (wordCountChoice.getSelectedIndex() == 0)
            {
                return -1;
            } else
            {
                try
                {
                    return Integer.parseInt(numWordsField.getText().trim());
                } catch (Exception exc)
                {
                    numWordsField.setText("50");
                }
            }
            return -1;
        }

            /**
             * Item event handler.
             */
        public void itemStateChanged(ItemEvent ie)
        {
            Object src = ie.getSource();

            if (src == wordOrderChoice)
            {
                wordOptionsChanged = true;
            } else if (src == wordCountChoice)
            {
                wordOptionsChanged = true;
                numWordsField.setEnabled(
                        ! wordCountChoice.getSelectedItem().equals("All"));
            }
        }

        public void keyPressed(KeyEvent ke) { }
        public void keyReleased(KeyEvent ke) { } 
        public void keyTyped(KeyEvent ke)
        {
            if (ke.getSource() == numWordsField)
            {
                wordOptionsChanged = true;
            }
        }
    }

        /**
         * Panel for selecting the filter and word iterator options.
         */
    class FilterPanel extends EtchedPanel
                    implements ActionListener, ItemListener, MouseListener
    {
        protected Panel filterCenterPanel = 
                    new Panel(new BorderLayout());
        protected Panel filterEditorPanel = 
                    new Panel(new BorderLayout());
        protected Checkbox invertCheckbox = 
                    new Checkbox("Invert Filter", false);
        protected Button updateFilterButton = new Button("Update Filter");

            /**
             * List of Filter objects being used.
             */
        protected Vector filterList = new Vector();

            /**
             * Buttons for adding, removing and clearing filters.
             */
        protected Button addFilterButton = new Button("Add");
        protected Button removeFilterButton = new Button("Remove");
        protected Button applyFiltersButton = new Button("Clear");
        protected Button clearFilterButton = new Button("Apply");

            // panel to hold the list and the buttons..
        protected Panel centerPanel = new Panel(new BorderLayout());
        protected Panel centerButtonPanel = new Panel(new BorderLayout());

            /**
             * Set of filters current used.
             */
        protected java.awt.List currFilterList = new java.awt.List();

            // set up the filter panel...
        public FilterPanel()
        {
            super("Filter Options");
            setLayout(new BorderLayout());

                // top panel for the count and order
            Panel northPanel = new Panel(new GridLayout(2, 1));

                // do the center panel
            Panel buttonPanel = new Panel(new GridLayout(4, 1));
            buttonPanel.add(addFilterChoice);
            buttonPanel.add(removeFilterButton);
            buttonPanel.add(applyFiltersButton);
            buttonPanel.add(clearFilterButton);
            centerButtonPanel.add("North", buttonPanel);
            centerPanel.add("Center", currFilterList);
            centerPanel.add("East", centerButtonPanel);
            currFilterList.add(addFiltersPopup);
            //currFilterList.addMouseListener(this);


                // center panel..
            filterEditorPanel.setVisible(false);
            filterEditorPanel.add("West", invertCheckbox);
            filterEditorPanel.add("Center", filterEditorCards);
            filterEditorPanel.add("East", updateFilterButton);

            add("North", northPanel);
            add("Center", centerPanel);
            add("South", filterEditorPanel);

                // set listeners
            currFilterList.addItemListener(this);
            clearFilterButton.addActionListener(this);
            applyFiltersButton.addActionListener(this);
            removeFilterButton.addActionListener(this);
            updateFilterButton.addActionListener(this);

            addFilterChoice.addItemListener(this);
        }

            /**
             * Gets the list of filters to be applied.
             */
        public Vector getFilterList()
        {
            return filterList;
        }

            /**
             * Overridden Mouse event handlers.
             */
        public void mouseEntered(MouseEvent me) { }
        public void mouseClicked(MouseEvent me) { }
        public void mouseExited(MouseEvent me) { }

            /**
             * When mouse released open the popup menu.
             */
        public void mouseReleased(MouseEvent me)
        {
            if (me.getSource() == addFilterButton)
            {
                //maybeShowPopupMenu(me);
            }
        }

            /**
             * When mouse pressed open the popup menu.
             */
        public void mousePressed(MouseEvent me)
        {
            if (me.getSource() == currFilterList)
            {
                maybeShowPopupMenu(me);
            }
        }

            /**
             * Show the popup if it has to be.
             */
        protected void maybeShowPopupMenu(MouseEvent me)
        {
            System.out.println("Showing: " + me);
            addFiltersPopup.show(me.getComponent(),
                                 me.getX(), me.getY());
        }

            /**
             * Item event handler.
             */
        public void itemStateChanged(ItemEvent ie)
        {
            Object src = ie.getSource();

            if (src == currFilterList)
            {
                selectFilter(currFilterList.getSelectedIndex());
            } else if (src == addFilterChoice)
            {
                String filterName = addFilterChoice.getSelectedItem();
                System.out.println("Selected: " + filterName);

                FilterInfo fInfo = (FilterInfo)filterTable.get(filterName);
                if (fInfo == null) return ;

                try
                {
                    WordFilter filter =
                        (WordFilter)fInfo.filterClass.newInstance();

                    currFilterList.add(fInfo.filterName);
                    filterList.addElement(filter);

                    filterOptionsChanged = true;

                    selectFilter(currFilterList.countItems() - 1);
                } catch (Exception exc)
                {
                    exc.printStackTrace();
                }
            }
        }

            /**
             * Action event handler.
             */
        public void actionPerformed(ActionEvent ae)
        {
            Object src = ae.getSource();

            if (src == applyFiltersButton)
            {
                updateFilteredList();
            } else if (src == clearFilterButton)
            {
                currFilterList.removeAll();
                invertCheckbox.setState(false);
                filterList.removeAllElements();
                filterOptionsChanged = true;
                editorChooser.show(filterEditorCards, "");
            } else if (src == removeFilterButton)
            {
                int selIndex = currFilterList.getSelectedIndex();
                if (selIndex < 0) return ;

                currFilterList.remove(selIndex);
                filterList.removeElementAt(selIndex);
                filterOptionsChanged = true;
                selectFilter(selIndex);
            }
        }

            /**
             * Selects the given filter.
             */
        protected void selectFilter(int index)
        {
            int fSize = filterList.size();

            filterEditorPanel.setVisible(false);

            if (index < 0 || index >= fSize) return ;

            System.out.println("Indx, fs = " + index + ", " + fSize);
            currFilterList.select(index);
            String filterName = currFilterList.getSelectedItem();
            FilterInfo filterInfo = (FilterInfo)filterTable.get(filterName);

            if (filterInfo == null) return ;

            filterInfo.filterEditor.setFilter
                    ((WordFilter)filterList.elementAt(index));

            filterEditorPanel.setVisible(true);
            editorChooser.show(filterEditorCards, filterName);
            //filterEditorPanel.invalidate();
            doLayout();
        }
    }
 
        /**
         * Panel for doing all the searches and all.
         */
    class SearchPanel extends EtchedPanel
                      implements
                        ActionListener,
                        ItemListener,
                        KeyListener
    {
        protected Checkbox filteredOnlyCheckbox =
                new Checkbox("Used filtered word list.", false);

        protected Checkbox useOrderAndCount =
                new Checkbox("Include word order and count.", false);

            // Panel to search for words
        protected Button searchButton = new Button("Search");
        protected TextField searchField = new TextField("Search Word...");
        protected TextArea searchedMeaning =
                new TextArea("", 3, 40, TextArea.SCROLLBARS_VERTICAL_ONLY);

        protected Button firstButton = new Button("First");
        protected Button nextButton = new Button("Next");
        protected Button prevButton = new Button("Previous");
        protected Button lastButton = new Button("Last");

        int searchedIndex = 0;

            // tells which list to use...
        WordList listToUse = masterList;

            /**
             * Constructor.
             */
        public SearchPanel()
        {
            super("Search", new BorderLayout());

            Panel topPanel = new Panel(new BorderLayout());
            Panel southPanel = new Panel(new GridLayout(3, 1));
            Panel buttonPanel = new Panel();
            buttonPanel.add(firstButton);
            buttonPanel.add(nextButton);
            buttonPanel.add(prevButton);
            buttonPanel.add(lastButton);

            southPanel.add(filteredOnlyCheckbox);
            southPanel.add(useOrderAndCount);
            southPanel.add(buttonPanel);

                // the search panel...
            topPanel.add("East", searchButton);
            topPanel.add("Center", searchField);
            topPanel.add("West", new Label("Search Word: "));
            add("North", topPanel);
            add("Center", searchedMeaning);
            //add("South", southPanel);
            add("South", buttonPanel);

            searchedMeaning.setEditable(false);

            filteredOnlyCheckbox.addItemListener(this);
            useOrderAndCount.addItemListener(this);
            nextButton.addActionListener(this);
            prevButton.addActionListener(this);
            lastButton.addActionListener(this);
            firstButton.addActionListener(this);

            searchButton.addActionListener(this);
            searchButton.setEnabled(true);
            searchedMeaning.addKeyListener(this);
        }

            /**
             * Item event handler.
             */
        public void itemStateChanged(ItemEvent ie)
        {
            Object src = ie.getSource();
            if (src == filteredOnlyCheckbox || src == useOrderAndCount)
            {
                searchedIndex = 0;
                setSearchWord(searchedIndex);
            }
        }

            /**
             * Handles an action event.
             */
        public void actionPerformed(ActionEvent ae)
        {
            Object src = ae.getSource();

            if (src == searchButton)
            {
                String word = searchField.getText().trim();

                selectList();

                int index = masterList.indexOf(word);

                if (index < 0)
                {
                    searchedMeaning.setText(word + " not found.");
                } else
                {
                    searchedIndex = index;
                    searchedMeaning.setText(masterList.getWord(index).meaning);
                }
            } else if (src == nextButton)
            {
                nextWord();
            } else if (src == lastButton)
            {
                lastWord();
            } else if (src == prevButton)
            {
                previousWord();
            } else if (src == firstButton)
            {
                firstWord();
            }
        }

            /**
             * Selects the appropriate list if any options have changed...
             */
        protected void selectList()
        {
            /*boolean f = filteredOnlyCheckbox.getState();
            boolean o = useOrderAndCount.getState();

            WordList currList = listToUse;

            listToUse = masterList;

            if (f)
            {
                updateFilteredList();
                listToUse = filteredList;
            }

            if (o)
            {
                updateWordOrderAndCount();
                listToUse = finalList;
            }

            if (currList != listToUse)
            {
                System.out.println("List Changed...");
                searchedIndex = 0;
                setSearchWord(searchedIndex);
            }*/
        }

            /**
             * Go to the next word.
             */
        public void nextWord()
        {
            selectList();
            searchedIndex = (searchedIndex + 1) % masterList.nWords;
            setSearchWord(searchedIndex);
        }

            /**
             * Go to the previous word.
             */
        public void previousWord()
        {
            selectList();
            searchedIndex--;
            if (searchedIndex < 0) searchedIndex = masterList.nWords - 1;
            if (searchedIndex < 0) searchedIndex = 0;
            setSearchWord(searchedIndex);
        }

            /**
             * Go to the last word.
             */
        public void lastWord()
        {
            selectList();
            searchedIndex = masterList.nWords - 1;
            if (searchedIndex < 0) searchedIndex = 0;
            setSearchWord(searchedIndex);
        }

            /**
             * Go to the first word.
             */
        public void firstWord()
        {
            selectList();
            searchedIndex = 0;
            setSearchWord(searchedIndex);
        }

        public void keyPressed(KeyEvent ke)
        {
            int code = ke.getKeyCode();
            if (ke.getSource() == searchedMeaning)
            {
                if (code == KeyEvent.VK_LEFT) previousWord();
                else if (code == KeyEvent.VK_RIGHT) nextWord();
                else if (code == KeyEvent.VK_DOWN) lastWord();
                else if (code == KeyEvent.VK_UP) firstWord();
            }
        }
        public void keyReleased(KeyEvent ke) { } 
        public void keyTyped(KeyEvent ke) { }

            /**
             * Set the word at a given index.
             */
        public void setSearchWord(int index)
        {
            if (index >= 0 && index < listToUse.nWords)
            {
                Word word = listToUse.getWord(index);
                searchField.setText(word.wordValue);
                searchedMeaning.setText(word.meaning);
            } else
            {
                searchField.setText("");
                searchedMeaning.setText("");
            }
        }
    }

        /**
         * Panel where the quiz will be shown.
         *
         * The following are required:
         *
         * 1) Each word when asked to display will be displayed with 4
         * other "incorrect" answers.
         * 2) Once a selection is made, 
         */
    class QuizPanel extends EtchedPanel implements ActionListener, ItemListener
    {
            // holds info about the multiple choice questions and which
            // ones were answered and so on....
        protected int choiceInfo[][] = null;
        protected int quizSize = 0;
        protected int numAnswered = 0;
        protected boolean wordMarked[] = null;

            // current word index;
        protected int currWordIndex = 0;

        protected Label currWordLabel = new Label("Word: ");
        protected Label quizStatusLabel = new Label(" / ");
        protected Label resultsLabel = new Label("Results: ");

        protected Panel topLabelsPanel = new Panel(new GridLayout(1, 3));

            // Panels for showing just the quiz answers...
        protected Panel quizChoicesPanel = new Panel(new BorderLayout());
        protected CheckboxGroup answerGroup = new CheckboxGroup();
        protected Checkbox answerCheckboxes[] = new Checkbox[NUM_OPTIONS];
        protected TextArea answerTextAreas[] = new TextArea[NUM_OPTIONS];
        protected Panel answerCheckPanel =
                new Panel(new GridLayout(NUM_OPTIONS, 1));
        protected Panel answerTextPanel =
                new Panel(new GridLayout(NUM_OPTIONS, 1));

            /**
             * Button to traverse forward and backward.
             */
        protected Panel buttonPanel = new Panel();
            
            // Go to the next or previous word...
        protected Button nextButton = new Button("Next >");
        protected Button prevButton = new Button("< Previous");
        protected Button firstButton = new Button("First");
        protected Button lastButton = new Button("Last");

            // show the answer for the current work...
        protected Button showAnswer = new Button("Show Answer");

            // Starts and finish the test...
        protected Button startButton = new Button("Start");
        protected Button finishButton = new Button("Finish");

        protected Panel markedItemsPanel = new Panel();

            // Buttons to mark words to get to them later.
        protected Checkbox markCheckbox = new Checkbox("Mark Word");
            // set of marked items...
        protected Choice markedItemsChoice = new Choice();

            /**
             * Shows the meaning of the current word.
             */
        protected Button showAnswerButton = new Button("Reveal");

            /**
             * Constructor.
             */
        public QuizPanel()
        {
            super("Word Quiz");

            layComponents();
        }

        protected final static int ANSWER_INDEX = NUM_OPTIONS;
        protected final static int SELECTED_INDEX = NUM_OPTIONS + 1;

            /**
             * Evaluates the choice information.
             */
        protected void evaluateChoiceInfo()
        {
            updateFilteredList();
            updateWordOrderAndCount();

            numAnswered = 0;
            quizSize = finalList.nWords;
            int randIndex = 0;

            if (choiceInfo == null || choiceInfo.length < quizSize)
            {
                choiceInfo = new int[quizSize][8];
                wordMarked = new boolean[quizSize];
            }

            for (int i = 0;i < quizSize;i++)
            {
                choiceInfo[i][0] = i;
                for (int j = 1;j < NUM_OPTIONS;j++)
                {
                    choiceInfo[i][j] = (i + (j * 2) + 1) % quizSize;

                    //TODO:: Do it truly random
                }

                choiceInfo[i][SELECTED_INDEX] = -1;
                choiceInfo[i][ANSWER_INDEX] = 
                        ((int)(Math.random() * 1000)) % NUM_OPTIONS;
                wordMarked[i] = false;

                int correctPos = choiceInfo[i][ANSWER_INDEX];
                int temp = choiceInfo[i][correctPos];
                choiceInfo[i][correctPos] = choiceInfo[i][0];
                choiceInfo[i][0] = temp;
            }
        }

            /**
             * Set the quiz word at a given index.
             */
        public void setQuizWord(int index)
        {
            currWordLabel.setText("");
            if (index >= 0 && index < quizSize)
            {
                currWordLabel.setText(finalList.words[index].wordValue);

                for (int i = 0;i < NUM_OPTIONS;i++)
                {
                    Word word = finalList.getWord(choiceInfo[index][i]);
                    answerCheckboxes[i].setState(false);
                    answerTextAreas[i].setText(word.meaning);
                }
            } else
            {
                for (int i = 0;i < NUM_OPTIONS;i++)
                {
                    Word word = finalList.getWord(choiceInfo[index][i]);
                    answerCheckboxes[i].setState(false);
                    answerTextAreas[i].setText("");
                }
            }
        }

            /**
             * Go to the next word.
             */
        public void nextWord()
        {
            currWordIndex = (currWordIndex + 1) % quizSize;
            setQuizWord(currWordIndex);
        }

            /**
             * Go to the previous word.
             */
        public void previousWord()
        {
            currWordIndex--;
            if (currWordIndex < 0) currWordIndex = quizSize - 1;
            if (currWordIndex < 0) currWordIndex = 0;
            setQuizWord(currWordIndex);
        }

            /**
             * Go to the last word.
             */
        public void lastWord()
        {
            currWordIndex = quizSize - 1;
            if (currWordIndex < 0) currWordIndex = 0;
            setQuizWord(currWordIndex);
        }

            /**
             * Go to the first word.
             */
        public void firstWord()
        {
            currWordIndex = 0;
            setQuizWord(currWordIndex);
        }

            /**
             * Item event handler.
             */
        public void itemStateChanged(ItemEvent ie)
        {
            Object src = ie.getSource();

            if (currWordIndex < 0 || currWordIndex >= quizSize ) return ;

            for (int i = 0;i < NUM_OPTIONS;i++)
            {
                if (src == answerCheckboxes[i])
                {
                    if (answerCheckboxes[i].getState())
                    {
                        if (choiceInfo[currWordIndex][SELECTED_INDEX] < 0)
                        {
                            numAnswered ++;
                        }
                        choiceInfo[currWordIndex][SELECTED_INDEX] = i;
                        quizStatusLabel.setText(numAnswered + " / " + quizSize);
                    } else
                    {
                        choiceInfo[currWordIndex][SELECTED_INDEX] = -1;
                        numAnswered --;
                        quizStatusLabel.setText(numAnswered + " / " + quizSize);
                    }
                } else
                {
                    answerCheckboxes[i].setState(false);
                }
            }

            System.out.println("Selected Answer: " + 
                               choiceInfo[currWordIndex][SELECTED_INDEX]);
        }

            /**
             * Action event handler.
             */
        public void actionPerformed(ActionEvent ae)
        {
            Object src = ae.getSource();

            if (src == startButton)
            {
                markedItemsChoice.removeAll();
                markCheckbox.setState(false);

                evaluateChoiceInfo();

                quizStatusLabel.setText(quizSize + " words");

                for (int i = 0;i < NUM_OPTIONS;i++)
                {
                    answerCheckboxes[i].setState(false);
                }

                nextButton.setEnabled(true);
                prevButton.setEnabled(true);
                lastButton.setEnabled(true);
                firstButton.setEnabled(true);
                finishButton.setEnabled(true);

                firstWord();

                    // now do a question list with the "final" words...

                //public final static int SEARCH_MODE = 0;
                //public final static int QUIZ_MODE = 1;
                //protected int displayMode = 0;
            } else if (src == finishButton)
            {
            } else if (src == nextButton)
            {
                nextWord();
            } else if (src == lastButton)
            {
                lastWord();
            } else if (src == prevButton)
            {
                previousWord();
            } else if (src == firstButton)
            {
                firstWord();
            }
        }

            /**
             * Layout the components.
             */
        public void layComponents()
        {
            setLayout(new BorderLayout());

            buttonPanel.add(firstButton);
            buttonPanel.add(prevButton);
            buttonPanel.add(startButton);
            buttonPanel.add(finishButton);
            buttonPanel.add(nextButton);
            buttonPanel.add(lastButton);

            for (int i = 0;i < NUM_OPTIONS;i++)
            {
                answerCheckboxes[i] = new Checkbox("");
                answerCheckPanel.add(answerCheckboxes[i]);
                answerCheckboxes[i].addItemListener(this);

                answerTextAreas[i] =
                    new TextArea("", 3, 50,
                                 TextArea.SCROLLBARS_VERTICAL_ONLY);
                answerTextAreas[i].setEditable(false);
                answerTextPanel.add(answerTextAreas[i]);
            }

            quizChoicesPanel.add("West", answerCheckPanel);
            quizChoicesPanel.add("Center", answerTextPanel);

            topLabelsPanel.add(currWordLabel);
            topLabelsPanel.add(quizStatusLabel);

                // panel for buttons and search...
            Panel southPanel = new Panel(new BorderLayout());
            southPanel.add("South", buttonPanel);
            southPanel.add("North", markedItemsPanel);

            markedItemsPanel.add(markCheckbox);
            markedItemsPanel.add(new Label("Marked Words: "));
            markedItemsPanel.add(markedItemsChoice);

            //Panel topPanel = new Panel(new GridLayout(2, 1, 1, 1));
            //topPanel.add(topLabelsPanel);
            //topPanel.add(markedItemsPanel);

            add("North", topLabelsPanel);
            add("Center", quizChoicesPanel);
            add("South", southPanel);

            lastButton.addActionListener(this);
            firstButton.addActionListener(this);
            prevButton.addActionListener(this);
            nextButton.addActionListener(this);
            startButton.addActionListener(this);
            finishButton.addActionListener(this);

            lastButton.setEnabled(false);
            firstButton.setEnabled(false);
            nextButton.setEnabled(false);
            nextButton.setEnabled(false);
            prevButton.setEnabled(false);
            finishButton.setEnabled(false);
        }
    }

        /**
         * Information regarding a filter.
         */
    class FilterInfo
    {
        public String filterName;
        public Class filterClass;
        public FilterEditor filterEditor;
    }

        /**
         * Main entry point as an application.
         */
    public static void main(String args[])
    {
        /*Frame f = new Frame(VocabUtils.getAppTitle() +
                            "V" + VocabUtils.getAppVersion());
        f.setLayout(new BorderLayout());
        f.add("Center", new VocabApplet());
        f.setBounds(100, 100, 300, 300);
        f.setVisible(true);
        f.toFront();*/
        //f.addWindowListener(this);
        //f.pack();
    }
}
