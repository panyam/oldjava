package svtool.data;

import svtool.core.*;

/**
 * A class that represents the task of opening a connection to a database.
 */
public class DatabaseConnector implements Task
{
    boolean isDone = true;
    boolean connectionFailed = false;
    int value = 0;
    Object connResult = null;
    Database dbase;

    public String getMessage()
    {
        if (dbase == null) return "";

        if (!isDone)
        {
            return "Connecting to: " +
                    dbase.getLogin() + "@" + dbase.getHost() + "...";
        }
        return "Connected.";
    }

        /**
         * Gets the percentage completed.
         */
    public double getPercentComplete()
    {
        return -1;
    }

        /**
         * Gets the current progress value.
         */
    public int getCurrentValue()
    {
        return value++;
    }

        /**
         * Gets the minimum value.
         */
    public int getMinimumValue()
    {
        return 0;
    }

        /**
         * Sets the database that needs
         * to do the connection.
         */
    public void setDatabase(Database db)
    {
        this.dbase = db;
    }

        /**
         * Get the maximum value.
         */
    public int getMaximumValue()
    {
        return -1;
    }

        /**
         * Starts the task.
         */
    public void startTask()
    {
        doConnection(null, null, null);
    }

        /**
         * Stops the thread.
         */
    public boolean stopTask()
    {
        if (isDone)
        {
            return true;
        } else
        {
            if (dbase == null) return true;
            dbase.closeConnection();
                // if we are still going send a flag to
                // stop the connection...
        }
        return true;
        //return false;
    }

        /**
         * Starts the database connection.
         */
    public void doConnection(final String dbName,
                             final String login,
                             final String pwd) 
    {
        final SwingWorker worker = new SwingWorker()
        {
            public Object construct()
            {
                connectionFailed = isDone = false;
                connResult = dbase;
                value = 0;
                try
                {
                    if (dbName != null && login != null && pwd != null)
                    {
                        dbase.setParameters(dbName, login, pwd);
                    }
                    dbase.connect();
                    isDone = true;
                } catch (Exception exc)
                {
                    isDone = true;
                    connectionFailed = true;
                    connResult = exc;
                }
                return connResult;
            }
        };
        worker.start();
    }

    public Object getResult()
    {
        return connResult;
    }

        /**
         * Gets the status of the task.
         */
    public int getTaskStatus()
    {
        if (isDone)
        {
            return (connectionFailed ? FAILED : SUCCEEDED);
        } else
        {
            return EXECUTING;
        }
    }
}
