package com.moonlight.buildingtools.helpers.loaders;

import com.moonlight.buildingtools.Reference;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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
	}

    /**
     * Registers all items. The basic format is [MODID_NAME]
     * When you call this method, with your item assigned, it will take care of everything.
     * @param item
     */
	public static void registerItem(Item item){
		GameRegistry.register(item);
	}
	
	public static void registerTileEntity(Class<? extends TileEntity> entity, Block block){
		GameRegistry.registerTileEntity(entity, Reference.MODID + "_" + block.getUnlocalizedName().substring(5) + "Tile");
	}
}