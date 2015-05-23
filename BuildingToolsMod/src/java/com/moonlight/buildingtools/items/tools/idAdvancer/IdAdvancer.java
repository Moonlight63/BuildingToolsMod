package com.moonlight.buildingtools.items.tools.idAdvancer;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.RGBA;
//import java.util.Optional;
//import com.moonlight.buildingtools.utils.KeyBindsHandler.ETKeyBinding;

public class IdAdvancer extends Item implements IOutlineDrawer{
	
	public static BlockPos targetBlock;
	public static World world;
	
	public static ItemStack thisStack;
	
	public IdAdvancer(){
		super();
		setUnlocalizedName("idadvancer");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		//if(playerIn.isSneaking()){
		//	getNBT(itemStackIn).setBoolean("bpos1Set", false);
		//	getNBT(itemStackIn).setBoolean("bpos2Set", false);
		//}
        return itemStackIn;
    }
		
	public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos,
            EnumFacing side, float hitX, float hitY, float hitZ){
		
		
		if(!worldIn.isRemote){
			
			System.out.println(worldIn.getBlockState(pos).getProperties());
			
			if(worldIn.getBlockState(pos).getProperties().containsKey(PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL))){
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).cycleProperty(PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)));
			}
			else if(worldIn.getBlockState(pos).getProperties().containsKey(PropertyEnum.create("axis", BlockLog.EnumAxis.class))){
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).cycleProperty(PropertyEnum.create("axis", BlockLog.EnumAxis.class)));
			}
			else if(worldIn.getBlockState(pos).getProperties().containsKey(PropertyEnum.create("variant", BlockQuartz.EnumType.class))){
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).cycleProperty(PropertyEnum.create("variant", BlockQuartz.EnumType.class)));
			}
			if(worldIn.getBlockState(pos).getProperties().containsKey(PropertyInteger.create("rotation", 0, 15))){
					//System.out.println(worldIn.getBlockState(pos).getProperties());
					worldIn.setBlockState(pos, worldIn.getBlockState(pos).cycleProperty(PropertyInteger.create("rotation", 0, 15)));
				}
		}
		
		return true;
	}
	
	@Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
		BlockPos target = event.target.getBlockPos();
        World world = event.player.worldObj;

        if (event.player.isSneaking())
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }	        
        
        return true;
    }
	
}
