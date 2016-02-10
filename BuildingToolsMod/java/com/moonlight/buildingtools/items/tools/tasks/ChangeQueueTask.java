package com.moonlight.buildingtools.items.tools.tasks;

import java.util.Optional;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class ChangeQueueTask implements Runnable{
	
	@Override
	public void run() {
        int n = 0;
        for (PlayerWrapper p : BuildingTools.getPlayerRegistry().getPlayers()){
        	n++;
        }
        
        if (n == 0)
            return;
        
        int remaining = 4096;
        
        if (n != 0){
        	for (PlayerWrapper p : BuildingTools.getPlayerRegistry().getPlayers()){
        		if(!p.hasPendingChanges())
        			continue;                
        		//System.out.println(p);
            	if(p.pendingChangeQueue == null){
            		if(p.getNextPendingChange().get().isFinished()){
            			p.clearNextPending(false);
            		}
            		else{
            			p.getNextPendingChange().get().perform();
            		}
            	}
            	
            }
        }
	}
	
}
