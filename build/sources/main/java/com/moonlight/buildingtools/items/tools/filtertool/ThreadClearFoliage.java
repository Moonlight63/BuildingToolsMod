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
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadClearFoliage implements IShapeable, BlockChangeBase{
	
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
    protected Set<ChangeBlockToThis> tempList;
    protected Set<ChangeBlockToThis> filledList;
    protected Set<BlockPos> checkedList;
    
    protected List<Set<ChangeBlockToThis>> listSet = Lists.newArrayList();
	protected boolean shapeFinished = false;
	

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
        if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && ((world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockDoublePlant) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockLeaves) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockBush) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockTallGrass) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockCrops) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockFlower) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockVine) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockReed) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockSapling) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockWeb) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockCactus) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockLilyPad) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockSign) || (world.getBlockState(bpos.add(origin)).getBlock() instanceof BlockStem)))
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
        	addSetToList();
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
            	addSetToList();
            }
        }
        
        shapeFinished();

    }

    public void perform()
    {
        if(!currentlyCalculating)
        {
            this.tempList.clear();
            Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
            ClearBlocks();
        }
        
        if(listSet.isEmpty() && shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
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
