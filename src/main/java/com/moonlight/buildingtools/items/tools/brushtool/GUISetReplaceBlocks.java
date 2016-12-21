package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.GUIBlockSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GUISetReplaceBlocks extends GUIBlockSelection{

	public GUISetReplaceBlocks(EntityPlayer player) {
		super(player);
	}
	
	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType){
		
		ItemStack stack;
		if(slotIn != null)
			stack = slotIn.getStack();
		else
			return;
		
		this.keyOrButtonClicked = true;
		if(clickType == ClickType.PICKUP){
	        if (mode == 0){
		        if(clickedButton == 0){
	        		if(stack == null)
	        			return;
	        		if(blockReplaceList.contains(stack)){
	        			blockReplaceList.remove(stack);
	        		}
	        		else{
	        			if(!blockFillList.contains(stack))
	        				blockReplaceList.add(stack);
	        		}
	        	}
	        }
        }
    }
	
	
	/**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
	@Override
    public void onGuiClosed(){
		super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        	
    	if(!blockReplaceList.isEmpty()){
        	
        	List<Integer> ID2 = Lists.<Integer>newArrayList();
        	List<Integer> META2 = Lists.<Integer>newArrayList();
        	
        	for(int i = 0; i < blockReplaceList.size(); i++){
        		
        		if(ItemStack.areItemsEqual(blockReplaceList.get(i), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
        			ID2.add(Block.getIdFromBlock(Blocks.AIR));
        			META2.add(i, blockReplaceList.get(i).getMetadata());
        		}
        		else if(ItemStack.areItemsEqual(blockReplaceList.get(i), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
        			ID2.add(Block.getIdFromBlock(Blocks.FLOWING_WATER));
        			ID2.add(Block.getIdFromBlock(Blocks.WATER));
        			META2.add(i, blockReplaceList.get(i).getMetadata());
        			META2.add(i, blockReplaceList.get(i).getMetadata());
        		}
        		else if(ItemStack.areItemsEqual(blockReplaceList.get(i), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
        			ID2.add(Block.getIdFromBlock(Blocks.FLOWING_LAVA));
        			ID2.add(Block.getIdFromBlock(Blocks.LAVA));
        			META2.add(i, blockReplaceList.get(i).getMetadata());
        			META2.add(i, blockReplaceList.get(i).getMetadata());
        		}
        		else{
        			ID2.add(i, Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(i).getItem())));
        			META2.add(i, blockReplaceList.get(i).getMetadata());
        		}
        		
        		System.out.println("SIZE = " + blockReplaceList.size());
        		
        	}
        	
        	System.out.println(ID2 + "   " + META2);
        	
        	NBTTagCompound commandPacket = new NBTTagCompound();
        	
        	commandPacket.setTag("Commands", new NBTTagCompound());
        	commandPacket.getCompoundTag("Commands").setString("1", "SetReplace");
        	
    		commandPacket.setTag("replaceblocks", new NBTTagCompound());
    		for (int i = 0; i < ID2.size(); i++) {			
    			ItemStack replace = new ItemStack(Block.getBlockById(ID2.get(i)));
    			replace.setItemDamage(META2.get(i));
    			commandPacket.getCompoundTag("replaceblocks").setTag(Integer.toString(i), replace.writeToNBT(new NBTTagCompound()));
    		}
    		
    		PacketDispatcher.sendToServer(new SendNBTCommandPacket(commandPacket));
        	
        }
    }
	
	@Override
	protected boolean showModeSwitchButton(){
		return false;
	}

}
