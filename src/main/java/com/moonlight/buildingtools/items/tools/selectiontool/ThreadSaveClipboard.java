package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.Reference;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;

public class ThreadSaveClipboard implements BlockChangeBase{
	
	protected EntityPlayer player; 
	protected boolean isFinished = false;
	protected boolean currentlyCalculating = false;
	protected String savename;
	protected String versionName;
	
	public ThreadSaveClipboard(Entity playerIn, String savename){
		this.player = (EntityPlayer) playerIn;	
		this.savename = "/" + savename + ".json";
		this.versionName = "/" + savename + ".version"; 
	}
	
	@Override
	public void perform(){
		if(!currentlyCalculating){
			try{
				currentlyCalculating = true;
				PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
				
				NBTTagCompound COPYDATANBT = new NBTTagCompound();
				COPYDATANBT.setString("SaveVersion", Reference.VERSION);
				COPYDATANBT.setTag("Blocks", new NBTTagCompound());
			    COPYDATANBT.setIntArray("MaxPos", new int[]{playerwrap.clipboardMaxPos.getX(), playerwrap.clipboardMaxPos.getY(), playerwrap.clipboardMaxPos.getZ()});

				int count = 0;
			    for(BlockInfoContainer change : playerwrap.currentCopyClipboard){
			    	
			    	count++;
			    	NBTTagCompound changedata = new NBTTagCompound();
			    	changedata.setIntArray("Position", new int[]{change.change.getBlockPos().getX(), change.change.getBlockPos().getY(), change.change.getBlockPos().getZ()});
			    	changedata.setInteger("id", Block.getIdFromBlock(change.change.getBlockState().getBlock()));
			    	changedata.setInteger("meta", change.change.getBlockState().getBlock().getMetaFromState(change.change.getBlockState()));
			    	if(change.change.getNBTTag() != null){
			    		changedata.setTag("nbt", change.change.getNBTTag());
			    	}
			    	COPYDATANBT.getCompoundTag("Blocks").setTag(Integer.toString(count), changedata);
			    	
			    }
			    
				File savedirectory = BuildingTools.clipboardSaveDir;
				new File(savedirectory, savename).createNewFile();
				new File(savedirectory, versionName).createNewFile();
				BufferedWriter os = new BufferedWriter(new FileWriter(new File(savedirectory, savename)));
				BufferedWriter os2 = new BufferedWriter(new FileWriter(new File(savedirectory, versionName)));
				player.addChatMessage(new TextComponentString("Writing data"));
				os.write(COPYDATANBT.toString());
				os2.write(COPYDATANBT.getString("SaveVersion"));
				os.close();
				os2.close();
				player.addChatMessage(new TextComponentString("Done"));
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

