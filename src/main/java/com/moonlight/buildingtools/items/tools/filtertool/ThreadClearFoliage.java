package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
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
    protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
    protected Set<ChangeBlockToThis> filledList = new CopyOnWriteArraySet<ChangeBlockToThis>();
    protected Set<BlockPos> checkedList = new HashSet<BlockPos>();
	protected boolean shapeFinished = false;
	

    public ThreadClearFoliage(World world, BlockPos origin, EnumFacing side, EntityPlayer entity, NBTTagCompound nbtdata){
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
