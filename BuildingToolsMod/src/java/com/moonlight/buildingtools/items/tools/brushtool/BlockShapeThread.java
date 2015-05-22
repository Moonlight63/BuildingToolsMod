package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class BlockShapeThread implements IShapeable, BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected int radiusX;
	protected int radiusY;
	protected int radiusZ;
	protected EnumFacing side;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	protected int count = 0;
	
	//protected List<BlockPos> tempList = new ArrayList<BlockPos>();
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected Set<BlockPos> checkedList = new LinkedHashSet<BlockPos>();
	protected Set<BlockPos> checkedPos = new LinkedHashSet<BlockPos>();
	
	//protected Set<BlockPos> selectionSet = new CopyOnWriteArraySet<BlockPos>();
	protected boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	
	protected IShapeGenerator generator;
	
	protected boolean forceBlocksToFall = false;
	protected boolean fillmode = true;
	protected IBlockState blockStateToPlace = Blocks.stone.getDefaultState();
	protected IBlockState blockStateToReplace = Blocks.air.getDefaultState();
	protected boolean replaceAll = false;
	
	public BlockShapeThread(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, EnumFacing side, EntityPlayer entity, IShapeGenerator shapegen, boolean fill, boolean forceblockstofall, IBlockState blockStateToCreate){
		this.world = world;
		this.origin = origin;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.side = side;
		this.entity = entity;
		this.generator = shapegen;
		if(blockStateToCreate == BlockLoader.tempBlock.getDefaultState())
			this.blockStateToPlace = Blocks.air.getDefaultState();
		else
			this.blockStateToPlace = blockStateToCreate;
		this.forceBlocksToFall = forceblockstofall;
		this.fillmode = fill;
	}
	
	public BlockShapeThread(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, EnumFacing side, EntityPlayer entity, IShapeGenerator shapegen, boolean fill, boolean forceblockstofall, IBlockState blockStateToCreate, IBlockState blockStateToReplace){
		this.world = world;
		this.origin = origin;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.side = side;
		this.entity = entity;
		this.generator = shapegen;
		if(blockStateToCreate == BlockLoader.tempBlock.getDefaultState())
			this.blockStateToPlace = Blocks.air.getDefaultState();
		else
			this.blockStateToPlace = blockStateToCreate;
		this.blockStateToReplace = blockStateToReplace;
		this.forceBlocksToFall = forceblockstofall;
		this.fillmode = fill;
	}
	
	public BlockShapeThread(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, EnumFacing side, EntityPlayer entity, IShapeGenerator shapegen, boolean fill, boolean forceblockstofall, IBlockState blockStateToCreate, boolean replaceAllBlocks){
		this.world = world;
		this.origin = origin;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.side = side;
		this.entity = entity;
		this.generator = shapegen;
		if(blockStateToCreate == BlockLoader.tempBlock.getDefaultState())
			this.blockStateToPlace = Blocks.air.getDefaultState();
		else
			this.blockStateToPlace = blockStateToCreate;
		this.replaceAll = replaceAllBlocks;
		this.forceBlocksToFall = forceblockstofall;
		this.fillmode = fill;
	}
	
	@Override
	public void setBlock(BlockPos tempPos) {
		
			if(count < 4096){
				
				currentlyCalculating = true;
				//count = 0;
				
				BlockPos bpos = tempPos;
						
				if (side == EnumFacing.UP || side == EnumFacing.DOWN){
					bpos = new BlockPos(tempPos.getX(), side == EnumFacing.UP ? tempPos.getY() : -tempPos.getY(), tempPos.getZ());
				}
				else if (side == EnumFacing.NORTH || side == EnumFacing.SOUTH){
					bpos = new BlockPos(tempPos.getX(), tempPos.getZ(), side == EnumFacing.NORTH ? -tempPos.getY() : tempPos.getY());
				}
				else if (side == EnumFacing.EAST || side == EnumFacing.WEST){
					bpos = new BlockPos(side == EnumFacing.WEST ? -tempPos.getY() : tempPos.getY(), tempPos.getX(), tempPos.getZ());
				}
			
				BlockPos blockpos1;
				if(forceBlocksToFall){
		            for (blockpos1 = bpos.add(origin).down(); canFallInto(world, blockpos1) && blockpos1.getY() > 0; blockpos1 = blockpos1.down()){
		            	;
		            }
				}
				else{
					blockpos1 = bpos.add(origin).down();
				}
				
				if((blockpos1.up()).getY() > 0 && (blockpos1.up()).getY() < 256){
					
					//if(!checkedList.contains(tempPos)){
						if(forceBlocksToFall){
							if(!checkedList.contains(tempPos)){
								if(replaceAll){
									if(world.getBlockState(blockpos1.up()) != blockStateToPlace){
										tempList.add(new ChangeBlockToThis(blockpos1.up(), blockStateToPlace));
										//if(forceBlocksToFall){
											checkedPos.add(blockpos1.up());
											checkedList.add(tempPos);
										//}
										count++;
									}
								}
								else{
									if(world.getBlockState(blockpos1.up()) == blockStateToReplace){
										tempList.add(new ChangeBlockToThis(blockpos1.up(), blockStateToPlace));
										//if(forceBlocksToFall){
											checkedPos.add(blockpos1.up());
											checkedList.add(tempPos);
										//}
										count++;
									}
								}
							}
						}
						
						else{
							//if(!checkedList.contains(tempPos)){
								if(replaceAll){
									if(world.getBlockState(blockpos1.up()) != blockStateToPlace){
										tempList.add(new ChangeBlockToThis(blockpos1.up(), blockStateToPlace));
										//if(forceBlocksToFall){
											checkedPos.add(blockpos1.up());
										//}
										checkedList.add(tempPos);
										count++;
									}
								}
								else{
									if(world.getBlockState(blockpos1.up()) == blockStateToReplace){
										tempList.add(new ChangeBlockToThis(blockpos1.up(), blockStateToPlace));
										//if(forceBlocksToFall){
											checkedPos.add(blockpos1.up());
										//}
										checkedList.add(tempPos);
										count++;
									}
								}
							//}
						}
					//}
					
					//else{
					//	System.out.println("Cought Contains");
					//}
					
				}
				
			}
			else{
				return;
			}
		//}
		
		//else{
		//	return;
		//}
		
		
		
		
		
				
				/*BlockPos blockpos1;
				
				if(forceBlocksToFall){
	                for (blockpos1 = bpos.add(origin).down(); canFallInto(world, blockpos1) && blockpos1.getY() > 0; blockpos1 = blockpos1.down()){
	                	;
	                }
				}else{
					blockpos1 = bpos.add(origin).down();
				}

                if((blockpos1.up()).getY() > 0 && (blockpos1.up()).getY() < 256){
	                //if(this.world.isAirBlock(blockpos1.up()) && (blockpos1.up()).getY() > 0 && (blockpos1.up()).getY() < 256){
	                //if(count<8192){		                	
	                	tempList.add(new ChangeBlockToThis(blockpos1.up(), blockStateToPlace));
	                	checkedPos.add(blockpos1.up());
	                	count++;
	                //}
                }
                
                */
                
				
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
	
	
	public boolean canFallInto(World worldIn, BlockPos pos)
    {
		if(checkedPos.contains(pos)){
        	return false;
        }
        if (worldIn.isAirBlock(pos)) return true;
        Block block = worldIn.getBlockState(pos).getBlock();
        Material material = block.getMaterial();
        return block == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava || block == BlockLoader.tempBlock;
    }
	
	int tempCount = 0;
	public void perform(){
		
		if(!currentlyCalculating){
			tempCount++;
			System.out.println(tempCount);
			
			tempList.clear();
			
			generator.generateShape(radiusX, radiusY, radiusZ, this, fillmode);
			
			if(!tempList.isEmpty() && tempList != null){
				
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(CalcUndoList(tempList));
				
				if(replaceAll)
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, replaceAll);
				if(!replaceAll)
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, blockStateToReplace);
			}
				
			if(count < 4096){
				isFinished = true;
				if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist.add(new LinkedHashSet<ChangeBlockToThis>((BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList))))
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//System.out.println("Added all blocks to undo list: " + BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist);
			}
			
			count = 0;
			currentlyCalculating = false;
			
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
