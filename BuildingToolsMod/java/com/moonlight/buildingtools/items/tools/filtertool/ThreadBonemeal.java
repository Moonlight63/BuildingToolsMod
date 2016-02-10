// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadTopsoil.java

package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
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

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

public class ThreadBonemeal implements IShapeable, BlockChangeBase{
	
    protected World world;
    protected BlockPos origin;
    protected int radiusX;
    protected int radiusY;
    protected int radiusZ;
    protected EnumFacing side;
    protected boolean isFinished;
    protected EntityPlayer entity;
    protected int count;
    protected boolean currentlyCalculating;
    protected Set<ChangeBlockToThis> tempList;
    protected Set<BlockPos> checkedList;

    public ThreadBonemeal(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, EnumFacing side, EntityPlayer entity){
        isFinished = false;
        count = 0;
        currentlyCalculating = false;
        tempList = new HashSet<ChangeBlockToThis>();
        checkedList = new HashSet<BlockPos>();
        this.world = world;
        this.origin = origin;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
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
            	if(world.rand.nextInt(8) <= 0){
	            	if(world.getBlockState(bpos.add(origin)) == Blocks.grass.getDefaultState()){
	            		if(world.rand.nextInt(8) == 0){
	            			BlockFlower.EnumFlowerType blockflower$enumflowertype = world.getBiomeGenForCoords(bpos.add(origin).up()).pickRandomFlower(world.rand, bpos.add(origin).up());
                            BlockFlower blockflower = blockflower$enumflowertype.getBlockType().getBlock();
                            IBlockState iblockstate = blockflower.getDefaultState().withProperty(blockflower.getTypeProperty(), blockflower$enumflowertype);

                            if (blockflower.canBlockStay(world, bpos.add(origin).up(), iblockstate))
                            	tempList.add(new ChangeBlockToThis(bpos.add(origin).up(), iblockstate));
                            
	            		}
	            		else{
	            			IBlockState iblockstate1 = Blocks.tallgrass.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

                            if (Blocks.tallgrass.canBlockStay(world, bpos.add(origin).up(), iblockstate1))
                            	tempList.add(new ChangeBlockToThis(bpos.add(origin).up(), iblockstate1));
                            
	            		}
	            		
	            		System.out.println((new StringBuilder("Setblock ")).append(count).toString());
	            		count++;
	            		
	            	}
            	}
            	
                
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
                ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
                ((PlayerWrapper)BuildingTools.getPlayerRegistry().getPlayer(entity).get()).pendingChangeQueue = new BlockChangeQueue(tempList, world, true);
            }
            if(count < 4096)
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

	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}
}
