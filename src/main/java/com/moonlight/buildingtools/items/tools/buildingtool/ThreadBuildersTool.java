package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.HashSet;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected Set<BlockPos> checkedList = new HashSet<BlockPos>();
	
	public ThreadBuildersTool(World world, BlockPos origin, EnumFacing side, EntityPlayer entity, NBTTagCompound nbtdata){
		this.world = world;
		this.origin = origin;
		this.side = side;
		this.entity = entity;
		this.radiusX = nbtdata.getInteger("radiusX");
		this.radiusZ = nbtdata.getInteger("radiusZ");
		this.replaceAll = nbtdata.getBoolean("placeAll");
	}
	
	@Override
	public void setBlock(BlockPos tempPos) {
		
		BlockPos bpos = tempPos;
			
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
        	if(!world.isAirBlock(bpos.add(origin).offset(side)))
        		return;
        	
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
        	
        	count++;
        }
        
        if(count > 4096){
        	checkAndAddQueue();
			return;
		}
        
	}
	
	@Override
	public void perform(){
		
		if(!currentlyCalculating && !shapeFinished){
			tempList.clear();
			Shapes.Cuboid.generator.generateShape(radiusX, 0, radiusZ, this, true);
		}
		
		if(shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}

	}
	
    public World getWorld(){
        return this.world;
    }

	@Override
	public boolean isFinished(){
		return isFinished;
	}

	public void checkAndAddQueue(){		
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(tempList, world, true));
		tempList.clear();
		count = 0;
	}

	@Override
	public void shapeFinished() {
		checkAndAddQueue();
		shapeFinished = true;
	}
	

}
