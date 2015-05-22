package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class TopSoilThread implements IShapeable, BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected int radiusX;
	protected int radiusY;
	protected int radiusZ;
	protected int depth;
	protected EnumFacing side;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	protected int count = 0;
	
	protected boolean currentlyCalculating = false;
	
	//protected List<BlockPos> tempList = new ArrayList<BlockPos>();
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected Set<BlockPos> checkedList = new HashSet<BlockPos>();
	
	public TopSoilThread(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, int depth, EnumFacing side, EntityPlayer entity){
		this.world = world;
		this.origin = origin;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.depth = depth;
		this.side = side;
		this.entity = entity;
	}
	
	@Override
	public void setBlock(BlockPos tempPos) {
		//if(this.world.isAirBlock(bpos.add(origin)) && (bpos.add(origin)).getY() > 0 && (bpos.add(origin)).getY() < 256){
		//if((bpos.add(origin)).getY() > 0 && (bpos.add(origin)).getY() < 256){
		
		BlockPos bpos = tempPos;
		
			if(count<4096 && !checkedList.contains(tempPos)){
				
				checkedList.add(tempPos);
				
                if(bpos.add(origin).getY() > 0 && bpos.add(origin).getY() < 256){
	                //if(this.world.isAirBlock(blockpos1.up()) && (blockpos1.up()).getY() > 0 && (blockpos1.up()).getY() < 256){
	                //if(count<8192){
                	
                	if(!world.isAirBlock(bpos.add(origin)) && world.isAirBlock(bpos.add(origin).up())){
                		tempList.add(new ChangeBlockToThis(bpos.add(origin), Blocks.grass.getDefaultState()));
                		for(int i = 1; i < depth; i++){
	                		tempList.add(new ChangeBlockToThis(bpos.add(origin).down(i), Blocks.dirt.getDefaultState()));
	                		count++;
	                		//world.markBlockForUpdate(bpos.add(origin).down(i));
                		}
                		//world.markBlockForUpdate(bpos.add(origin));
                		System.out.println("Setblock " + count);
                		count++;
	                }
                }                
				
				/*int random = new Random().nextInt((10 - 1) + 1) + 1;
				
				IBlockState blockstate;
				
				if(random <= 8){
					blockstate = Blocks.stone.getDefaultState();
				}
				else if (random == 9){
					blockstate = Blocks.cobblestone.getDefaultState();
				}
				else if (random == 10){
					blockstate = Blocks.mossy_cobblestone.getDefaultState();
				}
				else{
					blockstate = blockStateToPlace;
				}*/
				
				//System.out.println(random);
                
				//tempList.add(new ChangeBlockToThis(bpos.add(origin), blockStateToPlace));
				//count++;
				
			//}
		}
	}
	
	public void perform(){
		
		if(!currentlyCalculating){
			
			tempList.clear();
			
			Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
			
			if(!tempList.isEmpty() && tempList != null){
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(CalcUndoList(tempList));
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, true);
			}
			
			if(count < 4096){
				if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist.add(new LinkedHashSet<ChangeBlockToThis>((BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList))))
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				isFinished = true;
			}
			
			currentlyCalculating = false;
			
			count = 0;
		}

	}
	
	public Set<ChangeBlockToThis> CalcUndoList(Set<ChangeBlockToThis> tempList){
		Set<ChangeBlockToThis> newTempList = new LinkedHashSet<ChangeBlockToThis>();
		
		for(ChangeBlockToThis pos : tempList){
			newTempList.add(addBlockWithNBT(pos.getBlockPos(), world.getBlockState(pos.getBlockPos()), pos.getBlockPos()));
		}
		
		return newTempList;
	}
	
	public ChangeBlockToThis addBlockWithNBT(BlockPos oldPosOrNull, IBlockState blockState, BlockPos newPos){
		if(oldPosOrNull != null && world.getTileEntity(oldPosOrNull) != null){
    		NBTTagCompound compound = new NBTTagCompound();
    		world.getTileEntity(oldPosOrNull).writeToNBT(compound);
    		//tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
    		return new ChangeBlockToThis(newPos, blockState, compound);
		}
    	else{
    		//tempList.add(new ChangeBlockToThis(newPos, blockState));
    		if(blockState.getBlock() instanceof BlockDoor){
    			if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER){
					return new ChangeBlockToThis(newPos, blockState.withProperty(BlockDoor.HINGE, world.getBlockState(oldPosOrNull.up()).getValue(BlockDoor.HINGE)));
				}
    			else if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER){
					return new ChangeBlockToThis(newPos, blockState.withProperty(BlockDoor.FACING, world.getBlockState(oldPosOrNull.down()).getValue(BlockDoor.FACING)));
				}
    		}
    		return new ChangeBlockToThis(newPos, blockState);
    	}
	}
	
	/**
     * The world that this queue is change
     * 
     * @return the world
     */
    public World getWorld()
    {
        return this.world;
    }

	public boolean isFinished(){
		return isFinished;
	}
	

	
}
