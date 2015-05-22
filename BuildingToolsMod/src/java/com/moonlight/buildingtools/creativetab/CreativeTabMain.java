package com.moonlight.buildingtools.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabMain extends CreativeTabs {
	
	public CreativeTabMain(int id, String unlocalizedName){
		super(id, unlocalizedName);
	}
	
	@SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        //return Item.getItemFromBlock(Blocks.chest);
        return Items.milk_bucket;
    }
	
	public boolean hasSearchBar() {
        return false;
    }
}
