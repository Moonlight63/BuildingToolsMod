package com.moonlight.buildingtools.items.tools.selectiontool;

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

import com.google.common.collect.Maps;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.network.playerWrapper.PlayerRegistry;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

public class ThreadLoadClipboard implements BlockChangeBase{
	
	protected EntityPlayer player; 
	protected boolean isFinished = false;
	protected boolean currentlyCalculating = false;
	protected String fileName;
	
	public ThreadLoadClipboard(Entity playerIn, String name){
		this.player = (EntityPlayer) playerIn;	
		this.fileName = name;
	}
	
	//protected int count = 0;
	public void perform(){
		if(!currentlyCalculating){
			try{
				currentlyCalculating = true;
				
				Gson gson = new GsonBuilder()
	            .disableHtmlEscaping()
	            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
	            .setPrettyPrinting()
	            .serializeNulls()
	            .create();
				
				String DATAIN = "";
				File savedirectory = BuildingTools.clipboardSaveDir;
				//new File(savedirectory, "/" + fileName.replace(".json", "") + ".json").createNewFile();
				BufferedReader is = new BufferedReader(new FileReader(new File(savedirectory, "/" + fileName.replace(".json", "") + ".json")));
				player.addChatMessage(new ChatComponentText("Reading data"));

				String line = null;
				while((line = is.readLine()) != null) {
					DATAIN = DATAIN + line;
	                //player.addChatMessage(new ChatComponentText(line));
	            }   
				
				is.close();
				
				CopyData JSONDATA = gson.fromJson(DATAIN, CopyData.class);
				
				System.out.println(JSONDATA.getClass());
				System.out.println(JSONDATA.blocks.get(0));
				System.out.println(JSONDATA.blocks.get(0).getClass());
				//System.out.println(gson.fromJson(DATAIN, Object.class));
				
				List<ChangeBlockToThis> newChangeList = new LinkedList<ChangeBlockToThis>();
				
				for(BlockData data : JSONDATA.blocks){
					if(data.nbt != null){
						if(data.nbt.isEmpty())
							newChangeList.add(new ChangeBlockToThis(new BlockPos(data.pos[0], data.pos[1], data.pos[2]), Block.getStateById(data.block).getBlock().getStateFromMeta(data.data)));
						else
							newChangeList.add(new ChangeBlockToThis(new BlockPos(data.pos[0], data.pos[1], data.pos[2]), Block.getStateById(data.block).getBlock().getStateFromMeta(data.data), NBTCompoundPhraser(data.nbt)));
					}
					else
						newChangeList.add(new ChangeBlockToThis(new BlockPos(data.pos[0], data.pos[1], data.pos[2]), Block.getStateById(data.block).getBlock().getStateFromMeta(data.data)));
				} 
				
				PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
				
				playerwrap.currentCopyClipboard.clear();
				playerwrap.currentCopyClipboard.addAll(newChangeList);
				playerwrap.clipboardMaxPos = new BlockPos(JSONDATA.maxPos[0], JSONDATA.maxPos[1], JSONDATA.maxPos[2]);
				
				
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
