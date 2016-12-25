package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.items.tools.GUIBlockSelection;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendNBTCommandPacket;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GUISetPaintBlock extends GUIBlockSelection{
	
	public static final GuiButton tutorialMode = 	new GuiButton(100, 20, 20, 20, 20, "?");

	public GUISetPaintBlock(EntityPlayer player) {
		super(player);
	}
	
	@Override
	protected boolean showModeSwitchButton() {
		return false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		if (tutorialMode.isMouseOver()) { // Tells you if the button is hovered by mouse
			List<String> temp = Arrays.asList(new String[]{ 
				"This is the Paint Block Selection Menu:",
				"",
				"Left click on a block to add it to your paint material.",
				"Right click to remove a block from your paint material.",
				"",
				"Selecting multiple blocks allows each block you paint to be randomly chosen from your selection.",
				"You can also add the same block more than once, which makes it more likly to be chosen when you paint.",
				"For example: If you select Stone blocks 10 times, and Dirt blocks once, Stone will be painted 10 times more than dirt.",
				"",
				"Hold Shift to add or remove 10 blocks from your selection at a time.",
				"",
				"By default: Blocks of the same ID, but different MetaData are hidden.",
				"The 'Meta Data' Button will show ALL blocks."
			});
			
			drawHoveringText(temp, mouseX, mouseY, fontRendererObj);
		}
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buttonList.add(tutorialMode);
	}
	
	@Override
	protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, ClickType clickType){
        
		ItemStack stack;
		if(slotIn != null)
			stack = slotIn.getStack();
		else
			return;
		
		int amount = 1;
		
		if(clickType == ClickType.QUICK_MOVE){
			amount = 10;
		}
		else{
			amount = 1;
		}
		
		this.keyOrButtonClicked = true;
		
//        if (mode == 0){
//	        if(clickedButton == 0){
//        		if(stack == null)
//        			return;
//        		
//        		blockFillList.add(stack);
//        		this.mc.thePlayer.closeScreen();
//	        }
//        }
//        else{
        	if(clickedButton == 0){
        		if(stack == null)
        			return;
        		if(!blockFillList.contains(stack)){
        			if(clickType == ClickType.QUICK_MOVE){
						stack.func_190920_e(stack.func_190916_E()+amount-1);
					}
        			blockFillList.add(stack);
        		}
        		else{
        			blockFillList.remove(stack);
        			stack.func_190920_e(stack.func_190916_E()+amount);
        			blockFillList.add(stack);
        		}
	        }
	        else if(clickedButton == 1){
        		if(stack == null)
        			return;
        		if(blockFillList.contains(stack)){
        			blockFillList.remove(stack);
        			if(stack.func_190916_E()>1){
        				stack.func_190920_e(stack.func_190916_E()-amount);
        				boolean flag = false;;
        				if(stack.func_190916_E() < 1){
        					stack.func_190920_e(1);
        					flag = true;
        				}
        				if(!flag)
        					blockFillList.add(stack);
        			}	        			
        		}
        	}
        //}
        
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
