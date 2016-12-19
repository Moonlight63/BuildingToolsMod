package com.moonlight.buildingtools.items.tools.undoTool;

import java.io.Serializable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockWallSign;
import net.minecraft.block.BlockBanner.BlockBannerHanging;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRedstoneComparator;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

import com.google.common.collect.ImmutableMap;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class BlockInfoContainer implements Serializable{
	
	private static final long serialVersionUID = -3457714512620564053L;
	public ChangeBlockToThis change;
	public BlockTypes blockType;
	public boolean needsSecondPass;
	public boolean setAir;
	
	public static enum BlockTypes{
		Standard,
		Door,
		TrapDoor,
		Torch,
		BannerHanging,
		Stairs,
		Ladder,
		Rotating,
		Skull,
		Logs,
		Quartz_Pillar,
		Signs,
		Carpet,
		Lever,
		Rail,
		RedStone,
		Buttons,
		TripWire,
		Slab;
	}
	
	public BlockInfoContainer(ChangeBlockToThis change){
		this.change = change;
		getBlockType();
	}
	
	public void getBlockType(){
		IBlockState blockState = this.change.getBlockState();
		//NBTTagCompound compound = this.change.getNBTTag();
		Block block = blockState.getBlock();
		
		ImmutableMap<?, ?> properties = blockState.getProperties();
		IProperty<Integer>    standingRotProperty = PropertyInteger.create("rotation", 0, 15);
		
		if(block instanceof BlockAir){
			this.setAir = true;
			return;
		}
		
		//Redstone
		if (block instanceof BlockRedstoneWire
				|| block instanceof BlockRedstoneDiode
				|| block instanceof BlockBasePressurePlate
				) {
			this.blockType = BlockTypes.RedStone;
			this.needsSecondPass = true;
		}
		
		//ROTATION STATES
		else if(properties.containsKey(BlockHorizontal.FACING)){
			
			//Doors
			if(block instanceof BlockDoor){
				this.blockType = BlockTypes.Door;
				this.needsSecondPass = true;
			}
			
			//TrapDoor
			else if(block instanceof BlockTrapDoor){
				this.blockType = BlockTypes.TrapDoor;
			}
			
			//Hanging Banners
			else if (block instanceof BlockBannerHanging){
				this.blockType = BlockTypes.BannerHanging;
				this.needsSecondPass = true;
			}
			
			//Stairs
			else if (block instanceof BlockStairs) {
				this.blockType = BlockTypes.Stairs;
			}
			
			//Ladders
			else if (block instanceof BlockLadder) {
				this.blockType = BlockTypes.Ladder;
				this.needsSecondPass = true;
			}
			
			//Skull
			else if (block instanceof BlockSkull) {
				this.blockType = BlockTypes.Skull;
			}
			
			//WallSign
			else if (block instanceof BlockWallSign) {
				this.blockType = BlockTypes.Skull;
				this.needsSecondPass = true;
			}
			
			//Any Other Rotation Block
			else {
				this.blockType = BlockTypes.Rotating;
			}
			
		}
		
		//Buttons
		else if (block instanceof BlockButton) {
			this.blockType = BlockTypes.Buttons;
			this.needsSecondPass = true;
		}
		
		//Tripwire
		else if (block instanceof BlockTripWireHook) {
			this.blockType = BlockTypes.TripWire;
			this.needsSecondPass = true;
		}

		//Torch
		else if (block instanceof BlockTorch){
			this.blockType = BlockTypes.Torch;
			this.needsSecondPass = true;
		}
		
		//LOG
		else if(properties.containsKey(BlockLog.LOG_AXIS)){
			this.blockType = BlockTypes.Logs;
		}
		
		//QUARTZ
		else if(properties.containsKey(BlockQuartz.VARIANT)){
			this.blockType = BlockTypes.Quartz_Pillar;
		}
		
		//SIGNS
		else if(properties.containsKey(standingRotProperty)){
			this.blockType = BlockTypes.Signs;
			this.needsSecondPass = true;
		}
		
		//CARPET
		else if(block instanceof BlockCarpet || block instanceof BlockFlowerPot){
			this.blockType = BlockTypes.Carpet;
			this.needsSecondPass = true;
		}
		
		//LEVERS
		else if(block instanceof BlockLever){
			this.blockType = BlockTypes.Lever;
			this.needsSecondPass = true;
		}
		
		//RAILS
		else if(block instanceof BlockRailBase){
			this.blockType = BlockTypes.Rail;
			this.needsSecondPass = true;
		}
		
		//SLABS
		else if(block instanceof BlockSlab){
			this.blockType = BlockTypes.Slab;
		}
		
		//STANDARD BLOCK
		else{
			this.blockType = BlockTypes.Standard;
		}
		
		
	}
	
}
