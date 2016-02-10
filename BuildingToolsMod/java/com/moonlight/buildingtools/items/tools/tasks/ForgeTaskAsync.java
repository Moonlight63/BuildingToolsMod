package com.moonlight.buildingtools.items.tools.tasks;



public class ForgeTaskAsync extends Task implements Runnable
{

    private boolean running;
    private ForgeSchedulerService scheduler;

    /**
     * Creates a new {@link ForgeTaskAsync}.
     * 
     * @param runnable The task runnable
     * @param interval The interval
     * @param scheduler The scheduler
     */
    public ForgeTaskAsync(Runnable runnable, int interval, ForgeSchedulerService scheduler)
    {
    	super(runnable, interval);
    	//System.out.println("ForgeTaskAsync.ForgeTaskAsync()");
        this.scheduler = scheduler;
        this.running = true;
        
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel()
    {
        this.running = false;
        this.scheduler.cancel(this);
    }

    /**
     * Starts the task executing on the current thread, recommend call from a
     * spawned thread.
     */
    @Override
    public void run()
    {
    	//System.out.println("ForgeTaskAsync.run()");
        //long start = 0;
        //long delta = 0;
    	int i = 0;
        while (this.running)
        {
        	//System.out.print(i+"Tick!\n");
            //start = System.currentTimeMillis();
        	//for(int i = 0; i < this.getInterval(); i++){
        		
        		
        		
        		if(i >= this.getInterval()){
        			
        			//System.out.println("ForgeTaskAsync.run() \n");
        			this.getRunnable().run();
        			
        			i = 0;
        		}
        		else{
        			i++;
        		}
        	//}
            
            //delta = System.currentTimeMillis() - start;
            //Thread.yield();
            /*try
            {
                Thread.sleep(this.getInterval());
            } catch (InterruptedException e)
            {
            //    this.running = false;
            }*/
        }
    }

    /**
     * Gets whether this task is still running.
     * 
     * @return Is running
     */
    public boolean isRunning()
    {
        return this.running;
    }

}