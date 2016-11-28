package com.moonlight.buildingtools.items.tools.undoTool;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.selectiontool.GUISaveLoadClipboard;
import com.moonlight.buildingtools.items.tools.selectiontool.ThreadPasteClipboard;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.KeyHelper;

public class ToolUndo extends Item{
	
	EntityPlayer playerIn;
	
	public ToolUndo(){
		super();
		setUnlocalizedName("undoTool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
		this.playerIn = (EntityPlayer) entityIn;
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
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		if(playerIn.isSneaking()){
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIUndoSave, worldIn, 0, 0, 0);
			//PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			//player.addPending(new ThreadSaveUndoList(playerIn, new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())));
		}
		else{
			if(!worldIn.isRemote){
				System.out.println("Used Undo Tool");
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				//System.out.println(player.undolist);
				if(!player.undolist.isEmpty() && player.UndoIsSaved){
					player.addPending(new ThreadPasteClipboard(worldIn, playerIn, /*player.lastUndo, */new LinkedHashSet<Entity>()));		
					playerIn.addChatMessage(new TextComponentString("Undoing"));
				}
				if(!player.UndoIsSaved){
					playerIn.addChatMessage(new TextComponentString("The last operation is not finished saving. Please Wait!"));
				}
				else if (player.undolist.isEmpty()){
					playerIn.addChatMessage(new TextComponentString("No Undo operations are recorded"));
				}
			}
		}
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
    }
		
	public boolean onItemUse(ItemStack stack,
            EntityPlayer playerIn,
            World worldIn,
            BlockPos pos,
            EnumFacing side,
            float hitX,
            float hitY,
            float hitZ){
		
		onItemRightClick(worldIn, playerIn, EnumHand.MAIN_HAND);
		
		return true;
	}
	
	public void loadUndos(String file){
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
		player.addPending(new ThreadLoadUndo(playerIn, file));
	}
}
