package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class ThreadLoadClipboard implements BlockChangeBase{
	
	protected EntityPlayer player; 
	protected boolean isFinished = false;
	protected boolean currentlyCalculating = false;
	protected String fileName;
	
	public ThreadLoadClipboard(Entity playerIn, String name){
		this.player = (EntityPlayer) playerIn;	
		this.fileName = name;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void perform(){
		if(!currentlyCalculating){
			try{
				currentlyCalculating = true;
				PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
				String DATAIN = "";
				File savedirectory = BuildingTools.clipboardSaveDir;
				System.out.println(fileName);
				BufferedReader is = new BufferedReader(new FileReader(new File(savedirectory, "/" + fileName + ".json")));
				player.addChatMessage(new TextComponentString("Reading data"));

				String line = null;
				while((line = is.readLine()) != null) {
					DATAIN = DATAIN + line;
	            }   
				
				is.close();
				
				NBTTagCompound LOADDATANBT = JsonToNBT.getTagFromJson(DATAIN);
				
				playerwrap.clipboardMaxPos = new BlockPos(LOADDATANBT.getIntArray("MaxPos")[0], LOADDATANBT.getIntArray("MaxPos")[1], LOADDATANBT.getIntArray("MaxPos")[2]);
				NBTTagCompound blocksnbt = LOADDATANBT.getCompoundTag("Blocks");
				List<ChangeBlockToThis> newChangeList = new LinkedList<ChangeBlockToThis>();
				for (String key : blocksnbt.getKeySet()) {
					NBTTagCompound blocknbt = blocksnbt.getCompoundTag(key);
					
					if(blocknbt.getCompoundTag("nbt").hasNoTags()){
						newChangeList.add(
								new ChangeBlockToThis(
										new BlockPos(
											blocknbt.getIntArray("Position")[0], 
											blocknbt.getIntArray("Position")[1], 
											blocknbt.getIntArray("Position")[2]), 
										Block.getStateById(blocknbt.getInteger("id")).getBlock().getStateFromMeta(blocknbt.getInteger("meta"))));
					}
					else{
						newChangeList.add(
								new ChangeBlockToThis(
										new BlockPos(
											blocknbt.getIntArray("Position")[0], 
											blocknbt.getIntArray("Position")[1], 
											blocknbt.getIntArray("Position")[2]), 
										Block.getStateById(blocknbt.getInteger("id")).getBlock().getStateFromMeta(blocknbt.getInteger("meta")),
										blocknbt.getCompoundTag("nbt")));
					}
					
				}
				
				
				playerwrap.currentCopyClipboard.clear();
				for (ChangeBlockToThis changeBlockToThis : newChangeList) {
					playerwrap.currentCopyClipboard.add(new BlockInfoContainer(changeBlockToThis));
				}				
				
			}
			catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
			
			isFinished = true;				
		}
	}
	
	@Override
	public boolean isFinished(){
		return isFinished;
	}

}
