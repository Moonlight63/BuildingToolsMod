package com.moonlight.buildingtools.items.tools.tasks;



public class ForgeTask extends Task
{

    private static final int MILLISECONDS_PER_TICK = 50;

    private ForgeSchedulerService scheduler;
    private int ticks = 0;

    /**
     * Creates a new {@link ForgeTask}.
     * 
     * @param runnable the runnable for the task
     * @param interval the interval to execute the task on
     * @param scheduler the scheduler controlling this task
     */
    public ForgeTask(Runnable runnable, int interval, ForgeSchedulerService scheduler)
    {
        super(runnable, interval);
        this.scheduler = scheduler;
        //System.out.println("ForgeTask.ForgeTask()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
        this.scheduler.cancel(this);
    }

    /**
     * Returns whether this task should be run.
     * 
     * @return should run
     */
    public boolean shouldTick()
    {
        return this.ticks * MILLISECONDS_PER_TICK > this.getInterval();
    }

    /**
     * Executes this task if the number of elapsed ticks is greater than the
     * interval number of times. Please note that this will not attempt to stack
     * multiple ticks in the event of server lag causing a larger effective
     * delta time as this would only compound any lag experienced.
     */
    public void tick()
    {
    	//System.out.println("ForgeTask.tick()");
        this.ticks++;
        //if (shouldTick())
        //{
            this.getRunnable().run();
            this.ticks = 0;
        //}
    }

}
