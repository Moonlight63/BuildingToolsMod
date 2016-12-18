package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner;
import net.minecraft.block.BlockBanner.BlockBannerHanging;
import net.minecraft.block.BlockBanner.BlockBannerStanding;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockVine;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.MiscUtils;

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
	
	protected List<Set<ChangeBlockToThis>> listSet = Lists.newArrayList();
	
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
		
		secondPassSet.addAll(playerwrap.rejectedSecondPass);
		entitySet.addAll(entities);
		
		this.copyToPos = new BlockPos(0, 0, 0);		
		this.rotation = 0;
		this.flipX = false;
		this.flipY = false;
		this.flipZ = false;
		this.saveUndo = false;
		
	}
	
	public EnumFacing getAdjustedRotation(EnumFacing facing){
		
		switch (this.rotation){
		
		case 0:
			
			if(!flipX && !flipZ){
				return facing;
			}
			else if(flipX && !flipZ){
				if(facing == EnumFacing.EAST || facing == EnumFacing.WEST){
					return facing.getOpposite();
				}
				else{
					return facing;
				}
			}
			else if(!flipX && flipZ){
				if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH){
					return facing.getOpposite();
				}
				else{
					return facing;
				}
			}
			else if(flipX && flipZ){
				return facing.getOpposite();
			}
			
			break;
		
		case 1:
			
			if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
				if(!flipX && !flipZ){
					return facing.rotateY();
				}
				else if(flipX && !flipZ){
					if(facing == EnumFacing.EAST || facing == EnumFacing.WEST){
						return facing.rotateY().getOpposite();
					}
					else{
						return facing.rotateY();
					}
				}
				else if(!flipX && flipZ){
					if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH){
						return facing.rotateY().getOpposite();
					}
					else{
						return facing.rotateY();
					}
				}
				else if(flipX && flipZ){
					return facing.rotateY().getOpposite();
				}
			}
			
			break;
			
		case 2:
			if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
				if(!flipX && !flipZ){
					return facing.rotateY().rotateY();
				}
				else if(flipX && !flipZ){
					if(facing == EnumFacing.EAST || facing == EnumFacing.WEST){
						return facing.rotateY().rotateY().getOpposite();
					}
					else{
						return facing.rotateY().rotateY();
					}
				}
				else if(!flipX && flipZ){
					if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH){
						return facing.rotateY().rotateY().getOpposite();
					}
					else{
						return facing.rotateY().rotateY();
					}
				}
				else if(flipX && flipZ){
					return facing.rotateY().rotateY().getOpposite();
				}
			}
			break;
			
		case 3:
			if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
				if(!flipX && !flipZ){
					return facing.rotateY().rotateY().rotateY();
				}
				else if(flipX && !flipZ){
					if(facing == EnumFacing.EAST || facing == EnumFacing.WEST){
						return facing.rotateY().rotateY().rotateY().getOpposite();
					}
					else{
						return facing.rotateY().rotateY().rotateY();
					}
				}
				else if(!flipX && flipZ){
					if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH){
						return facing.rotateY().rotateY().rotateY().getOpposite();
					}
					else{
						return facing.rotateY().rotateY().rotateY();
					}
				}
				else if(flipX && flipZ){
					return facing.rotateY().rotateY().rotateY().getOpposite();
				}
			}
			break;
			
		default:
			break;
		}
		return facing;
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
	
	public byte getSkullRotation(byte rot){
		
		byte tempInt = rot;
		
		if(flipX){
			tempInt = (byte) (16 - tempInt);
		}
		
		if(flipZ){
			//tempInt = (byte) (8 + tempInt);
			tempInt = (byte) (8 - tempInt);
		}
		
		switch (this.rotation) {
		case 0:
			//tempInt = rot;
			break;
			
		case 1:
			tempInt = (byte) (rot + 4);
			break;
			
		case 2:
			tempInt = (byte) (rot + 8);
			break;
			
		case 3:
			tempInt = (byte) (rot + 12);
			break;

		default:
			break;
		}
		
		if(tempInt > 15){
			tempInt = (byte) (tempInt - 16);
		}
		
		if(tempInt < 0){
			tempInt = (byte) (16 + tempInt);
		}
		
		return tempInt;
		
	}
	
	public int getSkullRotation(int rot){
		
		int tempInt = rot;
		System.out.println(rot);
		
		
		if(flipX){
			tempInt = (16 - tempInt);
		}
		
		if(flipZ){
			//tempInt = (8 + tempInt);
			tempInt = (8 - tempInt);
		}
		
		
		switch (this.rotation) {
		case 0:
			//tempInt = rot;
			break;
			
		case 1:
			tempInt = (tempInt + 4);
			break;
			
		case 2:
			tempInt = (tempInt + 8);
			break;
			
		case 3:
			tempInt = (tempInt + 12);
			break;

		default:
			break;
		}
		
		
		if(tempInt > 15){
			tempInt = tempInt - 16;
		}
		
		if(tempInt < 0){
			tempInt = 16 + tempInt;
		}
		
		return tempInt;
		
	}
	
	public void RunFirstPass(){
		
		currentlyCalculating = true;
		
		for(BlockInfoContainer bpos : selectionSet){
				
			IBlockState blockState = bpos.change.getBlockState();
			NBTTagCompound compound = bpos.change.getNBTTag();
			
			IProperty<Integer>    standingRotProperty = PropertyInteger.create("rotation", 0, 15);
			
			BlockPos normalizedPos = bpos.change.getBlockPos();
			BlockPos adjustedPos = getAdjustedBlockPos(normalizedPos);
			BlockPos newPos = adjustedPos.add(copyToPos);
			
			EnumFacing facing;
			
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
			
				switch (bpos.blockType) {
				case Standard:
					System.out.println("STANDARD BLOCK");
					if(flipY){
						System.out.println("NON-SLABS");
                		tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
					}
					else{
						tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
					}
					
					break;
					
				case Door:
					System.out.println("DOORS");
					facing = (EnumFacing)blockState.getValue(BlockDoor.FACING);
					
					if(!flipY){
						if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER){
							secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), newPos.down()));
						}
					}
					else{
						if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER){
							secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), newPos.down()));
						}
					}
					break;
					
				case TrapDoor:
					System.out.println("TRAPDOOR");
					facing = (EnumFacing)blockState.getValue(BlockTrapDoor.FACING);
					
					if(flipY){
            			if(blockState.getValue(BlockTrapDoor.HALF) == BlockTrapDoor.DoorHalf.TOP){
            				secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.BOTTOM).withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), newPos.offset(getAdjustedRotation(facing))));
            			}
            			else{
            				secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP).withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), newPos.offset(getAdjustedRotation(facing))));
            			}
					}
					else{
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), newPos.offset(getAdjustedRotation(facing).getOpposite())));
					}
					
					break;
					
				case Torch:
					System.out.println("TORCHES");
					facing = (EnumFacing)blockState.getValue(BlockTorch.FACING);
					
					if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), newPos.offset(getAdjustedRotation(facing).getOpposite())));
					}
					else{
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState, compound), newPos.offset(getAdjustedRotation(facing).getOpposite())));
					}
					
					break;
					
				case BannerHanging:
					System.out.println("BANNERS");
					facing = (EnumFacing)blockState.getValue(BlockBannerHanging.FACING);
					secondPassSet.add(new SecondPass(new ChangeBlockToThis(flipY ? newPos.up():newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound), (flipY ? newPos.up():newPos).offset(getAdjustedRotation(facing).getOpposite())));
				
					break;
					
				case Stairs:
					System.out.println("STAIRS");
					facing = (EnumFacing)blockState.getValue(BlockStairs.FACING);
					if(flipY){
						if(blockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP){
            				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
            			}
            			else{
            				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
            			}
					}
					else{
						System.out.println("NOT FLIP Y");
						System.out.println(newPos);
						tempList.add(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
					}
					
					break;
					
				case Ladder:
					facing = (EnumFacing)blockState.getValue(BlockLadder.FACING);
					secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ)), newPos.offset(getAdjustedRotation(facing).getOpposite())));
					break;
					
				case Skull:
					System.out.println("SKULLS");
					facing = (EnumFacing)blockState.getValue(BlockHorizontal.FACING);
					if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
						tempList.add(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
					}
					else{
						NBTTagCompound tempNBT = compound;
            			tempNBT.setByte("Rot", getSkullRotation(tempNBT.getByte("Rot")));
            			tempList.add(new ChangeBlockToThis(newPos, blockState, tempNBT));
					}
					break;
					
				case Rotating:
					
					facing = (EnumFacing)blockState.getValue(BlockHorizontal.FACING);
