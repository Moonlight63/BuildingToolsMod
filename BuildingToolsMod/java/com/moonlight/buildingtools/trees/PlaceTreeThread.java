package com.moonlight.buildingtools.trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.WorldEventHandler;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.helpers.shapes.MathUtils;
import com.moonlight.buildingtools.helpers.shapes.GeometryUtils.Octant;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class PlaceTreeThread implements BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	
	protected boolean currentlyCalculating = false;
	protected boolean treeCalculated = false;
	
	//protected List<BlockPos> tempList = new ArrayList<BlockPos>();
	protected Set<ChangeBlockToThis> treeList = new CopyOnWriteArraySet<ChangeBlockToThis>();
	
	public PlaceTreeThread(World world, BlockPos origin, EntityPlayer entity){
		this.world = world;
		this.origin = origin;
		this.entity = entity;
	}
	
	public Set RunPass(){
		System.out.println("Running Pass");
		currentlyCalculating = true;
		Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
		int passCount = 0;
		for(ChangeBlockToThis change : treeList){
			passCount++;
			if(passCount < 4096){
				tempList.add(new ChangeBlockToThis(change.getBlockPos().add(origin), change.getBlockState()));
				treeList.remove(change);
			}
			else{
				break;
			}
		}
		return tempList;
	}
	
	public void CreateTree(){
		
		System.out.println("Creating Tree");
		
		currentlyCalculating = true;
		
		Tree tree = new ProcedTreeTest();
		
		tree.prepare();
		tree.makeTrunk();
		tree.makeFoliage();
		
		
		treeList.addAll(tree.foliageSet);
		treeList.addAll(tree.logSet);
		
		treeCalculated = true;
		
	}
	
	public void perform(){
		System.out.println("PlaceTreeThread.perform()");
		if(!currentlyCalculating){
			if(!treeCalculated){
				System.out.println("Sending Create Tree");
				CreateTree();
				currentlyCalculating = false;
			}
			else{
				if(!treeList.isEmpty()){
					System.out.println("Sending Run Tree Pass");
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(RunPass(), world, true);
					currentlyCalculating = false;
				}
				else{
					System.out.println("Finished");
					isFinished = true;
				}
			}			
		}
	}
	
    public World getWorld()
    {
        return this.world;
    }

	public boolean isFinished(){
		return isFinished;
	}
	
}
