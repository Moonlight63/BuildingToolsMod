package com.moonlight.buildingtools.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class MiscUtils {
	
	public static ChangeBlockToThis addBlockWithNBT(BlockPos oldPosOrNull, IBlockState blockState, BlockPos newPos, World world){
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
	
	public static Set<BlockInfoContainer> CalcUndoList(Set<ChangeBlockToThis> tempList, World world){
		Set<BlockInfoContainer> newTempList = new LinkedHashSet<BlockInfoContainer>();
		
		for(ChangeBlockToThis pos : tempList){
			newTempList.add(new BlockInfoContainer(addBlockWithNBT(pos.getBlockPos(), world.getBlockState(pos.getBlockPos()), pos.getBlockPos(), world)));
		}
		
		return newTempList;
	}
	
	public static void dumpUndoList(EntityPlayer entity){		
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(entity).get();
		player.UndoIsSaved = false;
		if(player.undolist.add(Lists.newCopyOnWriteArrayList(player.tempUndoList))){
			player.tempUndoList.clear();
			player.UndoIsSaved = true;
			entity.addChatComponentMessage(new ChatComponentText("Done Saving Undo"));
		}
	}

}
