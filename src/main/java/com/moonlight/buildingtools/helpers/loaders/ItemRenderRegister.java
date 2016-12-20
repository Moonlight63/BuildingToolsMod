package com.moonlight.buildingtools.helpers.loaders;


import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemRenderRegister {
	
	public static void registerItemRenderer() {
		
		for(Item item : ItemLoader.itemsToRegister){
			reg(item);
		}
		
		//reg(ItemLoader.blockChanger);
		//reg(ItemLoader.blockSmoother);
	}

	public static void reg(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	    //Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

}
