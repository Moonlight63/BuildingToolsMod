package com.moonlight.buildingtools.items.tools.undoTool;

import java.util.LinkedHashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.selectiontool.ThreadPasteClipboard;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.KeyHelper;

public class ToolUndo extends Item{
	
	
	
	public ToolUndo(){
		super();
		setUnlocalizedName("undoTool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
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
            //list.add("Hold SHIFT for details");
            //list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(!worldIn.isRemote){
			System.out.println("Used Undo Tool");
			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			//System.out.println(player.undolist);
			if(!player.undolist.isEmpty() && player.UndoIsSaved){
				player.addPending(new ThreadPasteClipboard(worldIn, playerIn, /*player.lastUndo, */new LinkedHashSet<Entity>()));				
			}
			if(!player.UndoIsSaved){
				playerIn.addChatComponentMessage(new ChatComponentText("The last operation is not finished saving. Please Wait!"));
			}
			else if (player.undolist.isEmpty()){
				playerIn.addChatComponentMessage(new ChatComponentText("No Undo operations are recorded"));
			}
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
