package com.moonlight.buildingtools.items.tools.selectiontool;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.DimensionManager;

import com.google.common.collect.Lists;
import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RayTracing;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SendRaytraceResult;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.RGBA;

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
	        
	        ItemStack defaultFill = new ItemStack(Blocks.STONE);
	        stack.getTagCompound().setTag("fillblocks", new NBTTagCompound());
	        //stack.getTagCompound().getCompoundTag("fillblocks").setTag("0", new NBTTagCompound());
	        //stack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag("0").setInteger("chance", 1);
	        //stack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag("0").setTag("blockstate", defaultFill.writeToNBT(new NBTTagCompound()));
	        
	        ItemStack defaultReplace = new ItemStack(Blocks.AIR);
	        stack.getTagCompound().setTag("replaceblocks", new NBTTagCompound());
	        //stack.getTagCompound().getCompoundTag("replaceblocks").setTag("0", defaultReplace.writeToNBT(new NBTTagCompound()));
	        
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
		return new ActionResult(EnumActionResult.PASS, itemStackIn);
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

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown,
			ItemStack stack) {
		
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
		
		
		thisStack = stack;
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
		
		if (buttonID == GUISelectionTool.copytoclipboard.id) {
			System.out.println("Copying");
			player.addPending(new ThreadCopyToClipboard(getBlockPos1(stack), getBlockPos2(stack), world, currPlayer));
			
		} else if (buttonID == GUISelectionTool.pasteclipboard.id) {
			player.addPending(new ThreadPasteClipboard(
					world, currPlayer, targetBlock,
					getNBT(stack).getInteger("Rotation"),
					getNBT(stack).getBoolean("flipX"),
					getNBT(stack).getBoolean("flipY"),
					getNBT(stack).getBoolean("flipZ")));
			if(getNBT(stack).getInteger("repeat") > 0){				
				for(int i = 1; i < getNBT(stack).getInteger("repeat"); i++){
					player.addPending(new ThreadPasteClipboard(
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
		} else if (buttonID == GUISelectionTool.selectpaste.id) {
			if(player.clipboardMaxPos != null){
				setBlockPos1(stack, targetBlock);
				BlockPos tempPos = getAdjustedBlockPos(player.clipboardMaxPos).add(targetBlock);
				setBlockPos2(stack, tempPos);
			}
		} else if (buttonID == GUISelectionTool.clearselction.id) {
			getNBT(stack).getCompoundTag("bpos1").setBoolean("set", false);
			getNBT(stack).getCompoundTag("bpos2").setBoolean("set", false);
		} else if (buttonID == GUISelectionTool.rotate90.id) {
			int temp = getNBT(stack).getInteger("Rotation") + amount;
			if(temp == -1)
				temp = 3;
			else if(temp == 4)
				temp = 0;
			getNBT(stack).setInteger("Rotation", temp);
		} else if (buttonID == GUISelectionTool.flipx.id) {
			getNBT(stack).setBoolean("flipX", !getNBT(stack).getBoolean("flipX"));
		} else if (buttonID == GUISelectionTool.flipy.id) {
			getNBT(stack).setBoolean("flipY", !getNBT(stack).getBoolean("flipY"));
		} else if (buttonID == GUISelectionTool.flipz.id) {
			getNBT(stack).setBoolean("flipZ", !getNBT(stack).getBoolean("flipZ"));
		} 
//		else if (buttonID == 9) {
//			System.out.println("Clearing");
//			if(getBlockPos1(stack) != null && getBlockPos2(stack) != null){
//				player.addPending(new ThreadClearSelection(getBlockPos1(stack),	getBlockPos2(stack), world));
//			}
//		} 
		else if (buttonID == GUISelectionTool.repeat.id) {
			getNBT(stack).setInteger("repeat", getNBT(stack).getInteger("repeat") + (amount * multiplier));
		} else if (buttonID == GUISelectionTool.moveX.id) {
			getNBT(stack).setInteger("repeatMovmentX", getNBT(stack).getInteger("repeatMovmentX") + (amount * multiplier));
		} else if (buttonID == GUISelectionTool.moveY.id) {
			getNBT(stack).setInteger("repeatMovmentY", getNBT(stack).getInteger("repeatMovmentY") + (amount * multiplier));
		} else if (buttonID == GUISelectionTool.moveZ.id) {
			getNBT(stack).setInteger("repeatMovmentZ", getNBT(stack).getInteger("repeatMovmentZ") + (amount * multiplier));
		} else if (buttonID == 14){
			
		} else if (buttonID == 16) {
			
		} else if (buttonID == 17) {
			
		} else {
			
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

	public void SaveSelectionToFile(String savename, int mouseButton,
			boolean ctrlDown, boolean altDown, boolean shiftDown,
			ItemStack heldItem) {
		
		thisStack = heldItem;
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		player.addPending(new ThreadSaveClipboard(currPlayer, savename));
		
	}

	public void LoadSelectionFromFile(String file, ItemStack heldItem) {
		thisStack = heldItem;
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		player.addPending(new ThreadLoadClipboard(currPlayer, file));
	}
	
	
//	NBTTagCompound tempNBT;
//	
//	public void setFillBlocks(List<Integer> ID, List<Integer> DATA, List<Integer> COUNT){
//		System.out.println("Recieved Message!");
//		System.out.println(ID + "   " + DATA + "   " + COUNT);
//		
//		thisStack.getTagCompound().setTag("fillblocks", new NBTTagCompound());
//		for (int i = 0; i < ID.size(); i++) {
//			ItemStack fill = new ItemStack(Block.getBlockById(ID.get(i)));
//			fill.setItemDamage(DATA.get(i));
//			thisStack.getTagCompound().getCompoundTag("fillblocks").setTag(Integer.toString(i), new NBTTagCompound());
//			thisStack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setInteger("chance", COUNT.get(i));
//			thisStack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setTag("blockstate", fill.writeToNBT(new NBTTagCompound()));
//		}
//		tempNBT = thisStack.getTagCompound();
//		System.out.println(tempNBT);
//		//PacketDispatcher.sendToServer(new SyncNBTDataMessage(thisStack.getTagCompound()));
//		System.out.println(thisStack.getTagCompound().getCompoundTag("fillblocks"));
//	}
//	
//	public void setReplaceBlocks(List<Integer> ID, List<Integer> DATA){
//		System.out.println("Recieved Message!");
//		System.out.println(ID + "   " + DATA);
//		//System.out.println(tempNBT);
//		thisStack.setTagCompound(tempNBT);
//		thisStack.getTagCompound().setTag("replaceblocks", new NBTTagCompound());
//		for (int i = 0; i < ID.size(); i++) {			
//			ItemStack replace = new ItemStack(Block.getBlockById(ID.get(i)));
//			replace.setItemDamage(DATA.get(i));
//			thisStack.getTagCompound().getCompoundTag("replaceblocks").setTag(Integer.toString(i), replace.writeToNBT(new NBTTagCompound()));
//		}
//        
//	}
	
	public void setReplaceBlocks(List<Integer> ID, List<Integer> DATA){
		
	}
	public void setFillBlocks(List<Integer> ID, List<Integer> DATA, List<Integer> COUNT){
		
	}
	
	public void setFillAndReplace(List<Integer> ID, List<Integer> DATA, List<Integer> COUNT, List<Integer> ID2, List<Integer> DATA2){
		System.out.println("Recieved Message!");
		System.out.println(ID + "   " + DATA + "   " + COUNT);
		
		thisStack.getTagCompound().setTag("fillblocks", new NBTTagCompound());
		for (int i = 0; i < ID.size(); i++) {
			ItemStack fill = new ItemStack(Block.getBlockById(ID.get(i)));
			fill.setItemDamage(DATA.get(i));
			thisStack.getTagCompound().getCompoundTag("fillblocks").setTag(Integer.toString(i), new NBTTagCompound());
			thisStack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setInteger("chance", COUNT.get(i));
			thisStack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag(Integer.toString(i)).setTag("blockstate", fill.writeToNBT(new NBTTagCompound()));
		}
		System.out.println(thisStack.getTagCompound().getCompoundTag("fillblocks"));
		
		System.out.println("Recieved Message!");
		System.out.println(ID2 + "   " + DATA2);
		thisStack.getTagCompound().setTag("replaceblocks", new NBTTagCompound());
		for (int i = 0; i < ID2.size(); i++) {			
			ItemStack replace = new ItemStack(Block.getBlockById(ID2.get(i)));
			replace.setItemDamage(DATA2.get(i));
			thisStack.getTagCompound().getCompoundTag("replaceblocks").setTag(Integer.toString(i), replace.writeToNBT(new NBTTagCompound()));
		}
		
		World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
		PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
		player.addPending(new ThreadAdvancedFill(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer, thisStack.getTagCompound()));
	}
	
//	@Override
//	public void readCustomCommand(String message){
//		System.out.println("Got Command: " + message);
//		if(message.equals("Fill/Replace")){
//			System.out.println("Processing...");
//			PacketDispatcher.sendToServer(new SyncNBTDataMessage(thisStack.getTagCompound()));
//			System.out.println(thisStack.getTagCompound().getCompoundTag("fillblocks"));
//			World world = DimensionManager.getWorld(Minecraft.getMinecraft().theWorld.provider.getDimension());
//			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(currPlayer).get();
//			player.addPending(new ThreadAdvancedFill(getBlockPos1(thisStack), getBlockPos2(thisStack), world, currPlayer, thisStack.getTagCompound()));
//		}
//	}
	
}
