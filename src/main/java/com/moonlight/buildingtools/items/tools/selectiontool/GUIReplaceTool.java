package com.moonlight.buildingtools.items.tools.selectiontool;

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

public class GUIReplaceTool extends GUIBlockSelection{

	public GUIReplaceTool(EntityPlayer player) {
		super(player);
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
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType){
		this.keyOrButtonClicked = true;
		
		ItemStack stack = slotIn.getStack();
		
		if(clickType == ClickType.PICKUP){
			if(clickedButton == 0){
				if(!blockFillList.contains(stack)){
					if(!blockReplaceList.contains(stack)){
						blockFillList.add(stack);
					}
					else{
						blockReplaceList.remove(stack);
					}
				}
				else{
					blockFillList.remove(stack);
	    			slotIn.getStack().func_190920_e(stack.func_190916_E()+1);
	    			blockFillList.add(stack);
				}
			}
			
			else if(clickedButton == 1){
				if(!blockReplaceList.contains(stack)){
					if(!blockFillList.contains(stack)){
	        			blockReplaceList.add(stack);
	        		}
	        		else{
	        			blockFillList.remove(stack);
	        			if(stack.func_190916_E()>1){
	        				stack.func_190920_e(stack.func_190916_E()-1);
	        				blockFillList.add(stack);
	        			}
	        			else if (stack.func_190916_E() == 1){
	        				blockFillList.remove(stack);
	        			}
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
    	System.out.println("Sending Command");
    	updateSelection();
    	//PacketDispatcher.sendToServer(new SendCustomMessageCommand("Fill/Replace"));
        	
    }
	
	public void updateSelection(){
		List<Integer> ID = Lists.<Integer>newArrayList();
    	List<Integer> META = Lists.<Integer>newArrayList();
    	List<Integer> CHANCE = Lists.<Integer>newArrayList();
    	
    	for(int i = 0; i < blockFillList.size(); i++){
    		System.out.println("SIZE = " + blockFillList.size());
    		if(ItemStack.areItemsEqual(blockFillList.get(i), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
    			ID.add(i, Block.getIdFromBlock(Blocks.AIR));
    		}
    		else if(ItemStack.areItemsEqual(blockFillList.get(i), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
    			ID.add(i, Block.getIdFromBlock(Blocks.FLOWING_WATER));
    		}
    		else if(ItemStack.areItemsEqual(blockFillList.get(i), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
    			ID.add(i, Block.getIdFromBlock(Blocks.FLOWING_LAVA));
    		}
    		else{
    			ID.add(i, Block.getIdFromBlock(Block.getBlockFromItem(blockFillList.get(i).getItem())));
    		}
    		META.add(i, blockFillList.get(i).getMetadata());
    		CHANCE.add(i, blockFillList.get(i).func_190916_E());
    	}
    	
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
    		
    		System.out.println("SIZE = " + blockFillList.size());
    		
    	}
    	
    	System.out.println(ID + "   " + META + "   " + CHANCE);
    	
    	NBTTagCompound commandPacket = new NBTTagCompound();
    	
    	commandPacket.setTag("Commands", new NBTTagCompound());
    	commandPacket.getCompoundTag("Commands").setString("1", "SetFill");
    	commandPacket.getCompoundTag("Commands").setString("2", "SetReplace");
    	commandPacket.getCompoundTag("Commands").setString("3", "RunFillReplace");
    	
    	
    	commandPacket.setTag("fillblocks", new NBTTagCompound());
		for (int i = 0; i < ID.size(); i++) {
			ItemStack fill = new ItemStack(Block.getBlockById(ID.get(i)));
			fill.setItemDamage(META.get(i));
			commandPacket.getCompoundTag("fillblocks").setTag(Integer.toString(i), new NBTTagCompound());
			commandPacket.getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setInteger("chance", CHANCE.get(i));
			commandPacket.getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setTag("blockstate", fill.writeToNBT(new NBTTagCompound()));
		}
		
		commandPacket.setTag("replaceblocks", new NBTTagCompound());
		for (int i = 0; i < ID2.size(); i++) {			
			ItemStack replace = new ItemStack(Block.getBlockById(ID2.get(i)));
			replace.setItemDamage(META2.get(i));
			commandPacket.getCompoundTag("replaceblocks").setTag(Integer.toString(i), replace.writeToNBT(new NBTTagCompound()));
		}
		
		PacketDispatcher.sendToServer(new SendNBTCommandPacket(commandPacket));
    	
	}
	
	@Override
	protected boolean showModeSwitchButton(){
		return false;
	}
	

}
