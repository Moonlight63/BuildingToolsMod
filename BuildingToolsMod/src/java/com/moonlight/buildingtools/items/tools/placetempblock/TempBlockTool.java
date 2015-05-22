package com.moonlight.buildingtools.items.tools.placetempblock;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;

public class TempBlockTool extends Item{
	
	public static BlockPos targetBlock;
	public static World world;
	
	public static ItemStack thisStack;
	
	public TempBlockTool(){
		super();
		setUnlocalizedName("tempblockplacer");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){
		EntityPlayer player = (EntityPlayer) entity;
		
		if(player.getCurrentEquippedItem() == itemstack){
			BuildingTools.proxy.setExtraReach(player, 200);
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(!worldIn.isRemote){
			worldIn.setBlockState(playerIn.getPosition(), BlockLoader.tempBlock.getDefaultState());
		}
		
        return itemStackIn;
    }
		
	public boolean onItemUse(ItemStack stack,
            EntityPlayer playerIn,
            World worldIn,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ){
		
		
		if(!worldIn.isRemote){
			if(playerIn.isSneaking())
				worldIn.setBlockState(pos.offset(side), BlockLoader.tempBlock.getDefaultState());
			else
				worldIn.setBlockState(playerIn.getPosition(), BlockLoader.tempBlock.getDefaultState());
		}
		
		return true;
	}
	
}
