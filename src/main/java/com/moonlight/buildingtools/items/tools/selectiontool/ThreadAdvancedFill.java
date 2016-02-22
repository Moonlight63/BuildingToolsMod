package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
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
	
	protected List<Set<ChangeBlockToThis>> listSet = Lists.newArrayList();
	
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
	
	public ThreadAdvancedFill(BlockPos blockpos1, BlockPos blockpos2, World world, EntityPlayer player, List<IBlockState> fillBlock, List<Integer> fillChance){
		System.out.println("Thread Started");
		if(blockpos1 != null && blockpos2 != null)
			this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		else{
			System.out.println(blockpos1 + "" + blockpos2); return;}
		
		this.world = world;		
		this.entity = player;
		this.fillBlockState = fillBlock;
		this.fillBlockChance = fillChance;
	}
	
	public ThreadAdvancedFill(BlockPos blockpos1, BlockPos blockpos2, World world, EntityPlayer player, List<IBlockState> fillBlock,List<IBlockState> replaceBlock, List<Integer> fillChance){
		System.out.println("Thread Started");
		if(blockpos1 != null && blockpos2 != null)
			this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		else{
			System.out.println(blockpos1 + "" + blockpos2); return;}
		
		this.world = world;		
		this.entity = player;
		this.fillBlockState = fillBlock;
		this.fillBlockChance = fillChance;
		this.replaceBlockState = replaceBlock;
		if(replaceBlockState.contains(BlockLoader.tempBlock.getDefaultState()))
			replaceBlockState.add(Blocks.air.getDefaultState());
	}
	
	@Override
	public void setBlock(BlockPos bpos){
		
		if(!replaceBlockState.isEmpty() && !replaceBlockState.contains(world.getBlockState(bpos)))
			return;
		
		currentlyCalculating = true;
		
		int chanceTotal = 0;
		
		for (Integer integer : fillBlockChance) {
			chanceTotal += integer;
		}
		
		int random = new Random().nextInt(chanceTotal);
		IBlockState blockstate = Blocks.air.getDefaultState();
		
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
			addSetToList();
		}
		
	}
	
	public void perform(){
		
		if(!currentlyCalculating){
			tempList.clear();
			GeometryUtils.makeFilledCube(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ), structureBoundingBox.getXSize()-1, structureBoundingBox.getYSize()-1, structureBoundingBox.getZSize()-1, this);
		}
		
		if(listSet.isEmpty() && shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}
		
	}
	
	public boolean isFinished(){
		return isFinished;
	}
	
	public void checkAndAddQueue(){
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(listSet.get(0), world));
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(listSet.get(0), world, true));
		listSet.remove(0);
	}
	
	public void addSetToList(){
		listSet.add(Sets.newHashSet(tempList));
		tempList.clear();
		count = 0;
		checkAndAddQueue();
	}

	@Override
	public void shapeFinished() {
		addSetToList();
		shapeFinished = true;
		currentlyCalculating = false;
	}

}
