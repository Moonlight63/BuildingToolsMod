package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.GeometryUtils;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class ThreadSimpleFill implements BlockChangeBase, IShapeable{
	
	protected StructureBoundingBox structureBoundingBox;
	protected AxisAlignedBB entityDetectionBox;
	protected World world;
	protected EntityPlayer entity;
	
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	protected Set<BlockPos> checkList = new HashSet<BlockPos>();
	
	protected boolean isFinished = false;
	protected Set<ChangeBlockToThis> selectionSet = new LinkedHashSet<ChangeBlockToThis>();
	protected Set<Entity> entitySet = new LinkedHashSet<Entity>();
	
	public boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	protected IBlockState fillBlockState;
	protected IBlockState replaceBlockState;
	protected int count = 0;
	
	public ThreadSimpleFill(BlockPos blockpos1, BlockPos blockpos2, World world, EntityPlayer player, IBlockState fillBlock){
		System.out.println("Thread Started");
		if(blockpos1 != null && blockpos2 != null)
			this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		else{
			System.out.println(blockpos1 + "" + blockpos2); return;}
		
		this.world = world;		
		this.entity = player;
		this.fillBlockState = fillBlock;
	}
	
	public ThreadSimpleFill(BlockPos blockpos1, BlockPos blockpos2, World world, EntityPlayer player, IBlockState fillBlock, IBlockState replaceBlock){
		System.out.println("Thread Started");
		if(blockpos1 != null && blockpos2 != null)
			this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		else{
			System.out.println(blockpos1 + "" + blockpos2); return;}
		
		this.world = world;		
		this.entity = player;
		this.fillBlockState = fillBlock;
		this.replaceBlockState = replaceBlock;
	}
	
	@Override
	public void setBlock(BlockPos bpos){
		//System.out.println(world);
		
		if(count < 4096){
			
			if(!checkList.contains(bpos)){
				
				if(replaceBlockState != null && world.getBlockState(bpos) != replaceBlockState)
					if(replaceBlockState == BlockLoader.tempBlock.getDefaultState() && !world.isAirBlock(bpos))
						return;
					else if (replaceBlockState != BlockLoader.tempBlock.getDefaultState())
						return;
			
				currentlyCalculating = true;
			
				tempList.add(new ChangeBlockToThis(bpos, fillBlockState));
				checkList.add(bpos);
				count++;
				
			}
		}
		else{
			return;
		}
		
		
	}
	
	int tempCount = 0;
	public void perform(){
		
		if(!currentlyCalculating){
			tempCount++;
			System.out.println(tempCount);
			
			tempList.clear();
			
			GeometryUtils.makeFilledCube(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ), structureBoundingBox.getXSize()-1, structureBoundingBox.getYSize()-1, structureBoundingBox.getZSize()-1, this);
			
			if(!tempList.isEmpty() && tempList != null){
				
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(CalcUndoList(tempList));
				
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, true);
				
			}
				
			if(count < 4096){
				isFinished = true;
				if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist.add(new LinkedHashSet<ChangeBlockToThis>((BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList))))
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//System.out.println("Added all blocks to undo list: " + BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist);
			}
			
			count = 0;
			currentlyCalculating = false;
			
		}
	}
	
	public ChangeBlockToThis addBlockWithNBT(BlockPos oldPosOrNull, IBlockState blockState, BlockPos newPos){
		if(oldPosOrNull != null && world.getTileEntity(oldPosOrNull) != null){
    		NBTTagCompound compound = new NBTTagCompound();
    		world.getTileEntity(oldPosOrNull).writeToNBT(compound);
    		return new ChangeBlockToThis(newPos, blockState, compound);
		}
    	else{
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
	
	public Set<ChangeBlockToThis> CalcUndoList(Set<ChangeBlockToThis> tempList){
		Set<ChangeBlockToThis> newTempList = new LinkedHashSet<ChangeBlockToThis>();
		
		for(ChangeBlockToThis pos : tempList){
			newTempList.add(addBlockWithNBT(pos.getBlockPos(), world.getBlockState(pos.getBlockPos()), pos.getBlockPos()));
		}
		
		return newTempList;
	}
	
	public boolean isFinished(){
		return isFinished;
	}

}
