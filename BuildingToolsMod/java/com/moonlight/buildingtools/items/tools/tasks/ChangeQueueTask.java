package com.moonlight.buildingtools.items.tools.tasks;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

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
                			//if(FMLCommonHandler.instance().getSide().isClient())
                				p.getNextPendingChange().get().perform();
                		}
                	}
                }
            }
        }
	}
	
}
