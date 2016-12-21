package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.helpers.shapes.IShapeGenerator;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.utils.MiscUtils;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ThreadPaintShape implements IShapeable, BlockChangeBase {
	
	protected World world;
	protected BlockPos origin;
	protected int radiusX;
	protected int radiusY;
	protected int radiusZ;
	protected EnumFacing side;
	protected boolean isFinished = false;
	protected EntityPlayer entity;
	protected int count = 0;
	protected boolean shapeFinished = false;
	
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	
	protected Set<BlockPos> checkedList = new LinkedHashSet<BlockPos>();
	protected Set<BlockPos> checkedPos = new LinkedHashSet<BlockPos>();
	
	protected boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	
	protected IShapeGenerator generator;
	
	protected boolean forceBlocksToFall = false;
	protected boolean fillmode = true;
	
	protected List<IBlockState> fillBlockStates = Lists.<IBlockState>newArrayList();
	protected List<Integer> fillBlockChance = Lists.<Integer>newArrayList();
	protected List<IBlockState> replaceBlockStates = Lists.<IBlockState>newArrayList();
	
	@SuppressWarnings("deprecation")
	public ThreadPaintShape(World world, BlockPos origin, EnumFacing side, EntityPlayer entity, NBTTagCompound nbtData){
		
		this.world = world;
		this.origin = origin;
		this.radiusX = nbtData.getInteger("radiusX");
		this.radiusY = nbtData.getInteger("radiusY");
		this.radiusZ = nbtData.getInteger("radiusZ");
		this.side = side;
		this.entity = entity;
		this.generator = Shapes.VALUES[nbtData.getInteger("generator")].generator;
		this.forceBlocksToFall = nbtData.getBoolean("forcefall");
		this.fillmode = nbtData.getBoolean("fillmode");
		
		this.fillBlockStates.clear();
		this.fillBlockChance.clear();
		
		NBTTagCompound fillBlocks = nbtData.getCompoundTag("fillblocks");
		for(String key : fillBlocks.getKeySet()){
			ItemStack item = new ItemStack(fillBlocks.getCompoundTag(key).getCompoundTag("blockstate"));			
			this.fillBlockStates.add(Block.getBlockFromItem(item.getItem()).getStateFromMeta(item.getMetadata()));
			this.fillBlockChance.add(fillBlocks.getCompoundTag(key).getInteger("chance"));
		}
		
		this.replaceBlockStates.clear();
		
		int replace = nbtData.getInteger("replacemode");
		switch (replace) {
		case 1:
			this.replaceBlockStates.add(Blocks.AIR.getDefaultState());
			break;
			
		case 2:
			this.replaceBlockStates.add(world.getBlockState(origin));
			break;
		
		case 3:
			//Do Nothing
			break;
		case 4:
			NBTTagCompound replaceBlocks = nbtData.getCompoundTag("replaceblocks");
			for(String key : replaceBlocks.getKeySet()){
				ItemStack item = new ItemStack(replaceBlocks.getCompoundTag(key));
				this.replaceBlockStates.add(Block.getBlockFromItem(item.getItem()).getStateFromMeta(item.getMetadata()));
				System.out.println(this.replaceBlockStates);
			}
			break;
		default:
			break;
		}
		
	}	
	
	@Override
	public void setBlock(BlockPos tempPos) {
			
		currentlyCalculating = true;
		
		IBlockState blockstate = Blocks.AIR.getDefaultState();
		
		int chanceTotal = 0;
		
		for (Integer integer : fillBlockChance) {
			chanceTotal += integer;
		}
		
		int random = new Random().nextInt(chanceTotal);
		
		int curVal = 0;
		for(int i = 0; i < fillBlockChance.size(); i++){
			curVal += fillBlockChance.get(i);
			
			if(random < curVal){
				blockstate = fillBlockStates.get(i);
				break;
			}
		}
		
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
		
		if(this.replaceBlockStates != null && !this.replaceBlockStates.isEmpty() && !this.replaceBlockStates.contains(world.getBlockState(bpos.add(origin))))
			return;		
	
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
			
			if(forceBlocksToFall){
				tempList.add(new ChangeBlockToThis(blockpos1.up(), blockstate));
				checkedPos.add(blockpos1.up());
				count++;
			}
			
			else{
				tempList.add(new ChangeBlockToThis(blockpos1.up(), blockstate));
				count++;
			}
			
		}
		
		if(count > 4096){
			checkAndAddQueue();
			return;
		}
		
	}
	
	
	public boolean canFallInto(World worldIn, BlockPos pos){
		if(checkedPos.contains(pos)){
        	return false;
        }
        if (worldIn.isAirBlock(pos)) return true;
        Block block = worldIn.getBlockState(pos).getActualState(worldIn, pos).getBlock();
        Material material = worldIn.getBlockState(pos).getActualState(worldIn, pos).getMaterial();
        return block == Blocks.FIRE || material == Material.AIR || material == Material.WATER || material == Material.LAVA || block == BlockLoader.tempBlock;
    }
	
	
	@Override
	public void perform(){
		if(!currentlyCalculating && !shapeFinished){
			tempList.clear();
			generator.generateShape(radiusX, radiusY, radiusZ, this, fillmode);
		}
		
		if(shapeFinished){
			System.out.println("Finished");
			MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}
	}
	
    public World getWorld(){
        return this.world;
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

	@Override
	public void shapeFinished() {
		checkAndAddQueue();
		shapeFinished = true;
	}

	
}
