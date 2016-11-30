import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * This holds the actual contents/data in the document.
 */
public class Document
{
        /**
         * The terminal and non-terminal names used in the syntax.
         */
    protected StringIDTable stringIDTable = new StringIDTable ();

        /**
         * The cursor object that is used to traverse the data.
         */
    protected Cursor cursor;

        /**
         * The parser stack object.
         */
    protected Stack parserStack = new Stack();
}
