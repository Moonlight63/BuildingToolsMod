package com.moonlight.buildingtools.items.tools.filtertool;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.*;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

import java.util.*;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadTopsoil implements IShapeable, BlockChangeBase{
	
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
    protected Set<ChangeBlockToThis> tempList;
    
    protected List<Set<ChangeBlockToThis>> listSet = Lists.newArrayList();
	protected boolean shapeFinished = false;

    public ThreadTopsoil(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, int depth, EnumFacing side, 
            EntityPlayer entity)
    {
        isFinished = false;
        count = 0;
        currentlyCalculating = false;
        tempList = new HashSet();
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
        if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && !world.isAirBlock(bpos.add(origin)) && world.isAirBlock(bpos.add(origin).up()))
        {
            tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.GRASS.getDefaultState()));
            for(int i = 1; i < depth; i++)
            {
                tempList.add(new ChangeBlockToThis(bpos.add(origin).down(i), Blocks.DIRT.getDefaultState()));
                count++;
            }

            System.out.println((new StringBuilder("Setblock ")).append(count).toString());
            count++;
        }
        
        if(count > 4096){
        	addSetToList();
        }
    }

    public void perform(){
    	
        if(!currentlyCalculating)
        {
            tempList.clear();
            Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
        }
        if(listSet.isEmpty() && shapeFinished){
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
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(listSet.get(0), world));
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(listSet.get(0), world, true));
		listSet.remove(0);
	}
	
	public void addSetToList(){
		listSet.add(Sets.newHashSet(tempList));
		tempList.clear();
		count = 0;
		checkAndAddQueue();
	}

	@Override
	public void shapeFinished() {
		addSetToList();
		shapeFinished = true;
		//currentlyCalculating = false;
	}
}
