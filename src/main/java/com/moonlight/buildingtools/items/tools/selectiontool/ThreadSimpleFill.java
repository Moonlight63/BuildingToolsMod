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
import com.moonlight.buildingtools.utils.MiscUtils;

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
				
				System.out.println(world.getBlockState(bpos) + "         " + replaceBlockState);
				
				if(replaceBlockState != null){
					if(replaceBlockState.getBlock() == Blocks.flowing_water){
						if(!(world.getBlockState(bpos).getBlock() == Blocks.flowing_water || world.getBlockState(bpos).getBlock() == Blocks.water)){
							return;
						}
					}
					else if(replaceBlockState.getBlock() == Blocks.flowing_lava){
						if(!(world.getBlockState(bpos).getBlock() == Blocks.flowing_lava || world.getBlockState(bpos).getBlock() == Blocks.lava))
							return;
					}
					else{
						if(world.getBlockState(bpos) != replaceBlockState)
							if(replaceBlockState == BlockLoader.tempBlock.getDefaultState() && !world.isAirBlock(bpos))
								return;
							else if (replaceBlockState != BlockLoader.tempBlock.getDefaultState())
								return;
					}
				}
			
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
				
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
				
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(tempList, world, true));
				
			}
				
			if(count < 4096){
				isFinished = true;
				MiscUtils.dumpUndoList(entity);
				//BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//System.out.println("Added all blocks to undo list: " + BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist);
			}
			
			count = 0;
			currentlyCalculating = false;
			
		}
	}
	
	public boolean isFinished(){
		return isFinished;
	}

	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}

}
