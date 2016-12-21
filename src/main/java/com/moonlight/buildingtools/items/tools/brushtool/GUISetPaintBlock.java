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

public class GUISetPaintBlock extends GUIBlockSelection{

	public GUISetPaintBlock(EntityPlayer player) {
		super(player);
	}
	
	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType){
        this.keyOrButtonClicked = true;
        if(clickType == ClickType.PICKUP){
	        if (mode == 0){
		        if(clickedButton == 0){
	        		if(slotIn.getStack() == null)
	        			return;
	        		
	        		blockFillList.add(slotIn.getStack());
	        		this.mc.thePlayer.closeScreen();
		        }
	        }
	        else{
	        	if(clickedButton == 0){
	        		if(slotIn.getStack() == null)
	        			return;
	        		if(!blockFillList.contains(slotIn.getStack())){
	        			blockFillList.add(slotIn.getStack());
	        		}
	        		else{
	        			blockFillList.remove(slotIn.getStack());
	        			slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()+1);
	        			blockFillList.add(slotIn.getStack());
	        		}
		        }
		        else if(clickedButton == 1){
	        		if(slotIn.getStack() == null)
	        			return;
	        		if(blockFillList.contains(slotIn.getStack())){
	        			blockFillList.remove(slotIn.getStack());
	        			if(slotIn.getStack().func_190916_E()>1){
	        				slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()-1);
	        				blockFillList.add(slotIn.getStack());
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
        	
    	if(!blockFillList.isEmpty()){
    	
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
        	
        	System.out.println(ID + "   " + META + "   " + CHANCE);
        	
        	NBTTagCompound commandPacket = new NBTTagCompound();
        	
        	commandPacket.setTag("Commands", new NBTTagCompound());
        	commandPacket.getCompoundTag("Commands").setString("1", "SetFill");
        	
        	commandPacket.setTag("fillblocks", new NBTTagCompound());
    		for (int i = 0; i < ID.size(); i++) {
    			ItemStack fill = new ItemStack(Block.getBlockById(ID.get(i)));
    			fill.setItemDamage(META.get(i));
    			commandPacket.getCompoundTag("fillblocks").setTag(Integer.toString(i), new NBTTagCompound());
    			commandPacket.getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setInteger("chance", CHANCE.get(i));
    			commandPacket.getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setTag("blockstate", fill.writeToNBT(new NBTTagCompound()));
    		}
    		
    		PacketDispatcher.sendToServer(new SendNBTCommandPacket(commandPacket));
        	
    	}
    }

}
