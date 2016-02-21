package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.BlockReed;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockVine;
import net.minecraft.block.BlockWeb;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class FilterShapeVisualizer implements IShapeable{

	public Set<BlockPos> blocks = new LinkedHashSet<BlockPos>();
	
	public boolean finishedGenerating = false;
	
	int filterType;
	int x;
	int y;
	int z;

	
	public FilterShapeVisualizer() {
		
	}
	
	public void RegenShape(IShapeGenerator generator, int x, int y, int z, int filterType){
		
		//System.out.println("Starting Regen");
		this.filterType = filterType;
		this.x = x;
		this.y = y;
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
		
        if(filterType == 1)
        {
            if(!world.isAirBlock(bpos.add(targetBlock)) && world.isAirBlock(bpos.add(targetBlock).up())){
                return (new BlockPos(bpos.add(targetBlock)));
            }
        } 
        else if(filterType == 2)
        {
            if(
            		!world.isAirBlock(bpos.add(targetBlock)) 
            		&& 
            		(
	            		world.getBlockState(bpos.add(targetBlock)) == Blocks.water.getDefaultState() ||
	            		world.getBlockState(bpos.add(targetBlock)) == Blocks.flowing_water.getDefaultState() ||
	            		world.getBlockState(bpos.add(targetBlock)) == Blocks.lava.getDefaultState() ||
	            		world.getBlockState(bpos.add(targetBlock)) == Blocks.flowing_lava.getDefaultState()
            		)
            		&& 
	            	(
	            		world.getBlockState(bpos.add(targetBlock).up()) != Blocks.water.getDefaultState() &&
	            		world.getBlockState(bpos.add(targetBlock).up()) != Blocks.flowing_water.getDefaultState() &&
	            		world.getBlockState(bpos.add(targetBlock).up()) != Blocks.lava.getDefaultState() &&
	            		world.getBlockState(bpos.add(targetBlock).up()) != Blocks.flowing_lava.getDefaultState()
	            	)
            	){
            	
            	return (new BlockPos(bpos.add(targetBlock)));
            	
            }
        }
        else if(filterType == 3){
        	if(
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockDoublePlant) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockLeaves) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockBush) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockTallGrass) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockCrops) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockFlower) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockVine) || 
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockReed) || 
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockSapling) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockWeb) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockCactus) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockLilyPad) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockSign) ||
        			(world.getBlockState(bpos.add(targetBlock)).getBlock() instanceof BlockStem)
        		){
        		
        		return (new BlockPos(bpos.add(targetBlock)));
        		
        	}
        }
        else if(filterType == 4)
        {
            if(world.getBlockState(bpos.add(targetBlock)) == Blocks.grass.getDefaultState() && world.isAirBlock(bpos.add(targetBlock).up())){
            	return (new BlockPos(bpos.add(targetBlock)));
            }
        } 
        
        return null;
        
	}

	@Override
	public void shapeFinished() {
		finishedGenerating = true;
	}

	
	
}
