
import java.io.*;
import java.util.*;

/**
 * The main Regular Expression Engine that maintains the rules, state
 * machine, states and notifies the listener when a match was made.
 */
public class RegExEngine extends StringIDTable
{
        /**
         * Holds all the rules in the engine.
         */
    public Vector rules = new Vector();

    protected static ExpNode endOfLineMatch = null;
    protected static ExpNode endOfLineMatchUniCode = null;

        /**
         * Tells what the line and column positions are.
         */
    protected int lineCounter = 0, columnCounter = 0;

        /**
         * The current state that the lexer is in.
         * If this is less than 0 then we are in an error state.
         */
    protected int currState = 0;

        /**
         * Stores info about all states that the lexer can be accepting
         * expressions in.
         */
    protected StringIDTable stateTable = new StringIDTable();

        /**
         * Must be set to true before any tokenizing can take place.
         * This changing to true happens automatically.
         */
    protected boolean initialised = false;

        /**
         * Regular expression factory.
         */
    protected ExpNodeFactory expFactory = new ExpNodeFactory();

        /**
         * Means the engine cannot be started again.
         */
    protected boolean engineRunning = false;

        /**
         * Used to stop the RegEx engine.
         */
    protected boolean engineStopped = false;

        /**
         * The Expression listener that is notified when a match is made.
         */
    protected RegExListener listener = null;

        /**
         * Sets the regular expression listener.
         */
    public void setRegExListener(RegExListener listener)
    {
        this.listener = listener;
    }

        /**
         * Constructor.
         */
    public RegExEngine()
    {
        this(null);
    }

        /**
         * Constructor.
         */
    public RegExEngine(Vector stateList)
    {
            // initial state must ALWAYS be added
        currState = stateTable.registerString("YYINITIAL");

        for (int i = 0, s = stateList == null ? 0 : stateList.size();
                i < s;i++)
        {
            stateTable.registerString((String)stateList.elementAt(i));
        }
    }

        /**
         * Add a new regular expression to the table.
         *
         * @param tokenID       ID of the token that is to be returned if a
         *                      match was found.
         * @param tokenExpr     The expression that triggers the match.
         * @param validStates   Tells in which states, this rule is valid.
         *                      If this is null then the rule is matched
         *                      only on the default state.
         * @param startOfLine   Tells if this rule is to be matched only at
         *                      the start of the line.
         * @param lookAhead     Match the expression only if the lookAhead
         *                      expression was found.  Lookahead must be a
         *                      "$" for end of line or another regex
         * @param newState      If this value is positive, upon the match,
         *                      the state of the regex engine changes to
         *                      the specified state.
         */
    public int addRule(int tokenID, String ruleExpr,
                       int validStates[], boolean startOfLine,
                       String lookAheadExpr, int nextState)
        throws RegExException
    {
        //int tokenID = registerString(tokenName);
        initialised = false;

        ExpNode laExpr = null;
        if (lookAheadExpr != null)
        {
                // TODO:    Consider putting this as an operator in 
                //          the ExpFactory class
            if (lookAheadExpr.charAt(0) == '$')
            {
                if (endOfLineMatch == null)
                {
                    endOfLineMatch =
                        expFactory.createExpression("\\r|\\n|\\r\\n");
                    endOfLineMatchUniCode =
                        expFactory.createExpression("\\r|\\n|\\r\\n|\\u2028|\\u2029|\\u000B|\\u000C|\\u0085");
                }

                    // or pick the unicode one...
                laExpr = endOfLineMatch;
            }
            else 
            {
                laExpr = expFactory.createExpression(lookAheadExpr);
            }
        }

        Bits stateList = new Bits(stateTable.size());

        for (int i = 0;i < validStates.length;i++)
        {
            stateList.setBit(validStates[i], true);
        }

        RegExRule newRule = new RegExRule
                                (rules.size(),
                                 expFactory.createExpression(ruleExpr),
                                 stateList,
                                 startOfLine,
                                 laExpr,
                                 nextState);

        rules.addElement(newRule);

            // add the new rule to the table
        return newRule.tokenID;
    }

        /**
         * Constructs the state machines and prepares this engine
         * before it can begin tokenizing.
         */
    protected void initialiseStateMachine()
    {
            // Steps:
            // 1) Create a NFA from the expressions.
            // 2) Convert the NFA to a DFA using subset matching.
            //
            // It is to be noted that a NFA is a super set of a DFA where
            // there are no Epsilon transitions and there is ONLY one
            // output from a state for any given input.
        initialised = true;

            // now expressions would have been created for all rules,
            // now its a matter of creating the DFA for this!!!
            // where do we go from here???

            // At the moment we have a whole bunch of Rules... Now a
            // statemachine has to be created out of this that would keep
            // trackof where we are and so on...  Also, "pushback" has to
            // be allowed as well so that with lookAhead matches, the
            // "second" expression can be saved or restarted from when the
            // next call to nextToken occurs
    }

        /**
         * Runs the engine.
         */
    public synchronized void start(String input)
    {
        start(new StringReader(input));
    }

        /**
         * Runs the engine by taking in inputs from a given
         * input stream.
         */
    public synchronized void start(Reader reader)
    {
        if (!initialised)
        {
            initialiseStateMachine();
        }
    }

        /**
         * Stops tokenizing at this point.
         */
    public synchronized Token nextToken()
    {
        return null;
    }

        /**
         * Test function.
         */
    public static void main(String args[])
        throws Exception
    {
        int len = args[0].length();
        char array[] = args[0].toCharArray();

        System.out.println("Building tree for: " + args[0]);
        ExpNodeFactory factory = new ExpNodeFactory();
        ExpNode node = factory.createExpression(array, 0, len);
        node.print();
        System.out.println("\n");

        DFACreator dfaCreator = new DFACreator(node);
    }
}
