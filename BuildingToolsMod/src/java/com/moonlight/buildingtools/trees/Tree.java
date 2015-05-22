package com.moonlight.buildingtools.trees;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class Tree {
	
	public IBlockState logBlock = Blocks.log.getDefaultState();
	public IBlockState leafBlock = Blocks.leaves.getDefaultState();
	
	public int trunkheight;
	public int height = 20;
	
	public Set<ChangeBlockToThis> logSet = new LinkedHashSet<ChangeBlockToThis>();
	public Set<ChangeBlockToThis> foliageSet = new LinkedHashSet<ChangeBlockToThis>();
	
	public void prepare(){}

	public void makeTrunk(){}
	
	public void makeFoliage(){}
	
	
	
}
