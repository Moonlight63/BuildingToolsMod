package com.moonlight.buildingtools.items.tools.erosionTool;

import java.util.HashSet;
import java.util.Set;

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
import com.moonlight.buildingtools.helpers.Shapes;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.items.tools.IToolOverrideHitDistance;
import com.moonlight.buildingtools.items.tools.brushtool.GUIToolBrush;
import com.moonlight.buildingtools.network.GuiHandler;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IKeyHandler;
import com.moonlight.buildingtools.utils.IOutlineDrawer;
import com.moonlight.buildingtools.utils.Key;
import com.moonlight.buildingtools.utils.RGBA;

public class ToolErosion extends Item implements IKeyHandler, IOutlineDrawer, IGetGuiButtonPressed, IToolOverrideHitDistance{
	
	private static Set<Key.KeyCode> handledKeys;
	
	static
    {
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	public ToolErosion(){
		super();
		setUnlocalizedName("erosionTool");
		setCreativeTab(BuildingTools.tabBT);
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
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIErosionTool, worldIn, 0, 0, 0);
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
		
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, GuiHandler.GUIErosionTool, worldIn, 0, 0, 0);
		else{
			if(!worldIn.isRemote){
				PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
				player.addPending(new ThreadErosion(worldIn, pos, getTargetRadius(stack), playerIn, getNBT(stack).getInteger("preset")));
				//player.addPending(new ErosionThread(worldIn, pos, 3, 3, 3, side, playerIn, getNBT(stack).getInteger("preset")));
			}
		}
		return true;
	}
	    
	public void setTargetRadius(ItemStack stack, int radius)
    {		
		getNBT(stack).setInteger("radius", radius);
    }

    public int getTargetRadius(ItemStack stack)
    {
        if (stack.hasTagCompound() && (getNBT(stack).hasKey("radius")))
        {
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
	
    
    @Override
    public Set<Key.KeyCode> getHandledKeys()
    {
        return ToolErosion.handledKeys;
    }

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown, ItemStack stack) {
		
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
				if(getNBT(stack).getInteger("preset") < ErosionVisuallizer.Preset.values().length - 1)
					getNBT(stack).setInteger("preset", getNBT(stack).getInteger("preset") + 1);
				else
					getNBT(stack).setInteger("preset", 0);
			}
			else if(mouseButton == 1){
				if(getNBT(stack).getInteger("preset") > 0)
					getNBT(stack).setInteger("preset", getNBT(stack).getInteger("preset") - 1);
				else
					getNBT(stack).setInteger("preset", ErosionVisuallizer.Preset.values().length - 1);
			}
		}
		else if(buttonID == 2){
			int radius = getNBT(stack).getInteger("radius");
			radius+=amount;
			if (radius < 1){radius = 1;}
			getNBT(stack).setInteger("radius", radius);
		}
		else{
		}
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}

	@Override
	public boolean drawOutline(DrawBlockHighlightEvent event) {
		BlockPos target = event.target.getBlockPos();
		
		ErosionVisuallizer visuallizer = new ErosionVisuallizer(getTargetRadius(event.currentItem), event.player.worldObj, target, getNBT(event.currentItem).getInteger("preset"));
		
		for(BlockPos pos : visuallizer.getErosionData()){
			RenderHelper.renderBlockOutline(event.context, event.player, pos, RGBA.Red.setAlpha(0.6f), 2.0f, event.partialTicks);
		}
		
		for(BlockPos pos : visuallizer.getFillData()){
			RenderHelper.renderBlockOutline(event.context, event.player, pos, RGBA.Green.setAlpha(0.6f), 2.0f, event.partialTicks);
		}
		
		return true;
	}

}
