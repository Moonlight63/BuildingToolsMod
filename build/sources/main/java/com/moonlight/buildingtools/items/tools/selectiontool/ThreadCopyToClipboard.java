package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.shapes.GeometryUtils;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class ThreadCopyToClipboard implements BlockChangeBase, IShapeable{
	
	protected StructureBoundingBox structureBoundingBox;
	protected AxisAlignedBB entityDetectionBox;
	protected World world;
	protected EntityPlayer entity;
	
	protected boolean isFinished = false;
	protected Set<ChangeBlockToThis> selectionSet = new LinkedHashSet<ChangeBlockToThis>();
	protected Set<Entity> entitySet = new LinkedHashSet<Entity>();
	
	public boolean selectionCalculated = false;
	protected boolean currentlyCalculating = false;
	
	public ThreadCopyToClipboard(BlockPos blockpos1, BlockPos blockpos2, World world, EntityPlayer player){
		this.structureBoundingBox = new StructureBoundingBox(blockpos1, blockpos2);
		
		int p1x = (blockpos1.getX() <= blockpos2.getX()) ? blockpos1.getX() : blockpos1.getX() + 1;
        int p1y = (blockpos1.getY() <= blockpos2.getY()) ? blockpos1.getY() : blockpos1.getY() + 1;
        int p1z = (blockpos1.getZ() <= blockpos2.getZ()) ? blockpos1.getZ() : blockpos1.getZ() + 1;
        int p2x = (blockpos2.getX() < blockpos1.getX()) ? blockpos2.getX() : blockpos2.getX() + 1;
        int p2y = (blockpos2.getY() < blockpos1.getY()) ? blockpos2.getY() : blockpos2.getY() + 1;
        int p2z = (blockpos2.getZ() < blockpos1.getZ()) ? blockpos2.getZ() : blockpos2.getZ() + 1;
		
		this.entityDetectionBox = new AxisAlignedBB(new BlockPos(p1x, p1y, p1z), new BlockPos(p2x, p2y, p2z));
		this.world = world;		
		this.entity = player;
	}
	
	public ChangeBlockToThis addBlockWithNBT(BlockPos oldPosOrNull, IBlockState blockState, BlockPos newPos){
		if(oldPosOrNull != null && world.getTileEntity(oldPosOrNull) != null){
    		NBTTagCompound compound = new NBTTagCompound();
    		world.getTileEntity(oldPosOrNull).writeToNBT(compound);
    		return new ChangeBlockToThis(newPos, blockState, compound);
		}
    	else{
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
	
	@Override
	public void setBlock(BlockPos bpos){
		System.out.println(world);
		if(bpos.getY() > 0 && bpos.getY() < 256 && !world.isAirBlock(bpos)){
			currentlyCalculating = true;
			selectionSet.add(addBlockWithNBT(bpos, world.getBlockState(bpos), bpos.add(new BlockPos(-structureBoundingBox.minX, -structureBoundingBox.minY, -structureBoundingBox.minZ))));
		}
		
		
	}
	
	//protected int count = 0;
	public void perform(){
		
		if(!currentlyCalculating){
			
			if(!selectionCalculated){
				
				entity.addChatComponentMessage(new ChatComponentText("Copying Selection. Please Wait!"));
				
				GeometryUtils.makeFilledCube(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ), structureBoundingBox.getXSize()-1, structureBoundingBox.getYSize()-1, structureBoundingBox.getZSize()-1, this);
				
				List<Entity> entitiesInBox = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(entityDetectionBox.minX,  entityDetectionBox.minY, entityDetectionBox.minZ, entityDetectionBox.maxX, entityDetectionBox.maxY, entityDetectionBox.maxZ));
				
				//PAINTINGS AND ITEM FRAMES
				if(!entitiesInBox.isEmpty()){
					for(Entity e : entitiesInBox){
						if (e instanceof EntityHanging){
							//Class<? extends EntityHanging> tempEntclass = e.getClass();
							
							//e.setDead();
							
							
							EntityHanging tempEnt = (EntityHanging) e;
//							if(e instanceof EntityItemFrame){
//								tempEnt = new EntityItemFrame(world, e.getPosition(), e.func_181012_aH());
//							}
//							else{
//								tempEnt = new EntityPainting(world, e.getPosition(), e.func_181012_aH(), ((EntityPainting)e).art.title);
//							}
//							
							
									//(EntityHanging) e;
							NBTTagCompound entNBT = new NBTTagCompound();
							
							tempEnt.writeEntityToNBT(entNBT);
							
							entNBT.setInteger("TileX", ((EntityHanging) e).getHangingPosition().getX() - structureBoundingBox.minX);
							entNBT.setInteger("TileY", ((EntityHanging) e).getHangingPosition().getY() - structureBoundingBox.minY);
							entNBT.setInteger("TileZ", ((EntityHanging) e).getHangingPosition().getZ() - structureBoundingBox.minZ);
							
							tempEnt.readEntityFromNBT(entNBT);
							
							entitySet.add(tempEnt);
							
							world.spawnEntityInWorld(e);
							//if(!checkedEntityPos.contains(((EntityHanging)e).func_174857_n().subtract(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ)).add(copyToPos))){
							//	checkedEntityPos.add(((EntityHanging)e).func_174857_n().subtract(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ)).add(copyToPos));
								//entitySet.add(new EntityPass(((EntityHanging)e).func_174857_n().subtract(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ)), e, e.getHorizontalFacing().getOpposite()));
							//}
						}
					}
				}
				
				entity.addChatComponentMessage(new ChatComponentText("Done Copying!"));
				
//				System.out.println("Done Copying");
				selectionCalculated = true;
				
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(entity).get();
//				entity.addChatComponentMessage(new ChatComponentText(player.toString()));
//				entity.addChatComponentMessage(new ChatComponentText(player.currentCopyClipboard.toString()));
				player.currentCopyClipboard.clear();
				for(ChangeBlockToThis change : selectionSet){
					player.currentCopyClipboard.add(new BlockInfoContainer(change));
				}
				System.out.println("Done Copying");
				//entity.addChatComponentMessage(new ChatComponentText(player.currentCopyClipboard.toString()));
				player.currentClipboardEntities = entitySet;
				player.clipboardMaxPos = new BlockPos(structureBoundingBox.maxX, structureBoundingBox.maxY, structureBoundingBox.maxZ).add(new BlockPos(-structureBoundingBox.minX, -structureBoundingBox.minY, -structureBoundingBox.minZ));
				//entity.addChatComponentMessage(new ChatComponentText(player.clipboardMaxPos.toString()));
				currentlyCalculating = false;
				
			}
			
			else{
				isFinished = true;				
			}
			
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
	
	public boolean isFinished(){
		return isFinished;
	}

	@Override
	public void shapeFinished() {
		// TODO Auto-generated method stub
		
	}

}
