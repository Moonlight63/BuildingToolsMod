package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
    protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
    protected Set<BlockPos> checkedList = new HashSet<BlockPos>();
	protected boolean shapeFinished = false;

    public ThreadBonemeal(World world, BlockPos origin, EnumFacing side, EntityPlayer entity, NBTTagCompound nbtdata){
        this.world = world;
        this.origin = origin;
        this.side = side;
        this.entity = entity;
        this.radiusX = nbtdata.getInteger("radiusX");
        this.radiusY = nbtdata.getInteger("radiusY");
        this.radiusZ = nbtdata.getInteger("radiusZ");
    }

    @Override
	public void setBlock(BlockPos tempPos)
    {
        BlockPos bpos = tempPos;
        if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256 && !world.isAirBlock(bpos.add(origin)) && world.isAirBlock(bpos.add(origin).up())){
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
        	checkAndAddQueue();
		}	
        
    }

    @Override
	public void perform()
    {
        if(!currentlyCalculating){
            tempList.clear();
            Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
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

    @Override
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
