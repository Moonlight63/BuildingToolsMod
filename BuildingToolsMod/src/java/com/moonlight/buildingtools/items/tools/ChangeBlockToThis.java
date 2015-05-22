package com.moonlight.buildingtools.items.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class ChangeBlockToThis {
	
	private final BlockPos thisPos;
	private final IBlockState changeToThis;
	private final NBTTagCompound nbttag;
	
	public ChangeBlockToThis(BlockPos pos, IBlockState state){
		this.thisPos = pos;
		this.changeToThis = state;		
		this.nbttag = (NBTTagCompound)null;
	}
	
	public ChangeBlockToThis(BlockPos pos, IBlockState state, NBTTagCompound compound){
		this.thisPos = pos;
		this.changeToThis = state;
		this.nbttag = compound;
		//System.out.println("ChangeBlock" + compound);
	}
	
	public BlockPos getBlockPos(){
		return this.thisPos;
	}
	
	public IBlockState getBlockState(){
		return this.changeToThis;
	}
	
	public NBTTagCompound getNBTTag(){
		//System.out.println("GetNBT" + this.nbttag);
		return this.nbttag;
	}

}
