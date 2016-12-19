package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.GUIBlockSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendAdvancedFillPacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendAdvancedReplacePacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SendCustomMessageCommand;
import com.moonlight.buildingtools.network.packethandleing.SendFillAndReplacePacketToItemMessage;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GUIReplaceTool extends GUIBlockSelection{

	public GUIReplaceTool(EntityPlayer player) {
		super(player);
		this.modeSwitch.visible = false;
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
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType){
    //protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType){
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
		
		//updateSelection();
		
//		
//		
//        if (mode == 0){
//	        if(clickedButton == 0){
//	        	
//	        	if(clickType == ClickType.PICKUP){
//	        		if(slotIn.getStack() == null)
//	        			return;
//	        		blockReplaceList.clear();
//	        		blockReplaceList.add(0, slotIn.getStack());
//	        	}
//	        	System.out.println(blockReplaceList);
//	        }
//	        else if(clickedButton == 1){
//	        	if(clickType == ClickType.PICKUP){
//	        		if(slotIn.getStack() == null)
//	        			return;
//	        		if(!blockReplaceList.isEmpty()){
//	        			
//	        			int currID;
//		        		int currDATA;
//		        		if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
//		        			currID = Block.getIdFromBlock(Blocks.AIR);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
//		        			currID = Block.getIdFromBlock(Blocks.FLOWING_WATER);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
//		        			currID = Block.getIdFromBlock(Blocks.FLOWING_LAVA);
//		        			currDATA = 0;
//		        		}
//		        		else{
//			        		currID = Block.getIdFromBlock(Block.getBlockFromItem(slotIn.getStack().getItem()));
//			        		currDATA = slotIn.getStack().getItem().getMetadata(slotIn.getStack());
//		        		}
//		        		
//		        		int currID2;
//		        		int currDATA2 = 0;
//		        		if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
//		        			currID2 = Block.getIdFromBlock(Blocks.AIR);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
//		        			currID2 = Block.getIdFromBlock(Blocks.FLOWING_WATER);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
//		        			currID2 = Block.getIdFromBlock(Blocks.FLOWING_LAVA);
//		        			currDATA = 0;
//		        		}
//		        		else{
//		        			currID2 = Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(0).getItem()));
//		        			currDATA2 = blockReplaceList.get(0).getItem().getMetadata(blockReplaceList.get(0));
//		        		}
//		        		
//		        		System.out.println(currID + "   " + currID2);
//	        			
////		        		int currID2 = Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(0).getItem()));
////		        		int currDATA2 = blockReplaceList.get(0).getItem().getMetadata(blockReplaceList.get(0));
//		        		PacketDispatcher.sendToServer(new SendSimpleReplacePacketToItemMessage(currID, currDATA, currID2, currDATA2));
//		        		this.mc.thePlayer.closeScreen();
//	        		}
//	        	}
////	        	else if(clickType == 1){
////	        		
////	        	}
//	        }
//        }
//        else{
//        	if(clickedButton == 0){
//	        	if(clickType == ClickType.PICKUP){
//	        		if(slotIn.getStack() == null)
//	        			return;
//	        		if(blockReplaceList.contains(slotIn.getStack())){
//	        			blockReplaceList.remove(slotIn.getStack());
//	        		}
//	        		else{
//	        			if(!blockFillList.contains(slotIn.getStack()))
//	        				blockReplaceList.add(slotIn.getStack());
//	        		}
//	        			
//	        		System.out.println(blockReplaceList);
//	        		
//	        	}
//	        	else if(clickType == ClickType.QUICK_MOVE){
//	        		if(!blockFillList.contains(slotIn.getStack())){
//	        			blockFillList.add(slotIn.getStack());
//	        		}
//	        		else{
//	        			blockFillList.remove(slotIn.getStack());
//	        			slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()+1);
//	        			blockFillList.add(slotIn.getStack());
//	        		}
//	        		System.out.println(blockFillList);
//	        		//slotIn.setColor(RGBA.Red.setAlpha(100));
//	        	}
//	        }
//	        else if(clickedButton == 1){
//	        	if(slotIn.getStack() == null)
//        			return;
//	        	//if(clickType == 0){
//	        		if(!blockFillList.contains(slotIn.getStack())){
//	        			//((ContainerBlockSelMenu.CustomSlot) slotIn).setColor(RGBA.White.setAlpha(0));
//	        			//blockFillList.add(slotIn.getStack());
//	        		}
//	        		else{
//	        			blockFillList.remove(slotIn.getStack());
//	        			if(slotIn.getStack().func_190916_E()>1){
//	        				slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()-1);
//	        				blockFillList.add(slotIn.getStack());
//	        			}
//	        			else if (slotIn.getStack().func_190916_E() == 1){
//	        				//((ContainerBlockSelMenu.CustomSlot) slotIn).clearColor();
//	        			}
//	        			
//	        		}
//	        		System.out.println(blockFillList);
////	        	}
////	        	else if(clickType == 1){
////	        		
////	        	}
//	        }
//        }

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
    	
    	PacketDispatcher.sendToServer(new SendFillAndReplacePacketToItemMessage(ID, META, CHANCE, ID2, META2));
//    	PacketDispatcher.sendToServer(new SendAdvancedFillPacketToItemMessage(ID, META, CHANCE));
//    	PacketDispatcher.sendToServer(new SendAdvancedReplacePacketToItemMessage(ID2, META2));
	}
	
	@Override
	protected boolean showModeSwitchButton(){
		return false;
	}
	
	
	
//	/**
//     * Called when the mouse is clicked over a slot or outside the gui.
//     * Click Type 1 = Shift Click
//     * Click Type 2 = Hotbar key
//     * Click Type 3 = Pick Block
//     * Click Type 4 = Drop Key, Click outside of GUI
//     * Click Type 5 =
//     */
//	@Override
//	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType){
//    //protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType){
//		this.keyOrButtonClicked = true;
//		
//        if (mode == 0){
//	        if(clickedButton == 0){
//	        	
//	        	if(clickType == ClickType.PICKUP){
//	        		if(slotIn.getStack() == null)
//	        			return;
//	        		blockReplaceList.clear();
//	        		blockReplaceList.add(0, slotIn.getStack());
//	        	}
//	        	System.out.println(blockReplaceList);
//	        }
//	        else if(clickedButton == 1){
//	        	if(clickType == ClickType.PICKUP){
//	        		if(slotIn.getStack() == null)
//	        			return;
//	        		if(!blockReplaceList.isEmpty()){
//	        			
//	        			int currID;
//		        		int currDATA;
//		        		if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
//		        			currID = Block.getIdFromBlock(Blocks.AIR);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
//		        			currID = Block.getIdFromBlock(Blocks.FLOWING_WATER);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(slotIn.getStack(), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
//		        			currID = Block.getIdFromBlock(Blocks.FLOWING_LAVA);
//		        			currDATA = 0;
//		        		}
//		        		else{
//			        		currID = Block.getIdFromBlock(Block.getBlockFromItem(slotIn.getStack().getItem()));
//			        		currDATA = slotIn.getStack().getItem().getMetadata(slotIn.getStack());
//		        		}
//		        		
//		        		int currID2;
//		        		int currDATA2 = 0;
//		        		if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
//		        			currID2 = Block.getIdFromBlock(Blocks.AIR);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
//		        			currID2 = Block.getIdFromBlock(Blocks.FLOWING_WATER);
//		        			currDATA = 0;
//		        		}
//		        		else if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
//		        			currID2 = Block.getIdFromBlock(Blocks.FLOWING_LAVA);
//		        			currDATA = 0;
//		        		}
//		        		else{
//		        			currID2 = Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(0).getItem()));
//		        			currDATA2 = blockReplaceList.get(0).getItem().getMetadata(blockReplaceList.get(0));
//		        		}
//		        		
//		        		System.out.println(currID + "   " + currID2);
//	        			
////		        		int currID2 = Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(0).getItem()));
////		        		int currDATA2 = blockReplaceList.get(0).getItem().getMetadata(blockReplaceList.get(0));
//		        		PacketDispatcher.sendToServer(new SendSimpleReplacePacketToItemMessage(currID, currDATA, currID2, currDATA2));
//		        		this.mc.thePlayer.closeScreen();
//	        		}
//	        	}
////	        	else if(clickType == 1){
////	        		
////	        	}
//	        }
//        }
//        else{
//        	if(clickedButton == 0){
//	        	if(clickType == ClickType.PICKUP){
//	        		if(slotIn.getStack() == null)
//	        			return;
//	        		if(blockReplaceList.contains(slotIn.getStack())){
//	        			blockReplaceList.remove(slotIn.getStack());
//	        		}
//	        		else{
//	        			if(!blockFillList.contains(slotIn.getStack()))
//	        				blockReplaceList.add(slotIn.getStack());
//	        		}
//	        			
//	        		System.out.println(blockReplaceList);
//	        		
//	        	}
//	        	else if(clickType == ClickType.QUICK_MOVE){
//	        		if(!blockFillList.contains(slotIn.getStack())){
//	        			blockFillList.add(slotIn.getStack());
//	        		}
//	        		else{
//	        			blockFillList.remove(slotIn.getStack());
//	        			slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()+1);
//	        			blockFillList.add(slotIn.getStack());
//	        		}
//	        		System.out.println(blockFillList);
//	        		//slotIn.setColor(RGBA.Red.setAlpha(100));
//	        	}
//	        }
//	        else if(clickedButton == 1){
//	        	if(slotIn.getStack() == null)
//        			return;
//	        	//if(clickType == 0){
//	        		if(!blockFillList.contains(slotIn.getStack())){
//	        			//((ContainerBlockSelMenu.CustomSlot) slotIn).setColor(RGBA.White.setAlpha(0));
//	        			//blockFillList.add(slotIn.getStack());
//	        		}
//	        		else{
//	        			blockFillList.remove(slotIn.getStack());
//	        			if(slotIn.getStack().func_190916_E()>1){
//	        				slotIn.getStack().func_190920_e(slotIn.getStack().func_190916_E()-1);
//	        				blockFillList.add(slotIn.getStack());
//	        			}
//	        			else if (slotIn.getStack().func_190916_E() == 1){
//	        				//((ContainerBlockSelMenu.CustomSlot) slotIn).clearColor();
//	        			}
//	        			
//	        		}
//	        		System.out.println(blockFillList);
////	        	}
////	        	else if(clickType == 1){
////	        		
////	        	}
//	        }
//        }
//
//    }
//	
//	
//	
//	/**
//     * Called when the screen is unloaded. Used to disable keyboard repeat events
//     */
//	@Override
//    public void onGuiClosed(){
//		super.onGuiClosed();
//        Keyboard.enableRepeatEvents(false);
//        
//        if(mode == 1){
//        	
//        	if(!blockFillList.isEmpty() && !blockReplaceList.isEmpty()){
//        	
//	        	List<Integer> ID = Lists.<Integer>newArrayList();
//	        	List<Integer> META = Lists.<Integer>newArrayList();
//	        	List<Integer> CHANCE = Lists.<Integer>newArrayList();
//	        	
//	        	for(int i = 0; i < blockFillList.size(); i++){
//	        		System.out.println("SIZE = " + blockFillList.size());
//	        		if(ItemStack.areItemsEqual(blockFillList.get(i), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
//	        			ID.add(i, Block.getIdFromBlock(Blocks.AIR));
//	        		}
//	        		else if(ItemStack.areItemsEqual(blockFillList.get(i), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
//	        			ID.add(i, Block.getIdFromBlock(Blocks.FLOWING_WATER));
//	        		}
//	        		else if(ItemStack.areItemsEqual(blockFillList.get(i), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
//	        			ID.add(i, Block.getIdFromBlock(Blocks.FLOWING_LAVA));
//	        		}
//	        		else{
//	        			ID.add(i, Block.getIdFromBlock(Block.getBlockFromItem(blockFillList.get(i).getItem())));
//	        		}
//	        		META.add(i, blockFillList.get(i).getMetadata());
//	        		CHANCE.add(i, blockFillList.get(i).func_190916_E());
//	        	}
//	        	
//	        	List<Integer> ID2 = Lists.<Integer>newArrayList();
//	        	List<Integer> META2 = Lists.<Integer>newArrayList();
//	        	
//	        	for(int i = 0; i < blockReplaceList.size(); i++){
//	        		
//	        		if(ItemStack.areItemsEqual(blockReplaceList.get(i), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
//	        			ID2.add(Block.getIdFromBlock(Blocks.AIR));
//	        			META2.add(i, blockReplaceList.get(i).getMetadata());
//	        		}
//	        		else if(ItemStack.areItemsEqual(blockReplaceList.get(i), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
//	        			ID2.add(Block.getIdFromBlock(Blocks.FLOWING_WATER));
//	        			ID2.add(Block.getIdFromBlock(Blocks.WATER));
//	        			META2.add(i, blockReplaceList.get(i).getMetadata());
//	        			META2.add(i, blockReplaceList.get(i).getMetadata());
//	        		}
//	        		else if(ItemStack.areItemsEqual(blockReplaceList.get(i), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
//	        			ID2.add(Block.getIdFromBlock(Blocks.FLOWING_LAVA));
//	        			ID2.add(Block.getIdFromBlock(Blocks.LAVA));
//	        			META2.add(i, blockReplaceList.get(i).getMetadata());
//	        			META2.add(i, blockReplaceList.get(i).getMetadata());
//	        		}
//	        		else{
//	        			ID2.add(i, Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(i).getItem())));
//	        			META2.add(i, blockReplaceList.get(i).getMetadata());
//	        		}
//	        		
//	        		System.out.println("SIZE = " + blockFillList.size());
////	        		if(blockReplaceList.get(i).getItem() == Items.lava_bucket){
////	        			ID2.add(Block.getIdFromBlock(Blocks.flowing_lava));
////	        			ID2.add(Block.getIdFromBlock(Blocks.lava));
////	        			META2.add(i, blockReplaceList.get(i).getMetadata());
////	        			META2.add(i, blockReplaceList.get(i).getMetadata());
////	        		}
////	        		else if(blockReplaceList.get(i).getItem() == Items.water_bucket){
////	        			ID2.add(Block.getIdFromBlock(Blocks.flowing_water));
////	        			ID2.add(Block.getIdFromBlock(Blocks.water));
////	        			META2.add(i, blockReplaceList.get(i).getMetadata());
////	        			META2.add(i, blockReplaceList.get(i).getMetadata());
////	        		}
////	        		else{
////	        			ID2.add(i, Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(i).getItem())));
////	        			META2.add(i, blockReplaceList.get(i).getMetadata());
////	        		}
//	        		
//	        	}
//	        	
//	        	System.out.println(ID + "   " + META + "   " + CHANCE);
//	        	
//	        	PacketDispatcher.sendToServer(new SendAdvancedReplacePacketToItemMessage(ID, META, CHANCE, ID2, META2));
//	        	
//        	}
//        }
//    }

}
