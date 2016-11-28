package com.moonlight.buildingtools.items.tools.filtertool;

import java.io.IOException;
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
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GUISetLogMat extends GUIBlockSelection{

	EntityPlayer player;
	protected GuiButton done = new GuiButton(1, 0, 0, "Done");
	
	public GUISetLogMat(EntityPlayer player) {
		super(player);
		this.player = player;
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
	        		if(blockFillList.isEmpty())
	        			blockFillList.add(0, slotIn.getStack());
	        		else
	        			blockFillList.set(0, slotIn.getStack());
	        	}
	        	else if(clickType == 1){
	        		
	        	}
	        	System.out.println(blockReplaceList);
	        }
	        else if(clickedButton == 1){
	        	if(clickType == 0){
	        		if(blockReplaceList.isEmpty())
	        			blockReplaceList.add(0, slotIn.getStack());
	        		else
	        			blockReplaceList.set(0, slotIn.getStack());
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
	
	
	@Override
	public void initGui(){
		super.initGui();
        this.done.xPosition = this.guiLeft;
        this.done.yPosition = this.guiTop + 135;
        this.done.setWidth(100);
        this.buttonList.add(done);
	}
	
	
	
	/**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
	@Override
    public void onGuiClosed(){
		super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }
	
	@Override
	protected boolean showModeSwitchButton(){
		return false;
	}
	
	
	/**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
	@Override
    protected void actionPerformed(GuiButton button) throws IOException{
    	super.actionPerformed(button);
    	
    	if(button == done){
    		if(!blockReplaceList.isEmpty() && !blockFillList.isEmpty()){
            	
            	int ID = 0;
            	int META = 0;
            	int ID2 = 0;
            	int META2 = 0;
            	
            	if(ItemStack.areItemsEqual(blockFillList.get(0), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
        			ID=(Block.getIdFromBlock(Blocks.AIR));
        			META=(blockFillList.get(0).getMetadata());
        		}
        		else if(ItemStack.areItemsEqual(blockFillList.get(0), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
        			ID=(Block.getIdFromBlock(Blocks.WATER));
        			META=(blockFillList.get(0).getMetadata());
        		}
        		else if(ItemStack.areItemsEqual(blockFillList.get(0), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
        			ID=(Block.getIdFromBlock(Blocks.LAVA));
        			META=(blockFillList.get(0).getMetadata());
        		}
        		else{
        			ID=(Block.getIdFromBlock(Block.getBlockFromItem(blockFillList.get(0).getItem())));
        			META=(blockFillList.get(0).getMetadata());
        		}
            	
            	if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.BUCKET).setStackDisplayName("Air"))){
        			ID2=(Block.getIdFromBlock(Blocks.AIR));
        			META2=(blockReplaceList.get(0).getMetadata());
        		}
        		else if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.WATER_BUCKET).setStackDisplayName("Water"))){
        			ID2=(Block.getIdFromBlock(Blocks.WATER));
        			META2=(blockReplaceList.get(0).getMetadata());
        		}
        		else if(ItemStack.areItemsEqual(blockReplaceList.get(0), new ItemStack(Items.LAVA_BUCKET).setStackDisplayName("Lava"))){
        			ID2=(Block.getIdFromBlock(Blocks.LAVA));
        			META2=(blockReplaceList.get(0).getMetadata());
        		}
        		else{
        			ID2=(Block.getIdFromBlock(Block.getBlockFromItem(blockReplaceList.get(0).getItem())));
        			META2=(blockReplaceList.get(0).getMetadata());
        		}
            	
            	PacketDispatcher.sendToServer(new SendSimpleReplacePacketToItemMessage(ID, META, ID2, META2));
            	
            	this.mc.displayGuiScreen((GuiScreen) null);
    			this.mc.displayGuiScreen(new GUIEditTree(this.player, ((ToolFilter)(player.getHeldItemMainhand().getItem())).treeData));
            	
            }
    	}
    	
    }

}
