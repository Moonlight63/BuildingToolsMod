package com.moonlight.buildingtools.items.tools.terrainsmoother;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class ThreadTerrainSmooth implements BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected boolean isFinished = false;
	
	protected int raduis;
	protected ItemStack stack;
	protected EntityPlayer entity;
	
	protected boolean precalcDone = false;
	protected boolean currentlyCalculating = false;
	
	protected Set<ChangeBlockToThis> tempSet = new CopyOnWriteArraySet<ChangeBlockToThis>();
	
	
	private int[] data;
    private int width;
    private int height;
	
	public ThreadTerrainSmooth(World world, BlockPos origin, int radius, ItemStack stack, EntityPlayer player){
		this.world = world;
		this.origin = origin;
		this.raduis = radius;
		this.stack = stack;
		this.entity = player;
	}
	
	public Set<ChangeBlockToThis> RunPass(){
		System.out.println("Running Pass");
		currentlyCalculating = true;
		Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
		int passCount = 0;
		for(ChangeBlockToThis change : tempSet){
			passCount++;
			if(passCount < 4096){
				tempList.add(change);
				tempSet.remove(change);
			}
			else{
				break;
			}
		}
		return tempList;
	}
	
	public void Calculate(){
		currentlyCalculating = true;
		
		BlockPos min = origin.add(new Vec3i(-raduis, 0, -raduis));
    	BlockPos max = origin.add(raduis, 256 - origin.getY(), raduis);
    	
    	HeightMap(false, min, max, world);
    	HeightMapFilter filter = new HeightMapFilter(new GaussianKernel(ToolTerrainSmooth.getNBT(stack).getInteger("iterations"), ToolTerrainSmooth.getNBT(stack).getDouble("sigma")));
    	if(data != null){
    		System.out.print(data);
    		applyFilter(filter, 3, min, max, world);
    	}
		
		precalcDone = true;
	}
	
	
	
	 /**
     * Constructs the HeightMap
     *
     * @param session an edit session
     * @param region the region
     * @param naturalOnly ignore non-natural blocks
     */
    public void HeightMap(boolean naturalOnly, BlockPos min, BlockPos max, World worldIn) {
        //checkNotNull(session);
        //checkNotNull(region);

        //this.session = session;
        //this.region = region;

        width = max.getX() - min.getX() + 1;
        height = max.getY() - min.getY() + 1;

        int minX = min.getX();
        int minY = min.getY();
        int minZ = min.getZ();
        int maxY = max.getY();

        // Store current heightmap data
        data = new int[width * height];
        for (int z = 0; z < height; ++z) {
            for (int x = 0; x < width; ++x) {
                data[z * width + x] = getHighestTerrainBlock(x + minX, z + minZ, minY, maxY, naturalOnly, worldIn);
            }
        }
    }
    
    
    
    public int getHighestTerrainBlock(int x, int z, int minY, int maxY, boolean naturalOnly, World worldIn) {
        for (int y = maxY; y >= minY; --y) {
            BlockPos pt = new BlockPos(x, y, z);
            if(worldIn.getBlockState(pt) != Blocks.air.getDefaultState())
                return y;
        }
        return minY;
    }
    
    
    
    /**
     * Apply the filter 'iterations' amount times.
     * 
     * @param filter the filter
     * @param iterations the number of iterations
     * @return number of blocks affected
     * @throws MaxChangedBlocksException
     */
    public int applyFilter(HeightMapFilter filter, int iterations, BlockPos min, BlockPos max, World worldIn){
        checkNotNull(filter);

        int[] newData = new int[data.length];
        System.arraycopy(data, 0, newData, 0, data.length);

        for (int i = 0; i < iterations; ++i) {
            newData = filter.filter(newData, width, height);
        }

        return apply(newData, min, max, worldIn);
    }
    
    
	
    /**
     * Apply a raw heightmap to the region
     * 
     * @param data the data
     * @return number of blocks affected
     * @throws MaxChangedBlocksException
     */
    public int apply(int[] data, BlockPos min, BlockPos max, World worldIn){    	
        checkNotNull(data);

        //Vector minY = region.getMinimumPoint();
        int originX = min.getX();
        int originY = min.getY();
        int originZ = min.getZ();

        int maxY = max.getY();
        int blocksChanged = 0;

        // Apply heightmap
        for (int z = 0; z < width; ++z) {
            for (int x = 0; x < width; ++x) {
                int index = z * width + x;
                int curHeight = this.data[index];

                // Clamp newHeight within the selection area
                int newHeight = Math.min(maxY, data[index]);

                // Offset x,z to be 'real' coordinates
                int xr = x + originX;
                int zr = z + originZ;

                // We are keeping the topmost blocks so take that in account for the scale
                double scale = (double) (curHeight - originY) / (double) (newHeight - originY);

                // Depending on growing or shrinking we need to start at the bottom or top
                if (newHeight > curHeight) {
                    // Set the top block of the column to be the same type (this might go wrong with rounding)
                    Block existing = worldIn.getBlockState(new BlockPos(xr, curHeight, zr)).getBlock();

                    // Skip water/lava
                    if (!existing.getMaterial().isLiquid()) {
                    	
                    	tempSet.add(new ChangeBlockToThis(new BlockPos(xr, newHeight, zr), existing.getDefaultState()));
                    	
                    	//worldIn.setBlockState(new BlockPos(xr, newHeight, zr), existing.getDefaultState());

                        ++blocksChanged;

                        // Grow -- start from 1 below top replacing airblocks
                        for (int y = newHeight - 1 - originY; y >= 0; --y) {
                            int copyFrom = (int) (y * scale);
                            
                            tempSet.add(new ChangeBlockToThis(new BlockPos(xr, originY + y, zr), worldIn.getBlockState(new BlockPos(xr, originY + copyFrom, zr))));
                            
                            //worldIn.setBlockState(new BlockPos(xr, originY + y, zr), worldIn.getBlockState(new BlockPos(xr, originY + copyFrom, zr)));
                            
                            ++blocksChanged;
                        }
                    }
                } else if (curHeight > newHeight) {
                    // Shrink -- start from bottom
                    for (int y = 0; y < newHeight - originY; ++y) {
                        int copyFrom = (int) (y * scale);
                        
                        tempSet.add(new ChangeBlockToThis(new BlockPos(xr, originY + y, zr), worldIn.getBlockState(new BlockPos(xr, originY + copyFrom, zr))));
                        
                        //worldIn.setBlockState(new BlockPos(xr, originY + y, zr), worldIn.getBlockState(new BlockPos(xr, originY + copyFrom, zr)));
                        
                        ++blocksChanged;
                    }

                    // Set the top block of the column to be the same type
                    // (this could otherwise go wrong with rounding)
                    
                   tempSet.add(new ChangeBlockToThis(new BlockPos(xr, newHeight, zr), worldIn.getBlockState(new BlockPos(xr, curHeight , zr)))); 
                    
                    //worldIn.setBlockState(new BlockPos(xr, newHeight, zr), worldIn.getBlockState(new BlockPos(xr, curHeight, zr)));
                    ++blocksChanged;

                    // Fill rest with air
                    for (int y = newHeight + 1; y <= curHeight; ++y) {
                    	
                    	tempSet.add(new ChangeBlockToThis(new BlockPos(xr, y, zr), Blocks.air.getDefaultState())); 
                    	
                    	//worldIn.setBlockToAir(new BlockPos(xr, y, zr));
                    	
                        ++blocksChanged;
                    }
                }
            }
        }

        // Drop trees to the floor -- TODO

        return blocksChanged;
    }
    
	
	public void perform(){
		if(!currentlyCalculating){
			if(!precalcDone){
				Calculate();
				currentlyCalculating = false;
			}
			else{
				if(!tempSet.isEmpty()){
					System.out.println("Sending Run Tree Pass");
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(RunPass(), world, true);
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(CalcUndoList(
							BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.blockpos));
					currentlyCalculating = false;
				}
				else{
					System.out.println("Finished");
					if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist.add(new LinkedHashSet<ChangeBlockToThis>((BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList))))
						BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
					isFinished = true;
				}
			}			
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
	
    public World getWorld()
    {
        return this.world;
    }

	public boolean isFinished(){
		return isFinished;
	}
	
}
