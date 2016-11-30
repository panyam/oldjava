package svtool.core;

/**
 * Super class of all interfaces that monitor statuses of tasks.
 *
 * When the status of a task changes, the methods in this interface are
 * invoked.
 *
 * @author Sri Panyam
 */
public interface TaskMonitor
{
        /**
         * Called when the status of a task has changed.
         *
         * @param   task    The task being monitored whose status has
         *                  changed.
         *
         * @author  Sri Panyam
         */
    public void taskStatusChanged(Task task);
}
