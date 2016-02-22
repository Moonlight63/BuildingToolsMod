package com.moonlight.buildingtools.items.tools.undoTool;

import java.io.Serializable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockBanner.BlockBannerHanging;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import com.google.common.collect.ImmutableMap;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class BlockInfoContainer implements Serializable{
	
	private static final long serialVersionUID = -3457714512620564053L;
	public ChangeBlockToThis change;
	public BlockTypes blockType;
	public boolean setAir;
	
	public static enum BlockTypes{
		Standard,
		Door,
		TrapDoor,
		Torch,
		BannerHanging,
		Stairs,
		Rotating,
		Skull,
		Logs,
		Quartz_Pillar,
		Signs,
		Carpet,
		Lever,
		Slab;
	}
	
	public BlockInfoContainer(ChangeBlockToThis change){
		this.change = change;
		getBlockType();
	}
	
	private void getBlockType(){
		IBlockState blockState = this.change.getBlockState();
		NBTTagCompound compound = this.change.getNBTTag();
		Block block = blockState.getBlock();
		
		ImmutableMap<?, ?> properties = blockState.getProperties();
		IProperty<EnumFacing> directionalBlockProperty = PropertyDirection.create("facing"/*, EnumFacing.Plane.HORIZONTAL*/);
		IProperty<EnumAxis>   logDirectionProperty = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
		IProperty<EnumType>   quartzPillerProperty = PropertyEnum.create("variant", BlockQuartz.EnumType.class);
		IProperty<Integer>    bannerStandingRotation = PropertyInteger.create("rotation", 0, 15);
		
		if(block instanceof BlockAir){
			this.setAir = true;
			return;
		}
		
		//ROTATION STATES
		if(properties.containsKey(directionalBlockProperty)){
			
			//Doors
			if(block instanceof BlockDoor){
				this.blockType = BlockTypes.Door;
			}
			
			//TrapDoor
			else if(block instanceof BlockTrapDoor){
				this.blockType = BlockTypes.TrapDoor;
			}
			
			//Torch
			else if (block instanceof BlockTorch){
				this.blockType = BlockTypes.Torch;
			}
			
			//Hanging Banners
			else if (block instanceof BlockBannerHanging){
				this.blockType = BlockTypes.BannerHanging;
			}
			
			//Stairs
			else if (block instanceof BlockStairs) {
				this.blockType = BlockTypes.Stairs;
			}
			
			//Skull
			else if (block instanceof BlockSkull) {
				this.blockType = BlockTypes.Skull;
			}
			
			//Any Other Rotation Block
			else {
				this.blockType = BlockTypes.Rotating;
			}
			
		}
		
		//LOG
		else if(properties.containsKey(logDirectionProperty)){
			
			this.blockType = BlockTypes.Logs;
			
		}
		
		//QUARTZ
		else if(properties.containsKey(quartzPillerProperty)){
			
			this.blockType = BlockTypes.Quartz_Pillar;
			
		}
		
		//SIGNS
		else if(properties.containsKey(bannerStandingRotation)){
			
			this.blockType = BlockTypes.Signs;
			
		}
		
		//CARPET
		else if(block instanceof BlockCarpet || block instanceof BlockFlowerPot){
			
			this.blockType = BlockTypes.Carpet;
			
		}
		
		//LEVERS
		else if(block instanceof BlockLever){
			
			this.blockType = BlockTypes.Lever;
			
		}
		
		//STANDARD BLOCK
		else{
			
			this.blockType = BlockTypes.Standard;
			
		}
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
