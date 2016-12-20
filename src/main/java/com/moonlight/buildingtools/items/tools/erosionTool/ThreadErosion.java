package com.moonlight.buildingtools.items.tools.erosionTool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadErosion implements BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected int radiusX;
	protected int radiusY;
	protected int radiusZ;
	protected EnumFacing side;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	protected int count = 0;
	
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected Set<BlockPos> checkedList = new LinkedHashSet<BlockPos>();
	protected Set<BlockPos> checkedPos = new LinkedHashSet<BlockPos>();
	
	protected boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	
	protected ErosionVisuallizer erosiondata;
	
	public ThreadErosion(World world, BlockPos origin, int radius, EntityPlayer entity, int preset){
		this.world = world;
		this.origin = origin;
		this.entity = entity;
		
		this.erosiondata = new ErosionVisuallizer(radius, world, origin, preset);
	}
	
	
	int tempCount = 0;
	public void perform(){
		if(!currentlyCalculating){
			tempCount++;
			System.out.println(tempCount);
			
			tempList.clear();
			
			for(BlockPos pos : erosiondata.tracker.getMap().keySet())
				tempList.add(new ChangeBlockToThis(pos, erosiondata.tracker.getMap().get(pos)));
			
			if(!tempList.isEmpty() && tempList != null){
				
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(tempList, world, true));
			}
				
			//if(count < 4096){
				isFinished = true;
				MiscUtils.dumpUndoList(entity);
				//BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//System.out.println("Added all blocks to undo list: " + BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist);
			//}
			
			count = 0;
			currentlyCalculating = false;
			
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
