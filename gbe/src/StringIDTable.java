
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * This class holds a table of String/ID pairs so that 
 * given one the other can be found in near constant time.
 */
public class StringIDTable
{
        /**
         * Names of all the strings.
         */
    protected String stringNames[] = new String[32];
    protected int nStrings = 0;

        /**
         * A hashtable for storing the ID given the string name.
         */
    protected Hashtable stringTable = new Hashtable();

        /**
         * Given a name returns its id.
         */
    public String getString(int id)
    {
        return stringNames[id];
    }

        /**
         * Tells how many strings are defined here.
         */
    public int size()
    {
        return nStrings;
    }

        /**
         * Given a name returns its id.
         */
    public int getID(String name)
    {
        Integer id = (Integer)stringTable.get(name);
        if (id == null) return -1;
        return id.intValue();
    }

        /**
         * Register a new name in the string table.
         */
    public synchronized int registerString(String name)
    {
        Integer id = (Integer)stringTable.get(name);

            // then the value doesnt exist 
            // so add it in
        if (id == null)
        {
            id = new Integer(nStrings);

                // now also add it to the indexed list...
            if (stringNames.length <= nStrings)
            {
                    // then resize it
                String names[] = stringNames;
                stringNames = new String[(nStrings * 3) / 2];
                System.arraycopy(names, 0, stringNames, 0, nStrings);
            }
            stringNames[nStrings] = name;
            stringTable.put(name, id);
        }
        nStrings++;
        return id.intValue();
    }
}
