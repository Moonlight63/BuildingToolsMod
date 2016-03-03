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
    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType){
        this.keyOrButtonClicked = true;
        clickType = slotId == -999 && clickType == 0 ? 4 : clickType;
        
        if (mode == 0){
	        if(clickedButton == 0){
	        	if(clickType == 0){
	        		if(slotIn.getStack() == null)
	        			return;
	        		int currID;
	        		int currDATA;
//	        		if(slotIn.getStack().getItem() instanceof ItemBucket){
//	        			currID = ((ItemBucket)slotIn.getStack().getItem()) == Items.lava_bucket ? Block.getIdFromBlock(Blocks.flowing_lava) : Block.getIdFromBlock(Blocks.flowing_water);
//	        			currDATA = 0;
//	        		}
	        		if(slotIn.getStack().getIsItemStackEqual(new ItemStack(Items.bucket).setStackDisplayName("Air"))){
	        			currID = Block.getIdFromBlock(Blocks.air);
	        			currDATA = 0;
	        		}
	        		else if(slotIn.getStack().getIsItemStackEqual(new ItemStack(Items.water_bucket).setStackDisplayName("Water"))){
	        			currID = Block.getIdFromBlock(Blocks.flowing_water);
	        			currDATA = 0;
	        		}
	        		else if(slotIn.getStack().getIsItemStackEqual(new ItemStack(Items.lava_bucket).setStackDisplayName("Lava"))){
	        			currID = Block.getIdFromBlock(Blocks.flowing_lava);
	        			currDATA = 0;
	        		}
	        		else{
		        		currID = Block.getIdFromBlock(Block.getBlockFromItem(slotIn.getStack().getItem()));
		        		currDATA = slotIn.getStack().getItem().getMetadata(slotIn.getStack());
	        		}
	        		PacketDispatcher.sendToServer(new SendSimpleFillPacketToItemMessage(currID, currDATA));
	        		this.mc.thePlayer.closeScreen();
	        	}
	        	else if(clickType == 1){
	        		//((ContainerBlockSelMenu.CustomSlot) slotIn).setColor(RGBA.Red.setAlpha(100));;
	        		//slotIn.setColor(RGBA.Red.setAlpha(100));
	        	}
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
	        		if(slotIn.getStack() == null)
	        			return;
	        		if(!blockFillList.contains(slotIn.getStack())){
	        			blockFillList.add(slotIn.getStack());
	        		}
	        		else{
	        			blockFillList.remove(slotIn.getStack());
	        			slotIn.getStack().stackSize++;
	        			blockFillList.add(slotIn.getStack());
	        		}
	        		System.out.println(blockFillList);
	        	}
	        	else if(clickType == 1){
	        		
	        		//slotIn.setColor(RGBA.Red.setAlpha(100));
	        	}
	        }
	        else if(clickedButton == 1){
	        	if(clickType == 0){
	        		if(slotIn.getStack() == null)
	        			return;
	        		if(!blockFillList.contains(slotIn.getStack())){
	        			//((ContainerBlockSelMenu.CustomSlot) slotIn).setColor(RGBA.White.setAlpha(0));
	        			//blockFillList.add(slotIn.getStack());
	        		}
	        		else{
	        			blockFillList.remove(slotIn.getStack());
	        			if(slotIn.getStack().stackSize>1){
	        				slotIn.getStack().stackSize--;
	        				blockFillList.add(slotIn.getStack());
	        			}
	        			else if (slotIn.getStack().stackSize == 1){
	        				//((ContainerBlockSelMenu.CustomSlot) slotIn).clearColor();
	        			}
	        			
	        		}
	        		System.out.println(blockFillList);
	        	}
	        	else if(clickType == 1){
	        		
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
        
        if(mode == 1){
        	
        	if(!blockFillList.isEmpty()){
        	
	        	List<Integer> ID = Lists.<Integer>newArrayList();
	        	List<Integer> META = Lists.<Integer>newArrayList();
	        	List<Integer> CHANCE = Lists.<Integer>newArrayList();
	        	
	        	for(int i = 0; i < blockFillList.size(); i++){
	        		
	        		System.out.println("SIZE = " + blockFillList.size());
	        		
	        		if(blockFillList.get(i).getIsItemStackEqual(new ItemStack(Items.bucket).setStackDisplayName("Air"))){
	        			ID.add(i, Block.getIdFromBlock(Blocks.air));
	        		}
	        		else if(blockFillList.get(i).getIsItemStackEqual(new ItemStack(Items.water_bucket).setStackDisplayName("Water"))){
	        			ID.add(i, Block.getIdFromBlock(Blocks.flowing_water));
	        		}
	        		else if(blockFillList.get(i).getIsItemStackEqual(new ItemStack(Items.lava_bucket).setStackDisplayName("Lava"))){
	        			ID.add(i, Block.getIdFromBlock(Blocks.flowing_lava));
	        		}
	        		else{
	        			ID.add(i, Block.getIdFromBlock(Block.getBlockFromItem(blockFillList.get(i).getItem())));
	        		}
	        		
//	        		ID.add(i, 
//	        				blockFillList.get(i).getItem() instanceof ItemBucket ? (
//	        						blockFillList.get(i).getItem() == Items.lava_bucket ? Block.getIdFromBlock(Blocks.flowing_lava) : Block.getIdFromBlock(Blocks.flowing_water)) :
//	        				Block.getIdFromBlock(Block.getBlockFromItem(blockFillList.get(i).getItem()))
//	        				);
	        		META.add(i, blockFillList.get(i).getMetadata());
	        		CHANCE.add(i, blockFillList.get(i).stackSize);
	        	}
	        	
	        	System.out.println(ID + "   " + META + "   " + CHANCE);
	        	
	        	PacketDispatcher.sendToServer(new SendAdvancedFillPacketToItemMessage(ID, META, CHANCE));
	        	
        	}
        }
    }

}