//					if(flipY){
//            			System.out.println("FLIP Y BLOCKS");
//            			tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockHorizontal.FACING, facing.getOpposite()), compound));
//            		}
//            		else{
            			System.out.println("ANY OTHER ROTATABLE BLOCK");
            			tempList.add(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
//            		}
					break;
					
				case Logs:
					System.out.println("LOGS");
//					if(rotation == 1 || rotation == 3){
//						if(blockState.getValue(BlockLog.LOG_AXIS) == BlockLog.EnumAxis.X){
//							tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.Z), compound));
//						}
//						else if(blockState.getValue(BlockLog.LOG_AXIS) == BlockLog.EnumAxis.Z){
//							tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockLog.LOG_AXIS, BlockLog.EnumAxis.X), compound));
//						}
//						else{
//							tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
//						}
//					}
//					else{
						tempList.add(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
//					}
					break;
					
				case Quartz_Pillar:
					System.out.println("QUARTZ");
//					if(rotation == 1 || rotation == 3){
//						if(blockState.getValue(BlockQuartz.VARIANT) == BlockQuartz.EnumType.LINES_X){
//							tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z), compound));
//						}
//						else if(blockState.getValue(BlockQuartz.VARIANT) == BlockQuartz.EnumType.LINES_Z){
//							tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_X), compound));
//						}
//						else{
//							tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
//						}
//					}
//					else{
						tempList.add(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ), compound));
