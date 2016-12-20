package com.moonlight.buildingtools.items.tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GetGUIBlocks {
	
	public GetGUIBlocks () {
		
	}
	
	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	public void GetBlocksForGUI(List<ItemStack> items) {
		for(Block b : GameData.getBlockRegistry()){
    		if(Item.getItemFromBlock(b) != null/* && Item.getItemFromBlock(b).getCreativeTab() != null*/)
    			items.add(new ItemStack(b));
    	}
	}
	
	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	public void GetBlocksForGUIMeta(NonNullList<ItemStack> items) {
		for(Block b : GameData.getBlockRegistry()){
    		if(Item.getItemFromBlock(b) != null/* && Item.getItemFromBlock(b).getCreativeTab() != null*/)
    			Item.getItemFromBlock(b).getSubItems(Item.getItemFromBlock(b), null, items);
    			//items.add(new ItemStack(b));
    	}
	}

}
