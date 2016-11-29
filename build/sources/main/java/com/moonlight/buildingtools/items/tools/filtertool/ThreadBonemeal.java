package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
    
    protected List<Set<ChangeBlockToThis>> listSet = Lists.newArrayList();
	protected boolean shapeFinished = false;

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
        if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && !world.isAirBlock(bpos.add(origin)) && world.isAirBlock(bpos.add(origin).up()))
        {
        	if(world.rand.nextInt(8) <= 0){
            	if(world.getBlockState(bpos.add(origin)) == Blocks.GRASS.getDefaultState()){
            		if(world.rand.nextInt(8) == 0){
            			BlockFlower.EnumFlowerType blockflower$enumflowertype = world.getBiomeForCoordsBody(bpos.add(origin).up()).pickRandomFlower(world.rand, bpos.add(origin).up());
                        BlockFlower blockflower = blockflower$enumflowertype.getBlockType().getBlock();
                        IBlockState iblockstate = blockflower.getDefaultState().withProperty(blockflower.getTypeProperty(), blockflower$enumflowertype);

                        if (blockflower.canBlockStay(world, bpos.add(origin).up(), iblockstate))
                        	tempList.add(new ChangeBlockToThis(bpos.add(origin).up(), iblockstate));
                        
            		}
            		else{
            			IBlockState iblockstate1 = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

                        if (Blocks.TALLGRASS.canBlockStay(world, bpos.add(origin).up(), iblockstate1))
                        	tempList.add(new ChangeBlockToThis(bpos.add(origin).up(), iblockstate1));
                        
            		}
            		
            		System.out.println((new StringBuilder("Setblock ")).append(count).toString());
            		count++;
            		
            	}
        	}   
        }
        
        if(count > 4096){
			addSetToList();
		}	
        
    }

    public void perform()
    {
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