//					}
					break;
					
				case Signs:			
					if(!flipY){
						System.out.println("SIGNS AND BANNERS");
						int tempInt = (Integer) blockState.getValue(standingRotProperty);
						
						System.out.println(tempInt);
						
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.
								withProperty(standingRotProperty, getSkullRotation(tempInt)),
								compound), newPos.down()));
					}
					break;
					
				case Carpet:
					if(!flipY){
						System.out.println("Carpet");
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState, compound), newPos.down()));
					}
					break;
					
				case Lever:
//					EnumFacing updownface = (((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite();
//					
//					if(this.rotation == 1 || this.rotation == 3){
//						if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_X || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_X){
//							updownface = EnumFacing.NORTH;
//						}
//						else if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_Z || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_Z){
//							updownface = EnumFacing.EAST;
//						}
//						else{
//							updownface = getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite();
//						}
//					}
//					else{
//						if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_X || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_X){
//							updownface = EnumFacing.EAST;
//						}
//						else if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_Z || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_Z){
//							updownface = EnumFacing.NORTH;
//						}
//						else{
//							updownface = getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite();
//						}
//					}
//					
//					BlockLever.EnumOrientation newfacing = BlockLever.EnumOrientation.forFacings(getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()), updownface);
//					if(flipY){
//						if(newfacing == BlockLever.EnumOrientation.DOWN_X){
//							newfacing = BlockLever.EnumOrientation.UP_X;
//						}
//						else if(newfacing == BlockLever.EnumOrientation.DOWN_Z){
//							newfacing = BlockLever.EnumOrientation.UP_Z;
//						}
//						else if(newfacing == BlockLever.EnumOrientation.UP_X){
//							newfacing = BlockLever.EnumOrientation.DOWN_X;
//						}
//						else if(newfacing == BlockLever.EnumOrientation.UP_Z){
//							newfacing = BlockLever.EnumOrientation.DOWN_Z;
//						}
//					}
					
					IBlockState state = blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ);
					
					if(flipY){
						if(state.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_X){
							state.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_X);
						}
						else if(state.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.DOWN_Z){
							state.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.UP_Z);
						}
						else if(state.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_X){
							state.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_X);
						}
						else if(state.getValue(BlockLever.FACING) == BlockLever.EnumOrientation.UP_Z){
							state.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.DOWN_Z);
						}
					}
					
					secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, state, compound), newPos.offset(state.getValue(BlockLever.FACING).getFacing().getOpposite())));
					break;
					
				case Rail:
					
					if(!flipY)
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withRotation(rot).withMirror(mirX).withMirror(mirZ)), newPos.down()));
					
					break;
					
				case Slab:
					if(flipY){
						System.out.println("SLABS");
            			if(blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP){
            				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM), compound));
            			}
            			else{
            				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP), compound));
            			}
					}
					else{
						System.out.println("NON-ROTATABLE");
						tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
					}
					break;

				default:
					break;
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
    			addSetToList();
    			//return;
    		}
			
				//System.out.println("REMOVING BPOS");
			//	firstPassCount++;
			//	selectionSet.remove(bpos);
				
			//}
			//else{
			//	System.out.println("Break Loop");
			//	break;
			//}
			
		}
		if(!tempList.isEmpty())
			addSetToList();
		
		//return tempList;
		
	}
	
	public void RunSecondPass(){
		
		//Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
		//int secondPassCount = 0;
		
		currentlyCalculating = true;
		
		for(SecondPass pass : secondPassSet){
			
			while (world.isAirBlock(pass.posToCheckForAir)) {
				
			}
			
			if(!world.isAirBlock(pass.posToCheckForAir)){
//				if(pass.blockChange.getBlockState().getBlock() instanceof BlockDoor){
//					ItemDoor.placeDoor(world, pass.blockChange.getBlockPos(), (EnumFacing) pass.blockChange.getBlockState().getValue(BlockDoor.FACING), pass.blockChange.getBlockState().getBlock(), false);
//				}
//				else{
					tempList.add(pass.blockChange);
				//}
				count++;
				secondPassSet.remove(pass);
			}
			//else{
			//	pass.tries++;
			//}
			
			if(pass.tries > 25){
				entity.addChatMessage(new TextComponentString("Tried 25 times to place block at " + pass.blockChange.getBlockPos()));
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().rejectedSecondPass.add(pass);
				secondPassSet.remove(pass);
			}
			
			if(count > 4096){
    			addSetToList();
    			//return;
    		}
			
		}
		if(!tempList.isEmpty())
			addSetToList();
		
	}
	
	public void RunEntityPass(){
		
		//int passCount = 0;
		currentlyCalculating = true;
		for(EntityPass e : entityPassSet){
			//if(passCount < 10){
			
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
			//	passCount++;
			//}
			//else{
			//	entityPassSet.remove(e);
			//}
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
		
		if(listSet.isEmpty() && canFinish){
			System.out.println("Finished");
			if(saveUndo)
				MiscUtils.dumpUndoList(entity);
			isFinished = true;
		}
		
//		if(!currentlyCalculating){
//			//System.out.println("Starting Paste");
//			if(!selectionSet.isEmpty()){
//				//entity.addChatComponentMessage(new ChatComponentText("Pasting Phase 1"));
//				//System.out.println("Running First Pass");
//				Set<ChangeBlockToThis> temp1 = RunFirstPass();
//				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(temp1, world, true));
//				if(saveUndo)
//					BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(temp1, world));
//				currentlyCalculating = false;
//				System.out.println("First Pass Done");
//			}
//			else{
//				if(!secondPassSet.isEmpty()){
//					//entity.addChatComponentMessage(new ChatComponentText("Pasting Phase 2"));
//					//System.out.println("Running Second Pass");
//					Set<ChangeBlockToThis> temp2 = RunSecondPass();
//					BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(temp2, world, true));
//					if(saveUndo)
//						BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(temp2, world));
//					currentlyCalculating = false;
//					System.out.println("Second Pass Done");
//				}
//				else{
//					if(!entityPassSet.isEmpty()){
//						//entity.addChatComponentMessage(new ChatComponentText("Pasting Entities"));
//						//System.out.println("Running Entity Pass");
//						RunEntityPass();
//						currentlyCalculating = false;
//						System.out.println("Entity Pass Done");
//					}
//					else{
//						if(saveUndo)
//							MiscUtils.dumpUndoList(entity);
//						entity.addChatComponentMessage(new ChatComponentText("Finished!"));
//						System.out.println("Finished");
//						isFinished = true;
//					}
//				}
//			}			
//		}
		
	}
	
	public boolean isFinished(){
		return isFinished;
	}
	
	public void checkAndAddQueue(){
//		if(BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue != null)
//			return;
		
		if(saveUndo)
			BuildingTools.getPlayerRegistry().getPlayer(entity).get().tempUndoList.addAll(MiscUtils.CalcUndoList(listSet.get(0), world));
		BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue.add(new BlockChangeQueue(listSet.get(0), world, true));
		
		listSet.remove(0);
		
	}
	
	public void addSetToList(){
		listSet.add(Sets.newHashSet(tempList));
		tempList.clear();
		count = 0;
		checkAndAddQueue();
	}
	
	public class SecondPass{
		public final ChangeBlockToThis blockChange;
		public final BlockPos posToCheckForAir;
		public int tries;
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
