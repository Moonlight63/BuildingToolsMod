// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadTopsoil.java

package com.moonlight.buildingtools.items.tools.filtertool;

import com.google.common.base.Optional;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.*;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import java.io.PrintStream;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ThreadTopsoil
    implements IShapeable, BlockChangeBase
{

    public ThreadTopsoil(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, int depth, EnumFacing side, 
            EntityPlayer entity)
    {
        isFinished = false;
        count = 0;
        currentlyCalculating = false;
        tempList = new HashSet();
        checkedList = new HashSet();
        this.world = world;
        this.origin = origin;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
        this.depth = depth;
        this.side = side;
        this.entity = entity;
    }

    public void setBlock(BlockPos tempPos)
    {
        BlockPos bpos = tempPos;
        if(count < 4096 && !checkedList.contains(tempPos))
        {
            checkedList.add(tempPos);
            if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && !world.isAirBlock(bpos.add(origin)) && world.isAirBlock(bpos.add(origin).up()))
            {
                tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.grass.getDefaultState()));
                for(int i = 1; i < depth; i++)
                {
                    tempList.add(new ChangeBlockToThis(bpos.add(origin).down(i), Blocks.dirt.getDefaultState()));
                    count++;
                }

                System.out.println((new StringBuilder("Setblock ")).append(count).toString());
                count++;
            }
        }
    }

    public void perform()
    {
        if(!currentlyCalculating)
        {
            tempList.clear();
            Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
            if(!tempList.isEmpty() && tempList != null)
            {
                ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList.addAll(CalcUndoList(tempList));
                ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).pendingChangeQueue = new BlockChangeQueue(tempList, world, true);
            }
            if(count < 4096)
            {
                if(((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).undolist.add(new LinkedHashSet(((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList)))
                    ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList.clear();
                isFinished = true;
            }
            currentlyCalculating = false;
            count = 0;
        }
    }

    public Set CalcUndoList(Set tempList)
    {
        Set newTempList = new LinkedHashSet();
        ChangeBlockToThis pos;
        for(Iterator iterator = tempList.iterator(); iterator.hasNext(); newTempList.add(addBlockWithNBT(pos.getBlockPos(), world.getBlockState(pos.getBlockPos()), pos.getBlockPos())))
            pos = (ChangeBlockToThis)iterator.next();

        return newTempList;
    }

    public ChangeBlockToThis addBlockWithNBT(BlockPos oldPosOrNull, IBlockState blockState, BlockPos newPos)
    {
        if(oldPosOrNull != null && world.getTileEntity(oldPosOrNull) != null)
        {
            NBTTagCompound compound = new NBTTagCompound();
            world.getTileEntity(oldPosOrNull).writeToNBT(compound);
            return new ChangeBlockToThis(newPos, blockState, compound);
        }
        if(blockState.getBlock() instanceof BlockDoor)
        {
            if(blockState.getValue(BlockDoor.HALF) == net.minecraft.block.BlockDoor.EnumDoorHalf.LOWER)
                return new ChangeBlockToThis(newPos, blockState.withProperty(BlockDoor.HINGE, (net.minecraft.block.BlockDoor.EnumHingePosition)world.getBlockState(oldPosOrNull.up()).getValue(BlockDoor.HINGE)));
            if(blockState.getValue(BlockDoor.HALF) == net.minecraft.block.BlockDoor.EnumDoorHalf.UPPER)
                return new ChangeBlockToThis(newPos, blockState.withProperty(BlockDoor.FACING, (EnumFacing)world.getBlockState(oldPosOrNull.down()).getValue(BlockDoor.FACING)));
        }
        return new ChangeBlockToThis(newPos, blockState);
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
    protected int depth;
    protected EnumFacing side;
    protected boolean isFinished;
    protected EntityPlayer entity;
    protected int count;
    protected boolean currentlyCalculating;
    protected Set tempList;
    protected Set checkedList;
}
