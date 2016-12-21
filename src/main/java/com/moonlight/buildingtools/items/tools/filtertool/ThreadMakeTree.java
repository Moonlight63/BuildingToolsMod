// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ThreadTopsoil.java

package com.moonlight.buildingtools.items.tools.filtertool;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ThreadMakeTree implements BlockChangeBase{
	
    protected World world;
    protected BlockPos origin;
    protected boolean isFinished;
    protected EntityPlayer entity;
    protected int count;
    protected boolean currentlyCalculating;
    protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
    protected List<ChangeBlockToThis> data = Lists.newArrayList();
    protected Set<BlockPos> checkList = Sets.newHashSet();
    protected Set<BlockPos> checkPos = Sets.newHashSet();
	protected boolean shapeFinished = false;
    
    private Random rand;
    List<FoliageCoordinates> foliageCoords;
    List<FoliageCoordinates> extrafoliageCoords;
    
    List<List<Float>> foliage_shape;
    int height = 64;
    double scaleWidth = 1.0D;
    int trunkRadBot = 4;
    int trunkRadMid = 2;
    int trunkRadTop = 1;
    float trunkHeight = 0.8f;
    float trunkMidPoint = 0.382f;
    float branchStart = 0.2f;
    float foliageStart = 0.35f;
    double branchSlope = 0.381D;
    double leafDensity = 1.0D;
    double branchDensity = 0.25D;
    boolean hollowTrunk = false;
    int trunkWallThickness = 1;
    int clusterShape = 0;
    IBlockState logMat = Blocks.LOG.getDefaultState();
    IBlockState leafMat = Blocks.LEAVES.getDefaultState();

    public ThreadMakeTree(World world, BlockPos origin, EntityPlayer entity, ProceduralTreeData data){
        this.world = world;
        this.origin = origin;
        this.entity = entity;
        this.rand = new Random();
        //this.foliage_shape = new float[][]{/*{2, 4, 6, 6, 6, 6, 6, 5, 4, 3}, */{2, 3, 3, 2, 1}};
        
        this.height = data.GetTreeHeight();
        this.trunkRadBot = data.GetTrunkBottom();
        this.trunkRadMid = data.GetTrunkMiddle();
        this.trunkRadTop = data.GetTrunkTop();
        this.trunkHeight = data.GetTrunkHeight();
        this.trunkMidPoint = data.GetTrunkMidPoint();
        this.branchStart = data.GetBranchStart();
        this.foliageStart = data.GetFoliageStart();
        this.branchSlope = data.GetBranchSlope();
        this.leafDensity = data.GetLeafDensity();
        this.branchDensity = data.GetBranchDensity();
        this.foliage_shape = data.GetFoliageShapes();
        
        this.clusterShape = data.GetClusterShape();
        
        this.hollowTrunk = data.hollowTrunk;
        this.scaleWidth = data.GetScaleWidth();
        this.trunkWallThickness = data.trunkWallThickness;
        
        this.logMat = data.logMat;
        this.leafMat = data.leafMat;
    }
    
    public void convertData(){
    	entity.addChatMessage(new TextComponentString("Generating All Blocks"));
    	for(ChangeBlockToThis pos : data){
    		
    		BlockPos blockpos1;
    		
			if(logMat.getBlock() instanceof BlockFalling || leafMat.getBlock() instanceof BlockFalling){
	            for (blockpos1 = pos.getBlockPos().down(); canFallInto(world, blockpos1) && blockpos1.getY() > 0; blockpos1 = blockpos1.down()){
	            	;
	            }
			}
			else{
				blockpos1 = pos.getBlockPos().down();
			}
    		
    		tempList.add(new ChangeBlockToThis(blockpos1.up(), pos.getBlockState()));
    		checkPos.add(blockpos1.up());
    		count++;
    		if(count > 4096){
    			entity.addChatMessage(new TextComponentString(pos.getBlockPos().toString()));
    			checkAndAddQueue();
            }
    	}
    	checkAndAddQueue();
    }
    
    public void add(ChangeBlockToThis change){
    	if(!checkList.contains(change.getBlockPos())){
    		this.data.add(change);
    		this.checkList.add(change.getBlockPos());
    	}
    }
    
    public boolean canFallInto(World worldIn, BlockPos pos)
    {
		if(checkPos.contains(pos)){
        	return false;
        }
        if (worldIn.isAirBlock(pos)) return true;
        Block block = worldIn.getBlockState(pos).getActualState(worldIn, pos).getBlock();
        Material material = worldIn.getBlockState(pos).getActualState(worldIn, pos).getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA || block == BlockLoader.tempBlock;
    }

    @Override
	public void perform()
    {
    	if(!currentlyCalculating){
    		currentlyCalculating = true;
    		
    		this.generateLeafNodeList();
    		this.generateLeaves();
    		this.generateTrunk();
    		this.generateLeafNodeBases();
            this.convertData();
            
    		shapeFinished();
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
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(tempList, world));
		tempList.clear();
		count = 0;
	}

	public void shapeFinished() {
		checkAndAddQueue();
		shapeFinished = true;
	}
    
    
    
    /**
     * Places the trunk for the big tree that is being generated. Able to generate double-sized trunks by changing a
     * field that is always 1 to 2.
     */
    void generateTrunk(){
    	
    	entity.addChatMessage(new TextComponentString("Generating Trunk"));
    	
        int trunkheight = (int) (this.height * this.trunkHeight);
        
        int midy = (int)(trunkheight * this.trunkMidPoint);
        int topy = trunkheight;
        
        Set<BlockPos> blocks = Sets.newHashSet();
        Set<BlockPos> blockRemoval = Sets.newHashSet();
        
        blocks.addAll(taperedcylinder(BlockPos.ORIGIN, new BlockPos(0, midy, 0), trunkRadBot, trunkRadMid));
        blocks.addAll(taperedcylinder(new BlockPos(0, midy, 0), new BlockPos(0, topy, 0), trunkRadMid, trunkRadTop));
        
        if(trunkRadBot > 2 && trunkRadMid > 2 && trunkRadTop > 2 && hollowTrunk){
        	blockRemoval.addAll(taperedcylinder(BlockPos.ORIGIN, new BlockPos(0, midy, 0), trunkRadBot - trunkWallThickness, trunkRadMid - trunkWallThickness));
        	blockRemoval.addAll(taperedcylinder(new BlockPos(0, midy,0), new BlockPos(0, topy, 0), trunkRadMid - trunkWallThickness, trunkRadTop - trunkWallThickness));
        }
        
        for(BlockPos pos : blocks){
        	if(blockRemoval.contains(pos))
        		this.add(new ChangeBlockToThis(pos.add(origin), Blocks.AIR.getDefaultState()));
        	else
        		this.add(new ChangeBlockToThis(pos.add(origin), this.logMat));
        }
        
    }
    
    void generateLeafNodeList(){
    	
    	entity.addChatMessage(new TextComponentString("Generating Leaf Positions"));
    	
        int i = (int)(1.382D + Math.pow(this.leafDensity * this.height / 13.0D, 2.0D));

        if (i < 1)
            i = 1;

        int height = this.height;
        int count = this.height;
        int branchStart = (int) (this.height * this.branchStart);
        this.foliageCoords = Lists.<FoliageCoordinates>newArrayList();
        this.extrafoliageCoords = Lists.<FoliageCoordinates>newArrayList();
        
        for (; count >= 0; --count){
        	
            float f = this.layerSize(count);

            if (f >= 0.0F){
                for (int l = 0; l < i; ++l){
                	
                    double d0 = this.scaleWidth * f * (this.rand.nextFloat() + 0.328D);
                    double d1 = this.rand.nextFloat() * 2.0F * Math.PI;
                    double d2 = d0 * Math.sin(d1) + 0.5D;
                    double d3 = d0 * Math.cos(d1) + 0.5D;
                    BlockPos blockpos = new BlockPos(d2, count - 1, d3);
                    
                    int i1 = -blockpos.getX();
                    int j1 = -blockpos.getZ();
                    double d4 = blockpos.getY() - Math.sqrt(i1 * i1 + j1 * j1) * this.branchSlope;
                    int k1 = d4 > height ? height : (int)d4 < branchStart ? branchStart : (int)d4;

                    //if (this.checkBlockLine(blockpos2, blockpos) == -1)
                    this.foliageCoords.add(new FoliageCoordinates(blockpos.add(origin), k1));
                    
                    
                    
                    /*    this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(7), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(7), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).north(7), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).south(7), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).east(7), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).west(7), k1));
                        
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).north(5), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).south(5), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).north(5), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).south(5), k1));
                        
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).east(5), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).west(5), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).east(5), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).west(5), k1));
                        
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).north(4).east(4), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).north(4).west(4), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).south(4).east(4), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).up(5).south(4).west(4), k1));
                        
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).north(4).east(4), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).north(4).west(4), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).south(4).east(4), k1));
                        this.extrafoliageCoords.add(new CustomTreeTest.FoliageCoordinates(blockpos.add(basePos).down(5).south(4).west(4), k1));*/
                }
            }
            
        }
        
    }
    
    /**
     * Gets the rough size of a layer of the tree.
     */
    float layerSize(int layer){
    	
    	if(clusterShape == 0){
    	
	        if (layer < this.height * this.foliageStart)
	        {
	            return -1.0F;
	        }
	        else
	        {
	            float rad = this.height / 2.0F;
	            float adj = rad - layer;
	            float dist = MathHelper.sqrt_float(rad * rad - adj * adj);
	
	            if (adj == 0.0F)
	            {
	            	dist = rad;
	            }
	            else if (Math.abs(adj) >= rad)
	            {
	                return 0.0F;
	            }
	
	            return dist * 0.618F;
	        }
        
    	}
    	
    	else if(clusterShape == 1){
    		if(layer < this.height * (0.25 + 0.05 * Math.sqrt(Math.random()))){
        		return -1;
        	}
        	
        	float rad = (this.height - layer) * this.foliageStart;
        	if(rad < 0)
        		rad = 0;
        	
        	return rad;
    	}
    	else{
    		return 0;
    	}
    }
    
    /**
     * Generates additional wood blocks to fill out the bases of different leaf nodes that would otherwise degrade.
     */
    void generateLeafNodeBases(){
    	entity.addChatMessage(new TextComponentString("Generating Branches"));
        for (FoliageCoordinates worldgenbigtree$foliagecoordinates : this.foliageCoords){
        	if(Math.random()<=this.branchDensity){
	            int i = worldgenbigtree$foliagecoordinates.getBranchStartY();
	            BlockPos blockpos = new BlockPos(this.origin.getX(), i, this.origin.getZ());
	
	            if (!blockpos.equals(worldgenbigtree$foliagecoordinates) /*&& this.leafNodeNeedsBase(i - this.origin.getY())*/){
	                this.generateBranch(blockpos, worldgenbigtree$foliagecoordinates);
	            }
        	}
        }
    }
    
    void generateBranch(BlockPos pos1, BlockPos pos2){
        BlockPos blockpos = pos2.add(-pos1.getX(), -pos1.getY(), -pos1.getZ());
        int i = this.getGreatestDistance(blockpos);
        float f = (float)blockpos.getX() / (float)i;
        float f1 = (float)blockpos.getY() / (float)i;
        float f2 = (float)blockpos.getZ() / (float)i;

        for (int j = 0; j <= i; ++j){
        	BlockPos blockpos1 = pos1.add(0.5F + j * f, 0.5F + j * f1, 0.5F + j * f2);
        	if(this.logMat.getBlock().equals(Blocks.LOG) || this.logMat.getBlock().equals(Blocks.LOG2)){
	            BlockLog.EnumAxis blocklog$enumaxis = this.getLogAxis(pos1, blockpos1);
	            this.add(new ChangeBlockToThis(blockpos1, this.logMat.withProperty(BlockLog.LOG_AXIS, blocklog$enumaxis)));
	            //this.data.add(new ChangeBlockToThis(blockpos1, this.logMat.withProperty(BlockLog.LOG_AXIS, blocklog$enumaxis)));
	            //this.setBlockAndNotifyAdequately(this.world, blockpos1, this.logMat.withProperty(BlockLog.LOG_AXIS, blocklog$enumaxis));
        	}
        	else{
        		this.add(new ChangeBlockToThis(blockpos1, this.logMat));
        		//this.data.add(new ChangeBlockToThis(blockpos1, this.logMat));
	            //this.setBlockAndNotifyAdequately(this.world, blockpos1, this.logMat);
        	}
        }
    }
    
    /**
     * Returns the absolute greatest distance in the BlockPos object.
     */
    private int getGreatestDistance(BlockPos posIn){
        int i = MathHelper.abs_int(posIn.getX());
        int j = MathHelper.abs_int(posIn.getY());
        int k = MathHelper.abs_int(posIn.getZ());
        return k > i && k > j ? k : (j > i ? j : i);
    }

    private BlockLog.EnumAxis getLogAxis(BlockPos startPos, BlockPos directionPos){
        BlockLog.EnumAxis blocklog$enumaxis = BlockLog.EnumAxis.Y;
        int i = Math.abs(directionPos.getX() - startPos.getX());
        int j = Math.abs(directionPos.getZ() - startPos.getZ());
        int k = Math.max(i, j);

        if (k > 0){
            if (i == k){
                blocklog$enumaxis = BlockLog.EnumAxis.X;
            }
            else if (j == k){
                blocklog$enumaxis = BlockLog.EnumAxis.Z;
            }
        }

        return blocklog$enumaxis;
    }
    
    /**
     * Generates the leaf portion of the tree as specified by the leafNodes list.
     * Generates the leaves surrounding an individual entry in the leafNodes list.
     */
    void generateLeaves(){
    	entity.addChatMessage(new TextComponentString("Generating Leaves"));
        for (FoliageCoordinates coord : this.foliageCoords){
        	entity.addChatMessage(new TextComponentString("At: " + coord.toString()));
        	for(BlockPos pos : this.foliagecluster(coord)){
        		if(this.leafMat.getBlock().equals(Blocks.LEAVES) || this.leafMat.getBlock().equals(Blocks.LEAVES2)){
        			this.add(new ChangeBlockToThis(pos, this.leafMat.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, false)));
        			//this.data.add(new ChangeBlockToThis(pos, this.leafMat.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, false)));
        			//this.setBlockAndNotifyAdequately(this.world, pos, this.leafMat.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, false));
        		}
        		else{
        			this.add(new ChangeBlockToThis(pos, this.leafMat));
        			//this.data.add(new ChangeBlockToThis(pos, this.leafMat));
        			//this.setBlockAndNotifyAdequately(this.world, pos, this.leafMat);
        		}
        	}
        }
        
        for (FoliageCoordinates coord : this.extrafoliageCoords){
        	for(BlockPos pos : this.foliagecluster(coord)){
        		if(this.leafMat.getBlock().equals(Blocks.LEAVES) || this.leafMat.getBlock().equals(Blocks.LEAVES2)){
        			this.add(new ChangeBlockToThis(pos, this.leafMat.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, false)));
        			//this.data.add(new ChangeBlockToThis(pos, this.leafMat.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, false)));
        			//this.setBlockAndNotifyAdequately(this.world, pos, this.leafMat.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, false));
        		}
        		else{
        			this.add(new ChangeBlockToThis(pos, this.leafMat));
        			//this.data.add(new ChangeBlockToThis(pos, this.leafMat));
        			//this.setBlockAndNotifyAdequately(this.world, pos, this.leafMat);
        		}
        	}
        }
    }
    
    public Set<BlockPos> foliagecluster(BlockPos center){
    	
    	Set<BlockPos> set = Sets.newHashSet();
    	if(this.foliage_shape.isEmpty()){
    		return set;
    	}
    	
    	int y = center.getY();
    	for(float i : this.foliage_shape.get(new Random().nextInt(this.foliage_shape.size()))){
    		set.addAll(this.crossection(new int[]{center.getX(), y, center.getZ()}, i, 1));
    		y++;
    	}
    	
    	return set;
    }
    
    
    
    
    
    
    
    
    static class FoliageCoordinates extends BlockPos{
        private final int branchStart;

        public FoliageCoordinates(BlockPos pos, int branchY){
            super(pos.getX(), pos.getY(), pos.getZ());
            this.branchStart = branchY;
        }

        public int getBranchStartY(){
            return this.branchStart;
        }
    }
    
    public Set<BlockPos> taperedcylinder(BlockPos start, BlockPos end, float startsize, float endsize){
    	int[] delta = {end.getX() - start.getX(), end.getY() - start.getY(), end.getZ() - start.getZ()};
    	int[] startarr = {start.getX(), start.getY(), start.getZ()};
    	
    	int maxdist = getMax(delta[0], delta[1], delta[2]);
    	
    	int primidx = maxdist;
    	
    	int secidx1 = (primidx - 1) % 3;
        int secidx2 = (1 + primidx) % 3;
    	
    	int primsign = delta[primidx] / Math.abs(delta[primidx]);
    	
    	float secdelta1 = delta[secidx1];
        float secfac1 = (secdelta1) / delta[primidx];
        float secdelta2 = delta[secidx2];
        float secfac2 = (secdelta2) / delta[primidx];
        
        int[] coord = {0, 0, 0};
        
        int endoffset = delta[primidx] + primsign;
        
        Set<BlockPos> set = Sets.newHashSet();
        
        for(int primoffset : Range.range(0, endoffset, primsign)){
        	int primloc = startarr[primidx] + primoffset;
            int secloc1 = (int) (startarr[secidx1] + primoffset * secfac1);
            int secloc2 = (int) (startarr[secidx2] + primoffset * secfac2);
            coord[primidx] = primloc;
            coord[secidx1] = secloc1;
            coord[secidx2] = secloc2;
            int primdist = Math.abs(delta[primidx]);
            float radius = endsize + (startsize - endsize) * Math.abs(delta[primidx] - primoffset) / primdist;
            
            set.addAll(crossection(coord, radius, primidx));
        }
        
        return set;
        		
    }
    
    public Set<BlockPos> crossection(int[] center, float radius, int diraxis){
    	int rad = (int) (radius + 0.618);
    	if(rad <= 0)
    		return null;
    	
    	int secidx1 = (diraxis - 1) % 3;
        int secidx2 = (1 + diraxis) % 3;
    	
        int[] coord = {0, 0, 0};
        
        Set<BlockPos> set = Sets.newHashSet();
        
        for(int off1 : Range.range(-rad, rad + 1)){
        	for(int off2 : Range.range(-rad, rad + 1)){
        		double thisdist = Math.sqrt((Math.pow(Math.abs(off1) + .5, 2) + (Math.pow(Math.abs(off2) + .5, 2))));
        		if (thisdist > radius)
                    continue;
        		
        		int pri = center[diraxis];
                int sec1 = center[secidx1] + off1;
                int sec2 = center[secidx2] + off2;
                coord[diraxis] = pri;
                coord[secidx1] = sec1;
                coord[secidx2] = sec2;
                
                set.add(new BlockPos(coord[0], coord[1], coord[2]));
        	}
        }
        return set;
    }
    
    
    public int getMax(int x, int y, int z){
    	int max = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
    	
    	if(x == max)
    		return 0;
    	if(y == max)
    		return 1;
    	if(z == max)
    		return 2;
    	return -1;
    }
    
    
    public static class Range{
    	public static Iterable<Integer> range( final int start, final int stop, final int step ){
    		if ( step <= 0 )
    			throw new IllegalArgumentException( "step > 0 isrequired!" );
    		return new Iterable<Integer>(){
    			@Override
				public Iterator<Integer> iterator(){
    				return new Iterator<Integer>(){
    					private int counter = start;
    					@Override
						public boolean hasNext(){
    						return counter < stop;
    					}
    					@Override
						public Integer next(){
    						try{
    							return counter;
    						}
    						finally { counter += step; }
    					}
    					@Override
						public void remove() { }
    				};
    			}
    		};
    	}
		public static Iterable<Integer> range( final int start, final int stop ){
			return range( start, stop, 1 );
		}
		public static Iterable<Integer> range( final int stop ){
			return range( 0, stop, 1 );
		}
    }
	
}
