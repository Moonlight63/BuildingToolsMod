package com.moonlight.buildingtools.items.tools.placetempblock;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.loaders.BlockLoader;

public class ToolPlaceTempBlock extends Item{
	
	public static BlockPos targetBlock;
	public static EnumFacing targetFace;
	public static World world;
	
	public static ItemStack thisStack;
	
	public ToolPlaceTempBlock(){
		super();
		setUnlocalizedName("tempblockplacer");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){
		
		RayTracing.instance().fire(1000, true);
		MovingObjectPosition target = RayTracing.instance().getTarget();
		
		if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK){
			targetBlock = target.getBlockPos();
			targetFace = target.sideHit;
		}
		else{
			targetBlock = null;
			targetFace = null;
		}
		
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(!worldIn.isRemote){
			if(playerIn.isSneaking())
				if(targetBlock != null)
					worldIn.setBlockState(targetBlock.offset(targetFace), BlockLoader.tempBlock.getDefaultState());
			else
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
		
		
		onItemRightClick(stack, worldIn, playerIn);
		
		return true;
	}
	
}
