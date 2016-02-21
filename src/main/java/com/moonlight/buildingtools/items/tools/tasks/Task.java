package com.moonlight.buildingtools.items.tools.tasks;

public abstract class Task
{

    private final Runnable runnable;
    private final int interval;

    /**
     * Creates a new {@link Task}.
     * 
     * @param runnable the task's runnable
     * @param interval the interval of the task's execution, in milliseconds
     */
    public Task(Runnable runnable, int interval)
    {
        this.runnable = runnable;
        this.interval = interval;
    }

    /**
     * Returns the task's runnable.
     * 
     * @return the runnable
     */
    public Runnable getRunnable()
    {
        return this.runnable;
    }

    /**
     * The task's interval in milliseconds.
     * 
     * @return the interval
     */
    public int getInterval()
    {
        return this.interval;
    }

    /**
     * Cancels this task.
     */
    public abstract void cancel();

}
