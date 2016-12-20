package com.moonlight.buildingtools.creativetab;

import com.moonlight.buildingtools.helpers.loaders.ItemLoader;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabMain extends CreativeTabs {
	
	public CreativeTabMain(int id, String unlocalizedName){
		super(id, unlocalizedName);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        //return Item.getItemFromBlock(Blocks.chest);
        return new ItemStack(ItemLoader.toolBuilding);
    }
	
	@Override
	public boolean hasSearchBar() {
        return false;
    }
}
