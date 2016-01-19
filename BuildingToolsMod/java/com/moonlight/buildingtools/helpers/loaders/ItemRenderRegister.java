package com.moonlight.buildingtools.helpers.loaders;


import com.moonlight.buildingtools.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ItemRenderRegister {
	
	public static void registerItemRenderer() {
		
		for(Item item : ItemLoader.itemsToRegister){
			reg(item);
		}
		
		//reg(ItemLoader.blockChanger);
		//reg(ItemLoader.blockSmoother);
	}

	public static void reg(Item item) {
	    Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}

}
