// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadClearWater.java

package com.moonlight.buildingtools.items.tools.filtertool;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.*;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadClearWater implements IShapeable, BlockChangeBase{
	
	protected World world;
    protected BlockPos origin;
    protected int radiusX;
    protected int radiusY;
    protected int radiusZ;
    protected EnumFacing side;
    protected boolean isFinished;
    protected EntityPlayer entity;
    protected int count;
    protected boolean blocksCalculated;
    protected boolean alsofill;
    protected boolean currentlyCalculating;
    protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
    protected Set<ChangeBlockToThis> filledList = new CopyOnWriteArraySet<ChangeBlockToThis>();
    protected Set<BlockPos> checkedList = new HashSet<BlockPos>();
    
	protected boolean shapeFinished = false;

    public ThreadClearWater(World world, BlockPos origin, EnumFacing side, EntityPlayer entity, NBTTagCompound nbtdata){
        this.world = world;
        this.origin = origin;
        this.side = side;
        this.entity = entity;
        this.radiusX = nbtdata.getInteger("radiusX");
        this.radiusY = nbtdata.getInteger("radiusY");
        this.radiusZ = nbtdata.getInteger("radiusZ");
        this.alsofill = nbtdata.getInteger("fillorclear") != 1;
    }

    public void setBlock(BlockPos tempPos)
    {
        BlockPos bpos = tempPos;
        if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && !world.isAirBlock(bpos.add(origin)) && (
        		world.getBlockState(bpos.add(origin)) == Blocks.WATER.getDefaultState() || world.getBlockState(bpos.add(origin)) == Blocks.FLOWING_WATER.getDefaultState()
        		|| world.getBlockState(bpos.add(origin)) == Blocks.LAVA.getDefaultState() || world.getBlockState(bpos.add(origin)) == Blocks.FLOWING_LAVA.getDefaultState()))
        {
            if(alsofill)
            {
                tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.STONE.getDefaultState()));
                filledList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.AIR.getDefaultState()));
            } else
            {
                tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.AIR.getDefaultState()));
            }
            System.out.println((new StringBuilder("Setblock ")).append(count).toString());
            count++;
        }
        
        if(count > 4096){
        	checkAndAddQueue();
        }
        
    }

    public void ClearBlocks()
    {
        currentlyCalculating = true;
        shapeFinished = false;
        for(Iterator<ChangeBlockToThis> iterator = filledList.iterator(); iterator.hasNext();)
        {
            ChangeBlockToThis block = (ChangeBlockToThis)iterator.next();
            tempList.add(block);
            count++;
            if(count > 4096){
            	checkAndAddQueue();
            }
        }
        
        shapeFinished();

    }

    public void perform()
    {
        if(!currentlyCalculating){
            this.tempList.clear();
            Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
            ClearBlocks();
        }
        
        if(shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}
    }


    public World getWorld(){
        return world;
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
