
/**
 * Super class of all exceptions associated with Regular Expressions
 * and the Regular Expression engine.
 */
public class RegExException extends Exception
{
    public final static int INVALID_OCTAL = 0;
    public final static int INCOMPLETE_OCTAL = 1; 
    public final static int INVALID_ASCII = 2;
    public final static int INCOMPLETE_ASCII = 3;
    public final static int INVALID_UNICODE = 4;
    public final static int INCOMPLETE_UNICODE = 5;
    public final static int UNARY_AT_START = 6;
    public final static int UNMATCHED_PARANTHESIS = 7;
    public final static int UNTERMINATED_SQ_BRACKET = 8;
    public final static int UNTERMINATED_ESCAPE_SEQUENCE = 9;

        /**
         * Exception code.
         */
    protected int excCode;

        /**
         * Constructor.
         */
    public RegExException(int code)
    {
        this.excCode = code;
    }
}
