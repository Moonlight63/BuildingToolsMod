package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.sun.prism.Material;

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
	
	protected boolean currentlyCalculating = false;
	
	//protected List<BlockPos> tempList = new ArrayList<BlockPos>();
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
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
		
		if(count<4096 && !checkedList.contains(tempPos)){
			
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
            	
            	checkedList.add(tempPos);
            	count++;
            }
                
		}
	}
	
	public void perform(){
		
		if(!currentlyCalculating){
			
			tempList.clear();
			
			Shapes.Cuboid.generator.generateShape(radiusX, 0, radiusZ, this, true);
			
			if(!tempList.isEmpty() && tempList != null){
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(CalcUndoList(tempList));
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, Blocks.air.getDefaultState());
			}
			
			if(count < 4096){
				if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist.add(new LinkedHashSet<ChangeBlockToThis>((BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList))))
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				isFinished = true;
			}
			
			currentlyCalculating = false;
			
			count = 0;
		}

	}
	
	public Set<ChangeBlockToThis> CalcUndoList(Set<ChangeBlockToThis> tempList){
		Set<ChangeBlockToThis> newTempList = new LinkedHashSet<ChangeBlockToThis>();
		
		for(ChangeBlockToThis pos : tempList){
			newTempList.add(addBlockWithNBT(pos.getBlockPos(), world.getBlockState(pos.getBlockPos()), pos.getBlockPos()));
		}
		
		return newTempList;
	}
	
	public ChangeBlockToThis addBlockWithNBT(BlockPos oldPosOrNull, IBlockState blockState, BlockPos newPos){
		if(oldPosOrNull != null && world.getTileEntity(oldPosOrNull) != null){
    		NBTTagCompound compound = new NBTTagCompound();
    		world.getTileEntity(oldPosOrNull).writeToNBT(compound);
    		//tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
    		return new ChangeBlockToThis(newPos, blockState, compound);
		}
    	else{
    		//tempList.add(new ChangeBlockToThis(newPos, blockState));
    		if(blockState.getBlock() instanceof BlockDoor){
    			if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER){
					return new ChangeBlockToThis(newPos, blockState.withProperty(BlockDoor.HINGE, world.getBlockState(oldPosOrNull.up()).getValue(BlockDoor.HINGE)));
				}
    			else if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER){
					return new ChangeBlockToThis(newPos, blockState.withProperty(BlockDoor.FACING, world.getBlockState(oldPosOrNull.down()).getValue(BlockDoor.FACING)));
				}
    		}
    		return new ChangeBlockToThis(newPos, blockState);
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
