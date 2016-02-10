package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BrushShapeVisualizer implements IShapeable{

	public Set<BlockPos> blocks = new LinkedHashSet<BlockPos>();
	
	public boolean finishedGenerating = false;
	
	public IShapeGenerator currentGen;
	
	int replaceblock;
	int x;
	int y; 
	int z;

	
	public BrushShapeVisualizer() {
		
	}
	
	public void RegenShape(IShapeGenerator generator, int x, int y, int z, int replace){
		
		//System.out.println("Starting Regen");
		this.replaceblock = replace;
		this.x = x;
		this.y = y;
		this.z = z;
		this.blocks = Sets.newHashSet();
		blocks.clear();
		finishedGenerating = false;
		this.currentGen = generator;
		generator.generateShape(x, y, z, this, replace == 2);
	
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
		
		if (face == EnumFacing.UP || face == EnumFacing.DOWN){
			
			if(replaceblock == 1){
				if(!world.isAirBlock(new BlockPos(bpos.getX(), face == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock))){
					return null;
				}
			}else if(replaceblock == 2){
				if(world.getBlockState(new BlockPos(bpos.getX(), face == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock)){
					return null;
				}
			}else if(replaceblock == 3){
			}
			
			return (new BlockPos(bpos.getX(), face == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ()).add(targetBlock));
		}
		else if (face == EnumFacing.NORTH || face == EnumFacing.SOUTH){
			
			if(replaceblock == 1){
				if(!world.isAirBlock(new BlockPos(bpos.getX(), bpos.getZ(), face == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock))){
					return null;
				}
			}else if(replaceblock == 2){
				if(world.getBlockState(new BlockPos(bpos.getX(), bpos.getZ(), face == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock)) != world.getBlockState(targetBlock)){
					return null;
				}
			}else if(replaceblock == 3){
			}
			
			return (new BlockPos(bpos.getX(), bpos.getZ(), face == EnumFacing.NORTH ? -bpos.getY() : bpos.getY()).add(targetBlock));
		}
		else{
			
			if(replaceblock == 1){
				if(!world.isAirBlock(new BlockPos(face == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock))){
					return null;
				}
			}else if(replaceblock == 2){
				if(world.getBlockState(new BlockPos(face == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock)) != world.getBlockState(targetBlock)){
					return null;
				}
			}else if(replaceblock == 3){
			}
			
			return (new BlockPos(face == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ()).add(targetBlock));
		}
	
	}

	@Override
	public void shapeFinished() {
		finishedGenerating = true;
	}

	
	
}
