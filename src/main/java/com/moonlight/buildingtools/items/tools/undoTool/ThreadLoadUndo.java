package com.moonlight.buildingtools.items.tools.undoTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scala.Char;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class ThreadLoadUndo implements BlockChangeBase{
	
	protected EntityPlayer player; 
	protected boolean isFinished = false;
	protected boolean currentlyCalculating = false;
	protected String fileName;
	
	public ThreadLoadUndo(Entity playerIn, String name){
		this.player = (EntityPlayer) playerIn;	
		this.fileName = name;
	}
	
	//protected int count = 0;
	public void perform(){
		if(!currentlyCalculating){
			try{
				currentlyCalculating = true;
								
				File savedirectory = BuildingTools.oldUndoDir;
				//new File(savedirectory, "/" + fileName.replace(".json", "") + ".json").createNewFile();
				BufferedReader is = new BufferedReader(new FileReader(new File(savedirectory, "/" + fileName.replace(".nbt", "") + ".nbt")));
				player.addChatMessage(new ChatComponentText("Reading data"));
				
				PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
				playerwrap.UndoIsSaved = false;
				NBTTagCompound undolistnbt = CompressedStreamTools.read(new File(savedirectory, "/" + fileName.replace(".nbt", "") + ".nbt"));
				
				
				//Undo List is loaded in opposite order
				
				for(int i = 1; i <= undolistnbt.getInteger("Undo Count"); i++){
					System.out.println(i);
					NBTTagCompound change = (NBTTagCompound) undolistnbt.getTag("Undo: " + i);
					List<BlockInfoContainer> tempList = Lists.newArrayList();
					for(String blockChangeInfo : change.getKeySet()){
						NBTTagCompound changeinfo = (NBTTagCompound) change.getTag(blockChangeInfo);
						if(changeinfo.getTag("BlockCompound") != null){
							ChangeBlockToThis newChange = new ChangeBlockToThis(
									new BlockPos(
											changeinfo.getInteger("X"),
											changeinfo.getInteger("Y"),
											changeinfo.getInteger("Z"))
									,
									Block.getBlockById(
											changeinfo.getInteger("BlockId")).
											getStateFromMeta(changeinfo.getInteger("BlockState"))
									,
									(NBTTagCompound) changeinfo.getTag("BlockCompound")
											);
							tempList.add(new BlockInfoContainer(newChange));
						}
						else{
							ChangeBlockToThis newChange = new ChangeBlockToThis(
									new BlockPos(
											changeinfo.getInteger("X"),
											changeinfo.getInteger("Y"),
											changeinfo.getInteger("Z"))
									,
									Block.getBlockById(
											changeinfo.getInteger("BlockId")).
											getStateFromMeta(changeinfo.getInteger("BlockState")));
							tempList.add(new BlockInfoContainer(newChange));
						}
					}
					playerwrap.undolist.add(tempList);
					playerwrap.UndoIsSaved = true;
				}
				
				is.close();
				
				player.addChatMessage(new ChatComponentText("That undo list has now been added to the end of your current session. Your next undo operation will start from the end of your selected list."));
				
			}
			catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
			
			isFinished = true;				
		}
	}
	
	
	public NBTTagCompound NBTCompoundPhraser(List<NBTData> tag){
		
		NBTTagCompound tempCompound = new NBTTagCompound();
		

		for(NBTData nbt : tag){
			
			byte id = nbt.tagType;
			
            if(id == 0)
            	;
            else if(id == 1)
            	tempCompound.setByte(nbt.name, (byte) nbt.data.byteData);
            else if(id == 2)
            	tempCompound.setShort(nbt.name, (short) nbt.data.shortData);
            else if(id == 3)
            	tempCompound.setInteger(nbt.name, nbt.data.intData);
            else if(id == 4)
            	tempCompound.setLong(nbt.name, (long) nbt.data.longData);
            else if(id == 5)
            	tempCompound.setFloat(nbt.name, (float) nbt.data.floatData);
            else if(id == 6)
            	tempCompound.setDouble(nbt.name, (double) nbt.data.doubleData);
            else if(id == 7)
            	tempCompound.setByteArray(nbt.name, (byte[]) nbt.data.byteArrayData);
            else if(id == 8)
            	tempCompound.setString(nbt.name, (String) nbt.data.stringData);
            else if(id == 9)
            	tempCompound.setTag(nbt.name, NBTTagListPhraser((ArrayList<NBTData>) nbt.data.listTagData));
            else if(id == 10)
            	tempCompound.setTag(nbt.name, NBTCompoundPhraser((List<NBTData>) nbt.data.compoundTagData));
            else if(id == 11)
            	tempCompound.setIntArray(nbt.name, (int[]) nbt.data.intArrayData);
					
    	}
		
		return tempCompound;
		
	}
	
	
	public NBTTagList NBTTagListPhraser(ArrayList<NBTData> tag){
		
		NBTTagList temp = new NBTTagList();
		
		System.out.println(tag.getClass());

		for(NBTData nbt : tag){
			
			//System.out.println(tag.getClass());
			//System.out.println(tag.get(i));
			int id = nbt.tagType;
			
        	if(id == 0)
        		;
        	else if(id == 1)
        		temp.appendTag(new NBTTagByte(nbt.data.byteData));
        	else if(id == 2)
        		temp.appendTag(new NBTTagShort((short) nbt.data.shortData));
        	else if(id == 3)
        		temp.appendTag(new NBTTagInt((int) nbt.data.intData));
        	else if(id == 4)
        		temp.appendTag(new NBTTagLong((long) nbt.data.longData));
        	else if(id == 5)
        		temp.appendTag(new NBTTagFloat((float) nbt.data.floatData));
        	else if(id == 6)
        		temp.appendTag(new NBTTagDouble((double) nbt.data.doubleData));
        	else if(id == 7)
        		temp.appendTag(new NBTTagByteArray((byte[]) nbt.data.byteArrayData));
        	else if(id == 8)
        		temp.appendTag(new NBTTagString((String) nbt.data.stringData));
        	else if(id == 9)
        		temp.appendTag(NBTTagListPhraser((ArrayList<NBTData>) nbt.data.listTagData));
        	else if(id == 10)
        		temp.appendTag(NBTCompoundPhraser((List<NBTData>) nbt.data.compoundTagData));
        	else if(id == 11)
        		temp.appendTag(new NBTTagIntArray((int[]) nbt.data.intArrayData));
					
    	}
		
		return temp;
		
	}
	
	
	public boolean isFinished(){
		return isFinished;
	}

}
