package svtool.core;

/**
 * The parent class of all Task objects.
 *
 * These objects represent tasks that can run in parallel and can have
 * their progress monitored.
 *
 * @author  Sri Panyam
 */
public interface Task
{
        /**
         * Indicates that the task has completed successfully.
         */
    public final static int SUCCEEDED   = 0;

        /**
         * Indicates that the task is still executing.
         */
    public final static int EXECUTING   = 1;

        /**
         * Indicates that the task has been cancelled.
         */
    public final static int CANCELLED   = 2;

        /**
         * Indicates that the task has completed but unsuccessfully.
         */
    public final static int FAILED      = 3;

        /**
         * Gets the current value of the task.
         * @return Returns the current value.  A negative value would
         * indicate an task of indeterminate length.
         */
    public int getCurrentValue();

        /**
         * Gets the minimum value of the task.
         *
         * @return Returns the minimum value.  A negative value would
         * indicate an task of indeterminate length.
         */
    public int getMinimumValue();

        /**
         * Gets the maximum value of the task.
         *
         * @return Returns the maximum value.  A negative value would
         * indicate an task of indeterminate length.
         */
    public int getMaximumValue();

        /**
         * Tells the value completed.
         *
         * @return Returns the percentage completed value of this task.  A
         * negative value would indicate an task of indeterminate length.
         */
    public double getPercentComplete();

        /**
         * Get the message.
         *
         * @return  Return a string based message description of the task.
         */
    public String getMessage();

        /**
         * Tells the status of the task.
         *
         * @return  SUCCESS      - Finished successfully.  <br>
         *          FAILED       - Task failed.  <br>
         *          CANCELLED    -   Task was cancelled.  <br>
         *          PERFORMING   -   Task still going one.<br>
         */
    public int getTaskStatus();

        /**
         * Starts the task.
         */
    public void startTask();

        /**
         * Stops the task.
         *
         * @return False if task stopping failed.
         */
    public boolean stopTask();

        /**
         * Gets the object which is the result of the task
         * execution.  This call is invalid if the task
         * is still executing.
         *
         * Technically this should block till execution has
         * completed.
         *
         * @return Returns the object encapsulating the "core" of this
         *          Task.
         */
    public Object getResult();
}
