package com.moonlight.buildingtools.items.tools.tasks;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class BlockChangeTask implements Runnable{

	@Override
	public void run() {		
		int n = 0;
        for (PlayerWrapper p : BuildingTools.getPlayerRegistry().getPlayers()){
        	n++;
        }
        if (n != 0){
        	for (PlayerWrapper p : BuildingTools.getPlayerRegistry().getPlayers()){
        		//System.out.println(p);
                if(p.pendingChangeQueue != null){
                	if(!p.pendingChangeQueue.isFinished()){
                		p.pendingChangeQueue.perform();
                	}
                	else{
                		p.pendingChangeQueue = null;
                	}
                }
            }
        }
	}
	
}
