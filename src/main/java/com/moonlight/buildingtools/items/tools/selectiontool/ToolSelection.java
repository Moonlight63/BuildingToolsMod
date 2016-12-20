package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.DimensionManager;

public class ToolSelection extends ToolBase{
	
	public BlockPos blockpos1;
	public BlockPos blockpos2;
	public boolean blockpos1set;
	public boolean blockpos2set;
	
	public static ThreadPasteClipboard copyThread;
	
	public ToolSelection(){
		super();
		setUnlocalizedName("ToolSelection");
		setRegistryName("selectiontool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        
	        stack.getTagCompound().setInteger("Rotation", 0);
	        stack.getTagCompound().setBoolean("flipX", false);
	        stack.getTagCompound().setBoolean("flipY", false);
	        stack.getTagCompound().setBoolean("flipZ", false);
	        
	        stack.getTagCompound().setInteger("repeat", 0);
	        
	        stack.getTagCompound().setInteger("repeatMovmentX", 0);
	        stack.getTagCompound().setInteger("repeatMovmentY", 0);
	        stack.getTagCompound().setInteger("repeatMovmentZ", 0);
	        
	        stack.getTagCompound().setTag("fillblocks", new NBTTagCompound());
	        stack.getTagCompound().setTag("replaceblocks", new NBTTagCompound());
	        
	        stack.getTagCompound().setTag("bpos1", new NBTTagCompound());
	        stack.getTagCompound().getCompoundTag("bpos1").setInteger("x", 0);
	        stack.getTagCompound().getCompoundTag("bpos1").setInteger("y", 0);
	        stack.getTagCompound().getCompoundTag("bpos1").setInteger("z", 0);
	        stack.getTagCompound().getCompoundTag("bpos1").setBoolean("set", false);
	        stack.getTagCompound().setTag("bpos2", new NBTTagCompound());
	        stack.getTagCompound().getCompoundTag("bpos2").setInteger("x", 0);
	        stack.getTagCompound().getCompoundTag("bpos2").setInteger("y", 0);
	        stack.getTagCompound().getCompoundTag("bpos2").setInteger("z", 0);
	        stack.getTagCompound().getCompoundTag("bpos2").setBoolean("set", false);
	    }
	    return stack.getTagCompound();	    
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {		
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStackIn)));
		currPlayer = playerIn;
		if(playerIn.isSneaking()){
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUISelectionTool, worldIn, 0, 0, 0);
		}
		else{
			if(targetBlock == null){
//				if(playerIn.isSneaking()){
//					getNBT(itemStackIn).setBoolean("bpos1Set", false);
//					getNBT(itemStackIn).setBoolean("bpos2Set", false);
//				}
			}
			else{
				if(!worldIn.isRemote){
					if(!getNBT(itemStackIn).getCompoundTag("bpos1").getBoolean("set") && !getNBT(itemStackIn).getCompoundTag("bpos2").getBoolean("set")){
						setBlockPos1(itemStackIn, targetBlock);
					}
					else if(getNBT(itemStackIn).getCompoundTag("bpos1").getBoolean("set") && !getNBT(itemStackIn).getCompoundTag("bpos2").getBoolean("set")){
						setBlockPos2(itemStackIn, targetBlock);
					}
//					else if(getNBT(itemStackIn).getBoolean("bpos1Set") && getNBT(itemStackIn).getBoolean("bpos2Set")){
//						setBlockPos1(itemStackIn, targetBlock);
//						getNBT(itemStackIn).setBoolean("bpos2Set", false);
//					}
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
    }
	
	public void setBlockPos1 (ItemStack stack, BlockPos pos){
		
		getNBT(stack).getCompoundTag("bpos1").setInteger("x", pos.getX());
		getNBT(stack).getCompoundTag("bpos1").setInteger("y", pos.getY());
		getNBT(stack).getCompoundTag("bpos1").setInteger("z", pos.getZ());
		getNBT(stack).getCompoundTag("bpos1").setBoolean("set", true);
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}
	
	public void setBlockPos2 (ItemStack stack, BlockPos pos){
		getNBT(stack).getCompoundTag("bpos2").setInteger("x", pos.getX());
		getNBT(stack).getCompoundTag("bpos2").setInteger("y", pos.getY());
		getNBT(stack).getCompoundTag("bpos2").setInteger("z", pos.getZ());
		getNBT(stack).getCompoundTag("bpos2").setBoolean("set", true);
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}
	
	public BlockPos getBlockPos1(ItemStack stack){
		if(getNBT(stack).getCompoundTag("bpos1").getBoolean("set")){
			return new BlockPos(getNBT(stack).getCompoundTag("bpos1").getInteger("x"),
								getNBT(stack).getCompoundTag("bpos1").getInteger("y"),
								getNBT(stack).getCompoundTag("bpos1").getInteger("z"));
		}
		else{
			return null;
		}
	}
	
	public BlockPos getBlockPos2(ItemStack stack){
		if(getNBT(stack).getCompoundTag("bpos2").getBoolean("set")){
			return new BlockPos(getNBT(stack).getCompoundTag("bpos2").getInteger("x"),
								getNBT(stack).getCompoundTag("bpos2").getInteger("y"),
								getNBT(stack).getCompoundTag("bpos2").getInteger("z"));
		}
		else{
			return null;
		}
	}
	
	@Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {
		//BlockPos target = event.target.getBlockPos();
		if(renderer == null){
	    	renderer = new RenderHelper();
	    }
		if(targetBlock!=null){
			
	        //if (event.getPlayer().isSneaking())
	        //{
	        	renderer.startDraw();
	        	renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), event.getPartialTicks());
	            renderer.finalizeDraw();
	        	//RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), 2.0f, event.getPartialTicks());
	        //    return true;
	        //}
	        
	        if (getNBT(event.getPlayer().getHeldItemMainhand()).getCompoundTag("bpos1").getBoolean("set")){
	        	renderer.startDraw();
	        	renderer.addOutlineToBuffer(event.getPlayer(), getBlockPos1(event.getPlayer().getHeldItemMainhand()), RGBA.Blue.setAlpha(150), event.getPartialTicks());
	        	renderer.finalizeDraw();
	        	//RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), getBlockPos1(event.getPlayer().getHeldItemMainhand()), RGBA.Blue, 1.0f, event.getPartialTicks());
	        }
	        
	        if (getNBT(event.getPlayer().getHeldItemMainhand()).getCompoundTag("bpos2").getBoolean("set")){
	        	renderer.startDraw();
	        	renderer.addOutlineToBuffer(event.getPlayer(), getBlockPos2(event.getPlayer().getHeldItemMainhand()), RGBA.Blue.setAlpha(150), event.getPartialTicks());
	        	renderer.finalizeDraw();
	        	//RenderHelper.renderBlockOutline(event.getContext(), event.getPlayer(), getBlockPos2(event.getPlayer().getHeldItemMainhand()), RGBA.Blue, 1.0f, event.getPartialTicks());
	        }
	        
	        if (getNBT(event.getPlayer().getHeldItemMainhand()).getCompoundTag("bpos1").getBoolean("set") && getNBT(event.getPlayer().getHeldItemMainhand()).getCompoundTag("bpos2").getBoolean("set")){
	        	renderer.startDraw();
	        	renderer.renderSelectionOutline(event.getPlayer(), getBlockPos1(event.getPlayer().getHeldItemMainhand()), getBlockPos2(event.getPlayer().getHeldItemMainhand()), RGBA.Red, event.getPartialTicks());
	        	renderer.finalizeDraw();
	        	RenderHelper.renderSelectionBox(event.getContext(), event.getPlayer(), getBlockPos1(event.getPlayer().getHeldItemMainhand()), getBlockPos2(event.getPlayer().getHeldItemMainhand()), RGBA.Red, 1.5f, event.getPartialTicks());
	        }
	        
		}
		
        return true;
    }

	
	public void GuiButtonPressed(int buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
		
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
		
		int multiplier = 0;
		if(isShiftDown)
			multiplier = 10;
		else
			multiplier = 1;
		
		int amount = 0;
		if(mouseButton == 0)
			amount = 1;
		else if(mouseButton == 1)
			amount = -1;
		else 
			return;
		
		if (buttonID == GUISelectionTool.copytoclipboard.id) {
			System.out.println("Copying");
			player.addPending(new ThreadCopyToClipboard(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer));
			
		} else if (buttonID == GUISelectionTool.pasteclipboard.id) {
			player.addPending(new ThreadPasteClipboard(
					world, currPlayer, targetBlock,
					getNBT(thisStack).getInteger("Rotation"),
					getNBT(thisStack).getBoolean("flipX"),
					getNBT(thisStack).getBoolean("flipY"),
					getNBT(thisStack).getBoolean("flipZ")));
			if(getNBT(thisStack).getInteger("repeat") > 0){				
				for(int i = 1; i < getNBT(thisStack).getInteger("repeat"); i++){
					player.addPending(new ThreadPasteClipboard(
					world, currPlayer, targetBlock.add(new BlockPos(
							getNBT(thisStack).getInteger("repeatMovmentX") * i, 
							getNBT(thisStack).getInteger("repeatMovmentY") * i, 
							getNBT(thisStack).getInteger("repeatMovmentZ") * i)
					),
					getNBT(thisStack).getInteger("Rotation"),
					getNBT(thisStack).getBoolean("flipX"),
					getNBT(thisStack).getBoolean("flipY"),
					getNBT(thisStack).getBoolean("flipZ")));
				}				
			}
		} else if (buttonID == GUISelectionTool.selectpaste.id) {
			if(player.clipboardMaxPos != null){
				setBlockPos1(thisStack, targetBlock);
				BlockPos tempPos = getAdjustedBlockPos(player.clipboardMaxPos).add(targetBlock);
				setBlockPos2(thisStack, tempPos);
			}
		} else if (buttonID == GUISelectionTool.clearselction.id) {
			getNBT(thisStack).getCompoundTag("bpos1").setBoolean("set", false);
			getNBT(thisStack).getCompoundTag("bpos2").setBoolean("set", false);
		} else if (buttonID == GUISelectionTool.rotate90.id) {
			int temp = getNBT(thisStack).getInteger("Rotation") + amount;
			if(temp == -1)
				temp = 3;
			else if(temp == 4)
				temp = 0;
			getNBT(thisStack).setInteger("Rotation", temp);
		} else if (buttonID == GUISelectionTool.flipx.id) {
			getNBT(thisStack).setBoolean("flipX", !getNBT(thisStack).getBoolean("flipX"));
		} else if (buttonID == GUISelectionTool.flipy.id) {
			getNBT(thisStack).setBoolean("flipY", !getNBT(thisStack).getBoolean("flipY"));
		} else if (buttonID == GUISelectionTool.flipz.id) {
			getNBT(thisStack).setBoolean("flipZ", !getNBT(thisStack).getBoolean("flipZ"));
		} else if (buttonID == GUISelectionTool.repeat.id) {
			getNBT(thisStack).setInteger("repeat", getNBT(thisStack).getInteger("repeat") + (amount * multiplier));
		} else if (buttonID == GUISelectionTool.moveX.id) {
			getNBT(thisStack).setInteger("repeatMovmentX", getNBT(thisStack).getInteger("repeatMovmentX") + (amount * multiplier));
		} else if (buttonID == GUISelectionTool.moveY.id) {
			getNBT(thisStack).setInteger("repeatMovmentY", getNBT(thisStack).getInteger("repeatMovmentY") + (amount * multiplier));
		} else if (buttonID == GUISelectionTool.moveZ.id) {
			getNBT(thisStack).setInteger("repeatMovmentZ", getNBT(thisStack).getInteger("repeatMovmentZ") + (amount * multiplier));
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
	
	@Override
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		
		System.out.println(nbtcommand);
		Set<String> commandset = nbtcommand.getCompoundTag("Commands").getKeySet();
		World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		
		for (String key : commandset) {
			String command = nbtcommand.getCompoundTag("Commands").getString(key);
			
			switch (command) {
			case "SetFill":
				System.out.println("Recieved Message!");
				System.out.println(nbtcommand.getCompoundTag("fillblocks"));
				thisStack.getTagCompound().setTag("fillblocks", nbtcommand.getCompoundTag("fillblocks"));
				System.out.println(thisStack.getTagCompound().getCompoundTag("fillblocks"));
				break;
			case "SetReplace":
				System.out.println("Recieved Message!");
				System.out.println(nbtcommand.getCompoundTag("replaceblocks"));
				thisStack.getTagCompound().setTag("replaceblocks", nbtcommand.getCompoundTag("replaceblocks"));
				System.out.println(thisStack.getTagCompound().getCompoundTag("replaceblocks"));
				break;
			case "RunFillReplace":
				player.addPending(new ThreadAdvancedFill(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer, thisStack.getTagCompound()));
				break;
			case "SaveFile":
				player.addPending(new ThreadSaveClipboard(currPlayer, nbtcommand.getString("File")));
				break;
			case "LoadFile":
				player.addPending(new ThreadLoadClipboard(currPlayer, nbtcommand.getString("File")));
				break;
			case "GetButton":
				GuiButtonPressed(nbtcommand.getInteger("ButtonID"), nbtcommand.getInteger("Mouse"), nbtcommand.getBoolean("CTRL"), nbtcommand.getBoolean("ALT"), nbtcommand.getBoolean("SHIFT"));
				break;
			default:
				break;
			}
		}
	}
	
}
