
/**
 * Holds the information about an expression.
 */
public class RegExRule
{
    int tokenID;
    ExpNode regex;
    ExpNode lookAhead;
    int nextState = -1;
    boolean startOfLine = false;
    Bits stateList = null;

        /**
         * Constructor for creating a new expression.
         */
    public RegExRule(int tokenID, ExpNode regexNode, Bits validStates,
                     boolean startOfLine, ExpNode laExpr, int nextState)
    {
        this.tokenID = tokenID;
        this.startOfLine = startOfLine;

        this.regex = regexNode;
        this.lookAhead = laExpr;

        this.nextState = nextState;
        this.stateList = validStates;
    }
}
