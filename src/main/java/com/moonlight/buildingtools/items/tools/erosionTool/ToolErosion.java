package com.moonlight.buildingtools.items.tools.erosionTool;

import java.util.Set;

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.helpers.RenderHelper;
import com.moonlight.buildingtools.items.tools.ToolBase;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.RGBA;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;

public class ToolErosion extends ToolBase{
	
	public ToolErosion(){
		super();
		setUnlocalizedName("ToolErosion");
		setRegistryName("erosionTool");
		setCreativeTab(BuildingTools.tabBT);
		setMaxStackSize(1);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        stack.getTagCompound().setInteger("preset", 1);
	        stack.getTagCompound().setInteger("radius", 1);
	    }
	    return stack.getTagCompound();	    
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		
		ItemStack itemStackIn = playerIn.getHeldItemMainhand();
		
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIErosionTool, worldIn, 0, 0, 0);
		else{
			if(!worldIn.isRemote && targetBlock != null && !worldIn.isAirBlock(targetBlock)){
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				player.addPending(new ThreadErosion(worldIn, targetBlock, getTargetRadius(itemStackIn), playerIn, getNBT(itemStackIn).getInteger("preset")));
			}
		}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
        
    }
	    
	public void setTargetRadius(ItemStack stack, int radius){		
		getNBT(stack).setInteger("radius", radius);
    }

    public int getTargetRadius(ItemStack stack){
        if (stack.hasTagCompound() && (getNBT(stack).hasKey("radius"))){
            return getNBT(stack).getInteger("radius");
        }
        else{
        	getNBT(stack).setInteger("radius", 3);
        	PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
        	return 3;
        }
    }

    @Override
    public void handleKey(EntityPlayer player, ItemStack itemStack, Key.KeyCode key){

    	System.out.print("Key Recived");
    	
        int radius = getNBT(itemStack).getInteger("radius");

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

        getNBT(itemStack).setInteger("radius", radius);
        PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(itemStack)));
        System.out.print(getNBT(itemStack).getInteger("radius"));
        
    }
	
	public void GuiButtonPressed(int buttonID, int mouseButton, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
		
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
				if(getNBT(thisStack).getInteger("preset") < ErosionVisuallizer.Preset.values().length - 1)
					getNBT(thisStack).setInteger("preset", getNBT(thisStack).getInteger("preset") + 1);
				else
					getNBT(thisStack).setInteger("preset", 0);
			}
			else if(mouseButton == 1){
				if(getNBT(thisStack).getInteger("preset") > 0)
					getNBT(thisStack).setInteger("preset", getNBT(thisStack).getInteger("preset") - 1);
				else
					getNBT(thisStack).setInteger("preset", ErosionVisuallizer.Preset.values().length - 1);
			}
		}
		else if(buttonID == 2){
			int radius = getNBT(thisStack).getInteger("radius");
			radius+=amount;
			if (radius < 1){radius = 1;}
			getNBT(thisStack).setInteger("radius", radius);
		}
		else{
		}
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(thisStack)));
	}

	@Override
	public boolean drawOutline(DrawBlockHighlightEvent event) {
		
		if(targetBlock != null){
			ErosionVisuallizer visuallizer = new ErosionVisuallizer(getTargetRadius(event.getPlayer().getHeldItemMainhand()), event.getPlayer().worldObj, targetBlock, getNBT(event.getPlayer().getHeldItemMainhand()).getInteger("preset"));
			
			if(renderer == null){
	    		renderer = new RenderHelper();
	    	}
			renderer.startDraw();
			
			for(BlockPos pos : visuallizer.getErosionData()){
				renderer.addOutlineToBuffer(event.getPlayer(), pos, RGBA.Red.setAlpha(1500), event.getPartialTicks());
			}
			
			for(BlockPos pos : visuallizer.getFillData()){
				renderer.addOutlineToBuffer(event.getPlayer(), pos, RGBA.Green.setAlpha(1500), event.getPartialTicks());
			}
			
			renderer.finalizeDraw();
		}
		
		return true;
	}
	
	@Override
	public void ReadNBTCommand(NBTTagCompound nbtcommand){
		System.out.println(nbtcommand);
		Set<String> commandset = nbtcommand.getCompoundTag("Commands").getKeySet();
		for (String key : commandset) {
			String command = nbtcommand.getCompoundTag("Commands").getString(key);
			
			if (command.equals("GetButton")) {
				GuiButtonPressed(nbtcommand.getInteger("ButtonID"), nbtcommand.getInteger("Mouse"), nbtcommand.getBoolean("CTRL"), nbtcommand.getBoolean("ALT"), nbtcommand.getBoolean("SHIFT"));
			} else {
			}
		}
	}

}
