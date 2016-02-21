package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

public class ThreadBuildersTool implements IShapeable, BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected int radiusX;
	protected boolean replaceAll;
	protected int radiusZ;
	protected EnumFacing side;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	protected int count = 0;
	protected boolean shapeFinished = false;
	
	protected boolean currentlyCalculating = false;
	
	//protected List<BlockPos> tempList = new ArrayList<BlockPos>();
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected List<Set<ChangeBlockToThis>> listSet = Lists.newArrayList();
	
	protected Set<BlockPos> checkedList = new HashSet<BlockPos>();
	
	public ThreadBuildersTool(World world, BlockPos origin, int radiusX, boolean replaceall, int radiusZ, EnumFacing side, EntityPlayer entity){
		this.world = world;
		this.origin = origin;
		this.radiusX = radiusX;
		this.replaceAll = replaceall;
		this.radiusZ = radiusZ;
		this.side = side;
		this.entity = entity;
	}
	
	@Override
	public void setBlock(BlockPos tempPos) {
		
		BlockPos bpos = tempPos;
		
		//if(count<4096 && !checkedList.contains(tempPos)){
			
			currentlyCalculating = true;
			
			if (side == EnumFacing.UP || side == EnumFacing.DOWN){
				bpos = new BlockPos(tempPos.getX(), side == EnumFacing.UP ? tempPos.getY() : -tempPos.getY(), tempPos.getZ());
			}
			else if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH){
				bpos = new BlockPos(tempPos.getX(), tempPos.getZ(), side == EnumFacing.NORTH ? -tempPos.getY() : tempPos.getY());
			}
			else if (side == EnumFacing.EAST || side == EnumFacing.WEST){
				bpos = new BlockPos(side == EnumFacing.WEST ? -tempPos.getY() : tempPos.getY(), tempPos.getX(), tempPos.getZ());
			}
			
            if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256){
            	if(!replaceAll && world.getBlockState(bpos.add(origin)) != world.getBlockState(origin))
            		return;
            	
            	
            	if(world.getTileEntity(bpos.add(origin)) != null){
            		NBTTagCompound compound = new NBTTagCompound();
            		world.getTileEntity(bpos.add(origin)).writeToNBT(compound);
            		tempList.add(new ChangeBlockToThis(bpos.add(origin).offset(side), world.getBlockState(bpos.add(origin)), compound));
            	}
            	else{
            		tempList.add(new ChangeBlockToThis(bpos.add(origin).offset(side), world.getBlockState(bpos.add(origin))));
            	}
            	
            	//checkedList.add(tempPos);
            	count++;
            }
            
            if(count > 4096){
    			addSetToList();
    			return;
    		}
                
		//}
	}
	
	public void perform(){
		
		if(!currentlyCalculating && !shapeFinished){
			tempList.clear();
			Shapes.Cuboid.generator.generateShape(radiusX, 0, radiusZ, this, true);
		}
		
		if(listSet.isEmpty() && shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
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

	public void checkAndAddQueue(){
//		if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue != null)
//			return;
		
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(listSet.get(0), world));
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(listSet.get(0), world, Blocks.air.getDefaultState()));
		
		listSet.remove(0);
		
	}
	
	public void addSetToList(){
		listSet.add(Sets.newHashSet(tempList));
		tempList.clear();
		count = 0;
		checkAndAddQueue();
	}

	@Override
	public void shapeFinished() {
		//System.out.println("Shape Finished");
		addSetToList();
		shapeFinished = true;
		currentlyCalculating = false;
	}
	

	
}
