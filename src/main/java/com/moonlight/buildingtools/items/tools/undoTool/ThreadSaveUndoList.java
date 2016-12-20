package com.moonlight.buildingtools.items.tools.undoTool;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.BlockChangeBase;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
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
import net.minecraft.util.text.TextComponentString;

public class ThreadSaveUndoList implements BlockChangeBase{
	
	protected EntityPlayer player; 
	protected boolean isFinished = false;
	protected boolean currentlyCalculating = false;
	protected String savename;
	
	public ThreadSaveUndoList(Entity playerIn, String savename){
		this.player = (EntityPlayer) playerIn;	
		this.savename = "/" + savename + ".nbt";
	}
	
	@Override
	public void perform(){
		if(!currentlyCalculating){
			try{
				currentlyCalculating = true;
								
				PlayerWrapper playerwrap = BuildingTools.getPlayerRegistry().getPlayer(player).get();
				
				NBTTagCompound undolistnbt = new NBTTagCompound();
				undolistnbt.setInteger("Undo Count", playerwrap.undolist.size());
				int count = 0;
				for(List<BlockInfoContainer> list : playerwrap.undolist){
					NBTTagCompound blockinfo = new NBTTagCompound();
					for(BlockInfoContainer blocks : list){
						NBTTagCompound block = new NBTTagCompound();
						block.setInteger("X", blocks.change.getBlockPos().getX());
						block.setInteger("Y", blocks.change.getBlockPos().getY());
						block.setInteger("Z", blocks.change.getBlockPos().getZ());
						block.setInteger("BlockId", Block.getIdFromBlock(blocks.change.getBlockState().getBlock()));
						block.setInteger("BlockState", blocks.change.getBlockState().getBlock().getMetaFromState(blocks.change.getBlockState()));
						if(blocks.change.getNBTTag() != null)
							block.setTag("BlockCompound", blocks.change.getNBTTag());
						blockinfo.setTag("X: " + blocks.change.getBlockPos().getX() + " Y: " + blocks.change.getBlockPos().getY() + " Z: " + blocks.change.getBlockPos().getZ(), block);
					}
					count++;
					undolistnbt.setTag("Undo: " + count, blockinfo);
				}
			    
				File savedirectory = BuildingTools.oldUndoDir;
				new File(savedirectory, savename).createNewFile();
				CompressedStreamTools.safeWrite(undolistnbt, new File(savedirectory, savename));
				player.addChatMessage(new TextComponentString("Writing data"));
				player.addChatMessage(new TextComponentString("Done"));
			}
			catch(Exception e){
				System.out.println(e);
				e.printStackTrace();
			}
			
			isFinished = true;				
		}
	}
	
	
	public List<NBTData> NBTCompoundParser(NBTTagCompound tag){
		
		List<NBTData> tempList = new LinkedList<NBTData>();

		for(Object nbt : tag.getKeySet()){
			
			NBTData tempData = new NBTData();
			
			tempData.tagType = tag.getTag((String) nbt).getId();
			tempData.name = (String) nbt;
			tempData.data = new NBTDataType();
			byte id = tag.getTag((String) nbt).getId();
			
            if(id == 0)
            	;
            else if(id == 1)
            	tempData.data.byteData = tag.getByte((String) nbt);
            else if(id == 2)
            	tempData.data.shortData = tag.getShort((String) nbt);
            else if(id == 3)
            	tempData.data.intData = tag.getInteger((String) nbt);
            else if(id == 4)
            	tempData.data.longData = tag.getLong((String) nbt);
            else if(id == 5)
            	tempData.data.floatData = tag.getFloat((String) nbt);
            else if(id == 6)
            	tempData.data.doubleData = tag.getDouble((String) nbt);
            else if(id == 7)
            	tempData.data.byteArrayData = tag.getByteArray((String) nbt);
            else if(id == 8)
            	tempData.data.stringData = tag.getString((String) nbt);
            else if(id == 9)
            	tempData.data.listTagData = NBTTagListParser((NBTTagList)tag.getTag((String) nbt));
            else if(id == 10)
            	tempData.data.compoundTagData = NBTCompoundParser(tag.getCompoundTag((String) nbt));
            else if(id == 11)
            	tempData.data.intArrayData = tag.getIntArray((String) nbt);
            
            tempList.add(tempData);
					
    	}
		
		return tempList;
		
	}
	
	
	public List<NBTData> NBTTagListParser(NBTTagList tag){
		
		List<NBTData> tempList = new LinkedList<NBTData>();

		for(int i = 0; i < tag.tagCount(); i++){
			
			NBTData tempData = new NBTData();
			
			tempData.tagType = (byte) tag.getTagType();
			tempData.name = Integer.toString(i);
			
			int id = tag.getTagType();
			
        	if(id == 0)
            	;
        	else if(id == 1)
            	tempData.data.byteData = ((NBTTagByte)tag.get(i)).getByte();
        	else if(id == 2)
            	tempData.data.shortData = ((NBTTagShort)tag.get(i)).getShort();
        	else if(id == 3)
            	tempData.data.intData = ((NBTTagInt)tag.get(i)).getInt();
        	else if(id == 4)
            	tempData.data.longData = ((NBTTagLong)tag.get(i)).getLong();
        	else if(id == 5)
            	tempData.data.floatData = ((NBTTagFloat)tag.get(i)).getFloat();
        	else if(id == 6)
            	tempData.data.doubleData = ((NBTTagDouble)tag.get(i)).getDouble();
        	else if(id == 7)
            	tempData.data.byteArrayData = ((NBTTagByteArray)tag.get(i)).getByteArray();
        	else if(id == 8)
            	tempData.data.stringData = ((NBTTagString)tag.get(i)).getString();
        	else if(id == 9)
            	tempData.data.listTagData = NBTTagListParser(((NBTTagList)tag.get(i)));
        	else if(id == 10)
            	tempData.data.compoundTagData = NBTCompoundParser((tag.getCompoundTagAt(i)));
        	else if(id == 11)
            	tempData.data.intArrayData = ((NBTTagIntArray)tag.get(i)).getIntArray();
	        	
	        tempList.add(tempData);
					
    	}
		
		return tempList;
		
	}
	
	
	@Override
	public boolean isFinished(){
		return isFinished;
	}

}

class CopyData{
	public int[] maxPos;
	public List<BlockData> blocks;
	public List<List<NBTData>> entities;
}

class BlockData{
	
	public int[] pos;
	public int block;
	public int data;
	public List<NBTData> nbt;
	
}

class NBTData{
	
	public byte tagType;
	public String name;
	public NBTDataType data = new NBTDataType();
	
}

class NBTDataType{
	
	public byte byteData;
	public short shortData;
	public int intData;
	public long longData;
	public float floatData;
	public double doubleData;
	public byte[] byteArrayData;
	public String stringData;
	public List<NBTData> listTagData = new LinkedList<NBTData>();
	public List<NBTData> compoundTagData = new LinkedList<NBTData>();
	public int[] intArrayData;
}
