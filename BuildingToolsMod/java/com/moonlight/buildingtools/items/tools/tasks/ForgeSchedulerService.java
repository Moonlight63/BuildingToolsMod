package com.moonlight.buildingtools.items.tools.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class ForgeSchedulerService
{

    private List<ForgeTask> tasks;
    private List<ForgeTaskAsync> asyncTasks;
    public ExecutorService exec;

    /**
     * Creates a new {@link ForgeSchedulerService}.
     */
    public ForgeSchedulerService()
    {
        //super(11);
        this.exec = java.util.concurrent.Executors.newCachedThreadPool();
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return "scheduler";
    }

    /**
     * {@inheritDoc}
     */
    public void init()
    {
        this.tasks = Lists.newArrayList();
        this.asyncTasks = Lists.newArrayList();
    }

    /**
     * {@inheritDoc}
     */
    public void destroy()
    {
        stopAllTasks();
        this.tasks = null;
        this.asyncTasks = null;
    }

    /**
     * {@inheritDoc}
     */
    public Optional<ForgeTask> startSynchronousTask(Runnable runnable, int interval)
    {
    	//System.out.println("\n\n\n\n\n\n\nForgeSchedulerService.startSynchronousTask()\n\n\n\n\n\n\n");
        ForgeTask task = new ForgeTask(runnable, interval, this);
        this.tasks.add(task);
        return Optional.of(task);
    }

    /**
     * {@inheritDoc}
     */
    public Optional<ForgeTaskAsync> startAsynchronousTask(Runnable runnable, int interval)
    {
    	//System.out.println("ForgeSchedulerService.startAsynchronousTask()");
    	
        ForgeTaskAsync task = new ForgeTaskAsync(runnable, interval, this);
        //Thread newthread = new Thread(task, this.getName());
    	//Thread test = new Thread (runnable, "asyncTask");
        //newthread.start();
        this.asyncTasks.add(task);
        this.exec.execute(task);
        
        return Optional.of(task);
    }

    /**
     * {@inheritDoc}
     */
    public void stopAllTasks()
    {
        this.tasks.clear();
        for (Iterator<ForgeTaskAsync> it = this.asyncTasks.iterator(); it.hasNext();)
        {
            ForgeTaskAsync task = it.next();
            task.cancel();
        }
    }

    /**
     * Cancels the given {@link ForgeTask} if it exists with this scheduler.
     * 
     * @param forgeTask the task to cancel
     */
    public void cancel(Task forgeTask)
    {
        this.tasks.remove(forgeTask);
        this.asyncTasks.remove(forgeTask);
    }

    /**
     * Ticks the scheduler. Which ticks all tasks within this scheduler by the
     * correct number of intervals according to the delta time since this was
     * last called.
     */
    public void onTick()
    {
        for (ForgeTask task : this.tasks)
        {
            task.tick();
        }
        for (Iterator<ForgeTaskAsync> it = this.asyncTasks.iterator(); it.hasNext();)
        {
            ForgeTaskAsync task = it.next();
            if (!task.isRunning())
            {
                it.remove();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterable<? extends Task> getAllTasks()
    {
        List<Task> tasks = Lists.newArrayList();
        tasks.addAll(this.tasks);
        tasks.addAll(this.asyncTasks);
        return tasks;
    }
}
