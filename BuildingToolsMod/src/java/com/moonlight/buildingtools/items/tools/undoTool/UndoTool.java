package com.moonlight.buildingtools.items.tools.undoTool;

import java.util.LinkedHashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.selectiontool.RegoinCopyThread;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.KeyHelper;

public class UndoTool extends Item{
	
	
	
	public UndoTool(){
		super();
		setUnlocalizedName("undoTool");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, @SuppressWarnings("rawtypes") List list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        if (KeyHelper.isShiftDown())
        {
            if (stack.getTagCompound() == null){
                //setDefaultTag(stack, 0);
            }


            //ItemStack pb = ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock"));
            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.source " + pb.getDisplayName()) + EnumChatFormatting.RESET);

            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.radius " + this.getTargetRadius(stack)));

            //list.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + /*LocalisationHelper.localiseString*/("info.exchanger.shift_to_select_source") + EnumChatFormatting.RESET);
        }
        else{
            list.add("Hold SHIFT for details");
            list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(!worldIn.isRemote){
			System.out.println("Used Undo Tool");
			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			if(!player.undolist.isEmpty())
				player.addPending(new RegoinCopyThread(worldIn, playerIn, player.undolist.pollLast(), new LinkedHashSet<Entity>()));
			//player.addPending(new UndoThread(worldIn, playerIn));
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
			System.out.println("Used Undo Tool");
			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			if(!player.undolist.isEmpty())
				player.addPending(new RegoinCopyThread(worldIn, playerIn, player.undolist.pollLast(), new LinkedHashSet<Entity>()));
		}
		
		return true;
	}
}
