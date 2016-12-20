package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BrushShapeVisualizer implements IShapeable{

	public Set<BlockPos> blocks = new LinkedHashSet<BlockPos>();
	
	public boolean finishedGenerating = false;
	
	public IShapeGenerator currentGen;
	
	int replaceblock;
	int x;
	int y; 
	int z;
	
	public List<IBlockState> replaceBlocks = new LinkedList<IBlockState>();

	
	public BrushShapeVisualizer() {
		
	}
	
	@SuppressWarnings("deprecation")
	public void RegenShape(IShapeGenerator generator, int x, int y, int z, int replace, NBTTagCompound replaceBlocksNBT){
		
		//System.out.println("Starting Regen");
		this.replaceblock = replace;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blocks = Sets.newHashSet();
		blocks.clear();
		finishedGenerating = false;
		this.currentGen = generator;
		generator.generateShape(x, y, z, this, replace == 2 || replace == 4);
		
		replaceBlocks.clear();
		for(String key : replaceBlocksNBT.getKeySet()){
			ItemStack item = new ItemStack(replaceBlocksNBT.getCompoundTag(key));
			item.deserializeNBT(replaceBlocksNBT.getCompoundTag("blockstate"));
			this.replaceBlocks.add(Block.getBlockFromItem(item.getItem()).getStateFromMeta(item.getMetadata()));
		}
		//this.replaceBlocks = replaceBlocks;
	}
	
	public Set<BlockPos> GetBlocks(){
		
		if(blocks == null){
			return null;
		}
		if(blocks.isEmpty()){
			return null;
		}
		if(!finishedGenerating){
			return null;
		}
		//System.out.println("Block Set");
		return blocks;
		
	}
	
	
	@Override
	public void setBlock(BlockPos bpos) {
		blocks.add(bpos);
	}
	
	public BlockPos CalcOffset(BlockPos bpos, BlockPos targetBlock, EnumFacing face, World world){
		
		BlockPos tempPos;
		
		if (face == EnumFacing.UP || face == EnumFacing.DOWN){
			tempPos = new BlockPos(bpos.getX(), face == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ());
		}
		else if (face == EnumFacing.NORTH || face == EnumFacing.SOUTH){
			tempPos = new BlockPos(bpos.getX(), bpos.getZ(), face == EnumFacing.NORTH ? -bpos.getY() : bpos.getY());
		}
		else{
			tempPos = new BlockPos(face == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ());
		}
		
		if(replaceblock == 1){
			if(!world.isAirBlock(tempPos.add(targetBlock))){
				return null;
			}
		}else if(replaceblock == 2){
			if(world.getBlockState(tempPos.add(targetBlock)) != world.getBlockState(targetBlock)){
				return null;
			}
		}else if(replaceblock == 3){
		}
		else if(replaceblock == 4){
			if(!this.replaceBlocks.contains(world.getBlockState(tempPos.add(targetBlock))))
				return null;
		}
		
		return tempPos.add(targetBlock);
	
	}

	@Override
	public void shapeFinished() {
		finishedGenerating = true;
	}

	
	
}
