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
                if(!p.pendingChangeQueue.isEmpty()){
                	//System.out.println(p.pendingChangeQueue);
                	if(!p.pendingChangeQueue.peek().isFinished()){
                		p.pendingChangeQueue.poll().perform();
                	}
                }
            }
        }
	}
	
}
