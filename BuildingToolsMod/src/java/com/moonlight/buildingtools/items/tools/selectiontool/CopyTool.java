package com.moonlight.buildingtools.items.tools.selectiontool;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.RGBA;

public class CopyTool extends Item implements IOutlineDrawer, IGetGuiButtonPressed{
	
	public static BlockPos targetBlock;
	public World world;
	public static EntityPlayer currPlayer;
	public static ItemStack thisStack;
	
	public static BlockPos blockpos1;
	public static BlockPos blockpos2;
	public static boolean blockpos1set;
	public static boolean blockpos2set;
	
	public static RegoinCopyThread copyThread;
	
	public CopyTool(){
		super();
		setUnlocalizedName("copyTool");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        
	        stack.getTagCompound().setBoolean("bpos1Set", false);
	        stack.getTagCompound().setBoolean("bpos2Set", false);
	        stack.getTagCompound().setInteger("Rotation", 0);
	        stack.getTagCompound().setBoolean("flipX", false);
	        stack.getTagCompound().setBoolean("flipY", false);
	        stack.getTagCompound().setBoolean("flipZ", false);
	        
	        stack.getTagCompound().setInteger("repeat", 0);
	        
	        stack.getTagCompound().setInteger("repeatMovmentX", 0);
	        stack.getTagCompound().setInteger("repeatMovmentY", 0);
	        stack.getTagCompound().setInteger("repeatMovmentZ", 0);
	    }
	    return stack.getTagCompound();	    
	}

	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool){
		EntityPlayer player = (EntityPlayer) entity;
		
		if(thisStack == null){
			thisStack = itemstack;
		}
		
		if(this.world == null){
			this.world = world;
		}
		
		if(player.getCurrentEquippedItem() == itemstack){
			BuildingTools.proxy.setExtraReach(player, 200);
		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(playerIn.isSneaking()){
			getNBT(itemStackIn).setBoolean("bpos1Set", false);
			getNBT(itemStackIn).setBoolean("bpos2Set", false);
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
		
		currPlayer = playerIn;
		world = worldIn;
		targetBlock = pos;
		thisStack = stack;
		if(playerIn.isSneaking()){
				
				playerIn.openGui(BuildingTools.instance, SelectionToolGui.GUI_ID, worldIn, 0, 0, 0);
				
			}
		
		if(!worldIn.isRemote){
			
			if(playerIn.isSneaking()){
				
			//	playerIn.openGui(BuildingTools.instance, SelectionToolGui.GUI_ID, worldIn, 0, 0, 0);
				
			}
			else{
			
			
				thisStack = stack;
			
				if(!getNBT(stack).getBoolean("bpos1Set") && !getNBT(stack).getBoolean("bpos2Set")){
					setBlockPos1(stack, pos);
				}
				else if(getNBT(stack).getBoolean("bpos1Set") && !getNBT(stack).getBoolean("bpos2Set")){
					setBlockPos2(stack, pos);
				}
				else if(getNBT(stack).getBoolean("bpos1Set") && getNBT(stack).getBoolean("bpos2Set")){
					setBlockPos1(stack, pos);
				}
			
			}
			
		}
		
		return true;
	}
	
	public void setBlockPos1 (ItemStack stack, BlockPos pos){
		getNBT(stack).setInteger("bpos1X", pos.getX());
		getNBT(stack).setInteger("bpos1Y", pos.getY());
		getNBT(stack).setInteger("bpos1Z", pos.getZ());
		
		getNBT(stack).setBoolean("bpos1Set", true);
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}
	
	public void setBlockPos2 (ItemStack stack, BlockPos pos){
		getNBT(stack).setInteger("bpos2X", pos.getX());
		getNBT(stack).setInteger("bpos2Y", pos.getY());
		getNBT(stack).setInteger("bpos2Z", pos.getZ());
		
		getNBT(stack).setBoolean("bpos2Set", true);
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}
	
	public BlockPos getBlockPos1(ItemStack stack){
		if(getNBT(stack).getBoolean("bpos1Set")){
			return new BlockPos(getNBT(stack).getInteger("bpos1X"),
								getNBT(stack).getInteger("bpos1Y"),
								getNBT(stack).getInteger("bpos1Z"));
		}
		else{
			return null;
		}
	}
	
	public BlockPos getBlockPos2(ItemStack stack){
		if(getNBT(stack).getBoolean("bpos2Set")){
			return new BlockPos(getNBT(stack).getInteger("bpos2X"),
								getNBT(stack).getInteger("bpos2Y"),
								getNBT(stack).getInteger("bpos2Z"));
		}
		else{
			return null;
		}
	}
	
	@Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
		BlockPos target = event.target.getBlockPos();

        if (event.player.isSneaking())
        {
            RenderHelper.renderBlockOutline(event.context, event.player, target, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);
            return true;
        }
        
        if (getNBT(event.currentItem).getBoolean("bpos1Set")){
        	RenderHelper.renderBlockOutline(event.context, event.player, getBlockPos1(event.currentItem), RGBA.Blue, 1.0f, event.partialTicks);
        }
        
        if (getNBT(event.currentItem).getBoolean("bpos2Set")){
        	RenderHelper.renderBlockOutline(event.context, event.player, getBlockPos2(event.currentItem), RGBA.Blue, 1.0f, event.partialTicks);
        }
        
        if (getNBT(event.currentItem).getBoolean("bpos1Set") && getNBT(event.currentItem).getBoolean("bpos2Set")){
        	RenderHelper.renderSelectionBox(event.context, event.player, getBlockPos1(event.currentItem), getBlockPos2(event.currentItem), RGBA.Red, 1.5f, event.partialTicks);
        }
	        
        
        return true;
    }

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown,
			ItemStack stack) {
		
		thisStack = stack;
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		switch (buttonID) {
		case 1:
			System.out.println("Copying");
			player.addPending(new CopyToClipboardThread(
					new BlockPos(getBlockPos1(stack).getX(),
								getBlockPos1(stack).getY(),
								getBlockPos1(stack).getZ()),
					new BlockPos(getBlockPos2(stack).getX(),
								getBlockPos2(stack).getY(),
								getBlockPos2(stack).getZ()), 
					world, currPlayer));
			break;
			
		case 2:
			
			player.addPending(new RegoinCopyThread(
					world, currPlayer, targetBlock,
					getNBT(stack).getInteger("Rotation"),
					getNBT(stack).getBoolean("flipX"),
					getNBT(stack).getBoolean("flipY"),
					getNBT(stack).getBoolean("flipZ")));
			
			if(getNBT(stack).getInteger("repeat") > 0){				
				for(int i = 1; i < getNBT(stack).getInteger("repeat"); i++){
					player.addPending(new RegoinCopyThread(
					world, currPlayer, targetBlock.add(new BlockPos(
							getNBT(stack).getInteger("repeatMovmentX") * i, 
							getNBT(stack).getInteger("repeatMovmentY") * i, 
							getNBT(stack).getInteger("repeatMovmentZ") * i)
					),
					getNBT(stack).getInteger("Rotation"),
					getNBT(stack).getBoolean("flipX"),
					getNBT(stack).getBoolean("flipY"),
					getNBT(stack).getBoolean("flipZ")));
				}				
			}
			
			break;
		
		case 3:
			if(player.clipboardMaxPos != null){
				setBlockPos1(stack, targetBlock);
				BlockPos tempPos = getAdjustedBlockPos(player.clipboardMaxPos).add(targetBlock);
				//BlockPos tempPos2 = new BlockPos(-player.clipboardMaxPos.getZ(), player.clipboardMaxPos.getY(), player.clipboardMaxPos.getX()).add(targetBlock);
				setBlockPos2(stack, tempPos);
			}
			break;

		case 4:
			getNBT(stack).setInteger("Rotation", getNBT(stack).getInteger("Rotation") == 3 ? 0 : getNBT(stack).getInteger("Rotation") + 1);
			break;
			
		case 5:
			getNBT(stack).setBoolean("flipX", !getNBT(stack).getBoolean("flipX"));
			break;
			
		case 6:
			getNBT(stack).setBoolean("flipY", !getNBT(stack).getBoolean("flipY"));
			break;
			
		case 7:
			getNBT(stack).setBoolean("flipZ", !getNBT(stack).getBoolean("flipZ"));
			break;
			
		case 8:
			System.out.println("Clearing");
			if(getBlockPos1(stack) != null && getBlockPos2(stack) != null){
				player.addPending(new ClearSelectionThread(
						new BlockPos(getBlockPos1(stack).getX(),
									getBlockPos1(stack).getY(),
									getBlockPos1(stack).getZ()),
						new BlockPos(getBlockPos2(stack).getX(),
									getBlockPos2(stack).getY(),
									getBlockPos2(stack).getZ()), 
						world));
			}
			break;
			
		case 9:
			System.out.println(mouseButton);
			if(mouseButton == 0){
				getNBT(stack).setInteger("repeat", getNBT(stack).getInteger("repeat") + 1);
			}
			else if(mouseButton == 1){
				getNBT(stack).setInteger("repeat", getNBT(stack).getInteger("repeat") - 1);
			}
			break;
			
		case 10:
			if(mouseButton == 0){
				getNBT(stack).setInteger("repeatMovmentX", getNBT(stack).getInteger("repeatMovmentX") + 1);
			}
			else if(mouseButton == 1){
				getNBT(stack).setInteger("repeatMovmentX", getNBT(stack).getInteger("repeatMovmentX") - 1);
			}
			break;
			
		case 11:
			if(mouseButton == 0){
				getNBT(stack).setInteger("repeatMovmentY", getNBT(stack).getInteger("repeatMovmentY") + 1);
			}
			else if(mouseButton == 1){
				getNBT(stack).setInteger("repeatMovmentY", getNBT(stack).getInteger("repeatMovmentY") - 1);
			}
			break;
			
		case 12:
			if(mouseButton == 0){
				getNBT(stack).setInteger("repeatMovmentZ", getNBT(stack).getInteger("repeatMovmentZ") + 1);
			}
			else if(mouseButton == 1){
				getNBT(stack).setInteger("repeatMovmentZ", getNBT(stack).getInteger("repeatMovmentZ") - 1);
			}
			break;
			
		default:
			break;
		}
		
	}
	
	public BlockPos getAdjustedBlockPos(BlockPos originalPos){
		BlockPos tempPos = originalPos;
		
		System.out.println(originalPos);
		
		switch (getNBT(thisStack).getInteger("Rotation")) {
		
		
		case 0:
			tempPos = originalPos;
			
			System.out.println(originalPos);
			break;
			
		case 1:
			tempPos = new BlockPos(-originalPos.getZ(), originalPos.getY(), originalPos.getX());
			break;
			
		case 2:
			BlockPos pos1 = new BlockPos(-originalPos.getZ(), originalPos.getY(), originalPos.getX());
			tempPos = new BlockPos(-pos1.getZ(), pos1.getY(), pos1.getX());
			break;
			
		case 3:
			BlockPos pos2 = new BlockPos(-originalPos.getZ(), originalPos.getY(), originalPos.getX());
			BlockPos pos3 = new BlockPos(-pos2.getZ(), pos2.getY(), pos2.getX());
			tempPos = new BlockPos(-pos3.getZ(), pos3.getY(), pos3.getX());
			break;

		default:
			break;
		}
		
		return tempPos;
	}
	
}
