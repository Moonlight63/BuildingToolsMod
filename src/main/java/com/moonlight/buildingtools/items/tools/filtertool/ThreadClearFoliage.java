// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadClearFoliage.java

package com.moonlight.buildingtools.items.tools.filtertool;

import com.google.common.base.Optional;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ThreadClearFoliage
    implements IShapeable, BlockChangeBase
{

    public ThreadClearFoliage(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, boolean alsofill, EnumFacing side, 
            EntityPlayer entity)
    {
        isFinished = false;
        count = 0;
        blocksCalculated = false;
        this.alsofill = false;
        currentlyCalculating = false;
        tempList = new HashSet();
        filledList = new CopyOnWriteArraySet();
        checkedList = new HashSet();
        this.world = world;
        this.origin = origin;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
        this.side = side;
        this.entity = entity;
        this.alsofill = alsofill;
    }

    public void setBlock(BlockPos tempPos)
    {
        BlockPos bpos = tempPos;
        if(count < 4096 && !checkedList.contains(tempPos))
        {
            checkedList.add(tempPos);
            if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && ((world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockDoublePlant) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockLeaves) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockBush) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockTallGrass) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockCrops) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockFlower) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockVine) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockReed) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockSapling) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockWeb) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockCactus) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockLilyPad) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockSign) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockStem)))
            {
                if(alsofill)
                {
                    tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.stone.getDefaultState()));
                    filledList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.air.getDefaultState()));
                } else
                {
                    tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.air.getDefaultState()));
                }
                System.out.println((new StringBuilder("Setblock ")).append(count).toString());
                count++;
            }
        }
    }

    public Set ClearBlocks()
    {
        Set tempList = new HashSet();
        int passCount = 0;
        currentlyCalculating = true;
        for(Iterator iterator = filledList.iterator(); iterator.hasNext();)
        {
            ChangeBlockToThis block = (ChangeBlockToThis)iterator.next();
            if(passCount >= 4096)
                break;
            tempList.add(block);
            filledList.remove(block);
            passCount++;
        }

        return tempList;
    }

    public void perform()
    {
        if(!currentlyCalculating)
        {
            this.tempList.clear();
            if(!blocksCalculated)
                Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
            if(!this.tempList.isEmpty())
            {
                if(this.tempList != null)
                {
                    ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
                    ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).pendingChangeQueue.add(new BlockChangeQueue(this.tempList, world, true));
                }
            } else
            if(!filledList.isEmpty())
            {
                blocksCalculated = true;
                if(filledList != null)
                {
                    Set tempList = ClearBlocks();
                    ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
                    ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).pendingChangeQueue.add(new BlockChangeQueue(tempList, world, true));
                }
            } else
            {
            	MiscUtils.dumpUndoList(entity);
                isFinished = true;
            }
            currentlyCalculating = false;
            count = 0;
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
    protected Set tempList;
    protected Set filledList;
    protected Set checkedList;
	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}
}
