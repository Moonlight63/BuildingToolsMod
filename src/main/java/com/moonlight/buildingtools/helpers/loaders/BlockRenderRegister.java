package com.moonlight.buildingtools.helpers.loaders;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public final class BlockRenderRegister {
		
    public static void registerBlockRenderer() {
    	for(Block block : BlockLoader.blocksToRegister){
    		reg(block);
    	}
    }
    public static void reg(Block block) {
    	ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
        //Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(Reference.MODID + ":" + block.getUnlocalizedName().substring(5), "inventory"));
    }
}