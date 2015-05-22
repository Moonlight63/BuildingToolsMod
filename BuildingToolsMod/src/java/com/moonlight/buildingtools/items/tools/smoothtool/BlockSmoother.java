package com.moonlight.buildingtools.items.tools.smoothtool;

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

import com.moonlight.buildingtools.BuildingTools;
import com.moonlight.buildingtools.items.tools.IGetGuiButtonPressed;
import com.moonlight.buildingtools.network.packethandleing.PacketDispatcher;
import com.moonlight.buildingtools.network.packethandleing.SyncNBTDataMessage;
import com.moonlight.buildingtools.network.playerWrapper.PlayerWrapper;
import com.moonlight.buildingtools.utils.IKeyHandler;
import com.moonlight.buildingtools.utils.Key;

public class BlockSmoother extends Item implements IKeyHandler, IGetGuiButtonPressed{
	
	private static Set<Key.KeyCode> handledKeys;
	
	static
    {
        handledKeys = new HashSet<Key.KeyCode>();
        handledKeys.add(Key.KeyCode.TOOL_INCREASE);
        handledKeys.add(Key.KeyCode.TOOL_DECREASE);
    }
	
	public BlockSmoother(){
		super();
		setUnlocalizedName("blockSmoother");
		setCreativeTab(BuildingTools.tabBT);
	}
	
	public static NBTTagCompound getNBT(ItemStack stack) {
	    if (stack.getTagCompound() == null) {
	        stack.setTagCompound(new NBTTagCompound());
	        stack.getTagCompound().setInteger("radius", 1);
	        stack.getTagCompound().setInteger("iterations", 5);
	        stack.getTagCompound().setDouble("sigma", 2);
	    }
	    return stack.getTagCompound();	    
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int metadata, boolean bool)
	{
		EntityPlayer player = (EntityPlayer) entity;
		if(player.getCurrentEquippedItem() == itemstack){
			BuildingTools.proxy.setExtraReach(player, 200);
		}
//		else{
//			BuildingTools.proxy.setExtraReach(player, 5);
//		}
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
    {
		if(playerIn.isSneaking())
			playerIn.openGui(BuildingTools.instance, 1, worldIn, 0, 0, 0);
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
			PlayerWrapper player = BuildingTools.getPlayerRegistry().getPlayer(playerIn).get();
			player.addPending(new SmoothToolThread(worldIn, pos, getTargetRadius(stack), stack, playerIn));
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
        return BlockSmoother.handledKeys;
    }

	@Override
	public void GetGuiButtonPressed(byte buttonID, int mouseButton, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown, ItemStack stack) {
		switch (buttonID) {
		case 0:
			getNBT(stack).setInteger("radius", getNBT(stack).getInteger("radius") - 1);
			break;
			
		case 1:
			getNBT(stack).setInteger("radius", getNBT(stack).getInteger("radius") + 1);
			break;
			
		case 2:
			getNBT(stack).setInteger("iterations", getNBT(stack).getInteger("iterations") - 1);
			break;
			
		case 3:
			getNBT(stack).setInteger("iterations", getNBT(stack).getInteger("iterations") + 1);
			break;
			
		case 4:
			getNBT(stack).setInteger("sigma", getNBT(stack).getInteger("sigma") - 1);
			break;
			
		case 5:
			getNBT(stack).setInteger("sigma", getNBT(stack).getInteger("sigma") + 1);
			break;

		default:
			break;
		}
		
		PacketDispatcher.sendToServer(new SyncNBTDataMessage(getNBT(stack)));
	}

}
