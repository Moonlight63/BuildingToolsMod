package com.moonlight.buildingtools.items.tools;

import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;

import net.minecraft.block.BlockDoor;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockChangeQueue {
	
	protected World world;
	protected boolean isFinished = false;
	public Set<ChangeBlockToThis> blockpos = Sets.newConcurrentHashSet();
	
	public BlockChangeQueue(Set<ChangeBlockToThis> tempList, World world){
		this.blockpos.addAll(tempList);
		this.world = world;		
	}
	
	public void perform(){
		for(ChangeBlockToThis bpos : blockpos){
			
			if(bpos.getBlockState() != BlockLoader.tempBlock.getDefaultState()){
				if(bpos.getBlockState().getBlock() instanceof BlockDoor){
					System.out.println(bpos.getBlockPos());
					System.out.println(bpos.getBlockState());
					ItemDoor.placeDoor(world, bpos.getBlockPos(), bpos.getBlockState().getValue(BlockDoor.FACING), bpos.getBlockState().getBlock(), false);
				}
				else{
					world.setBlockState(bpos.getBlockPos(), bpos.getBlockState());
				}
			}
			
			if(!world.isAirBlock(bpos.getBlockPos())){
				TileEntity tileentity = world.getTileEntity(bpos.getBlockPos());
				
				if(tileentity != null){
					NBTTagCompound compound = bpos.getNBTTag();
					
					if(compound!=null){						
						compound.setInteger("x", bpos.getBlockPos().getX());
						compound.setInteger("y", bpos.getBlockPos().getY());
						compound.setInteger("z", bpos.getBlockPos().getZ());
						tileentity.readFromNBT(bpos.getNBTTag());
						tileentity.markDirty();
					}
					
				}
			}
		}
		
		isFinished = true;
	}
	
    public World getWorld()
    {
        return this.world;
    }

	public boolean isFinished(){
		return isFinished;
	}
}
