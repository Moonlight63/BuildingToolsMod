package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.Set;

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
				
				//world.getChunkFromBlockCoords(tempPos.add(origin)).setTerrainPopulated(false);
				//world.markChunkDirty(bpos.add(origin), new TileEntity() {
				//});
				
				/*if (side == EnumFacing.UP || side == EnumFacing.DOWN){
					bpos = new BlockPos(tempPos.getX(), side == EnumFacing.UP ? tempPos.getY() : -tempPos.getY(), tempPos.getZ());
				}
				else if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH){
					bpos = new BlockPos(tempPos.getX(), tempPos.getZ(), side == EnumFacing.NORTH ? -tempPos.getY() : tempPos.getY());
				}
				else if (side == EnumFacing.EAST || side == EnumFacing.WEST){
					bpos = new BlockPos(side == EnumFacing.WEST ? -tempPos.getY() : tempPos.getY(), tempPos.getX(), tempPos.getZ());
				}*/
				
				
				
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
		
		Shapes.Cuboid.generator.generateShape(radiusX, radiusY, radiusZ, this, true);
		
		if(count < 4096){
			System.out.println("Finished " + count);
			isFinished = true;
		}
		
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, true);
		System.out.println(count);
		count = 0;

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
