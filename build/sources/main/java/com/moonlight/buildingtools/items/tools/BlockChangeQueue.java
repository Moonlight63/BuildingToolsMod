package com.moonlight.buildingtools.items.tools;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;

public class BlockChangeQueue {
	
	protected World world;
	protected boolean isFinished = false;
	public Set<ChangeBlockToThis> blockpos = Sets.newConcurrentHashSet();
	protected IBlockState blockStateToPlace = Blocks.STONE.getDefaultState();
	protected IBlockState blockStateToReplace = Blocks.AIR.getDefaultState();
	protected boolean replaceAll = false;
	
	public BlockChangeQueue(Set<ChangeBlockToThis> tempList, World world, IBlockState stateToReplace){
		this.blockpos.addAll(tempList);
		this.world = world;
		this.blockStateToReplace = stateToReplace;
	}
	
	public BlockChangeQueue(Set<ChangeBlockToThis> tempList, World world, boolean replaceAll){
		this.blockpos.addAll(tempList);
		this.world = world;
		this.replaceAll = replaceAll;
		
	}
	
	public void perform(){
		for(ChangeBlockToThis bpos : blockpos){
			
			if(bpos.getBlockState() != BlockLoader.tempBlock.getDefaultState()){
				if(replaceAll)
					world.setBlockState(bpos.getBlockPos(), bpos.getBlockState());
				if(!replaceAll)
					if(world.getBlockState(bpos.getBlockPos()) == blockStateToReplace){
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
