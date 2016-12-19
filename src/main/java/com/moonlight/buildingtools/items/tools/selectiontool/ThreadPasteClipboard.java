package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ThreadPasteClipboard implements BlockChangeBase{
	
	protected AxisAlignedBB entityDetectionBox;
	protected World world;
	protected BlockPos copyToPos;
	protected EntityPlayer entity;
	
	protected List<BlockInfoContainer> selectionSet = new CopyOnWriteArrayList<BlockInfoContainer>();
	protected Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
	protected int count;
	
	protected boolean isFinished = false;
	protected boolean canFinish = false;
	
	protected Set<ChangeBlockToThis> firstPassSet = new CopyOnWriteArraySet<ChangeBlockToThis>();
	protected Set<SecondPass> secondPassSet = new CopyOnWriteArraySet<SecondPass>();
	protected Set<Entity> entitySet = new CopyOnWriteArraySet<Entity>();
	protected Set<EntityPass> entityPassSet = new CopyOnWriteArraySet<EntityPass>();
	
	protected boolean currentlyCalculating = false;
	
	protected Set<BlockPos> checkedEntityPos = new CopyOnWriteArraySet<BlockPos>();
	protected int rotation;
	protected boolean flipX;
	protected boolean flipY;
	protected boolean flipZ;
	
	protected boolean saveUndo = true;
	
	public ThreadPasteClipboard(World world, EntityPlayer player, BlockPos copyTo, int rot, boolean flipx, boolean flipy, boolean flipz){
		
		this.world = world;
		this.copyToPos = copyTo;
		this.entity = player;
		
		PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
		selectionSet.addAll(playerwrap.currentCopyClipboard);
		entitySet.addAll(playerwrap.currentClipboardEntities);
		this.rotation = rot;
		
		this.flipX = flipx;
		this.flipY = flipy;
		this.flipZ = flipz;
		
	}
	
	public ThreadPasteClipboard(World world, EntityPlayer player, Set<Entity> entities){
		
		this.world = world;
		this.entity = player;		
		
		PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
		if(!playerwrap.undolist.isEmpty())
			selectionSet = playerwrap.undolist.pollLast();
		
		entitySet.addAll(entities);
		
		this.copyToPos = new BlockPos(0, 0, 0);		
		this.rotation = 0;
		this.flipX = false;
		this.flipY = false;
		this.flipZ = false;
		this.saveUndo = false;
		
	}
	
	public EnumFacing getAdjustedRotation(EnumFacing facing){
		
		EnumFacing face2 = facing;
		switch (this.rotation){
		case 0:
			break;
		case 1:
			face2.rotateY();
			break;
		case 2:
			face2.rotateY().rotateY();
			break;
		case 3:
			face2.rotateY().rotateY().rotateY();
			break;
		default:
			break;
		}
		
		if(flipX && !flipZ){
			if(face2 == EnumFacing.EAST || face2 == EnumFacing.WEST){
				return face2.getOpposite();
			}
			else{
				return face2;
			}
		}
		else if(!flipX && flipZ){
			if(face2 == EnumFacing.NORTH || face2 == EnumFacing.SOUTH){
				return face2.getOpposite();
			}
			else{
				return face2;
			}
		}
		else if(flipX && flipZ){
			return face2.getOpposite();
		}
		
		return face2;
	}
	
	public BlockPos getAdjustedBlockPos(BlockPos originalPos){
		
		BlockPos tempPos = originalPos;
		
		PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(entity).get();
		
		if(flipX){
			tempPos = new BlockPos(-tempPos.getX()+playerwrap.clipboardMaxPos.getX(), tempPos.getY(), tempPos.getZ());
		}
		if(flipY){
			tempPos = new BlockPos(tempPos.getX(), -tempPos.getY()+playerwrap.clipboardMaxPos.getY(), tempPos.getZ());
		}
		if(flipZ){
			tempPos = new BlockPos(tempPos.getX(), tempPos.getY(), -tempPos.getZ()+playerwrap.clipboardMaxPos.getZ());
		}
		
		switch (this.rotation) {
		case 0:
			//tempPos = originalPos;
			break;
			
		case 1:
			tempPos = new BlockPos(-tempPos.getZ(), tempPos.getY(), tempPos.getX());
			break;
			
		case 2:
			BlockPos pos1 = new BlockPos(-tempPos.getZ(), tempPos.getY(), tempPos.getX());
			tempPos = new BlockPos(-pos1.getZ(), pos1.getY(), pos1.getX());
			break;
			
		case 3:
			BlockPos pos2 = new BlockPos(-tempPos.getZ(), tempPos.getY(), tempPos.getX());
			BlockPos pos3 = new BlockPos(-pos2.getZ(), pos2.getY(), pos2.getX());
			tempPos = new BlockPos(-pos3.getZ(), pos3.getY(), pos3.getX());
			break;

		default:
			break;
		}
		
		return tempPos;
	}

	public IBlockState getYFlipped(BlockInfoContainer blockInfo){
		
		IBlockState blockState = blockInfo.change.getBlockState();
		System.out.println(blockInfo.change.getBlockState());
		System.out.println(blockInfo.blockType);
		switch (blockInfo.blockType) {
		case Standard:
			break;
		case Door:
			System.out.println("DOORS");
			if(!flipY){
				if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER){
					return blockState;
				}
				else return null;
			}
			else{
				if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER){
					return blockState;
				}
				else return null;
			}
		case TrapDoor:
			System.out.println("TRAPDOOR");
			if(flipY){
				if(blockState.getValue(BlockTrapDoor.HALF) == BlockTrapDoor.DoorHalf.TOP){
					return blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.BOTTOM);
				}
				else{
					return blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP);			
				}
			}
			break;
		case Torch:
			System.out.println("TORCHES");			
			if(flipY){
				EnumFacing facing = (EnumFacing)blockState.getValue(BlockTorch.FACING);
				if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
					return blockState;
				}
				else{
					return null;
				}
			}
			break;
		case Stairs:
			System.out.println("STAIRS");
			if(flipY){
				if(blockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP){
					return blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM);
				}
				else{
					return blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP);
				}
			}
			break;
		case Signs:		
			if(flipY)
				return null;
			break;
		case Carpet:
			if(flipY)
				return null;
			break;
		case Lever:
			if(flipY){
				if(blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_X){
					blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_X);
				}
				else if(blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_Z){
					blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_Z);
				}
				else if(blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_X){
					blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_X);
				}
				else if(blockState.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_Z){
					blockState.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_Z);
				}
				
				return blockState;
			}
			break;
		case Rail:
			if(flipY)
				return null;
			break;
		case RedStone:
			if(flipY)
				return null;
			break;
		case Slab:
			if(flipY){
				if(blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP){
					return blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM);
				}
				else{
					return blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP);
				}
			}
			break;
		default:
			break;
		}
		
		return blockState;
	}
	
	public EnumFacing getSecondPassPos(BlockInfoContainer.BlockTypes blocktype, IBlockState state){
		
		switch (blocktype) {
		case BannerHanging:
			break;
		case Carpet:
			return EnumFacing.DOWN;
		case Door:
			return EnumFacing.DOWN;
		case Ladder:
			break;
		case Lever:
			return state.getValue(BlockLever.FACING).getFacing().getOpposite();
		case Logs:
			break;
		case Quartz_Pillar:
			break;
		case Rail:
			return EnumFacing.DOWN;
		case Rotating:
			break;
		case Signs:
			return EnumFacing.DOWN;
		case Skull:
			break;
		case Slab:
			break;
		case Stairs:
			break;
		case Standard:
			break;
		case Torch:
			return state.getValue(BlockTorch.FACING).getOpposite();
		case RedStone:
			return EnumFacing.DOWN;
		case Buttons:
			return state.getValue(BlockButton.FACING).getOpposite();
		case TripWire:
			break;
		case TrapDoor:
			break;

		default:
			break;
		}
		
		return state.getValue(BlockHorizontal.FACING).getOpposite();
	}
	
	public void RunFirstPass(){
		
		currentlyCalculating = true;
		
		for(BlockInfoContainer bpos : selectionSet){
			
			NBTTagCompound compound = bpos.change.getNBTTag();
			
			BlockPos normalizedPos = bpos.change.getBlockPos();
			BlockPos adjustedPos = getAdjustedBlockPos(normalizedPos);
			BlockPos newPos = adjustedPos.add(copyToPos);
			
			Rotation rot = Rotation.NONE;
			Mirror mirX = Mirror.NONE;
			Mirror mirZ = Mirror.NONE;
			
			if(rotation == 1)
				rot = Rotation.CLOCKWISE_90;
			else if(rotation == 2)
				rot = Rotation.CLOCKWISE_180;
			else if(rotation == 3)
				rot = Rotation.COUNTERCLOCKWISE_90;
			if(flipX)
				mirX = Mirror.FRONT_BACK;
			if(flipZ)
				mirZ = Mirror.LEFT_RIGHT;
			
			
			
			if(bpos.setAir){
				tempList.add(new ChangeBlockToThis(newPos, Blocks.AIR.getDefaultState()));
			}
			else{
				if(!bpos.needsSecondPass){
					IBlockState state = getYFlipped(bpos);
					if(state != null)
						tempList.add(new ChangeBlockToThis(newPos, state.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
				}
				else if(bpos.needsSecondPass){
					IBlockState state = getYFlipped(bpos);
					if(state != null){
						state = state.withRotation(rot).withMirror(mirX).withMirror(mirZ);
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, state, compound), newPos.offset(getSecondPassPos(bpos.blockType, state))));
					}
				}
				
			}
			
			if(!entitySet.isEmpty()){
				System.out.println("ENTITIES");
				for(Entity e : entitySet){
					if (e instanceof EntityHanging){
						BlockPos entPos = getAdjustedBlockPos(((EntityHanging)e).getHangingPosition());
						entityPassSet.add(new EntityPass(entPos.add(copyToPos), e, getAdjustedRotation(e.getHorizontalFacing()).getOpposite()));
						entitySet.remove(e);
					}
					else{
						entitySet.remove(e);
					}
				}
			}
			
			count++;
			
			if(count > 4096){
				checkAndAddQueue();
    			//return;
    		}
			
		}
		if(!tempList.isEmpty())
			checkAndAddQueue();
		
	}
	
	public void RunSecondPass(){
		
		currentlyCalculating = true;
		
		for(SecondPass pass : secondPassSet){
			
			while (world.isAirBlock(pass.posToCheckForAir)) {
			}
			
			if(!world.isAirBlock(pass.posToCheckForAir)){
				tempList.add(pass.blockChange);
				count++;
				secondPassSet.remove(pass);
			}
			
			if(count > 4096){
				checkAndAddQueue();
    			//return;
    		}
			
		}
		if(!tempList.isEmpty())
			checkAndAddQueue();
		
	}
	
	public void RunEntityPass(){
		
		currentlyCalculating = true;
		for(EntityPass e : entityPassSet){
			
			while (world.isAirBlock(e.placmentPos.offset(e.posToCheckForAir))) {
				
			}
			
			if(!world.isAirBlock(e.placmentPos.offset(e.posToCheckForAir))){
				if(e.entityToPlace instanceof EntityPainting){
					world.spawnEntityInWorld(new EntityPainting(world, e.placmentPos, e.posToCheckForAir.getOpposite(), ((EntityPainting)e.entityToPlace).art.title));
					entityPassSet.remove(e);
				}
				else if(e.entityToPlace instanceof EntityItemFrame){
					EntityItemFrame itemframe = new EntityItemFrame(world, e.placmentPos, e.posToCheckForAir.getOpposite());
					itemframe.setDisplayedItem(((EntityItemFrame)e.entityToPlace).getDisplayedItem());
					world.spawnEntityInWorld(itemframe);
					entityPassSet.remove(e);
				}
			}
			
		}
		
		canFinish = true;
		
	}
	
	public void perform(){
		
		if(!currentlyCalculating){
			currentlyCalculating = true;
			RunFirstPass();
			RunSecondPass();
			RunEntityPass();
			
		}
		
		if(canFinish){
			System.out.println("Finished");
			if(saveUndo)
				MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}
		
	}
	
	public boolean isFinished(){
		return isFinished;
	}
	
	public void checkAndAddQueue(){
		
		if(saveUndo)
			BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(tempList, world));
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(tempList, world, true));
		
		tempList.clear();
		count = 0;
		
	}
	
	public class SecondPass{
		public final ChangeBlockToThis blockChange;
		public final BlockPos posToCheckForAir;
		public SecondPass(ChangeBlockToThis blockChange, BlockPos posForCheck){
			this.blockChange = blockChange;
			this.posToCheckForAir = posForCheck;
		}
	}
	
	public class EntityPass{
		public final BlockPos placmentPos;
		public final Entity entityToPlace;
		public final EnumFacing posToCheckForAir;
		public EntityPass(BlockPos posToPlace, Entity entityToPlace, EnumFacing posForCheck){
			this.placmentPos = posToPlace;
			this.entityToPlace = entityToPlace;
			this.posToCheckForAir = posForCheck;
		}
	}
	
}
