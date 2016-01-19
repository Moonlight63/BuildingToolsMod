package com.moonlight.buildingtools.items.tools.undoTool;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

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
		
		PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(entity).get();
		if(!playerwrap.undolist.isEmpty())
			undoSet.addAll(playerwrap.undolist.pollLast());
		
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
	
	public void perform(){
		if(!currentlyCalculating){
			
			if(!undoSet.isEmpty()){
				//System.out.println("Starting Undo");
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(RunUndoPass(), world, true);
				currentlyCalculating = false;
			}
			else{
				isFinished = true;
			}
			
		}
	}
	
	/**
     * The world that this queue is change
     * 
     * @return the world
     */
    public World getWorld()
    {
        return this.world;
    }

	public boolean isFinished(){
		return isFinished;
	}
	

	
}
