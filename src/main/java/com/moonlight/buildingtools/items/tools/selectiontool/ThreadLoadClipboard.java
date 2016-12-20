package com.moonlight.buildingtools.items.tools.selectiontool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.items.tools.ChangeBlockToThis;
import com.moonlight.buildingtools.items.tools.undoTool.BlockInfoContainer;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
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
	
	//protected int count = 0;
	@Override
	@SuppressWarnings("deprecation")
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
				System.out.println(fileName);
				BufferedReader is = new BufferedReader(new FileReader(new File(savedirectory, "/" + fileName + ".json")));
				player.addChatMessage(new TextComponentString("Reading data"));

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
				for (ChangeBlockToThis changeBlockToThis : newChangeList) {
					playerwrap.currentCopyClipboard.add(new BlockInfoContainer(changeBlockToThis));
				}
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
            	tempCompound.setByte(nbt.name, nbt.data.byteData);
            else if(id == 2)
            	tempCompound.setShort(nbt.name, nbt.data.shortData);
            else if(id == 3)
            	tempCompound.setInteger(nbt.name, nbt.data.intData);
            else if(id == 4)
            	tempCompound.setLong(nbt.name, nbt.data.longData);
            else if(id == 5)
            	tempCompound.setFloat(nbt.name, nbt.data.floatData);
            else if(id == 6)
            	tempCompound.setDouble(nbt.name, nbt.data.doubleData);
            else if(id == 7)
            	tempCompound.setByteArray(nbt.name, nbt.data.byteArrayData);
            else if(id == 8)
            	tempCompound.setString(nbt.name, nbt.data.stringData);
            else if(id == 9)
            	tempCompound.setTag(nbt.name, NBTTagListPhraser((ArrayList<NBTData>) nbt.data.listTagData));
            else if(id == 10)
            	tempCompound.setTag(nbt.name, NBTCompoundPhraser(nbt.data.compoundTagData));
            else if(id == 11)
            	tempCompound.setIntArray(nbt.name, nbt.data.intArrayData);
					
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
        		temp.appendTag(new NBTTagShort(nbt.data.shortData));
        	else if(id == 3)
        		temp.appendTag(new NBTTagInt(nbt.data.intData));
        	else if(id == 4)
        		temp.appendTag(new NBTTagLong(nbt.data.longData));
        	else if(id == 5)
        		temp.appendTag(new NBTTagFloat(nbt.data.floatData));
        	else if(id == 6)
        		temp.appendTag(new NBTTagDouble(nbt.data.doubleData));
        	else if(id == 7)
        		temp.appendTag(new NBTTagByteArray(nbt.data.byteArrayData));
        	else if(id == 8)
        		temp.appendTag(new NBTTagString(nbt.data.stringData));
        	else if(id == 9)
        		temp.appendTag(NBTTagListPhraser((ArrayList<NBTData>) nbt.data.listTagData));
        	else if(id == 10)
        		temp.appendTag(NBTCompoundPhraser(nbt.data.compoundTagData));
        	else if(id == 11)
        		temp.appendTag(new NBTTagIntArray(nbt.data.intArrayData));
					
    	}
		
		return temp;
		
	}
	
	
	@Override
	public boolean isFinished(){
		return isFinished;
	}

}
