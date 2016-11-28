package com.moonlight.buildingtools.items.tools.erosionTool;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;

public class ErosionVisuallizer implements IShapeable{
	
	private static final BlockPos[] FACES_TO_CHECK = { new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, 1, 0), new BlockPos(0, -1, 0), new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0) };
	
	protected World world;
	protected BlockPos origin;
	
	protected Set<BlockPos> tempList = new HashSet<BlockPos>();
	protected Set<BlockPos> tempErosionSet = new HashSet<BlockPos>();
	protected Set<BlockPos> tempFillSet = new HashSet<BlockPos>();
	
	protected IShapeGenerator generator;
	
	protected ErosionPreset curPreset;
	
	protected boolean fillPass = false;
	
	public ErosionChangeTracker tracker; 
	
	protected int curErodeIteration;
	protected int curFillIteration;
	
	
	public ErosionVisuallizer(int radius, World world, BlockPos origin, int preset){
		this.tracker = new ErosionChangeTracker(world);
		this.world = world;
		this.origin = origin;
		this.curPreset = Preset.values()[preset].getPreset();
		
		this.generator = Shapes.Sphere.generator;
		
		for(int i = 0; i < curPreset.getErosionRecursion(); i++){
			curErodeIteration = tracker.nextIteration();
			generator.generateShape(radius, radius, radius, this, true);
		}
		fillPass = true;
		for(int i = 0; i < curPreset.getFillRecursion(); i++){
			curFillIteration = tracker.nextIteration();
			generator.generateShape(radius, radius, radius, this, true);
		}
		
		for(BlockPos pos : tracker.getMap().keySet())
			tempList.add(pos);
	}
	
	
	public Set<BlockPos> getErosionData(){
		return tempErosionSet;
	}
	
	public Set<BlockPos> getFillData(){
		return tempFillSet;
	}
	
	
	@Override
	public void setBlock(BlockPos tempPos) {
		
		BlockPos bpos = tempPos.add(origin);
		
		IBlockState curBlock = tracker.get(bpos, curErodeIteration);
		
		if(!fillPass){
		
			if(curBlock != Blocks.AIR.getDefaultState()){
				int tempCount = 0;
				
				for(BlockPos pos : FACES_TO_CHECK){
					BlockPos relativePos = bpos.add(pos);
					if(world.isAirBlock(relativePos))
						tempCount++;
				}
				
				if(tempCount >= curPreset.getErosionFaces()){
					tracker.put(bpos, Blocks.AIR.getDefaultState(), curErodeIteration);
					tempErosionSet.add(bpos);
					//tempList.add(new ChangeBlockToThis(bpos, Blocks.air.getDefaultState()));
				}
				
			}
		
		}
		else if(fillPass){
			
			if(curBlock == Blocks.AIR.getDefaultState()){
				int tempCount = 0;
				Map<IBlockState, Integer> blockCount = new HashMap<IBlockState, Integer>();
	              for (BlockPos pos : FACES_TO_CHECK)
	              {
	            	  BlockPos relativePosition = bpos.add(pos);
	            	  IBlockState relativeBlock = tracker.get(relativePosition, curFillIteration);
	            	  if ((relativeBlock != Blocks.AIR.getDefaultState()))
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
	              
	              IBlockState currentMaterial = Blocks.AIR.getDefaultState();
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
	              if (tempCount >= curPreset.getFillFaces()) {
	                tracker.put(bpos, currentMaterial, curFillIteration);
	                tempFillSet.add(bpos);
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
	
	public static enum Preset{
	    MELT(new ErosionVisuallizer.ErosionPreset(2, 1, 5, 1)),  
	    FILL(new ErosionVisuallizer.ErosionPreset(5, 1, 2, 1)),  
	    SMOOTH(new ErosionVisuallizer.ErosionPreset(3, 1, 3, 1)),  
	    LIFT(new ErosionVisuallizer.ErosionPreset(6, 0, 1, 1)),  
	    FLOATCLEAN(new ErosionVisuallizer.ErosionPreset(6, 1, 6, 1));
	    
	    private ErosionVisuallizer.ErosionPreset preset;
	    
	    private Preset(ErosionVisuallizer.ErosionPreset preset){
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
	    
	    public ErosionPreset getPreset(){
	      return this.preset;
	    }
	}
	
	public static final class ErosionChangeTracker{
	    private final Map<Integer, Map<BlockPos, IBlockState>> changes;
	    private final Map<BlockPos, IBlockState> flatChanges;
	    private final World world;
	    private int nextIteration = 0;
	    
	    public ErosionChangeTracker(World world)
	    {
	      this.changes = new HashMap<Integer, Map<BlockPos, IBlockState>>();
	      this.flatChanges = new HashMap<BlockPos, IBlockState>();
	      this.world = world;
	    }
	    
	    public IBlockState get(BlockPos position, int iteration)
	    {
	      IBlockState changedBlock = null;
	      for (int i = iteration - 1; i >= 0; i--) {
	        if ((this.changes.containsKey(Integer.valueOf(i))) && (((Map<BlockPos, IBlockState>)this.changes.get(Integer.valueOf(i))).containsKey(position)))
	        {
	          changedBlock = (IBlockState)((Map<BlockPos, IBlockState>)this.changes.get(Integer.valueOf(i))).get(position);
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
	      return this.nextIteration++;
	    }
	    
	    public void put(BlockPos position, IBlockState changedBlock, int iteration)
	    {
	      if (!this.changes.containsKey(Integer.valueOf(iteration))) {
	        this.changes.put(Integer.valueOf(iteration), new HashMap<BlockPos, IBlockState>());
	      }
	      ((Map<BlockPos, IBlockState>)this.changes.get(Integer.valueOf(iteration))).put(position, changedBlock);
	      this.flatChanges.put(position, changedBlock);
	    }
	}

	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}

}
