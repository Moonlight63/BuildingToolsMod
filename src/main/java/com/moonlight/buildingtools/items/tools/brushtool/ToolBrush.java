package com.moonlight.buildingtools.items.tools.brushtool;

import java.util.List;
//import java.util.Optional;
import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.KeyHelper;
import com.moonlight.buildingtools.utils.RGBA;
//import com.moonlight.buildingtools.utils.KeyBindsHandler.ETKeyBinding;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolBrush extends ToolBase{
	
	private BrushShapeVisualizer visualizer;
	
	public ToolBrush(){
		super();
		setUnlocalizedName("ToolBrush");
		setRegistryName("brushtool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        stack.getTagCompound().setInteger("radiusX", 1);
	        stack.getTagCompound().setInteger("radiusY", 1);
	        stack.getTagCompound().setInteger("radiusZ", 1);
	        stack.getTagCompound().setInteger("generator", 0);
	        stack.getTagCompound().setBoolean("forcefall", false);
	        stack.getTagCompound().setBoolean("fillmode", true);
	        stack.getTagCompound().setInteger("replacemode", 1);
	        
	        ItemStack defaultFill = new ItemStack(Blocks.STONE);
	        stack.getTagCompound().setTag("fillblocks", new NBTTagCompound());
	        stack.getTagCompound().getCompoundTag("fillblocks").setTag("0", new NBTTagCompound());
	        stack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag("0").setInteger("chance", 1);
	        stack.getTagCompound().getCompoundTag("fillblocks").getCompoundTag("0").setTag("blockstate", defaultFill.writeToNBT(new NBTTagCompound()));
	        
	        ItemStack defaultReplace = new ItemStack(Blocks.AIR);
	        stack.getTagCompound().setTag("replaceblocks", new NBTTagCompound());
	        stack.getTagCompound().getCompoundTag("replaceblocks").setTag("0", defaultReplace.writeToNBT(new NBTTagCompound()));
	        
	    }
	    //thisStack = stack;
	    return stack.getTagCompound();
	}
	
	@Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean check)
    {
        super.addInformation(stack, player, list, check);

        if (KeyHelper.isShiftDown())
        {
            if (stack.getTagCompound() == null)
            {
                //setDefaultTag(stack, 0);
            }
            
            //ItemStack pb = ItemStack.loadItemStackFromNBT(getNBT(stack).getCompoundTag("sourceblock"));
            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.source " + pb.getDisplayName()) + EnumChatFormatting.RESET);

            //list.add(EnumChatFormatting.GREEN + /*LocalisationHelper.localiseString*/("info.exchanger.radius " + this.getTargetRadius(stack)));

            //list.add(EnumChatFormatting.AQUA + "" + EnumChatFormatting.ITALIC + /*LocalisationHelper.localiseString*/("info.exchanger.shift_to_select_source") + EnumChatFormatting.RESET);
        } else
        {
            //list.add("Hold SHIFT for details");
            
            //list.add(player.getDisplayNameString());
        }
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		
		if(targetBlock != null && !worldIn.isAirBlock(targetBlock)){
			
			PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStackIn)));
			
			if(playerIn.isSneaking()){
				playerIn.openGui(BuildingTools.instance, GuiHandler.GUIBrushTool, worldIn, 0, 0, 0);
				return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
			}
			
			
			if(!worldIn.isRemote){
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				player.addPending(new ThreadPaintShape(worldIn, targetBlock, targetFace, playerIn, getNBT(itemStackIn)));
			}
			
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
		
    }
    
	@Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key){

    	//System.out.print("Key Recived");
    	
        int radius = getNBT(itemStack).getInteger("radiusX");
        
        float yMult = 0;
        float zMult = 0;
        
	        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY")){
	        	if(getNBT(itemStack).getInteger("radiusY") > 0)
	        		yMult = (getNBT(itemStack).getInteger("radiusX") / getNBT(itemStack).getInteger("radiusY"));
	        }
	        else{
	        	if(getNBT(itemStack).getInteger("radiusX") > 0)
	        		yMult = (getNBT(itemStack).getInteger("radiusY") / getNBT(itemStack).getInteger("radiusX"));
	        }
        
        
	        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ")){
	        	if(getNBT(itemStack).getInteger("radiusZ") > 0)
	        		zMult = (getNBT(itemStack).getInteger("radiusX") / getNBT(itemStack).getInteger("radiusZ"));
	        }
	        else{
	        	if(getNBT(itemStack).getInteger("radiusX") > 0)
	        		zMult = (getNBT(itemStack).getInteger("radiusZ") / getNBT(itemStack).getInteger("radiusX"));
	        }
	        
	        
        

        if (key == Key.KeyCode.TOOL_INCREASE){
            if (player.isSneaking()){
                radius += 10;
            } else{
                radius++;
            }

        } else if (key == Key.KeyCode.TOOL_DECREASE){
            if (player.isSneaking())
            {
                radius -= 10;
            } else
            {
                radius--;
            }
        }
        

        if (radius < 0){radius = 0;}
        
        getNBT(itemStack).setInteger("radiusX", radius);
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusY"))
        	getNBT(itemStack).setInteger("radiusY", (int) yMult == 0 ? 1 : (int) (radius / yMult));
        else
        	getNBT(itemStack).setInteger("radiusY", (int) yMult == 0 ? 0 : (int) (radius * yMult));
        
        if(getNBT(itemStack).getInteger("radiusX") > getNBT(itemStack).getInteger("radiusZ"))
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 1 : (int) (radius / zMult));
        else
        	getNBT(itemStack).setInteger("radiusZ", (int) zMult == 0 ? 0 : (int) (radius * zMult));
        
        
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
        
    }
	
    @Override
    public boolean drawOutline(DrawBlockHighlightEvent event)
    {

    	if(visualizer==null){
    		visualizer = new BrushShapeVisualizer();
    	}
    	if(this.renderer == null){
    		renderer = new RenderHelper();
    	}
    	
        if(targetBlock != null){
        	
        	//RenderHelper renderer = new RenderHelper();
        	
        	renderer.startDraw();
        	
	        if (event.getPlayer().isSneaking())
	        {
	        	renderer.addOutlineToBuffer(event.getPlayer(), targetBlock, RGBA.Green.setAlpha(150), event.getPartialTicks());
	            //RenderHelper.renderBlockOutline(event.context, event.player, targetBlock, RGBA.Green.setAlpha(150), 2.0f, event.partialTicks);
	        	renderer.finalizeDraw();
	            return true;
	        }
	        	
        	if(checkVisualizer(visualizer, event.getPlayer().getHeldItemMainhand())){
                visualizer.RegenShape(
            			Shapes.VALUES[getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("generator")].generator, 
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusX"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusY"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("radiusZ"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("replacemode"),
            			getNBT(event.getPlayer().getHeldItemMainhand()).getCompoundTag("replaceblocks")
        		);
                updateVisualizer = false;
        	}
        	
        	if(visualizer.finishedGenerating){
	        	Set<BlockPos> blockData = visualizer.GetBlocks();
	        	
	        	if(blockData != null){
		        	for(BlockPos pos : blockData){
		        		BlockPos newPos = visualizer.CalcOffset(pos, targetBlock, targetFace, world);
		        		
		        		if(newPos != null){
		        			if(!(getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("replacemode") == 2 && world.isAirBlock(newPos)))
		        				renderer.addOutlineToBuffer(event.getPlayer(), newPos, RGBA.White.setAlpha(150), event.getPartialTicks());
		        			//RenderHelper.renderBlockOutline(event.context, event.player, newPos, RGBA.White.setAlpha(150), 1.0f, event.partialTicks);
		        		}
		        	}
	        	}
        	}
	        
        	renderer.finalizeDraw();
        }
        return true;
    }
    
    public boolean checkVisualizer(BrushShapeVisualizer vis, ItemStack stack){
    	
    	return (
    			(visualizer.currentGen != Shapes.VALUES[getNBT(stack).getInteger("generator")].generator) ||
    			(visualizer.x != getNBT(stack).getInteger("radiusX")) ||
    			(visualizer.y != getNBT(stack).getInteger("radiusY")) ||
    			(visualizer.z != getNBT(stack).getInteger("radiusZ")) ||
    			(visualizer.replaceblock != getNBT(stack).getInteger("replacemode")) ||
    			(visualizer.replaceBlocks != getNBT(stack).getCompoundTag("replaceblocks")) ||
    			updateVisualizer
    			);
    	
    }

	public void GuiButtonPressed(int buttonID, int mouseButton,
			boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
		
		int multiplier = 0;
		if(isShiftDown)
			multiplier = 10;
		else
			multiplier = 1;
		
		int amount = 0;
		if(mouseButton == 0)
			amount = multiplier;
		else if(mouseButton == 1)
			amount = -multiplier;
		else 
			return;
		
		
		if (buttonID == 1) {
			if(mouseButton == 0){
				if(getNBT(thisStack).getInteger("generator") < Shapes.VALUES.length - 1)
					getNBT(thisStack).setInteger("generator", getNBT(thisStack).getInteger("generator") + 1);
				else
					getNBT(thisStack).setInteger("generator", 0);
			}
			else if(mouseButton == 1){
				if(getNBT(thisStack).getInteger("generator") > 0)
					getNBT(thisStack).setInteger("generator", getNBT(thisStack).getInteger("generator") - 1);
				else
					getNBT(thisStack).setInteger("generator", Shapes.VALUES.length - 1);
			}
		} else if (buttonID == 2) {
			int radiusx = getNBT(thisStack).getInteger("radiusX");
	        radiusx+=amount;
			if (radiusx < 0){radiusx = 0;}
			getNBT(thisStack).setInteger("radiusX", radiusx);
		} else if (buttonID == 3) {
			int radiusy = getNBT(thisStack).getInteger("radiusY");
	        radiusy+=amount;
			if (radiusy < 0){radiusy = 0;}
			getNBT(thisStack).setInteger("radiusY", radiusy);
		} else if (buttonID == 4) {
			int radiusz = getNBT(thisStack).getInteger("radiusZ");
			radiusz+=amount;
			if (radiusz < 0){radiusz = 0;}
			getNBT(thisStack).setInteger("radiusZ", radiusz);
		} else if (buttonID == 5) {
			System.out.println(world);
			System.out.println(targetBlock);
			System.out.println(thisStack);
			System.out.println(world.getBlockState(targetBlock));
			System.out.println(Block.getIdFromBlock(world.getBlockState(targetBlock).getBlock()));
			System.out.println(world.getBlockState(targetBlock).getBlock().getMetaFromState(world.getBlockState(targetBlock)));
			
			int id = Block.getIdFromBlock(world.getBlockState(targetBlock).getBlock());
			int meta = world.getBlockState(targetBlock).getBlock().getMetaFromState(world.getBlockState(targetBlock));
			ItemStack fill = new ItemStack(Block.getBlockById(id));
			fill.setItemDamage(meta);
			
			NBTTagCompound commandPacket = new NBTTagCompound();
        	
        	commandPacket.setTag("Commands", new NBTTagCompound());
        	commandPacket.getCompoundTag("Commands").setString("1", "SetFill");
        	commandPacket.setTag("fillblocks", new NBTTagCompound());
			commandPacket.getCompoundTag("fillblocks").setTag("0", new NBTTagCompound());
			commandPacket.getCompoundTag("fillblocks").getCompoundTag("0").setInteger("chance", 1);
			commandPacket.getCompoundTag("fillblocks").getCompoundTag("0").setTag("blockstate", fill.writeToNBT(new NBTTagCompound()));
    		
			ReadNBTCommand(commandPacket);
			
		} else if (buttonID == 6) {
			int id = Block.getIdFromBlock(Blocks.AIR);
			int meta = Blocks.AIR.getMetaFromState(Blocks.AIR.getDefaultState());
			
			ItemStack fill = new ItemStack(Block.getBlockById(id));
			fill.setItemDamage(meta);
			
			NBTTagCompound commandPacket = new NBTTagCompound();
        	
        	commandPacket.setTag("Commands", new NBTTagCompound());
        	commandPacket.getCompoundTag("Commands").setString("1", "SetFill");
        	commandPacket.setTag("fillblocks", new NBTTagCompound());
			commandPacket.getCompoundTag("fillblocks").setTag("0", new NBTTagCompound());
			commandPacket.getCompoundTag("fillblocks").getCompoundTag("0").setInteger("chance", 1);
			commandPacket.getCompoundTag("fillblocks").getCompoundTag("0").setTag("blockstate", fill.writeToNBT(new NBTTagCompound()));
			
			ReadNBTCommand(commandPacket);
		
		} else if (buttonID == 7) {
			getNBT(thisStack).setBoolean("fillmode", !getNBT(thisStack).getBoolean("fillmode"));
		} else if (buttonID == 8) {
			getNBT(thisStack).setBoolean("forcefall", !getNBT(thisStack).getBoolean("forcefall"));
		} else if (buttonID == 9) {
			if(getNBT(thisStack).getInteger("replacemode") == 1){
				getNBT(thisStack).setInteger("replacemode", 2);
			}
			else if(getNBT(thisStack).getInteger("replacemode") == 2){
				getNBT(thisStack).setInteger("replacemode", 3);
			}
			else if(getNBT(thisStack).getInteger("replacemode") == 3){
				getNBT(thisStack).setInteger("replacemode", 4);
			}
			else if(getNBT(thisStack).getInteger("replacemode") == 4){
				getNBT(thisStack).setInteger("replacemode", 1);
			}
		}  else {
		}
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(thisStack)));
		
	}
	
	@Override
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		System.out.println(nbtcommand);
		Set<String> commandset = nbtcommand.getCompoundTag("Commands").getKeySet();
		
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
			case "GetButton":
				GuiButtonPressed(nbtcommand.getInteger("ButtonID"), nbtcommand.getInteger("Mouse"), nbtcommand.getBoolean("CTRL"), nbtcommand.getBoolean("ALT"), nbtcommand.getBoolean("SHIFT"));
				break;
			default:
				break;
			}
		}
	}
	
}
