package com.moonlight.buildingtools.items.tools.smoothtool;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
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
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;

public class ErosionThread implements IShapeable, BlockChangeBase {
	
	private static final BlockPos[] FACES_TO_CHECK = { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0) };
	
	protected World world;
	protected BlockPos origin;
	protected int radiusX;
	protected int radiusY;
	protected int radiusZ;
	protected EnumFacing side;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	protected int count = 0;
	
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected Set<BlockPos> checkedList = new LinkedHashSet<BlockPos>();
	protected Set<BlockPos> checkedPos = new LinkedHashSet<BlockPos>();
	
	protected boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	
	protected IShapeGenerator generator;
	
	protected ErosionPreset curPreset = Preset.SMOOTH.getPreset();
	
	protected boolean fillPass = false;
	
	protected BlockChangeTracker tracker; 
	
	protected int curErodeIteration;
	protected int curFillIteration;
	
	
	public ErosionThread(World world, BlockPos origin, int radiusX, int radiusY, int radiusZ, EnumFacing side, EntityPlayer entity){
		this.world = world;
		this.origin = origin;
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.radiusZ = radiusZ;
		this.side = side;
		this.entity = entity;
		this.generator = Shapes.Sphere.generator;
		this.tracker = new BlockChangeTracker(world);
	}
	

	
	@Override
	public void setBlock(BlockPos tempPos) {
		
		BlockPos bpos = tempPos.add(origin);
		
		IBlockState curBlock = tracker.get(bpos, curErodeIteration);
		
		if(!fillPass){
		
			if(curBlock != Blocks.air.getDefaultState()){
				int tempCount = 0;
				
				for(BlockPos pos : FACES_TO_CHECK){
					BlockPos relativePos = bpos.add(pos);
					if(world.isAirBlock(relativePos))
						tempCount++;
				}
				
				if(tempCount >= curPreset.getErosionFaces()){
					tracker.put(bpos, Blocks.air.getDefaultState(), curErodeIteration);
					//tempList.add(new ChangeBlockToThis(bpos, Blocks.air.getDefaultState()));
				}
				
			}
		
		}
		else if(fillPass){
			
			if(curBlock == Blocks.air.getDefaultState()){
				int tempCount = 0;
				Map<IBlockState, Integer> blockCount = new HashMap();
	              for (BlockPos pos : FACES_TO_CHECK)
	              {
	            	  BlockPos relativePosition = bpos.add(pos);
	            	  IBlockState relativeBlock = tracker.get(relativePosition, curFillIteration);
	            	  if ((relativeBlock == Blocks.air.getDefaultState()))
	            	  {
	            		  tempCount++;
	            		  IBlockState typeBlock = relativeBlock;
	            		  if (blockCount.containsKey(typeBlock)) {
	            			  blockCount.put(typeBlock, Integer.valueOf(((Integer)blockCount.get(typeBlock)).intValue() + 1));
	            		  } else {
	            			  blockCount.put(typeBlock, Integer.valueOf(1));
	            		  }
	            	  }
	              }
	              
	              IBlockState currentMaterial = Blocks.air.getDefaultState();
	              int amount = 0;
	              for (IBlockState state : blockCount.keySet())
	              {
	                Integer currentCount = (Integer)blockCount.get(state);
	                if (amount <= currentCount.intValue())
	                {
	                  currentMaterial = state;
	                  amount = currentCount.intValue();
	                }
	              }
	              if (count >= curPreset.getFillFaces()) {
	                tracker.put(bpos, currentMaterial, curFillIteration);
	              }
				
			}
			
		}
		
	}
	
	protected static final class ErosionPreset{
		private final int erosionFaces;
		private final int erosionRecursion;
		private final int fillFaces;
		private final int fillRecursion;
	    
		public ErosionPreset(int erosionFaces, int erosionRecursion, int fillFaces, int fillRecursion){
		  this.erosionFaces = erosionFaces;
		  this.erosionRecursion = erosionRecursion;
		  this.fillFaces = fillFaces;
		  this.fillRecursion = fillRecursion;
		}
		
		public int getErosionFaces(){
		  return this.erosionFaces;
		}
		
		public int getErosionRecursion(){
		  return this.erosionRecursion;
		}
		
		public int getFillFaces(){
		  return this.fillFaces;
		}
		
		public int getFillRecursion(){
		  return this.fillRecursion;
		}
		
		public ErosionPreset getInverted(){
		  return new ErosionPreset(this.fillFaces, this.fillRecursion, this.erosionFaces, this.erosionRecursion);
		}
	}
	
	private static enum Preset{
	    MELT(new ErosionThread.ErosionPreset(2, 1, 5, 1)),  
	    FILL(new ErosionThread.ErosionPreset(5, 1, 2, 1)),  
	    SMOOTH(new ErosionThread.ErosionPreset(3, 1, 3, 1)),  
	    LIFT(new ErosionThread.ErosionPreset(6, 0, 1, 1)),  
	    FLOATCLEAN(new ErosionThread.ErosionPreset(6, 1, 6, 1));
	    
	    private ErosionThread.ErosionPreset preset;
	    
	    private Preset(ErosionThread.ErosionPreset preset){
	      this.preset = preset;
	    }
	    
	    public static String getValuesString(String seperator){
	      String valuesString = "";
	      
	      boolean delimiterHelper = true;
	      for (Preset preset : values()){
	        if (delimiterHelper) {
	          delimiterHelper = false;
	        } else {
	          valuesString = valuesString + seperator;
	        }
	        valuesString = valuesString + preset.name();
	      }
	      return valuesString;
	    }
	    
	    public ErosionThread.ErosionPreset getPreset(){
	      return this.preset;
	    }
	}
	
	private static final class BlockChangeTracker{
	    private final Map<Integer, Map<BlockPos, IBlockState>> blockChanges;
	    private final Map<BlockPos, IBlockState> flatChanges;
	    private final World world;
	    private int nextIterationId = 0;
	    
	    public BlockChangeTracker(World world)
	    {
	      this.blockChanges = new HashMap();
	      this.flatChanges = new HashMap();
	      this.world = world;
	    }
	    
	    public IBlockState get(BlockPos position, int iteration)
	    {
	      IBlockState changedBlock = null;
	      for (int i = iteration - 1; i >= 0; i--) {
	        if ((this.blockChanges.containsKey(Integer.valueOf(i))) && (((Map)this.blockChanges.get(Integer.valueOf(i))).containsKey(position)))
	        {
	          changedBlock = (IBlockState)((Map)this.blockChanges.get(Integer.valueOf(i))).get(position);
	          return changedBlock;
	        }
	      }
	      changedBlock = world.getBlockState(position);
	      
	      return changedBlock;
	    }
	    
	    public Collection<IBlockState> getAll()
	    {
	      return this.flatChanges.values();
	    }
	    
	    public Map<BlockPos, IBlockState> getMap(){
	    	return flatChanges;
	    }
	    
	    public int nextIteration()
	    {
	      return this.nextIterationId++;
	    }
	    
	    public void put(BlockPos position, IBlockState changedBlock, int iteration)
	    {
	      if (!this.blockChanges.containsKey(Integer.valueOf(iteration))) {
	        this.blockChanges.put(Integer.valueOf(iteration), new HashMap());
	      }
	      ((Map)this.blockChanges.get(Integer.valueOf(iteration))).put(position, changedBlock);
	      this.flatChanges.put(position, changedBlock);
	    }
	}
	
	
	int tempCount = 0;
	public void perform(){
		
		if(!currentlyCalculating){
			tempCount++;
			System.out.println(tempCount);
			
			tempList.clear();
			
			for(int i = 0; i < curPreset.getErosionRecursion(); i++){
				curErodeIteration = tracker.nextIteration();
				generator.generateShape(radiusX, radiusY, radiusZ, this, true);
			}
			fillPass = true;
			for(int i = 0; i < curPreset.getFillRecursion(); i++){
				curFillIteration = tracker.nextIteration();
				generator.generateShape(radiusX, radiusY, radiusZ, this, true);
			}
			
			for(BlockPos pos : tracker.getMap().keySet())
				tempList.add(new ChangeBlockToThis(pos, tracker.getMap().get(pos)));
			
			if(!tempList.isEmpty() && tempList != null){
				
				//BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(CalcUndoList(tempList));
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(tempList, world, true);
			}
				
			//if(count < 4096){
				isFinished = true;
				if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist.add(new LinkedHashSet<ChangeBlockToThis>((BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList))))
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.clear();
				//System.out.println("Added all blocks to undo list: " + BuildingTools.getPlayerRegistry().getPlayer(entity).get().undolist);
			//}
			
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
