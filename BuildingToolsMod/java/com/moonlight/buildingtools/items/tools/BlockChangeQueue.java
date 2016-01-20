package com.moonlight.buildingtools.items.tools;

import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.moonlight.buildingtools.helpers.loaders.BlockLoader;

public class BlockChangeQueue {
	
	protected World world;
	protected boolean isFinished = false;
	public Set<ChangeBlockToThis> blockpos;
	protected IBlockState blockStateToPlace = Blocks.stone.getDefaultState();
	protected IBlockState blockStateToReplace = Blocks.air.getDefaultState();
	protected boolean replaceAll = false;
	
	public BlockChangeQueue(Set<ChangeBlockToThis> tempList, World world, IBlockState stateToReplace){
		this.blockpos = tempList;
		this.world = world;
		this.blockStateToReplace = stateToReplace;
	}
	
	public BlockChangeQueue(Set<ChangeBlockToThis> tempList, World world, boolean replaceAll){
		this.blockpos = tempList;
		this.world = world;
		this.replaceAll = replaceAll;
		
	}
	
	public void perform(){		
		for(ChangeBlockToThis bpos : blockpos){
			
			if(replaceAll)
				world.setBlockState(bpos.getBlockPos(), bpos.getBlockState() != BlockLoader.tempBlock.getDefaultState() ? bpos.getBlockState() : Blocks.air.getDefaultState());
			if(!replaceAll)
				if(world.getBlockState(bpos.getBlockPos()) == blockStateToReplace){
					world.setBlockState(bpos.getBlockPos(), bpos.getBlockState() != BlockLoader.tempBlock.getDefaultState() ? bpos.getBlockState() : Blocks.air.getDefaultState());
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
