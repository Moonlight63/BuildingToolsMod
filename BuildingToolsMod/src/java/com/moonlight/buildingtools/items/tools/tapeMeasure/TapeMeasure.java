package com.moonlight.buildingtools.items.tools.tapeMeasure;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;

public class TapeMeasure extends Item{
	
	private BlockPos firstPos = BlockPos.ORIGIN;
	
	public TapeMeasure(){
		super();
		setUnlocalizedName("tape");
		setCreativeTab(BuildingTools.tabBT);
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
			if(this.firstPos != BlockPos.ORIGIN){
				System.out.println(firstPos);
				if (Math.abs(this.firstPos.getX() - pos.getX()) != 0) {
					playerIn.addChatComponentMessage(new ChatComponentText("X: " + (this.firstPos.getX() - pos.getX())));
				}
				if (Math.abs(this.firstPos.getY() - pos.getY()) != 0) {
					playerIn.addChatComponentMessage(new ChatComponentText("Y: " + (this.firstPos.getY() - pos.getY())));
				}
				if (Math.abs(this.firstPos.getZ() - pos.getZ()) != 0) {
					playerIn.addChatComponentMessage(new ChatComponentText("Z: " + (this.firstPos.getZ() - pos.getZ())));
				}
				firstPos = BlockPos.ORIGIN;
			}
			else{
				playerIn.addChatComponentMessage(new ChatComponentText("Position 1 Set"));
				firstPos = pos;
			}
		}
		
		return true;
	}
}
