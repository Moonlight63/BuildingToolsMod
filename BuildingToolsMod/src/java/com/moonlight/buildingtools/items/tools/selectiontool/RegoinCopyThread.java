package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBanner.BlockBannerHanging;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTrapDoor;
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
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.BlockChangeQueue;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class RegoinCopyThread implements BlockChangeBase{
	
	protected AxisAlignedBB entityDetectionBox;
	protected World world;
	protected BlockPos copyToPos;
	protected EntityPlayer entity;
	
	protected boolean isFinished = false;
	
	protected Set<ChangeBlockToThis> firstPassSet = new CopyOnWriteArraySet<ChangeBlockToThis>();
	protected Set<SecondPass> secondPassSet = new CopyOnWriteArraySet<SecondPass>();
	protected Set<Entity> entitySet = new CopyOnWriteArraySet<Entity>();
	protected Set<EntityPass> entityPassSet = new CopyOnWriteArraySet<EntityPass>();
	
	protected Set<ChangeBlockToThis> selectionSet = new CopyOnWriteArraySet<ChangeBlockToThis>();
	
	protected boolean currentlyCalculating = false;
	
	protected Set<BlockPos> checkedEntityPos = new CopyOnWriteArraySet<BlockPos>();
	protected int rotation;
	protected boolean flipX;
	protected boolean flipY;
	protected boolean flipZ;
	
	public RegoinCopyThread(World world, EntityPlayer player, BlockPos copyTo, int rot, boolean flipx, boolean flipy, boolean flipz){
		
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
	
	public Set<ChangeBlockToThis> RunFirstPass(){
		
		Set<ChangeBlockToThis> tempList = new HashSet<ChangeBlockToThis>();
		int firstPassCount = 0;
		
		currentlyCalculating = true;
		
		for(ChangeBlockToThis bpos : selectionSet){
			
			if(firstPassCount < 4096){
				//System.out.println(firstPassCount);
				
				IBlockState blockState = bpos.getBlockState();//world.getBlockState(bpos);
				NBTTagCompound compound = bpos.getNBTTag();
				Block block = blockState.getBlock();
				
				ImmutableMap<?, ?> properties = blockState.getProperties();
				IProperty directionalBlockProperty = PropertyDirection.create("facing"/*, EnumFacing.Plane.HORIZONTAL*/);
				IProperty logDirectionProperty = PropertyEnum.create("axis", BlockLog.EnumAxis.class);
				IProperty quartzPillerProperty = PropertyEnum.create("variant", BlockQuartz.EnumType.class);
				IProperty bannerStandingRotation = PropertyInteger.create("rotation", 0, 15);
				
				BlockPos normalizedPos = bpos.getBlockPos();//.subtract(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ));
				BlockPos adjustedPos = getAdjustedBlockPos(normalizedPos);
				BlockPos newPos = adjustedPos.add(copyToPos);				
				
				//GET BLOCKS THAT HAVE A ROTATION STATE
				if(properties.containsKey(directionalBlockProperty)){
					EnumFacing facing = (EnumFacing)blockState.getValue(directionalBlockProperty);
					
					//SPECIAL BLOCK CASES THAT NEED A SECOND PASS
					
					//Doors
					if(block instanceof BlockDoor){
						System.out.println("DOORS");
						if(!flipY){
							if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.LOWER){
								secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), newPos.down()));
							}
						}
						else{
							if(blockState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER){
								secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), newPos.down()));
							}
						}
					}
					
					
					//TrapDoors
					else if(block instanceof BlockTrapDoor){
						System.out.println("TRAPDOOR");
						if(flipY){
	            			if(blockState.getValue(BlockTrapDoor.HALF) == BlockTrapDoor.DoorHalf.TOP){
	            				secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.BOTTOM).withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), newPos.offset(getAdjustedRotation(facing))));
	            			}
	            			else{
	            				secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.TOP).withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), newPos.offset(getAdjustedRotation(facing))));
	            			}
						}
						else{
							secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), newPos.offset(getAdjustedRotation(facing).getOpposite())));
						}
            		}
					
					//Torches
					else if (block instanceof BlockTorch){
						System.out.println("TORCHES");
						if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
							secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), newPos.offset(getAdjustedRotation(facing).getOpposite())));
						}
						else{
							secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState, compound), newPos.offset(getAdjustedRotation(facing).getOpposite())));
						}
						
					}
					
					//Hanging Banners
					else if (block instanceof BlockBannerHanging){
						System.out.println("BANNERS");
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(flipY ? newPos.up():newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound), (flipY ? newPos.up():newPos).offset(getAdjustedRotation(facing).getOpposite())));
					}
					
					//Any Other 'Rotatable' Block
					else{
						if(facing != EnumFacing.UP && facing != EnumFacing.DOWN){
							if(flipY){
								if(block instanceof BlockStairs){
									System.out.println("STAIRS");
		                			if(blockState.getValue(BlockStairs.HALF) == BlockStairs.EnumHalf.TOP){
		                				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.BOTTOM).withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound));
		                			}
		                			else{
		                				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockStairs.HALF, BlockStairs.EnumHalf.TOP).withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound));
		                			}
		                		}
		                		else{
		                			System.out.println("NOT STAIRS");
		                			tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound));
		                		}
							}
							else{
								System.out.println("NOT FLIP Y");
								System.out.println(newPos);
								tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, getAdjustedRotation(facing)), compound));
							}
						}
						else{
							//SKULL EXCEPTION
	                		if(block instanceof BlockSkull){
	                			System.out.println("SKULLS");
	                			NBTTagCompound tempNBT = compound;
	                			tempNBT.setByte("Rot", getSkullRotation(tempNBT.getByte("Rot")));
	                			tempList.add(new ChangeBlockToThis(newPos, blockState, tempNBT));
	                		}
	                		if(flipY){
	                			System.out.println("FLIP Y BLOCKS");
	                			tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(directionalBlockProperty, facing.getOpposite()), compound));
	                		}
	                		else{
	                			System.out.println("ANY OTHER ROTATABLE BLOCK");
	                			tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
	                		}
						}
						
					}
								
				}
				
				//LOGS
				else if(properties.containsKey(logDirectionProperty)){
					System.out.println("LOGS");
					if(blockState.getValue(logDirectionProperty) == BlockLog.EnumAxis.X){
						tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(logDirectionProperty, BlockLog.EnumAxis.Z), compound));
					}
					else if(blockState.getValue(logDirectionProperty) == BlockLog.EnumAxis.Z){
						tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(logDirectionProperty, BlockLog.EnumAxis.X), compound));
					}
					else{
						tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
					}
				}
				
				//Quartz Pillar
				else if(properties.containsKey(quartzPillerProperty)){
					System.out.println("QUARTZ");
					if(blockState.getValue(quartzPillerProperty) == BlockQuartz.EnumType.LINES_X){
						tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(quartzPillerProperty, BlockQuartz.EnumType.LINES_Z), compound));
					}
					else if(blockState.getValue(quartzPillerProperty) == BlockQuartz.EnumType.LINES_Z){
						tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(quartzPillerProperty, BlockQuartz.EnumType.LINES_X), compound));
					}
					else{
						tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
					}
				}
				
				//STANDING BANNERS AND SIGNS
				else if (properties.containsKey(bannerStandingRotation)){
					if(!flipY){
						System.out.println("SIGNS AND BANNERS");
						
						
						
						int tempInt = (Integer) blockState.getValue(bannerStandingRotation);
						
						System.out.println(tempInt);
						
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.
								withProperty(bannerStandingRotation, getSkullRotation(tempInt)),
								compound), newPos.down()));
					}
				}
				
				//CARPET
				else if (block instanceof BlockCarpet || block instanceof BlockFlowerPot){
					if(!flipY){
						System.out.println("Carpet");
						secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState, compound), newPos.down()));
					}
				}
				
				//LEVERS
				else if(block instanceof BlockLever){
					
					EnumFacing updownface = (((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite();
					
					if(this.rotation == 1 || this.rotation == 3){
						if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_X || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_X){
							updownface = EnumFacing.NORTH;
						}
						else if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_Z || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_Z){
							updownface = EnumFacing.EAST;
						}
						else{
							updownface = getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite();
						}
					}
					else{
						if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_X || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_X){
							updownface = EnumFacing.EAST;
						}
						else if(((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.DOWN_Z || ((BlockLever.EnumOrientation)blockState.getValue(BlockLever.FACING)) == BlockLever.EnumOrientation.UP_Z){
							updownface = EnumFacing.NORTH;
						}
						else{
							updownface = getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite();
						}
					}
					
					BlockLever.EnumOrientation newfacing = BlockLever.EnumOrientation.forFacings(getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()), updownface);
					if(flipY){
						if(newfacing == BlockLever.EnumOrientation.DOWN_X){
							newfacing = BlockLever.EnumOrientation.UP_X;
						}
						else if(newfacing == BlockLever.EnumOrientation.DOWN_Z){
							newfacing = BlockLever.EnumOrientation.UP_Z;
						}
						else if(newfacing == BlockLever.EnumOrientation.UP_X){
							newfacing = BlockLever.EnumOrientation.DOWN_X;
						}
						else if(newfacing == BlockLever.EnumOrientation.UP_Z){
							newfacing = BlockLever.EnumOrientation.DOWN_Z;
						}
					}
					secondPassSet.add(new SecondPass(new ChangeBlockToThis(newPos, blockState.withProperty(BlockLever.FACING, newfacing), compound), newPos.offset(
							flipY ? getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()) : 
								getAdjustedRotation(((BlockLever.EnumOrientation) blockState.getValue(BlockLever.FACING)).getFacing()).getOpposite()
									)));
				
				}
				
				else{
					
					if(flipY){
                		if(block instanceof BlockSlab){
                			System.out.println("SLABS");
                			if(blockState.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP){
                				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM), compound));
                			}
                			else{
                				tempList.add(new ChangeBlockToThis(newPos, blockState.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP), compound));
                			}
                		}
                		else{
                			System.out.println("NON-SLABS");
                			tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
                		}
					}
					else{
						System.out.println("NON-ROTATABLE");
						tempList.add(new ChangeBlockToThis(newPos, blockState, compound));
					}
					
				}
				
				if(!entitySet.isEmpty()){
					System.out.println("ENTITIES");
					for(Entity e : entitySet){
						if (e instanceof EntityHanging){
							BlockPos entPos = getAdjustedBlockPos(((EntityHanging)e).func_174857_n());
							entityPassSet.add(new EntityPass(entPos.add(copyToPos), e, getAdjustedRotation(e.getHorizontalFacing()).getOpposite()));
							entitySet.remove(e);
						}
						else{
							entitySet.remove(e);
						}
					}
				}
			
				System.out.println("REMOVING BPOS");
				firstPassCount++;
				selectionSet.remove(bpos);
				
			}
			
			else{
				System.out.println("Break Loop");
				break;
			}
			
		}
		
		return tempList;
		
	}
	
	
	public Set<ChangeBlockToThis> RunSecondPass(){
		
		Set<ChangeBlockToThis> tempSet = new HashSet<ChangeBlockToThis>();
		int secondPassCount = 0;
		
		currentlyCalculating = true;
		
		for(SecondPass pass : secondPassSet){
			if(secondPassCount < 4096){
				if(!world.isAirBlock(pass.posToCheckForAir)){
					if(pass.blockChange.getBlockState().getBlock() instanceof BlockDoor){
						ItemDoor.placeDoor(world, pass.blockChange.getBlockPos(), (EnumFacing) pass.blockChange.getBlockState().getValue(BlockDoor.FACING), pass.blockChange.getBlockState().getBlock());
					}
					else{
						tempSet.add(pass.blockChange);
					}
					secondPassCount++;
					secondPassSet.remove(pass);
				}
			}
			else{
				break;
			}
		}
		return tempSet;
		
	}
	
	public void RunEntityPass(){
		
		int passCount = 0;
		currentlyCalculating = true;
		for(EntityPass e : entityPassSet){
			if(passCount < 10){
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
				passCount++;
			}
			else{
				entityPassSet.remove(e);
			}
		}
		
	}
	
	public void perform(){
		
		if(!currentlyCalculating){
			//System.out.println("Starting Paste");
			if(!selectionSet.isEmpty()){
				System.out.println("Running First Pass");
				BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(RunFirstPass(), world, true);
				currentlyCalculating = false;
				System.out.println("First Pass Done");
			}
			else{
				if(!secondPassSet.isEmpty()){
					System.out.println("Running Second Pass");
					BuildingTools.getPlayerRegistry().getPlayer(entity).get().pendingChangeQueue = new BlockChangeQueue(RunSecondPass(), world, true);
					currentlyCalculating = false;
					System.out.println("Second Pass Done");
				}
				else{
					if(!entityPassSet.isEmpty()){
						System.out.println("Running Entity Pass");
						RunEntityPass();
						currentlyCalculating = false;
						System.out.println("Entity Pass Done");
					}
					else{
						System.out.println("Finished");
						isFinished = true;
					}
				}
			}			
		}
		
	}
	
	public boolean isFinished(){
		return isFinished;
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
