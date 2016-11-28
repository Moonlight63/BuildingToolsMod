package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.GUIBlockSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendAdvancedFillPacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendAdvancedReplacePacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendSimpleFillPacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendSimpleReplacePacketToItemMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GUISetReplaceBlocks extends GUIBlockSelection{

	public GUISetReplaceBlocks(EntityPlayer player) {
		super(player);
		// TODO Auto-generated constructor stub
	}
	
	/**
     * Called when the mouse is clicked over a slot or outside the gui.
     * Click Type 1 = Shift Click
     * Click Type 2 = Hotbar key
     * Click Type 3 = Pick Block
     * Click Type 4 = Drop Key, Click outside of GUI
     * Click Type 5 =
     */
	@Override
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType){
		this.keyOrButtonClicked = true;
        clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
        
        //if(slotIn != null && slotIn.getStack() != null)
        	//System.out.println("Button = " + clickedButton + "     Type = " + clickType);
        	//System.out.println(slotIn.getStack().getDisplayName());
        
        
        if (mode == 0){
	        if(clickedButton == 0){
	        	if(clickType == 0){
	        		if(slotIn.getStack() == null)
	        			return;
	        		if(blockReplaceList.contains(slotIn.getStack())){
	        			blockReplaceList.remove(slotIn.getStack());
	        		}
	        		else{
	        			if(!blockFillList.contains(slotIn.getStack()))
	        				blockReplaceList.add(slotIn.getStack());
	        		}
	        		System.out.println(blockReplaceList);
	        	}
	        	else if(clickType == 1){
	        		
	        	}
	        	System.out.println(blockReplaceList);
	        }
	        else if(clickedButton == 1){
	        	if(clickType == 0){
	        		
	        	}
	        	else if(clickType == 1){
	        		
	        	}
	        }
        }
        else{
        	if(clickedButton == 0){
	        	if(clickType == 0){
	        
	        	}
	        	else if(clickType == 1){
	        		
	        	}
	        }
	        else if(clickedButton == 1){
	        	
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
        	
	        	List<Integer> ID = Lists.<Integer>newArrayList();
	        	List<Integer> META = Lists.<Integer>newArrayList();
	        	List<Integer> CHANCE = Lists.<Integer>newArrayList();
	        	
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
	        	
	        	System.out.println(ID2 + "   " + META2 + "   " + CHANCE);
	        	
	        	PacketDispatcher.sendToServer(new SendAdvancedReplacePacketToItemMessage(ID, META, CHANCE, ID2, META2));
	        	
        	
        }
    }
	
	@Override
	protected boolean showModeSwitchButton(){
		return false;
	}

}
