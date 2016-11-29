package com.moonlight.buildingtools.helpers.loaders;

import com.moonlight.buildingtools.Reference;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegisterHelper 
{
    /**
     * Registers all blocks. The basic format is [MODID_NAME]
     * When you call this method, with your block assigned, it will take care of everything.
     * @param block
     */
	public static void registerBlock(Block block){
		GameRegistry.register(block);
		//System.out.print(block.getUnlocalizedName().substring(5));
	}

    /**
     * Registers all items. The basic format is [MODID_NAME]
     * When you call this method, with your item assigned, it will take care of everything.
     * @param item
     */
	public static void registerItem(Item item){
		//GameRegistry.register(item, new ResourceLocation(item.getUnlocalizedName().substring(5)));
		GameRegistry.register(item);
	}
	
//	public static void registerItemRenderer(Item item){
//		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, 
//				new ModelResourceLocation(Reference.MODID + ":" + item.getUnlocalizedName().substring(5), "inventory")
//				);
//	}
	
	public static void registerTileEntity(Class<? extends TileEntity> entity, Block block){
		GameRegistry.registerTileEntity(entity, Reference.MODID + "_" + block.getUnlocalizedName().substring(5) + "Tile");
	}
}