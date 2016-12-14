package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.GUIBlockSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendAdvancedFillPacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendSimpleFillPacketToItemMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GUISetPaintBlock extends GUIBlockSelection{

	public GUISetPaintBlock(EntityPlayer player) {
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
    //protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType){
        this.keyOrButtonClicked = true;
        //clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
        
        if (mode == 0){
	        if(clickedButton == 0){
	        	if(clickType == ClickType.PICKUP){
	        		if(slotIn.getStack() == null)
	        			return;
	        		
	        		blockFillList.add(slotIn.getStack());
	        		
	        		int currID;
	        		int currDATA;
//	        		if(slotIn.getStack().getItem() instanceof ItemBucket){
//	        			currID = ((ItemBucket)slotIn.getStack().getItem()) == Items.lava_bucket ? Block.getIdFromBlock(Blocks.flowing_lava) : Block.getIdFromBlock(Blocks.flowing_water);
//	        			currDATA = 0;
//	        		}
	        		if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
	        			currID = Block.getIdFromBlock(Blocks.AIR);
	        			currDATA = 0;
	        		}
	        		else if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
	        			currID = Block.getIdFromBlock(Blocks.FLOWING_WATER);
	        			currDATA = 0;
	        		}
	        		else if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
	        			currID = Block.getIdFromBlock(Blocks.FLOWING_LAVA);
	        			currDATA = 0;
	        		}
	        		else{
		        		currID = Block.getIdFromBlock(Block.getBlockFromItem(slotIn.getStack().getItem()));
		        		currDATA = slotIn.getStack().getItem().getMetadata(slotIn.getStack());
	        		}
	        		
	        		//PacketDispatcher.sendToServer(new SendSimpleFillPacketToItemMessage(currID, currDATA));
	        		this.mc.thePlayer.closeScreen();
	        	}
//	        	else if(clickType == 1){
//	        		//((ContainerBlockSelMenu.CustomSlot) slotIn).setColor(RGBA.Red.setAlpha(100));;
//	        		//slotIn.setColor(RGBA.Red.setAlpha(100));
//	        	}
	        }
//	        else if(clickedButton == 1){
//	        	if(clickType == 0){
//	        		
//	        	}
//	        	else if(clickType == 1){
//	        		
//	        	}
//	        }
        }
        else{
        	if(clickedButton == 0){
	        	if(clickType == ClickType.PICKUP){
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
	        		System.out.println(blockFillList);
	        	}
//	        	else if(clickType == 1){
//	        		
//	        		//slotIn.setColor(RGBA.Red.setAlpha(100));
//	        	}
	        }
	        else if(clickedButton == 1){
	        	if(clickType == ClickType.PICKUP){
	        		if(slotIn.getStack() == null)
	        			return;
	        		if(!blockFillList.contains(slotIn.getStack())){
	        			//((ContainerBlockSelMenu.CustomSlot) slotIn).setColor(RGBA.White.setAlpha(0));
	        			//blockFillList.add(slotIn.getStack());
	        		}
	        		else{
	        			blockFillList.remove(slotIn.getStack());
	        			if(slotIn.getStack().func_190916_E()>1){
	        				slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()-1);
	        				blockFillList.add(slotIn.getStack());
	        			}
	        			else if (slotIn.getStack().func_190916_E() == 1){
	        				//((ContainerBlockSelMenu.CustomSlot) slotIn).clearColor();
	        			}
	        			
	        		}
	        		System.out.println(blockFillList);
	        	}
//	        	else if(clickType == 1){
//	        		
//	        	}
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
        
        //if(mode == 1){
        	
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
	        	
	        	PacketDispatcher.sendToServer(new SendAdvancedFillPacketToItemMessage(ID, META, CHANCE));
	        	
        	}
        //}
    }

}
