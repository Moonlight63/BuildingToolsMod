// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadTopsoil.java

package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

public class ThreadMakeTree implements BlockChangeBase{
	
    protected World world;
    protected BlockPos origin;
    protected boolean isFinished;
    protected EntityPlayer entity;
    protected int count;
    protected boolean currentlyCalculating;
    protected Set<ChangeBlockToThis> tempList;
    protected Set<BlockPos> checkedList;
    protected CustomTreeTest treeGen;
    protected boolean dataRecived = false;
    protected Set<ChangeBlockToThis> data;

    public ThreadMakeTree(World world, BlockPos origin, EntityPlayer entity){
        isFinished = false;
        count = 0;
        currentlyCalculating = false;
        tempList = new HashSet<ChangeBlockToThis>();
        data = new HashSet<ChangeBlockToThis>();
        checkedList = new HashSet<BlockPos>();
        this.world = world;
        this.origin = origin;
        this.entity = entity;
        this.treeGen = new CustomTreeTest(origin);
    }
    
    

    public void perform()
    {
    	
//    	if(!treeGen.isGenerating && !dataRecived){
//    		currentlyCalculating = true;
//    		if(tempList.addAll(treeGen.data)){
//    			dataRecived = true;
//    		}
//    	}
    	
//    	if(dataRecived && !treeGen.isGenerating){
//    		currentlyCalculating = true;
//    		tempList.clear();
//    		count = 0;
//    		for(ChangeBlockToThis change : treeGen.data){
//    			count++;
//    			tempList.add(change);
//    			treeGen.data.remove(change);
//    			if(count > 4096)
//    				break;
//    		}
//    		currentlyCalculating = false;
//    	}
    	
    	if(!treeGen.isGenerating){
    		System.out.println("TreeGen Finished Generating");
	    	if(!treeGen.data.isEmpty() && treeGen.data != null)
	        {
	    		System.out.println("TreeGen is not empty");
	            ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList.addAll(MiscUtils.CalcUndoList(treeGen.data, world));
	            ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).pendingChangeQueue.add(new BlockChangeQueue(treeGen.data, world, true));
	            MiscUtils.dumpUndoList(entity);
	            isFinished = true;
	            
	        }
			
			if(count < 4096)
	        {
	        	
	        }
    	}
        
    }
    
    public World getWorld()
    {
        return world;
    }

    public boolean isFinished()
    {
        return isFinished;
    }
	
}
