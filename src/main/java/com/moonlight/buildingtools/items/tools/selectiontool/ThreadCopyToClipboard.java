package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.LinkedHashSet;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.shapes.GeometryUtils;
import com.moonlight.buildingtools.helpers.shapes.IShapeable;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class ThreadCopyToClipboard implements BlockChangeBase, IShapeable{
	
	protected StructureBoundingBox structureBoundingBox;
	protected AxisAlignedBB entityDetectionBox;
	protected World world;
	protected EntityPlayer entity;
	
	protected boolean isFinished = false;
	protected Set<ChangeBlockToThis> selectionSet = new LinkedHashSet<ChangeBlockToThis>();
	
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
	
	@Override
	public void perform(){
		
		if(!currentlyCalculating){
			
			if(!selectionCalculated){
				
				entity.addChatComponentMessage(new TextComponentString("Copying Selection. Please Wait!"), true);
				
				GeometryUtils.makeFilledCube(new BlockPos(structureBoundingBox.minX, structureBoundingBox.minY, structureBoundingBox.minZ), structureBoundingBox.getXSize()-1, structureBoundingBox.getYSize()-1, structureBoundingBox.getZSize()-1, this);
				
				entity.addChatMessage(new TextComponentString("Done Copying!"));
				
				selectionCalculated = true;
				
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(entity).get();
//				entity.addChatComponentMessage(new ChatComponentText(player.toString()));
//				entity.addChatComponentMessage(new ChatComponentText(player.currentCopyClipboard.toString()));
				player.currentCopyClipboard.clear();
				for(ChangeBlockToThis change : selectionSet){
					player.currentCopyClipboard.add(new BlockInfoContainer(change));
				}
				System.out.println("Done Copying");
				player.clipboardMaxPos = new BlockPos(structureBoundingBox.maxX, structureBoundingBox.maxY, structureBoundingBox.maxZ).add(new BlockPos(-structureBoundingBox.minX, -structureBoundingBox.minY, -structureBoundingBox.minZ));
				currentlyCalculating = false;
				
			}
			
			else{
				isFinished = true;				
			}
			
		}
	}
	
	@Override
	public boolean isFinished(){
		return isFinished;
	}

	@Override
	public void shapeFinished() {
		
	}

}
