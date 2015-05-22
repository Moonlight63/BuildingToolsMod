package com.moonlight.buildingtools.items.tools.tasks;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class ChangeQueueTask implements Runnable{
	
	@Override
	public void run() {
        int n = 0;
        for (PlayerWrapper p : BuildingTools.getPlayerRegistry().getPlayers()){
        	n++;
        }
        if (n != 0){
        	for (PlayerWrapper p : BuildingTools.getPlayerRegistry().getPlayers()){
                if(p.hasPendingChanges()){
                	if(p.pendingChangeQueue == null){
                		if(p.getNextPendingChange().get().isFinished()){
                			p.clearNextPending();
                		}
                		else{
                			p.getNextPendingChange().get().perform();
                		}
                	}
                }
            }
        }
	}
	
}
