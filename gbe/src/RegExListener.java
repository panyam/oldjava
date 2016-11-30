
/**
 * A listener for listening to the regular expression state machine
 * when a match is made.
 */
public interface RegExListener
{
        /**
         * This is called bt the RegExEngine when a match is made.
         */
    void ruleMatched(Object rule, Token token);
}
