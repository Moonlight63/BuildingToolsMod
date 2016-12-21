package com.moonlight.buildingtools.items.tools.buildingtool;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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

		return blocks;
		
	}
	
	
	@Override
	public void setBlock(BlockPos bpos) {
		blocks.add(bpos);
	}
	
	public BlockPos CalcOffset(BlockPos bpos, BlockPos targetBlock, EnumFacing targetFace, World world){
		
		BlockPos bpos2;
			
		if (targetFace == EnumFacing.UP || targetFace == EnumFacing.DOWN){
			bpos2 = new BlockPos(bpos.getX(), targetFace == EnumFacing.UP ? bpos.getY() : -bpos.getY(), bpos.getZ());
		}
		else if (targetFace == EnumFacing.NORTH || targetFace == EnumFacing.SOUTH){
			bpos2 = new BlockPos(bpos.getX(), bpos.getZ(), targetFace == EnumFacing.NORTH ? -bpos.getY() : bpos.getY());
		}
		else{
			bpos2 = new BlockPos(targetFace == EnumFacing.WEST ? -bpos.getY() : bpos.getY(), bpos.getX(), bpos.getZ());
		}
		
        if(bpos2.add(targetBlock).getY() > 0 && bpos2.add(targetBlock).getY() < 256){
        	if(!replaceblock && world.getBlockState(bpos2.add(targetBlock)) != world.getBlockState(targetBlock))
        		return null;
        	if(world.isAirBlock(bpos2.add(targetBlock)))
				return null;
			if(!world.isAirBlock(bpos2.add(targetBlock).offset(targetFace)))
				return null;
        	return bpos2.add(targetBlock).offset(targetFace);
        }
        return null;
	
	}

	@Override
	public void shapeFinished() {
		finishedGenerating = true;
	}

}
