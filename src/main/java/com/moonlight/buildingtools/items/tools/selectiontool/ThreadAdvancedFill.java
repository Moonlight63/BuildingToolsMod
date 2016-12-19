package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.GeometryUtils;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

public class ThreadAdvancedFill implements BlockChangeBase, IShapeable{
	
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
	
	protected List<IBlockState> fillBlockState = Lists.<IBlockState>newArrayList();
	protected List<IBlockState> replaceBlockState = Lists.<IBlockState>newArrayList();
	protected List<Integer> fillBlockChance = Lists.<Integer>newArrayList();
	protected int count = 0;
	protected boolean shapeFinished = false;
	
	
	public ThreadAdvancedFill(BlockPos blockpos1, BlockPos blockpos2, World world, EntityPlayer player, NBTTagCompound nbtData){
		System.out.println("Thread Started");
		if(blockpos1 != null && blockpos2 != null)
			this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		else{
			System.out.println(blockpos1 + "" + blockpos2); return;}
		
		this.world = world;		
		this.entity = player;
		
		NBTTagCompound fillBlocks = nbtData.getCompoundTag("fillblocks");
		for(String key : fillBlocks.getKeySet()){
			ItemStack item = new ItemStack(fillBlocks.getCompoundTag(key).getCompoundTag("blockstate"));			
			this.fillBlockState.add(Block.getBlockFromItem(item.getItem()).getStateFromMeta(item.getMetadata()));
			this.fillBlockChance.add(fillBlocks.getCompoundTag(key).getInteger("chance"));
		}
		
		System.out.println(this.fillBlockState);
		
		this.replaceBlockState.clear();
		NBTTagCompound replaceBlocks = nbtData.getCompoundTag("replaceblocks");
		for(String key : replaceBlocks.getKeySet()){
			ItemStack item = new ItemStack(replaceBlocks.getCompoundTag(key));
			this.replaceBlockState.add(Block.getBlockFromItem(item.getItem()).getStateFromMeta(item.getMetadata()));
			System.out.println(this.replaceBlockState);
		}
		
	}
	
	@Override
	public void setBlock(BlockPos bpos){
		
		currentlyCalculating = true;
		
		System.out.println(world.getBlockState(bpos));
		
		if(fillBlockState.isEmpty())
			return;
		
		if(!replaceBlockState.isEmpty() && !replaceBlockState.contains(world.getBlockState(bpos).getActualState(world, bpos)))
			return;
		
		IBlockState blockstate = Blocks.AIR.getDefaultState();
		
		int chanceTotal = 0;
		
		for (Integer integer : fillBlockChance) {
			chanceTotal += integer;
		}
		if(chanceTotal == 0)
			chanceTotal = 1;
		
		int random = new Random().nextInt(chanceTotal);
		
		int curVal = 0;
		for(int i = 0; i < fillBlockChance.size(); i++){
			curVal += fillBlockChance.get(i);
			
			if(random < curVal){
				blockstate = fillBlockState.get(i);
				break;
			}
		}
	
		tempList.add(new ChangeBlockToThis(bpos, blockstate));
		count++;
		
		if(count > 4096){
			checkAndAddQueue();
		}
		
	}
	
	public void perform(){
		
		if(!currentlyCalculating){
			tempList.clear();
			GeometryUtils.makeFilledCube(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ), structureBoundingBox.getXSize()-1, structureBoundingBox.getYSize()-1, structureBoundingBox.getZSize()-1, this);
		}
		
		if(shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}
		
	}
	
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
