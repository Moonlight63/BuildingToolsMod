package com.moonlight.buildingtools.items.tools.undoTool;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

@Deprecated
public class ThreadUndo implements BlockChangeBase {
	
	protected World world;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	
	protected Set<ChangeBlockToThis> undoSet = new CopyOnWriteArraySet<ChangeBlockToThis>();
	protected boolean currentlyCalculating = false;
	
	@Deprecated
	public ThreadUndo(World world, EntityPlayer entity){
		this.world = world;
		this.entity = entity;
		System.out.println(undoSet.size());
	}
	
	public Set<ChangeBlockToThis> RunUndoPass(){
		Set<ChangeBlockToThis> tempList = new LinkedHashSet<ChangeBlockToThis>();
		int count = 0;
		currentlyCalculating = true;
		for(ChangeBlockToThis change : undoSet){
			if(count < 100){
				tempList.add(change);
				undoSet.remove(change);
				count++;
			}
			else{
				break;
			}
		}
		return tempList;
	}
	
	@Override
	public void perform(){
		if(!currentlyCalculating){
			
			if(!undoSet.isEmpty()){
				//System.out.println("Starting Undo");
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(RunUndoPass(), world));
				currentlyCalculating = false;
			}
			else{
				isFinished = true;
			}
			
		}
	}
	
    public World getWorld()
    {
        return this.world;
    }

	@Override
	public boolean isFinished(){
		return isFinished;
	}
	

	
}
