package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BuildingShapeVisualizer implements IShapeable{

	public Set<BlockPos> blocks = new LinkedHashSet<BlockPos>();
	
	public boolean finishedGenerating = false;
	
	boolean replaceblock;
	int x;
	int y;
	int z;

	
	public BuildingShapeVisualizer() {
		
	}
	
	public void RegenShape(IShapeGenerator generator, int x, int z, boolean replace){
		
		//System.out.println("Starting Regen");
		this.replaceblock = replace;
		this.x = x;
		this.z = z;
		this.blocks = Sets.newHashSet();
		blocks.clear();
		finishedGenerating = false;
		generator.generateShape(x, y, z, this, true);
	
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
	
	public BlockPos CalcOffset(BlockPos bpos, BlockPos targetBlock, EnumFacing targetFace, World world){
		
		if (targetFace == EnumFacing.UP || targetFace == EnumFacing.DOWN){
			if(!world.isAirBlock(new BlockPos(bpos.getX(), targetFace == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock).offset(targetFace))){
				return null;
			}
			if(world.isAirBlock(new BlockPos(bpos.getX(), targetFace == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock))){
				return null;
			}
			if(!this.replaceblock && world.getBlockState(new BlockPos(bpos.getX(), targetFace == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock))
        		return null;
			return (new BlockPos(bpos.getX(), targetFace == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock).offset(targetFace));
		}
		else if (targetFace == EnumFacing.NORTH || targetFace == EnumFacing.SOUTH){
			
			if(!world.isAirBlock(new BlockPos(bpos.getX(), bpos.getZ(), targetFace == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock).offset(targetFace))){
				return null;
			}
			if(world.isAirBlock(new BlockPos(bpos.getX(), bpos.getZ(), targetFace == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock))){
				return null;
			}
			if(!this.replaceblock && world.getBlockState(new BlockPos(bpos.getX(), bpos.getZ(), targetFace == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock)) != world.getBlockState(targetBlock))
        		return null;
			return (new BlockPos(bpos.getX(), bpos.getZ(), targetFace == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock).offset(targetFace));
		}
		else{
			
			if(!world.isAirBlock(new BlockPos(targetFace == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock).offset(targetFace))){
				return null;
			}
			if(world.isAirBlock(new BlockPos(targetFace == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock))){
				return null;
			}
			if(!this.replaceblock && world.getBlockState(new BlockPos(targetFace == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock))
        		return null;
			return (new BlockPos(targetFace == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock).offset(targetFace));
		}
	
	}

	@Override
	public void shapeFinished() {
		finishedGenerating = true;
	}

	
	
}
